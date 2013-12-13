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
		if (element.getWidth() < inRect.width() * scale) {
			return outRect.width() * scale;
		}
		return outRect.width() * scale - inRect.width() * scale + element.getWidth();
	}

	@Override
	public float getHeight() {
		if (element.getHeight() < inRect.height() * scale) {
			return outRect.height() * scale;
		}
		return outRect.height() * scale;
	}
	@Override
	public void setGraphics(MdCFontLetters mdcFontLetters) {
		element.setGraphics(mdcFontLetters);
		this.mdcFontLetters = mdcFontLetters;
		int height = mdcFontLetters.getCharacterHeight (codePoint, 0);
		int width = mdcFontLetters.getCharacterWidth (codePoint, 0);
		inRect = mdcFontLetters.getInnerRect (codePoint);
		outRect = new Rect (0, 0, width, height);
	}

	public float getCartoucheVScale() {
		return ((float) inRect.height()) / (float) outRect.height();
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
		return scale * inRect.height();
	}

	public float getInWidth() {
		return getWidth () - scale * (outRect.width() - inRect.width());
	}

	public Bitmap getBitmap () {
		Bitmap bmp = mdcFontLetters.getBitmap(codePoint, scale, 0, false);
	    int width = bmp.getWidth();
	    int height = bmp.getHeight();

	    int [] pixels = new int [width * height];

	    bmp.getPixels(pixels, 0, width, 0, 0, width, height);

	    int totWidth = (int) getWidth();

		if (bmp.getWidth() < totWidth) {
			//stretch horizontally
			int dw = totWidth - bmp.getWidth();
			Bitmap result = Bitmap.createBitmap(totWidth, (int) (scale * outRect.height()), Bitmap.Config.ARGB_8888);
			for (int j = 0; j < width / 2; ++ j) {
				for (int i = 0; i < height; ++ i) {
					int color = pixels [i * width + j];
					result.setPixel(j, i, color);
				}
			}

			for (int j = 0; j < dw; ++ j) {
				for (int i = 0; i < height; ++ i) {
					result.setPixel((width / 2) + j, i, pixels [i * width + width / 2]);
				}
			}

			for (int j = width / 2; j < width; ++ j) {
				for (int i = 0; i < height; ++ i) {
					int color = pixels [i * width + j];
					result.setPixel(j + dw, i, color);
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

	public float getLeftBorder() {
		return scale * (inRect.left - outRect.left);
	}

	public float getTopBorder() {
		return scale * (inRect.top - outRect.top);
	}

	@Override
	public void scale(float scale) {
		super.scale (scale);
		element.scale (scale);
	}
}
