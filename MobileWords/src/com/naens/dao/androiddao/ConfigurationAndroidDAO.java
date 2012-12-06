package com.naens.dao.androiddao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.graphics.Typeface;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.naens.dao.ConfigurationDAO;
import com.naens.model.WordFolder;
import com.naens.model.WordFont;

public class ConfigurationAndroidDAO implements ConfigurationDAO {

	private Map <String, Properties> propertiesMap = new HashMap <String, Properties> ();

	private Map <String, Typeface> externalFonts;

	private Properties getProperties (WordFolder wordFolder) {
		if (propertiesMap.containsKey (wordFolder.getName ())) {
			return propertiesMap.get (wordFolder.getName ());
		} else {
			Properties properties = new Properties ();
			try {
				properties.load (new FileInputStream (String.format ("%s/%s.properties", WordFolderAndroidDAO.getRoot (),
						wordFolder.getName ())));
			} catch (IOException e) {
			}
			propertiesMap.put (wordFolder.getName (), properties);
			return properties;
		}

	}

	@Override
	public SparseArray <WordFont> getStyles (WordFolder wordFolder) {
		Properties properties = getProperties (wordFolder);

		SparseArray <WordFont> fonts = new SparseArray <WordFont> ();
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
			fonts.put (side, new WordFont (fontNames.get (side), sizes.get (side)));
		}
		return fonts;
	}

	@Override
	public boolean isOneDirection (WordFolder wordFolder) {
		Properties properties = getProperties (wordFolder);
		String oneDirection = properties.getProperty ("oneDiretion");
		return oneDirection != null && oneDirection.toLowerCase ().trim ().equals ("true");
	}

	@Override
	public Map <String, Typeface> getFonts () {
		if (externalFonts != null) {
			return externalFonts;
		}
		File fontDir = new File (WordFolderAndroidDAO.getRoot () + "/fonts");
		externalFonts = new HashMap <String, Typeface> ();
		for (File fontFile : fontDir.listFiles ()) {
			if (fontFile.exists () && fontFile.isFile ()) {
				Typeface t = Typeface.createFromFile (fontFile);
				externalFonts.put (fontFile.getName (), t);
			}
		}
		return externalFonts;
	}

}
