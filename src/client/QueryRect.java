package client;

public class QueryRect 
{
	private Point point1;
	private Point point2;

	public QueryRect()
	{
		point1 = new Point();
		point2 = new Point();
	}
	
	/**
	 * update the older point(a smaller id) with a new one
	 * @param point, a new point to update QueryRect
	 */
	public void update(Point point)
	{
		if(point1.getID() < point2.getID())
		{// point1 is older than point2
			point1 = null;
			point1 = point2;
			point2 = point;
			reorganize();
		}
		else
		{// point2 is older than point1
			point2 = null;
			point2 = point1;
			point1 = point;
			reorganize();
		}
	}
	
	/**
	 * make sure that point1 is at the upper left corner
	 * and point2 is an the lower right corner
	 */
	private void reorganize()
	{
		if(point2.getX() < point1.getX())
		{
			Point tempP = point1;
			point1 = point2;
			point2 = tempP;
			tempP = null;
		}
		if(point2.getY() < point1.getY())
		{
			double tempY = point2.getY();
			point2.setY(point1.getY());
			point1.setY(tempY);
		}
	}
	
	public String toString()
	{
		return "Range: " + point1.toString() + "--" + point2.toString();
	}
	
	public static void main(String[] args) 
	{
		Point p1 = new Point(4, 10);
		Point p2 = new Point(300, 400);
		QueryRect queryRect = new QueryRect();
		queryRect.update(p2);
		queryRect.update(p1);
		System.out.println(queryRect);
	}

}
