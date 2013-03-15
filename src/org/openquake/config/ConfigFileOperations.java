package org.openquake.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;

public class ConfigFileOperations {

	public static void save(File targetFile, HashMap<ConfigParameter, String> parameters) throws Exception {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile)));
		Iterator<ConfigParameter> iter = parameters.keySet().iterator();
		
		while(iter.hasNext()) {
			ConfigParameter key = iter.next();
			writer.write(key.name() + "=" + parameters.get(key));
			writer.newLine();
		}
		
		writer.flush();
		writer.close();
	}
	
	public static HashMap<ConfigParameter, String> read(File sourceFile) throws Exception {
		HashMap<ConfigParameter, String> result = new HashMap<ConfigParameter, String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
		
		String line;
		while((line = reader.readLine()) != null) {
			int index = line.indexOf("=");
			if(index != -1) {
				String key = line.substring(0, index).trim();
				String value = line.substring(index + 1);
				result.put(ConfigParameter.valueOf(key), value);
			}
		}
		reader.close();
		return result;
	}
	
}
