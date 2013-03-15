package org.openquake.parsers.commons;

import java.util.Random;

public class ShapeFileConstants {
	// tectonic regions
	public static String STABLE_CONTINENTAL_EXT = "SCR-Ext";
	public static String STABLE_CONTINENTAL_NO_EXT = "SCR-NoExt";
	public static String STABLE_CONTINENTAL_SHIELD = "SCR-Shield";
	public static String OCEANIC_CRUST = "OC";
	public static String AZORES_GIBRALTAR = "Azores-Gibraltar";
	public static String OCEANIC_RIDGE = "Ridge";
	public static String ACTIVE_SHALLOW_CRUST = "Active";
	public static String SUBDUCTION_INSLAB = "Inslab";
	public static String SUBDUCTION_INTERFACE = "Interface";
	
	// src record tags
	public static String id = "IDAS";
	public static String title = "IDAS";
	public static String name = "NAME";
	public static String tectonicRegionType = "TECREG";
	public static String strikeSlipWeight = "SS";
	public static String normalWeight     = "NF";
	public static String thrustWeight     = "TF";
	
	public static String minimumDepth   = "MINDEPTH";
	public static String averHypoDepth1 = "HYPODEPTH1";
	public static String averHypoDepth2 = "HYPODEPTH2";
	public static String averHypoDepth3 = "HYPODEPTH3";
	public static String averHypoDepth4 = "HYPODEPTH4"; 
	public static String averHypoDepth5 = "HYPODEPTH5";
	public static String maximumDepth   = "MAXDEPTH";
	
	public static String weightsHypoDepth1 = "WHDEPTH1";
	public static String weightsHypoDepth2 = "WHDEPTH2";
	public static String weightsHypoDepth3 = "WHDEPTH3";
	public static String weightsHypoDepth4 = "WHDEPTH4";
	public static String weightsHypoDepth5 = "WHDEPTH5";
	
	public static String maximumMagnitude1 = "MAXMAG01";
	public static String maximumMagnitude2 = "MAXMAG02";
	public static String maximumMagnitude3 = "MAXMAG03"; 
	public static String maximumMagnitude4 = "MAXMAG04";

	public static String weightMagMax1 = "WMAXMAG01";
	public static String weightMagMax2 = "WMAXMAG02";
	public static String weightMagMax3 = "WMAXMAG03";
	public static String weightMagMax4 = "WMAXMAG04";

	public static String aValueGR = "A";
	public static String bValueGR = "B";
	// Maximum Magnitude -weights

	public static String StrikeValues = "MINSTRIKE";
	public static String DipValues = "DIP";

	// rake value for strike-slip, thrust, and normal
	public static double RAKE_STRIKE_SLIP = 0.0;
	public static double RAKE_THRUST = 90.0;
	public static double RAKE_NORMAL = -90.0;

	// magnitude frequency distribution discretization interval
	public static double MFD_DELTA = 0.2;// for testing the computational model;
	
	// random number generator
	static long seed = 123456789;
	public static Random rn = new Random(seed);
	public static double aAndBweight = 1.00;
}
