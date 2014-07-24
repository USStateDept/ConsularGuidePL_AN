package com.agitive.usembassy.databases;

import com.agitive.usembassy.interfaces.LayoutTypeInterface;

public class VideosLayout implements LayoutTypeInterface {

	private int id;
	private int parentId;
	private String titleEn;
	private String titlePl;
	
	public VideosLayout(int id, int parentId) { // NO_UCD (use default)
		this.id = id;
		this.parentId = parentId;
		this.titleEn = null;
		this.titlePl = null;
	}
	
	public void setTitleEn(String titleEn) {
		this.titleEn = titleEn;
	}
	
	public void setTitlePl(String titlePl) {
		this.titlePl = titlePl;
	}
	
	@Override
	public int getLayoutType() {
		return LayoutTypeInterface.VIDEOS_LAYOUT;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public int getParentId() {
		return this.parentId;
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
