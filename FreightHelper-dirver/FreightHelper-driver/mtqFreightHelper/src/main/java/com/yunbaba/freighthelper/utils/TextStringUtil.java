package com.yunbaba.freighthelper.utils;

import java.util.ArrayList;

import com.yunbaba.freighthelper.bean.TaskSpInfo;

import android.text.Html;
import android.text.TextUtils;

/**
 * Created by lenovo on 2016/3/22.
 */
public class TextStringUtil {

	public static boolean isEmpty(String str) {

		// "".equals(str.trim()) 之所以不写成str.trim().equals("");
		// 是防止空指针，但这里之前已经判断了str == null
		return str == null || "".equals(str.trim());
	}

	public static ArrayList<String> splitPhoneString(String sourceStr) {

		if (sourceStr == null)
			return null;

		String[] sourceStrArray = sourceStr.split(",");
		ArrayList<String> list = new ArrayList<>();

		for (int i = 0; i < sourceStrArray.length; i++) {
			if (!TextUtils.isEmpty(sourceStrArray[i]))
				list.add(sourceStrArray[i]);

		}

		return list;
	}

	public static ArrayList<String> splitFeedBackReasonString(String sourceStr) {

		if (sourceStr == null)
			return null;

		
		String[] sourceStrArray = sourceStr.split("\\|");
		ArrayList<String> list = new ArrayList<>();

		for (int i = 0; i < sourceStrArray.length; i++) {
			if (!TextUtils.isEmpty(sourceStrArray[i]))
				list.add(sourceStrArray[i]);

		}

		return list;
	}

	public static boolean isContain(ArrayList<TaskSpInfo> arrayList, String str) {

		if (arrayList == null)
			return false;

		if (str == null)
			return false;

		boolean res = false;

		over: for (TaskSpInfo tmpstr : arrayList) {

			if (str.equals(tmpstr.taskid)) {

				res = true;
				break over;
			}

		}

		return res;
	}

	public static boolean isContain(ArrayList<TaskSpInfo> arrayList, String str, String cust_orderid) {


		if (arrayList == null)
			return false;

		if (cust_orderid == null)
			return false;

		boolean res = false;

		over: for (TaskSpInfo tmpstr : arrayList) {

			if (str.equals(tmpstr.taskid)) {

				if (!TextUtils.isEmpty(tmpstr.cu_orderid) && !TextUtils.isEmpty(cust_orderid)) {

					if (tmpstr.cu_orderid.equals(cust_orderid)) {

						res = true;
						break over;
					}
				}
			}

		}

		return res;
	}

	public static boolean isContainStr(ArrayList<String> arrayList, String mTaskId) {

		if (arrayList == null)
			return false;

		if (mTaskId == null)
			return false;

		boolean res = false;

		over: for (String tmpstr : arrayList) {

			if (!TextUtils.isEmpty(tmpstr)) {

				if (mTaskId.equals(tmpstr)) {

					res = true;
					break over;
				}
			}

		}

		return res;
	}

	public static String ReplaceHtmlTag(String sourcestr) {

		String resstr = sourcestr;

		while (resstr.contains("&lt;") || resstr.contains("&gt;") || resstr.contains("&amp;")
				|| resstr.contains("&apos;") || resstr.contains("&quot;")) {

			resstr = Html.fromHtml(resstr).toString();

		}

		return resstr;

	}

}
