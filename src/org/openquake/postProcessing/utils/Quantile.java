package org.openquake.postProcessing.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;



public class Quantile {
	/**
	 * get Quantile method 
	 */
		public static Map<String, ArbitrarilyDiscretizedFunc> getQuantile
		    (List<String> curveFiles, double quantile, Map<String, Double> weightsMap) {
			Map<String, ArbitrarilyDiscretizedFunc> qhazCurve = new HashMap<String, ArbitrarilyDiscretizedFunc>();
			
			// start readinf the directories (CompBoxes)
			//File comp = new File(compdir);
			// get list of subfolder (which are the gmpes_names_combination)
			//File[] gmpes = comp.listFiles();
			// instantiate the hazard curves list and weights list  
			List<Map<String, ArbitrarilyDiscretizedFunc>> hazCurvesList = new ArrayList<Map<String, ArbitrarilyDiscretizedFunc>>();
			List<Double> weights = new ArrayList<Double>();
			
			// loop over all gmpes
			for (String file : curveFiles)
			{
				// get gmpes folders
				//File gmpe = gmpes[j];
				//if (!gmpe.isDirectory()) continue;
				//File xml = gmpe.listFiles()[0];
			
				// get the hazard curves for each subfolder,
				// assume the weight is equal to 1.00
				Map<String, ArbitrarilyDiscretizedFunc> hazCurves = new HashMap<String, ArbitrarilyDiscretizedFunc>();
				ReadSumHazardCurves.readAndSumHazardCurves(file, hazCurves, 1);
				hazCurvesList.add(hazCurves);
				
			}
			
			for(String gmpe : weightsMap.keySet()) {
				weights.add(weightsMap.get(gmpe));
			}
			
			// for each site get the associated hazard curve
			Set<String> sites = hazCurvesList.get(0).keySet();
			// instantiate the ground motion levels of the hazard curve (IML)
			ArbitrarilyDiscretizedFunc tmp = hazCurvesList.get(0).get(sites.iterator().next());
			double[] imls = tmp.getXVals();

			// loop every site
			for (String site : sites)
			{
				qhazCurve.put(site, new ArbitrarilyDiscretizedFunc());
				// loop over all IMLs
				for (double iml : imls)
				{
					// instantiate the probWeights 
					List<Pair<Double, Double>> probWeights = new ArrayList<Pair<Double, Double>>();

	                 // loop over all hazardCurves list (in a CompBox)
					for (int i = 0; i < hazCurvesList.size(); i++)
					{
						// get the poes (Y-axis) of the hazardCurve
						double prob = hazCurvesList.get(i).get(site).getY(iml); 
						// add the poes and the weight for each IML
						probWeights.add(new Pair<Double, Double>(prob, weights.get(i)));
					}
	                    // sort the probWeights for each IML
					Collections.sort(probWeights);
					
					double q = quantile;
	                 //Find the corresponding poe
					// search if the weights of the Y-axis are equal to 1.00
					// the algorithm searches for the clossed value on the x-axis (poes), starting from the
					// origin, and reporting the values on the x-axis corresponding to the closes level of the Y-axis.  
					double sumweight = 0;
					for (int i = 0; i  < probWeights.size(); i++)
					{
						sumweight += probWeights.get(i).getSecond();
						System.err.println("sumweight:" + sumweight);

					}
					if (Math.abs(sumweight - 1) > 0.00001) 
					{
						System.out.println("sumweight:" + sumweight);
						System.exit(0);
					}

					for (int i = 0; i < probWeights.size(); i++)
					{
						double prob = probWeights.get(i).getFirst();
						double weight = probWeights.get(i).getSecond();;
						if (q < weight || Math.abs(q - weight) < 0.00001 ) 
						{
							double prev = 0; 
							if (i != 0) prev = probWeights.get(i - 1).getFirst();
								
							double probq = prev + (prob - prev) * q / weight;
							
							qhazCurve.get(site).set(iml, probq);
							
							
							break;
						}
						else 
						{
							if (i == probWeights.size() - 1) System.out.println("error");
							q = q - weight;

						}
					}
					
				}
			}
			return qhazCurve;
		}
}
