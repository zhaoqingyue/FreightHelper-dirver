/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: AppCenterAPI.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.api.appcenter
 * @Description: 升级相关接口
 * @author: zhaoqy
 * @date: 2017年4月20日 下午12:22:01
 * @version: V1.0
 */

package com.yunbaba.api.appcenter;

import com.yunbaba.ols.api.CldKAppCenterAPI;
import com.yunbaba.ols.api.CldKAppCenterAPI.IUpgradeListener;
import com.yunbaba.ols.dal.CldDalKAppCenter;
import com.yunbaba.ols.sap.bean.CldSapKAppParm.MtqAppInfoNew;

public class AppCenterAPI {

	private static AppCenterAPI mAppCenterAPI = null;

	public static AppCenterAPI getInstance() {
		if (mAppCenterAPI == null) {
			synchronized (AppCenterAPI.class) {
				if (mAppCenterAPI == null) {
					mAppCenterAPI = new AppCenterAPI();
				}
			}
		}
		return mAppCenterAPI;
	}

	/**
	 * @Title: getMtqAppInfo
	 * @Description: 获取应用版本信息
	 * @return: MtqAppInfo
	 */
	public MtqAppInfoNew getMtqAppInfo() {
		//return CldKAppCenterAPI.getInstance().getMtqAppInfo();
		
		return CldDalKAppCenter.getInstance().getMtqAppInfo();
	}

	/**
	 * @Title: hasNewVersion
	 * @Description: 是否存在新版本
	 * @return: boolean
	 */
	public boolean hasNewVersion() {
		//return CldKAppCenterAPI.getInstance().hasNewVersion();
		
		return CldDalKAppCenter.getInstance().getNewVersion();
	}

	/**
	 * @Title: clearAppVersion
	 * @Description: 清除版本信息
	 * @return: void
	 */
	public void clearAppVersion() {
		CldKAppCenterAPI.getInstance().clearAppVersion();
	}

	public void getUpgrade(int regionId, int customCode, int planCode,
			IUpgradeListener listener) {
		CldKAppCenterAPI.getInstance().getUpgrade(regionId, customCode,
				planCode, listener);
	}
}
