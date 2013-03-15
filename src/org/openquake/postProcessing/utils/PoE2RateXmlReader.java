package org.openquake.postProcessing.utils;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.opensha.commons.data.Site;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.Location;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author ocozalp
 *
 */
public class PoE2RateXmlReader extends DefaultHandler {

	private Map<String, ArbitrarilyDiscretizedFunc> hazardCurves;
	private double weight;
	private String lastLocalName;
	
	private String[] imlVals;
	private String poe;
	private String position;
	
	private boolean insideTag;
	
	public PoE2RateXmlReader(Map<String, ArbitrarilyDiscretizedFunc> hazardCurves, double weight) {
		this.hazardCurves = hazardCurves;
		this.weight = weight;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		lastLocalName = qName;
		insideTag = true;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(lastLocalName != null && insideTag) {
			if(lastLocalName.equals("IML")) {
				String iml = new String(ch, start, length);
				imlVals = iml.split(" ");
			} else if(lastLocalName.equals("poE")) {
				poe = new String(ch, start, length);
			} else if(lastLocalName.equals("gml:pos")) {
				position = new String(ch, start, length);
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException { //TODO: more refactoring
		if(qName.equals("HCNode")) {
			ArbitrarilyDiscretizedFunc hazCurve = new ArbitrarilyDiscretizedFunc();
			String [] coords = position.split(" ");
			Site siteLoc = new Site(new Location(Double.valueOf(coords[1]), Double.valueOf(coords[0])));
			
			String siteKey = siteLoc.getLocation().getLatitude() + "_" + siteLoc.getLocation().getLongitude();
			
			String [] poeVals = poe.split(" ");
	
			for (int index = 0; index < imlVals.length; index++) {
				hazCurve.set(Double.valueOf(imlVals[index]), weight * Double.valueOf(poeVals[index]));
			}
	
			if (!hazardCurves.containsKey(siteKey)) {
				hazardCurves.put(siteKey, hazCurve);
			} else {
				ArbitrarilyDiscretizedFunc oldHazCurve = hazardCurves.get(siteKey);
				
				for (int iv = 0; iv < oldHazCurve.getNum(); iv++) {
					double rateOld = (-Math.log(1 - oldHazCurve.getY(iv)) / 50);
					double rateNew = (-Math.log(1 - hazCurve.getY(iv))/  50);
					double rate = rateOld + rateNew;
					
					double newVal = 1 - Math.exp(- rate * 50);
					oldHazCurve.set(iv, newVal);
				}
				
				hazardCurves.put(siteKey, oldHazCurve);
			}
		}
		
		insideTag = false;
	}
	
	public static void readAndSumHazardCurves(String path, Map<String, ArbitrarilyDiscretizedFunc> hazardCurves, double weight) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		
		PoE2RateXmlReader handler = new PoE2RateXmlReader(hazardCurves, weight);
		
		System.out.println("Started parsing file : " + path);
		long t = System.currentTimeMillis();
		saxParser.parse((new File(path)), handler);
		System.out.println("Finished parsing file at " + (System.currentTimeMillis() - t) + " msecs...");
	}
	
}
