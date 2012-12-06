package com.naens.wordgame;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import com.naens.mobilewords.WordGui;
import com.naens.model.WordPair;
import com.naens.tools.ToolUtilities;

public class WordGame implements Serializable {

	private static final long serialVersionUID = -2810102531607617129L;

	private transient WordGui gui;

	private WordGameAgent agent;

	private transient Timer timer;

	private boolean timerRunning = false;

	int timeValue = 0;

	private boolean isFisrtSide;

	private List <Integer> firstSideTimes;

	private List <Integer> secondSideTimes;

	private List <Integer> firstSideCorrects;

	private List <Integer> secondSideCorrects;

	private boolean oneDirection;

	private boolean doSecondSide;

	private int total;

//	private boolean active;

	public WordGame () {
		firstSideTimes = new LinkedList <Integer> ();
		firstSideCorrects = new LinkedList <Integer> ();
		secondSideTimes = new LinkedList <Integer> ();
		secondSideCorrects = new LinkedList <Integer> ();
//		active = false;
	}

	/**
	 * !!!gui != null
	 * 
	 * @param wordWraps
	 * @param limit
	 * @param inverse
	 * @param oneDirection 
	 */
	@SuppressWarnings("unchecked")
	public void startWordGame (List <WordPair> wordWraps, int limit, boolean inverse, boolean oneDirection) {
		if (gui == null) {
			throw new RuntimeException ("gui is null");
		}
		this.oneDirection = oneDirection;
		doSecondSide = !oneDirection;
		agent = new WordGameAgent (ToolUtilities.randomizeList (wordWraps, limit), inverse);
		total = agent.getCurrentGameWords ();
		startTimer ();
		isFisrtSide = true;
//		active = true;
		gui.displayWord (agent.getSide (), agent.isCorrect ());
		gui.displayStatus (agent.getCurrent (), agent.getCurrentGameWords ());
	}

//	public boolean isActive () {
//		return active;
//	}

	public void pause () {
		if (timerRunning) {
			timerStop ();
			gui.displayPause ();
		} else {
			startTimer ();
			gui.displayWord (agent.getSide (), agent.isCorrect ());
		}
	}

	public void nextWord () {
		if (timerRunning) {
			if (agent.isLastWord ()) {
				endParty ();
			} else {
				agent.nextWord ();
				gui.displayWord (agent.getSide (), agent.isCorrect ());
				gui.displayStatus (agent.getCurrent (), agent.getCurrentGameWords ());
			}
		}
	}

	public void previousWord () {
		if (timerRunning) {
			agent.previousWord ();
			gui.displayWord (agent.getSide (), agent.isCorrect ());
			gui.displayStatus (agent.getCurrent (), agent.getCurrentGameWords ());
		}
	}

	public void nextSide () {
		if (timerRunning) {
			gui.displayWord (agent.nextSide (), agent.isCorrect ());
		}
	}

	public void previousSide () {
		if (timerRunning) {
			gui.displayWord (agent.previousSide (), agent.isCorrect ());
		}
	}

	public void endParty () {
		if (isFisrtSide) {
			firstSideTimes.add (timeValue);
			firstSideCorrects.add (agent.getCorrects ());
			Log.i ("TAG", "add1 " + timeValue);
		} else {
			secondSideTimes.add (timeValue);
			secondSideCorrects.add (agent.getCorrects ());
			Log.i ("TAG", "add2 " + timeValue);
		}
		timerStop ();
		gui.displayBlank ();
		if (agent.isAllCorrect ()) {
			// first side & ask continue ? go second side. otherwise: finish
			if (!isLastGame ()) {
				gui.askSecondSide (firstSideTimes, firstSideCorrects);
			} else {
				gui.displayStats (firstSideTimes, firstSideCorrects, secondSideTimes, secondSideCorrects, total);
//				finishGame ();
//				gui.gameEnded ();
			}
		} else {
			// ask continue
			gui.askContinueGame (agent.getIncorrects ());
		}

	}

	public void askSecondSideYes () {
		timerStop ();
		timeValue = 0;
		isFisrtSide = false;
		agent.flipGame ();
		startTimer ();
		gui.displayWord (agent.getSide (), agent.isCorrect ());
		gui.displayStatus (agent.getCurrent (), agent.getCurrentGameWords ());

	}

	public void askSecondSideNo () {
		doSecondSide = false;
		gui.displayStats (firstSideTimes, firstSideCorrects, secondSideTimes, secondSideCorrects, total);
		finishGame ();
//		gui.gameEnded ();
	}

	public void askContinueGameYes () {
		timeValue = 0;
		agent.endParty ();
		startTimer ();
		gui.displayWord (agent.getSide (), agent.isCorrect ());
		gui.displayStatus (agent.getCurrent (), agent.getCurrentGameWords ());
	}

	public void askContinueGameNo () {
		doSecondSide = false;
		gui.displayStats (firstSideTimes, firstSideCorrects, secondSideTimes, secondSideCorrects, total);
		finishGame ();
//		gui.gameEnded ();
	}

	private void finishGame () {
		timeValue = 0;
//		active = false;
		firstSideTimes.clear ();
		firstSideCorrects.clear ();
		secondSideTimes.clear ();
		secondSideCorrects.clear ();
	}

	public void flipCorrect () {
		if (timerRunning) {
			agent.setCorrect (!agent.isCorrect ());
			gui.displayCorrect (agent.isCorrect ());
		}
	}

	public WordGui getGui () {
		return gui;
	}

	public void setGui (WordGui gui) {
		gui.setGameController (this);
		this.gui = gui;
	}

	public boolean isLastWord () {
		return agent.isLastWord ();
	}

	private void timerStop () {
		timerRunning = false;
		timer.cancel ();
		timer.purge ();
	}

	private void startTimer () {
		timerRunning = true;
		timer = new Timer ();
		timer.schedule (new GameTimerTask (), 1000, 1000);
	}

	public boolean isPaused () {
		return /*active && */!timerRunning;
	}

	private class GameTimerTask extends TimerTask {

		private Handler h = new Handler (new Callback () {

			@Override
			public boolean handleMessage (Message msg) {
				gui.displayTime (timeValue);
				return false;
			}
		});

		@Override
		public void run () {
			if (timerRunning) {
				h.sendEmptyMessage (0);
				++timeValue;
			}
		}

	}

	public void recreate (boolean paused) {
		timerRunning = !paused;
		if (isPaused ()) {
			gui.displayPause ();
		} else {
			startTimer ();
			gui.displayWord (agent.getSide (), agent.isCorrect ());
			gui.displayCorrect (agent.isCorrect ());
		}
		gui.displayStatus (agent.getCurrent (), agent.getCurrentGameWords ());
		gui.displayTime (timeValue);
	}

	public boolean isLastGame () {
		return oneDirection || !doSecondSide || !isFisrtSide;
	}

	public void resume () {
		if (isFisrtSide) {
			int location = firstSideTimes.size () - 1;
			Log.i ("TAG", "remove1 (" + location + ") = " + firstSideTimes.get (location));
			firstSideTimes.remove (location );
			firstSideCorrects.remove (location);
		} else {
			int location = secondSideTimes.size () - 1;
			Log.i ("TAG", "remove2 (" + location + ") = " + secondSideTimes.get (location));
			secondSideTimes.remove (location);
			secondSideCorrects.remove (location);
		}
		recreate (false);
	}

}
