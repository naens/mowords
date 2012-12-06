package com.naens.mobilewords;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naens.dao.ConfigurationDAO;
import com.naens.dao.WordFileDAO;
import com.naens.dao.WordFolderDAO;
import com.naens.dao.androiddao.ConfigurationAndroidDAO;
import com.naens.dao.androiddao.WordFileAndroidDAO;
import com.naens.dao.androiddao.WordFolderAndroidDAO;
import com.naens.model.WordFolder;
import com.naens.model.WordPair;

public class MainActivity extends Activity {

	public static final String FOLDER_NAME = "com.naens.mobilewords.FOLDER_NAME";
	private static Activity activity;
	private static WordFileDAO wordFileDAO;
	private static WordFolderDAO wordFolderDAO;
	private static ConfigurationDAO configurationDAO;
	public static List <WordPair> chosenWords;

	@Override
	protected void onCreate (Bundle savedInstanceState) {

		File sdcard = Environment.getExternalStorageDirectory ();
		activity = this;
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);

		wordFileDAO = new WordFileAndroidDAO ();
		wordFolderDAO = new WordFolderAndroidDAO (sdcard.getAbsolutePath ());
		configurationDAO = new ConfigurationAndroidDAO ();

		for (WordFolder folder : wordFolderDAO.getFolders ()) {
			TextView textView = new TextView (this);
			textView.setText (folder.getName ());
			textView.setClickable (true);
			textView.setTextSize (32);
			textView.setPadding (8, 8, 8, 8);
			textView.setOnClickListener (new TextClickListener (folder.getName ()));
			((LinearLayout) findViewById (R.id.main_layout)).addView (textView);
		}
	}

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

	public static void message (String s) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder (activity);
		alertbox.setMessage (s);
		alertbox.show ();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.activity_main, menu);
		return true;
	}

	public static WordFileDAO getWordFileDAO () {
		return wordFileDAO;
	}

	public static WordFolderDAO getWordFolderDAO () {
		return wordFolderDAO;
	}

	public static ConfigurationDAO getConfigurationDAO () {
		return configurationDAO;
	}

}
