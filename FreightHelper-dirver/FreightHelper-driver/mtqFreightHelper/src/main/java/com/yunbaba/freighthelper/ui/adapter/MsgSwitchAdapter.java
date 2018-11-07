/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: MsgSwitchAdapter.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.adapter
 * @Description: 通知开关
 * @author: zhaoqy
 * @date: 2017年4月14日 下午2:20:11
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.msg.Filter;
import com.yunbaba.freighthelper.db.MsgFilterTable;

public class MsgSwitchAdapter extends BaseAdapter {
	private List<Filter> mList = null;
	private Context mContext;

	public MsgSwitchAdapter(Context context, List<Filter> list) {
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
		final Filter filter = mList.get(position);
		final ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_msg_switch, null);
			viewHolder.title = (TextView) view
					.findViewById(R.id.msg_switch_title);
			viewHolder.line = view.findViewById(R.id.msg_switch_line);
			viewHolder.content = (TextView) view
					.findViewById(R.id.msg_switch_content);
			viewHolder.checkbox = (CheckBox) view
					.findViewById(R.id.msg_switch_checkbox);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		if (filter.getPosition() == 0) {
			viewHolder.title.setText(filter.getTitle());
			viewHolder.title.setVisibility(View.VISIBLE);
			viewHolder.line.setVisibility(View.GONE);
		} else {
			viewHolder.title.setVisibility(View.GONE);
			viewHolder.line.setVisibility(View.VISIBLE);
		}
		viewHolder.content.setText(filter.getContent());

		if (filter.getOpen() == 0) {
			viewHolder.checkbox.setChecked(false);
		} else if (filter.getOpen() == 1) {
			viewHolder.checkbox.setChecked(true);
		}
		
		/**
		 * ListView滚动时自动调用 onCheckedChanged, 导致CheckBox状态不停变化
		 * 改用OnClickListener
		 */
		viewHolder.checkbox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (filter.getOpen() == 0) {
					viewHolder.checkbox.setChecked(true);
					filter.setOpen(1);
				} else if (filter.getOpen() == 1) {
					viewHolder.checkbox.setChecked(false);
					filter.setOpen(0);
				}
				MsgFilterTable.getInstance().update(filter, 2);
			}
		});
		return view;
	}

	final static class ViewHolder {
		TextView title;
		View line;
		TextView content;
		CheckBox checkbox;
	}
}
