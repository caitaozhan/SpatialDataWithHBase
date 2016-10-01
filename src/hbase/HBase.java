package hbase;

import java.io.IOException;
import java.util.Arrays;

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
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import client.Point;


public class HBase
{
	//static HBase Configuration, /conf/hbase-site.xml
	static Configuration cfg = HBaseConfiguration.create();
	
	/**
	 * generate a Point's row key. row key = hilbert + id
	 * row key length is 16: first 6 is hilbert curve encode, and last 10 is point's id
	 * @param point, a point that needs to generate a row key
	 * @return point's row key
	 */
	public static String generatePointRowKey(Point point)
	{
		int hilbertLength = 6, idLength = 10;
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
	
	//add an entry of data(i.e. cell) through HTable Put
	public static void put(String tablename, String row, String columnFamily, String column, String data) throws Exception
	{
		Connection connection = ConnectionFactory.createConnection(cfg);
		Table table = connection.getTable(TableName.valueOf(tablename));
		Put p1 = new Put(Bytes.toBytes(row));
		p1.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(data));
		table.put(p1);
		System.out.println("put '" + row + "','" + columnFamily + column + "','" + data + "'");
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
	
	//show all data, through HTable Scan
	public static void scan(String tablename) throws IOException
	{
		Connection connection = ConnectionFactory.createConnection(cfg);
		Table table = connection.getTable(TableName.valueOf(tablename));
		Scan s = new Scan();
		ResultScanner rs = table.getScanner(s);
		for(Result result : rs)
		{// a result is a collection of cells that have a same row key
			for(Cell cell : result.rawCells())
			{	
				System.out.println(Bytes.toString(cell.getValueArray(), 
						cell.getValueOffset(), cell.getValueLength()));
			}
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
	
	public static void main(String [] args)
	{
		String tablename = "SpatialTest";
		String columnFamily = "Point";
		try
		{
//			for(int i = 0; i < 100; ++i)
//			{
//				Point point = new Point();
//				point.random(512, 512);
//				String rowKey = generatePointRowKey(point);
//				System.out.println(rowKey);
//			}
			
			HBase.create(tablename, columnFamily);
			HBase.put(tablename, "row1", columnFamily, "x", "hello motto");
			HBase.put(tablename, "row1", columnFamily, "y", "hello world");
			HBase.get(tablename, "row1");
			HBase.scan(tablename);

			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}


