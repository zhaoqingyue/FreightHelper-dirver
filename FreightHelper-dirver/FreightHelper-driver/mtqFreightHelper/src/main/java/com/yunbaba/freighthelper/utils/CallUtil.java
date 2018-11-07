package com.yunbaba.freighthelper.utils;

import java.util.ArrayList;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.ui.dialog.PhoneCallDialog;
import com.yunbaba.freighthelper.ui.dialog.PhoneCallDialog.IPhoneCallDialogListener;
import com.yunbaba.freighthelper.ui.dialog.PromptDialog;
import com.yunbaba.freighthelper.ui.dialog.PromptDialog.IPromptDialogListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

public class CallUtil {

	public static void call(final Activity context, String phones) {
		final String phone = phones;
		String cancel = context.getResources().getString(R.string.dialog_cancel);
		String sure = context.getResources().getString(R.string.dialog_call);

		boolean isSingleNum = true;

		if (!phone.contains(",")) {
			isSingleNum = true;
		} else {

			ArrayList<String> phonelist = TextStringUtil.splitPhoneString(phone);
			if(phonelist!=null && phonelist.size()>=2){
				
				isSingleNum = false;
				
			}
			
		}

		if (isSingleNum) {
			PromptDialog dialog = new PromptDialog(context, phone.replaceAll(",", ""), cancel, sure, new IPromptDialogListener() {

				@Override
				public void OnSure() {

					onSure(context, phone.replace(",", ""));
				}

				@Override
				public void OnCancel() {

					
				}
			});

			dialog.show();
		} else {
			PhoneCallDialog dialog = new PhoneCallDialog(context, phone, cancel, sure, new IPhoneCallDialogListener() {

				@Override
				public void OnCancel() {

				}

				@Override
				public void OnSure(String phones) {
					onSure(context, phones);
				}
			});
			dialog.show();
		}
	}

	@SuppressLint("NewApi")
	private static void onSure(Activity context, String phone) {
		if (!TextUtils.isEmpty(phone)) {
			/*
			 * try { intentToCall(phone); } catch (Exception e) {
			 * 
			 * }
			 */

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				// 在6.0 系统中请求某些权限需要检查权限
				if (!hasPermission(context)) {
					// 动态请求拨打电话权限
					context.requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 0x11);
				} else {
					intentToCall(context, phone);
				}
			} else {
				intentToCall(context, phone);
			}
		}
	}

	@SuppressLint("NewApi")
	private static boolean hasPermission(Activity context) {
		String permission = Manifest.permission.CALL_PHONE;
		if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
			return false;
		}
		return true;
	}

	public static void intentToCall(Context context, String phoneNumber) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		Uri data = Uri.parse("tel:" + phoneNumber);
		intent.setData(data);
		context.startActivity(intent);
	}

}
