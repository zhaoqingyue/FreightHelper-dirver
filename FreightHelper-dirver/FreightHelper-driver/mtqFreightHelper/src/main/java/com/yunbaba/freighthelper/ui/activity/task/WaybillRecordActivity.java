package com.yunbaba.freighthelper.ui.activity.task;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunbaba.api.trunk.WaybillManager;
import com.yunbaba.api.trunk.bean.UploadGoodScanRecordResult;
import com.yunbaba.api.trunk.listner.OnObjectListner;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.ui.adapter.ScanRecordListAdapter;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class WaybillRecordActivity extends BaseButterKnifeActivity {

    @BindView(R.id.title_left_img)
    ImageView mImgBack;

    @BindView(R.id.title_text)
    TextView mTvTitle;

    @BindView(R.id.waybill_recordLv)
    ListView mLvRecord;

    private MtqDeliStoreDetail mStoreDetail;
    private MtqDeliOrderDetail mOrderDetail;

    private String mJsonStore;
    private String mJsonOrder;


    @Override
    public int getLayoutId() {

        return R.layout.activity_waybill_record;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initData();
        EventBus.getDefault().register(this);
    }

    private void initData() {
        String taskId = null;
        String cust_orderid = null;
        if (getIntent().hasExtra("taskId")) {
            taskId = getIntent().getStringExtra("taskId");
        }
        if (getIntent().hasExtra("cust_orderid")) {
            cust_orderid = getIntent().getStringExtra("cust_orderid");
        }

        if (mTvTitle != null) {
            mTvTitle.setText("查看扫描记录");
            //mTvTitle.setText("运单号" + cust_orderid);
        }

        if (mImgBack != null) {
            mImgBack.setVisibility(View.VISIBLE);
        }

        WaybillManager.getInstance().
                queryAllBySearchKey(taskId + cust_orderid, new OnObjectListner<List<UploadGoodScanRecordResult>>() {
                    @Override
                    public void onResult(List<UploadGoodScanRecordResult> res) {
                        final List<UploadGoodScanRecordResult> listResult = res;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLvRecord.setAdapter(new ScanRecordListAdapter(WaybillRecordActivity.this, listResult));
                            }
                        });
                    }
                });


    }


    @OnClick(R.id.title_left_img)
    void onReturn() {
        finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public synchronized void onTaskBusinessMsgEvent(TaskBusinessMsgEvent event) {

        switch (event.getType()) {

            // 任务刷新
            case 2:

                break;

            // 作废任务，删除某些任务
            case 3:

                if (mStoreDetail == null)
                    return;

                if (isFinishing())
                    return;

                if (event.getTaskIdList() != null && event.getTaskIdList().size() > 0) {

                    if (TextStringUtil.isContainStr(event.getTaskIdList(), mStoreDetail.taskId)) {

                        // Toast.makeText(this, "当前操作任务单已撤回",
                        // Toast.LENGTH_LONG).show();
                        finish();
                    }

                }

                break;
            case 4:
                if (mStoreDetail == null)
                    return;

                if (isFinishing())
                    return;

                if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {

                    if (mOrderDetail != null && !TextUtils.isEmpty(mOrderDetail.waybill)) {

                        if (TextStringUtil.isContain(event.getRefreshtaskIdList(), mStoreDetail.taskId, mOrderDetail.waybill)) {
                            finish();
                        }

                    }
                }

                break;

            default:
                break;
        }

    }
}
