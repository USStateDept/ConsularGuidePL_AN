package com.agitive.usembassy.fragments.layoutFragments;

import java.util.ArrayList;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.adapters.ManuItemAdapter;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.databases.MenuLayout;
import com.agitive.usembassy.databases.MenuLayoutItem;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.layouts.CustomTextView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MenuFragment extends Fragment implements LayoutFragmentInterface {

	
	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.MenuFragment.layoutId";
	
	private View rootView;
	private MenuLayout menuLayout;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    
        this.rootView = inflater.inflate(R.layout.menu_fragment_layout, container, false);
        
        setLayout();
        setMarginForEmblem();
        setMenuName();
        setBackButton();
        setMenu();
        
        return rootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		((MainActivity)getActivity()).runHandler(MainActivity.HANDLER_MESSAGE_NORMAL_RUN);
	}
	
	@Override
	public void changeLanguage() {
		setMenuName();
		setMenuItems();
	}
	
	private void setLayout() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		int id = getArguments().getInt(MenuFragment.LAYOUT_ID_KEY);
		this.menuLayout = (MenuLayout) databaseReader.getLayout(id);
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.menu_fragment_layout_back_button);
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
		CustomTextView menuName = (CustomTextView) this.rootView.findViewById(R.id.menu_fragment_layout_menu_name);
		if (menuName == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			menuName.setText(this.menuLayout.getTitleEn());
		} else {
			menuName.setText(this.menuLayout.getTitlePl());
		}
	}
	
	private void setMarginForEmblem() {
		CustomTextView menuName = (CustomTextView) this.rootView.findViewById(R.id.menu_fragment_layout_menu_name);
		if (menuName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		menuName.setLayoutParams(params);
	}
	
	private void setMenuItems() {
		ArrayList<MenuLayoutItem> items;
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			items = this.menuLayout.getMenuItemsEn();
		} else {
			items = this.menuLayout.getMenuItemsPl();
		}
		
		ManuItemAdapter listAdapter = new ManuItemAdapter(items, getActivity());
		ListView list = (ListView) this.rootView.findViewById(R.id.menu_fragment_layout_list);
		if (list == null) {
			return;
		}
		
		list.setAdapter(listAdapter);
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private void setMenu() {
		setMenuItems();
		setOnItemClickListenerInMenu();
	}
	
	private void setOnItemClickListenerInMenu() {
		ListView list = (ListView) this.rootView.findViewById(R.id.menu_fragment_layout_list);
		if (list == null) {
			return;
		}
		
		final MainActivity mainActivity = (MainActivity) getActivity();
		list.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MenuLayoutItem item = (MenuLayoutItem) parent.getItemAtPosition(position);
				
				mainActivity.openLayout(item.getPathToId(), menuLayout.getId());
			}
		});
	}
}
