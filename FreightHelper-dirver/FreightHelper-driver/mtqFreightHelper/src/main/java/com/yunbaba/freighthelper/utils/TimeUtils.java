package com.yunbaba.freighthelper.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.yunbaba.freighthelper.bean.CustomDate;

public class TimeUtils {

	public static final int SECONDS_IN_DAY = 60 * 60 * 24;
	public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

	public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
		final long interval = ms1 - ms2;
		return interval < MILLIS_IN_DAY && interval > -1L * MILLIS_IN_DAY
				&& toDay(ms1) == toDay(ms2);
	}

	@SuppressLint("UseValueOf")
	public static boolean isSameDayOfMillis(final String s1, final String s2) {
		long ms1 = new Long(s1) * 1000;
		long ms2 = new Long(s2) * 1000;
		final long interval = ms1 - ms2;
		return interval < MILLIS_IN_DAY && interval > -1L * MILLIS_IN_DAY
				&& toDay(ms1) == toDay(ms2);
	}

	private static long toDay(long millis) {
		return (millis + TimeZone.getDefault().getOffset(millis))
				/ MILLIS_IN_DAY;
	}

	/**
	 * @Title: getLong
	 * @Description: 将时间戳字符串转化成毫秒单位的Long型
	 * @param stamp
	 * @return: Long
	 */
	@SuppressLint("UseValueOf")
	public static Long getLong(String stamp) {
		long l = new Long(stamp);
		 long timeStamp = l * 1000;
		return timeStamp;
	}

	/**
	 * @Title: getTimestamp
	 * @Description: 将 yyyy-MM-dd HH:mm:ss格式的时间转化成时间戳
	 * @param formattime
	 * @return: String
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getTimestamp(String formattime) {
		String stamp = "";
		if (!TextUtils.isEmpty(formattime)) {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date = null;
			try {
				date = format.parse(formattime);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (date != null) {
				stamp = date.getTime() + "";
			}
		}
		return stamp;
	}
	
	/**
	 * 
	 * @Title: isOverdue
	 * @Description: 将 yyyy-MM-dd格式的时间与当前时间比较
	 * @param formattime
	 * @return: boolean
	 */
	@SuppressLint("SimpleDateFormat") 
	public static boolean isOverdue(String formattime) {
		boolean overdue = false;
		long duestamp = 0;
		long curstamp = System.currentTimeMillis();
		if (!TextUtils.isEmpty(formattime)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = format.parse(formattime);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (date != null) {
				duestamp = date.getTime();
				MLog.e("TimeUtils", "duestamp: " + duestamp);
				MLog.e("TimeUtils", "curstamp: " + curstamp);
				if (duestamp >= curstamp) {
					overdue = false;
				} else {
					overdue = true;
				}
			}
		}
		return overdue;
	}

	/**
	 * @Title: stampToDate
	 * @Description: 将时间戳转换为yyyy-MM-dd HH:mm:ss格式的时间
	 * @param stamp
	 * @return: String
	 */
	@SuppressLint({ "SimpleDateFormat", "UseValueOf" })
	public static String stampToDate(String stamp) {
		String res = "";
		String ftime = "";
		SimpleDateFormat dateFormat = null;
		long timeStamp = new Long(stamp);

		long lt = timeStamp / 86400000;
		long ct = System.currentTimeMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			dateFormat = new SimpleDateFormat("HH:mm");
			ftime = "今天 ";
		} else if (days == 1) {
			dateFormat = new SimpleDateFormat("HH:mm");
			ftime = "昨天 ";
		} else if (days == 2) {
			dateFormat = new SimpleDateFormat("HH:mm");
			ftime = "前天 ";
		} else {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}

		Date date = new Date(timeStamp);
		res = ftime + dateFormat.format(date);
		
		//MLog.e("zhaoqy", "stamp: " + stamp + ", res: " + res);
		return res;
	}

	public static String getStarttime() {
		String starttime = (System.currentTimeMillis() - TimeUtils.MILLIS_IN_DAY * 31)
				/ 1000 + "";
		return starttime;
	}

	public static String getEndtime() {
		String endtime = System.currentTimeMillis() / 1000 + "";
		return endtime;
	}

	@SuppressLint({ "SimpleDateFormat", "UseValueOf" })
	public static String stampToHour(String s) {
		String res = "";
		SimpleDateFormat dateFormat = null;
		dateFormat = new SimpleDateFormat("HH:mm");
		long l = new Long(s);
		long timeStamp = l * 1000;
//		long timeStamp = new Long(s);
		// long timeStamp = l * 1000;

		Date date = new Date(timeStamp);
		res = dateFormat.format(date);
		return res;
	}

	@SuppressLint({ "SimpleDateFormat", "UseValueOf" })
	public static String stampToFormat(String stamp) {
		String res = "";
		SimpleDateFormat dateFormat = null;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		long l = new Long(stamp);
		long timeStamp = l * 1000;

		Date date = new Date(timeStamp);
		res = dateFormat.format(date);
		return res;
	}
	
	@SuppressLint({ "SimpleDateFormat", "UseValueOf" })
	public static String stampToYMDHMS(String stamp) {
		String res = "";
		SimpleDateFormat dateFormat = null;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long l = new Long(stamp);
		long timeStamp = l * 1000;

		Date date = new Date(timeStamp);
		res = dateFormat.format(date);
		return res;
	}
	
	@SuppressLint({ "SimpleDateFormat", "UseValueOf" })
	public static String stampToYMDHMS1(String stamp) {
		String res = "";
		SimpleDateFormat dateFormat = null;
		dateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss"); 
		long l = new Long(stamp);
		long timeStamp = l * 1000;

		Date date = new Date(timeStamp);
		res = dateFormat.format(date);
		return res;
	}

	@SuppressLint({ "SimpleDateFormat", "UseValueOf" })
	public static String stampToDay(String seconds) {
		String res = "";
		SimpleDateFormat dateFormat = null;
		dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		long l = new Long(seconds);
		long timeStamp = l * 1000;

		Date date = new Date(timeStamp);
		res = dateFormat.format(date);
		return res;
	}

	@SuppressLint("SimpleDateFormat")
	public static String dateFromString(CustomDate date) {
		String year = date.year + "";
		String month = "";
		if (date.month > 9) {
			month = date.month + "";
		} else {
			month = "0" + date.month;
		}
		String day = "";
		if (date.day > 9) {
			day = date.day + "";
		} else {
			day = "0" + date.day;
		}
		return year + "/" + month + "/" + day;
	}
	
	public static String stampToFormat(String stamp,String format) {
		String res = "";
		SimpleDateFormat dateFormat = null;
		dateFormat = new SimpleDateFormat(format);
		long l = new Long(stamp);
		long timeStamp = l * 1000;

		Date date = new Date(timeStamp);
		res = dateFormat.format(date);
		return res;
	}
	
	public static String secondToMinute(String second){
		String minute = "";
		
		if (!TextUtils.isEmpty(second)){
			int s = Integer.valueOf(second);
			s += 30;
			minute = s/60 +"min";
		}		
		
		return minute;
	}
	
	
	/**
	 * @Title: changeDay
	 * @Description: 改变天数
	 * @param startTime：时间戳
	 * @param changeDay(改变的天数，可以加或者减)
	 * @return
	 * @return: 返回改变天数后的字符串。
	 */
	public static String changeDay(String startTime,int changeDay)
	{
		String add = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			
			long l = new Long(startTime);
			long timeStamp = l * 1000;
	
			Date timeNow = new Date(timeStamp);
			
			Calendar begin=Calendar.getInstance();
			begin.setTime(timeNow);
			begin.add(Calendar.DAY_OF_MONTH,changeDay);
			add = df.format(begin.getTime());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return add;
	}
	
	
	/**
	 * @Title: changeDay
	 * @Description: TODO
	 * @param changeDay(改变的天数，可以加或者减)
	 * @param startDay:换算好的日期字符串
	 * @return
	 * @return: String ：返回改变天数后的字符串。
	 */
	public static String changeDay(int changeDay,String startDay)
	{
		String add = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			
			Date timeNow = new Date(startDay);			
			Calendar begin=Calendar.getInstance();
			begin.setTime(timeNow);
			begin.add(Calendar.DAY_OF_MONTH,changeDay);
			add = df.format(begin.getTime());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return add;
	}
	
	
	/**
	 * @Title: daysBetweenByDate
	 * @Description: TODO
	 * @param smdate:
	 * @param bdate
	 * @return:两个date间隔多少天
	 * @return: int
	 */
	public static int daysBetweenByDate(String smdate,String bdate) {  
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");  
        Calendar cal = Calendar.getInstance();  
        try {
			cal.setTime(sdf.parse(smdate));
		} catch (ParseException e) {

			e.printStackTrace();
		}    
        long time1 = cal.getTimeInMillis();            
        try {
			cal.setTime(sdf.parse(bdate));
		} catch (ParseException e) {

			e.printStackTrace();
		}    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));     
    }
}
