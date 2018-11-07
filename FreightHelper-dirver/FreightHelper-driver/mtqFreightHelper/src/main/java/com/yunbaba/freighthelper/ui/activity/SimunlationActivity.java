package com.yunbaba.freighthelper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cld.mapapi.map.MapView;
import com.cld.navisdk.guide.BaseNavigorView;
import com.cld.navisdk.guide.IOnGuideListener;
import com.cld.navisdk.routeguide.CldNavigator;
import com.cld.navisdk.routeplan.CldRoutePlaner;
import com.cld.nv.guide.CldHudInfo;
import com.cld.nv.guide.CldHudModel.HudGuide;
import com.yunbaba.api.map.MapViewAPI;
import com.yunbaba.api.map.NavigateAPI;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.api.trunk.bean.UpdateTaskPointStatusResult;
import com.yunbaba.api.trunk.bean.UpdateTaskStatusResult;
import com.yunbaba.api.trunk.listner.OnObjectListner;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointStatusRefreshEvent;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.bean.eventbus.UpdateTaskStatusEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.ui.activity.task.TaskPointDetailActivity;
import com.yunbaba.freighthelper.ui.customview.TaskAskPopUpDialog;
import com.yunbaba.freighthelper.ui.customview.TaskDialoginNavigation;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.TaskStatus;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;

public class SimunlationActivity extends BaseButterKnifeActivity {

    @BindView(R.id.navigation_mapView)
    FrameLayout mMapLayout;

    private MapView mMapView;
    private BaseNavigorView mNavigatorView;

    String corpid;
    String taskid;

    MtqDeliStoreDetail mStoreDetail;
    MtqDeliOrderDetail mOrderDetail;
    MtqDeliTaskDetail mTaskDetail;

    String jsonStore;
    String jsonOrder;
    String jsonDeliTaskDetail;

    TaskDialoginNavigation dlg;
    private int mCnt = 0;
    boolean isFromTaskDetail = false;


    @Override
    public int getLayoutId() {

        return R.layout.activity_navigation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initData();
        init();
        EventBus.getDefault().register(this);
    }

    private void initData() {
        jsonStore = getIntent().getStringExtra("storedetail");
        if (!TextUtils.isEmpty(jsonStore)) {
            mStoreDetail = GsonTool.getInstance().fromJson(jsonStore, MtqDeliStoreDetail.class);
        }

        if (getIntent().hasExtra("corpid"))
            corpid = getIntent().getStringExtra("corpid");

        if (getIntent().hasExtra("taskid"))
            taskid = getIntent().getStringExtra("taskid");

        if (getIntent().hasExtra("orderdetail")) {
            jsonOrder = getIntent().getStringExtra("orderdetail");
            mOrderDetail = GsonTool.getInstance().fromJson(jsonOrder, MtqDeliOrderDetail.class);
        }

        if (getIntent().hasExtra("taskdetail")) {
            jsonDeliTaskDetail = getIntent().getStringExtra("taskdetail");
            mTaskDetail = GsonTool.getInstance().fromJson(jsonDeliTaskDetail, MtqDeliTaskDetail.class);
        }

        if(getIntent().hasExtra("isFromTaskDetail")){

            isFromTaskDetail =  getIntent().getBooleanExtra("isFromTaskDetail",false);

        }

    }

    private void init() {
        // 创建导航视图
        mMapView = MapViewAPI.getInstance().createMapView(this);
        // 初始化导航控件
        mNavigatorView = NavigateAPI.getInstance().initSimulation(this, mMapView);

        mNavigatorView.setFocusable(true);
        mMapLayout.addView(mNavigatorView);

        // 开始导航
        NavigateAPI.getInstance().startNavi();

        // 让屏幕保持不暗不关闭
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mNavigatorView.setOnGuideListener(new IOnGuideListener() {

            @Override
            public void onYaWingPlanSuccess() {

                MLog.e("NavigatorActivity", "onYaWingPlanSuccess");
            }

            @Override
            public void onYaWingPlanStart() {

                MLog.e("NavigatorActivity", "onYaWingPlanStart");
            }

            @Override
            public void onYaWingPlanFail(int errCode) {

                MLog.e("NavigatorActivity", "onYaWingPlanFail");
            }

            @Override
            public void onOverSpeed(int speed) {

                MLog.e("NavigatorActivity", "onOverSpeed");
            }

            @Override
            public void onHudUpdate(CldHudInfo hudInfo) {

                if (hudInfo != null) {
                    HudGuide guide = hudInfo.getHudGuide();
                    MLog.e("NavigatorActivity", "onHudUpdate:" + guide.roadName + " 剩余距离：" + guide.remDistance + " 剩余时间："
                            + guide.remTime + "\n" + hudInfo.getHudTTS().voiceText);
                }
            }

            @Override
            public void onCityChange(String startCityName, String destCityName) {

                MLog.e("NavigatorActivity", "onCityChange 从" + startCityName + "进入" + destCityName);
            }

            @Override
            public void onArrivePass(Object pass) {

                MLog.e("NavigatorActivity", "onArrivePass");
            }

            @Override
            public void onArriveDestNear() {

                MLog.e("NavigatorActivity", "onArriveDestNear");
            }

            @Override
            public void onArriveDest() {







//                if (mCnt != 0) {
//                    return;
//                }
//                mCnt++;
//                NavigateTaskOperator.getInstance().setmStoreDetail(mStoreDetail);
//                NavigateTaskOperator.getInstance().setmOrderDetail(mOrderDetail);
//                NavigateTaskOperator.getInstance().showTaskStatusChangeDialog(SimunlationActivity.this, null,
//                        NavigationDlgType.ARRIVE_DEST, new OnDialogListener() {
//
//                            @Override
//                            public void OnDialogDismiss() {
//
//
//                            }
//
//                            @Override
//                            public void OnClickRight(UpdateTaskStatusEvent event) {
//
//                                if (mOrderDetail != null) {
//                                    // 跳到收款界面
//                                    Intent intent = new Intent(SimunlationActivity.this, UploadPaymentActivity.class);
//                                    intent.putExtra("storedetail", jsonStore);
//
//                                    // 添加taskid
//                                    intent.putExtra("taskid", taskid);
//                                    intent.putExtra("corpid", corpid);
//                                    intent.putExtra("isFromTaskDetail", isFromTaskDetail);
//                                    if (mOrderDetail != null) {
//                                        // 添加orderdetail
//                                        intent.putExtra("orderdetail", jsonOrder);
//                                    }
//                                    startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);
//                                    mNavigatorView.stopNavi();
//                                } else {
//                                    // 跳转到选择运货点界面
//                                    final MtqDeliTask deliTask = TaskOperator.getInstance().getmCurrentTask();
//                                    //	final int nofinishcnt = deliTask.store_count - deliTask.finish_count;
//                                    int nofinishcnt = getTaskStatusIsFinish(mStoreDetail);
//                                    Runnable r = null;
//                                    if (nofinishcnt != 2) {
//
//                                        r = new Runnable() {
//
//                                            @Override
//                                            public void run() {
//
//                                                // stub
////                                                Intent intent = new Intent(SimunlationActivity.this,
////                                                        SelectTransPointActivity.class);
////                                                intent.putExtra("corpid", deliTask.corpid);
////                                                intent.putExtra("taskid", deliTask.taskid);
////                                                intent.putExtra("isFromTaskDetail", isFromTaskDetail);
////                                                startActivity(intent);
//                                                mNavigatorView.stopNavi();
//                                            }
//                                        };
//                                        finishDeliver(mStoreDetail, -1, r);
//                                    } else {
//                                        r = new Runnable() {
//                                            @Override
//                                            public void run() {
//
//                                                // stub
//                                                Toast.makeText(SimunlationActivity.this, "任务完成", Toast.LENGTH_SHORT)
//                                                        .show();
//                                                mNavigatorView.stopNavi();
//                                            }
//                                        };
//                                        finishDeliver(mStoreDetail, TaskStatus.DELIVERRED, r);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void OnClickLeft(UpdateTaskStatusEvent event) {
//
//                                WaitingProgressTool.showProgress(SimunlationActivity.this);
//                                stopDeliver(mStoreDetail, new Runnable() {
//
//                                    @Override
//                                    public void run() {
//
//                                        mNavigatorView.stopNavi();
//                                    }
//                                });
//                            }
//                        });

            }
        });

        mNavigatorView.setOnStopListener(new BaseNavigorView.OnStopListener() {
            @Override
            public boolean onBeforeStop() {
                ShowAskDialog();

                return  true;
            }

            @Override
            public void onStop() {

            }
        });

    }

    public void onBackPressed() {
        NavigateAPI.getInstance().onBackPressed();// 调用导航模式相应回退方法
    }

    /**
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {

        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mMapView) {
            mMapView.onPause();// 当地图控件存在，调用地图控件暂停方法

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mMapView) {
            mMapView.onResume();// 当地图控件存在，调用地图控件恢复方法
            mMapView.update();
        }
        CldNavigator.getInstance().onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        CldRoutePlaner.getInstance().clearRoute();
        if (null != mMapView) {
            mMapView.destroy();// 当地图控件存在时，销毁地图控件
        }
    }

    private void stopDeliver(MtqDeliStoreDetail detail, Runnable callback) {
        // 测速要求不改单的状态

        UpdateTaskStatusEvent event = new UpdateTaskStatusEvent(corpid, taskid, 3, corpid, taskid, 0, 0, 0, 0);
        HandleTaskStatusChangeEvent(event, 3, detail, callback);

        // updateStoreStatus(detail,TaskStatus.PAUSE_DELIVER,"",-1,callback);

        // if (isNeedchangeTaskStatus(detail,mTaskDetail.getStore())){
        // updateStoreStatus(detail,TaskStatus.PAUSE_DELIVER,"",TaskStatus.PAUSE_DELIVER,callback);
        //
        // }else{
        // updateStoreStatus(detail,TaskStatus.PAUSE_DELIVER,"",-1,callback);
        // }
    }

    private void finishDeliver(MtqDeliStoreDetail detail, int taskstatus, Runnable callback) {
        updateStoreStatus(detail, TaskStatus.DELIVERRED, "", taskstatus, callback);
    }

    private boolean isNeedchangeTaskStatus(MtqDeliStoreDetail detail, List<MtqDeliStoreDetail> storelist) {
        boolean change = true;

        Iterator<MtqDeliStoreDetail> iterator = storelist.iterator();

        while (iterator.hasNext()) {
            MtqDeliStoreDetail tmp = iterator.next();
            if (!tmp.waybill.equals(detail.waybill) && TaskStatus.DELIVERRED != tmp.storestatus
                    && TaskStatus.PAUSE_DELIVER != tmp.storestatus) {
                // 如果有一个非当前点的状态不是完成送货或者暂停状态，就不用改变单的状态
                change = false;
            }
        }

        return change;
    }

    private void updateStoreStatus(final MtqDeliStoreDetail detail, final int status, final String ewaybill,
                                   final int taskStaus, final Runnable callback) {

        DeliveryApi.getInstance().UpdateStoreStatus(this,corpid, taskid, detail.storeid, status, detail.waybill, ewaybill,
                taskStaus, new OnResponseResult<UpdateTaskPointStatusResult>() {

                    @Override
                    public void OnResult(UpdateTaskPointStatusResult result) {

                        MLog.e("check", "" + result.getErrCode());
                        WaitingProgressTool.closeshowProgress();
                        if (result.getErrCode() != 0) {
                            if (SimunlationActivity.this != null) {
                                Toast.makeText(SimunlationActivity.this,  TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            if (result.getTaskid() == null) {
                                result.setTaskid(taskid);
                            }

                            if (taskStaus == TaskStatus.DELIVERRED) {
                                UpdateTaskStatusResult taskStatusResult = new UpdateTaskStatusResult(0, corpid, taskid,
                                        corpid, taskid, taskStaus);
                                TaskOperator.getInstance().HandleResultAfterRequest(taskStatusResult);
                                EventBus.getDefault().post(new FreightPointStatusRefreshEvent());
                            }

                            // mResultCode =
                            // FreightConstant.TASK_POINT_INFO_NEED_UPDATE;

                            if (SimunlationActivity.this != null) {
                                // Toast.makeText(NavigatorActivity.this,
                                // R.string.request_success,
                                // Toast.LENGTH_SHORT).show();

                                // 成功之后更新本地的状态。
                                detail.storestatus = status;

                                result.setTaskid(taskid);
                                result.setStoreid(detail.storeid);
                                result.setStatus(status);
                                result.setWaybill(detail.waybill);
                                result.setEwaybill(ewaybill);
                                TaskOperator.getInstance().UpdateTaskStateByStoreStatusChangeResult(result);
                            }
                            setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);

                            if (callback != null) {
                                callback.run();
                            }
                        }
                    }

                    @Override
                    public void OnError(int ErrCode) {

                        WaitingProgressTool.closeshowProgress();
                        if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                            // Toast.makeText(TaskPointDetailActivity.this,
                            // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(SimunlationActivity.this,  TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                }, detail.waybill);
    }

    // 处理任务状态变更
    public void HandleTaskStatusChangeEvent(UpdateTaskStatusEvent event, final int statusOperateType,
                                            final MtqDeliStoreDetail detail, final Runnable callback) {

        final String ecorpid = TaskOperator.getInstance().getmCurrentTask() == null ? ""
                : TaskOperator.getInstance().getmCurrentTask().corpid;
        final String etaskid = TaskOperator.getInstance().getmCurrentTask() == null ? ""
                : TaskOperator.getInstance().getmCurrentTask().taskid;

        DeliveryApi.getInstance().UpdateTaskInfo(SimunlationActivity.this,corpid, taskid, event.getStatus(), ecorpid, etaskid, event.getX(),
                event.getY(), event.getCell(), event.getUid(), new OnResponseResult<UpdateTaskStatusResult>() {

                    @Override
                    public void OnResult(UpdateTaskStatusResult result) {

                        if (isFinishing())
                            return;
                        // MLog.e("updatetask", result.getErrCode() + " " +
                        // result.getStatus() + " " + result.getTaskid());
                        WaitingProgressTool.closeshowProgress();

                        if (result.getErrCode() != 0) {
                            // 请求错误
                            Toast.makeText(SimunlationActivity.this,TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        TaskOperator.getInstance().HandleResultAfterRequest(result);
                        EventBus.getDefault().post(new FreightPointStatusRefreshEvent());

                        if (statusOperateType == 3) {

                            if(!isFromTaskDetail) {

                                final Intent intent = new Intent(SimunlationActivity.this, TaskPointDetailActivity.class);

                                // 添加storedetail
                                detail.storestatus = statusOperateType;
                                String str = GsonTool.getInstance().toJson(detail);
                                intent.putExtra("storedetail", str);

                                // 添加taskid
                                intent.putExtra("taskid", detail.taskId);
                                intent.putExtra("corpid", detail.corpId);
                                intent.putExtra("isFromTaskDetail", isFromTaskDetail);
                                TaskOperator.getInstance()
                                        .GetorderDetailFromDB(detail.taskId, detail.waybill, new OnObjectListner<MtqDeliOrderDetail>() {
                                            @Override
                                            public void onResult(MtqDeliOrderDetail res) {
                                                MtqDeliOrderDetail orderdetail = res;
                                                if (orderdetail != null) {
                                                    String str2 = GsonTool.getInstance().toJson(orderdetail);
                                                    intent.putExtra("orderdetail", str2);
                                                }

                                                startActivity(intent);
                                                // Toast.makeText(NavigatorActivity.this,
                                                // R.string.request_success, Toast.LENGTH_SHORT)
                                                // .show();

                                                setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
                                            }
                                        });

                            }
                        }

                        if (callback != null) {
                            callback.run();
                        }
                    }

                    @Override
                    public void OnError(int ErrCode) {

                        if (isFinishing())
                            return;

                        // MLog.e("updatetask", ErrCode + " error");
                        if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                            // Toast.makeText(TaskPointDetailActivity.this,
                            // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(SimunlationActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
                        WaitingProgressTool.closeshowProgress();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                });
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
                        if (mNavigatorView != null)
                            mNavigatorView.stopNavi();
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
                            if (mNavigatorView != null)
                                mNavigatorView.stopNavi();
                        }

                    }
                }

                break;

            default:
                break;
        }

    }

    public int getTaskStatusIsFinish(MtqDeliStoreDetail storedetail) {

        MtqDeliTaskDetail taskdetail = TaskOperator.getInstance().getTaskDetailDataFromDB(storedetail.taskId);
        if (taskdetail == null || taskdetail.store == null)
            return -1;


        boolean isfinish = true;

        over:
        for (MtqDeliStoreDetail store : taskdetail.getStore()) {

            if (!store.waybill.equals(mStoreDetail.waybill) && store.storestatus != TaskStatus.DELIVERRED) {

                isfinish = false;
                break over;
            }

        }
        MLog.e("check isfinish", "" + isfinish);

        if (isfinish)
            return 2;
        else
            return -1;

    }

    TaskAskPopUpDialog mPopUpDialog;

    public void ShowAskDialog() {


        mPopUpDialog = new TaskAskPopUpDialog(this);

        mPopUpDialog.show();

        mPopUpDialog.setDialogType(11);

        // final MtqDeliStoreDetail detail = mS

        // 选择优先运货到 " + detail.storename + " ,是否继续?"

        mPopUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopUpDialog.tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                mPopUpDialog.dismiss();


            }
        });
        mPopUpDialog.tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {



                mPopUpDialog.dismiss();

                mNavigatorView.stopNavi();
                //   finish();

            }
        });

    }

}
