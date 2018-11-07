package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.TimestampTool;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqExaminationUnit;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CarCheckHistoryAdapter extends BaseAdapter {

	ArrayList<MtqExaminationUnit> mlistdata;
	// HashMap<String, String> mlistdata;
	Context mContext;

	public CarCheckHistoryAdapter(Context mContext, ArrayList<MtqExaminationUnit> mlistdata2) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.mlistdata = mlistdata2;
	}

	@Override
	public int getCount() {

		return mlistdata == null ? 0 : mlistdata.size();
	}

	@Override
	public MtqExaminationUnit getItem(int position) {

		// if()
		return mlistdata == null ? null : mlistdata.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {


		ViewHolder holder;

		if (view != null) {
			holder = (ViewHolder) view.getTag();
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_carcheck_history, parent, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		}

		MtqExaminationUnit tmp = getItem(position);

		holder.time.setText(TimestampTool.TimeToString(tmp.time*1000L));

		String[] error = tmp.errvalue.split(",");

		if (TextUtils.isEmpty(tmp.errvalue)||error == null || error.length == 0) {
			holder.state.setText("正常");
			holder.state.setTextColor(mContext.getResources().getColor(R.color.app_color));

		} else {

			holder.state.setText("异常");
			holder.state.setTextColor(mContext.getResources().getColor(R.color.red));
		}

		return view;
	}

	static class ViewHolder {

		@BindView(R.id.tv_time)
		TextView time;

		@BindView(R.id.tv_state)
		TextView state;

		@BindView(R.id.ll_item)
		PercentRelativeLayout item;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

	}
}
