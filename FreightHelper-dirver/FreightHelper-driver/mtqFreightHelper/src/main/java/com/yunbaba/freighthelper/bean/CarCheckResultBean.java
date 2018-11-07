package com.yunbaba.freighthelper.bean;

public class CarCheckResultBean {
	
	
	public CarCheckResultBean(int normalResId, int errorResId, String itemName) {
		super();
		NormalResId = normalResId;
		ErrorResId = errorResId;
		ItemName = itemName;
	}
	
	public int NormalResId ;
	public int ErrorResId ;
	public String ItemName;
	public boolean isSpread = false;
	
	public boolean isError = false;
	public String  desc = "";

}
