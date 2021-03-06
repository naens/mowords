package com.naens.mowords;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.naens.dao.ConfigurationDAO;
import com.naens.dao.WordFolderDAO;
import com.naens.dao.androiddao.ConfigurationAndroidPrefDAO;
import com.naens.dao.androiddao.WordFolderAndroidDAO;
import com.naens.model.WordFolder;
import com.naens.model.WordPair;
import com.naens.preferences.MainPreferenceActivity;
import com.naens.preferences.SettingsValues;
import com.naens.ui.FlowLayout;

public class MainActivity extends Activity {

	public static final String FOLDER_NAME = "com.naens.mowords.FOLDER_NAME";
	private WordFolderDAO wordFolderDAO;
	private ConfigurationAndroidPrefDAO configurationDAO;
	public static List <WordPair> chosenWords;
	private static Context context;
//	private String theme;

	@Override
	protected void onCreate (Bundle savedInstanceState) {

		super.onCreate (savedInstanceState);

//		deleteDatabase(GameLogDbHelper.DATABASE_NAME);
		setContentView (R.layout.activity_main);
		File sdcard = Environment.getExternalStorageDirectory ();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String filesFolder = sharedPref.getString(SettingsValues.KEY_PREF_ROOT_DIRECTORY, null);

		configurationDAO = new ConfigurationAndroidPrefDAO (this);
//		sharedPref.registerOnSharedPreferenceChangeListener(this);
		String rootFolder = configurationDAO.getRootFolder ();
		File rootDirectory = new File(sdcard.getAbsolutePath () + "/" + rootFolder);
		if (filesFolder == null || !rootDirectory.exists()) {
//			Intent intent = new Intent (MainActivity.this, WelcomeActivity.class);
//			startActivity (intent);
////			finish ();
		}
//		rootFolder = null;
//    	loadTheme();
		context = this;
		if (rootFolder == null || !rootDirectory.exists()) {
//    	    Intent intent = new Intent(this, WelcomeSettingsActivity.class);
//    	    startActivity(intent);
//			@SuppressWarnings("unused")
//			DialogChooseDirectory dcd = new DialogChooseDirectory("DCD", this, new DialogChooseDirectory.Result() {
//				@Override
//				public void onChooseDirectory(String dir) {
//					Toast.makeText(MainActivity.this, dir, Toast.LENGTH_SHORT).show();
//					
//				}
//			}, sdcard.getAbsolutePath(), R.id.main_view);
    	    return;
		}
		wordFolderDAO = getWordFolderDAO ();
		displayFolders();
	}

	private void displayFolders() {
//    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//    	theme = sharedPref.getString ("pref_theme", "Dark");
//    	theme = "Light";
//    	if (theme.equals("Light")) {
//    		setTheme(android.R.style.Theme_Light);
//    	}
//    	if (theme.equals("Dark")) {
//    		setTheme(android.R.style.Theme);
//    	}
		ViewGroup foldersLayout = ((FlowLayout) findViewById (R.id.folders_layout));
		foldersLayout.setKeepScreenOn(false);
		foldersLayout.removeAllViews();

		String rootFolder = getConfigurationDAO ().getRootFolder ();
		File sdcard = Environment.getExternalStorageDirectory ();
		wordFolderDAO = new WordFolderAndroidDAO (sdcard.getAbsolutePath () + "/" + rootFolder, context);
		List <WordFolder> folders = wordFolderDAO.getFolders ();
		for (WordFolder folder : folders) {
			String folderName = folder.getName ();

			Button btn = new Button (this);
			btn.setText (folderName);
//		    	if (theme.equals("Light")) {
			btn.setBackgroundResource(R.drawable.folder_button);
			btn.setTextAppearance(this, R.style.FolderButton);
//		    	}
//		    	if (theme.equals("Dark")) {
//					btn.setBackgroundResource(R.drawable.folder_button_dark);
//					btn.setTextAppearance(this, R.style.FolderButton_dark);
//		    	}
			btn.setOnClickListener (new TextClickListener (folderName));
			FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(12, 8);
			foldersLayout.addView (btn, params);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		ViewGroup layout = ((RelativeLayout) findViewById (R.id.main_view));
		layout.requestFocus();
		Log.i("TAG", "main:resume");
		File sdcard = Environment.getExternalStorageDirectory ();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String filesFolder = sharedPref.getString(SettingsValues.KEY_PREF_ROOT_DIRECTORY, null);
//		sharedPref.registerOnSharedPreferenceChangeListener(this);
		String rootFolder = configurationDAO.getRootFolder ();
		File rootDirectory = new File(sdcard.getAbsolutePath () + "/" + rootFolder);
		if (filesFolder == null || !rootDirectory.exists()) {
			Intent intent = new Intent (MainActivity.this, WelcomeActivity.class);
			startActivityForResult(intent, 0);
//			finish ();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i("TAG", "main:start");
	}

//	private void loadTheme() {
////		RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_view);
////    	TextView tv = (TextView) findViewById(R.id.main_folders_text);
////    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mainLayout.getContext());
////    	theme = sharedPref.getString("pref_theme", "Dark");
////    	if (theme.equals("Light")) {
////    		setTheme(android.R.style.Theme_Holo_Light);
////    		mainLayout.setBackgroundResource(android.R.color.background_light);
////    		tv.setTextColor(getResources().getColor(android.R.color.primary_text_light));
////    	}
////    	if (theme.equals("Dark")) {
////    		setTheme(android.R.style.Theme_Holo);
////    		mainLayout.setBackgroundResource(android.R.color.background_dark);
////    		tv.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
////    	}
//	}

	private class TextClickListener implements OnClickListener {

		private String name;

		public TextClickListener (String name) {
			this.name = name;
		}

		@Override
		public void onClick (View v) {
			Intent intent = new Intent (MainActivity.this, FolderActivity.class);
			intent.putExtra (FOLDER_NAME, name);
			startActivity (intent);
		}

	}

	public WordFolderDAO getWordFolderDAO () {
		String rootFolder = getConfigurationDAO ().getRootFolder ();
		File sdcard = Environment.getExternalStorageDirectory ();
		return wordFolderDAO == null ? new WordFolderAndroidDAO (sdcard.getAbsolutePath () + "/" + rootFolder, context) : wordFolderDAO;
	}

	public ConfigurationDAO getConfigurationDAO () {
		return configurationDAO == null ? new ConfigurationAndroidPrefDAO (context) : configurationDAO;
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater ().inflate (R.menu.activity_main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_settings:
	    	    Intent intent = new Intent(MainActivity.this, MainPreferenceActivity.class);
	        	startActivityForResult(intent, 0);
	            return true;
//	        case R.id.help:
//	            showHelp();
//	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		displayFolders();
	}
}
