package com.agitive.usembassy.databases;

import com.agitive.usembassy.interfaces.LayoutTypeInterface;

public class MainScreenLayout implements LayoutTypeInterface {

	private static final int ID = 0;
	
	public MainScreenLayout() {
	}
	
	@Override
	public int getLayoutType() {
		return LayoutTypeInterface.MAIN_SCREEN_LAYOUT;
	}

	@Override
	public int getParentId() {
		return 0;
	}

	@Override
	public int getId() {
		return MainScreenLayout.ID;
	}

	@Override
	public boolean hasAdditionalContent() {
		return false;
	}
	
	@Override
	public String getTitleEn() {
		return null;
	}
	
	@Override
	public String getTitlePl() {
		return null;
	}
}
