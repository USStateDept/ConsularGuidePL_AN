package com.agitive.usembassy.adapters;

import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.WelcomeActivity;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.layouts.WelcomeScrollView;

public class WelcomePagerAdapter extends PagerAdapter {

	private static final int PAGE_NUMBER = 2;
	private static final double ACCEPT_BUTTON_WIDTH_TO_LAYOUT_WIDTH_PORTRAIT = 0.37;
	private static final double ACCEPT_BUTTON_WIDTH_TO_LAYOUT_WIDTH_LANDSCAPE = 0.28;
	
	private static final double TEXT_WIDTH_TO_LAYOUT_WIDTH_PORTRAIT = 0.78;
	private static final double TEXT_WIDTH_TO_LAYOUT_WIDTH_LANDSCAPE = 0.78;
	
	private WelcomeActivity welcomeActivity;
	private View viewPage2;
	private boolean isAcceptButtonActive;
	
	public WelcomePagerAdapter(WelcomeActivity welcomeActivity) {
		this.welcomeActivity = welcomeActivity;
		this.isAcceptButtonActive = false;
	}
	
	@Override
	public int getCount() {
		return WelcomePagerAdapter.PAGE_NUMBER;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View newView = null;
		
		LayoutInflater inflater = this.welcomeActivity.getLayoutInflater();
		newView = inflater.inflate(getLayoutId(position), null);
		setViewLayout2(newView, position);
		
		setAcceptButtonWidth(newView, position);
		setAcceptButttonOnClickListener(newView, position);
		setAcceptButtonActivity(position);
		setScrollViewListener(newView, position);
		setContentWidth(newView, position);
		
		container.addView(newView);
		
		return newView;
	}
	
	public void activateAcceptButton() { // NO_UCD (use default)
		this.isAcceptButtonActive = true;
		RelativeLayout acceptButtonLayout = (RelativeLayout) this.viewPage2.findViewById(R.id.welcome_page_2_layout_accept_button_layout);
		if (acceptButtonLayout == null) {
			return;
		}
		
		acceptButtonLayout.setBackgroundColor(this.welcomeActivity.getResources().getColor(R.color.welcome_accept_layout_active));
		
		CustomTextView acceptText = (CustomTextView) this.viewPage2.findViewById(R.id.welcome_page_2_layout_accept_text);
		if (acceptText == null) {
			return;
		}
		
		acceptText.setTextColor(this.welcomeActivity.getResources().getColor(R.color.welcome_accept_text_active));
		
		ImageView acceptIcon = (ImageView) this.viewPage2.findViewById(R.id.welcome_page_2_layout_accept_icon);
		if (acceptIcon == null) {
			return;
		}
		
		acceptIcon.setImageResource(R.drawable.accept_arrow_active);
	}
	
	public boolean getIsAcceptButtonActive() {
		return this.isAcceptButtonActive;
	}
	
	public void setIsAcceptButtonActive() {
		this.isAcceptButtonActive = true;
	}
	
	private void setAcceptButtonActivity(int position) {
		if (!this.isAcceptButtonActive) {
			return;
		}
		
		if (position == 0) {
			return;
		}
		
		activateAcceptButton();
	}
	
	private void setAcceptButttonOnClickListener(View view, int position) {
		if (position == 0) {
			return;
		}
		
		final RelativeLayout acceptButtonLayout = (RelativeLayout) view.findViewById(R.id.welcome_page_2_layout_accept_button_layout);
		if (acceptButtonLayout == null) {
			return;
		}
		
		acceptButtonLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isAcceptButtonActive) {
					return;
				}
				
				welcomeActivity.savePrivacyStatementAccept();
				welcomeActivity.animateEmblem();
				acceptButtonLayout.setClickable(false);
			}
			
		});
	}
	
	private void setViewLayout2(View view, int position) {
		if (position == 0) {
			return;
		}
		
		this.viewPage2 = view;
	}
	
	private void setScrollViewListener(View view, int position) {
		if (position == 0) {
			return;
		}
		
		WelcomeScrollView scrollView = (WelcomeScrollView) view.findViewById(R.id.welcome_layout_scroll_view);
		if (scrollView == null) {
			return;
		}
		
		scrollView.setWelcomePagerAdapter(this);
	}
	
	private void setAcceptButtonWidth(View view, int position) {
		if (position == 0) {
			return;
		}
		
		RelativeLayout acceptButtonLayout = (RelativeLayout) view.findViewById(R.id.welcome_page_2_layout_accept_button_layout);
		if (acceptButtonLayout == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) acceptButtonLayout.getLayoutParams();
		params.width = (int) (getDisplayWidth() * getAcceptButtonWidthToLayoutWidth());
		
		view.setLayoutParams(params);
	}
	
	private double getAcceptButtonWidthToLayoutWidth() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return WelcomePagerAdapter.ACCEPT_BUTTON_WIDTH_TO_LAYOUT_WIDTH_PORTRAIT;
		} else {
			return WelcomePagerAdapter.ACCEPT_BUTTON_WIDTH_TO_LAYOUT_WIDTH_LANDSCAPE;
		}
	}
	
	private int getOrientation() {
		return this.welcomeActivity.getResources().getConfiguration().orientation;
	}
	
	private void setPage1ContentWidth(View view) {
		setWelcomeTextWidth(view);
		setSwipeToAcceptTextWidth(view);
	}
	
	private void setWelcomeTextWidth(View view) {
		CustomTextView welcomeText = (CustomTextView) view.findViewById(R.id.welcome_page_1_layout_welcome_text);
		if (welcomeText == null) {
			return;
		}
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) welcomeText.getLayoutParams();
		params.width = (int) (getDisplayWidth() * getTextWidthToLayoutWidth());
		welcomeText.setLayoutParams(params);
	}
	
	private void setSwipeToAcceptTextWidth(View view) {
		CustomTextView swipeToAcceptText = (CustomTextView) view.findViewById(R.id.welcome_page_1_swipe_to_accept_text);
		if (swipeToAcceptText == null) {
			return;
		}
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) swipeToAcceptText.getLayoutParams();
		params.width = (int) (getDisplayWidth() * getTextWidthToLayoutWidth());
		swipeToAcceptText.setLayoutParams(params);
	}
	
	private void setPage2ContentWidth(View view) {
		setRegulationInfoWidth(view);
		setRegulationDividerWidth(view);
		setRegulationWidth(view);	
	}
	
	private void setRegulationDividerWidth(View view) {
		View regulationDivider= view.findViewById(R.id.welcome_page_2_regulation_divider);
		if (regulationDivider == null) {
			return;
		}
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) regulationDivider.getLayoutParams();
		params.width = (int) (getDisplayWidth() * getTextWidthToLayoutWidth());
		regulationDivider.setLayoutParams(params);
	}
	
	private void setRegulationInfoWidth(View view) {
		CustomTextView regulationInfoText = (CustomTextView) view.findViewById(R.id.welcome_page_2_layout_regulation_info_text);
		if (regulationInfoText == null) {
			return;
		}
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) regulationInfoText.getLayoutParams();
		params.width = (int) (getDisplayWidth() * getTextWidthToLayoutWidth());
		regulationInfoText.setLayoutParams(params);
	}
	private void setRegulationPartWidth(View view, int id) {
		CustomTextView regulationTextPart = (CustomTextView) view.findViewById(id);
		if (regulationTextPart == null) {
			return;
		}
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) regulationTextPart.getLayoutParams();
		params.width = (int) (getDisplayWidth() * getTextWidthToLayoutWidth());
		regulationTextPart.setLayoutParams(params);
	}
	
	private void setRegulationWidth(View view) {
		setRegulationPartWidth(view, R.id.welcome_page_2_layout_regulation_text_part_0);
		setRegulationPartWidth(view, R.id.welcome_page_2_layout_regulation_text_part_1);
		setRegulationPartWidth(view, R.id.welcome_page_2_layout_regulation_text_part_2);
		setRegulationPartWidth(view, R.id.welcome_page_2_layout_regulation_text_part_3);
		setRegulationPartWidth(view, R.id.welcome_page_2_layout_regulation_text_part_4);
		setRegulationPartWidth(view, R.id.welcome_page_2_layout_regulation_text_part_5);
		setRegulationPartWidth(view, R.id.welcome_page_2_layout_regulation_text_part_6);
	}
	
	private void setContentWidth(View view, int position) {
		if (position == 0) {
			setPage1ContentWidth(view);
		} else {
			setPage2ContentWidth(view);
		}
	}
	
	private double getTextWidthToLayoutWidth() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return WelcomePagerAdapter.TEXT_WIDTH_TO_LAYOUT_WIDTH_PORTRAIT;
		} else {
			return WelcomePagerAdapter.TEXT_WIDTH_TO_LAYOUT_WIDTH_LANDSCAPE;
		}
	}
	
	private int getLayoutId(int position) {
		if (position == 0) {
			return R.layout.welcome_page_1_layout;
		} else {
			return R.layout.welcome_page_2_layout;
		}
	}
	
	@SuppressWarnings("deprecation")
	private int getDisplayWidth() {
		Display display = this.welcomeActivity.getWindowManager().getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT < 13) {
			return display.getWidth();
		} else {
			Point size = new Point();
			display.getSize(size);
			
			return size.x;
		}
	}
}
