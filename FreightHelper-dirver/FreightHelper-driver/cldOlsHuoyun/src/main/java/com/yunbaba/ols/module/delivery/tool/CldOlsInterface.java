/*
 * @Title CldOlsInterface.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2016-1-4 下午2:50:09
 * @version 1.0
 */
package com.yunbaba.ols.module.delivery.tool;

/**
 * 接口类
 * 
 * @author Zhouls
 * @date 2016-1-4 下午2:50:09
 */
public class CldOlsInterface {
	/**
	 * 只返回错误码回调
	 * 
	 * @author Zhouls
	 * @date 2015-3-4 下午4:02:01
	 */
	public static interface ICldResultListener {
		public void onGetResult(int errCode);
	}

}
