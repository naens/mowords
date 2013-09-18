package com.naens.tools.mdctools.mdcstruct;

import com.naens.tools.mdctools.mdcgraphics.MdCFontLetters;
import com.naens.tools.mdctools.mdcgraphics.MdCGraphicLetterInfo;

public class MdCLetter extends MdCElement {

	private int codePoint;

	private MdCGraphicLetterInfo info = null;

	public MdCLetter(int codePoint) {
		this.codePoint = codePoint;
	}

	public int getCodePoint () {
		return codePoint;
	}

	public void setGraphics (MdCFontLetters mdcFontLetters) {
		info = new MdCGraphicLetterInfo(mdcFontLetters, codePoint);
	}

	public MdCGraphicLetterInfo getInfo() {
		return info;
	}

	@Override
	public float getWidth() {
		return info.getWidth();
	}

	@Override
	public float getHeight() {
		return info.getHeight();
	}
}
