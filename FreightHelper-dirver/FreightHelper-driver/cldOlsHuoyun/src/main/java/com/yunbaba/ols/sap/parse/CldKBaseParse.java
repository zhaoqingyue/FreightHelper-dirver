/*
 * @Title CldKBaseParse.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-18 下午3:12:51
 * @version 1.0
 */
package com.yunbaba.ols.sap.parse;

import android.text.TextUtils;

import com.cld.log.CldLog;
import com.yunbaba.ols.sap.CldSapUtil;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;

import java.util.Map;

/**
 * 解析层基类
 * 
 * @author Zhouls
 * @date 2015-3-18 下午3:12:51
 */
public class CldKBaseParse {

	/**
	 * 获取Get方法拼接的URL
	 * 
	 * @param map
	 * @param urlHead
	 * @param key
	 * @return
	 * @return ProtReturn
	 * @author Zhouls
	 * @date 2015-4-3 上午11:30:06
	 */
	public static CldSapReturn getGetParms(Map<String, Object> map,
			String urlHead, String key) {
		CldLog.i("ols", "key: " + key);
		String urlSource = "";
		String md5Source = "";
		if (null != map) {
			md5Source = CldSapParser.formatSource(map);
			urlSource = CldSapParser.formatUrlSource(map);
		}
		if (!TextUtils.isEmpty(key)) {
			md5Source += key;
		}
		String sign = CldSapParser.MD5(md5Source);
		CldLog.i("ols", md5Source);
		String strUrl = urlHead + "?" + urlSource + "&sign=" + sign;
		CldLog.i("ols", strUrl);
		CldSapReturn errRes = new CldSapReturn();
		errRes.url = strUrl;
		return errRes;
	}
	
	/**
	 * 获取Get方法拼接的URL
	 * 
	 * @param map
	 * @param urlHead
	 * @param key
	 * @return
	 * @return ProtReturn
	 * @author Zhouls
	 * @date 2015-4-3 上午11:30:06
	 */
	public static CldSapReturn getGetParms(Map<String, Object> map,
			String urlHead, String md5map,String key) {
		String urlSource = "";
		String md5Source = md5map;
		if (null != map) {
			//md5Source = CldSapParser.formatSource(map);
			urlSource = CldSapParser.formatUrlSource(map);
		}
		if (!TextUtils.isEmpty(key)) {
			md5Source += key;
		}
		String sign = CldSapParser.MD5(md5Source);
		CldLog.i("ols", md5Source);
		String strUrl = urlHead + "?" + urlSource + "&sign=" + sign;
		CldLog.i("ols", strUrl);
		CldSapReturn errRes = new CldSapReturn();
		errRes.url = strUrl;
		return errRes;
	}

	/**
	 * url 不统一转码
	 * 
	 * @param map
	 * @param urlHead
	 * @param key
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-7-9 下午4:36:36
	 */
	public static CldSapReturn getGetParmsNoEncode(Map<String, Object> map,
			String urlHead, String key) {
		String urlSource = "";
		String md5Source = "";
		if (null != map) {
			md5Source = CldSapParser.formatSource(map);
			urlSource = md5Source;
		}
		if (!TextUtils.isEmpty(key)) {
			md5Source += key;
		}
		String sign = CldSapParser.MD5(md5Source);
		String strUrl = urlHead + "?" + urlSource + "&sign=" + sign;
		CldLog.i("ols", strUrl);
		CldSapReturn errRes = new CldSapReturn();
		errRes.url = strUrl;
		return errRes;
	}

	/**
	 * 获取Post方法拼接的URL
	 * 
	 * @param map
	 * @param urlHead
	 * @param key
	 * @return
	 * @return ProtReturn
	 * @author Zhouls
	 * @date 2015-4-3 上午11:42:43
	 */
	public static CldSapReturn getPostParms(Map<String, Object> map,
			String urlHead, String key) {
		CldSapReturn errRes = new CldSapReturn();
		if (null != map) {
			/**
			 * map不为空
			 */
			String urlSource = CldSapParser.formatSource(map);
			String md5Source = urlSource;
			if (!TextUtils.isEmpty(key)) {
				md5Source += key;
			}
			String sign = CldSapUtil.MD5(md5Source);
			map.put("sign", sign);
			String strUrl = urlHead;
			CldLog.i("ols", md5Source);
			CldLog.i("ols", strUrl);
			String strPost = CldSapParser.mapToJson(map);
			CldLog.i("ols", strPost);
			errRes.url = urlHead;
			errRes.jsonPost = strPost;
		}
		return errRes;
	}
	
	/**
	 * 获取Post方法拼接的URL(app包名和版本号不加入计算sign)
	 * 
	 * @param map
	 * @param urlHead
	 * @param key
	 * @param myMd5Source
	 * 
	 * @return CldSapReturn
	 * @author zhaoqingyue
	 * @date 2017-2-8
	 */
	public static CldSapReturn getPostParms(Map<String, Object> map,
			String urlHead, String key, String myMd5Source) {
		CldSapReturn errRes = new CldSapReturn();
		if (null != map) {
			CldLog.i("ols", "key: " + key);
			/**
			 * map不为空
			 */
			String md5Source = (myMd5Source != null) ? myMd5Source: CldSapParser.formatSource(map);
			if (!TextUtils.isEmpty(key)) {
				md5Source += key;
			}
			
			
			String sign = CldSapUtil.MD5(md5Source);
			map.put("sign", sign);
			CldLog.i("ols", "md5Source: " + md5Source);
			String strPost = CldSapParser.mapToJson(map);
			CldLog.i("ols", "strPost: " + strPost);
			errRes.url = urlHead;
			errRes.jsonPost = strPost;
		}
		return errRes;
	}

	/**
	 * 
	 * 协议层返回值基类
	 * 
	 * @author Zhouls
	 * @date 2015-3-18 下午3:40:24
	 */
	public static class ProtBase {
		private int errcode;
		private String errmsg;

		public ProtBase() {
			errcode = -1;
			errmsg = "";
		}

		public int getErrcode() {
			return errcode;
		}

		public void setErrcode(int errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}
	}

	/**
	 * 
	 * 密钥通用解析类
	 * 
	 * @author Zhouls
	 * @date 2015-3-18 下午3:40:49
	 */
	public static class ProtKeyCode extends ProtBase {
		/** 服务端返回密钥 */
		private String code;

		public ProtKeyCode() {
			code = "";
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}
}
