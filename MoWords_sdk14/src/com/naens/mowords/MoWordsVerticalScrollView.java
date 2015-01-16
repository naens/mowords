package com.naens.mowords;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MoWordsVerticalScrollView extends ScrollView {

	private VerticalScrollViewListener verticalScrollViewListener;

	public MoWordsVerticalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MoWordsVerticalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MoWordsVerticalScrollView(Context context) {
		super(context);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		verticalScrollViewListener.onScrollChanged(l, t, oldl, oldt);
	}

	public interface VerticalScrollViewListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);
	}

	public VerticalScrollViewListener getVerticalScrollViewListener() {
		return verticalScrollViewListener;
	}

	public void setVerticalScrollViewListener(VerticalScrollViewListener verticalScrollViewListener) {
		this.verticalScrollViewListener = verticalScrollViewListener;
	}

}
