/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: ContactsAdapter.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.adapter
 * @Description: 通讯录adapter
 * @author: zhaoqy
 * @date: 2017年3月11日 下午3:44:17
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.ContactsInfo;
import com.yunbaba.freighthelper.ui.activity.contacts.ContactsDetailActivity;

import java.util.List;

public class ContactsAdapter extends BaseAdapter implements SectionIndexer {

	private List<ContactsInfo> mList = null;
	private Context mContext;

	public ContactsAdapter(Context context, List<ContactsInfo> list) {
		mContext = context;
		mList = list;
	}

	public void updateListView(List<ContactsInfo> list) {
		mList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ContactsInfo contacts = mList.get(position);
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.layout_contacts_item, null);
			viewHolder.letter = (TextView) view
					.findViewById(R.id.id_contacts_item_letter);
			viewHolder.layout = view
					.findViewById(R.id.id_contacts_item_layout);
			viewHolder.first = (TextView) view
					.findViewById(R.id.id_contacts_item_first);
			viewHolder.name = (TextView) view
					.findViewById(R.id.id_contacts_item_name);
			viewHolder.line = (TextView) view
					.findViewById(R.id.id_contacts_item_line);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		int section = getSectionForPosition(position);
		if (position == getPositionForSection(section)) {
			viewHolder.letter.setVisibility(View.VISIBLE);
			viewHolder.letter.setText(contacts.getLetters());
		} else {
			viewHolder.letter.setVisibility(View.GONE);
			viewHolder.line.setVisibility(View.VISIBLE);
		}

		viewHolder.first.setText(contacts.getFirst());
		viewHolder.name.setText(contacts.getName());
		viewHolder.layout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(mContext, ContactsDetailActivity.class);
				//Bundle bundle = new Bundle();
			//	bundle.putExParcelable("contactsInfo", contacts);
				intent.putExtra("contactsInfo", contacts);
				mContext.startActivity(intent);
			}});

		return view;
	}

	final static class ViewHolder {
		TextView letter;
		View layout;
		TextView first;
		TextView name;
		TextView line;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mList.get(i).getLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return mList.get(position).getLetters().charAt(0);
	}
}
