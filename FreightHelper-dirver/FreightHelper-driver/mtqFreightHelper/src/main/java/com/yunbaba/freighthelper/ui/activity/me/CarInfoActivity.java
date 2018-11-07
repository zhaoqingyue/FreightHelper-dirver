/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: CarInfoActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 车辆信息界面
 * @author: zhaoqy
 * @date: 2017年3月20日 下午6:43:44
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.me;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.TaskSpInfo;
import com.yunbaba.freighthelper.bean.car.CarInfo;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.db.TravelCarTable;
import com.yunbaba.freighthelper.manager.CarManager;
import com.yunbaba.freighthelper.manager.CarManager.IGetCarListener;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.SPHelper;

public class CarInfoActivity extends BaseActivity implements OnClickListener {
	private ImageView mBack;
	private TextView mTitle;
	private TextView plate;
	private TextView type;
	private TextView brand;
	private TextView model;
	private TextView name;
	private TextView serial;
	private TextView expirate,tv_hint;
	private LinearLayout mLayout;
	private RelativeLayout mWaitting;
	private View mEmptyView;
	private CarInfo mCarInfo;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_carinfo;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mLayout = (LinearLayout) findViewById(R.id.carinfo_layout);
		plate = (TextView) findViewById(R.id.carinfo_plate);
		type = (TextView) findViewById(R.id.carinfo_type);
		brand = (TextView) findViewById(R.id.carinfo_brand);
		model = (TextView) findViewById(R.id.carinfo_model);
		name = (TextView) findViewById(R.id.carinfo_device_name);
		serial = (TextView) findViewById(R.id.carinfo_device_serial);
		expirate = (TextView) findViewById(R.id.carinfo_device_expirate);
		tv_hint = (TextView) findViewById(R.id.tv_hint);
		mWaitting = (RelativeLayout) findViewById(R.id.carinfo_waiting);
		mEmptyView = findViewById(R.id.carinfo_empty);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.carinfo);
	}

	/**
	 * 获取司机驾驶车辆信息
	 */
	@Override
	protected void loadData() {
		
		if (!CldPhoneNet.isNetConnected()) {
			Toast.makeText(this, R.string.common_network_abnormal,
					Toast.LENGTH_SHORT).show();
		} else {
			
			
			
			if(TaskOperator.getInstance().getmCurrentTask()==null){
				
				
				hasNoCar(0);
				return;
			}
			
			
			
			
			
			showProgressBar();
			/**
			 * 重新切换账号，最近一次任务信息没有同步
			 */
			TaskSpInfo task = SPHelper.getInstance(this).getRecentModifyTask();
			if (task != null) {
				final String taskId = task.taskid;
				final String corpId = task.corpid;
				CarManager.getInstance().getCarInfo(taskId, corpId,
						new IGetCarListener() {

							@Override
							public void onGetResult(int errCode, CarInfo result) {
								hideProgressBar();
								if (errCode == 0 && result != null) {
									mCarInfo = result;
									mHandler.sendEmptyMessage(0);
								} else {
									/**
									 * 获取失败
									 */
									mCarInfo = TravelCarTable.getInstance()
											.query(taskId, corpId);
									if (mCarInfo != null) {
										mHandler.sendEmptyMessage(0);
									} else {
										/**
										 * 无车辆信息
										 */
										hasNoCar(1);
									}
								}
							}
						});
			} else {
				/**
				 * 无车辆信息
				 */
				hasNoCar(1);
			}
		}
	}

	@Override
	protected void updateUI() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img: {
			finish();
			break;
		}
		default:
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				plate.setText(SetStrSafety(mCarInfo.carlicense));
				type.setText(SetStrSafety(mCarInfo.carmodel));
				brand.setText(SetStrSafety(mCarInfo.brand));
				model.setText(SetStrSafety(mCarInfo.vehicletype));
				name.setText(SetStrSafety(mCarInfo.devicename));
				serial.setText(SetStrSafety(mCarInfo.mcuid));
				expirate.setText(SetStrSafety(mCarInfo.sim_endtime));
				break;
			}
			default:
				break;
			}
		}
	};

	
	public CharSequence SetStrSafety(CharSequence str) {

		return TextUtils.isEmpty(str) ? "--" : str;

	}
	
	
	@Override
	protected void messageEvent(AccountEvent event) {
		switch (event.msgId) {
		case MessageId.MSGID_GET_CARINFO_SUCCESS:

			break;
		case MessageId.MSGID_GET_CARINFO_FAILED:

			break;
		default:
			break;
		}
	}

	protected void showProgressBar() {
		mLayout.setVisibility(View.GONE);
		mWaitting.setVisibility(View.VISIBLE);
		mEmptyView.setVisibility(View.GONE);
	}

	protected void hideProgressBar() {
		mLayout.setVisibility(View.VISIBLE);
		mWaitting.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.GONE);
	}
	
	protected void hasNoCar(int type) {
		mLayout.setVisibility(View.GONE);
		mWaitting.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.VISIBLE);
		
		
		if(type == 0){
			
			tv_hint.setText("当前没有任务车辆");
			
		}else{
			
			
			tv_hint.setText("暂无车辆信息");
		}
	}
}
