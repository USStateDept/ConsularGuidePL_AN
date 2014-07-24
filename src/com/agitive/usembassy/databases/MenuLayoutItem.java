package com.agitive.usembassy.databases;

public class MenuLayoutItem {
	
	private String name;
	private int pathToId;
	private boolean hasSubmenu;
	
	public MenuLayoutItem(String name, int pathToId, boolean hasSubmenu) { // NO_UCD (use default)
		this.name = name;
		this.pathToId = pathToId;
		this.hasSubmenu = hasSubmenu;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getPathToId() {
		return this.pathToId;
	}
	
	public boolean getHasSubmenu() {
		return this.hasSubmenu;
	}
	
	public String toString() {
		return this.name;
	}
}
