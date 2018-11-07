package com.yunbaba.freighthelper.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import com.yunbaba.api.trunk.bean.UploadGoodScanRecordResult;
import com.yunbaba.freighthelper.R;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: ScanRecordListAdapter.java
 * @Prject: Freighthelper
 * @Package: com.mtq.freighthelper.ui.adapter
 * @Description: 扫描运货单列表项
 * @author: zsx
 * @date: 2017-3-30 下午4:28:07
 * @version: V1.0
 */
public class ScanRecordListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<UploadGoodScanRecordResult> mListResult;
	public ScanRecordListAdapter(Context ctx,List<UploadGoodScanRecordResult> listResult){
		mContext = ctx.getApplicationContext();
		mLayoutInflater = LayoutInflater.from(mContext);
		mListResult = listResult;
	}
	
	@Override
	public int getCount() {

		if (mListResult != null){
			return mListResult.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		
		if (convertView == null){
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.listitem_waybill, parent, false);
			viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.waybill_list_name);
			viewHolder.tvBarCode = (TextView)convertView.findViewById(R.id.waybill_list_item_number);
			viewHolder.tvUploadResult = (TextView)convertView.findViewById(R.id.waybill_list_item_uploadresult);
			viewHolder.tvAddress = (TextView)convertView.findViewById(R.id.waybill_list_item_addressName);
			viewHolder.tvDate = (TextView)convertView.findViewById(R.id.waybill_list_item_date);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		UploadGoodScanRecordResult result = mListResult.get(position);
		
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss"); 
		String time = format.format(result.uploadDate*1000L);
		if (result.uploadDate == 0){
			time = "";
		}
		
		viewHolder.tvTitle.setText(result.name);
		viewHolder.tvBarCode.setText(result.bar_code);
		String tmp1 = "已传";
		String tmp2 = "" + result.scan_cnt;
		String source = tmp1+ tmp2 + "/" +result.amount;

		SpannableString ss = new SpannableString(source);
		if (result.scan_cnt < result.amount){
//			int statrIndex = 0;
//			int endIndex = tmp1.length() + 1;
//			ss.setSpan(new ForegroundColorSpan(R.color.scanrecord_list_uploadresult), statrIndex,
//					endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			int statrIndex = tmp1.length();
			int endIndex = tmp1.length() + tmp2.length();
			ss.setSpan(new ForegroundColorSpan(Color.parseColor("#ff7800")), statrIndex,
					endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			
//			statrIndex = endIndex;
//			endIndex = source.length();
//			ss.setSpan(new ForegroundColorSpan(R.color.scanrecord_list_uploadresult), statrIndex,
//					endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		viewHolder.tvUploadResult.setText(ss);
		viewHolder.tvAddress.setText(result.address);
		viewHolder.tvDate.setText(time);
		
		return convertView;
	}
	
	
	final class ViewHolder{
		TextView tvTitle;
		TextView tvBarCode;
		TextView tvUploadResult;
		TextView tvAddress;
		TextView tvDate;
	}

}
