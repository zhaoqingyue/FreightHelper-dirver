/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: AreaActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 区域选择界面
 * @author: zhaoqy
 * @date: 2017年4月6日 上午9:10:29
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.me;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.location.CldLocation;
import com.cld.location.ICldLocationListener;
import com.yunbaba.api.map.LocationAPI;
import com.yunbaba.api.map.LocationAPI.MTQLocationMode;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.UserInfo;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.manager.UserManager;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.ui.adapter.ExpendAdapter;
import com.yunbaba.freighthelper.utils.MLog;

import java.util.ArrayList;

import cld.navi.region.CldRegionEx;
import cld.navi.region.CldRegionEx.CityLevel;
import cld.navi.region.CldRegionEx.ProvinceLevel;

public class AreaActivity extends BaseActivity implements OnClickListener,
		OnGroupClickListener, OnChildClickListener {

	private Context mContext;
	private ImageView mBack;
	private TextView mTitle;
	private TextView mCurArea;
	private ImageView mLocation;
	private ImageView mLocating;
	private ExpandableListView mListView;
	private ExpendAdapter mAdapter;
	private RotateAnimation mAnim;
	private UserInfo mUserInfo;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_area;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mCurArea = (TextView) findViewById(R.id.area_cur_area);
		mLocation = (ImageView) findViewById(R.id.area_location);
		mLocating = (ImageView) findViewById(R.id.area_locating);
		mListView = (ExpandableListView) findViewById(R.id.area_list);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
		mLocation.setOnClickListener(this);
		mListView.setOnGroupClickListener(this);
		mListView.setOnChildClickListener(this);
	}

	@Override
	protected void initData() {
		mContext = this;
		mBack.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.area_title);
		mUserInfo = UserManager.getInstance().getUserInfo();
		mAdapter = new ExpendAdapter(this);
		mListView.setAdapter(mAdapter);
		initAnimation();
	}

	private void initAnimation() {
		Interpolator _Interpolator = new LinearInterpolator();
		mAnim = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mAnim.setInterpolator(_Interpolator);
		mAnim.setDuration(650);
		mAnim.setRepeatCount(Integer.MAX_VALUE);
		mAnim.setFillAfter(true);
	}

	@Override
	protected void loadData() {
	}

	@Override
	protected void updateUI() {
		String area = mUserInfo.getAddress();
		mCurArea.setText(area);
	}

	@Override
	protected void messageEvent(AccountEvent event) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img: {
			back();
			break;
		}
		case R.id.area_location: {
			onLocation();
			break;
		}
		default:
			break;
		}
	}

	@SuppressLint("NewApi") private void onLocation() {
		/*try {
			startLocation();
		} catch (Exception e) {

		}*/
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// 在6.0 系统中请求某些权限需要检查权限
			if (!hasPermission()) {
				// 动态请求拍照权限
				requestPermissions(
						new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
						223);
			} else {
				startLocation();
			}
		} else {
			startLocation();
		}
	}

	@SuppressLint("NewApi") private boolean hasPermission() {
		String permission = Manifest.permission.ACCESS_FINE_LOCATION;
		if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
			return false;
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		switch (requestCode) {
		case 223:
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission Granted
				startLocation();
			} else {
				// Permission Denied
			}
			break;
		default:
			super.onRequestPermissionsResult(requestCode, permissions,
					grantResults);
		}
	}

	private void startLocation() {
		if (!CldPhoneNet.isNetConnected()) {
			Toast.makeText(this, "无法获取你的位置信息。",
					Toast.LENGTH_SHORT).show();
		} else {
			String text = getResources().getString(R.string.area_location);
			mCurArea.setText(text);
			startLocatAnim();
			location();
		}
	}

	private void location() {
		MLog.e("test", " ++++ location ++++ ");
		
		LocationAPI.getInstance().location(MTQLocationMode.NETWORK, 3000, this)
				.setLinster(new ICldLocationListener() {

					@Override
					public void onReceiveLocation(CldLocation location) {
						MLog.e("test", " location: " + location);
						LocationAPI.getInstance().stop();
						onLocateSuccess(location);
					}
				});
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		ArrayList<ProvinceLevel> provinceList = mAdapter.getGroupList();
		if (provinceList != null && !provinceList.isEmpty()) {
			String address = "";
			ProvinceLevel province = provinceList.get(groupPosition);
			int provinceid = province.id;
			if (!CldRegionEx.getInstance().isMunicipality(provinceid)
					&& !CldRegionEx.getInstance().isSpecialDistrict(provinceid)) {
				address = province.name + province.name_sufix;
				ArrayList<CityLevel> cityList = CldRegionEx.getInstance()
						.getCityListByProvinceId(provinceid);
				String city = "";
				if (cityList != null && !cityList.isEmpty()) {
					city = cityList.get(childPosition).name;
				}
				setAddress(address + city);
			}
		}
		return false;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		ArrayList<ProvinceLevel> provinceList = mAdapter.getGroupList();
		if (provinceList != null && !provinceList.isEmpty()) {
			String address = "";
			ProvinceLevel province = provinceList.get(groupPosition);
			int provinceid = province.id;
			if (CldRegionEx.getInstance().isMunicipality(provinceid)) {
				address = province.name + province.name_sufix;
				setAddress(address);
			} else if (CldRegionEx.getInstance().isSpecialDistrict(provinceid)) {
				address = province.name + province.name_sufix;
				setAddress(address);
			}
		}
		return false;
	}

	/**
	 * 开始定位动画
	 */
	private void startLocatAnim() {
		mLocation.setVisibility(View.GONE);
		mLocating.setVisibility(View.VISIBLE);
		mLocating.startAnimation(mAnim);
	}

	/**
	 * 停止定位动画
	 */
	private void stopLocatAnim() {
		mLocation.setVisibility(View.VISIBLE);
		mLocating.setVisibility(View.GONE);
		mLocating.clearAnimation();
	}

	protected void onLocateSuccess(CldLocation location) {
		String address = "";
		if (location != null && location.getErrCode() == 0) { 
			String province = location.getProvince();
			if (!TextUtils.isEmpty(province)) {
				ProvinceLevel provinceItem = CldRegionEx.getInstance()
						.searchProvince(province);
				if (CldRegionEx.getInstance().isMunicipality(provinceItem.id)
						|| CldRegionEx.getInstance().isSpecialDistrict(
								provinceItem.id)) {
					address = province;
				} else {
					address = province + location.getCity();
				}
			}
		} else {
			Toast.makeText(mContext, R.string.account_locate_failed,
					Toast.LENGTH_SHORT).show();
			address = mUserInfo.getAddress();
		}

		stopLocatAnim();
		if (mCurArea != null) {
			mCurArea.setText(address);
		}
	}

	@Override
	public void onBackPressed() {
		back();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void back() {
		LocationAPI.getInstance().stop();
		if (mCurArea != null) {
			String address = mCurArea.getText().toString();
			String hint = getResources().getString(R.string.area_location);
			if (!address.equals(mUserInfo.getAddress())
					&& !address.equals(hint)) {
				Intent intent = getIntent();
				intent.putExtra("address", address);
				setResult(RESULT_OK, intent);
			}
		}
		finish();
	}

	private void setAddress(String address) {
		Intent intent = getIntent();
		intent.putExtra("address", address);
		setResult(RESULT_OK, intent);
		finish();
	}
}
