package com.naens.tools.mdctools.mdcgraphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.SparseArray;

public class MdCFontLetters {

	private int fontSize;

	private Typeface typeface;

	private SparseArray <LetterInfo> letterInfos;

	private int color;

	public MdCFontLetters (int fontSize, Typeface typeface, int color) {
		this.fontSize = fontSize;
		this.typeface = typeface;
		this.color = color;
		letterInfos = new SparseArray <MdCFontLetters.LetterInfo> ();
	}

	public Bitmap getBitmap (Integer codePoint, float scale, int rotate, boolean flip) {
		LetterInfo info = getInfo(codePoint);
		return createBitmap (info, scale, rotate, flip);
	}

	private LetterInfo getInfo (int codePoint) {
		LetterInfo info = letterInfos.get (codePoint);
		if (info == null) {
			info = new LetterInfo (codePoint);
			letterInfos.put (codePoint, info);
		}
		return info;
	}

	private Bitmap createBitmap (LetterInfo info, float scale, int rotate, boolean flip) {
		float width = scale * info.width;
		float height = scale * info.height;

		Paint paint = new Paint();
		paint.setTextSize((int) (scale * fontSize));
		paint.setTypeface(typeface);
		paint.setColor(color);
		paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        if (scale < 0.75) {
        	float blur = (float) 0.3;
        	paint.setShadowLayer(blur, 0, 0, color);
        }

		int w = (int) (paint.measureText(info.symbol));
		int h = (int) (-paint.ascent() + paint.descent());

		Bitmap bitmap = Bitmap.createBitmap((int) w, (int) h, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		int backgroundColor = Color.TRANSPARENT;
		c.drawColor(backgroundColor);
		c.drawText(info.symbol, 0, -paint.ascent(), paint);

		Rect rect = getBitmapRect(bitmap, backgroundColor);
		int nWidth = rect.width() + 1;
		int nHeight = rect.height() + 1;

		float scaleWidth = width / nWidth;
		float scaleHeight = height / nHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(flip ? -scaleWidth : scaleWidth, scaleHeight);
		matrix.postRotate(rotate);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, nWidth, nHeight, matrix, true);
//		Log.i("TAG", String.format("resized bitmap: w=%d, h=%d", resizedBitmap.getWidth(), resizedBitmap.getHeight()));
		return resizedBitmap;
	}

	public int getCharacterWidth (int codePoint, int rotate) {
		LetterInfo info = getInfo(codePoint);
		return Math.abs(rotate) % 180 == 90 ? info.height : info.width;
	}

	public int getCharacterHeight (int codePoint, int rotate) {
		LetterInfo info = getInfo(codePoint);
		return Math.abs(rotate) % 180 == 90 ? info.width : info.height;
	}

	private class LetterInfo {
		private int height;
		private int width;
		private String symbol;

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
			width = rect.width();
			height = rect.height();
			Log.i("TAG", String.format("create letter: code point = %s, width = %d, height = %d", Integer.toHexString(codePoint), width, height));
		}
		
	}

	private Rect getBitmapRect (Bitmap bmp, int backColor) {
	    int width = bmp.getWidth();
	    int height = bmp.getHeight();

	    int [] pixels = new int [width * height];
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

	public Rect getInnerRect(int codePoint) {
		Bitmap bmp = getBitmap(codePoint, 1, 0, false);
	    int [] pixels = new int [bmp.getHeight() * bmp.getWidth()];

	    int width = bmp.getWidth();
	    int height = bmp.getHeight();

	    bmp.getPixels(pixels, 0, width, 0, 0, width, height);

	    int x = width / 2;
	    int y = height / 2;

	    Point lt = new Point(x, y);
	    Point rt = new Point(x, y);
	    Point lb = new Point(x, y);
	    Point rb = new Point(x, y);

	    if (height > width) {
	    	while (pixels [width * lt.y + lt.x] == 0) {
	    		-- lt.y;
	    		lt.x = (int) (lt.y * ((double) width / (double) height));
	    	}
	    	while (pixels [width * rt.y + rt.x] == 0) {
	    		-- rt.y;
	    		rt.x = width - (int) (rt.y * ((double) width / (double) height));
	    	}
	    	while (pixels [width * lb.y + lb.x] == 0) {
	    		++ lb.y;
	    		lb.x = width - (int) (lb.y * ((double) width / (double) height));
	    	}
	    	while (pixels [width * rb.y + rb.x] == 0) {
	    		++ rb.y;
	    		rb.x = (int) (rb.y * ((double) width / (double) height));
	    	}
	    } else {
	    	while (pixels [width * lt.y + lt.x] == 0) {
	    		-- lt.x;
	    		lt.y = (int) (lt.x * ((double) height / (double) width));
	    	}
	    	while (pixels [width * rt.y + rt.x] == 0) {
	    		++ rt.x;
	    		rt.y = height - (int) (rt.x * ((double) height / (double) width));
	    	}
	    	while (pixels [width * lb.y + lb.x] == 0) {
	    		-- lb.x;
	    		lb.y = height - (int) (lb.x * ((double) height / (double) width));
	    	}
	    	while (pixels [width * rb.y + rb.x] == 0) {
	    		++ rb.x;
	    		rb.y = (int) (rb.x * ((double) height / (double) width));
	    	}
	    }
	    int rXLeft = Math.max (lt.x, lb.x);
	    int rYTop = Math.max (lt.y, rt.y);
	    int rXRight = Math.min (rt.x, rb.x);
	    int rYBottom = Math.min (lb.y, rb.y);

		return new Rect(rXLeft, rYTop, rXRight, rYBottom);
	}

}
