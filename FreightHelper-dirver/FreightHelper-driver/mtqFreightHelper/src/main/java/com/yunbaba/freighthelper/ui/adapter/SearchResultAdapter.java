package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.TimestampTool;
import com.yunbaba.ols.module.delivery.bean.MtqRequestTime;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultAdapter extends BaseAdapter {

	public List<MtqRequestTime> mlistdata;
	// HashMap<String, String> mlistdata;
	Context mContext;
	private int currentselect = 0;
    private boolean isRecent = true;
  
	
	public boolean isRecent() {
		return isRecent;
	}

	public void setRecent(boolean isRecent) {
		this.isRecent = isRecent;
	}

	public void RefreshList(boolean  isRecent){

		this.isRecent = isRecent;
		this.notifyDataSetChanged();
	}

	public SearchResultAdapter(Context mContext, List<MtqRequestTime> mlistdata2) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.mlistdata = mlistdata2;
	}

	@Override
	public int getCount() {

		return mlistdata == null ? 0 : mlistdata.size() ;
	}

	@Override
	public MtqRequestTime getItem(int position) {

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
			view = LayoutInflater.from(mContext).inflate(R.layout.item_search_result, parent, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		}

		
		MtqRequestTime tmp =  getItem(position);
		
		
//		switch (tmp.storestatus) {
//
//		case 0:
//		case 3:
//			holder.tvStatus.setText("待作业");
//			break;
//		case 1:
//			holder.tvStatus.setText("作业中");
//			break;
//		case 2:
//			holder.tvStatus.setText("已完成");
//			break;
//		default:
//			holder.tvStatus.setText("已完成");
//			break;
//
//		}
		
	//	holder.tvOrdernum.setText("单号:"+tmp.cust_orderid);
		
//		
//		
//		if(position == 0)
//		  holder.tvTitle.setText(FreightLogicTool.getStoreNameRecommend((position+1)+mContext.getString(R.string.dot)+tmp.storename));
//		else
//		  holder.tvTitle.setText((position+1)+mContext.getString(R.string.dot)+tmp.storename);
//		
//		
		
		
		
//		holder.tvAddress.setText(tmp.storeaddr);
//		holder.tvContact.setText(tmp.linkman);
//		holder.tvPhone.setText(tmp.linkphone);
//		
//		
    	//MLog.e("sendtime",""+GsonTool.getInstance().toJson(tmp));
		holder.tvTime.setText(TimestampTool.getMonthDay(tmp.req_time_e*1000L));
		
		
//		if(isRecent )
//			holder.ivRecent.setVisibility(View.VISIBLE);
//		else
//			holder.ivRecent.setVisibility(View.GONE);
		
		
		
		if(position == mlistdata.size()-1)
			holder.vStroke.setVisibility(View.GONE);
		else
			holder.vStroke.setVisibility(View.VISIBLE);
		
		

		
		return view;
	}

    public MtqRequestTime getSelectStoreDetail(){
    	
    	if(currentselect<0||currentselect>mlistdata.size()-1)
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
//        @BindView(R.id.tv_ordernum)
//        TextView tvOrdernum;
//        @BindView(R.id.iv_recent)
//        ImageView ivRecent;
       
        @BindView(R.id.tv_time)
        TextView tvTime;
      
        @BindView(R.id.v_stroke)
        View vStroke;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

	public void setList(List<MtqRequestTime> result) {

		this.mlistdata = result;
	}


}
