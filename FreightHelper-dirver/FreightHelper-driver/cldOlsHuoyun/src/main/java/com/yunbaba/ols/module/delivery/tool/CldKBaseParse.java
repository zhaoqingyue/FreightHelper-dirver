/*
 * @Title CldKBaseParse.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-18 下午3:12:51
 * @version 1.0
 */
package com.yunbaba.ols.module.delivery.tool;

/**
 * 解析层基类
 * 
 * @author Zhouls
 * @date 2015-3-18 下午3:12:51
 */
public class CldKBaseParse {
	/**
	 * 
	 * 协议层返回值基类
	 * 
	 * @author Zhouls
	 * @date 2015-3-18 下午3:40:24
	 */
	public static class ProtBase {
		public int errcode;
		public String errmsg;
		public int errorcode;
		public String errormsg;

		public ProtBase() {
			errcode = -1;
			errmsg = "";
			errorcode = -1;
			errormsg = "";
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

		public int getErrorcode() {
			return errorcode;
		}

		public void setErrorcode(int errorcode) {
			this.errorcode = errorcode;
		}

		public String getErrormsg() {
			return errormsg;
		}

		public void setErrormsg(String errormsg) {
			this.errormsg = errormsg;
		}
	}

	/**
	 * 
	 * 协议层返回值基类
	 * 
	 * @author Zhouls
	 * @date 2015-3-18 下午3:40:24
	 */
	public static class ProtBaseSpec {
		public int errorcode;
		public String errormsg;

		public ProtBaseSpec() {
			errorcode = -1;
			errormsg = "";
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
		public String code;

		public ProtKeyCode() {
			code = "";
		}
	}
}
