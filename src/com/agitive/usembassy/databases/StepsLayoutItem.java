package com.agitive.usembassy.databases;

public class StepsLayoutItem {
	
	private int id;
	private String name;
	private boolean hasSubmenu;
	
	public StepsLayoutItem(int id, String name, boolean hasSubmenu) {
		this.id = id;
		this.name = name;
		this.hasSubmenu = hasSubmenu;
	}

	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean getHasSubmenu() {
		return this.hasSubmenu;
	}
	
	public String toString() {
		return this.name;
	}
}
