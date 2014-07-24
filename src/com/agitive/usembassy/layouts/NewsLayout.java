package com.agitive.usembassy.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class NewsLayout extends RelativeLayout {
	
	private static final double NEWS_LAYOUT_HEIGHT_TO_NEWS_LAYOUT_WIDTH = 0.65;
	
	public NewsLayout(Context context) { // NO_UCD (unused code)
		super(context);
	}
	
	public NewsLayout(Context context, AttributeSet attrs) { // NO_UCD (unused code)
		super(context, attrs);
	}
	
	public NewsLayout(Context context, AttributeSet attrs, int defStyle) { // NO_UCD (unused code)
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = (int) (width * NewsLayout.NEWS_LAYOUT_HEIGHT_TO_NEWS_LAYOUT_WIDTH);
	
	    setMeasuredDimension(width, height);
	}
	
	public int getOrientation() {
    	return getResources().getConfiguration().orientation;
    }
}
