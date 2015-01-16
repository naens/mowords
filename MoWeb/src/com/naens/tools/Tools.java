package com.naens.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Tools {


	// convert InputStream to String
	public static String getStringFromInputStream(InputStream is, String encoding) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is, encoding));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

	public static void moveObject (Numbered [] ns, int startIndex, int endIndex) {
		Numbered n = ns[startIndex];
		int unit = (endIndex - startIndex) < 0 ? -1 : 1;
		for (int i = endIndex; i != startIndex; i -= unit) {
			Numbered pt = ns[i];
			pt.setNumber(i - unit);
		}
		n.setNumber(endIndex);
	}
}
