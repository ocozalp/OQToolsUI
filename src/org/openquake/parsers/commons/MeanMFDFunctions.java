package org.openquake.parsers.commons;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opensha.commons.calc.magScalingRelations.magScalingRelImpl.WC1994_MagAreaRelationship;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.data.function.EvenlyDiscretizedFunc;
import org.opensha.commons.geo.BorderType;
import org.opensha.commons.geo.LocationList;
import org.opensha.commons.geo.Region;
import org.opensha.sha.earthquake.FocalMechanism;
import org.opensha.sha.earthquake.griddedForecast.MagFreqDistsForFocalMechs;
import org.opensha.sha.faultSurface.FaultTrace;
import org.opensha.sha.magdist.GutenbergRichterMagFreqDist;
import org.opensha.sha.magdist.IncrementalMagFreqDist;
import org.opensha.sha.magdist.SummedMagFreqDist;
import org.opensha.sha.util.TectonicRegionType;



/**
 * @author laurentiudanciu
 *
 */

public class MeanMFDFunctions {

	public static void validateMaxMagnitudeValuesAndWeights(
			Map<String, Object> srcRecord, double[] maxMags,
			double[] maxMagsWeights) {
		if (maxMags[0] == 0.0) {
			String err = "Ignoring src: " + srcRecord.get(ShapeFileConstants.id)
			+ ", becasue maximum magnitude is null";
			throw new RuntimeException(err);
		}
		if (maxMags.length != maxMagsWeights.length) {
			String err = "Number of maximum magnitude values != to number of maximum magnitude weights!";
			throw new RuntimeException(err);
		}
	}
	
	public static IncrementalMagFreqDist computeMeanMfd(Map<String, Object> srcRecord, double minimumMagnitude) {
		// extract non-zero Mmaxes and corresponding weights
		double[] maxMags = getMaxMagnitudeValues(srcRecord);
		// no nonzeroMaxMag
		if (maxMags.length == 0){
			String err = "Ignoring src: " + srcRecord.get(ShapeFileConstants.id)
					+ ", becasue maximum magnitude is null";
			System.err.println(err);
			return null;
		}
//		System.out.println("original max mags");
		for(double v : maxMags){
//			System.out.println(v);
		}
		double[] maxMagsWeights = getMaxMagnitudeWeights(srcRecord);
		validateMaxMagnitudeValuesAndWeights(srcRecord, maxMags, maxMagsWeights);
		// extract a-b pair and the associated weights 
		double aVal = Double.parseDouble((String) srcRecord.get(ShapeFileConstants.aValueGR));
		double bVal = Double.parseDouble((String) srcRecord.get(ShapeFileConstants.bValueGR));
		GutenbergRichterAB[] abValues = new GutenbergRichterAB[1];
		abValues[0] = new GutenbergRichterAB(aVal, bVal);
		double[] aAndBWeights = new double[] { 1.0 };

		// compute mean mfd from all Mmaxes and ab combinations
		IncrementalMagFreqDist mfd = computeMeanMagFreqDist(maxMags,
				maxMagsWeights, abValues, aAndBWeights, minimumMagnitude);
		return mfd;
	}

	public static double[] getMaxMagnitudeWeights(Map<String, Object> srcRecord) {
		return ShareParsersUtils.getNonZeroValues(new double[] {
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.weightMagMax1)),
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.weightMagMax2)),
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.weightMagMax3)),
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.weightMagMax4)) });
	}

	static double[] getMaxMagnitudeValues(Map<String, Object> srcRecord) {
		return ShareParsersUtils.getNonZeroValues(new double[] {
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.maximumMagnitude1)),
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.maximumMagnitude2)),
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.maximumMagnitude3)),
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.maximumMagnitude4)) });
	}



	static double[] getHypoDepths(Map<String, Object> srcRecord) {
		return ShareParsersUtils.getNonZeroValues(new double[] {
				Double.parseDouble((String) srcRecord.get(ShapeFileConstants.averHypoDepth1)),
				Double.parseDouble((String) srcRecord.get(ShapeFileConstants.averHypoDepth2)),
				Double.parseDouble((String) srcRecord.get(ShapeFileConstants.averHypoDepth3)),
				Double.parseDouble((String) srcRecord.get(ShapeFileConstants.averHypoDepth4)),
				Double.parseDouble((String) srcRecord.get(ShapeFileConstants.averHypoDepth5)) });
	}
	static double[] getHypoDepthsWeights(Map<String, Object> srcRecord) {
		return ShareParsersUtils.getNonZeroValues(new double[] {
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.weightsHypoDepth1)),
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.weightsHypoDepth2)),
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.weightsHypoDepth3)),
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.weightsHypoDepth4)),
				Double.parseDouble((String)srcRecord.get(ShapeFileConstants.weightsHypoDepth5)) });
	}


	/**
	 * Compute mean MFD, from GR MFDs corresponding to all combinations of Mmax
	 * and ab weights.
	 */
	public static  SummedMagFreqDist computeMeanMagFreqDist(double[] maxMagValues,
			double[] maxMagWeights, GutenbergRichterAB[] aAndBValues,
			double[] aAndBWeights, double minimumMagnitude) {

		SummedMagFreqDist meanMfd = null;

		// round minimum value and shift to bin center
		double minMag = Math.round(minimumMagnitude / ShapeFileConstants.MFD_DELTA)
								* ShapeFileConstants.MFD_DELTA;
		minMag = minMag + ShapeFileConstants.MFD_DELTA / 2;
		minMag = new BigDecimal(minMag).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		// round maximum magnitude values
		double[] maxMagsRounded = new double[maxMagValues.length];
		for (int i = 0; i < maxMagValues.length; i++) {
			maxMagsRounded[i] = Math.round(maxMagValues[i] / ShapeFileConstants.MFD_DELTA)
			* ShapeFileConstants.MFD_DELTA;
		}
		// shift maximum magnitudes to bin center
		for (int i = 0; i < maxMagsRounded.length; i++) {
			maxMagsRounded[i] = maxMagsRounded[i] - ShapeFileConstants.MFD_DELTA / 2;
			maxMagsRounded[i] = new BigDecimal(maxMagsRounded[i]).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}

		// get maximum max value
		double maxMaxMag = -Double.MAX_VALUE;
		for (int i = 0; i < maxMagValues.length; i++) {
			if (maxMagsRounded[i] > maxMaxMag) {
				maxMaxMag = maxMagsRounded[i];
			}
		}


		// compute number of bins
		int num = (int) Math.round((maxMaxMag - minMag) / ShapeFileConstants.MFD_DELTA + 1);

		meanMfd = new SummedMagFreqDist(minMag, num, ShapeFileConstants.MFD_DELTA);//new SummedMagFreqDist(minMag, maxMaxMag, num);

//		System.out.println("Individual MFDs");
		// loop over maximum magnitude values
		for (int i = 0; i < maxMagValues.length; i++) {
			double maxMag = maxMagValues[i];
			double maxMagRounded = maxMagsRounded[i];
			double maxMagWeight = maxMagWeights[i];

			// loop over a and b values
			for (int j = 0; j < aAndBValues.length; j++) {
				double aValue = aAndBValues[j].getA();
				double bValue = aAndBValues[j].getB();
				double abWeight = aAndBWeights[j];

				// define GR mfd, by computing total cumulative rate (scaled by
				// the corresponding weight), between min and max
				GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(minMag,num, ShapeFileConstants.MFD_DELTA);
				double totCumRate = Math.pow(10, aValue - bValue
						* minimumMagnitude)
						- Math.pow(10, aValue - bValue * maxMag);
				totCumRate = totCumRate * maxMagWeight * abWeight;
				mfd.setAllButTotMoRate(minMag, maxMagRounded, totCumRate,
						bValue);

				mfd.getCumRateDist();

//				System.out.println("Max mag: "+maxMag+", a: "+aValue+", b: "+bValue+", max mag weight: "+maxMagWeight+", (a,b) weight: "+abWeight);
//				System.out.println("mag, incremental rate");
				for(int iv=0;iv<num;iv++){
//					System.out.println(mfd.getX(iv)+", "+mfd.getY(iv));
				}
//				System.out.println("mag, cumulative rate");
				EvenlyDiscretizedFunc cumMfd = mfd.getCumRateDist();
				for(int iv=0;iv<cumMfd.getNum();iv++){
//					System.out.println(cumMfd.getX(iv)+", "+cumMfd.getY(iv));
				}

				meanMfd.addIncrementalMagFreqDist(mfd);
			}
//			System.out.println("Mean MFD");
//			System.out.println("mag, incremental rate");
			for(int iv=0;iv<num;iv++){
//				System.out.println(meanMfd.getX(iv)+", "+meanMfd.getY(iv));
			}
//			System.out.println("mag, cumulative rate");
			EvenlyDiscretizedFunc cumMfd = meanMfd.getCumRateDist();
			for(int iv=0;iv<cumMfd.getNum();iv++){
//				System.out.println(cumMfd.getX(iv)+", "+cumMfd.getY(iv));
			}
		}

		return meanMfd;
	}

	public static HypoDepthDistribution getHypoDepthDistribution( 
			Map<String, Object> srcRecord) {
		return  new HypoDepthDistribution(
				MeanMFDFunctions.getHypoDepths(srcRecord),
				MeanMFDFunctions.getHypoDepthsWeights(srcRecord),
				Double.parseDouble((String) srcRecord.get(ShapeFileConstants.minimumDepth)),
				Double.parseDouble((String) srcRecord.get(ShapeFileConstants.maximumDepth)));

	}

	/**--bug fix!!! strike-normal-thrust weights were given not in percentage!!!!--**/ 
	public static MagFreqDistsForFocalMechs getMagFreqDistsFM(
			Map<String, Object> srcRecord, double depthWeight, 
			double strikeAngleValues[], double dipAngleValues[], IncrementalMagFreqDist mfd) { 
		return 	AsModelDistrOverStrikeDipMethod.getMagFreqDistsForFocalMechs(
				depthWeight, strikeAngleValues, dipAngleValues,
				Double.parseDouble((String) srcRecord.get(ShapeFileConstants.strikeSlipWeight))/100,
				Double.parseDouble((String) srcRecord.get(ShapeFileConstants.normalWeight))/100, 
				Double.parseDouble((String) srcRecord.get(ShapeFileConstants.thrustWeight))/100, mfd); 
	}

	public static String getID(Map<String, Object> srcRecord) {
		return (String) srcRecord.get(ShapeFileConstants.id);
	}

	public static String getTitle(Map<String, Object> srcRecord) {
		return (String) srcRecord.get(ShapeFileConstants.title);
	}

}



