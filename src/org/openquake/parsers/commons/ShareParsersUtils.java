package org.openquake.parsers.commons;

import java.util.ArrayList;
import java.util.List;

public class ShareParsersUtils {
	
	/**
	 * Get number of non-zero faulting styles
	 */
	public static int getNonZeroFaultingStyles(double strikeSlipWeight,
			double normalWeight, double thrustWeight) {
		// count number of non-zero faulting style weights
		int numFaultingStyles = 0;
		if (strikeSlipWeight != 0.0)
			numFaultingStyles = numFaultingStyles + 1;
		if (normalWeight != 0.0)
			numFaultingStyles = numFaultingStyles + 1;
		if (thrustWeight != 0.0)
			numFaultingStyles = numFaultingStyles + 1;
		return numFaultingStyles;
	}
	
	/**
	 * Get list of non-zero values
	 */
	public static double[] getNonZeroValues(double[] values) {
		List<Double> v = new ArrayList<Double>();
		for (int i = 0; i < values.length; i++) {
			if (values[i] != 0.0) {
				v.add(values[i]);
			}
		}
		double[] vals = new double[v.size()];
		for (int i = 0; i < v.size(); i++) {
			vals[i] = v.get(i);
		}
		return vals;
	}
	// discard the sites of zero rates for all magnitudes
	public static boolean areAllZeros(double[]  values){
		for (int i = 0; i < values.length; i++) {
			if (values[i] > 0.0)
				return false;
		}
		return true;
	}
	
	// create unique ID for point sources
    public static String trimUUID (String uuid){ 
        char[] uuidChars = uuid.toCharArray();
        String newUUID ="";
        for(int i = 0; i<8;i++){
            newUUID += uuidChars[i];
        }                           
        return newUUID;
    }
    
    
}
