/*
 * @Title CldOlsUtcRecoder.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2016-1-19 上午11:24:42
 * @version 1.0
 */
package com.yunbaba.ols.tools.model;

import com.cld.log.CldLog;

/**
 * 耗时记录
 * 
 * @author Zhouls
 * @date 2016-1-19 上午11:24:42
 */
public class CldOlsUtcRecoder {

	private CldOlsUtcRecoder() {

	}

	private static CldOlsUtcRecoder innerRecoder;

	public static CldOlsUtcRecoder getInstance() {
		if (null == innerRecoder) {
			innerRecoder = new CldOlsUtcRecoder();
		}
		return innerRecoder;
	}

	private long utc = 0;

	/**
	 * 开始记录
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2016-1-19 上午11:13:13
	 */
	public void start() {
		utc = System.currentTimeMillis();
	}

	/**
	 * 结束记录
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2016-1-19 上午11:13:38
	 */
	public void end() {
		if (utc == 0) {
			CldLog.w("ols_utc", "Have you called start before end?");
		} else {
			utc = System.currentTimeMillis() - utc;
			if (utc < 0) {
				CldLog.i("ols_utc", "use:" + utc + "ms");
			} else if (utc < 150) {
				CldLog.d("ols_utc", "use:" + utc + "ms");
			} else if (utc < 300) {
				CldLog.w("ols_utc", "use:" + utc + "ms");
			} else {
				CldLog.w("ols_utc", "use:" + utc + "ms");
			}
			// 日志处理完成后清零
			utc = 0;
		}
	}

}
