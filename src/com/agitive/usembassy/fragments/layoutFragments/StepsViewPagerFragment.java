package com.agitive.usembassy.fragments.layoutFragments;

import java.util.ArrayList;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.adapters.StepsViewPagerAdapter;
import com.agitive.usembassy.databases.CustomContentLayout;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.databases.StepsLayout;
import com.agitive.usembassy.interfaces.FragmentAboveInterface;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.interfaces.LayoutTypeInterface;
import com.viewpagerindicator.CirclePageIndicator;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class StepsViewPagerFragment extends Fragment implements LayoutFragmentInterface, FragmentAboveInterface {
	
	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.layoutFragments.StepsViewPagerFragment.layoutId";
	
	private View rootView;
	private LayoutTypeInterface step;
	private ArrayList<LayoutTypeInterface> steps;
	private boolean isStartStepOpenFirstTime;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    
        this.rootView = inflater.inflate(R.layout.steps_view_pager_layout, container, false);
        this.isStartStepOpenFirstTime = true;
        
        setLayout();
        setContent(savedInstanceState);
        
        
        return this.rootView;
	}
	
	@Override
	public void changeLanguage() {
		showMainProgressBar();
		
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.steps_view_pager_layout_view_pager);
				if (viewPager == null) {
					return;
				}
				
				int currentItem = viewPager.getCurrentItem();
				setViewPager();
				setCurrentItemInViewPager(currentItem);
				hideMainProgressBar();
			}
			
		});
	}
	
	@Override
	public void setContent() {
		setViewPager();
        setPageIndicator();
        setCurrentItemInViewPager(getCurrentItemPosition(setStepsFragmentsInList(), this.step));
        hideMainProgressBar();
	}
	
	private void hideMainProgressBar() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.steps_view_pager_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
	}
	
	private void showMainProgressBar() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.steps_view_pager_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.VISIBLE);
	}
	
	private void setContent(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		
		setContent();
	}
	
	private void setLayout() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		int id = getArguments().getInt(StepsViewPagerFragment.LAYOUT_ID_KEY);
		this.step = databaseReader.getLayout(id);
	}
	
	private void setViewPager() {
		ViewPager viewPager = (ViewPager) this.rootView.findViewById(R.id.steps_view_pager_layout_view_pager);
		if (viewPager == null) {
			return;
		}
		
		StepsViewPagerAdapter stepsViewPagerAdapter = new StepsViewPagerAdapter(getChildFragmentManager());
		
		this.steps = setStepsFragmentsInList();
		stepsViewPagerAdapter.setSteps(this.steps);
		
		viewPager.setAdapter(stepsViewPagerAdapter);
	}
	
	private void setPageIndicator() {
		CirclePageIndicator pageIndicator = (CirclePageIndicator) this.rootView.findViewById(R.id.steps_view_pager_layout_page_indicator);
		if (pageIndicator == null) {
			return;
		}
		
		ViewPager viewPager = (ViewPager) this.rootView.findViewById(R.id.steps_view_pager_layout_view_pager);
		if (viewPager == null) {
			return;
		}
		
		pageIndicator.setViewPager(viewPager);
		pageIndicator.setStrokeColor(getResources().getColor(R.color.main_theme));
		pageIndicator.setFillColor(getResources().getColor(R.color.main_theme));
		pageIndicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {	
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				if (steps.get(position).getId() == step.getId() &&
						isStartStepOpenFirstTime) {
					isStartStepOpenFirstTime = false;
					return;
				}
				((MainActivity)getActivity()).openAddtionalContent(steps.get(position).getId());
			}
			
		});
	}
	
	private void setCurrentItemInViewPager(int number) {
		ViewPager viewPager = (ViewPager) this.rootView.findViewById(R.id.steps_view_pager_layout_view_pager);
		if (viewPager == null) {
			return;
		}
		
		viewPager.setCurrentItem(number);
	}

	private ArrayList<LayoutTypeInterface> setStepsFragmentsInList() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		
		LayoutTypeInterface parent = databaseReader.getLayout(this.step.getParentId());
		
		ArrayList<LayoutTypeInterface> children = new ArrayList<LayoutTypeInterface>();
		
		if (parent.getLayoutType() == LayoutTypeInterface.STEPS_LAYOUT) {
			CustomContentLayout step0 = createStep0Layout((StepsLayout) parent);
			children.add(step0);
		}
		
		children.addAll(databaseReader.getChildren(parent.getId()));

		return children;
	}
	
	private CustomContentLayout createStep0Layout(StepsLayout layout) {
		CustomContentLayout customContentLayout = new CustomContentLayout(-layout.getId(), layout.getId());
		customContentLayout.setTitleEn(layout.getTitleEn());
		customContentLayout.setTitlePl(layout.getTitlePl());
		customContentLayout.setContentEn(layout.getContentEn());
		customContentLayout.setContentPl(layout.getContentPl());
		customContentLayout.setAdditionalEn(layout.getAdditionalEn());
		customContentLayout.setAdditionalPl(layout.getAdditionalPl());
		
		return customContentLayout;
	}
	
	private int getCurrentItemPosition(ArrayList<LayoutTypeInterface> items, LayoutTypeInterface item) {
		int position = 0;
		for (LayoutTypeInterface checkedItem: items) {
			
			if (checkedItem.getId() == item.getId()) {
				
				return position;
			}
			
			++position;
		}

		return -1;
	}
}
