package com.naens.android_mdc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.naens.preferences.SettingsActivity;
import com.naens.tools.mdctools.InvalidMdCCodeException;
import com.naens.tools.mdctools.MdCToUnicode;
import com.naens.tools.mdctools.MdCTool;
import com.naens.tools.mdctools.mdcgraphics.LetterAroundInformation;
import com.naens.tools.mdctools.mdcgraphics.MdCFontLetters;
import com.naens.tools.mdctools.mdcstruct.MdCLetter;

public class MainActivity extends Activity {

	private static Context context;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		context = this;
		setContentView(R.layout.activity_main);

		
		textView = (TextView) findViewById(R.id.text_view);
/*
		Button btn = new Button (this);
		btn.setText ("abcd");
        btn.setBackgroundResource(R.drawable.folder_button);
		btn.setTextSize(28);
		btn.setTextColor(Color.rgb(0x61, 0x30, 0));
		btn.setShadowLayer(2, 1, 1, Color.rgb(0x80, 0x80, 0x80));
		btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
		        ViewGroup.LayoutParams.WRAP_CONTENT));
		((LinearLayout) findViewById (R.id.main_layout)).addView (btn);
*/
	}

	public void drawShape(View view) {
		int szx = 100;
		int szy = 200;
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(3);
		paint.setAntiAlias(true);

		Path path = new Path();
		path.moveTo(5, 5);
		path.quadTo(szx - 5, 5, szx - 5, szy - 5);
		path.moveTo(szx - 5, 5);
		path.quadTo(5, 5, 5, szy - 5);

		bmp = Bitmap.createBitmap(szx, szy, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		c.drawPath(path, paint);

		Drawable d = new BitmapDrawable(null, bmp);
		d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
		textView.setCompoundDrawables(null, d, null, null);
	}

	public void drawFontLetter(View view) {
		Paint paint = new Paint();
		paint.setTextSize(20);
		paint.setAntiAlias(true);
		paint.setTypeface(Typeface.MONOSPACE);
		paint.setColor(Color.MAGENTA);

		String text = "Abcd";
		int width = (int) (paint.measureText(text)) + 5;
		int height = (int) (-paint.ascent() + paint.descent()) + 5;

		bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		c.drawText(text, 0, height, paint);

		Drawable d = new BitmapDrawable(null, bmp);
		d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
		textView.setCompoundDrawables(null, d, null, null);
	}

	Bitmap bmp = null;

	public void drawImage(View view) {
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.statue);
		bmp = getRoundedCornerBitmap(b, Color.BLACK, 40, 3, this);

		Drawable d = new BitmapDrawable(null, bmp);
		d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
		textView.setCompoundDrawables(null, d, null, null);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, int cornerDips, int borderDips, Context context) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
	            Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips,
	            context.getResources().getDisplayMetrics());
	    final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips,
	            context.getResources().getDisplayMetrics());
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);

	    // prepare canvas for transfer
	    paint.setAntiAlias(true);
	    paint.setColor(0xFFFFFFFF);
	    paint.setStyle(Paint.Style.FILL);
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

	private String [] tests = {

//			//around:vertical:above:right
//			"D:D", "D:D:A", "D:D:D", "D:D:D:D", "D:p:D",
//			"D:p:D:p", "D:s:D", "A:D", "D:p:D:1", 	"D:A:D:s",
//			"D:A", "D:s:p", "p:D:n", "D:s", "D:n:n", "D:n:s", "S:D:n:s",
//			"D:2", "D:p", "D:i", "D:n", "D:A1", "D:p:p", "D:p:p:p", "D:p*p", "D:i*s",
//			"D:2", "D:i*i", "D:p*p", "D:p*i", "D:n:2", "D:r:i*i", "D:f:p*p", "D:n:p*i",
//			"D:2:n", "D:i*i:r", "D:p*p:f", "D:p*i:n", "D:A*A",
			//I11 F20 F39
//			"I11:2", "F20:i*i", "F39:p*p", "I11:p*i", "F20:n:2", "F39:r:i*i", "I11:f:p*p", "F20:n:p*i",
//			"I11:2:n", "F20:i*i:r", "F39:p*p:f", "I11:p*i:n", "F39:A*A",

//			around:vertical:above:leftright
//			"F40:p", "F40:s", "F40:A", "F40:A1", "F40:i", "F40:1",
//			"F40:n:1", "F40:2", "F40:i*i", "F40:p*p", "F40:p*i",
//			"F40:n:2", "F40:r:i*i", "F40:f:p*p", "F40:n:p*i",
//			"F40:2:n", "F40:i*i:r", "F40:p*p:f", "F40:p*i:n",
//			"F40:p:p", "F40:n", "F40:D", "F40:A*A",
			//N1 N11 N12
//			"N1:n:1", "N11:2", "N12:i*i", "N1:p*p", "N11:p*i",
//			"N12:n:2", "N1:r:i*i", "N11:f:p*p", "N12:n:p*i",
//			"N1:2:n", "N11:i*i:r", "N12:p*p:f", "N1:p*i:n",

//			//around:vertical:below:right
//			"p:U1", "n:U1", "i:U1", "A1:U1", "1:U1", "2:U1", "i*i:U1", "A1*A1:U1", "p:p:U1", "p:p:p:U1", "p:U2",
//			"n:D36", "i:D41", "A1:D42", "1:U1", "2:D45", "i*i:U2", "A1*A1:D36", "p:p:D41", "p:p:p:D42", "i*i:D36",

			//around:vertical:below:leftright
//			"p:D40", "n:D40", "i:D40", "A1:D40", "1:D40", "2:D40", "i*i:D40", "A1*A1:D40", "p:p:D40",

			//around:horizontal
//			"G13*1", "G13*p", "G13*q", "G13*k", "G13*i", "G13*A", "G13*x", "G13*G13",
//			"G13*p", "G13*2", "G13*3", "G13*1*A", "G13*2*A",
//			"1*D58", "p*D58", "p*p*D58", "2*D58", "3*D58", "A*1*D58", "A*2*D58", "A*1",
//			"A*3", "A*1*O6", "A*3*O6",
//			"1*R8", "2*R8", "3*R8", "p*R8", "p*p*R8", "A*1*R8", "A*2*R8", "A*3*R8",
//			"w Z4", "w t",
			
//			"n:S", "A*i", "A", "A:n", "A:i", "A1:A2", "n:i", "n:A", "i:A1", "i:s",
//			"A1*i:n", "p*i:n", "a*r:n", "A1*i:s", "p*i:s", "a*r:s", "A1*i:k", "p*i:k", "a*r:k",
//			"n:A1*i", "n:p*i", "n:a*r", "s:A1*i", "s:p*i", "s:a*r", "k:A1*i", "k:p*i", "k:a*r",
//			"A1*i:A1*i", "A1*i:p*i", "A1*i:a*r", "p*i:A1*i", "p*i:p*i", "p*i:a*r", "a*r:A1*i", "a*r:p*i", "a*r:a*r",

/*			"A1:(A1*i:n)", "(n:p*i):n", "A1:(a*r:n)", "(n:a*r):n", "A1:(A1*i:a*r)", "(a*r:s):n", "A1:(a*r:A1*i)", "(a*r:p*i):n", "A1:(k:p*i)", 
			"p:(A1*i:n):n", "p:(n:p*i)", "p:(a*r:n):n", "p:(A1*i:s):n", "p:(A1*i:a*r)", "p:(p*i:k):n", "p:(a*r:A1*i):n", "p:(s:A1*i)", "p:(k:p*i):n", 
			"n:(A1*i:n):i", "(n:p*i):i", "n:(n:a*r)", "n:(A1*i:s):i", "(a*r:s):i", "n:(p*i:k):i", "n:(a*r:p*i):i", "n:(s:A1*i)", "n:(p*i:a*r):i", 
			"p*n:r*q", "r*x:p*n",
*/
			//Cartouches
//			"<A>", "<#A>", "<$A>", "<0A>", "<#1>", "<$1>", "<01>",
//			"<A>:r", "<#A>:r", "<$A>:r", "<0A>:r",

			//"<p>",
//			"A1 B1 r",
			"t t",
			"<t*t>",
			//"<p*p*p>","<p*p*p*p>","<p*p*p*p*p>",
			//cartouche outside
//			"<A>", "<A*i>", "<A:n>", "<A:i>", "<A1:A2>", "<n:i>", "<n:A>", "<i:A1>", "<i:s>", 
//			"<A1*i:n>", "<p*i:n>", "<a*r:n>", "<A1*i:s>", "<p*i:s>", "<a*r:s>", "<A1*i:k>", "<p*i:k>", "<a*r:k>",
//			"<n:A1*i>", "<n:p*i>", "<n:a*r>", "<s:A1*i>", "<s:p*i>", "<s:a*r>", "<k:A1*i>", "<k:p*i>", "<k:a*r>",
//			"<A1*i:A1*i>", "<A1*i:p*i>", "<A1*i:a*r>", "<p*i:A1*i>", "<p*i:p*i>", "<p*i:a*r>", "<a*r:A1*i>", "<a*r:p*i>", "<a*r:a*r>",
//			"<p*n:r*q>", "<r*x:p*n>",

			//cartouche inside
//			"<A1>:(A1*i:n)", "A1:(a*<r:n>)", "<n:a*r>:n", "<a*r:s>:n", "<A1>:(a*r:A1*i)", "<a*r:p*i>:n",
//			"p:<n:p*i>", "p:<A1*i:s>:n", "p:(A1*i:<a*r>)", "p:<a*r:A1*i>:n", "p:<s:A1*i>", "p:<k:p*i>:n", 
//			"n:(<A1*i>:n):i", "<n>:(n:a*r)", "(a*r:s):<i>",
//			"<n>:(p*i:k):i",
//			"n:<a*r:p*i>:i", "<n>:(p*i:a*r):i", 
//			"p*<n>:r*q", "<r>*x:p*n", "p*n:<r>*q",
//			"r*x:p*<n>",
//			"p*<n>:<r>*q", "<r>*x:p*<n>",  "<p*n>:<r*q>", "<r*x>:<p*n>",
/*
			"r*x:p*<p>", "p*<p>", "r:p*<p>", "x:p*<p>", "r*x:<p*p>", "r*x:<p>", "r*x:p*<p>",
			"r*x:p*<n>", "p*<n>", "r:p*<n>", "x:p*<n>", "r*x:<p*n>", "r*x:<n>", "r*x:p*n", "r*x:p*<n>",
			"<n>:r", "<n>:x", "r:<n>", "x:<n>",

			"r*x:p*<A1>", "r*x:<p*A1>",	//ok
			"x:n", "n:x", "<A1>:x", "x:<A1>"	//ok
*/
			//not working
//			"(n:(A1*i:n):i):(A1*i:n)",
//			"k:(a*r:n):((n:p*i):i)",
//			"(p:(A1*i:s):n):(n:a*r)",
//			"A1:(A1*(n:(p*i:k):i):n)",
//			"p:(a*r:(n:(a*r:p*i):i)):n",
//			"n:((A1:(k:p*i)):a*r)", 
	};

//	private String [] tests = {"a*r:n", "a*r:k", "n:a*r", "k:a*r"};
	private int c = 0;

	private String mdc = "";
	private String oldmdc = "";
	public void drawHieroglyph(View view) {
		boolean topbottom = ((CheckBox) findViewById(R.id.checkbox_top_bottom)).isChecked();
		EditText edit = (EditText) findViewById(R.id.mdc_edit);
		Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Gardiner.ttf");
		int height = 70;
		String editString = edit.getText ().toString ().trim();
		if (editString.equals("") || editString.equals(oldmdc)) {
			mdc = tests [c ++ % tests.length];
			oldmdc = mdc;
			edit.setText (mdc);
		} else {
			c = 0;
			mdc = editString;
			oldmdc = "";
		}
		try {
			bmp = MdCTool.mdcToBitmap(typeface, (topbottom ? "#v " : "") + mdc, height, Color.DKGRAY);

			Drawable d = new BitmapDrawable(null, bmp);
			d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
			textView.setCompoundDrawables(d, null, null, null);
			textView.setPadding(textView.getWidth() / 2 - bmp.getWidth() / 2, 0, 0, 0);
			textView.setText("");

		} catch (InvalidMdCCodeException e) {
			bmp = null;
			String message = String.format("wrong mdc code '%s'", e.getWrongChar());
			Log.e("TAG", message);
			textView.setText(message);
			e.printStackTrace();
		}
	}

	public void drawAround (View view) {
		String mdcChar = ((EditText) findViewById(R.id.mdc_edit)).getText ().toString();
		if (mdcChar.trim().equals("")) {
			mdcChar = "G13";
			((EditText) findViewById(R.id.mdc_edit)).setText(mdcChar);
		}
		Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Gardiner.ttf");
		MdCFontLetters mdcFontLetters = new MdCFontLetters(70, typeface, Color.DKGRAY);
		int codePoint = MdCToUnicode.getCode(mdcChar);
		MdCLetter letter = new MdCLetter(codePoint, 0);
		letter.setGraphics(mdcFontLetters);
		LetterAroundInformation lai = letter.aroundInfo();
		RectF inRect = lai.getInRect();
		Toast.makeText(MainActivity.getContext(), String.format("(%.1f,%.1f)  (%.1f,%.1f)",
				inRect.left, inRect.top, inRect.right, inRect.bottom), Toast.LENGTH_SHORT).show();
		bmp = letter.getBitmap();
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setColor(Color.CYAN);
        paint.setStyle(Style.FILL);
		canvas.drawRect(inRect, paint);

		Drawable d = new BitmapDrawable(null, bmp);
		d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
		textView.setCompoundDrawables(null, d, null, null);
	}

	public void drawCharFrame (View view) throws InvalidMdCCodeException {
		Spinner spinnerPlaces = (Spinner) findViewById(R.id.spinner_places);
		String place = spinnerPlaces.getSelectedItem().toString();
		Spinner spinnerRotate = (Spinner) findViewById(R.id.spinner_rotate);
		int rotate = Integer.parseInt(spinnerRotate.getSelectedItem().toString());
		CheckBox flipCheckBox = (CheckBox) findViewById(R.id.checkbox_flip);
		boolean flip = flipCheckBox.isChecked();
		int x = 0;
		int y = 0;
		Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Gardiner.ttf");
		MdCFontLetters mdCFontLetters = new MdCFontLetters(120, typeface, Color.DKGRAY);
		String mdcChar = ((EditText) findViewById(R.id.mdc_edit)).getText ().toString();
		if (mdcChar.trim().equals("")) {
			mdcChar = "D42";
			((EditText) findViewById(R.id.mdc_edit)).setText(mdcChar);
		}
		int codePoint = MdCToUnicode.getCode(mdcChar);
		bmp = mdCFontLetters.getBitmap(codePoint, 1, rotate, flip);
		if (place.equals("Left Bottom")) {
			y = bmp.getHeight() - 1;
		}
		if (place.equals("Right Bottom")) {
			y = bmp.getHeight() - 1;
			x = bmp.getWidth() - 1;
		}
		if (place.equals("Left Top")) {
		}
		if (place.equals("Right Top")) {
			x = bmp.getWidth() - 1;
		}
		if (place.equals("Center")) {
			y = bmp.getHeight() / 2;
			x = bmp.getWidth() / 2;
		}
		Rect inRect = MdCTool.findInsideRect(bmp, x, y, Color.TRANSPARENT);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setColor(Color.CYAN);
        paint.setStyle(Style.FILL);
		canvas.drawRect(inRect, paint);

		Drawable d = new BitmapDrawable(null, bmp);
		d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
		textView.setCompoundDrawables(null, d, null, null);
	}

	public void rotate(View view) {
		if (bmp != null) {
			Bitmap original = bmp;
			Matrix matrix = new Matrix();
			matrix.postRotate(90);

			bmp = Bitmap.createBitmap(original, 0, 0, original.getWidth(),
					original.getHeight(), matrix, true);

			Drawable d = new BitmapDrawable(null, bmp);
			d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
			textView.setCompoundDrawables(null, d, null, null);
		}
	}

	public void findFolder (View view) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public static Context getContext() {
		return context;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_settings:
	    	    Intent intent = new Intent(this, SettingsActivity.class);
	    	    startActivity(intent);
	            return true;
//	        case R.id.help:
//	            showHelp();
//	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
