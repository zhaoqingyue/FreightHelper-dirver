/*
 * @Title MyKClistener.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.apitest.listener;

import java.util.ArrayList;
import java.util.List;
import com.cld.log.CldLog;
import com.yunbaba.ols.api.CldKCallNaviAPI;
import com.yunbaba.ols.api.CldKCallNaviAPI.ICldKCallNaviListener;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareAKeyCallParm;


/**
 * The Class MyKClistener.
 * 
 * @Description 一键通消息回调
 * @author Zhouls
 * @date 2014-11-14 上午9:47:46
 */
public class MyKClistener implements ICldKCallNaviListener {

	/**
	 * On get identifycode result.
	 * 
	 * @param errCode
	 *            the err code
	 * @see com.yunbaba.ols.api.CldKCallNaviAPI.CldKCallNaviListener#onGetIdentifycodeResult(int)
	 */
	@Override
	public void onGetIdentifycodeResult(int errCode) {
		CldLog.i("ols", errCode + "_getIden");

	}

	/**
	 * On get bind mobile result.
	 * 
	 * @param errCode
	 *            the err code
	 * @see com.yunbaba.ols.api.CldKCallNaviAPI.CldKCallNaviListener#onGetBindMobileResult(int,
	 *      java.util.List)
	 */
	@Override
	public void onGetBindMobileResult(int errCode) {
		CldLog.i("ols", errCode + "_getBind");
	}

	/**
	 * On bind to mobile result.
	 * 
	 * @param errCode
	 *            the err code
	 * @see com.yunbaba.ols.api.CldKCallNaviAPI.CldKCallNaviListener#onBindToMobileResult(int)
	 */
	@Override
	public void onBindToMobileResult(int errCode) {
		CldLog.i("ols", errCode + "_BindTo");
	}

	/**
	 * On up location result.
	 * 
	 * @param errCode
	 *            the err code
	 * @see com.yunbaba.ols.api.CldKCallNaviAPI.CldKCallNaviListener#onUpLocationResult(int)
	 */
	@Override
	public void onUpLocationResult(int errCode) {
		if (errCode == 407) {
			CldKCallNaviAPI.getInstance().updateBindMobile(
					new ArrayList<String>());
		}

	}

	/**
	 * On rec ppt msg result.
	 * 
	 * @param errCode
	 *            the err code
	 * @param list
	 *            the list
	 * @see com.yunbaba.ols.api.CldKCallNaviAPI.CldKCallNaviListener#onRecPptMsgResult(int,
	 *      java.util.List)
	 */
	@Override
	public void onRecPptMsgResult(int errCode, List<ShareAKeyCallParm> list) {
		CldLog.i("ols", errCode + "_recPPt");
	}

	/**
	 * On rregister result.
	 * 
	 * @param errCode
	 *            the err code
	 * @see com.yunbaba.ols.api.CldKCallNaviAPI.CldKCallNaviListener#onRregisterResult(int)
	 */
	@Override
	public void onRegisterResult(int errCode) {


	}

	/**
	 * On del mobile result.
	 * 
	 * @param errCode
	 *            the err code
	 * @see com.yunbaba.ols.api.CldKCallNaviAPI.CldKCallNaviListener#onDelMobileResult(int)
	 */
	@Override
	public void onDelMobileResult(int errCode) {


	}
}
