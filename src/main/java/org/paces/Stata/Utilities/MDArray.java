package org.paces.Stata.Utilities;

/**
 * @author Billy Buchanan
 * @version 0.0.0
 */
public class MDArray {

	/**
	 * Method to convert a 2D Object array to a 2D primitive array
	 * @param thedata A two-dimensional array of Bytes
	 * @return A two-dimensional array of bytes
	 */
	public static byte[][] toPrimative(Byte[][] thedata) {
		Integer i = thedata.length;
		Integer j = thedata[0].length;
		byte[][] retdata = new byte[i][j];
		for(Integer v = 0; v < i; v++) {
			for(Integer x = 0; x < j; x++) {
				retdata[v][x] = thedata[v][x];
			}
		}
		return retdata;
	}

	/**
	 * Method to convert a 2D Object array to a 2D primitive array
	 * @param thedata A two-dimensional array of Doubles
	 * @return A two-dimensional array of doubles
	 */
	public static double[][] toPrimative(Double[][] thedata) {
		Integer i = thedata.length;
		Integer j = thedata[0].length;
		double[][] retdata = new double[i][j];
		for(Integer v = 0; v < i; v++) {
			for(Integer x = 0; x < j; x++) {
				retdata[v][x] = thedata[v][x];
			}
		}
		return retdata;
	}

	/**
	 * Method to convert a 2D Object array to a 2D primitive array
	 * @param thedata A two-dimensional array of Integer
	 * @return A two-dimensional array of ints
	 */
	public static int[][] toPrimative(Integer[][] thedata) {
		Integer i = thedata.length;
		Integer j = thedata[0].length;
		int[][] retdata = new int[i][j];
		for(Integer v = 0; v < i; v++) {
			for(Integer x = 0; x < j; x++) {
				retdata[v][x] = thedata[v][x];
			}
		}
		return retdata;
	}

	/**
	 * Method to convert a 2D Object array to a 2D primitive array
	 * @param thedata A two-dimensional array of Longs
	 * @return A two-dimensional array of longs
	 */
	public static long[][] toPrimative(Long[][] thedata) {
		Integer i = thedata.length;
		Integer j = thedata[0].length;
		long[][] retdata = new long[i][j];
		for(Integer v = 0; v < i; v++) {
			for(Integer x = 0; x < j; x++) {
				retdata[v][x] = thedata[v][x];
			}
		}
		return retdata;
	}

}
