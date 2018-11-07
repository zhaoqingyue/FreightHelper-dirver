/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: CarInfo.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.bean.car
 * @Description: 车辆信息
 * @author: zhaoqy
 * @date: 2017年4月15日 上午9:46:33
 * @version: V1.0
 */

package com.yunbaba.freighthelper.bean.car;

import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCar;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCarstate;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqNavi;

public class CarInfo extends MtqCar {
	
	public String taskId; // 配送任务ID
	public String corpId; // 物流公司ID
	
	public CarInfo() {
		taskId = "";
		corpId = "";
		carlicense = "";
		carduid = "";
		carmodel = "";
		brand = "";
		vehicletype = "";
		devicename = "";
		mcuid = "";
		sim_endtime = "";
		navi = new MtqNavi();
		carstate = new MtqCarstate();
	}
	
	public CarInfo(CarInfo carInfo) {
		taskId = carInfo.taskId;
		corpId = carInfo.corpId;
		carlicense = carInfo.carlicense;
		carduid = carInfo.carduid;
		carmodel = carInfo.carmodel;
		brand = carInfo.brand;
		vehicletype = carInfo.vehicletype;
		devicename = carInfo.devicename;
		devicetype = carInfo.devicetype;
		mcuid = carInfo.mcuid;
		sim_endtime = carInfo.sim_endtime;
		navi = carInfo.navi;
		carstate = carInfo.carstate;
		
	}
}
