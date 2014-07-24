package com.agitive.usembassy.databases;

public class ListLayoutItem {
	
	private String name;
	private int id;
	
	public ListLayoutItem(int id, String name) { // NO_UCD (use default)
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
}
