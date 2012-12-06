package com.naens.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WordPair implements Serializable {

	private static final long serialVersionUID = -6986391149586418435L;

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

}
