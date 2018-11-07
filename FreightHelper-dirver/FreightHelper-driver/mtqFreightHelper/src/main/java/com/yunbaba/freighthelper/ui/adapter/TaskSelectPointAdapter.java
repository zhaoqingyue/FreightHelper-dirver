package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

//选择运货点列表adapter
public class TaskSelectPointAdapter extends BaseAdapter {

	public ArrayList<MtqDeliStoreDetail> mlistdata;
	// HashMap<String, String> mlistdata;
	Context mContext;
	private int currentselect = 0;

	public TaskSelectPointAdapter(Context mContext, ArrayList<MtqDeliStoreDetail> mlistdata2) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.mlistdata = mlistdata2;
	}

	@Override
	public int getCount() {

		return mlistdata == null ? 0 : mlistdata.size();
	}

	@Override
	public MtqDeliStoreDetail getItem(int position) {

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
			view = LayoutInflater.from(mContext).inflate(R.layout.item_taskpoint, parent, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		}

		MtqDeliStoreDetail tmp = getItem(position);
		
		
		CharSequence storeName;
		if (position == 0)
			storeName = new SpannableString( FreightLogicTool
					.getStoreNameRecommend((position + 1) + mContext.getString(R.string.dot) +(
							TextUtils.isEmpty(tmp.storecode)?"":(tmp.storecode+"-"))+ tmp.storename));
		
		else
			storeName = new SpannableString((position + 1) + mContext.getString(R.string.dot) +(
					TextUtils.isEmpty(tmp.storecode)?"":(tmp.storecode+"-"))+ tmp.storename);

		
		if(tmp.storex == 0 && tmp.storey == 0)
			storeName =  new SpannableString(FreightLogicTool.getStoreNameNoPosition(storeName));
		
		
		if(tmp.optype == 4)
			storeName =  FreightLogicTool.getStoreNameAddBackPic(mContext, storeName);


		if(tmp.optype == 5)
			storeName =  FreightLogicTool.getStoreNameAddPassByPic(mContext, storeName);


		holder.tvTitle.setText(storeName);
		

		holder.tvAddress.setText((tmp.regionname + tmp.storeaddr).replaceAll("\\s*", ""));
		holder.tvContact.setText(tmp.linkman);
		holder.tvPhone.setText(tmp.linkphone);

		if (TextUtils.isEmpty(tmp.linkman)) {
			holder.pll2.setVisibility(View.GONE);
		} else {
			holder.pll2.setVisibility(View.VISIBLE);
		}

		if (TextUtils.isEmpty(tmp.linkphone)) {
			holder.pll3.setVisibility(View.GONE);
		} else {
			holder.pll3.setVisibility(View.VISIBLE);
		}

		if (currentselect == position)
			holder.ivSelect.setImageResource(R.drawable.icon_check_circle_yes);
		else
			holder.ivSelect.setImageResource(R.drawable.icon_check_circle_no);

		if (position == mlistdata.size() - 1)
			holder.vStroke.setVisibility(View.GONE);
		else
			holder.vStroke.setVisibility(View.VISIBLE);

		return view;
	}

	public MtqDeliStoreDetail getSelectStoreDetail() {

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

	class ViewHolder {
		@BindView(R.id.tv_title)
		TextView tvTitle;
		@BindView(R.id.iv_select)
		ImageView ivSelect;
		@BindView(R.id.tv_address)
		TextView tvAddress;
		@BindView(R.id.pll_taskpoint_detail_address)
		PercentLinearLayout pll1;
		@BindView(R.id.tv_contact)
		TextView tvContact;
		@BindView(R.id.pll_taskpoint_detail_kcode)
		PercentLinearLayout pll2;
		@BindView(R.id.tv_phone)
		TextView tvPhone;
		@BindView(R.id.pll3)
		PercentLinearLayout pll3;
		@BindView(R.id.v_stroke)
		View vStroke;

		ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}

}
