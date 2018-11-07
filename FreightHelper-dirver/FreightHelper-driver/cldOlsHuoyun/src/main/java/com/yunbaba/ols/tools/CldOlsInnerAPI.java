/*
 * @Title CldOlsInnerAPI.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-12-11 下午4:45:34
 * @version 1.0
 */
package com.yunbaba.ols.tools;

/**
 * 模块内部插入接口
 * 
 * @author Zhouls
 * @date 2015-12-11 下午4:45:34
 */
public class CldOlsInnerAPI {

	private ICldOlsInnerListener listener;

	public void setListener(ICldOlsInnerListener listener) {
		if (null != listener) {
			this.listener = listener;
		}
	}

	/**
	 * 处理输入输出
	 * 
	 * @param getURL
	 *            url
	 * @param postJson
	 *            postjson
	 * @param returnJson
	 *            webreturn
	 * @param fuctionName
	 *            fuctionName 方法名
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-11 下午4:54:12
	 */
	public void dealInOut(String getURL, String postJson, String fuctionName,
			int apiNum, Object parm, Object result) {
		if (null != listener) {
			listener.dealInOut(getURL, postJson, fuctionName, apiNum, parm,
					result);
		}
	}

	/**
	 * 
	 * 接口内部插入回调
	 * 
	 * @author Zhouls
	 * @date 2015-12-11 下午4:48:36
	 */
	public static interface ICldOlsInnerListener {

		/**
		 * 处理事务
		 * 
		 * @param getURL
		 *            url
		 * @param postJson
		 *            post json
		 * @param fuctionName
		 *            方法名
		 * @param apiNum
		 *            接口编号
		 * @param parm
		 *            请求参数
		 * @param result
		 *            输出结果
		 * @return void
		 * @author Zhouls
		 * @date 2015-12-14 下午3:33:42
		 */
		public void dealInOut(String getURL, String postJson,
				String fuctionName, int apiNum, Object parm, Object result);
	}

	private static CldOlsInnerAPI cldOlsInnerAPI;

	public static CldOlsInnerAPI getInstance() {
		if (null == cldOlsInnerAPI) {
			cldOlsInnerAPI = new CldOlsInnerAPI();
		}
		return cldOlsInnerAPI;
	}

	private CldOlsInnerAPI() {

	}
}
