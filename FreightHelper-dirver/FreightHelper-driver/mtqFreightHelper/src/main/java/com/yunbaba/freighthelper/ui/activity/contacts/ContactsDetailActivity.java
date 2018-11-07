/**
 *
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: ContactsDetailActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity
 * @Description: 通讯录详情Activity
 * @author: zhaoqy
 * @date: 2017年3月11日 下午3:45:35
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.contacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.ContactsInfo;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.utils.CallUtil;

public class ContactsDetailActivity extends BaseActivity implements
		OnClickListener {
	private ImageView mBack;
	private TextView mTitle;
	private TextView mFirst;
	private TextView mName;
	private TextView mTeam;
	private TextView mPhone;
	private ContactsInfo mContactsInfo;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_contacts_detail;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mFirst = (TextView) findViewById(R.id.contacts_detail_first);
		mName = (TextView) findViewById(R.id.contacts_detail_name);
		mTeam = (TextView) findViewById(R.id.contacts_detail_team);
		mPhone = (TextView) findViewById(R.id.contacts_detail_phone);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
		findViewById(R.id.contacts_detail_call).setOnClickListener(this);
	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.contacts_detail);
	}

	@Override
	protected void loadData() {
		//Bundle bundle = getIntent().getExtras();
		if (getIntent() == null || getIntent().getParcelableExtra("contactsInfo") == null)
			finish();
		mContactsInfo = getIntent().getParcelableExtra("contactsInfo");
	}

	@Override
	protected void updateUI() {
		if (mContactsInfo != null) {
			mFirst.setText(mContactsInfo.getFirst());
			mName.setText(mContactsInfo.getName());
			mTeam.setText(mContactsInfo.getTeam());
			mPhone.setText(mContactsInfo.getPhone());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_left_img: {
				finish();
				break;
			}
			case R.id.contacts_detail_call: {
				call();
				break;
			}
			default:
				break;
		}
	}

	private void call() {
		final String phone = mContactsInfo.getPhone();
//		String cancel = getResources().getString(R.string.dialog_cancel);
//		String sure = getResources().getString(R.string.dialog_call);
//		PhoneCallDialog dialog = new PhoneCallDialog(this, phone, cancel, sure,
//				new PhoneCalldialog.iPhoneCallDialogListener() {
//
//					@Override
//					public void OnCancel() {
//
//					}
//
//					@Override
//					public void OnSure(String phones) {
//						onSure(phones);
//					}
//				});
//		dialog.show();

		CallUtil.call(this, phone);
	}

	@SuppressLint("NewApi")
	private void onSure(String phone) {
		if (!TextUtils.isEmpty(phone)) {
			/*try {
				intentToCall(phone);
			} catch (Exception e) {

			}*/

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				// 在6.0 系统中请求某些权限需要检查权限
				if (!hasPermission()) {
					// 动态请求拨打电话权限
					requestPermissions(
							new String[]{Manifest.permission.CALL_PHONE},
							0x11);
				} else {
					intentToCall(phone);
				}
			} else {
				intentToCall(phone);
			}
		}
	}

	@SuppressLint("NewApi")
	private boolean hasPermission() {
		String permission = Manifest.permission.CALL_PHONE;
		if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
			return false;
		}
		return true;
	}

	private void intentToCall(String phoneNumber) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		Uri data = Uri.parse("tel:" + phoneNumber);
		intent.setData(data);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		startActivity(intent);
	}

	/**
	 * * 动态请求拨打电话权限后，监听用户的点击事件
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		switch (requestCode) {
		case 0x11:
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission Granted
				String phone = mContactsInfo.getPhone();
				intentToCall(phone);
			} else {
				// Permission Denied
			}
			break;
		default:
			super.onRequestPermissionsResult(requestCode, permissions,
					grantResults);
		}
	}

	@Override
	protected void messageEvent(AccountEvent event) {

	}


}
