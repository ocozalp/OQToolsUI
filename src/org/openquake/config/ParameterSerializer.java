package org.openquake.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.openquake.generator.Area;
import org.openquake.generator.SourceModel;

public class ParameterSerializer {

	public static String getRawListData(HashMap<String, SourceModel> listData) {
		StringBuffer buffer = new StringBuffer();
		Iterator<String> modelIterator = listData.keySet().iterator();
		int modelCounter = 0;
		while(modelIterator.hasNext()) {
			SourceModel model = listData.get(modelIterator.next());
			if(modelCounter > 0)
				buffer.append(";");
			
			buffer.append(model.getName()).append("#").append(model.getType()).append("{");//start of model
			
			Iterator<String> areaIterator = model.getAreas().keySet().iterator();
			int areaCounter = 0;
			while(areaIterator.hasNext()) {
				Area area = model.getAreas().get(areaIterator.next());
				if(areaCounter > 0)
					buffer.append("|");
				
				buffer.append(area.getName()).append("#").append(area.getType()).append("[");
				
				Iterator<String> algorithms = area.getAttenuations().keySet().iterator();
				for(int i = 0; algorithms.hasNext(); ++i) {
					if(i > 0)
						buffer.append(",");
					
					buffer.append(algorithms.next());
				}

				buffer.append("]");
				
				++areaCounter;
			}
			
			buffer.append("}");//end of model
			++modelCounter;
		}
		return buffer.toString();
	}
	
	public static String getRawCoordinates(Vector<Coordinate> coordinates) {
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i<coordinates.size(); ++i) {
			if(i > 0) {
				buffer.append(",");
			}
			
			Coordinate c = coordinates.get(i);
			buffer.append(c.latitude).append(",").append(c.longitude);
		}
		
		return buffer.toString();
	}
	
	public static String getRawArray(Object[] array) {
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i<array.length; ++i) {
			if(i > 0) {
				buffer.append(",");
			}
			
			if(array[i] != null) {
				buffer.append(array[i].toString());
			}
		}
		
		return buffer.toString();
	}
	
}
