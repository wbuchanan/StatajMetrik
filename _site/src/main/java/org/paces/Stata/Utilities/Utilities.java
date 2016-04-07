package org.paces.Stata.Utilities;

import java.util.*;

/**
 * Created by billy on 2/7/16.
 */
public class Utilities {

	public static List<List<Double>> dataSetToList(double[][] values) {
		List<List<Double>> retvals = new ArrayList<>();
		for(Integer i = 0; i < values.length; i++) {
			List<Double> l1 = new ArrayList<>();
			for(Integer j = 0; j < values[i].length; j++) {
				l1.add(values[i][j]);
			}
			retvals.add(l1);
		}
		return retvals;
	}



}
