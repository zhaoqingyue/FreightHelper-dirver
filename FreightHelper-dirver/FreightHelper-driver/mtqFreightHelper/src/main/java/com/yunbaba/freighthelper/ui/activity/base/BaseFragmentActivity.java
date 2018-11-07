/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: BaseActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity
 * @Description: 基类Activity
 * @author: zhaoqy
 * @date: 2017年3月11日 下午3:46:04
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.ui.activity.RestoreLoginActivity;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SystemBarHintUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.yunbaba.freighthelper.utils.CompatibilityUtil.FlymeSetStatusBarLightMode;
import static com.yunbaba.freighthelper.utils.CompatibilityUtil.MIUISetStatusBarLightMode;

public abstract class BaseFragmentActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResID());
	//	AppManager.getInstance().addActivity(this);
		SystemBarHintUtil.setSystemBarHint(this);


		MIUISetStatusBarLightMode(this.getWindow(), true);
		FlymeSetStatusBarLightMode(this.getWindow(), true);

		initViews();
		setListener();
		initData();
		loadData();
		updateUI();
	}

	protected abstract int getLayoutResID();

	protected abstract void initViews();

	protected abstract void setListener();

	protected abstract void initData();

	protected abstract void loadData();

	protected abstract void updateUI();

	protected abstract void messageEvent(AccountEvent event);

	@Override
	protected void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		AppStatApi.statOnPageStart(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppStatApi.statOnPagePause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(AccountEvent event) {
		messageEvent(event);
	}

	public static final int ReStoreLoginResult = 6399;

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);

	//	MLog.w("check", "onRestoreInstanceState");

		if (isNeedToRestoreLoginWhenAppWasRecycle()) {

			if (!(AccountAPI.getInstance().isLogined())) {

			//	MLog.e("check", "start RestoreLoginActivity");

				startActivityForResult(new Intent(this, RestoreLoginActivity.class), ReStoreLoginResult);

			}

		}
	}

	public boolean isNeedToRestoreLoginWhenAppWasRecycle() {

		return true;

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {

		super.onActivityResult(arg0, arg1, arg2);

		if (arg0 == ReStoreLoginResult) {

			ReRestoreDataAndUI();
		}

	}

	public void ReRestoreDataAndUI() {

		MLog.w("ReRestoreDataAndUI", "ReRestoreDataAndUI");

		// this.recreate();

	}
}
