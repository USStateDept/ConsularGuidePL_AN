package com.agitive.usembassy.objects;

public class DayMonthYearDate {
	private int day;
	private int month;
	private int year;
	
	public DayMonthYearDate(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	public int getDay() {
		return this.day;
	}
	
	public int getMonth() {
		return this.month;
	}
	
	public int getYear() {
		return this.year;
	}
}
