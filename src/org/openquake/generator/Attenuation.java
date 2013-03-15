package org.openquake.generator;

public class Attenuation {

	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Attenuation && ((Attenuation) obj).name.equals(name);
	}
	
}
