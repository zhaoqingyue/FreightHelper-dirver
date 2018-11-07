package com.yunbaba.api.trunk;

import android.content.Context;

import com.yunbaba.freighthelper.bean.TaskSpInfo;

import java.util.ArrayList;

public class CheckTaskExpireTool {

    public static void CheckTask(final Context context, final String corpid, final String taskid) {

//        DeliveryApi.getInstance().getTaskDetailInServer(taskid, corpid, new OnResponseResult<MtqDeliTaskDetail>() {
//
//            @Override
//            public void OnResult(MtqDeliTaskDetail result) {
//
//
//                if (context == null || result == null || result.getOrders() == null || result.getStore() == null
//                        ) {
//
//                    return;
//
//                }
//
//                TaskOperator.getInstance().saveTaskDetailDataToBD(result);
//
//                for (MtqDeliOrderDetail order : result.getOrders()) {
//
//                    if (context == null)
//                        return;
//
//                    if (!GeneralSPHelper.getInstance(context).isFinishRemindOrder(taskid + order.orderno, 17)) {
//                        if (order.expired != 0) {
//
//                            for (MtqDeliStoreDetail store : result.getStore()) {
//
//                                if (store.waybill != null && store.waybill.equals(order.waybill)) {
//
//                                    String pointName = "";
//
//                                    if (TextUtils.isEmpty(store.storecode) && !TextUtils.isEmpty(store.storename) && store.storeaddr.contains(store.storename)) {
//                                        pointName = (store.regionname + store.storeaddr).replaceAll("\\s*", "");
//                                    } else {
//                                        pointName = (TextUtils.isEmpty(store.storecode) ? "" : (store.storecode + "-"))
//                                                + store.storename;
//                                    }
//                                    String warningText = TimeEarlyWarningService.combineTaskInfoToContent(result, 17,
//                                            pointName);
//
//                                    GeneralSPHelper.getInstance(context).FinishRemindOrder(taskid + order.orderno, 17);
//
//                                    MsgManager.getInstance().createRemindMsg(warningText);
//                                }
//
//                            }
//
//                        }
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void OnError(int ErrCode) {
//
//            }
//
//            @Override
//            public void OnGetTag(String Reqtag) {
//
//
//            }
//        });
    }

    public static void CheckTaskExpire(final Context context, final ArrayList<TaskSpInfo> refreshtaskIdList) {

//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//
//                for (TaskSpInfo tmp : refreshtaskIdList) {
//
//                    if (!TextUtils.isEmpty(tmp.corpid) && !TextUtils.isEmpty(tmp.taskid)) {
//
//                        CheckTask(context.getApplicationContext(), tmp.corpid, tmp.taskid);
//
//                    }
//
//                }
//            }
//        }, 5000);

    }

}
