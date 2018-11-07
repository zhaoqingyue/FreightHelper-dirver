/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: CarInfoAdapter.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.adapter
 * @Description: 车辆信息
 * @author: zhaoqy
 * @date: 2017年3月25日 下午5:37:33
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.car.CarInfo;
import com.yunbaba.freighthelper.utils.MLog;

import java.util.ArrayList;

public class CarInfoAdapter extends BaseAdapter {

	private ArrayList<CarInfo> mList;
	private Context mContext;
	
	public CarInfoAdapter(Context context, ArrayList<CarInfo> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public CarInfo getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		CarInfo carInfo = mList.get(position);
		MLog.e("CarInfoAdapter", "taskId: " + carInfo.taskId);
		MLog.e("CarInfoAdapter", "corpId: " + carInfo.corpId);
		
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.layout_carinfo_item, parent, false);
			viewHolder.num = (TextView) view
					.findViewById(R.id.carinfo_num);
			viewHolder.plate = (TextView) view
					.findViewById(R.id.carinfo_plate);
			viewHolder.type = (TextView) view
					.findViewById(R.id.carinfo_type);
			viewHolder.brand = (TextView) view
					.findViewById(R.id.carinfo_brand);
			viewHolder.model = (TextView) view
					.findViewById(R.id.carinfo_model);
			viewHolder.name = (TextView) view
					.findViewById(R.id.carinfo_device_name);
			viewHolder.serial = (TextView) view
					.findViewById(R.id.carinfo_device_serial);
			viewHolder.expirate = (TextView) view
					.findViewById(R.id.carinfo_device_expirate);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		
		MLog.e("CarInfoAdapter", "carlicense: " + carInfo.carlicense);
		MLog.e("CarInfoAdapter", "carmodel: " + carInfo.carmodel);
		
		MLog.e("CarInfoAdapter", "brand: " + carInfo.brand);
		MLog.e("CarInfoAdapter", "devicename: " + carInfo.devicename);
		
		String numHint = mContext.getResources().getString(
				R.string.carinfo_num);
		String num = String.format(numHint, position+1);
		viewHolder.num.setText(num);
		if (!TextUtils.isEmpty(carInfo.carlicense)) {
			viewHolder.plate.setText(carInfo.carlicense);
		}
		if (!TextUtils.isEmpty(carInfo.carmodel)) {
			viewHolder.type.setText(carInfo.carmodel);
		}
		if (!TextUtils.isEmpty(carInfo.brand)) {
			viewHolder.brand.setText(carInfo.brand);
		}
		if (!TextUtils.isEmpty(carInfo.vehicletype)) {
			viewHolder.model.setText(carInfo.vehicletype);
		}
		if (!TextUtils.isEmpty(carInfo.devicename)) {
			viewHolder.name.setText(carInfo.devicename);
		}
		if (!TextUtils.isEmpty(carInfo.mcuid)) {
			viewHolder.serial.setText(carInfo.mcuid);
		}
		if (!TextUtils.isEmpty(carInfo.sim_endtime)) {
			viewHolder.expirate.setText(carInfo.sim_endtime);
		}
		
		MLog.e("CarInfoAdapter", "view: " + view.toString());
		return view;
	}

	final static class ViewHolder {
		TextView num;
		TextView plate;
		TextView type;
		TextView brand;
		TextView model;
		TextView name;
		TextView serial;
		TextView expirate;
	}
}
