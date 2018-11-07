/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: AlarmMsgAdapter.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.adapter
 * @Description: 报警消息adapter
 * @author: zhaoqy
 * @date: 2017年3月29日 下午4:00:49
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.msg.MsgContent;
import com.yunbaba.freighthelper.bean.msg.MsgInfo;

public class AlarmMsgAdapter extends BaseAdapter {

	public static final int ALARM_CAR_SAFETY = 0;
	public static final int ALARM_CAR_ABNORMAL = 1;
	public static final int ALARM_DEVICE_ABNORMAL = 2;

	private List<MsgInfo> mList = null;
	private Context mContext;

	public AlarmMsgAdapter(Context context, List<MsgInfo> list) {
		mContext = context;
		mList = list;
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
		MsgContent item = mList.get(position).getMsgContent();
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_msg_alarm_msg, null);
			viewHolder.time = (TextView) view
					.findViewById(R.id.alarm_msg_item_time);
			viewHolder.icon = (ImageView) view
					.findViewById(R.id.alarm_msg_item_icon);
			viewHolder.title = (TextView) view
					.findViewById(R.id.alarm_msg_item_title);
			viewHolder.content = (TextView) view
					.findViewById(R.id.alarm_msg_item_content);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.time.setText(item.getCreatetime());
		int alarmtype = item.getAlarmType();
		String title = "";
		switch (alarmtype) {
		case ALARM_CAR_SAFETY: {
			viewHolder.icon.setImageResource(R.drawable.msg_alarm_safety);
			title = mContext.getResources().getString(
					R.string.switch_car_safety);
			viewHolder.title.setText(title);
			break;
		}
		case ALARM_CAR_ABNORMAL: {
			viewHolder.icon.setImageResource(R.drawable.msg_alarm_abnormal);
			title = mContext.getResources().getString(
					R.string.switch_car_abnormal);
			viewHolder.title.setText(title);
			break;
		}
		case ALARM_DEVICE_ABNORMAL: {
			viewHolder.icon.setImageResource(R.drawable.msg_alarm_abnormal);
			title = mContext.getResources().getString(
					R.string.switch_device_abnormal);
			viewHolder.title.setText(title);
			break;
		}
		default:
			break;
		}
		
		/*String brandHint = mContext.getResources().getString(
				R.string.msg_alarm_brand);
		String brand = String.format(brandHint, item.getBrand());
		String content = brand + item.getContent();
		holder.content.setText(content);*/
		viewHolder.content.setText(item.getContent());
		return view;
	}

	/**
	 * @Description: 报警消息
	 * @author: zhaoqy
	 * @date: 2017年3月29日 下午3:36:34
	 * @version: V1.0
	 */
	final static class ViewHolder {
		ImageView icon;
		TextView time;
		TextView title;
		TextView content;
	}
}
