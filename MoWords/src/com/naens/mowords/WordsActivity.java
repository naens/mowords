package com.naens.mowords;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.naens.dao.WordFileDAO;
import com.naens.dao.androiddao.WordFileAndroidDAO;
import com.naens.mdctools.InvalidMdCCodeException;
import com.naens.model.Word;
import com.naens.model.WordPair;
import com.naens.preferences.SettingsValues;
import com.naens.preferences.WordPreferenceActivity;
import com.naens.service.GameLogDbHelper;
import com.naens.tools.FontProvider;
import com.naens.tools.ToolUtilities;
import com.naens.ui.DrawView;
import com.naens.ui.DrawView.PenMode;
import com.naens.ui.DrawView.PenModeListener;
import com.naens.wordgame.WordGame;

public class WordsActivity extends Activity implements WordGui, PenModeListener {

	public static final String WORD_GAME = "com.naens.mowords.WORGD_GAME";
	public static final String PAUSED = "com.naens.mowords.PAUSED";
	public static final String WORD_FOLDER = "com.naens.mowords.WORD_FOLDER";
	public static final String WORD_SIDE = "com.naens.mowords.SIDE";
//	public static final String WORD_WORDS = "com.naens.mowords.WORDS";
	public static final String WORD_GAME_WORDS = "com.naens.mowords.GAME_WORDS";
	public static final String WORD_GAME_CURRENT_WORD = "com.naens.mowords.GAME_CURRENT_WORD";
	public static final String WORD_FILES = "com.naens.mowords.WORD_FILES";
	public static final String WORD_BEGIN_TIME = "com.naens.mowords.WORD_BEGIN_TIME";
	public static final String GAMELOG_TABLE_NAME = "gamelog";
	public static final String GAMELOG_COLUMN_ID = "id";
	public static final String GAMELOG_COLUMN_FOLDER = "folder";
	public static final String GAMELOG_COLUMN_DATE = "date";
	public static final String GAMELOG_COLUMN_FILES = "files";
	public static final String GAMELOG_COLUMN_DONE = "done";
	public static final String GAMELOG_COLUMN_TOTAL = "total";
	public static final String GAMELOG_COLUMN_GAME_TIME = "gametime";
	public static final String GAMELOG_COLUMN_SIDE = "side";
	public static final String GAMELOG_COLUMN_SIDES = "sides";
	public static final String GAMELOG_COLUMN_INVERSE = "inverse";

	public static final int MINIMUM_DRAW_HEIGHT = 64;
	public static final int MINIMUM_WORD_SPACE = 240;

	private WordGame wg;

	private LinearLayout layout;
	private TextView wordView;

	private Word wordToDisplay;
	private Typeface defaultItalic = FontProvider.getDefaultItalic(); 
	private String folderName;
	private Menu menu;
	private boolean inverse;
	private ArrayList<String> fileNames;
	private long beginTime;
	private long beginSecondTime;
	private DrawView drawView;
	private enum ControlState {SCREEN, KEYBOARD};
	private ControlState cs = ControlState.SCREEN;
	private View actionBarHeightView;
	private RelativeLayout screenLayout;
	private boolean startNewGame = false;
	private boolean oneDirection;
	private int limit;
	private SharedPreferences sharedPref;
	private ToggleButton soundButton;
	private MediaPlayer mediaPlayer;
	private WordFileDAO wordFileDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_words);
		layout = (LinearLayout) findViewById(R.id.words_word_layout);
		wordView = (TextView) findViewById(R.id.words_text_view);
		soundButton = (ToggleButton) findViewById(R.id.words_sound_button);
		actionBarHeightView = findViewById(R.id.words_actionbar_height);
		loadTheme();

		ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(this);

		Intent intent = getIntent();
		if (savedInstanceState == null) { // initialize new game
			startNewGame = true;
			inverse = intent.getBooleanExtra(FolderActivity.WORDS_INVERSE, false);
			oneDirection = intent.getBooleanExtra(FolderActivity.WORDS_ONE_DIRECTION, false);
			limit = intent.getIntExtra(FolderActivity.WORDS_LIMIT, 25);
			folderName = intent.getStringExtra(WORD_FOLDER);
			fileNames = intent.getStringArrayListExtra(WORD_FILES);
			cs = intent.getBooleanExtra(FolderActivity.WORDS_STATE_KB, false) ? ControlState.KEYBOARD : ControlState.SCREEN;
		}

		layout.setOnTouchListener(activitySwipeDetector);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
	        public void onPrepared(MediaPlayer mp) {
	        }   
	    });
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				soundButton.setChecked(false);
			}
		});
		soundButton.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {	//play
					soundButton.setBackgroundResource(android.R.drawable.ic_media_pause);
					mediaPlayer.start();
					Log.i("TAG", "mp:onCheckedChanged checked");
				} else {			//pause
					soundButton.setBackgroundResource(android.R.drawable.ic_media_play);
					mediaPlayer.pause();
					Log.i("TAG", "mp:onCheckedChanged unchecked");
				}
			}
		});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				String w = what == MediaPlayer.MEDIA_ERROR_UNKNOWN ? "MEDIA_ERROR_UNKNOWN" :
					what == MediaPlayer.MEDIA_ERROR_SERVER_DIED ? "MEDIA_ERROR_SERVER_DIED" : "-----";
//				String e = what == MediaPlayer.MEDIA_ERROR_IO ? "MEDIA_ERROR_IO" :
//					what == MediaPlayer.MEDIA_ERROR_MALFORMED ? "MEDIA_ERROR_MALFORMED" : 
//					what == MediaPlayer.MEDIA_ERROR_UNSUPPORTED ? "MEDIA_ERROR_UNSUPPORTED" : 
//					what == MediaPlayer.MEDIA_ERROR_UNSUPPORTED ? "MEDIA_ERROR_UNSUPPORTED" : 
//					what == MediaPlayer.MEDIA_ERROR_TIMED_OUT ? "MEDIA_ERROR_TIMED_OUT" : "-----";
//				Log.e ("TAG", "error: what=" + w + " extra=" + e);
				Log.e ("TAG", "error: what=" + w);
				return true;
			}
		});

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		wordFileDAO = new WordFileAndroidDAO (this);

		drawView = (DrawView) findViewById(R.id.draw_view);
		drawView.setPenModeListener(this);
		View drawButton = (View) findViewById(R.id.draw_view_button);
		drawButton.setOnTouchListener(new View.OnTouchListener() {
			private float originalY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float y = event.getY();
				float dy = y - originalY;
				float height = drawView.getHeight();
				int drawHeight = (int)(height - dy);
				int h = screenLayout.getHeight();
				int actionbarHeight = getActionBar().getHeight();
				int statusBarHeight = findViewById(R.id.words_status_panel).getHeight();
				int freeSpace = h - actionbarHeight - statusBarHeight - drawHeight;
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						Log.i ("TAG", "touch:down");
						originalY = y;
						return true;
					}
					case MotionEvent.ACTION_MOVE: {
//						Log.i ("TAG", "touch:move change = " + dy);
//						Log.i ("TAG", "drawView height = " + height);
						if (freeSpace < MINIMUM_WORD_SPACE || drawHeight < MINIMUM_DRAW_HEIGHT) {
							return true;
						}
						RelativeLayout.LayoutParams dwlp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, drawHeight); 
						drawView.setLayoutParams(dwlp);
						return true;
					}
					case MotionEvent.ACTION_UP: {
						//save
//						Log.i ("TAG", "touch:up change = " + dy);
//						Log.i ("TAG", "drawView height = " + height);
						if (freeSpace < MINIMUM_WORD_SPACE) {
							drawHeight = h - actionbarHeight - statusBarHeight - MINIMUM_WORD_SPACE;
						}
						if (drawHeight < MINIMUM_DRAW_HEIGHT) {
							drawHeight = MINIMUM_DRAW_HEIGHT;
						}
						sharedPref.edit().putInt(SettingsValues.getDrawHeightKey(folderName), drawHeight).commit();
//						RelativeLayout.LayoutParams dwlp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, drawHeight); 
//						drawView.setLayoutParams(dwlp);
						drawView.setPreferedHeight(drawHeight);
						drawView.setPreferedWidth(screenLayout.getWidth());
						drawView.updateSize();
						//TODO: test
//						return true;
				        v.performClick();
				        break;
					}
				}
				return false;
			}
		});
		boolean showDraw = sharedPref.getBoolean(SettingsValues.getShowDrawKey(folderName), false);
		if (showDraw) {
			drawView.setVisibility(View.VISIBLE);
			drawButton.setVisibility(View.VISIBLE);
		} else {
			drawView.setVisibility(View.GONE);
			drawButton.setVisibility(View.GONE);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int h = newConfig.screenHeightDp;
		int actionbarHeight = getActionBar().getHeight();
		int statusBarHeight = findViewById(R.id.words_status_panel).getHeight();
		int defaultheight = h > 800 ? 400 : h / 2;
		int drawHeight = sharedPref.getInt(SettingsValues.getDrawHeightKey(folderName), defaultheight);
		int freeSpace = h - actionbarHeight - statusBarHeight - drawHeight;
		Log.i("TAG", String.format("total height=%d actionbar=%d statusbar=%d free space=%d", h, actionbarHeight, statusBarHeight, freeSpace));
		if (freeSpace < MINIMUM_WORD_SPACE) {
			drawHeight = h - actionbarHeight - statusBarHeight - MINIMUM_WORD_SPACE;
		}
		if (drawHeight < MINIMUM_DRAW_HEIGHT) {
			drawHeight = MINIMUM_DRAW_HEIGHT;
		}
		sharedPref.edit().putInt(SettingsValues.getDrawHeightKey(folderName), drawHeight).commit();
		RelativeLayout.LayoutParams dwlp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, drawHeight); 
		drawView.setLayoutParams(dwlp);
		Log.i("TAG", "conf changed. new h=" + h + " drawh=" + drawHeight);
		drawView.setPreferedHeight(drawHeight);
		drawView.setPreferedWidth(newConfig.screenWidthDp);
		drawView.updateSize();
	}

	@Override
	protected void onResume() {
		screenLayout = (RelativeLayout) findViewById(R.id.words_layout);
		
		ViewTreeObserver viewTreeObserver = screenLayout.getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
			viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				@Override
				public void onGlobalLayout() {
					if (startNewGame) {
						startNewGame = false;
						wg = new WordGame();
						beginTime = Calendar.getInstance().getTimeInMillis();
						List<WordPair> words = MainActivity.chosenWords;
						wg.setGui(WordsActivity.this);
						changeControlState(cs);

						wg.startWordGame(words, limit, inverse, oneDirection);
					}
					if (wg.isGameActive()) {
						if (wg.isPaused()) {
							displayPause();
						} else {	//TODO else if -> avoid recursion
//							displayWord(wordToDisplay, correct);
						}
					}
					int actionbarHeight = getActionBar().getHeight();
					actionBarHeightView = findViewById(R.id.words_actionbar_height);
					RelativeLayout.LayoutParams abhvlp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, actionbarHeight);
					actionBarHeightView.setLayoutParams(abhvlp);
					
				}
			});
		}
		super.onResume();
	}

	private void loadTheme() {
		RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.words_layout);
//		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mainLayout.getContext());
//		theme = sharedPref.getString("pref_theme", "Dark");
//		if (theme.equals("Light")) {
			setTheme(android.R.style.Theme_Holo_Light);
			mainLayout.setBackgroundResource(android.R.color.background_light);
			wordView.setBackgroundResource(R.drawable.wordview_background);
			wordView.setTextColor(getResources().getColor(android.R.color.primary_text_light));
			((TextView) findViewById(R.id.words_status_view)).setTextColor(getResources().getColor(
					android.R.color.secondary_text_light));
			((TextView) findViewById(R.id.words_time_view)).setTextColor(getResources().getColor(
					android.R.color.secondary_text_light));
//		}
//		if (theme.equals("Dark")) {
//			setTheme(android.R.style.Theme_Holo);
//			mainLayout.setBackgroundResource(android.R.color.background_dark);
//			wordView.setBackgroundResource(R.drawable.wordview_background_dark);
//			wordView.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
//			((TextView) findViewById(R.id.words_status_view)).setTextColor(getResources().getColor(
//					android.R.color.secondary_text_dark));
//			((TextView) findViewById(R.id.words_time_view)).setTextColor(getResources().getColor(
//					android.R.color.secondary_text_dark));
//		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Log.i("TAG", "save game...");
		savedInstanceState.putLong(WORD_BEGIN_TIME, beginTime);
		savedInstanceState.putParcelable(WORD_GAME, wg);
		savedInstanceState.putBoolean(PAUSED, wg.isPaused());
		Log.i("TAG", "save game: ok!");
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// load saved game
		Log.i("TAG", "load game...");
		beginTime = savedInstanceState.getLong(WORD_BEGIN_TIME);
		wg = (WordGame) savedInstanceState.getParcelable(WORD_GAME);
		boolean paused = savedInstanceState.getBoolean(PAUSED);
		wg.setGui(this);
		wg.recreate(paused);
		Log.i("TAG", "load game: ok!");
	}

	public void wordClicked(View view) {
		changeControlState (ControlState.SCREEN);
		wg.flipCorrect();
	}

	public void pauseClicked(View view) {
		wg.pause();
	}

	public void previousWordClicked(View view) {
		if (!mediaPlayer.isPlaying())
		wg.previousWord();
	}

	public void nextWordClicked(View view) {
		if (!mediaPlayer.isPlaying())
		wg.nextWord();
	}

	public void previousSideClicked(View view) {
		wg.previousSide();
	}

	public void nextSideClicked(View view) {
		wg.nextSide();
	}


	@Override
	public void displayWord(Word word, boolean correct) {
		layout.setKeepScreenOn(true);
		 wordView.setVisibility(View.VISIBLE);
		if (cs.equals(ControlState.KEYBOARD)) {
			getActionBar().hide();
			actionBarHeightView.setVisibility(View.GONE);
		}
		wordToDisplay = word;

	//sound
		if (word.isSound()) {
			wordView.setVisibility(View.GONE);
			soundButton.setVisibility(View.VISIBLE);
			File soundFile = new File(wordToDisplay.getString());
			if (!soundFile.exists()) {
				String errorString = String.format("sound file %s doesn't exist!", soundFile.getAbsolutePath());
				Log.e("TAG", errorString);
				soundButton.setVisibility(View.GONE);
				wordView.setVisibility(View.VISIBLE);
				displayText(errorString, word.getFontSize(), FontProvider.getFont(word.getFontName(), this));
			}
			Uri myUri = Uri.parse(soundFile.toString());
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			try {
				Log.i("TAG", "mp:reset");
				mediaPlayer.reset();
				mediaPlayer.setDataSource(getApplicationContext(), myUri);
				mediaPlayer.prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			soundButton.setVisibility(View.GONE);
			wordView.setVisibility(View.VISIBLE);
		}

	//image
		if (word.isImage() || word.isMdC()) {
			Bitmap bmp = word.getImage();
			if (bmp == null) {
//				if (theme.equals("Light")) {
					try {
						wordFileDAO.loadImage(word, getResources().getColor(android.R.color.primary_text_light));
					} catch (NotFoundException e) {
						e.printStackTrace();
					} catch (InvalidMdCCodeException e) {
						String errorMdCString = getResources().getString(R.string.words_error_mdc, word.getString());
//						word.setWord(errorMdCString);
						displayText(errorMdCString, word.getFontSize(), FontProvider.getFont(word.getFontName(), this));
						e.printStackTrace();
					}
//				}
//				if (theme.equals("Dark")) {
//					MainActivity.getWordFileDAO().loadImage(word,
//							getResources().getColor(android.R.color.primary_text_dark));
//				}
				bmp = word.getImage();
			}
			if (bmp == null) { // no image
				String noImageString = getResources().getString(R.string.words_no_image, word.getString());
				displayText(noImageString, 36, defaultItalic);
			} else {
				displayImage(bmp, !word.isMdC());
			}

		} else { // display word
			displayText(word.getString(), word.getFontSize(), FontProvider.getFont(word.getFontName(), this));
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

	private void displayImage(Bitmap bmp, boolean rounded) {
		int border = 12;
		int actionbarHeight = getActionBar().getHeight();
		int statusBarHeight = findViewById(R.id.words_status_panel).getHeight();
		int w = screenLayout.getWidth() - 2 * border;
		int h = screenLayout.getHeight() - actionbarHeight - statusBarHeight - 2 * border;
		bmp = ToolUtilities.assureSize (bmp, w, h);
		if (rounded) {
//			wordView.setPadding(1, 1, 1, 1);
			bmp = ToolUtilities.getRoundedCornerBitmap(bmp, Color.TRANSPARENT, 11, 3, this);
		} else {
			wordView.setPadding(8, 8, 8, 8);
		}
		Drawable d = new BitmapDrawable(null, bmp);
		d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
		wordView.setCompoundDrawables(d, null, null, null);
		wordView.setText("");
//		if (theme.equals("Light")) {
			wordView.setBackgroundResource(R.drawable.wordview_background);
//		}
//		if (theme.equals("Dark")) {
//			wordView.setBackgroundResource(R.drawable.wordview_background_dark);
//		}
	}

	private void displayText(String text, int size, Typeface typeface) {
		wordView.setText(text);
		wordView.setCompoundDrawables(null, null, null, null);
		wordView.setTextSize(size);
		wordView.setTypeface(typeface);
		wordView.setPadding(12, 8, 12, 8);
//		if (theme.equals("Light")) {
			wordView.setBackgroundResource(R.drawable.wordview_background);
//		}
//		if (theme.equals("Dark")) {
//			wordView.setBackgroundResource(R.drawable.wordview_background_dark);
//		}
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
		if (wordToDisplay.isSound()) {
			soundButton.setVisibility(View.GONE);
			wordView.setVisibility(View.VISIBLE);
		}
		if (cs.equals(ControlState.KEYBOARD)) {
			getActionBar().show(); 
			actionBarHeightView.setVisibility(View.VISIBLE);
		}
		displayText(getResources().getString(R.string.words_paused), 36, defaultItalic);
		layout.setBackgroundResource(0);
		layout.setKeepScreenOn(false);
	}

	@Override
	public void displayBlank() {
		 wordView.setVisibility(View.GONE);
	}

	@Override
	public void askSecondSide(List<Integer> firstSideTimes, List<Integer> firstSideCorrects) {
		String secondSideMessage = getResources().getString(R.string.words_second_side_message,
				ToolUtilities.timeListToString(firstSideTimes));
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage(secondSideMessage);
		alertbox.setNeutralButton(getResources().getString(R.string.words_ok_button),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				if (drawView != null && drawView.getVisibility() == View.VISIBLE) {
					drawView.clear();
				}
				beginSecondTime = Calendar.getInstance().getTimeInMillis();
				wg.askSecondSideYes();
			}
		});
		alertbox.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				wg.resume();
				 wordView.setVisibility(View.VISIBLE);
			}
		});
		final AlertDialog alert = alertbox.create();
		alert.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {

				Button positive = alert.getButton(AlertDialog.BUTTON_POSITIVE);
				positive.requestFocus();
				
			}
		});
		alert.show();
	}

	@Override
	public void askContinueGame(int incorrects) {
		String continueGameMessage = getResources().getString(R.string.words_continue_game_message, incorrects);

		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage(continueGameMessage);
		alertbox.setNeutralButton(getResources().getString(R.string.words_ok_button),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				if (drawView != null && drawView.getVisibility() == View.VISIBLE) {
					drawView.clear();
				}
				wg.askContinueGameYes();
			}
		});
		alertbox.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				wg.resume();
				 wordView.setVisibility(View.VISIBLE);
			}
		});
		final AlertDialog alert = alertbox.create();
		alert.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {

				Button positive = alert.getButton(AlertDialog.BUTTON_POSITIVE);
				positive.requestFocus();
				
			}
		});
		alert.show();
	}

	private void logGame (long time, boolean inverse, ArrayList<String> files, int done, int total, int gameTime, int side, int sides) {
		GameLogDbHelper dbHelper = new GameLogDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		StringBuffer filesString = new StringBuffer();
		for (int i = 0; i < files.size(); ++ i) {
			String s = files.get(i);
			filesString.append(s);
			if (i < files.size() - 1) {
				filesString.append(", ");
			}
		}

		ContentValues values = new ContentValues();
		values.put(GAMELOG_COLUMN_FOLDER, folderName);
		values.put(GAMELOG_COLUMN_DATE, time);
		values.put(GAMELOG_COLUMN_FILES, filesString.toString());
		values.put(GAMELOG_COLUMN_INVERSE, inverse ? 1 : 0);
		values.put(GAMELOG_COLUMN_DONE, done);
		values.put(GAMELOG_COLUMN_TOTAL, total);
		values.put(GAMELOG_COLUMN_GAME_TIME, gameTime);
		values.put(GAMELOG_COLUMN_SIDE, side);
		values.put(GAMELOG_COLUMN_SIDES, sides);

//		// Insert the new row, returning the primary key value of the new row
		long newRowId = db.insert(GAMELOG_TABLE_NAME, null, values);
		Log.i("TAG", "inserted game log with row id " + newRowId);
		db.close();
		dbHelper.close();
	}

	@Override
	public void displayStats(List<Integer> firstTimes, List<Integer> firstCorr, List<Integer> secondTimes,
			List<Integer> secondCorr, int total, boolean aborted) {
		int correct1 = firstCorr.get(0);
		String statsGame1Message = getResources().getString(R.string.words_display_stats_game_message, 1,  
				correct1, total, ((double) correct1 * 100) / total, "%");
		String message = statsGame1Message;
		List<Integer> allTimes = new LinkedList<Integer>();
		int firstTimeSum = ToolUtilities.sumList(firstTimes);
		int secondTimeSum = 0;
		allTimes.add(firstTimeSum);
		if (!aborted) {
			if (secondTimes.size() > 0) {
			//	log first game
				logGame(beginTime, inverse, fileNames, correct1, total, firstTimeSum, 1, 2);
				secondTimeSum = ToolUtilities.sumList(secondTimes);
				int correct2 = secondCorr.get(0);
				String statsGame2Message = getResources().getString(R.string.words_display_stats_game_message, 2,  
						correct2, total, ((double) correct2 * 100) / total, "%");
				message += "\n" + statsGame2Message;
				allTimes.add(secondTimeSum);
			//	log second game
				logGame(beginSecondTime, !inverse, fileNames, correct2, total, secondTimeSum, 2, 2);
			} else {
				logGame(beginTime, inverse, fileNames, correct1, total, firstTimeSum, 1, 1);
			}
		}
		if (wg.isLastGame()) {
			String statsTotalMessage = getResources().getString(R.string.words_display_stats_total_message, 
					ToolUtilities.timeListToString(allTimes));
			message += "\n" + statsTotalMessage;
		}

		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage(message);
		alertbox.setCancelable(false);	//prevents the back button from closing the dialog
		String statsOkMessage = getResources().getString(R.string.words_ok_button);
		alertbox.setNeutralButton(statsOkMessage, new DialogInterface.OnClickListener() {
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
		if (correct) {
			layout.setBackgroundResource(R.drawable.words_word_border);
		} else {
			layout.setBackgroundResource(0);
		}
	}

	@Override
	public void displayStatus(int current, int numberWords) {
		((TextView) findViewById(R.id.words_status_view)).setText(getResources().getString(R.string.words_display_status_message,
				(current + 1), numberWords));
	}

	public void onLongClick() {
		changeControlState (ControlState.SCREEN);
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

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode != KeyEvent.KEYCODE_P) {
			changeControlState (ControlState.KEYBOARD);
		}
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
				wg.previousSide();
				return true;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				wg.nextSide();
				return true;
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (!mediaPlayer.isPlaying())
				wg.previousWord();
				return true;
			case KeyEvent.KEYCODE_MEDIA_NEXT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (!mediaPlayer.isPlaying())
				wg.nextWord();
				return true;
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				wg.pause();
				return true;
			case KeyEvent.KEYCODE_P:
				if ((event.getFlags() & KeyEvent.FLAG_FROM_SYSTEM) > 0) {
					wg.pause();
				}
				return true;
			case KeyEvent.KEYCODE_ENTER:
			case KeyEvent.KEYCODE_SPACE:
				wg.flipCorrect();
				return true;
			case KeyEvent.KEYCODE_W:
				if ((event.getFlags() & KeyEvent.FLAG_FROM_SYSTEM) > 0) {
					showDraw ();
				}
				return true;
			case KeyEvent.KEYCODE_DEL:
			case KeyEvent.KEYCODE_CLEAR:
				drawView.clear();
				return true;
			case KeyEvent.KEYCODE_MENU:
				if (!wg.isPaused()) {
					wg.pause();
				}
				return false;
			default:
				return super.onKeyUp(keyCode, event);
		}
	}

	private void changeControlState (ControlState controlState) {
		if (cs.equals(controlState)) {
			//nothing to do
			return;
		}
		if (controlState.equals(ControlState.SCREEN)) {
			getActionBar().show();
			actionBarHeightView.setVisibility(View.VISIBLE);
			cs = ControlState.SCREEN;
		}
		if (controlState.equals(ControlState.KEYBOARD)) {
			getActionBar().hide();
			actionBarHeightView.setVisibility(View.GONE);
			cs = ControlState.KEYBOARD;
		}
		
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
			changeControlState (ControlState.SCREEN);
			if (!mediaPlayer.isPlaying())
			wg.nextWord();
		}

		public void onLeftToRightSwipe() {
			changeControlState (ControlState.SCREEN);
			if (!mediaPlayer.isPlaying())
			wg.previousWord();
		}

		public void onTopToBottomSwipe() {
			changeControlState (ControlState.SCREEN);
			wg.previousSide();
		}

		public void onBottomToTopSwipe() {
			changeControlState (ControlState.SCREEN);
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
							//TODO: test
//							return true;
					        v.performClick();
					        break;
						}
						if (deltaX > 0) {
							this.onRightToLeftSwipe();
							//TODO: test
//							return true;
					        v.performClick();
					        break;
						}
					} else {
//						Log.i(logTag, "Swipe (x) was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
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
//						Log.i(logTag, "Swipe (y) was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
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
		getMenuInflater().inflate(R.menu.activity_words_menu, menu);
		this.menu = menu;
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		boolean showDraw = sharedPref.getBoolean(SettingsValues.getShowDrawKey(folderName), false);
		MenuItem clear = (MenuItem) menu.findItem(R.id.words_action_clear);
		MenuItem back = (MenuItem) menu.findItem(R.id.words_action_back);
		MenuItem forward = (MenuItem) menu.findItem(R.id.words_action_forward);
		MenuItem erase = (MenuItem) menu.findItem(R.id.words_action_erase);
		clear.setVisible(showDraw);
		back.setVisible(showDraw);
		forward.setVisible(showDraw);
		erase.setVisible(showDraw);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.words_menu_settings:
				Intent intent = new Intent();
				intent.setClass(WordsActivity.this, WordPreferenceActivity.class);
				List <WordPair> pairs = wg.getPairs ();
				WordPair [] gamePairs = pairs.toArray(new WordPair [pairs.size()]);

				intent.putExtra(WORD_FOLDER, folderName);
				intent.putExtra(WORD_SIDE, wordToDisplay.getSide());
				intent.putExtra (WORD_GAME_CURRENT_WORD, wg.getCurrentWordNumber ());
				intent.putExtra (WORD_GAME_WORDS, gamePairs);
				startActivityForResult(intent, 0);
				if (!wg.isPaused() && !isFinishing()) {
					wg.pause();
				}
				return true;
			case R.id.words_action_draw:
				showDraw ();
				return true;
			case R.id.words_action_clear:
				drawView.clear();
				return true;
			case R.id.words_action_back:
				drawView.back();
				return true;
			case R.id.words_action_forward:
				drawView.forward();
				return true;
			case R.id.words_action_erase:
				drawView.changePenMode();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void penModeChanged (PenMode penMode) {
		MenuItem erase = (MenuItem) menu.findItem(R.id.words_action_erase);
		if (penMode.equals(DrawView.PenMode.DRAW)) {
			erase.setIcon(getResources().getDrawable(R.drawable.erase));
		} else {
			erase.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_edit));
		}
	}

	private void showDraw () {
		boolean showDraw = drawView.getVisibility() == View.GONE;
		MenuItem clear = (MenuItem) menu.findItem(R.id.words_action_clear);
		MenuItem back = (MenuItem) menu.findItem(R.id.words_action_back);
		MenuItem forward = (MenuItem) menu.findItem(R.id.words_action_forward);
		MenuItem erase = (MenuItem) menu.findItem(R.id.words_action_erase);
		View drawButton = (View) findViewById(R.id.draw_view_button);
//		mdpi:32x32
//		ldpi:36x36
//		hdpi:48x48
//		xhdpi:64x64
//		xxhdpi:96x96		
		clear.setVisible(showDraw);
		back.setVisible(showDraw);
		forward.setVisible(showDraw);
		erase.setVisible(showDraw);
		if (showDraw) {
			drawView.setVisibility(View.VISIBLE);
			drawButton.setVisibility(View.VISIBLE);
		} else {
			drawView.setVisibility(View.GONE);
			drawView.clear();
			drawButton.setVisibility(View.GONE);
		}
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(SettingsValues.getShowDrawKey(folderName), showDraw); 
		editor.commit();
	}

	@Override
	public void onBackPressed() {
		// pause
		if (!wg.isPaused()) {
			wg.pause();
		}
		String message = getResources().getString(R.string.words_back_pressed_message);
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage(message);
		alertbox.setPositiveButton(getResources().getString(R.string.words_back_pressed_positive),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				WordsActivity.super.onBackPressed();
			}
		});

		alertbox.setNegativeButton(getResources().getString(R.string.words_back_pressed_negative),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		alertbox.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
			}
		});
		alertbox.show();
	}

	@Override
	public void nextWord() {
		if (drawView != null && drawView.getVisibility() == View.VISIBLE) {
			drawView.clear();
		}
	}

	@Override
	public void previousWord() {
		if (drawView != null && drawView.getVisibility() == View.VISIBLE) {
			drawView.clear();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		for (WordPair wp : MainActivity.chosenWords) {
			int pairSize = wp.getSize();
			for (int i = 0; i < pairSize; ++i) {
				Word word = wp.getWordN(i);
				if (!word.isImage()) {
					String typeFaceKey = SettingsValues.getFontNameKey(folderName, word.getSide());
					String typefaceDef = getResources().getString(R.string.pref_side_typeface_default);
					String fontName = sp.getString(typeFaceKey, typefaceDef);
					word.setFontName(fontName);
	
					String fontSizeKey = SettingsValues.getFontSizeKey(folderName, word.getSide());
					String fontSizeDef = getResources().getString(R.string.pref_side_font_size_default);
					String fontSizeValue = sp.getString(fontSizeKey, fontSizeDef);
					int fontSize = Integer.parseInt(fontSizeValue);
					word.setFontSize(fontSize);
					
					String mdcKey = SettingsValues.getMdCKey(folderName, word.getSide());
					boolean encodingDef = getResources().getBoolean(R.bool.pref_side_mdc_default);
					boolean mdc = sp.getBoolean(mdcKey, encodingDef);
				/*	word.setMdC(mdc);*/
					if (mdc) {
						try {
							wordFileDAO.loadImage(word, getResources().getColor(android.R.color.primary_text_light));
						} catch (NotFoundException e) {
							e.printStackTrace();
						} catch (InvalidMdCCodeException e) {
					/*		String errorMdCString = getResources().getString(R.string.words_error_mdc, word.getString());
							word.setWord(errorMdCString);*/
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
