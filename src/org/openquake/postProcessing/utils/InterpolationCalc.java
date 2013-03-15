package org.openquake.postProcessing.utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;


public class InterpolationCalc {

	public static void calculate(String path, double [] poes, boolean isLogarithmic) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + "_result")));
		StringBuffer sb = new StringBuffer();
		ArbitrarilyDiscretizedFunc func = new ArbitrarilyDiscretizedFunc();
		String line;
		while((line = br.readLine()) != null) {
			line = line.replace(',', '.');
			
			StringTokenizer st = new StringTokenizer(line, "\t ");
			if(st.countTokens() >= 2) {
				double x = Double.parseDouble(st.nextToken());
				double y = Double.parseDouble(st.nextToken());
				
				func.set(x, y);
			}
		}
		
		br.close();
		
		for(double p : poes) {
			double poe = 1 / p;
			sb.append(p).append(" degeri icin x : \t");
			
			if(isLogarithmic)
				sb.append(String.format("%.4f", func.getFirstInterpolatedX_inLogXLogYDomain(poe)));
			else
				sb.append(String.format("%.4f", func.getFirstInterpolatedX(poe)));
				
			sb.append("\n");
		}
		
		bw.write(sb.toString());
		bw.close();
	}
	
}
