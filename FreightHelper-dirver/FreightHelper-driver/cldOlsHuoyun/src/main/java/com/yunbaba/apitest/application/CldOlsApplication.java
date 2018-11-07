/*
 * @Title CldOlsApplication.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-9 上午10:10:26
 * @version 1.0
 */
package com.yunbaba.apitest.application;

import com.cld.base.CldBase;
import com.cld.base.CldBaseParam;
import com.cld.log.CldCrashHandler;
import com.yunbaba.ols.api.CldOlsBase;
import com.yunbaba.ols.api.CldOlsBase.CldOlsParam;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

/**
 * @author Zhouls
 * @date 2015-3-9 上午10:10:26
 */
public class CldOlsApplication extends Application {
	/**
	 * @see android.app.Application#onCreate()
	 */
	private static Context mContext = null;
	
	@Override
	public void onCreate() {

		super.onCreate();
		CldBaseParam param = new CldBaseParam();
		param.ctx = getApplicationContext();
		mContext = getApplicationContext();
		CldBase.init(param);
		CldCrashHandler crashHandler = CldCrashHandler.getInstance();
		crashHandler.init(this, Environment.getExternalStorageDirectory()
				+ "/CldOlsApi/log/CldOlsLog.txt");
		Thread.setDefaultUncaughtExceptionHandler(crashHandler);
		crashHandler.start();
		CldOlsParam olsparam = new CldOlsParam();
		olsparam.isTestVersion = true;
		olsparam.appPath = Environment.getExternalStorageDirectory()
				+ "/CldOlsApi";
		CldOlsBase.getInstance().init(olsparam, null);
	}
	
	public static Context getContext(){
		return mContext;
	}
}
