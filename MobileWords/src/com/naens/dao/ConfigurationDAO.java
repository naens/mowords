package com.naens.dao;

import java.util.Map;

import android.graphics.Typeface;
import android.util.SparseArray;

import com.naens.model.WordFolder;
import com.naens.model.WordFont;

public interface ConfigurationDAO {

	public SparseArray <WordFont> getStyles (WordFolder wordFolder);

	public boolean isOneDirection(WordFolder wordFolder);

	public Map <String, Typeface> getFonts ();

}
