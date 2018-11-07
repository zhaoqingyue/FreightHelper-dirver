package com.yunbaba.freighthelper.ui.activity.task;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.OrmLiteApi;
import com.yunbaba.api.trunk.bean.GetTaskListResult;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.ui.adapter.TaskListFiveFinishAdapter;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.module.delivery.bean.MtqHistroyTask;
import com.yunbaba.ols.module.delivery.bean.MtqHistroyTask2;
import com.yunbaba.ols.module.delivery.bean.MtqHistroyTaskStack;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

//已完成任务页面
public class FinishTaskListActivity extends BaseButterKnifeActivity {

	@BindView(R.id.rv_list)
	ListView rv_list;

	@BindView(R.id.tv_title)
	TextView tv_title;

	@BindView(R.id.iv_titleleft)
	ImageView iv_titleleft;

	@BindView(R.id.iv_titleright)
	ImageView iv_titleright;

	@BindView(R.id.pb_waiting)
	PercentRelativeLayout pb_waiting;

//	TaskListAdapter mAdapter;

	// add by zhaoqy 2018-04-25
	TaskListFiveFinishAdapter mFiveAdapter;

	ArrayList<MtqHistroyTask> mTasklist;

	MyHandler mHandler;

	View emptyview;

	public static final int RequestFinishTaskList = 1;
	public static final int RequestFinishTaskListMore = 2;

	public static final int pageSize = 10;
	int pageIndex = 1;
	boolean loadFinish = false;
	int lastItem;

	private CorpBean mCurCorp;

	private int ErrorTimes = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		tv_title.setText("已完成任务");
	//	iv_titleright.setImageResource(R.drawable.icon_message);
		iv_titleright.setVisibility(View.GONE);

		mHandler = new MyHandler(this);

		mCurCorp = SPHelper.getInstance(this).ReadCurrentSelectCompanyBean();

		LayoutInflater mLayoutInflater = LayoutInflater.from(this);
		emptyview = mLayoutInflater.inflate(R.layout.view_emptyspace, null);

		rv_list.addHeaderView(emptyview, null, false);
		rv_list.addFooterView(emptyview, null, false);

		mTasklist = new ArrayList<>();
//		mAdapter = new TaskListAdapter(this, mTasklist);
//		rv_list.setAdapter(mAdapter);

		// add by zhaoqy 2018-04-26
		mFiveAdapter = new TaskListFiveFinishAdapter(this, mTasklist);
		rv_list.setAdapter(mFiveAdapter);

		if (mHandler != null)
			mHandler.sendEmptyMessage(RequestFinishTaskList);

		rv_list.setOnScrollListener(new OnScrollListener() {
			// AbsListView view 这个view对象就是listview
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						loadMore();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount - 1;
			}
		});

	}

	public void CheckIsHasGetFinishTask() {

	}

	public void GetListFromNet(final boolean isMore) {

		showProgressBar();

		if (isMore)
			pageIndex = pageIndex + 1;


		MLog.e("loadfinishtask","加载数据"+getCurrentCorpId()+" "+pageIndex+" "+pageSize);

		DeliveryApi.getInstance().getHisTaskInServer("2", getCurrentCorpId(), null,pageIndex, pageSize,
				new OnResponseResult<GetTaskListResult>() {

					@Override
					public void OnResult(GetTaskListResult result) {

						
						if(isFinishing())
							return;
						
						ErrorTimes = 0;

						MLog.e("loadfinishtask",
								result.getPage() + " " + result.getPagecount() + " " + result.getTotal());

						if (!isMore&&(result.getLstOfTask()==null||result.getLstOfTask().size()==0))
							Toast.makeText(FinishTaskListActivity.this, "未找到已完成任务", Toast.LENGTH_SHORT).show();
						
						
						
						if (result != null && result.getLstOfTask() != null) {
							// List<MtqDeliTask> lstOfTask = ;
							//mTasklist.addAll(result.getLstOfTask());

							if(mTasklist == null)
							    mTasklist = new ArrayList<>();
							MtqHistroyTask tmptask;
							for(MtqDeliTask task:result.getLstOfTask()){
								
								
								
								tmptask = new MtqHistroyTask(task);
								mTasklist.add(tmptask);
							}


							if(pageIndex == 1) {

								ThreadPoolTool.getInstance().execute(new Runnable() {
									@Override
									public void run() {
										OrmLiteApi.getInstance().deleteAll(MtqHistroyTask.class);
										OrmLiteApi.getInstance().saveAll2(mTasklist);

										OrmLiteApi.getInstance().deleteAll(MtqHistroyTaskStack.class);
										OrmLiteApi.getInstance().deleteAll(MtqHistroyTask2.class);
									}
								});

							}
                            mFiveAdapter.setList(mTasklist);
							mFiveAdapter.notifyDataSetChanged();

							if ((pageIndex) * pageSize >= result.getTotal()) {

								loadFinish = true;

							}
							hideProgressBar();
						}

					}

					@Override
					public void OnError(int ErrCode) {

						// Toast.makeText(FinishTaskListActivity.this,
						// R.string.get_data_fail, Toast.LENGTH_SHORT).show();
						
						if(isFinishing())
							return;
						MLog.e("loadfinishtask","加载数据"+ErrCode);
						ErrorTimes += 1;

						if (ErrorTimes < 3) {

							if (mHandler != null)
								mHandler.sendEmptyMessage(RequestFinishTaskList);

						} else {
							hideProgressBar();
//							if (!isMore)
//								Toast.makeText(FinishTaskListActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
//							else
//								Toast.makeText(FinishTaskListActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();

							if (!isMore) {

								ThreadPoolTool.getInstance().execute(new Runnable() {
									@Override
									public void run() {

										if(mTasklist == null)
											mTasklist = new ArrayList<MtqHistroyTask>();

										List<MtqHistroyTaskStack> res2  = OrmLiteApi.getInstance().queryAll(MtqHistroyTaskStack.class);




										if(res2!=null && res2.size()>0){

											MLog.e("history","size"+res2.size());

											Collections.reverse(res2);


											mTasklist.clear();

											for(MtqHistroyTaskStack task:res2){
												if(task.histroytask!=null) {
													MLog.e("history","add1"+task.histroytask.pdeliver);

													mTasklist.add(new MtqHistroyTask(task.histroytask));
												}
											}

										//	mTasklist.addAll((ArrayList)res2);
										}



										final List<MtqHistroyTask> res  = OrmLiteApi.getInstance().queryAll(MtqHistroyTask.class);



										if(res!=null && res.size()>0){
											//mTasklist.clear();
											mTasklist.addAll(res);
										}else{

											//Toast.makeText(FinishTaskListActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();

										}


										FinishTaskListActivity.this.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												mFiveAdapter.setList(mTasklist);
												mFiveAdapter.notifyDataSetChanged();

												if(res!=null && res.size()>0){

												}else{

													Toast.makeText(FinishTaskListActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();

												}
											}
										});




									}
								});


							}else
								Toast.makeText(FinishTaskListActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();

						}
					}

					@Override
					public void OnGetTag(String Reqtag) {

						
					}
				});

	}

	protected void loadMore() {

		// int count = mAdapter.getCount();
		// mAdapter.getCount() % 20 == 0 &&
		if (!loadFinish) {

			// 加载更多
			mHandler.sendEmptyMessage(RequestFinishTaskListMore);
			// GetListFromNet(true);
		} else {

			// 已经取完了
			mHandler.post(new Runnable() {

				@Override
				public void run() {

					Toast.makeText(FinishTaskListActivity.this, "没有更多记录了", Toast.LENGTH_SHORT).show();
				}
			});

		}

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
		WaitingProgressTool.closeshowProgress(this);
	}

	private static class MyHandler extends Handler {
		private final WeakReference<FinishTaskListActivity> mActivity;

		public MyHandler(FinishTaskListActivity activity) {
			mActivity = new WeakReference<FinishTaskListActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// System.out.println(msg);
			if (mActivity.get() == null) {
				return;
			}

			if (msg.what == RequestFinishTaskListMore)
				mActivity.get().GetListFromNet(true);
			else if (msg.what == RequestFinishTaskList)
				mActivity.get().GetListFromNet(false);
			else {

			}
			// mActivity.get().todo();
		}
	}

	public String getCurrentCorpId() {
		mCurCorp = SPHelper.getInstance(this).ReadCurrentSelectCompanyBean();
		return mCurCorp == null ? null : mCurCorp.getCorpId();

	}

	@OnClick(R.id.iv_titleleft)
	public void Finish() {

		this.finish();
	}

//	@OnItemClick(R.id.iv_titleright)
//	public void CheckMsg() {
//
//		Intent intent3 = new Intent(this, MsgActivity.class);
//		startActivity(intent3);
//
//	}

	@Override
	public int getLayoutId() {

		return R.layout.activity_finishtask;
	}

	protected void showProgressBar() {
		if (pb_waiting != null)
			pb_waiting.setVisibility(View.VISIBLE);
	}

	protected void hideProgressBar() {
		if (pb_waiting != null)
			pb_waiting.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {

		super.onResume();
	//	updateNews();
	}

//	private void updateNews() {
//		if (MsgManager.getInstance().hasUnReadMsg()) {
//			iv_titleright.setImageResource(R.drawable.msg_icon_news);
//		} else {
//			iv_titleright.setImageResource(R.drawable.msg_icon);
//		}
//	}

	@Override
	public void onStart() {
		super.onStart();
	//	EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		super.onStop();
	//	EventBus.getDefault().unregister(this);
	}

//	@Subscribe(threadMode = ThreadMode.MAIN)
//	public void onMessageEvent(NewMsgEvent event) {
//		switch (event.msgId) {
//		case MessageId.MSGID_MSG_NEW: {
//			updateNews();
//			break;
//		}
//		default:
//			break;
//		}
//	}

}
