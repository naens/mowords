package com.naens.preferences;

import java.util.Arrays;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.naens.mowords.R;

public class FolderPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	public static final String INTENT_FOLDER = "FOLDER_FRAGMENT_FOLDER_NAME";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Activity context = getActivity();
		if (context == null) {
			throw new RuntimeException("FolderPreferenceFragment: context null");
		}

		Intent intent = context.getIntent();
		String folderName = intent.getStringExtra(INTENT_FOLDER);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

    	PreferenceScreen folderScreen = getPreferenceManager().createPreferenceScreen(context);
    	folderScreen.setKey(SettingsValues.FOLDER_PREFIX + folderName);
    	folderScreen.setPersistent(false);
    	folderScreen.setTitle(folderName);

    	CheckBoxPreference oneDirPref = new CheckBoxPreference(context);
    	oneDirPref.setKey(SettingsValues.getOneDirKey(folderName));
    	oneDirPref.setPersistent(true);
    	oneDirPref.setDefaultValue(Boolean.parseBoolean(getResources().getString(R.string.pref_folder_one_direction_default)));
    	oneDirPref.setTitle(R.string.pref_folder_one_direction_title);
    	folderScreen.addPreference(oneDirPref);

    	MultiChoiceDialogPreference limitsPref = new MultiChoiceDialogPreference(context);
    	limitsPref.setKey(SettingsValues.getLimitsKey (folderName));
    	limitsPref.setPersistent(true);
    	String limitsDef = getResources().getString(R.string.pref_folder_limits_default);
       	StringBuffer limDefVal = new StringBuffer();	//at least one value!
       	String [] limitsDefs = limitsDef.split ("[, ]+");
       	for (int i = 0; i < limitsDefs.length; ++ i) {
       		String lim = limitsDefs [i];
  			limDefVal.append(lim);
   			if (i < limitsDefs.length - 1) {
   				limDefVal.append(", ");
   			}
       	}
       	String[] allLimits = getResources().getString(R.string.pref_folder_limits_values).split("[, ]+");
		limitsPref.setEntryValues(allLimits);
       	limitsPref.setDefaultValue(limDefVal.toString());
       	String limitsValue = sp.getString(SettingsValues.getLimitsKey (folderName), limDefVal.toString());
       	limitsPref.setSummary(limitsValue);
    	limitsPref.setTitle(R.string.pref_folder_limits_title);
    	folderScreen.addPreference(limitsPref);

    	ListPreference defLimitPref = new ListPreference(context);
    	defLimitPref.setKey(SettingsValues.getDefLimitKey (folderName));
    	defLimitPref.setPersistent(true);
    	String [] lims = (String[]) limitsValue.split ("[, ]+");
    	defLimitPref.setEntries(lims);
    	defLimitPref.setEntryValues(lims);
    	String defLimitDef = lims.length == 0 ? "---" : lims [0];
    	defLimitPref.setDefaultValue(defLimitDef);
    	//value in lims!
    	String defLimitValue = sp.getString(SettingsValues.getDefLimitKey (folderName), defLimitDef);
    	if (!Arrays.asList(lims).contains(defLimitValue)) {
    		defLimitValue = defLimitDef;
    		defLimitPref.setValue(defLimitDef);
    	}
    	defLimitPref.setSummary(defLimitValue);

    	defLimitPref.setTitle(R.string.pref_folder_default_limit_title);
    	folderScreen.addPreference(defLimitPref);

		setPreferenceScreen(folderScreen);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		@SuppressWarnings("unchecked")
		Map <String, Object> prefs = (Map<String, Object>) sharedPreferences.getAll();
    	Preference connectionPref = findPreference(key);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
    	if (connectionPref != null) {
	    	Object value = prefs.get(key);
	    	if (value instanceof String) {
	    		String sValue = (String) value;
	            connectionPref.setSummary(sValue);
	            Log.i("TAG", String.format("preference changed '%s' -> '%s'", key, sValue));
	            if (key.matches("^pref_folder_.+_limits$")) {
	            	String folderKey = key.replaceFirst("_limits$", "");
	            	String defKey = folderKey + "_def_limit";
	            	ListPreference defPref = (ListPreference) findPreference(defKey);
	            	if (sp.contains(defKey)) {
	            		String defValue = sp.getString(defKey, "-=-=-");
	                	String [] lims = (String[]) sValue.split ("[, ]+");
	            		defPref.setEntries(lims);
	            		defPref.setEntryValues(lims);
	    	            if (!Arrays.asList(lims).contains(defValue)) {
	    	            	String newDefLimit = lims.length == 0 ? "---" : lims [0];
	    	            	defPref.setValue(newDefLimit);
	    	            }
	            	}
	            }
	    	}
	    	if (value instanceof Integer) {
	            connectionPref.setSummary(value.toString());
	    	}
	    }
    }

	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
	    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	    super.onPause();
	}

}
