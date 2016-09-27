package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
class HBaseClient extends JFrame implements Runnable
{
	private static final long serialVersionUID = -2346534561072742542L;
	private JLabel myLabel;
	private JTextField editNumPoints;
	//private TextArea textArea;
	private JTextField jTextFieldInput;
	private JPanel panelNorth;
	private JButton insertButton;
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
		myLabel = new JLabel("随机产生");
		editNumPoints = new JTextField(6);
		editNumPoints.setEditable(true);
		panelNorth = new JPanel();
		panelNorth.add(myLabel);
		panelNorth.add(editNumPoints);
		panelNorth.add(getInsertButton());

		jTextFieldInput = new JTextField();
		jTextFieldInput.setEditable(true);

		setBounds(400, 100, 500, 500);
		setTitle("HBase Client -- 詹才韬");
		add(panelNorth, BorderLayout.NORTH);
		add(jTextFieldInput, BorderLayout.SOUTH);
		jTextFieldInput.addActionListener((ActionEvent e) ->
		{
			jTextFieldInput_actionPerformed(e);
		});

		addMouseListener(new MouseAdapter()
		{  //匿名内部类，鼠标事件
			public void mouseClicked(MouseEvent e)
			{   //鼠标完成点击事件
				if (e.getButton() == MouseEvent.BUTTON3)
				{ //e.getButton就会返回点鼠标的那个键，左键还是右健，3代表右键
					int x = e.getX();  //左上角为(0, 0)坐
		            int y = e.getY();
					System.out.println("(" + (x-8) + "," + (y-31) + ")");  // 校准
				}
			}
		});

		thread = new Thread(this); // 创建线程
		thread.start();
	}

	public void run()
	{
		while (true)
		{
			try
			{
			}
			catch (Exception e)
			{
			}
		}
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

	public JButton getInsertButton()
	{
		if (insertButton == null) // 当第一次调用这个方法的时候，rButton == null，进行初始化操作
		{
			insertButton = new JButton("个二维坐标，并插入HBase");
			insertButton.addActionListener((ActionEvent e) ->
			{
				try
				{
				}
				catch (Exception exception)
				{
					exception.printStackTrace();
				}
			});
		}
		return insertButton;
	}

}
