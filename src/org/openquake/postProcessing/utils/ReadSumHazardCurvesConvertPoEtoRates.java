package org.openquake.postProcessing.utils;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.opensha.commons.data.Site;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.Location;

public class ReadSumHazardCurvesConvertPoEtoRates {

	public static void readAndSumHazardCurves(String path,
			Map<String, ArbitrarilyDiscretizedFunc> hazCurves, double weight) {
		System.err.println("reading file: " + path);
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
			while (iHazCurve.hasNext()) {
				Element elem = (Element) iHazCurve.next();
				if (elem.getName().equalsIgnoreCase("IML")) {
					String iml = elem.getText();
					imlVals = iml.split(" ");
				} else if (elem.getName().equalsIgnoreCase("HCNode")) {
					ArbitrarilyDiscretizedFunc hazCurve = new ArbitrarilyDiscretizedFunc();
					Element site = elem.element("site");
					String location = site.element("Point").element("pos")
							.getText();
					coords = location.split(" ");
					Site siteLoc = new Site(new Location(
							Double.valueOf(coords[1]),
							Double.valueOf(coords[0])));
					String siteKey = siteLoc.getLocation().getLatitude() + "_"
							+ siteLoc.getLocation().getLongitude();
					Element hazardCurve = elem.element("hazardCurve");
					String poe = hazardCurve.element("poE").getText();
					poeVals = poe.split(" ");

					for (int index = 0; index < imlVals.length; index++) {
						hazCurve.set(Double.valueOf(imlVals[index]), weight * Double.valueOf(poeVals[index]));
					}

					if (!hazCurves.containsKey(siteKey)) {
						hazCurves.put(siteKey, hazCurve);
					} else {
						ArbitrarilyDiscretizedFunc oldHazCurve = hazCurves
								.get(siteKey);
						
						for (int iv = 0; iv < oldHazCurve.getNum(); iv++) {
							
							double rateOld = (-Math.log(1 - oldHazCurve.getY(iv)) / 50);
							double rateNew = (-Math.log(1 - hazCurve.getY(iv))/  50);
							double rate = rateOld + rateNew;
							
							double newVal = 1 - Math.exp(- rate * 50);
							//double newVal = oldHazCurve.getY(iv)
							//		+ hazCurve.getY(iv);
							oldHazCurve.set(iv, newVal);
						}
						hazCurves.put(siteKey, oldHazCurve);
					}
				}
			}
		}
	}
}
