/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: Route.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.bean.car
 * @Description: 单个行程
 * @author: zhaoqy
 * @date: 2017年4月14日 上午10:53:35
 * @version: V1.0
 */

package com.yunbaba.freighthelper.bean.car;

import java.io.Serializable;

import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqNavi;

@SuppressWarnings("serial")
public class Navi extends MtqNavi implements Serializable {

	public String carlicense; // 车牌号码
	
	//结构变了，这几个移到MtqNavi 里面
//	public String carduid; // 车辆设备ID  
//	public String taskId; // 行程配载单
//	public String corpId; // 行程配载单
	
	
	public int position;

	public Navi() {
		carlicense = "";
		carduid = "";
		position = -1;
		serialid = "";
		starttime = "";
		endtime = "";
		mileage = "";
		traveltime = "";
	}

	public Navi(Navi navi) {
		carlicense = navi.carlicense;
		carduid = navi.carduid;
		position = navi.position;
		serialid = navi.serialid;
		starttime = navi.starttime;
		endtime = navi.endtime;
		mileage = navi.mileage;
		traveltime = navi.traveltime;
		orders = navi.orders;
//		corpId = navi.corpId;
//		taskId = navi.taskId;
	}
}
