package com.naens.wordgame;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import com.naens.model.WordPair;
import com.naens.mowords.WordGui;
import com.naens.tools.ToolUtilities;

public class WordGame implements Parcelable {

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

	private boolean gameActive;

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
		gameActive = true;
	}

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
				gui.nextWord ();
				gui.displayWord (agent.getSide (), agent.isCorrect ());
				gui.displayStatus (agent.getCurrent (), agent.getCurrentGameWords ());
			}
		}
	}

	public void previousWord () {
		if (timerRunning) {
			agent.previousWord ();
			gui.previousWord ();
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
		gameActive = false;
		if (isFisrtSide) {
			firstSideTimes.add (timeValue);
			firstSideCorrects.add (agent.getCorrects ());
		} else {
			secondSideTimes.add (timeValue);
			secondSideCorrects.add (agent.getCorrects ());
		}
		timerStop ();
		gui.displayBlank ();
		if (agent.isAllCorrect ()) {
			// first side & ask continue ? go second side. otherwise: finish
			if (!isLastGame ()) {
				gui.askSecondSide (firstSideTimes, firstSideCorrects);
			} else {
				//write to log
				gui.displayStats (firstSideTimes, firstSideCorrects, secondSideTimes, secondSideCorrects, total, false);
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
		gameActive = true;
	}

	public void askSecondSideNo () {
		doSecondSide = false;
		gui.displayStats (firstSideTimes, firstSideCorrects, secondSideTimes, secondSideCorrects, total, true);
		finishGame ();
//		gui.gameEnded ();
	}

	public void askContinueGameYes () {
		timeValue = 0;
		agent.endParty ();
		startTimer ();
		gui.displayWord (agent.getSide (), agent.isCorrect ());
		gui.displayStatus (agent.getCurrent (), agent.getCurrentGameWords ());
		gameActive = true;
	}

	public void askContinueGameNo () {
		doSecondSide = false;
		gui.displayStats (firstSideTimes, firstSideCorrects, secondSideTimes, secondSideCorrects, total, true);
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

	public boolean isFirstWord() {
		return agent.isFirstWord ();
	}

	public boolean isFirstSide() {
		return agent.isFirstSide ();
	}

	public boolean isLastSide() {
		return agent.isLastSide ();
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
			firstSideTimes.remove (location);
			firstSideCorrects.remove (location);
		} else {
			int location = secondSideTimes.size () - 1;
			secondSideTimes.remove (location);
			secondSideCorrects.remove (location);
		}
		recreate (false);
		gameActive = true;
	}
	//--parceable
	public WordGame (Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	private void readFromParcel(Parcel in) {
		total = in.readInt();
		in.readList (secondSideCorrects, Integer.class.getClassLoader());
		in.readList (firstSideCorrects, Integer.class.getClassLoader());
		in.readList (secondSideTimes, Integer.class.getClassLoader());
		in.readList (firstSideTimes, Integer.class.getClassLoader());
		timeValue = in.readInt();
		boolean [] booleanArray = new boolean [4];
		timerRunning = booleanArray [0];
		isFisrtSide = booleanArray [1];
		oneDirection = booleanArray [2];
		doSecondSide = booleanArray [3];
		in.readBooleanArray(booleanArray);
		agent = in.readParcelable(WordGameAgent.class.getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(agent, 0);
		boolean [] booleanArray = {timerRunning, isFisrtSide, oneDirection, doSecondSide};
		dest.writeBooleanArray(booleanArray);
		dest.writeInt(timeValue);
		dest.writeList(firstSideTimes);
		dest.writeList(secondSideTimes);
		dest.writeList(firstSideCorrects);
		dest.writeList(secondSideCorrects);
		dest.writeInt(total);
	}

	public int getSide() {
		return agent.getCurrentSide();
	}

	public static final Parcelable.Creator<WordGame> CREATOR = new Creator<WordGame>() {

	    public WordGame createFromParcel(Parcel source) {

	        return new WordGame(source);
	    }

	    public WordGame[] newArray(int size) {

	        return new WordGame[size];
	    }

	};

	public List<WordPair> getPairs() {
		return agent.getGamePairs ();
	}

	public int getCurrentWordNumber() {
		return agent.getCurrent();
	}

	public boolean isGameActive() {
		return gameActive;
	}

}
