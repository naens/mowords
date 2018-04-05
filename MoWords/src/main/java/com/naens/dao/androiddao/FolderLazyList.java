package com.naens.dao.androiddao;

import java.util.List;

import android.content.Context;

import com.naens.model.WordFile;
import com.naens.model.WordFolder;

public class FolderLazyList extends LazyList <WordFile> {

	private static final long serialVersionUID = -8045641263843926223L;

	private WordFolder wordFolder;

	private Context context;

	@Override
	List <WordFile> load () {
		WordFileAndroidDAO fileDAO = new WordFileAndroidDAO (context);
		return fileDAO.getWordFiles (wordFolder);
	}

	public FolderLazyList (WordFolder wordFolder, Context context) {
		this.wordFolder = wordFolder;
		this.context = context;
	}

}
