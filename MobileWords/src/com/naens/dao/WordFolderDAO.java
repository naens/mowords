package com.naens.dao;

import java.util.List;

import com.naens.model.WordFolder;

public interface WordFolderDAO {

	public List<WordFolder> getFolders();

	public WordFolder getFolderByName(String folderName);

}
