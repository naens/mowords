package com.naens.model;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.naens.dao.androiddao.WordFolderAndroidDAO;
import com.naens.tools.FontProvider;

public class Word implements Parcelable {

	private String text;

	private String fontName;

	private int fontSize;

	private transient Bitmap image;

	private String imageName;

	private String mdcString;

	private String imagePath;

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
		imageName = null;
		mdcString = null;
		image = null;
	}

	public Word(String word) {
		super();
		this.text = word;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize == 0 ? FontProvider.getDefaultSize() : fontSize;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public Word (String imageName, WordFile wordFile, boolean isMdC) {
		super();
		if (isMdC) {
			mdcString = imageName;
		} else {
			this.imageName = imageName;
			String folderName = wordFile.getFolder().getName();
			String fileName = wordFile.getName();
			imagePath = WordFolderAndroidDAO.getRoot() + "/"+ folderName + "/images/" + fileName + "/" + imageName;
		}
	}

	public boolean isMdC () {
		return mdcString != null;
	}

	public Bitmap getImage () {
		return image;
	}

	public boolean isImage() {
		return imageName != null || mdcString != null;
	}

	public String getMdCString() {
		return mdcString;
	}

	public String getImagePath() {
		return imagePath;
	}

	//--parceable
	public Word (Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	private void readFromParcel(Parcel in) {
		imagePath =  in.readString();
		mdcString = in.readString();
		imageName = in.readString();
		fontSize = in.readInt();
		fontName = in.readString();
		text = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(text);
		dest.writeString(fontName);
		dest.writeInt(fontSize);
		dest.writeString(imageName);
		dest.writeString(mdcString);
		dest.writeString(imagePath);
	}

}
