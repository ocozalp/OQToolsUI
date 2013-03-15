package org.openquake.config;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openquake.generator.Area;
import org.openquake.generator.Attenuation;
import org.openquake.generator.SourceModel;

public class ParameterDeserializer {

	public static HashMap<String, SourceModel> getAlgorithmMap(String rawData) {
		HashMap<String, SourceModel> result = new HashMap<String, SourceModel>();
		StringTokenizer modelTokenizer = new StringTokenizer(rawData, ";");
		
		while(modelTokenizer.hasMoreTokens()) {
			String modelStr = modelTokenizer.nextToken().trim();
			int startIndex = modelStr.indexOf("{");
			int endIndex = modelStr.lastIndexOf("}");
			
			String modelNameStr = modelStr.substring(0, startIndex).trim();
			int index = modelNameStr.indexOf("#");
			String modelName = modelNameStr.substring(0, index);
			String modelType = modelNameStr.substring(index + 1);
			SourceModel model = new SourceModel(modelName, modelType);
			
			String modelAreaStr = modelStr.substring(startIndex + 1, endIndex);
			
			StringTokenizer areaTokenizer = new StringTokenizer(modelAreaStr, "|");
			
			while(areaTokenizer.hasMoreTokens()) {
				String areaStr = areaTokenizer.nextToken().trim();
				int areaStart = areaStr.indexOf("[");
				int areaEnd = areaStr.lastIndexOf("]");
				String areaMeta = areaStr.substring(0, areaStart);
				
				int areaDel = areaMeta.indexOf("#");
				String areaName = areaMeta.substring(0, areaDel);
				String areaType = areaMeta.substring(areaDel + 1);
				
				Area area = new Area();
				area.setName(areaName);
				area.setType(areaType);
				
				String algorithmStr = areaStr.substring(areaStart + 1, areaEnd);
				
				StringTokenizer algorithmTokenizer = new StringTokenizer(algorithmStr, ",");
				while(algorithmTokenizer.hasMoreTokens()) {
					Attenuation atten = new Attenuation();
					atten.setName(algorithmTokenizer.nextToken());
					area.getAttenuations().put(atten.getName(), atten);
				}
				
				model.getAreas().put(area.getName(), area);
			}
			
			result.put(model.getName(), model);
		}
		return result;
	}
	
	public static Vector<Coordinate> getCoordinateList(String rawData) {
		StringTokenizer st = new StringTokenizer(rawData, ",");
		Vector<Coordinate> result = new Vector<Coordinate>();
		while(st.hasMoreTokens()) {
			String lat = st.nextToken();
			String lon = st.nextToken();
			
			Coordinate coord = new Coordinate();
			coord.latitude = Double.parseDouble(lat);
			coord.longitude = Double.parseDouble(lon);
			result.add(coord);
		}
		
		return result;
	}

	public static Vector<Double> getPeriodList(String rawData) {
		Vector<Double> result = new Vector<Double>();
		StringTokenizer st = new StringTokenizer(rawData, ",");
		while(st.hasMoreTokens()) {
			result.add(Double.parseDouble(st.nextToken()));
		}
		return result;
	}
	
}
