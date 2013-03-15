package org.openquake.postProcessing.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.StringTokenizer;

import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;

public class PrintToXML {
	// method to write the mean/quantile to xml
	public static void printHazardCurvesToXML(Map<String, ArbitrarilyDiscretizedFunc> hazCurves, String fileName) 
			throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
		bw.write("<?xml version='1.0' encoding='UTF-8'?>\n");
		bw.write("<nrml xmlns:gml=\"http://www.opengis.net/gml\" xmlns=\"http://openquake.org/xmlns/nrml/0.3\" gml:id=\"n1\">\n");
		bw.write("  <hazardResult gml:id=\"hr1\">\n");
		bw.write("    <config>\n");
		bw.write("      <hazardProcessing investigationTimeSpan=\"50.0\"/>\n");
		bw.write("    </config>\n");
		bw.write("    <hazardCurveField gml:id=\"hcf_0\" endBranchLabel=\"0\">\n");
		// check the number of IML levels!!!
		bw.write("      <IML IMT=\"PGA\">0.005 0.007 0.0098 0.0137 0.0192 0.0269 0.0376 0.0527 0.0738 0.103 0.145 0.203 0.284 0.397 0.556 0.778 1.09 1.52 2.13 2.85 3.45</IML>\n");

		int hcn = 0;
		for (String key: hazCurves.keySet())
		{
			bw.write("      <HCNode gml:id=\"hcn_" + hcn +"\">\n");
			bw.write("        <site>\n");
			bw.write("          <gml:Point srsName=\"epsg:4326\">\n");

			StringTokenizer tokenizer = new StringTokenizer(key, "_");
			String lat = tokenizer.nextToken();
			String lon = tokenizer.nextToken();

			bw.write("            <gml:pos>" + lon + " " + lat + "</gml:pos>\n");		 
			bw.write("          </gml:Point>\n");
			bw.write("        </site>\n");

			bw.write("        <hazardCurve>\n");
			bw.write("          <poE>");

			ArbitrarilyDiscretizedFunc poe = hazCurves.get(key);

	        //DecimalFormat f = new DecimalFormat("##.00000000");  

			for (int i = 0; i < poe.getNum(); i++)
//			bw.write(f.format(poe.getY(i)) + " ");
				bw.write(poe.getY(i) + " ");
			bw.write("</poE>\n");

			bw.write("        </hazardCurve>\n");
			bw.write("      </HCNode>\n");
			hcn++;
		}

		bw.write("    </hazardCurveField>\n");
		bw.write("  </hazardResult>\n");
		bw.write("</nrml>\n");

		bw.close();
	}

}
