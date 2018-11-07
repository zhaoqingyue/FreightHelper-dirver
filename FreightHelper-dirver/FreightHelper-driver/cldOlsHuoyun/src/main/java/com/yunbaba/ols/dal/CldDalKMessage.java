/*
 * @Title CldDalKMessage.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.ols.dal;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.cld.setting.CldSetting;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareAKeyCallParm.CldAreaEgg;


/**
 * 消息系统数据存储
 * 
 * @author Zhouls
 * @date 2014-12-18 上午11:12:18
 */
public class CldDalKMessage {

	/** 消息系统密钥 */
	private String cldKMKey;
	/** 消息历史最大值 */
	private int maxlength;
	/** 区域彩蛋列表 */
	private volatile List<CldAreaEgg> listEggs;

	private static CldDalKMessage cldDalKMessage;

	/**
	 * Gets the single instance of CldDalKMessage.
	 * 
	 * @return single instance of CldDalKMessage
	 */
	public static CldDalKMessage getInstance() {
		if (null == cldDalKMessage) {
			cldDalKMessage = new CldDalKMessage();
		}
		return cldDalKMessage;
	}

	/**
	 * Instantiates a new cld dal k message.
	 */
	private CldDalKMessage() {
		listEggs = new ArrayList<CldAreaEgg>();
		maxlength = 0;
	}

	/**
	 * Gets the list eggs.
	 * 
	 * @return the list eggs
	 */
	public synchronized List<CldAreaEgg> getListEggs() {
		return listEggs;
	}

	/**
	 * Sets the list eggs.
	 * 
	 * @param listEggs
	 *            the new list eggs
	 */
	public synchronized void setListEggs(List<CldAreaEgg> listEggs) {
		if (null != this.listEggs) {
			this.listEggs.clear();
			this.listEggs.addAll(listEggs);
		}
	}

	public int getMaxlength() {
		return maxlength;
	}

	public void setMaxlength(int maxlength) {
		this.maxlength = maxlength;
	}

	/** @return the cldKMKey */
	public String getCldKMKey() {
		if (!TextUtils.isEmpty(cldKMKey)) {
			return cldKMKey;
		} else {
			return CldSetting.getString("CldKMKey");
		}
	}

	/**
	 * @param cldKMKey
	 *            the cldKMKey to set
	 */
	public void setCldKMKey(String cldKMKey) {
		this.cldKMKey = cldKMKey;
		CldSetting.put("CldKMKey", cldKMKey);
	}

}
