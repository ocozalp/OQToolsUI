package org.openquake.parsers.commons;
import java.util.Map;

import org.opensha.commons.geo.BorderType;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationList;
import org.opensha.commons.geo.Region;

public class RegionUtils {

	/**
	 * Get source region, by reading source geometry file, and by extracting
	 * source geometry from the source ID.
	 */
	public static Region getRegion(LocationList border) {
		return new Region(border, BorderType.MERCATOR_LINEAR);

	}

	/**
	 * @param srcRecord
	 * @return
	 * returns list of vertexes for smooth seismicity model
	 */

	public static LocationList getRegionSmoothSeismicity( Map<String, Object> srcRecord){

		double latLonDelta = 0.05;

		double lon = (Double) srcRecord.get(SEDssConstants.lon);
		double lat = (Double) srcRecord.get(SEDssConstants.lat);

		double NorthLat = lat + latLonDelta;
		double SouthLat = lat - latLonDelta;
		double WestLon  = lon - latLonDelta;
		double EastLon  = lon + latLonDelta;

		LocationList vertexes = new LocationList();
        // generate polygon vertexes -- counterwise enumeration		
		vertexes.add(new Location(NorthLat, WestLon));
		vertexes.add(new Location(NorthLat, EastLon));
		vertexes.add(new Location(SouthLat, EastLon));
		vertexes.add(new Location(SouthLat, WestLon));
		vertexes.add(new Location(NorthLat, WestLon)); 
		System.out.println(vertexes);

		return (LocationList) vertexes;	
	}
	
}