package com.yunbaba.api.trunk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.yunbaba.api.trunk.bean.UpdateTaskPointStatusResult;
import com.yunbaba.api.trunk.bean.UpdateTaskStatusResult;
import com.yunbaba.api.trunk.listner.OnBooleanListner;
import com.yunbaba.api.trunk.listner.OnObjectListner;
import com.yunbaba.api.trunk.listner.OnQueryResultListner;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.bean.TaskSpInfo;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointStatusRefreshEvent;
import com.yunbaba.freighthelper.bean.eventbus.RefreshTaskListFromNetEvent;
import com.yunbaba.freighthelper.bean.eventbus.UpdateTaskStatusEvent;
import com.yunbaba.freighthelper.ui.customview.TaskAskPopUpDialog;
import com.yunbaba.freighthelper.ui.dialog.NaviDisableDialog;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.OnCallBack;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.SPHelper2;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
import com.yunbaba.ols.module.delivery.bean.MtqHistroyTask2;
import com.yunbaba.ols.module.delivery.bean.MtqHistroyTaskStack;
import com.yunbaba.ols.module.delivery.bean.MtqRequestTime;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 任务列表数据缓存和处理类
 */
public class TaskOperator {

    private static SPHelper sptool = SPHelper.getInstance(MainApplication.getContext());

    private static TaskOperator instance = new TaskOperator();

    private OnTaskListChangeListener listenr = null;

    // 未完成任务列表，不包括当前任务
    private static List<MtqDeliTask> mTasklist;

    // 当前任务列表
    private static MtqDeliTask mCurrentTask;

    // 对话框对象
    private TaskAskPopUpDialog mPopUpDialog;

    private boolean isNotFirstGetTaskList = false;

    public TaskOperator() {
        // init();

//        OrmLiteApi.getInstance().queryAll(MtqDeliTask.class, new OnQueryResultListner<MtqDeliTask>() {
//            @Override
//            public void onResult(List<MtqDeliTask> res) {
//                mTasklist = res;
//                isNotFirstGetTaskList = false;
//                setTaskListData(null, mTasklist, false);
//            }
//        });


        mTasklist = OrmLiteApi.getInstance().queryAll(MtqDeliTask.class);
//        MLog.e("klogin","2account"+ CldKAccountAPI.getInstance().getKuidLogin());
//        MLog.e("klogin","tasklistsize"+ (mTasklist == null ? "0'":(""+mTasklist.size())));

        isNotFirstGetTaskList = false;
        setTaskListData(null, mTasklist, false);


        // MLog.e("check", mTasklist.size() + " ");


        // MLog.e("check2", mTasklist.size() + " ");
        //
        // ThreadPoolTool.getInstance().execute(new Runnable() {
        //
        // @Override
        // public void run() {
        //
        //
        //
        //
        // }
        // });

    }

    public static TaskOperator getInstance() {

        return instance;

    }

    // 设置未完成列表数据
    public synchronized void setTaskListData(Context context, final List<MtqDeliTask> lstOfTask,
                                             boolean isDataFromWeb) {


        if (lstOfTask == null)
            return;


        if (isDataFromWeb) {
            // mCurrentTask = null;

            // MLog.e("taskoperator", "settasklist");
            // 备份上一份任务列表

//            if (mCurrentTask != null)
//                mTasklist.add(mCurrentTask);

            sptool.SetBackUpLastTimeTaskList();


            if (context != null) {

                // 让预警消息服务重新检查一遍任务
                if (isNotFirstGetTaskList) {

                    //TimeEarlyWarningTask.getInstance(context).startCheck(context, false);

                    // Intent sintent = new Intent(context,
                    // TimeEarlyWarningService.class);
                    // sintent.putExtra("rechecktasklist", true);
                    // context.startService(sintent);

                } else {

                    // TimeEarlyWarningTask.getInstance(context).startCheck(context, true);

                    isNotFirstGetTaskList = true;
//                    try {
//                        Intent sintent = new Intent(context, TimeEarlyWarningService.class);
//                        context.startService(sintent);
//                    } catch (Exception e) {
//                    }
                }
            }

        }

        mTasklist.clear();
        mTasklist.addAll(lstOfTask);

        boolean isHasCurrent = false;

        Iterator<MtqDeliTask> iter = mTasklist.iterator();
        MtqDeliTask tmp;
        searchforrunningtask:
        while (iter.hasNext()) {
            // 运货中
            tmp = iter.next();
            if (tmp.status == 1) {
                isHasCurrent = true;
                mCurrentTask = tmp;
                iter.remove();
                break searchforrunningtask;
            }

        }

        if (!isHasCurrent)
            mCurrentTask = null;

        if (isDataFromWeb) {
            // 数据库更新状态列表

            ThreadPoolTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    MLog.e("deletetask123", "all");
                    OrmLiteApi.getInstance().deleteAll(MtqDeliTask.class);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }finally {
                    MLog.e("savetask123", "num:" + lstOfTask.size());
                    OrmLiteApi.getInstance().saveAll2(lstOfTask);
                    //      }

                }
            });


//            ThreadPoolTool.getInstance().execute(new Runnable() {
//
//                @Override
//                public void run() {
//
//
//
//
////                    ArrayList<MtqHistroyTask> list = new ArrayList<>();
////                    MtqHistroyTask tmptask;
////                    for (MtqDeliTask task : lstOfTask) {
////
////                        tmptask = new MtqHistroyTask(task);
////                        list.add(tmptask);
////                    }
////
////                    OrmLiteApi.getInstance().saveAll(list);
//
//                }
//            });

        }

        if (isDataFromWeb && mCurrentTask != null)
            getSPhelper().saveRecentModifyTask(
                    new TaskSpInfo(mCurrentTask.taskid, mCurrentTask.corpid, mCurrentTask.carlicense));

        if (listenr != null) {
            listenr.OnTaskListChange();

        }

    }

    // 设置某任务已暂停
    public void PauseTask(final String corpid, final String taskid, final int status) {

        MLog.e("pausetask", "status::" + status);
        // getSPhelper().saveRecentModifyTask(new TaskSpInfo(taskid, corpid));
//        if (mCurrentTask == null)
//            return;
//
//        if (!mCurrentTask.taskid.equals(taskid)) {
//            // Toast.makeText(MainApplication.getContext(), "所暂停任务非当前运输中任务！",
//            // Toast.LENGTH_SHORT).show();
//            return;
//        }

        //      mCurrentTask.status = status;

        // final String taskids = mCurrentTask.taskid;

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // 数据库更新状态字段
                ColumnsValue cv = new ColumnsValue(new String[]{"_status"}, new Object[]{status});
                OrmLiteApi.getInstance().getLiteOrm().update(
                        new WhereBuilder(MtqDeliTask.class).where("_task_id" + " = ?", new Object[]{taskid}),
                        cv, ConflictAlgorithm.None);


                MtqDeliTaskDetail detail = getTaskDetailDataFromDB(taskid);

                if (detail != null) {


                    for (MtqDeliStoreDetail store : detail.getStore()) {

                        if (store.storestatus == 1) {

                            ColumnsValue cv2 = new ColumnsValue(new String[]{"_store_status"}, new Object[]{3});
                            OrmLiteApi.getInstance().getLiteOrm().update(
                                    new WhereBuilder(MtqDeliStoreDetail.class).where("_waybill" + " = ?", new Object[]{store.waybill}).and("_task_id" + " = ?", new Object[]{taskid}),
                                    cv2, ConflictAlgorithm.None);


                        }

                    }

                }


            }
        });
        MLog.e("pausetask", "status:size" + mTasklist.size());

        if (mCurrentTask != null && mCurrentTask.taskid.equals(taskid)) {
            mCurrentTask.status = 3;

            boolean isHastask = false;
            for (MtqDeliTask task : mTasklist) {

                if (task.taskid.equals(mCurrentTask.taskid)) {
                    isHastask = true;
                }

            }

            if (!isHastask)
                mTasklist.add(mCurrentTask);
            mCurrentTask = null;
        }

        MLog.e("pausetask", "status:size" + mTasklist.size());

        if (listenr != null) {
            listenr.OnTaskListChange();
        }
        // RefreshHeadAndTaskList();

        // mTasklist = OrmLiteApi.getInstance().queryAll(MtqDeliTask.class);
        // MLog.e("check", mTasklist.size() + " ");
        // setTaskListData(mTasklist, false);

        // EventBus.getDefault().post(new FreightPointStatusRefreshEvent());
    }

    // 根据taskid获取任务对象
    public MtqDeliTask GetTask(String taskid) {
        Iterator<MtqDeliTask> iter = mTasklist.iterator();
        MtqDeliTask tmp = null;
        searchforrunningtask:
        while (iter.hasNext()) {
            // 运货中
            tmp = iter.next();
            if (tmp.taskid.equals(taskid)) {

                break searchforrunningtask;
            }
            tmp = null;
        }

        return tmp;

    }

    // 将列表中某一项移除
    public synchronized void RemoveTask(final String taskid, int status) {

        if (taskid == null)
            return;

        MtqDeliTask deletetask = null;

        Iterator<MtqDeliTask> iter = mTasklist.iterator();
        MtqDeliTask tmp;
        // searchforrunningtask:
        while (iter.hasNext()) {
            // 运货中
            tmp = iter.next();
            if (tmp.taskid.equals(taskid)) {
                tmp.status = status;

                deletetask = tmp;
                if (status == 2)
                    sptool.setIsHasFinishTask(tmp.corpid, true);
                iter.remove();

                //break searchforrunningtask;
            }

        }


        if (mCurrentTask != null && taskid.equals(mCurrentTask.taskid)) {
            mCurrentTask.status = status;

            deletetask = mCurrentTask;

            if (status == 2)
                sptool.setIsHasFinishTask(mCurrentTask.corpid, true);

            mCurrentTask = null;
        }


        final MtqDeliTask deletetasks = deletetask;

        if (deletetasks != null) {
            deletetasks.finish_count = deletetasks.store_count;
            ThreadPoolTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {


                    MtqHistroyTaskStack stack = new MtqHistroyTaskStack();

                    stack.histroytask = new MtqHistroyTask2(deletetasks);

                    OrmLiteApi.getInstance().getLiteOrm().save(stack);

                }
            });

        }

        // 数据库删除某个数据
        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {


                OrmLiteApi.getInstance().getLiteOrm()
                        .delete(new WhereBuilder(MtqDeliTask.class)
                                // .whereEquals("_task_id", tmp.taskid)
                                .where("_task_id" + " = ?", new Object[]{taskid}));
            }
        });


        if (listenr != null) {
            listenr.OnTaskListChange();
        }

    }

    // 设置当前任务
    public void ChangeCurrentTask(final String corpid, final String taskid, int status) {

        MLog.e("ChangeCurrentTask", "status::" + status);

        Iterator<MtqDeliTask> iter = mTasklist.iterator();
        MtqDeliTask tmp;
        MtqDeliTask currentBackUp = null;
        searchforrunningtask:
        while (iter.hasNext()) {
            // 运货中
            tmp = iter.next();
            if (tmp.taskid.equals(taskid)) {

                if (mCurrentTask != null) {
                    currentBackUp = mCurrentTask;
                    currentBackUp.status = 3;
                }

                tmp.status = status;
                mCurrentTask = tmp;
                iter.remove();
                break searchforrunningtask;
            }

        }

        if (currentBackUp != null) {
            mTasklist.add(0, currentBackUp);
            OrmLiteApi.getInstance().save(currentBackUp);


            final String taskidss = currentBackUp.taskid;


            ThreadPoolTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    // 数据库更新状态字段
//                    ColumnsValue cv = new ColumnsValue(new String[]{"_status"}, new Object[]{status});
//                    OrmLiteApi.getInstance().getLiteOrm().update(
//                            new WhereBuilder(MtqDeliTask.class).where("_task_id" + " = ?", new Object[]{currentBackUp.taskid}),
//                            cv, ConflictAlgorithm.None);


                    MtqDeliTaskDetail detail = getTaskDetailDataFromDB(taskidss);

                    if (detail != null) {


                        for (MtqDeliStoreDetail store : detail.getStore()) {

                            if (store.storestatus == 1) {

                                ColumnsValue cv2 = new ColumnsValue(new String[]{"_store_status"}, new Object[]{3});
                                OrmLiteApi.getInstance().getLiteOrm().update(
                                        new WhereBuilder(MtqDeliStoreDetail.class).where("_waybill" + " = ?", new Object[]{store.waybill}).and("_task_id" + " = ?", new Object[]{taskidss}),
                                        cv2, ConflictAlgorithm.None);


                            }

                        }

                    }


                }
            });


            // OrmLiteApi.getInstance().saveAll2(mTasklist);
        }

        if (mCurrentTask != null) {
            getSPhelper().saveRecentModifyTask(new TaskSpInfo(taskid, corpid, mCurrentTask.carlicense));
            // 数据库更新单个任务
            OrmLiteApi.getInstance().save(mCurrentTask);
        }

        if (listenr != null) {
            listenr.OnTaskListChange();
        }

    }

    // 显示询问对话框,不做具体业务操作，只是显示对话框而且返回点击结果
    public synchronized void showTaskStatusChangeAskDialog(Context context, final UpdateTaskStatusEvent event,
                                                           final OnDialogListener listener) {

        // 无当前进行中的任务并且是开始运货，则直接请求完跳到选择运货点

        //
        //// 以上是错误的
        //// 判断选择的任务是否已经开始，则从未开始过则直接请求完跳到选择运货点
        //
        if (mCurrentTask == null && event.getStatus() == 1) {
            // HandleStatusChangeEvent(event);
            listener.OnClickRight(event);
            listener.OnDialogDismiss();
            return;
        }

        setPopUpDialog(context);

        mPopUpDialog.show();
        switch (event.getStatus()) {
            case 1:

                mPopUpDialog.setDialogType(3);

                mPopUpDialog.setSpText(mCurrentTask, 3);

                MtqDeliTaskDetail detail = getTaskDetailDataFromDB(mCurrentTask.taskid);
                if (detail != null) {

                    MtqDeliStoreDetail store = GetRunningStoreFromTaskDetail(detail.getStore());

                    if (store != null)
                        mPopUpDialog.setSpText2(store, 1);

                }
//                getTaskDetailDataFromDB(mCurrentTask.taskid, new OnObjectListner<MtqDeliTaskDetail>() {
//                    @Override
//                    public void onResult(MtqDeliTaskDetail res) {
//                        MtqDeliTaskDetail detail = res;
//
//                    }
//                });


                // MtqDeliTask tmp = GetTask(event.getTaskid());
                // if(tmp!=null)
                // mPopUpDialog.setSpText(tmp,3);

                break;
            case 2:
                mPopUpDialog.setDialogType(2);

                MtqDeliTask tmp = GetTask(event.getTaskid());
                if (tmp != null)
                    mPopUpDialog.setSpText(tmp, 2);
                break;
            case 3:
                mPopUpDialog.setDialogType(1);
                break;
            case 4:
                // mPopUpDialog.setDialogType(1);
                break;

            default:
                break;
        }

        // mPopUpDialog.tvTitle.setText(R.string.firmware_checkUpdate_title);
        mPopUpDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mPopUpDialog.tvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                listener.OnClickLeft(event);

                listener.OnDialogDismiss();

                mPopUpDialog.dismiss();
            }
        });
        mPopUpDialog.tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {


                // HandleStatusChangeEvent(event);
                listener.OnClickRight(event);

                listener.OnDialogDismiss();

                mPopUpDialog.dismiss();
            }
        });

    }

    // 处理任务状态变更请求后的结果
    public void HandleResultAfterRequest(UpdateTaskStatusResult result) {

        int status = result.getStatus();
        switch (status) {
            case 0:

                PauseTask(result.getCorpid(), result.getTaskid(), status);
                break;
            case 1:

                ChangeCurrentTask(result.getCorpid(), result.getTaskid(), status);

                break;

            case 2:

                RemoveTask(result.getTaskid(), status);

                break;
            case 3:

                PauseTask(result.getCorpid(), result.getTaskid(), status);
                break;
            default:
                break;
        }
    }

    // 保存单个任务详情到数据库
    public void saveTaskDetailDataToBD(final MtqDeliTaskDetail detail) {

//        ThreadPoolTool.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {
        OrmLiteApi.getInstance().save(detail);
//            }
//        });


    }

    // todo
    // 完成任务需要刷新任务列表
    // 暂停任务

    public synchronized void showNavigationDialog(Context context, final UpdateTaskStatusEvent event,
                                                  final OnDialogListener listener) {

        setPopUpDialog(context);
        mPopUpDialog.show();

        mPopUpDialog.setDialogType(4);
        mPopUpDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mPopUpDialog.tvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                listener.OnClickLeft(event);

                listener.OnDialogDismiss();

                mPopUpDialog.dismiss();
            }
        });
        mPopUpDialog.tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {


                // HandleStatusChangeEvent(event);
                listener.OnClickRight(event);

                listener.OnDialogDismiss();

                mPopUpDialog.dismiss();
            }
        });

        mPopUpDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // if(keyCode == KeyEvent.KEYCODE_BACK &&
                // event.getRepeatCount()==0)
                // {
                // myMaterDia@BindView(dismiss();
                // UserBuyActivity.this.finish();
                // }
                return true;
            }
        });

    }


    // todo
    // 完成任务需要刷新任务列表
    // 暂停任务

    public synchronized void showNavigationDisableDialog(Context context, final UpdateTaskStatusEvent event,
                                                         final OnDialogListener listener) {


        final NaviDisableDialog dsdialog = new NaviDisableDialog(context);
        dsdialog.show();


        dsdialog.setDialogType(4);

        dsdialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        dsdialog.tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {


                // HandleStatusChangeEvent(event);
                listener.OnClickRight(event);

                listener.OnDialogDismiss();

                dsdialog.dismiss();

                //dismiss();
            }
        });


        dsdialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // if(keyCode == KeyEvent.KEYCODE_BACK &&
                // event.getRepeatCount()==0)
                // {
                // myMaterDia@BindView(dismiss();
                // UserBuyActivity.this.finish();
                // }
                return true;
            }
        });

    }

    public void ShowNaviDisableDialog(Context context, final OnCallBack callback) {

        TaskOperator.getInstance().showNavigationDisableDialog(context, null, new OnDialogListener() {

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


    // todo
    // 完成任务需要刷新任务列表
    // 暂停任务

    // 读取单个任务详情从数据库
    public void getTaskDetailDataFromDB(final Activity activity, String taskId, final OnObjectListner<MtqDeliTaskDetail> listner) {

        if (taskId == null)
            listner.onResult(null);

        OrmLiteApi.getInstance().queryByKey(MtqDeliTaskDetail.class,
                MtqDeliTaskDetail.COL_TASK_ID, taskId, new OnQueryResultListner<MtqDeliTaskDetail>() {
                    @Override
                    public void onResult(final List<MtqDeliTaskDetail> res) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res == null || res.size() == 0)
                                    listner.onResult(null);
                                else
                                    listner.onResult(res.get(0));
                            }
                        });


                    }
                });


    }

    // 读取单个任务详情从数据库
    public MtqDeliTaskDetail getTaskDetailDataFromDB(String taskId) {

        if (taskId == null)
            return null;

        List<MtqDeliTaskDetail> res = OrmLiteApi.getInstance().queryByKey(MtqDeliTaskDetail.class,
                MtqDeliTaskDetail.COL_TASK_ID, taskId);
        if (res == null || res.size() == 0)
            return null;
        else
            return res.get(0);

    }

    // 保存单个OrderDetail
    public void saveOrderDetailDataToDB(MtqDeliStoreDetail orderdetail) {

        OrmLiteApi.getInstance().save(orderdetail);

    }

    // 通过配送单号 获取StoreDetail
    public void GetStoreDetailFromDB(String waybill, final OnObjectListner<MtqDeliStoreDetail> listner) {

        OrmLiteApi.getInstance().queryByKey(MtqDeliStoreDetail.class, "_waybill",
                waybill, new OnQueryResultListner<MtqDeliStoreDetail>() {
                    @Override
                    public void onResult(List<MtqDeliStoreDetail> res) {
                        if (res == null || res.size() == 0)
                            listner.onResult(null);
                        else
                            listner.onResult(res.get(0));
                    }
                });


    }

    // 通过配送单号 获取StoreDetail
    public void GetStoreDetailListFromDB(final String[] waybilllist, final OnObjectListner<List<MtqDeliStoreDetail>> listner) {

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                QueryBuilder<MtqDeliStoreDetail> qb = new QueryBuilder<MtqDeliStoreDetail>(MtqDeliStoreDetail.class)
                        .whereIn("_waybill", new Object[]{waybilllist});

                List<MtqDeliStoreDetail> res = OrmLiteApi.getInstance().getLiteOrm().query(qb);
                listner.onResult(res);
            }
        });


    }

    /**
     * * 通过模糊搜索客户单号 获取StoreDetail列表**
     */
    public void SearchStoreDetailByCustorderidFromDB(final String corpid, final String fuzzycustorderid,
                                                     final int start, final int length, final OnObjectListner<List<MtqDeliStoreDetail>> listner) {
        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                List<MtqDeliStoreDetail> res = null;
                QueryBuilder<MtqDeliStoreDetail> qb = null;
                if (TextUtils.isEmpty(corpid)) {

                    qb = new QueryBuilder<MtqDeliStoreDetail>(MtqDeliStoreDetail.class)
                            .where("_cust_order_id" + " LIKE ?", new Object[]{"%" + fuzzycustorderid + "%"})
                            .limit(start, length);
                } else {

                    // IN语句 获取同时满足：corpid等于 & custorderid 包含
                    qb = new QueryBuilder<MtqDeliStoreDetail>(MtqDeliStoreDetail.class).whereEquals("_corp_id", corpid)
                            .whereAppendAnd()
                            .whereAppend("_cust_order_id" + " LIKE ?", new Object[]{"%" + fuzzycustorderid + "%"})
                            .limit(start, length);

                }

                res = OrmLiteApi.getInstance().getLiteOrm().query(qb);
                if (res == null || res.size() == 0)
                    listner.onResult(null);
                else
                    listner.onResult(res);
            }
        });

    }

    /**
     * * 通过模糊搜索客户单号 获取StoreDetail列表**
     */
    public void SearchCuOrderIdListByCustorderidFromDB(final String corpid, final String fuzzycustorderid,
                                                       final int start, final int length, final OnObjectListner<List<MtqRequestTime>> listner) {


        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                QueryBuilder<MtqRequestTime> qb = null;
                if (TextUtils.isEmpty(corpid)) {

                    qb = new QueryBuilder<MtqRequestTime>(MtqRequestTime.class)
                            .where("_cust_order_id" + " LIKE ?", new Object[]{"%" + fuzzycustorderid + "%"})
                            .limit(start, length);
                } else {

                    // IN语句 获取同时满足：corpid等于 & custorderid 包含
                    qb = new QueryBuilder<MtqRequestTime>(MtqRequestTime.class).whereEquals("_corp_id", corpid).whereAppendAnd()
                            .whereAppend("_cust_order_id" + " LIKE ?", new Object[]{"%" + fuzzycustorderid + "%"})
                            .limit(start, length);

                }
                List<MtqRequestTime> res = OrmLiteApi.getInstance().getLiteOrm().query(qb);

                if (res == null || res.size() == 0) {
                    //  listner.onResult(null);

                    listner.onResult(new ArrayList<MtqRequestTime>());

                } else
                    listner.onResult(res);
            }
        });


    }

    // 通过客户单号 获取StoreDetail
    public void GetStoreDetailByCustorderidFromDB(String custorderid, final OnObjectListner<MtqDeliStoreDetail> listner) {

        OrmLiteApi.getInstance().queryByKey(MtqDeliStoreDetail.class, "_cust_order_id",
                custorderid, new OnQueryResultListner<MtqDeliStoreDetail>() {
                    @Override
                    public void onResult(List<MtqDeliStoreDetail> res) {
                        if (res == null || res.size() == 0)
                            listner.onResult(null);
                        else
                            listner.onResult(res.get(0));
                    }
                });


    }

    // 通过客户单号 获取Taskid,corpid
    public void GetTaskInfoByCustorderidFromDB(String custorderid, final OnObjectListner<TaskSpInfo> listner) {

        OrmLiteApi.getInstance().queryByKey(MtqRequestTime.class, "_cust_order_id",
                custorderid, new OnQueryResultListner<MtqRequestTime>() {
                    @Override
                    public void onResult(List<MtqRequestTime> res) {
                        if (res == null || res.size() == 0)
                            listner.onResult(null);
                        else {

                            // 有可能会有重复的客户单号，这个需要问产品了
                            MtqRequestTime time = res.get(0);
                            if (time != null && !TextUtils.isEmpty(time.corpid) && !TextUtils.isEmpty(time.taskid)) {

                                listner.onResult(new TaskSpInfo(time.taskid, time.corpid, ""));

                            } else

                                listner.onResult(null);

                        }
                    }
                });


    }

    // 通过客户单号 获取cust_orderid
    public void GetorderDetailFromDB(final String taskid, final String waybill, final OnObjectListner<MtqDeliOrderDetail> listner) {

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                QueryBuilder<MtqDeliOrderDetail> qb = null;
                qb = new QueryBuilder<MtqDeliOrderDetail>(MtqDeliOrderDetail.class).whereEquals("_task_id", taskid)
                        .whereAppendAnd().whereEquals("_waybill", waybill);

                List<MtqDeliOrderDetail> res = OrmLiteApi.getInstance().getLiteOrm().query(qb);

                if (res == null || res.size() == 0)
                    listner.onResult(null);
                else
                    listner.onResult(res.get(0));
            }
        });


    }

    // 通过客户单号 获取cust_orderid
    public MtqDeliOrderDetail GetorderDetailFromDB(final String taskid, final String waybill) {


        QueryBuilder<MtqDeliOrderDetail> qb = null;
        qb = new QueryBuilder<MtqDeliOrderDetail>(MtqDeliOrderDetail.class).whereEquals("_task_id", taskid)
                .whereAppendAnd().whereEquals("_waybill", waybill);

        List<MtqDeliOrderDetail> res = OrmLiteApi.getInstance().getLiteOrm().query(qb);

        if (res == null || res.size() == 0)
            return null;
        else
            return res.get(0);


    }

    // 通过TASKID 获取MtqDeliTask
    public void getMtqDeliTaskFromDB(String taskid, final OnObjectListner<MtqDeliTask> listner) {

        if (taskid == null)
            listner.onResult(null);

        OrmLiteApi.getInstance().queryByKey(MtqDeliTask.class, "_task_id", taskid, new OnQueryResultListner<MtqDeliTask>() {
            @Override
            public void onResult(List<MtqDeliTask> res2) {
                List<MtqDeliTask> res = res2;
                if (res == null || res.size() == 0)
                    listner.onResult(null);
                else
                    listner.onResult(res.get(0));
                ;

            }
        });


    }


    // 通过TASKID 获取MtqDeliTask
    public MtqDeliTask getMtqDeliTaskFromDB(String taskid) {

        if (taskid == null)
            return null;

        List<MtqDeliTask> res = OrmLiteApi.getInstance().queryByKey(MtqDeliTask.class, "_task_id", taskid);

        if (res == null || res.size() == 0)
            return null;
        else
            return res.get(0);


    }


    public MtqDeliStoreDetail GetRunningStoreFromTaskDetail(List<MtqDeliStoreDetail> list) {

        if (list == null)
            return null;

        Iterator<MtqDeliStoreDetail> iter = list.iterator();
        MtqDeliStoreDetail tmp = null;
        searchforrunningtask:
        while (iter.hasNext()) {
            // 运货中
            tmp = iter.next();
            if (tmp.storestatus == 1) {

                break searchforrunningtask;
            }

            tmp = null;

        }

        return tmp;

    }

    /**
     * 更新数据库中
     */
    public synchronized void UpdateTaskStateByStoreStatusChangeResult(final UpdateTaskPointStatusResult storeupdateresult) {

//        ThreadPoolTool.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {
        // 如果操作的不是当前运输中的任务 则不管
//                if (mCurrentTask == null || !storeupdateresult.getTaskid().equals(getmCurrentTask().taskid)) {
//
//                    MLog.e("storestatus", "updateres  error  currenttask null");
//                    return;
//                }

        MtqDeliTask mCurrentTask = getMtqDeliTaskFromDB(storeupdateresult.getTaskid());

        if (mCurrentTask == null)
            return;

        /** 运货点状态【0待运货1运货中2已完成3暂停状态 4-中止运货】 **/
        switch (storeupdateresult.getStatus()) {
            case 0:

                break;
            case 1:
                //ChangeCurrentTask(result.getCorpid(), result.getTaskid(), status);
                break;
            case 2: // 完成
                mCurrentTask.finish_count += 1;

                ColumnsValue cv = new ColumnsValue(new String[]{"_finish_count"},
                        new Object[]{mCurrentTask.finish_count});

                int updateres = OrmLiteApi.getInstance().getLiteOrm().update(new WhereBuilder(MtqDeliTask.class)
                                .where("_task_id" + " = ?", new Object[]{mCurrentTask.taskid}), cv,
                        ConflictAlgorithm.None);


                if (mCurrentTask.finish_count >= mCurrentTask.store_count) {

                    RemoveTask(storeupdateresult.getTaskid(), 2);

                } else {


                }

                break;
            case 3:

                break;
            case 4:

                break;

            default:
                break;
        }

        if (mCurrentTask != null) {

            if (storeupdateresult.getData() == null) {

                if (storeupdateresult.getStatus() == 2)
                    EventBus.getDefault().post(new FreightPointStatusRefreshEvent());

            } else {

                boolean isNeedRefresh = false;

                if (storeupdateresult.getStatus() == 2) {

                    isNeedRefresh = true;

                }

//                if (!mCurrentTask.pdeliver.equals(storeupdateresult.getData().pdeliver)
//                        || !mCurrentTask.preceipt.equals(storeupdateresult.getData().preceipt)
//                        || mCurrentTask.freight_type != storeupdateresult.getData().freight_type) {
//
//                    isNeedRefresh = true;
//
//                    if (storeupdateresult.getData().pdeliver != null)
//                        mCurrentTask.pdeliver = storeupdateresult.getData().pdeliver;
//
//                    if (storeupdateresult.getData().preceipt != null)
//                        mCurrentTask.preceipt = storeupdateresult.getData().preceipt;
//
//                    if (storeupdateresult.getData().freight_type != 0)
//                        mCurrentTask.freight_type = storeupdateresult.getData().freight_type;

//                    ColumnsValue cv = new ColumnsValue(new String[]{"_pdeliver", "_preceipt", "_freight_type"},
//                            new Object[]{mCurrentTask.pdeliver, mCurrentTask.preceipt, mCurrentTask.freight_type
//
//                                    // storeupdateresult.getData().pdeliver,
//                                    // storeupdateresult.getData().preceipt,
//                                    // storeupdateresult.getData().freight_type
//
//                            });
//
//                    int updateres = OrmLiteApi.getInstance().getLiteOrm().update(new WhereBuilder(MtqDeliTask.class)
//                                    .where("_task_id" + " = ?", new Object[]{mCurrentTask.taskid}), cv,
//                            ConflictAlgorithm.None);

                // MLog.e("storedeliveryhint", "updatedeliveryhint" +
                // updateres);

//                }
//
//                MLog.e("delivery", "pdeliver" + storeupdateresult.getData().pdeliver + " "
//                        + storeupdateresult.getData().preceipt + " " + storeupdateresult.getData().freight_type);
//
                if (isNeedRefresh)
                    EventBus.getDefault().post(new FreightPointStatusRefreshEvent());// FreightPointStatusRefreshEvent());
            }


        }

        // 更新数据库里store的状态
        if (storeupdateresult.getWaybill() != null) {


            try {
                MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(storeupdateresult.getTaskid());


                if (detail == null)
                    return;

                // MtqDeliStoreDetail store  =

                for (MtqDeliStoreDetail store : detail.getStore()) {

                    if (store.waybill.equals(storeupdateresult.getWaybill())) {

                        store.storestatus = storeupdateresult.getStatus();

                    }else  if (!TextUtils.isEmpty(storeupdateresult.getEwaybill())
                            && storeupdateresult.getEwaybill().equals(store.waybill)) {


                        store.storestatus = 3;

                    }

                }

                TaskOperator.getInstance().saveTaskDetailDataToBD(detail);




            } catch (Exception e) {

            }


        }


//            ColumnsValue cv = new ColumnsValue(new String[]{"_store_status"},
//                    new Object[]{storeupdateresult.getStatus()});
//            int updateres = OrmLiteApi.getInstance().getLiteOrm().update(new WhereBuilder(MtqDeliStoreDetail.class)
//                            .where("_waybill" + " = ?", new Object[]{storeupdateresult.getWaybill()}), cv,
//                    ConflictAlgorithm.None);

        //       MLog.e("storestatus", "updateres" + updateres + " " + storeupdateresult.getWaybill());

   //     if (!TextUtils.isEmpty(storeupdateresult.getEwaybill())
    //            && !storeupdateresult.getEwaybill().equals(storeupdateresult.getWaybill())) {

//                ColumnsValue cv2 = new ColumnsValue(new String[]{"_store_status"}, new Object[]{3});
//                int updateres2 = OrmLiteApi.getInstance().getLiteOrm().update(new WhereBuilder(MtqDeliStoreDetail.class)
//                                .where("_waybill" + " = ?", new Object[]{storeupdateresult.getEwaybill()}), cv2,
//                        ConflictAlgorithm.None);
//
//                MLog.e("storestatus", "stop store updateres" + updateres2 + " " + storeupdateresult.getWaybill());

            //   }

      //  }

        if (listenr != null)

        {
            listenr.OnTaskListChange();
        }

        if (storeupdateresult.getStatus() == 2)

        {

            EventBus.getDefault().post(new RefreshTaskListFromNetEvent());

        }
//            }
//        });


    }

    public MtqDeliTask getmCurrentTask() {
        return mCurrentTask;
    }

    public void setmCurrentTask(MtqDeliTask mCurrentTask) {
        this.mCurrentTask = mCurrentTask;
    }

    public ArrayList<MtqDeliTask> getmTasklist() {
        return (ArrayList<MtqDeliTask>) mTasklist;
    }

    public void setmTasklist(ArrayList<MtqDeliTask> mTasklist) {
        this.mTasklist = mTasklist;
    }

    public OnTaskListChangeListener getListenr() {
        return listenr;
    }

    public void setListenr(OnTaskListChangeListener listenr) {
        this.listenr = listenr;
    }

    public synchronized void setPopUpDialog(Context context) {
        if (mPopUpDialog != null && context.hashCode() != mPopUpDialog.getContext().hashCode()) {
            mPopUpDialog = null;
        }

        if (mPopUpDialog == null)
            mPopUpDialog = new TaskAskPopUpDialog(context);
    }

    // 检查是否重新登录后导致需要重建liteorm对象

    public void CheckIsReLoginAndHandle(Context context) {

        if (OrmLiteApi.getInstance().CheckSelfAndFixDBName(context)) {

            MLog.e("重建liteorm", "重建liteorm");

            SPHelper.getInstance(context).reCreate();
            SPHelper2.getInstance(context).reCreate();
            mTasklist.clear();
            mCurrentTask = null;

            isNotFirstGetTaskList = false;
            EventBus.getDefault().post(new RefreshTaskListFromNetEvent());

        }
    }

    public interface OnTaskListChangeListener {

        public void OnTaskListChange();

    }

    public interface OnDialogListener {

        // 点击左边按钮一般是取消
        public void OnClickLeft(UpdateTaskStatusEvent event);

        // 点击左边按钮一般是确定
        public void OnClickRight(UpdateTaskStatusEvent event);

        // 对话框消息监听
        public void OnDialogDismiss();

    }

    public void getAllTaskList(final OnObjectListner<List<MtqDeliTask>> listner) {

        OrmLiteApi.getInstance().queryAll(MtqDeliTask.class, new OnQueryResultListner<MtqDeliTask>() {
            @Override
            public void onResult(List<MtqDeliTask> res) {
                listner.onResult(res);
            }
        });
    }

    public synchronized SPHelper getSPhelper() {

        if (sptool == null)
            sptool = SPHelper.getInstance(MainApplication.getContext());

        return sptool;
    }

    public String completelyDeleteTaskByTaskIdList(ArrayList<String> taskIdList) {


        if (taskIdList == null)
            return null;

        // MLog.e("check", "delete taskid" + taskIdList.toString());

        String orderidlist = "";

        for (String taskid : taskIdList) {

            OrmLiteApi.getInstance().queryByKey(MtqRequestTime.class, "_task_id",
                    taskid, new OnQueryResultListner<MtqRequestTime>() {
                        @Override
                        public void onResult(List<MtqRequestTime> res) {
                            List<MtqRequestTime> timelist = res;
                            for (MtqRequestTime time : timelist) {

                                OrmLiteApi.getInstance().getLiteOrm().delete(time);

                            }
                        }
                    });


            if (mCurrentTask != null && mCurrentTask.taskid != null && taskid != null) {

                if (mCurrentTask.taskid.equals(taskid)) {

//                    if (mCurrentTask.req_times != null) {
//
//                        for (MtqRequestTime time : mCurrentTask.req_times) {
//                            orderidlist = orderidlist + "(" + time.cust_orderid + ")";
//                        }
//                    }
                    mCurrentTask = null;
                }

            }

            over:
            for (MtqDeliTask task : mTasklist) {

                if (task != null && task.taskid != null && taskid != null)
                    if (task.taskid.equals(taskid)) {

//                        if (task.req_times != null) {
//                            for (MtqRequestTime time : task.req_times) {
//                                orderidlist = orderidlist + "(" + time.cust_orderid + ")";
//                            }
//                        }

                        mTasklist.remove(task);

                        break over;
                    }

            }

        }

        sptool.deleteRecentCheckOrderList(taskIdList);

        if (listenr != null) {
            listenr.OnTaskListChange();
        }

        return orderidlist;

    }

    /**
     * 判断是否更改了推荐的送货顺序
     */
    public boolean isChangeSequence(MtqDeliTaskDetail mTaskDetail, MtqDeliStoreDetail selectstore) {


        // 如果是提货点就直接返回false

        // 如果是提并送
        // 就判断对应的那个任务的提货点是否完成

        // 如果是提货和送货
        // 则判断该freight_type的任务是否已完成

        // MLog.e("isChangeSequence", "" + selectstore.freight_type +
        // GsonTool.getInstance().toJson(mTaskDetail.store));

        // MLog.e("isChangeSequence", " " +
        // GsonTool.getInstance().toJson(selectstore));

        if (selectstore.optype == 3)
            return false;

        boolean res = false;

        if (selectstore.optype == 3) {

            // String cuid = selectstore.cust_orderid;

            // MtqDeliOrderDetail slorder ;

            // MLog.e("isChangeSequence", "" +
            // GsonTool.getInstance().toJson(mTaskDetail.getStore()));

            over:
            for (MtqDeliStoreDetail store : mTaskDetail.getStore()) {
                if (!store.storecode.equals(selectstore.storecode) && store.waybill.equals(selectstore.waybill)) {

                    if (store.storestatus == 2)
                        res = false;
                    else
                        res = true;

                    break over;

                }
            }

            return res;

        } else if (selectstore.optype == 1 || selectstore.optype == 2) {

            MLog.e("isChangeSequence", "2");

            res = false;

            over:
            for (MtqDeliStoreDetail store : mTaskDetail.getStore()) {
                if (!store.waybill.equals(selectstore.waybill) && (store.optype == 3)) {
                    MLog.e("check", GsonTool.getInstance().toJson(store));
                    if (store.storestatus != 2) {
                        MLog.e("check", "istrue");
                        res = true;
                        break over;
                    }

                }
            }
            MLog.e("check", "istrue " + res);
            return res;
        } else {

            // res = true;

            res = false;

            over:
            for (MtqDeliStoreDetail store : mTaskDetail.getStore()) {

//                if (store.waybill == selectstore.waybill) {
//
//                    break over;
//
//                } else {

                if (!store.waybill.equals(selectstore.waybill) && store.storestatus != 2 && (store.optype == 3)) {
                    res = true;
                    break over;

                }

                //  }
            }

            return res;

        }

    }

    /**
     * 根据企业id过滤正在运输中的任务
     **/
    public ArrayList<MtqDeliTask> getWaitingTaskListAfterSort(String currentCorpId) {



        if (mTasklist != null) {


            if (TextUtils.isEmpty(currentCorpId)) {

                Collections.sort(mTasklist, new Comparator<MtqDeliTask>() {

                    @Override

                    public int compare(MtqDeliTask obj1, MtqDeliTask obj2) {

                        long obj1time = obj1.status == 1 ? obj1.departtime - ONE_YEAR_TIMSTAMP : obj1.departtime;
                        long obj2time = obj2.status == 1 ? obj2.departtime - ONE_YEAR_TIMSTAMP : obj2.departtime;


                        return ((Long) obj1time).compareTo(((Long) obj2time));

                    }

                });


                String currenttaskid = (mCurrentTask != null) ? mCurrentTask.taskid : "";

                if (!TextUtils.isEmpty(currenttaskid)) {

                    Iterator<MtqDeliTask> iter = mTasklist.iterator();
                    MtqDeliTask tmp;
                    while (iter.hasNext()) {
                        // 运货中
                        tmp = iter.next();
                        if (tmp.taskid.equals(currenttaskid)) {

                            iter.remove();
                            // break searchforrunningtask;
                        }

                    }
                }

                return (ArrayList<MtqDeliTask>) mTasklist;
            }
            ArrayList<MtqDeliTask> tmplist = new ArrayList<MtqDeliTask>();

            // Iterator<MtqDeliTask> iter = mTasklist.iterator();
            // MtqDeliStoreDetail tmp;
            // while (iter.hasNext()) {
            // // 运货中
            // tmp = iter.next();
            // if (tmp.storestatus == 2) {
            //
            // iter.remove();
            // // break searchforrunningtask;
            // }
            //
            // }
            String currenttaskid = (mCurrentTask != null) ? mCurrentTask.taskid : "";

            for (MtqDeliTask task : mTasklist) {

                if (!TextUtils.isEmpty(currenttaskid)) {
                    if (task.taskid.equals(currenttaskid)) {


                    } else {

                        if (task.corpid.equals(currentCorpId))
                            tmplist.add(task);

                    }
                } else {
                    if (task.corpid.equals(currentCorpId))
                        tmplist.add(task);
                }

            }

            // 排序
            // 暂停运货的在等待运货的前面
            // 然后各部分按下发时间排序
            // 等待运货的直接加上一年的时间戳来比较

            Collections.sort(tmplist, new Comparator<MtqDeliTask>() {

                @Override

                public int compare(MtqDeliTask obj1, MtqDeliTask obj2) {

                    long obj1time = obj1.status == 1 ? obj1.departtime - ONE_YEAR_TIMSTAMP : obj1.departtime;
                    long obj2time = obj2.status == 1 ? obj2.departtime - ONE_YEAR_TIMSTAMP : obj2.departtime;

                    return ((Long) obj1time).compareTo(((Long) obj2time));

                }

            });
            return tmplist;
        } else
            return new ArrayList<>();

    }

    public static final long ONE_YEAR_TIMSTAMP = 3600 * 24 * 365L;

    // 任务是否存在该id的
    public void isExistTask(String taskId, final OnBooleanListner listner) {

        OrmLiteApi.getInstance().queryByKey(MtqDeliTask.class, "_task_id", taskId, new OnQueryResultListner<MtqDeliTask>() {
            @Override
            public void onResult(List<MtqDeliTask> res) {
                if (res == null || res.size() == 0)
                    listner.onResult(false);
                else
                    listner.onResult(true);
            }
        });


    }

    // 任务是否存在该id的
    public boolean isExistTask(String taskId) {

//        OrmLiteApi.getInstance().queryByKey(MtqDeliTask.class, "_task_id", taskId, new OnQueryResultListner<MtqDeliTask>() {
//            @Override
//            public void onResult(List<MtqDeliTask> res) {
//                if (res == null || res.size() == 0)
//                    listner.onResult(false);
//                else
//                    listner.onResult(true);
//            }
//        });

        if (mCurrentTask != null) {
            if (mCurrentTask.taskid.equals(taskId)) {

                return true;
            }
        }

        boolean isfind = false;

        if (mTasklist != null) {
            over:
            for (MtqDeliTask task : mTasklist) {

                if (task.taskid.equals(taskId)) {

                    isfind = true;
                    break over;

                }
            }

        }

        if (isfind)
            return true;
        else
            return false;

    }

    public void ClearData() {

        mCurrentTask = null;
        mTasklist.clear();

    }

    public String getCustOrderIdList(ArrayList<TaskSpInfo> refreshtaskIdList) {

        if (refreshtaskIdList == null)
            return "";

        String orderidlist = "";

        for (TaskSpInfo info : refreshtaskIdList) {

            if (info != null && info.cu_orderid != null) {

                if (!TextUtils.isEmpty(info.cu_orderid)) {

                    orderidlist = orderidlist + "(" + info.cu_orderid + ")";

                }

            }

        }

        return orderidlist;

    }

    /**
     * 根据
     */
    public String getCustOrderIdByWayBill(String corpId, String taskId, String waybill) {


        return null;
    }

}
