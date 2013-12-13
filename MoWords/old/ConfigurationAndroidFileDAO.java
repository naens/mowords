package com.naens.dao.androiddao;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.naens.dao.ConfigurationDAO;
import com.naens.dao.FontInfo;
import com.naens.mowords.R;

public class ConfigurationAndroidFileDAO implements ConfigurationDAO {

	private Map <String, Properties> propertiesMap = new HashMap <String, Properties> ();

	private Map <String, SparseArray <FontInfo>> styles = new HashMap <String, SparseArray<FontInfo>> ();

	private Map <String, List <Integer>> mdcs = new HashMap<String, List<Integer>>();

	private Context context;

	public ConfigurationAndroidFileDAO(Context context) {
		this.context = context;
	}


	private Properties getProperties (String wordFolder) {
		if (propertiesMap.containsKey (wordFolder)) {
			return propertiesMap.get (wordFolder);
		} else {
			Properties properties = new Properties ();
			try {
				properties.load (new FileInputStream (String.format ("%s/%s.properties", WordFolderAndroidDAO.getRoot (),
						wordFolder)));
			} catch (IOException e) {
			}
			propertiesMap.put (wordFolder, properties);
			return properties;
		}

	}

	private SparseArray <FontInfo> getStyles (String wordFolder) {
		Properties properties = getProperties (wordFolder);

		SparseArray <FontInfo> fonts = new SparseArray <FontInfo> ();
		SparseIntArray sizes = new SparseIntArray ();
		SparseArray <String> fontNames = new SparseArray <String> ();
		Enumeration <Object> rbkeys = properties.keys ();
		while (rbkeys.hasMoreElements ()) {
			String key = rbkeys.nextElement ().toString ();
			if (key.matches ("^side\\d.*")) {
				int side = Integer.parseInt (key.split ("^side")[1].split ("[^\\d]+")[0]);
				if (key.matches ("^side\\d+.font$")) {
					fontNames.put (side - 1, properties.getProperty (key));
				}
				if (key.matches ("^side\\d+.size$")) {
					sizes.put (side - 1, Integer.parseInt (properties.getProperty (key)));
				}
			}
		}

		for (int i = 0; i < sizes.size (); ++i) {
			int side = sizes.keyAt (i);
			fonts.put (side, new FontInfo (fontNames.get (side), sizes.get (side)));
		}
		return fonts;
	}

	@Override
	public boolean isOneDirection (String wordFolder) {
		Properties properties = getProperties (wordFolder);
		String oneDirection = properties.getProperty ("oneDiretion");
		return oneDirection != null && oneDirection.toLowerCase ().trim ().equals ("true");
	}

	@Override
	public String [] getLimitList(String wordFolder) {
		Properties properties = getProperties (wordFolder);
		String ls = properties.getProperty ("limits");
		if (ls == null) {
			return context.getResources().getString(R.string.pref_folder_limits_default).split("[, ]+");
		}
		String [] limits = ls.split("[, ]+");
		String [] res = new String [limits.length];
		for (int i = 0; i < limits.length; i++) {
			String string = limits[i];
			if (string.contains("[")) {
				limits[i] = string.substring(1, string.length() - 1);
			}
			res [i] = limits [i];
			
		}
		
		return res;
	}

	@Override
	public String getDefaultLimit(String wordFolder) {
		Properties properties = getProperties (wordFolder);
		String ls = properties.getProperty ("limits");
		if (ls == null) {
			return context.getResources().getString(R.string.pref_folder_limits_default).split("[, ]+")[0];
		}
		String[] limits = ls.split("[, ]+");
		for (int i = 0; i < limits.length; i++) {
			String string = limits[i];
			if (string.contains("[")) {
				return string.substring(1, string.length() - 1);
			}
		}
		return context.getResources().getString(R.string.pref_folder_limits_default).split("[, ]+")[0];
	}

	public List <Integer> getMdCSides(String wordFolder) {
		Properties properties = getProperties (wordFolder);
		String sidesString = properties.getProperty ("mdcSides");
		List <Integer> result = new LinkedList<Integer>();
		if (sidesString == null) {
			return result;
		}
		String [] sides = sidesString.split("[, ]+");
		for (int i = 0; i < sides.length; i++) {
			result.add(Integer.parseInt(sides[i]) - 1);
		}
		return result;
	}

	@Override
	public boolean getVisible(String folder) {
		return true;
	}

	@Override
	public String getRootFolder() {
		return "wordfiles";
	}

	@Override
	public String getSideFontSize(String wordFolder, int side) {
		SparseArray <FontInfo> fontStyles = styles.get(wordFolder);
		if (styles == null) {
			fontStyles = getStyles(wordFolder);
			styles.put(wordFolder, fontStyles);
		}
		FontInfo info = fontStyles.get(side - 1);
		if (info == null) {
			return context.getResources().getString(R.string.pref_side_font_size_default);
		}
		return Integer.toString(info.getFontSize());
	}

	@Override
	public String getSideFontName(String wordFolder, int side) {
		SparseArray <FontInfo> fontStyles = styles.get(wordFolder);
		if (fontStyles == null) {
			fontStyles = getStyles(wordFolder);
			styles.put(wordFolder, fontStyles);
		}
		FontInfo info = fontStyles.get(side - 1);
		if (info == null) {
			return context.getResources().getString(R.string.pref_side_typeface_default);
		}
		return info.getFontName();
	}

	@Override
	public String getEncoding(String wordFolder, int side) {
		List <Integer> fmdcs = mdcs.get(wordFolder);
		if (fmdcs == null) {
			fmdcs = getMdCSides(wordFolder);
			mdcs.put(wordFolder, fmdcs);
		}
		return fmdcs.contains(side - 1) ?
				context.getResources().getStringArray(R.array.pref_side_encoding_values)[1] :
					context.getResources().getStringArray(R.array.pref_side_encoding_values)[0];
	}

	@Override
	public boolean isMdCSide(String wordFolder, int side) {
		String encoding = getEncoding(wordFolder, side);
		return context.getResources().getStringArray(R.array.pref_side_encoding_values)[1].equals(encoding);
	}

}
