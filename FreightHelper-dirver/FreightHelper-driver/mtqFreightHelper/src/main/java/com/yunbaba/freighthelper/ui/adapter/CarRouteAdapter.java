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
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.CustomDate;
import com.yunbaba.freighthelper.bean.car.Navi;
import com.yunbaba.freighthelper.ui.activity.car.TravelDetialActivity;
import com.yunbaba.freighthelper.utils.FormatUtils;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.TimeUtils;

import java.util.List;

public class CarRouteAdapter extends BaseAdapter {

	private List<Navi> mList = null;
	private Context mContext;
	
	//选中的日期
	private String mChoiceDate;
	
	private RelativeSizeSpan mSizeSpan;
	private ForegroundColorSpan mColorSpan;

	public CarRouteAdapter(Context context, List<Navi> list) {
		mContext = context;
		mList = list;
		mChoiceDate = "";
		mSizeSpan = new RelativeSizeSpan(0.6f);
		mColorSpan = new ForegroundColorSpan(Color.parseColor("#ff7800"));
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Navi route = mList.get(position);
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_car_route, null);
			viewHolder.carlicense = (TextView) view
					.findViewById(R.id.car_route_carlicense);
			viewHolder.gap = view.findViewById(R.id.car_route_gap);
			viewHolder.routelayout = view.findViewById(R.id.car_route_layout);
			viewHolder.period2 = (TextView) view
					.findViewById(R.id.car_route_period2);
			viewHolder.period4 = (TextView) view
					.findViewById(R.id.car_route_period4);
			viewHolder.waybilllayout = view
					.findViewById(R.id.car_route_waybill_layout);
			viewHolder.waybill = (TextView) view
					.findViewById(R.id.car_route_waybill);
			viewHolder.mileage = (TextView) view
					.findViewById(R.id.car_route_total_mileage);
			viewHolder.time = (TextView) view
					.findViewById(R.id.car_route_total_time);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		String start = TimeUtils.stampToHour(route.starttime);
		String end = TimeUtils.stampToHour(route.endtime);
		// holder.period.setText(start + "-" + end);
		
		String startDate = TimeUtils.stampToDay(route.starttime);
		String endDate = TimeUtils.stampToDay(route.endtime);
		
		if (startDate.equals(endDate)){
			//同一天
			viewHolder.period2.setText(start + "-");
			viewHolder.period4.setText(end);
		}else{
			
			int between = TimeUtils.daysBetweenByDate(startDate, mChoiceDate);
			String tmp;
			String text;
			SpannableString ss;
			if (between != 0){
				tmp = "(-" + between + "天)";
				text = start + tmp + "-";
				ss = new SpannableString(text);
				int statrIndex = start.length();
				int endIndex = start.length() + tmp.length();
				
				ss.setSpan(mColorSpan, statrIndex,
						endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ss.setSpan(mSizeSpan, statrIndex,
						endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				viewHolder.period2.setText(ss);
			}else{
				viewHolder.period2.setText(start + "-");
			}
			
			between = TimeUtils.daysBetweenByDate(mChoiceDate, endDate);
			
			if (between != 0){
				tmp = "(+" + between + "天)";
				text = end + tmp;
				ss = new SpannableString(text);
				int statrIndex = start.length();
				int endIndex = start.length() + tmp.length();
				
				ss.setSpan(mColorSpan, statrIndex,
						endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ss.setSpan(mSizeSpan, statrIndex,
						endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				viewHolder.period4.setText(ss);
				
			}else{
				viewHolder.period4.setText(end);
			}
		}

		if (route.position == 0) {
			viewHolder.carlicense.setVisibility(View.VISIBLE);
			viewHolder.carlicense.setText(route.carlicense);
			viewHolder.gap.setVisibility(View.GONE);
		} else {
			viewHolder.carlicense.setVisibility(View.GONE);
			viewHolder.gap.setVisibility(View.VISIBLE);
		}

		viewHolder.waybilllayout.setVisibility(View.GONE);
		// List<MtqOrder> orderList = route.orders;
		// if (orderList != null && !orderList.isEmpty()) {
		// holder.waybilllayout.setVisibility(View.VISIBLE);
		// /**
		// * 多个运单号，用逗号隔开
		// */
		// String waybill = "";
		// int len = orderList.size();
		// for (int i = 0; i < len; i++) {
		// waybill += orderList.get(i).cut_orderid;
		// if (i != len - 1) {
		// waybill += ", ";
		// }
		// }
		// holder.waybill.setText(waybill);
		// } else {
		// holder.waybilllayout.setVisibility(View.GONE);
		// }
		float dis = Float.valueOf(route.mileage);
		dis *=1000;
		viewHolder.mileage.setText(FormatUtils
				.meterDisToString((int) dis, true));

		// holder.time.setText(FormatUtils.formatTime(Integer.valueOf(route.traveltime),true));
		viewHolder.time.setText(Integer.valueOf(route.traveltime) / 60 + "min");

		viewHolder.routelayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, TravelDetialActivity.class);

				String str = GsonTool.getInstance().toJson(route);
				intent.putExtra("route", str);
				
				mContext.startActivity(intent);
			}
		});
		return view;
	}

	final static class ViewHolder {
		TextView carlicense;
		View gap;
		View routelayout;
		TextView period1;
		TextView period2;
		TextView period3;
		TextView period4;
		View waybilllayout;
		TextView waybill;
		TextView mileage;
		TextView time;
	}
//	
//	List<MtqCarRoute> listOfResult;
//
//	public void setResourceList(List<MtqCarRoute> listOfResult) {
//
//		this.listOfResult = listOfResult;
//	}
//	
	public void setChoiceDate(CustomDate customDate){
		mChoiceDate = customDate.year + "/"+ customDate.month + "/" + customDate.day;
	}
	
}
