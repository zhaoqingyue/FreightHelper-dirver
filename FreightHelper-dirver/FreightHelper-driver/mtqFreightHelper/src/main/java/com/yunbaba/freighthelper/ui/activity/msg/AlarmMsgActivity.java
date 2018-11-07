/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: AlarmMsgActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.msg
 * @Description: 报警消息界面
 * @author: zhaoqy
 * @date: 2017年3月22日 下午3:15:25
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.msg;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.bean.eventbus.NewMsgEvent;
import com.yunbaba.freighthelper.bean.msg.Filter;
import com.yunbaba.freighthelper.bean.msg.MsgInfo;
import com.yunbaba.freighthelper.db.MsgInfoTable;
import com.yunbaba.freighthelper.manager.MsgManager;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.ui.adapter.AlarmMsgAdapter;
import com.yunbaba.freighthelper.ui.dialog.FilterAlarmDailog;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class AlarmMsgActivity extends BaseActivity implements OnClickListener {

    private ImageView mBack;
    private TextView mTitle;
    private TextView mFilter;
    private ListView mListView;
    private View mEmptyView;
    private View mWaitting;
    private AlarmMsgAdapter mAdapter;
    private ArrayList<MsgInfo> mMsgList;
    private FilterAlarmDailog mDialog;
    private List<Filter> mFilters = new ArrayList<Filter>();
    public static final int pageSize = 10;
    int pageIndex = 1;
    boolean loadFinish = false;

    PercentRelativeLayout pbWaiting;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_msg_alarm_msg;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initViews() {
        mBack = (ImageView) findViewById(R.id.title_left_img);
        mTitle = (TextView) findViewById(R.id.title_text);
        mFilter = (TextView) findViewById(R.id.title_right_text);
        mListView = (ListView) findViewById(R.id.alarm_msg_listview);
        mWaitting = findViewById(R.id.larm_msg_waiting);
        pbWaiting = (PercentRelativeLayout) findViewById(R.id.pb_waiting);
        LayoutInflater mLayoutInflater = LayoutInflater.from(this);
        mEmptyView = mLayoutInflater.inflate(R.layout.view_empty_filter_msg,
                null);
        mEmptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        mEmptyView.setVisibility(View.GONE);
        ((ViewGroup) mListView.getParent()).addView(mEmptyView);
        mListView.setEmptyView(mEmptyView);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            // AbsListView view 这个view对象就是listview
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        loadMore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);
        mFilter.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mBack.setVisibility(View.VISIBLE);
        mTitle.setText(R.string.msg_alarm_msg);
        mFilter.setVisibility(View.VISIBLE);
        mFilter.setText(R.string.msg_filter);
        mFilter.setTextColor(getResources().getColor(R.color.app_color2));
    }

    @Override
    protected void loadData() {
        Intent intent = getIntent();
        boolean extra = intent.getBooleanExtra("extra", false);
        //  MLog.e("test", "extra: " + extra);

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                mMsgList = MsgManager.getInstance().queryMsg2(MsgManager.MSG_ALARM, 1, pageSize);
                pageIndex = 1;
                loadFinish = false;

                updateUI(mMsgList);
                AlarmMsgActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLog.e("checkmsgsize", "size" + mMsgList.size());
                        mAdapter = new AlarmMsgAdapter(AlarmMsgActivity.this, mMsgList);
                        mListView.setAdapter(mAdapter);
                    }
                });


            }
        });


    }


    protected void updateUI(List<MsgInfo> mlist) {


//        ThreadPoolTool.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {

        if (mlist == null || mlist.isEmpty())
            return;

        int len = mlist.size();
        for (int i = 0; i < len; i++) {
            MsgInfo msginfo = mlist.get(i);
            if (msginfo.getReadMark() != 3) {
                MsgInfoTable.getInstance().update(msginfo);
            }
        }

        //   }
//        });
    }

    @Override
    protected void updateUI() {


        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                if (mMsgList == null || mMsgList.isEmpty())
                    return;

                int len = mMsgList.size();
                for (int i = 0; i < len; i++) {
                    MsgInfo msginfo = mMsgList.get(i);
                    if (msginfo.getReadMark() != 3) {
                        MsgInfoTable.getInstance().update(msginfo);
                    }
                }

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ThreadPoolTool.getInstance().execute(update);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewMsgEvent event) {
        switch (event.msgId) {
            case MessageId.MSGID_MSG_NEW: {
                if (mFilters != null && mFilters.size() > 0) {
                    ThreadPoolTool.getInstance().execute(updateandfilter);
                } else
                    ThreadPoolTool.getInstance().execute(update);
                break;
            }
            default:
                break;
        }
    }

    List<MsgInfo> temp;

    private Runnable updateandfilter = new Runnable() {

        @Override
        public void run() {
            if (temp != null) {
                temp.clear();
            }
//            temp = MsgManager.getInstance().queryMsg2(MsgManager.MSG_ALARM,pageIndex,pageSize);
//            // mHandler.sendEmptyMessage(3);
//
//            if (temp != null) {
//                temp.clear();
//            }
            temp = MsgManager.getInstance().getFilterMsg(MsgManager.MSG_ALARM,
                    mFilters, 1, pageSize);
            mHandler.sendEmptyMessage(0);
        }
    };

    private Runnable update = new Runnable() {

        @Override
        public void run() {
            if (temp != null) {
                temp.clear();
            }
            temp = MsgManager.getInstance().queryMsg2(MsgManager.MSG_ALARM, 1, pageSize);
            mHandler.sendEmptyMessage(2);
        }
    };

    private Runnable filter = new Runnable() {

        @Override
        public void run() {
            if (temp != null) {
                temp.clear();
            }
            temp = MsgManager.getInstance().getFilterMsg(MsgManager.MSG_ALARM,
                    mFilters, 1, pageSize);
            mHandler.sendEmptyMessage(0);
        }
    };

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0: {
                    mMsgList.clear();
                    mMsgList.addAll(temp);
                    pageIndex = 1;
                    loadFinish = false;
                    mListView.requestLayout();
                    mAdapter.notifyDataSetChanged();
                    hideProgressBar();
                    updateUI(mMsgList);
                    break;
                }
                case 2: {
                    mMsgList.clear();
                    mMsgList.addAll(temp);
                    pageIndex = 1;
                    loadFinish = false;
                    mListView.requestLayout();
                    mAdapter.notifyDataSetChanged();
                    updateUI(mMsgList);
                    break;
                }
                default:
                    break;
            }
        }
    };

    protected void showProgressBar() {
        WaitingProgressTool.showProgress(this);
    }

    protected void hideProgressBar() {
        WaitingProgressTool.closeshowProgress();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img: {
                finish();
                break;
            }
            case R.id.title_right_text: {
                if (mDialog == null) {
                    mDialog = new FilterAlarmDailog(this, new FilterAlarmDailog.IFiltertener() {

                        @Override
                        public void OnFinish(List<Filter> filters) {
                            showProgressBar();
                            mFilters.clear();
                            mFilters.addAll(filters);
                            ThreadPoolTool.getInstance().execute(filter);
                        }
                    });
                }
                mDialog.show();
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void messageEvent(AccountEvent event) {
        switch (event.msgId) {
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mFilters.clear();
        EventBus.getDefault().post(new NewMsgEvent(MessageId.MSGID_MSG_UPDATE));
    }

    public void showProgressBar2() {

        if (pbWaiting != null)
            pbWaiting.setVisibility(View.VISIBLE);

    }

    public void hideProgressBar2() {
        if (pbWaiting != null)
            pbWaiting.setVisibility(View.GONE);
    }


    protected void loadMore() {

        if (!loadFinish) {

            // 加载更多


            showProgressBar2();

            pageIndex++;


            ThreadPoolTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    //   MLog.e("checkmsgsize","sizepageindex"+ pageIndex+","+ pageSize);

                   List<MsgInfo> lists;

                    if (mFilters != null && mFilters.size() > 0) {


                        lists = MsgManager.getInstance().getFilterMsg(MsgManager.MSG_ALARM,mFilters, pageIndex, pageSize,
                                (mMsgList!=null?mMsgList.size():0));
                    } else {

                        lists =  MsgManager.getInstance().queryMsg2(MsgManager.MSG_ALARM, pageIndex, pageSize);

                    }

                    final  List<MsgInfo> list = lists;



                    //  MLog.e("checkmsgsize","getsize"+list.size());

                    if (list != null) {
                        mMsgList.addAll(list);
                        updateUI(list);
                    }
                    //  MLog.e("checkmsgsize","finalsize"+mMsgList.size());


                    AlarmMsgActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            hideProgressBar2();
                            //int count = mAdapter.getCount();
                            if (mAdapter.getCount() % 10 != 0 || list == null || list.size() == 0) {


                                loadFinish = true;

                            } else {


                                loadFinish = false;

                            }

                            mAdapter.notifyDataSetChanged();

                        }
                    });


                }
            });


        } else {
            Toast.makeText(AlarmMsgActivity.this, "没有更多记录了", Toast.LENGTH_SHORT).show();
            // 已经取完了


        }

    }

}
