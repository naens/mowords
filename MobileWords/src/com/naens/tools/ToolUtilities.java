package com.naens.tools;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.naens.model.WordFont;

public class ToolUtilities {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List randomizeList (List l) {
		List list = new LinkedList (l);
		List result = new LinkedList ();
		for (int i = 0; i < l.size (); ++i) {
			int x = (int) Math.round (Math.random () * (list.size () - 1.0));
			result.add (list.remove (x));
		}
		return result;
	}

	//returns list of limit elements
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List randomizeList (List l, int limit) {
		if (l.size () < limit) {
			limit = l.size ();
		}
		List list = new LinkedList (l);
		List result = new LinkedList ();
		for (int i = 0; i < limit; ++i) {
			int x = (int) Math.round (Math.random () * (list.size () - 1.0));
			result.add (list.remove (x));
		}
		return result;
	}

	public static String timeListToString (List <Integer> list) {
		String result = "";
		int sum = 0;
		for (Integer x : list) {
			result += String.format ("%02d:%02d + ", x / 60, x % 60);
			sum += x;
		}
		return String.format ("%02d:%02d (%s) ", sum / 60, sum % 60, result.substring (0, result.length () - 3));
	}

	public static Integer sumList (List <Integer> list) {
		Integer sum = 0;
		for (Integer i : list)
			sum = sum + i;
		return sum;
	}

	public static SparseArray <WordFont> getFontsFromBundle (ResourceBundle rb) {
		SparseArray <WordFont> fonts = new SparseArray <WordFont> ();
		SparseIntArray sizes = new SparseIntArray ();
		SparseArray <String> fontNames = new SparseArray <String> ();
		Enumeration <String> rbKeys = rb.getKeys ();
		while (rbKeys.hasMoreElements ()) {
			String key = rbKeys.nextElement ();
			if (key.matches ("^side\\d.*")) {
				int side = Integer.parseInt (key.split ("^side")[1].split ("[^\\d]+")[0]);
				if (key.matches ("^side\\d+.font$")) {
					fontNames.put (side - 1, rb.getString (key));
				}
				if (key.matches ("^side\\d+.size$")) {
					sizes.put (side - 1, Integer.parseInt (rb.getString (key)));
				}
			}
		}

		for (int i = 0; i < sizes.size (); ++i) {
			int side = sizes.keyAt (i);
			fonts.put (side, new WordFont (fontNames.get (side), sizes.get (side)));
		}
		return fonts;
	}

	public static Bitmap updateSize (Bitmap b, int w, int h) {
		if (w <= 0 || h <= 0) {
			return b;
		}
		if (b.getWidth () > w || b.getHeight () > h) {
			int width = w;
			int height = h;
			if (width * b.getHeight () > height * b.getWidth ()) {
				Log.i ("TAG", ((b.getWidth () * height) / b.getHeight ()) + "x" + (height));
				width = (int)(((double)b.getWidth () * height) / b.getHeight ());
			} else {
				Log.i ("TAG", (width) + "x" + ((b.getHeight () * width) / b.getWidth ()));
				height = (int)(((double)b.getHeight () * width) / b.getWidth ());
			}
			return Bitmap.createScaledBitmap (b, width, height, false);
		}
		return b;
	}

}
