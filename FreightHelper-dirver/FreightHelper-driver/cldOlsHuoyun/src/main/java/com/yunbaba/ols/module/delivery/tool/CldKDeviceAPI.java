/*
 * @Title CldKDeviceAPI.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-12-15 下午7:04:30
 * @version 1.0
 */
package com.yunbaba.ols.module.delivery.tool;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 设备信息接口
 * 
 * @author Zhouls
 * @date 2015-12-15 下午7:04:30
 */
public class CldKDeviceAPI {
	/**
	 * 获取duid
	 * 
	 * @return
	 * @return long
	 * @author Zhouls
	 * @date 2015-12-15 下午7:05:16
	 */
	public static long getDuid() {
		return CldDalDevice.getInstance().getDuid();
	}

	/**
	 * 获取服务器时间
	 * 
	 * @return 当前服务器时间 long
	 * @author Zhouls
	 * @date 2014-9-19 上午10:28:30
	 */
	public static long getSvrTime() {
		return System.currentTimeMillis()/ 1000;
			//	- CldDalKAccount.getInstance().getTimeDif();
		//return CldPubFuction.getLocalTime()
		//		- CldDalDevice.getInstance().getTimeDif();
	}

	/**
	 * 返回字符时间
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-10-14 上午9:25:40
	 */
	public static String getStrSvrTime() {
		long utc = CldKDeviceAPI.getSvrTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sdf.format(new Date(utc * 1000));
		return date;
	}
}
