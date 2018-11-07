/*
 * @Title CldKUtilAPI.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.ols.api;

import com.yunbaba.ols.api.CldOlsBase.CldOlsParam;
import com.yunbaba.ols.bll.CldBllUtil;



/**
 * 公共参数相关模块,提供设置车机模式等功能
 * 
 * @author Zhouls
 * @date 2015-3-5 下午3:43:48
 */
public class CldKUtilAPI {

	private static CldKUtilAPI cldKUtilAPI;

	private CldKUtilAPI() {

	}

	public static CldKUtilAPI getInstance() {
		if (null == cldKUtilAPI) {
			cldKUtilAPI = new CldKUtilAPI();
		}
		return cldKUtilAPI;
	}

	/**
	 * 初始化公共参数
	 * 
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:43:58
	 */
	public void init(CldOlsParam parm) {
		CldBllUtil.getInstance().init(parm);
	}

	/**
	 * 反初始化
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:44:17
	 */
	public void uninit() {

	}

	/**
	 * 设置车机模式
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:44:24
	 */
	public void setCarMode() {
		CldBllUtil.getInstance().setCarMode();
	}

	/**
	 * 设置Iphone模式
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:44:34
	 */
	public void setIponeMode() {
		CldBllUtil.getInstance().setIponeMode();
	}

	/**
	 * 返回是否为车机模式
	 * 
	 * @return
	 * @return boolean
	 * @author Zhouls
	 * @date 2015-3-5 下午3:44:43
	 */
	public boolean isCarMode() {
		int apptype = CldBllUtil.getInstance().getApptype();
		if (apptype == 5) {
			return true;
		} else {
			return false;
		}
	}
}
