package com.agitive.usembassy.databases;

import com.agitive.usembassy.interfaces.LayoutTypeInterface;

public class PassportTrackingLayout implements LayoutTypeInterface {

	private int id;
	private int parentId;
	private String titleEn;
	private String titlePl;
	
	public PassportTrackingLayout(int id, int parentId) { // NO_UCD (use default)
		this.id = id;
		this.parentId = parentId;
		this.titleEn = null;
		this.titlePl = null;
	}
	
	public void setTitleEn(String title) {
		this.titleEn = title;
	}
	
	public void setTitlePl(String title) {
		this.titlePl = title;
	}
	
	@Override
	public int getLayoutType() {
		return LayoutTypeInterface.PASSPORT_TRACKING_LAYOUT;
	}

	@Override
	public int getParentId() {
		return this.parentId;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public boolean hasAdditionalContent() {
		return false;
	}
	
	@Override
	public String getTitleEn() {
		return this.titleEn;
	}
	
	@Override
	public String getTitlePl() {
		return this.titlePl;
	}
}
