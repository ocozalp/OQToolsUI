package org.openquake.postProcessing.utils;

import java.util.HashMap;

public class GMPEsWeights {
	public final static HashMap<String, Double> gmpeAscWeights = new HashMap<String, Double>();
	static {
		gmpeAscWeights.put("AkB_2010_AttenRel", 0.45); 
		gmpeAscWeights.put("CY_2008_AttenRel",  0.35);
		gmpeAscWeights.put("ZhaoEtAl_2006_AttenRel",  0.20);
	}	

	public final static HashMap<String, Double> gmpeScrWeights = new HashMap<String, Double>();
	static {
		gmpeScrWeights.put("scrAkB_2010_AttenRel", 0.20);
		gmpeScrWeights.put("scrCF_2008_AttenRel",  0.20);
		gmpeScrWeights.put("scrCY_2008_AttenRel",  0.20);
		gmpeScrWeights.put("scrCampbell_2003_SHARE_AttenRel", 0.20); 
		gmpeScrWeights.put("scrToroEtAl_2002_SHARE_AttenRel", 0.20);
	}	

	public final static HashMap<String, Double> gmpeShieldWeights = new HashMap<String, Double>();
	static {

		gmpeScrWeights.put("shieldCampbell_2003_SHARE_AttenRel", 0.50); 
		gmpeScrWeights.put("shieldToroEtAl_2002_SHARE_AttenRel", 0.50);
	}
	
	public final static HashMap<String, Double> gmpeInSlabWeights = new HashMap<String, Double>(); 
	static {
		gmpeInSlabWeights.put("inslabAB_2003_AttenRel", 0.20);
		gmpeInSlabWeights.put("inslabLL_2008_AttenRel", 0.20);
		gmpeInSlabWeights.put("inslabYoungsEtAl_1997_AttenRel", 0.20);
		gmpeInSlabWeights.put("inslabZhaoEtAl_2006_AttenRel",  0.40);
	}	
	public final static HashMap<String, Double> gmpeInterfaceWeights = new HashMap<String, Double>(); 
	static {
		gmpeInSlabWeights.put("interfaceAB_2003_AttenRel", 0.20);
		gmpeInSlabWeights.put("interfaceLL_2008_AttenRel", 0.20);
		gmpeInSlabWeights.put("interfaceYoungsEtAl_1997_AttenRel", 0.20);
		gmpeInSlabWeights.put("interfaceZhaoEtAl_2006_AttenRel",  0.40);
	}
}
