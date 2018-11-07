/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: ContactsActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity
 * @Description: 通讯录列表Activity
 * @author: zhaoqy
 * @date: 2017年3月11日 下午3:43:36
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunbaba.api.car.CarAPI;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.ContactsInfo;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.ui.adapter.ContactsAdapter;
import com.yunbaba.freighthelper.ui.customview.ClearEditText;
import com.yunbaba.freighthelper.ui.customview.SideBar;
import com.yunbaba.freighthelper.ui.customview.SideBar.OnTouchingLetterChangedListener;
import com.yunbaba.freighthelper.utils.CharacterParser;
import com.yunbaba.freighthelper.utils.PinyinComparator;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliGroup;

public class ContactsActivity extends BaseActivity
		implements OnClickListener, OnTouchingLetterChangedListener, TextWatcher {

	private ImageView mBack;
	private TextView mTitle;
	private ClearEditText mClear;
	private ListView mListView;
	private SideBar mSideBar;
	private TextView mSelected;
	private ContactsAdapter mAdapter;
	private List<ContactsInfo> mContactsList;
	private CharacterParser mParser;
	private PinyinComparator mComparator;
	private List<String> mLetters;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_contacts;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mClear = (ClearEditText) findViewById(R.id.contacts_search);
		mListView = (ListView) findViewById(R.id.contacts_listview);
		mSideBar = (SideBar) findViewById(R.id.contacts_sidrbar);
		mSelected = (TextView) findViewById(R.id.contacts_selected);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
		mClear.addTextChangedListener(this);
		mSideBar.setOnTouchingLetterChangedListener(this);
	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.contacts_company);
		mParser = CharacterParser.getInstance();
		mComparator = new PinyinComparator();
		mSideBar.setTextView(mSelected);
	}

	@Override
	protected void loadData() {
		List<CldDeliGroup> deliGroupList = CarAPI.getInstance().getMyGroups();
		if (deliGroupList != null && !deliGroupList.isEmpty()) {
			mContactsList = transformDeliGroup(deliGroupList);
			Collections.sort(mContactsList, mComparator);
			mAdapter = new ContactsAdapter(this, mContactsList);
			mListView.setAdapter(mAdapter);
			setLetters();
		}
	}

	private void setLetters() {
		mLetters = new ArrayList<String>();
		List<String> letter = new ArrayList<String>();
		/**
		 * 获取通讯录首字母
		 */
		int len = mContactsList.size();
		for (int i = 0; i < len; i++) {
			ContactsInfo contacts = mContactsList.get(i);
			letter.add(contacts.getLetters());
		}

		/**
		 * 去掉重复字母
		 */
		mLetters.add(letter.get(0));
		for (int i = 1; i < len; i++) {
			if (!letter.get(i).equalsIgnoreCase(letter.get(i - 1))) {
				mLetters.add(letter.get(i));
			}
		}
		mSideBar.setLetters(mLetters);
	}

	@Override
	protected void updateUI() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img: {
			finish();
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		filterContacts(s.toString());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void onTouchingLetterChanged(String s) {
		int position = mAdapter.getPositionForSection(s.charAt(0));
		if (position != -1) {
			mListView.setSelection(position);
		}
	}

	@SuppressLint("DefaultLocale")
	private List<ContactsInfo> transformDeliGroup(List<CldDeliGroup> deliGroupList) {
		List<ContactsInfo> list = new ArrayList<ContactsInfo>();
		if (deliGroupList != null && !deliGroupList.isEmpty()) {
			int len = deliGroupList.size();
			for (int i = 0; i < len; i++) {
				CldDeliGroup deliGroup = deliGroupList.get(i);
				ContactsInfo contactsInfo = new ContactsInfo();

				if (!TextUtils.isEmpty(deliGroup.corpName))
					contactsInfo.setFirst(deliGroup.corpName.subSequence(0, 1).toString());
				else
					contactsInfo.setFirst("");
				contactsInfo.setName(deliGroup.corpName);
				contactsInfo.setTeam(deliGroup.groupName);
				contactsInfo.setPhone(deliGroup.mobile);

				String pinyin = mParser.getSelling(deliGroup.corpName);
				String sortString = pinyin.substring(0, 1).toUpperCase();
				if (sortString.matches("[A-Z]")) {
					contactsInfo.setLetters(sortString.toUpperCase());
				} else {
					contactsInfo.setLetters("#");
				}
				list.add(contactsInfo);
			}
		}
		return list;
	}

	@SuppressLint("DefaultLocale")
	private List<ContactsInfo> filledData(String[] date) {
		List<ContactsInfo> list = new ArrayList<ContactsInfo>();
		for (int i = 0; i < date.length; i++) {
			ContactsInfo contactsInfo = new ContactsInfo();

			if (!TextUtils.isEmpty(date[i]))
				contactsInfo.setFirst(date[i].subSequence(0, 1).toString());
			else
				contactsInfo.setFirst("");
			contactsInfo.setName(date[i]);
			String pinyin = mParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			if (sortString.matches("[A-Z]")) {
				contactsInfo.setLetters(sortString.toUpperCase());
			} else {
				contactsInfo.setLetters("#");
			}

			list.add(contactsInfo);
		}
		return list;
	}

	private void filterContacts(String filterStr) {
		List<ContactsInfo> filterDateList = new ArrayList<ContactsInfo>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = mContactsList;
		} else {
			filterDateList.clear();
			for (ContactsInfo contacts : mContactsList) {
				String name = contacts.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| mParser.getSelling(name).startsWith(filterStr.toString())) {
					filterDateList.add(contacts);
				}
			}
		}

		if (filterDateList != null) {
			Collections.sort(filterDateList, mComparator);
			mAdapter.updateListView(filterDateList);
		}
	}

	@Override
	protected void messageEvent(AccountEvent event) {

	}
}
