/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: ContactFragment.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.fragment
 * @Description: 通讯录fragment
 * @author: zhaoqy
 * @date: 2017年3月11日 下午3:41:03
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.api.car.CarAPI;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseMainFragment;
import com.yunbaba.freighthelper.bean.eventbus.NewMsgEvent;
import com.yunbaba.freighthelper.ui.activity.contacts.ContactsActivity;
import com.yunbaba.freighthelper.utils.MessageId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ContactFragment extends BaseMainFragment implements
		OnClickListener {

	private static final String TAG = "ContactFragment";
	private TextView mTitle;
	private ImageView mNewsImg;
	private TextView mCompany;
	private TextView mStore;
	private TextView mDelivery;
	private TextView mCustomer;
	private int mLength;

	public static ContactFragment newInstance() {

		Bundle args = new Bundle();
		ContactFragment fragment = new ContactFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contacts, container,
				false);

		initViews(view);
		setListener(view);
		initData();
		loadData();
		updateUI();
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			updateUI();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateUI();
	}

	private void initViews(View view) {
		mTitle = (TextView) view.findViewById(R.id.title_text);
		mNewsImg = (ImageView) view.findViewById(R.id.title_right_img);
		mCompany = (TextView) view.findViewById(R.id.contacts_company_name);
		mStore = (TextView) view.findViewById(R.id.contacts_store_name);
		mDelivery = (TextView) view.findViewById(R.id.contacts_delivery_name);
		mCustomer = (TextView) view.findViewById(R.id.contacts_customer_name);
	}

	private void setListener(View view) {
		mNewsImg.setOnClickListener(this);
		view.findViewById(R.id.contacts_company).setOnClickListener(this);
		view.findViewById(R.id.contacts_store).setOnClickListener(this);
		view.findViewById(R.id.contacts_delivery).setOnClickListener(this);
		view.findViewById(R.id.contacts_customer).setOnClickListener(this);
	}

	private void initData() {
		mTitle.setText(R.string.main_bottom_contacts);
		mNewsImg.setVisibility(View.VISIBLE);
	}

	private void loadData() {
		
	}

	private void updateUI() {
		updateNews();
		String str = getResources().getString(R.string.contacts_company_format);
		mLength = CarAPI.getInstance().getMyGroups().size();
		//MLog.e(TAG, "mLength: " + mLength);
		String companyStr = String.format(str, mLength);
		mCompany.setText(companyStr);
		mStore.setText(R.string.contacts_store);
		mDelivery.setText(R.string.contacts_delivery);
		mCustomer.setText(R.string.contacts_customer);
	}
	
	private void updateNews() {
//		if (MsgManager.getInstance().hasUnReadMsg()) {
//			mNewsImg.setImageResource(R.drawable.msg_icon_news);
//		} else {
//			mNewsImg.setImageResource(R.drawable.msg_icon);
//		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_right_img: {
//			Intent intent = new Intent(getActivity(), MsgActivity.class);
//			startActivity(intent);
			break;
		}
		case R.id.contacts_company: {
			if (mLength > 0) {
				Intent intent = new Intent(getActivity(),
						ContactsActivity.class);
				startActivity(intent);
			}
			break;
		}
		case R.id.contacts_store: {
			break;
		}
		case R.id.contacts_delivery: {
			break;
		}
		case R.id.contacts_customer: {
			break;
		}
		default:
			break;
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(NewMsgEvent event) {
		switch (event.msgId) {
		case MessageId.MSGID_MSG_NEW: {
			updateNews();
			break;
		}
		default:
			break;
		}
	}

	/*
	 * @Override public void onItemClick(AdapterView<?> parent, View view, int
	 * position, long id) {
	 * 
	 * ContactsTypeInfo contactsType = (ContactsTypeInfo) mAdapter
	 * .getItem(position);
	 * 
	 * Intent intent = new Intent(getActivity(), ContactsActivity.class); Bundle
	 * bundle = new Bundle(); bundle.putParcelable("contactsType",
	 * contactsType); intent.putExtras(bundle); startActivity(intent); }
	 */
}
