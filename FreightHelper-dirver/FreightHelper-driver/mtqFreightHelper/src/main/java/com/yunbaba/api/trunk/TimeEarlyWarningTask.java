package com.yunbaba.api.trunk;

public class TimeEarlyWarningTask {
//
//    private Timer mTimer;
//    private EarlyWarningTimerTask checkTimeTask;
//    private static final int checkPeriod = 120000; // 2分钟
//    private static final int checkDelay = 10000; // 5秒后再开始任务
//    private static final int checkInterval = 300000; //
//    private GeneralSPHelper sptool;
//    private Context mContext;
//    private List<MtqDeliTask> tasklist;
//    private static TimeEarlyWarningTask instance;// = new
//    // TimeEarlyWarningTask();
//
//    public synchronized static TimeEarlyWarningTask getInstance(Context context) {
//
//        if (instance == null)
//            instance = new TimeEarlyWarningTask(context.getApplicationContext());
//
//        return instance;
//
//    }
//
//    public TimeEarlyWarningTask(Context context) {
//        // TODO Auto-generated constructor stub
//        mContext = context;
//        isTimerFirstTime = true;
//
//    }
//
//    private boolean isTimerFirstTime = true;
//
//    public synchronized void startCheck(Context context, boolean isFirst) {
//
//        if (context != null)
//            mContext = context.getApplicationContext();
//
//        if (checkTimeTask != null && checkTimeTask.hasStarted && !isFirst)
//            return;
//
//        MLog.e("EarlyWarningTimerTask", "start");
//
//        stopTimer();
//
//        getTimer();
//
//        if (checkTimeTask == null) {
//            checkTimeTask = new EarlyWarningTimerTask();
//
//        }
//
//        if (mTimer != null && checkTimeTask != null) {
//
//            mTimer.schedule(checkTimeTask, (isTimerFirstTime ? checkDelay : 0), checkPeriod);
//            isTimerFirstTime = false;
//
//        }
//
//    }
//
//    public class EarlyWarningTimerTask extends TimerTask {
//
//        private boolean hasStarted = false;
//
//        @Override
//        public void run() {
//            this.hasStarted = true;
//            // rest of run logic here...
//
//            MLog.e("EarlyWarningTimerTask", "check");
//
//            List<MtqDeliTask> tasklist = new ArrayList<>();
//
//            if (TaskOperator.getInstance().getmCurrentTask() != null)
//                tasklist.add(TaskOperator.getInstance().getmCurrentTask());
//
//            if (TaskOperator.getInstance().getmTasklist() != null)
//                tasklist.addAll(TaskOperator.getInstance().getmTasklist());
//
//            if (tasklist != null) {
//                for (MtqDeliTask task : tasklist) {
//
//                    // MLog.e(TAG, "Check backup task" +
//                    // GsonTool.getInstance().toJson(task));
//
//                    if (task != null && task.req_times != null) {
//                        for (MtqRequestTime time : task.req_times) {
//
//                            // 检查任务生成timetask
//                            CheckTaskTimeAndCreateTimeTask(task, time);
//
//                        }
//                    }
//
//                }
//            }
//
//        }
//
//        public boolean hasRunStarted() {
//            return this.hasStarted;
//        }
//    }
//
//    private synchronized void CheckTaskTimeAndCreateTimeTask(MtqDeliTask task, MtqRequestTime time) {
//
//        if (task == null || time == null || mContext == null)
//            return;
//
//        // MLog.e("timeearlywarning", "check"+time.req_time_e * 1000+"
//        // "+System.currentTimeMillis());
//
//
//        // 任务还没到超时时间 ，距离要求送到时间还有1个小时以上
//        if ((time.req_time_e * 1000 - 3600000 - checkPeriod) <= System.currentTimeMillis()
//                && System.currentTimeMillis() <= (time.req_time_e * 1000 - 3600000 + checkPeriod)) {
//
//            MLog.e("EarlyWarningTimerTask", "setovertime36");
//
//            if (!getSPhelper().isFinishRemindOrder(task.taskid + time.cust_orderid, 14)) {
//
//                AddWarningAfterCheck(task.taskid + time.cust_orderid, 14, task.taskid, time.cust_orderid, task, time, task.corpid);
//
//            }
//
//        }
//
//        // 任务还没到超时时间 ，距离要求送到时间在1个小时以内
//        else if ((time.req_time_e * 1000) <= (System.currentTimeMillis())
//                && System.currentTimeMillis() <= (time.req_time_e * 1000 + 300000)) {
//
//            MLog.e("EarlyWarningTimerTask", "setovertime3600000");
//
//            if (!getSPhelper().isFinishRemindOrder(task.taskid + time.cust_orderid, 15)) {
//
//                AddWarningAfterCheck(task.taskid + time.cust_orderid, 15, task.taskid, time.cust_orderid, task, time, task.corpid);
//            }
//
//        } else {
//
//        }
//
//    }
//
//    public void AddWarningAfterCheck(final String declareid, final int type, final String taskId,
//                                     final String cu_orderid, final MtqDeliTask task, final MtqRequestTime time, String corpId) {
//
//        if (mContext == null)
//            return;
//        try {
//
//
////
//            MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskId);
//
//           if (detail == null || detail.orders == null || detail.getStore() == null) {
//
////                DeliveryApi.getInstance().getTaskDetailInServer(taskId, corpId, new OnResponseResult<MtqDeliTaskDetail>() {
////
////                    @Override
////                    public void OnResult(MtqDeliTaskDetail result) {
////
////
////                        if (result != null && result.getOrders() != null && result.getStore() != null) {
////                            TaskOperator.getInstance().saveTaskDetailDataToBD(result);
////                            JudgeSendWarning(result, declareid, type, taskId, cu_orderid, task, time);
////                        }
////                    }
////
////                    @Override
////                    public void OnGetTag(String Reqtag) {
////
////
////                    }
////
////                    @Override
////                    public void OnError(int ErrCode) {
////
////
////                    }
////                });
//
//            } else {
//
//               JudgeSendWarning(detail, declareid, type, taskId, cu_orderid, task, time);
//           }
//        } catch (Exception e) {
//
//       }
//    }
//
//    public synchronized GeneralSPHelper getSPhelper() {
//
//        if (sptool == null && mContext != null)
//            sptool = GeneralSPHelper.getInstance(mContext);
//
//        return sptool;
//    }
//
//    private synchronized void stopTimer() {
//        if (mTimer != null) {
//            mTimer.cancel();
//            mTimer.purge();
//            mTimer = null;
//        }
//        if (checkTimeTask != null) {
//            checkTimeTask.cancel();
//            checkTimeTask = null;
//        }
//    }
//
//    public synchronized Timer getTimer() {
//
//        if (mTimer == null)
//            mTimer = new Timer();
//
//        return mTimer;
//
//    }
//
//    /**
//     * 判断是否添加任务提示消息
//     */
//    public void JudgeSendWarning(MtqDeliTaskDetail detail, String declareid, int type, String taskId, String cu_orderid,
//                                 MtqDeliTask task, MtqRequestTime time) {
//
//        if (detail != null && detail.orders != null) {
//
//            EarlyWarningBean res = isStartTransOrFinish(detail, taskId, cu_orderid);
//
//            if (res.statu == 2) {
//                getSPhelper().FinishRemindOrder(declareid, type);
//                return;
//            }
//
//            if (res.statu == 0) {
//
//                getSPhelper().FinishRemindOrder(declareid, type);
//                MsgManager.getInstance().createRemindMsg(
//                        TimeEarlyWarningService.combineTaskInfoToContent(task, type, res.pointName));
//            }
//
//        }
//    }
//
//    /**
//     * 0 未开始 1 已开始 2 已完成
//     */
//    public EarlyWarningBean isStartTransOrFinish(MtqDeliTaskDetail detail, String taskId, String cu_orderid) {
//
//        EarlyWarningBean res = new EarlyWarningBean();
//        res.pointName = "";
//        int isStartorFinish = 2;
//
//        /**
//         * 提货送货判断单个点 提并送判断送货点 1：派送 2：提货 3：提并送
//         *
//         */
//
//
//        MtqDeliOrderDetail orderDetail = null;
//        over:
//        for (MtqDeliOrderDetail order : detail.getOrders()) {
//            if (order.orderno.contains(cu_orderid)) {
//                orderDetail = order;
//                break over;
//            }
//        }
//
//        if (orderDetail != null) {
//
//
//            Iterator<MtqDeliStoreDetail> iter = detail.store.iterator();
//            MtqDeliStoreDetail tmp;
//            traversal:
//            while (iter.hasNext()) {
//
//                tmp = iter.next();
//                if (tmp.waybill.equals(orderDetail.waybill)) {
//
//                    if (tmp.freight_type == 1 || tmp.freight_type == 2) {
//
//                        if (tmp.storestatus == 2) {
//
//                            isStartorFinish = 2;
//
//                        } else if (tmp.storestatus == 1) {
//                            isStartorFinish = 1;
//
//                        } else {
//                            isStartorFinish = 0;
//                            if (TextUtils.isEmpty(tmp.storecode) && !TextUtils.isEmpty(tmp.storename) && tmp.storeaddr.contains(tmp.storename)) {
//                                res.pointName = (tmp.regionname + tmp.storeaddr).replaceAll("\\s*", "");
//                            } else
//                                res.pointName = (TextUtils.isEmpty(tmp.storecode) ? "" : (tmp.storecode + "-")) + tmp.storename;
//
//                        }
//                        break traversal;
//                    } else {
//                        // 任务类型（1送货/3提货/4回/5必经地）
//                        if (tmp.optype == 1) {
//                            if (tmp.storestatus == 2) {
//
//                                isStartorFinish = 2;
//
//                            } else if (tmp.storestatus == 1) {
//                                isStartorFinish = 1;
//
//                            } else {
//                                isStartorFinish = 0;
//                                if (TextUtils.isEmpty(tmp.storecode) && !TextUtils.isEmpty(tmp.storename) && tmp.storeaddr.contains(tmp.storename)) {
//                                    res.pointName = (tmp.regionname + tmp.storeaddr).replaceAll("\\s*", "");
//                                } else
//                                    res.pointName = (TextUtils.isEmpty(tmp.storecode) ? "" : (tmp.storecode + "-"))
//                                            + tmp.storename;
//                            }
//                            break traversal;
//                        }
//
//                    }
//                }
//            }
//        }
//        res.statu = isStartorFinish;
//        return res;
//    }

}
