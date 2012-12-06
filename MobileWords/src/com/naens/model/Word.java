package com.naens.model;


import java.io.Serializable;

import android.graphics.Bitmap;

public class Word implements Serializable {

	private static final long serialVersionUID = -164055807789730718L;

	private String text;

	private WordFont style;

	private transient Bitmap image;

	private String imageName;

	private WordFile wordFile;

	public String getImageName () {
		return imageName;
	}

	public void setImageName (String imageName) {
		this.imageName = imageName;
	}

	public void setImage (Bitmap image) {
		this.image = image;
	}

	public String getText() {
		return text;
	}

	public void setWord(String word) {
		this.text = word;
	}

	public WordFont getStyle() {
		return style;
	}

	public void setStyle(WordFont style) {
		this.style = style;
	}

	public Word(String word, WordFont style) {
		super();
		this.text = word;
		this.style = style;
	}

	public Word (String imageName, WordFile wordFile) {
		super();
		this.imageName = imageName;
		this.wordFile = wordFile;
	}

	public Bitmap getImage () {
		return image;
	}

	public boolean isImage() {
		return imageName != null;
	}

	public WordFile getFile () {
		return wordFile;
	}

}
