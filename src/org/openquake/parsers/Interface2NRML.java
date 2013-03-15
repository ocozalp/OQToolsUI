package org.openquake.parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openquake.parsers.commons.MeanMFDFunctions;
import org.openquake.parsers.commons.ShapeFileConstants;
import org.openquake.parsers.commons.TectRegionUtils;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationList;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMSourceData;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMSubductionFaultSourceData;
import org.opensha.sha.faultSurface.FaultTrace;
import org.opensha.sha.magdist.IncrementalMagFreqDist;
import org.opensha.sha.util.TectonicRegionType;

import diewald_shapeFile.files.dbf.DBF_Field;
import diewald_shapeFile.files.dbf.DBF_File;
import diewald_shapeFile.files.shp.SHP_File;
import diewald_shapeFile.files.shp.shapeTypes.ShpPolyLine;
import diewald_shapeFile.files.shp.shapeTypes.ShpShape;
import diewald_shapeFile.files.shx.SHX_File;
import diewald_shapeFile.shapeFile.ShapeFile;

public class Interface2NRML extends GemFileParser {

	public static double RAKE_THRUST = 90.0;
	
	private String shape_file;

	/**
	 * Parser constructor. Initialize source data list, and file paths.
	 */
	public Interface2NRML(String shape_file) {
		this.shape_file = shape_file;
	}

	/**
	 * Parse source data, and create ArrayList<GEMSourceData> with given
	 * threshold magnitude for top of rupture depth distribution, strike and dip
	 * values for focal mechanism definition.
	 */
	public void parser(double thresholdMagnitude, double minimumMagnitude) {
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

			File shapeF = new File(shape_file);
			shapefile_src_data = new ShapeFile(shapeF.getParent(), shapeF.getName().split("\\.")[0]); 
			shapefile_src_data.READ();

			ShpShape.Type shape_type = shapefile_src_data.getSHP_shapeType();
			System.out.println("\nshape_type = " + shape_type);

			int number_of_shapes = shapefile_src_data.getSHP_shapeCount();
			int number_of_fields = shapefile_src_data.getDBF_fieldCount();

			// count the number of shapes and polygons
			for(int i = 0; i < number_of_shapes; i++){
				ShpPolyLine shape    = shapefile_src_data.getSHP_shape(i);
				String[] shape_info = shapefile_src_data.getDBF_record(i);
				ShpShape.Type type     = shape.getShapeType();
				System.out.printf("\nSHAPE[%2d] - %s\n", i, type);

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
				System.out.println(mfd);

				// 4. get tectonic region type
				TectonicRegionType tectReg = TectRegionUtils.getTectonicRegionTypeAsModel(srcRecord);;
				
				FaultTrace faultTrace = new FaultTrace(ShapeFileConstants.id);
				
				// 10. create source
				boolean floatRuptureFlag = true;

				GEMSubductionFaultSourceData ComplexFaultData = new GEMSubductionFaultSourceData(MeanMFDFunctions.getID(srcRecord), 
						MeanMFDFunctions.getID(srcRecord), tectReg, 
						faultTrace, faultTrace, RAKE_THRUST, mfd, 
				        floatRuptureFlag);
						srcDataList.add(ComplexFaultData);
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException {
		if(args.length != 2) {
			System.out.println("Usage : <command_name> source_file target_file");
			return;
		}
		
		parse(args[0], args[1], 5.0);
	}
	
	public static void parse(String sourceFile, String targetFile, double minimumMagnitude) {
		Interface2NRML parser = new Interface2NRML(sourceFile);
		double thresholdMagnitude = 6.0;
		parser.parser(thresholdMagnitude, minimumMagnitude);
		parser.writeSource2NrmlFormat(new File(targetFile));
	}
}
