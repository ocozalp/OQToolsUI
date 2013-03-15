package org.openquake.postProcessing.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class GetGMPEsDir {
	public static String[] getGMPEs(String gmpeDir)
	{
		List<String> list = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(gmpeDir, "_"); 

		String gmpe = "";
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();

			if (gmpe.equals("")) gmpe = token;
			else gmpe = gmpe + "_" + token;

			if (token.equals("AttenRel"))
			{
				list.add(gmpe);
				gmpe = "";
			}
		}
		String[] result = new String[list.size()]; 
		list.toArray(result);
		return result;
	}
}
