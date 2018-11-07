package com.yunbaba.freighthelper.utils;

import android.text.TextUtils;

public class MsgParseTool {

	public static String getCuOrderIdFromMsgContent(String MsgContent) {

		//MLog.e("checkck", MsgContent+"checkckckck");
		
		if (TextUtils.isEmpty(MsgContent))
			return "";

		String[] sArray = MsgContent.split("#");

		if (sArray == null || sArray.length < 5) {

			return "";

		} else {

			return sArray[4];
		}

	}

}
