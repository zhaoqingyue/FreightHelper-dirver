package com.yunbaba.freighthelper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cld.mapapi.map.MapView;
import com.cld.navisdk.hy.utils.CldTruckUtil;
import com.cld.navisdk.hy.utils.IOnClickLimitListener;
import com.cld.navisdk.routeplan.CldRoutePlaner;
import com.cld.nv.hy.company.CldWayBillRoute;
import com.cld.nv.hy.limit.CldLimitManager;
import com.cld.nv.hy.limit.LimitInfoBean;
import com.cld.nv.route.CldRoute;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.api.map.MapViewAPI;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.utils.CommonTool;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.ToastUtils;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: RoutePreviewActivity.java
 * @Prject: Freighthelper
 * @Package: com.mtq.freighthelper.ui.activity
 * @Description: 路径概览界面
 * @author: zsx
 * @date: 2017-5-11 下午3:48:19
 * @version: V1.0
 */
public class RoutePreviewActivity extends BaseButterKnifeActivity {

	@BindView(R.id.rl_routeprew_navigate)
	RelativeLayout mNavigate;

	@BindView(R.id.rl_routeprew_simunlation)
	RelativeLayout mSimunlation;

	@BindView(R.id.routepreview_start)
	TextView mTvStart;

	@BindView(R.id.routepreview_dest)
	TextView mTvDest;

	@BindView(R.id.routeprew_distance)
	TextView mTvDistance;

	@BindView(R.id.routeprew_limit_num)
	TextView mTvLimitNum;

	@BindView(R.id.routeprew_station_num)
	TextView mTvStationNum;

	@BindView(R.id.routeprew_time)
	TextView mTvTime;

	@BindView(R.id.tv_routeprew_bussinessroute)
	TextView mTvBussinessroute;

	@BindView(R.id.map_layout)
	FrameLayout mMapLayout;

	private String mStartName;
	private String mDestName;
	private String mTotalDistal;
	private String mToatalTime;
	private int mLimitNum;
	private int mStationNum;

	private MapView mMapView;
	boolean isFromTaskDetail = false;
	private boolean firstCome = true;

	String corpid;
	String taskid;

	MtqDeliStoreDetail mStoreDetail;
	MtqDeliOrderDetail mOrderDetail;

	String jsonStore;
	String jsonOrder;
	String jsonDeliTaskDetail;

	@Override
	public int getLayoutId() {

		return R.layout.activity_routepreview;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initData();
		initView();
		initControl();
		EventBus.getDefault().register(this);


		CldTruckUtil.drawLimitIcons();
		CldTruckUtil.setmClickLimitListener(new IOnClickLimitListener() {

			@Override
			public void onClick(int index, LimitInfoBean limitInfobean) {
				// TODO Auto-generated method stub
				if(limitInfobean!=null && limitInfobean.limitDesc[0] !=null) {

					ToastUtils.showMessageLong(RoutePreviewActivity.this,limitInfobean.limitDesc[0]);
				  //	Toast.makeText(RoutePreviewActivity.this, limitInfobean.limitDesc[0], Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	protected void onResume() {

		super.onResume();
		if (mMapView != null) {
			mMapView.onResume();// 当地图控件存在时，调用相应的恢复方法
			if (firstCome) {
				mMapView.getMap().enableRouteOverview(true);
				firstCome = false;
			} else {
				mMapView.update();// 同时更新地图控件状态
			}
		}
	}

	@Override
	public void onPause() {

		super.onPause();
		if (mMapView != null) {
			mMapView.onPause();
		}
	}

	@Override
	public void onStop() {

		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	private void initData() {

		jsonStore = getIntent().getStringExtra("storedetail");
		if (!TextUtils.isEmpty(jsonStore)) {
			mStoreDetail = GsonTool.getInstance().fromJson(jsonStore, MtqDeliStoreDetail.class);
		}

		if (getIntent().hasExtra("corpid"))
			corpid = getIntent().getStringExtra("corpid");

		if (getIntent().hasExtra("taskid"))
			taskid = getIntent().getStringExtra("taskid");

		if (getIntent().hasExtra("orderdetail")) {
			jsonOrder = getIntent().getStringExtra("orderdetail");
		}

		if (getIntent().hasExtra("taskdetail")) {
			jsonDeliTaskDetail = getIntent().getStringExtra("taskdetail");
		}

		if(getIntent().hasExtra("isFromTaskDetail")){

			isFromTaskDetail =  getIntent().getBooleanExtra("isFromTaskDetail",false);

		}

		if (FreightConstant.isShowMap) {
			mMapView = MapViewAPI.getInstance().createMapView(this);
		}
		mStartName = CldRoute.getStart().uiName;

		if (mStoreDetail != null) {
			mDestName = (TextUtils.isEmpty(mStoreDetail.storecode) ? "" : (mStoreDetail.storecode + "-"))
					+ CldRoute.getDestination().uiName;
		} else
			mDestName = CldRoute.getDestination().uiName;
		mStationNum = CldLimitManager.getInstance().getTruckCheckNum();
		mLimitNum = CldTruckUtil.getLimitNum();

		long disAndTime[] = CldRoutePlaner.getInstance().getRouteDistanceAndTime(0);

		mTotalDistal = changeLenth(disAndTime[0]);
		mToatalTime = changeTime(disAndTime[1]);
	}

	private void initView() {
		if (FreightConstant.isShowMap) {
			if (mMapView != null) {
				mMapLayout.addView(mMapView);
				mMapView.getMap().setTrafficEnabled(false);
			}
		}

		mTvDest.setText(mDestName);
		mTvStart.setText(mStartName);

		mTvLimitNum.setText(mLimitNum + "个");
		mTvStationNum.setText(mStationNum + "个");

		mTvDistance.setText(mTotalDistal);
		mTvTime.setText(mToatalTime);

		if (CldWayBillRoute.isEnterpriseRouteActive) {
			mTvBussinessroute.setVisibility(View.VISIBLE);
		} else {
			mTvBussinessroute.setVisibility(View.INVISIBLE);
		}


	}

	private void initControl() {
		mNavigate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

                AppStatApi.statOnEventWithLoginName(getApplicationContext(), AppStatApi.BD_STAT_NAVI_START_CLICK);

				if (!FreightConstant.isShowMap) {
					return;
				}

				Intent intent = new Intent(RoutePreviewActivity.this, NavigatorActivity.class);
				if (!TextUtils.isEmpty(jsonStore)) {
					intent.putExtra("storedetail", jsonStore);
				}
				if (!TextUtils.isEmpty(jsonOrder)) {
					intent.putExtra("orderdetail", jsonOrder);
				}

				if (!TextUtils.isEmpty(taskid)) {
					intent.putExtra("taskid", taskid);
				}

				if (!TextUtils.isEmpty(corpid)) {
					intent.putExtra("corpid", corpid);
				}

				if (!TextUtils.isEmpty(jsonDeliTaskDetail)) {
					intent.putExtra("taskdetail", jsonDeliTaskDetail);
				}
				intent.putExtra("isFromTaskDetail", isFromTaskDetail);
				startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);
				setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
				finish();
			}
		});

		mSimunlation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!FreightConstant.isShowMap) {
					return;
				}

				Intent intent = new Intent(RoutePreviewActivity.this, SimunlationActivity.class);

				// 测试需求
				if (!TextUtils.isEmpty(jsonStore)) {
					intent.putExtra("storedetail", jsonStore);
				}
				if (!TextUtils.isEmpty(jsonOrder)) {
					intent.putExtra("orderdetail", jsonOrder);
				}

				if (!TextUtils.isEmpty(taskid)) {
					intent.putExtra("taskid", taskid);
				}

				if (!TextUtils.isEmpty(corpid)) {
					intent.putExtra("corpid", corpid);
				}

				if (!TextUtils.isEmpty(jsonDeliTaskDetail)) {
					intent.putExtra("taskdetail", jsonDeliTaskDetail);
				}

				intent.putExtra("isFromTaskDetail", isFromTaskDetail);

				startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);
				setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
				finish();
			}
		});
	}

	@OnClick({ R.id.title_left_img })
	void onclick(View view) {

		if (CommonTool.isFastDoubleClick()) {

			return;
		}

		switch (view.getId()) {
		case R.id.title_left_img:
			finish();
			CldRoutePlaner.getInstance().clearRoute(); // 关闭界面时，清除规划路线
			break;
		}
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		CldRoutePlaner.getInstance().clearRoute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {

		super.onActivityResult(requestCode, resultCode, arg2);
		if (requestCode == FreightConstant.TASK_POINT_REQUSEST_CODE
				&& (resultCode == FreightConstant.TASK_POINT_INFO_NEED_UPDATE
						|| resultCode == FreightConstant.TASK_AND_POINT_NEED_UPDATE)) {

			mStoreDetail.storestatus = FreightConstant.TASK_PAUSE;
			jsonStore = GsonTool.getInstance().toJson(mStoreDetail);

			setResult(resultCode);
		}
	}

	/**
	 * 转换时间,把秒转换成分和小时
	 * 
	 * @return String
	 */
	private String changeTime(long second) {
		String time = "";
		if (second < 3600) {
			time = second / 60 + "分钟";
		} else {
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(0);
			time = second / 3600 + "小时" + (second % 3600) / 60 + "分钟";
		}
		return time;
	}

	/**
	 * 转换长度单位,把米转换成公里
	 * 
	 * @return String
	 */
	private String changeLenth(long length) {
		String distance = "";
		// 如果小于一公里,就用米为单位,如果大于一公里,就以公里为单位
		if (length < 1000) {
			distance = length + "米";
		} else {
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(1);
			distance = df.format(length / 1000.0) + "公里";
		}
		return distance;
	}

	@Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
	public synchronized void onTaskBusinessMsgEvent(TaskBusinessMsgEvent event) {

		switch (event.getType()) {

		// 任务刷新
		case 2:

			break;

		// 作废任务，删除某些任务
		case 3:

			if (mStoreDetail == null)
				return;

			if (isFinishing())
				return;

			if (event.getTaskIdList() != null && event.getTaskIdList().size() > 0) {

				if (TextStringUtil.isContainStr(event.getTaskIdList(), mStoreDetail.taskId)) {

					// Toast.makeText(this, "当前操作任务单已撤回",
					// Toast.LENGTH_LONG).show();
					finish();
				}

			}

			break;
		case 4:
			if (mStoreDetail == null)
				return;

			if (isFinishing())
				return;

			if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {

				if (mOrderDetail != null && !TextUtils.isEmpty(mOrderDetail.waybill)) {

					if (TextStringUtil.isContain(event.getRefreshtaskIdList(), mStoreDetail.taskId,
							mOrderDetail.waybill)) {
						finish();
					}

				}
			}

			break;

		default:
			break;
		}

	}
}
