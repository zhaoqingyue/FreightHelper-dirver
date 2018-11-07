package com.yunbaba.freighthelper.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.freighthelper.ui.activity.RestoreLoginActivity;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SystemBarHintUtil;

import butterknife.ButterKnife;

import static com.yunbaba.freighthelper.utils.CompatibilityUtil.FlymeSetStatusBarLightMode;
import static com.yunbaba.freighthelper.utils.CompatibilityUtil.MIUISetStatusBarLightMode;

public abstract class BaseButterKnifeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayoutId());
		//AppManager.getInstance().addActivity(this);
		SystemBarHintUtil.setSystemBarHint(this);


		MIUISetStatusBarLightMode(this.getWindow(), true);
		FlymeSetStatusBarLightMode(this.getWindow(), true);

		ButterKnife.bind(this);
	}

	public abstract int getLayoutId();

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//ButterKnife.unbind(this);
	}

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

		// if(! (AccountAPI.getInstance().isLogined())) {
		//
		//
		//
		// }
		//

	}


	

	
	public static final int ReStoreLoginResult = 6399;
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
		
		//MLog.e("check", "onRestoreInstanceState");
		
		if (isNeedToRestoreLoginWhenAppWasRecycle()) {

			if (!(AccountAPI.getInstance().isLogined())) {
				
			//	MLog.e("check", "start RestoreLoginActivity");
				
				startActivityForResult(new Intent(this,RestoreLoginActivity.class),ReStoreLoginResult);
				
				

			}

		}
	}

	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {

		super.onActivityResult(arg0, arg1, arg2);
		
		if(arg0 == ReStoreLoginResult){
			
			 ReRestoreDataAndUI() ;
		}
		
	}
	
	public  boolean isNeedToRestoreLoginWhenAppWasRecycle(){
		
		return true;
		
	}
	
	 public void ReRestoreDataAndUI() {
		 
		 MLog.w("ReRestoreDataAndUI", "ReRestoreDataAndUI");
		 
		// this.recreate();
	     
	 }

}