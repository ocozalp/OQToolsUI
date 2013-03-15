package org.openquake.postProcessing.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.opensha.commons.data.Site;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.Location;

/**
 * 
 * @author ocozalp
 *
 */
public class PrintToAscii {
	
	public static void printHazardCurvesToAscii(ArrayList<String> paths, String fileName) throws IOException {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(fileName));
			
			int indexFile = 0;
			for (String path : paths) {
				File xml = new File(path);
				SAXReader reader = new SAXReader();
				Document doc = null;
				try {
					doc = reader.read(xml);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				Element root = doc.getRootElement();
				Iterator i = root.elements().iterator();
				while (i.hasNext()) {
					Element hazardResult = (Element) i.next();
					String elemName = hazardResult.getName();
					Element hazardCurveField = hazardResult.element("hazardCurveField");
					Iterator iHazCurve = hazardCurveField.elements().iterator();
					String[] imlVals = null;
					String[] coords = null;
					String[] poeVals = null;
					ArbitrarilyDiscretizedFunc hazCurve = null;
					while (iHazCurve.hasNext()) {
						Element elem = (Element) iHazCurve.next();
						if (elem.getName().equalsIgnoreCase("IML")) {
							String iml = elem.getText();
							imlVals = iml.split(" ");
							if (indexFile == 0) {
								for (String val : imlVals)
									bw.write(val + " ");
								bw.write("\n");
							}
						} else if (elem.getName().equalsIgnoreCase("HCNode")) {
							hazCurve = new ArbitrarilyDiscretizedFunc();
							Element site = elem.element("site");
							String location = site.element("Point").element("pos")
									.getText();
							coords = location.split(" ");
							bw.write(coords[0] + " " + coords[1] + " ");
							Site siteLoc = new Site(new Location(
									Double.valueOf(coords[1]),
									Double.valueOf(coords[0])));
							Element hazardCurve = elem.element("hazardCurve");
							String poe = hazardCurve.element("poE").getText();
							poeVals = poe.split(" ");
							for (int index = 0; index < imlVals.length; index++) {
								hazCurve.set(Double.valueOf(imlVals[index]),
										Double.valueOf(poeVals[index]));
								bw.write(poeVals[index] + " ");
							}
							bw.write("\n");
						}
					}
				}
				indexFile = indexFile + 1;
			}
		} finally {
			bw.close();
		}
	}
}
