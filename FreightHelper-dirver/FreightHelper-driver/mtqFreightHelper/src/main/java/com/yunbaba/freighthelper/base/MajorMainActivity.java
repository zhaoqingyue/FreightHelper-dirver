package com.yunbaba.freighthelper.base;

import android.os.Bundle;
import android.os.Handler;

import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.freighthelper.utils.CommonTool;

import me.yokeyword.fragmentation.SupportActivity;

import static com.yunbaba.freighthelper.utils.CompatibilityUtil.MIUIANDFlymeStatusBarColorCheck;

public abstract class MajorMainActivity extends SupportActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResID());
		//AppManager.getInstance().addActivity(this);

		//SystemBarHintUtil.setSystemBarHint(this);
		MIUIANDFlymeStatusBarColorCheck(this.getWindow(), true);

		showProgress();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				initFragment(savedInstanceState);
				initViews();
				setListener();
				afterInit();
			}
		},500);

	}

	protected abstract void showProgress();

	protected abstract int getLayoutResID();

	protected abstract void initViews();

	protected abstract void setListener();

	protected abstract void initFragment(Bundle savedInstanceState);

	protected abstract void afterInit();

	protected boolean isRunning = false;

	@Override
	protected void onPause() {

		super.onPause();

		AppStatApi.statOnPagePause(this);
		isRunning = false;

	}

	@Override
	protected void onResume() {

		super.onResume();
		AppStatApi.statOnPageStart(this);

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

				CommonTool.restartApplication(this);

			}

		}
	}

	public boolean isNeedToRestoreLoginWhenAppWasRecycle() {

		return true;

	}

	
	
}
