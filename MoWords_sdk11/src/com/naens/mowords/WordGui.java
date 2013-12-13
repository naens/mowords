package com.naens.mowords;

import java.util.List;

import com.naens.model.Word;
import com.naens.wordgame.WordGame;

public interface WordGui {

	public void displayWord(Word word, boolean correct);

	public void setGameController (WordGame wordGame);

	public void displayTime (int time);

	public void displayPause ();

	public void displayBlank ();

	public void askSecondSide (List <Integer> firstSideTimes, List <Integer> firstSideCorrects);		//First side finished. Ask if go second Side

	public void askContinueGame (int incorrects);		//Party finished. Ask continue incorrect words

	public void displayStats (List <Integer> firstTimes, List <Integer> firstCorr, List <Integer> secondTimes, List <Integer> secondCorr, int totalWords);

	public void displayCorrect (boolean correct);

	public void displayStatus (int current, int numberWords);

	public void nextWord();

	public void previousWord();

//	public void gameEnded ();

}
