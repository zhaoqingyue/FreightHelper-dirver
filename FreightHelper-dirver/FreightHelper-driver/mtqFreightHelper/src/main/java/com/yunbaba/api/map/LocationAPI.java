package com.yunbaba.api.map;

import android.content.Context;

import com.cld.location.CldLocation;
import com.cld.location.CldLocationClient;
import com.cld.location.CldLocationOption;
import com.cld.location.ICldLocationListener;
import com.cld.navisdk.CldNaviAuthManager;
import com.cld.navisdk.CldNaviAuthManager.CldAuthManagerListener;
import com.cld.navisdk.utils.CldNaviSdkUtils;
import com.cld.ols.module.authcheck.CldKAuthcheckAPI;
import com.cld.ols.module.authcheck.bean.CldAkeyType;
import com.yunbaba.freighthelper.utils.MLog;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: LocationManager.java
 * @Prject: Freighthelper
 * @Package: com.mqt.api.map
 * @Description: 定位管理类
 * @author: zsx
 * @date: 2017-3-25 下午4:57:39
 * @version: V1.0
 */
public class 	LocationAPI {
	private static final String TAG = "LocationManager";
	private static LocationAPI mLocationManager;
	private CldLocationClient mCldLocationClient;
	private ICldLocationListener mCldLocationListener;
	private static boolean mIsAuthing = false;  //正在sdk鉴权
	
	public static LocationAPI getInstance(){
		synchronized (TAG) {
			if (mLocationManager == null){
				synchronized (TAG) {
					mLocationManager = new LocationAPI();
				}
			}
		}
		return mLocationManager;
	}
	
	/**
	 * @Title: setLinster
	 * @Description:  设置定位回调
	 * @param listener
	 * @return
	 * @return: LocationManager
	 */
	public LocationAPI setLinster(ICldLocationListener listener){
		mCldLocationListener = listener;
		return mLocationManager;
	}
	
	/**
	 * @Title: location
	 * @Description: 开启定位
	 * @param locationMode ：定位模式  参考类 MTQLocationMode
	 * @param spanMs：定位频率 单位毫秒
	 * @param ctx：context
	 * @return: LocationManager
	 */
	public LocationAPI location(int locationMode, int spanMs,Context ctx) {
		
		if ((!CldNaviAuthManager.getInstance().isAuthStatus() ||
				CldKAuthcheckAPI.getInstance().getAkeyType() != CldAkeyType.HY) ){
			CldNaviAuthManager.getInstance().authenticate(new CldAuthManagerListener() {
				
				@Override
				public void onAuthResult(int i, String s) {

//					mIsAuthing = false;
				}
			}, CldNaviSdkUtils.getAuthValue(ctx.getApplicationContext()));
		}
		
		if (null == mCldLocationClient) {
			mCldLocationClient = new CldLocationClient(ctx);
		}
		// 如果已开启定位，先停掉
		if (mCldLocationClient.isStarted()) {
			mCldLocationClient.stop();
		}
		// 设置定位选项
		CldLocationOption option = new CldLocationOption();
		option.setLocationMode(locationMode);// 设置定位模式
		option.setNetworkScanSpan(spanMs);// 定位扫描时间
		mCldLocationClient.setLocOption(option);
		mCldLocationClient.registerLocationListener(new ICldLocationListener() {

			@Override
			public void onReceiveLocation(CldLocation location) {
					MLog.e("test", " onReceiveLocation: " + location);
					if (mCldLocationListener != null){
						mCldLocationListener.onReceiveLocation(location);
					}				
				}
			});
		
		mCldLocationClient.start();
		return mLocationManager;
	}
	
	
	/**
	 * @Title: stop
	 * @Description: 关闭定位
	 * @return: void
	 */
	public void stop(){
		if (mCldLocationClient != null){
			mCldLocationClient.stop();
			mCldLocationClient = null;
		}
	}
	
	
	//定位模式
	public static class MTQLocationMode{
		 public static final int GPS = 1;  //GPS
		 public static final int NETWORK = 2; //网络
		 public static final int MIXED = 4;   //混合
	}
}
