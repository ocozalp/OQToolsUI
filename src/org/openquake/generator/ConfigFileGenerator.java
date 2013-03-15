package org.openquake.generator;

import java.io.File;
import java.util.Vector;

import org.openquake.config.ConfigParameter;

public class ConfigFileGenerator {

	private String mainDirectory;
	private String directoryName;
	private BaseConfig baseConfig;
	private GemConfig userProperties;
	private Vector<File> gemConfigPaths;
	
	public ConfigFileGenerator(BaseConfig baseConfig, GemConfig userProperties) {
		this.baseConfig = baseConfig;
		this.userProperties = userProperties;
		this.gemConfigPaths = new Vector<File>();
	}
	
	public void generate() throws ConfigFileGeneratorException {
		File rootDir = generateRootDirectory();
		
		for(String sourceModelName : baseConfig.getSourceModels().keySet()) {
			SourceModel sourceModel = baseConfig.getSourceModels().get(sourceModelName);
			File sourceModelDir = generateSourceModelDirectory(rootDir, sourceModel.getName());
			
			for(String areaName : sourceModel.getAreas().keySet()) {
				File areaDir = generateAreaDirectory(sourceModelDir, areaName);
				Area area = sourceModel.getAreas().get(areaName);
				
				for(String atten : area.getAttenuations().keySet()) {
					File gmpeDir = generateAttenuationDirectory(areaDir, atten);
					
					createSourceModelLogicTree(gmpeDir, areaName);
					
					Vector<Double> periods = baseConfig.getPeriods();
					for(int i = 0; i < periods.size(); ++i) {
						File logicTreeFile = generateGmpeLogicTreeFile(gmpeDir, sourceModel, areaName, atten);
						
						generateGemConfigFile(gmpeDir, area, area.getAttenuations().get(atten), logicTreeFile, periods.get(i));
					}
				}
			}
		}
	}
	
	private File generateGmpeLogicTreeFile(File gmpeDir, SourceModel sourceModel, String compBox, String gmpe) throws ConfigFileGeneratorException{
		try {
			File gmpeLogicTreeFile = new File(gmpeDir, "gmpe_" + gmpe + ".xml");
			gmpeLogicTreeFile.createNewFile();
			
			//TODO : get multiple parameters!
			String[] gmpeList = new String[]{ sourceModel.getType() };
			
			GmpeLogicTreeFile.write(gmpeLogicTreeFile, baseConfig, gmpeList, gmpe);
			
			return gmpeLogicTreeFile;
		} catch (Throwable t) {
			throw new ConfigFileGeneratorException("Can not generate GMPE logic tree file for GMPE : " + gmpe, t);
		}
	}

	private void generateGemConfigFile(File gmpeDir, Area area, Attenuation atten, File logicTreeFile, Double period) throws ConfigFileGeneratorException {
		String fileName = "config_" + atten.getName() + "[SA" + period.toString().replace(".", "") + "]"+ ".gem";
		try {
			File gemConfigFile = new File(gmpeDir, fileName);

			gemConfigFile.createNewFile();
			
			gemConfigPaths.add(gemConfigFile);

			userProperties.setProperty("HAZARD", ConfigParameter.INTENSITY_MEASURE_TYPE, period == 0 ? "PGA" : "SA");
			userProperties.setProperty("HAZARD", ConfigParameter.PERIOD, period.toString());
			
			userProperties.setProperty("HAZARD", ConfigParameter.SOURCE_MODEL_LOGIC_TREE_FILE, "sourceModel_logic_tree.xml");
			userProperties.setProperty("HAZARD", ConfigParameter.GMPE_LOGIC_TREE_FILE, logicTreeFile.getName());
			userProperties.setProperty("HAZARD", ConfigParameter.OUTPUT_DIR, "SA["+ period.toString().replace(".", "") +"]");
			userProperties.setProperty("HAZARD", ConfigParameter.TREAT_AREA_SOURCE_AS, area.getType());
			
			if(atten.getName().startsWith("AkC_2010") 
					|| atten.getName().startsWith("CF_2008")
					|| atten.getName().startsWith("AB_2003")
					|| atten.getName().startsWith("LL_2008")
					|| atten.getName().startsWith("YoungsEtAl_1997")) {
				userProperties.setProperty("HAZARD", ConfigParameter.COMPONENT, "Average Horizontal");
			}
			
			GemConfigFile.write(baseConfig, userProperties, gemConfigFile);
		} catch (Throwable t) {
			throw new ConfigFileGeneratorException("Can not generate GEM config file : " + fileName, t);
		}
	}

	private void createSourceModelLogicTree(File gmpeDir, String compBox) throws ConfigFileGeneratorException {
		try {
			File fSourceModelLTF = new File(gmpeDir, "sourceModel_logic_tree.xml");
			fSourceModelLTF.createNewFile();
			SourceModelLogicTree.write(baseConfig, fSourceModelLTF, compBox);
		} catch (Throwable t) {
			throw new ConfigFileGeneratorException("Can not generate source model logic tree for gmpe : " + gmpeDir.getName() + " and compoBox" + compBox, t);
		}
		
	}

	private File generateDirectoryWithRootDir(File rootDir, String directoryName, String exceptionText) throws ConfigFileGeneratorException {
		try {
			File dir = new File(rootDir, directoryName);
			dir.mkdirs();
			return dir;
		} catch (Throwable t) {
			throw new ConfigFileGeneratorException(exceptionText, t);
		}
		
	}
	
	private File generateAttenuationDirectory(File compBoxDir, String gmpe) throws ConfigFileGeneratorException {
		return generateDirectoryWithRootDir(compBoxDir, gmpe, "Can not generate gmpe directory for : " + gmpe);
	}

	private File generateAreaDirectory(File sourceModelDir, String area) throws ConfigFileGeneratorException {
		return generateDirectoryWithRootDir(sourceModelDir, area, "Can not generate area directory for : " + area);
	}

	private File generateSourceModelDirectory(File rootDir, String sourceModel) throws ConfigFileGeneratorException {
		return generateDirectoryWithRootDir(rootDir, sourceModel, "Can not generate source model directory for : " + sourceModel);
	}

	private File generateRootDirectory() throws ConfigFileGeneratorException {
		String rootPath = mainDirectory + File.separatorChar + directoryName;
		try {
			File rootDir = new File(rootPath);
			rootDir.mkdirs();
			
			return rootDir;
		} catch(Throwable t) {
			throw new ConfigFileGeneratorException("Can not generate root directory", t);
		}
	}

	public String getMainDirectory() {
		return mainDirectory;
	}

	public void setMainDirectory(String mainDirectory) {
		this.mainDirectory = mainDirectory;
	}

	public String getDirectoryName() {
		return directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}
	
	public Vector<File> getGemConfigPaths() {
		return gemConfigPaths;
	}

}
