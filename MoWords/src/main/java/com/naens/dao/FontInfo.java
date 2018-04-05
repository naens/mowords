package com.naens.dao;

public class FontInfo {

	private int fontSize;
	private String fontName;

	public FontInfo(String fontName, int fontSize) {
		super();
		this.fontSize = fontSize;
		this.fontName = fontName;
	}
	public int getFontSize() {
		return fontSize;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	public String getFontName() {
		return fontName;
	}
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
}
