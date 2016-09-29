package client;

/**
 * encapsulate a two dimensional spatial point
 * @author Caitao Zhan (caitaozhan@163.com)
 * @see <a href="https://github.com/caitaozhan/SpatialDataWithHBase">Github</a>
 */
public class Point
{
	private double m_x;
	private double m_y;

	public Point()
	{
		m_x = m_y = 0;
	}

	public Point(int x, int y)
	{
		m_x = x;
		m_y = y;
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

	public double getY()
	{
		return m_y;
	}
	
	/*
	 * 以可点击区域的最左上角为（0，0）
	 */
	public void calibrate2Clickable()
	{
		m_x -= 1;
		m_y -= 49;
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

	}
}
