package org.openquake.generator;

import java.util.HashMap;

public class SourceModel {

	private String name;
	private String type;
	private HashMap<String, Area> areas;
	
	public SourceModel(String name, String type) {
		this.name = name;
		this.type = type;
		areas = new HashMap<String, Area>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public HashMap<String, Area> getAreas() {
		return areas;
	}

	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SourceModel) && ((SourceModel) obj).getName().equals(name);
	}
	
}
