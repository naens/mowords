package com.naens.mowords;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.naens.dao.WordFileDAO;
import com.naens.dao.WordFolderDAO;
import com.naens.dao.androiddao.ConfigurationAndroidPrefDAO;
import com.naens.dao.androiddao.WordFileAndroidDAO;
import com.naens.dao.androiddao.WordFolderAndroidDAO;
import com.naens.model.WordFile;
import com.naens.model.WordFolder;
import com.naens.model.WordPair;
import com.naens.preferences.FolderPreferenceActivity;
import com.naens.preferences.FolderPreferenceFragment;
import com.naens.preferences.WordPreferenceActivity;
import com.naens.tools.ToolUtilities;
import com.naens.ui.FlowLayout;

public class FolderActivity extends Activity {

	public static final String WORDS_INVERSE = "com.naens.mowords.words_inverse";

	public static final String WORDS_LIMIT = "com.naens.mowords.words_limit";

	public static final String WORDS_ONE_DIRECTION = "com.naens.mowords.words_one_direction";

	public static final String WORDS_WORD_PAIRS = "com.naens.mowords.words_word_pairs";

	public static final String WORDS_BOXES = "com.naens.mowords.words_boxes";

	public static final String WORDS_SCROLL_POSITION = "com.naens.mowords.words_scroll_position";

	public static final String WORDS_STATE_KB = "com.naens.mowords.words_state_kb";

	private List<ToggleButton> fileToggleButtons;

	private ArrayList<Integer> savedBoxes;

	private Map<String, WordFile> files;

	private boolean oneDirConf;

	private ScrollView scrollView;

	private int scrollPosition;

	private WordFolder wordFolder;

	// private String theme;

	private String folderName;

	private ConfigurationAndroidPrefDAO configurationDAO;
	
	private boolean controlStateKeyboard = false;

	private WordFileDAO wordFileDAO;

	@Override
	protected void onResume() {
		controlStateKeyboard = false;
		LinearLayout layout = ((LinearLayout) findViewById (R.id.folder_layout));
		layout.requestFocus();
		if (savedBoxes != null) {
			for (Integer box : savedBoxes) {
				fileToggleButtons.get(box).setChecked(true);
			}
		}
		scrollView.setKeepScreenOn(false);
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		fileToggleButtons = new LinkedList<ToggleButton>();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder);
//		loadTheme();
		Intent intent = getIntent();
		folderName = intent.getStringExtra(MainActivity.FOLDER_NAME);
		wordFileDAO = new WordFileAndroidDAO(this);
		configurationDAO = new ConfigurationAndroidPrefDAO (this);

//get folder dao
		String rootFolder = configurationDAO.getRootFolder ();
		File sdcard = Environment.getExternalStorageDirectory ();
		WordFolderDAO folderDAO = new WordFolderAndroidDAO (sdcard.getAbsolutePath () + "/" + rootFolder, this);

		wordFolder = folderDAO.getFolderByName(folderName);
		oneDirConf = configurationDAO.isOneDirection(folderName);
		List <WordFile> fileList = wordFileDAO.getWordFiles(wordFolder);

		files = new HashMap<String, WordFile>();

		FlowLayout clt = (FlowLayout)  findViewById(R.id.folder_checkbox_layout);
		for (WordFile file : fileList) {
			this.files.put(file.getName(), file);
			ToggleButton toggle = new ToggleButton(this);
			fileToggleButtons.add(toggle);
			toggle.setText(file.getName() + " (" + wordFileDAO.getFileSize(file) + ")");
			toggle.setTextOn(toggle.getText());
			toggle.setTextOff(toggle.getText());
			toggle.setTag(file.getName());

//			if (theme.equals("Light")) {
				toggle.setBackgroundResource(R.drawable.folder_toggle);
				toggle.setTextAppearance(this, R.style.FolderCheckbox);
//			}
//			if (theme.equals("Dark")) {
//				toggle.setBackgroundResource(R.drawable.folder_checkbox_dark);
//				toggle.setTextAppearance(this, R.style.FolderCheckbox_dark);
//			}

			FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(12, 8);
			clt.addView(toggle, params);
		}

		loadPref();

		scrollView = (ScrollView) findViewById(R.id.folder_scroll_view);
		Button okBtn = (Button) findViewById(R.id.folder_ok_button);
//		if (theme.equals("Light")) {
			okBtn.setBackgroundResource(R.drawable.folder_button);
			okBtn.setTextAppearance(this, R.style.FolderButton);
			scrollView.setBackgroundResource(R.drawable.folderlist_background);
//			((Button) findViewById(R.id.folder_inverse_btn)).setBackgroundResource(R.drawable.btn_inverse);
//			((Button) findViewById(R.id.folder_invsel_btn)).setBackgroundResource(R.drawable.btn_invsel);
//			((Button) findViewById(R.id.folder_one_direction_btn)).setBackgroundResource(R.drawable.btn_onedir);
//			((Button) findViewById(R.id.folder_selectall_btn)).setBackgroundResource(R.drawable.btn_selectall);
//			((TextView) findViewById(R.id.folder_limit_label)).setTextColor(getResources().getColor(android.R.color.secondary_text_light));
//		}
//		if (theme.equals("Dark")) {
//			okBtn.setBackgroundResource(R.drawable.folder_button_dark);
//			okBtn.setTextAppearance(this, R.style.FolderButton_dark);
//			scrollView.setBackgroundResource(R.drawable.folderlist_background_dark);
//			((Button) findViewById(R.id.folder_inverse_btn)).setBackgroundResource(R.drawable.btn_inverse_dark);
//			((Button) findViewById(R.id.folder_invsel_btn)).setBackgroundResource(R.drawable.btn_invsel_dark);
//			((Button) findViewById(R.id.folder_one_direction_btn)).setBackgroundResource(R.drawable.btn_onedir_dark);
//			((Button) findViewById(R.id.folder_selectall_btn)).setBackgroundResource(R.drawable.btn_selectall_dark);
//			((TextView) findViewById(R.id.folder_limit_label)).setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
//		}
			okBtn.setOnKeyListener(new View.OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					controlStateKeyboard = true;
					Log.i("TAG", "control: set to kb");
					return false;
				}
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_folder_menu, menu);
		return true;
	}

	public void buttonStart(View view) {
		int limit = Integer.parseInt(((Spinner) findViewById(R.id.folder_limit_spinner)).getSelectedItem().toString());
		ArrayList <WordPair> chosenWords = new ArrayList<WordPair>();
		ArrayList <String> fileNames = new ArrayList <String> (chosenWords.size());
		for (ToggleButton toggle : fileToggleButtons) {
			if (toggle.isChecked()) {
				String fileName = (String) toggle.getTag();
				fileNames.add(fileName);
				chosenWords.addAll(files.get(fileName).getWordPairs());
			}
		}

		if (chosenWords.size() > 0) {
			Intent intent = new Intent(FolderActivity.this, WordsActivity.class);
			boolean inverse = ((ToggleButton) findViewById(R.id.folder_inverse_btn)).isChecked();
			boolean oneDirection = ((ToggleButton) findViewById(R.id.folder_one_direction_btn)).isChecked();

			intent.putExtra(WORDS_INVERSE, inverse);
			intent.putExtra(WORDS_ONE_DIRECTION, oneDirConf || oneDirection);
			intent.putExtra(WORDS_LIMIT, limit);
			intent.putExtra(WordsActivity.WORD_FOLDER, wordFolder.getName());
			intent.putStringArrayListExtra(WordsActivity.WORD_FILES, fileNames);
			intent.putExtra(WORDS_STATE_KB, controlStateKeyboard);
			// intent.putExtra (WORDS_WORD_PAIRS, chosenWords);
			MainActivity.chosenWords = chosenWords;
			startActivity(intent);
		}
	}

	public void selectAll (View view) {
		int nchechked = 0;
		for (ToggleButton checkBox : fileToggleButtons) {
			if (checkBox.isChecked()) {
				++ nchechked;
			}
		}
		boolean checked = nchechked * 2 >= fileToggleButtons.size();
		for (ToggleButton checkBox : fileToggleButtons) {
			checkBox.setChecked(!checked);
		}
	}

	public void inverseSelection (View view) {
		for (ToggleButton checkBox : fileToggleButtons) {
			checkBox.setChecked(!checkBox.isChecked());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedBoxes = new ArrayList<Integer>();
		for (int i = 0; i < fileToggleButtons.size(); ++i) {
			ToggleButton box = fileToggleButtons.get(i);
			if (box.isChecked()) {
				savedBoxes.add(i);
			}

		}
		savedInstanceState.putSerializable(WORDS_BOXES, savedBoxes);

		savedInstanceState.putInt(WORDS_SCROLL_POSITION,
				scrollView.getScrollY());

		super.onSaveInstanceState(savedInstanceState);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		savedBoxes = (ArrayList<Integer>) savedInstanceState
				.getSerializable(WORDS_BOXES);

		scrollPosition = savedInstanceState.getInt(WORDS_SCROLL_POSITION);
		scrollView.post(new Runnable() {
			public void run() {
				scrollView.scrollTo(scrollView.getScrollX(), scrollPosition);
			}
		});
		scrollView.scrollTo(scrollView.getScrollX(), scrollPosition);

		super.onRestoreInstanceState(savedInstanceState);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menu_settings:
				Intent intent = new Intent();
				intent.setClass(FolderActivity.this, FolderPreferenceActivity.class);
				intent.putExtra (FolderPreferenceFragment.INTENT_FOLDER, folderName);
				startActivityForResult(intent, 0);
				return true;
			case R.id.folder_game_logs:
				Intent gameLogIntent = new Intent();
				gameLogIntent.setClass(FolderActivity.this, GameLogActivity.class);
				gameLogIntent.putExtra (GameLogActivity.INTENT_FOLDER, folderName);
				startActivityForResult(gameLogIntent, 0);
				return true;
			case R.id.folder_configure_fonts:
				Intent configureFontsIntent = new Intent();
				configureFontsIntent.setClass(FolderActivity.this, WordPreferenceActivity.class);

				int limit = Integer.parseInt(((Spinner) findViewById(R.id.folder_limit_spinner)).getSelectedItem().toString());
				List <WordPair> pairs = new ArrayList<WordPair>();
				ArrayList <WordPair> allPairs = new ArrayList<WordPair>();
				for (ToggleButton toggle : fileToggleButtons) {
					String fileName = (String) toggle.getTag();
					allPairs.addAll(files.get(fileName).getWordPairs());
					if (toggle.isChecked()) {
						pairs.addAll(files.get(fileName).getWordPairs());
					}
				}
				if (pairs.size() == 0) {
					pairs = allPairs;
				}
				pairs = ToolUtilities.randomizeList (pairs, limit);
				WordPair [] gamePairs = pairs.toArray(new WordPair [pairs.size()]);

				configureFontsIntent.putExtra(WordsActivity.WORD_FOLDER, folderName);
				configureFontsIntent.putExtra(WordsActivity.WORD_SIDE, gamePairs[0].getWordN(0).getSide());
				configureFontsIntent.putExtra (WordsActivity.WORD_GAME_CURRENT_WORD, 0);
				configureFontsIntent.putExtra (WordsActivity.WORD_GAME_WORDS, gamePairs);
				startActivityForResult(configureFontsIntent, 0);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		List <WordFile> fileList = wordFileDAO.getWordFiles(wordFolder);

		files = new HashMap<String, WordFile>();

		for (WordFile file : fileList) {
			this.files.put(file.getName(), file);
		}
		loadPref();
	}

	private void loadPref() {
		if (configurationDAO == null) {
			configurationDAO = new ConfigurationAndroidPrefDAO (this);
		}
		oneDirConf = configurationDAO.isOneDirection(folderName);

		Spinner spinner = (Spinner) findViewById(R.id.folder_limit_spinner);
		String [] lims = configurationDAO.getLimitList(folderName);
		String defLimit = configurationDAO.getDefaultLimit(folderName);
		int position = Arrays.asList(lims).indexOf(defLimit);
		if (lims != null) {
			ArrayAdapter <String> adapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item,  lims);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setSelection(position);
		} else {
			spinner.setSelection(4);
		}

		((ToggleButton) findViewById(R.id.folder_one_direction_btn)).setVisibility(oneDirConf ? CheckBox.GONE : CheckBox.VISIBLE);
		((ToggleButton) findViewById(R.id.folder_inverse_btn)).setVisibility(oneDirConf ? CheckBox.GONE : CheckBox.VISIBLE);
		
	}

}
