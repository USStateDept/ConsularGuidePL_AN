package com.agitive.usembassy.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {

	public CustomTextView(Context context) {
		super(context);
		
		if (isInEditMode()) {
			return;
		}
		
		setFont(-1);
	}
	
	public CustomTextView(Context context, AttributeSet attrs) { // NO_UCD (unused code)
		super(context, attrs);
		
		if (isInEditMode()) {
			return;
		}
		
		int style = -1;
		if (attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textStyle") != null) {
			style = Integer.parseInt(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textStyle").replaceAll("0x", ""), 16);
			
		}
		
		setFont(style);
	}
	
	public CustomTextView(Context context, AttributeSet attrs, int defStyle) { // NO_UCD (unused code)
		super(context, attrs, defStyle);
		
		if (isInEditMode()) {
			return;
		}
		
		int style = -1;
		if (attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textStyle") != null) {
			style = Integer.parseInt(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textStyle").replaceAll("0x", ""), 16);
			
		}
		
		setFont(style);
	}
	
	private void setFont(int style) {
		Typeface typeface = Typeface.DEFAULT;
		setTypeface(typeface, style);
	}

	protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);  
    }
	
	@Override
	public void setTypeface(Typeface typeface, int style) {
		Typeface font = Typeface.DEFAULT;
		
		if (style == Typeface.NORMAL) {
			super.setTypeface(font);
		} else if (style == Typeface.BOLD) {
			super.setTypeface(font, Typeface.BOLD);
		}
	}
}
