package com.naens.preferences;

import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.InputType;

import com.naens.mowords.R;
import com.naens.mowords.WordsActivity;

public class WordSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private Integer sides;
	private String folderName;
	private int side;
	private SharedPreferences sp;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.wordpreferences);
		PreferenceScreen rootScreen = getPreferenceScreen();

        folderName = getIntent().getStringExtra(WordsActivity.WORD_FOLDER);
        side = getIntent().getIntExtra(WordsActivity.WORD_SIDE, 1);

        sp = rootScreen.getSharedPreferences();
    	ListPreference typefacePref = new ListPreference(this);
    	typefacePref.setKey(SettingsActivity.getFontNameKey(folderName, side));
    	typefacePref.setPersistent(true);
    	typefacePref.setEntries(SettingsActivity.getTypeFaceStrings (this));
    	typefacePref.setEntryValues(SettingsActivity.getTypeFaceStrings (this));
    	String typefaceDef = getResources().getString(R.string.pref_side_typeface_default);
    	String typefaceValue = sp.getString(SettingsActivity.getFontNameKey(folderName, side), typefaceDef);
    	typefacePref.setDefaultValue(typefaceDef);
    	typefacePref.setSummary(typefaceValue);
    	typefacePref.setTitle(R.string.pref_side_typeface);
    	rootScreen.addPreference(typefacePref);

    	EditTextPreference fontSizePref = new EditTextPreference(this);
    	fontSizePref.setKey(SettingsActivity.getFontSizeKey(folderName, side));
    	fontSizePref.setPersistent(true);
    	fontSizePref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
    	String fontSizeDef = getResources().getString(R.string.pref_side_font_size_default);
    	String fontSizeValue = sp.getString(SettingsActivity.getFontSizeKey(folderName, side), fontSizeDef);
    	fontSizePref.setDefaultValue(fontSizeDef);
    	fontSizePref.setSummary(fontSizeValue);
    	fontSizePref.setTitle(R.string.pref_side_font_size);
    	rootScreen.addPreference(fontSizePref);
    	sides = Integer.parseInt(sp.getString(SettingsActivity.getSidesKey (folderName), "-1"));
	}

	@SuppressWarnings("deprecation")
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	@SuppressWarnings("unchecked")
		Map <String, Object> prefs = (Map<String, Object>) sharedPreferences.getAll();
    	Preference connectionPref = findPreference(key);
    	if (connectionPref != null) {
    		if (sides == null || side > sides) {
    			SharedPreferences.Editor editor = sp.edit();
    			editor.putString(SettingsActivity.getSidesKey(folderName), Integer.toString(side)); 
    			editor.commit();
    		}
	    	Object value = prefs.get(key);
	    	if (value instanceof String) {
	            connectionPref.setSummary((String) value);
	    	}
	    	if (value instanceof Integer) {
	            connectionPref.setSummary(value.toString());
	    	}
	    }
    }

    @SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
