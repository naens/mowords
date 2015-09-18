package com.naens.dao.androiddao;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.naens.dao.WordFolderDAO;
import com.naens.model.WordFolder;
import com.naens.tools.FontProvider;

public class WordFolderAndroidDAO implements WordFolderDAO {

	public static String root;

	public final static String BUNDLE_ROOT = "com.naens.wordfiles";

	private List <WordFolder> wordFolders;

	@Override
	public List <WordFolder> getFolders () {
		return wordFolders;
	}

	public WordFolderAndroidDAO (String rootPath, Context context) {
		root = rootPath;
		File rootDirectory = new File (root);
		if (!rootDirectory.exists ()) {
			throw new RuntimeException ("root directory doesn't exist: " + root);
		}

		File [] folders = rootDirectory.listFiles ();
		Arrays.sort(folders);
		wordFolders = new LinkedList <WordFolder> ();

		for (File subDir : folders) {
			if (subDir.isDirectory () && !subDir.getName ().equals (FontProvider.FONT_DIR)) { //test if is a directory
				WordFolder wordFolder = new WordFolder (subDir.getName ());
				wordFolder.setWordFiles (new FolderLazyList (wordFolder, context));
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
