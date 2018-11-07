package com.yunbaba.freighthelper.ui.activity.car;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.CustomDate;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.ui.activity.base.BaseFragmentActivity;
import com.yunbaba.freighthelper.ui.customview.NoScrollViewPager;
import com.yunbaba.freighthelper.ui.customview.PopupTravel;
import com.yunbaba.freighthelper.ui.customview.PopupTravel.OnPopupListener;
import com.yunbaba.freighthelper.ui.fragment.car.SelectDateFragment;
import com.yunbaba.freighthelper.ui.fragment.car.SelectDateFragment.OnDateSelectListener;
import com.yunbaba.freighthelper.ui.fragment.car.SelectRouteFragment;
import com.yunbaba.freighthelper.ui.fragment.car.SelectTimeFragment;

import java.util.ArrayList;

public class CarSelectActivity extends BaseFragmentActivity implements
		OnClickListener, OnDateSelectListener {

	private Context mContext;
	private ImageView mBack;
	private TextView mTitle;
	private ImageView mNewsImg;
	private TextView mRoot;
	private TextView mSelectTime;
	private NoScrollViewPager mViewPager;
	private ImageView mCursor;
	private ArrayList<Fragment> fragmentList;
	private PopupTravel mPopup;
	private int mOffset = 0;
	private int mCurIndex = 0;
	private int mBmpW;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_car_select;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mNewsImg = (ImageView) findViewById(R.id.title_right_img);

		mRoot = (TextView) findViewById(R.id.car_select_route);
		mSelectTime = (TextView) findViewById(R.id.car_select_time);
		mViewPager = (NoScrollViewPager) findViewById(R.id.car_select_viewpager);
		mCursor = (ImageView) findViewById(R.id.car_select_cursor);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
		mNewsImg.setOnClickListener(this);
		mSelectTime.setOnClickListener(this);
		mRoot.setOnClickListener(this);
		findViewById(R.id.car_select_date).setOnClickListener(this);
	}

	@Override
	protected void initData() {
		mContext = this;
		mBack.setVisibility(View.VISIBLE);
		mNewsImg.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.car_condition);
		mSelectTime.setTextColor(Color.parseColor("#8ec2c4"));

		String title = getResources().getString(
				R.string.car_condition_recent_route);
		mPopup = new PopupTravel(mContext, title, new OnPopupListener() {

			@Override
			public void onClick() {
				finish();
			}
		});

		initCursor();
		initViewPage();
	}

	@Override
	protected void loadData() {
	}

	@Override
	protected void updateUI() {
		updateNews();
	}

	private void updateNews() {
//		if (MsgManager.getInstance().hasUnReadMsg(1)) {
//			mNewsImg.setImageResource(R.drawable.msg_icon_news);
//		} else {
//			mNewsImg.setImageResource(R.drawable.msg_icon);
//		}
	}

	@Override
	protected void messageEvent(AccountEvent event) {

	}

	private void initCursor() {
		mBmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.car_cursor).getWidth(); // 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels; // 获取分辨率宽度
		mOffset = (screenW / 3 - mBmpW) / 2; // 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(mOffset, 0);
		mCursor.setImageMatrix(matrix); // 设置动画初始位置
	}

	@SuppressWarnings("deprecation")
	private void initViewPage() {
		fragmentList = new ArrayList<Fragment>();
		Fragment routeFragment = new SelectRouteFragment();
		fragmentList.add(routeFragment);

		SelectDateFragment dateFragment = new SelectDateFragment();
		dateFragment.setOnDateSelectListener(this);
		fragmentList.add(dateFragment);

		SelectTimeFragment timeFragment = new SelectTimeFragment();
		fragmentList.add(timeFragment);

		mViewPager.setNoScroll(true);
		mViewPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		mViewPager.setCurrentItem(1);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		translateCursor(1);
	}

	public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		ArrayList<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getCount() {
			return list.size();
		}

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		int one = mOffset * 2 + mBmpW; // 页卡1 -> 页卡2 偏移量

		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageSelected(int arg0) {
			translateCursor(arg0);
		}
	}

	private void translateCursor(int index) {
		int one = mOffset * 2 + mBmpW;

		Animation animation = new TranslateAnimation(one * mCurIndex, one
				* index, 0, 0);
		mCurIndex = index;
		animation.setFillAfter(true);
		animation.setDuration(300);
		mCursor.startAnimation(animation);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img: {
			finish();
			break;
		}
		case R.id.title_right_img: {
//			Intent intent = new Intent(this, MsgActivity.class);
//			startActivity(intent);
			break;
		}
		case R.id.car_select_route: {
			// mViewPager.setCurrentItem(0);
			mPopup.showAsDropDown(mCursor, 0, 0);
			break;
		}
		case R.id.car_select_date: {
			mViewPager.setCurrentItem(1);
			mSelectTime.setTextColor(Color.parseColor("#8ec2c4"));
			break;
		}
		case R.id.car_select_time: {
			// mViewPager.setCurrentItem(2);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void dateSelect(CustomDate date) {
		// String string = date.year + "年" + date.month + "月" + date.day + "日";
		// Toast.makeText(this, string, Toast.LENGTH_SHORT).show();

		mSelectTime.setTextColor(Color.parseColor("#109E98"));
		mViewPager.setCurrentItem(2);
	}

	@Override
	protected void onResume() {

		super.onResume();
	}
}
