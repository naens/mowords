package com.naens.model;

import java.util.List;

public class WordFolder {

	private List <WordFile> wordFiles;

	public List <WordFile> getWordFiles () {
		return wordFiles;
	}

	private String name;

	public String getName () {
		return name;
	}

	public WordFolder (String name) {
		this.name = name;
	}

	public void setWordFiles (List <WordFile> wordFiles) {
		this.wordFiles = wordFiles;
	}

	public WordFile getWordFile (int i) {
		return wordFiles.get (i);
	}

	public void addWordFile(WordFile wf) {
		wordFiles.add (wf);
	}

	public int getSize() {
		return wordFiles.size ();
	}

}
