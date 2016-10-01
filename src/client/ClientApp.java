package client;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import hbase.*;

public class ClientApp
{
	public static void main(String[] args)
	{
		HBaseClient UDPserver = new HBaseClient();
		UDPserver.setVisible(true);
	}
}

/**
 * implement all functions in client(both server side stuffs and client side stuffs)
 * @author Caitao Zhan (caitaozhan@163.com)
 * @see <a href="https://github.com/caitaozhan/SpatialDataWithHBase">Github</a>
 */
class HBaseClient extends JFrame
{
	private static final long serialVersionUID = -2346534561072742542L;

	private HBase caitaoHBase;
	static private String TABLE_NAME;
	static private String COLUMN_FAMILY;
	static private String QUALIFY_X;
	static private String QUALIFY_Y;
	static private String QUALIFY_ID;
	static
	{// this is the schema of storing spatial points in HBase
		TABLE_NAME = new String("Spatial");
		COLUMN_FAMILY = new String("Point");
		QUALIFY_X = new String("X");
		QUALIFY_Y = new String("Y");
		QUALIFY_ID = new String("ID");
		try 
		{// create a HBase schema
			HBase.create(TABLE_NAME, COLUMN_FAMILY);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private QueryRect queryRect;
	private final int MAX_X = 512;
	private final int MAX_Y = 512;
	private JTextField editInsertNumber;
	private JButton insertButton;

	private JTextField queryResults;
	private JButton queryButton;
	private JPanel panelNorth;
	private JPanel panelSouth;
	Thread thread;

	public HBaseClient()
	{
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try
		{
			init();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void init() throws Exception
	{
		caitaoHBase = new HBase();
		queryRect = new QueryRect();
		
		setResizable(false);
		panelNorth = new JPanel();
		editInsertNumber = new JTextField();
		editInsertNumber.setEditable(true);
		panelNorth.setLayout(new GridLayout(1, 2));
		panelNorth.add(editInsertNumber);
		panelNorth.add(getInsertButton());
	
		queryResults = new JTextField(10);
		queryResults.setEditable(true);
		panelSouth = new JPanel();
		panelSouth.setLayout(new GridLayout(1, 2));
		panelSouth.add(queryResults);
		panelSouth.add(getQueryButton());

		setBounds(100, 10, 514, 587);
		setTitle("HBase Client -- 詹才韬");
		setLayout(new BorderLayout(1, 1));
		add(panelNorth, BorderLayout.NORTH);
		add(panelSouth, BorderLayout.SOUTH);
		queryResults.addActionListener((ActionEvent e) ->
		{
			jTextFieldInput_actionPerformed(e);
		});

		addMouseListener(new MouseAdapter()
		{  //匿名内部类，鼠标事件
			public void mouseClicked(MouseEvent e)
			{   //鼠标完成点击事件
				if (e.getButton() == MouseEvent.BUTTON3)
				{ //e.getButton就会返回点鼠标的那个键，左键还是右健，3代表右键
					Point point = new Point();
					point.setX(e.getX());
					point.setY(e.getY());
					point.calibrate2Clickable();  // 校准
					queryRect.update(point);
					System.out.println(queryRect);
				}
			}
		});
	}

	private JButton getInsertButton()
	{
		if (insertButton == null) // 当第一次调用这个方法的时候，rButton == null，进行初始化操作
		{
			insertButton = new JButton("Input N, put N random (x, y) into HBase");
			insertButton.addActionListener((ActionEvent e) ->
			{
				try
				{
					String string = editInsertNumber.getText();
					int insertNum = Integer.valueOf(string);
					
					caitaoHBase.insertRandomPoints(insertNum, MAX_X, MAX_Y,
							TABLE_NAME, COLUMN_FAMILY, QUALIFY_X, QUALIFY_Y, QUALIFY_ID);
				}
				catch (NumberFormatException exception)
				{
					System.out.println("didn't input an integer");
					exception.printStackTrace();
				}
				catch (Exception exception)
				{
					exception.printStackTrace();
				}
			});
		}
		return insertButton;
	}
	
	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			System.exit(0);
		}
	}

	void jTextFieldInput_actionPerformed(ActionEvent e)
	{
		try
		{
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	private JButton getQueryButton()
	{
		if (queryButton == null)
		{
			queryButton = new JButton("Range Query");
			queryButton.addActionListener((ActionEvent e) ->
			{
				try
				{

				}
				catch (Exception exception)
				{

				}
			});
		}
		return queryButton;
	}

}
