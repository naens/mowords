package com.naens.dao.androiddao;

import java.util.List;

import com.naens.model.WordFile;
import com.naens.model.WordFolder;

public class FolderLazyList extends LazyList <WordFile> {

	private static final long serialVersionUID = -8045641263843926223L;

	private WordFolder wordFolder;

	@Override
	List <WordFile> load () {
		WordFileAndroidDAO fileDAO = new WordFileAndroidDAO ();
		return fileDAO.getWordFiles (wordFolder);
	}

	public FolderLazyList (WordFolder wordFolder) {
		this.wordFolder = wordFolder;
	}

}
