package com.naens.tools.mdctools.mdcstruct;

import com.naens.tools.mdctools.mdcgraphics.MdCFontLetters;

public abstract class MdCElement {

	private boolean root = false;

	protected float scale = 1;

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
	
}
