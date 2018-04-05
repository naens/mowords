package com.naens.dao.androiddao;

import java.util.List;

import android.content.Context;

import com.naens.model.WordFile;
import com.naens.model.WordPair;

public class FileLazyList extends LazyList <WordPair> {

	private static final long serialVersionUID = 6145523731446173434L;

	private WordFile wordFile;

	private Context context;

	public FileLazyList (WordFile wordFile, Context context) {
		this.context = context;
		this.wordFile = wordFile;
	}

	@Override
	List <WordPair> load () {
		WordFileAndroidDAO fileDAO = new WordFileAndroidDAO (context);
		return fileDAO.getWordPairs (wordFile);
	}
}
