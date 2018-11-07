package com.yunbaba.freighthelper.ui.activity.task;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.navisdk.routeplan.CldRoutePlaner.RoutePlanListener;
import com.yunbaba.api.map.NavigateAPI;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.DeliveryRouteAPI;
import com.yunbaba.api.trunk.NavigateTaskOperator;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.TaskOperator.OnDialogListener;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.api.trunk.bean.UpdateTaskPointStatusResult;
import com.yunbaba.api.trunk.bean.UpdateTaskStatusResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.car.TruckCarParams;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointStatusRefreshEvent;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointUpdateEvent;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.bean.eventbus.UpdateTaskStatusEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.constant.MAP_HC_RESULT;
import com.yunbaba.freighthelper.ui.activity.RoutePreviewActivity;
import com.yunbaba.freighthelper.ui.adapter.TaskSelectPointAdapter;
import com.yunbaba.freighthelper.ui.customview.TaskAskPopUpDialog;
import com.yunbaba.freighthelper.ui.customview.TaskDialoginNavigation.NavigationDlgType;
import com.yunbaba.freighthelper.utils.CommonTool;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// 选择货运点列表
public class SelectTransPointActivity extends BaseButterKnifeActivity {

    @BindView(R.id.iv_titleleft)
    ImageView ivTitleleft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_titleright)
    ImageView ivTitleright;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_checktask)
    TextView tvChecktask;
    @BindView(R.id.rv_list)
    ListView rvList;
    @BindView(R.id.iv_check)
    ImageView ivCheck;
    @BindView(R.id.pll_check)
    PercentLinearLayout pllCheck;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_transgood)
    TextView tvTransgood;

    ArrayList<MtqDeliStoreDetail> mlistdata;

    TaskSelectPointAdapter mAdapter;
    @BindView(R.id.pb_waiting)
    ProgressBar pb_waiting;

    @BindView(R.id.pb_waiting2)
    PercentRelativeLayout pb_waiting2;

    MtqDeliTaskDetail mTaskDetail;
    String corpid = null, taskid = null;

    @Override
    public int getLayoutId() {

        return R.layout.activity_selectpoint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        if (getIntent() != null) {
            corpid = getIntent().getStringExtra("corpid");
            taskid = getIntent().getStringExtra("taskid");

        }

        if (corpid == null || taskid == null)
            finish();

        tvChecktask.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

//		if (TaskOperator.getInstance().getmCurrentTask() == null
//				|| !TaskOperator.getInstance().getmCurrentTask().taskid.equals(taskid)) {

        tvTitle.setText(R.string.select_transpoint);

//		} else
//			tvTitle.setText(R.string.select_transpoint);

        // ivTitleright.setImageResource(R.drawable.icon_message);
        ivTitleright.setVisibility(View.GONE);

        mlistdata = new ArrayList<>();

        mAdapter = new TaskSelectPointAdapter(this, mlistdata);

        rvList.setAdapter(mAdapter);

        rvList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                mAdapter.setCurrentselect(position);
                mAdapter.notifyDataSetChanged();

                // MtqDeliStoreDetail storedetail = mAdapter.getItem(position);
                //
                //
                // Intent intent = new
                // Intent(SelectTransPointActivity.this,UploadPaymentActivity.class);
                //
                // //添加storedetail
                // String str = GsonTool.getInstance().toJson(storedetail);
                // intent.putExtra("storedetail", str);
                //
                // //添加taskid
                // intent.putExtra("taskid", mTaskDetail.getTaskid());
                // intent.putExtra("corpid", mTaskDetail.getCorpid());
                //
                // //添加orderdetail
                // for(MtqDeliOrderDetail order : mTaskDetail.getOrders()){
                // if(order.cust_orderid.equals(storedetail.cust_orderid)){
                //
                // String str2 = GsonTool.getInstance().toJson(order);
                // intent.putExtra("orderdetail", str2);
                // }
                //
                // }
                //
                // startActivity(intent);

            }
        });

        // 设置导航状态d
        SetNavi(GeneralSPHelper.getInstance(this).getTaskNaviState(taskid));

        // RefreshData();

        // pllCheck.setTag(1);
        // ivCheck.setImageResource(R.drawable.icon_check_square_yes);

        EventBus.getDefault().register(this);

    }

    public void RefreshData() {

        showProgressBar();
        DeliveryApi.getInstance().getTaskDetailInServer(taskid, corpid, new OnResponseResult<MtqDeliTaskDetail>() {

            @Override
            public void OnResult(MtqDeliTaskDetail result) {

                if (isFinishing())
                    return;

                if (result == null || result.getStore() == null) {
                   // Toast.makeText(SelectTransPointActivity.this, R.string.request_fail, Toast.LENGTH_SHORT).show();
                   // return;

                    mTaskDetail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);
                    if(mTaskDetail == null)
                        return;

                }else{

                    TaskOperator.getInstance().saveTaskDetailDataToBD(mTaskDetail);
                    mTaskDetail = result;
                }

                hideProgressBar();





                mlistdata.clear();

                List<MtqDeliStoreDetail> list = new ArrayList<MtqDeliStoreDetail>(mTaskDetail.getStore());

                FilterAndSort(list, mTaskDetail.getOrders());
                // MLog.e("check",
                // GsonTool.getInstance().toJson(list).toString());

                if (list.size() == 0) {
                    Toast.makeText(SelectTransPointActivity.this, "没有可运输的路由点", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                mlistdata.addAll(list);

                mAdapter.notifyDataSetChanged();

                tvHint.setText("您还有" + (mlistdata.size()) + "个未完成的路由点");

                TruckCarParams.mMtqCarInfo = result.ums_carinfo;

                if (list.size() == 1 && list.get(0).optype == 4)

                    tvTransgood.setText("回程");
                else
                    tvTransgood.setText("前往");

            }

            @Override
            public void OnError(int ErrCode) {
                if (isFinishing())
                    return;

                if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                    // Toast.makeText(SelectTransPointActivity.this,
                    // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(SelectTransPointActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }

            @Override
            public void OnGetTag(String Reqtag) {


            }
        });

    }

    @OnClick({R.id.iv_titleleft, R.id.iv_titleright, R.id.tv_checktask, R.id.pll_check, R.id.tv_cancel,
            R.id.tv_transgood})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_titleleft:
                this.finish();
                break;
            case R.id.iv_titleright:
//                Intent intent3 = new Intent(this, MsgActivity.class);
//                startActivity(intent3);
                break;
            case R.id.tv_checktask:

                if (mTaskDetail != null) {

                    Intent intent = new Intent(this, FreightPointActivity.class);
                    intent.putExtra("corpid", mTaskDetail.getCorpid());
                    intent.putExtra("taskid", mTaskDetail.getTaskid());
                    startActivity(intent);
                }
                // finish();
                break;
            case R.id.pll_check:

                if (pllCheck.getTag() == null || (Integer) pllCheck.getTag() == 0) {

                    pllCheck.setTag(1);
                    ivCheck.setImageResource(R.drawable.icon_check_square_yes);
                } else {

                    pllCheck.setTag(0);
                    ivCheck.setImageResource(R.drawable.icon_check_square_no);
                }

                break;
            case R.id.tv_cancel:
                this.finish();
                break;
            case R.id.tv_transgood:

                if (!CommonTool.isFastDoubleClick()) {

                    if (mTaskDetail != null) {
                        // 如果使用了推荐顺序，否则弹出询问框

                        MtqDeliStoreDetail selectstore = mAdapter.getSelectStoreDetail();

                        if (!TaskOperator.getInstance().isChangeSequence(mTaskDetail, selectstore)) {
                            Operate();
                        } else {
                            ShowChangeAskDialog();
                        }
                    }
                }
                break;
        }
    }

    public void SetNavi(int isNavi) {

        if (isNavi == -1) {
            pllCheck.setTag(1);
            ivCheck.setImageResource(R.drawable.icon_check_square_yes);
        } else {

            if (isNavi == 1) {
                pllCheck.setTag(1);
                ivCheck.setImageResource(R.drawable.icon_check_square_yes);
            } else

            {
                pllCheck.setTag(0);
                ivCheck.setImageResource(R.drawable.icon_check_square_no);
            }

        }

    }

    public boolean GetNaviState() {

        if (pllCheck.getTag() == null || (Integer) pllCheck.getTag() == 0) {

            return false;
        } else

        {
            return true;
        }

    }

    public void StartTask() {

        if (mTaskDetail == null)
            return;

        final MtqDeliStoreDetail detail = mAdapter.getSelectStoreDetail();

        showProgressBar2();

        DeliveryApi.getInstance().UpdateStoreStatus(this,mTaskDetail.getCorpid(), mTaskDetail.getTaskid(), detail.storeid, 1,
                detail.waybill, "", 1, new OnResponseResult<UpdateTaskPointStatusResult>() {

                    @Override
                    public void OnResult(UpdateTaskPointStatusResult result) {

                        if (isFinishing())
                            return;

                        hideProgressBar2();

                        MLog.e("check", "" + result.getErrCode());
                        if (result.getErrCode() != 0) {
                            Toast.makeText(SelectTransPointActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT).show();
                        } else {

                            result.setCorpid(mTaskDetail.getCorpid());
                            result.setTaskid(mTaskDetail.getTaskid());
                            result.setStoreid(detail.storeid);
                            result.setStatus(1);
                            result.setWaybill(detail.waybill);
                            result.setEwaybill("");

                            TaskOperator.getInstance().ChangeCurrentTask(result.getCorpid(), result.getTaskid(), 1);
                            TaskOperator.getInstance().UpdateTaskStateByStoreStatusChangeResult(result);

                            // Toast.makeText(SelectTransPointActivity.this,
                            // R.string.request_success,
                            // Toast.LENGTH_SHORT).show();

                            GeneralSPHelper.getInstance(SelectTransPointActivity.this)
                                    .setTaskNavi(mTaskDetail.getTaskid(), GetNaviState());

                            EventBus.getDefault().post(new FreightPointStatusRefreshEvent());

                            EventBus.getDefault().post(new FreightPointUpdateEvent(0));

                            if (GetNaviState()) {
                                detail.storestatus = 1;
                                routeplan(detail);
                            } else {

                                finish();
                            }

                        }

                    }

                    @Override
                    public void OnError(int ErrCode) {
                        if (isFinishing())
                            return;

                        hideProgressBar2();

                        if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                            // Toast.makeText(SelectTransPointActivity.this,
                            // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(SelectTransPointActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                }, detail.waybill);

    }

    public void StopTask() {

        if (mTaskDetail == null)
            return;

        showProgressBar2();

        // status; //【0待运货1运货中2已完成3暂停状态4中止状态 】
        //
        // if(event.getStatus() == 3)
        // TaskOperator.getInstance().getmCurrentTask().corpid,
        // TaskOperator.getInstance().getmCurrentTask().taskid

        DeliveryApi.getInstance().UpdateTaskInfo(SelectTransPointActivity.this,TaskOperator.getInstance().getmCurrentTask().corpid,
                TaskOperator.getInstance().getmCurrentTask().taskid, 3, "", "", 0, 0, 0, 0,
                new OnResponseResult<UpdateTaskStatusResult>() {

                    @Override
                    public void OnResult(UpdateTaskStatusResult result) {

                        if (isFinishing())
                            return;
                        MLog.e("updatetask", result.getErrCode() + " " + result.getStatus() + " " + result.getTaskid());
                        hideProgressBar2();

                        if (result.getErrCode() != 0) {
                            // 请求错误
                            Toast.makeText(SelectTransPointActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        TaskOperator.getInstance().HandleResultAfterRequest(result);

                        // 刷新首页任务列表
                        EventBus.getDefault().post(new FreightPointStatusRefreshEvent());
                        // TaskOperator.getInstance().ChangeCurrentTask(result.getCorpid(),
                        // result.getTaskid(), 1);

                        StartTask();

                    }

                    @Override
                    public void OnError(int ErrCode) {

                        if (isFinishing())
                            return;

                        MLog.e("updatetask", ErrCode + " error");
                        if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                            // Toast.makeText(SelectTransPointActivity.this,
                            // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(SelectTransPointActivity.this,
                                    TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
                        hideProgressBar2();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                });
    }

    public void TransPoint() {

        if (mTaskDetail != null) {

            try {

                final MtqDeliStoreDetail detail = mAdapter.getSelectStoreDetail();

                showProgressBar2();

                DeliveryApi.getInstance().UpdateStoreStatus(this,mTaskDetail.getCorpid(), mTaskDetail.getTaskid(),
                        detail.storeid, 1, detail.waybill, "", new OnResponseResult<UpdateTaskPointStatusResult>() {

                            @Override
                            public void OnResult(UpdateTaskPointStatusResult result) {

                                if (isFinishing())
                                    return;

                                hideProgressBar2();

                                MLog.e("check", "" + result.getErrCode());
                                if (result.getErrCode() != 0) {
                                    Toast.makeText(SelectTransPointActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT)
                                            .show();
                                } else {

                                    result.setCorpid(mTaskDetail.getCorpid());
                                    result.setTaskid(mTaskDetail.getTaskid());
                                    result.setStoreid(detail.storeid);
                                    result.setStatus(1);
                                    result.setWaybill(detail.waybill);
                                    result.setEwaybill("");

                                    TaskOperator.getInstance().UpdateTaskStateByStoreStatusChangeResult(result);

                                    // Toast.makeText(SelectTransPointActivity.this,
                                    // R.string.request_success,
                                    // Toast.LENGTH_SHORT).show();

                                    GeneralSPHelper.getInstance(SelectTransPointActivity.this)
                                            .setTaskNavi(mTaskDetail.getTaskid(), GetNaviState());

                                    EventBus.getDefault().post(new FreightPointUpdateEvent(0));

                                    if (GetNaviState()) {
                                        detail.storestatus = 1;
                                        routeplan(detail);
                                    } else {

                                        finish();
                                    }

                                }

                            }

                            @Override
                            public void OnError(int ErrCode) {
                                if (isFinishing())
                                    return;

                                hideProgressBar2();

                                if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                                    // Toast.makeText(SelectTransPointActivity.this,
                                    // "当前操作任务单已撤回", Toast.LENGTH_SHORT)
                                    // .show();
                                } else
                                    Toast.makeText(SelectTransPointActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT)
                                            .show();
                            }

                            @Override
                            public void OnGetTag(String Reqtag) {


                            }
                        }, detail.waybill);

            } catch (Exception e) {

            }
        }

    }

    public void FilterAndSort(List<MtqDeliStoreDetail> list, List<MtqDeliOrderDetail> listorder) {

        Iterator<MtqDeliStoreDetail> iter = list.iterator();
        MtqDeliStoreDetail tmp;
        while (iter.hasNext()) {
            // 运货中
            tmp = iter.next();
            if (tmp.storestatus == 2) {

                iter.remove();
                // break searchforrunningtask;
            } else {

                if (!TextUtils.isEmpty(tmp.waybill)) {

                    over:
                    for (MtqDeliOrderDetail order : listorder) {

                        if (tmp.waybill.equals(order.waybill) && order.expired > 0) {

                            iter.remove();
                            break over;

                        }
                    }

                }

            }

        }

    }

    protected void showProgressBar() {
        if (pb_waiting != null)
            pb_waiting.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        if (pb_waiting != null)
            pb_waiting.setVisibility(View.GONE);
    }

    protected void showProgressBar2() {
        if (pb_waiting2 != null)
            pb_waiting2.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar2() {
        if (pb_waiting2 != null)
            pb_waiting2.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {

        super.onResume();

        // updateNews();

        RefreshData();

    }

    // private void updateNews() {
    // if (MsgManager.getInstance().hasUnReadMsg()) {
    // ivTitleright.setImageResource(R.drawable.msg_icon_news);
    // } else {
    // ivTitleright.setImageResource(R.drawable.msg_icon);
    // }
    // }

    // 对话框对象
    private TaskAskPopUpDialog mPopUpDialog;

    public void ShowChangeAskDialog() {

        mPopUpDialog = new TaskAskPopUpDialog(this);

        mPopUpDialog.show();

        mPopUpDialog.setDialogType(7);

        // final MtqDeliStoreDetail detail = mAdapter.getSelectStoreDetail();

        mPopUpDialog.tvTitle.setText("您调整了运货顺序，请确认是否已提货?");

        // 选择优先运货到 " + detail.storename + " ,是否继续?"

        mPopUpDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mPopUpDialog.tvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                mPopUpDialog.dismiss();
            }
        });
        mPopUpDialog.tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {


                mPopUpDialog.dismiss();

                Operate();
            }
        });

    }

    protected void Operate() {

        try {


            MtqDeliStoreDetail selectstore = mAdapter.getSelectStoreDetail();


            if(selectstore == null)
                return;


            if (GetNaviState()) {


                if (selectstore.storex == 0 && selectstore.storey == 0) {

                    TaskOperator.getInstance().showNavigationDisableDialog(this, null, new OnDialogListener() {

                        @Override
                        public void OnDialogDismiss() {


                        }

                        @Override
                        public void OnClickRight(UpdateTaskStatusEvent event) {

                            realOperate();
                        }

                        @Override
                        public void OnClickLeft(UpdateTaskStatusEvent event) {

                            realOperate();
                        }
                    });

                } else {
                    realOperate();
                }

            } else {

                realOperate();
            }
        } catch (Exception e) {

        }
    }

    public void realOperate() {

        // 如果当前任务不是我这个页面的任务 则先改变当前任务为我这个任务
        if (TaskOperator.getInstance().getmCurrentTask() != null
                && !TaskOperator.getInstance().getmCurrentTask().taskid.equals(mTaskDetail.getTaskid()))
            StopTask();
        else if (TaskOperator.getInstance().getmCurrentTask() != null
                && TaskOperator.getInstance().getmCurrentTask().taskid.equals(mTaskDetail.getTaskid())) {
            TransPoint();
        } else
            StartTask();

    }

    private void routeplan(final MtqDeliStoreDetail detail) {

        if (detail.storex == 0 && detail.storey == 0) {

            finish();
            return;
        }

        DeliveryRouteAPI.routeplan(detail, SelectTransPointActivity.this, new RoutePlanListener() {

            @Override
            public void onRoutePlanSuccessed() {
                // TODO Auto-generated
                // method stub
                SelectTransPointActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO
                        // Auto-generated
                        // method stub
                        Intent intent = new Intent(SelectTransPointActivity.this, RoutePreviewActivity.class);

                        String str = GsonTool.getInstance().toJson(detail);
                        intent.putExtra("storedetail", str);

                        // 添加taskid
                        intent.putExtra("taskid", detail.taskId);
                        intent.putExtra("corpid", detail.corpId);

                        for (MtqDeliOrderDetail order : mTaskDetail.getOrders()) {
                            if (order.waybill.equals(detail.waybill)) {

                                String str2 = GsonTool.getInstance().toJson(order);
                                intent.putExtra("orderdetail", str2);
                            }
                        }

                        str = GsonTool.getInstance().toJson(mTaskDetail);
                        intent.putExtra("taskdetail", str);

                        startActivity(intent);
                        finish();
                    }
                });

            }

            @Override
            public void onRoutePlanFaied(int arg0, String arg1) {
                // TODO Auto-generated
                // method stub
                final String errText = arg1;
                final int errcode = arg0;
                SelectTransPointActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO
                        // Auto-generated
                        // method stub
                        String text = errText;
                        System.out.println("onRoutePlanFaied errcode = " + errcode);
                        if (errcode == MAP_HC_RESULT.HC_ERRORCODE_RP_RANGE_SD
                                || errcode == MAP_HC_RESULT.HMI_ONLINE_SHORT_SD
                                || errcode == MAP_HC_RESULT.HC_ERRORCODE_RP_ROAD_SD) {
                            if (GeneralSPHelper.getInstance(SelectTransPointActivity.this).isTaskNavi(taskid)) {
                                text = "运货完成,下一个路由点已在附近 ";
                                NavigateAPI.getInstance().playVoiceText(text, 3);
                                Toast.makeText(SelectTransPointActivity.this, text, Toast.LENGTH_LONG).show();
                                showArrivedDlg(detail);
                            } else {
                                text = "运货完成,下一个路由点已在附近";
                                NavigateAPI.getInstance().playVoiceText(text, 3);
                                Toast.makeText(SelectTransPointActivity.this, text, Toast.LENGTH_LONG).show();
                                finish();
                            }

                        } else {
                            Toast.makeText(SelectTransPointActivity.this, text, Toast.LENGTH_LONG).show();
                            finish();
                        }

                    }
                });
            }

            @Override
            public void onRoutePlanCanceled() {
                // TODO Auto-generated
                // method stub
                finish();
            }
        });
    }

    private void showArrivedDlg(final MtqDeliStoreDetail detail) {
        NavigateTaskOperator.getInstance().setmStoreDetail(detail);
        MtqDeliOrderDetail orderDetail = null;
        for (MtqDeliOrderDetail order : mTaskDetail.getOrders()) {
            if (order.waybill.equals(detail.waybill)) {
                orderDetail = order;
                break;
            }
        }

        final MtqDeliOrderDetail orderTmp = orderDetail;
        NavigateTaskOperator.getInstance().setmOrderDetail(orderDetail);
        NavigateTaskOperator.getInstance().showTaskStatusChangeDialog(SelectTransPointActivity.this, null,
                NavigationDlgType.ARRIVE_DEST, new OnDialogListener() {

                    @Override
                    public void OnDialogDismiss() {


                    }

                    @Override
                    public void OnClickRight(UpdateTaskStatusEvent event) {
                        // 到达目的地

                        Intent intent = new Intent(SelectTransPointActivity.this, UploadPaymentActivity.class);
                        String str = GsonTool.getInstance().toJson(detail);
                        intent.putExtra("storedetail", str);

                        // 添加taskid
                        intent.putExtra("taskid", taskid);
                        intent.putExtra("corpid", corpid);

                        if (orderTmp != null) {
                            // 添加orderdetail
                            String str2 = GsonTool.getInstance().toJson(orderTmp);
                            intent.putExtra("orderdetail", str2);
                        }
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void OnClickLeft(UpdateTaskStatusEvent event) {
                        // 暂停运货
                        WaitingProgressTool.showProgress(SelectTransPointActivity.this);
                        pauseTask(detail);
                    }
                });
    }

    private void pauseTask(final MtqDeliStoreDetail detail) {
        if (mTaskDetail == null) {
            return;
        }

        DeliveryApi.getInstance().UpdateStoreStatus(this,mTaskDetail.getCorpid(), mTaskDetail.getTaskid(), detail.storeid,
                FreightConstant.TASK_PAUSE, detail.waybill, "", new OnResponseResult<UpdateTaskPointStatusResult>() {

                    @Override
                    public void OnResult(UpdateTaskPointStatusResult result) {

                        if (isFinishing())
                            return;
                        MLog.e("check", "" + result.getErrCode());
                        if (result.getErrCode() != 0) {
                            Toast.makeText(SelectTransPointActivity.this,  TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            result.setCorpid(mTaskDetail.getCorpid());
                            result.setTaskid(mTaskDetail.getTaskid());
                            result.setStoreid(detail.storeid);
                            result.setStatus(1);
                            result.setWaybill(detail.waybill);
                            result.setEwaybill("");

                            TaskOperator.getInstance().UpdateTaskStateByStoreStatusChangeResult(result);

                            EventBus.getDefault().post(new FreightPointUpdateEvent(0));

                            finish();
                        }
                        WaitingProgressTool.closeshowProgress();
                    }

                    @Override
                    public void OnError(int ErrCode) {

                        if (isFinishing())
                            return;

                        if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                            // Toast.makeText(SelectTransPointActivity.this,
                            // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(SelectTransPointActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
                        WaitingProgressTool.closeshowProgress();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                }, detail.waybill);
    }

    @Override
    protected void onDestroy() {

        WaitingProgressTool.closeshowProgress();
        super.onDestroy();

        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public synchronized void onTaskBusinessMsgEvent(TaskBusinessMsgEvent event) {

        switch (event.getType()) {

            // 任务刷新
            case 2:
                if (taskid == null)
                    return;

                if (isFinishing())
                    return;

                if (isRunning) {

                    if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {
                        if (TextStringUtil.isContain(event.getRefreshtaskIdList(), taskid)) {
                            RefreshData();
                        }
                    }

                }
                break;
            // 作废任务，删除某些任务
            case 3:

                if (taskid == null)
                    return;

                if (isFinishing())
                    return;

                if (event.getTaskIdList() != null && event.getTaskIdList().size() > 0) {

                    if (TextStringUtil.isContainStr(event.getTaskIdList(), taskid)) {
                        if (isRunning) {
                            // Toast.makeText(this, "当前操作任务单已撤回",
                            // Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }

                }

                break;
            case 4:
                if (taskid == null)
                    return;

                if (isFinishing())
                    return;

                if (isRunning) {

                    if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {
                        if (TextStringUtil.isContain(event.getRefreshtaskIdList(), taskid)) {
                            RefreshData();
                        }
                    }

                }

                break;
            default:
                break;
        }

    }
}
