package com.yunbaba.freighthelper.ui.fragment.car;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.CustomDate;
import com.yunbaba.freighthelper.bean.eventbus.TravelTaskEvent;
import com.yunbaba.freighthelper.ui.adapter.CalendarViewAdapter;
import com.yunbaba.freighthelper.ui.customview.CalendarCard;
import com.yunbaba.freighthelper.ui.customview.CalendarCard.OnCellClickListener;
import com.yunbaba.freighthelper.ui.customview.VerticalViewPager;

import org.greenrobot.eventbus.EventBus;

public class SelectDateFragment extends Fragment implements OnCellClickListener {

	private VerticalViewPager mViewPager;
	private TextView mCurMonth;
	private CalendarCard[] mShowViews;
	private CalendarViewAdapter<CalendarCard> adapter;
	private SildeDirection mDirection = SildeDirection.NO_SILDE;
	private int mCurIndex = 498;

	enum SildeDirection {
		DOWN, UP, NO_SILDE;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_car_select_date,
				container, false);
		initViews(view);
		setListener(view);
		initData();
		updateUI();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		AppStatApi.statOnPageStart(getContext(), this);
	}

	@Override
	public void onPause() {
		super.onPause();
		AppStatApi.statOnPagePause(getContext(), this);
	}

	private void initViews(View view) {
		mViewPager = (VerticalViewPager) view.findViewById(R.id.date_viewpager);
		mCurMonth = (TextView) view.findViewById(R.id.date_year);
	}

	private void setListener(View view) {

	}

	private void initData() {
		CalendarCard[] views = new CalendarCard[3];
		for (int i = 0; i < 3; i++) {
			views[i] = new CalendarCard(getActivity(), this);
		}
		adapter = new CalendarViewAdapter<CalendarCard>(views);
		setViewPager();

	}

	private void updateUI() {
	}

	@SuppressWarnings("deprecation")
	private void setViewPager() {
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(498);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				measureDirection(position);
				updateCalendarView(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	public void clickDate(CustomDate date) {
		/*
		 * String string = date.year + "年" + date.month + "月" + date.day + "日";
		 * Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
		 */

		if (mListener != null) {
			mListener.dateSelect(date);
		}

		EventBus.getDefault().post(
				new TravelTaskEvent(date.year, date.month, date.day));
	}

	@Override
	public void changeDate(CustomDate date) {
		mCurMonth.setText(date.year + "年" + date.month + "月");
	}

	/**
	 * 计算方向
	 */
	private void measureDirection(int position) {
		if (position > mCurIndex) {
			mDirection = SildeDirection.DOWN;
		} else if (position < mCurIndex) {
			mDirection = SildeDirection.UP;
		}
		mCurIndex = position;
	}

	/**
	 * 更新日历视图
	 */
	private void updateCalendarView(int position) {
		mShowViews = adapter.getAllItems();
		if (mDirection == SildeDirection.DOWN) {
			mShowViews[position % mShowViews.length].downSlide();
		} else if (mDirection == SildeDirection.UP) {
			mShowViews[position % mShowViews.length].upSlide();
		}
		mDirection = SildeDirection.NO_SILDE;
	}

	private OnDateSelectListener mListener;

	public interface OnDateSelectListener {

		void dateSelect(CustomDate date);
	}

	public void setOnDateSelectListener(OnDateSelectListener listener) {
		mListener = listener;
	}
}
