package com.yunbaba.freighthelper.ui.activity.car;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseMainActivity;
import com.yunbaba.freighthelper.bean.CustomDate;
import com.yunbaba.freighthelper.ui.fragment.car.SelectDateFragment;
import com.yunbaba.freighthelper.ui.fragment.car.SelectDateFragment.OnDateSelectListener;
import com.yunbaba.freighthelper.ui.fragment.car.SelectTimeFragment;

public class HistroyTravelActivity extends BaseMainActivity implements
		OnClickListener, OnDateSelectListener {

	private ImageView mBack;
	private TextView mTitle;
	private FrameLayout mFrameLayout;
	private RelativeLayout mRlChoiceDate;
	private RelativeLayout mRlChoiceTravel;

	private boolean mIsChoiceDate;

	private FragmentManager mFragmentManager;
	private Fragment mDateFragment;
	private Fragment mTravelFragment;

	@Override
	protected int getLayoutResID() {

		return R.layout.activity_histroy_travel;
	}

	@Override
	protected void initViews() {

		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);

		mRlChoiceDate = (RelativeLayout) findViewById(R.id.rl_history_travel_choice_date);
		mRlChoiceTravel = (RelativeLayout) findViewById(R.id.rl_history_travel_choice_travel);

		mIsChoiceDate = true;
		setSelectStatus(mIsChoiceDate);
		
		mRlChoiceTravel.setEnabled(false);

		mBack.setVisibility(View.VISIBLE);
		mTitle.setText("历史行程");
	}

	@Override
	protected void setListener() {

		mBack.setOnClickListener(this);
		mRlChoiceDate.setOnClickListener(this);
		mRlChoiceTravel.setOnClickListener(this);
	}

	@Override
	protected void initFragment(Bundle savedInstanceState) {

		mFragmentManager = getSupportFragmentManager();
		mFrameLayout = (FrameLayout) findViewById(R.id.fl_histroy_travel_fragment);
		mIsChoiceDate = true;
		loadFragment();
	}

	@Override
	protected void afterInit() {


	}

	private void loadFragment() {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		mDateFragment = new SelectDateFragment();
		((SelectDateFragment) mDateFragment).setOnDateSelectListener(this);
		transaction.add(R.id.fl_histroy_travel_fragment, mDateFragment);
		mTravelFragment = new SelectTimeFragment();
		transaction.add(R.id.fl_histroy_travel_fragment, mTravelFragment);
		transaction.commitAllowingStateLoss();
		setFragment();
	}

	private void setFragment() {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();

		if (mIsChoiceDate) {
			transaction.show(mDateFragment);
			transaction.hide(mTravelFragment);
		} else {
			transaction.show(mTravelFragment);
			transaction.hide(mDateFragment);
		}
		transaction.commitAllowingStateLoss();
	}

	private void setSelectStatus(boolean isChoiceDate) {
		mRlChoiceDate.setSelected(isChoiceDate);
		mRlChoiceTravel.setSelected(!isChoiceDate);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.rl_history_travel_choice_date:
			// 选择日期
			if (!mIsChoiceDate) {
				mIsChoiceDate = true;
				setSelectStatus(mIsChoiceDate);
				setFragment();
			}
			break;
		case R.id.rl_history_travel_choice_travel:
			// 选择行程
			if (mIsChoiceDate) {
				mIsChoiceDate = false;
				setSelectStatus(mIsChoiceDate);
				setFragment();
			}
			break;

		case R.id.title_left_img:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void dateSelect(CustomDate date) {

		mIsChoiceDate = false;
		mRlChoiceTravel.setEnabled(true);
		setSelectStatus(mIsChoiceDate);
		setFragment();
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

}
