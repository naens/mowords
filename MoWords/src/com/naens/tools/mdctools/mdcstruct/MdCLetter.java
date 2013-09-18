package com.naens.tools.mdctools.mdcstruct;

import android.graphics.Bitmap;

import com.naens.tools.mdctools.mdcgraphics.LetterAroundInformation;
import com.naens.tools.mdctools.mdcgraphics.MdCFontLetters;

public class MdCLetter extends MdCElement {

	private int codePoint;

	private int rotate;

	private MdCFontLetters mdcFontLetters;

	private float x;

	private float y;

	private boolean flip;

	private LetterAroundInformation aroundInformation;

	public MdCLetter(int codePoint, int rotate) {
		this.codePoint = codePoint;
		this.rotate = rotate;
	}

	public int getRotate() {
		return rotate;
	}

	public int getCodePoint () {
		return codePoint;
	}

	@Override
	public void setGraphics (MdCFontLetters mdcFontLetters) {
		this.mdcFontLetters = mdcFontLetters;
		aroundInformation = new LetterAroundInformation(this, mdcFontLetters);
	}

	@Override
	public float getWidth() {
		return scale * mdcFontLetters.getCharacterWidth(codePoint, rotate);
	}

	@Override
	public float getHeight() {
		return scale * mdcFontLetters.getCharacterHeight(codePoint, rotate);
	}

	public float getX () {
		return x;
	}

	public float getY () {
		return y;
	}

	public void setX (float x) {
		this.x = x;
	}

	public void setY (float y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return Integer.toHexString(codePoint);
//		return Integer.toHexString(codePoint & 0xff);
	}

	public Bitmap getBitmap() {
		return mdcFontLetters.getBitmap(codePoint, scale, rotate, flip);
	}

	public void setFlip(boolean flip) {
		this.flip = flip;
	}

	public boolean isFlip() {
		return flip;
	}

	public void setRotate(int rotate) {
		this.rotate = rotate;
	}

	public LetterAroundInformation aroundInfo() {
		return aroundInformation;
	}

	@Override
	public void scale(float scale) {
		super.scale(scale);
		if (aroundInformation != null) {
			aroundInformation.scale (scale);
		}
	}

}
