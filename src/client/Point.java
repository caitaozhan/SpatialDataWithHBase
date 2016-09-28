package client;

/**
 * encapsulate a two dimensional spatial point
 * @author Caitao Zhan (caitaozhan@163.com)
 * @see <a href="https://github.com/caitaozhan/SpatialDataWithHBase">Github</a>
 */
public class Point
{
	private int m_x;
	private int m_y;

	public Point()
	{
		m_x = m_y = 0;
	}

	public Point(int x, int y)
	{
		m_x = x;
		m_y = y;
	}

	public void setX(int x)
	{
		m_x = x;
	}

	public void setY(int y)
	{
		m_y = y;
	}

	public int getX()
	{
		return m_x;
	}

	public int getY()
	{
		return m_y;
	}
	
	/*
	 * 以可点击区域的最左上角为（0，0）
	 */
	public void calibrate2Clickable()
	{
		m_x -= 8;
		m_y -= 31;
	}

	/*
	 * 以java程序界面的最左上角为（0，0）
	 */
	public void calibrate2JavaApp()
	{
		m_x += 8;
		m_y += 31;
	}
	
	public void hilbertCurve()
	{

	}

	public String toString()
	{
		return "(" + m_x + "," + m_y + ")";
	}

	public static void main(String[] args)
	{

	}
}
