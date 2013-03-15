package org.openquake.parsers;



/**
 * 
 */
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openquake.parsers.commons.HypoDepthDistribution;
import org.openquake.parsers.commons.MeanMFDFunctions;
import org.openquake.parsers.commons.RegionUtils;
import org.openquake.parsers.commons.TectRegionUtils;
import org.openquake.parsers.commons.TopOfRuptureDepthDistMethod;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationList;
import org.opensha.commons.geo.Region;
import org.opensha.sha.earthquake.griddedForecast.MagFreqDistsForFocalMechs;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMAreaSourceData;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMSourceData;
import org.opensha.sha.magdist.IncrementalMagFreqDist;
import org.opensha.sha.util.TectonicRegionType;

import diewald_shapeFile.files.dbf.DBF_Field;
import diewald_shapeFile.files.dbf.DBF_File;
import diewald_shapeFile.files.shp.SHP_File;
import diewald_shapeFile.files.shp.shapeTypes.ShpPolygon;
import diewald_shapeFile.files.shx.SHX_File;
import diewald_shapeFile.shapeFile.ShapeFile;




/**
 * @author laurentiudanciu
 * This class reads an area source model and generates a mean source model according to the source model logic tree - defined in SHARE project; 
 *
 */
public class ShapeFile2NRMLconverter extends GemFileParser {

	private String fileName;

	/**
	 * Parser constructor. Initialize source data list, and file paths.
	 */
	public ShapeFile2NRMLconverter(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Parse source data, and create ArrayList<GEMSourceData> with given
	 * threshold magnitude for top of rupture depth distribution, strike and dip
	 * values for focal mechanism definition.
	 */
	/**
	 * @param thresholdMagnitude
	 * @param strikeAngleValues
	 * @param dipAngleValues
	 */
	public void parser(double thresholdMagnitude, double[] strikeAngleValues,
			double[] dipAngleValues, double minimumMagnitude) {
		this.srcDataList = new ArrayList<GEMSourceData>();

		try {

			ShapeFile shapefile_src_data;

			DBF_File.LOG_INFO           = !false;
			DBF_File.LOG_ONLOAD_HEADER  = false;
			DBF_File.LOG_ONLOAD_CONTENT = false;

			SHX_File.LOG_INFO           = !false;
			SHX_File.LOG_ONLOAD_HEADER  = false;
			SHX_File.LOG_ONLOAD_CONTENT = false;

			SHP_File.LOG_INFO           = !false;
			SHP_File.LOG_ONLOAD_HEADER  = false;
			SHP_File.LOG_ONLOAD_CONTENT = false;
			
			File shapeFile = new File(fileName);
			shapefile_src_data = new ShapeFile(shapeFile.getParent(), shapeFile.getName().split("\\.")[0]); 

			shapefile_src_data.READ();

			int number_of_shapes = shapefile_src_data.getSHP_shapeCount();
			int number_of_fields = shapefile_src_data.getDBF_fieldCount();

			// count the number of shapes and polygons
			for(int i = 0; i < number_of_shapes; i++){
				ShpPolygon shape    = shapefile_src_data.getSHP_shape(i);
				String[] shape_info = shapefile_src_data.getDBF_record(i);

				int number_of_vertices = shape.getNumberOfPoints();
				LocationList border = new LocationList();

				for (int v = 0; v < number_of_vertices; v++){
					double lon = shape.getPoints()[v][0];
					double lat = shape.getPoints()[v][1];
					border.add(new Location(lat, lon));
				}

				Map<String,Object> srcRecord = new HashMap<String, Object>();

				// read data fields for each shape and each polygon
				for(int j = 0; j < number_of_fields; j++){
					String data = shape_info[j].trim();
					DBF_Field field = shapefile_src_data.getDBF_field(j);
					String field_name = field.getName();
					// assign data 
					srcRecord.put(field_name, data);
				}

				// 3. compute mean mfd 
				IncrementalMagFreqDist mfd = MeanMFDFunctions.computeMeanMfd(srcRecord, minimumMagnitude);
				if (mfd == null) continue;

				// 4. get tectonic region type
				TectonicRegionType tectReg = TectRegionUtils.getTectonicRegionTypeAsModel(srcRecord);

				// 5. get region
				Region reg = RegionUtils.getRegion(border);

				// 6. extract depth values and corresponding weights
				HypoDepthDistribution hypoDepthDist = MeanMFDFunctions.getHypoDepthDistribution(srcRecord);

				// 7. loop over depths
				for (int j = 0; j < hypoDepthDist.getDepths().length; j++) {

					double depth = hypoDepthDist.getDepths()[j];
					double depthWeight = hypoDepthDist.getDepthsWeights()[j];

					// 8. create mfds for focal mechanisms. A mfd-fm pair is
					// created for each Mmax-AB-strike-dip-rake combinations.
					MagFreqDistsForFocalMechs mfdFm = MeanMFDFunctions.getMagFreqDistsFM(srcRecord, 
							depthWeight, strikeAngleValues, dipAngleValues, mfd);

					// 9a. create top of rupture depth distribution for active shallow regions
					ArbitrarilyDiscretizedFunc aveRupTopVsMag = TopOfRuptureDepthDistMethod.getTopOfRuptureDepthDist(
							2, thresholdMagnitude, depth);
					
//					// 9b. create top of rupture depth distribution: for now assume equal to hypocentral depth
//					ArbitrarilyDiscretizedFunc aveRupTopVsMag = TopOfRuptureDepthDistMethod.getTopOfRuptureDepthDist(
//							thresholdMagnitude, depth);

					// 10. create source
					srcDataList.add(
							new GEMAreaSourceData("asz"
									+ String.valueOf(MeanMFDFunctions.getID(srcRecord) + "_"
											+ Double.toString(depth)),
											MeanMFDFunctions.getTitle(srcRecord), tectReg, reg, mfdFm,
											aveRupTopVsMag, depth));
				}

			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.println("Usage : <command_name> source_file target_file");
			return;
		}
		
		parse(args[0], args[1], 5.0);
	}

	public static void parse(String sourceFile, String targetFile, double minimumMagnitude) {
		ShapeFile2NRMLconverter c = new ShapeFile2NRMLconverter(sourceFile);
		
		double thresholdMagnitude = 6.00;
		double[] strikeAngleValues = new double[] {45.0};
		double[] dipAngleValues = new double[] {90.00};

		c.parser(thresholdMagnitude, strikeAngleValues,dipAngleValues, minimumMagnitude);
		c.writeSource2NrmlFormat(new File(targetFile));
	}

}
