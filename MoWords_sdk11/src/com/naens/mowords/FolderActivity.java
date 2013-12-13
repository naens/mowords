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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.naens.dao.ConfigurationDAO;
import com.naens.dao.WordFileDAO;
import com.naens.dao.WordFolderDAO;
import com.naens.dao.androiddao.ConfigurationAndroidPrefDAO;
import com.naens.dao.androiddao.WordFolderAndroidDAO;
import com.naens.model.WordFile;
import com.naens.model.WordFolder;
import com.naens.model.WordPair;
import com.naens.mowords.R;
import com.naens.ui.FlowLayout;

public class FolderActivity extends Activity {

	public static final String WORDS_INVERSE = "com.naens.mowords.words_inverse";

	public static final String WORDS_LIMIT = "com.naens.mowords.words_limit";

	public static final String WORDS_ONE_DIRECTION = "com.naens.mowords.words_one_direction";

	public static final String WORDS_WORD_PAIRS = "com.naens.mowords.words_word_pairs";

	public static final String WORDS_BOXES = "com.naens.mowords.words_boxes";

	public static final String WORDS_SCROLL_POSITION = "com.naens.mowords.words_scroll_position";

	private List<ToggleButton> boxes;

	private ArrayList<Integer> savedBoxes;

	private Map<String, WordFile> files;

	private boolean oneDirConf;

	private ScrollView scrollView;

	private int scrollPosition;

	private WordFolder wordFolder;

	private String theme;

	@Override
	protected void onResume() {
		if (savedBoxes != null) {
			for (Integer box : savedBoxes) {
				boxes.get(box).setChecked(true);
			}
		}
		scrollView.setKeepScreenOn(false);
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		boxes = new LinkedList<ToggleButton>();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder);
    	loadTheme();
		Intent intent = getIntent();
		String folderName = intent.getStringExtra(MainActivity.FOLDER_NAME);
		WordFileDAO wordFileDAO = MainActivity.getWordFileDAO();
		ConfigurationDAO configurationDAO = new ConfigurationAndroidPrefDAO (this);

//get folder dao
		String rootFolder = configurationDAO.getRootFolder ();
		File sdcard = Environment.getExternalStorageDirectory ();
		WordFolderDAO folderDAO = new WordFolderAndroidDAO (sdcard.getAbsolutePath () + "/" + rootFolder);

		wordFolder = folderDAO.getFolderByName(folderName);
		oneDirConf = configurationDAO.isOneDirection(folderName);
		List <WordFile> fileList = wordFileDAO.getWordFiles(wordFolder);

		files = new HashMap<String, WordFile>();

		FlowLayout clt = (FlowLayout)  findViewById(R.id.folder_checkbox_layout);
		for (WordFile file : fileList) {
			this.files.put(file.getName(), file);
			ToggleButton toggle = new ToggleButton(this);
			boxes.add(toggle);
			// checkBox.setText (file.getName () + " (" + file.getSize () +  ")");
			toggle.setText(file.getName() + " (" + wordFileDAO.getFileSize(file) + ")");
			toggle.setTextOn(toggle.getText());
			toggle.setTextOff(toggle.getText());
			toggle.setTag(file.getName());

	    	if (theme.equals("Light")) {
				toggle.setBackgroundResource(R.drawable.folder_checkbox);
				toggle.setTextAppearance(this, R.style.FolderCheckbox);
	    	}
	    	if (theme.equals("Dark")) {
				toggle.setBackgroundResource(R.drawable.folder_checkbox_dark);
				toggle.setTextAppearance(this, R.style.FolderCheckbox_dark);
	    	}

			FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(6, 4);
			clt.addView(toggle, params);
		}
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

		if (oneDirConf) {
			((ToggleButton) findViewById(R.id.folder_one_direction_btn)).setVisibility(CheckBox.GONE);
			((ToggleButton) findViewById(R.id.folder_inverse_btn)).setVisibility(CheckBox.GONE);
		}
		scrollView = (ScrollView) findViewById(R.id.folder_scroll_view);
		Button okBtn = (Button) findViewById(R.id.folder_ok_button);
    	if (theme.equals("Light")) {
    		okBtn.setBackgroundResource(R.drawable.folder_button);
    		okBtn.setTextAppearance(this, R.style.FolderButton);
    		scrollView.setBackgroundResource(R.drawable.folderlist_background);
    		((Button) findViewById(R.id.folder_inverse_btn)).setBackgroundResource(R.drawable.btn_inverse);
    		((Button) findViewById(R.id.folder_invsel_btn)).setBackgroundResource(R.drawable.btn_invsel);
    		((Button) findViewById(R.id.folder_one_direction_btn)).setBackgroundResource(R.drawable.btn_onedir);
    		((Button) findViewById(R.id.folder_selectall_btn)).setBackgroundResource(R.drawable.btn_selectall);
    		((TextView) findViewById(R.id.folder_limit_label)).setTextColor(getResources().getColor(android.R.color.secondary_text_light));
    	}
    	if (theme.equals("Dark")) {
    		okBtn.setBackgroundResource(R.drawable.folder_button_dark);
    		okBtn.setTextAppearance(this, R.style.FolderButton_dark);
    		scrollView.setBackgroundResource(R.drawable.folderlist_background_dark);
    		((Button) findViewById(R.id.folder_inverse_btn)).setBackgroundResource(R.drawable.btn_inverse_dark);
    		((Button) findViewById(R.id.folder_invsel_btn)).setBackgroundResource(R.drawable.btn_invsel_dark);
    		((Button) findViewById(R.id.folder_one_direction_btn)).setBackgroundResource(R.drawable.btn_onedir_dark);
    		((Button) findViewById(R.id.folder_selectall_btn)).setBackgroundResource(R.drawable.btn_selectall_dark);
    		((TextView) findViewById(R.id.folder_limit_label)).setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
    	}
	}

	private void loadTheme() {
    	LinearLayout mainLayout = (LinearLayout) findViewById(R.id.folder_layout);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mainLayout.getContext());
    	theme = sharedPref.getString("pref_theme", "Dark");
    	if (theme.equals("Light")) {
    		setTheme(android.R.style.Theme_Light);
    		mainLayout.setBackgroundResource(android.R.color.background_light);
    	}
    	if (theme.equals("Dark")) {
    		setTheme(android.R.style.Theme);
    		mainLayout.setBackgroundResource(android.R.color.background_dark);
    	}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_folder_menu, menu);
		return true;
	}

	public void buttonStart(View view) {
		ArrayList<WordPair> chosenWords = new ArrayList<WordPair>(boxes.size());
		for (ToggleButton checkBox : boxes) {
			if (checkBox.isChecked()) {
				chosenWords.addAll(files.get(checkBox.getTag()).getWordPairs());
			}
		}

		if (chosenWords.size() > 0) {
			Intent intent = new Intent(FolderActivity.this, WordsActivity.class);
			boolean inverse = ((ToggleButton) findViewById(R.id.folder_inverse_btn)).isChecked();
			boolean oneDirection = ((ToggleButton) findViewById(R.id.folder_one_direction_btn)).isChecked();
			int limit = Integer.parseInt(((Spinner) findViewById(R.id.folder_limit_spinner)).getSelectedItem().toString());

			intent.putExtra(WORDS_INVERSE, inverse);
			intent.putExtra(WORDS_ONE_DIRECTION, oneDirConf || oneDirection);
			intent.putExtra(WORDS_LIMIT, limit);
			intent.putExtra(WordsActivity.WORD_FOLDER, wordFolder.getName());
			// intent.putExtra (WORDS_WORD_PAIRS, chosenWords);
			MainActivity.chosenWords = chosenWords;
			startActivity(intent);
		}
	}

	public void selectAll (View view) {
		int nchechked = 0;
		for (ToggleButton checkBox : boxes) {
			if (checkBox.isChecked()) {
				++ nchechked;
			}
		}
		boolean checked = nchechked * 2 >= boxes.size();
		for (ToggleButton checkBox : boxes) {
			checkBox.setChecked(!checked);
		}
	}

	public void inverseSelection (View view) {
		for (ToggleButton checkBox : boxes) {
			checkBox.setChecked(!checkBox.isChecked());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedBoxes = new ArrayList<Integer>();
		for (int i = 0; i < boxes.size(); ++i) {
			ToggleButton box = boxes.get(i);
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
//		scrollView.scrollTo(100, 100);

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
	    return false;
	}

}
