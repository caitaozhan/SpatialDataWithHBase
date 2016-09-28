package test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Layout
{
	static final int WIDTH = 300;
	static final int HEIGHT = 200;

	JFrame jf = new JFrame("BorderLayout示例");

	JButton jb1, jb2, jb3, jb4, jb5;
	JPanel panelN, panelC;

	public void go()
	{
		jf = new JFrame("BorderLayout");

		jb1 = new JButton("东");
		jb2 = new JButton("南");
		jb3 = new JButton("西");
		jb4 = new JButton("北");
		jb5 = new JButton("中");
		panelN = new JPanel();
		panelC = new JPanel();

		jf.setLayout(new BorderLayout(1, 1));// 上边距,下边距  

		//jf.add(jb1, BorderLayout.EAST);
		jf.add(jb2, BorderLayout.SOUTH);
		//jf.add(jb3, BorderLayout.WEST);
		jf.add(panelN, BorderLayout.NORTH);
		jf.add(panelC, BorderLayout.CENTER);

		jf.setSize(new Dimension(400, 200));
		jf.setLocation(600, 250);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}

	public static void main(String[] args)
	{
		new Layout().go();
	}

}