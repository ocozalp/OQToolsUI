package org.openquake.parsers.commons;

/**
 * @author laurentiudanciu
 *
 */
public class MaxMagDistribution {
	private double[] mags;
	private double[] magsWeights;

	/**
	 * @param mags
	 * @param magsWeights
	 */
	public MaxMagDistribution(double[] mags, double[] magsWeights) {
		this.mags = mags;
		this.magsWeights =  magsWeights;
	}

	public double[] getMags() {
		return mags;
	}

	public double[] getMagsWeights() {
		return magsWeights;
	}
}
