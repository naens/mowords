package com.naens.preferences;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.naens.mowords.FlowLayout;
import com.naens.mowords.R;
import com.naens.tools.FontProvider;

@SuppressWarnings("deprecation")
public class WelcomeSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static SharedPreferences sp;
	private static String[] typeFaceStrings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.welcomepreference);
		sp = getPreferenceScreen().getSharedPreferences();

		FindFolderDialogPreference rdi = (FindFolderDialogPreference) findPreference(SettingsActivity.KEY_PREF_ROOT_DIRECTORY);
		rdi.setRootFolder(Environment.getExternalStorageDirectory().getAbsolutePath());
//		rdi.showDialog(null);
		// finish ();

		ListPreference themePreference = (ListPreference) findPreference("pref_theme");
		String theme = sp.getString("pref_theme", "Dark");
		if (!sp.contains("pref_theme")) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("pref_theme", "Dark");
			editor.commit();
		}
		themePreference.setSummary(theme);


		Button btn = new Button (this);
		btn.setText ("OK");
    	if (theme.equals("Light")) {
			btn.setBackgroundResource(R.drawable.folder_button);
			btn.setTextAppearance(this, R.style.FolderButton);
    	}
    	if (theme.equals("Dark")) {
			btn.setBackgroundResource(R.drawable.folder_button_dark);
			btn.setTextAppearance(this, R.style.FolderButton_dark);
    	}
		btn.setOnClickListener (new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String folder = sp.getString(SettingsActivity.KEY_PREF_ROOT_DIRECTORY, null);
				if (folder != null) {
					startActivity(getIntent());
					// IF FOLDER OK
					//TODO copy structure and files to new chosen folder
				}
//				WelcomeSettingsActivity.this.finish ();
				WelcomeSettingsActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						WelcomeSettingsActivity.this.finish ();
//						startActivity(getIntent());
					}
				});
			}
		});
//		FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(12, 8);
//
//		getListView().addView(btn, params);
		BtnPreference bp = new BtnPreference(this, btn);
		getPreferenceScreen().addPreference (bp);

	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		@SuppressWarnings("unchecked")
		Map<String, Object> prefs = (Map<String, Object>) sharedPreferences.getAll();
		Preference connectionPref = findPreference(key);
		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
		if (connectionPref != null) {
			Object value = prefs.get(key);
			if (value instanceof Boolean) {
			}
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
						String[] lims = (String[]) sValue.split("[, ]+");
						defPref.setEntries(lims);
						defPref.setEntryValues(lims);
						if (!Arrays.asList(lims).contains(defValue)) {
							String newDefLimit = lims.length == 0 ? "---" : lims[0];
							defPref.setValue(newDefLimit);
						}
					}
				}
				if (value.equals("Light")) {
					finish();
					startActivity(getIntent());
				}
				if (value.equals("Dark")) {
					finish();
					startActivity(getIntent());
				}
			}
			if (value instanceof Integer) {
				connectionPref.setSummary(value.toString());
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		super.onPreferenceTreeClick(preferenceScreen, preference);
		if (preference != null) {
			if (preference instanceof PreferenceScreen) {
				PreferenceScreen pScreen = (PreferenceScreen) preference;
				if (pScreen.getDialog() != null) {
					Drawable bkg = getWindow().getDecorView().getBackground().getConstantState().newDrawable();
					pScreen.getDialog().getWindow().setBackgroundDrawable(bkg);
				}
			}
		}
		return false;
	}

	public static CharSequence[] getTypeFaceStrings(Context context) {
		if (typeFaceStrings == null) {
			Set<String> fonts = FontProvider.getFontSet();
			fonts.add(FontProvider.MDC_HIEROGLIPH_FONT);
			fonts.add(FontProvider.MDC_TRANSLITERATION_FONT);
			typeFaceStrings = new String[fonts.size() + 1];
			int z = 1;
			typeFaceStrings[0] = context.getResources().getString(R.string.pref_side_typeface_default);
			for (String s : fonts) {
				typeFaceStrings[z] = s;
				++z;
			}
		}
		return typeFaceStrings;
	}

}
