package com.naens.model;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.naens.tools.FontProvider;

public class Word implements Parcelable {

	private String fontName;

	private int fontSize;

	private transient Bitmap image;

	private int side;

	public enum WordType {TEXT, IMAGE, SOUND, MDC};

	private WordType wordType;

	private String string;

	public void setImage (Bitmap image) {
		this.image = image;
	}

	public Bitmap getImage () {
		return image;
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

	public Word (WordType wordType, String string) {
		this.wordType = wordType;
		this.string = string;
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
		fontName = in.readString();
		fontSize = in.readInt();
		side = in.readInt();
		string = in.readString();
		wordType = WordType.values() [in.readInt()];
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(fontName);
		dest.writeInt(fontSize);
		dest.writeInt(side);
		dest.writeString(string);
		dest.writeInt(wordType.ordinal());
	}

	public void setSide(int side) {
		this.side = side;
	}

	public int getSide() {
		return side;
	}

	public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
	    public Word createFromParcel(Parcel in) {
	        return new Word(in);
	    }

	    public Word[] newArray(int size) {
	        return new Word[size];
	    }
	};

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fontName == null) ? 0 : fontName.hashCode());
		result = prime * result + fontSize;
		result = prime * result + side;
		result = prime * result + ((string == null) ? 0 : string.hashCode());
		result = prime * result + ((wordType == null) ? 0 : wordType.hashCode());
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
		if (side != other.side)
			return false;
		if (string == null) {
			if (other.string != null)
				return false;
		} else if (!string.equals(other.string))
			return false;
		if (wordType != other.wordType)
			return false;
		return true;
	}

	public WordType getWordType() {
		return wordType;
	}

	public String getString() {
		return string;
	}

	public boolean isImage() {
		return wordType.equals(WordType.IMAGE);
	}

	public boolean isMdC() {
		return wordType.equals(WordType.MDC);
	}

	public boolean isSound() {
		return wordType.equals(WordType.SOUND);
	}

	@Override
	public String toString() {
		return "Word [fontName=" + fontName + ", fontSize=" + fontSize + ", side=" + side + ", wordType=" + wordType
				+ ", string=" + string + "]";
	}

	

}
