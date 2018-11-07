package com.yunbaba.ols.dal;

import com.yunbaba.ols.sap.bean.CldSapKAppParm.MtqAppInfoNew;

public class CldDalKAppCenter {
	
	/** app信息  */
	private MtqAppInfoNew appinfo;
	/** 有新版本  */
	private boolean hasNewVersion;
	
	private static CldDalKAppCenter cldDataKApp;

	public static CldDalKAppCenter getInstance() {
		if (cldDataKApp == null) {
			cldDataKApp = new CldDalKAppCenter();
		}
		return cldDataKApp;
	}
	
	private CldDalKAppCenter() {
		appinfo = new MtqAppInfoNew();
		hasNewVersion = false;
	}
	
	public void uninit() {
		appinfo = new MtqAppInfoNew();
		hasNewVersion = false;
	}
	
	public MtqAppInfoNew getMtqAppInfo() {
		return appinfo;
	}

	public void setMtqAppInfo(MtqAppInfoNew appinfo) {
		this.appinfo = appinfo;
	}
	
	public boolean getNewVersion() {
		return hasNewVersion;
	}

	public void setNewVersion(boolean hasNewVersion) {
		this.hasNewVersion = hasNewVersion;
	}
}
