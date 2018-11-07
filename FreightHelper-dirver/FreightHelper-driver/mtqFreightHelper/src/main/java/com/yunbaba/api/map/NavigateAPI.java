package com.yunbaba.api.map;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.cld.mapapi.map.CldMap.NaviInitListener;
import com.cld.mapapi.map.MapView;
import com.cld.navisdk.CldNaviAuthManager;
import com.cld.navisdk.CldNaviAuthManager.CldAuthManagerListener;
import com.cld.navisdk.CldNaviManager;
import com.cld.navisdk.guide.CldNavigatorView;
import com.cld.navisdk.guide.CldSimulationView;
import com.cld.navisdk.hy.routeplan.CldHYRoutePlaner;
import com.cld.navisdk.hy.routeplan.HYRoutePlanParm;
import com.cld.navisdk.hy.routeplan.HYRoutePlanParm.HYTruckType;
import com.cld.navisdk.hy.routeplan.HYRoutePlanParm.HYWeightFlag;
import com.cld.navisdk.routeguide.CldNavigator;
import com.cld.navisdk.routeplan.CldRoutePlaner;
import com.cld.navisdk.routeplan.CldRoutePlaner.RoutePlanListener;
import com.cld.navisdk.routeplan.RoutePlanNode;
import com.cld.navisdk.util.view.CldProgress;
import com.cld.navisdk.util.view.CldProgress.CldProgressListener;
import com.cld.navisdk.utils.CldNaviSdkUtils;
import com.cld.nv.env.CldNvBaseEnv;
import com.cld.nv.hy.company.CldWayBillRoute;
import com.cld.nv.route.CldRoute;
import com.cld.nv.util.CldNaviUtil;
import com.cld.ols.module.authcheck.CldKAuthcheckAPI;
import com.cld.ols.module.authcheck.bean.CldAkeyType;
import com.yunbaba.freighthelper.bean.car.TruckCarParams;
import com.yunbaba.freighthelper.constant.FreightConstant;

import java.util.List;

import hmi.packages.HPRoutePlanAPI.HPRPVehicleType;
import hmi.packages.HPVoiceAPI;
import hmi.packages.HPVoiceAPI.HPVoiceId;


/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: NavigateAPI.java
 * @Prject: Freighthelper
 * @Package: com.mqt.api.map
 * @Description: 导航相关API
 * @author: zsx
 * @date: 2017-3-24 下午12:26:27
 * @version: V1.0
 */
public class NavigateAPI {
	private String TAG = "NavigateAPI";
	private static NavigateAPI mNavigateAPI;
	static boolean mIsAuthing = false;  //正在sdk鉴权

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean init) {
		isInit = init;
	}

	private boolean  isInit = false;
	public static NavigateAPI getInstance(){
		synchronized (NavigateAPI.class) {
			if (mNavigateAPI == null){
				synchronized (NavigateAPI.class) {
					mNavigateAPI = new NavigateAPI();
				}
			}
		}
		return mNavigateAPI;
	}
	
//	public void setAppProperty(AppProperty appProperty){
//		CldNvBaseManager.setAppProperty(appProperty);
//	}
	
	
	// 初始化 sdk
	public void init(Context ctx,NaviInitListener listener){
		isInit = true;
		CldNaviManager.getInstance().init(ctx, listener);
	}
	
	//
	public MapView createMapView(Context ctx){
		return CldNaviManager.getInstance().createNMapView(ctx);
	}
	
	 public CldNavigatorView init(Activity activity, MapView mapView){
		return CldNavigator.getInstance().init(activity,mapView);
	 }
	 
	public CldSimulationView initSimulation(Activity activity,MapView mapView){
		return CldNavigator.getInstance().initSimulation(activity, mapView);
	}
	 
	//开始导航
	public void startNavi(){
		CldNavigator.getInstance().startNavi();
	}
	
	//调用导航模式相应回退方法
	public void onBackPressed() {
		 CldNavigator.getInstance().onBackPressed();
	}
	
	public void onResume(){
		CldNavigator.getInstance().onResume();
	}
	
	//计算路径	
	/**
	 * @Title: routePlan
	 * @Description: 普通路线，这个接口，运东东不调用
	 * @param ctx  
	 * @param startNode ：起点
	 * @param passListNode ：经由地列表
	 * @param destinationNode ： 终点
	 * @param planPreference：算路方式
	 * @param routePlanListener：回调
	 * @return: void
	 */
	public void routePlan(Context ctx, RoutePlanNode startNode, List<RoutePlanNode> passListNode, RoutePlanNode destinationNode, 
			int planPreference, RoutePlanListener routePlanListener){
		
		if (!FreightConstant.isShowMap){
			return ;
		}
		
		// 显示等待进度条
		CldProgress.showProgress(ctx, "正在规划路线...",
				new CldProgressListener() {

					@Override
					public void onCancel() {

					}
				});
		
		CldRoutePlaner.getInstance().routePlan(ctx, startNode,
				passListNode, 
				destinationNode, 
				planPreference, 
				routePlanListener);
	}
	
	/**
	 * @Title: hyRoutePlan
	 * @Description: 计算货车路线，如果没有货车参数的话就调这个接口
	 * @param ctx
	 * @param startNode：起点
	 * @param passListNode：经由地列表
	 * @param destinationNode ： 终点
	 * @param planPreference：算路方式
	 * @param routePlanListener：回调
	 * @return: void
	 */
	public void hyRoutePlan(Context ctx, RoutePlanNode startNode,
			List<RoutePlanNode> passListNode, RoutePlanNode destinationNode, 
			int planPreference, RoutePlanListener routePlanListener){
		HYRoutePlanParm hyRpParm = new HYRoutePlanParm();
	
		hyRpParm.truckType = HYTruckType.LightTruck;
		hyRpParm.weightFlag = HYWeightFlag.NO_WEIGHT_TO_PLAN_PLAY;
		if (TruckCarParams.mMtqCarInfo != null){
			if (!TextUtils.isEmpty(TruckCarParams.mMtqCarInfo.tht)){
				hyRpParm.height = Float.valueOf(TruckCarParams.mMtqCarInfo.tht);
				hyRpParm.height = hyRpParm.height/10;
			}
			if (!TextUtils.isEmpty(TruckCarParams.mMtqCarInfo.twt)){
				float weight = Float.valueOf(TruckCarParams.mMtqCarInfo.twt);
				if (weight != 0){
					if (weight <=100){
						hyRpParm.weight = weight /10;
					}else{
						hyRpParm.weight = weight - 90;
					}
				}
			}
			if (!TextUtils.isEmpty(TruckCarParams.mMtqCarInfo.twh)){
				hyRpParm.width = Float.valueOf(TruckCarParams.mMtqCarInfo.twh);
				hyRpParm.width /= 10;
			}
			
			if (!TextUtils.isEmpty(TruckCarParams.mMtqCarInfo.tvt)){
				int type = Integer.valueOf(TruckCarParams.mMtqCarInfo.tvt);
				if (type == HPRPVehicleType.erpvtLightTruck){
					hyRpParm.truckType = HYTruckType.LightTruck;
				}else if (type == HPRPVehicleType.erpvtMiniTruck){
					hyRpParm.truckType = HYTruckType.MiniTruck;
				}else if (type == HPRPVehicleType.erpvtMediumTruck){
					hyRpParm.truckType = HYTruckType.MiddleTruck;
				}else if (type == HPRPVehicleType.erpvtHeavyTruck){
					hyRpParm.truckType = HYTruckType.HeavyTruck;
				}
			}
		}
		
//		hyRpParm.weightFlag = 1;
//		hyRpParm.weight = 40;
//		hyRpParm.height =1;
//		hyRpParm.width = 1;
//		hyRpParm.axleCount = 4;
		hyRpParm.axleCount = 2;
		hyRoutePlan(ctx, startNode, passListNode, 
				destinationNode,planPreference,hyRpParm, routePlanListener);
	}
	
	/**
	 * @Title: hyRoutePlan
	 * @Description: 计算货车路线，如果有货车参数的话就调这个接口
	 * @param ctx
	 * @param startNode：起点
	 * @param passListNode：经由地列表
	 * @param destinationNode ： 终点
	 * @param planPreference：算路方式
	 * @param hyRpParm：货车参数
	 * @param routePlanListener：回调
	 * @return: void
	 */
	public void hyRoutePlan(final Context ctx, RoutePlanNode startNode, 
			List<RoutePlanNode> passListNode, RoutePlanNode destinationNode, 
			int planPreference, HYRoutePlanParm hyRpParm,final RoutePlanListener routePlanListener){
		
		
		hyRpParm.axleCount = 2;
		
		if ((!CldNaviAuthManager.getInstance().isAuthStatus() ||
				CldKAuthcheckAPI.getInstance().getAkeyType() != CldAkeyType.HY) ){
			CldNaviAuthManager.getInstance().authenticate(new CldAuthManagerListener() {
				
				@Override
				public void onAuthResult(int i, String s) {

//					mIsAuthing = false;
				}
			}, CldNaviSdkUtils.getAuthValue(ctx.getApplicationContext()));
		}
		
		// 显示等待进度条
			if (!CldProgress.isShowProgress())
				CldProgress.showProgress(ctx, "正在规划路线...",
						new CldProgressListener() {

							@Override
							public void onCancel() {
								CldRoute.cancleRoutePlan();
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
						});
				
		CldWayBillRoute.isEnterpriseRouteActive = false;
		CldHYRoutePlaner.getInstance().hyRoutePlan(ctx,startNode,
				passListNode,destinationNode,planPreference,hyRpParm,new RoutePlanListener() {
					
					@Override
					public void onRoutePlanSuccessed() {

						routePlanListener.onRoutePlanSuccessed();
					}
					
					@Override
					public void onRoutePlanFaied(int arg0, String arg1) {

						
						if (!CldNaviUtil.isNetConnected() || arg0 == 59) {
							arg1 = "网络通信出现问题，无法规划路径，请稍后再试。";
						}
						
						routePlanListener.onRoutePlanFaied(arg0, arg1);
					}
					
					@Override
					public void onRoutePlanCanceled() {

						routePlanListener.onRoutePlanCanceled();
					}
				});
	}
	
	
	public int playVoiceText(String contetStr, int iLevel) {
		int ret = 0;
		int iNum = 1;
		HPVoiceId[] voiceids = new HPVoiceId[iNum];
		voiceids[0] = new HPVoiceId();
		voiceids[0].blUText = true;
		voiceids[0].setText(contetStr);

		HPVoiceAPI mVoiceAPI = CldNvBaseEnv.getHpSysEnv().getVoiceAPI();
		ret = mVoiceAPI.play(voiceids, iNum, iLevel);

		return ret;
	}

}
