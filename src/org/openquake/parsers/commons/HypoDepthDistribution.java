package org.openquake.parsers.commons;

public class HypoDepthDistribution {
	private double[] depths;
	private double[] depthsWeights;

	public HypoDepthDistribution(double[] depths, double[] depthsWeights,
			double minimumDepth, double maximumDepth) {
		if (depths.length == 0) {
			this.depths = new double[] { (minimumDepth + maximumDepth) / 2 };
			this.depthsWeights = new double[] { 1.0 };
		} else {
			this.depths = depths;
			this.depthsWeights = depthsWeights;
		}
	}

	public double[] getDepths() {
		return depths;
	}

	public double[] getDepthsWeights() {
		return depthsWeights;
	}
}
