
package com.yunbaba.freighthelper.ui.activity.mapselect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cld.mapapi.model.LatLng;
import com.cld.mapapi.model.PoiInfo;
import com.cld.mapapi.search.exception.IllegalSearchArgumentException;
import com.cld.mapapi.search.geocode.GeoCodeResult;
import com.cld.mapapi.search.geocode.GeoCoder;
import com.cld.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.cld.mapapi.search.geocode.ReverseGeoCodeOption;
import com.cld.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cld.mapapi.search.poi.OnPoiSearchResultListener;
import com.cld.mapapi.search.poi.PoiCitySearchOption;
import com.cld.mapapi.search.poi.PoiResult;
import com.cld.mapapi.search.poi.PoiSearch;
import com.cld.nv.location.CldCoordUtil;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.eventbus.MapSelectPointEvent;
import com.yunbaba.freighthelper.ui.customview.ClearEditGrayText;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.LimitQueue;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.TimeTaskUtils;
import com.yunbaba.freighthelper.utils.TimeTaskUtils.OnTimerListener;
import com.yunbaba.ols.module.delivery.bean.MtqStore;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hmi.packages.HPDefine.HPWPoint;

public class MapSearchPoiActivity extends BaseButterKnifeActivity
		implements OnPoiSearchResultListener, OnGetGeoCoderResultListener {

	@BindView(R.id.iv_titleleft)
	ImageView ivTitleleft;
	@BindView(R.id.et_search)
	ClearEditGrayText etSearch;
	@BindView(R.id.ll1)
	PercentRelativeLayout ll1;
	@BindView(R.id.rv_list)
	ListView rvList;
	// @BindView(R.id.srl_layout)
	// SwipeRefreshLayout srlLayout;
	@BindView(R.id.tv_empty_hint)
	TextView tvEmptyHint;
	@BindView(R.id.ll_empty)
	PercentLinearLayout llEmpty;
	@BindView(R.id.pb_waiting)
	PercentRelativeLayout pbWaiting;
	public int pageNum = 0;
	List<PoiInfo> poilist = new ArrayList<>();
	PoiSearchResultAdapter poiAdapter;
	PoiSearch poisearch;
	MyHandler mHandler;
	LimitQueue<PoiInfo> recentChecklist;
	static final int FLAG_SHOWHISTORY = 10001;
	static final int FLAG_LOADMORE = 10002;
	boolean loadFinishSearch = false;
	View mHeadView, mFootView;
	PercentRelativeLayout prl_clean_history_search;
	TextView tv_no_more_result_hint, tv_recentcheck;
	LayoutInflater mLayoutInflater;

	MtqStore mStoreDetail;
	GeoCoder mGeoCoder;

	@Override
	public int getLayoutId() {

		return R.layout.activity_mapsearchres;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: add setContentView(...) invocation

		if (getIntent() != null && getIntent().getStringExtra("storedetail") != null) {

			String jsonstr = getIntent().getStringExtra("storedetail");

			mStoreDetail = GsonTool.getInstance().fromJson(jsonstr, MtqStore.class);

		}

		EventBus.getDefault().register(this);

		ButterKnife.bind(this);
		mHandler = new MyHandler(this);
		poiAdapter = new PoiSearchResultAdapter(this, poilist);

		mLayoutInflater = LayoutInflater.from(this);
		mHeadView = mLayoutInflater.inflate(R.layout.view_recent_search_head, null);
		mFootView = mLayoutInflater.inflate(R.layout.view_search_result_foot, null);
		prl_clean_history_search = ButterKnife.findById(mFootView, R.id.prl_clean_history_search);
		tv_no_more_result_hint = ButterKnife.findById(mFootView, R.id.tv_no_more_result_hint);
		tv_recentcheck = ButterKnife.findById(mHeadView, R.id.tv_recentcheck);
		prl_clean_history_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				if (TextUtils.isEmpty(etSearch.getText())) {

					recentChecklist.clear();

					poilist.clear();
					// poiAdapter.notifyDataSetChanged();
					poiAdapter.resetPois(poilist, (etSearch.getText() == null ? null : etSearch.getText().toString()));
					// mlistdata.clear();
					// mAdapter.notifyDataSetChanged();
					//
					// setHeadFootViewState(0);
					setViewState(1);
					SPHelper.getInstance(MapSearchPoiActivity.this).saveRecentSearchPoiInfo(recentChecklist);
				}
			}
		});

		rvList.addHeaderView(mHeadView, null, false);
		rvList.addFooterView(mFootView, null, false);

		rvList.setAdapter(poiAdapter);
		getPoiSearchInstance().setOnPoiSearchListner(this);
		recentChecklist = SPHelper.getInstance(this).getRecentSearchPoiInfo();

		setViewState(1);

		if (recentChecklist != null) {

			poilist = new ArrayList<PoiInfo>(recentChecklist.getQueue());

			Collections.reverse(poilist);

			poiAdapter.resetPois(poilist, (etSearch.getText() == null ? null : etSearch.getText().toString()));

		}

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {


			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {


			}

			@Override
			public void afterTextChanged(Editable s) {


				rvList.setSelection(0);

				// CheckRefreshLayoutEnable();

				if (etSearch.getText() == null || TextUtils.isEmpty(etSearch.getText().toString())) {

					mHandler.sendEmptyMessage(FLAG_SHOWHISTORY);
					// setListState(false, -1);

					setHistoryList();
					setViewState(1);
					return;
				} else {

					mHandler.post(new Runnable() {

						@Override
						public void run() {
							String str = etSearch.getText().toString();

							getTimer().NewInput(str);

							setListener();
						}
					});

				}

			}
		});

		rvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


				//
				// if (!mAdapter.isRecent() && !isContain(order)) {
				//
				// // recentChecklist.add(storedetail);
				// recentChecklist.offer(order);
				// SPHelper.getInstance(SearchTaskActivity.this).saveRecentCheckOrderList(recentChecklist);
				// }

				if (poiAdapter.getItem(position - 1) == null)
					return;

				// if (tv_recentcheck!=null && tv_recentcheck.getVisibility() ==
				// View.GONE) {

				if (!isContain(poiAdapter.getItem(position - 1))) {

					recentChecklist.offer(poiAdapter.getItem(position - 1));
					SPHelper.getInstance(MapSearchPoiActivity.this).saveRecentSearchPoiInfo(recentChecklist);
				}

				// recentChecklist.offer(poiAdapter.getItem(position-1));
				// SPHelper.getInstance(MapSearchPoiActivity.this).saveRecentSearchPoiInfo(recentChecklist);

				// }
				Intent it = new Intent(MapSearchPoiActivity.this, MapSelectAndSearchActivity.class);

				if (mStoreDetail != null)
					it.putExtra("storedetail", GsonTool.getInstance().toJson(mStoreDetail));

				it.putExtra("poiinfo", poiAdapter.getItem(position - 1));

				startActivity(it);

			}

		});

		rvList.setOnScrollListener(new OnScrollListener() {
			// AbsListView view 这个view对象就是listview
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						// loadMore();
						getTimer().clear();

						loadMore(etSearch.getText().toString());

						// new Handler().postDelayed(new Runnable() {
						//
						// @Override
						// public void run() {
						//
						// String str = etSearch.getText().toString();
						// Search(true, str);
						// }
						// }, 700);

					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// lastItem = firstVisibleItem + visibleItemCount - 1;
			}
		});

	}

	protected void loadMore(String key) {

		if (!loadFinishSearch) {

			// 加载更多
			Message msg = new Message();
			msg.what = FLAG_LOADMORE;
			msg.obj = key;

			mHandler.sendMessage(msg);

		} else {

			// 已经取完了
			mHandler.post(new Runnable() {

				@Override
				public void run() {

					// setViewState(0);
					// Toast.makeText(MapSearchPoiActivity.this, "没有更多记录了",
					// Toast.LENGTH_SHORT).show();
				}
			});

		}

	}

	public void setViewState(int state) {

		switch (state) {
		case 0:
			tv_recentcheck.setVisibility(View.GONE);
			tv_no_more_result_hint.setVisibility(View.VISIBLE);
			prl_clean_history_search.setVisibility(View.GONE);

			if (!loadFinishSearch)
				tv_no_more_result_hint.setText(R.string.loading_more_record);
			else
				tv_no_more_result_hint.setText(R.string.no_record_any_more);

			break;
		case 1:
			tv_recentcheck.setVisibility(View.VISIBLE);
			tv_no_more_result_hint.setVisibility(View.GONE);

			if (recentChecklist.size() <= 0)
				prl_clean_history_search.setVisibility(View.GONE);
			else
				prl_clean_history_search.setVisibility(View.VISIBLE);

			break;
		default:
			break;
		}

	}

	@OnClick(R.id.iv_titleleft)
	public void onClick() {

		finish();
	}

	TimeTaskUtils timer;

	public synchronized TimeTaskUtils getTimer() {

		if (timer == null)
			timer = new TimeTaskUtils();

		return timer;

	}

	public void setListener() {

		if (getTimer().getListener() == null) {
			getTimer().setListener(new OnTimerListener() {

				@Override
				public void AfterDebounce(final String str) {

					search(pageNum, str);

				}
			});
		}
	}

	@Override
	public void onGetPoiSearchResult(int errorCode, PoiResult result) {


		if (isFinishing())
			return;

		if (errorCode == 0 && result != null) {

			if (result.totalCount == 0) {

				// if (poilist == null || poilist.size() == 0) {
				llEmpty.setVisibility(View.VISIBLE);
				// }

			} else {
				pageNum = result.getCurrentPageNum();

				// 得到PoiInfo集合List<PoiInfo> list

				if (pageNum == 0) {

					poilist.clear();
					poilist.addAll(result.getPoiInfos());
					// MLog.e("first page", result.getPoiInfos().size() + " ? " +
					// poilist.size()+" "+pageNum);
				} else {
					poilist.addAll(result.getPoiInfos());
					// MLog.e("not first page", result.getPoiInfos().size() + " ?
					// " + poilist.size()+" "+ pageNum);
				}

				llEmpty.setVisibility(View.GONE);

				if (pageNum < (result.totalCount / result.pageCapacity)) {
					loadFinishSearch = false;
					setViewState(0);
					// setViewState(0);
				} else {
					loadFinishSearch = true;
					setViewState(0);
				}

				setAdapter(poilist);
			}

		} else

		{
			// loadFinishSearch = false;
			// Toast.makeText(PoiCitySearchActivity.this, "没有检索到数据",
			// Toast.LENGTH_SHORT).show();
			// if (poilist == null || poilist.size() == 0) {
			// llEmpty.setVisibility(View.VISIBLE);
			// }

		}
	}

	static final String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{9}$";

	protected void search(int pageNum, String keyword) {

		if (keyword.trim().matches(regex)) {

			MLog.e("匹配k码", " " + keyword.trim());
			try {

				HPWPoint point = CldCoordUtil.kcodeToCLD(keyword.trim());

				
			//	double lt = point.x /3600000.0D;
		//		double lg = point.y /3600000.0D;
				
			//	LatLng lp =
						//CldCoordUtil.cldToGCJ(point);
				
				LatLng lp2 = new LatLng( point.y,point.x);//CldCoordUtil.gcjToCLDEx(lp);
;
				GeoCode(lp2);

			} catch (Exception e) {

				MLog.e("不匹配k码", " exception " + keyword + " " + e.getMessage());

				PoiCitySearchOption option = new PoiCitySearchOption();
				option.city = "深圳市";
				option.keyword = keyword;
				option.pageCapacity = 20;
				option.pageNum = pageNum;
				getPoiSearchInstance().searchInCity(option);

			}

		} else {

			MLog.e("不匹配k码", " normal " + keyword);



			PoiCitySearchOption option = new PoiCitySearchOption();
			option.city = "深圳市";
			option.keyword = keyword;
			option.pageCapacity = 20;
			option.pageNum = pageNum;
			getPoiSearchInstance().searchInCity(option);
		}
		// PoiBoundSearchOption option = new PoiBoundSearchOption();
		// option.keyword = etSearch.getText().toString();
		// option.pageCapacity = 20;
		// option.pageNum = pageNum;
		// option.latLngBounds.northeast =
		// (positionAdapter.getLatLngs().get(0));
		// option.latLngBounds.southwest =
		// (positionAdapter.getLatLngs().get(1));
		// try {
		// getPoiSearchInstance().searchInBound(option);
		// } catch (IllegalSearchArgumentException e) {
		//
		// e.printStackTrace();
		// Toast.makeText(this, "搜索异常，请重新尝试", Toast.LENGTH_SHORT).show();
		// }

	}

	public void GeoCode(LatLng latLng) {

		ReverseGeoCodeOption option = new ReverseGeoCodeOption();

		// 设置逆地理坐标的经度值
		option.location.longitude = latLng.longitude;

		// 设置逆地理坐标的纬度值
		option.location.latitude = latLng.latitude;

		getGeoCoder().setOnGetGeoCodeResultListener(this);
		try {
			getGeoCoder().reverseGeoCode(option);// 传入逆地理参数对象
		} catch (IllegalSearchArgumentException e) {

			e.printStackTrace();
		}

	}

	public GeoCoder getGeoCoder() {

		if (mGeoCoder == null)
			mGeoCoder = GeoCoder.newInstance();

		return mGeoCoder;

	}

	private static class MyHandler extends Handler {
		private final WeakReference<MapSearchPoiActivity> mActivity;

		public MyHandler(MapSearchPoiActivity activity) {
			mActivity = new WeakReference<MapSearchPoiActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// System.out.println(msg);
			if (mActivity.get() == null) {
				return;
			}

			if (msg.what == FLAG_SHOWHISTORY) {

				mActivity.get().setHistoryList();
			} else if (msg.what == FLAG_LOADMORE) {
				mActivity.get().search(mActivity.get().pageNum + 1, (String) msg.obj);
			}

			// if (msg.what == RequestListMore) {
			// mActivity.get().getList(true, false);
			// } else if (msg.what == RequestList) {
			// mActivity.get().getList(false, false);
			// } else if (msg.what == RequestSearchListMore) {
			// mActivity.get().searchResult((String) msg.obj, true);
			// } else if (msg.what == RequestSearchList) {
			// mActivity.get().searchResult((String) msg.obj, false);
			// } else {
			//
			// }
			// mActivity.get().todo();
		}

	}

	public void setHistoryList() {

		if (recentChecklist != null) {
			poilist = new ArrayList<PoiInfo>(recentChecklist.getQueue());

			Collections.reverse(poilist);

			poiAdapter.resetPois(poilist, (etSearch.getText() == null ? null : etSearch.getText().toString()));
		}

		llEmpty.setVisibility(View.GONE);

	}

	public boolean isContain(PoiInfo obj) {

		if (recentChecklist == null)
			return true;

		if (recentChecklist.size() == 0)
			return false;

		boolean res = false;

		over: for (PoiInfo tmp : recentChecklist.getQueue()) {

			if (tmp.uid.equals(obj.uid)) {

				res = true;
				recentChecklist.getQueue().remove(tmp);
				break over;
			}
		}

		if (res) {

			recentChecklist.offer(obj);
			SPHelper.getInstance(this).saveRecentSearchPoiInfo(recentChecklist);

		}

		return res;
	}

	public PoiSearch getPoiSearchInstance() {

		if (poisearch == null)
			poisearch = PoiSearch.newInstance();

		return poisearch;

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	/**
	 * 填充数据
	 * 
	 * @param poiSpecs
	 * @return void
	 * @author LiangYJ
	 * @date 2015-10-30 上午11:08:15
	 */
	private void setAdapter(List<PoiInfo> poiSpecs) {
		if (poiAdapter == null)
			poiAdapter = new PoiSearchResultAdapter(this, poiSpecs);
		else
			poiAdapter.resetPois(poiSpecs, (etSearch.getText() == null ? null : etSearch.getText().toString()));
	}

	protected void showProgressBar() {

		if (pbWaiting != null)
			pbWaiting.setVisibility(View.VISIBLE);

	}

	protected void hideProgressBar() {
		if (pbWaiting != null)
			pbWaiting.setVisibility(View.GONE);
	}

	@Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
	public synchronized void onSelectMapPointEvent(MapSelectPointEvent event) {

		finish();

	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {


	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {

		if (isFinishing())
			return;

		MLog.e("反地理编码", arg0.errorCode + " " + arg0.errorMsg + " " + arg0.address);

		if (arg0.errorCode == 0 && !TextUtils.isEmpty(arg0.address)&& arg0.addressDetail != null) {

			pageNum = 0;

			// 得到PoiInfo集合List<PoiInfo> list

			if (pageNum == 0) {

				poilist.clear();

			}

			PoiInfo info = new PoiInfo();
			info.address = arg0.address;
			info.city = arg0.addressDetail.city;
			info.province = arg0.addressDetail.province;
			info.location = arg0.location;

			if (!TextUtils.isEmpty(arg0.addressDetail.street + arg0.addressDetail.streetNumber)) {
				info.name = arg0.addressDetail.street + arg0.addressDetail.streetNumber;
			} else if (!TextUtils.isEmpty(arg0.businessCircle)) {
				info.name = arg0.businessCircle;
			} else {

				info.name = arg0.address;
			}

			poilist.add(info);

			llEmpty.setVisibility(View.GONE);

			loadFinishSearch = true;
			setViewState(0);

			setAdapter(poilist);

		} else {

			llEmpty.setVisibility(View.VISIBLE);

		}
	}
}
