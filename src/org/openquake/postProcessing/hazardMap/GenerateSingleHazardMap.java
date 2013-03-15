package org.openquake.postProcessing.hazardMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.openquake.postProcessing.hazardCurve.XMLReaderHazardCurves;
import org.opensha.commons.data.Site;

 public class GenerateSingleHazardMap {
	
	private static String PATH = "C:\\Users\\ASUS\\Desktop\\CY_24";

	public static void main(String[] args) throws IOException{
		genSingleHazMap(PATH, new double[]{0.1, 0.02});
	}

	private static void genSingleHazMap(String path, double[] poes) throws IOException {
		File model = new File(path);

		String hazMapDir = new File(model.getParentFile(), "RP").getAbsolutePath();
		(new File(hazMapDir)).mkdirs();
		
		double [] returnPeriods = { 72, 475, 2475, 4975 };
		for(File xml : model.listFiles()) {
			
			String fileName = xml.getName();
			
			if(!fileName.startsWith("hazardcurve")) continue;
			
			HashMap<Site, Vector<Double>> cache  = new HashMap<Site, Vector<Double>>();
			
			for(double rp : returnPeriods) {
				double poe = 1.0 / rp;
				
				Map<Site, Double> map = XMLReaderHazardCurves.computeHazardMap(xml.getAbsolutePath(), poe);
				//Map<Site, Double> map = XMLReaderHazardCurves.readHazardMap(xml.getAbsolutePath());

				String poeString = "" + poe;
				poeString = poeString.replace(".", "");
				// create this directory first!!
				
				// write the Return Period folder
				// Make ReturnPEriods directories
				String meanHazMapDir = hazMapDir + "/RP=" + rp + "yrs"+ "/";
				(new File(meanHazMapDir)).mkdir();

				String HazMap =  meanHazMapDir + "/" + xml.getName().replace(".xml", "") + "_" + "RP" + rp + "yrs" + ".dat";

				File targetFile = new File(HazMap);
				FileWriter output = new FileWriter(targetFile);

				for(Site site : map.keySet())
				{
					output.write(site.getLocation().getLongitude()+" ");
					output.write(site.getLocation().getLatitude()+" ");
					output.write(map.get(site)+"\n");
					
					Vector<Double> vals = cache.get(site);
					if(vals == null) {
						vals = new Vector<Double>();
						cache.put(site, vals);
					}
					
					vals.add(map.get(site));
				}
				output.close();	
				
				System.out.println(xml.getAbsolutePath());
			}
			
			File f = new File(hazMapDir, "t_" + fileName);
			FileWriter totalW = new FileWriter(f);
			
			for(Site site : cache.keySet()) {
				totalW.write(site.getLocation().getLongitude() + "\t");
				totalW.write(site.getLocation().getLatitude() + "\t");
				Vector<Double> list = cache.get(site);
				for (Double val : list) {
					totalW.write(val + "\t");
				}
				totalW.write("\n");
			}
			
			totalW.close();
			
		}
	}
}
