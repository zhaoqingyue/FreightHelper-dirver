package com.yunbaba.freighthelper.bean;

import com.yunbaba.freighthelper.utils.DateUtils;

public class CustomDate {
	public int year;
	public int month;
	public int day;
	public int week;
	public boolean isTravel = false;
	public MonthState monthState;
	public DayState dayState;
	
	public CustomDate(int year, int month, int day) {
		if (month > 12) {
			month = 1;
			year++;
		} else if (month < 1) {
			month = 12;
			year--;
		}
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public CustomDate() {
		this.year = DateUtils.getYear();
		this.month = DateUtils.getMonth();
		this.day = DateUtils.getCurMonthDay();
	}

	public static CustomDate buildDay(CustomDate date, int day) {
		CustomDate modifiDate = new CustomDate(date.year, date.month, day);
		return modifiDate;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}
	
	public boolean getIsTravel() {
		return isTravel;
	}

	public void setIsTravel(boolean isTravel) {
		this.isTravel = isTravel;
	}
	
	public MonthState getMonthState() {
		return monthState;
	}

	public void setMonthState(MonthState monthState) {
		this.monthState = monthState;
	}
	
	public DayState getDayState() {
		return dayState;
	}

	public void setDayState(DayState dayState) {
		this.dayState = dayState;
	}
	
	public enum MonthState {
		NONE,
		PRE_MONTH, 
		CUR_MONTH, 
		NEXT_MONTH
	}
	
	public enum DayState {
		TODAY, 
		CUR_MONTH_DAY, 
		PAST_MONTH_DAY, 
		NEXT_MONTH_DAY, 
		UNREACH_DAY,
		CLICK
	}
}
