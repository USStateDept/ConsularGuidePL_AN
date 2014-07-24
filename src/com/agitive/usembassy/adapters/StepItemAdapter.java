package com.agitive.usembassy.adapters;

import java.util.ArrayList;

import com.agitive.usembassy.R;
import com.agitive.usembassy.databases.StepsLayoutItem;
import com.agitive.usembassy.layouts.CustomTextView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class StepItemAdapter extends BaseAdapter {
	
	private ArrayList<StepsLayoutItem> items;
	private StepsLayoutItem item0;
	private Activity activity;

	public StepItemAdapter(ArrayList<StepsLayoutItem> items, StepsLayoutItem item0, Activity activity) {
		this.items = items;
		this.item0 = item0;
		this.activity = activity;
	}
	
	@Override
	public int getCount() {
		return this.items.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position == 0) {
			return item0;
		} else {
			return this.items.get(position - 1);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		StepsLayoutItem item = (StepsLayoutItem) getItem(position);
		
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.steps_list_item_layout, null);
		}
		
		CustomTextView itemName = (CustomTextView) convertView.findViewById(R.id.steps_list_item_layout_item_name);
		if (itemName == null) {
			return null;
		}
		
		itemName.setText(item.getName());
		
		setMoreIconVisibility(item, convertView);
		
		return convertView;
	}
	
	private void setMoreIconVisibility(StepsLayoutItem item, View convertView) {
		ImageView moreIcon = (ImageView) convertView.findViewById(R.id.steps_list_item_layout_icon);
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
