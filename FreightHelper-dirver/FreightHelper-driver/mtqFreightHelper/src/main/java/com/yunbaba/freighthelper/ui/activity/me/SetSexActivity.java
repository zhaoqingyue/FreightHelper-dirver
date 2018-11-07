/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: ModifySexActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 设置性别
 * @author: zhaoqy
 * @date: 2017年4月5日 下午12:24:09
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.me;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;

public class SetSexActivity extends BaseActivity implements OnClickListener {
	private ImageView mBack;
	private TextView mTitle;
	private TextView mMale;
	private TextView mFemale;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_set_sex;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mMale = (TextView) findViewById(R.id.set_sex_male);
		mFemale = (TextView) findViewById(R.id.set_sex_female);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
		mMale.setOnClickListener(this);
		mFemale.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.modify_sex);
	}

	@Override
	protected void loadData() {
		int sex = getIntent().getIntExtra("sex", 2);
		switch (sex) {
		case 1: {
			mFemale.setSelected(true);
			Drawable drawable = getResources().getDrawable(
					R.drawable.userinfo_sex_selected);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			mFemale.setCompoundDrawables(null, null, drawable, null);

			mMale.setSelected(false);
			mMale.setCompoundDrawables(null, null, null, null);
			break;
		}
		case 2: {
			mMale.setSelected(true);
			Drawable drawable = getResources().getDrawable(
					R.drawable.userinfo_sex_selected);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			mMale.setCompoundDrawables(null, null, drawable, null);

			mFemale.setSelected(false);
			mFemale.setCompoundDrawables(null, null, null, null);
			break;
		}
		default:
			break;
		}
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
		case R.id.set_sex_male: {
			if (!mMale.isSelected()) {
				mFemale.setSelected(false);
				mFemale.setCompoundDrawables(null, null, null, null);

				mMale.setSelected(true);
				Drawable drawable = getResources().getDrawable(
						R.drawable.userinfo_sex_selected);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mMale.setCompoundDrawables(null, null, drawable, null);

				Intent intent = getIntent();
				intent.putExtra("sex", 2);
				setResult(RESULT_OK, intent);
				finish();
			}
			break;
		}
		case R.id.set_sex_female: {
			if (!mFemale.isSelected()) {
				mMale.setSelected(false);
				mMale.setCompoundDrawables(null, null, null, null);

				mFemale.setSelected(true);
				Drawable drawable = getResources().getDrawable(
						R.drawable.userinfo_sex_selected);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				mFemale.setCompoundDrawables(null, null, drawable, null);

				Intent intent = getIntent();
				intent.putExtra("sex", 1);
				setResult(RESULT_OK, intent);
				finish();
			}
			break;
		}
		default:
			break;
		}
	}

	@Override
	protected void messageEvent(AccountEvent event) {
		switch (event.msgId) {
		default:
			break;
		}
	}
}
