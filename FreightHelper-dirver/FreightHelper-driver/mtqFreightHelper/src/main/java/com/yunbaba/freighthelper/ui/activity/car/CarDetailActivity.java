/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: CarDetailActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.car
 * @Description: 车辆详情
 * @author: zhaoqy
 * @date: 2017年4月11日 下午4:12:28
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.car;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.car.Navi;
import com.yunbaba.freighthelper.bean.car.TravelDetail;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.manager.CarManager;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.ui.customview.PopupTravel;
import com.yunbaba.freighthelper.ui.customview.PopupTravel.OnPopupListener;
import com.yunbaba.freighthelper.utils.CarUtils;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.TimeUtils;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ITaskDetailListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqOrder;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTaskDetail;

import java.util.List;

public class CarDetailActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = "CarDetailActivity";
	private ImageView mBack;
	private TextView mTitle;
	private ImageView mNewsImg;
	private LinearLayout mLayout;
	private TextView mRoot;
	private TextView mTime;
	private TextView mPlate;
	private TextView mWaybill;
	private TextView mFuel;
	private TextView mHundredFuel;
	private TextView mIdleFuel;
	private TextView mIdleTime;
	private TextView mTotalTime;
	private TextView mMileage;
	private TextView mMaxSpeed;
	private TextView mAveSpeed;
	private Navi mNavi;
	private TravelDetail mDetail;
	private PopupTravel mPopup;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_car_detail;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mNewsImg = (ImageView) findViewById(R.id.title_right_img);
		mLayout = (LinearLayout) findViewById(R.id.car_detail_layout);
		mRoot = (TextView) findViewById(R.id.car_detail_route);
		mTime = (TextView) findViewById(R.id.car_detail_time);
		mPlate = (TextView) findViewById(R.id.car_detail_plate);
		mWaybill = (TextView) findViewById(R.id.car_detail_waybill);
		mFuel = (TextView) findViewById(R.id.car_detail_total_fuel);
		mHundredFuel = (TextView) findViewById(R.id.car_detail_hundred_fuel);
		mIdleFuel = (TextView) findViewById(R.id.car_detail_idle_speed_fuel);
		mIdleTime = (TextView) findViewById(R.id.car_detail_idle_speed_time);
		mTotalTime = (TextView) findViewById(R.id.car_detail_total_time);
		mMileage = (TextView) findViewById(R.id.car_detail_total_mileage);
		mMaxSpeed = (TextView) findViewById(R.id.car_detail_max_speed);
		mAveSpeed = (TextView) findViewById(R.id.car_detail_average_speed);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
		mNewsImg.setOnClickListener(this);
		findViewById(R.id.car_detail_route).setOnClickListener(this);
	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mNewsImg.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.car_condition_detail);
		
		String title = getResources().getString(R.string.car_condition_recent_route);
		mPopup = new PopupTravel(this, title, new OnPopupListener() {
			
			@Override
			public void onClick() {
				
				finish();
				//AppManager.getInstance().finishActivity(CarSelectActivity.class);
			}
		});
	}

	@Override
	protected void loadData() {


		if(getIntent() == null || getIntent().getExtras() == null || getIntent().getExtras().getSerializable("route") == null)
			finish();

		Bundle bundle = getIntent().getExtras();
		mNavi = (Navi) bundle.getSerializable("route");
		if (mNavi != null) {
			final String carlicense = mNavi.carlicense;
			final String carduid = mNavi.carduid;
			final String serialid = mNavi.serialid;
			/**
			 * 获取行程详情
			 */
			getTaskDetail(carlicense, carduid, serialid);
		}
	}

	private void getTaskDetail(final String carlicense, String carduid,
			final String serialid) {
		CarManager.getInstance().getTaskDetail(carduid, serialid,
				new ITaskDetailListener() {

					@Override
					public void onGetResult(int errCode, MtqTaskDetail result) {
						MLog.e(TAG, "errCode: " + errCode + ", result: " + result);
						if (errCode == 0 && result != null) {
							dealTaskDetail(result, carlicense, serialid);
						} else {
							/**
							 * 
							 */
							onFailed();
						}
					}

					@Override
					public void onGetReqKey(String arg0) {

						
					}
				});
	}

	protected void dealTaskDetail(MtqTaskDetail result, String carlicense,
			String serialid) {
		mDetail = CarUtils.formatTaskDetail(result, carlicense,
				serialid);
		if (mDetail != null && mNavi != null) {
			mLayout.setVisibility(View.VISIBLE);
			updateNaviDetail();
			//TravelDetailTable.getInstance().insert(mDetail);
		} else {
			onFailed();
		}
	}

	@Override
	protected void updateUI() {
		updateNews();
	}

	private void updateNews() {
//		if (MsgManager.getInstance().hasUnReadMsg(1)) {
//			mNewsImg.setImageResource(R.drawable.msg_icon_news);
//		} else {
//			mNewsImg.setImageResource(R.drawable.msg_icon);
//		}
	}

	private void updateNaviDetail() {
		boolean isSameDay = TimeUtils.isSameDayOfMillis(mDetail.navi.starttime,
				mDetail.navi.endtime);
		if (isSameDay) {
			String day = TimeUtils.stampToDay(mDetail.navi.starttime);
			String start = TimeUtils.stampToHour(mDetail.navi.starttime);
			String end = TimeUtils.stampToHour(mDetail.navi.endtime);
			mTime.setText(day + " " + start + "-" + end);
		} else {
			String start = TimeUtils.stampToHour(mDetail.navi.starttime);
			String end = TimeUtils.stampToHour(mDetail.navi.endtime);
			mTime.setText(start + "-" + end);
		}

		String carHint = getResources().getString(R.string.car_condition_car);
		String car = String.format(carHint, mDetail.carlicense);
		mPlate.setText(car);

		List<MtqOrder> orderList = mNavi.orders;
		String waybill = "";
		if (orderList != null && !orderList.isEmpty()) {
			/**
			 * 多个运单号，用逗号隔开
			 */
			int len = orderList.size();
			for (int i = 0; i < len; i++) {
				waybill += orderList.get(i).cut_orderid;
				if (i != len - 1) {
					waybill += ", ";
				}
			}
		}
		String waybillHint = getResources().getString(
				R.string.car_condition_waybill);
		String bill = String.format(waybillHint, waybill);
		mWaybill.setText(bill);
		mFuel.setText(mDetail.navi.fuelcon);
		mHundredFuel.setText(mDetail.hundred_fuel);
		mIdleFuel.setText(mDetail.navi.idlefuelcon);
		mIdleTime.setText(mDetail.navi.idletime);
		mTotalTime.setText(mDetail.navi.traveltime);
		mMileage.setText(mDetail.navi.mileage);
		mMaxSpeed.setText(mDetail.navi.topspeed);
		mAveSpeed.setText(mDetail.average_speed);
	}
	
	private void onFailed() {
		mLayout.setVisibility(View.GONE);
	}

	@Override
	protected void messageEvent(AccountEvent event) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img: {
			finish();
			break;
		}
		case R.id.title_right_img: {
//			Intent intent = new Intent(this, MsgActivity.class);
//			startActivity(intent);
			break;
		}
		case R.id.car_detail_route: {
			//finish();
			//AppManager.getInstance().finishActivity(CarSelectActivity.class);
			mPopup.showAsDropDown(mRoot, 0, 0);
			break;
		}
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {

		super.onResume();
	}
}
