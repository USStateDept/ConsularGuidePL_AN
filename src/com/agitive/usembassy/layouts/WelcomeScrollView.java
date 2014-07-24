package com.agitive.usembassy.layouts;

import com.agitive.usembassy.adapters.WelcomePagerAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class WelcomeScrollView extends ScrollView { // NO_UCD (use default)
	
	private WelcomePagerAdapter welcomePagerAdapter;

	public WelcomeScrollView(Context context) { // NO_UCD (unused code)
		super(context);
	}
	
	public WelcomeScrollView(Context context, AttributeSet attrs) { // NO_UCD (unused code)
		super(context, attrs);
	}
	
	public WelcomeScrollView(Context context, AttributeSet attrs, int defStyle) { // NO_UCD (unused code)
		super(context, attrs, defStyle);
	}
	
	public void setWelcomePagerAdapter(WelcomePagerAdapter welcomePagerAdapter) {
		this.welcomePagerAdapter = welcomePagerAdapter;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		
		View view = (View) getChildAt(getChildCount() - 1);
		int diff = (view.getBottom() - getHeight() - getScrollY());
		if (diff <= 0) {
			this.welcomePagerAdapter.activateAcceptButton();
		}
	}
}
