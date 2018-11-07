/*
 * @Title CldOlsBase.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.ols.api;

import android.os.Environment;

import com.cld.base.CldBase;
import com.cld.log.CldLog;
import com.cld.net.CldHttpClient;
import com.cld.setting.CldSetting;
import com.cld.utils.CldPackage;
import com.yunbaba.ols.bll.CldBllUtil;
import com.yunbaba.ols.tools.model.CldOlsInitManager;

/**
 * 在线系统基层
 * 
 * @author Zhouls
 * @date 2015-3-5 下午3:44:55
 */
public class CldOlsBase {

	private static CldOlsBase cldOlsBase;
	private CldOlsInit cldOlsInit;

	private boolean isTestVersion;

	public boolean isTestVersion() {
		return isTestVersion; 
	}

	public void setTestVersion(boolean isTestVersion) {
		this.isTestVersion = isTestVersion; 
	}
	

	public static CldOlsBase getInstance() {
		if (null == cldOlsBase) {
			cldOlsBase = new CldOlsBase();
		}
		return cldOlsBase;
	}

	private CldOlsBase() {
		cldOlsInit = new CldOlsInit();
	}

	/**
	 * 
	 * 初始化回调
	 * 
	 * @author Zhouls
	 * @date 2015-3-20 上午8:53:59
	 */
	public interface IInitListener {
		/**
		 * 配置更新回调
		 * 
		 * @return void
		 * @author Zhouls
		 * @date 2015-7-30 上午10:59:13
		 */
		public void onUpdateConfig();

		/**
		 * 初始化duid回调
		 * 
		 * @return void
		 * @author Zhouls
		 * @date 2015-7-30 上午10:59:39
		 */
		public void onInitDuid();
	}

	/**
	 * Ols模块初始化进口
	 * 
	 * @param param
	 *            初始化参数表
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:45:04
	 */
	public void init(CldOlsParam param, IInitListener listener) {
		/**
		 * 默认是正式版,CM一套参数
		 */
		CldKUtilAPI.getInstance().init(param);
		setTestVersion(param.isTestVersion);  //yyh
		versionChanged(param.isDefInit);
		CldKConfigAPI.getInstance().initDefConfig();
		initEx(param);
		if (!param.isDefInit) {
			initBaseCondition(listener);
		}
		// 启动初始化流程
		CldOlsInitManager.getInstance().init();
	}
	
	private void initEx(final CldOlsParam param) {
		CldHttpClient.init(CldBase.getAppContext());
	}

	/**
	 * 反初始化
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:45:23
	 */
	public void uninit() {

	}

	/**
	 * 初始化在线服务网络参数
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:45:32
	 */
	public void initBaseCondition(IInitListener listener) {
		cldOlsInit.init(listener);
	}

	/**
	 * 设置调试配置路径
	 * 
	 * @param appPath
	 *            调试配置项路径
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:45:41
	 */
	public void setDebugMode(String appPath) {
		CldBllUtil.getInstance().setAppPath(appPath);
	}

	/**
	 * 初始化参数表
	 * 
	 * @author Zhouls
	 * @date 2015-3-5 下午3:45:52
	 */
	public static class CldOlsParam {

		/** 导航版本号(M1831-D6073-3223J0K) */
		public String appver = "";
		/** 地图版本 */
		public String mapver = "";
		/** true 测试版本 false 正式版本 */
		public boolean isTestVersion = false;
		/** 是否是默认值初始化 */
		public boolean isDefInit = true;
		/** 应用程序类型 */
		public int apptype = 1;
		/** 业务Id */
		public int bussinessid = 1;
		/** 应用类型 */
		public int appid = 9;
		/** 操作系统类型 */
		public int osType = 1;
		/** 程序渠道号 */
		public int cid = 1;
		/** 程序路径，默认为根目录 */
		public String appPath = Environment.getExternalStorageDirectory() + "";
	}
	
	/**
	 * 版本变化清掉配置
	 * 
	 * @param isDefInit
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-29 上午11:01:43
	 */
	private void versionChanged(boolean isDefInit) {
		if (isDefInit) {
			/**
			 * 版本升级清空配置
			 */
			int verCode = CldSetting.getInt("ols_vercode", 0);
			CldLog.d("ols_ver", "lastVer:" + verCode + "," + "curVer:"
					+ CldPackage.getAppVersionCode());
			if (verCode != CldPackage.getAppVersionCode()) {
				CldLog.e("ols_ver", "update new ver clean");
				CldSetting.remove("1001001000");
				CldSetting.remove("1001001100");
				CldSetting.remove("1001003000");
				CldSetting.remove("1003001200");
				CldSetting.remove("2002001000");
				CldSetting.remove("2004001000");
			}
			CldSetting.put("ols_vercode", CldPackage.getAppVersionCode());
		}
	}
}
