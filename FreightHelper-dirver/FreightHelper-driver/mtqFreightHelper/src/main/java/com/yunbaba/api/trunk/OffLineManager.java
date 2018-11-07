package com.yunbaba.api.trunk;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.yunbaba.api.trunk.listner.OnBooleanListner;
import com.yunbaba.freighthelper.bean.TaskSpInfo;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
import com.yunbaba.ols.module.delivery.bean.TStoreStatusFinish;
import com.yunbaba.ols.module.delivery.bean.TStoreStatusPause;
import com.yunbaba.ols.module.delivery.bean.TStoreStatusStart;
import com.yunbaba.ols.module.delivery.bean.TaskStore;
import com.yunbaba.ols.module.delivery.tool.CldKDeviceAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hmi.packages.HPDefine;

/**
 * Created by zhonghm on 2018/4/24.
 */

public class OffLineManager {


    private static OffLineManager instance = new OffLineManager();

    public static OffLineManager getInstance() {
        return instance;
    }

    private boolean isRunning = false;

    private ArrayList<TaskSpInfo> updatelist = new ArrayList<>();

    //,long createtime
    public void saveOfflineStoreStatus(HPDefine.HPWPoint point, String waybill, int status, String taskid, String corpid, String ewaybill) {

        if (TextUtils.isEmpty(waybill) || TextUtils.isEmpty(taskid) || TextUtils.isEmpty(corpid))
            return;



        //先处理暂停，再处理开始


        if (!TextUtils.isEmpty(ewaybill)) {

            TaskStore tmpStore2 = null;

            QueryBuilder<TaskStore> qb2 = new QueryBuilder<TaskStore>(TaskStore.class)
                    .where("_waybill" + " = ?", new Object[]{(ewaybill)}).whereAnd("_taskid" + " = ?",new Object[]{(taskid)});

            List<TaskStore> res2 = OrmLiteApi.getInstance().getLiteOrm().query(qb2);

            if (res2 != null && res2.size() > 0) {
                tmpStore2 = res2.get(0);
            }
            //如果已经完成，直接结束

            if(tmpStore2!=null && tmpStore2.finish!=null)
                return;


            //如果已经开始，则去掉开始
            if (tmpStore2 != null && tmpStore2.start != null) {
                tmpStore2.start = null;

                if ((tmpStore2.finish != null || tmpStore2.start != null || tmpStore2.pause != null)&&!TextUtils.isEmpty(tmpStore2.waybill)) {
                    OrmLiteApi.getInstance().save(tmpStore2);

                } else {

                    OrmLiteApi.getInstance().getLiteOrm()
                            .delete(new WhereBuilder(TaskStore.class).where("_waybill" + " = ?", new Object[]{(ewaybill)}).and("_taskid" + " = ?", new Object[]{(taskid)}));
                    // .whereEquals("_task_id", tmp.taskid)

                }


            }else {


                //如果没有开始，则加个暂停
                if(tmpStore2==null)
                    tmpStore2 = new TaskStore();


                tmpStore2.waybill = ewaybill;
                tmpStore2.taskid =taskid;
                tmpStore2.corpid = corpid;

                TStoreStatusPause tmpStatusp = new TStoreStatusPause();

                tmpStatusp.uptime = CldKDeviceAPI.getSvrTime();

                tmpStatusp.cell = 0;
                tmpStatusp.uid = 0;
                tmpStatusp.x = point.x;
                tmpStatusp.y = point.y;

                tmpStore2.pause = tmpStatusp;


                if ((tmpStore2.finish != null || tmpStore2.start != null || tmpStore2.pause != null) && !TextUtils.isEmpty(tmpStore2.waybill)) {
                    OrmLiteApi.getInstance().save(tmpStore2);

                } else {

                    OrmLiteApi.getInstance().getLiteOrm()
                            .delete(new WhereBuilder(TaskStore.class).where("_waybill" + " = ?", new Object[]{(ewaybill)}).and("_taskid" + " = ?", new Object[]{(taskid)}));
                    // .whereEquals("_task_id", tmp.taskid)

                }


            }


        }












        MLog.e("OffLineManagersaveOff", taskid + "" + status);

        TaskStore tmpStore = null;

        QueryBuilder<TaskStore> qb = new QueryBuilder<TaskStore>(TaskStore.class)
                .where("_waybill" + " = ?", new Object[]{(waybill)}).whereAnd("_taskid" + " = ?", new Object[]{(taskid)});

        List<TaskStore> res = OrmLiteApi.getInstance().getLiteOrm().query(qb);

        if (res != null && res.size() > 0) {
            tmpStore = res.get(0);
        }


        if (tmpStore == null) {

            tmpStore = new TaskStore();
//            tmpStore.waybill = waybill;
//            tmpStore.taskid = taskid;
//            tmpStore.corpid = corpid;
        }

        tmpStore.waybill = waybill;
        tmpStore.taskid = taskid;
        tmpStore.corpid = corpid;

        //1-正在配送中，2-已完成配送，3-暂停送货 ）


        switch (status) {

            case 1:

                if(tmpStore.start == null) {
                    TStoreStatusStart tmpStatus = new TStoreStatusStart();

//                if(CldKDeviceAPI.getSvrTime()<createtime){
//
//                    tmpStatus.uptime = createtime+100;
//
//                }else{
                    tmpStatus.uptime = CldKDeviceAPI.getSvrTime();
                    //  }


                    tmpStatus.cell = 0;
                    tmpStatus.uid = 0;
                    tmpStatus.x = point.x;
                    tmpStatus.y = point.y;
                    tmpStore.start = tmpStatus;
                }
                tmpStore.pause = null;
                break;
            case 2:
                TStoreStatusFinish tmpStatusf = new TStoreStatusFinish();

                tmpStatusf.uptime = CldKDeviceAPI.getSvrTime();

                tmpStatusf.cell = 0;
                tmpStatusf.uid = 0;
                tmpStatusf.x = point.x;
                tmpStatusf.y = point.y;
                tmpStore.finish = tmpStatusf;

                tmpStore.pause = null;
                break;

            case 3:
                //tmpStore.start = null;
                if (tmpStore.start != null)
                    tmpStore.start = null;
                else {

                    TStoreStatusPause tmpStatusp = new TStoreStatusPause();

                    tmpStatusp.uptime = CldKDeviceAPI.getSvrTime();

                    tmpStatusp.cell = 0;
                    tmpStatusp.uid = 0;
                    tmpStatusp.x = point.x;
                    tmpStatusp.y = point.y;

                    tmpStore.pause = tmpStatusp;

                }
                break;


        }


        if ((tmpStore.finish != null || tmpStore.start != null || tmpStore.pause != null)&&!TextUtils.isEmpty(tmpStore.waybill)) {
            OrmLiteApi.getInstance().save(tmpStore);

        } else {

            OrmLiteApi.getInstance().getLiteOrm()
                    .delete(new WhereBuilder(TaskStore.class).where("_waybill" + " = ?", new Object[]{(waybill)}).and("_taskid" + " = ?", new Object[]{(taskid)}));
            // .whereEquals("_task_id", tmp.taskid)

        }




    }


    public void pauseOfflineStoreStatus(final HPDefine.HPWPoint point, int status, final String taskid, final String corpid, final OnBooleanListner lis) {

        if (TextUtils.isEmpty(taskid) || TextUtils.isEmpty(corpid)) {
            lis.onResult(true);
            return;
        }


        MLog.e("OffLineManagerpauseOff", taskid + "" + corpid);

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {


                QueryBuilder<TaskStore> qb = new QueryBuilder<TaskStore>(TaskStore.class)
                        .where("_taskid" + " = ?", new Object[]{(taskid)});

                List<TaskStore> res = OrmLiteApi.getInstance().getLiteOrm().query(qb);

                if (!(res == null || res.size() == 0)) {


                    for (TaskStore ts : res) {
                        // tmpStore = ts;


                        if (ts.finish == null) {


                            if (ts.start != null)
                                ts.start = null;
                            else {

                                TStoreStatusPause tmpStatusp = new TStoreStatusPause();

                                tmpStatusp.uptime = CldKDeviceAPI.getSvrTime();

                                tmpStatusp.cell = 0;
                                tmpStatusp.uid = 0;
                                tmpStatusp.x = point.x;
                                tmpStatusp.y = point.y;

                                ts.pause = tmpStatusp;

                            }


                            if ((ts.finish != null || ts.start != null || ts.pause != null)&&!TextUtils.isEmpty(ts.waybill)) {
                                OrmLiteApi.getInstance().save(ts);

                            } else {

                                OrmLiteApi.getInstance().getLiteOrm()
                                        .delete(new WhereBuilder(TaskStore.class).where("_waybill" + " = ?", new Object[]{(ts.waybill)}).and("_taskid" + " = ?", new Object[]{(taskid)}));
                                // .whereEquals("_task_id", tmp.taskid)

                            }
                        }
                    }
                }


                MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);

                if (detail != null) {


                    for (MtqDeliStoreDetail store : detail.getStore()) {

                        if (store.storestatus == 1) {


                            saveOfflineStoreStatus(point, store.waybill, 3, store.taskId,store.corpId, "") ;


                        }

                    }

                }




                lis.onResult(true);
            }
        });


    }


    public synchronized void TraverseOfflineData(final Context context) {


        if (isRunning)
            return;


       // isRunning = true;
     //   MLog.e("OffLineManagerTraverse", "true");
        TaskStore tmpStore = null;

        QueryBuilder<TaskStore> qb = new QueryBuilder<TaskStore>(TaskStore.class)
                .limit(0, 1);

        List<TaskStore> res = OrmLiteApi.getInstance().getLiteOrm().query(qb);

        if (res != null && res.size() > 0) {
            tmpStore = res.get(0);


            if(TextUtils.isEmpty(tmpStore.waybill)){

                OrmLiteApi.getInstance().deleteAll2(TaskStore.class);
            }

            //若数据不齐则删除数据
            if (TextUtils.isEmpty(tmpStore.corpid) || TextUtils.isEmpty(tmpStore.taskid) ||

                    (tmpStore.finish == null && tmpStore.start == null && tmpStore.pause == null)
                    ) {

                OrmLiteApi.getInstance().delete(tmpStore);

                new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       // isRunning = false;
                        TraverseOfflineData(context);
                    }
                }, 2000);


                return;
            }


            isRunning = true;

            final TaskStore ftmpStore = tmpStore;


            ThreadPoolTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {


                    final String taskid = ftmpStore.taskid;
                    final String corpid = ftmpStore.corpid;
                    QueryBuilder<TaskStore> qb2 = new QueryBuilder<TaskStore>(TaskStore.class)
                            .whereIn("_taskid", new Object[]{(taskid)});


                    List<TaskStore> taskStoreslist = OrmLiteApi.getInstance().getLiteOrm().query(qb2);


                    Iterator<TaskStore> it = taskStoreslist.iterator();
                    while (it.hasNext()) {
                        TaskStore tmpStore = it.next();
                        if (tmpStore.finish == null && tmpStore.start == null && tmpStore.pause== null) {


                            MLog.e("OffLineManagerTraverse", "remove"+tmpStore.taskid);
                            it.remove();
                        } else if (tmpStore.finish != null) {
                              // && tmpStore.start == null
//                            tmpStore.start = new TStoreStatusStart();
//                            tmpStore.start.x = tmpStore.finish.x;
//                            tmpStore.start.y = tmpStore.finish.y;
//                            tmpStore.start.uptime = tmpStore.finish.uptime - 50;

                            if(tmpStore.start!=null){

                                if(tmpStore.start.uptime >= tmpStore.finish.uptime) {
                                    tmpStore.start.uptime = tmpStore.finish.uptime - 60;
                                }

                                //tmpStore.start.uptime = tmpStore.finish.uptime - 5;

                            }

                            tmpStore.pause = null;

                        }


                    }


                    if (!(taskStoreslist != null && taskStoreslist.size() > 0)) {
                        isRunning = false;
                        new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                               // isRunning = false;
                                TraverseOfflineData(context);
                            }
                        }, 2000);
                        return;

                    }


                    UploadData(taskid, corpid, taskStoreslist, updatelist, new OnUploadDataListner() {
                        @Override
                        public void onGetResult(int errCode, List<CldSapKDeliveryParam.StoreStatusResult> lstOfResult) {

                            if (errCode == 0 || !TaskUtils.isNetWorkError(errCode)) {

                                if (updatelist == null)
                                    updatelist = new ArrayList<TaskSpInfo>();


                                updatelist.add(new TaskSpInfo(taskid, corpid, "1900"));


                                OrmLiteApi.getInstance().getLiteOrm()
                                        .delete(new WhereBuilder(TaskStore.class)
                                                // .whereEquals("_task_id", tmp.taskid)
                                                .where("_taskid" + " = ?", new Object[]{taskid}));


                            }


                            isRunning = false;

                            if (!TaskUtils.isNetWorkError(errCode)) {
                                new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        TraverseOfflineData(context);
                                    }
                                }, 200);
                            } else {

                                remindUi();


                            }
                            return;
                        }
                    });
                }
            });


        } else {
           // isRunning = false;
            remindUi();

        }


    }


    public void remindUi() {

        if (updatelist != null && updatelist.size() > 0) {

            EventBus.getDefault().post(new TaskBusinessMsgEvent(2, null, updatelist));
            updatelist.clear();
        }
    }


    public interface OnUploadDataListner {

        public void onGetResult(int errCode, List<CldSapKDeliveryParam.StoreStatusResult> lstOfResult);

    }

    public void UploadData(String taskid, String corpid, List<TaskStore> taskStoreslist, final ArrayList<TaskSpInfo> updatelist, final OnUploadDataListner listner) {

        MLog.e("OffLineManagerTraverse", "uploaddata");


        CldKDeliveryAPI.getInstance().batchReportTaskstoreStatus(corpid, taskid, taskStoreslist, new CldKDeliveryAPI.IBatchReportTaskstoreStatusListener() {
            @Override
            public void onGetResult(int errCode, List<CldSapKDeliveryParam.StoreStatusResult> lstOfResult) {

                if (listner != null)
                    listner.onGetResult(errCode, lstOfResult);

//                if(errCode == 0)
//                {
//
//
//
//
//
//                }


            }

            @Override
            public void onGetReqKey(String arg0) {

            }
        });


    }


}
