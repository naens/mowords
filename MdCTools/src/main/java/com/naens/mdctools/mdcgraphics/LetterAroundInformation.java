package com.naens.mdctools.mdcgraphics;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.SparseArray;

import com.naens.mdctools.MdCToUnicode;
import com.naens.mdctools.MdCTool;
import com.naens.mdctools.mdcstruct.MdCElement;
import com.naens.mdctools.mdcstruct.MdCLetter;

public class LetterAroundInformation {

	//vertical
	private static SparseArray <AroundLetter> aboveRight = new SparseArray<AroundLetter>();
	private static SparseArray <AroundLetter> aboveLeft = new SparseArray<AroundLetter>();
	private static SparseArray <AroundLetter> aboveLeftRight = new SparseArray<AroundLetter>();
	private static SparseArray <AroundLetter> belowRight = new SparseArray<AroundLetter>();
	private static SparseArray <AroundLetter> belowLeft = new SparseArray<AroundLetter>();
	private static SparseArray <AroundLetter> belowLeftRight = new SparseArray<AroundLetter>();

	//horizontal
	private static SparseArray <AroundLetter> beforeTop = new SparseArray<AroundLetter>();
	private static SparseArray <AroundLetter> beforeBottom = new SparseArray<AroundLetter>();
	private static SparseArray <AroundLetter> beforeTopBottom = new SparseArray<AroundLetter>();
	private static SparseArray <AroundLetter> afterTop = new SparseArray<AroundLetter>();
	private static SparseArray <AroundLetter> afterBottom = new SparseArray<AroundLetter>();
	private static SparseArray <AroundLetter> afterTopBottom = new SparseArray<AroundLetter>();
	static {
		//vertical
		addAroundLetter (aboveRight, "I10");
		addAroundLetter (aboveRight, "I11");
		addAroundLetter (aboveRight, "F20");
		addAroundLetter (aboveRight, "F39");
		addAroundLetter (aboveRight, "V22");
		addAroundLetter (aboveRight, "V23");
		addAroundLetter (aboveLeftRight, "F40");
		addAroundLetter (aboveLeftRight, "N1");//?
		addAroundLetter (aboveLeftRight, "N11");
		addAroundLetter (aboveLeftRight, "N12");
		addAroundLetter (aboveLeftRight, "N26");
		addAroundLetter (belowRight, "U1");
		addAroundLetter (belowRight, "U2", 0.3);
		addAroundLetter (belowRight, "D36", 0.1);
		addAroundLetter (belowRight, "D41", 0.5);
		addAroundLetter (belowRight, "D42", 0.5);
		addAroundLetter (belowRight, "F19", 0.2);
		addAroundLetter (belowLeftRight, "D37");
		addAroundLetter (belowLeftRight, "D38");
		addAroundLetter (belowLeftRight, "D39");
		addAroundLetter (belowLeftRight, "D40");
		addAroundLetter (belowLeftRight, "D43");
		addAroundLetter (belowLeftRight, "D44");
		addAroundLetter (belowLeftRight, "D45");
		addAroundLetter (belowLeftRight, "F13");//?

		//horizontal
		addAroundLetter (beforeBottom, "G1");	addAroundLetter (beforeBottom, "G2");	addAroundLetter (beforeBottom, "G4");
		addAroundLetter (beforeBottom, "G5");	addAroundLetter (beforeBottom, "G7");	addAroundLetter (beforeBottom, "G7a");
		addAroundLetter (beforeBottom, "G7b");	addAroundLetter (beforeBottom, "G8");	addAroundLetter (beforeBottom, "G9");
		addAroundLetter (beforeBottom, "G10");	addAroundLetter (beforeBottom, "G11");	addAroundLetter (beforeBottom, "G12");
		addAroundLetter (beforeBottom, "G13");	addAroundLetter (beforeBottom, "G14");	addAroundLetter (beforeBottom, "G17");
		addAroundLetter (beforeBottom, "G18");	addAroundLetter (beforeBottom, "G21");	addAroundLetter (beforeBottom, "G22");
		addAroundLetter (beforeBottom, "G23");	addAroundLetter (beforeBottom, "G25");	addAroundLetter (beforeBottom, "G26");
		addAroundLetter (beforeBottom, "G26a");	addAroundLetter (beforeBottom, "G27");	addAroundLetter (beforeBottom, "G29");
		addAroundLetter (beforeBottom, "G30");	addAroundLetter (beforeBottom, "G33");	addAroundLetter (beforeBottom, "G34");
		addAroundLetter (beforeBottom, "G35");	addAroundLetter (beforeBottom, "G36");	addAroundLetter (beforeBottom, "G37");
		addAroundLetter (beforeBottom, "G38");	addAroundLetter (beforeBottom, "G39");	addAroundLetter (beforeBottom, "G42");
		addAroundLetter (beforeBottom, "G43");	addAroundLetter (beforeBottom, "G44");	addAroundLetter (beforeBottom, "G53");
		addAroundLetter (beforeBottom, "E13");
		addAroundLetter (beforeBottom, "E20");
		addAroundLetter (beforeBottom, "E30");
		addAroundLetter (beforeBottom, "E31");
		addAroundLetter (beforeBottom, "E32");
//		addAroundLetter (beforeBottom, "E103");//doesn't exist in unicode
		addAroundLetter (beforeBottom, "I7");
		addAroundLetter (beforeBottom, "I12");
		addAroundLetter (beforeBottom, "I13");
		addAroundLetter (beforeTop, "M4");
		addAroundLetter (beforeTop, "O14");
		addAroundLetter (beforeTop, "O38");
		addAroundLetter (beforeTop, "S44");
		addAroundLetter (beforeTopBottom, "M5");
		addAroundLetter (beforeTopBottom, "M6");

		addAroundLetter (afterTop, "F12");
		addAroundLetter (afterTop, "M10");
		addAroundLetter (afterTop, "R8");
//		addAroundLetter (afterTop, "R8a");
		addAroundLetter (afterTop, "S39");
		addAroundLetter (afterTop, "T7a");
		addAroundLetter (afterBottom, "D56");
		addAroundLetter (afterBottom, "D58");
		addAroundLetter (afterBottom, "W25");
	}


	private boolean above = false;
	private boolean below = false;
	private boolean before = false;
	private boolean after = false;
	private boolean left = false;
	private boolean right = false;
	private boolean top = false;
	private boolean bottom = false;

	private MdCLetter letter;
	private RectF inRect;
	private List <MdCElement> aroundElements = new LinkedList <MdCElement>();
	private boolean aroundFull = false;
	private AroundLetter aroundLetter;
//	private float insideElementsWidth;

	public LetterAroundInformation(MdCLetter letter, MdCFontLetters mdcFontLetters) {
		this.letter = letter;
		int codepoint = letter.getCodePoint();
		aroundLetter = initAround(codepoint, letter.getRotate(), letter.isFlip());
		if (aroundLetter == null) {
			return;
		}
		int letterWidth = mdcFontLetters.getCharacterWidth(codepoint, letter.getRotate());
		int letterHeight = mdcFontLetters.getCharacterHeight(codepoint, letter.getRotate());
		int startX = 0;
		int startY = 0;
		Bitmap bmp = mdcFontLetters.getBitmap(codepoint, 1, letter.getRotate(), letter.isFlip());
		Double hint = aroundLetter.getAroundHint();
		if (isAroundVertical()) {
			startX = hint != null ? (int)(hint * letterWidth) - 1 : (isAroundLeftRight() ? letterWidth / 2 : isLeft() ? letterWidth - 1 : 0);
			startY = above ? letterHeight - 1 : 0;
		}
		if (isAroundHorizontal()) {
			startX = before ? letterWidth - 1 : 0;
			startY = hint != null ? (int)(hint * letterHeight) - 1 : (isAroundTopBottom() ? letterHeight / 2 : isTop() ? letterHeight - 1 : 0);
		}
		Rect r = MdCTool.findInsideRect(bmp, startX, startY, Color.TRANSPARENT);
		if (r != null && (isAroundVertical() || isAroundHorizontal())) {
			inRect = new RectF (r);
		}
	}

	private AroundLetter initAround(int codepoint, int rotate, boolean flip) {
		AroundLetter result = null;
		//set vertical values
		if (aboveRight.get(codepoint) != null) {
			result = aboveRight.get(codepoint);
			above = true;
			right = true;
		}
		if (aboveLeft.get(codepoint) != null) {
			result = aboveLeft.get(codepoint);
			above = true;
			left = true;
		}
		if (aboveLeftRight.get(codepoint) != null) {
			result = aboveLeftRight.get(codepoint);
			above = true;
			left = true;
			right = true;
		}
		if (belowRight.get(codepoint) != null) {
			result = belowRight.get(codepoint);
			below = true;
			right = true;
		}
		if (belowLeft.get(codepoint) != null) {
			result = belowLeft.get(codepoint);
			below = true;
			left = true;
		}
		if (belowLeftRight.get(codepoint) != null) {
			result = belowLeftRight.get(codepoint);
			below = true;
			left = true;
			right = true;
		}

		//set horizontal values
		if (beforeTop.get(codepoint) != null) {
			result = beforeTop.get(codepoint);
			before = true;
			top = true;
		}
		if (beforeBottom.get(codepoint) != null) {
			result = beforeBottom.get(codepoint);
			before = true;
			bottom = true;
		}
		if (beforeTopBottom.get(codepoint) != null) {
			result = beforeTopBottom.get(codepoint);
			before = true;
			top = true;
			bottom = true;
		}
		if (afterTop.get(codepoint) != null) {
			result = afterTop.get(codepoint);
			after = true;
			top = true;
		}
		if (afterBottom.get(codepoint) != null) {
			result = afterBottom.get(codepoint);
			after = true;
			bottom = true;
		}
		if (afterTopBottom.get(codepoint) != null) {
			result = afterTopBottom.get(codepoint);
			after = true;
			top = true;
			bottom = true;
		}

		//flip
		if (flip) {
			boolean tmp = right;
			right = left;
			left = tmp;

			tmp = after;
			after = before;
			before = tmp;
		}

		//rotate
		if (rotate % 360 == 90) {
			boolean tmp = left;
			left = bottom;
			bottom = right;
			right = top;
			top = tmp;

			tmp = above;
			above = before;
			before = below;
			below = after;
			after = tmp;
		}
		if (rotate % 360 == 180) {
			boolean tmp = above;
			above = below;
			below = tmp;

			tmp = right;
			right = left;
			left = tmp;

			tmp = after;
			after = before;
			before = tmp;

			tmp = top;
			top = bottom;
			bottom = tmp;
		}
		if (rotate % 360 == 270) {
			boolean tmp = left;
			left = top;
			top = right;
			right = bottom;
			bottom = tmp;

			tmp = above;
			above = after;
			after = below;
			below = before;
			before = tmp;
		}
		return result;
	}

	public float getAroundLetterPartHeight() {
		return letter.getHeight() - inRect.height();
	}

	public float getAroundLetterPartWidth() {
		float letterWidth = letter.getWidth();
		float inrectWidth = inRect.width();
		return letterWidth - inrectWidth;
	}

	public float getAroundVerticalShiftX() {
		float sign = isLeft() ? +1 : -1;
		if (getAroundInsideWidth() < getInsideElementsWidth()) {
			return sign * (letter.getWidth() /*- insideElementsWidth */+ MdCGraphicConverter.H_GAP - getAroundInsideWidth()) / 2;
		} else {
			if (isAroundLeftRight()) {
				return (inRect.left - (letter.getWidth() - inRect.right)) / 2;
			} else {
				return sign * (letter.getWidth() - getAroundInsideWidth()) / 2;
			}
		}
	}

	public float getAroundVerticalLetterShiftX() {
		float sign = isLeft() ? +1 : -1;
		float insideElementsWidth = getInsideElementsWidth();
		if (getAroundInsideWidth() < insideElementsWidth ) {
			return sign * -(insideElementsWidth + MdCGraphicConverter.H_GAP - getAroundInsideWidth()) / 2;
		} else {
			return 0;
		}
	}

//	public float getInsideElementsWidth () {
//		return insideElementsWidth;
//	}

	public float getAroundVerticalShiftY() {
		return above ? inRect.top: 0;
	}

	public void addAroundElement(MdCElement e) {
		e.setAroundLetter (letter);
		aroundElements.add(e);
		e.setAroundOrder(aroundElements.size() - 1);
//		if (isAroundVertical()) {
//			if (e.getWidth() > insideElementsWidth) {
//				insideElementsWidth = e.getWidth();
//			}
//		} else {
//			insideElementsWidth += e.getWidth();
//		}
	}

	public float getInsideElementsWidth() {
		float totWidth = 0;
		float maxWidth = 0;
		for (MdCElement e : aroundElements) {
			totWidth += e.getWidth();
			if (e.getWidth() > totWidth) {
				totWidth = e.getWidth();
			}
		}
		if (isAroundVertical()) {
			return maxWidth;
		} else {
			return totWidth;
		}
//		return insideElementsWidth;
	}

	public float getAroundInsideWidth() {
//		int hgaps = inRect.left > 0 && inRect.right > 0 ? 2 : 1; 
		return inRect.width();
	}

	public float getAroundLeftInsideWidth() {
		return inRect.left;
	}

	public float getAroundInsideHeight() {
//		int vgaps = inRect.top > 0 && inRect.bottom > 0 ? 2 : 1; 
		return inRect.height();
	}

	public void setAroundFull(boolean aroundFull) {
		this.aroundFull = aroundFull;
	}

	public boolean isAroundFull() {
		return aroundFull && aroundElements.size() > 0;
	}

	public boolean isAroundEmpty() {
		return aroundFull && aroundElements.size() == 0;
	}

	public boolean isAroundVertical() {
		return above || below;
	}

	public boolean isAroundHorizontal() {
		return before || after;
	}

	public boolean isAroundYBefore() {
		return above;
	}

	public boolean isAroundXBefore() {
		return before;
	}

	public boolean isAroundTopBottom() {
		return top & bottom;
	}

	public boolean isAroundLeftRight() {
		return left & right;
	}

//	public void setInsideElementsWidth(float width) {
//		this.insideElementsWidth = width;
//	}

	public boolean isAroundYAfter() {
		return below;
	}

	public boolean isAroundXAfter() {
		return after;
	}

	public float getAroundVGap() {
		if (aroundElements.size() == 0) {
			return MdCGraphicConverter.V_GAP + letter.aroundInfo().getAroundInsideHeight();
		}
		if (aroundFull) {
			return MdCGraphicConverter.V_GAP;
		}
		float sumH = 0;
		for (MdCElement e : aroundElements) {
			sumH += e.getHeight();
		}
		if (aroundElements.size() == 1) {
			return (getAroundInsideHeight() - sumH) / 2;
		}
		return (getAroundInsideHeight() - sumH) / aroundElements.size();
	}

	public float getAroundHGap() {
		if (isAroundFull() || isAroundEmpty()) {
			return MdCGraphicConverter.H_GAP;
		}
		return (getAroundInsideWidth() - getInsideElementsWidth()) / (aroundElements.size() + 1);
	}

	public void scale(float scale) {
		if (inRect != null) {
			inRect.bottom *= scale;
			inRect.left *= scale;
			inRect.right *= scale;
			inRect.top *= scale;
		}
//		insideElementsWidth *= scale;
	}

	public RectF getInRect() {
		return inRect;
	}

	public boolean isLeft () {
		return left && !right;
	}

	public boolean isRight() {
		return right && !left;
	}

	public boolean isTop () {
		return top && !bottom;
	}

	public boolean isBottom () {
		return bottom && !top;
	}

	private static void addAroundLetter(SparseArray<AroundLetter> array, String mdc) {
		int codepoint = MdCToUnicode.getCode(mdc);
		AroundLetter aroundLetter = new AroundLetter(codepoint);
		array.append(codepoint, aroundLetter);
	}

	private static void addAroundLetter(SparseArray<AroundLetter> array, String mdc, double aroundHint) {
		int codepoint = MdCToUnicode.getCode(mdc);
		AroundLetter aroundLetter = new AroundLetter(codepoint, aroundHint);
		array.append(codepoint, aroundLetter);
	}

	static class AroundLetter {
		private int codepoint;
		private Double aroundHint;
		public AroundLetter(int codepoint) {
			this.codepoint = codepoint;
		}
		public AroundLetter(int codepoint, double aroundHint) {
			this.codepoint = codepoint;
			this.aroundHint = aroundHint;
		}
		public Integer getCodepoint() {
			return codepoint;
		}
		public Double getAroundHint() {
			return aroundHint;
		}
		@Override
		public boolean equals(Object o) {
			if (o instanceof Integer) {
				return o.equals(codepoint);
			}
			return super.equals(o);
		}
	}
	/*
	 * 

	public float getAroundHGap() {
		if (isAroundFull() || isAroundEmpty()) {
			return MdCGraphicConverter.H_GAP;
		}
		return (getAroundInsideWidth() - insideElementsWidth) / (aroundElements.size() + 1);
	}
	 */

	public int getNumberOfElements() {
		return aroundElements.size();
	}

	public float getTop() {
		return inRect.top;
	}

}
