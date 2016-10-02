package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import hbase.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;

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
	static private int INTERACT_POINT_ID = 0;  // not a beautiful design, just to give Points in queryInteract an ID
	static
	{// this is the schema of storing spatial points in HBase
		TABLE_NAME = new String("Spatial");
		COLUMN_FAMILY = new String("Point");
		QUALIFY_X = new String("X");
		QUALIFY_Y = new String("Y");
		QUALIFY_ID = new String("ID");
		try 
		{// create a HBase table
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
	private final int ORDER = 9;
	private JTextField editInsertNumber;
	private JButton insertButton;

	private JTextField queryInteract;
	private JButton queryButton;
	private JPanel panelNorth;
	private JPanel panelSouth;
//	Thread thread;

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
	
		queryInteract = new JTextField(10);
		queryInteract.setEditable(true);
		panelSouth = new JPanel();
		panelSouth.setLayout(new GridLayout(1, 2));
		panelSouth.add(queryInteract);
		panelSouth.add(getQueryButton());

		setBounds(100, 10, 513, 594);
		setTitle("HBase Client -- 詹才韬");
		setLayout(new BorderLayout(1, 1));
		add(panelNorth, BorderLayout.NORTH);
		add(panelSouth, BorderLayout.SOUTH);
		queryInteract.addActionListener((ActionEvent e) ->
		{
			jTextFieldInput_actionPerformed(e);
		});

		addMouseListener(new MouseAdapter()
		{  //匿名内部类，鼠标事件
			public void mouseClicked(MouseEvent e)
			{   //鼠标完成点击事件
				if (e.getButton() == MouseEvent.BUTTON3)
				{ //e.getButton就会返回点鼠标的那个键，左键还是右健，3代表右键
					Point point = new Point(++INTERACT_POINT_ID);
					point.setX(e.getX());
					point.setY(e.getY());
					point.calibrate2Clickable();  // 校准
					queryRect.update(point);
					queryInteract.setText(queryRect.toString());
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
					
					System.out.println("Successfully inserted " + insertNum + " random points into HBase!");
				}
				catch (NumberFormatException exception)
				{
					System.err.println("didn't input an integer");
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
			queryButton = new JButton("Query");
			queryButton.addActionListener((ActionEvent e) ->
			{
				try
				{//Range: (10.0, 20.0)--(50.0, 60.0)
					String queryText = queryInteract.getText();
					double RectMinX, RectMinY, RectMaxX, RectMaxY;
					String d = "(\\d+\\.\\d+)"; // can't represent every double, but is enough in this case
					String regex = ".*[^\\d]"+d+".*[^\\d]"+d+".*[^\\d]"+d+".*[^\\d]"+d+".*";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(queryText);
					if(matcher.find())
					{
						RectMinX = Double.valueOf(matcher.group(1));
						RectMinY = Double.valueOf(matcher.group(2));
						RectMaxX = Double.valueOf(matcher.group(3));
						RectMaxY = Double.valueOf(matcher.group(4));
					}
					else
					{
						throw new Exception("No match");
					}
					
					long[] hilbert = null;
					hilbert = getAllHilbertDistance((int)RectMinX, (int)RectMinY, (int)RectMaxX, (int)RectMaxY);
					ArrayList<Integer> ranges = splitRange(hilbert);
					ArrayList<Point> queryResult = caitaoHBase.rangeQuery(
							TABLE_NAME, COLUMN_FAMILY, QUALIFY_X, QUALIFY_Y,
							hilbert, ranges, RectMinX, RectMinY, RectMaxX, RectMaxY);
//					String text = queryInteract.getText();
//					queryInteract.setText(text + " --> " + String.valueOf(queryResult.size()) + "points");
//					System.out.println(queryResult);
				}
				catch (Exception exception)
				{
					System.out.println(exception);
					exception.printStackTrace();
				}
			});
		}
		return queryButton;
	}
	
	// the split ranges are inclusive on both sides, i.e., [.. , ..]
	private ArrayList<Integer> splitRange(long[] hilbert)
	{
//		for(int i = 0; i < hilbert.length; ++i)
//		{
//			System.out.print(hilbert[i] + " ");
//		}
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		try
		{
			for(int i = 0, j = 0; j < hilbert.length - 1; ++j)
			{
				if(hilbert[j + 1] - hilbert[j] == 1)
				{
					continue;
				}
				else
				{// inclusive: [lower , higher]
					ranges.add(Integer.valueOf(i));
					ranges.add(Integer.valueOf(j));
					i = j + 1;
				}
			}
			//System.out.println("\n" + ranges);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ranges;
	}
	
	private long[] getAllHilbertDistance(int minX, int minY, int maxX, int maxY)
	{
		int lenX = maxX - minX + 1;
		int lenY = maxY - minY + 1;
		long[] hilbert = new long[lenX*lenY];
		for(int i = 0; i < lenY; ++i)
		{
			for(int j = 0; j < lenX; ++j)
			{
				hilbert[i * lenX + j] = HilbertCurve.encode(minX + i, minY + j, ORDER);
			}
		}
		Arrays.sort(hilbert);
		return hilbert;
	}
}
