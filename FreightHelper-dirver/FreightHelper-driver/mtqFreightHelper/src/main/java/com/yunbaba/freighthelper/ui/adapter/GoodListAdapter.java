package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.GoodInfo;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoodListAdapter extends BaseAdapter {

//	ArrayList<GoodInfo> mlistdata;
	// HashMap<String, String> mlistdata;
	Context mContext;
	MtqDeliOrderDetail mlistdata;
//	public GoodListAdapter(Context mContext, ArrayList<GoodInfo> mlistdata2) {
//		// TODO Auto-generated constructor stub
//		this.mContext = mContext;
//		this.mlistdata = mlistdata2;
//	}

	public GoodListAdapter(Context mContext, MtqDeliOrderDetail mlistdata2) {
//		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.mlistdata = mlistdata2;
	}

	@Override
	public int getCount() {

		return 4;
		//mlistdata == null ? 0 : mlistdata.size()
	}

	@Override
	public GoodInfo getItem(int position) {

		// if()
		return null;//mlistdata == null ? null : mlistdata.get(position);
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
			view = LayoutInflater.from(mContext).inflate(R.layout.item_good, parent, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		}

		//GoodInfo tmp = getItem(position);

		switch (position){



			case 0:
				holder.goodname.setText("总件数: ");
				holder.goodnum.setText(mlistdata.piececount+"件");

				holder.goodname2.setText("总件数: ");
				holder.goodnum2.setText(mlistdata.piececount+"件");
				break;
			case 1:
				holder.goodname.setText("总箱数: ");
				holder.goodnum.setText(""+mlistdata.framecount+"箱");

				holder.goodname2.setText("总箱数: ");
				holder.goodnum2.setText(""+mlistdata.framecount+"箱");
				break;
			case 2:
				holder.goodname.setText("总体积: ");
				holder.goodname2.setText("总体积: ");
				BigDecimal bg = new BigDecimal(mlistdata.volume);
				double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

				holder.goodnum.setText(""+getPrettyNumber(f1)+"方");
				holder.goodnum2.setText(""+getPrettyNumber(f1)+"方");
				break;
			case 3:
				holder.goodname.setText("总重量: ");
				holder.goodname2.setText("总重量: ");

				BigDecimal bg2 = new BigDecimal(mlistdata.weight);
				double f2 = bg2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

				holder.goodnum.setText(getPrettyNumber(f2)+"吨");
				holder.goodnum2.setText(getPrettyNumber(f2)+"吨");
				break;
//			case 4:
//				break;


		}



		if(position%2 ==  1){
			holder.goodname.setVisibility(View.GONE);
			holder.goodnum.setVisibility(View.GONE);
			holder.goodname2.setVisibility(View.VISIBLE);
			holder.goodnum2.setVisibility(View.VISIBLE);
		}else {

			holder.goodname.setVisibility(View.VISIBLE);
			holder.goodnum.setVisibility(View.VISIBLE);
			holder.goodname2.setVisibility(View.GONE);
			holder.goodnum2.setVisibility(View.GONE);

		}


//
//		if (TextUtils.isEmpty(tmp.Name)) {
//
//			holder.goodname.setText(tmp.Name);
//		}else{
//
//			holder.goodname.setText("");
//		}
//
//
//		if (TextUtils.isEmpty(tmp.Num)) {
//
//			holder.goodnum.setText(tmp.Num);
//		}else{
//
//			holder.goodnum.setText("");
//		}

//		String unit = "";
//
//		if(!TextUtils.isEmpty(tmp.unit)&&!"无".equals(tmp.unit))
//			unit = tmp.unit;
		
		
	//	holder.goodnum.setText(tmp.amount + unit);

		// holder.tv_name.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		//
		// }
		// });

		return view;
	}


	public String getPrettyNumber(double number) {

		if(number == 0)
			return "0";

		String str = number+"";
		try {
			str = BigDecimal.valueOf(number)
					.stripTrailingZeros().toPlainString();
		}catch (Exception e){



		}
		return  str;

	}

	static class ViewHolder {

		@BindView(R.id.tv_goodname)
		TextView goodname;

		@BindView(R.id.tv_goodnum)
		TextView goodnum;

		@BindView(R.id.tv_goodname2)
		TextView goodname2;

		@BindView(R.id.tv_goodnum2)
		TextView goodnum2;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

	}

}
