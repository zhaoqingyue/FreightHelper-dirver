/*
 * @Title CldSapReturn.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-13 下午12:09:01
 * @version 1.0
 */
package com.yunbaba.ols.tools;

import java.util.Map;

import com.yunbaba.ols.dal.CldDalKAccount;

/**
 * 协议层返回结果类
 * 
 * @author Zhouls
 * @date 2015-3-13 下午12:09:01
 */
public class CldSapReturn {
	/** 错误码 */
	public int errCode;
	/** 错误信息 */
	public String errMsg;
	/** 接口使用session */
	public String session;
	/** 返回Json */
	public String jsonReturn;
	/** 请求URL */
	public String url;
	/** PostJson串 */
	public String jsonPost;
	/** Post二进制数组 */
	public byte[] bytePost;
	/** Post标准map*/
	public Map<String, String> mapPost;

	public CldSapReturn() {
		errCode = -1;
		errMsg = "";
		session = CldDalKAccount.getInstance().getSession();
		jsonReturn = "";
		url = "";
		jsonPost = "";
	}
}
