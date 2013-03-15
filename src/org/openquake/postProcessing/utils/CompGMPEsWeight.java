package org.openquake.postProcessing.utils;

public class CompGMPEsWeight {
	// computes GmpesWeights
	public static double  computeGMPESWeight(String gmpes) {

		double w = 1;
		String[] gs = GetGMPEsDir.getGMPEs(gmpes);		
		
		for (String g : gs)
		{
			if (GMPEsWeights.gmpeAscWeights.containsKey(g))
				w *= GMPEsWeights.gmpeAscWeights.get(g);
			else if (GMPEsWeights.gmpeScrWeights.containsKey(g))
				w *= GMPEsWeights.gmpeScrWeights.get(g);
			else  if (GMPEsWeights.gmpeInSlabWeights.containsKey(g))
				w *= GMPEsWeights.gmpeInSlabWeights.get(g);
			else  if (GMPEsWeights.gmpeInterfaceWeights.containsKey(g))
				w *= GMPEsWeights.gmpeInterfaceWeights.get(g);
			else  if (GMPEsWeights.gmpeShieldWeights.containsKey(g))
				w *= GMPEsWeights.gmpeShieldWeights.get(g);
			else System.out.println("not found:" + g);							
		}
				
		return w;
		
	}
}
