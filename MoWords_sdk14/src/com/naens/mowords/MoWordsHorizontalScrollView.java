package com.naens.mowords;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class MoWordsHorizontalScrollView extends HorizontalScrollView {

	private HorizontalScrollViewListener horizontalScrollViewListener;

	public MoWordsHorizontalScrollView(Context context) {
		super(context);
	}

	

	public MoWordsHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}



	public MoWordsHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		horizontalScrollViewListener.onScrollChanged(l, t, oldl, oldt);
	}

	public interface HorizontalScrollViewListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);
	}

	public HorizontalScrollViewListener getHorizontalScrollViewListener() {
		return horizontalScrollViewListener;
	}

	public void setHorizontalScrollViewListener(HorizontalScrollViewListener horizontalScrollViewListener) {
		this.horizontalScrollViewListener = horizontalScrollViewListener;
	}
}
