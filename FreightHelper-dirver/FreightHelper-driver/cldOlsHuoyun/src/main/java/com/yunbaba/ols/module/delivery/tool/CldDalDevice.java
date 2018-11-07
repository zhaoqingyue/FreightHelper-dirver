/*
 * @Title CldDalDevice.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-12-15 下午6:02:32
 * @version 1.0
 */
package com.yunbaba.ols.module.delivery.tool;

import android.text.TextUtils;

import com.cld.setting.CldSetting;

/**
 * 设备数据层
 * 
 * @author Zhouls
 * @date 2015-12-15 下午6:02:32
 */
public class CldDalDevice {
	/** 帐号系统密钥 */
	private String cldKAKey;
	/** duid */
	private long duid;
	/** 服务器时间和本地时间差. */
	private long timeDif;
	
	private static CldDalDevice cldDalDevice;

	public static CldDalDevice getInstance() {
		if (null == cldDalDevice) {
			cldDalDevice = new CldDalDevice();
		}
		return cldDalDevice;
	}

	/**
	 * Instantiates a new cld dal k account.
	 */
	private CldDalDevice() {
		cldKAKey = "";
		timeDif = 0;
	}

	/**
	 * Gets the time dif.
	 * 
	 * @return the time dif
	 */
	public long getTimeDif() {
		return timeDif;
	}

	/**
	 * Sets the time dif.
	 * 
	 * @param timeDif
	 *            the new time dif
	 */
	public void setTimeDif(long timeDif) {
		this.timeDif = timeDif;
	}

	/** @return the cldKAKey */
	public String getCldKAKey() {
		if (!TextUtils.isEmpty(cldKAKey)) {
			return cldKAKey;
		} else {
			return CldSetting.getString("CldKAKey");
		}
	}

	/**
	 * @param cldKAKey
	 *            the cldKAKey to set
	 */
	public void setCldKAKey(String cldKAKey) {
		this.cldKAKey = cldKAKey;
		CldSetting.put("CldKAKey", cldKAKey);
	}

	/**
	 * Gets the duid.
	 * 
	 * @return the duid
	 */
	public long getDuid() {
		if (duid != 0) {
			return duid;
		} else {
			if (CldSetting.getString("duid").length() > 0) {
				return Long.parseLong(CldSetting.getString("duid"));
			} else {
				return 0;
			}
		}
	}

	/**
	 * Sets the duid.
	 * 
	 * @param duid
	 *            the new duid
	 */
	public void setDuid(long duid) {
		this.duid = duid;
		CldSetting.put("duid", duid + "");
	}
}
