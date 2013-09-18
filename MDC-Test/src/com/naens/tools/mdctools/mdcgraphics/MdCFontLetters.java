package com.naens.tools.mdctools.mdcgraphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.SparseArray;

public class MdCFontLetters {

	private int fontSize;

	private Typeface typeface;

	private SparseArray <LetterInfo> letterInfos;

	public MdCFontLetters (int fontSize, Typeface typeface) {
		this.fontSize = fontSize;
		this.typeface = typeface;
		letterInfos = new SparseArray <MdCFontLetters.LetterInfo> ();
	}

	public Bitmap getBitmap (Integer codePoint, float scale, int rotate, boolean flip) {
		LetterInfo info = letterInfos.get (codePoint);
		if (info == null) {
			info = new LetterInfo (codePoint);
			letterInfos.put (codePoint, info);
		}
		info.flip = flip;
		info.rotate = rotate;
		return createBitmap (info, scale);
	}

	private Bitmap createBitmap (LetterInfo info, float scale) {
		float width = scale * info.width;
		float height = scale * info.height;

		Paint paint = new Paint();
		paint.setTextSize((int) (scale * fontSize));
		paint.setTypeface(typeface);
		paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

		int w = (int) (paint.measureText(info.symbol));
		int h = (int) (-paint.ascent() + paint.descent());

		Bitmap bitmap = Bitmap.createBitmap((int) w, (int) h, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		int color = Color.LTGRAY;
		c.drawColor(color);
		c.drawText(info.symbol, 0, -paint.ascent(), paint);

		Rect rect = getBitmapRect(bitmap, color);
		int nWidth = rect.width() + 1;
		int nHeight = rect.height() + 1;

		float scaleWidth = width / nWidth;
		float scaleHeight = height / nHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(info.flip ? -scaleWidth : scaleWidth, scaleHeight);
		matrix.postRotate(info.rotate);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, nWidth, nHeight, matrix, true);
		return resizedBitmap;
	}

	public int getCharacterWidth (int codePoint) {
		return letterInfos.get(codePoint).width;
	}

	public int getCharacterHeight (int codePoint) {
		return letterInfos.get(codePoint).height;
	}

	private class LetterInfo {
		private int height;
		private int width;
		private String symbol;
		private int rotate;
		private boolean flip;

		private LetterInfo (int codePoint) {
			Paint paint = new Paint();
			paint.setTextSize(fontSize);
			paint.setTypeface(typeface);

			char[] charPair = Character.toChars(codePoint);
			symbol = new String(charPair);

			int w = (int) (paint.measureText(symbol));
			int h = (int) (-paint.ascent() + paint.descent());

			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(bitmap);
			c.drawText(symbol, 0, -paint.ascent(), paint);
			Rect rect = getBitmapRect(bitmap, 0);
			this.width = rect.width();
			this.height = rect.height();;
		}
		
	}

	private Rect getBitmapRect (Bitmap bmp, int backColor) {
	    int [] pixels = new int [bmp.getHeight() * bmp.getWidth()];

	    //Convenience variables
	    int width = bmp.getWidth();
	    int height = bmp.getHeight();
	    int length = pixels.length;

	    bmp.getPixels(pixels, 0, width, 0, 0, width, height);

	    int firstLine = 0;
	    int lastLine = height - 1;
	    int firstColumn = 0;
	    int lastColumn = width - 1;

	    for (int i = 0; i < length; ++ i) {
			if (pixels[i] != backColor) {
				firstLine = i / width;
				break;
			}
		}

	    for (int i = length - 1; i >= 0; -- i) {
			if (pixels[i] != backColor) {
				lastLine = i / width;
				break;
			}
		}

	    for (int i = 0; i < width; ++ i) {
	    	for (int j = firstLine; j < lastLine; ++ j) {
				if (pixels[j * width + i] != backColor) {
					lastColumn = i;
					break;
				}
			}
		}

	    for (int i = width - 1; i >= 0; -- i) {
	    	for (int j = firstLine; j < lastLine; ++ j) {
				if (pixels[j * width + i] != backColor) {
					firstColumn = i;
					break;
				}
			}
		}

	    Rect result = new Rect(firstColumn, firstLine, lastColumn, lastLine);
	    return result;
	}

}
