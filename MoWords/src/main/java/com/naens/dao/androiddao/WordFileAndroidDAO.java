package com.naens.dao.androiddao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.naens.dao.ConfigurationDAO;
import com.naens.dao.WordFileDAO;
import com.naens.mdctools.InvalidMdCCodeException;
import com.naens.mdctools.MdCTool;
import com.naens.model.Word;
import com.naens.model.WordFile;
import com.naens.model.WordFolder;
import com.naens.model.WordPair;
import com.naens.tools.FontProvider;

public class WordFileAndroidDAO implements WordFileDAO {

	private ConfigurationDAO configurationDAO = null;

	private ConfigurationDAO getConfigurationDAO () {
		if (configurationDAO == null) {
			configurationDAO = new ConfigurationAndroidPrefDAO (context);
		}
		return configurationDAO;
	}

	private Context context;

	public WordFileAndroidDAO(Context context) {
		this.context = context;
	}

	@Override
	public List<WordFile> getWordFiles(WordFolder wordFolder) {

		List<WordFile> l = new ArrayList<WordFile>();

		File subDir = new File(String.format("%s/%s", WordFolderAndroidDAO.getRoot(), wordFolder.getName()));
		if (subDir.isDirectory()) {
			File[] files = subDir.listFiles();
			Arrays.sort(files);
			for (File f : files) {
				if (f.getName().endsWith(".txt")) {
					WordFile wordFile = new WordFile(wordFolder, f.getName().replace(".txt", ""));
					wordFile.setWordPairs(new FileLazyList(wordFile, context));
					l.add(wordFile);
				}
			}
		}
		return l;
	}

	static String stringToHex(String s) {
		String result = " ";
		for (char c : s.toCharArray()) {
			result += Integer.toHexString(c) + " ";
		}
		return result;
	}

	@Override
	public List<WordPair> getWordPairs(WordFile wordFile) {
		WordFolder wordFolder = wordFile.getFolder();
		String folderName = wordFolder.getName();

		List<WordPair> list = new ArrayList<WordPair>();
		ConfigurationDAO configurationDAO = getConfigurationDAO ();

		String filename = String.format("%s/%s/%s.txt", WordFolderAndroidDAO.getRoot(), folderName, wordFile.getName());
		File f = new File(filename);
		try {
	        Pattern pattern = Pattern.compile("\\\\n");

			FileInputStream inputStream = new FileInputStream(f);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String fileLine = "";
			while ((fileLine = bufferedReader.readLine()) != null) {
				fileLine = fileLine.replaceAll("[\uFEFF\uFFFF]", "");
				if (fileLine.length() > 0) {
					WordPair wp = new WordPair();
					String[] words = fileLine.split("\\t");
//					Log.i("TAG", "sides: " + words.length);
					for (int i = 0; i < words.length; i++) {
						String word = words[i].trim();
						if (word.length() == 0) {
//							Log.i("TAG", "word.length() == 0");
						}
						if (word.length() > 0) {
							if (word.contains("[image:")) {
								String imageName = word.split("image:")[1].split("\\]$")[0];
								String folderPath = WordFolderAndroidDAO.getRoot() + "/"+ folderName;
								String imagePath1 = folderPath + "/images/" + wordFile.getName() + "/" + imageName;
								String imagePath2 = folderPath + "/images/" + imageName;
						        Word w = new Word(Word.WordType.IMAGE, new File(imagePath1).exists() ?  imagePath1 : imagePath2);
						        w.setSide (i + 1);
								wp.addWord(w);
							} else if (word.contains("[sound:")) {
								String imageName = word.split("sound:")[1].split("\\]$")[0];
								String folderPath = WordFolderAndroidDAO.getRoot() + "/"+ folderName;
								String imagePath1 = folderPath + "/sounds/" + wordFile.getName() + "/" + imageName;
								String imagePath2 = folderPath + "/sounds/" + imageName;
						        Word w = new Word(Word.WordType.SOUND, new File(imagePath1).exists() ?  imagePath1 : imagePath2);
						        w.setSide (i + 1);
								wp.addWord(w);
							} else if (configurationDAO.isMdCSide(folderName, i + 1)) {
								Word mdcWord = new Word(Word.WordType.MDC, word);
								mdcWord.setFontName(configurationDAO.getSideFontName(folderName, i + 1));
								mdcWord.setFontSize(Integer.parseInt(configurationDAO.getSideFontSize(folderName, i + 1)));
								mdcWord.setSide (i + 1);
								wp.addWord(mdcWord);
							} else {
						        Matcher matcher = pattern.matcher(word);
						        Word w = new Word(Word.WordType.TEXT, matcher.replaceAll("\\\n"));
						        w.setFontName(configurationDAO.getSideFontName(folderName, i + 1));
						        w.setFontSize(Integer.parseInt(configurationDAO.getSideFontSize(folderName, i + 1)));
								w.setSide (i + 1);
						        wp.addWord(w);
							}
						}
					}
					if (wp.getSize() > 0) {
						list.add(wp);
					}
				}
			}
			bufferedReader.close();
			return list;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public int getFileSize(WordFile wordFile) {
		WordFolder wordFolder = wordFile.getFolder();
		int count = 0;
		String filename = String.format("%s/%s/%s.txt",
				WordFolderAndroidDAO.getRoot(), wordFolder.getName(),
				wordFile.getName());
		File f = new File(filename);
		try {
			FileInputStream inputStream = new FileInputStream(f);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String fileLine = "";
			while ((fileLine = bufferedReader.readLine()) != null) {
				if (fileLine.trim().length() > 0) {
					++count;
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return count;
	}

	/**
	 * word.isImage () == true
	 * @throws InvalidMdCCodeException 
	 */
	@Override
	public void loadImage (Word word, int color) throws InvalidMdCCodeException {
		switch (word.getWordType()) {
			case IMAGE:
				try {
					InputStream imageStream = new FileInputStream(word.getString ());
					Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
					word.setImage(bitmap);
				} catch (FileNotFoundException e) {
					word.setImage(null);
				}
				break;
			case MDC:
				if (word.getFontName() == null) {
					word.setFontName("Gardiner");
				}
				Bitmap bmp = MdCTool.mdcToBitmap(
						FontProvider.getFont(word.getFontName(), context),
						word.getString(),
						word.getFontSize(),
						color);
				word.setImage(bmp);
				break;
			default:
				throw new RuntimeException("load image only if word mdc or image");
		}
	}

}
