package hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.util.Bytes;

import client.Point;


public class HBase
{
	//static HBase Configuration, /conf/hbase-site.xml
	static Configuration cfg = HBaseConfiguration.create();
	static private int hilbertLength = 6;
	static private int idLength = 10;
	static private int TOTAL_POINTS = 0;
	
	/**
	 * generate a Point's row key. row key = hilbert + id
	 * row key length is 16: first 6 is hilbert curve encode, and last 10 is point's id
	 * @param point, a point that needs to generate a row key
	 * @return point's row key
	 */
	public static String generatePointRowKey(Point point)
	{
		int order = 9;  // x, y : (0, 2^9-1)
		StringBuffer rowKey = new StringBuffer();
		int x = (int) point.getX();
		int y = (int) point.getY();
		int id = point.getID();
		
		long encode = HilbertCurve.encode(x, y, order);
		String encodeStr = String.valueOf(encode);
		String idString = String.valueOf(id);
		
		byte[] temp = new byte[hilbertLength - encodeStr.length()];
		Arrays.fill(temp, (byte) '0');
		rowKey.append(new String(temp));
		rowKey.append(encodeStr);
		
		temp = new byte[idLength - idString.length()];
		Arrays.fill(temp, (byte) '0');
		rowKey.append(new String(temp));
		rowKey.append(idString);
		
		return rowKey.toString();
	}
	
	//create a table
	public static void create(String tablename, String columnFamily) throws Exception
	{
		Connection connection = ConnectionFactory.createConnection(cfg);
		Admin admin = connection.getAdmin();
		TableName tableName = TableName.valueOf(tablename);
		if(admin.tableExists(tableName))
		{
			System.out.println("table Exists!");
			System.exit(0);
		}
		else
		{
			HTableDescriptor tableDesc = new HTableDescriptor(tableName);
			tableDesc.addFamily(new HColumnDescriptor(columnFamily));
			admin.createTable(tableDesc);
			System.out.println("create table success!");
		}
	}
	
	public void insertRandomPoints(int insertNum, int maxX, int maxY, String tableName, 
			String columnFamily, String qualifyX, String qualifyY, String qualifyID) throws IOException
	{
		Connection connection = ConnectionFactory.createConnection(cfg);
		Table table = connection.getTable(TableName.valueOf(tableName));
		List<Row> batch = new ArrayList<Row>();  // a batch of puts is faster than many seperate puts
		for(int i = 0; i < insertNum; i++)
		{
			Point point = new Point(++TOTAL_POINTS);
			point.random(maxX, maxY);
			String rowKey = generatePointRowKey(point);	
			Put put = new Put(Bytes.toBytes(rowKey));
			put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifyID), Bytes.toBytes(point.getID()));
			put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifyX), Bytes.toBytes(point.getX()));
			put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifyY), Bytes.toBytes(point.getY()));
			batch.add(put);
		}
		Object[] results = new Object[batch.size()];
		try
		{
			table.batch(batch, results);
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e);
		}
		table.close();
		connection.close();
	}

	//add an entry of data(i.e. cell) through HTable Put
	public static void put(String tablename, String row, String columnFamily, String column, String data) throws Exception
	{
		Connection connection = ConnectionFactory.createConnection(cfg);
		Table table = connection.getTable(TableName.valueOf(tablename));
		Put p1 = new Put(Bytes.toBytes(row));
		p1.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(data));
		table.put(p1);
		System.out.println("put '" + row + "','" + columnFamily + ":" + column + "','" + data + "'");
		connection.close();
	}
	
	// get all cells with a same row key
	public static void get(String tablename, String row) throws IOException
	{
		Connection connection = ConnectionFactory.createConnection(cfg);
		Table table = connection.getTable(TableName.valueOf(tablename));
		Get g = new Get(Bytes.toBytes(row));
		Result result = table.get(g);
		System.out.println("Get: " + result);
		// a result is a collection of cells that have a same row key
		for(Cell cell : result.rawCells())
		{	
			System.out.println(Bytes.toString(cell.getValueArray(), 
					cell.getValueOffset(), cell.getValueLength()));
		}
	}
	
	public ArrayList<Point> rangeQuery(String tableName, String columnFamily, String qualifyX, String qualifyY,
			long[] hilbert, ArrayList<Integer> ranges, double rectMinX, double rectMinY, double rectMaxX, double rectMaxY) throws IOException
	{
		Connection connection = ConnectionFactory.createConnection(cfg);
		Table table = connection.getTable(TableName.valueOf(tableName));
		ArrayList<Point> queriedPoints = new ArrayList<Point>();
		int counter = 0;
		for(int i = 0, j = 1; j < ranges.size(); i+=2, j+=2)
		{
			String startStr = String.valueOf((hilbert[ranges.get(i).intValue()]));
			String endStr   = String.valueOf((hilbert[ranges.get(j).intValue()]) + 1);  // [startRow, endRow]
			
			StringBuffer startBuffer = new StringBuffer();
			byte[] temp = new byte[hilbertLength - startStr.length()];
			Arrays.fill(temp, (byte)'0');
			startBuffer.append(new String(temp));
			startBuffer.append(startStr);
			
			StringBuffer endBuffer   = new StringBuffer();
			temp = new byte[hilbertLength - endStr.length()];
			Arrays.fill(temp, (byte)'0');
			endBuffer.append(new String(temp));
			endBuffer.append(endStr);
			
			//System.out.println("\n" + startBuffer.toString() + " --> " + endBuffer.toString());
			// 过滤：限定 (x, y)的取值范围
			FilterList filterList = new FilterList(Operator.MUST_PASS_ALL);
			SingleColumnValueFilter filterMinX = new SingleColumnValueFilter(
					Bytes.toBytes(columnFamily), Bytes.toBytes(qualifyX),
					CompareFilter.CompareOp.GREATER_OR_EQUAL, 
					new BinaryComparator(Bytes.toBytes(rectMinX)));
			filterList.addFilter(filterMinX);
			
			SingleColumnValueFilter filterMaxX = new SingleColumnValueFilter(
					Bytes.toBytes(columnFamily), Bytes.toBytes(qualifyX),
					CompareFilter.CompareOp.LESS,
					new BinaryComparator(Bytes.toBytes(rectMaxX)));
			filterList.addFilter(filterMaxX);

			SingleColumnValueFilter filterMinY = new SingleColumnValueFilter(
					Bytes.toBytes(columnFamily), Bytes.toBytes(qualifyY),
					CompareFilter.CompareOp.GREATER_OR_EQUAL,
					new BinaryComparator(Bytes.toBytes(rectMinY)));
			filterList.addFilter(filterMinY);
			
			SingleColumnValueFilter filterMaxY = new SingleColumnValueFilter(
					Bytes.toBytes(columnFamily), Bytes.toBytes(qualifyY),
					CompareFilter.CompareOp.LESS,
					new BinaryComparator(Bytes.toBytes(rectMaxY)));
			filterList.addFilter(filterMaxY);
			
			Scan scan = new Scan();
			scan.setStartRow(Bytes.toBytes(startBuffer.toString()));
			scan.setStopRow(Bytes.toBytes(endBuffer.toString()));
			scan.setFilter(filterList);
			
			ResultScanner scanner = table.getScanner(scan);
			for(Result result : scanner)
			{
				++counter;
//				for(Cell cell : result.rawCells())
//				{
//					//System.out.println("Cell: " + cell + ", Value: " + 
//						//	Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
//				}
			}
		}
		double ratio = (rectMaxX - rectMinX) * (rectMaxY - rectMinY) / (512*512);
		System.out.println("theoretical points = " + (int)(TOTAL_POINTS * ratio) + "\nreality points = " + counter);
		return queriedPoints;
	}

	
	//show all data, through HTable Scan
	public static void scan(String tableName) throws IOException
	{
		Connection connection = ConnectionFactory.createConnection(cfg);
		Table table = connection.getTable(TableName.valueOf(tableName));
		Scan s = new Scan();
		ResultScanner rs = table.getScanner(s);
		for(Result result : rs)
		{// a result is a collection of cells that have a same row key
			for(Cell cell : result.rawCells())
			{	
				System.out.print(Bytes.toString(cell.getValueArray(), 
						cell.getValueOffset(), cell.getValueLength()) + "  ");
			}
			System.out.println();
		}
	}
	
	public static boolean delete(String tablename) throws IOException
	{
		Connection connection = ConnectionFactory.createConnection(cfg);
		Admin admin = connection.getAdmin();
		TableName tableName = TableName.valueOf(tablename);
		if(admin.tableExists(tableName))
		{
			try
			{
				admin.disableTable(tableName);
				admin.deleteTable(tableName);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public static void printBytes(byte[] b)
	{
		for(int i = 0; i < b.length; ++i)
		{
			System.out.print(b[i] + " ");
		}
		System.out.println();
	}
	
	public static void main(String [] args)
	{
		byte[] b1 = Bytes.toBytes(30.1110);
		printBytes(b1);
		
		byte[] b2 = Bytes.toBytes(3.20);
		printBytes(b2);
		
		byte[] b3 = Bytes.toBytes(String.valueOf(3000));
		printBytes(b3);
		
		byte[] b4 = Bytes.toBytes(String.valueOf(300.1));
		printBytes(b4);
	}
}
