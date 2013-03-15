package org.openquake.generator;

import java.io.File;
import java.util.HashMap;

import org.openquake.config.ConfigFileOperations;
import org.openquake.config.ConfigParameter;
import org.openquake.config.ParameterDeserializer;
import org.openquake.ui.InputFileGeneratorController;

public class ConfigFileGeneratorMain {

	public static void main(String[] args) throws Exception {
		String configFilePath = args[0];
		String targetDir = args[1];
		
		HashMap<ConfigParameter, String> parameters = ConfigFileOperations.read(new File(configFilePath));
		HashMap<String, SourceModel> sourceModels = ParameterDeserializer.getAlgorithmMap(parameters.get(ConfigParameter.ALGORITHMS));
		
		parameters.put(ConfigParameter.ROOT_PATH, targetDir);
		
		InputFileGeneratorController.generateInputFiles(parameters, sourceModels);
		
		System.out.println("Completed...");
	}
	
}
