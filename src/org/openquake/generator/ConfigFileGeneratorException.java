package org.openquake.generator;

public class ConfigFileGeneratorException extends Exception {

	private static final long serialVersionUID = 7634571510356380309L;

	public ConfigFileGeneratorException() {
		super();
	}
	
	public ConfigFileGeneratorException(String message) {
		super(message);
	}
	
	public ConfigFileGeneratorException(Throwable t) {
		super(t);
	}
	
	public ConfigFileGeneratorException(String message, Throwable t) {
		super(message, t);
	}
}
