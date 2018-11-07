/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: VersionInfoActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 版本信息界面
 * @author: zhaoqy
 * @date: 2017年3月27日 上午9:35:56
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

public class VersionInfoActivity extends BaseActivity implements
		OnClickListener {

	private ImageView mBack;
	private TextView mTitle;
	
	@Override
	protected int getLayoutResID() {
		return R.layout.activity_versioninfo;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);

	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);

	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.about_version);

	}

	@Override
	protected void loadData() {

	}

	@Override
	protected void updateUI() {

	}

	@Override
	protected void messageEvent(AccountEvent event) {

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
}
