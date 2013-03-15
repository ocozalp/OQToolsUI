package org.openquake.generator;

import java.util.HashMap;

public class Area {

	private String name;
	private String type;
	private HashMap<String, Attenuation> attenuations;
	
	public Area() {
		attenuations = new HashMap<String, Attenuation>();
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
	public HashMap<String, Attenuation> getAttenuations() {
		return attenuations;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Area && ((Area) obj).name.equals(name);
	}
	
}
