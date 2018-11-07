/*
 * @Title CldBllUtil.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:59
 * @version 1.0
 */
package com.yunbaba.ols.bll;

import com.cld.base.CldBase;
import com.cld.device.CldPhoneManager;
import com.cld.device.CldPhoneNet;
import com.cld.setting.CldSetting;
import com.cld.utils.CldPackage;
import com.yunbaba.ols.api.CldOlsBase.CldOlsParam;
import com.yunbaba.ols.sap.CldSapKAccount;
import com.yunbaba.ols.sap.CldSapKAppCenter;
import com.yunbaba.ols.sap.CldSapKCombo;
import com.yunbaba.ols.sap.CldSapKPub;

import android.content.Context;

/**
 * 逻辑层公共参数类
 * 
 * @author Zhouls
 * @date 2014-9-11 下午3:38:52
 */
public class CldBllUtil {

	/** 设备应用类型(1:CM;5:车机). */
	private int apptype;
	/** 业务编号(1:CM；5：车机) */
	private int bussinessid;
	/** 应用id. */
	private int appid;
	/** 设备[系统]类型(1Android 2IOS 3WP 4WINCE，5未知) */
	private int osType;
	/** 消息所属模块(1:K云;2:WEB地图;3:一键通) */
	private int module;
	/** 获取程序版本号（6.2） */
	private String prover;
	/** 导航版本号(M1831-D6073-3223J0K) */
	private String appver;
	/** 地图版本 */
	private String mapver;
	/** 程序路径 */
	private String appPath;
	/** 是否是测试版 */
	private boolean isTestVersion;
	/** 程序渠道号 */
	private int cid;

	private static CldBllUtil cldBllUtil;

	/**
	 * Instantiates a new cld bll util.
	 */
	private CldBllUtil() {
		apptype = 1;
		bussinessid = 1;
		appid = 9;
		osType = 1;
		module = 1;
		prover = "";
		appver = "";
		mapver = "";
		appPath = "";
		cid = 1;
		isTestVersion = true;
	}

	/**
	 * Gets the single instance of CldBllUtil.
	 * 
	 * @return single instance of CldBllUtil
	 */
	public static CldBllUtil getInstance() {
		if (cldBllUtil == null) {
			cldBllUtil = new CldBllUtil();
		}
		return cldBllUtil;
	}

	/**
	 * 数据初始化
	 * 
	 * @param parm
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 下午4:36:07
	 */
	public void init(CldOlsParam parm) {
		/**
		 * 初始化设备apptype
		 */
		apptype = parm.apptype;
		CldSetting.put("apptype", apptype + "");
		/**
		 * 初始化业务编号
		 */
		bussinessid = parm.bussinessid;
		CldSetting.put("bussinessid", bussinessid + "");
		/**
		 * 应用id
		 */
		appid = parm.appid;
		CldSapKAccount.APPID = appid;
		CldSapKPub.APPID = appid;
		CldSetting.put("appid", appid + "");
		/**
		 * 设备[系统]类型
		 */
		osType = parm.osType;
		this.prover = CldPackage.getAppVersion();
		this.appver = parm.appver;
		this.mapver = parm.mapver;
		this.isTestVersion = parm.isTestVersion;
		/**
		 * 程序路径只有第一次指定才起作用
		 */
		if (parm.isDefInit) {
			this.appPath = parm.appPath;
		}
		/**
		 * 初始化渠道号
		 */
		this.cid = parm.cid;
		CldSapKAccount.PROVER = this.prover;
		CldSapKAccount.CID = parm.cid;
		
		/**
		 * add by zhaoqy 2017-2-15
		 */
		CldSapKAppCenter.PROVER = this.prover;
		CldSapKAppCenter.CID = this.cid;
		
		CldSapKCombo.PROVER = this.prover;
		CldSapKCombo.CID = this.cid;
	}

	/**
	 * 为服务初始化一些变量
	 * 
	 * @param isTestVersion
	 *            true 测试版 false 正式版
	 * @param appPath
	 *            程序路径
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 上午10:46:42
	 */
	public void init(boolean isTestVersion, String appPath) {
		this.appPath = appPath;
		this.isTestVersion = isTestVersion; 
	}

	/**
	 * 设置为车机模式
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2014-9-11 下午7:29:49
	 */
	public void setCarMode() {
		setAppid(24);
		setApptype(5);
		setBussinessid(5);
		setOsType(1);
	}

	/**
	 * 设置为iphone
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2014-9-17 下午2:40:30
	 */
	public void setIponeMode() {
		setApptype(2);
		setAppid(12);
		setBussinessid(4);
		setOsType(2);
	}

	/**
	 * Gets the apptype.
	 * 
	 * @return the apptype
	 */
	public int getApptype() {
		return apptype;
	}

	/**
	 * Sets the apptype.
	 * 
	 * @param apptype
	 *            the new apptype
	 */
	public void setApptype(int apptype) {
		this.apptype = apptype;
		CldSetting.put("apptype", apptype + "");
	}

	/**
	 * Gets the bussinessid.
	 * 
	 * @return the bussinessid
	 */
	public int getBussinessid() {
		return bussinessid;
	}

	/**
	 * Sets the bussinessid.
	 * 
	 * @param bussinessid
	 *            the new bussinessid
	 */
	public void setBussinessid(int bussinessid) {
		this.bussinessid = bussinessid;
	}

	/**
	 * Gets the appid.
	 * 
	 * @return the appid
	 */
	public int getAppid() {
		return appid;
	}

	/**
	 * Sets the appid.
	 * 
	 * @param appid
	 *            the new appid
	 */
	public void setAppid(int appid) {
		this.appid = appid;
		CldSapKAccount.APPID = appid;
	}

	/**
	 * Gets the os type.
	 * 
	 * @return the os type
	 */
	public int getOsType() {
		return osType;
	}

	/**
	 * Sets the os type.
	 * 
	 * @param osType
	 *            the new os type
	 */
	public void setOsType(int osType) {
		this.osType = osType;
	}

	/**
	 * Gets the module.
	 * 
	 * @return the module
	 */
	public int getModule() {
		return module;
	}

	/**
	 * Sets the module.
	 * 
	 * @param module
	 *            the new module
	 */
	public void setModule(int module) {
		this.module = module;
	}

	/**
	 * 获取程序版本号（6.2）
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-4-8 下午4:36:45
	 */
	public String getProver() {
		return prover;
	}

	/**
	 * 导航版本号(M1831-D6073-3223J0K)
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-4-8 下午4:36:56
	 */
	public String getAppver() {
		return appver;
	}

	/**
	 * 地图版本
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-4-8 下午4:37:05
	 */
	public String getMapver() {
		return mapver;
	}

	/**
	 * 设置程序版本号
	 * 
	 * @param prover
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 下午4:37:13
	 */
	public void setProver(String prover) {
		this.prover = prover;
	}

	/**
	 * 设置导航版本号
	 * 
	 * @param appver
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 下午4:37:27
	 */
	public void setAppver(String appver) {
		this.appver = appver;
	}

	/**
	 * 设置地图版本号
	 * 
	 * @param mapver
	 *            the new mapver
	 * @return void
	 * @author Zhouls
	 * @date 2014-10-30 下午5:20:58
	 */
	public void setMapver(String mapver) {
		this.mapver = mapver;
	}

	/**
	 * 屏幕宽
	 * 
	 * @return int
	 * @author Zhouls
	 * @date 2014-10-8 上午9:43:37
	 */
	public int getScreenW() {
		return CldPhoneManager.getScreenWidth();
	}

	/**
	 * 屏幕高
	 * 
	 * @return int
	 * @author Zhouls
	 * @date 2014-10-8 上午9:43:51
	 */
	public int getScreenH() {
		return CldPhoneManager.getScreenHeight();
	}

	/**
	 * 运营商类型
	 * 
	 * @return int
	 * @author Zhouls
	 * @date 2014-10-8 上午9:44:06
	 */
	public int getUimtype() {
		int uimtype = -1;
		String operator = CldPhoneManager.getSimOperator();
		if (null != operator && operator.length() > 0) {
			if (operator.equals("46000") || operator.equals("46002")
					|| operator.equals("46007")) {
				// 中国移动
				uimtype = 4;
			} else if (operator.equals("46003")) {
				// 中国电信
				uimtype = 5;
			} else if (operator.equals("46001")) {
				// 中国联通
				uimtype = 6;
			}
		} else {
		}
		return uimtype;
	}

	/**
	 * Gets the wifi mac.
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-10-8 上午9:44:17
	 */
	public String getWifiMac() {
		return CldPhoneNet.getMacAddress();
	}

	/**
	 * Gets the blue mac.
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-10-8 上午9:44:32
	 */
	public String getBlueMac() {
		return CldPhoneNet.getBluetoothAddress();
	}

	/**
	 * Gets the imei.
	 * 
	 * @return String
	 * 
	 * @author Zhouls
	 * @date 2014-10-8 上午9:44:50
	 */
	public String getImei() {
		try {
			return CldPhoneManager.getImei();
		} catch (Exception e) {
			return "empty";
		}
	}

	/**
	 * Gets the imsi.
	 * 
	 * @return String
	 * 
	 * @author Zhouls
	 * @date 2014-10-8 上午9:44:50
	 */
	public String getImsi() {
		try {
			return CldPhoneManager.getImsi();
		} catch (Exception e) {
			return "empty";
		}	
	}

	/**
	 * Gets the sN.
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-10-8 上午9:45:00
	 */
	public String getSN() {

		try {
			return CldPhoneManager.getSimSerialNumber();
		} catch (Exception e) {
			return "empty";
		}
	}

	/**
	 * Gets the model.
	 * 
	 * @return String
	 * 
	 * @author Zhouls
	 * @date 2014-10-8 上午9:45:08
	 */
	public String getModel() {
		return CldPhoneManager.getPhoneModel();
	}

	/**
	 * Gets the sDK version.
	 * 
	 * @return String
	 * 
	 * @author Zhouls
	 * @date 2014-10-8 上午9:45:23
	 */
	public String getSDKVersion() {
		return CldPhoneManager.getSDKVersion();
	}

	/**
	 * Gets the oS version.
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-10-8 上午9:45:37
	 */
	public String getOSVersion() {
		return CldPhoneManager.getOSVersion();
	}

	/**
	 * Gets the device code.
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-10-8 上午10:18:57
	 */
	public String getDeviceCode() {
		// String wifiMac = getWifiMac();
		String wifiMac = getImei();
		if (null != wifiMac) {
			return wifiMac;
		} else {
			String imei = getImei();
			if (null != imei) {
				return imei;
			} else {
				String sn = getSN();
				if (null != sn) {
					return sn;
				} else {
					String blueMac = getBlueMac();
					if (null != blueMac) {
						return blueMac;
					}
					return null;
				}
			}
		}
	}

	/**
	 * Gets the device name.
	 * 
	 * @return String
	 * 
	 * @author Zhouls
	 * @date 2014-10-8 上午10:20:50
	 */
	public String getDeviceName() {
		String deviceName = getDeviceCode();
		return deviceName;
	}

	/**
	 * Gets the context.
	 * 
	 * @return Context
	 * 
	 * @author Zhouls
	 * @date 2014-11-5 上午10:22:27
	 */
	public Context getContext() {
		return CldBase.getAppContext();
	}

	/**
	 * Gets the app path.
	 * 
	 * @return the app path
	 */
	public String getAppPath() {
		return appPath;
	}

	/**
	 * Sets the app path.
	 * 
	 * @param appPath
	 *            the new app path
	 */
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

	/**
	 * Checks if is test version.
	 * 
	 * @return true, if is test version
	 */
	public boolean isTestVersion() {
		return isTestVersion;
	}

	/**
	 * Sets the test version.
	 * 
	 * @param isTestVersion
	 *            the new test version
	 */
	public void setTestVersion(boolean isTestVersion) {
		this.isTestVersion = isTestVersion;
	}

	/**
	 * 获取版本类型
	 * 
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-3-20 上午9:38:14
	 */
	public int getVersionType() {
		if (isTestVersion) {
			return 1;
		} else {
			return 2;
		}
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}
}
