/*
 * @Title CldDalKPublic.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-4-8 上午9:55:21
 * @version 1.0
 */
package com.yunbaba.ols.dal;

import android.text.TextUtils;

import com.cld.setting.CldSetting;

/**
 * 零散接口数据层
 * 
 * @author Zhouls
 * @date 2015-4-8 上午9:55:21
 */
public class CldDalKPublic {

	/** 零散接口密钥 */
	private String cldKPubKey;

	private static CldDalKPublic cldDalKPublic;

	public static CldDalKPublic getInstance() {
		if (null == cldDalKPublic) {
			cldDalKPublic = new CldDalKPublic();
		}
		return cldDalKPublic;
	}

	private CldDalKPublic() {

	}

	/** @return the cldKPubKey */
	public String getCldKPubKey() {
		if (!TextUtils.isEmpty(cldKPubKey)) {
			return cldKPubKey;
		}
		return CldSetting.getString("ols_CldKPUBKey");
	}

	/**
	 * @param cldKPubKey
	 *            the cldKPubKey to set
	 */
	public void setCldKPubKey(String cldKPubKey) {
		this.cldKPubKey = cldKPubKey;
		CldSetting.put("ols_CldKPUBKey", cldKPubKey);
	}

}
