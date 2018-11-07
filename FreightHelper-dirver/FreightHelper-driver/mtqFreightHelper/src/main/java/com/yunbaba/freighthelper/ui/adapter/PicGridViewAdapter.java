package com.yunbaba.freighthelper.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.freighthelper.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PicGridViewAdapter extends BaseAdapter {

	private Context mContext;
	private int picNumLimit = 3;
	private List<String> list = new ArrayList<String>();
	private int type = 1;
	/** 1 详情 2 上传收款 **/
	private String corpid = "";
	private String taskid = "";

	public PicGridViewAdapter() {
		super();
	}

	/**
	 * 获取列表数据
	 * 
	 * @param list
	 */
	public void setList(List<String> list) {
		// this.list = list;
		this.notifyDataSetChanged();

	}

	public PicGridViewAdapter(Context mContext, List<String> list, int limit, int type, String corpid, String taskid) {
		super();
		this.mContext = mContext;
		this.list = list;
		this.picNumLimit = limit;
		this.type = type;
		this.corpid = corpid;
		this.taskid = taskid;
	}

	@Override
	public int getCount() {
		// MLog.e(" ", list.size() + "");
		if (list == null) {
			return 1;
		} else if (list.size() >= picNumLimit) {
			return picNumLimit;
		} else {
			return list.size() + 1;
		}
	}

	@Override
	public String getItem(int position) {
		if (list != null && list.size() == picNumLimit) {
			return list.get(position);
		} else if (list != null && list.size() < picNumLimit) {

			if (position < list.size()) {
				return list.get(position);

			} else
				return null;

		} else if(list != null && list.size() >= picNumLimit){
		
			
			return list.get(position);
			
		}else
			return null;

		// else if (list == null || (list!=position - 1 < 0) || position >
		// list.size()) {
		// return null;
		// } else {
		// return list.get(position - 1);
		// }
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_published_grida, null);
			holder = new ViewHolder();
			holder.item_grida_image = (ImageView) convertView.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (isShowAddItem(position)) {
			// holder.item_grida_image.setImageResource(R.drawable.btn_add_pic);
			Picasso.with(mContext).load(R.drawable.img_add_pic_normal)  //.resize(100, 100).centerCrop()
					.into(holder.item_grida_image);

			holder.item_grida_image.setBackgroundResource(R.color.gray);

		} else {
			// holder.item_grida_image.setImageBitmap(list.get(position));
			// holder.item_grida_image.setImageResource(R.drawable.img_add_pic_normal);

			if (type == 2)
				Picasso.with(mContext).load("file://" + getItem(position))   //.resize(100, 100)
						.into(holder.item_grida_image);
			else {

				
				//MLog.e("checkgetitem", ""+getItem(position));
				
				if (getItem(position) != null) {

					if (getItem(position).contains(".jpg")) {

						Picasso.with(mContext).load("file://" + getItem(position))   //.resize(100, 100)  //.fit()
								.into(holder.item_grida_image);

					} else {

						//MLog.e("checkpicpath", getThumbpicUrl(corpid, taskid, getItem(position)) + "");
						Picasso.with(mContext).load(DeliveryApi.getThumbpicUrl(corpid, taskid, getItem(position)))   //.fit()  //.resize(100, 100).centerCrop()  //
								.into(holder.item_grida_image);
					}
				}

			}

			holder.item_grida_image.setBackgroundResource(R.color.gray);
		}
		return convertView;
	}

	/**
	 * 判断当前下标是否是最大值
	 * 
	 * @param position
	 *            当前下标
	 * @return
	 */
	private boolean isShowAddItem(int position) {
		int size = (list == null) ? 0 : list.size();
		return position == size;
	}

	public int getPicNumLimit() {
		return picNumLimit;
	}

	public void setPicNumLimit(int picNumLimit) {
		this.picNumLimit = picNumLimit;
	}

	
	// public String getThumbpicUrl(String corpid, String taskid, String picid)
	// {
	// return
	// "http://test.careland.com.cn/kz/web/khyapi/hyapi/tis/v1/delivery_get_thumb.php?corpid=\""
	// + corpid
	// + "\"&taskid=\"" + taskid + "\"&picid=" + picid + "&uptime=" +
	// System.currentTimeMillis() / 1000;
	//
	// }
	//
	// public String getSourcepicUrl(String corpid, String taskid, String picid)
	// {
	//
	// return
	// "http://test.careland.com.cn/kz/web/khyapi/hyapi/tis/v1/delivery_get_pic.php?corpid=\""
	// + corpid
	// + "\"&taskid=\"" + taskid + "\"&picid=" + picid + "&uptime=" +
	// System.currentTimeMillis() / 1000;
	//
	// }

	static class ViewHolder {
		ImageView item_grida_image;
	}

}
