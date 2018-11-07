package com.yunbaba.freighthelper.bean;


public class CarSpInfo {
	public CarSpInfo(String carduid, String carlicense) {
		this.carduid = carduid;
		this.carlicense = carlicense;
	}

	/** 车辆设备ID **/
	public String carduid;
	/** 车牌号**/
	public String carlicense;
}
