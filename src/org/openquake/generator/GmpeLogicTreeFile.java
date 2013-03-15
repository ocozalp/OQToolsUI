package org.openquake.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class GmpeLogicTreeFile {

	public static void write(File destinationFile, BaseConfig baseConfig, String[] gmpeList, String gmpe) throws ConfigFileGeneratorException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			sb.append("\n");
			sb.append("<nrml xmlns:gml=\"http://www.opengis.net/gml\"\n");
			sb.append("\t\txmlns=\"http://openquake.org/xmlns/nrml/0.3\"\n");
			sb.append("\t\tgml:id=\"n1\">\n");
			sb.append("\t<logicTree logicTreeID=\"lt1\">\n");
			
			for (int i = 0; i < gmpeList.length; ++i) {
				sb.append("\t\t<logicTreeBranchingLevel branchingLevelID=\"bl" + (i+1) + "\">\n");
				sb.append("\t\t\t<logicTreeBranchSet uncertaintyType=\"gmpeModel\" applyToTectonicRegionType=\""+ gmpeList[i] + "\"  branchSetID=\"bs" + (i+1) + "\">\n");
				sb.append("\t\t\t\t<logicTreeBranch branchID=\"b" + (i+1) + "\">\n");
				sb.append("\t\t\t\t\t<uncertaintyModel>"+ gmpe +"</uncertaintyModel>\n");
				sb.append("\t\t\t\t\t<uncertaintyWeight>1.0</uncertaintyWeight>\n");
				sb.append("\t\t\t\t</logicTreeBranch>\n");
				sb.append("\t\t\t</logicTreeBranchSet>\n");
				sb.append("\t\t</logicTreeBranchingLevel>\n");
			}
			sb.append("\t</logicTree>\n");
			sb.append("</nrml>\n");
			
			FileWriter fstream = new FileWriter(destinationFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(sb.toString());
			out.flush();
			out.close();
		} catch (Throwable t) {
			throw new ConfigFileGeneratorException("Error while creating logic tree file", t);
		}
	}
}
