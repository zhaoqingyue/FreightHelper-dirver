/*
 * @Title CldOlsInit.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-2-12 下午5:32:28
 * @version 1.0
 */
package com.yunbaba.ols.api;

import com.yunbaba.ols.api.CldOlsBase.IInitListener;


/**
 * 初始化机制
 * 
 * @author Zhouls
 * @date 2015-2-12 下午5:32:28
 */
public class CldOlsInit {
	/**
	 * 初始化在线服务参数
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-1 下午2:09:02
	 */
	public void init(IInitListener listener) {
		updateConfig(listener);
		initKey(listener);
	}

	/**
	 * 初始化密钥
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-2-12 下午6:06:39
	 */
	private void initKey(final IInitListener listener) {
		/**
		 * 初始化帐户系统密钥
		 */
		CldKAccountAPI.getInstance().initKey(new ICldOlsInitListener() {
			@Override
			public void onInitReslut() {
				// 初始化duid
				initDuid(listener);
				// 初始化时间戳
				initUtc();
			}
		});
		/**
		 * 初始化消息系统密钥
		 */
		CldKMessageAPI.getInstance().initKey(null);
		/**
		 * 初始化一键通系统密钥
		 */
		CldKCallNaviAPI.getInstance().initKey(null);
		
		/**
		 * 初始化应用中心密钥
		 */
		CldKAppCenterAPI.getInstance().initKey(null);
	}

	/**
	 * 初始化duid
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-1 上午10:06:34
	 */
	private void initDuid(IInitListener listener) {
		CldKAccountAPI.getInstance().initDuid(listener);
	}

	/**
	 * 检测配置项更新
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-1 上午10:50:09
	 */
	private void updateConfig(IInitListener listener) {
		CldKConfigAPI.getInstance().updateCofig(listener);
	}

	/**
	 * 初始化时间戳
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-1 下午1:01:12
	 */
	private void initUtc() {
		CldKAccountAPI.getInstance().initSvrTime();
	}

	/**
	 * 初始化接口回调
	 * 
	 * @author Zhouls
	 * @date 2015-3-1 下午4:56:38
	 */
	public static interface ICldOlsInitListener {
		public void onInitReslut();
	}
}
