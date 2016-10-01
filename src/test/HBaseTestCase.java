package test;

import java.io.IOException;
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


public class HBaseTestCase
{
	//static HBase Configuration, /conf/hbase-site.xml
	static Configuration cfg = HBaseConfiguration.create();
	
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
		String tablename = "hbase_tb2";
		String columnFamily = "cf";
		try
		{
			HBaseTestCase.create(tablename, columnFamily);
			HBaseTestCase.put(tablename, "row1", columnFamily, "cl1", "hello caitao");
			HBaseTestCase.put(tablename, "row1", columnFamily, "cl2", "hello world");
			HBaseTestCase.get(tablename, "row1");
			HBaseTestCase.scan(tablename);
			
//			byte[] bytes1 = Bytes.toBytes(6.66666);
//			System.out.println(bytes1);
//			System.out.println(new String(bytes1) + "\n");
//			
//			byte[] bytes2 = Bytes.toBytes("6.66666");
//			System.out.println(bytes2);
//			System.out.println(new String(bytes2) + "\n");
//			
//			byte[] bytes3 = new String("6.66666").getBytes();
//			System.out.println(bytes3);
//			System.out.println(new String(bytes3) + "\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}


