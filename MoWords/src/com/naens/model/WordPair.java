package com.naens.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class WordPair implements Parcelable {

	private List <Word> words;

	public Word getWord (int side) {
		return words.get (side);
	}

	public int getSize () {
		return words.size ();
	}

	public void addWord (Word w) {
		words.add (w);
	}

	public WordPair () {
		words = new ArrayList <Word> ();
	}

	public boolean hasSide (int side) {
		return (side >= 0 && side < words.size ());
	}

	//--parceable
	public WordPair (Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	private void readFromParcel(Parcel in) {
		in.readList(words, Word.class.getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(words);
	}

}
