package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.CarCheckResultBean;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.Examinationdetail;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CarCheckResultAdapter extends BaseAdapter {

	ArrayList<Examinationdetail> mlistdata;
	ArrayList<CarCheckResultBean> reslist;

	// HashMap<String, String> mlistdata;
	Context mContext;
	public static final int ItemNum = 4;

	public CarCheckResultAdapter(Context mContext, ArrayList<Examinationdetail> mlistdata2) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.mlistdata = mlistdata2;

		init();

	}

	public void setData(ArrayList<Examinationdetail> mlistdata2) {
		this.mlistdata = mlistdata2;

		init();
	}

	public void setSpread(boolean isSpread, int item) {

		if (reslist == null)
			init();

		if (reslist != null && reslist.size() > item) {

			if (reslist.get(item) != null) {

				reslist.get(item).isSpread = isSpread;

			}

		}
	}

	public void init() {

		reslist = new ArrayList<>();
		CarCheckResultBean bean = null;

		for (int i = 0; i < 4; i++) {
			switch (i) {
			case 0:
				bean = new CarCheckResultBean(R.drawable.img_engine_normal, R.drawable.img_engine_error, "发动机系统");
				break;
			case 1:
				bean = new CarCheckResultBean(R.drawable.img_brake_normal, R.drawable.img_brake_error, "电子刹车系统");
				break;
			case 2:
				bean = new CarCheckResultBean(R.drawable.img_control_normal, R.drawable.img_control_error, "车身控制系统");
				break;
			case 3:
				bean = new CarCheckResultBean(R.drawable.img_other_normal, R.drawable.img_other_error, "其他");
				break;
			default:
				bean = new CarCheckResultBean(R.drawable.img_other_normal, R.drawable.img_other_error, "其他");
				break;
			}

			reslist.add(bean);
		}

		// faulttype 系统类型（0：发动机；1：变速箱；2：刹车；3：气囊；4：仪表板5：车身控制；6：空调；100：其它）

		for (Examinationdetail res : mlistdata) {

			switch (res.faulttype) {
			case 0:

				bean = reslist.get(0);
				bean.isError = true;
				bean.desc = res.faultname;

				break;
			case 2:
				bean = reslist.get(1);
				bean.isError = true;
				bean.desc = res.faultname;
				break;
			case 5:
				bean = reslist.get(2);
				bean.isError = true;
				bean.desc = res.faultname;
				break;

			default:

				bean = reslist.get(3);
				bean.isError = true;

				if (TextUtils.isEmpty(bean.desc)) {

					bean.desc += res.faultname;
				} else {
					bean.desc += "\n";
					bean.desc += res.faultname;
				}

				break;
			}

		}

	}

	@Override
	public int getCount() {

		return reslist.size();
	}

	@Override
	public CarCheckResultBean getItem(int position) {

		// if()
		// CarCheckResultBean bean = null;

		// switch (position) {
		// case 0:
		// bean = new CarCheckResultBean(R.drawable.img_engine_normal,
		// R.drawable.img_engine_error, "发动机系统");
		// break;
		// case 1:
		// bean = new CarCheckResultBean(R.drawable.img_brake_normal,
		// R.drawable.img_brake_error, "电子刹车系统");
		// break;
		// case 2:
		// bean = new CarCheckResultBean(R.drawable.img_control_normal,
		// R.drawable.img_control_error, "车身控制系统");
		// break;
		// case 3:
		// bean = new CarCheckResultBean(R.drawable.img_other_normal,
		// R.drawable.img_other_error, "其他");
		// break;
		// default:
		// bean = new CarCheckResultBean(R.drawable.img_other_normal,
		// R.drawable.img_other_error, "其他");
		// break;
		// }
		// reslist

		return reslist.get(position);
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
			view = LayoutInflater.from(mContext).inflate(R.layout.item_carcheck, parent, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		}

		CarCheckResultBean tmp = getItem(position);

		holder.tvPart.setText(tmp.ItemName);

		if (tmp.isError) {
			holder.ivPart.setImageResource(tmp.ErrorResId);
			holder.tvState.setText("异常");
			holder.tvState.setTextColor(mContext.getResources().getColor(R.color.red));

			if (tmp.isSpread) {
				holder.tvDesc.setVisibility(View.VISIBLE);

				holder.tvDesc.setText(tmp.desc);
			} else {
				holder.tvDesc.setVisibility(View.GONE);
				holder.tvDesc.setText(tmp.desc);
			}
		}

		else {
			holder.ivPart.setImageResource(tmp.NormalResId);
			holder.tvState.setText("正常");
			holder.tvState.setTextColor(mContext.getResources().getColor(R.color.app_color));
			holder.tvDesc.setVisibility(View.GONE);
		}

		return view;
	}

	static class ViewHolder {

		@BindView(R.id.iv_part)
		ImageView ivPart;

		@BindView(R.id.tv_part)
		TextView tvPart;

		@BindView(R.id.tv_desc)
		TextView tvDesc;

		@BindView(R.id.tv_state)
		TextView tvState;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

	}

}
