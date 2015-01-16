package com.naens.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;

import com.naens.dao.FontInfo;
import com.naens.model.Word;
import com.naens.model.WordPair;
import com.naens.preferences.SettingsValues;

public class ToolUtilities {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List randomizeList(List l) {
		List list = new LinkedList(l);
		List result = new LinkedList();
		for (int i = 0; i < l.size(); ++i) {
			int x = (int) Math.round(Math.random() * (list.size() - 1.0));
			result.add(list.remove(x));
		}
		return result;
	}

	// returns list of limit elements
	public static <T> List<T> randomizeList(List<T> l, int limit) {
		if (l.size() < limit) {
			limit = l.size();
		}
		List<T> list = new LinkedList<T>(l);
		List<T> result = new LinkedList<T>();
		for (int i = 0; i < limit; ++i) {
			int x = (int) Math.round(Math.random() * (list.size() - 1.0));
			result.add(list.remove(x));
		}
		return result;
	}

	public static String timeListToString(List<Integer> list) {
		String result = "";
		int sum = 0;
		for (Integer x : list) {
			result += String.format("%02d:%02d + ", x / 60, x % 60);
			sum += x;
		}
		return String.format("%02d:%02d (%s) ", sum / 60, sum % 60, result.substring(0, result.length() - 3));
	}

	public static Integer sumList(List<Integer> list) {
		Integer sum = 0;
		for (Integer i : list)
			sum = sum + i;
		return sum;
	}

	public static SparseArray<FontInfo> getFontsFromBundle(ResourceBundle rb) {
		SparseArray<FontInfo> fonts = new SparseArray<FontInfo>();
		SparseIntArray sizes = new SparseIntArray();
		SparseArray<String> fontNames = new SparseArray<String>();
		Enumeration<String> rbKeys = rb.getKeys();
		while (rbKeys.hasMoreElements()) {
			String key = rbKeys.nextElement();
			if (key.matches("^side\\d.*")) {
				int side = Integer.parseInt(key.split("^side")[1].split("[^\\d]+")[0]);
				if (key.matches("^side\\d+.font$")) {
					fontNames.put(side - 1, rb.getString(key));
				}
				if (key.matches("^side\\d+.size$")) {
					sizes.put(side - 1, Integer.parseInt(rb.getString(key)));
				}
			}
		}

		for (int i = 0; i < sizes.size(); ++i) {
			int side = sizes.keyAt(i);
			fonts.put(side, new FontInfo(fontNames.get(side), sizes.get(side)));
		}
		return fonts;
	}

	public static Bitmap updateSize(Bitmap b, int w, int h) {
		if (w <= 0 || h <= 0) {
			return b;
		}
		if (b.getWidth() > w || b.getHeight() > h) {
			int width = w;
			int height = h;
			if (width * b.getHeight() > height * b.getWidth()) {
				width = (int) (((double) b.getWidth() * height) / b.getHeight());
			} else {
				height = (int) (((double) b.getHeight() * width) / b.getWidth());
			}
			return Bitmap.createScaledBitmap(b, width, height, false);
		}
		return b;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, float cornerDips, float borderDips,
			Context context) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderDips, context
				.getResources().getDisplayMetrics());
		final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cornerDips, context
				.getResources().getDisplayMetrics());
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		// prepare canvas for transfer
		paint.setAntiAlias(true);
		// paint.setColor(0xFFFFFFFF);
		// paint.setStyle(Paint.Style.FILL);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

		// draw bitmap
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		// draw border
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) borderSizePx);
		canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

		return output;
	}

	public Bitmap drawFontLetter(String text, Typeface typeface, int color, int size) {
		Paint paint = new Paint();
		paint.setTextSize(size);
		paint.setAntiAlias(true);
		paint.setTypeface(typeface);
		paint.setColor(color);

		int width = (int) (paint.measureText(text));
		int height = (int) (-paint.ascent() + paint.descent());

		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		c.drawText(text, 0, height, paint);

		return bmp;
	}

	public static List<String> createWordListFromWordPairsAndSide(List<WordPair> pairs, int side) {
		List<String> words = new LinkedList<String>();
		for (WordPair wordPair : pairs) {
			int pairSize = wordPair.getSize();
			for (int i = 0; i < pairSize; ++i) {
				Word word = wordPair.getWordN(i);
				if (word.getSide() == side) {
					words.add(word.getString());
				}
			}
		}
		return words;
	}

	public static TreeSet<Integer> findGameSides(Object[] pairs) {
		TreeSet<Integer> result = new TreeSet<Integer>();
		for (Object o : pairs) {
			WordPair wordPair = (WordPair) o;
			int pairSize = wordPair.getSize();
			for (int i = 0; i < pairSize; ++i) {
				Word word = wordPair.getWordN(i);
				result.add(word.getSide());
			}
		}
		if (result.size() == 0) {
			throw new RuntimeException("find sides: not found!");
		}
		return result;
	}

	public static Bitmap assureSize(Bitmap bmp, float w, float h) {
		float bw = bmp.getWidth();
		float bh = bmp.getHeight();
		float wf = w / bw;
		float hf = h / bh;
		float f = Math.min(wf, hf);
		if (f >= 1) {
			return bmp;
		}
		// create bmp
		int nw = Math.round(bw * f);
		int nh = Math.round(bh * f);

		return Bitmap.createScaledBitmap(bmp, nw, nh, true);
	}

	public static void makeAccountId(Context context) throws NoAccountIdEception {
		String googleEmail = null;
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		googleEmail = sp.getString(SettingsValues.KEY_GOOGLE_EMAIL, null);
		if (googleEmail != null) {
			return;
		}
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(context).getAccounts();

		final ArrayList<String> emails = new ArrayList<String>(50);
		// accountIds.add("123@321.78");
		// accountIds.add("abc@bca.we");
		// accountIds.add("kio@popopo.nana");
		// accountIds.add("deru@redu.udre");
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				emails.add(account.name);
			}
		}
		if (emails.size() == 0) {
			throw new NoAccountIdEception();
		} else if (emails.size() == 1) {
			googleEmail = emails.get(0).toString();
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(SettingsValues.KEY_GOOGLE_EMAIL, googleEmail);
			editor.commit();
		} else {
			new AlertDialog.Builder(context)
					.setTitle("title")
					.setSingleChoiceItems(emails.toArray(new String[emails.size()]), -1,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									String googleEmail = emails.get(which);
									SharedPreferences.Editor editor = sp.edit();
									editor.putString(SettingsValues.KEY_GOOGLE_EMAIL, googleEmail);
									editor.commit();
								}
							}).create().show();
		}
	}

	public static class NoAccountIdEception extends Exception {

		private static final long serialVersionUID = -2367996538938448676L;

	}

	// convert InputStream to String
	public static String getStringFromInputStream(InputStream is, String encoding) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is, encoding));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
}
