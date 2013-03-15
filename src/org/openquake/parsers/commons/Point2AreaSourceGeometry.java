package org.openquake.parsers.commons;

import org.opensha.commons.geo.LocationList;

public class Point2AreaSourceGeometry {

	double id;
	public LocationList vertexes;

	public Point2AreaSourceGeometry() {
	}

	public Point2AreaSourceGeometry(double id, LocationList vertexes) {
		this.id = id;
		this.vertexes = vertexes;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public void setVertexes(LocationList vertexes) {
		this.vertexes = vertexes;
	}

	public LocationList getVertexes() {
		return this.vertexes;
	}

	public double getId() {
		return this.id;
	}
	
}
