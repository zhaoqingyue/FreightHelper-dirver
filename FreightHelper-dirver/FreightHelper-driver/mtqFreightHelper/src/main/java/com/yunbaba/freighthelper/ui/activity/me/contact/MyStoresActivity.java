package com.yunbaba.freighthelper.ui.activity.me.contact;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.bean.OnResponseResultContainMsg;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.ui.activity.me.StoreSearchAndMarkActivity;
import com.yunbaba.freighthelper.ui.adapter.MyStoreListAdapter;
import com.yunbaba.freighthelper.ui.customview.ClearEditGrayText;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.TimeTaskUtils;
import com.yunbaba.freighthelper.utils.TimestampTool;
import com.yunbaba.freighthelper.utils.ToastUtils;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

public class MyStoresActivity extends BaseButterKnifeActivity {

    private static final int APPROVE_FILTER_PENDDING = 0;
    private static final int APPROVE_FILTER_APPROVED = 1;
    private static final int APPROVE_FILTER_NOT_APPROVED = 2;
    private static final int APPROVE_FILTER_NO_LIMITED = -1;

    private static final int DATE_FITLER_TODAY = 0;
    private static final int DATE_FILTER_YESTERDAY = 1;
    private static final int DATE_FILTER_ONE_WEEK = 2;
    private static final int DATE_FILTER_THREE_MONTHS = 3;

    private static final int TYPE_UPDATE_GENERAL = 0;
    private static final int TYPE_UPDATE_SEARCH_KEYWORD = 1;
    private static final int TYPE_UPDATE_LOAD_MORE = 2;

    CorpBean corpBean;

    MyStoreListAdapter adapter;

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.tv_add)
    TextView tvAdd;

    @BindView(R.id.et_search)
    ClearEditGrayText etSearch;

    @BindView(R.id.sp_my_stores_date_filter)
    NiceSpinner spDateFilter;

    @BindView(R.id.sp_my_stores_approve_filter)
    NiceSpinner spApproveFilter;

    @BindView(R.id.lv_my_stores_list)
    ListView lvContent;

    @BindView(R.id.prl_ms_processing)
    PercentRelativeLayout prlProcessing;

    @BindView(R.id.tv_ms_hint_err)
    PercentLinearLayout tvHintErr;

    @BindArray(R.array.Date)
    String[] lstDateFilterItem;

    @BindArray(R.array.StoresApprove)
    String[] lstStoresApproveFilterItem;

    int currentDateFilterItem;
    int currentApproveFilterItem;

    TimeTaskUtils timer;
    CldSapKDeliveryParam.CldDeliGetMyMarkStoreResult preResult;

    int pageLoaded = 1;
    String mKeyword = "";

    boolean isSearchAction = false;

    @OnClick({R.id.iv_back, R.id.tv_add})
    protected void onClick(View view) {

        switch (view.getId()) {

            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_add:
                // TODO: 2018-06-04 Add
                startActivity(new Intent(this, StoreSearchAndMarkActivity.class));

                break;

        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_stores;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spDateFilter.attachDataSource(Arrays.asList(lstDateFilterItem));
        spApproveFilter.attachDataSource(Arrays.asList(lstStoresApproveFilterItem));

        adapter = new MyStoreListAdapter(this);

        lvContent.setAdapter(adapter);
//        lvContent.setDivider(getResources().getDrawable(R.color.transparent));
//        lvContent.setDividerHeight(DensityUtils.dip2px(getApplicationContext(), 18));

        corpBean = SPHelper.getInstance(this).ReadStoreSelectCompanyBean();

        currentDateFilterItem = DATE_FITLER_TODAY;
        currentApproveFilterItem = APPROVE_FILTER_PENDDING;

        spDateFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDateFilterItem = position;

                if (etSearch.getText() == null || TextUtils.isEmpty(etSearch.getText().toString())) {

                    generalUpdateData(position, currentApproveFilterItem);

                } else {

                    clearSearchBarContent();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spApproveFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentApproveFilterItem = position;

                if (etSearch.getText() == null || TextUtils.isEmpty(etSearch.getText().toString())) {

                    generalUpdateData(currentDateFilterItem, position);

                } else {

                    clearSearchBarContent();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etSearch.setText("");

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

//                if (etSearch.getText() == null || TextUtils.isEmpty(etSearch.getText().toString())) {
//
//                    mKeyword = "";
//
//
//                } else {
                    mKeyword = etSearch.getText().toString();

                    getTimer().NewInput(mKeyword);

                    setListener();


//                }

            }
        });

        lvContent.setOnScrollListener(new AbsListView.OnScrollListener() {
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
    protected void onStart() {
        super.onStart();

        generalUpdateData(currentDateFilterItem, currentApproveFilterItem);
    }

    private void searchData(String keyword) {
        updateData(TYPE_UPDATE_SEARCH_KEYWORD, mKeyword, currentDateFilterItem, currentApproveFilterItem);
    }

    private void loadMoreData() {
        updateData(TYPE_UPDATE_LOAD_MORE, mKeyword, currentDateFilterItem, currentApproveFilterItem);
    }

    private void generalUpdateData(int endTime, int approveStatus) {
        updateData(TYPE_UPDATE_GENERAL, "", endTime, approveStatus);
    }

    private void updateData(final int type, String keyword, int endTime, int approveStatus) {

        int loadPage = 1;

        int currnetTimeStampS = (int) ((System.currentTimeMillis()) / 1000);
        int todayZeroTimeStampS = (int) (TimestampTool.getTodayZeroTimeStamp() / 1000);
        int yesterdayTimeStampS = todayZeroTimeStampS - 24 * 60 * 60;
        int oneWeekTimeStamps = currnetTimeStampS - 7 * 24 * 60 * 60;
        int threeMonthsTimeStampS = currnetTimeStampS - 90 * 24 * 60 * 60;

        int startTimeStampS = currnetTimeStampS;
        int endTimeStampS = todayZeroTimeStampS;

        if (type != TYPE_UPDATE_LOAD_MORE) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showProgressBar();
                }
            });
        }


        switch (endTime) {
            case DATE_FITLER_TODAY:
                endTimeStampS = todayZeroTimeStampS;
                break;

            case DATE_FILTER_YESTERDAY:
                startTimeStampS = todayZeroTimeStampS;
                endTimeStampS = yesterdayTimeStampS;
                break;

            case DATE_FILTER_ONE_WEEK:
                endTimeStampS = oneWeekTimeStamps;
                break;

            case DATE_FILTER_THREE_MONTHS:
                endTimeStampS = threeMonthsTimeStampS;
                break;
        }

        if (type == TYPE_UPDATE_LOAD_MORE) {
            loadPage = ++pageLoaded;
        } else {
            loadPage = 1;
        }

        DeliveryApi.getInstance().GetMyMarkStoreRecord(corpBean.getCorpId(), keyword, 0,
                endTimeStampS, startTimeStampS, approveStatus, loadPage, 20, new OnResponseResultContainMsg<CldSapKDeliveryParam.CldDeliGetMyMarkStoreResult>() {
                    @Override
                    public void OnResult(CldSapKDeliveryParam.CldDeliGetMyMarkStoreResult result) {

                        if (result.record == 0) {

                            if (type == TYPE_UPDATE_SEARCH_KEYWORD) {
                                ToastUtils.showMessage(getApplicationContext(), "没有搜索到相关门店");
                            } else {
                            }

                            showHintErrContent();
                        } else {

                            hideHintErrContent();

                            if (type == TYPE_UPDATE_LOAD_MORE) {
                                adapter.addDataSet(result.lstOfRecord);
                            } else {
                                adapter.updateData(result.lstOfRecord);
                                pageLoaded = 1;
                            }

                            preResult = result;
                        }

                        hideProgressBar();
                    }

                    @Override
                    public void OnError(int ErrCode, String ErrMsg) {
                        hideProgressBar();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {
//                        hideProgressBar();
                    }
                });

    }

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

                    DoSearch(str);

                }
            });
        }
    }

    public synchronized void DoSearch(final String str) {
//        if (etSearch != null && !TextUtils.isEmpty(etSearch.getText()) && str.equals(etSearch.getText().toString())) {

            searchData(str);

//        }
    }

    public void loadMore() {

        if (pageLoaded + 1 > preResult.pagecount) {

            ToastUtils.showMessage(getApplicationContext(), "所有门店已全部加载");

        } else {

//            pageLoaded++;
            loadMoreData();
        }
    }

    private void clearSearchBarContent() {
        etSearch.setText("");
    }

    public void showProgressBar() {

        if (prlProcessing != null)
            prlProcessing.setVisibility(View.VISIBLE);

    }

    public void hideProgressBar() {
        if (prlProcessing != null)
            prlProcessing.setVisibility(View.GONE);
    }

    private void showHintErrContent() {
        tvHintErr.setVisibility(View.VISIBLE);
    }

    private void hideHintErrContent() {
        tvHintErr.setVisibility(View.GONE);
    }

}
