package com.agitive.usembassy.adapters;

import java.util.ArrayList;

import com.agitive.usembassy.R;
import com.agitive.usembassy.databases.MenuLayoutItem;
import com.agitive.usembassy.layouts.CustomTextView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ManuItemAdapter extends BaseAdapter{

	private ArrayList<MenuLayoutItem> items;
	private Activity activity;
	
	public ManuItemAdapter(ArrayList<MenuLayoutItem> items, Activity activity) {
		this.items = items;
		this.activity = activity;
	}
	
	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public Object getItem(int position) {
		return this.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MenuLayoutItem item = (MenuLayoutItem) getItem(position);
		
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.menu_fragment_list_item_layout, null);
		}
		
		CustomTextView itemName = (CustomTextView) convertView.findViewById(R.id.menu_fragment_list_item_layout_item_name);
		if (itemName == null) {
			return null;
		}
		
		itemName.setText(item.getName());
		
		setMoreIconVisibility(item, convertView);
		
		return convertView;
	}
	
	private void setMoreIconVisibility(MenuLayoutItem item, View convertView) {
		ImageView moreIcon = (ImageView) convertView.findViewById(R.id.menu_fragment_list_item_layout_icon);
		if (moreIcon == null) {
			return;
		}
		
		if (item.getHasSubmenu()) {
			moreIcon.setVisibility(ImageView.VISIBLE);
		} else {
			moreIcon.setVisibility(ImageView.INVISIBLE);
		}
	}
}
