package com.yunbaba.freighthelper.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.KeywordUtil;
import com.yunbaba.ols.module.delivery.bean.MtqStore;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreListAdapter extends BaseAdapter implements SectionIndexer {

	private List<MtqStore> mList = null;
	private Context mContext;
	public static final int keycolor = Color.parseColor("#ef2c29");
	private int storetype;
	String tmpName;
	String tmpAddress;

	public StoreListAdapter(Context context, List<MtqStore> list, int type,int storetype) {
		mContext = context;
		mList = list;
		this.type = type;
		this.setStoretype(storetype);
	}

	public void updateListView(List<MtqStore> list) {
		mList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public MtqStore getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final MtqStore contacts = mList.get(position);

		ViewHolder viewHolder;

		if (view != null) {
			viewHolder = (ViewHolder) view.getTag();
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_storelist, parent, false);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		}

		
//		if (storetype == 3)
//			tmpName = (TextUtils.isEmpty(contacts.storeCode)?"":(contacts.storeCode+"-"))+contacts.linkMan.replaceAll("\\s*", "");
//		else
		tmpName = (TextUtils.isEmpty(contacts.storeCode)?"":(contacts.storeCode+"-"))+contacts.storeName.replaceAll("\\s*", "");
		
		if (TextUtils.isEmpty(contacts.first)) {
			if (TextUtils.isEmpty(tmpName)) {
				viewHolder.tv_first.setText("");
			} else {


//				if (storetype == 3)
//					viewHolder.tv_first.setText(contacts.linkMan.replaceAll("\\s*", "").trim().subSequence(0, 1));
//				else

				String strTmp = contacts.storeName.replaceAll("\\s*", "").trim();

				if (strTmp.length() > 0 ) {

					viewHolder.tv_first.setText(contacts.storeName.replaceAll("\\s*", "").trim().subSequence(0, 1));
				}

			}
		}

		else {
			viewHolder.tv_first.setText(contacts.first);
		}
		
	
		

		if (type == 1 && !TextUtils.isEmpty(keyword)) {

			viewHolder.tv_letter.setVisibility(View.GONE);
			viewHolder.v_line.setVisibility(View.VISIBLE);
			viewHolder.tv_store.setText(KeywordUtil.matcherSearchTitle(keycolor, tmpName, keyword));

		} else {

			int section = getSectionForPosition(position);
			if (position == getPositionForSection(section)) {
				viewHolder.tv_letter.setVisibility(View.VISIBLE);
				viewHolder.tv_letter.setText(contacts.letter);
			} else {
				viewHolder.tv_letter.setVisibility(View.GONE);
				viewHolder.v_line.setVisibility(View.VISIBLE);
			}

			viewHolder.tv_store.setText(tmpName);

		}

		if (!TextUtils.isEmpty(contacts.kCode)) {
			
			tmpAddress = (contacts.regionName+contacts.storeAddr).replaceAll("\\s*", "");
			

			if (type == 1 && !TextUtils.isEmpty(keyword)&&tmpAddress.contains(keyword) ) {
				
                 if(!TextUtils.isEmpty(tmpName)&&tmpName.contains(keyword)){
                	 
                	 
                	 
                	 viewHolder.tv_address.setText(tmpAddress);
                 }else
                	 viewHolder.tv_address.setText(KeywordUtil.matcherSearchTitle(keycolor, tmpAddress, keyword));
			} else {
				viewHolder.tv_address.setText(tmpAddress);
			}

			viewHolder.tv_address.setVisibility(View.VISIBLE);
			viewHolder.tv_mark.setVisibility(View.GONE);
			
			viewHolder.iv_arrow.setVisibility(View.VISIBLE);
		} else {
			viewHolder.tv_address.setVisibility(View.GONE);
			viewHolder.tv_mark.setVisibility(View.VISIBLE);
			viewHolder.iv_arrow.setVisibility(View.GONE);
		}
		// holder.ll_item.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// // Intent intent = new
		// // Intent(StoreListActivity.this,StoreDetailActivity.class);
		// //
		//
		// //
		// // startActivity(intent);
		//
		// if (position < getCount()) {
		//
		// Intent intent = new Intent(mContext, StoreDetailActivity.class);
		// // Bundle bundle = new Bundle();
		// // intent.putExtra("storeinfo",
		// // GsonTool.getInstance().toJson(contacts));
		//
		// intent.putExtra("store",
		// GsonTool.getInstance().toJson(getItem(position)));
		// // intent.putExtras(bundle);
		// mContext.startActivity(intent);
		// }
		// }
		// });

		return view;
	}

	static class ViewHolder {

		@BindView(R.id.tv_letter)
		TextView tv_letter;

		@BindView(R.id.tv_first)
		TextView tv_first;

		@BindView(R.id.tv_mark)
		TextView tv_mark;

		@BindView(R.id.tv_store)
		TextView tv_store;

		@BindView(R.id.tv_address)
		TextView tv_address;

		@BindView(R.id.v_line)
		View v_line;
		
		@BindView(R.id.iv_arrow)
		ImageView iv_arrow;

		@BindView(R.id.ll_item)
		PercentRelativeLayout ll_item;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mList.get(i).letter;
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return mList.get(position).letter.charAt(0);
	}

	int type = 0; // 0默认 1搜索
	String keyword;

	public void setData(List<MtqStore> mlist2, int i) {

		mList = mlist2;
		type = i;

	}

	public void setData(List<MtqStore> mlist2, int i, String keyword) {

		mList = mlist2;
		type = i;
		this.keyword = keyword;

	}

	public int getStoretype() {
		return storetype;
	}

	public void setStoretype(int storetype) {
		this.storetype = storetype;
	}

}