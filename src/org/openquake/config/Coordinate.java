package org.openquake.config;

public class Coordinate {
	public double latitude, longitude;
	
	@Override
	public String toString() {
		return "Lat:" + latitude + ", Lon: " + longitude;
	}
}
