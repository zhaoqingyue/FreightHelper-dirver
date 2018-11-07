package com.yunbaba.api.trunk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.cld.mapapi.model.LatLng;
import com.cld.navisdk.routeplan.CldRoutePlaner;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.api.trunk.bean.OnUIResult;
import com.yunbaba.api.trunk.bean.UpdateTaskPointStatusResult;
import com.yunbaba.api.trunk.bean.UpdateTaskStatusResult;
import com.yunbaba.api.trunk.listner.OnObjectListner;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.PointInfo;
import com.yunbaba.freighthelper.bean.car.TruckCarParams;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointEvent;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointStatusRefreshEvent;
import com.yunbaba.freighthelper.bean.eventbus.UpdateTaskStatusEvent;
import com.yunbaba.freighthelper.constant.MAP_HC_RESULT;
import com.yunbaba.freighthelper.ui.customview.TaskAskPopUpDialog;
import com.yunbaba.freighthelper.ui.dialog.PhoneCallDialog;
import com.yunbaba.freighthelper.ui.dialog.PromptDialog;
import com.yunbaba.freighthelper.utils.ErrCodeUtil;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.OnCallBack;
import com.yunbaba.freighthelper.utils.TaskStatus;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: FreightPointDeal.java
 * @Prject: Freighthelper
 * @Package: com.mtq.api.trunk
 * @Description: 货运点的处理逻辑
 * @author: zsx
 * @date: 2017-4-6 上午10:53:02
 * @version: V1.0
 */
@SuppressLint("NewApi")
public class FreightPointDeal {
    public final static String TAG = "FreightPointDeal";
    private static FreightPointDeal mFreightPointDeal;
    private Context mCtx;
    public ArrayList<MtqDeliStoreDetail> mStore;
    public HashMap<Integer, MtqDeliOrderDetail> mOrder;
    public ArrayList<PointInfo> mPointList;
    // 第一个string的格式是 taskId|corpId

    // 当前选择的企业ID和运单ID
    private String mCurSelectTaskId;
    private String mCurSelectCorpId;
    // 终止的企业ID和运单ID
    private String mEndTaskId = "";
    private String mEndCorpId = "";
    private String mkey;
    private String mPhone;

    /**
     * 需要暂停的运货点号
     **/
    private String mNeedEndWaybill;
    /**
     * 需要暂停的运货点号的索引
     **/
    private int mNeedEndWaybillIndex;

    private boolean isSameTask; // 与正在运输的是否是同个单
    private boolean isUpdateTask; // 是否需要刷新单的状态。

    private boolean isListMode = true;
    public boolean setAdpate = false;

    private OnUIResult mListUICallBack;
    private OnUIResult mMapUICallBack;

    // 导航的操作
    private Runnable mNavigateRunnable = null;
    // 界面调整的操作
    private Runnable mJumpRunnable = null;
    // 调整运货顺序的执行的操作
    private Runnable mChangeSequenceRunnable = null;

    // 暂停当前运货点，执行新的运货操作。
    private Runnable mNewDeliverRunnable = null;

    private Runnable mRouteSucessable = null;

    private Runnable mRouteFailable = null;

    private MtqDeliTaskDetail mMtqDeliTaskDetail;

    public Handler mHandler = null;
    private final int DEALY_TIME = 300; // 延迟显示时间

    public static FreightPointDeal getInstace() {
        synchronized (TAG) {
            if (mFreightPointDeal == null) {
                synchronized (TAG) {
                    mFreightPointDeal = new FreightPointDeal();
                }
            }
        }
        return mFreightPointDeal;
    }

    public FreightPointDeal init(Context ctx) {
        mCtx = ctx; // 看用app的ctx 行不行。
        setAdpate = false;
        return mFreightPointDeal;
    }

    public void uninit() {
        mCtx = null;
        setAdpate = false;
        mListUICallBack = null;
        mMapUICallBack = null;
        cleanCallback();
        mHandler = null;
    }

    public ArrayList<MtqDeliStoreDetail> getmStore() {
        return mStore;
    }

    public void setmStore(ArrayList<MtqDeliStoreDetail> mStore) {
        this.mStore = mStore;
    }

    public HashMap<Integer, MtqDeliOrderDetail> getmOrder() {
        return mOrder;
    }

    public void setmOrder(HashMap<Integer, MtqDeliOrderDetail> mOrder) {
        this.mOrder = mOrder;
    }

    public void setmCallBack(OnUIResult mCallBack) {
        this.mListUICallBack = mCallBack;
    }

    public void setmMapUICallBack(OnUIResult mMapUICallBack) {
        this.mMapUICallBack = mMapUICallBack;
    }

    public Runnable getmJumpRunnable() {
        return mJumpRunnable;
    }

    public void setmJumpRunnable(Runnable mJumpRunnable) {
        this.mJumpRunnable = mJumpRunnable;
    }

    public void setmRouteSucessable(Runnable mRouteSucessable) {
        this.mRouteSucessable = mRouteSucessable;
    }

    public void setmRouteFailable(Runnable mRouteFailable) {
        this.mRouteFailable = mRouteFailable;
    }

    public MtqDeliTaskDetail getmMtqDeliTaskDetail() {
        return mMtqDeliTaskDetail;
    }

    /**
     * @param taskId：运货单ID
     * @param corpId：企业ID
     * @param callBack：回调
     * @Title: getTaskDetail
     * @Description: 获取运货点详情
     * @return: void
     */
    public void getTaskDetail(Activity activity, String taskId, String corpId, final OnUIResult callBack) {
        mkey = taskId + "|" + corpId;
        mCurSelectTaskId = taskId;
        mCurSelectCorpId = corpId;
        setTaskParams(taskId);
        // 每次都去请求，失败再去找数据库的
        getTaskDetailInServer(activity, taskId, corpId, callBack);
    }

    /**
     * @Title: findNeedEndWayBill
     * @Description: 查找需要下次暂停的配送单号，即当前正在运单的配送号
     * @return: void
     */
    private void findNeedEndWayBill() {
        mNeedEndWaybill = "";
        mNeedEndWaybillIndex = -1;
        for (int i = 0; i < mStore.size(); i++) {
            if (mStore.get(i).storestatus == TaskStatus.DELIVERRING) {
                mNeedEndWaybill = mStore.get(i).waybill;
                mNeedEndWaybillIndex = i;
            }
        }
    }

    /**
     * @param storeDetail
     * @param storelist
     * @return
     * @Title: isNeedchangeOtherPointStatus
     * @Description: 是否需要修改其他点状态
     * @return: boolean
     */
    private boolean isNeedchangeOtherPointStatus(MtqDeliStoreDetail storeDetail,
                                                 ArrayList<MtqDeliStoreDetail> storelist) {
        boolean change = false;

        // if (!TextUtils.isEmpty(storeDetail.waybill) &&
        // !TextUtils.isEmpty(mNeedEndWaybill) &&
        // !storeDetail.waybill.equals(mNeedEndWaybill)){
        // change = true;
        // }

        Iterator<MtqDeliStoreDetail> iterator = storelist.iterator();
        while (iterator.hasNext()) {
            MtqDeliStoreDetail tmp = iterator.next();

            if (!tmp.waybill.equals(storeDetail.waybill) && tmp.storestatus == TaskStatus.DELIVERRING) {
                change = true;
                break;
            }
        }

        return change;
    }

    // 暂停的时候开始。 正在运输 -到完成。
    private void setTaskParams(String taskid) {
        isSameTask = false;
        MtqDeliTask deliTask = TaskOperator.getInstance().getmCurrentTask();

        // 如果当前没有正在运输的单，那就是需要修改运单的状态
        if (deliTask == null) {
            // 开始运货的时候需要更改运货单的状态。
            isSameTask = true;
            isUpdateTask = true;
        } else {
            if (deliTask.taskid.equals(taskid)) {
                isSameTask = true;
                isUpdateTask = false;
                // 需要进一步检测
            } else {
                isSameTask = false;
                isUpdateTask = true;
            }
        }

        if (isSameTask) {
            mEndTaskId = mCurSelectTaskId;
            mEndCorpId = mCurSelectCorpId;
        } else {
            mEndCorpId = deliTask.corpid;
            mEndTaskId = deliTask.taskid;
        }
    }

    /**
     * @return
     * @Title: isOverTime
     * @Description: 是否超时
     * @return: boolean
     */
    public boolean isOverTime(MtqDeliStoreDetail storeDetail, MtqDeliOrderDetail orderDetial) {
        boolean ret = false;
        if (orderDetial == null) {
            return false;
        }

        if (storeDetail.finishtime == 0 && (orderDetial.reqtime_e) < System.currentTimeMillis() / 1000) {
            // 没有完成。
            ret = true;
        }

        return ret;
    }

    public boolean isOverTime(int index) {
        return isOverTime(mStore.get(index), mOrder.get(index));
    }

    /**
     * @param storedetail：操作的的点
     * @param requestStatus：请求的状态。
     * @return
     * @Title: isNeedChangeTaskStatus
     * @Description: 获取是否需要更改单的状态
     * @return: boolean
     */
    public boolean isNeedChangeTaskStatus(MtqDeliStoreDetail storedetail, int requestStatus) {
        MtqDeliTask deliTask = TaskOperator.getInstance().getmCurrentTask();

        return isNeedChangeTaskStatus(deliTask, mStore, storedetail, requestStatus);
    }

    /**
     * @param curdeliTask
     * @param storelist
     * @param storedetail
     * @param requestStatus
     * @return
     * @Title: isNeedChangeTaskStatus
     * @Description: 是否需要改变运单的状态（在同个单的情况下）
     * @return: boolean
     */
    public boolean isNeedChangeTaskStatus(MtqDeliTask curdeliTask, ArrayList<MtqDeliStoreDetail> storelist,
                                          MtqDeliStoreDetail storedetail, int requestStatus) {
        boolean update = false;

        // 如果当前没有正在运货的单，则是需要更改单的状态
        if (curdeliTask == null) {
            return true;
        }

        Iterator<MtqDeliStoreDetail> iterator = storelist.iterator();
        // 请求的是完成运货点 而整个单不是完成状态
        if (requestStatus == TaskStatus.DELIVERRED && curdeliTask.status != TaskStatus.DELIVERRED) {
            // 请求是完成送货
            update = true;
            while (iterator.hasNext()) {
                MtqDeliStoreDetail tmp = iterator.next();

                if (!tmp.waybill.equals(storedetail.waybill) && TaskStatus.DELIVERRED != tmp.storestatus) {
                    // 如果有一个非当前点的状态不是完成送货，就不用改变单的状态
                    update = false;
                }
            }
        } else if (requestStatus == TaskStatus.PAUSE_DELIVER && curdeliTask.status != TaskStatus.PAUSE_DELIVER) {
            // 请求是暂停送货
            update = true;
            while (iterator.hasNext()) {
                MtqDeliStoreDetail tmp = iterator.next();

                if (!tmp.waybill.equals(storedetail.waybill) && TaskStatus.DELIVERRED != tmp.storestatus
                        && TaskStatus.PAUSE_DELIVER != tmp.storestatus) {
                    // 如果有一个非当前点的状态不是完成送货或者暂停状态，就不用改变单的状态
                    update = false;
                }
            }
        }

        return update;
    }

    /**
     * @param callBack：回调
     * @Title: getTaskDetailInServer
     * @Description: 从服务器获取详情
     * @return: void
     */
    public void getTaskDetailInServer(final Activity context, final String taskId, String corpId, final OnUIResult callBack) {
        // showProgressBar();
        final String key = taskId + "|" + corpId;
        DeliveryApi.getInstance().getTaskDetailInServer(taskId, corpId, new OnResponseResult<MtqDeliTaskDetail>() {

            @Override
            public void OnResult(MtqDeliTaskDetail result) {

                if (mCtx != null) {
                    // Toast.makeText(mCtx, "Sucess ",
                    // Toast.LENGTH_SHORT).show();


                    TaskOperator.getInstance().saveTaskDetailDataToBD(result);
                    mMtqDeliTaskDetail = result;

                    generateData(result);

                    // mStoreMap.put(key, mStore);
                    // mOrderMap.put(key,mOrder);

                    callBack.OnResult();
                }
            }

            @Override
            public void OnError(final int ErrCode) {

                if (mCtx != null) {
                    TaskOperator.getInstance().getTaskDetailDataFromDB(context, taskId, new OnObjectListner<MtqDeliTaskDetail>() {
                        @Override
                        public void onResult(final MtqDeliTaskDetail taskDetail) {


                            if (taskDetail != null) {
                                sortStorelist(taskDetail.store);
                                generateData(taskDetail);
                                mMtqDeliTaskDetail = taskDetail;
                                callBack.OnResult();
                            } else {
                                callBack.OnError(ErrCode);
                            }


                        }
                    });

                }
            }

            @Override
            public void OnGetTag(String Reqtag) {


            }
        });
    }

    /**
     * @param result
     * @Title: generateData
     * @Description: 生成数据
     * @return: void
     */
    private void generateData(MtqDeliTaskDetail result) {

        if (result == null)
            return;

        mStore = (ArrayList<MtqDeliStoreDetail>) result.getStore();
        ArrayList<MtqDeliOrderDetail> order = (ArrayList<MtqDeliOrderDetail>) result.getOrders();
        generateOrder(mStore, order);
        findNeedEndWayBill();
//		if (result.getStatus() == TaskStatus.DELIVERRED) {
//			// 产品要求，如果单已经完成，里面的点也要改成完成。bug编号M3916L400195
//			setStoreStatus(TaskStatus.DELIVERRED, mStore);
//		}

        TruckCarParams.mMtqCarInfo = result.ums_carinfo;
    }

    private void setStoreStatus(int status, ArrayList<MtqDeliStoreDetail> storelist) {
        Iterator<MtqDeliStoreDetail> iter = storelist.iterator();
        while (iter.hasNext()) {
            MtqDeliStoreDetail tmp = iter.next();
            tmp.storestatus = status;
        }
    }

    // 从数据库拿出来的数据有可能会排序不对
    private void sortStorelist(ArrayList<MtqDeliStoreDetail> list) {
        Collections.sort(list, new Comparator<MtqDeliStoreDetail>() {

            @Override
            public int compare(MtqDeliStoreDetail obj1, MtqDeliStoreDetail obj2) {

                return obj1.storesort - obj2.storesort;
            }

        });
    }

    /**
     * @param store
     * @param order
     * @Title: generateOrder
     * @Description: 生成一一对应的order，如果store没有order，就给一个空的。方便获取。
     * @return: void
     */
    private void generateOrder(ArrayList<MtqDeliStoreDetail> store, ArrayList<MtqDeliOrderDetail> order) {
        mOrder = new HashMap<>();
        mPointList = new ArrayList<PointInfo>();
        boolean flag = false; // 对应的标志

        if (store == null) {

            mOrder.put(0, null);
            return;

        }

        for (int i = 0; i < store.size(); i++) {
            flag = false;
            String storeID = store.get(i).waybill;

            if(!TaskUtils.isStorePosUnknown( store.get(i))) {
                LatLng point = new LatLng((double) store.get(i).storey, (double) store.get(i).storex);
                mPointList.add(new PointInfo(point, store.get(i).storesort));
            }
            if (storeID == null) {
                mOrder.put(store.get(i).storesort, null);
                continue;
            }
            for (int j = 0; j < order.size(); j++) {
                String StrID = order.get(j).waybill;
                if (StrID == null) {
                    continue;
                }
                if (storeID.equals(StrID)) {
                    mOrder.put(store.get(i).storesort, order.get(j));
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                // 找不到
                mOrder.put(store.get(i).storesort, null);
            }
        }
    }

    /**
     * @param storeStatus
     * @param optype
     * @return
     * @Title: getStoreStatusText
     * @Description: 获取状态描述
     * @return: String
     */
    public String getStoreStatusText(int storeStatus, int optype) {
        String str = "";
        switch (storeStatus) {
            case TaskStatus.WAITTING_DELIVER:

                if (optype == 4)
                    str = "等待回程";
                else
                    str = "待作业";
                break;
            case TaskStatus.DELIVERRING:
                if (optype == 4)
                    str = "回程中";
                else
                    str = "作业中";
                break;
            case TaskStatus.DELIVERRED:
                // if (optype == 4)
                // str = "等待回程";
                // else
                str = "已完成";
                break;
            case TaskStatus.PAUSE_DELIVER:
                if (optype == 4)
                    str = "暂停回程";
                else
                    str = "暂停";
                break;
        }

        return str;
    }

    /**
     * @param detail
     * @Title: startDeliver
     * @Description: 开始送货
     * @return: void
     */
    public void startDeliver(MtqDeliStoreDetail detail) {
        if (isSameTask) {
            // 同个单
            isUpdateTask = isNeedChangeTaskStatus(detail, TaskStatus.DELIVERRING);
            if (detail.waybill.equals(mNeedEndWaybill)) {
                mNeedEndWaybill = "";
            }
            if (isUpdateTask) {
                updateStoreStatus(detail, TaskStatus.DELIVERRING, mNeedEndWaybill, TaskStatus.DELIVERRING);
            } else {
                updateStoreStatus(detail, TaskStatus.DELIVERRING, mNeedEndWaybill);
            }
        } else {
            // 不同单
            isSameTask = true;
        }
    }

    public void finishDeliver(MtqDeliStoreDetail detail) {
        isUpdateTask = isNeedChangeTaskStatus(detail, TaskStatus.DELIVERRED);
        if (isUpdateTask) {
            updateStoreStatus(detail, TaskStatus.DELIVERRED, "", TaskStatus.DELIVERRED);
        } else {
            updateStoreStatus(detail, TaskStatus.DELIVERRED, "");
        }
    }

    public void stopDeliver(MtqDeliStoreDetail detail) {
        updateStoreStatus(detail, TaskStatus.PAUSE_DELIVER, "");
    }

    public void waitDeliver(MtqDeliStoreDetail detail) {
        updateStoreStatus(detail, TaskStatus.WAITTING_DELIVER, mNeedEndWaybill);
    }

    public void continueDeliver(MtqDeliStoreDetail detail) {
        if (isSameTask) {
            // 同个单
            isUpdateTask = isNeedChangeTaskStatus(detail, TaskStatus.DELIVERRING);
            if (detail.waybill.equals(mNeedEndWaybill)) {
                mNeedEndWaybill = "";
            }
            if (isUpdateTask) {
                updateStoreStatus(detail, TaskStatus.DELIVERRING, mNeedEndWaybill, TaskStatus.DELIVERRING);
            } else {
                updateStoreStatus(detail, TaskStatus.DELIVERRING, mNeedEndWaybill);
            }

        } else {
            // 不同单
            isSameTask = true;
        }
    }

    /**
     * @param detail
     * @param status
     * @Title: updateStoreStatus
     * @Description: 更新运货点的状态
     * @return: void
     */
    private void updateStoreStatus(final MtqDeliStoreDetail detail, final int status, final String ewaybill) {
        updateStoreStatus(detail, status, ewaybill, -1);
    }

    private void updateStoreStatus(final MtqDeliStoreDetail detail, final int status, final String ewaybill,
                                   final int taskStaus) {

        DeliveryApi.getInstance().UpdateStoreStatus(mCtx, mCurSelectCorpId, mCurSelectTaskId, detail.storeid, status,
                detail.waybill, ewaybill, taskStaus, new OnResponseResult<UpdateTaskPointStatusResult>() {

                    @Override
                    public void OnResult(UpdateTaskPointStatusResult result) {

                        MLog.e("check", "" + result.getErrCode());
                        if (result.getErrCode() != 0) {
                            if (mCtx != null) {
                                Toast.makeText(mCtx, TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT).show();
                                if (mListUICallBack != null) {
                                    mListUICallBack.OnError(result.getErrCode());
                                }
                                if (mMapUICallBack != null) {
                                    mMapUICallBack.OnError(result.getErrCode());
                                }
                            }
                        } else {
                            if (result.getTaskid() == null) {
                                result.setTaskid(mCurSelectTaskId);
                            }

                            if (mCtx != null) {
                                // Toast.makeText(mCtx,
                                // R.string.request_success,
                                // Toast.LENGTH_SHORT).show();

                                // 如果之前有正在运的单，要将之前运单设置为暂停
                                if (mNeedEndWaybillIndex != -1 && !TextUtils.isEmpty(ewaybill)) {
                                    mStore.get(mNeedEndWaybillIndex).storestatus = TaskStatus.PAUSE_DELIVER;
                                }

                                // 成功之后更新本地的状态。
                                detail.storestatus = status;

                                findNeedEndWayBill();

                                if (isUpdateTask) {
                                    // 需要更新货运单
                                    UpdateTaskStatusResult taskStatusResult = new UpdateTaskStatusResult(0,
                                            mCurSelectCorpId, mCurSelectTaskId, mEndCorpId, mEndTaskId, taskStaus);
                                    TaskOperator.getInstance().HandleResultAfterRequest(taskStatusResult);
                                    EventBus.getDefault().post(new FreightPointStatusRefreshEvent());
                                } else {

                                }

                                result.setTaskid(mCurSelectTaskId);
                                result.setStoreid(detail.storeid);
                                result.setStatus(status);
                                result.setWaybill(detail.waybill);
                                result.setEwaybill(ewaybill);
                                TaskOperator.getInstance().UpdateTaskStateByStoreStatusChangeResult(result);

                                if (mJumpRunnable != null) {
                                    mJumpRunnable.run();
                                    mJumpRunnable = null;
                                }

                                if (mNavigateRunnable != null) {
                                    mNavigateRunnable.run();
                                    mNavigateRunnable = null;
                                }

                                if (mListUICallBack != null) {
                                    mListUICallBack.OnResult();
                                }
                                if (mMapUICallBack != null) {
                                    mMapUICallBack.OnResult();
                                }
                            }
                        }
                    }

                    @Override
                    public void OnError(int ErrCode) {

                        cleanCallback();
                        if (mCtx != null) {

                            if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL
                                    || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                                // Toast.makeText(TaskPointDetailActivity.this,
                                // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                            } else {

                                if (ErrCodeUtil.isNetErrCode(ErrCode)) {
                                    Toast.makeText(mCtx, "网络通信出现问题，请稍后再试", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mCtx, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (mListUICallBack != null) {
                                mListUICallBack.OnError(ErrCode);
                            }
                            if (mMapUICallBack != null) {
                                mMapUICallBack.OnError(ErrCode);
                            }
                        }
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                }, detail.waybill);
    }

    /**
     * @param phoneNumber
     * @Title: call
     * @Description: TODO
     * @return: void
     */
    public void call(final String phoneNumber) {
        if (mCtx == null)
            return;
        mPhone = phoneNumber;
        String cancel = mCtx.getResources().getString(R.string.dialog_cancel);
        String sure = mCtx.getResources().getString(R.string.dialog_call);

        boolean isSingleNum = true;

        if (!phoneNumber.contains(",")) {
            isSingleNum = true;
        } else {

            ArrayList<String> phonelist = TextStringUtil.splitPhoneString(phoneNumber);
            if (phonelist != null && phonelist.size() >= 2) {

                isSingleNum = false;

            }

        }
        if (isSingleNum) {
            PromptDialog dialog = new PromptDialog(mCtx, phoneNumber.replaceAll(",", ""), cancel, sure,
                    new PromptDialog.IPromptDialogListener() {

                        @Override
                        public void OnSure() {

                            onSure(phoneNumber.replaceAll(",", ""));
                        }

                        @Override
                        public void OnCancel() {


                        }
                    });

            dialog.show();
        } else {
            PhoneCallDialog dialog = new PhoneCallDialog(mCtx, phoneNumber, cancel, sure,
                    new PhoneCallDialog.IPhoneCallDialogListener() {

                        @Override
                        public void OnCancel() {

                        }

                        @Override
                        public void OnSure(String phones) {
                            onSure(phones);
                        }

                    });
            dialog.show();

        }

    }

    private void onSure(String phone) {
        if (!TextUtils.isEmpty(phone)) {
            int curApiVersion = Build.VERSION.SDK_INT;

            // intentToCall(phone);

            if (curApiVersion >= Build.VERSION_CODES.M) {
                // 在6.0 系统中请求某些权限需要检查权限
                if (!hasPermission()) {
                    // 动态请求拨打电话权限
                    ((Activity) mCtx).requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 0x11);
                } else {
                    intentToCall(phone);
                }
            } else {
                intentToCall(phone);
            }
        }
    }

    @TargetApi(23)
    private boolean hasPermission() {
        String permission = Manifest.permission.CALL_PHONE;
        if (mCtx.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void intentToCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNumber);
        intent.setData(data);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mCtx.startActivity(intent);
    }

    public void intentToCall() {
        String phoneNumber = mPhone;
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNumber);
        intent.setData(data);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mCtx.startActivity(intent);
    }

    public void routeplan(final MtqDeliStoreDetail storeDetail) {

        DeliveryRouteAPI.routeplan(storeDetail, mCtx, new CldRoutePlaner.RoutePlanListener() {

            @Override
            public void onRoutePlanSuccessed() {

                if (mRouteSucessable != null) {
                    mHandler.post(mRouteSucessable);
                }
                // EventBus.getDefault().post(new
                // FreightPointEvent(MessageId.MSGID_ROUTE_SUCESS,mCtx.getClass().getSimpleName()));
            }

            @Override
            public void onRoutePlanFaied(int errcode, String s) {


                if (errcode == MAP_HC_RESULT.HC_ERRORCODE_RP_RANGE_SD || errcode == MAP_HC_RESULT.HMI_ONLINE_SHORT_SD
                        || errcode == MAP_HC_RESULT.HC_ERRORCODE_RP_ROAD_SD) {
                    // s = "离目的地太近,导航失败";

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            if (mRouteFailable != null) {
                                mRouteFailable.run();
                            }
                            cleanCallback();
                        }
                    });
                }
                EventBus.getDefault().post(
                        new FreightPointEvent(MessageId.MSGID_ROUTE_FAIL, errcode, s, mCtx.getClass().getSimpleName()));

            }

            @Override
            public void onRoutePlanCanceled() {


            }
        });

    }

    /**
     * @param taskDetail
     * @param storeDetail
     * @Title: deliverAndRoute
     * @Description: 运货并导航
     * @return: void
     */
    private void deliverAndRoute(MtqDeliTaskDetail taskDetail, final MtqDeliStoreDetail storeDetail) {
        if (TaskOperator.getInstance().isChangeSequence(taskDetail, storeDetail)) {
            // 调整了运货顺序。
            ShowChangeAskDialog(storeDetail, new Runnable() {

                @Override
                public void run() {


                    if (TaskUtils.isStorePosUnknown(storeDetail)) {

                        ShowNaviDisableDialog(new OnCallBack() {

                            @Override
                            public void onYES() {

                                WaitingProgressTool.showProgress(mCtx);
                                if (storeDetail.storestatus == TaskStatus.WAITTING_DELIVER) {
                                    startDeliver(storeDetail);
                                } else if (storeDetail.storestatus == TaskStatus.PAUSE_DELIVER) {
                                    // 继续
                                    continueDeliver(storeDetail);
                                }
                            }
                        });

                    } else
                        deliverAndRoute(storeDetail);
                }
            });
        } else {
            if (TaskUtils.isStorePosUnknown(storeDetail)) {

                ShowNaviDisableDialog(new OnCallBack() {

                    @Override
                    public void onYES() {

                        WaitingProgressTool.showProgress(mCtx);
                        if (storeDetail.storestatus == TaskStatus.WAITTING_DELIVER) {
                            startDeliver(storeDetail);
                        } else if (storeDetail.storestatus == TaskStatus.PAUSE_DELIVER) {
                            // 继续
                            continueDeliver(storeDetail);
                        }
                    }
                });

            } else
                deliverAndRoute(storeDetail);
        }
    }

    private void deliverAndRoute(final MtqDeliStoreDetail storeDetail) {
        WaitingProgressTool.showProgress(mCtx);
        if (storeDetail.storestatus == TaskStatus.WAITTING_DELIVER) {
            startDeliver(storeDetail);
        } else if (storeDetail.storestatus == TaskStatus.PAUSE_DELIVER) {
            // 继续
            continueDeliver(storeDetail);
        }

        // 算路的要跳转到行程概览界面，清掉其他的跳转界面。
        mJumpRunnable = null;

        mNavigateRunnable = new Runnable() {
            @Override
            public void run() {

                routeplan(storeDetail);
            }
        };
    }

    /**
     * @param taskDetail
     * @param storeDetail
     * @Title: deliver
     * @Description: 运货流程，全程导航不勾选
     * @return: void
     */
    private void deliver(MtqDeliTaskDetail taskDetail, final MtqDeliStoreDetail storeDetail) {
        if (TaskOperator.getInstance().isChangeSequence(taskDetail, storeDetail)) {
            // 调整了运货顺序。
            ShowChangeAskDialog(storeDetail, new Runnable() {

                @Override
                public void run() {


                    if (TaskUtils.isStorePosUnknown(storeDetail)) {

//						ShowNaviDisableDialog(new OnCallBack() {
//
//							@Override
//							public void onYES() {
//
//							
//							}
//						});

                        WaitingProgressTool.showProgress(mCtx);
                        if (storeDetail.storestatus == TaskStatus.WAITTING_DELIVER) {
                            startDeliver(storeDetail);
                        } else if (storeDetail.storestatus == TaskStatus.PAUSE_DELIVER) {
                            // 继续
                            continueDeliver(storeDetail);
                        }

                    } else {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showNavigationDialog(storeDetail);
                            }
                        }, 500);

                    }

                }
            });
        } else {
            if (TaskUtils.isStorePosUnknown(storeDetail)) {

//				ShowNaviDisableDialog(new OnCallBack() {
//
//					@Override
//					public void onYES() {
//
//						
//					}
//				});

                WaitingProgressTool.showProgress(mCtx);
                if (storeDetail.storestatus == TaskStatus.WAITTING_DELIVER) {
                    startDeliver(storeDetail);
                } else if (storeDetail.storestatus == TaskStatus.PAUSE_DELIVER) {
                    // 继续
                    continueDeliver(storeDetail);
                }

            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showNavigationDialog(storeDetail);
                    }
                }, 500);
            }
        }
    }

    /**
     * @param storeDetail
     * @Title: startOrContinueDeliverAndNavigate
     * @Description: 直接导航，全程导航开关为true
     * @return: void
     */
    public void startOrContinueDeliverAndNavigate(final MtqDeliStoreDetail storeDetail) {
        if (isSameTask) {
            deliverAndRoute(mMtqDeliTaskDetail, storeDetail);
            // 交互说同个单不需要弹这个框
            // if (isNeedchangeOtherPointStatus(storeDetail, mStore)){
            // //如果需要当前已经在运其他的点了
            //
            // showChangeStoreDlg(storeDetail,new Runnable() {
            // @Override
            // public void run() {
            //
            // //这个就不要弹ShowChangeAskDialog
            // deliverAndRoute(storeDetail);
            // }
            // });
            // }else{
            // deliverAndRoute(mMtqDeliTaskDetail,storeDetail);
            // }
        } else {
            showChangeTaskDlg(storeDetail);
        }
    }

    /**
     * @param storeDetail
     * @Title: startOrContinueDeliver
     * @Description: 开始继续运货
     * @return: void
     */
    public void startOrContinueDeliver(final MtqDeliStoreDetail storeDetail) {
        if (isSameTask) {
            deliver(mMtqDeliTaskDetail, storeDetail);
            // 交互说同个单不需要弹这个框
            // if (isNeedchangeOtherPointStatus(storeDetail, mStore)){
            // //如果需要当前已经在运其他的点了
            // showChangeStoreDlg(storeDetail,new Runnable() {
            //
            // @Override
            // public void run() {
            //
            // showNavigationDialog(storeDetail);
            // }
            // });
            // }else{
            // deliver(mMtqDeliTaskDetail,storeDetail);
            // }
        } else {
            showChangeTaskDlg(storeDetail);
        }
    }

    private void showNavigationDialog(final MtqDeliStoreDetail storeDetail) {
        TaskOperator.getInstance().showNavigationDialog(mCtx, null, new TaskOperator.OnDialogListener() {

            @Override
            public void OnDialogDismiss() {


            }

            @Override
            public void OnClickRight(UpdateTaskStatusEvent event) {

                deliverAndRoute(storeDetail);
            }

            @Override
            public void OnClickLeft(UpdateTaskStatusEvent event) {

                WaitingProgressTool.showProgress(mCtx);
                if (storeDetail.storestatus == TaskStatus.WAITTING_DELIVER) {
                    startDeliver(storeDetail);
                } else if (storeDetail.storestatus == TaskStatus.PAUSE_DELIVER) {
                    // 继续
                    continueDeliver(storeDetail);
                }
            }
        });
    }

    /**
     * @param storeDetail
     * @Title: showChangeTaskDlg
     * @Description: 显示运货单状态变化对话框
     * @return: void
     */
    public void showChangeTaskDlg(final MtqDeliStoreDetail storeDetail) {
        UpdateTaskStatusEvent event = new UpdateTaskStatusEvent(mCurSelectCorpId, mCurSelectTaskId, 1,
                TaskOperator.getInstance().getmCurrentTask().corpid,
                TaskOperator.getInstance().getmCurrentTask().taskid, 0, 0, 0, 0);
        TaskOperator.getInstance().showTaskStatusChangeAskDialog(mCtx, event, new TaskOperator.OnDialogListener() {

            @Override
            public void OnDialogDismiss() {


            }

            @Override
            public void OnClickRight(UpdateTaskStatusEvent event) {

                HandleStatusChangeEvent(event, storeDetail);
            }

            @Override
            public void OnClickLeft(UpdateTaskStatusEvent event) {

                cleanCallback();
            }
        });
    }

    public void showChangeStoreDlg(final MtqDeliStoreDetail storeDetail, final Runnable deliverRunnable) {
        UpdateTaskStatusEvent event = new UpdateTaskStatusEvent(mCurSelectCorpId, mCurSelectTaskId, 1,
                TaskOperator.getInstance().getmCurrentTask().corpid,
                TaskOperator.getInstance().getmCurrentTask().taskid, 0, 0, 0, 0);
        TaskOperator.getInstance().showTaskStatusChangeAskDialog(mCtx, event, new TaskOperator.OnDialogListener() {

            @Override
            public void OnDialogDismiss() {


            }

            @Override
            public void OnClickRight(UpdateTaskStatusEvent event) {

                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        deliverRunnable.run();
                    }
                }, DEALY_TIME);

            }

            @Override
            public void OnClickLeft(UpdateTaskStatusEvent event) {

                cleanCallback();
            }
        });
    }

    /**
     * @param event
     * @param storeDetail
     * @Title: HandleStatusChangeEvent
     * @Description: 处理单状态变化的事件
     * @return: void
     */
    private void HandleStatusChangeEvent(UpdateTaskStatusEvent event, final MtqDeliStoreDetail storeDetail) {
        WaitingProgressTool.showProgress(mCtx);
        DeliveryApi.getInstance().UpdateTaskInfo((Activity) mCtx,event.getCorpid(), event.getTaskid(), event.getStatus(),
                event.getEcorpid(), event.getEtaskid(), event.getX(), event.getY(), event.getCell(), event.getUid(),
                new OnResponseResult<UpdateTaskStatusResult>() {

                    @Override
                    public void OnResult(UpdateTaskStatusResult result) {


                        MLog.e("updatetask", result.getErrCode() + " " + result.getStatus() + " " + result.getTaskid());
                        WaitingProgressTool.closeshowProgress();

                        if (result.getErrCode() != 0) {
                            // 请求错误
                            if (ErrCodeUtil.isNetErrCode(result.getErrCode())) {
                                Toast.makeText(mCtx, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                        // 现在 是同个单
                        isSameTask = true;
                        mEndCorpId = mCurSelectCorpId;
                        mEndTaskId = mCurSelectTaskId;
                        // UpdateTaskStatusResult taskStatusResult = new
                        // UpdateTaskStatusResult(0,
                        // mCurSelectCorpId, mCurSelectTaskId, mEndCorpId,
                        // mEndTaskId, result.getStatus());
                        // TaskOperator.getInstance().HandleResultAfterRequest(taskStatusResult);
                        TaskOperator.getInstance().HandleResultAfterRequest(result);
                        EventBus.getDefault().post(new FreightPointStatusRefreshEvent());

                        if (GeneralSPHelper.getInstance(mCtx.getApplicationContext()).isTaskNavi(result.getTaskid())) {
                            // deliverAndRoute(storeDetail);
                            startOrContinueDeliverAndNavigate(storeDetail);
                        } else {
                            // showNavigationDialog(storeDetail);
                            startOrContinueDeliver(storeDetail);
                        }
                    }

                    @Override
                    public void OnError(int ErrCode) {

                        if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                            // Toast.makeText(TaskPointDetailActivity.this,
                            // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                        } else {
                            if (ErrCodeUtil.isNetErrCode(ErrCode)) {
                                Toast.makeText(mCtx, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
                            }
                        }

                        cleanCallback();
                        WaitingProgressTool.closeshowProgress();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                });
    }

    // 对话框对象
    private TaskAskPopUpDialog mPopUpDialog;

    /**
     * @param storeDetail
     * @Title: ShowChangeAskDialog
     * @Description: 调整运单顺序对话框
     * @return: void
     */
    public void ShowChangeAskDialog(final MtqDeliStoreDetail storeDetail, final Runnable runnable) {

        mPopUpDialog = new TaskAskPopUpDialog(mCtx);

        mPopUpDialog.show();

        mPopUpDialog.setDialogType(7);

        mPopUpDialog.tvTitle.setText("您调整了运货顺序，请确认是否已提货?");

        // 选择优先运货到 " + detail.storename + " ,是否继续?"

        mPopUpDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mPopUpDialog.tvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cleanCallback();
                mPopUpDialog.dismiss();
            }
        });
        mPopUpDialog.tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                mPopUpDialog.dismiss();

                if (runnable != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runnable.run();
                        }
                    }, DEALY_TIME);
                }
            }
        });

    }

    public void ShowNaviDisableDialog(final OnCallBack callback) {

        TaskOperator.getInstance().showNavigationDisableDialog(mCtx, null, new TaskOperator.OnDialogListener() {

            @Override
            public void OnDialogDismiss() {


            }

            @Override
            public void OnClickRight(UpdateTaskStatusEvent event) {

                callback.onYES();
            }

            @Override
            public void OnClickLeft(UpdateTaskStatusEvent event) {

                callback.onYES();
            }
        });

    }

    public void cleanCallback() {
        mJumpRunnable = null;
        mNavigateRunnable = null;
        mChangeSequenceRunnable = null;
        mNewDeliverRunnable = null;
        mRouteSucessable = null;
        mRouteFailable = null;
    }
}
