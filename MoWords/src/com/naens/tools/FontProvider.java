package com.naens.tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.graphics.Typeface;

import com.naens.dao.androiddao.WordFolderAndroidDAO;
import com.naens.mowords.MainActivity;

public class FontProvider {
	public static final String MDC_HIEROGLIPH_FONT = "Gardiner";
	public static final String MDC_TRANSLITERATION_FONT = "MDCTranslitLC";
	private static Map <String, Typeface> map = null;
	private static Typeface defaultFont = Typeface.DEFAULT;
	private static Typeface defaultItalic = Typeface.create (defaultFont, Typeface.ITALIC);

	public static void loadFontsFromFiles () {
		map = new HashMap <String, Typeface> ();
		File fontFolder = new File(WordFolderAndroidDAO.getRoot () + "/fonts/");
		for (File fontFile : fontFolder.listFiles ()) {
			if (fontFile.exists () && fontFile.isFile ()) {
				Typeface t = Typeface.createFromFile (fontFile);
				map.put (fontFile.getName ().split("\\.")[0], t);
			}
		}
	}

	public static Typeface getFont (String name) {
		if (name == null || name.trim().equals("")) {
			return null;
		}
		if (map == null) {
			loadFontsFromFiles ();
		}
		if (map.containsKey (name)) {
			return map.get (name);
		}
		
		Typeface result = null;

		//try assets: ttf
		try {
			result = Typeface.createFromAsset(MainActivity.getContext().getAssets(), "fonts/" + name + ".ttf");
			if (result != null) {
				map.put(name, result);
				return result;
			}
		} catch (RuntimeException e) {
		}

		//try assets: otf
		try {
			result = Typeface.createFromAsset(MainActivity.getContext().getAssets(), "fonts/" + name + ".otf");
			if (result != null) {
				map.put(name, result);
				return result;
			}
		} catch (RuntimeException e) {
		}
	
		result = Typeface.create(name, Typeface.NORMAL);
		if (result != null) {
			map.put(name, result);
			return result;
		}
//		result = defaultFont;
//		map.put(name, result);
		return result;
	}

	public static Typeface getDefaultFont() {
		return defaultFont;
	}

	public static Typeface getDefaultItalic() {
		return defaultItalic;
	}

	public static int getDefaultSize() {
		return 24;
	}

	public static Set <String> getFontSet() {
		if (map == null) {
			loadFontsFromFiles ();
		}
		TreeSet <String> result = new TreeSet<String>(new CaseUnsensitiveComparator());
		result.addAll(map.keySet());
		return result;
	}

}
