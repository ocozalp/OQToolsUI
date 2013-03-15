package org.openquake.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class SourceModelLogicTree {

	public static void write(BaseConfig baseConfig, File destination, String compBox) throws ConfigFileGeneratorException {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<nrml xmlns:gml=\"http://www.opengis.net/gml\"\n");
		sb.append("\t\txmlns=\"http://openquake.org/xmlns/nrml/0.3\"\n");
		sb.append("\tgml:id=\"n1\">\n");
		sb.append("\n");
		sb.append("    <logicTree logicTreeID=\"lt1\">\n");
		sb.append("     <logicTreeBranchingLevel branchingLevelID=\"bl1\">\n");
		sb.append("      <logicTreeBranchSet uncertaintyType=\"sourceModel\" branchSetID=\"bs1\">\n");
		sb.append("       <logicTreeBranch branchID=\"b1\">\n");
		sb.append("         <uncertaintyModel>"+ "../../../../../SourceModels/" + compBox + "_SourceModel.xml</uncertaintyModel>\n");
		sb.append("         <uncertaintyWeight>1.00</uncertaintyWeight>\n");
		sb.append("        </logicTreeBranch>\n");
		sb.append("      </logicTreeBranchSet>\n");
		sb.append("     </logicTreeBranchingLevel>\n");
		sb.append("    </logicTree>\n");
		sb.append("</nrml>\n");

		try{
			FileWriter fstream = new FileWriter(destination);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(sb.toString());
			out.flush();
			out.close();
		}catch (Throwable t){
			throw new ConfigFileGeneratorException("Can not generate source model logic tree", t);
		}
	}
}
