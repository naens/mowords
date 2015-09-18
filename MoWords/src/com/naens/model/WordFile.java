package com.naens.model;

import java.util.List;

public class WordFile {

	private List <WordPair> wordPairs;

	public List <WordPair> getWordPairs () {
		return wordPairs;
	}

	private String name;

	private WordFolder folder;

	public WordFolder getFolder () {
		return folder;
	}

	public String getName () {
		return name;
	}

	public WordPair getWordPair (int i) {
		return wordPairs.get (i);
	}

	public void addWordPair (WordPair wp) {
		wordPairs.add (wp);
	}

	public WordFile (WordFolder folder, String name) {
		this.folder = folder;
		this.name = name;
	}

	public void setWordPairs (List <WordPair> wordPairs) {
		this.wordPairs = wordPairs;
	}

	public int getSize() {
		return wordPairs.size ();
	}

}
