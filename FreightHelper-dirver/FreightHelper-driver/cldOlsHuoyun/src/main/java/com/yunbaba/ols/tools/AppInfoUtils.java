package com.yunbaba.ols.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.DisplayMetrics;

public class AppInfoUtils {
	
	public static String getPackName(Context context) {
		return context.getPackageName();
	}
	
	
	/**
	 * @Title: getAppName
	 * @Description: 获取app名称
	 * @param context
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 通过pkgname获取版本号VerCode
	 * 
	 * @param context
	 * @param pkgname
	 */
	public static int getVerCode(Context context) {
		int verCode = 0;
		String pkgname = context.getPackageName();
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = pm.getPackageInfo(pkgname,
					PackageManager.GET_PERMISSIONS);
			if (packageInfo != null) {
				verCode = packageInfo.versionCode;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}

	/**
	 * 通过pkgname获取版本号VerName
	 * 
	 * @param context
	 * @param pkgname
	 */
	public static String getVerName(Context context) {
		String verName = "";
		String pkgname = context.getPackageName();
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = pm.getPackageInfo(pkgname,
					PackageManager.GET_PERMISSIONS);
			if (packageInfo != null) {
				verName = packageInfo.versionName;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}

	/**
	 * @Title: getSystemVer
	 * @Description: 获取android系统版本号
	 */
	public static String getSystemVer() {
		String version_release = Build.VERSION.RELEASE; // 系统版本号
		return version_release;
	}

	/**
	 * @Title: getDensityWidth
	 * @Description: 获取DensityWidth
	 * @param context
	 */
	public static int getDensityWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * @Title: getDensityHeight
	 * @Description: 获取DensityHeight
	 * @param context
	 */
	public static int getDensityHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	/**
	 * @Title: getDensityDpi
	 * @Description: 获取DensityDpi
	 * @param context
	 */
	public static int getDensityDpi(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.densityDpi;
	}

	/**
	 * @Title: getDensity
	 * @Description: 获取Density
	 * @param context
	 */
	public static float getDensity(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.density;
	}
}
