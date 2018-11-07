/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: SelectTimeFragment.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.fragment.car
 * @Description: 车况-选择时间段
 * @author: zhaoqy
 * @date: 2017年4月28日 上午11:05:33
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.fragment.car;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.CustomDate;
import com.yunbaba.freighthelper.bean.car.Navi;
import com.yunbaba.freighthelper.bean.car.TravelTask;
import com.yunbaba.freighthelper.bean.eventbus.TravelTaskEvent;
import com.yunbaba.freighthelper.db.TravelTaskTable;
import com.yunbaba.freighthelper.manager.CarManager;
import com.yunbaba.freighthelper.ui.adapter.CarRouteAdapter;
import com.yunbaba.freighthelper.utils.CarUtils;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICarRouteListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCarRoute;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTask;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SelectTimeFragment extends Fragment {

	protected static final String TAG = "SelectTimeFragment";
	private ListView mListView;
	private View mWaitting;
	private CarRouteAdapter mAdapter;
	private List<Navi> mRouteList;
	private TextView mTvEmptyView;
	private TravelTaskEvent mEvent;
	private PercentRelativeLayout prl_netfail;
	private TextView tv_reload;

	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
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

	@Override
	public void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_car_select_time,
				container, false);
		initViews(view);
		initData();
		updateUI();
		return view;
	}

	private void initViews(View view) {
		mListView = (ListView) view.findViewById(R.id.select_time_listview);

		mTvEmptyView = (TextView) view.findViewById(R.id.tv_list_entryview);
		mListView.setEmptyView(mTvEmptyView);

		mWaitting = view.findViewById(R.id.select_time_waiting);
		
		prl_netfail = (PercentRelativeLayout)view.findViewById(R.id.prl_netfail);
		prl_netfail.setVisibility(View.GONE);
		
		tv_reload = (TextView)view.findViewById(R.id.tv_reload);
		
		tv_reload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

//				EventBus.getDefault().post(
//						new TravelTaskEvent(date.year, date.month, date.day));
				onMessageEvent(mEvent);
			}
		});
	}

	private void initData() {
		mRouteList = new ArrayList<Navi>();
		mRouteList.clear();
		mAdapter = new CarRouteAdapter(getActivity(), mRouteList);
		mListView.setAdapter(mAdapter);
	}

	private void updateUI() {

	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(TravelTaskEvent event) {
		mEvent = event;
		if (!CldPhoneNet.isNetConnected()) {
			prl_netfail.setVisibility(View.VISIBLE);
			Toast.makeText(getActivity(), R.string.common_network_abnormal,
					Toast.LENGTH_SHORT).show();
		} else {
			showProgressBar();
			// mTvEmptyView.setText("正在加载中...");
			mTvEmptyView.setText("");
			CustomDate customDate = new CustomDate();
			customDate.setYear(event.year);
			customDate.setMonth(event.month);
			customDate.setDay(event.day);
			mAdapter.setChoiceDate(customDate);
			
			List<TravelTask> travelTasks = TravelTaskTable.getInstance().query(
					customDate);
			if (travelTasks != null && !travelTasks.isEmpty()) {
				String date = event.year + "-" + event.month + "-" + event.day;
				List<MtqTask> tasks = new ArrayList<MtqTask>();
				int len = travelTasks.size();
				for (int i = 0; i < len; i++) {
					TravelTask travel = travelTasks.get(i);
					MtqTask task = new MtqTask();
					task.taskid = travel.taskid;
					task.corpid = travel.corpid;
					tasks.add(task);
				}
				/**
				 * 获取单天行程列表
				 */
				getCarRoutes(date, tasks);
			}
		}
	}

	private void getCarRoutes(String date, List<MtqTask> tasks) {
		CarManager.getInstance().getCarRoutes(date, tasks,
				new ICarRouteListener() {

					@Override
					public void onGetResult(int errCode,
							List<MtqCarRoute> listOfResult) {
						MLog.e(TAG, "getCarRoutes errCode: " + errCode);
						prl_netfail.setVisibility(View.GONE);
						hideProgressBar();
						if (errCode == 0 && listOfResult != null
								&& !listOfResult.isEmpty()) {
							updateRoutes(listOfResult);
						} else {
							/**
							 * 获取失败
							 */
							if (errCode != 0){
								prl_netfail.setVisibility(View.VISIBLE);
								Toast.makeText(getActivity(), "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
							}
							
							updateRoutes(null);
						}
					}

					@Override
					public void onGetReqKey(String arg0) {


					}
				});
	}

	private void updateRoutes(List<MtqCarRoute> listOfResult) {
		if (mRouteList == null) {
			mRouteList = new ArrayList<Navi>();
		}
		mRouteList.clear();
		mRouteList.addAll(CarUtils.formatNaviRoutes(listOfResult));
		if (mRouteList.size() == 0) {
			mTvEmptyView.setText("暂无数据");
		}
		
		//mAdapter.setResourceList(listOfResult);
		mAdapter.notifyDataSetChanged();
	}

	protected void showProgressBar() {
		// mWaitting.setVisibility(View.VISIBLE);
		WaitingProgressTool.showProgress(getActivity());

	}

	protected void hideProgressBar() {
		// mWaitting.setVisibility(View.GONE);
		WaitingProgressTool.closeshowProgress();
	}
}
