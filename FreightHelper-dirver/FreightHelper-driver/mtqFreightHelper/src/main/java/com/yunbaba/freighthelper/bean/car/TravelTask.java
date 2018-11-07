/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: TravelTask.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.bean.car
 * @Description: 行程日期
 * @author: zhaoqy
 * @date: 2017年4月15日 上午9:35:33
 * @version: V1.0
 */

package com.yunbaba.freighthelper.bean.car;

import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTask;

public class TravelTask extends MtqTask {

	public String date; // 日期
	
	public TravelTask() {
		taskid = "";
		corpid = "";
		starttime = "";
		finishtime = "";
		date = "";
	}

	public TravelTask(TravelTask task) {
		taskid = task.taskid;
		corpid = task.corpid;
		starttime = task.starttime;
		finishtime = task.finishtime;
		date = task.date;
	}
}
