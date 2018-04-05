package com.naens.wordgame;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.naens.model.Word;
import com.naens.model.WordPair;
import com.naens.tools.ToolUtilities;

@SuppressLint("ParcelCreator")
public class WordGameAgent implements Parcelable {

	private int currentWord;

	private int currentSide;

	private List <WordPair> originalPairs;

	private List <WordPair> wordPairs;

	private SortedSet <Integer> incorrectList = new TreeSet <Integer> ();

	private boolean inverse;

	public WordGameAgent(List <WordPair> wordPairs, boolean inverse) {	//wordPairs not empty!!!
		this.originalPairs = wordPairs;
		this.wordPairs = wordPairs;
		this.inverse = inverse;
		for (int i = 0; i < wordPairs.size (); ++ i) {
			incorrectList.add (i);
		}
		firstWord ();
	}

	public void setCorrect(boolean correct) {
		if (!correct) {
			incorrectList.add (currentWord);
		} else {
			incorrectList.remove (currentWord);
		}
	}

	@SuppressWarnings("unchecked")
	public void endParty() {
		List <WordPair> newPairs = new LinkedList <WordPair>();
		for (int i : incorrectList) {
			newPairs.add (wordPairs.get (i));
		}

		incorrectList.clear ();
		wordPairs = ToolUtilities.randomizeList (newPairs);
		for (int i = 0; i < wordPairs.size (); ++ i) {
			incorrectList.add (i);
		}
		firstWord ();
	}

	public boolean isCorrect () {
		return ! incorrectList.contains (currentWord);
	}

	@SuppressWarnings("unchecked")
	public void flipGame () {
		inverse = ! inverse;
		incorrectList.clear ();
		this.wordPairs = ToolUtilities.randomizeList (originalPairs);
		for (int i = 0; i < wordPairs.size (); ++ i) {
			incorrectList.add (i);
		}
		firstWord ();
	}

	private WordPair firstWord () {
		currentWord = 0;
		currentSide = inverse ? wordPairs.get (currentWord).getSize () - 1 : 0;
		return wordPairs.get (currentWord);
	}

	public WordPair nextWord () {
		currentWord = currentWord >= wordPairs.size () - 1 ? currentWord : currentWord + 1;
		currentSide = inverse ? wordPairs.get (currentWord).getSize () - 1 : 0;
		return wordPairs.get (currentWord);
	}

	public WordPair previousWord () {
		currentWord = currentWord == 0 ? 0 : currentWord - 1;
		currentSide = inverse ? wordPairs.get (currentWord).getSize () - 1 : 0;
		return wordPairs.get (currentWord);
	}

	private Word setSide (int side) {
		currentSide = wordPairs.get (currentWord).hasSide (side) ? side : currentSide;
		return wordPairs.get (currentWord).getWordN (currentSide);
	}

	public Word nextSide () {
		int newSide = inverse ? currentSide - 1 : currentSide + 1;
		return setSide (newSide);
	}

	public Word getSide () {
		return wordPairs.get (currentWord).getWordN (currentSide);
	}

	public Word previousSide () {
		int newSide = inverse ? currentSide + 1 : currentSide - 1;
		currentSide = wordPairs.get (currentWord).hasSide (newSide) ? newSide : currentSide;
		return setSide (newSide);
	}

	public boolean isFirstWord() {
		return currentWord == 0;
	}

	public boolean isLastWord () {
		return currentWord == wordPairs.size () - 1;
	}

	public boolean isFirstSide() {
		return currentSide == (inverse ? wordPairs.get (currentWord).getSize() - 1 : 0) ;
	}

	public boolean isLastSide() {
		return currentSide == (inverse ? 0 : wordPairs.get (currentWord).getSize() - 1);
	}

	public boolean isAllCorrect () {
		return incorrectList.size () == 0;
	}

	public int  getCorrects () {
		return wordPairs.size () - incorrectList.size ();
	}

	public int getCurrent () {
		return currentWord;
	}

	public int getCurrentGameWords () {
		return wordPairs.size ();
	}

	public int getIncorrects () {
		return incorrectList.size ();
	}

	//--parceable
	public WordGameAgent (Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	private void readFromParcel(Parcel in) {

		inverse = in.readByte() == 1;
		List <Integer> incorrectListTMP = new LinkedList<Integer>();
		in.readList(incorrectListTMP, Integer.class.getClassLoader());
		incorrectList = new TreeSet<Integer>(incorrectListTMP);
		in.readList(wordPairs, Integer.class.getClassLoader());
		in.readList(originalPairs, Integer.class.getClassLoader());
		currentSide = in.readInt();
		currentWord = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeInt(currentWord);
		dest.writeInt(currentSide);
		dest.writeList(originalPairs);
		dest.writeList(wordPairs);
		dest.writeList(new LinkedList <Integer> (incorrectList));
		dest.writeByte((byte) (inverse ? 1 : 0));
	}

	public int getCurrentSide() {
		return currentSide;
	}

	public List<WordPair> getGamePairs() {
		return wordPairs;
	}

}
