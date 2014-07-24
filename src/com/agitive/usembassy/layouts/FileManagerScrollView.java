package com.agitive.usembassy.layouts;

import com.agitive.usembassy.fragments.layoutFragments.FileManagerFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class FileManagerScrollView extends ScrollView {
	
	private Fragment fragment;

	public FileManagerScrollView(Context context) {
		super(context);
	}
	
	public FileManagerScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public FileManagerScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (((FileManagerFragment)this.fragment).getCanScroll()) {
				return super.onTouchEvent(event);
			}
			
			return false;
		}
		
		return super.onTouchEvent(event);
    }
	
	@Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (((FileManagerFragment)this.fragment).getCanScroll()) {
        	return super.onInterceptTouchEvent(event);
        }
        
        return false;
    }
}
