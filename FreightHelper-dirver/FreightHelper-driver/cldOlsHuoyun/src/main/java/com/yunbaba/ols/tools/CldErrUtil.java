/*
 * @Title CldErrUtil.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-4-10 上午9:42:18
 * @version 1.0
 */
package com.yunbaba.ols.tools;

import android.text.TextUtils;

import com.cld.log.CldLog;

/**
 * 错误码处理
 * 
 * @author Zhouls
 * @date 2015-4-10 上午9:42:18
 */
public class CldErrUtil {

	/**
	 * 通用错误日志处理
	 * 
	 * @param errCode
	 *            接口返回的错误码
	 * @param strUrl
	 *            网络请求
	 * @param strPost
	 *            post方法传递参数
	 * @param ret
	 *            回调结果
	 * @return void
	 * @author Zhouls
	 * @date 2015-2-5 下午2:34:48
	 */
	public static void handleErr(CldSapReturn res) {
		switch (res.errCode) {
		case 0:
			/** 接口返回正确 */
		case 101:
			/** 用户已注册 */
		case 110:
			/** 本地有配置 */
		case 201:
			/** 手机号已注册 */
		case 301:
			/** 设备已注册 */
			break;
		default:
			CldLog.e("[ols]" + res.errCode);
			if (!TextUtils.isEmpty(res.url)) {
				CldLog.e("[ols]" + res.url);
			}
			if (!TextUtils.isEmpty(res.jsonPost)) {
				CldLog.e("[ols]" + res.jsonPost);
			}
			if (!TextUtils.isEmpty(res.jsonReturn)) {
				CldLog.e("[ols]" + res.jsonReturn);
			}
		}
	}

	/**
	 * 参数不合法处理
	 * 
	 * @param res
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-10 上午9:45:01
	 */
	public static CldSapReturn errDeal() {
		CldSapReturn res = new CldSapReturn();
		res.errCode = 401;
		res.errMsg = "参数不合法";
		return res;
	}
}
