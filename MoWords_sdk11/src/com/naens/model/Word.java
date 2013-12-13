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

	private int side;

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

	public void setSide(int side) {
		this.side = side;
	}

	public int getSide() {
		return side;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fontName == null) ? 0 : fontName.hashCode());
		result = prime * result + fontSize;
		result = prime * result + ((imageName == null) ? 0 : imageName.hashCode());
		result = prime * result + ((imagePath == null) ? 0 : imagePath.hashCode());
		result = prime * result + ((mdcString == null) ? 0 : mdcString.hashCode());
		result = prime * result + side;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (fontName == null) {
			if (other.fontName != null)
				return false;
		} else if (!fontName.equals(other.fontName))
			return false;
		if (fontSize != other.fontSize)
			return false;
		if (imageName == null) {
			if (other.imageName != null)
				return false;
		} else if (!imageName.equals(other.imageName))
			return false;
		if (imagePath == null) {
			if (other.imagePath != null)
				return false;
		} else if (!imagePath.equals(other.imagePath))
			return false;
		if (mdcString == null) {
			if (other.mdcString != null)
				return false;
		} else if (!mdcString.equals(other.mdcString))
			return false;
		if (side != other.side)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

}
