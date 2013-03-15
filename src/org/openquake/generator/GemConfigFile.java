package org.openquake.generator;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.openquake.config.ConfigParameter;

public class GemConfigFile {

	public static void write(BaseConfig baseConfig, GemConfig userProperties, File destination) throws ConfigFileGeneratorException {
		try {
			setDefaultProperties(userProperties);
			
			write(userProperties, destination);
		} catch (Throwable t) {
			throw new ConfigFileGeneratorException("Can not form GEM config", t);
		}
	}

	private static void setDefaultProperties(GemConfig templateConfig) {
		templateConfig.setPropertyIfNull("general", ConfigParameter.CALCULATION_MODE, "Classical");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.WORKAROUND_1027041, "true");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.MINIMUM_MAGNITUDE, "5.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.MAXIMUM_DISTANCE, "200.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.INVESTIGATION_TIME, "50.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.WIDTH_OF_MFD_BIN, "0.20");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.COMPONENT, "Average Horizontal (GMRotI50)");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.QUANTILE_LEVELS, "");
		
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.DAMPING, "5.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.INTENSITY_MEASURE_LEVELS, "0.005, 0.007, 0.0098, 0.0137, 0.0192, 0.0269, 0.0376, 0.0527, 0.0738, 0.103, 0.145, 0.203, 0.284, 0.397, 0.556, 0.778, 1.09, 1.52, 2.13, 2.85, 3.45, 4.09, 4.85, 5.2");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.GMPE_TRUNCATION_TYPE, "2 Sided");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.TRUNCATION_LEVEL, "3");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.STANDARD_DEVIATION_TYPE, "Total");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.REFERENCE_VS30_VALUE, "760.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.REFERENCE_DEPTH_TO_2PT5KM_PER_SEC_PARAM, "5.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.DEPTHTO1PT0KMPERSEC, "100.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.VS30_TYPE, "measured");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.SADIGH_SITE_TYPE, "Rock");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.INCLUDE_AREA_SOURCES, "true");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.AREA_SOURCE_DISCRETIZATION, "0.1");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.AREA_SOURCE_MAGNITUDE_SCALING_RELATIONSHIP, "W&C 1994 Mag-Length Rel.");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.INCLUDE_GRID_SOURCES, "true");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.TREAT_GRID_SOURCE_AS, "Point Sources");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.GRID_SOURCE_MAGNITUDE_SCALING_RELATIONSHIP, "W&C 1994 Mag-Length Rel.");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.INCLUDE_FAULT_SOURCE, "true");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.FAULT_RUPTURE_OFFSET, "5.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.FAULT_SURFACE_DISCRETIZATION, "5.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.FAULT_MAGNITUDE_SCALING_RELATIONSHIP, "PEER Tests Mag-Area Rel.");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.FAULT_MAGNITUDE_SCALING_SIGMA, "0.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.RUPTURE_ASPECT_RATIO, "2.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.RUPTURE_FLOATING_TYPE, "Along strike and down dip");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.INCLUDE_SUBDUCTION_FAULT_SOURCE, "true");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.SUBDUCTION_FAULT_RUPTURE_OFFSET, "10.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.SUBDUCTION_FAULT_SURFACE_DISCRETIZATION, "10.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.SUBDUCTION_FAULT_MAGNITUDE_SCALING_RELATIONSHIP, "W&C 1994 Mag-Length Rel.");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.SUBDUCTION_FAULT_MAGNITUDE_SCALING_SIGMA, "0.0");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.SUBDUCTION_RUPTURE_ASPECT_RATIO, "1.5");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.SUBDUCTION_RUPTURE_FLOATING_TYPE, "Along strike and down dip");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.NUMBER_OF_LOGIC_TREE_SAMPLES, "1");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.SOURCE_MODEL_LT_RANDOM_SEED, "23");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.GMPE_LT_RANDOM_SEED, "5");
		templateConfig.setPropertyIfNull("HAZARD", ConfigParameter.COMPUTE_MEAN_HAZARD_CURVE, "true");
	}
	
	/*
	@SuppressWarnings("resource")
	private static GemConfig loadProperties(String gemFilePath) throws ConfigFileGeneratorException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(gemFilePath)));
			String currentSectionName = null;
			GemConfig result = new GemConfig();
			
			String line;
			
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(line.length() > 1 && line.charAt(0) != '#') {
					if(line.charAt(0) == '[') {
						if(line.charAt(line.length() - 1) != ']') {
							throw new ConfigFileGeneratorException("Gem file format is invalid");
						}
						
						currentSectionName = line.substring(1, line.length() - 1);
					} else if(currentSectionName != null) {
						int separatorIndex = line.indexOf('=');
						
						if(separatorIndex != -1 && separatorIndex < line.length() - 1) {
							String key = line.substring(0, separatorIndex).trim();
							String value = line.substring(separatorIndex+1).trim();
							result.setProperty(currentSectionName, key, value);
						}
					} else {
						throw new ConfigFileGeneratorException("Gem file format is invalid");
					}
					
				}
			}
			
			return result;
		} catch (Throwable t) {
			throw new ConfigFileGeneratorException("Can not parse base config file", t);
		} finally {
			closeStream(reader);
		}
	}
	*/
	private static void write(GemConfig config, File destination) throws ConfigFileGeneratorException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destination)));
			writer.write(config.toString());
			writer.flush();
		} catch (Throwable t) {
			throw new ConfigFileGeneratorException("Can not write base config file", t);
		} finally {
			closeStream(writer);
		}
	}
	
	private static void closeStream(Closeable closeable) {
		try {
			closeable.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
