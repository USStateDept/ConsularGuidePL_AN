package com.agitive.usembassy.fragments.layoutFragments;

import java.util.ArrayList;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.adapters.StepItemAdapter;
import com.agitive.usembassy.databases.CustomContentLayout;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.databases.StepsLayout;
import com.agitive.usembassy.databases.StepsLayoutItem;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.layouts.CustomTextView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class StepsFragment extends Fragment implements LayoutFragmentInterface {

	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.layoutFragments.StepsFragment.layoutId";
	
	private View rootView;
	private StepsLayout stepsLayout;
	private boolean openStep0;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    
        this.rootView = inflater.inflate(R.layout.steps_fragment_layout, container, false);
        
        setLayout();
        setStep0();
        
        if (this.openStep0) {
        	
        	return null;
        }
        
        setMarginForEmblem();
        setMenuName();
        setBackButton();
        setMenu();
        
        return rootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		if (this.openStep0) {
			((MainActivity)getActivity()).runHandler(MainActivity.HANDLER_MESSAGE_NORMAL_RUN_FOR_STEP_0);
		} else {
			((MainActivity)getActivity()).runHandler(MainActivity.HANDLER_MESSAGE_NORMAL_RUN);
		}
	}
	
	@Override
	public void changeLanguage() {
		setMenuName();
		setMenuItems();
	}
	
	private void setLayout() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		int id = getArguments().getInt(StepsFragment.LAYOUT_ID_KEY);
		this.stepsLayout = (StepsLayout) databaseReader.getLayout(id);
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.steps_fragment_layout_back_button);
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
	
	private void setMenuName() {
		CustomTextView menuName = (CustomTextView) this.rootView.findViewById(R.id.steps_fragment_layout_menu_name);
		if (menuName == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			menuName.setText(this.stepsLayout.getTitleEn());
		} else {
			menuName.setText(this.stepsLayout.getTitlePl());
		}
		
	}
	
	private void setMarginForEmblem() {
		CustomTextView menuName = (CustomTextView) this.rootView.findViewById(R.id.steps_fragment_layout_menu_name);
		if (menuName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		menuName.setLayoutParams(params);
	}
	
	private void setMenuItems() {
		ArrayList<StepsLayoutItem> items;
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			items = this.stepsLayout.getItemsEn();
		} else {
			items = this.stepsLayout.getItemsPl();
		}
		
		StepItemAdapter listAdapter = new StepItemAdapter(items, createStep0ItemLayout(), getActivity());
		ListView list = (ListView) this.rootView.findViewById(R.id.steps_fragment_layout_list);
		if (list == null) {
			return;
		}
		
		list.setAdapter(listAdapter);
	}
	
	private void setMenu() {
		setMenuItems();
		setOnItemClickListenerInMenu();
	}
	
	private String getStep0Title() {
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return this.stepsLayout.getTitleEn();
		} else {
			return this.stepsLayout.getTitlePl();
		}
	}
	
	private StepsLayoutItem createStep0ItemLayout() {
		StepsLayoutItem stepLayoutItem = new StepsLayoutItem(-this.stepsLayout.getId(), getStep0Title(), false);
		
		return stepLayoutItem;
	}
	
	private void setStep0() {
		CustomContentLayout customContentLayout = new CustomContentLayout(-this.stepsLayout.getId(), this.stepsLayout.getId());
		customContentLayout.setTitleEn(this.stepsLayout.getTitleEn());
		customContentLayout.setTitlePl(this.stepsLayout.getTitlePl());
		customContentLayout.setContentEn(this.stepsLayout.getContentEn());
		customContentLayout.setContentPl(this.stepsLayout.getContentPl());
		customContentLayout.setAdditionalEn(this.stepsLayout.getAdditionalEn());
		customContentLayout.setAdditionalPl(this.stepsLayout.getAdditionalPl());
	}
	
	private void setOnItemClickListenerInMenu() {
		ListView list = (ListView) this.rootView.findViewById(R.id.steps_fragment_layout_list);
		if (list == null) {
			return;
		}
		
		final MainActivity mainActivity = (MainActivity) getActivity();
		list.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StepsLayoutItem item = (StepsLayoutItem) parent.getItemAtPosition(position);
				mainActivity.openLayout(item.getId(), stepsLayout.getId());
			}
		});
	}
	
}
