package com.naens.dao.androiddao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.naens.dao.ConfigurationDAO;
import com.naens.mowords.R;
import com.naens.preferences.SettingsValues;

public class ConfigurationAndroidPrefDAO implements ConfigurationDAO {

	private Context context;
	private SharedPreferences sharedPref;

	public ConfigurationAndroidPrefDAO (Context context) {
		this.context = context;
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	public boolean isOneDirection (String wordFolder) {
		return sharedPref.getBoolean(SettingsValues.getOneDirKey(wordFolder), 
				Boolean.parseBoolean(context.getResources().getString(R.string.pref_folder_one_direction_default)));
	}

	@Override
	public String [] getLimitList(String wordFolder) {
		return sharedPref.getString (SettingsValues.getLimitsKey(wordFolder), 
				context.getResources().getString(R.string.pref_folder_limits_default)).split("[, ]+");
	}

	@Override
	public String getDefaultLimit(String wordFolder) {
		return sharedPref.getString (SettingsValues.getDefLimitKey(wordFolder), "0");
	}

	@Override
	public String getRootFolder() {
		return sharedPref.getString(SettingsValues.KEY_PREF_ROOT_DIRECTORY, null);
	}

	@Override
	public String getSideFontSize(String wordFolder, int side) {
		return sharedPref.getString(SettingsValues.getFontSizeKey (wordFolder, side),
				context.getResources().getString(R.string.pref_side_font_size_default));
	}

	@Override
	public String getSideFontName(String wordFolder, int side) {
		return sharedPref.getString(SettingsValues.getFontNameKey (wordFolder, side),
				context.getResources().getString(R.string.pref_side_typeface_default));
	}

	@Override
	public boolean isMdCSide(String folderName, int side) {
		return sharedPref.getBoolean(SettingsValues.getMdCKey (folderName, side),
				context.getResources().getBoolean(R.bool.pref_side_mdc_default));
	}

}
