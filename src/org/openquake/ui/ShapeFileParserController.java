package org.openquake.ui;

import org.openquake.parsers.AsModelShapeFile2NRMLParserVer04;
import org.openquake.parsers.Interface2NRML;
import org.openquake.parsers.ShapeFile2NRMLconverter;

public class ShapeFileParserController {

	public static void parseAsc(String sourceFile, String targetFile, double minimumMagnitude) {
		AsModelShapeFile2NRMLParserVer04.parse(sourceFile, targetFile, minimumMagnitude);
	}
	
	public static void parseInterface(String sourceFile, String targetFile, double minimumMagnitude) {
		Interface2NRML.parse(sourceFile, targetFile, minimumMagnitude);
	}
	
	public static void parseInslab(String sourceFile, String targetFile, double minimumMagnitude) {
		ShapeFile2NRMLconverter.parse(sourceFile, targetFile, minimumMagnitude);
	}
}
