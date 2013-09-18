package com.naens.mowords;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naens.model.Word;
import com.naens.model.WordFont;
import com.naens.model.WordPair;
import com.naens.tools.ToolUtilities;
import com.naens.wordgame.WordGame;

public class WordsActivity extends Activity implements WordGui {

	private static final String WORGD_GAME = "com.naens.mowords.WORGD_GAME";
	private static final String PAUSED = "com.naens.mowords.PAUSED";

	private WordGame wg;

	//	private boolean finished = false;

	//	private SimpleGestureFilter detector;
	private WordFont defaultFont;
	private LinearLayout layout;
	private TextView wordView;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_words);
		layout = (LinearLayout) findViewById (R.id.words_word_layout);
		wordView = (TextView) findViewById (R.id.words_text_view);

		//		detector = new SimpleGestureFilter (this, this);
		ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector (this);

		defaultFont = new WordFont ("Palatino Linotype", 36);

		Intent intent = getIntent ();
		if (savedInstanceState == null) { //initialize new game
			wg = new WordGame ();
			boolean inverse = intent.getBooleanExtra (FolderActivity.WORDS_INVERSE, false);
			boolean oneDirection = intent.getBooleanExtra (FolderActivity.WORDS_ONE_DIRECTION, false);
			int limit = intent.getIntExtra (FolderActivity.WORDS_LIMIT, 25);
			List <WordPair> words = MainActivity.chosenWords;
			//			@SuppressWarnings("unchecked")
			//			List <WordPair> words = (List <WordPair>) intent.getSerializableExtra (FolderActivity.WORDS_WORD_PAIRS);
			wg.setGui (this);

			wg.startWordGame (words, limit, inverse, oneDirection);
		}
		/*
		layout.setOnLongClickListener (new OnLongClickListener () {

			@Override
			public boolean onLongClick (View v) {
				WordsActivity.this.onLongClick ();
				return true;
			}
		});*/

		layout.setOnTouchListener (activitySwipeDetector);
	}

	@Override
	public void onSaveInstanceState (Bundle savedInstanceState) {
		savedInstanceState.putSerializable (WORGD_GAME, wg);
		savedInstanceState.putBoolean (PAUSED, wg.isPaused ());
	}

	@Override
	public void onRestoreInstanceState (Bundle savedInstanceState) {
		//load saved game
		wg = (WordGame) savedInstanceState.getSerializable (WORGD_GAME);
		boolean paused = savedInstanceState.getBoolean (PAUSED);
		wg.setGui (this);
		Log.i ("TAG", String.format ("onRestoreInstanceState, paused = " + paused));
		wg.recreate (paused);
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.activity_words, menu);
		return true;
	}

	public void wordClicked (View view) {
		wg.flipCorrect ();
	}

	public void pauseClicked (View view) {
		wg.pause ();
	}

	public void previousWordClicked (View view) {
		wg.previousWord ();
	}

	public void nextWordClicked (View view) {
		wg.nextWord ();
	}

	public void previousSideClicked (View view) {
		wg.previousSide ();
	}

	public void nextSideClicked (View view) {
		wg.nextSide ();
	}

	@SuppressLint("NewApi")
	private Word wordToDisplay;
	private Typeface defaultItalic = Typeface.create ("Times", Typeface.ITALIC);
	@Override
	public void displayWord (Word word, boolean correct) {

		wordToDisplay = word;
//		wordView = (TextView) findViewById (R.id.words_text_view);
		if (word.isImage ()) { //display image
			if (word.getImage () == null) {
				MainActivity.getWordFileDAO ().loadImage (word);
			}
			if (word.getImage () == null) { //no image

				displayText(String.format ("no image: '%s'", word.getImageName ()), Color.BLACK, 36, defaultItalic );
	/*			wordView.setText (String.format ("no image: '%s'", word.getImageName ()));
				wordView.setTextSize (36);
				wordView.setTypeface (Typeface.create ("Times", Typeface.ITALIC));
				wordView.setCompoundDrawables (null, null, null, null);
				wordView.setPadding(0, 0, 0, 0);
				layout.setBackgroundResource (0);*/
			} else {
				wordView.post(new Runnable() {
					
					@Override
					public void run() {
						wordView.setText ("");
						Bitmap bmp = wordToDisplay.getImage ();
						WordsActivity.this.displayImage(bmp);
		/*				Drawable d = new BitmapDrawable(null, bmp);
						d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
						wordView.setCompoundDrawables(d, null, null, null);
						wordView.setPadding(wordView.getWidth() / 2 - bmp.getWidth() / 2, 0, 0, 0);
						wordView.setText("");*/
//						Drawable img = new BitmapDrawable (null, wordToDisplay.getImage ());
/*
			Drawable d = new BitmapDrawable(null, bmp);
 			Bitmap bmp = wordToDisplay.getImage ();
			d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
			wordView.setCompoundDrawables(d, null, null, null);
			wordView.setPadding(textView.getWidth() / 2 - bmp.getWidth() / 2, 0, 0, 0);
			wordView.setText("");
 */
//						img.setBounds (0, 0, wordToDisplay.getImage ().getWidth (), wordToDisplay.getImage ().getHeight ());
//						wordView.setCompoundDrawables (null, img, null, null);

//						int border = 20;
//						int width = layout.getWidth () - border;
//						int height = layout.getHeight () - border;
//						Bitmap bitmap = ToolUtilities.updateSize (wordToDisplay.getImage (), width, height);
//						Drawable img = new BitmapDrawable (null, bitmap);
	//					Drawable img = new BitmapDrawable (null, wordToDisplay.getImage ());

//						img.setBounds (0, 0, bitmap.getWidth (), bitmap.getHeight ());
		//				wordView.setCompoundDrawables (img, null, null, null);
					}
				});
			}

		} else { //display word
			WordFont wordFont = word.getStyle ();
			Typeface t = null;
			if (wordFont == null) {
				wordFont = defaultFont;
				t = Typeface.create (wordFont.getName (), Typeface.NORMAL);
			} else { //check file fonts
				t = MainActivity.getConfigurationDAO ().getFonts ().get (wordFont.getName ());
				if (t == null) {
					t = Typeface.create (wordFont.getName (), Typeface.NORMAL);
				}
			}

			displayText(word.getText (), Color.BLACK, wordFont.getSize (), t );
/*			wordView.setText (word.getText ());
			wordView.setCompoundDrawables (null, null, null, null);
			wordView.setPadding(0, 0, 0, 0);
			wordView.setTextSize (wordFont.getSize ());
			wordView.setTypeface (t);
			layout.setBackgroundResource (0);*/
		}
		Button nextButton = (Button) findViewById (R.id.words_next_word_button);
		if (wg.isLastWord ()) {
			nextButton.setText (getResources ().getString (R.string.words_word_finish_button_text));
		} else {
			nextButton.setText (getResources ().getString (R.string.words_next_word_button_text));
		}
		displayCorrect (correct);
	}

	private void displayImage (Bitmap bmp) {
		Drawable d = new BitmapDrawable(null, bmp);
		d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
		wordView.setCompoundDrawables(d, null, null, null);
		wordView.setPadding(wordView.getWidth() / 2 - bmp.getWidth() / 2, 0, 0, 0);
		wordView.setText("");
		wordView.setBackgroundResource(R.drawable.wordview_background);
	}

	private void displayText (String text, int color, int size, Typeface typeface) {
		wordView.setText (text);
		wordView.setCompoundDrawables (null, null, null, null);
		wordView.setPadding(0, 0, 0, 0);
		wordView.setTextSize (size);
		wordView.setTypeface (typeface);
	}

	@Override
	public void setGameController (WordGame wordGame) { // not needed
	}

	@Override
	public void displayTime (int time) {
		TextView timeView = (TextView) findViewById (R.id.words_time_view);
		timeView.setText (String.format ("%02d:%02d", time / 60, time % 60));
	}

	@Override
	public void displayPause () {
		wordView.setCompoundDrawables (null, null, null, null);
		wordView.setText ("Paused");
		wordView.setTextSize (36);
		wordView.setTypeface (Typeface.create ("Times", Typeface.ITALIC));
		layout.setBackgroundResource (0);
	}

	@Override
	public void displayBlank () {
		wordView.setCompoundDrawables (null, null, null, null);
		wordView.setText ("");
	}

	@Override
	public void askSecondSide (List <Integer> firstSideTimes, List <Integer> firstSideCorrects) {
		String message = "Done.\n" + ToolUtilities.timeListToString (firstSideTimes) + "\nDo you want to continue in the other direction?";
		AlertDialog.Builder alertbox = new AlertDialog.Builder (this);
		alertbox.setMessage (message);
		alertbox.setPositiveButton ("Yes", new DialogInterface.OnClickListener () {
			public void onClick (DialogInterface arg0, int arg1) {
				wg.askSecondSideYes ();
			}
		});

		alertbox.setNegativeButton ("No", new DialogInterface.OnClickListener () {
			public void onClick (DialogInterface arg0, int arg1) {
				wg.askSecondSideNo ();
			}
		});
		alertbox.setOnCancelListener (new OnCancelListener () {
			public void onCancel (DialogInterface dialog) {
				wg.resume ();
			}
		});
		alertbox.show ();
	}

	@Override
	public void askContinueGame (int incorrects) {
		String message = incorrects + " words are wrong. Do you want to continue?";

		AlertDialog.Builder alertbox = new AlertDialog.Builder (this);
		alertbox.setMessage (message);
		alertbox.setPositiveButton ("Yes", new DialogInterface.OnClickListener () {
			public void onClick (DialogInterface arg0, int arg1) {
				wg.askContinueGameYes ();
			}
		});

		alertbox.setNegativeButton ("No", new DialogInterface.OnClickListener () {
			public void onClick (DialogInterface arg0, int arg1) {
				wg.askContinueGameNo ();
			}
		});
		alertbox.setOnCancelListener (new OnCancelListener () {
			public void onCancel (DialogInterface dialog) {
				wg.resume ();
			}
		});
		alertbox.show ();
	}

	@Override
	public void displayStats (List <Integer> firstTimes, List <Integer> firstCorr, List <Integer> secondTimes, List <Integer> secondCorr,
			int total) {
		int correct1 = firstCorr.get (0);
		String message = String.format ("Game 1 (%d/%d=%.1f%s)", correct1, total, ((double) correct1 * 100) / total, "%");
		if (secondTimes.size () > 0) {
			int correct2 = secondCorr.get (0);
			message += String.format ("\nGame 2 (%d/%d=%.1f%s)", correct2, total, ((double) correct2 * 100) / total, "%");
			List <Integer> allTimes = new LinkedList <Integer> ();
			allTimes.add (ToolUtilities.sumList (firstTimes));
			allTimes.add (ToolUtilities.sumList (secondTimes));
			message += "\nTotal: " + ToolUtilities.timeListToString (allTimes);
		}

		AlertDialog.Builder alertbox = new AlertDialog.Builder (this);
		alertbox.setMessage (message);
		alertbox.setNeutralButton ("Ok", new DialogInterface.OnClickListener () {
			public void onClick (DialogInterface arg0, int arg1) {
				if (wg.isLastGame ()) {
					finish ();
				}
			}
		});
		alertbox.setOnCancelListener (new OnCancelListener () {
			public void onCancel (DialogInterface dialog) {
				wg.resume ();
			}
		});
		alertbox.show ();
	}

	@Override
	public void displayCorrect (boolean correct) {
		if (correct) {
			layout.setBackgroundResource (R.drawable.words_word_border);
		} else {
			layout.setBackgroundResource (0);
		}
	}

	@Override
	public void displayStatus (int current, int numberWords) {
		((TextView) findViewById (R.id.words_status_view)).setText ("word " + (current + 1) + " of " + numberWords);
	}

	public void onLongClick () {
		wg.pause ();
	}

	@Override
	public boolean dispatchTouchEvent (MotionEvent ev) {
		return super.dispatchTouchEvent (ev);
	}

	@Override
	protected void onPause () {
		if (!wg.isPaused () && !isFinishing ()) {
			wg.pause ();
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

		public ActivitySwipeDetector (Activity activity) {
			this.activity = activity;
		}

		public void onRightToLeftSwipe () {
			Log.i (logTag, "RightToLeftSwipe!");
			wg.nextWord ();
		}

		public void onLeftToRightSwipe () {
			Log.i (logTag, "LeftToRightSwipe!");
			// activity.doSomething();
			wg.previousWord ();
		}

		public void onTopToBottomSwipe () {
			Log.i (logTag, "onTopToBottomSwipe!");
			// activity.doSomething();
			wg.previousSide ();
		}

		public void onBottomToTopSwipe () {
			Log.i (logTag, "onBottomToTopSwipe!");
			// activity.doSomething();
			wg.nextSide ();
		}

		private void onLongClick () {
			WordsActivity.this.onLongClick();
		}

		public boolean onTouch (View v, MotionEvent event) {
			
			switch (event.getAction ()) {
				case MotionEvent.ACTION_DOWN: {
					downX = event.getX ();
					downY = event.getY ();
					begin = event.getEventTime ();
					return false;
				}
				case MotionEvent.ACTION_UP: {
//					Log.i (logTag, "time: " + (event.getEventTime () - begin));
					upX = event.getX ();
					upY = event.getY ();

					float deltaX = downX - upX;
					float deltaY = downY - upY;

					// swipe horizontal?
					if (Math.abs (deltaX) > MIN_DISTANCE && Math.abs (deltaY) < Math.abs (deltaX)) {
						// left or right
						if (deltaX < 0) {
							this.onLeftToRightSwipe ();
							return true;
						}
						if (deltaX > 0) {
							this.onRightToLeftSwipe ();
							return true;
						}
					} else {
						Log.i (logTag, "Swipe (x) was only " + Math.abs (deltaX) + " long, need at least " + MIN_DISTANCE);
					}
					// swipe vertical?
					if (Math.abs (deltaY) > MIN_DISTANCE) {
						// top or down
						if (deltaY < 0) {
							this.onTopToBottomSwipe ();
							return true;
						}
						if (deltaY > 0) {
							this.onBottomToTopSwipe ();
							return true;
						}
					} else {
						Log.i (logTag, "Swipe (y) was only " + Math.abs (deltaX) + " long, need at least " + MIN_DISTANCE);
					}
					if (event.getEventTime () - begin > LONG_CLICK_TIME) {
						this.onLongClick ();
						return true;
					}

					return false;
				}
			}
			return false;
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
/*		if (wordToDisplay != null && wordToDisplay.isImage()) {
//			int w = newConfig.screenWidthDp;
//			int h = newConf
			Log.i ("TAG", String.format ("image"));
			wordView.setText ("");
			int border = 20;
			int width = layout.getWidth () - border;
			int height = layout.getHeight () - border;
			wordView.post(new Runnable() {
				
				@Override
				public void run() {

					int border = 20;
					int width = layout.getWidth () - border;
					int height = layout.getHeight () - border;
					Bitmap bitmap = ToolUtilities.updateSize (wordToDisplay.getImage (), width, height);
					Drawable img = new BitmapDrawable (null, bitmap);

					img.setBounds (0, 0, bitmap.getWidth (), bitmap.getHeight ());
					wordView.setCompoundDrawables (img, null, null, null);
				}
			});
			Toast.makeText(this, "onConfigurationChanged() : "  + width + ":" + height, Toast.LENGTH_SHORT).show();
		}
	//	displayWord(wordToDisplay, correct);*/
	}

}
