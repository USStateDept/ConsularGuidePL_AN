package com.agitive.usembassy.databases;

import java.util.ArrayList;

import com.agitive.usembassy.interfaces.LayoutTypeInterface;

public class MenuLayout implements LayoutTypeInterface {

	private int id;
	private int parentId; 
	private String titleEn;
	private String titlePl;
	private ArrayList<MenuLayoutItem> menuEn;
	private ArrayList<MenuLayoutItem> menuPl;
	
	public MenuLayout(int id, int parentId) { // NO_UCD (use default)
		this.id = id;
		this.parentId = parentId;
		this.titleEn = null;
		this.titlePl = null;
		this.menuEn = new ArrayList<MenuLayoutItem>();
		this.menuPl = new ArrayList<MenuLayoutItem>();
	}
	
	public void setTitleEn(String title) {
		this.titleEn = title;
	}
	
	public void setTitlePl(String title) {
		this.titlePl = title;
	}
	
	public void addMenuItemEn(MenuLayoutItem item) { // NO_UCD (use default)
		this.menuEn.add(item);
	}
	
	public void addMenuItemPl(MenuLayoutItem item) { // NO_UCD (use default)
		this.menuPl.add(item);
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	@Override
	public int getLayoutType() {
		return LayoutTypeInterface.MENU_LAYOUT;
	}
	
	@Override
	public String getTitleEn() {
		return this.titleEn;
	}
	
	@Override
	public String getTitlePl() {
		return this.titlePl;
	}
	
	public ArrayList<MenuLayoutItem> getMenuItemsEn() {
		return this.menuEn;
	}
	
	public ArrayList<MenuLayoutItem> getMenuItemsPl() {
		return this.menuPl;
	}

	@Override
	public int getParentId() {
		return this.parentId;
	}
	
	@Override
	public boolean hasAdditionalContent() {
		return false;
	}
}
