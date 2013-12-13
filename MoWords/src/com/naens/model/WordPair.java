package com.naens.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

@SuppressWarnings("rawtypes")
public class WordPair implements Parcelable {

	private SparseArray words;

	public Word getWordBySideNumber (int side) {
		//returns word with the side 'side'
		return (Word) words.get (side);
	}

	public int getSize () {
		return words.size ();
	}

	@SuppressWarnings("unchecked")
	public void addWord (Word w) {
		if (words.get(w.getSide()) != null) {
			throw new IllegalStateException("word with side " + w.getSide() + "already exists!");
		}
		words.put (w.getSide(), w);
	}

	public WordPair () {
		words = new SparseArray<Word> ();
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
		words = in.readSparseArray(Word.class.getClassLoader());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSparseArray(words);
	}

	public Word getWordN(int i) {
		return (Word) words.valueAt(i);
	}

}
