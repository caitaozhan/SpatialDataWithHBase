package hbase;

import com.google.common.collect.ImmutableMap;

/**
 * dimensionality reduction: map two dimensional (x,y) to one dimensional d
 * @see <a href="http://static.notdot.net/uploads/hilbert-table.png">Hilbert Table</a>
 * @see <a href="http://blog.csdn.net/firecoder/article/details/7178928">hilbert_map</a>
 * 
 */
public class HilbertCurve
{
	static ImmutableMap<String, Pair[][]> hilbertMap = null;

	static class Pair
	{
		int position;   // each square in Hilbert table has four positions
		String square;  // there are four squares in Hilbert Table
		Pair(int position, String square)
		{
			this.position = position;
			this.square = square;
		}
	}
	
	static
	{
		hilbertMap = ImmutableMap.of(
				"a", new Pair[][] { { new Pair(0, "d"), new Pair(3, "b") }, { new Pair(1, "a"), new Pair(2, "a") } }, 
				"b", new Pair[][] { { new Pair(2, "b"), new Pair(3, "a") }, { new Pair(1, "b"), new Pair(0, "c") } },
				"c", new Pair[][] { { new Pair(2, "c"), new Pair(1, "c") }, { new Pair(3, "d"), new Pair(0, "b") } }, 
				"d", new Pair[][] { { new Pair(0, "a"), new Pair(1, "d") }, { new Pair(3, "c"), new Pair(2, "d") } });
	}

	/**
	 * dimensionality reduction
	 * @param x
	 * @param y
	 * @param order, an n order curve fills a (2^n)*(2^n) grid 
	 * 
	 * @return a string, which is encoded from (x, y) using Hilbert curve
	 */
	public static long hilbertCurveEncode(int x, int y, int order)
	{
		String currentSquare = "a";
		long position = 0;  // max = 2^63-1
		int squareX = 0;
		int squareY = 0;
		int squarePosition = 0;
		
		for(int i = order - 1; i >= 0; i--)
		{
			position <<= 2;
			squareX = (x & (1 << i)) > 0 ? 1 : 0;
			squareY = (y & (1 << i)) > 0 ? 1 : 0;
			Pair pair = hilbertMap.get(currentSquare)[squareY][squareX];
			
			squarePosition = pair.position;
			currentSquare = pair.square;
			position |= squarePosition;
		}
		
		return position;
	}
	
	public static void main(String[] args)
	{
		System.out.println(hilbertCurveEncode(5, 2, 3));
	}

}
