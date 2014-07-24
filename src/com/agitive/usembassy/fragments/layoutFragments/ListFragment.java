package com.agitive.usembassy.fragments.layoutFragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.databases.ListLayout;
import com.agitive.usembassy.databases.ListLayoutItem;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.FragmentAboveInterface;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.parsers.CustomContentParser;

public class ListFragment extends Fragment implements LayoutFragmentInterface, FragmentAboveInterface {
	
	
	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.ListFragment.layoutId";
	public static final String IS_IN_STEPS_KEY = "com.agitive.usembassy.fragments.ListFragment.isInStepsKey";
	
	private View rootView;
	private ListLayout listLayout;
	private int id;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    
        this.rootView = inflater.inflate(R.layout.list_layout, container, false);
        
        this.id = 0;
    	
        setLayout();
    	setMarginForEmblem();
        setLayoutName();
        setBackButton();
        setContent(savedInstanceState);
        
        return this.rootView;
	}
	
	@Override
	public void setContent() {
		setCustomContent();
    	createList(this.listLayout);
    	hideMainProgressBar();
	}
	
	private void setContent(Bundle savedInstanceState) {
		if (savedInstanceState == null &&
				!getArguments().getBoolean(ListFragment.IS_IN_STEPS_KEY, false)) {
			return;
		}
		
		setContent();
	}
	
	private void hideMainProgressBar() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.list_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
	}
	
	private void showMainProgressBar() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.list_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.VISIBLE);
	}
	
	private void setCustomContent() {
		String content = getContentByAppLanguage(this.listLayout);
    	
    	CustomContentParser parser = new CustomContentParser(getActivity(), getContentWidth(), this.listLayout.getId());
    	RelativeLayout customContent = parser.parseCustomContent(content);
    	
    	moveViewsToLocalCustomContent(customContent);
	}
	
	private void setLayout() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		int id = getArguments().getInt(ListFragment.LAYOUT_ID_KEY);
		this.listLayout = (ListLayout) databaseReader.getLayout(id);
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	@Override
	public void changeLanguage() {
		showMainProgressBar();
	
		Handler handler = new Handler();
		handler.post(new Runnable () {

			@Override
			public void run() {
				setLayoutName();
				
				RelativeLayout rootLayout = (RelativeLayout) rootView.findViewById(R.id.list_layout_custom_content);
				if (rootLayout == null) {
					return;
				}
				
				rootLayout.removeAllViews();
				
				setCustomContent();
		    	createList(listLayout);
		    	
		    	hideMainProgressBar();
			}
			
		});
	}
	
	private String getContentByAppLanguage(ListLayout listLayout) {
		if (getAppLanguage().equals("EN")) {
    		return listLayout.getContentEn();
    	} else {
    		return listLayout.getContentPl();
    	}
	}
	
	private void moveViewsToLocalCustomContent(RelativeLayout customContent) {
		RelativeLayout localCustomContent = (RelativeLayout) this.rootView.findViewById(R.id.list_layout_custom_content);
		if (localCustomContent == null) {
			return;
		}
		
    	while (customContent.getChildCount() > 0) {
    		View view = customContent.getChildAt(0);
    		customContent.removeViewAt(0); 
    		localCustomContent.addView(view);
    	}
	}
	
	private void createList(final ListLayout listLayout) {
		LinearLayout list = (LinearLayout) this.rootView.findViewById(R.id.list_layout_list);
		if (list == null) {
			return;
		}
		
		list.removeAllViews();
		
		int index = 0;
		for (final ListLayoutItem item: getChildrenByAppLanguage(listLayout)) {
			if (index == 0) {
				ImageView divider = new ImageView(this.getActivity());
				LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) MainActivity.dpToPixel(1, this.getActivity()));
				divider.setLayoutParams(lineParams);
				divider.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.list_item_divider));
				list.addView(divider);
			}
			
			
			RelativeLayout itemLayout = new RelativeLayout(this.getActivity());
			LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			itemLayout.setLayoutParams(itemParams);
			
			int paddingRight = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
			int paddingTopBottom = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
			int paddingLeft = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
			itemLayout.setPadding(paddingLeft, paddingTopBottom, paddingRight, paddingTopBottom);
			
			ImageView arrow = new ImageView(getActivity());
			++this.id;
			arrow.setId(this.id);
			
			RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			arrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			arrowParams.addRule(RelativeLayout.CENTER_VERTICAL);
			arrow.setLayoutParams(arrowParams);
			
			arrow.setImageResource(R.drawable.list_arrow);
			
			itemLayout.addView(arrow);
			
			CustomTextView textView = new CustomTextView(this.getActivity());
			RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			textParams.addRule(RelativeLayout.LEFT_OF, arrow.getId());
			textView.setLayoutParams(textParams);
		
			textView.setTextColor(getActivity().getApplicationContext().getResources().getColor(R.color.list_item));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getApplicationContext().getResources().getDimension(R.dimen.text_normal));
			textView.setTypeface(null, Typeface.BOLD);
			
			textView.setText(item.getName());
			itemLayout.addView(textView);
			
			itemLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((MainActivity)getActivity()).openLayout(item.getId(), listLayout.getId());
				}
			});
			
			list.addView(itemLayout);
			
			View divider = new View(this.getActivity());
			LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) MainActivity.dpToPixel(1, this.getActivity()));
			divider.setLayoutParams(lineParams);
			divider.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.list_item_divider));
			list.addView(divider);
			
			++index;
		}
	}
	
	private ArrayList<ListLayoutItem> getChildrenByAppLanguage(ListLayout listLayout) {
		if (getAppLanguage().equals("EN")) {
    		return listLayout.getListItemsEn();
    	} else {
    		return listLayout.getListItemsPl();
    	}
	}
	
	private void setMarginForEmblem() {
		CustomTextView menuName = (CustomTextView) this.rootView.findViewById(R.id.list_layout_layout_name);
		if (menuName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		menuName.setLayoutParams(params);
	}
	
	private void setLayoutName() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.list_layout_layout_name);
		if (layoutName == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			layoutName.setText(this.listLayout.getTitleEn());
		} else {
			layoutName.setText(this.listLayout.getTitlePl());
		}
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.list_layout_back_button);
		if (backButton == null) {
			return;
		}
		
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().onBackPressed();	
			}
			
		});
	}
	
	public boolean isPortraitOrientation() {
    	return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
	
	@SuppressWarnings("deprecation")
	private int getDisplayWidth() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT < 13) {
			return display.getWidth();
		} else {
			Point size = new Point();
			display.getSize(size);
			
			return size.x;
		}
	}
	
	private int getContentWidth() {
		int marginLeftRight = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge);
    	
    	int contentWidth;
    	if (isPortraitOrientation()) {
    		contentWidth = getDisplayWidth() - 2 * marginLeftRight;
    	} else {
    		contentWidth = (int) ((getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH) - (2 * marginLeftRight));
    	}
    	
    	return contentWidth;
	}
}
