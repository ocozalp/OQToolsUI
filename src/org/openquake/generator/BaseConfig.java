package org.openquake.generator;

import java.util.HashMap;
import java.util.Vector;

public class BaseConfig {

	private HashMap<String, SourceModel> sourceModels;
	private String sourceModelPath;
	private boolean isFiniteRupture;
	private Vector<Double> periods;

	public HashMap<String, SourceModel> getSourceModels() {
		return sourceModels;
	}
	
	public void setSourceModels(HashMap<String, SourceModel> sourceModels) {
		this.sourceModels = sourceModels;
	}
	
	public String getSourceModelPath() {
		return sourceModelPath;
	}

	public void setSourceModelPath(String sourceModelPath) {
		this.sourceModelPath = sourceModelPath;
	}

	public boolean isFiniteRupture() {
		return isFiniteRupture;
	}

	public void setFiniteRupture(boolean isFiniteRupture) {
		this.isFiniteRupture = isFiniteRupture;
	}

	public Vector<Double> getPeriods() {
		return periods;
	}

	public void setPeriods(Vector<Double> periods) {
		this.periods = periods;
	}
}
