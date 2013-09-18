package com.naens.tools.mdctools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.naens.mowords.MainActivity;
import com.naens.mowords.R;

public class MdCToUnicode {

	private Map <String, Integer> map = new HashMap<String, Integer>();

	private static MdCToUnicode unicodeCharsRetriever;

	private MdCToUnicode () {
		try {
			Context context = MainActivity.getContext ();
			InputStream ims = context.getResources().openRawResource(R.raw.unicode_mdc_mapping_v1);
			BufferedReader in = new BufferedReader (new InputStreamReader(ims));
			String line = null;

			while((line = in.readLine()) != null) {
				String [] record = line.split("\\t");
				int codePoint = Integer.parseInt(record [1], 16);
				String [] mdcCodes = record [2].split(" ");
				for (String mdcCode : mdcCodes) {
					map.put (mdcCode, codePoint);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getCode(String mdcChar) throws InvalidMdCCodeException {
		if (unicodeCharsRetriever == null) {
			unicodeCharsRetriever = new MdCToUnicode();
		}
		Integer result = unicodeCharsRetriever.map.get(mdcChar);
		if (result == null) {
			throw new InvalidMdCCodeException (mdcChar);
		}
		return result;
	}

}
