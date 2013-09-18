package com.naens.tools.mdctools.mdcstruct;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.naens.tools.mdctools.InvalidMdCCodeException;
import com.naens.tools.mdctools.MdCToUnicode;
import com.naens.tools.mdctools.mdcgraphics.MdCFontLetters;

public class MdCCartouche extends MdCElement {

	private MdCElement element;

	private MdCFontLetters mdcFontLetters;

	private int codePoint;

	private float scale;

	private Rect inRect;

	private Rect outRect;

	private float y;

	private float x;

	public MdCCartouche(MdCElement element) {
		this.element = element;
		try {
			codePoint = MdCToUnicode.getCode("V10");
		} catch (InvalidMdCCodeException e) {
			throw new RuntimeException(e);
		}
	}

	public MdCElement getElement() {
		return element;
	}

	public void setElement(MdCElement element) {
		this.element = element;
	}

	@Override
	public float getWidth() {
		if (element.getWidth() < inRect.width()) {
			return scale * outRect.width();
		}
		return scale * element.getWidth() + inRect.left - outRect.left + outRect.right - inRect.right;
	}

	@Override
	public float getHeight() {
		if (element.getHeight() < inRect.height()) {
			return scale * outRect.height();
		}
		return scale * element.getHeight() + inRect.top - outRect.top + outRect.bottom - inRect.bottom;
	}

	public void setHeight(float height) {
		scale = height / outRect.height();	//how to scale cartouche
	}

	public void setGraphics(MdCFontLetters mdcFontLetters) {
		this.mdcFontLetters = mdcFontLetters;
		int height = mdcFontLetters.getCharacterHeight (codePoint, 0);
		int width = mdcFontLetters.getCharacterWidth (codePoint, 0);
		inRect = mdcFontLetters.getInnerRect (codePoint);
		outRect = new Rect (0, 0, width, height);
	}

	public float getCartoucheVScale() {
		return inRect.height() / outRect.height();
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

	public int getInHeight() {
		return inRect.height();
	}

	public int getInWidth() {
		return inRect.width();
	}

	public Bitmap getBitmap () {
		Bitmap bmp = mdcFontLetters.getBitmap(codePoint, scale, 0, false);
	    int width = bmp.getWidth();
	    int height = bmp.getHeight();

	    int [] pixels = new int [width * height];

	    bmp.getPixels(pixels, 0, width, 0, 0, width, height);
	    /*	do not make cartouche higher
		if (bmp.getHeight() < outRect.height()) {
			//stretch vertically
			int dh = outRect.height() - bmp.getHeight();
			Bitmap result = Bitmap.createBitmap(outRect.width(), outRect.height(), Bitmap.Config.ARGB_8888);
			for (int i = 0; i < height / 2; ++ i) {
				for (int j = 0; j < width; ++ j) {
					result.setPixel(i, j, pixels [i * width + j]);
				}
			}

			for (int i = 0; i < dh; ++ i) {
				for (int j = 0; j < width; ++ j) {
					result.setPixel((height / 2) + i, j, pixels [(height / 2)  * width + j]);
				}
			}

			for (int i = height / 2; i < height; ++ i) {
				for (int j = 0; j < width; ++ j) {
					result.setPixel(i + dh, j, pixels [i * width + j]);
				}
			}
			return result;
		}*/

		if (bmp.getWidth() < outRect.width()) {
			//stretch horizontally
			int dw = outRect.width() - bmp.getWidth();
			Bitmap result = Bitmap.createBitmap(outRect.width(), outRect.height(), Bitmap.Config.ARGB_8888);
			for (int j = 0; j < width / 2; ++ j) {
				for (int i = 0; i < height; ++ i) {
					result.setPixel(i, j, pixels [i * width + j]);
				}
			}

			for (int j = 0; j < dw; ++ j) {
				for (int i = 0; i < height; ++ i) {
					result.setPixel(i, (width / 2) + j, pixels [i * width + width / 2]);
				}
			}

			for (int j = 0; j < width / 2; ++ j) {
				for (int i = height; i < height; ++ i) {
					result.setPixel(i, j + dw, pixels [i * width + j]);
				}
			}
			return result;
		}

		return bmp;
	}

	@Override
	public String toString() {
		return "<" + element.toString() + ">";
	}
}
