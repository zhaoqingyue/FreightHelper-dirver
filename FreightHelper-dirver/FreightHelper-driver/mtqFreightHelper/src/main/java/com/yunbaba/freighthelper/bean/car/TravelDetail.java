/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: TravelDetail.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.bean.car
 * @Description: 行程详情
 * @author: zhaoqy
 * @date: 2017年4月14日 下午6:21:23
 * @version: V1.0
 */

package com.yunbaba.freighthelper.bean.car;

import java.util.ArrayList;

import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTaskDetail;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTrack;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.Navi;

public class TravelDetail extends MtqTaskDetail {
	/** 车牌号码 */
	public String carlicense;	
	/** 行程记录ID */
	public String serialid; 
	/** 百公里油耗（升） */
	public String hundred_fuel;
	/** 平均时速（公里/时） */
	public String average_speed;
	
	public TravelDetail() {
		carlicense = "";
		serialid = "";
		hundred_fuel = "";
		average_speed = "";
		navi = new Navi();
		tracks = new ArrayList<MtqTrack>();
	}
	
	public TravelDetail(TravelDetail detail) {
		navi = detail.navi;
		tracks = detail.tracks;
		hundred_fuel = detail.hundred_fuel;
		average_speed = detail.average_speed;
	}
}
