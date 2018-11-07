/*
 * @Title CldDalKPublic.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-4-8 上午8:52:30
 * @version 1.0
 */
package com.yunbaba.ols.dal;

import com.cld.setting.CldSetting;

import android.text.TextUtils;

/**
 * 零散接口数据管理
 * 
 * @author Zhouls
 * @date 2015-4-8 上午8:52:30
 */
public class CldDalKPosition {

	/** 位置服务密钥 */
	private String cldKPKey;

	private static CldDalKPosition cldDalKPosition;

	public static CldDalKPosition getInstance() {
		if (null == cldDalKPosition) {
			cldDalKPosition = new CldDalKPosition();
		}
		return cldDalKPosition;
	}

	private CldDalKPosition() {
		cldKPKey = "";
	}

	/** @return the cldKPKey */
	public String getCldKPKey() {
		if (!TextUtils.isEmpty(cldKPKey)) {
			return cldKPKey;
		}
		return CldSetting.getString("CldKPKey");
	}

	/**
	 * @param cldKPKey
	 *            the cldKPKey to set
	 */
	public void setCldKPKey(String cldKPKey) {
		this.cldKPKey = cldKPKey;
		CldSetting.put("CldKPKey", cldKPKey);
	}
}
