package com.naens.tools.mdctools.mdcgraphics;


public class MdCGraphicLetterInfo {

	private String symbol;

	private float scale = 1;

	private MdCFontLetters mdcFontLetters;

	private float x;

	private float y;

	private int codePoint;

	public MdCGraphicLetterInfo(MdCFontLetters mdcFontLetters, int codePoint) {
		this.codePoint = codePoint;
		this.mdcFontLetters = mdcFontLetters;

		char[] charPair = Character.toChars(codePoint);
		symbol = new String(charPair);
	}

	@Override
	public String toString() {
		;
		return "{" + symbol + "}";
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public void scale(float scale) {
		this.scale *= scale;
	}

	public float getHeight() {
		return scale * mdcFontLetters.getCharacterHeight(codePoint);
	}

	public float getWidth() {
		return scale * mdcFontLetters.getCharacterWidth(codePoint);
	}
}
