/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: NoticeSwitchActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.msg
 * @Description: 通知开关界面
 * @author: zhaoqy
 * @date: 2017年3月22日 下午7:31:11
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.msg;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.bean.msg.Filter;
import com.yunbaba.freighthelper.bean.msg.Filter.FilterId;
import com.yunbaba.freighthelper.db.MsgFilterTable;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.ui.adapter.MsgSwitchAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MsgSwitchActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "MsgSwitchActivity";
	private ImageView mBack;
	private TextView mTitle;
	private ListView mListView;
	private MsgSwitchAdapter mAdapter;
	private List<Filter> mFilterList;
	private int mLen = 16;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_msg_switch;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mListView = (ListView) findViewById(R.id.msg_switch_listview);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.msg_switch);
	}

	@Override
	protected void loadData() {
		mFilterList = MsgFilterTable.getInstance().queryByType(2);
		if (mFilterList == null || mFilterList.size() < mLen) {
			initFilters();
		}

		Iterator<Filter> iter = mFilterList.iterator();
		while(iter.hasNext()){

			Filter tmp = iter.next();

			if(tmp.getId()  == FilterId.FILTER_ID_14 ||
					tmp.getId()  == FilterId.FILTER_ID_15 ||
					tmp.getId()  == FilterId.FILTER_ID_16 ||
					tmp.getId()  == FilterId.FILTER_ID_17
					){
				iter.remove();
			}
		}

		if (mFilterList != null && !mFilterList.isEmpty()) {
			mAdapter = new MsgSwitchAdapter(this, mFilterList);
			mListView.setAdapter(mAdapter);
		}
	}

	@Override
	protected void updateUI() {
		
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
		default:
			break;
		}
	}

	private void initFilters() {
		mFilterList = new ArrayList<Filter>();
		mFilterList.clear();

//		/************ 任务 ********/
//		/**
//		 * 运单超时报警
//		 */
//		Filter filter0 = new Filter();
//		filter0.setId(FilterId.FILTER_ID_14);
//		filter0.setType(2);
//		filter0.setOpen(1);
//		filter0.setPosition(0);
//		filter0.setTitle(getResources().getString(R.string.switch_task));
//		filter0.setContent(getResources().getString(
//				R.string.switch_waybill_timeout_alarm));
//		mFilterList.add(filter0);
//
//		/**
//		 * 运单超时提醒
//		 */
//		Filter filter1 = new Filter();
//		filter1.setId(FilterId.FILTER_ID_15);
//		filter1.setType(2);
//		filter1.setOpen(1);
//		filter1.setTitle(getResources().getString(R.string.switch_task));
//		filter1.setContent(getResources().getString(
//				R.string.switch_waybill_timeout_remind));
//		mFilterList.add(filter1);
//
//		/**
//		 * 运单过期报警
//		 */
//		Filter filter2 = new Filter();
//		filter2.setId(FilterId.FILTER_ID_16);
//		filter2.setType(2);
//		filter2.setOpen(1);
//		filter2.setTitle(getResources().getString(R.string.switch_task));
//		filter2.setContent(getResources().getString(
//				R.string.switch_waybill_expired_alarm));
//		mFilterList.add(filter2);
//
//		/**
//		 * 运单过期提醒
//		 */
//		Filter filter3 = new Filter();
//		filter3.setId(FilterId.FILTER_ID_17);
//		filter3.setType(2);
//		filter3.setOpen(1);
//		filter3.setTitle(getResources().getString(R.string.switch_task));
//		filter3.setContent(getResources().getString(
//				R.string.switch_waybill_expired_remind));
//		mFilterList.add(filter3);

		/************ 车辆安全 ********/
		/**
		 * 碰撞提醒
		 */
		Filter filter4 = new Filter();
		filter4.setId(FilterId.FILTER_ID_19);
		filter4.setType(2);
		filter4.setOpen(1);
		filter4.setPosition(0);
		filter4.setTitle(getResources().getString(R.string.switch_car_safety));
		filter4.setContent(getResources().getString(
				R.string.switch_safety_collision));
		mFilterList.add(filter4);

		/**
		 * 翻车提醒
		 */
		Filter filter5 = new Filter();
		filter5.setId(FilterId.FILTER_ID_20);
		filter5.setType(2);
		filter5.setOpen(1);
		filter5.setTitle(getResources().getString(R.string.switch_car_safety));
		filter5.setContent(getResources().getString(
				R.string.switch_safety_rollover));
		mFilterList.add(filter5);

		/**
		 * 超速行驶提醒
		 */
		Filter filter6 = new Filter();
		filter6.setId(FilterId.FILTER_ID_21);
		filter6.setType(2);
		filter6.setOpen(1);
		filter6.setTitle(getResources().getString(R.string.switch_car_safety));
		filter6.setContent(getResources().getString(
				R.string.switch_safety_speeding));
		mFilterList.add(filter6);

		/**
		 * 疲劳驾驶提醒
		 */
		Filter filter7 = new Filter();
		filter7.setId(FilterId.FILTER_ID_22);
		filter7.setType(2);
		filter7.setOpen(1);
		filter7.setTitle(getResources().getString(R.string.switch_car_safety));
		filter7.setContent(getResources().getString(
				R.string.switch_safety_fatigue));
		mFilterList.add(filter7);

		/**
		 * 异常震动提醒
		 */
		Filter filter8 = new Filter();
		filter8.setId(FilterId.FILTER_ID_23);
		filter8.setType(2);
		filter8.setOpen(1);
		filter8.setTitle(getResources().getString(R.string.switch_car_safety));
		filter8.setContent(getResources().getString(
				R.string.switch_safety_vibration));
		mFilterList.add(filter8);

		/************ 车辆异常 ********/
		/**
		 * 车门状态异常提醒
		 */
		Filter filter9 = new Filter();
		filter9.setId(FilterId.FILTER_ID_24);
		filter9.setType(2);
		filter9.setOpen(1);
		filter9.setPosition(0);
		filter9.setTitle(getResources().getString(R.string.switch_car_abnormal));
		filter9.setContent(getResources().getString(
				R.string.switch_abnormal_door));
		mFilterList.add(filter9);

		/**
		 * 胎压和手刹异常提醒
		 */
		Filter filter10 = new Filter();
		filter10.setId(FilterId.FILTER_ID_25);
		filter10.setType(2);
		filter10.setOpen(1);
		filter10.setTitle(getResources()
				.getString(R.string.switch_car_abnormal));
		filter10.setContent(getResources().getString(
				R.string.switch_abnormal_handbrake));
		mFilterList.add(filter10);

		/**
		 * 水温异常提醒
		 */
		Filter filter11 = new Filter();
		filter11.setId(FilterId.FILTER_ID_26);
		filter11.setType(2);
		filter11.setOpen(1);
		filter11.setTitle(getResources()
				.getString(R.string.switch_car_abnormal));
		filter11.setContent(getResources().getString(
				R.string.switch_abnormal_water));
		mFilterList.add(filter11);

		/**
		 * 转速异常提醒
		 */
		Filter filter12 = new Filter();
		filter12.setId(FilterId.FILTER_ID_27);
		filter12.setType(2);
		filter12.setOpen(1);
		filter12.setTitle(getResources()
				.getString(R.string.switch_car_abnormal));
		filter12.setContent(getResources().getString(
				R.string.switch_abnormal_rotating_speed));
		mFilterList.add(filter12);

		/**
		 * 电瓶电压异常提醒
		 */
		Filter filter13 = new Filter();
		filter13.setId(FilterId.FILTER_ID_28);
		filter13.setType(2);
		filter13.setOpen(1);
		filter13.setTitle(getResources()
				.getString(R.string.switch_car_abnormal));
		filter13.setContent(getResources().getString(
				R.string.switch_abnormal_battery_voltage));
		mFilterList.add(filter13);

		/**
		 * 车辆故障提醒
		 */
		Filter filter14 = new Filter();
		filter14.setId(FilterId.FILTER_ID_29);
		filter14.setType(2);
		filter14.setOpen(1);
		filter14.setTitle(getResources()
				.getString(R.string.switch_car_abnormal));
		filter14.setContent(getResources().getString(
				R.string.switch_abnormal_vehicle_failure));
		mFilterList.add(filter14);

		/**
		 * 漏油提醒
		 */
		Filter filter15 = new Filter();
		filter15.setId(FilterId.FILTER_ID_30);
		filter15.setType(2);
		filter15.setOpen(1);
		filter15.setTitle(getResources()
				.getString(R.string.switch_car_abnormal));
		filter15.setContent(getResources().getString(
				R.string.switch_abnormal_oil_spill));
		mFilterList.add(filter15);

		/**
		 * 拖吊报警
		 */
		Filter filter16 = new Filter();
		filter16.setId(FilterId.FILTER_ID_31);
		filter16.setType(2);
		filter16.setOpen(1);
		filter16.setTitle(getResources()
				.getString(R.string.switch_car_abnormal));
		filter16.setContent(getResources().getString(
				R.string.switch_abnormal_towing));
		mFilterList.add(filter16);

		/************ 设备异常 ********/
		/**
		 * 断电提醒
		 */
		Filter filter17 = new Filter();
		filter17.setId(FilterId.FILTER_ID_32);
		filter17.setType(2);
		filter17.setOpen(1);
		filter17.setPosition(0);
		filter17.setTitle(getResources().getString(
				R.string.switch_device_abnormal));
		filter17.setContent(getResources().getString(
				R.string.switch_abnormal_poweroff));
		mFilterList.add(filter17);

		/**
		 * 终端异常
		 */
		Filter filter18 = new Filter();
		filter18.setId(FilterId.FILTER_ID_33);
		filter18.setType(2);
		filter18.setOpen(1);
		filter18.setTitle(getResources().getString(
				R.string.switch_device_abnormal));
		filter18.setContent(getResources().getString(
				R.string.switch_abnormal_terminal));
		mFilterList.add(filter18);

		/**
		 * 设备OBD终端的SIM卡
		 */
		Filter filter19 = new Filter();
		filter19.setId(FilterId.FILTER_ID_34);
		filter19.setType(2);
		filter19.setOpen(1);
		filter19.setTitle(getResources().getString(
				R.string.switch_device_abnormal));
		filter19.setContent(getResources().getString(
				R.string.switch_abnormal_sim));
		mFilterList.add(filter19);

		/**
		 * 保存到数据库
		 */
		MsgFilterTable.getInstance().insert(mFilterList);
	}

}
