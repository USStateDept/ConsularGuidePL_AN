package com.agitive.usembassy.adapters;

import java.util.ArrayList;

import com.agitive.usembassy.databases.CustomContentLayout;
import com.agitive.usembassy.databases.ListLayout;
import com.agitive.usembassy.databases.MapLayout;
import com.agitive.usembassy.fragments.layoutFragments.CustomContentFragment;
import com.agitive.usembassy.fragments.layoutFragments.ListFragment;
import com.agitive.usembassy.fragments.layoutFragments.MapFragment;
import com.agitive.usembassy.interfaces.LayoutTypeInterface;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class StepsViewPagerAdapter extends FragmentStatePagerAdapter {

	private ArrayList<LayoutTypeInterface> steps;
	
	public StepsViewPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}
	
	public void setSteps(ArrayList<LayoutTypeInterface> steps) {
		this.steps = steps;
	}

	@Override
	public Fragment getItem(int position) {
		switch (this.steps.get(position).getLayoutType()) {
			case LayoutTypeInterface.TEXT_LAYOUT:
				CustomContentFragment customContentFragment = new CustomContentFragment();
				customContentFragment.setArguments(createArgumentsForCustomContentFragment((CustomContentLayout) this.steps.get(position), position));
				
				return customContentFragment;
			case LayoutTypeInterface.CONTACT_LAYOUT:
				MapFragment mapFragment = new MapFragment();
				mapFragment.setArguments(createArgumentsForMapFragment((MapLayout) this.steps.get(position)));
			
				return mapFragment;
			case LayoutTypeInterface.LIST_LAYOUT:
				ListFragment listFragment = new ListFragment();
				listFragment.setArguments(createArgumentsForListFragment((ListLayout) this.steps.get(position)));
				
				return listFragment;
		}
		
		return null;
	}

	@Override
	public int getCount() {
		return this.steps.size();
	}

	private Bundle createArgumentsForCustomContentFragment(CustomContentLayout layout, int position) {
		Bundle arguments = new Bundle();
		arguments.putInt(CustomContentFragment.LAYOUT_ID_KEY, layout.getId());
		arguments.putBoolean(CustomContentFragment.IS_IN_STEPS_KEY, true);
		
		return arguments;
	}
	
	private Bundle createArgumentsForMapFragment(MapLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(MapFragment.LAYOUT_ID_KEY, layout.getId());
		arguments.putBoolean(MapFragment.IS_IN_STEPS_KEY, true);
		
		return arguments;
	}
	private Bundle createArgumentsForListFragment(ListLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(ListFragment.LAYOUT_ID_KEY, layout.getId());
		arguments.putBoolean(ListFragment.IS_IN_STEPS_KEY, true);
		
		return arguments;
	}
}
