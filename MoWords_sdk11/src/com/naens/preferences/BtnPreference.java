package com.naens.preferences;

import android.content.Context;
import android.preference.Preference;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class BtnPreference extends Preference implements OnTouchListener {

	private Context context;
	private Button button;

	public BtnPreference(Context context, Button btn) {
		super(context);
		this.context = context;
		setPersistent(false);
		this.button = btn;
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
		@SuppressWarnings("unused")
		View oldView = super.onCreateView(parent);
		LinearLayout layout = new LinearLayout(context);
		layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER);
		layout.setId(android.R.id.widget_frame);

		int space = 60;
		
		LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(
			     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		addParams.setMargins(0, 0, space / 2, 0);
		layout.addView(button, addParams);

		return layout;
	}

}
