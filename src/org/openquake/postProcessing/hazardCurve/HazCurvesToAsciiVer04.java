package org.openquake.postProcessing.hazardCurve;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.openquake.postProcessing.utils.PrintToAscii;
import org.openquake.postProcessing.utils.ReadSumHazardCurves;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;


public class HazCurvesToAsciiVer04 {
	
	public static void main(String[] args) throws IOException{
		String rootPath = "/Users/laurentiudanciu/Documents/workspace/OQutils/oq_results/as_rupture_types/ASZ_PointRuptures/asz_point/";
		String exportPath = "/Users/laurentiudanciu/Documents/workspace/OQutils/oq_export/as_rupture_types/";
		dirlist(rootPath, exportPath, true);
	}
	

	private static void dirlist(String path, String exportPath, boolean saveHazCurveAscii) throws IOException{

		File model = new File(path);
		System.err.println(" model    " + model);
		//get sub-directories under root dir
		File[] compboxes = model.listFiles();

		if(compboxes == null){
			System.out.println("Specified directory does not exist or is not a directory.");
			System.exit(0);

		}else{

			//get CompBoxes = TectonicRegions Folders
			for(int i = 0; i < compboxes.length; i++){
				if (!compboxes[i].isDirectory()) continue;
				
				File compbox = compboxes[i];
				System.err.println(" CompBoxTectReg  " + compbox);

				File[] gmpes = compbox.listFiles();
				Map<String, ArbitrarilyDiscretizedFunc> hazCurves = new HashMap<String, ArbitrarilyDiscretizedFunc>();
				for (int j = 0; j < gmpes.length; j++)
				{
					
					// get [GMPEs] folders
					File gmpe = gmpes[j];
					if (!gmpe.isDirectory()) continue;
					System.err.println(" GMPEs    " + gmpe.getName());

					// get GMPEsResults folders
					File[] gmpeResults = gmpe.listFiles(); 
					for (int k = 0; k < gmpeResults.length; k++){
						File gmpeResult = gmpeResults[k];
						if (!gmpeResult.isDirectory()) continue;
						System.err.println(" GMPEsResult    " + gmpeResult);

						// get [intensity measure types] folders
						File[] imts = gmpeResult.listFiles(); 
						for (int m = 0; m < imts.length; m++){
							File imt = imts[m];
							if (!imt.isDirectory()) continue;
							System.out.println(" IMT  " + imt);	
							
							// get hazard curves
							File xml = imt.listFiles()[0];
//							System.out.println("Current XMLfile: " + xml);

							ArrayList<String> paths = new ArrayList<String>();		
							paths.add(xml.toString());

							
							String txtDir = exportPath + "/" + compbox.getName() + "/" + gmpe.getName(); 
							// create new directories
							(new File(txtDir)).mkdirs();
							
							String  txtFile = txtDir + "/" + imt.getName() + ".txt";
							
							if (saveHazCurveAscii)PrintToAscii.printHazardCurvesToAscii(paths, txtFile);

							double w = 1;
							ReadSumHazardCurves.readAndSumHazardCurves(xml.toString(), hazCurves, w);
						}
					}
				}
			}
		}
	}

}
