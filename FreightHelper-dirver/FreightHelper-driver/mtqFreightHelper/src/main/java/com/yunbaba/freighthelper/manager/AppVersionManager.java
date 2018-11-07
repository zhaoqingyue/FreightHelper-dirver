package com.yunbaba.freighthelper.manager;

import com.yunbaba.api.appcenter.AppCenterAPI;
import com.yunbaba.freighthelper.utils.Config;
import com.yunbaba.ols.api.CldKAppCenterAPI.IUpgradeListener;
import com.yunbaba.ols.sap.bean.CldSapKAppParm.MtqAppInfoNew;

public class AppVersionManager {

	private static AppVersionManager mManager = null;

	private int customCode = 0;
	private int planCode = 0;

	public static AppVersionManager getInstance() {
		if (mManager == null) {
			synchronized (AppVersionManager.class) {
				if (mManager == null) {
					mManager = new AppVersionManager();
				}
			}
		}
		return mManager;
	}

	public AppVersionManager() {
		customCode = Config.custom_code;
		planCode = Config.plan_code;
	}

	public void init() {
		
	}

	public void checkVersion(final IAppVersionListener listener) {
		AppCenterAPI.getInstance().getUpgrade(0, customCode, planCode,
				new IUpgradeListener() {

//					@Override
//					public void onResult(int errCode, MtqAppInfoNew result) {
//						
//					}

					@Override
					public void onResult(int errCode, MtqAppInfoNew result) {

						if (listener != null) {
							listener.onResult(errCode, result);
						}
					}
				});
	}
	
	public static interface IAppVersionListener {

		/**
		 * 结果回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onResult(int errCode, MtqAppInfoNew result);
	}
}
