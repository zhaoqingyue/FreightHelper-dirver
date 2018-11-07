package com.yunbaba.freighthelper.utils;

public class FormatUtils {

	/**
	 * @Description 米距离转字串
	 * @param in_iDis
	 * @param in_bAlpha 是否为字母单位
	 * @return String
	 */
	public static String meterDisToString(int in_iDis, boolean in_bAlpha) {
		String strDis  = "";
		String strUnit = "";
		
		if (in_bAlpha)
			strUnit = "km";
		else
			strUnit = "公里";

		int iInt = in_iDis / 1000;
		int iDec = in_iDis % 1000;
		if (iInt >= 1000) {  // >=1000公里
			strDis = String.format("%d%s", iInt, strUnit);
		}
		else if (iInt >= 10) {  // >=10公里
			iDec = iDec / 100;
			strDis = String.format("%d.%d%s", iInt, iDec, strUnit);
		}
		else if (iInt >= 1) {  // >=1公里
			iDec = iDec / 10;
			strDis = String.format("%d.%02d%s", iInt, iDec, strUnit);
		} else {
			if (in_bAlpha)
				strUnit = "m";
			else
				strUnit = "米";
			strDis = String.format("%d%s", iDec, strUnit);
		}
		return strDis;
	}
	
	
	public static String formatTime(int iTimeSec) {
		String strTime = null;
		int lHour = iTimeSec / 3600;
		int lMinute = (iTimeSec % 3600) / 60;
		if (iTimeSec > 0 && iTimeSec < 60)
			lMinute = 1;
		if (lHour > 99) {
			strTime = "--:--";
		} else {
			strTime = String.format("%02d:%02d", lHour, lMinute);
		}
		return strTime;
	}
	
	/**
	 * @Title: formatTime
	 * @Description: 秒转换成字符串
	 * @param time
	 * @param in_bAlpha 是否为字母单位
	 * @return
	 * @return: String
	 */
	public static String formatTime(int time,boolean in_bAlpha) {
		if (in_bAlpha){
			return formatTime(time);
		}
		
		String strText = "";
		int hour = 0, minute = 0;
		if (time < 60) {
			time = 60;
		}
		hour = time / 3600;
		minute = (time / 60) % 60;

		if (hour <= 0){
			strText = minute +"分钟";			
		}else if(hour < 100){
			if (minute > 0){
				strText = hour + "小时"+ minute +"分钟";
			}else{
				strText = hour + "小时";
			}
		}else{
			strText = hour + "小时";
		}
		return strText;
	}
	
}
