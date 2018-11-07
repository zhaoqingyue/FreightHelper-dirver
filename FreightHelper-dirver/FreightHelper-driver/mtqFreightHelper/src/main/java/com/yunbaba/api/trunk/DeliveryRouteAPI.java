package com.yunbaba.api.trunk;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.navisdk.CldNaviAuthManager;
import com.cld.navisdk.CldNaviAuthManager.CldAuthManagerListener;
import com.cld.navisdk.CldNaviManager.RoutePlanPreference;
import com.cld.navisdk.routeplan.CldRoutePlaner.RoutePlanListener;
import com.cld.navisdk.routeplan.RoutePlanNode;
import com.cld.navisdk.routeplan.RoutePlanNode.CoordinateType;
import com.cld.navisdk.util.view.CldProgress;
import com.cld.navisdk.util.view.CldProgress.CldProgressListener;
import com.cld.navisdk.utils.CldNaviSdkUtils;
import com.cld.navisdk.utils.CldRoutePreUtil;
import com.cld.navisdk.utils.CldRouteUtil;
import com.cld.nv.datastruct.CldBaseGlobalvas;
import com.cld.nv.env.CldNvBaseEnv;
import com.cld.nv.hy.loader.CldHyBaseLoader;
import com.cld.nv.location.CldLocator;
import com.cld.nv.map.CldMapApi;
import com.cld.nv.map.CldMapController;
import com.cld.nv.route.CldRoute;
import com.cld.nv.route.assist.RoutePlanMode;
import com.cld.nv.route.assist.RoutePlanNetMode;
import com.cld.nv.route.assist.RoutePlanOption;
import com.cld.nv.route.entity.EnterpriseRoutePlanParam;
import com.cld.nv.route.entity.RoutePlanParam;
import com.cld.nv.route.listener.IRoutePlanListener;
import com.cld.nv.route.utils.CldRouteUtis;
import com.cld.ols.env.decoup.CldKDecoupAPI;
import com.cld.ols.env.decoup.CldKDecoupAPI.CldLoginStatus;
import com.cld.ols.env.decoup.CldKDecoupAPI.ICldDecoupKAccountListener;
import com.cld.ols.env.decoup.CldKDecoupAPI.ICldKGetUIsListener;
import com.cld.ols.module.authcheck.CldKAuthcheckAPI;
import com.cld.ols.module.authcheck.bean.CldAkeyType;
import com.cld.ols.tools.base.bean.CldKReturn;
import com.cld.ols.tools.model.ICldResultListener;
import com.yunbaba.api.map.NavigateAPI;
import com.yunbaba.freighthelper.bean.car.TruckCarParams;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.module.delivery.CldDalKDelivery;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliCorpRoutePlanResult;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hmi.mapctrls.HPMapAPI.HPMapCarIconInfo;
import hmi.packages.HPDefine.HPString;
import hmi.packages.HPDefine.HPWPoint;
import hmi.packages.HPKintrAPI;
import hmi.packages.HPOSALDefine.HPTruckSetting;
import hmi.packages.HPRoutePlanAPI;
import hmi.packages.HPRoutePlanAPI.HPOnLineRouteParams;
import hmi.packages.HPRoutePlanAPI.HPRPErrorInfo;
import hmi.packages.HPRoutePlanAPI.HPRPLicenseNumberType;
import hmi.packages.HPRoutePlanAPI.HPRPOption;
import hmi.packages.HPRoutePlanAPI.HPRPPosition;
import hmi.packages.HPRoutePlanAPI.HPRPVehicleType;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: DeliveryRouteAPI.java
 * @Prject: Freighthelper
 * @Package: com.mtq.api.trunk
 * @Description: 运货单算路
 * @author: zsx
 * @date: 2017-4-24 上午10:23:33
 * @version: V1.0
 */
public class DeliveryRouteAPI {
	final static String TAG = DeliveryRouteAPI.class.getSimpleName();
	static Thread mThread = null;
	static boolean mIsAuthing = false;  //正在sdk鉴权
	public static boolean isNeedSetSession = true;

	public static void routeplan(final MtqDeliStoreDetail storeDetail,final Context ctx ,final RoutePlanListener callback){
		// 显示等待进度条
		
		if (!FreightConstant.isShowMap){
			return ;
		}
		
		if (!CldProgress.isShowProgress())
		CldProgress.showProgress(ctx, "正在规划路线...",
				new CldProgressListener() {

					@Override
					public void onCancel() {
						
					}
				});
		
		routeEnginplan(storeDetail,ctx,callback);
		
//		planEnginRoute(1,storeDetail);
		
//		Runnable runnable = new Runnable() {
//			
//			@Override
//			public void run() {
//
//				int ret = planEnginRoute(1,storeDetail);
//				if (ret == 0){
//					if (callback != null){
//						((Activity)ctx).runOnUiThread(new Runnable() {
//							
//							@Override
//							public void run() {
//
//								if (CldProgress.isShowProgress()){
//									CldProgress.cancelProgress();
//								}
//							}
//						});
//						callback.onRoutePlanSuccessed();
//					}
//				}else{
//					planNormalRoute(storeDetail,ctx,callback);
//				}
//			}
//		};
//		
//		mThread = new Thread(runnable);
//		mThread.start();
	}
	
	/**
	 * @Title: planNormalRoute
	 * @Description: 运货点的普通的算路
	 * @param storeDetail
	 * @param ctx
	 * @return: void
	 */
	private static void planNormalRoute(final MtqDeliStoreDetail storeDetail,Context ctx,final RoutePlanListener callback){
		HPWPoint start = CldMapApi.getNMapCenter();
//		start.x = 420708800;
//		start.y = 144229000;
		RoutePlanNode startNode = new RoutePlanNode(start.y,start.x, "我的位置", "", CoordinateType.CLD);
//		RoutePlanNode destinationNode = new RoutePlanNode(144280600,420676400,
//				storeDetail.storename, storeDetail.storeaddr, CoordinateType.CLD);
		RoutePlanNode destinationNode = new RoutePlanNode(storeDetail.storey,storeDetail.storex,
				storeDetail.storename, storeDetail.storeaddr, CoordinateType.CLD);
		
		planNormalRoute(ctx,startNode,null,destinationNode,callback);
	}
	
	
	/**
	 * @Title: planNormalRoute
	 * @Description: TODO
	 * @param ctx
	 * @param startNode :出发地
	 * @param passListNode:经由地
	 * @param destinationNode：目的地
	 * @param callback：算路回调
	 * @return: void
	 */
	public static void planNormalRoute(final Context ctx,RoutePlanNode startNode,List<RoutePlanNode> passListNode,
			RoutePlanNode  destinationNode,final RoutePlanListener callback){
				
		NavigateAPI.getInstance().hyRoutePlan(ctx, startNode, passListNode, destinationNode,
				RoutePlanPreference.ROUTE_PLAN_MOD_RECOMMEND|RoutePlanPreference.ROUTE_PLAN_MOD_AVOID_TAFFICJAM, new RoutePlanListener() {
			
			@Override
			public void onRoutePlanSuccessed() {

				if (ctx != null){
					((Activity)ctx).runOnUiThread(new Runnable() {
						
						@Override
						public void run() {

							if (CldProgress.isShowProgress()){
								CldProgress.cancelProgress();
							}
						}
					});
				}
				callback.onRoutePlanSuccessed();
			}
			
			@Override
			public void onRoutePlanFaied(int arg0, String arg1) {

				callback.onRoutePlanFaied(arg0, arg1);
				if (ctx != null){
					((Activity)ctx).runOnUiThread(new Runnable() {
						
						@Override
						public void run() {

							if (CldProgress.isShowProgress()){
								CldProgress.cancelProgress();
							}
						}
					});
				}
			}
			
			@Override
			public void onRoutePlanCanceled() {

				callback.onRoutePlanCanceled();
			}
		});
	}
	
	
	/** 这个方法对SDK导航不能用。能算路但是导航不了
	 * @Title: planEnginRoute
	 * @Description: 计算企业路线
	 * @param isRoute:是否应用企业路线。
	 * @param storeDetail:运货点详情
	 * @return
	 * @return: int
	 */
	private static int planEnginRoute(int isRoute,MtqDeliStoreDetail storeDetail) {
		if(storeDetail == null){
			return -1;
		}
		HPOnLineRouteParams onLineRouteParams = new HPOnLineRouteParams();
		HPString strURL = new HPString();
		HPRPPosition start = new HPRPPosition();
		HPRPPosition dest = new HPRPPosition();
		HPMapCarIconInfo carInfo = new HPMapCarIconInfo();
		HPWPoint endPoint = new HPWPoint();
		HPRoutePlanAPI rpApi = CldNvBaseEnv.getHpSysEnv().getRoutePlanAPI();
		endPoint.x = storeDetail.storex;
		endPoint.y = storeDetail.storey;
		
		start.setPoint(CldMapApi.getNMapCenter());
		
		start.uiName = "我的位置";
		dest.setPoint(endPoint);
		dest.uiName = storeDetail.storename;
		onLineRouteParams.setStart(start);
		onLineRouteParams.setDest(dest);
		
		if (CldMapApi.isMapViewCreated
						|| CldBaseGlobalvas.getInstance().bInitGl){
			// 只有地图初始化了之后才能调用该方法
			CldNvBaseEnv.getHpSysEnv().getMapView().getCarIconInfo(true, false, carInfo);
//			carInfo.iCarDir = -1;
		}else{
			carInfo.iCarDir = -1;
		}
		
		HPTruckSetting truckSetting = new HPTruckSetting();
		truckSetting.uiLicenseNumberType = (short) HPRPLicenseNumberType.erpvtYellowLicense;
		truckSetting.ulVehicleType =  HPRPVehicleType.erpvtLightTruck; //轻型货车
		
		onLineRouteParams.setTruckSetting(truckSetting);
		
		if(CldLocator.getGpsInfo() == null)
			onLineRouteParams.direction = ((short) -1);
		else
			onLineRouteParams.direction =  carInfo.iCarDir;
//		
		onLineRouteParams.isMulti = false;
		onLineRouteParams.eRpCondition = ((byte) 1);
		
		onLineRouteParams.notTruckWeight = true;		
		onLineRouteParams.useTMC = false;
		onLineRouteParams.useConstruction = false;
		onLineRouteParams.downloadTMC = false;

		int planRet = 0;
		
		planRet = rpApi.getRouteURL(onLineRouteParams, strURL);
//		String url = strURL.getData();
		String url = strURL
				.getData()
				.replaceAll("userid=0",
						"userid=" + CldKAccountAPI.getInstance().getKuidLogin())
				.replaceAll("duid=0",
						"duid=" + CldKAccountAPI.getInstance().getDuid());
//		MLog.i(TAG, url);
		if (url != null) {
			CldDeliCorpRoutePlanResult result = null;
				result = CldKDeliveryAPI
						.getInstance().requestCorpRoutePlan(isRoute,
								storeDetail.corpId, storeDetail.taskId, 0, 0,
								1, url);
			if(result == null)
				return -1;
			
			planRet = result.errCode;
			MLog.i(TAG, "errcode" + result.errCode + "," + "isRoute"
					+ result.isRoute);
			if (planRet != 0) {
				planRet = result.errCode;
			} else {
				if (result.rpData != null && result.rpData.length > 0) {

					// 将ums写入文件分析
					File f = new File(Environment.getExternalStorageDirectory()+"/MTQFreightHelperFile"
							+ "/enterPrise.ums");
					if (f.exists()) {
						try {
							FileOutputStream fout = new FileOutputStream(f);
							fout.write(result.rpData, 0, result.rpData.length);
							fout.close();
							fout = null;
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						try {
							f.createNewFile();
						} catch (IOException e) {

							e.printStackTrace();
						}
					}

					planRet = rpApi.importRoutePackage(onLineRouteParams,
							result.rpData, result.rpData.length);
					MLog.i(TAG, "import data = " + planRet);
					if (planRet == 0){
					      CldRouteUtil.setRoutePlanType(1);
			              CldRoutePreUtil.setPlanPreference(1);
			              if (CldMapApi.isWholeRoute())
			                  CldMapApi.cancelWholeRoute();
			                CldMapApi.showWholeRoute();
			                CldMapController.getInstatnce().updateMap(true);
					}
				}
			}

		} else {
			planRet = -1;
		}

		return planRet;
	}
	
	
	/**
	 * @Title: routeEnginplan
	 * @Description: 计算企业线路
	 * @param storeDetail
	 * @param ctx
	 * @param callback
	 * @return: void
	 */
	private static void routeEnginplan(final MtqDeliStoreDetail storeDetail,final Context ctx ,final RoutePlanListener callback){
		if ((!CldNaviAuthManager.getInstance().isAuthStatus() ||
				CldKAuthcheckAPI.getInstance().getAkeyType() != CldAkeyType.HY)){
			CldNaviAuthManager.getInstance().authenticate(new CldAuthManagerListener() {
				
				@Override
				public void onAuthResult(int i, String s) {

//					mIsAuthing = false;
				}
			}, CldNaviSdkUtils.getAuthValue(ctx.getApplicationContext()));
		}
		
		
		CldHyBaseLoader hyLoader = new CldHyBaseLoader();
		hyLoader.init();
		
		HPRPPosition start = new HPRPPosition();
		HPRPPosition dest = new HPRPPosition();
		
		HPWPoint endPoint = new HPWPoint();
		endPoint.x = storeDetail.storex;
		endPoint.y = storeDetail.storey;
		start.setPoint(CldMapApi.getNMapCenter());
		start.uiName = "我的位置";
		dest.setPoint(endPoint);
		dest.uiName = storeDetail.storename;
		//RoutePlanMode.MOD_RECOMMEND
		//RoutePlanMode.MOD_LESS_HIGHWAY
		calRoute(ctx,start,dest,null,null,RoutePlanMode.MOD_RECOMMEND,RoutePlanNetMode.NET_MODE_ONLINEPRIORITY,
				false,storeDetail.corpId,storeDetail.taskId,callback);
		
	}
	
	
	/**
	 * 企业路线规划
	 * 
	 * @param mStarted
	 *            起点
	 * @param mDestination
	 *            终点
	 * @param mPassList
	 *            途经点
	 * @param mAvoidList
	 *            躲避点
	 * @param mPlanMode
	 *            计算方式
	 * @param mPlanNetMode
	 *            网络方式
	 * @param isRecoverLastRoute
	 *            是否保存上次计算的路径

	 * @param corpid
	 *            企业id
	 * @param taskid
	 *            运货单id
	 * @return void
	 * @author taojian
	 * @date 2016-5-13 上午9:48:23
	 */
	public static void calRoute(Context ctx,final HPRPPosition mStarted,
			final HPRPPosition mDestination,
			final ArrayList<HPRPPosition> mPassList,
			final ArrayList<HPRPPosition> mAvoidList, final int mPlanMode,
			final int mPlanNetMode, final boolean isRecoverLastRoute,
			final String corpid, final String taskid ,final RoutePlanListener callback) {
		
		if (mStarted == null) {
			return;
		}

		if (mDestination == null) {
			return;
		}
		
		setOlsParam();
		
		if (mPassList != null) {// 如果途经点是"我的位置"，手动修改为"地图上的点"
			for (int i = 0; i < mPassList.size(); i++) {
				HPRPPosition mPass = mPassList.get(i);
				if (mPass != null && "我的位置".equals(mPass.uiName)) {
					mPassList.get(i).uiName = "地图上的点";
				}
			}
		} 

		final RoutePlanParam routePlanParam = new RoutePlanParam();
		routePlanParam.passLst = mPassList;
		routePlanParam.destination = mDestination;
		routePlanParam.planMode = mPlanMode;

		routePlanParam.planNetMode = mPlanNetMode;
		routePlanParam.isRecoverLastRoute = isRecoverLastRoute;
		routePlanParam.start = mStarted;
		routePlanParam.avoidLst = mAvoidList;
		routePlanParam.truckWeightFlag = false;  // 默认质量不参与计算。
		routePlanParam.truckWeightPalyFlag = true; // 要播报
		routePlanParam.truckSetting = new HPTruckSetting();
		//设置默认轻型的货车参数
		routePlanParam.truckSetting.uiLicenseNumberType = (short) HPRPLicenseNumberType.erpvtYellowLicense;
		routePlanParam.truckSetting.ulVehicleType =  HPRPVehicleType.erpvtLightTruck; //轻型货车
		routePlanParam.truckSetting.iHeight = 24;
		routePlanParam.truckSetting.iWidth = 19;
		routePlanParam.truckSetting.iWeight = 18;
		routePlanParam.truckSetting.iAxleLoad = 9;
		//平台上没有轴重和轴数下来 默认为2轴
		float axleNum = 2.0f;
		if (TruckCarParams.mMtqCarInfo != null){
			if (!TextUtils.isEmpty(TruckCarParams.mMtqCarInfo.tht)){
				routePlanParam.truckSetting.iHeight = Short.valueOf(TruckCarParams.mMtqCarInfo.tht);
			}
			if (!TextUtils.isEmpty(TruckCarParams.mMtqCarInfo.twt)){
				routePlanParam.truckSetting.iWeight = Short.valueOf(TruckCarParams.mMtqCarInfo.twt);
				float Axleloadweihgt = 0; //轴重的具体数值    轴重 = 重量/轴数  
			
				if (routePlanParam.truckSetting.iWeight <= 100){
					//重量等级小于100档       100档以下是 0.1吨一档     
					Axleloadweihgt = (routePlanParam.truckSetting.iWeight/10.0f)/axleNum;
				}else{
					//重量等级大于100档       100档以上是 1吨一档     
					Axleloadweihgt = (routePlanParam.truckSetting.iWeight - 90) /axleNum;
				}
				//将实际的重量换成等级。
				if (Axleloadweihgt >= 0 && Axleloadweihgt < 10) {
					routePlanParam.truckSetting.iAxleLoad = (short) Math.ceil(Axleloadweihgt * 10);
				} else if (Axleloadweihgt >= 10) {
					routePlanParam.truckSetting.iAxleLoad = (short) Math.ceil(Axleloadweihgt + 90);
				}
				
			}
			if (!TextUtils.isEmpty(TruckCarParams.mMtqCarInfo.twh)){
				routePlanParam.truckSetting.iWidth = Short.valueOf(TruckCarParams.mMtqCarInfo.twh);
			}
			if (!TextUtils.isEmpty(TruckCarParams.mMtqCarInfo.tlnt)){
				short type = Short.valueOf(TruckCarParams.mMtqCarInfo.tlnt);
				if (type != 0){
					routePlanParam.truckSetting.uiLicenseNumberType = type;
				}
			}
			if (!TextUtils.isEmpty(TruckCarParams.mMtqCarInfo.tvt)){
				int type = Integer.valueOf(TruckCarParams.mMtqCarInfo.tvt);
				if (type != 0){
					routePlanParam.truckSetting.ulVehicleType = type;
				}
			}
		}
		
		// 添加错误信息描述
		routePlanParam.errInfo = new HPRPErrorInfo();
		if(corpid != null && taskid != null){
			EnterpriseRoutePlanParam param = new EnterpriseRoutePlanParam();
			param.corpid = corpid;
			param.taskid = taskid;
			routePlanParam.enterpriseParam = param;
			//使用企业路线导航
			CldRoutePreUtil.setEnterpriseRoute(true);
		}else{
			CldRoutePreUtil.setEnterpriseRoute(false);
		}

		routePlanParam.planOption = RoutePlanOption.OP_AVOID_TMC;

		/**
		 * 无网络又无法进行离线计算的话；
		 */
		if (!CldRouteUtis.isCanOffLineCalc(routePlanParam)
				&& !CldPhoneNet.isNetConnected()) {
			Toast.makeText(ctx, "网络通信出现问题，无法规划路径，请稍后再试。", Toast.LENGTH_SHORT).show();
			
			CldProgress.cancelProgress();
			return;
		}else if (CldRouteUtis.isCanOffLineCalc(routePlanParam)
				&& !CldPhoneNet.isNetConnected()) {// 若没网 ，又是离线 则算一条路
			routePlanParam.planOption = RoutePlanOption.OP_SINGLE
					| HPRPOption.eRpOption_CheckNodeData
					| HPRPOption.eRpOption_OutputMissingCitys;
			routePlanParam.planNetMode = RoutePlanNetMode.NET_MODE_OFFLINE;
		}

//		// 补刀，开发包对无网络有要求 20160115
//		if (CldPhoneNet.isNetConnected()) { 
//			HPKintrAPI.setInvalidNetWork(0); // 网络可用
//		} else {
//			HPKintrAPI.setInvalidNetWork(1); // 网络不可用
//		}

		CldRoute.planRoute(routePlanParam, new IRoutePlanListener() {
			@Override
			public void onRoutePlanSucess() {
				   CldRouteUtil.setRoutePlanType(1);
	               CldRoutePreUtil.setPlanPreference(mPlanMode);
	               CldProgress.cancelProgress();
	               if(CldMapApi.isWholeRoute())
	                    CldMapApi.cancelWholeRoute();
	                CldMapApi.showWholeRoute();
	                if(callback != null)
	                	callback.onRoutePlanSuccessed();
	                CldMapController.getInstatnce().updateMap(true);
			}

			@Override
			public void onRoutePlanStart() {
				
				if(CldRoute.isPlannedRoute())  {
                    CldMapApi.cancelWholeRoute();
                    CldRoute.clearRoute();
                }
			}

			@Override
			public void onRoutePlanFail(int arg0) {
				
				CldProgress.cancelProgress();
                if(callback != null)                {
                    String failInfo = CldRouteUtil.formateError(arg0);
                    callback.onRoutePlanFaied(arg0, failInfo);
                }
			}

			@Override
			public void onRoutePlanCancle() {
				CldProgress.cancelProgress();
				if(callback != null)
					callback.onRoutePlanCanceled();
			}
		});
	}
	
	
	/**
	 * @Title: setOlsParam
	 * @Description: 设置ols参数
	 * @return: void
	 */
	public static void setOlsParam(){


		String ses = CldKDecoupAPI.getInstance().getSession();

		//if (TextUtils.isEmpty(CldKDecoupAPI.getInstance().getSession())||isNeedSetSession){
			HPKintrAPI.setUserInfo(CldKAccountAPI.getInstance().getKuidLogin(), CldKAccountAPI.getInstance().getDuid());

		com.cld.ols.module.delivery.CldDalKDelivery.getInstance().setCldDeliveryKey(CldDalKDelivery.getInstance().getCldDeliveryKey());
		com.cld.ols.module.delivery.CldDalKDelivery.getInstance().setLoginAuth(true);

			CldKDecoupAPI.getInstance().initKA(new ICldDecoupKAccountListener() {
				
				@Override
				public void updateTeamInfo(byte[] abyte0, String s, String s1,
						ICldResultListener icldresultlistener) {

					
				}
				
				@Override
				public void sessionRelogin() {

					
				}
				
				@Override
				public void sessionInvalid(int i, int j) {

					
				}
				
				@Override
				public String parseSession(CldKReturn cldkreturn) {

					return null;
				}
				
				@Override
				public boolean isLogined() {

					return true;
				}
				
				@Override
				public String getUsername() {

					return null;
				}
				
				@Override
				public void getUserInfoByKuids(long[] al,
						ICldKGetUIsListener icldkgetuislistener) {

					
				}
				
				@Override
				public String getSession() {

					return CldKAccountAPI.getInstance().getSession();
				}
				
				@Override
				public String getLoginname() {

					return CldKAccountAPI.getInstance().getLoginName();
				}
				
				@Override
				public CldLoginStatus getLoginStatus() {

					return CldLoginStatus.LOGIN_DONE;
				}
				
				@Override
				public long getKuid() {

					return CldKAccountAPI.getInstance().getKuidLogin();
				}
				
				@Override
				public String getBindMobile() {

					return null;
				}
			});
			
		//	isNeedSetSession  = false;
		//}
	}
	
}
