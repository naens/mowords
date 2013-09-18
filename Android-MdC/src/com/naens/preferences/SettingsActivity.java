package com.naens.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.naens.android_mdc.R;

public class SettingsActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
