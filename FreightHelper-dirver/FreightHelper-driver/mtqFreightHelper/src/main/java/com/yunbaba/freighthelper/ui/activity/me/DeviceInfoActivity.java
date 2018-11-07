/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: DeviceInfoActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 设备信息界面
 * @author: zhaoqy
 * @date: 2017年3月20日 下午6:44:04
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.me;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;

public class DeviceInfoActivity extends BaseActivity implements OnClickListener {
	private ImageView mBack;
	private TextView mTitle;
	private TextView mType;
	private TextView mSerial;
	private TextView mExpirate;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_deviceinfo;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mType = (TextView) findViewById(R.id.deviceinfo_type);
		mSerial = (TextView) findViewById(R.id.deviceinfo_serial);
		mExpirate = (TextView) findViewById(R.id.deviceinfo_expirate);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);

	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.deviceinfo);
	}

	@Override
	protected void loadData() {

	}

	@Override
	protected void updateUI() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img: {
			finish();
			break;
		}
		default:
			break;
		}
	}

	@Override
	protected void messageEvent(AccountEvent event) {

	}
}
