package org.openquake.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import org.openquake.config.ConfigParameter;
import org.openquake.config.ParameterDeserializer;
import org.openquake.generator.BaseConfig;
import org.openquake.generator.ConfigFileGenerator;
import org.openquake.generator.GemConfig;
import org.openquake.generator.SourceModel;

public class InputFileGeneratorController {

	public static void generateInputFiles(HashMap<ConfigParameter, String> userParameters, HashMap<String, SourceModel> list) throws Exception {
		File mainDir = new File(userParameters.get(ConfigParameter.ROOT_PATH));
		File sourceModelDir = new File(mainDir.getParent(), "SourceModels");
		
		BaseConfig baseConfig = new BaseConfig();
		baseConfig.setSourceModelPath(sourceModelDir.getAbsolutePath());
		baseConfig.setSourceModels(list);
		
		Vector<Double> periods = ParameterDeserializer.getPeriodList(userParameters.get(ConfigParameter.PERIOD));
		baseConfig.setPeriods(periods);
		
		GemConfig customPrm = new GemConfig();
		customPrm.setProperty("general", ConfigParameter.REGION_VERTEX, userParameters.get(ConfigParameter.REGION_VERTEX));
		customPrm.setProperty("general", ConfigParameter.REGION_GRID_SPACING, userParameters.get(ConfigParameter.REGION_GRID_SPACING));
		customPrm.setProperty("HAZARD", ConfigParameter.POES, userParameters.get(ConfigParameter.POES));
		customPrm.setProperty("HAZARD", ConfigParameter.INVESTIGATION_TIME, userParameters.get(ConfigParameter.INVESTIGATION_TIME));
		
		ConfigFileGenerator gen = new ConfigFileGenerator(baseConfig, customPrm);
		gen.setMainDirectory(mainDir.getAbsolutePath());
		gen.setDirectoryName(userParameters.get(ConfigParameter.DIRECTORY_NAME));
		
		gen.generate();
	}
}
