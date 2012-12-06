package com.naens.mobilewords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.naens.dao.WordFileDAO;
import com.naens.dao.WordFolderDAO;
import com.naens.model.WordFile;
import com.naens.model.WordFolder;
import com.naens.model.WordPair;

public class FolderActivity extends Activity {

	public static final String WORDS_INVERSE = "com.naens.mobilewords.words_inverse";

	public static final String WORDS_LIMIT = "com.naens.mobilewords.words_limit";

	public static final String WORDS_ONE_DIRECTION = "com.naens.mobilewords.words_one_direction";

	public static final String WORDS_WORD_PAIRS = "com.naens.mobilewords.words_word_pairs";

	private List <CheckBox> boxes;

	private Map <String, WordFile> files;

	private boolean oneDirConf;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		boxes = new LinkedList <CheckBox> ();
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_folder);
		Intent intent = getIntent ();
		String folderName = intent.getStringExtra (MainActivity.FOLDER_NAME);
		WordFileDAO wordFileDAO = MainActivity.getWordFileDAO ();
		WordFolderDAO folderDAO = MainActivity.getWordFolderDAO ();
		WordFolder wordFolder = folderDAO.getFolderByName (folderName);
		oneDirConf = MainActivity.getConfigurationDAO ().isOneDirection (wordFolder);
		List <WordFile> fileList = wordFileDAO.getWordFiles (wordFolder);
		files = new HashMap <String, WordFile> ();

		for (WordFile file : fileList) {
			this.files.put (file.getName (), file);
			CheckBox checkBox = new CheckBox (this);
			boxes.add (checkBox);
			//			checkBox.setText (file.getName ()  + " (" + file.getSize () + ")");
			checkBox.setText (file.getName () + " (" + wordFileDAO.getFileSize (file) + ")");
			checkBox.setTag (file.getName ());
			((LinearLayout) findViewById (R.id.folder_checkbox_layout)).addView (checkBox);
		}
		Spinner spinner = (Spinner) findViewById (R.id.folder_limit_spinner);
		//		SpinnerAdapter adapter = spinner.getAdapter ();
		//		spinner.setSelection(adapter.getPosition(25));
		spinner.setSelection (4);

		if (oneDirConf) {
			((CheckBox) findViewById (R.id.folder_one_direction_checkbox)).setVisibility (CheckBox.GONE);
			((CheckBox) findViewById (R.id.folder_inverse_checkbox)).setVisibility (CheckBox.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater ().inflate (R.menu.activity_folder, menu);
		return true;
	}

	public void buttonStart (View view) {
		Log.i("TAG", "start:0");
		ArrayList <WordPair> chosenWords = new ArrayList <WordPair> (boxes.size ());
		for (CheckBox checkBox : boxes) {
			if (checkBox.isChecked ()) {
				chosenWords.addAll (files.get (checkBox.getTag ()).getWordPairs ());
			}
		}
		Log.i("TAG", "start:1");

		if (chosenWords.size () > 0) {
			Intent intent = new Intent (FolderActivity.this, WordsActivity.class);
			boolean inverse = ((CheckBox) findViewById (R.id.folder_inverse_checkbox)).isChecked ();
			boolean oneDirection = ((CheckBox) findViewById (R.id.folder_one_direction_checkbox)).isChecked ();
			int limit = Integer.parseInt (((Spinner) findViewById (R.id.folder_limit_spinner)).getSelectedItem ().toString ());
			Log.i("TAG", "start:1 4");
			intent.putExtra (WORDS_INVERSE, inverse);
			Log.i("TAG", "start:1 5");
			intent.putExtra (WORDS_ONE_DIRECTION, oneDirConf || oneDirection);
			Log.i("TAG", "start:1 6");
			intent.putExtra (WORDS_LIMIT, limit);
			Log.i("TAG", "start:1 7");
//			intent.putExtra (WORDS_WORD_PAIRS, chosenWords);
			MainActivity.chosenWords = chosenWords;
			Log.i("TAG", "start:1 8");
			startActivity (intent);
			Log.i("TAG", "start:1 9");
		}
		Log.i("TAG", "start:2");
	}

}
