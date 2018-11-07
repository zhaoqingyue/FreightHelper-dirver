/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: CarManager.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.manager
 * @Description: 车况管理器
 * @author: zhaoqy
 * @date: 2017年4月14日 上午11:27:26
 * @version: V1.0
 */

package com.yunbaba.freighthelper.manager;

import android.content.Context;

import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.car.CarAPI;
import com.yunbaba.api.trunk.listner.OnBooleanListner;
import com.yunbaba.freighthelper.bean.car.CarInfo;
import com.yunbaba.freighthelper.bean.car.Navi;
import com.yunbaba.freighthelper.db.TravelCarTable;
import com.yunbaba.freighthelper.utils.CarUtils;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.TimeUtils;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICarListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICarRouteListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ITaskDetailListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ITaskListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCar;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCarRoute;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTask;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTaskDetail;

import java.util.ArrayList;
import java.util.List;

public class CarManager {

	protected static final String TAG = "CarManager";
	private Context mContext;
	private static CarManager mCarManager = null;
	
	private static String mLoginName = "";
	
	private static boolean mSuccessGetData = false;
	
	private final int INTERVAL_MAXTIME = 1800;

	public static CarManager getInstance() {
		if (mCarManager == null) {
			synchronized (CarManager.class) {
				if (mCarManager == null) {
					mCarManager = new CarManager();
				}
			}
		}
		return mCarManager;
	}

	public CarManager() {

	}

	public static void setmLoginName(String mLoginName) {
		CarManager.mLoginName = mLoginName;
	}

	public void init(Context context) {
		mContext = context;
		

	}

	public static interface IGetCarListener {

		/**
		 * 获取司机驾驶车辆信息回调
		 * 
		 * @param errCode
		 * @param result
		 */
		public void onGetResult(int errCode, CarInfo result);
	}

	/**
	 * 获取司机驾驶车辆信息
	 */
	public void getCarInfo(final String taskId, final String corpId,
			final IGetCarListener listener) {
		CarAPI.getInstance().getCarInfo(taskId, corpId, new ICarListener() {

			@Override
			public void onGetResult(int errCode, MtqCar result) {
				MLog.e(TAG, "getCarInfo errCode: " + errCode + ", result: "
						+ result);
				if (errCode == 0 && result != null) {
					if (listener != null) {
						CarInfo carinfo = CarUtils.parseCarInfo(taskId, corpId,
								result);
						listener.onGetResult(errCode, carinfo);
						TravelCarTable.getInstance().insert(carinfo);
					}
				} else {
					//MLog.e(TAG, "getCarInfo is null.  errCode: " + errCode);
					if (listener != null) {
						listener.onGetResult(errCode, null);
					}
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				
			}
		});
	}

	public static interface IGetTaskListener {

		/**
		 * 行程日期列表回调
		 * 
		 * @param errCode
		 * @param listOfResult
		 */
		public void onGetResult(int errCode, List<MtqTask> listOfResult);
	}

	/**
	 * 获取行程日期列表
	 */
	public void getTasks(final IGetTaskListener listener, final OnBooleanListner lis) {
		String starttime = TimeUtils.getStarttime();
		String endtime = TimeUtils.getEndtime();
		CarAPI.getInstance().getTasks(starttime, endtime, new ITaskListener() {

			@Override
			public void onGetResult(int errCode, List<MtqTask> listOfResult) {
				
				MLog.e(TAG, "getTasks errCode: " + errCode);
				if (errCode == 0 && listOfResult != null
						&& !listOfResult.isEmpty()) {
					if (listener != null) {
						listener.onGetResult(errCode, listOfResult);
					}
					
					/**
					 * 处理日期列表
					 */
					mSuccessGetData = true;

				} else {
					//MLog.e(TAG, "getTasks is null.  errCode: " + errCode);
					if (listener != null) {
						listener.onGetResult(errCode, null);
					}


					if(lis!=null)
					lis.onResult(false);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				
			}
		});
	}

	public static interface IGetLatestRouteListener {

		/**
		 * 单天行程列表回调
		 * 
		 * @param errCode

		 */
		public void onGetResult(int errCode, List<MtqCarRoute> listOfResult);
	}

	/**
	 * 获取最新日期行程列表
	 */
	public void getLatestTask(MtqTask mtqTask,
			final IGetLatestRouteListener listener) {
		String date = TimeUtils.stampToFormat(mtqTask.finishtime);
		//MLog.e(TAG, "date: " + date);
		List<MtqTask> tasks = new ArrayList<MtqTask>();
		tasks.add(mtqTask);
		CarAPI.getInstance().getCarRoutes(date, tasks, new ICarRouteListener() {

			@Override
			public void onGetResult(int errCode, List<MtqCarRoute> listOfResult) {
				if (errCode == 0 && listOfResult != null
						&& !listOfResult.isEmpty()) {
					if (listener != null) {
						listener.onGetResult(errCode, listOfResult);
					}
				} else {
					if (listener != null) {
						listener.onGetResult(errCode, null);
					}
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				
			}
		});
	}

	public static interface IGetTaskDetailListener {

		/**
		 * 单天行程列表回调
		 * 
		 * @param errCode
		 * @param result
		 */
		public void onGetResult(int errCode, MtqTaskDetail result);
	}

	/**
	 * 获取最近一次行程
	 */
	public void getLatestRouteDetail(Navi route,
			final IGetTaskDetailListener listener) {
		final String carlicense = route.carlicense;
		final String carduid = route.carduid;
		final String serialid = route.serialid;

		/**
		 * 获取行程详情
		 */
		CarManager.getInstance().getTaskDetail(carduid, serialid,
				new ITaskDetailListener() {

					@Override
					public void onGetResult(int errCode, MtqTaskDetail result) {
						if (errCode == 0 && result != null) {

							if (listener != null) {
								listener.onGetResult(errCode, result);
							}
//							TravelDetail detail = CarUtils.formatTaskDetail(
//									result, carlicense, serialid);
							// TravelDetailTable.getInstance().insert(detail);
						} else {
							if (listener != null) {
								listener.onGetResult(errCode, null);
							}
						}
					}

					@Override
					public void onGetReqKey(String arg0) {

						
					}
				});
	}

	/**
	 * 获取单天行程列表
	 */
	public void getCarRoutes(String date, List<MtqTask> tasks,
			ICarRouteListener listener) {
		CarAPI.getInstance().getCarRoutes(date, tasks, listener);
	}

	/**
	 * 获取行程详情
	 */
	public void getTaskDetail(String carduid, String serialid,
			ITaskDetailListener listener) {
		CarAPI.getInstance().getTaskDetail(carduid, serialid, listener);
	}


	public void getHistoryCarRoutes(final IGetTaskListener listener,final OnBooleanListner lis){

		//



		String loginname = AccountAPI.getInstance().getLoginName();
		if (!mSuccessGetData || loginname == null || !mLoginName.equals(loginname)){
			//不用每次都获取，换用户名的时候才登录 还没有每间隔INTERVAL_MAXTIME 秒取一次
			//要保证成功获取一下后才能间隔时间再获取
			mLoginName = loginname;
			if (mLoginName == null){
				mLoginName = "";
			}
			mSuccessGetData = false;
			getTasks(listener,lis);
		}else{

			listener.onGetResult(0,null);

		}


	}

}
