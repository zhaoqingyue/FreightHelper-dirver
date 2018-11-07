/*
 * @Title CldKAuthcheckAPI.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.ols.api;

import com.cld.utils.CldPackage;
import com.yunbaba.ols.bll.CldKAuthcheck;
import com.yunbaba.ols.tools.CldSapReturn;



/**
 * 鉴权相关模块，提供鉴权功能
 * 
 * @author Zhouls
 * @date 2015-3-5 下午3:07:24
 */
public class CldKAuthcheckAPI {
	/** 鉴权回调监听，初始化时设置一次 */
	private ICldKAuthcheckListener listener;

	private static CldKAuthcheckAPI cldKAuthcheckAPI;

	private CldKAuthcheckAPI() {
	}

	public static CldKAuthcheckAPI getInstance() {
		if (cldKAuthcheckAPI == null)
			cldKAuthcheckAPI = new CldKAuthcheckAPI();
		return cldKAuthcheckAPI;
	}

	/**
	 * 设置回调监听
	 * 
	 * @param listener
	 *            鉴权回调监听
	 * @return (0 设置成功，-1已有设置失败)
	 * @return int
	 * @author Zhouls
	 * @date 2015-3-5 下午3:07:39
	 */
	public int setCldKAuthcheckListener(ICldKAuthcheckListener listener) {
		if (null == this.listener) {
			this.listener = listener;
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 鉴权
	 * 
	 * @param key
	 *            访问密钥（由开发者在凯立德开发者平台申请的密钥）
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:08:01
	 */
	public void authCheck(final String key) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAuthcheck.getInstance().authCheck(
						key, CldPackage.getSafeCode());
				if (null != listener) {
					listener.onAuthCheckResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 鉴权回调
	 * 
	 * @author Zhouls
	 * @date 2015-3-5 下午3:08:15
	 */
	public interface ICldKAuthcheckListener {

		/**
		 * 鉴权回调
		 * 
		 * @param errCode
		 *            （0为成功，1为参数内容格式不合法，2为安全码与访问密钥不匹配， 3为访问密钥不存在或已删除，4为用户未审核，5
		 *            为安全码无权限访问，6为调用次数超额，100为系统错误）
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:08:27
		 */
		public void onAuthCheckResult(int errCode);
	}
}
