package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.OnStoreMarkClickEvent;
import com.yunbaba.freighthelper.utils.CommonTool;
import com.yunbaba.ols.module.delivery.bean.MtqStore;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by zhonghm on 2018/4/23.
 */

public class SearchAndMarkResultAdapter extends BaseAdapter {

	public List<MtqStore> mlistdata;
	// HashMap<String, String> mlistdata;
	Context mContext;
	private int currentselect = 0;

	public static final int keycolor = Color.parseColor("#ef2c29");



	private boolean isRecent = true;
	public boolean isRecent() {
		return isRecent;
	}

	public void setRecent(boolean isRecent) {
		this.isRecent = isRecent;
	}

	public void RefreshList(boolean isRecent) {

		this.isRecent = isRecent;
		this.notifyDataSetChanged();
	}

	public SearchAndMarkResultAdapter(Context mContext, List<MtqStore> mlistdata2) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.mlistdata = mlistdata2;
	}

	@Override
	public int getCount() {

		return mlistdata == null ? 0 : mlistdata.size();
	}

	@Override
	public MtqStore getItem(int position) {

		// if()
		return mlistdata == null ? null : mlistdata.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	String keyword;

	@Override
	public View getView(int position, View view, ViewGroup parent) {


		final ViewHolder  viewHolder;

		if (view != null) {
			viewHolder = (ViewHolder) view.getTag();
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_store_search_mark, parent, false);
			viewHolder = new ViewHolder(view);
			view.setTag( viewHolder);
		}


		MtqStore storeDetail = getItem(position);


		viewHolder.storeDetail = storeDetail;
		// viewHolder.address.setText((storeDetail.regionname +
		// storeDetail.storeaddr).replaceAll("\\s*", ""));

		if (storeDetail != null && !TextUtils.isEmpty(storeDetail.storeCode)) {
			//  String temp = storeDetail.storesort + "." +storeDetail.storeCode + "-" + storeDetail.storeName;
			CharSequence format =  storeDetail.storeCode+"-" + storeDetail.storeName;//FreightLogicTool.formatPointNameStr("", storeDetail.storeCode, "-" + storeDetail.storeName);
			if (format != null) {
				viewHolder.name.setText(format);
			} else {
				viewHolder.name.setText(
						 (TextUtils.isEmpty(storeDetail.storeCode) ? "" : (storeDetail.storeCode + "-"))
								+ storeDetail.storeName);
			}
		} else {
			// viewHolder.name.setText(storeDetail.storesort + "." + storeDetail.storeName);

			viewHolder.name.setText(
					(TextUtils.isEmpty(storeDetail.storeCode) ? "" : (storeDetail.storeCode + "-"))
							+ storeDetail.storeName);
		}



		viewHolder.address.setText((storeDetail.regionName + storeDetail.storeAddr).replaceAll("\\s*", ""));



//		viewHolder.status
//				.setText(FreightPointDeal.getInstace().getStoreStatusText(storeDetail.storestatus, storeDetail.optype));
		viewHolder.status.setVisibility(View.GONE);
		viewHolder.time.setVisibility(View.GONE);
		viewHolder.timeStatus.setVisibility(View.GONE);

//        }

		// viewHolder.operate2.setVisibility(View.INVISIBLE);

		//setImgbyOptype(storeDetail.optype, viewHolder.image);

		//   setViewbyStoreStatus(storeDetail.storestatus, viewHolder);

		viewHolder.contact.setText(storeDetail.linkMan);
		viewHolder.phone.setText(storeDetail.linkPhone); //


		viewHolder.locate.setVisibility(View.GONE);
		viewHolder.locate2.setVisibility(View.GONE);

		viewHolder.getpostion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonTool.isFastDoubleClick()) {

					return;
				}

				EventBus.getDefault().post(new OnStoreMarkClickEvent(viewHolder.storeDetail));

			}
		});

		return view;
	}

	public MtqStore getSelectStoreDetail() {

		if (currentselect < 0 || currentselect > mlistdata.size() - 1)
			return null;

		return mlistdata.get(currentselect);

	}

	public int getCurrentselect() {
		return currentselect;
	}

	public void setCurrentselect(int currentselect) {
		this.currentselect = currentselect;
	}

	static class ViewHolder {
//        @BindView(R.id.tv_letter)
//        TextView tv_letter;

	//	ImageView image;
		TextView name;
		TextView address;
		TextView status;
		TextView time;
		TextView phone;
		TextView contact;
		TextView getpostion;
		TextView timeStatus; // 已完成 超时
		TextView locate;
		LinearLayout locate2;

		LinearLayout line;

		MtqStore storeDetail;
		int index;

		public ViewHolder(View view) {

			address = (TextView) view.findViewById(R.id.id_freight_point_address);
			name = (TextView) view.findViewById(R.id.id_freight_point_name);
			status = (TextView) view.findViewById(R.id.id_freight_point_status);
			time = (TextView) view.findViewById(R.id.id_freight_point_time);
			timeStatus = (TextView) view.findViewById(R.id.freight_time_status);
			//image = (ImageView) view.findViewById(R.id.freight_point_type_pic);

			line = (LinearLayout) view.findViewById(R.id.freigt_point_line);
			locate = (TextView) view.findViewById(R.id.btn_getposition2);
			locate2  = (LinearLayout) view.findViewById(R.id.btn_getposition);
			phone = (TextView) view.findViewById(R.id.tv_phone);
			contact = (TextView) view.findViewById(R.id.tv_contact);
			getpostion = (TextView) view.findViewById(R.id.tv_getposition);
		}
	}

	public void setList(List<MtqStore> result) {

		this.mlistdata = result;
	}


}