package com.naens.dao.androiddao;

import java.util.List;

import com.naens.model.WordFile;
import com.naens.model.WordPair;

public class FileLazyList extends LazyList <WordPair> {

	private static final long serialVersionUID = 6145523731446173434L;

	private WordFile wordFile;

	public FileLazyList (WordFile wordFile) {
		this.wordFile = wordFile;
	}

	@Override
	List <WordPair> load () {
		WordFileAndroidDAO fileDAO = new WordFileAndroidDAO ();
		return fileDAO.getWordPairs (wordFile);
	}
}
