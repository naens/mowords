package com.naens.preferences;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.naens.mowords.R;

public class AddDelPreference extends Preference implements OnTouchListener {

	public interface AddDelListener {
		void onAddClick();
		void onDelClick();
	}

	private Context context;
	private AddDelListener addDelListener;

	public AddDelPreference(Context context) {
		super(context);
		this.context = context;
		setPersistent(false);
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

	public void setAddDelListner (AddDelListener addDelListener) {
		this.addDelListener = addDelListener;
	}

	@SuppressWarnings("deprecation")
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
		
		Button addButton = new Button (context);
		Drawable addDrawable = context.getResources().getDrawable(R.drawable.addm);
		addButton.setBackgroundDrawable(addDrawable);
		addButton.setWidth(addDrawable.getMinimumWidth());
		addButton.setHeight(addDrawable.getMinimumHeight());
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (addDelListener != null) {
					addDelListener.onAddClick ();
				}
			}
		});
		LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(
			     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		addParams.setMargins(0, 0, space / 2, 0);
		layout.addView(addButton, addParams);

		Button delButton = new Button (context);
		Drawable delDrawable = context.getResources().getDrawable(R.drawable.removem);
		delButton.setBackgroundDrawable(delDrawable);
		delButton.setWidth(delDrawable.getMinimumWidth());
		delButton.setHeight(delDrawable.getMinimumHeight());
		delButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (addDelListener != null) {
					addDelListener.onDelClick ();
				}
			}
		});
		LinearLayout.LayoutParams delParams = new LinearLayout.LayoutParams(
			     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		delParams.setMargins(space / 2, 0, 0, 0);
		layout.addView(delButton, delParams);

		return layout;
	}

}
