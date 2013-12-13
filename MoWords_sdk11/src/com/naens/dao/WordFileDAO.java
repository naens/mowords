package com.naens.dao;

import java.util.List;

import com.naens.model.Word;
import com.naens.model.WordFile;
import com.naens.model.WordFolder;
import com.naens.model.WordPair;

public interface WordFileDAO {

	public List <WordFile> getWordFiles (WordFolder wf);

	public List <WordPair> getWordPairs (WordFile wf);

	public int getFileSize (WordFile wordFile);

	public void loadImage(Word word, int color);

}
