/*
 * @Title MyKAClistener.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:59
 * @version 1.0
 */
package com.yunbaba.apitest.listener;

import com.cld.log.CldLog;
import com.yunbaba.ols.api.CldKAuthcheckAPI.ICldKAuthcheckListener;


/**
 * The Class MyKAClistener.
 *
 * @Description TODO
 * @author  Zhouls
 * @date  2014-12-10 上午9:48:28
 */
public class MyKAClistener implements ICldKAuthcheckListener {

	/**
	 * On auth check result.
	 *
	 * @param errCode the err code
	 * @see com.yunbaba.ols.api.CldKAuthcheckAPI.CldKAuthcheckListener#onAuthCheckResult(int)
	 */
	@Override
	public void onAuthCheckResult(int errCode) {
		CldLog.i("ols", errCode + "");

	}

}
