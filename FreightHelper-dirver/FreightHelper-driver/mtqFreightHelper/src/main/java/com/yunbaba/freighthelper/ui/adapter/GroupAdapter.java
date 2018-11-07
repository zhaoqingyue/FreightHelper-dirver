package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.CorpBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupAdapter extends BaseAdapter {

	ArrayList<CorpBean> mlistdata;
	// HashMap<String, String> mlistdata;
	Context mContext;

	public GroupAdapter(Context mContext, ArrayList<CorpBean> mlistdata2) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.mlistdata = mlistdata2;
	}

	@Override
	public int getCount() {

		return mlistdata == null ? 0 : mlistdata.size() ;
	}

	@Override
	public CorpBean getItem(int position) {

		// if()
		return mlistdata==null?null:mlistdata.get(position);
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
			view = LayoutInflater.from(mContext).inflate(R.layout.item_companylist, parent, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		}

		
		CorpBean tmp =  getItem(position);
		
		if(tmp.getCorpName() == null){
			
			holder.tv_name.setText(mContext.getString(R.string.all));
		}else{
			holder.tv_name.setText(tmp.getCorpName());
		}
		
		if(position == mlistdata.size()-1)
			holder.v_stroke.setVisibility(View.GONE);
		else
			holder.v_stroke.setVisibility(View.VISIBLE);
		
//		holder.tv_name.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//
//				
//			}
//		});
		
		return view;
	}

	static class ViewHolder {

		@BindView(R.id.tv_name)
		TextView tv_name;

		@BindView(R.id.v_stroke)
		View v_stroke;

		

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

	}

}
