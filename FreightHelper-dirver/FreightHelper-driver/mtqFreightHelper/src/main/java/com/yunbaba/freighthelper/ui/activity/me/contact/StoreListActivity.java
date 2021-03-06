package com.yunbaba.freighthelper.ui.activity.me.contact;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.bean.OnResponseResultContainMsg;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.bean.eventbus.CompanyChangeEvent;
import com.yunbaba.freighthelper.ui.adapter.StoreListAdapter;
import com.yunbaba.freighthelper.ui.customview.ClearEditGrayText;
import com.yunbaba.freighthelper.utils.CharacterParser;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.TimeTaskUtils;
import com.yunbaba.freighthelper.utils.TimeTaskUtils.OnTimerListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliSearchStoreResult;
import com.yunbaba.ols.module.delivery.bean.MtqStore;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.angmarch.views.NiceSpinner;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreListActivity extends StoreListBaseActivity implements OnRefreshListener {

	@BindView(R.id.iv_titleleft)
	ImageView ivTitleleft;
	@BindView(R.id.tv_title)
	TextView tvTitle;

	@BindView(R.id.tv_waitmark)
	TextView tvWaitmark;

	@BindView(R.id.tv_titleright)
	TextView tvTitleRight;

	@BindView(R.id.iv_selectcompany)
	ImageView ivSelectcompany;

	@BindView(R.id.ll_listsearch)
	PercentLinearLayout llSearch;
	@BindView(R.id.tv_searchnum)

	TextView tvSearchNum;
	@BindView(R.id.et_search)
	ClearEditGrayText etSearch;
	@BindView(R.id.ll1)
	PercentRelativeLayout ll1;
	@BindView(R.id.rv_list)
	ListView rvList;

	@BindView(R.id.rv_listsearch)
	ListView rvListsearch;

	@BindView(R.id.v_line)
	View vLine;
	@BindView(R.id.ll_notmark)
	PercentRelativeLayout llNotmark;

	@BindView(R.id.lv_company)
	ListView lvCompany;

	@BindView(R.id.ll_company)
	PercentRelativeLayout llCompany;
	@BindView(R.id.pb_waiting)
	PercentRelativeLayout pbWaiting;

	@BindView(R.id.srl_layout)
	SwipeRefreshLayout mRefreshLayout;

	@BindView(R.id.ll_empty)
	PercentLinearLayout llEmpty;

	@BindView(R.id.tv_empty_hint)
	TextView tvEmptyHint;

	// @BindView(R.id.tv_search)
	// TextView tvSearch;

    @BindView(R.id.prl_mask)
    PercentRelativeLayout plMask;

    @BindView(R.id.pll_filter)
    PercentLinearLayout prlFilter;

    @BindView(R.id.sp_stores_marked_filter)
	NiceSpinner spStoresMarkedFilter;

    @BindView(R.id.sp_stores_comfirmed_filter)
	NiceSpinner spStoresComfirmedFilter;

    @BindArray(R.array.StoresMarked)
    String[] lstStoresMarkedFilterItem;

    @BindArray(R.array.StoresApprove)
    String[] lstStoresConfirmedFilterItem;

	private CharacterParser mParser;
	private StorePinyinComparator mComparator;

	List<MtqStore> mlist = new ArrayList<>();
	List<MtqStore> searchlist = new ArrayList<>();

	StoreListAdapter normallistadapter;
	StoreListAdapter searchllistadapter;
	MyHandler mHandler;
	int storetype = 0;

	String tmpName;

	public static final int pageSize = 20;
	int pageIndex = 1;
	int searchPageIndex = 1;
	boolean loadFinish = false;
	boolean loadFinishSearch = false;

	public static final int RequestList = 1;
	public static final int RequestListMore = 2;
	public static final int RequestSearchList = 3;
	public static final int RequestSearchListMore = 4;

	@Override
	public int getLayoutId() {

		return R.layout.activity_storelist;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().getIntExtra("storetype", -1) == -1) {

			finish();
		}

		storetype = getIntent().getIntExtra("storetype", 0);
		// ||getIntent().getIntExtra("positiontype", -1) == -1
		// positiontype = getIntent().getIntExtra("positiontype", 0);

		// TODO: add setContentView(...) invocation
		ButterKnife.bind(this);
		EventBus.getDefault().register(this);

		mHandler = new MyHandler(this);

		mParser = CharacterParser.getInstance();
		mComparator = new StorePinyinComparator();

		normallistadapter = new StoreListAdapter(this, mlist, 0, storetype);

		searchllistadapter = new StoreListAdapter(this, searchlist, 1, storetype);

		switch (storetype) {
		case 0:
			tvWaitmark.setText("待标注门店");
			etSearch.setHint("门店名称/编号");
			break;
		case 1:
			tvWaitmark.setText("待标注配送中心");
			tvTitleRight.setVisibility(View.GONE);
			etSearch.setHint("门店名称/编号/地址/K码/联系人");

			break;

		case 3:
			tvWaitmark.setText("待标注客户信息");
            tvTitleRight.setVisibility(View.GONE);
			etSearch.setHint("客户地址");
			break;
		default:
			break;
		}

		if (storetype == 1 || storetype == 3)
			llNotmark.setVisibility(View.GONE);



		rvList.setAdapter(normallistadapter);
		rvListsearch.setAdapter(searchllistadapter);

		initCompanyListView(null);



		//mRefreshLayout.setColorSchemeResources (R.color.white,R.color.app_color2);

		tvTitle.setText(getStoreDesc(storetype));

        llNotmark.setVisibility(View.GONE);

        prlFilter.setVisibility(View.GONE);

		rvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {




				MtqStore store = normallistadapter.getItem(position);

				if(store == null)
					return;


				//if (!TextUtils.isEmpty(store.kCode)) {


					Intent intent = new Intent(StoreListActivity.this, StoreDetailActivity.class);

					intent.putExtra("store", GsonTool.getInstance().toJson(normallistadapter.getItem(position)));
					intent.putExtra("corpid", curbean.getCorpId());
					intent.putExtra("corpname", curbean.getCorpName());
					intent.putExtra("storetype", storetype);
					startActivity(intent);


//				}else {
//
//					if (DeliveryApi.getInstance().isHasAuthForStore(StoreListActivity.this, curbean.getCorpId(), 1)) {
//
//						Intent intent2 = new Intent(StoreListActivity.this, StoreUploadActivity.class);
//						intent2.putExtra("corpid", curbean.getCorpId());
//						intent2.putExtra("corpname", curbean.getCorpName());
//						intent2.putExtra("store", GsonTool.getInstance().toJson(normallistadapter.getItem(position)));
//						intent2.putExtra("storetype", storetype);
//						startActivity(intent2);
//					} else {
//
//						Toast.makeText(StoreListActivity.this, "您没有编辑门店的权限，请联系企业开通", Toast.LENGTH_LONG).show();
//
//					}
//				}

			}
		});

		rvListsearch.setOnItemClickListener(new OnItemClickListener() {



			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				MtqStore store = searchllistadapter.getItem(position);

				if (store == null)
					return;


		//		if (!TextUtils.isEmpty(store.kCode)) {


					Intent intent = new Intent(StoreListActivity.this, StoreDetailActivity.class);

					intent.putExtra("store", GsonTool.getInstance().toJson(searchllistadapter.getItem(position)));
					intent.putExtra("corpid", curbean.getCorpId());
					intent.putExtra("corpname", curbean.getCorpName());
					intent.putExtra("storetype", storetype);
					startActivity(intent);


//				} else {
//
//
//					if (DeliveryApi.getInstance().isHasAuthForStore(StoreListActivity.this, curbean.getCorpId(), 1)) {
//
//						Intent intent2 = new Intent(StoreListActivity.this, StoreUploadActivity.class);
//						intent2.putExtra("corpid", curbean.getCorpId());
//						intent2.putExtra("corpname", curbean.getCorpName());
//						intent2.putExtra("store", GsonTool.getInstance().toJson(searchllistadapter.getItem(position)));
//						intent2.putExtra("storetype", storetype);
//						startActivity(intent2);
//					} else {
//
//						Toast.makeText(StoreListActivity.this, "您没有编辑门店的权限，请联系企业开通", Toast.LENGTH_LONG).show();
//
//					}
//				}
			}
		});

		setListener();

		etSearch.setText("");
		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {


			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {


			}

			@Override
			public void afterTextChanged(Editable s) {


				rvListsearch.setSelection(0);

				CheckRefreshLayoutEnable();

				if (etSearch.getText() == null || TextUtils.isEmpty(etSearch.getText().toString())) {

					mHandler.sendEmptyMessage(RequestList);
					setListState(false, -1);

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

		rvList.setOnScrollListener(new OnScrollListener() {
			// AbsListView view 这个view对象就是listview
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {

						if (mRefreshLayout != null && mRefreshLayout.isRefreshing()) {

							Toast.makeText(StoreListActivity.this, "请等待刷新结束再加载更多记录", Toast.LENGTH_SHORT).show();
							return;
						}

						loadMore(false, null);
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});

		rvListsearch.setOnScrollListener(new OnScrollListener() {
			// AbsListView view 这个view对象就是listview
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						loadMore(true, etSearch.getText().toString());
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});

		if (!DeliveryApi.getInstance().isHasAuthForStore(this, curbean.getCorpId(), 0)) {

			tvTitleRight.setVisibility(View.GONE);

		} else {

			if (storetype == 1 || storetype == 3) {
				tvTitleRight.setVisibility(View.GONE);
			} else
				tvTitleRight.setVisibility(View.VISIBLE);
		}

		mRefreshLayout.setColorSchemeResources(R.color.app_color2);

		mRefreshLayout.setOnRefreshListener(this);




		// mHandler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		//
		// }
		// }, 1500);

		// llEmpty.setVisibility(View.VISIBLE);
		//
		// setEmptyHintLayoutVisiable(true, 0);

		if (mHandler != null)
			mHandler.sendEmptyMessage(RequestList);

	}

	public void getList(final boolean isMore, final boolean isShowProgress) {

		if (!checkPermissionForStore()) {
			mlist.clear();

			setEmptyHintLayoutVisiable(true, 1);

			normallistadapter.setData(mlist, 0);
			normallistadapter.notifyDataSetChanged();

			return;
		}

		if (!isShowProgress) {
			showProgressBar();
			mRefreshLayout.setEnabled(false);
		}

		if (isMore)
			pageIndex += 1;
		else {
			pageIndex = 1;
			loadFinish = false;

		}

		setEmptyHintLayoutVisiable(false, 1);

		DeliveryApi.getInstance().SearchStore(curbean.getCorpId(), null, -1, storetype, pageIndex, pageSize, storetype,
				new OnResponseResultContainMsg<CldDeliSearchStoreResult>() {

					@Override
					public void OnResult(CldDeliSearchStoreResult result) {
						if (isFinishing())
							return;
						// MLog.e("check", result.page + " " + result.pagecount +
						// " "
						// + (result.lstOfStores == null ? "null" :
						// result.lstOfStores.size()));
						hideProgressBar();

						if (!isShowProgress) {
							mRefreshLayout.setEnabled(true);
						}

						if (mRefreshLayout != null)
							mRefreshLayout.setRefreshing(false);

						if (result != null && result.lstOfStores != null) {

							if (!isMore) {
								mlist.clear();
								mlist.addAll(result.lstOfStores);
							} else {
								mlist.addAll(result.lstOfStores);
							}

							for (MtqStore item : mlist) {

//								if (storetype == 3)
//
//									tmpName = item.linkMan.replaceAll("\\s*", "");
//								else
									tmpName = item.storeName.replaceAll("\\s*", "");

								// MLog.e("check", tmpName+"to"+tmpName.get);

								if (!TextUtils.isEmpty(tmpName))
									item.first = tmpName.subSequence(0, 1).toString();
								else
									item.first = "";

								if (!TextUtils.isEmpty(tmpName)) {

									String pinyin = mParser.getSelling(tmpName);
									String sortString = pinyin.substring(0, 1).toUpperCase();
									if (sortString.matches("[0-9A-Z]")) {
										item.letter = sortString.toUpperCase();
									} else {
										item.letter = "#";
									}
								} else
									item.letter = "#";

							}

							Collections.sort(mlist, mComparator);
							normallistadapter.setData(mlist, 0);
							normallistadapter.notifyDataSetChanged();

							if ((pageIndex) * pageSize >= result.record) {

								loadFinish = true;

							}

							setListState(false, result.record);

						} else {
							if (!isMore) {
								mlist.clear();
							} else {
							}

							normallistadapter.setData(mlist, 0);
							normallistadapter.notifyDataSetChanged();

							loadFinish = true;
							setListState(false, result.record);

							Toast.makeText(StoreListActivity.this, "没有" + getStoreDesc(storetype) + "信息,请选择其他企业再试",
									Toast.LENGTH_SHORT).show();
						}

					}

					@Override
					public void OnGetTag(String Reqtag) {

						MLog.e("checktag", Reqtag);
					}

					@Override
					public void OnError(int ErrCode, String msg) {

						if (isFinishing())
							return;

						hideProgressBar();

						if (!isShowProgress) {
							mRefreshLayout.setEnabled(true);

						}

						if (!isShowProgress) {
							mRefreshLayout.setEnabled(true);
						}

						if (mRefreshLayout != null)
							mRefreshLayout.setRefreshing(false);


						MLog.e("checkerror", ErrCode + "");

						if (!isMore)
							Toast.makeText(StoreListActivity.this, msg, Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(StoreListActivity.this, msg, Toast.LENGTH_SHORT).show();

					}
				});

	}

	public void searchResult(final String key, final boolean isMore) {

		if (!checkPermissionForStore()) {
			searchlist.clear();
			searchllistadapter.setData(searchlist, 1, "");

			searchllistadapter.notifyDataSetChanged();
			return;
		}

		showProgressBar();

		if (isMore)
			searchPageIndex += 1;
		else {
			searchPageIndex = 1;
			loadFinishSearch = false;
		}

		setEmptyHintLayoutVisiable(false, 1);

		DeliveryApi.getInstance().SearchStore(curbean.getCorpId(), key, -1, storetype, searchPageIndex, pageSize,
				storetype, new OnResponseResultContainMsg<CldDeliSearchStoreResult>() {

					@Override
					public void OnResult(CldDeliSearchStoreResult result) {

						if (isFinishing())
							return;

						hideProgressBar();

						if (result != null && result.lstOfStores != null) {

							if (!isMore) {
								searchlist.clear();
								searchlist.addAll(result.lstOfStores);
							} else {
								searchlist.addAll(result.lstOfStores);
							}

							searchllistadapter.setData(searchlist, 1, key);
							searchllistadapter.notifyDataSetChanged();

							if ((searchPageIndex) * pageSize >= result.record) {

								loadFinishSearch = true;

							}

							setListState(true, result.record);

						} else {

							if (!isMore) {
								searchlist.clear();

							} else {
							}
							loadFinishSearch = true;
							searchllistadapter.setData(searchlist, 1, key);
							searchllistadapter.notifyDataSetChanged();
							setListState(true, result.record);

						}

					}

					@Override
					public void OnGetTag(String Reqtag) {


					}

					@Override
					public void OnError(int ErrCode,String msg) {

						if (isFinishing())
							return;

						hideProgressBar();
						
						if (!isMore)
							Toast.makeText(StoreListActivity.this, msg, Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(StoreListActivity.this, msg, Toast.LENGTH_SHORT).show();
						
//						if (!isMore)
//							Toast.makeText(StoreListActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
//						else
//							Toast.makeText(StoreListActivity.this, "加载更多失败", Toast.LENGTH_SHORT).show();

					}
				});
	}

	protected void loadMore(boolean isSearch, String key) {

		// int count = mAdapter.getCount();
		// mAdapter.getCount() % 20 == 0 &&
		if (isSearch) {

			if (!loadFinishSearch) {

				// 加载更多
				Message msg = new Message();
				msg.what = RequestSearchListMore;
				msg.obj = key;

				mHandler.sendMessage(msg);

			} else {

				// 已经取完了
				mHandler.post(new Runnable() {

					@Override
					public void run() {

						Toast.makeText(StoreListActivity.this, "没有更多记录了", Toast.LENGTH_SHORT).show();
					}
				});

			}

		} else {
			if (!loadFinish) {

				// 加载更多
				mHandler.sendEmptyMessage(RequestListMore);

			} else {

				// 已经取完了
				mHandler.post(new Runnable() {

					@Override
					public void run() {

						Toast.makeText(StoreListActivity.this, "没有更多记录了", Toast.LENGTH_SHORT).show();
					}
				});

			}
		}

	}

	@OnClick({ R.id.iv_titleleft, R.id.iv_titleright, R.id.ll_notmark, R.id.iv_selectcompany,R.id.tv_titleright })
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_titleleft:
			finish();
			break;
		case R.id.iv_selectcompany:
		case R.id.tv_title:

//			if (llCompany.getVisibility() == View.GONE) {
//
//				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//
//				imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
//
//				llCompany.setVisibility(View.VISIBLE);
//				ivSelectcompany.setImageResource(R.drawable.icon_select_up);
//			} else {
//				llCompany.setVisibility(View.GONE);
//				ivSelectcompany.setImageResource(R.drawable.icon_select_down);
//			}

			break;
		case R.id.tv_titleright:

			if (DeliveryApi.getInstance().isHasAuthForStore(this, curbean.getCorpId(), 0)) {

				Intent intent = new Intent(this, StoreUploadActivity.class);

				intent.putExtra("corpid", curbean.getCorpId());
				intent.putExtra("corpname", curbean.getCorpName());
				intent.putExtra("storetype", storetype);

				startActivity(intent);

				// finish();
			} else {

				Toast.makeText(this, "您没有新增门店的权限，请联系企业开通", Toast.LENGTH_LONG).show();

			}

			break;
		case R.id.ll_notmark:
			Intent intent = new Intent(this, StoreListUnMarkActivity.class);
			intent.putExtra("corpid", curbean.getCorpId());
			intent.putExtra("corpname", curbean.getCorpName());
			intent.putExtra("storetype", storetype);
			startActivityForResult(intent, 10011);
			break;

		// case R.id.tv_search:
		//
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// if (imm != null) {
		// imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
		// }
		//
		// if (!CommonTool.isFastDoubleClick()) {
		//
		//
		//
		//
		// if (etSearch != null && !TextUtils.isEmpty(etSearch.getText())) {
		//
		// // tosearch
		//
		// rvListsearch.setSelection(0);
		//
		// CheckRefreshLayoutEnable();
		//
		// if (etSearch.getText() == null ||
		// TextUtils.isEmpty(etSearch.getText().toString())) {
		//
		// mHandler.sendEmptyMessage(RequestList);
		// setListState(false, -1);
		//
		// return;
		// } else {
		//
		// mHandler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// String str = etSearch.getText().toString();
		//
		// // getTimer().NewInput(str);
		//
		// // setListener();
		// DoSearch(str);
		// }
		// });
		//
		// }
		// }
		//
		//
		//
		//
		// }
		// break;
		}
	}

	protected void showProgressBar() {

		if (mRefreshLayout != null)
			mRefreshLayout.setRefreshing(false);

		if (pbWaiting != null)
			pbWaiting.setVisibility(View.VISIBLE);

	}

	protected void hideProgressBar() {
		if (pbWaiting != null)
			pbWaiting.setVisibility(View.GONE);
	}

	@Override
	public void afterSelectCompany(CorpBean bean) {

		if (bean != null) {

			tvTitle.setText(bean.getCorpName());
			llCompany.setVisibility(View.GONE);
			ivSelectcompany.setImageResource(R.drawable.icon_select_down);
			/** 如果涉及到全局 */ /** 不涉及全局 */
			// SPHelper.getInstance(this).WriteCurrentSelectCompanyBean(bean);
			// EventBus.getDefault().post(new CompanyChangeEvent(bean));

			mlist.clear();
			normallistadapter.notifyDataSetChanged();

			if (!TextUtils.isEmpty(etSearch.getText())) {

				Message msg = new Message();
				msg.what = RequestSearchList;
				msg.obj = etSearch.getText().toString();

				mHandler.sendMessage(msg);

			} else {

				mHandler.sendEmptyMessage(RequestList);
			}

		} else {

			// tvTitle.setText(bean.getCorpName());
			llCompany.setVisibility(View.GONE);
			ivSelectcompany.setImageResource(R.drawable.icon_select_down);

		}

		if (!DeliveryApi.getInstance().isHasAuthForStore(this, curbean.getCorpId(), 0)) {

			tvTitleRight.setVisibility(View.GONE);

		} else {

			if (storetype == 1 || storetype == 3) {
				tvTitleRight.setVisibility(View.GONE);
			} else
				tvTitleRight.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public ListView getCompanyLv() {

		return lvCompany;
	}

	@Override
	public void onBackPressed() {


		if (llCompany.getVisibility() == View.GONE) {
			finish();
		} else {
			llCompany.setVisibility(View.GONE);
			ivSelectcompany.setImageResource(R.drawable.icon_select_down);
		}
		// super.onBackPressed();

	}

	public void setListState(boolean isSearch, int searchsum) {

		MLog.e("setliststate", isSearch + " " + searchsum);

		if (isSearch) {
			llSearch.setVisibility(View.VISIBLE);
			tvSearchNum.setVisibility(View.VISIBLE);
			rvListsearch.setVisibility(View.VISIBLE);

			if (searchsum == 0) {
				tvSearchNum.setText("没有匹配到结果");
			} else
				tvSearchNum.setText(FreightLogicTool.getSearchResultCountHint(searchsum));

			setEmptyHintLayoutVisiable(true, 0);

		} else {
			searchPageIndex = 1;
			searchlist.clear();
			llSearch.setVisibility(View.GONE);

			if (searchsum == 0) {

				setEmptyHintLayoutVisiable(true, 0);

			} else {

				setEmptyHintLayoutVisiable(false, 0);

			}

		}

	}

	private static class MyHandler extends Handler {
		private final WeakReference<StoreListActivity> mActivity;

		public MyHandler(StoreListActivity activity) {
			mActivity = new WeakReference<StoreListActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// System.out.println(msg);
			if (mActivity.get() == null) {
				return;
			}

			if (msg.what == RequestListMore) {
				mActivity.get().getList(true, false);
			} else if (msg.what == RequestList) {
				mActivity.get().getList(false, false);
			} else if (msg.what == RequestSearchListMore) {
				mActivity.get().searchResult((String) msg.obj, true);
			} else if (msg.what == RequestSearchList) {
				mActivity.get().searchResult((String) msg.obj, false);
			} else {

			}
			// mActivity.get().todo();
		}
	}

	public boolean checkPermissionForStore() {

		if (DeliveryApi.getInstance().isHasAuthForStore(this, curbean.getCorpId(), 2)) {

			return true;
		} else {
			Toast.makeText(StoreListActivity.this, "您没有查看" + getStoreDesc(storetype) + "的权限，请联系企业开通", Toast.LENGTH_LONG)
					.show();
			return false;

		}

	}

	TimeTaskUtils timer;

	public synchronized TimeTaskUtils getTimer() {

		if (timer == null)
			timer = new TimeTaskUtils();

		return timer;

	}

	public synchronized void DoSearch(final String str) {
		if (etSearch != null && !TextUtils.isEmpty(etSearch.getText()) && str.equals(etSearch.getText().toString())) {

			mHandler.post(new Runnable() {

				@Override
				public void run() {
					MLog.e("seach", str);
					synchronized (StoreListActivity.this) {

						Message msg = new Message();
						msg.what = RequestSearchList;
						msg.obj = str;

						mHandler.sendMessage(msg);

						// se(false, str);
					}

				}
			});
		}
	}

	public void setListener() {

		if (getTimer().getListener() == null) {
			getTimer().setListener(new OnTimerListener() {

				@Override
				public void AfterDebounce(final String str) {

					DoSearch(str);

				}
			});
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
		EventBus.getDefault().unregister(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10011 && resultCode == 1 && data != null) {

			String corpid = data.getStringExtra("corpid");
			String corpname = data.getStringExtra("corpname");

			if (!TextUtils.isEmpty(corpid) && !TextUtils.isEmpty(corpname)) {

				if (!curbean.getCorpId().equals(corpid)) {
					curbean = new CorpBean(corpid, corpname);

					afterSelectCompany(curbean);

					RefreshList();
				}

			}

		}

	}

	// 处理公司变更消息
	@Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
	public synchronized void onCompanyChangeEvent(CompanyChangeEvent event) {

		// mCurCorp = event.getCurrentCorp();

		if (event.isDeleteCropHappen && curbean.getCorpId() != null && curbean.getCorpId().equals(event.deletecropid)) {

			curbean = null;
			RefreshList();

			if (curbean == null) {

				Toast.makeText(this, "您尚未加入企业", Toast.LENGTH_SHORT).show();
				finish();

			} else {

				afterSelectCompany(curbean);

			}

		}

	}

	@Override
	public void onRefresh() {

		getList(false, true);

	}

	protected void CheckRefreshLayoutEnable() {

		if (etSearch.getText() == null || TextUtils.isEmpty(etSearch.getText())) {

			mRefreshLayout.setEnabled(true);

		} else
			mRefreshLayout.setEnabled(false);
	}

	public void setEmptyHintLayoutVisiable(boolean isVisible, int type) {

		// MLog.e("setEmptyHintLayoutVisiable", isVisible + " " + type);

		if (isVisible) {

			llEmpty.setVisibility(View.VISIBLE);

			if (type == 0) {
				tvEmptyHint.setText("未找到相关的" + getStoreDesc(storetype) + "信息");
			} else {
				tvEmptyHint.setText("没有查看" + getStoreDesc(storetype) + "的权限");
			}
		} else {

			llEmpty.setVisibility(View.GONE);

		}

	}

	@Override
	public void ReRestoreDataAndUI() {

		super.ReRestoreDataAndUI();
		MLog.e("restore", "storelistactivity");

		initCompanyListView(null);

		if (mHandler != null)
			mHandler.sendEmptyMessage(RequestList);

	}

}
