package org.openquake.postProcessing.hazardCurve;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.opensha.commons.data.Site;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.Location;

public class XMLReaderHazardCurves {
	
	public static Map<Site, Double> readHazardMap(String path) {
		Map<Site, Double> map = new HashMap<Site, Double>();
		System.out.println("reading file: " + path);
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
			Element hazardCurveField = hazardResult.element("hazardMap");
			Iterator iHazMapNode = hazardCurveField.elements().iterator();
			String[] coords = null;
			while (iHazMapNode.hasNext()) {
				Element elem = (Element) iHazMapNode.next();
				if (elem.getName().equalsIgnoreCase("HMNode")) {

					Element site = elem.element("HMSite");
					String location = site.element("Point").element("pos")
							.getText();
					coords = location.split(" ");
					Site siteLoc = new Site(new Location(
							Double.valueOf(coords[1]),
							Double.valueOf(coords[0])));

					Element iml = elem.element("IML");
					double val = Double.valueOf(iml.getText());

					map.put(siteLoc, val);
				}
			}
		}
		return map;
	}

	public static Map<Site, Double> computeHazardMap(String path, double probVal) {

		Map<Site, Double> map = new HashMap<Site, Double>();

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
				} else if (elem.getName().equalsIgnoreCase("HCNode")) {
					hazCurve = new ArbitrarilyDiscretizedFunc();
					Element site = elem.element("site");
					
					String location = site.element("Point").element("pos")
							.getText();
					
					coords = location.split(" ");
					Site siteLoc = new Site(new Location(
							Double.valueOf(coords[1]),
							Double.valueOf(coords[0])));
//					System.out.println(siteLoc);

					Element hazardCurve = elem.element("hazardCurve");

					String poe = hazardCurve.element("poE").getText();
					poeVals = poe.split(" ");
//					System.out.println(imlVals.length);
					for (int index = 0; index < imlVals.length; index++) {
						hazCurve.set(Double.valueOf(imlVals[index]),
								Double.valueOf(poeVals[index]));
//						System.out.println("index " + index);
//						System.err.println(Double.valueOf(imlVals[index]));
//						System.err.println(Double.valueOf(poeVals[index]));
					}
					if (probVal > hazCurve.getMaxY()) {
						map.put(siteLoc, hazCurve.getMinX());
						
					} else if (probVal < hazCurve.getMinY()) {
						map.put(siteLoc, hazCurve.getMaxX());
					} else {
						map.put(siteLoc,hazCurve.getFirstInterpolatedX(probVal));
					}

				}
			}
		}
		return map;
	}
}