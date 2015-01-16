package com.naens.drawview;

import com.naens.ui.DrawView;
import com.naens.ui.DrawView.PenMode;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private PenMode penMode = PenMode.DRAW;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void btnClear (View v) {
		DrawView dw = (DrawView) findViewById(R.id.draw_view);
		dw.clear();
		dw.setPenMode(PenMode.DRAW);
	}

	public void btnBack (View v) {
		DrawView dw = (DrawView) findViewById(R.id.draw_view);
		dw.back();
	}

	public void btnForward (View v) {
		DrawView dw = (DrawView) findViewById(R.id.draw_view);
		dw.forward();
	}

	public void btnPenMode (View v) {
		DrawView dw = (DrawView) findViewById(R.id.draw_view);
		if (penMode.equals(PenMode.DRAW)) {
			penMode = DrawView.PenMode.ERASE;
		} else {
			penMode = DrawView.PenMode.DRAW;
		}
		dw.setPenMode(penMode);
	}

}
