/*
 * @Title CldSapToolBox.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.ols.sap;

import com.yunbaba.ols.dal.CldDalKAccount;

import android.text.TextUtils;



/**
 * 
 * 车主工具箱
 * 
 * @author Zhouls
 * @date 2015-3-25 下午5:45:10
 */
public class CldSapToolBox {
	
	/** 代驾服务. */
	public static final String DRIVING = "driving";
	/** 车价评估. */
	public static final String CAR_EVALUATE = "car_evaluate";
	/** 车险计算器. */
	public static final String INSURE_CALCULATOR = "insure_calculator";
	/** 违章查询. */
	public static final String VIOLATION = "violation";

	/**
	 * 获取工具箱url(900)
	 * 
	 * @param tool
	 *            工具编码（driving为代驾服务，car_evaluate为车价评估，insure_calculator为车险计算器，
	 *            violation为违章查询)
	 * @param business
	 *            业务编号CM 传1
	 * @param type
	 *            终端来源（1为CM，2为iPhone）
	 * @return String 返回url
	 * @author huagx
	 * @date 2014-8-19 下午6:16:26
	 */
	public static String getToolBoxURL(String tool, int business, String type) {
		String dns = CldSapUtil.getNaviSvrWS();
		if (!TextUtils.isEmpty(dns) && !TextUtils.isEmpty(tool)
				&& !TextUtils.isEmpty(type)) {
			String url = dns + "toolbox/go.php?tool=" + tool + "&type=" + type
					+ "&business=" + business;
			long kuid = CldDalKAccount.getInstance().getKuid();
			String session = CldDalKAccount.getInstance().getSession();
			if (0 != kuid) {
				url += "&userid" + kuid;
			}
			if (!TextUtils.isEmpty(session)) {
				url += "&session" + session;
			}
			return url;
		}
		return null;
	}
}
