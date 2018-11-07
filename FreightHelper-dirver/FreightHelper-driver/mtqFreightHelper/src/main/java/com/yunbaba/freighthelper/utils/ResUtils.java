package com.yunbaba.freighthelper.utils;

import com.yunbaba.freighthelper.R;

import android.content.Context;

public class ResUtils {

	/**
	 * @Title: getSex
	 * @Description: 获取性别值
	 * @param context
	 * @param sex
	 * @return: String
	 */
	public static String getSex(Context context, int sex) {
		String string = "";
		String male = context.getResources()
				.getString(R.string.modify_sex_male);
		String female = context.getResources().getString(
				R.string.modify_sex_female);
		if (sex == 2) {
			string = male;
		} else if (sex == 1) {
			string = female;
		} else {
			string = male;
		}
		return string;
	}

}
