package org.openquake.parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//import org.gem.engine.hazard.parsers.GemFileParser;
import org.opensha.commons.geo.Location;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMFaultSourceData;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMSourceData;
import org.opensha.sha.faultSurface.FaultTrace;
import org.opensha.sha.magdist.GutenbergRichterMagFreqDist;
import org.opensha.sha.magdist.IncrementalMagFreqDist;
import org.opensha.sha.util.TectonicRegionType;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.ExtendedData;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.SchemaData;
import de.micromata.opengis.kml.v_2_2_0.SimpleData;

public class Inslab2NRML extends GemFileParser{
	private String[] idsToProcess;
	private static final double MINMAG = 5.5;
	private static final double deltaMFD = 0.1;
	//	private static final TectonicRegionType tectReg = TectonicRegionType.ACTIVE_SHALLOW;
	// tectonic regions
	private static String STABLE_CONTINENTAL_EXT = "SCR-Ext";
	private static String STABLE_CONTINENTAL_NO_EXT = "SCR-NoExt";
	private static String OCEANIC_CRUST = "OC";
	private static String AZORES_GIBRALTAR = "Azores-Gibraltar";
	private static String OCEANIC_RIDGE = "Ridge";
	private static String ACTIVE_SHALLOW_CRUST = "Active";
	/**
	 * Parser constructor. Initialize source data list, and file paths.
	 */
	public Inslab2NRML(String Path, String FileName, String[] idsToProcess) throws IOException{
		final Kml kml = Kml.unmarshal(new File(Path));
		final Document doc = (Document) kml.getFeature();
		this.idsToProcess = idsToProcess;

		srcDataList = new ArrayList<GEMSourceData>();

		List<Feature> featureList = doc.getFeature();
		for(Feature feat: featureList)
		{
			if(feat instanceof Folder)
			{
				final Folder folder = (Folder) feat;
				if(folder.getName().equalsIgnoreCase(FileName))
					System.out.println(FileName);
				{
					List<Feature> foldFeatList = folder.getFeature();
					for(Feature foldFeat : foldFeatList)
					{
						if(foldFeat instanceof Placemark)
						{
							Placemark src = (Placemark) foldFeat;
							String ID = null;
							TectonicRegionType tectRegType = null;

							double minDepth = Double.NaN;
							double maxDepth = Double.NaN;

							double strikeMin = Double.NaN;
							double strikeMax = Double.NaN;

							double minDip = Double.NaN;
							double maxDip = Double.NaN;
							double aveDip = Double.NaN;

							double minRake = Double.NaN;
							double maxRake = Double.NaN;
							double averRake = Double.NaN;

							// four MaxMags
							double maxMag01 = Double.NaN;
							double maxMag02 = Double.NaN;
							double maxMag03 = Double.NaN;
							double maxMag04 = Double.NaN;
							// four MaxMag Weights
							double wMaxMag01 = Double.NaN;
							double wMaxMag02 = Double.NaN;
							double wMaxMag03 = Double.NaN;
							double wMaxMag04 = Double.NaN;

							double aValGR = Double.NaN;
							double bValGR = Double.NaN;


							// seismicity parameters
							ExtendedData dataList = src.getExtendedData();
							SchemaData schemadata = dataList.getSchemaData().get(0);
							for (SimpleData simpledata: schemadata.getSimpleData()) {

								String name = simpledata.getName();
								String value = simpledata.getValue();
	

								if (name.equals("Fault_Id")) {
									ID = value + "_" + UUID.randomUUID().toString();
								}
								// read Tectonic Region
								if (name.equalsIgnoreCase("tectReg")) {
									tectRegType = getTectonicRegionType((value));
									//									System.out.println(tectRegType);
								}
								// read Minimum Depth
								else if (name.equals("MINDEPTH")) {
									minDepth = Double.valueOf(value);
									//									System.out.println(minDepth);
								}
								// read Maximum Depth
								else if (name.equals("MAXDEPTH")) {
									maxDepth = Double.valueOf(value);
									if(maxDepth > 20){
										maxDepth = 20.0;
									}
									if (maxDepth <=  minDepth) {
										throw new RuntimeException("ciccio");
									}
									//									System.out.println(maxDepth);
								}
								// read Minimum Slip	
								else if (name.equals("STRIKEMIN")) {
									strikeMin = Double.valueOf(value);
									//									System.out.println(strikeMin);
								}
								// read Maximum Slip 
								else if (name.equals("STRIKEMAX")) {
									strikeMax = Double.valueOf(value);
									//									System.out.println("strike max" + strikeMax);
								}
								// read Min Dip Angle Values	
								else if (name.equals("DIPMIN")) {
									minDip = Double.valueOf(value);
									//									System.out.println("minDip" + minDip);
								}

								else if (name.equals("DIPMAX")) {
									maxDip = Double.valueOf(value);
									if(maxDip > 90){
										maxDip = maxDip - 90.0;
									}
									//									System.out.println("maxDip " + maxDip);
								}

								else if (name.equals("RAKEMIN")) {
									minRake = Double.valueOf(value);
									//									System.out.println("minRake " + minRake);
								}
								else if (name.equals("RAKEMAX")) {
									maxRake = Double.valueOf(value);
									//									System.out.println("maxRake " + maxRake);
								}



								else if (name.equals("MAXMAG01")) {
									maxMag01 = Double.valueOf(value);
									//									System.out.println("maxMag: "+maxMag);
								}
								else if (name.equals("aValGR")) {
									aValGR = Double.valueOf(value);
									//									System.out.println(ID + "   bFbz:  "+ bFbz);
								}

								else if (name.equals("averageDip")) {
									aveDip = Double.valueOf(value);
									if(aveDip > 90){
										aveDip = aveDip - 90.0;	
									}
									//									System.out.println("aveDip: "+aveDip);
								}
							}

							if (idsToProcess != null && idsToProcess.length > 0) {
								boolean found = false;
								for (String idProbe : idsToProcess) {
									if (ID.equalsIgnoreCase(idProbe)) {
										found = true;
										//										System.err.println(ID.equalsIgnoreCase(idProbe));
									}
								}

								if (!found) {
									continue;
								}
							}

							// fault trace
							FaultTrace faultTrace = new FaultTrace(ID);
							Geometry geom = src.getGeometry();
							if(geom instanceof LineString)
							{
								LineString lineTrace = (LineString) geom;
								List<Coordinate> faultTraceCoords = lineTrace.getCoordinates();
								//								System.out.println("Fault trace (lon,lat): ");
								for(Coordinate coord : faultTraceCoords)
								{
									faultTrace.add(new Location(coord.getLatitude(), coord.getLongitude(), minDepth));
								}
							}					

							// check if the strike direction coming from the fault trace 
							// is compatible with the strike data. if not reverse the fault trace
							if(faultTrace.getStrikeDirection()<strikeMin || faultTrace.getStrikeDirection()>strikeMax)
								faultTrace.reverse();
							
							
							// define magnitude frequency distribution
							GutenbergRichterMagFreqDist mfd = createGrMfd(aValGR, bValGR,MINMAG, maxMag01, deltaMFD);

							double TotMomRate = mfd.getTotalMomentRate();
							//							System.out.println(ID + "     " + TotMomRate);
							//							System.out.println("processing fault : " +   ID + "   " + mfd);

							// define fault source data object
							boolean floatRuptureFlag = true;
							GEMFaultSourceData dissFaultData = new GEMFaultSourceData(ID, ID,
									tectRegType, mfd, faultTrace, aveDip, averRake, maxDepth, minDepth, 
									floatRuptureFlag);

							srcDataList.add(dissFaultData);
						}
					}
				}
			}
		}
	}
	private static GutenbergRichterMagFreqDist createGrMfd(double aVal, double bVal,
			double mMin, double mMax, double deltaMFD) {
		GutenbergRichterMagFreqDist mfd = null;
		// round mMin and mMax with respect to delta bin
		mMin = Math.round(mMin / deltaMFD) * deltaMFD;
		mMax = Math.round(mMax / deltaMFD) * deltaMFD;
		// compute total cumulative rate between minimum and maximum magnitude
		double totCumRate = Double.NaN;
		if (mMin != mMax) {
			totCumRate =
					Math.pow(10, aVal - bVal * mMin)
					- Math.pow(10, aVal - bVal * mMax);
			//			System.out.println(totCumRate);
		} else {
			// compute incremental a value and calculate rate corresponding to
			// minimum magnitude
			double aIncr = aVal + Math.log10(bVal * Math.log(10));
			totCumRate = Math.pow(10, aIncr - bVal * mMin);
		}
		if (mMax != mMin) {
			// shift to bin center
			mMin = mMin + deltaMFD / 2;
			mMax = mMax - deltaMFD / 2;
		}
		int numVal = (int) Math.round(((mMax - mMin) / deltaMFD + 1));


		mfd = new GutenbergRichterMagFreqDist(bVal, totCumRate, mMin, mMax,
				numVal);
		//		System.out.println(mfd);

		return mfd;

	}

	private TectonicRegionType getTectonicRegionType (String tecRegType) {
		if (tecRegType.equalsIgnoreCase(Inslab2NRML.ACTIVE_SHALLOW_CRUST)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		}
		else if (tecRegType.equalsIgnoreCase(Inslab2NRML.AZORES_GIBRALTAR)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		}
		else if (tecRegType.equalsIgnoreCase(Inslab2NRML.OCEANIC_CRUST)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		}
		else if (tecRegType.equalsIgnoreCase(Inslab2NRML.OCEANIC_RIDGE)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		}
		else if (tecRegType.equalsIgnoreCase(Inslab2NRML.STABLE_CONTINENTAL_EXT)) {
			return TectonicRegionType.STABLE_SHALLOW;
		}
		else if (tecRegType.equalsIgnoreCase(Inslab2NRML.STABLE_CONTINENTAL_NO_EXT)) {
			return TectonicRegionType.STABLE_SHALLOW;
		} else
			throw new RuntimeException("tectonic region type not recognized");
	}

}
