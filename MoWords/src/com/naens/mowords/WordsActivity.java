package com.naens.mowords;

import java.util.LinkedList;
import java.util.List;

import org.amr.arabic.ArabicUtilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.naens.model.Word;
import com.naens.model.WordPair;
import com.naens.mowords.R;
import com.naens.preferences.SettingsActivity;
import com.naens.preferences.WordSettingsActivity;
import com.naens.tools.FontProvider;
import com.naens.tools.ToolUtilities;
import com.naens.wordgame.WordGame;

public class WordsActivity extends Activity implements WordGui, OnSharedPreferenceChangeListener {

	private static final String WORD_GAME = "com.naens.mowords.WORGD_GAME";
	private static final String PAUSED = "com.naens.mowords.PAUSED";
	public static final String WORD_FOLDER = "com.naens.mowords.WORD_FOLDER";
	public static final String WORD_SIDE = "com.naens.mowords.SIDE";

	private WordGame wg;

	private LinearLayout layout;
	private TextView wordView;
	private int layoutWidth = 0;
	private int layoutHeight = 0;

	private Word wordToDisplay;
	private Typeface defaultItalic = Typeface.create("Times", Typeface.ITALIC);
	private boolean correct;
	private String folderName;
	private String theme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_words);
		layout = (LinearLayout) findViewById(R.id.words_word_layout);
		wordView = (TextView) findViewById(R.id.words_text_view);
		// wordView.setMovementMethod(new ScrollingMovementMethod());
		loadTheme();

		ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(this);

		Intent intent = getIntent();
		if (savedInstanceState == null) { // initialize new game
			wg = new WordGame();
			boolean inverse = intent.getBooleanExtra(FolderActivity.WORDS_INVERSE, false);
			boolean oneDirection = intent.getBooleanExtra(FolderActivity.WORDS_ONE_DIRECTION, false);
			int limit = intent.getIntExtra(FolderActivity.WORDS_LIMIT, 25);
			folderName = intent.getStringExtra(WORD_FOLDER);
			List<WordPair> words = MainActivity.chosenWords;
			wg.setGui(this);

			wg.startWordGame(words, limit, inverse, oneDirection);
		}

		layout.setOnTouchListener(activitySwipeDetector);
		layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				int newLayoutWidth = layout.getWidth();
				int newLayoutHeight = layout.getHeight();
				if (layoutWidth != newLayoutWidth || layoutHeight != newLayoutHeight) {
					layoutWidth = newLayoutWidth;
					layoutHeight = newLayoutHeight;
					if (wg.isPaused()) {
						displayPause();
					} else {
						displayWord(wordToDisplay, correct);
					}
				}
			}
		});

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPref.registerOnSharedPreferenceChangeListener(this);
	}

	private void loadTheme() {
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.words_layout);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mainLayout.getContext());
		theme = sharedPref.getString("pref_theme", "Dark");
		if (theme.equals("Light")) {
			setTheme(android.R.style.Theme_Light);
			mainLayout.setBackgroundResource(android.R.color.background_light);
			wordView.setBackgroundResource(R.drawable.wordview_background);
			wordView.setTextColor(getResources().getColor(android.R.color.primary_text_light));
			((TextView) findViewById(R.id.words_status_view)).setTextColor(getResources().getColor(
					android.R.color.secondary_text_light));
			((TextView) findViewById(R.id.words_time_view)).setTextColor(getResources().getColor(
					android.R.color.secondary_text_light));
		}
		if (theme.equals("Dark")) {
			setTheme(android.R.style.Theme);
			mainLayout.setBackgroundResource(android.R.color.background_dark);
			wordView.setBackgroundResource(R.drawable.wordview_background_dark);
			wordView.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
			((TextView) findViewById(R.id.words_status_view)).setTextColor(getResources().getColor(
					android.R.color.secondary_text_dark));
			((TextView) findViewById(R.id.words_time_view)).setTextColor(getResources().getColor(
					android.R.color.secondary_text_dark));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Log.i("TAG", "save game...");
		savedInstanceState.putParcelable(WORD_GAME, wg);
		savedInstanceState.putBoolean(PAUSED, wg.isPaused());
		Log.i("TAG", "save game: ok!");
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// load saved game
		Log.i("TAG", "load game...");
		wg = (WordGame) savedInstanceState.getParcelable(WORD_GAME);
		boolean paused = savedInstanceState.getBoolean(PAUSED);
		wg.setGui(this);
		wg.recreate(paused);
		Log.i("TAG", "load game: ok!");
	}

	public void wordClicked(View view) {
		wg.flipCorrect();
	}

	public void pauseClicked(View view) {
		wg.pause();
	}

	public void previousWordClicked(View view) {
		wg.previousWord();
	}

	public void nextWordClicked(View view) {
		wg.nextWord();
	}

	public void previousSideClicked(View view) {
		wg.previousSide();
	}

	public void nextSideClicked(View view) {
		wg.nextSide();
	}

	@SuppressLint("NewApi")
	@Override
	public void displayWord(Word word, boolean correct) {

		wordToDisplay = word;
		if (word.isImage()) { // display image
			if (word.getImage() == null) {
				if (theme.equals("Light")) {
					MainActivity.getWordFileDAO().loadImage(word,
							getResources().getColor(android.R.color.primary_text_light));
				}
				if (theme.equals("Dark")) {
					MainActivity.getWordFileDAO().loadImage(word,
							getResources().getColor(android.R.color.primary_text_dark));
				}
			}
			if (word.getImage() == null) { // no image
				displayText(String.format("no image: '%s'", word.getImageName()), 36, defaultItalic);
			} else if (layoutWidth > 0 && layoutHeight > 0) {
				int border = 12;
				LayoutParams lp = (LayoutParams) layout.getLayoutParams();
				int marginsH = lp.leftMargin + lp.rightMargin;
				int marginsV = lp.topMargin + lp.bottomMargin;
				int width = layoutWidth - 2 * border - marginsH;
				int height = layoutHeight - 2 * border - marginsV;
				Bitmap bmp = wordToDisplay.getImage();
				Log.i("TAG", String.format("new layout: w=%d h=%d", layoutWidth, layoutHeight));
				bmp = ToolUtilities.updateSize(wordToDisplay.getImage(), width, height);
				if (!word.isMdC()) {
					bmp = ToolUtilities.getRoundedCornerBitmap(bmp, Color.TRANSPARENT, 11, 3, this);
				}
				WordsActivity.this.displayImage(bmp);
			}

		} else { // display word
			displayText(word.getText(), word.getFontSize(), FontProvider.getFont(word.getFontName()));
		}
		displayCorrect(correct);

		// display buttons
		// ((Button)
		// findViewById(R.id.words_up_button)).setBackgroundResource(wg.isFirstWord()
		// ? R.drawable.bwvstop : R.drawable.bwup);
		// ((Button)
		// findViewById(R.id.words_down_button)).setBackgroundResource(wg.isLastWord()
		// ? R.drawable.bwvstop : R.drawable.bwdn);
		// ((Button)
		// findViewById(R.id.words_left_button)).setBackgroundResource(wg.isFirstSide()
		// ? R.drawable.bwhstop : R.drawable.bwlt);
		// ((Button)
		// findViewById(R.id.words_right_button)).setBackgroundResource(wg.isLastSide()
		// ? R.drawable.bwhstop : R.drawable.bwrt);
	}

	private void displayImage(Bitmap bmp) {
		// bmp = ToolUtilities.getRoundedCornerBitmap(bmp, Color.TRANSPARENT,
		// 11, 3, this);
		Drawable d = new BitmapDrawable(null, bmp);
		d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
		wordView.setCompoundDrawables(d, null, null, null);
		if (wordToDisplay.isMdC()) {
			wordView.setPadding(12, 12, 12, 12);
		} else {
			wordView.setPadding(1, 1, 1, 1);
		}
		wordView.setText("");
		if (theme.equals("Light")) {
			wordView.setBackgroundResource(R.drawable.wordview_background);
		}
		if (theme.equals("Dark")) {
			wordView.setBackgroundResource(R.drawable.wordview_background_dark);
		}
	}

	private boolean containsArabic(String s) {
		for (char c : s.toCharArray()) {
			if (c >= 0x600 && c <= 0x6ff			//Arabic
					|| c >= 0x0750 && c <= 0x077F	//Arabic Supplement
					|| c >= 0x08A0 && c <= 0x08FF	//Arabic Extended-A
					|| c >= 0xFB50 && c <= 0xFDFF	//Arabic Presentation Forms-A
					|| c >= 0xFE70 && c <= 0xFEFF	//Arabic Presentation Forms-B
					) {
				return true;
			}
		}
		return false;
	}

	private void displayText(String text, int size, Typeface typeface) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB 
				&& text.length() > 1 && containsArabic(text)) {	//doesn't display correctly with only one letter!
			wordView.setText(ArabicUtilities.reshapeSentence(text));
		} else {
			wordView.setText(text);// TODO:test
		}
		wordView.setCompoundDrawables(null, null, null, null);
		wordView.setTextSize(size);
		wordView.setTypeface(typeface);
		wordView.setPadding(8, 8, 8, 8);
		if (theme.equals("Light")) {
			wordView.setBackgroundResource(R.drawable.wordview_background);
		}
		if (theme.equals("Dark")) {
			wordView.setBackgroundResource(R.drawable.wordview_background_dark);
		}
	}

	@Override
	public void setGameController(WordGame wordGame) { // not needed
	}

	@Override
	public void displayTime(int time) {
		TextView timeView = (TextView) findViewById(R.id.words_time_view);
		timeView.setText(String.format("%02d:%02d", time / 60, time % 60));
	}

	@Override
	public void displayPause() {
		Typeface t = Typeface.create("Times", Typeface.ITALIC);
		displayText("Paused", 36, t);
		layout.setBackgroundResource(0);
	}

	@Override
	public void displayBlank() {
		// wordView.setCompoundDrawables (null, null, null, null);
		// wordView.setText ("");
	}

	@Override
	public void askSecondSide(List<Integer> firstSideTimes, List<Integer> firstSideCorrects) {
		String message = "Done.\n" + ToolUtilities.timeListToString(firstSideTimes)
				+ "\nDo you want to continue in the other direction?";
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage(message);
		alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				wg.askSecondSideYes();
			}
		});

		alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				wg.askSecondSideNo();
			}
		});
		alertbox.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				wg.resume();
			}
		});
		alertbox.show();
	}

	@Override
	public void askContinueGame(int incorrects) {
		String message = incorrects + " words are wrong. Do you want to continue?";

		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage(message);
		alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				wg.askContinueGameYes();
			}
		});

		alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				wg.askContinueGameNo();
			}
		});
		alertbox.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				wg.resume();
			}
		});
		alertbox.show();
	}

	@Override
	public void displayStats(List<Integer> firstTimes, List<Integer> firstCorr, List<Integer> secondTimes,
			List<Integer> secondCorr, int total) {
		int correct1 = firstCorr.get(0);
		String message = String
				.format("Game 1 (%d/%d=%.1f%s)", correct1, total, ((double) correct1 * 100) / total, "%");
		// if game 1 is last => display total time!!!
		List<Integer> allTimes = new LinkedList<Integer>();
		allTimes.add(ToolUtilities.sumList(firstTimes));
		if (secondTimes.size() > 0) {
			int correct2 = secondCorr.get(0);
			message += String
					.format("\nGame 2 (%d/%d=%.1f%s)", correct2, total, ((double) correct2 * 100) / total, "%");
			allTimes.add(ToolUtilities.sumList(secondTimes));
		}
		if (wg.isLastGame()) {
			message += "\nTotal: " + ToolUtilities.timeListToString(allTimes);

		}

		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage(message);
		alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				if (wg.isLastGame()) {
					finish();
				}
			}
		});
		alertbox.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				wg.resume();
			}
		});
		alertbox.show();
	}

	@Override
	public void displayCorrect(boolean correct) {
		this.correct = correct;
		if (correct) {
			layout.setBackgroundResource(R.drawable.words_word_border);
		} else {
			layout.setBackgroundResource(0);
		}
	}

	@Override
	public void displayStatus(int current, int numberWords) {
		((TextView) findViewById(R.id.words_status_view)).setText("word " + (current + 1) + " of " + numberWords);
	}

	public void onLongClick() {
		wg.pause();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onPause() {
		if (!wg.isPaused() && !isFinishing()) {
			wg.pause();
		}
		super.onPause();
	}

	public class ActivitySwipeDetector implements View.OnTouchListener {

		static final String logTag = "ActivitySwipeDetector";
		@SuppressWarnings("unused")
		private Activity activity;
		static final int MIN_DISTANCE = 60;
		static final int LONG_CLICK_TIME = 400;
		private float downX, downY, upX, upY;

		private long begin;

		public ActivitySwipeDetector(Activity activity) {
			this.activity = activity;
		}

		public void onRightToLeftSwipe() {
			wg.nextWord();
		}

		public void onLeftToRightSwipe() {
			wg.previousWord();
		}

		public void onTopToBottomSwipe() {
			wg.previousSide();
		}

		public void onBottomToTopSwipe() {
			wg.nextSide();
		}

		private void onLongClick() {
			WordsActivity.this.onLongClick();
		}

		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					downX = event.getX();
					downY = event.getY();
					begin = event.getEventTime();
					return false;
				}
				case MotionEvent.ACTION_UP: {
					// Log.i (logTag, "time: " + (event.getEventTime () -
					// begin));
					upX = event.getX();
					upY = event.getY();

					float deltaX = downX - upX;
					float deltaY = downY - upY;

					// swipe horizontal?
					if (Math.abs(deltaX) > MIN_DISTANCE && Math.abs(deltaY) < Math.abs(deltaX)) {
						// left or right
						if (deltaX < 0) {
							this.onLeftToRightSwipe();
							return true;
						}
						if (deltaX > 0) {
							this.onRightToLeftSwipe();
							return true;
						}
					} else {
						Log.i(logTag, "Swipe (x) was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
					}
					// swipe vertical?
					if (Math.abs(deltaY) > MIN_DISTANCE) {
						// top or down
						if (deltaY < 0) {
							this.onTopToBottomSwipe();
							return true;
						}
						if (deltaY > 0) {
							this.onBottomToTopSwipe();
							return true;
						}
					} else {
						Log.i(logTag, "Swipe (y) was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
					}
					if (event.getEventTime() - begin > LONG_CLICK_TIME) {
						this.onLongClick();
						return true;
					}

					return false;
				}
			}
			return false;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_words, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.menu_settings:
				Intent intent = new Intent(this, WordSettingsActivity.class);
				intent.putExtra(WORD_FOLDER, folderName);
				intent.putExtra(WORD_SIDE, wordToDisplay.getSide());
				startActivity(intent);
				if (!wg.isPaused() && !isFinishing()) {
					wg.pause();
				}
				return true;
				// case R.id.help:
				// showHelp();
				// return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// int side = wg.getSide() + 1;// side in game
		int side = wordToDisplay.getSide();// word side
		if (key.endsWith(SettingsActivity.FONTSIZE_SUFFIX)) {
			int fontSize = Integer.parseInt(MainActivity.getConfigurationDAO().getSideFontSize(folderName, side));
			for (WordPair wp : MainActivity.chosenWords) {
				Word w = wp.getWordBySideNumber(side);
				if (w != null) {
					w.setFontSize(fontSize);
				}
			}
		}

		if (key.endsWith(SettingsActivity.TYPEFACE_SUFFIX)) {
			String fontName = MainActivity.getConfigurationDAO().getSideFontName(folderName, side);
			for (WordPair wp : MainActivity.chosenWords) {
				Word w = wp.getWordBySideNumber(side);
				if (w != null) {
					w.setFontName(fontName);
				}
			}
		}

	}

	@Override
	public void onBackPressed() {
		// pause
		if (!wg.isPaused()) {
			wg.pause();
		}
		String message = "Quit study mode?";
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage(message);
		alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				WordsActivity.super.onBackPressed();
			}
		});

		alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		alertbox.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
			}
		});
		alertbox.show();
	}

}
