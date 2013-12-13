package com.naens.dao.androiddao;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.naens.dao.WordFolderDAO;
import com.naens.model.WordFolder;

public class WordFolderAndroidDAO implements WordFolderDAO {

	public static String root;

	public final static String BUNDLE_ROOT = "com.naens.wordfiles";

	private List <WordFolder> wordFolders;

	@Override
	public List <WordFolder> getFolders () {
		return wordFolders;
	}

	public WordFolderAndroidDAO (String rootPath) {
		root = rootPath;
		File rootDirectory = new File (root);
		if (!rootDirectory.exists ()) {
			throw new RuntimeException ("root directory doesn't exist: " + root);
		}

		File [] folders = rootDirectory.listFiles ();
		Arrays.sort(folders);
		wordFolders = new LinkedList <WordFolder> ();

		for (File subDir : folders) {
			if (subDir.isDirectory () && !subDir.getName ().equals ("fonts")) { //test if is a directory
				WordFolder wordFolder = new WordFolder (subDir.getName ());
				wordFolder.setWordFiles (new FolderLazyList (wordFolder));
				wordFolders.add (wordFolder);
			}
		}
	}

	@Override
	public WordFolder getFolderByName (String folderName) {
		for (WordFolder folder : wordFolders) {
			if (folder.getName ().equals (folderName)) {
				return folder;
			}
		}
		return null;
	}

	public static String getRoot () {
		return root;
	}

}
