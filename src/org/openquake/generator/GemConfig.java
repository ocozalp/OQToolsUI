package org.openquake.generator;

import java.util.HashMap;
import java.util.Properties;

import org.openquake.config.ConfigParameter;

public class GemConfig {

	private HashMap<String, Properties> propertySections;
	
	public GemConfig() {
		propertySections = new HashMap<String, Properties>();
	}
	
	public String getProperty(String sectionName, String key) {
		if(propertySections.get(sectionName) == null)
			return null;
		
		return propertySections.get(sectionName).getProperty(key);
	}
	
	public void setProperty(String sectionName, ConfigParameter key, String value) {
		Properties section = propertySections.get(sectionName);
		if(section == null) {
			section = new Properties();
			propertySections.put(sectionName, section);
		}
		section.setProperty(key.name(), value);
	}
	
	public void setPropertyIfNull(String sectionName, ConfigParameter key, String value) {
		Properties section = propertySections.get(sectionName);
		if(section == null) {
			section = new Properties();
			propertySections.put(sectionName, section);
		}
		
		if(section.getProperty(key.name()) == null) {
			section.setProperty(key.name(), value);
		}
	}
	
	public void append(GemConfig configFile) {
		if(configFile != null) {
			for(String sectionName : configFile.propertySections.keySet()) {
				Properties prop = configFile.propertySections.get(sectionName);
				for(Object key : prop.keySet()) {
					setProperty(sectionName, ConfigParameter.valueOf(key.toString()), configFile.getProperty(sectionName, key.toString()));
				}
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuffer rawFile = new StringBuffer();
		for(String sectionName : propertySections.keySet()) {
			Properties section = propertySections.get(sectionName);
			rawFile.append("[").append(sectionName).append("]").append("\n");
			
			for(Object key : section.keySet()) {
				rawFile.append(key.toString()).append(" = ").append(section.getProperty(key.toString())).append("\n");
			}
			
			rawFile.append("\n");
		}
		
		return rawFile.toString();
	}

}
