package com.naens.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

@SuppressWarnings("rawtypes")
public class WordPair implements Parcelable {

	private SparseArray <Word> words;

	public Word getWordBySideNumber (int side) {
		//returns word with the side 'side'
		return (Word) words.get (side);
	}

	public int getSize () {
		return words.size ();
	}

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
		int size = in.readInt();
		words = new SparseArray<Word> (size);
		for (int i = 0; i < size; ++i) {
			int key = in.readInt();
			Word word = in.readParcelable(Word.class.getClassLoader());
			words.put(key, word);
		}
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(words.size());
		for (int i = 0; i < words.size(); ++i) {
			int key = words.keyAt(i);
			Word word = words.get(key);
			dest.writeInt(key);
			dest.writeParcelable(word, flags);
		}
	}

	public Word getWordN(int i) {
		return (Word) words.valueAt(i);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
	    public WordPair createFromParcel(Parcel in) {
	        return new WordPair(in);
	    }

	    public WordPair[] newArray(int size) {
	        return new WordPair[size];
	    }
	};

	public SparseArray<Word> getWords() {
		return words;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words.size(); ++ i) {
			int key = words.keyAt(i);
			Word word = words.get(key);
			String ws = word == null ? "null" : word.toString();
			sb.append(String.format("{key:%d, value:%s}", i, ws));
		}
		return "WordPair [words=" + sb + "]";
	}

}
