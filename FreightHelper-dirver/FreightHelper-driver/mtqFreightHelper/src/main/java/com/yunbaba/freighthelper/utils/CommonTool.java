package com.yunbaba.freighthelper.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;

public class CommonTool {

	// 防止按钮快速点击造成重复事件
	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 1000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	// 把String转化为double
	public static double convertToDouble(String number, double defaultValue) {
		if (TextUtils.isEmpty(number)) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(number);
		} catch (Exception e) {
			return defaultValue;
		}

	}

	/**
	 * 禁止EditText输入空格 禁止EditText输入特殊字符
	 * 
	 * @param editText
	 */
	public static void setEditTextInhibitInputSpace(EditText editText) {
		InputFilter filter = new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				if (source.equals(" "))
					return "";
				else
					return null;
			}
		};

		InputFilter filter2 = new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				String speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
				Pattern pattern = Pattern.compile(speChat);
				Matcher matcher = pattern.matcher(source.toString());
				if (matcher.find())
					return "";
				else
					return null;
			}
		};
		editText.setFilters(new InputFilter[] { filter, filter2 });
	}
	
	//重启应用
	public static void restartApplication(Context context) {  
		
		MLog.w("重启应用", "重启应用");
		
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());  
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
        context.startActivity(intent);  
	}  

}
