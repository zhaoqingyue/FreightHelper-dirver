package com.yunbaba.freighthelper.ui.activity.task;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.bean.GetTaskListResult;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.ui.adapter.TaskListFiveAdapter;
import com.yunbaba.freighthelper.ui.customview.SearchCleanEditText;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TimeTaskUtils;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhonghm on 2018/5/31.
 */

public class SearchTaskListActivity extends BaseButterKnifeActivity {
    //    @BindView(R.id.tv_company)
//    TextView tvCompany;
//    @BindView(R.id.iv_select)
//    ImageView ivSelect;
//    @BindView(R.id.rll_company)
//    PercentRelativeLayout rllCompany;
    @BindView(R.id.et_search)
    SearchCleanEditText etSearch;
    @BindView(R.id.iv_search_empty)
    ImageView ivSearchEmpty;
    @BindView(R.id.iv_titleright)
    TextView ivTitleright;

    @BindView(R.id.iv_titleleft)
    ImageView iv_titleleft;
    @BindView(R.id.rv_list)
    ListView rvList;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    LayoutInflater mLayoutInflater;

    View mHeadView, mFootView;

    TaskListFiveAdapter mAdapter;

    MtqDeliTaskDetail mTaskDetail;
    // private Disposable mDisposable;

    public List<MtqDeliTask> mlistdata = new ArrayList<MtqDeliTask>();

    //  LimitQueue<MtqDeliTask> recentChecklist;

    public static final int pageSize = 10;
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

        //  mCurCorp = SPHelper.getInstance(this).ReadCurrentSelectCompanyBean();

        setCurrentCorp();

        //recentChecklist = SPHelper.getInstance(this).getRecentRecentCheckTaskList();

        // 刷新最近搜索运货点的状态
        //   RefreshStoreList();

        initHeadAndFootView();

        //  mlistdata = new ArrayList<MtqDeliTask>(recentChecklist.getQueue());

        mlistdata = new ArrayList<MtqDeliTask>();

      //  Collections.reverse(mlistdata);

        mAdapter = new TaskListFiveAdapter(this, mlistdata);

        rvList.setAdapter(mAdapter);

        mAdapter.RefreshList(true);

        setHeadFootViewState(0, 0);

        setListener();

        //rllCompany.setVisibility(View.INVISIBLE);
        etSearch.setHint("任务单号");
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
                    setHeadFootViewState(0,0);


                    mlistdata.clear();
                    mAdapter.notifyDataSetChanged();

//                    if (recentChecklist != null) {
//
//                        mHandler.post(new Runnable() {
//
//                            @Override
//                            public void run() {
//
//                                getTimer().clear();
//
//                                showResult(recentChecklist.getQueue(), true);
//
//                                setHeadFootViewState(0);
//                            }
//                        });
//
//                    }

                    return;
                } else {

                    ivSearchEmpty.setVisibility(View.VISIBLE);

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

        rvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


                // MLog.e("seach", "additem"+arg2);

                if (arg2 < 1)
                    return;

                int realposition = arg2 - 1;

                MtqDeliTask order = mAdapter.getItem(realposition);


                if (order != null) {


//                    MLog.e("check", "taskdetail not null");
//
//                    if (!mAdapter.isRecent() && !isContain(order)) {
//
//                        // recentChecklist.add(storedetail);
//                        recentChecklist.offer(order);
//                        SPHelper.getInstance(SearchTaskListActivity.this). saveRecentCheckTaskList(recentChecklist);
//                    }

                    Intent intent = new Intent(SearchTaskListActivity.this, FreightPointActivity.class);
                    intent.putExtra("corpid", order.corpid);
                    intent.putExtra("taskid", order.taskid);
                    startActivity(intent);
                 //   finish();
                } else {

                    //   RefreshDetailData(order.taskId, order.corpId, order);

                }
            }
        });

        rvList.setOnScrollListener(new AbsListView.OnScrollListener() {
            // AbsListView view 这个view对象就是listview
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
//                        // loadMore();
//
//
////                        new Handler().postDelayed(new Runnable() {
////
////                            @Override
////                            public void run() {
////
////
////                            }
////                        }, 700);
//
////                         getTimer().clear();
////                        String str = etSearch.getText().toString();
////                        Search(true, str);
//
//                    }
//                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
              //  lastItem = firstVisibleItem + visibleItemCount - 1;
            }
        });

        etSearch.setFocusable(true);
        etSearch.setFocusableInTouchMode(true);
        etSearch.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

//    // 刷新最近搜索运货点的状态,从数据库拉取
//    private void RefreshStoreList() {
//
//
//        ThreadPoolTool.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//
//                    LinkedList<MtqDeliTask> list =   recentChecklist.getQueue();
//
//                    if (list != null && list.size() > 0) {
//
//                        for (MtqDeliTask stmp : list) {
//
//                            if(waybilllist.containsKey(stmp.waybill)){
//
//                                recentChecklist.getQueue().set(waybilllist.get(stmp.waybill), stmp);
//
//                            }
//
//
////                    over:
////                    for (int i = 0; i < recentChecklist.getQueue().size(); i++) {
////
////                        if (stmp.waybill.equals(recentChecklist.getQueue().get(i).waybill))
////
////                        {
////                            recentChecklist.getQueue().set(i, stmp);
////
////                            break over;
////                        }
////                    }
//
//                        }
//
//                        // recentChecklist.resetList(list);
//
//                    }
//
//                    // queue = new LinkedList<MtqDeliTask>(list);
//
//                } catch (Exception e) {
//                    // TODO: handle exception
//                }
//            }
//        });
//
//
//
//    }

    private void loadMore() {


    }

    TimeTaskUtils timer;

    public synchronized TimeTaskUtils getTimer() {

        if (timer == null)
            timer = new TimeTaskUtils();

        return timer;

    }

    public void setListener() {

        if (getTimer().getListener() == null) {
            getTimer().setListener(new TimeTaskUtils.OnTimerListener() {

                @Override
                public void AfterDebounce(final String str) {

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            MLog.e("seach", str);
                            synchronized (SearchTaskListActivity.this) {

                                Search(false, str);
                            }

                        }
                    });

                }
            });
        }
    }

    public synchronized void Search(final boolean isMore, final String key) {

        if (TextUtils.isEmpty(etSearch.getText()))
            return;

        int start = 0;
        // int length = 20;

        if (!isMore) {
            pageIndex = 1;
            loadFinish = false;

        } else {

            pageIndex += 1;
            start = (pageIndex - 1) * 10;

        }

        // MLog.e("curcorpid", getCurrentCorpId() + mCurCorp.getCorpName());


        DeliveryApi.getInstance().getHisTaskInServer("0|1|2|3|4", null, key, pageIndex, pageSize, new OnResponseResult<GetTaskListResult>() {
            @Override
            public void OnResult(GetTaskListResult result) {


                if (result.getErrCode() == 0) {


                    if (!isMore) {
                        mlistdata.clear();

                        mlistdata = result.getLstOfTask();
                    } else {

                        mlistdata.addAll(result.getLstOfTask());
                    }


                    if ((pageIndex) * pageSize >= result.getTotal()) {

                        loadFinish = true;

                    } else {

                        loadFinish = false;
                    }

                    showResult(mlistdata, false);

                    setHeadFootViewState(1, result.getTotal());


                } else {


                    if (!isMore) {
                        mlistdata.clear();

                        loadFinish = true;

                        showResult(mlistdata, false);

                        setHeadFootViewState(0, 0);
                    }
                    Toast.makeText(SearchTaskListActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void OnError(int ErrCode) {

                Toast.makeText(SearchTaskListActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void OnGetTag(String Reqtag) {

            }
        });


    }

    protected void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    protected void showResult(List<MtqDeliTask> result, boolean isRecent) {
        if (!isRecent && result.isEmpty()) {
            Toast.makeText(this, "未找到匹配的结果", Toast.LENGTH_SHORT).show();

            mAdapter.setList(result);
            mAdapter.RefreshList(isRecent);

        } else if (isRecent) {
            // 最新查看的
            mlistdata.clear();// = new ArrayList<MtqDeliTask>();

           // Collections.reverse(mlistdata);

            mAdapter.setList(mlistdata);
            //    mAdapter.setKeyword("");
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
        mHeadView = mLayoutInflater.inflate(R.layout.view_recent_search_head2, null);
        mFootView = mLayoutInflater.inflate(R.layout.view_search_result_foot2, null);

        prl_clean_history_search = ButterKnife.findById(mFootView, R.id.prl_clean_history_search);
        tv_no_more_result_hint = ButterKnife.findById(mFootView, R.id.tv_no_more_result_hint);
        tv_recentcheck = ButterKnife.findById(mHeadView, R.id.tv_recentcheck);

//        prl_clean_history_search.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//
//                if (TextUtils.isEmpty(etSearch.getText())) {
//
//                  //  recentChecklist.clear();
//                    mlistdata.clear();
//                    mAdapter.notifyDataSetChanged();
//                 //   mAdapter.setKeyword("");
//                    setHeadFootViewState(0,0);
//                  //  SPHelper.getInstance(SearchTaskListActivity.this).saveRecentCheckTaskList(recentChecklist);
//                }
//            }
//        });

        tv_no_more_result_hint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (!TextUtils.isEmpty(etSearch.getText()) && !loadFinish) {


                    getTimer().clear();
                    String str = etSearch.getText().toString();
                    Search(true, str);
                }
            }
        });


        rvList.addHeaderView(mHeadView, null, false);
        rvList.addFooterView(mFootView, null, false);

    }

    /**
     * 0 默认 最新查看 ; 1 搜索结果 ;
     */
    public void setHeadFootViewState(int type, int num) {

        switch (type) {
            case 0:
                //  tv_recentcheck.setVisibility(View.VISIBLE);
                tv_recentcheck.setVisibility(View.GONE);
                //     tv_recentcheck.setText("最近查看");
                tv_recentcheck.setText("");
                tv_no_more_result_hint.setVisibility(View.GONE);


                prl_clean_history_search.setVisibility(View.GONE);
//                if (recentChecklist.size() <= 0)
//                    prl_clean_history_search.setVisibility(View.GONE);
//                else
//                    prl_clean_history_search.setVisibility(View.VISIBLE);
                break;
            case 1:
                tv_recentcheck.setVisibility(View.VISIBLE);

                tv_recentcheck.setText(FreightLogicTool.getSearchResultCountHint(num));


                prl_clean_history_search.setVisibility(View.GONE);

                if (mlistdata == null || mlistdata.size() == 0) {
                    tv_no_more_result_hint.setVisibility(View.GONE);
                } else
                    tv_no_more_result_hint.setVisibility(View.VISIBLE);

                if (!loadFinish) {
                    tv_no_more_result_hint.setText("点击查看更多");
                    tv_no_more_result_hint.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                } else {
                    tv_no_more_result_hint.setText(R.string.no_record_any_more);
                    tv_no_more_result_hint.getPaint().setFlags(0);
                    tv_no_more_result_hint.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
                }

                break;
            default:
                tv_recentcheck.setVisibility(View.VISIBLE);
                tv_no_more_result_hint.setVisibility(View.GONE);
                prl_clean_history_search.setVisibility(View.VISIBLE);
                break;
        }

    }

    @OnClick({R.id.iv_titleleft, R.id.iv_titleright})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rll_company:

                startActivity(new Intent(this, SelectCompanyActivity.class));
                // TransitionsHeleper.startActivity(this,
                // SelectCompanyActivity.class, ivSelect);
                break;
            case R.id.iv_titleleft:
                finish();
                break;
            case R.id.iv_titleright:
                finish();
                break;
        }
    }

//    public boolean isContain(MtqDeliTask obj) {
//
//        if (recentChecklist == null)
//            return true;
//
//        if (recentChecklist.size() == 0)
//            return false;
//
//        boolean res = false;
//
//        over:
//        for (MtqDeliTask tmp : recentChecklist.getQueue()) {
//
//            if (tmp.taskid.equals(obj.taskid) ) {
//
//                res = true;
//                recentChecklist.getQueue().remove(tmp);
//                break over;
//            }
//        }
//
//        if (res) {
//
//            recentChecklist.offer(obj);
//            SPHelper.getInstance(SearchTaskListActivity.this).saveRecentCheckTaskList(recentChecklist);
//
//        }
//
//        return res;
//    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        //   EventBus.getDefault().unregister(this);
    }

    private synchronized void setCurrentCorp() {


    }


}