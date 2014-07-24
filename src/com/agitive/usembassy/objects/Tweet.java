package com.agitive.usembassy.objects;

public class Tweet {

	private long id;
	private String text;
	
	public Tweet(long id) {
		this.id = id;
		this.text = "";
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public long getId() {
		return this.id;
	}
	
	public String getText() {
		return this.text;
	}
}
