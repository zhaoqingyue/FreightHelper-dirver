package com.yunbaba.freighthelper.bean.eventbus;

public class TravelTaskEvent {
	
	public final int year;
	public final int month;
	public final int day;
	
	public TravelTaskEvent(int year, int month, int day) {  
		this.year = year;
		this.month = month;
		this.day = day;
    } 

}
