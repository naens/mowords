package com.naens.mobilewords;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
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

public class WordsActivity extends Activity implements WordGui
//, SimpleGestureListener 
{

	private static final String WORGD_GAME = "com.naens.mobilewords.WORGD_GAME";
	private static final String PAUSED = "com.naens.mobilewords.PAUSED";

	private WordGame wg;

	//	private boolean finished = false;

	//	private SimpleGestureFilter detector;
	private WordFont defaultFont;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_words);

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
		LinearLayout layout = (LinearLayout) findViewById (R.id.words_word_layout);
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

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void displayWord (Word word, boolean correct) {
		TextView view = (TextView) findViewById (R.id.words_text_view);
		LinearLayout layout = (LinearLayout) findViewById (R.id.words_word_layout);
		if (word.isImage ()) { //display image
			if (word.getImage () == null) {
				MainActivity.getWordFileDAO ().loadImage (word);
			}
			if (word.getImage () == null) { //no image
				Log.i ("TAG", String.format ("no image: '%s'", word.getImageName ()));
				view.setText (String.format ("no image: '%s'", word.getImageName ()));
				view.setTextSize (36);
				view.setTypeface (Typeface.create ("Times", Typeface.ITALIC));
				view.setCompoundDrawables (null, null, null, null);
				layout.setBackgroundResource (0);
			} else {
				Log.i ("TAG", String.format ("image"));
				view.setText ("");
				int border = 20;
				int width = layout.getWidth () - border;
				int height = layout.getHeight () - border;
				if (layout.getWidth () == 0) { //no size available
					int below = 50; //guess status
					Display display = getWindowManager ().getDefaultDisplay ();
					if (android.os.Build.VERSION.SDK_INT >= 13) {
						Point size = new Point ();
						display.getSize (size);
						width = size.x - border;
						height = size.y - border - below;
					} else {
						width = display.getWidth () - border;
						height = display.getHeight () - border - below;
					}
				}
				Bitmap bitmap = ToolUtilities.updateSize (word.getImage (), width, height);
				Drawable img = new BitmapDrawable (null, bitmap);

				img.setBounds (0, 0, bitmap.getWidth (), bitmap.getHeight ());
				view.setCompoundDrawables (img, null, null, null);
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
			view.setText (word.getText ());
			view.setCompoundDrawables (null, null, null, null);
			view.setTextSize (wordFont.getSize ());
			view.setTypeface (t);
		}
		Button nextButton = (Button) findViewById (R.id.words_next_word_button);
		if (wg.isLastWord ()) {
			nextButton.setText (getResources ().getString (R.string.words_word_finish_button_text));
		} else {
			nextButton.setText (getResources ().getString (R.string.words_next_word_button_text));
		}
		displayCorrect (correct);
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
		TextView view = (TextView) findViewById (R.id.words_text_view);
		view.setCompoundDrawables (null, null, null, null);
		view.setText ("Paused");
		view.setTextSize (36);
		view.setTypeface (Typeface.create ("Times", Typeface.ITALIC));
		LinearLayout layout = (LinearLayout) findViewById (R.id.words_word_layout);
		layout.setBackgroundResource (0);
	}

	@Override
	public void displayBlank () {
		TextView view = (TextView) findViewById (R.id.words_text_view);
		view.setCompoundDrawables (null, null, null, null);
		view.setText ("");
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
		String message = String.format ("Game 1 (%d/%d=%.1f%s): %s", correct1, total, ((double) correct1 * 100) / total, "%",
				ToolUtilities.timeListToString (firstTimes));
		if (secondTimes.size () > 0) {
			int correct2 = secondCorr.get (0);
			message += String.format ("\nGame 2 (%d/%d=%.1f%s): %s", correct2, total, ((double) correct2 * 100) / total, "%",
					ToolUtilities.timeListToString (secondTimes));
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
		LinearLayout layout = (LinearLayout) findViewById (R.id.words_word_layout);
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

	//	@Override
	//	public void gameEnded () {
	//		finished = true;
	//	}

	//	@Override
	//	public void onSwipe (int direction) {
	//		switch (direction) {
	//			case SimpleGestureFilter.SWIPE_RIGHT:
	//				wg.previousWord ();
	//				break;
	//			case SimpleGestureFilter.SWIPE_LEFT:
	//				wg.nextWord ();
	//				break;
	//			case SimpleGestureFilter.SWIPE_DOWN:
	//				wg.previousSide ();
	//				break;
	//			case SimpleGestureFilter.SWIPE_UP:
	//				wg.nextSide ();
	//				break;
	//		}
	//	}

	//	@Override
	//	public void onDoubleTap () {
	//		LinearLayout layout = (LinearLayout) findViewById (R.id.words_word_layout);
	//		Toast.makeText (this, String.format ("%d x %d", layout.getWidth (), layout.getHeight ()), Toast.LENGTH_SHORT).show ();
	//	}

	public void onLongClick () {
		wg.pause ();
	}

	@Override
	public boolean dispatchTouchEvent (MotionEvent ev) {
		//		this.detector.onTouchEvent (ev);
		return super.dispatchTouchEvent (ev);
	}

	@Override
	protected void onPause () {
		if (!wg.isPaused () && !isFinishing ()) {
			wg.pause ();
		}
		super.onStop ();
	}

	public class ActivitySwipeDetector implements View.OnTouchListener {

		static final String logTag = "ActivitySwipeDetector";
		@SuppressWarnings("unused")
		private Activity activity;
		static final int MIN_DISTANCE = 100;
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
					if (Math.abs (deltaX) > MIN_DISTANCE) {
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

}
