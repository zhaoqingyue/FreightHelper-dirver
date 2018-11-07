package com.yunbaba.freighthelper.ui.activity.task.feedback;

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
import com.yunbaba.api.trunk.bean.GetFeedBackListResult;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBack;
import com.squareup.picasso.Picasso;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

public class FeedBackActivity extends BaseButterKnifeActivity {

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

	FeedBackListAdapter mAdapter;

	ArrayList<FeedBack> mTasklist;

	MyHandler mHandler;

	View emptyview;

	public static final int RequestFinishTaskList = 1;
	public static final int RequestFinishTaskListMore = 2;

	public static final int pageSize = 10;
	int pageIndex = 1;
	boolean loadFinish = false;
	int lastItem;
	String corpid;
	String taskid;
	String orderid;

	private int ErrorTimes = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (getIntent() == null || !getIntent().hasExtra("corpid") || !getIntent().hasExtra("taskid")|| !getIntent().hasExtra("orderid")) {

			finish();
		} else {

			if (getIntent().hasExtra("corpid"))
				corpid = getIntent().getStringExtra("corpid");

			if (getIntent().hasExtra("taskid"))
				taskid = getIntent().getStringExtra("taskid");
			
			if (getIntent().hasExtra("orderid"))
				orderid = getIntent().getStringExtra("orderid");

		}

		tv_title.setText("我的反馈");
		// iv_titleright.setImageResource(R.drawable.icon_message);
		iv_titleright.setVisibility(View.GONE);

		mHandler = new MyHandler(this);

		LayoutInflater mLayoutInflater = LayoutInflater.from(this);
		emptyview = mLayoutInflater.inflate(R.layout.view_emptyspace, null);

		rv_list.addHeaderView(emptyview, null, false);
		rv_list.addFooterView(emptyview, null, false);

		mTasklist = new ArrayList<>();
		mAdapter = new FeedBackListAdapter(this, mTasklist, corpid, taskid);

		rv_list.setAdapter(mAdapter);

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

				final Picasso picasso = Picasso.with(context);
				if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
					//如果在暂停或者触摸的情况下完成重置
					picasso.resumeTag(context);
				} else {
					//停止更新
					picasso.pauseTag(context);
				}


			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount - 1;
			}
		});
	}

	public void GetListFromNet(final boolean isMore) {

		showProgressBar();

		if (isMore)
			pageIndex = pageIndex + 1;

		DeliveryApi.getInstance().getFeedBackListInServer(taskid, corpid,orderid ,pageIndex, pageSize,
				new OnResponseResult<GetFeedBackListResult>() {

					@Override
					public void OnResult(GetFeedBackListResult result) {


						if (isFinishing())
							return;

						ErrorTimes = 0;

						MLog.d("loadfeedback", result.count + " " + GsonTool.getInstance().toJson(result.listOfRst));

						if (!isMore && (result.listOfRst == null || result.listOfRst.size() == 0))
							Toast.makeText(FeedBackActivity.this, "未找到反馈记录", Toast.LENGTH_SHORT).show();

						if (result != null && result != null) {
							// List<MtqDeliTask> lstOfTask = ;
							mTasklist.addAll(result.listOfRst);

							// ArrayList<MtqHistroyTask> list = new
							// ArrayList<>();
							// MtqHistroyTask tmptask;
							// for (MtqDeliTask task : result.listOfRst) {
							//
							// tmptask = new MtqHistroyTask(task);
							// list.add(tmptask);
							// }
							//
							// OrmLiteApi.getInstance().saveAll(list);

							mAdapter.notifyDataSetChanged();

							if ((pageIndex) * pageSize >= result.count) {

								loadFinish = true;

							}
							hideProgressBar();
						}

					}

					@Override
					public void OnError(int ErrCode) {

						// Toast.makeText(FeedBackActivity.this,
						// R.string.get_data_fail, Toast.LENGTH_SHORT).show();

						if (isFinishing())
							return;

						ErrorTimes += 1;

						if (ErrorTimes < 3) {

							if (mHandler != null)
								mHandler.sendEmptyMessage(RequestFinishTaskList);

						} else {
							hideProgressBar();
							if (!isMore)
								Toast.makeText(FeedBackActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
							else
								Toast.makeText(FeedBackActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
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

					Toast.makeText(FeedBackActivity.this, "没有更多记录了", Toast.LENGTH_SHORT).show();
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
		private final WeakReference<FeedBackActivity> mActivity;

		public MyHandler(FeedBackActivity feedBackActivity) {
			mActivity = new WeakReference<FeedBackActivity>(feedBackActivity);
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

	@OnClick(R.id.iv_titleleft)
	public void Finish() {

		this.finish();
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
		// updateNews();
	}

	@Override
	public void onStart() {
		super.onStart();
		// EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		// EventBus.getDefault().unregister(this);
	}

	@Override
	public int getLayoutId() {

		return R.layout.activity_feedbacklist;
	}

}
