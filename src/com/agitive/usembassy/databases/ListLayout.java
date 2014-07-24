package com.agitive.usembassy.databases;

import java.util.ArrayList;

import com.agitive.usembassy.interfaces.AdditionalContentInterface;
import com.agitive.usembassy.interfaces.LayoutTypeInterface;

public class ListLayout implements LayoutTypeInterface, AdditionalContentInterface {

	private int id;
	private int parentId;
	private String titleEn;
	private String titlePl;
	private String contentEn;
	private String contentPl;
	private String additionalEn;
	private String additionalPl;
	private ArrayList<ListLayoutItem> listItemsEn;
	private ArrayList<ListLayoutItem> listItemsPl;
	
	public ListLayout(int id, int parentId) { // NO_UCD (use default)
		this.id = id;
		this.parentId = parentId;
		this.titleEn = null;
		this.titlePl = null;
		this.contentEn = null;
		this.contentPl = null;
		this.additionalEn = null;
		this.additionalPl = null;
		this.listItemsEn = new ArrayList<ListLayoutItem>();
		this.listItemsPl = new ArrayList<ListLayoutItem>();
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
	
	public void addListItemEn(ListLayoutItem item) { // NO_UCD (use default)
		this.listItemsEn.add(item);
	}
	
	public void addListItemPl(ListLayoutItem item) { // NO_UCD (use default)
		this.listItemsPl.add(item);
	}
	
	@Override
	public int getLayoutType() {
		return LayoutTypeInterface.LIST_LAYOUT;
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
	
	public ArrayList<ListLayoutItem> getListItemsEn() {
		return this.listItemsEn;
	}
	
	public ArrayList<ListLayoutItem> getListItemsPl() {
		return this.listItemsPl;
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
