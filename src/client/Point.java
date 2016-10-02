package client;

import java.util.Vector;

/**
 * encapsulate a two dimensional spatial point
 * @author Caitao Zhan (caitaozhan@163.com)
 * @see <a href="https://github.com/caitaozhan/SpatialDataWithHBase">Github</a>
 */
public class Point
{	
	private double m_x;
	private double m_y;
	private int m_id;

	public Point()
	{
		m_x = m_y = 0;
		m_id = 0;
	}
	
	public Point(int id)
	{
		m_x = m_y = 0;
		m_id = id;
	}

	public Point(int x, int y)
	{
		m_x = x;
		m_y = y;
		m_id = 0;
	}

	public void setX(double x)
	{
		m_x = x;
	}

	public void setY(double y)
	{
		m_y = y;
	}

	public double getX()
	{
		return m_x;
	}

	public String getStringX()
	{
		return String.valueOf(m_x);
	}
	
	public double getY()
	{
		return m_y;
	}
	
	public String getStringY()
	{
		return String.valueOf(m_y);
	}
	
	public int getID()
	{
		return m_id;
	}
	
	public String getStringID()
	{
		return String.valueOf(m_id);
	}
	
	/*
	 * 以可点击区域的最左上角为（0，0）
	 */
	public void calibrate2Clickable()
	{
		m_x -= 1;
		m_y -= 57;
	}

	/*
	 * 以java程序界面的最左上角为（0，0）
	 */
	public void calibrate2JavaApp()
	{
		m_x += 8;
		m_y += 31;
	}
	
	public void random(int maxX, int maxY)
	{
		m_x = Math.random() * maxX;
		m_y = Math.random() * maxY;
	}

	public String toString()
	{
		return "(" + m_x + ", " + m_y + ")";
	}

	public static void main(String[] args)
	{

		Vector<Point> points = new Vector<Point>();
		for(int i = 0; i < 100; ++i)
		{
			Point point = new Point();
			point.random(512, 512);
			points.add(point);
		}
	}
}
