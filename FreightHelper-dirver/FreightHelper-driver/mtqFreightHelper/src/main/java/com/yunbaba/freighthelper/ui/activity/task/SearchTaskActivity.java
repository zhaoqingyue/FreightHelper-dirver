package com.yunbaba.freighthelper.ui.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.api.trunk.listner.OnObjectListner;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.bean.eventbus.CompanyChangeEvent;
import com.yunbaba.freighthelper.ui.adapter.SearchResultAdapter;
import com.yunbaba.freighthelper.ui.customview.SearchCleanEditText;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.LimitQueue;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.TimeTaskUtils;
import com.yunbaba.freighthelper.utils.TimeTaskUtils.OnTimerListener;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
import com.yunbaba.ols.module.delivery.bean.MtqRequestTime;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchTaskActivity extends BaseButterKnifeActivity {

	@BindView(R.id.tv_company)
	TextView tvCompany;
	@BindView(R.id.iv_select)
	ImageView ivSelect;
	@BindView(R.id.rll_company)
	PercentRelativeLayout rllCompany;
	@BindView(R.id.et_search)
	SearchCleanEditText etSearch;
	@BindView(R.id.iv_search_empty)
	ImageView ivSearchEmpty;
	@BindView(R.id.iv_titleright)
	TextView ivTitleright;
	@BindView(R.id.rv_list)
	ListView rvList;
	@BindView(R.id.progress_bar)
	ProgressBar mProgressBar;

	LayoutInflater mLayoutInflater;

	View mHeadView, mFootView;

	SearchResultAdapter mAdapter;

	// private Disposable mDisposable;

	public List<MtqRequestTime> mlistdata = new ArrayList<MtqRequestTime>();

	LimitQueue<MtqRequestTime> recentChecklist;

	public static final int pageSize = 20;
	int pageIndex = 1;
	boolean loadFinish = false;
	int lastItem;

	private CorpBean mCurCorp;

	private Handler mHandler = new Handler();

	@Override
	public int getLayoutId() {

		return R.layout.activity_searchtask;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: add setContentView(...) invocation
		ButterKnife.bind(this);
		EventBus.getDefault().register(this);

		mCurCorp = SPHelper.getInstance(this).ReadCurrentSelectCompanyBean();

		setCurrentCorp();

		recentChecklist = SPHelper.getInstance(this).getRecentCheckOrderList();

		// 刷新最近搜索运货点的状态
		// RefreshStoreList();

		initHeadAndFootView();

		mlistdata = new ArrayList<MtqRequestTime>(recentChecklist.getQueue());

		Collections.reverse(mlistdata);

		mAdapter = new SearchResultAdapter(this, mlistdata);

		rvList.setAdapter(mAdapter);

		mAdapter.RefreshList(true);

		setHeadFootViewState(0);

		setListener();

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

				// 没有任何字符串,显示最近查看任务
				if (etSearch.getText() == null || TextUtils.isEmpty(etSearch.getText().toString())) {

					ivSearchEmpty.setVisibility(View.VISIBLE);

					if (recentChecklist != null) {

						mHandler.post(new Runnable() {

							@Override
							public void run() {

								getTimer().clear();

								showResult(recentChecklist.getQueue(), true);

								setHeadFootViewState(0);
							}
						});

					}

					return;
				} else {

					ivSearchEmpty.setVisibility(View.GONE);

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
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


				// MLog.e("seach", "additem"+arg2);

				if (arg2 < 1)
					return;

				int realposition = arg2 - 1;

				MtqRequestTime order = mAdapter.getItem(realposition);
				
				MLog.e("check", GsonTool.getInstance().toJson(order));

				MtqDeliTaskDetail taskdetail = TaskOperator.getInstance().getTaskDetailDataFromDB(order.taskid);

				if (taskdetail != null) {
					
					
					MLog.e("check", "taskdetail not null");

					if (!mAdapter.isRecent() && !isContain(order)) {

						// recentChecklist.add(storedetail);
						recentChecklist.offer(order);
						SPHelper.getInstance(SearchTaskActivity.this).saveRecentCheckOrderList(recentChecklist);
					}

					Intent intent = new Intent(SearchTaskActivity.this, TaskPointDetailActivity.class);


					MtqDeliOrderDetail orderdetail = null;

					over2: for (MtqDeliOrderDetail order2 : taskdetail.getOrders()) {
						if (order2.orderno.equals(order.cust_orderid)) {
							MLog.e("check", "orderdetail not null");
							orderdetail = order2;
							break over2;
						}

					}
					
					if(orderdetail == null)
						return;


					MtqDeliStoreDetail mStoreDetail = null;
					
					over1: for (MtqDeliStoreDetail store : taskdetail.getStore()) {

						if (store.waybill.equals(orderdetail.waybill)) {

							mStoreDetail = store;
							MLog.e("check", "storedetail not null");
							break over1;
						}

					}
					
					
					// 添加storedetail
					 String str = GsonTool.getInstance().toJson(mStoreDetail);
					intent.putExtra("storedetail", str);

					// 添加taskid
					intent.putExtra("taskid", order.taskid);
					intent.putExtra("corpid", order.corpid);
					//intent.putExtra("isNeedFresh", true);



					if (orderdetail != null) {
						String str2 = GsonTool.getInstance().toJson(orderdetail);
						intent.putExtra("orderdetail", str2);
					}

					startActivity(intent);
				}else{
					
					RefreshDetailData(order.taskid,order.corpid,order);
					
				}
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

						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {

								String str = etSearch.getText().toString();
								Search(true, str);
							}
						}, 700);

					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount - 1;
			}
		});

	}

	// // 刷新最近搜索运货点的状态,从数据库拉取
	// private void RefreshStoreList() {
	//
	// try {
	// String[] waybilllist = new String[recentChecklist.getQueue().size()];
	//
	// for (int i = 0; i < recentChecklist.getQueue().size(); i++) {
	//
	// waybilllist[i] = recentChecklist.getQueue().get(i).waybill;
	//
	// }
	//
	// List<MtqRequestTime> list =
	// TaskOperator.getInstance().GetStoreDetailListFromDB(waybilllist);
	//
	// // LinkedList<MtqRequestTime> queue =
	// // recentChecklist.getQueue();
	//
	// if (list != null && list.size() > 0) {
	//
	// for (MtqRequestTime stmp : list) {
	// over: for (int i = 0; i < recentChecklist.getQueue().size(); i++) {
	//
	// if (stmp.waybill.equals(recentChecklist.getQueue().get(i).waybill))
	//
	// {
	// recentChecklist.getQueue().set(i, stmp);
	//
	// break over;
	// }
	// }
	//
	// }
	//
	// // recentChecklist.resetList(list);
	//
	// }
	//
	// // queue = new LinkedList<MtqRequestTime>(list);
	//
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// }

	// private void loadMore() {
	//
	//
	// }

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

					mHandler.post(new Runnable() {

						@Override
						public void run() {
							MLog.e("seach", str);
							synchronized (SearchTaskActivity.this) {

								Search(false, str);
							}

						}
					});

				}
			});
		}
	}

	public void Search(final boolean isMore, String key) {

		if (TextUtils.isEmpty(etSearch.getText()))
			return;

		int start = 0;
		// int length = 20;

		if (!isMore) {
			pageIndex = 1;
			loadFinish = false;

		} else {

			pageIndex += 1;
			start = (pageIndex - 1) * 20;

		}

		MLog.e("curcorpid", getCurrentCorpId() + mCurCorp.getCorpName());

		 TaskOperator.getInstance()
				.SearchCuOrderIdListByCustorderidFromDB(getCurrentCorpId(), key, start, pageSize, new OnObjectListner<List<MtqRequestTime>>() {
					@Override
					public void onResult(List<MtqRequestTime> res) {
						final List<MtqRequestTime> tmplistdata =res;

//						if(tmplistdata == null){
//							tmplistdata =
//						}


						runOnUiThread(new Runnable() {
							@Override
							public void run() {


								if(tmplistdata!=null) {


									if (tmplistdata.size() < pageSize)
										loadFinish = true;
									else
										loadFinish = false;

									if (isMore) {
										mlistdata.addAll(tmplistdata);
									} else {
										mlistdata = tmplistdata;
									}

									showResult(mlistdata, false);

									setHeadFootViewState(1);

								}
							}
						});
					}
				});



	}

	protected void showProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
	}

	protected void hideProgressBar() {
		mProgressBar.setVisibility(View.GONE);
	}

	protected void showResult(List<MtqRequestTime> result, boolean isRecent) {
		if (!isRecent && result.isEmpty()) {
			Toast.makeText(this, "未找到匹配的结果", Toast.LENGTH_SHORT).show();

			mAdapter.setList(result);
			mAdapter.RefreshList(isRecent);

		} else if (isRecent) {
			// 最新查看的
			mlistdata = new ArrayList<MtqRequestTime>(result);

			Collections.reverse(mlistdata);

			mAdapter.setList(mlistdata);
			mAdapter.RefreshList(isRecent);
		} else {
			// 搜索出来的结果
			mAdapter.setList(result);
			mAdapter.RefreshList(isRecent);

		}
	}

	PercentRelativeLayout prl_clean_history_search;
	TextView tv_no_more_result_hint, tv_recentcheck;

	public void initHeadAndFootView() {
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
					mlistdata.clear();
					mAdapter.notifyDataSetChanged();

					setHeadFootViewState(0);
					SPHelper.getInstance(SearchTaskActivity.this).saveRecentCheckOrderList(recentChecklist);
				}
			}
		});

		rvList.addHeaderView(mHeadView, null, false);
		rvList.addFooterView(mFootView, null, false);

	}

	/**
	 * 0 默认 最新查看 ; 1 搜索结果 ;
	 * 
	 */
	public void setHeadFootViewState(int type) {

		switch (type) {
		case 0:
			tv_recentcheck.setVisibility(View.VISIBLE);
			tv_no_more_result_hint.setVisibility(View.GONE);

			if (recentChecklist.size() <= 0)
				prl_clean_history_search.setVisibility(View.GONE);
			else
				prl_clean_history_search.setVisibility(View.VISIBLE);
			break;
		case 1:
			tv_recentcheck.setVisibility(View.VISIBLE);

			tv_no_more_result_hint.setVisibility(View.VISIBLE);
			prl_clean_history_search.setVisibility(View.GONE);

			if (!loadFinish)
				tv_no_more_result_hint.setText(R.string.loading_more_record);
			else
				tv_no_more_result_hint.setText(R.string.no_record_any_more);

			break;
		default:
			tv_recentcheck.setVisibility(View.VISIBLE);
			tv_no_more_result_hint.setVisibility(View.GONE);
			prl_clean_history_search.setVisibility(View.VISIBLE);
			break;
		}

	}

	@OnClick({ R.id.rll_company, R.id.iv_titleright })
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rll_company:

			startActivity(new Intent(this, SelectCompanyActivity.class));
			// TransitionsHeleper.startActivity(this,
			// SelectCompanyActivity.class, ivSelect);
			break;
		case R.id.iv_titleright:
			finish();
			break;
		}
	}

	public boolean isContain(MtqRequestTime obj) {

		if (recentChecklist == null)
			return true;

		if (recentChecklist.size() == 0)
			return false;

		boolean res = false;

		over: for (MtqRequestTime tmp : recentChecklist.getQueue()) {

			if (tmp.taskidandorderid.equals(obj.taskidandorderid)) {

				res = true;
				recentChecklist.getQueue().remove(tmp);
				break over;
			}
		}

		if (res) {

			recentChecklist.offer(obj);
			SPHelper.getInstance(SearchTaskActivity.this).saveRecentCheckOrderList(recentChecklist);

		}

		return res;
	}

	// 处理公司变更消息
	@Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
	public synchronized void onCompanyChangeEvent(CompanyChangeEvent event) {

		mCurCorp = event.getCurrentCorp();

		setCurrentCorp();
		HandleCompanyChange();

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	private synchronized void setCurrentCorp() {

		if (mCurCorp == null) {
			tvCompany.setText(R.string.all);
			return;
		}
		if (mCurCorp.getCorpId() == null) {
			tvCompany.setText(R.string.all);
		} else {
			tvCompany.setText(mCurCorp.getCorpName());
		}
	}

	// 处理企业变化
	public void HandleCompanyChange() {

		rvList.setSelection(0);

		// 没有任何字符串,显示最近查看任务
		if (etSearch.getText() == null || TextUtils.isEmpty(etSearch.getText().toString())) {

			ivSearchEmpty.setVisibility(View.VISIBLE);

			if (recentChecklist != null) {

				mHandler.post(new Runnable() {

					@Override
					public void run() {

						getTimer().clear();

						showResult(recentChecklist.getQueue(), true);

						setHeadFootViewState(0);
					}
				});

			}

			return;
		} else {

			ivSearchEmpty.setVisibility(View.GONE);

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

	public String getCurrentCorpId() {
		mCurCorp = SPHelper.getInstance(this).ReadCurrentSelectCompanyBean();
		return mCurCorp == null ? null : mCurCorp.getCorpId();

	}
	
	public void RefreshDetailData(String taskid,String corpid,final MtqRequestTime order) {

		showProgressBar();

		DeliveryApi.getInstance().getTaskDetailInServer(taskid, corpid, new OnResponseResult<MtqDeliTaskDetail>() {

			@Override
			public void OnResult(MtqDeliTaskDetail result) {

				if (isFinishing())
					return;

				if (result == null || result.getStore() == null) {
					Toast.makeText(SearchTaskActivity.this, "获取路由点详情失败", Toast.LENGTH_SHORT).show();
					hideProgressBar();
					return;

				}

				hideProgressBar();

				TaskOperator.getInstance().saveTaskDetailDataToBD(result);

				if (!mAdapter.isRecent() && !isContain(order)) {

					// recentChecklist.add(storedetail);
					recentChecklist.offer(order);
					SPHelper.getInstance(SearchTaskActivity.this).saveRecentCheckOrderList(recentChecklist);
				}

				Intent intent = new Intent(SearchTaskActivity.this, TaskPointDetailActivity.class);

				MtqDeliOrderDetail orderdetail = null;

				over2: for (MtqDeliOrderDetail order2 : result.getOrders()) {
					if (order2.orderno.equals(order.cust_orderid)) {
						MLog.e("check", "orderdetail not null");
						orderdetail = order2;
						break over2;
					}

				}


				if(orderdetail == null)
					return;

				MtqDeliStoreDetail mStoreDetail = null;

				over1: for (MtqDeliStoreDetail store : result.getStore()) {

					if (store.waybill.equals(orderdetail.waybill)) {

						mStoreDetail = store;
						MLog.e("check", "storedetail not null");
						break over1;
					}

				}
				
				
				// 添加storedetail
				 String str = GsonTool.getInstance().toJson(mStoreDetail);
				 intent.putExtra("storedetail", str);

				// 添加taskid
				intent.putExtra("taskid", order.taskid);
				intent.putExtra("corpid", order.corpid);
				//intent.putExtra("isNeedFresh", true);
				
				

//				MtqDeliOrderDetail orderdetail = null;
//
//				over2: for (MtqDeliOrderDetail order : result.getOrders()) {
//					if (order.cust_orderid.equals(order.cust_orderid)) {
//
//						orderdetail = order;
//						break over2;
//					}
//
//				}
//
				
				if (orderdetail != null) {
					String str2 = GsonTool.getInstance().toJson(orderdetail);
					intent.putExtra("orderdetail", str2);
				}

				startActivity(intent);
			}

			@Override
			public void OnError(int ErrCode) {

				if (isFinishing())
					return;

				Toast.makeText(SearchTaskActivity.this, "获取路由点详情失败", Toast.LENGTH_SHORT).show();
				hideProgressBar();
			}

			@Override
			public void OnGetTag(String Reqtag) {


			}
		});

	}

}
