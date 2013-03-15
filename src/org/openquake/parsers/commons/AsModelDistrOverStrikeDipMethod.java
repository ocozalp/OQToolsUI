package org.openquake.parsers.commons;

import org.opensha.sha.earthquake.FocalMechanism;
import org.opensha.sha.earthquake.griddedForecast.MagFreqDistsForFocalMechs;
import org.opensha.sha.magdist.IncrementalMagFreqDist;

public class AsModelDistrOverStrikeDipMethod {
	/**
	 * Distribute mfd over strike-dip-rake combinations
	 * 
	 */
	/**
	 * @param depthWeight
	 * @param strikeValues
	 * @param dipValues
	 * @param strikeSlipWeight
	 * @param normalWeight
	 * @param thrustWeight
	 * @param mfd
	 * @return
	 */

	public static  MagFreqDistsForFocalMechs getMagFreqDistsForFocalMechs(
			double depthWeight, double[] strikeValues, double[] dipValues,
			double strikeSlipWeight, double normalWeight, double thrustWeight,
			IncrementalMagFreqDist mfd) {

		// count number of non-zero faulting style weights
		int numFaultingStyles = ShareParsersUtils.getNonZeroFaultingStyles(strikeSlipWeight,  
				normalWeight, thrustWeight);
//		System.err.println("FM weights    : "  + strikeSlipWeight +  normalWeight + thrustWeight);
		// mfdList and focMechList contains the total number of
		// strike-dip-rake combination
		IncrementalMagFreqDist[] mfdList = new IncrementalMagFreqDist[strikeValues.length
		                                                              * dipValues.length * numFaultingStyles];
		FocalMechanism[] focMechList = new FocalMechanism[strikeValues.length
		                                                  * dipValues.length * numFaultingStyles];

		int index = 0;
		// loop over strike values and compute the weights
		for (int is = 0; is < strikeValues.length; is++) {
			double strikeWeight = 1.0 / strikeValues.length;
			// loop over dip values
			for (int idip = 0; idip < dipValues.length; idip++) {
				double dipWeight = 1.0 / dipValues.length;

				// compute total weight (except faulting style)
				double weight = depthWeight * strikeWeight * dipWeight;

				// strike-slip -> rake = 0
				if (strikeSlipWeight != 0) {
					mfdList[index] = MfdUtils.getScaledMFD(mfd, weight
							* strikeSlipWeight);
					focMechList[index] = new FocalMechanism(strikeValues[is],
							dipValues[idip], ShapeFileConstants.RAKE_STRIKE_SLIP);
					index = index + 1;
				}

				// normal -> rake = -90.0
				if (normalWeight != 0) {
					mfdList[index] = MfdUtils.getScaledMFD(mfd, weight * normalWeight);
					focMechList[index] = new FocalMechanism(strikeValues[is],
							dipValues[idip], ShapeFileConstants.RAKE_NORMAL);
					index = index + 1;
				}

				// thrust -> rake = 90.0
				if (thrustWeight != 0) {
					mfdList[index] = MfdUtils.getScaledMFD(mfd, weight * thrustWeight);
					focMechList[index] = new FocalMechanism(strikeValues[is],
							dipValues[idip], ShapeFileConstants.RAKE_THRUST);
					index = index + 1;
				}
			}
		}

		return new MagFreqDistsForFocalMechs(mfdList, focMechList);
	}


}
