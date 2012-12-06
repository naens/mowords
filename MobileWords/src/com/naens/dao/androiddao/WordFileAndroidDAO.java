package com.naens.dao.androiddao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

import com.naens.dao.WordFileDAO;
import com.naens.model.Word;
import com.naens.model.WordFile;
import com.naens.model.WordFolder;
import com.naens.model.WordFont;
import com.naens.model.WordPair;

public class WordFileAndroidDAO implements WordFileDAO {

	@Override
	public List <WordFile> getWordFiles (WordFolder wordFolder) {

		List <WordFile> l = new ArrayList <WordFile> ();

		File subDir = new File (String.format ("%s/%s", WordFolderAndroidDAO.getRoot (), wordFolder.getName ()));
		if (subDir.isDirectory ()) {
			File [] files = subDir.listFiles ();
			for (File f : files) {
				if (f.getName ().endsWith (".txt")) {
					WordFile wordFile = new WordFile (wordFolder, f.getName ().replace (".txt", ""));
					wordFile.setWordPairs (new FileLazyList (wordFile));
					l.add (wordFile);
				}
			}
		}
		return l;
	}

	@Override
	public List <WordPair> getWordPairs (WordFile wordFile) {
		WordFolder wordFolder = wordFile.getFolder ();

		List <WordPair> list = new ArrayList <WordPair> ();
		SparseArray <WordFont> styles = new ConfigurationAndroidDAO ().getStyles (wordFile.getFolder ());

		String filename = String.format ("%s/%s/%s.txt", WordFolderAndroidDAO.getRoot (), wordFolder.getName (), wordFile.getName ());
		File f = new File (filename);
		try {
			FileInputStream inputStream = new FileInputStream (f);
			BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (inputStream));
			String fileLine = "";
			while ((fileLine = bufferedReader.readLine ()) != null) {
				fileLine = fileLine.trim ();
				if (fileLine.length () > 0) {
					WordPair wp = new WordPair ();
					String [] words = fileLine.split ("\\t");
					for (int i = 0; i < words.length; i++) {
						String word = words[i].trim ();
						if (word.length () > 0) {
//							if (word.matches ("^\\[image:.*\\]$")) {
							if (word.contains ("[image:")) {
								String imageName = word.split ("image:")[1].split ("\\]$")[0];
								wp.addWord (new Word (imageName, wordFile));
							} else {
								wp.addWord (new Word (word, styles.get (i)));
							}
						}
					}
					list.add (wp);
				}
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace ();
			throw new RuntimeException (e.getMessage ());
		}
	}

	@Override
	public int getFileSize (WordFile wordFile) {
		WordFolder wordFolder = wordFile.getFolder ();
		int count = 0;
		String filename = String.format ("%s/%s/%s.txt", WordFolderAndroidDAO.getRoot (), wordFolder.getName (), wordFile.getName ());
		File f = new File (filename);
		try {
			FileInputStream inputStream = new FileInputStream (f);
			BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (inputStream));
			String fileLine = "";
			while ((fileLine = bufferedReader.readLine ()) != null) {
				if (fileLine.trim ().length () > 0) {
					++count;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException (e.getMessage ());
		}
		return count;
	}

	/**
	 * word.word.isImage () == true
	 */
	@Override
	public void loadImage (Word word) {
		WordFile wf = word.getFile ();

		String setName = wf.getFolder ().getName ();
		String listName = wf.getName ();
		String imageName = word.getImageName ();
		String imageFullName = WordFolderAndroidDAO.getRoot () + "/" + setName + "/images" + "/" + listName + "/" + imageName;
		try {
			InputStream imageStream = new FileInputStream (imageFullName);
			Bitmap bitmap = BitmapFactory.decodeStream (imageStream);
			word.setImage (bitmap);
		} catch (FileNotFoundException e) {
//			Log.e("TAG", e.getStackTrace ().toString ());
			word.setImage (null);
		}

	}
}
