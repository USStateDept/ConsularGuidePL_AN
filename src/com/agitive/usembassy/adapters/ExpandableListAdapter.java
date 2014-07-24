package com.agitive.usembassy.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import com.agitive.usembassy.R;
import com.agitive.usembassy.databases.MenuLayoutItem;
import com.agitive.usembassy.layouts.CustomTextView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
	
	public static final int HOME_POSITION = 0;
	//public static final int VISA_APPLICATION_STATUS_POSITION = 5;TODO
	public static final int PASSPORT_TRACKING_POSITION = 5;
	
	private ArrayList<String> headers;
	private HashMap<String, ArrayList<MenuLayoutItem>> structure;
	private Activity activity;

	public ExpandableListAdapter(ArrayList<String> headers, HashMap<String, ArrayList<MenuLayoutItem>> structure, Activity activity) {
		this.headers = headers;
		this.structure = structure;
		this.activity = activity;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.structure.get(this.headers.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		MenuLayoutItem child = (MenuLayoutItem) getChild(groupPosition, childPosition);
		
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.menu_list_child_layout, null);
		}
		
		CustomTextView childName = (CustomTextView) convertView.findViewById(R.id.menu_list_child_layout_child_name);
		if (childName == null) {
			return null;
		}
		
		childName.setText(child.getName());
		
		setMoreIconVisibilityInChild(child, convertView);
		
		return convertView;
 	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.structure.get(this.headers.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.headers.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.headers.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String header = (String) getGroup(groupPosition);
		
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.menu_list_header_layout, null);
		}
		
		CustomTextView headerName = (CustomTextView) convertView.findViewById(R.id.menu_list_header_layout_header_name);
		if (headerName == null) {
			return null;
		}
		
		headerName.setText(header);
		
		setMoreIconVisibilityInGroup(groupPosition, convertView);
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private void setMoreIconVisibilityInChild(MenuLayoutItem child, View convertView) {
		ImageView moreIcon = (ImageView) convertView.findViewById(R.id.menu_list_child_layout_more_icon);
		if (moreIcon == null) {
			return;
		}
		
		if (child.getHasSubmenu()) {
			moreIcon.setVisibility(ImageView.VISIBLE);
		} else {
			moreIcon.setVisibility(ImageView.INVISIBLE);
		}
	}
	
	private void setMoreIconVisibilityInGroup(int groupPosition, View convertView) {
		ImageView moreIcon = (ImageView) convertView.findViewById(R.id.menu_list_header_layout_more_icon);
		if (moreIcon == null) {
			return;
		}
		
		if (groupPosition == ExpandableListAdapter.HOME_POSITION ||
				//groupPosition == ExpandableListAdapter.VISA_APPLICATION_STATUS_POSITION ||
				groupPosition == ExpandableListAdapter.PASSPORT_TRACKING_POSITION) {
			moreIcon.setVisibility(ImageView.INVISIBLE);
		} else {
			moreIcon.setVisibility(ImageView.VISIBLE);
		}
	}
}
