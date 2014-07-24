package com.agitive.usembassy.databases;

import com.agitive.usembassy.interfaces.AdditionalContentInterface;
import com.agitive.usembassy.interfaces.LayoutTypeInterface;

public class CustomContentLayout implements LayoutTypeInterface, AdditionalContentInterface {

	private int id;
	private int parentId;
	private String titleEn;
	private String titlePl;
	private String contentEn;
	private String contentPl;
	private String additionalEn;
	private String additionalPl;
	
	public CustomContentLayout(int id, int parentId) {
		this.id = id;
		this.parentId = parentId;
		this.titleEn = null;
		this.titlePl = null;
		this.contentEn = null;
		this.contentPl = null;
		this.additionalEn = null;
		this.additionalPl = null;
	}
	
	public void setTitleEn(String title) {
		this.titleEn = title;
	}
	
	public void setTitlePl(String title) {
		this.titlePl = title;
	}
	
	public void setContentEn(String content) {
		this.contentEn = content;
	}
	
	public void setContentPl(String content) {
		this.contentPl = content;
	}
	
	public void setAdditionalEn(String additional) {
		this.additionalEn = additional;
	}
	
	public void setAdditionalPl(String additional) {
		this.additionalPl = additional;
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	@Override
	public int getLayoutType() {
		return LayoutTypeInterface.TEXT_LAYOUT;
	}

	@Override
	public int getParentId() {
		return this.parentId;
	}
	
	@Override
	public String getTitleEn() {
		return this.titleEn;
	}
	
	@Override
	public String getTitlePl() {
		return this.titlePl;
	}
	
	public String getContentEn() {
		return this.contentEn;
	}
	
	public String getContentPl() {
		return this.contentPl;
	}
	
	@Override
	public String getAdditionalEn() {
		return this.additionalEn;
	}
	
	@Override
	public String getAdditionalPl() {
		return this.additionalPl;
	}
	
	@Override
	public boolean hasAdditionalContent() {
		if ((this.additionalEn == null ||
				this.additionalEn.equals("")) &&
				(this.additionalPl == null ||
				this.additionalPl.equals(""))) {
			return false;
		} else {
			return true;
		}
	}
}
