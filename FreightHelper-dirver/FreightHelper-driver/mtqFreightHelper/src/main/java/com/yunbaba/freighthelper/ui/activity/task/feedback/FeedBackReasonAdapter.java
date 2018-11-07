package com.yunbaba.freighthelper.ui.activity.task.feedback;

import android.widget.BaseAdapter;

import java.util.ArrayList;

import com.yunbaba.freighthelper.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedBackReasonAdapter extends BaseAdapter {

	ArrayList<String> mlistdata;
	// HashMap<String, String> mlistdata;
	Context mContext;

	public FeedBackReasonAdapter(Context mContext, ArrayList<String> mlistdata2) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.mlistdata = mlistdata2;
	}

	@Override
	public int getCount() {

		return mlistdata == null ? 0 : mlistdata.size();
	}

	@Override
	public String getItem(int position) {

		// if()
		return mlistdata == null ? null : mlistdata.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	public void setData(ArrayList<String> mlistdata2) {
		// TODO Auto-generated constructor stub

		this.mlistdata = mlistdata2;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {


		ViewHolder holder;

		if (view != null) {
			holder = (ViewHolder) view.getTag();
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_feedback_reason, parent, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		}

		String tmp = getItem(position);

		if (tmp == null) {

			holder.tv_reason.setText("unknown");
		} else {
			holder.tv_reason.setText(tmp);
		}

		// if (position == mlistdata.size() - 1)
		// holder.v_stroke.setVisibility(View.GONE);
		// else
		// holder.v_stroke.setVisibility(View.VISIBLE);
		//
		// // holder.tv_name.setOnClickListener(new OnClickListener() {
		// //
		// // @Override
		// // public void onClick(View v) {
		// //
		// //
		// // }
		// // });

		return view;
	}

	static class ViewHolder {

		@BindView(R.id.tv_reason)
		TextView tv_reason;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

	}

}
