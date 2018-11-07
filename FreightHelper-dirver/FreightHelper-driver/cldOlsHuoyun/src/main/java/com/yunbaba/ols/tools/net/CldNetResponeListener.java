/*
 * @Title CldNetResponeListener.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-8-20 下午1:14:50
 * @version 1.0
 */
package com.yunbaba.ols.tools.net;

import com.cld.log.CldLog;
import com.cld.net.CldResponse.ICldNetworkResponse;
import com.cld.net.volley.NetworkResponse;

/**
 * 网络框架请求结果输出
 * 
 * @author Zhouls
 * @date 2015-8-20 下午1:14:50
 */
public class CldNetResponeListener implements ICldNetworkResponse {
	private String TAG = "ols";

	@Override
	public void onNetworkResponse(NetworkResponse response, byte[] dataArray) {
		if (null == response) {
			CldLog.e(TAG, "response is null!");
			return;
		}
		if (null == dataArray) {
			CldLog.e(TAG, "dataArray is null!");
			return;
		}
		CldLog.i(TAG, "responeJson:" + new String(dataArray));
		CldLog.d(TAG, "responeSize:" + dataArray.length / 8.0 + "B");
		CldLog.d(TAG, "statusCode:" + response.statusCode);
		CldLog.d(TAG, "networkTimeMs:" + response.networkTimeMs);
	}
}
