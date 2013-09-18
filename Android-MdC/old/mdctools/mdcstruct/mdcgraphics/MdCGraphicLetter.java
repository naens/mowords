package com.naens.tools.mdctools.mdcgraphics;

import java.io.UnsupportedEncodingException;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;

public class MdCGraphicLetter {

	private byte [] utf8Char;

	private float scale = 1;

	private int textSize;

	private Path path;

	private RectF rect;

	public MdCGraphicLetter(byte[] utf8Char, float x, float y, float scale, int textSize, Typeface font) {
		super();
		this.utf8Char = utf8Char;
		this.scale = scale;
		this.textSize = textSize;

		Paint paint = new Paint();
		paint.setTextSize(textSize);
		paint.setAntiAlias(true);
		paint.setTypeface(font);

		float width = paint.measureText(toString());
		float height = -paint.ascent() + paint.descent();

		rect = new RectF(x, y, x + width, y + height);
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public byte [] getCharacter () {
		return utf8Char;
	}

	@Override
	public String toString() {
		try {
			return new String(utf8Char, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "bad encoding";
		}
	}

	public RectF getRect () {
		return rect;
	}

	public Path getPath() {
		return path;
	}

	public byte[] getUtf8Char() {
		return utf8Char;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public int getTextSize() {
		return textSize;
	}

	public float getHeight () {
		return scale * rect.height();
	}

	public float getWidth () {
		return scale * rect.width();
	}

}
