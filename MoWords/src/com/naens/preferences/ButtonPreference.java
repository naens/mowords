package com.naens.preferences;

import android.content.Context;
import android.preference.Preference;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ButtonPreference extends Preference implements OnTouchListener {

	private Context context;
	private Button btn;

	public ButtonPreference(Context context, Button btn) {
		super(context);
		this.context = context;
		this.btn = btn;
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		view.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		View oldView = super.onCreateView(parent);
		ViewGroup layout = null;
		if (oldView instanceof ViewGroup) {
			layout = (ViewGroup) oldView;
		} else {
			layout = new LinearLayout(context);
			TextView textView = new TextView(context);
			textView.setText (getTitle());
			layout.addView(textView);
		}

		layout.addView(btn);
		layout.setId(android.R.id.widget_frame);
		return layout;
	}

}
