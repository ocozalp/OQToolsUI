package org.openquake.parsers.commons;

public class GutenbergRichterAB {
	private double a;
	private double b;

	public GutenbergRichterAB(double a, double b) {
		if(b<0){
			throw new RuntimeException("b value cannot be negative");
		}
		this.a = a;
		this.b = b;
	}

	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}
}

