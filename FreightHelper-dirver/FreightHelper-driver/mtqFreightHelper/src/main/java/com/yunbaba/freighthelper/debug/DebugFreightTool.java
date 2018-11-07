package com.yunbaba.freighthelper.debug;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.yunbaba.api.trunk.bean.GetTaskListResult;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliGetTaskListListener;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;

import java.util.List;

public class DebugFreightTool {

	private static final String TAG = "DebugFreightTool";
	private static String phone = "mwer30420124";
	private static String userName = "13713963683";
	private static String passWord = "123456";

	// private static final String userName = "15902067097";
	// private static final String passWord = "123456";
	public static final boolean TestFregightFlag = true;

	public static void GetList(final Context context) {
		CldBllKDelivery.getInstance().getDeliTaskList(null, new ICldDeliGetTaskListListener() {
			@Override
			public void onGetTaskLstResult(int errCode, List<MtqDeliTask> lstOfTask) {

				Toast.makeText(context, "返回结果" + errCode, Toast.LENGTH_SHORT).show();

				if (errCode != 0)
					return;
				for (int i = 0; i < lstOfTask.size(); i++) {
					MLog.e("yyh", "deliList id = " + lstOfTask.get(i).taskid);
					MLog.e("yyh", "deliList store count = " + lstOfTask.get(i).store_count);

					MLog.e("yyh", "deliList corpname = " + lstOfTask.get(i).corpname);

				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				
			}
		});

	}

	public static void AutoGetList(final Context context, final OnResponseResult<GetTaskListResult> response) {

		// CldKAccountAPI.getInstance().login(userName, passWord);

		CldBllKDelivery.getInstance().loginAuth(new ICldResultListener() {

			@Override
			public void onGetResult(int errCode) {

				Toast.makeText(context, "鉴权结果返回码:" + errCode, Toast.LENGTH_SHORT).show();
				
				
				if(errCode!=0){
					
					AutoGetList(context,response);
					
				}else{
					
				//	DeliveryApi.getInstance().getUnfinishTaskInServer(null, response);
				}

			}

			@Override
			public void onGetReqKey(String tag) {

				
			}
		});

	}

	public static void AutoLogin(final Context context) {

		// if(CldKAccountAPI.getInstance().getLogin)

		CldKAccountAPI.getInstance().login(userName, passWord);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				CldBllKDelivery.getInstance().loginAuth(new ICldResultListener() {

					@Override
					public void onGetResult(int errCode) {

						Toast.makeText(context, "鉴权结果返回码:" + errCode, Toast.LENGTH_SHORT).show();

						CldBllKDelivery.getInstance().getDeliTaskList(null, new ICldDeliGetTaskListListener() {
							@Override
							public void onGetTaskLstResult(int errCode, List<MtqDeliTask> lstOfTask) {

								Toast.makeText(context, "返回结果" + errCode, Toast.LENGTH_SHORT).show();

								if (errCode != 0)
									return;
								for (int i = 0; i < lstOfTask.size(); i++) {
									MLog.e("yyh", "deliList id = " + lstOfTask.get(i).taskid);
									MLog.e("yyh", "deliList store count = " + lstOfTask.get(i).store_count);

									MLog.e("yyh", "deliList corpname = " + lstOfTask.get(i).corpname);

								}
							}

							@Override
							public void onGetReqKey(String arg0) {

								
							}
						});

					}

					@Override
					public void onGetReqKey(String tag) {

						
					}
				});
			}
		}, 3000);

	}

}
