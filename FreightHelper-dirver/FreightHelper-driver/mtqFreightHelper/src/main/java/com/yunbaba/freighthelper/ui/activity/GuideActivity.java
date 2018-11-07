package com.yunbaba.freighthelper.ui.activity;

import java.util.ArrayList;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.ui.adapter.ViewPagerAdapter;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

// 引导页
public class GuideActivity  extends BaseActivity{
	private static final String TAG = "GuideActivity";

	private ViewPager viewPager;

	private ViewPagerAdapter vpAdapter;

	private ArrayList<View> Guide_views;

	private View Guide_view1, Guide_view2, Guide_view3, Guide_view4;

	private int currIndex = 0;

	private TextView tvStart;
	private ImageView ivJump;// ,ivJump2,ivJump3;

	Intent intentExtra;

	
	
	
		

	private void initView() {

		viewPager = (ViewPager) findViewById(R.id.viewpager);

		vpAdapter = new ViewPagerAdapter(Guide_views);
		vpAdapter.notifyDataSetChanged();
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setOffscreenPageLimit(4);//
		viewPager.setAdapter(vpAdapter);

	}

	private void initDatas() {
		LayoutInflater mLi = LayoutInflater.from(this);

		Guide_view1 = mLi.inflate(R.layout.view_guide, null);

		RelativeLayout back1 = (RelativeLayout) Guide_view1.findViewById(R.id.rll_background);
		back1.setBackgroundResource(R.drawable.bitmap_guide_bg_1);


		// ivJump1 = (ImageView) Guide_view1 .findViewById(R.id.iv_jump);

		Guide_view2 = mLi.inflate(R.layout.view_guide, null);

		RelativeLayout back2 = (RelativeLayout) Guide_view2.findViewById(R.id.rll_background);
		back2.setBackgroundResource(R.drawable.bitmap_guide_bg_2);

		// ivJump2 = (ImageView) Guide_view2 .findViewById(R.id.iv_jump);

		Guide_view3 = mLi.inflate(R.layout.view_guide, null);

		RelativeLayout back3 = (RelativeLayout) Guide_view3.findViewById(R.id.rll_background);
		back3.setBackgroundResource(R.drawable.bitmap_guide_bg_3);

		// ivJump3 = (ImageView) Guide_view3 .findViewById(R.id.iv_jump);
		// ImageView gv3 = (ImageView) Guide_view3
		// .findViewById(R.id.guideImageView);
		// gv3.setImageResource(R.drawable.guide_view03);
		// TextView guide31Tv = (TextView) Guide_view3
		// .findViewById(R.id.guide1TextView);
		// TextView guide32Tv = (TextView) Guide_view3
		// .findViewById(R.id.guide2TextView);
		// guide31Tv.setText(R.string.guide_3_1);
		// guide32Tv.setText(R.string.guide_3_2);

		Guide_view4 = mLi.inflate(R.layout.view_guide, null);

		RelativeLayout back4 = (RelativeLayout) Guide_view4.findViewById(R.id.rll_background);
		back4.setBackgroundResource(R.drawable.bitmap_guide_bg_4);

		tvStart = (TextView) Guide_view4.findViewById(R.id.tv_start);
		tvStart.setVisibility(View.VISIBLE);

		ivJump = (ImageView) findViewById(R.id.iv_jump);

		OnClickListener mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		};

		ivJump.setOnClickListener(mOnClickListener);
		// ivJump1.setOnClickListener(mOnClickListener);
		// ivJump2.setOnClickListener(mOnClickListener);
		// ivJump3.setOnClickListener(mOnClickListener);
		tvStart.setOnClickListener(mOnClickListener);

		Guide_views = new ArrayList<View>();

		Guide_views.add(Guide_view1);

		Guide_views.add(Guide_view2);

		Guide_views.add(Guide_view3);

		Guide_views.add(Guide_view4);

	}

	// private void inputData() {
	// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	// SharedPreferences.Editor editor = settings.edit();
	// editor.putBoolean("first_time", false);
	// editor.commit();
	// }

	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageSelected(int position) {

			// switch (position) {
			//
			// case 0:
			//
			// pointImage1.setImageDrawable(getResources().getDrawable(
			// R.drawable.guide_view_red));
			// pointImage2.setImageDrawable(getResources().getDrawable(
			// R.drawable.guide_view_gray));
			// break;
			//
			// case 1:
			//
			// pointImage1.setImageDrawable(getResources().getDrawable(
			// R.drawable.guide_view_gray));
			// pointImage2.setImageDrawable(getResources().getDrawable(
			// R.drawable.guide_view_red));
			// pointImage3.setImageDrawable(getResources().getDrawable(
			// R.drawable.guide_view_gray));
			// break;
			//
			// case 2:
			//
			// pointImage2.setImageDrawable(getResources().getDrawable(
			// R.drawable.guide_view_gray));
			//
			// pointImage3.setImageDrawable(getResources().getDrawable(
			// R.drawable.guide_view_red));
			// mLoginLinearLayout.setVisibility(View.VISIBLE);
			// break;
			// }

			currIndex = position;

			switch (position) {
			case 0:
				ivJump.setVisibility(View.VISIBLE);
				break;
			case 1:
				ivJump.setVisibility(View.VISIBLE);
				break;
			case 2:
				ivJump.setVisibility(View.VISIBLE);
				break;
			case 3:

				ivJump.setVisibility(View.GONE);
				break;
			default:
				break;
			}

		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

	}

	@Override
	protected int getLayoutResID() {

		return R.layout.activity_guide;
	}

	@Override
	protected void initViews() {

		initDatas();
		initView();
		GeneralSPHelper.getInstance(this).SetFirst();
		
	}

	@Override
	protected void setListener() {

		
	}

	@Override
	protected void initData() {

		
	}

	@Override
	protected void loadData() {

		
	}

	@Override
	protected void updateUI() {

		
	}

	@Override
	protected void messageEvent(AccountEvent event) {

		
	}

}
