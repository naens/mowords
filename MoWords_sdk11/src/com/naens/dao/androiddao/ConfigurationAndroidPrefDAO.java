package com.naens.dao.androiddao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.naens.dao.ConfigurationDAO;
import com.naens.mowords.MainActivity;
import com.naens.mowords.R;
import com.naens.preferences.SettingsActivity;

public class ConfigurationAndroidPrefDAO implements ConfigurationDAO {

	private Context context;
	private SharedPreferences sharedPref;

	public ConfigurationAndroidPrefDAO (Context context) {
		this.context = context;
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	public boolean isOneDirection (String wordFolder) {
		return sharedPref.getBoolean(SettingsActivity.getOneDirKey(wordFolder), 
				Boolean.parseBoolean(context.getResources().getString(R.string.pref_folder_one_direction_default)));
	}

	@Override
	public String [] getLimitList(String wordFolder) {
		return sharedPref.getString (SettingsActivity.getLimitsKey(wordFolder), 
				context.getResources().getString(R.string.pref_folder_limits_default)).split("[, ]+");
	}

	@Override
	public String getDefaultLimit(String wordFolder) {
		return sharedPref.getString (SettingsActivity.getDefLimitKey(wordFolder), "0");
	}

	@Override
	public boolean getVisible(String wordFolder) {
		return sharedPref.getBoolean(SettingsActivity.getVisibleKey(wordFolder),
				Boolean.parseBoolean(context.getResources().getString(R.string.pref_folder_visible_default)));
	}

	@Override
	public String getRootFolder() {
		return sharedPref.getString(SettingsActivity.KEY_PREF_ROOT_DIRECTORY, null);
	}

	@Override
	public String getSideFontSize(String wordFolder, int side) {
		return sharedPref.getString(SettingsActivity.getFontSizeKey (wordFolder, side),
				context.getResources().getString(R.string.pref_side_font_size_default));
	}

	@Override
	public String getSideFontName(String wordFolder, int side) {
		return sharedPref.getString(SettingsActivity.getFontNameKey (wordFolder, side),
				context.getResources().getString(R.string.pref_side_typeface_default));
	}

	@Override
	public String getEncoding (String wordFolder, int side) {
		return sharedPref.getString(SettingsActivity.getEncodingKey(wordFolder, side), 
				context.getResources().getString(R.string.pref_side_encoding_default));
	}

	@Override
	public boolean isMdCSide(String wordFolder, int side) {
		String encoding = getEncoding(wordFolder, side);
		return MainActivity.getContext().getResources().getStringArray(R.array.pref_side_encoding_values)[1].equals(encoding);
	}

	@Override
	public boolean isMdCTranslitSide(String wordFolder, int side) {
		String encoding = getEncoding(wordFolder, side);
		return MainActivity.getContext().getResources().getStringArray(R.array.pref_side_encoding_values)[2].equals(encoding);
	}

}
