package com.yunbaba.freighthelper.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.yunbaba.freighthelper.R;

import android.content.Context;

/**
 * 时间与时间戳转换工具
 */
public class TimestampTool {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
	private static SimpleDateFormat sdfsp = new SimpleDateFormat("HH:mm", Locale.CHINA);
	private static long YesterdayZeroTimeStamp; // 注意这个，要是北京时间的0点0分，应该是算出的时间戳减-8小时
	private static long TodayZeroTimeStamp;
	
	public static long getTodayZeroTimeStamp() {
		return TodayZeroTimeStamp;
	}

	public static void setTodayZeroTimeStamp(long todayZeroTimeStamp) {
		TodayZeroTimeStamp = todayZeroTimeStamp;
	}

	private static long TomorrowZeroTimeStamp;

	static {

		// sdf.setTimeZone(TimeZone.getDefault());
		long curtime = System.currentTimeMillis();
		TodayZeroTimeStamp = curtime - curtime % (24 * 60 * 60 * 1000L) - (60 * 60 * 8 * 1000L);
		TomorrowZeroTimeStamp = TodayZeroTimeStamp + (24 * 60 * 60 * 1000L);
		YesterdayZeroTimeStamp = TodayZeroTimeStamp - (24 * 60 * 60 * 1000L);
	}

	public static String getTimeDescriptionFromTimestamp(Context context, long timestamp) {

		String timestr;
		if (timestamp > TomorrowZeroTimeStamp+(24 * 60 * 60 * 1000L)) {

			timestr = sdf.format(timestamp);

		}else  if (timestamp > TomorrowZeroTimeStamp) {

			timestr = context.getString(R.string.tomorrow) + sdfsp.format(new Date(timestamp));

		} else if (timestamp > TodayZeroTimeStamp) {

			timestr = context.getString(R.string.today) + sdfsp.format(new Date(timestamp));

		} else if (timestamp > YesterdayZeroTimeStamp) {

			timestr = context.getString(R.string.yesterday) + sdfsp.format(new Date(timestamp));

		} else {

			timestr = sdf.format(timestamp);
			// MLog.e("checktime", YesterdayZeroTimeStamp +" "+timestr +"
			// "+timestamp);
			// long curtime = System.currentTimeMillis();

			// MLog.e("checktime",curtime +" "+ curtime % (24 * 60 * 60 * 1000L)
			// +" "+ (24 * 60 * 60 * 1000L));
		}
		return timestr;
	}
	
	public static long getZeroPointTimeStamp(long time){
		
		
		return (time - time % (24 * 60 * 60 * 1000L) - (60 * 60 * 8 * 1000L));
	}
	

	public static String getDate(long time) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		sdf.setTimeZone(TimeZone.getDefault());

		return sdf.format(time);
	}

	public static String getDayDate(long time) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setTimeZone(TimeZone.getDefault());

		return sdf.format(time);
	}

	public static String TransTimeZone(String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

		Date date = sdf.parse(time);
		sdf.setTimeZone(TimeZone.getDefault());

		return sdf.format(date);
	}

	public static String getTimeFromDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		sdf.setTimeZone(TimeZone.getDefault());

		return sdf.format(date);

	}

	public static String TimeToString(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		sdf.setTimeZone(TimeZone.getDefault());
		String str = sdf.format(timestamp);
		return str;
	}
	
	
	public static String DateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sdf.setTimeZone(TimeZone.getDefault());
		String str = sdf.format(date);
		return str;
	}

	public static String Date2String(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getDefault());
		String str = sdf.format(date);
		return str;
	}

	/**
	 * 
	 * TODO:输入一个时间，获取该时间的时间戳
	 * 
	 * @param @param
	 *            dateString
	 * @param @return
	 * 
	 */
	public static long string2Timestamp(String dateString) {
		Date date1 = null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);

		} catch (ParseException e) {

			e.printStackTrace();
		}
		long temp = date1.getTime();// JAVA的时间戳长度是13位
		return temp;
	}

	// 把字符串转为日期
	public static Date string2Date(String strDate) throws Exception {
		Date df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		return df;
	}

	public static long string2Timestamp2(String dateString) {
		Date date1 = null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);

		} catch (ParseException e) {

			e.printStackTrace();
		}
		long temp = date1.getTime();// JAVA的时间戳长度是13位
		return temp;
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @throws ParseException
	 * 
	 * @time 2014-4-24 下午5:16:01
	 */
	public static long getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		df.setTimeZone(TimeZone.getDefault());
		return string2Timestamp(df.format(new Date()));// new Date()为获取当前系统时间
	}

	public static String getCurrentTimeServeString() {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		df.setTimeZone(TimeZone.getDefault());
		return df.format(new Date());// new Date()为获取当前系统时间

	}

	public static String getCurrentTimeString() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");// 设置日期格式
		df.setTimeZone(TimeZone.getDefault());
		return df.format(new Date()); // new Date()为获取当前系统时间
	}

	// 比较两个时间戳的间隔是否大于n分钟

	public static boolean CompareTimestamp(long Timestampone, long Timestamptwo, int n) {

		if (Math.abs(Timestampone - Timestamptwo) / (1000 * 60) >= n)
			return true;
		else
			return false;

	}

	/**
	 * 获取系统时间戳
	 */
	public static String getTimestamp() {
		DecimalFormat mDecimalFormat = new DecimalFormat("#.######");
		String timestamp = mDecimalFormat.format(System.currentTimeMillis() / 1000.0);
		return timestamp;
	}

	/**
	 * 服务器时间转本地时间
	 */
	public static String transformTimeYear(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		format1.setTimeZone(TimeZone.getDefault());
		try {
			time = format1.format(format.parse(time));
		} catch (ParseException e) {

			e.printStackTrace();
			return "";
		}
		return time;
	}

	public static String transformTimeMonth(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		SimpleDateFormat format1 = new SimpleDateFormat("yy-MM-dd HH:mm");
		format1.setTimeZone(TimeZone.getDefault());
		try {
			time = format1.format(format.parse(time));
		} catch (ParseException e) {

			e.printStackTrace();
			return time;
		}
		return time;
	}

	public static String transformTimeMonth2(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		format1.setTimeZone(TimeZone.getDefault());
		try {
			time = format1.format(format.parse(time));
		} catch (ParseException e) {

			e.printStackTrace();
			return time;
		}
		return time;
	}

	// 时间yyyy-MM-dd HH:mm 转时间戳（秒）
	public static long StringToTimestamp(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {

			e.printStackTrace();
			return 0;
		}

		return date.getTime() / 1000;
	}

	public static CharSequence getTime(String string, String string2) {

		String a = string.replace("T", " ").replace("-", ".").substring(0, 19);
		String b = string2.replace("T", " ").replace("-", ".").substring(0, 19);

		// long start = TransTimeString(a);
		// long end = TransTimeString(b);
		// long now = System.currentTimeMillis();

		a = a.substring(0, 10);
		b = b.substring(0, 10);
		return a + "-" + b;
	}

	public static String getStoreDetailTimeHint(long time) {

		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		sdf.setTimeZone(TimeZone.getDefault());

		return sdf.format(time);
	}

	public static CharSequence getMonthDay(long time) {

		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		sdf.setTimeZone(TimeZone.getDefault());

		return sdf.format(time);
	}

	public static CharSequence getYearMonthDay(long time) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		sdf.setTimeZone(TimeZone.getDefault());

		return sdf.format(time);
	}

}
