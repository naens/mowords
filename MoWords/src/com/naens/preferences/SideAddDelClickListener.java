package com.naens.preferences;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.naens.preferences.AddDelPreference.AddDelListener;

public class SideAddDelClickListener implements AddDelListener {

	private SharedPreferences sp;
	private PreferenceScreen parentScreen;
	private PreferenceActivity pa;
	private int sides;
	private String folderName;;

	public SideAddDelClickListener(PreferenceActivity pa, SharedPreferences sp, String folderName,
			PreferenceScreen parentGroup) {
		this.pa = pa;
		this.sp = sp;
		this.parentScreen = parentGroup;
		this.folderName = folderName;
		String s = sp.getString("pref_folder_" + folderName + "_sides", "2");
		sides = Integer.parseInt(s);
	}

	@Override
	public void onAddClick() {
		if (sides < 4) {
			++sides;
			SettingsActivity.fillSidePreference(pa, parentScreen, sides - 1, folderName);

			SharedPreferences.Editor editor = sp.edit();
			editor.putString(SettingsActivity.getSidesKey(folderName), Integer.toString(sides)); 
			editor.commit();
		}
	}

	@Override
	public void onDelClick() {
		if (sides > 1) {
			--sides;
			Preference side = (Preference) parentScreen.getPreference(sides);
			if (side != null) {
				parentScreen.removePreference(side);
			}

			SharedPreferences.Editor editor = sp.edit();
			editor.putString(SettingsActivity.getSidesKey(folderName), Integer.toString(sides)); 
			editor.commit();
		}
	}

}
