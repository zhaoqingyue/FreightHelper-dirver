package com.yunbaba.freighthelper.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

public class PermissionUtil {

	/**
	 * 是否需要弹出权限询问框 如果是则返回true并请求权限 否则返回false requestCode
	 * 用于在activity里onactivityresult 监听权限请求结果
	 */
	public static boolean isNeedPermission(Activity context, String permission, int requestCode) {
		if (!isGranted(context, permission)) {
			// if (ActivityCompat.shouldShowRequestPermissionRationale(context,
			// permission)) {
			//
			// } else {
			//
			// }

			ActivityCompat.requestPermissions(context, new String[] { permission }, requestCode);
			return true;
		} else {
			// 直接执行相应操作了

			return false;
		}
	}

	/**
	 * 是否需要弹出权限询问框 如果是则返回true并请求权限 否则返回false
	 */
	public static boolean isNeedPermission(Activity context, String permission) {
		if (!isGranted(context, permission)) {
			// if (ActivityCompat.shouldShowRequestPermissionRationale(context,
			// permission)) {
			//
			// } else {
			//
			// }

			ActivityCompat.requestPermissions(context, new String[] { permission }, 99);
			return true;
		} else {
			// 直接执行相应操作了

			return false;
		}
	}

	/**
	 * 是否需要弹出权限询问框 For 储存空间权限
	 */
	public static boolean isNeedPermissionForStorage(Activity context) {
		if (!isGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				|| !isGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
			// if (ActivityCompat.shouldShowRequestPermissionRationale(context,
			// Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			//
			// } else {
			//
			// }
			ActivityCompat.requestPermissions(context, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.READ_EXTERNAL_STORAGE }, 99);
			return true;
		} else {
			// 直接执行相应操作了

			return false;
		}
	}

	@SuppressLint("NewApi")
	public static boolean isGranted(Context context, String permission) {

		boolean result = true;

		int targetSdkVersion = 23;

		try {
			final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			targetSdkVersion = info.applicationInfo.targetSdkVersion;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (targetSdkVersion >= Build.VERSION_CODES.M) {
				// targetSdkVersion >= Android M, we can
				// use Context#checkSelfPermission
				result = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
			} else {
				// targetSdkVersion < Android M, we have to use
				// PermissionChecker
				result = PermissionChecker.checkSelfPermission(context,
						permission) == PermissionChecker.PERMISSION_GRANTED;
			}
		}
		// int checkSelfPermission = ContextCompat.checkSelfPermission(context,
		// permission);
		// return checkSelfPermission == PackageManager.PERMISSION_GRANTED;

		return result;
	}

	//
	//
	// @Override
	// public void onRequestPermissionsResult(int requestCode, String[]
	// permissions, int[] grantResults) {
	// if (requestCode == CAMERA) {
	// if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
	// String jpgPath = getCacheDir() + "test.jpg";
	// takePhotoByPath(jpgPath, 2);
	// } else {
	// // Permission Denied
	// Toast.makeText(MainActivity.this, "您没有授权该权限，请在设置中打开授权",
	// Toast.LENGTH_SHORT).show();
	// }
	// return;
	// }
	// super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	// }
	//

}
