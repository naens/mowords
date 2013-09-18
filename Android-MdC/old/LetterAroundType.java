package com.naens.tools.mdctools;

import java.util.Arrays;


public class LetterAroundType {

	//vertical
	public static AroundLetter [] aboveRight = {
		new AroundLetter("I10"),
		new AroundLetter("I11"),
		new AroundLetter("F20"),
		new AroundLetter("F39")};
	public static AroundLetter [] aboveLeft = {};
	public static AroundLetter [] aboveLeftRight = {
		new AroundLetter("F40"),
		new AroundLetter("N1"),//?
		new AroundLetter("N11"),
		new AroundLetter("N12"),};
	public static AroundLetter [] belowRight = {
		new AroundLetter("U1"),
		new AroundLetter("U2"),
		new AroundLetter("D36"),
		new AroundLetter("D41"),
		new AroundLetter("D42"),
		new AroundLetter("D45")};
	public static AroundLetter [] belowLeft = {};
	public static AroundLetter [] belowLeftRight = {
		new AroundLetter("D37"),
		new AroundLetter("D38"),
		new AroundLetter("D39"),
		new AroundLetter("D40"),
		new AroundLetter("D43"),
		new AroundLetter("D44"),
		new AroundLetter("F13")};//?

	//horizontal
	public static AroundLetter [] beforeTop = {};
	public static AroundLetter [] beforeBottom = {};
	public static AroundLetter [] beforeTopBottom = {};
	public static AroundLetter [] afterTop = {};
	public static AroundLetter [] afterBottom = {};
	public static AroundLetter [] afterTopBottom = {};

	private boolean above = false;
	private boolean below = false;
	private boolean before = false;
	private boolean after = false;
	private boolean left = false;
	private boolean right = false;
	private boolean top = false;
	private boolean bottom = false;
	
	public LetterAroundType(int codepoint, int rotate, boolean flip) {
		//set vertical values
		if (Arrays.asList(aboveRight).contains(codepoint)) {
			above = true;
			right = true;
		}
		if (Arrays.asList(aboveLeft).contains(codepoint)) {
			above = true;
			left = true;
		}
		if (Arrays.asList(aboveLeftRight).contains(codepoint)) {
			above = true;
			left = true;
			right = true;
		}
		if (Arrays.asList(belowRight).contains(codepoint)) {
			below = true;
			right = true;
		}
		if (Arrays.asList(belowLeft).contains(codepoint)) {
			below = true;
			left = true;
		}
		if (Arrays.asList(belowLeftRight).contains(codepoint)) {
			below = true;
			left = true;
			right = true;
		}

		//set horizontal values
		if (Arrays.asList(beforeTop).contains(codepoint)) {
			before = true;
			top = true;
		}
		if (Arrays.asList(beforeBottom).contains(codepoint)) {
			before = true;
			bottom = true;
		}
		if (Arrays.asList(beforeTopBottom).contains(codepoint)) {
			before = true;
			top = true;
			bottom = true;
		}
		if (Arrays.asList(afterTop).contains(codepoint)) {
			after = true;
			top = true;
		}
		if (Arrays.asList(afterBottom).contains(codepoint)) {
			after = true;
			bottom = true;
		}
		if (Arrays.asList(afterTopBottom).contains(codepoint)) {
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
	}

	public boolean isAbove () {
		return above;
	}

	public boolean isBelow () {
		return below;
	}

	public boolean isBefore () {
		return before;
	}

	public boolean isAfter () {
		return after;
	}

	public boolean isLeft () {
		return left && !right;
	}

	public boolean isRight () {
		return right && !left;
	}

	public boolean isLeftRight () {
		return left & right;
	}

	public boolean isTop () {
		return top && !bottom;
	}

	public boolean isBottom () {
		return bottom && !top;
	}

	public boolean isTopBottom () {
		return top & bottom;
	}

	static class AroundLetter {
		private Integer codepoint;
		private Float aroundHint;
		public AroundLetter(String mdc) {
			codepoint = MdCToUnicode.getCode(mdc);
		}
		public AroundLetter(String mdc, float aroundHint) {
			this(mdc);
			this.aroundHint = aroundHint;
		}
		public Integer getCodepoint() {
			return codepoint;
		}
		public float getAroundHint() {
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

}
