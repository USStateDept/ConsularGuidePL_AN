package com.agitive.usembassy.layouts;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class TweetLayout extends RelativeLayout {
	
	private static final double TWEET_LAYOUT_HEIGHT_TO_TWEET_LAYOUT_HEIGHT_PORTRAIT = 0.325;
	private static final double TWEET_LAYOUT_HEIGHT_TO_TWEET_LAYOUT_HEIGHT_LANDSCAPE = 0.144;
	
	public TweetLayout(Context context) { // NO_UCD (unused code)
		super(context);
	}
	
	public TweetLayout(Context context, AttributeSet attrs) { // NO_UCD (unused code)
		super(context, attrs);
	}
	
	public TweetLayout(Context context, AttributeSet attrs, int defStyle) { // NO_UCD (unused code)
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = (int) (width * getTweetLayoutHeightToTweetLayoutHeight());
	   
	    setMeasuredDimension(width, height);
	}
	
	private double getTweetLayoutHeightToTweetLayoutHeight() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return TweetLayout.TWEET_LAYOUT_HEIGHT_TO_TWEET_LAYOUT_HEIGHT_PORTRAIT;
		} else {
			return TweetLayout.TWEET_LAYOUT_HEIGHT_TO_TWEET_LAYOUT_HEIGHT_LANDSCAPE;
		}
	}
	
	private int getOrientation() {
		return getResources().getConfiguration().orientation;
	}
}
