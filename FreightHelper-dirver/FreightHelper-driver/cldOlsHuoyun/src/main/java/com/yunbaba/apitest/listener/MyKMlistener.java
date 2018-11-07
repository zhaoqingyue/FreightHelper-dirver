/*
 * @Title MyKMlistener.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:59
 * @version 1.0
 */
package com.yunbaba.apitest.listener;

import java.util.List;
import com.yunbaba.ols.api.CldKMessageAPI.ICldKMessageListener;
import com.yunbaba.ols.sap.bean.CldSapKMParm;


/**
 * The Class MyKMlistener.
 *
 * @Description 消息系统回调
 * @author  Zhouls
 * @date  2014-11-4 下午12:07:52
 */
public class MyKMlistener implements ICldKMessageListener {

	/**
	 * On send poi result.
	 *
	 * @param errCode the err code
	 * @see com.cld.navicm.api.CldKMessageAPI.CldKMessageListener#onSendPoiResult(int)
	 */
	@Override
	public void onSendPoiResult(int errCode) {

		

	}

	/**
	 * On send route result.
	 *
	 * @param errCode the err code
	 * @see com.cld.navicm.api.CldKMessageAPI.CldKMessageListener#onSendRouteResult(int)
	 */
	@Override
	public void onSendRouteResult(int errCode) {


	}

	/**
	 * On up msg read status result.
	 *
	 * @param errCode the err code
	 * @param list the list
	 * @see com.cld.navicm.api.CldKMessageAPI.CldKMessageListener#onUpMsgReadStatusResult(int,
	 * java.util.List)
	 */
	@Override
	public void onUpMsgReadStatusResult(int errCode, List<String> list) {


	}

	/**
	 * On rec lastest msg history result.
	 *
	 * @param errCode the err code
	 * @param maxlength the maxlength
	 * @param list the list
	 * @param Tag_ the tag_
	 * @see com.yunbaba.ols.api.CldKMessageAPI.CldKMessageListener#onRecLastestMsgHistoryResult(int,
	 * int, java.util.List, java.lang.String)
	 */
	@Override
	public void onRecLastestMsgHistoryResult(int errCode, int maxlength,
			List<CldSapKMParm> list, String Tag_) {


	}

	/**
	 * On rec new msg history result.
	 *
	 * @param errCode the err code
	 * @param list the list
	 * @see com.yunbaba.ols.api.CldKMessageAPI.CldKMessageListener#onRecNewMsgHistoryResult(int,
	 * java.util.List)
	 */
	@Override
	public void onRecNewMsgHistoryResult(int errCode, List<CldSapKMParm> list) {


	}

	/**
	 * On rec old msg history result.
	 *
	 * @param errCode the err code
	 * @param list the list
	 * @see com.yunbaba.ols.api.CldKMessageAPI.CldKMessageListener#onRecOldMsgHistoryResult(int,
	 * java.util.List)
	 */
	@Override
	public void onRecOldMsgHistoryResult(int errCode, List<CldSapKMParm> list) {


	}

}
