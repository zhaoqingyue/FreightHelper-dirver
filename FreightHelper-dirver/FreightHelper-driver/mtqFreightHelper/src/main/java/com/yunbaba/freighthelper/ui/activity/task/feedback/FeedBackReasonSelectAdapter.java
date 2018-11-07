package com.yunbaba.freighthelper.ui.activity.task.feedback;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBackInfo;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedBackReasonSelectAdapter extends BaseAdapter {

	ArrayList<FeedBackInfo> mlistdata;
	// HashMap<String, String> mlistdata;
	boolean[] selectArray;
	Context mContext;
	OnReansonChangeListner onReasonChange = null;

	public FeedBackReasonSelectAdapter(Context mContext, ArrayList<FeedBackInfo> mlistdata2,
			OnReansonChangeListner onReasonChange) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.mlistdata = mlistdata2;
		this.onReasonChange = onReasonChange;

		selectArray = new boolean[mlistdata.size()];
		Arrays.fill(selectArray, false);

	}

	@Override
	public int getCount() {

		return mlistdata == null ? 0 : mlistdata.size();
	}

	@Override
	public FeedBackInfo getItem(int position) {

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
		OnClick listener = null;
		if (view != null) {
			holder = (ViewHolder) view.getTag();
			listener = (OnClick) view.getTag(holder.tv_reason.getId());
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_feedback_reason_select, parent, false);
			holder = new ViewHolder(view);

			listener = new OnClick();// 在这里新建监听对象

			view.setTag(holder);

			view.setTag(holder.tv_reason.getId(), listener);
		}

		FeedBackInfo tmp = getItem(position);

		if (tmp == null && !TextUtils.isEmpty(tmp.name)) {

			holder.tv_reason.setText("unknown");
		} else {
			holder.tv_reason.setText(tmp.name);
		}

		listener.setPosition(position);
		holder.tv_reason.setOnClickListener(listener);
		if (isSelect(position)) {

			holder.tv_reason
					.setBackground(mContext.getResources().getDrawable(R.drawable.retangle_background_appcolor_white));
			holder.iv_reason.setVisibility(View.VISIBLE);
			holder.tv_reason.setTextColor(mContext.getResources().getColor(R.color.app_color2));

		} else {

			holder.tv_reason
					.setBackground(mContext.getResources().getDrawable(R.drawable.retangle_background_gray_white));
			holder.iv_reason.setVisibility(View.GONE);
			holder.tv_reason.setTextColor(mContext.getResources().getColor(R.color.black1));
		}

		// holder.tv_reason.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		//
		// }
		// });

		return view;
	}

	class OnClick implements OnClickListener {
		int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// MLog.d(TAG, list.get(position));
			select(position);

		}
	}

	public boolean isSelect(int position) {

		if (selectArray == null) {
			selectArray = new boolean[mlistdata.size()];
			Arrays.fill(selectArray, false);
		}

		return selectArray[position];
	}

	public void select(int position) {

		try {

			if (selectArray == null) {
				selectArray = new boolean[mlistdata.size()];
				Arrays.fill(selectArray, false);
			}

			selectArray[position] = !selectArray[position];

		} catch (Exception e) {

		}

		if (onReasonChange != null)
			onReasonChange.onChange();

		notifyDataSetChanged();

	}

	public String getSelectResult() {

		try {

			String res = "";

			for (int i = 0; i < mlistdata.size(); i++) {

				if (selectArray[i]) {

					if (TextUtils.isEmpty(res))
						res = mlistdata.get(i).name;
					else
						res = res + "|" + mlistdata.get(i).name;

				}

			}

			return res;

		} catch (Exception e) {

			return "";

		}
	}

	static class ViewHolder {

		@BindView(R.id.tv_reason)
		TextView tv_reason;

		@BindView(R.id.iv_reason)
		ImageView iv_reason;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

	}

	public interface OnReansonChangeListner {

		void onChange();
	}

}
