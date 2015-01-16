package com.naens.mdctools.mdcstruct;

import com.naens.mdctools.mdcgraphics.MdCFontLetters;

public abstract class MdCElement {

	private boolean root = false;

	protected float scale = 1;

	private MdCLetter aroundLetter;

	private int aroundOrder;

//	public abstract float getUnscaledWidth ();
//
//	public abstract float getUnscaledHeight ();

	//set to whole element and to children
	public abstract void setGraphics (MdCFontLetters mdcFontLetters);

	public boolean isRoot() {
		return root;
	}

	public void setRoot () {
		root = true;
	}

	//and scale children
	public void scale (float scale) {
		this.scale *= scale;
	}
//
//	public float getScaledWidth () {
//		return scale * getUnscaledWidth ();
//	}
//
//	public float getScaledHeight () {
//		return scale * getUnscaledHeight ();
//	}

	public abstract float getWidth ();

	public abstract float getHeight ();

	public float getScale() {
		return scale;
	}

	public MdCLetter getAroundLetter() {
		return aroundLetter;
	}

	public void setAroundLetter(MdCLetter aroundLetter) {
		this.aroundLetter = aroundLetter;
	}

	public void setAroundOrder(int i) {
		aroundOrder = i;
	}

	public int getAroundOrder() {
		return aroundOrder;
	}
	
}
