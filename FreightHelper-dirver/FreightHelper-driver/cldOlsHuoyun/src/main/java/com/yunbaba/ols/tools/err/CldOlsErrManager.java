/*
 * @Title CldErrUtil.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-4-10 上午9:42:18
 * @version 1.0
 */
package com.yunbaba.ols.tools.err;

import java.util.concurrent.TimeoutException;

import android.text.TextUtils;

import com.cld.log.CldLog;
import com.cld.net.volley.AuthFailureError;
import com.cld.net.volley.VolleyError;
import com.yunbaba.ols.tools.CldSapReturn;
import com.yunbaba.ols.tools.parse.CldKReturn;

/**
 * 错误码处理
 * 
 * @author Zhouls
 * @date 2015-4-10 上午9:42:18
 */
public class CldOlsErrManager {
	private static String TAG = "ols_err:";

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
	private static void handleErr(CldKReturn res) {
		switch (res.errCode) {
		case 0:
			/** 接口返回正确 */
		case 1:
			/** 操作成功 */
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
			CldLog.e(TAG + res.errCode);
			if (!TextUtils.isEmpty(res.url)) {
				CldLog.e(TAG + res.url);
			}
			if (!TextUtils.isEmpty(res.jsonPost)) {
				CldLog.e(TAG + res.jsonPost);
			}
			if (!TextUtils.isEmpty(res.jsonReturn)) {
				CldLog.e(TAG + res.jsonReturn);
			}
		}
	}
	
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
	private static void handleErr(CldSapReturn res) {
		switch (res.errCode) {
		case 0:
			/** 接口返回正确 */
		case 1:
			/** 操作成功 */
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
			CldLog.e(TAG + res.errCode);
			if (!TextUtils.isEmpty(res.url)) {
				CldLog.e(TAG + res.url);
			}
			if (!TextUtils.isEmpty(res.jsonPost)) {
				CldLog.e(TAG + res.jsonPost);
			}
			if (!TextUtils.isEmpty(res.jsonReturn)) {
				CldLog.e(TAG + res.jsonReturn);
			}
		}
	}
	
	

	/**
	 * 异步请求前 将请求参数保存下来
	 * 
	 * @param request
	 * @param errRes
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-30 上午11:25:23
	 */
	public static void handleErr(CldKReturn request, CldKReturn errRes) {
		if (null != request) {
			if (null != errRes) {
				errRes.url = request.url;
				errRes.jsonPost = request.jsonPost;
				errRes.bytePost = request.bytePost;
				CldLog.i("ols", errRes.jsonReturn);
				handleErr(errRes);
			} else {
				handleErr(request);
			}
		}
	}
	

	/**
	 * 异步请求前 将请求参数保存下来
	 * 
	 * @param request
	 * @param errRes
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-30 上午11:25:23
	 */
	public static void handleErr(CldSapReturn request, CldSapReturn errRes) {
		if (null != request) {
			if (null != errRes) {
				errRes.url = request.url;
				errRes.jsonPost = request.jsonPost;
				errRes.bytePost = request.bytePost;
				CldLog.i("ols", errRes.jsonReturn);
				handleErr(errRes);
			} else {
				handleErr(request);
			}
		}
	}

	/**
	 * 网络框架返回异常的情况
	 * 
	 * @param error
	 *            网络框架返回错误
	 * @param errRes
	 *            接口通用回调
	 * @return void
	 * @author Zhouls
	 * @date 2015-9-11 上午10:52:31
	 */
	public static void handlerException(VolleyError error, CldKReturn errRes) {
		if (null != error) {
			if (null != error) {
				if (error.getCause() instanceof TimeoutException) {
					errRes.errCode = CldOlsErrCode.NET_TIMEOUT;
					errRes.errMsg = "网络超时";
				} else if (error.getCause() instanceof AuthFailureError) {
					errRes.errCode = CldOlsErrCode.HTTP_REJECT;
					errRes.errMsg = "svr reject!";
				} else {
					errRes.errCode = CldOlsErrCode.NET_OTHER_ERR;
					errRes.errMsg = "网络异常";
				}
			}
			error.printStackTrace();
			CldLog.d(TAG, error.getMessage());
		} else {
			CldLog.e(TAG, "error is null!");
		}
	}
	
	/**
	 * 网络框架返回异常的情况
	 * 
	 * @param error
	 *            网络框架返回错误
	 * @param errRes
	 *            接口通用回调
	 * @return void
	 * @author Zhouls
	 * @date 2015-9-11 上午10:52:31
	 */
	public static void handlerException(VolleyError error, CldSapReturn errRes) {
		if (null != error) {
			if (null != error) {
				if (error.getCause() instanceof TimeoutException) {
					errRes.errCode = CldOlsErrCode.NET_TIMEOUT;
					errRes.errMsg = "网络超时";
				} else if (error.getCause() instanceof AuthFailureError) {
					errRes.errCode = CldOlsErrCode.HTTP_REJECT;
					errRes.errMsg = "svr reject!";
				} else {
					errRes.errCode = CldOlsErrCode.NET_OTHER_ERR;
					errRes.errMsg = "网络异常";
				}
			}
			error.printStackTrace();
			CldLog.d(TAG, error.getMessage());
		} else {
			CldLog.e(TAG, "error is null!");
		}
	}

	/**
	 * 在线服务错误码管理
	 * 
	 * @author Zhouls
	 * @date 2015-9-11 上午10:58:08
	 */
	public static class CldOlsErrCode {
		// 网络 解析层错误码 （10001-10099）
		/** 无网络错误码 */
		public static final int NET_NO_CONNECTED = 10001;
		/** 网络超时错误码 */
		public static final int NET_TIMEOUT = NET_NO_CONNECTED + 1;
		/** 网络其他异常 */
		public static final int NET_OTHER_ERR = NET_TIMEOUT + 1;
		/** 解析异常 */
		public static final int PARSE_ERR = NET_OTHER_ERR + 1;
		/** 数据返回异常 */
		public static final int DATA_RETURN_ERR = PARSE_ERR + 1;
		/** http 403 */
		public static final int HTTP_REJECT = DATA_RETURN_ERR + 1;
		// 业务逻辑层错误码（10100-10199）
		/** 参数不合法 */
		public static final int PARAM_INBALID = 10100;
		// 业务逻辑错误（10200-10500）
		/** 未登录 */
		public static final int ACCOUT_NOT_LOGIN = 20001;
	}
}
