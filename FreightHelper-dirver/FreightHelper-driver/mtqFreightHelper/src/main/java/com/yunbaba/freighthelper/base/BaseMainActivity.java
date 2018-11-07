package com.yunbaba.freighthelper.base;

import android.content.Intent;
import android.os.Bundle;

import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.freighthelper.ui.activity.RestoreLoginActivity;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SystemBarHintUtil;

import me.yokeyword.fragmentation.SupportActivity;

import static com.yunbaba.freighthelper.utils.CompatibilityUtil.MIUIANDFlymeStatusBarColorCheck;

public abstract class BaseMainActivity extends SupportActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResID());
		//AppManager.getInstance().addActivity(this);

		SystemBarHintUtil.setSystemBarHint(this);


		MIUIANDFlymeStatusBarColorCheck(this.getWindow(), true);


		initFragment(savedInstanceState);
		initViews();
		setListener();
		afterInit();
	}

	protected abstract int getLayoutResID();

	protected abstract void initViews();

	protected abstract void setListener();

	protected abstract void initFragment(Bundle savedInstanceState);

	protected abstract void afterInit();

	protected boolean isRunning = false;

	@Override
	protected void onPause() {

		super.onPause();
		isRunning = false;

	}

	@Override
	protected void onResume() {

		super.onResume();
		isRunning = true;
	}

	public static final int ReStoreLoginResult = 6399;

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);

		//MLog.e("check", "onRestoreInstanceState");

		if (isNeedToRestoreLoginWhenAppWasRecycle()) {

			if (!(AccountAPI.getInstance().isLogined())) {

				//MLog.e("check", "start RestoreLoginActivity");

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
