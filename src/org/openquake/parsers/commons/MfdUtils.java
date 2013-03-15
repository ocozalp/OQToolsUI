package org.openquake.parsers.commons;

import org.opensha.sha.magdist.IncrementalMagFreqDist;

public class MfdUtils {
	public static IncrementalMagFreqDist getScaledMFD(IncrementalMagFreqDist mfd,
			double weight) {
		IncrementalMagFreqDist scaledMfd = mfd.deepClone();

		for (int i = 0; i < scaledMfd.getNum(); i++) {
			double scaledRate = scaledMfd.getY(i) * weight;
			scaledMfd.set(i, scaledRate);
		}
		return scaledMfd;
	}

}
