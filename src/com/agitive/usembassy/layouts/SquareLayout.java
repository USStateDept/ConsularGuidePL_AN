package com.agitive.usembassy.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareLayout extends RelativeLayout{
	
	public SquareLayout(Context context) { // NO_UCD (unused code)
		super(context);
	}
	
	public SquareLayout(Context context, AttributeSet attrs) { // NO_UCD (unused code)
		super(context, attrs);
	}
	
	public SquareLayout(Context context, AttributeSet attrs, int defStyle) { // NO_UCD (unused code)
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		
	    int width = MeasureSpec.getSize(widthMeasureSpec);
	    int height = MeasureSpec.getSize(heightMeasureSpec);
	    int size = Math.min(width, height);
	    setMeasuredDimension(size, size);
	}
}
