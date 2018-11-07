/*
 * @Title CldKReturn.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-11-16 上午11:29:41
 * @version 1.0
 */
package com.yunbaba.ols.tools.parse;

import java.util.Map;

/**
 * 协议层和逻辑层参数交换类
 * 
 * @author Zhouls
 * @date 2015-11-16 上午11:29:41
 */
public class CldKReturn {
	/** 错误码 */
	public int errCode;
	/** 错误信息 */
	public String errMsg;
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

	public CldKReturn() {
		errCode = -1;
		errMsg = "";
		jsonReturn = "";
		url = "";
		jsonPost = "";
	}
}
