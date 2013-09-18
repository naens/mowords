package com.naens.tools.mdctools.mdcstruct;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.naens.tools.mdctools.MdCToUnicode;
import com.naens.tools.mdctools.MdCTool;
import com.naens.tools.mdctools.mdcgraphics.MdCFontLetters;

public class MdCCartouche extends MdCElement {

	public enum CartoucheType {CARTOUCHE(MdCToUnicode.getCode("V10")), HWT(MdCToUnicode.getCode("O6")),
		SEREKH(MdCToUnicode.getCode("O6"), MdCToUnicode.getCode("O33")), ENCLOSURE(MdCToUnicode.getCode("O6"));
		int codepoint;
		int codepoint2;
		CartoucheType (int codepoint) {
			this.codepoint = codepoint;
		}
		CartoucheType (int codepoint, int codepoint2) {
			this.codepoint = codepoint;
			this.codepoint2 = codepoint2;
		}
	}

	private MdCElement element;

	private MdCFontLetters mdcFontLetters;

	private Rect inRect;

	private Rect outRect;

	private float y;

	private float x;

	private CartoucheType cartoucheType;

	private int rotate;

	private float k = 1;

//	private int codePoint;

	public MdCCartouche(MdCElement element) {
		this(element, CartoucheType.CARTOUCHE);
	}

	public MdCCartouche(MdCElement element, CartoucheType cartoucheType) {
		this.element = element;
		this.cartoucheType = cartoucheType != null ? cartoucheType : CartoucheType.CARTOUCHE;
//		try {
//			codePoint = MdCToUnicode.getCode("V10");
//		} catch (InvalidMdCCodeException e) {
//			throw new RuntimeException(e);
//		}
	}

	public MdCElement getElement() {
		return element;
	}

	public void setElement(MdCElement element) {
		this.element = element;
	}

	@Override
	public float getWidth() {
		float tmpElementScale = 1;
		if (element.getWidth() > 0) {
//		if (element instanceof MdCLetter) {
			tmpElementScale = element.getHeight() > inRect.height() * scale ?
					scale * ((float)inRect.height()) / element.getHeight() : 1;
		}
		int bordersWidth = outRect.width() - inRect.width();
		float elementWidth = element.getWidth() * tmpElementScale;
		if (elementWidth < inRect.width() * scale) {
			return outRect.width() * scale;
		}
		return bordersWidth * scale + elementWidth;
	}

	@Override
	public float getHeight() {
//		if (element.getHeight() < inRect.height() * scale) {
//			return outRect.height() * scale;
//		}
		return outRect.height() * scale;
	}
	@Override
	public void setGraphics(MdCFontLetters mdcFontLetters) {
		element.setGraphics(mdcFontLetters);
		this.mdcFontLetters = mdcFontLetters;
		if (cartoucheType.equals(CartoucheType.CARTOUCHE)) {
			k = 2;
			int height = (int) (k * mdcFontLetters.getCharacterHeight (cartoucheType.codepoint, 0));
			int width = (int) (k * mdcFontLetters.getCharacterWidth (cartoucheType.codepoint, 0));
//			Bitmap bmp = mdcFontLetters.getBitmap(cartoucheType.codepoint, k, rotate, false);
//			inRect = MdCTool.findInsideRect(bmp, width / 2, (int) (height / 1.5), Color.TRANSPARENT);
			inRect = mdcFontLetters.getInnerRect(cartoucheType.codepoint);
			inRect.bottom *= k;
			inRect.top *= k;
			inRect.left *= k;
			inRect.right *= k;
			outRect = new Rect (0, 0, width, height);
		}
		if (cartoucheType.equals(CartoucheType.HWT)) {
			int height = mdcFontLetters.getCharacterHeight (cartoucheType.codepoint, 0);
			int width = mdcFontLetters.getCharacterWidth (cartoucheType.codepoint, 0);
			Bitmap bmp = mdcFontLetters.getBitmap(cartoucheType.codepoint, 1, rotate, false);
			inRect = MdCTool.findInsideRect(bmp, width / 2, (int) (height / 1.5), Color.TRANSPARENT);
			outRect = new Rect (0, 0, width, height);
		}
		if (cartoucheType.equals(CartoucheType.SEREKH)) {
			int height = mdcFontLetters.getCharacterHeight (cartoucheType.codepoint, 0);
			int width = mdcFontLetters.getCharacterWidth (cartoucheType.codepoint, 0);
			float cp2scale = (float) height / mdcFontLetters.getCharacterHeight (cartoucheType.codepoint2, 90);
			int totW = (int) (mdcFontLetters.getCharacterWidth (cartoucheType.codepoint, 0) / 2 
					+ mdcFontLetters.getCharacterWidth (cartoucheType.codepoint2, 90) * cp2scale);
			Bitmap bmp = mdcFontLetters.getBitmap(cartoucheType.codepoint, 1, rotate, false);
			Rect r = MdCTool.findInsideRect(bmp, width / 2, (int) (height / 1.5), Color.TRANSPARENT);
			inRect = new Rect(r.left, r.top, width / 2, r.bottom);
			outRect = new Rect (0, 0, totW, height);
		}
		if (cartoucheType.equals(CartoucheType.ENCLOSURE)) {
			int height = mdcFontLetters.getCharacterHeight (cartoucheType.codepoint, 0);
			int width = mdcFontLetters.getCharacterWidth (cartoucheType.codepoint, 0);
			Bitmap bmp = mdcFontLetters.getBitmap(cartoucheType.codepoint, 1, rotate, false);
			Rect r = MdCTool.findInsideRect(bmp, width / 2, (int) (height / 1.5), Color.TRANSPARENT);
			inRect = new Rect(0, r.top, width, r.bottom);
			outRect = new Rect (0, 0, width, height);
		}
	}

	public float getCartoucheVScale() {
		return ((float) inRect.height() - 2) / (float) outRect.height();
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}

	public float getInHeight() {
		return scale * inRect.height() - 2;
	}

	public float getInWidth() {
		return getWidth () - scale * (outRect.width() - inRect.width());
	}

	public Bitmap getBitmap () {
		Bitmap bmp = mdcFontLetters.getBitmap(cartoucheType.codepoint, scale * k, rotate, false);
	    int width = bmp.getWidth();
	    int height = bmp.getHeight();
	    int totalCartoucheWidth = width;
		double kk = 0.5;
//		Canvas c = new Canvas(bmp);
//		c.drawRect(inRect, )

	    int [] pixels = new int [width * height];

	    bmp.getPixels(pixels, 0, width, 0, 0, width, height);

	    int realCartoucheWidth = (int) getWidth();
		if (cartoucheType.equals(CartoucheType.SEREKH)){
			totalCartoucheWidth = (int) (outRect.width() * scale);
		}

		if (cartoucheType.equals(CartoucheType.ENCLOSURE)){
			int dw = realCartoucheWidth;
			Bitmap result = Bitmap.createBitmap(realCartoucheWidth, height, Bitmap.Config.ARGB_8888);
			for (int j = 0; j < dw; ++ j) {
				for (int i = 0; i < height; ++ i) {
					result.setPixel(j, i, pixels [i * width + (int) (width * kk)]);
				}
			}
			return result;
		} else if (totalCartoucheWidth < realCartoucheWidth || cartoucheType.equals(CartoucheType.SEREKH)) {
			//stretch horizontally
			int dw = realCartoucheWidth - totalCartoucheWidth;
			Bitmap result = Bitmap.createBitmap(realCartoucheWidth, height, Bitmap.Config.ARGB_8888);
			Log.i("TAG", String.format("cartouche bitmap result: w=%d h=%d", result.getWidth(), result.getHeight()));

			//first half
			for (int j = 0; j < width * kk; ++ j) {
				for (int i = 0; i < height; ++ i) {
					int color = pixels [i * width + j];
					result.setPixel(j, i, color);
				}
			}

			//lengthen
			for (int j = 0; j < dw; ++ j) {
				for (int i = 0; i < height; ++ i) {
					result.setPixel((int) ((width * kk) + j), i, pixels [(int) (i * width + width * kk)]);
				}
			}

			//last half
			if (cartoucheType.equals(CartoucheType.SEREKH)) {
			    Bitmap bmp2 = mdcFontLetters.getBitmap(cartoucheType.codepoint2, 
			    		(float) height / mdcFontLetters.getCharacterHeight (cartoucheType.codepoint2, 90), rotate - 90, false);
				for (int j = 0; j < realCartoucheWidth - (int) (width * kk) - dw; ++ j) {
					for (int i = 0; i < height; ++ i) {
						int color = bmp2.getPixel(j, i);
						result.setPixel(j + dw + (int) (width * kk), i, color);
					}
				}
			} else {
				for (int j = (int) (width * kk); j < width; ++ j) {
					for (int i = 0; i < height; ++ i) {
						int color = pixels [i * width + j];
						result.setPixel(j + dw, i, color);
					}
				}
			}
			return result;
		}

		return bmp;
	}

	@Override
	public String toString() {
		String type = cartoucheType == CartoucheType.ENCLOSURE ? "#" : cartoucheType == CartoucheType.HWT ? "0" : cartoucheType == CartoucheType.SEREKH ? "$" : ""; 
		return "<" + type + element.toString() + ">";
	}

	public float getLeftBorder() {
		return scale * (inRect.left - outRect.left);
	}

	public float getTopBorder() {
		return scale * (inRect.top - outRect.top) + 1;
	}

	@Override
	public void scale(float scale) {
		super.scale (scale);
		element.scale (scale);
	}

	public int getRotate() {
		return rotate;
	}

	public void setRotate(int rotate) {
		this.rotate = rotate;
	}
}
