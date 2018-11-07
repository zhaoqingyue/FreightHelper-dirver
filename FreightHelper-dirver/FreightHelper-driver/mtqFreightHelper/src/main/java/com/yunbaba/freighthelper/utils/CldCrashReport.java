package com.yunbaba.freighthelper.utils;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: CldCrashReport.java
 * @Prject: Freighthelper
 * @Package: com.mtq.freighthelper.utils
 * @Description: bugly 错误日志统计帮助类
 * @author: zsx
 * @date: 2017-4-13 上午10:44:03
 * @version: V1.0
 */
public class CldCrashReport {

	/**
	 * 上Bugly(bugly.qq.com)注册产品获取的AppId
	 */
	//private final String APPID = "ce2a062b38";
	private final String APPID = "4ffe1923ac";

	
	
	public void init(Context context) {
		CrashReport.initCrashReport(context, APPID, false); // 初始化SDK
	}

	/**
	 * @Title: testJavaCrash
	 * @Description: 模拟Java Crash方法
	 * @return: void
	 */
	public void testJavaCrash() {
		CrashReport.testJavaCrash();
	}

	/**
	 * @Title: testNativeCrash
	 * @Description: 模拟Native Crash方法
	 * @return: void
	 */
	public void testNativeCrash() {
		CrashReport.testNativeCrash();
	}
}
