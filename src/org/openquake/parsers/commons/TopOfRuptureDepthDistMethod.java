package org.openquake.parsers.commons;

import org.opensha.commons.calc.magScalingRelations.magScalingRelImpl.WC1994_MagAreaRelationship;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;

public class TopOfRuptureDepthDistMethod {
	/**
	 * Create top of rupture depth distribution.    
	 * @return 
	 */
	/**
	 * @param aspectRatio
	 * @param thresholdMagnitude
	 * @param averageHypoDepth
	 * @return
	 */
	public static  ArbitrarilyDiscretizedFunc getTopOfRuptureDepthDist(double aspectRatio, 
			double thresholdMagnitude, double averageHypoDepth) {

		double deltaMag = 0.2;
//		double[] mags = {5.5, 5.7, 5.9,
		// for subduction
		double[] mags = {6.1, 6.3, 6.5, 6.7, 6.9, 
				         7.1, 7.3, 7.5, 7.7, 7.9,
				         8.1, 8.3, 8.5, 8.7, 8.9};
		WC1994_MagAreaRelationship magAreaRel = new WC1994_MagAreaRelationship(); 
		ArbitrarilyDiscretizedFunc aveRupTopVsMag = new ArbitrarilyDiscretizedFunc(); 
		
        for (int i=0;i<mags.length;i++) {
           
        	double mag = mags[i];
            double ruptureWC94Area =  magAreaRel.getMedianArea(mag);

            // compute rupture width
            double RuptureWidth = Math.sqrt((ruptureWC94Area/aspectRatio));
            
            // compute the depth to the top of rupture corresponding to the maximum magnitude
            double aveRupTopDepth = averageHypoDepth - RuptureWidth;

            if (aveRupTopDepth <= 0.00) aveRupTopDepth = 0.00;
        
            aveRupTopVsMag.set(mag - deltaMag, aveRupTopDepth);
        }


        return aveRupTopVsMag;
        
	}
	
	/**
	 * Create top of rupture depth distribution. Above threshold magnitude, top
	 * of rupture depth is set equal to average hypocentral depth.
	 */
	public static ArbitrarilyDiscretizedFunc getTopOfRuptureDepthDist(
			double thresholdMagnitude, double averageHypoDepth) {
		ArbitrarilyDiscretizedFunc aveRupTopVsMag = new ArbitrarilyDiscretizedFunc();
		aveRupTopVsMag.set(thresholdMagnitude, averageHypoDepth);
		return aveRupTopVsMag;
	}

}
