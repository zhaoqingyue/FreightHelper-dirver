package com.yunbaba.freighthelper.ui.activity.me;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.bean.OnResponseResultContainMsg;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.bean.eventbus.OnStoreMarkClickEvent;
import com.yunbaba.freighthelper.bean.eventbus.StoreMarkSuccessEvent;
import com.yunbaba.freighthelper.ui.activity.mapselect.MapSelectPointActivity;
import com.yunbaba.freighthelper.ui.adapter.SearchAndMarkResultAdapter;
import com.yunbaba.freighthelper.ui.customview.SearchCleanEditText;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.LimitQueue;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.TimeTaskUtils;
import com.yunbaba.ols.module.delivery.CldDalKDelivery;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam;
import com.yunbaba.ols.module.delivery.bean.MtqStore;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yunbaba.freighthelper.db.DbManager.mContext;

/**
 * Created by zhonghm on 2018/6/5.
 */

public class StoreSearchAndMarkActivity extends BaseButterKnifeActivity {

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
    @BindView(R.id.rv_list)
    ListView rvList;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    LayoutInflater mLayoutInflater;

    View mHeadView, mFootView;

    SearchAndMarkResultAdapter mAdapter;

    // private Disposable mDisposable;

    public List<MtqStore> mlistdata = new ArrayList<MtqStore>();

    LimitQueue<MtqStore> recentChecklist;

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
        EventBus.getDefault().register(this);

        mCurCorp = SPHelper.getInstance(this).ReadStoreSelectCompanyBean();


        if (mCurCorp == null || TextUtils.isEmpty(mCurCorp.getCorpId())) {

            setCurrentCorp();
        }


        recentChecklist = SPHelper.getInstance(this).getRecentCheckMarkStoreList(mCurCorp.getCorpId());

        // 刷新最近搜索运货点的状态
        // RefreshStoreList();

        initHeadAndFootView();

        mlistdata = new ArrayList<MtqStore>(recentChecklist.getQueue());

        Collections.reverse(mlistdata);

        mAdapter = new SearchAndMarkResultAdapter(this, mlistdata);

        rvList.setAdapter(mAdapter);

        mAdapter.RefreshList(true);

        setHeadFootViewState(0, 0);

        setListener();
        etSearch.setHint("门店名称/编码/地址/联系人/电话");  //请输入需要标记的门店编号

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

                                setHeadFootViewState(0, 0);
                            }
                        });

                    }

                    return;
                } else {

                    //  ivSearchEmpty.setVisibility(View.GONE);
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


            }
        });

        rvList.setOnScrollListener(new AbsListView.OnScrollListener() {
            // AbsListView view 这个view对象就是listview
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        // loadMore();

                        if (!loadFinish) {
                            getTimer().clear();
                            String str = etSearch.getText().toString();
                            Search(true, str);
                        }


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
    // List<MtqStore> list =
    // TaskOperator.getInstance().GetStoreDetailListFromDB(waybilllist);
    //
    // // LinkedList<MtqStore> queue =
    // // recentChecklist.getQueue();
    //
    // if (list != null && list.size() > 0) {
    //
    // for (MtqStore stmp : list) {
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
    // // queue = new LinkedList<MtqStore>(list);
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
            getTimer().setListener(new TimeTaskUtils.OnTimerListener() {

                @Override
                public void AfterDebounce(final String str) {

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            MLog.e("seach", str);
                            synchronized (StoreSearchAndMarkActivity.this) {

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


            // 已经取完了

            if (loadFinish) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(StoreSearchAndMarkActivity.this, "没有更多记录了", Toast.LENGTH_SHORT).show();
                    }
                });

                return;
            }

            pageIndex += 1;
            start = (pageIndex - 1) * pageSize;

        }


        DeliveryApi.getInstance().SearchStore(mCurCorp.getCorpId(), key, -1, 0, pageIndex, pageSize,
                0, new OnResponseResultContainMsg<CldSapKDeliveryParam.CldDeliSearchStoreResult>() {
                    @Override
                    public void OnResult(CldSapKDeliveryParam.CldDeliSearchStoreResult result) {


                        if (!isMore) {
                            mlistdata.clear();
                            if (result.lstOfStores != null)
                                mlistdata = result.lstOfStores;
                        } else {


                            if (result.lstOfStores != null)
                                mlistdata.addAll(result.lstOfStores);
                        }


                        if ((pageIndex) * pageSize >= result.record) {

                            loadFinish = true;

                        } else {

                            loadFinish = false;
                        }

                        showResult(mlistdata, false);

                        setHeadFootViewState(1, result.record);


                    }

                    @Override
                    public void OnError(int ErrCode, String errMsg) {

                        Toast.makeText(StoreSearchAndMarkActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                        //TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode)
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

    protected void showResult(List<MtqStore> result, boolean isRecent) {
        if (!isRecent && result.isEmpty()) {
            Toast.makeText(this, "未找到匹配的结果", Toast.LENGTH_SHORT).show();

            mAdapter.setList(result);
            mAdapter.RefreshList(isRecent);

        } else if (isRecent) {
            // 最新查看的
            mlistdata = new ArrayList<MtqStore>(result);

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
        mHeadView = mLayoutInflater.inflate(R.layout.view_recent_search_head2, null);
        mFootView = mLayoutInflater.inflate(R.layout.view_search_result_foot, null);

        prl_clean_history_search = ButterKnife.findById(mFootView, R.id.prl_clean_history_search);
        tv_no_more_result_hint = ButterKnife.findById(mFootView, R.id.tv_no_more_result_hint);
        tv_recentcheck = ButterKnife.findById(mHeadView, R.id.tv_recentcheck);

        prl_clean_history_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(etSearch.getText())) {

                    recentChecklist.clear();
                    mlistdata.clear();
                    mAdapter.notifyDataSetChanged();

                    setHeadFootViewState(0, 0);
                    SPHelper.getInstance(StoreSearchAndMarkActivity.this).saveRecentCheckMarkStoreList(recentChecklist, mCurCorp.getCorpId());
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
                 tv_recentcheck.setVisibility(View.VISIBLE);
               // tv_recentcheck.setVisibility(View.GONE);
                   tv_recentcheck.setText("最近查看");
              //  tv_recentcheck.setText("");
                tv_no_more_result_hint.setVisibility(View.GONE);


            //    prl_clean_history_search.setVisibility(View.GONE);
                if (recentChecklist.size() <= 0)
                    prl_clean_history_search.setVisibility(View.GONE);
                else
                    prl_clean_history_search.setVisibility(View.VISIBLE);
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
                    tv_no_more_result_hint.setText("正在加载更多");
                    //tv_no_more_result_hint.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                } else {
                    tv_no_more_result_hint.setText(R.string.no_record_any_more);
                    tv_no_more_result_hint.getPaint().setFlags(0);
                    tv_no_more_result_hint.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
                }

                break;
            default:
                tv_recentcheck.setVisibility(View.GONE);
                tv_no_more_result_hint.setVisibility(View.GONE);
                prl_clean_history_search.setVisibility(View.GONE);
                break;
        }

    }

    @OnClick({R.id.iv_titleright})
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.rll_company:
//
//                startActivity(new Intent(this, SelectCompanyActivity.class));
//                // TransitionsHeleper.startActivity(this,
//                // SelectCompanyActivity.class, ivSelect);
//                break;
            case R.id.iv_titleright:
                finish();
                break;
        }
    }

    public boolean isContain(MtqStore obj) {

        if (recentChecklist == null)
            return true;

        if (recentChecklist.size() == 0)
            return false;

        boolean res = false;

        over:
        for (MtqStore tmp : recentChecklist.getQueue()) {

            if (tmp.storeId.equals(obj.storeId)) {

                res = true;
                recentChecklist.getQueue().remove(tmp);
                break over;
            }
        }

        if (res) {

            recentChecklist.offer(obj);
            SPHelper.getInstance(StoreSearchAndMarkActivity.this).saveRecentCheckMarkStoreList(recentChecklist, mCurCorp.getCorpId());

        }

        return res;
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OnStoreMarkClickEvent event) {

        if (event != null && event.store != null) {

            MtqStore mtqStore = event.store;

            if (mtqStore != null) {


                if (!mAdapter.isRecent() && !isContain(mtqStore)) {

                    // recentChecklist.add(storedetail);
                    recentChecklist.offer(mtqStore);
                    SPHelper.getInstance(StoreSearchAndMarkActivity.this).saveRecentCheckMarkStoreList(recentChecklist, mCurCorp.getCorpId());
                }


                Intent intent = new Intent(mContext, MapSelectPointActivity.class);
                intent.putExtra("storedetail", GsonTool.getInstance().toJson(mtqStore));
                startActivity(intent);

            } else {


                //   RefreshDetailData(order.taskId, order.corpId, order);

            }


        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent2(StoreMarkSuccessEvent event) {
        finish();
    }

    private synchronized void setCurrentCorp() {

        if (mCurCorp == null) {
            HashMap<String, String> mtmplistdata = new HashMap<>();
            over:
            for (CldSapKDeliveryParam.CldDeliGroup item : CldDalKDelivery.getInstance().getLstOfMyGroups()) {

                // mtmplistdata.put(item.corpId, item.corpName);
                if (mCurCorp == null) {
                    CorpBean bean = new CorpBean();

                    bean.setCorpId(item.corpId);
                    bean.setCorpName(item.corpName);
                    mCurCorp = bean;
                    break over;

                }

            }


        }

        if (mCurCorp == null)
            finish();


    }




}
