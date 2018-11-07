package com.yunbaba.freighthelper.ui.activity.task.feedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBackPictures;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.yunbaba.api.trunk.DeliveryApi.getThumbpicUrlByFileLink;

public class FeedBackPicAdapter extends BaseAdapter {

	private Context mContext;
	private int picNumLimit = 3;
	private List<FeedBackPictures> list = new ArrayList<FeedBackPictures>();

	private String corpid = "";
	private String taskid = "";

	public FeedBackPicAdapter() {
		super();
	}

	/**
	 * 获取列表数据
	 * 
	 * @param list
	 */
	public void setList(List<FeedBackPictures> list) {
		this.list = list;
		this.notifyDataSetChanged();

	}

	public FeedBackPicAdapter(Context mContext, List<FeedBackPictures> list, String corpid, String taskid) {

		this.mContext = mContext;
		this.list = list;

		this.corpid = corpid;
		this.taskid = taskid;
	}

	@Override
	public int getCount() {
		// MLog.e(" ", list.size() + "");
		if (list == null) {
			return 0;
		} else {
			return list.size();
		}
	}

	@Override
	public FeedBackPictures getItem(int position) {
		if (list != null) {
			return list.get(position);
		} else
			return null;

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

		if (getItem(position) != null) {

//			if (getItem(position).picid.contains(".jpg")) {
//
//				Picasso.with(mContext).load("file://" + getItem(position)) // .resize(100,
//																			// 100)
//																			// //.fit()
//						.resize(150,150).into(holder.item_grida_image);
//
//			} else {

				// MLog.e("checkpicpath", getThumbpicUrl(corpid, taskid,
				// getItem(position)) + "");
			String ss  =  getThumbpicUrlByFileLink(getItem(position).file);

			//MLog.e("mystring",ss+"");

				Picasso.with(mContext).load(ss).resize(150,150).into(holder.item_grida_image);

					//	getItem(position).file)

				// DeliveryApi.getThumbpicUrl(corpid, taskid,
				// getItem(position).picid)) // .fit()
				// //.resize(100,
				// 100).centerCrop()
				// //

			//}
		} else

			holder.item_grida_image.setBackgroundResource(R.color.gray);

		return convertView;
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