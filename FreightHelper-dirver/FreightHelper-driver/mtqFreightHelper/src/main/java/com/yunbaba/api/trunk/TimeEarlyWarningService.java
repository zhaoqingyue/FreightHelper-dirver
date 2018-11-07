package com.yunbaba.api.trunk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * 运货单预警服务
 */
public class TimeEarlyWarningService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    /**
//     * 本地要保存一份数据 用来保证不会重复提醒
//     * <p>
//     * 用sp 保存 是否该任务的该提醒已经提醒过了
//     */
//    private String TAG = "TimeEarlyWarningService";
//    private Timer mTimer;
//    // private ArrayList<TimeWarningTask> mTaskList;
//    private GeneralSPHelper sptool;
   public static final String ACTION_WARNING = "com.mtq.freighthelper.TASK_WARNING";
//    AlarmManager am;
//
//    private SPHelper idsptool;
//
////    WakeLock mWakeLock;// 电源锁
////	TimeWarningTask wtask;
//
//    @Override
//    public IBinder onBind(Intent arg0) {
//
//        return null;
//    }
//
//    @Override
//    public void onStart(Intent intent, int startId) {
//
//        super.onStart(intent, startId);
//
//    }
//
//    @Override
//    public void onCreate() {
//
//        super.onCreate();
//
//        MLog.e("timeearlywarning", "oncreate");
//       // acquireWakeLock();
//
//    }
//
//    @Override
//    public void onDestroy() {
//
//        super.onDestroy();
//
//        MLog.e("timeearlywarning", "ondestory");
//       // releaseWakeLock();
//
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//
//        // int val = super.onStartCommand(intent, flags, startId);
//
//        // START_REDELIVER_INTENT
//        if (intent != null && intent.hasExtra("rechecktasklist") && intent.getBooleanExtra("rechecktasklist", false)) {
//
//            // rechecklist
//            MLog.e(TAG, "Check list but only new task");
//            CheckFullTaskListAndSendMsg(true);
//        } else {
//
//            MLog.e(TAG, "Check list to all task");
//            CheckFullTaskListAndSendMsg(false);
//        }
//
//        //flags = Service.START_NOT_STICKY;
//        return super.onStartCommand(intent, flags, startId);
//
//    }
//
//    public synchronized void CheckFullTaskListAndSendMsg(boolean isJustCheckNesTask) {
//
//        List<MtqDeliTask> tasklist = TaskOperator.getInstance().getAllTaskList();
//
//        if (isJustCheckNesTask) {
//
//            // 如果没有新任务，则返回不进行遍历
//
//            ArrayList<MtqDeliTask> backuplist = getIdSPhelper(this).GetBackUpLastTimeTaskList();
//
//            // MLog.e(TAG, "Check backup task" + backuplist.size() + " " +
//            // tasklist.size());// +
//            // backuplist.size()+GsonTool.getInstance().toJson(backuplist));
//
//            tasklist.removeAll(backuplist);
//
//            if (tasklist.size() == 0) {
//                MLog.e(TAG, "no new task");
//                return;
//            }
//
//            // MLog.e(TAG, "Check new task" + tasklist.size() +
//            // GsonTool.getInstance().toJson(tasklist));
//
//            getTimer();
//            // if(mTaskList == null)
//            // mTaskList = new ArrayList<>();
//
//        } else {
//
//            if (mTimer != null) {
//
//                mTimer.cancel();
//                mTimer.purge();
//                mTimer = null;
//
//                // if (mTaskList != null)
//                // mTaskList.clear();
//            }
//
//            getTimer();
//            // mTaskList = new ArrayList<>();
//
//        }
//
//        for (MtqDeliTask task : tasklist) {
//
//            // MLog.e(TAG, "Check backup task" +
//            // GsonTool.getInstance().toJson(task));
//
//            if (task != null && task.req_times != null) {
//                for (MtqRequestTime time : task.req_times) {
//
//                    // 检查任务生成timetask
//                    CheckTaskTimeAndCreateTimeTask(task, time);
//
//                }
//            }
//
//        }
//
//    }
//
//    private synchronized void CheckTaskTimeAndCreateTimeTask(MtqDeliTask task, MtqRequestTime time) {
//
//        if (task == null || time == null)
//            return;
//
//        // MLog.e("timeearlywarning", "check"+time.req_time_e * 1000+"
//        // "+System.currentTimeMillis());
//        //
//        //
//        // // 任务还没到超时时间 ，距离要求送到时间还有1个小时以上
//        // if ((time.req_time_e * 1000) >= (System.currentTimeMillis() +
//        // 3600000)) {
//        //
//        // MLog.e("timeearlywarning", "setovertime36");
//        //
//        // wtask = new TimeWarningTask(combineTaskInfoToContent(task, time, 14),
//        // time.cust_orderid, task.taskid,
//        // time.cust_orderid, 14, GsonTool.getInstance().toJson(task),
//        // GsonTool.getInstance().toJson(time));
//        //
//        // // AddTask(wtask);
//        //
//        // mTimer.schedule(wtask, new Date(time.req_time_e * 1000 - 3600000));
//        // }
//        //
//        // // 任务还没到超时时间 ，距离要求送到时间在1个小时以内
//        // else if ((time.req_time_e * 1000) >= (System.currentTimeMillis())
//        // && (time.req_time_e * 1000) < (System.currentTimeMillis() + 3600000))
//        // {
//        //
//        // MLog.e("timeearlywarning", "setovertime3600000");
//        //
//        // if (!getSPhelper().isFinishRemindOrder(time.cust_orderid, 15)) {
//        // wtask = new TimeWarningTask(combineTaskInfoToContent(task, time, 15),
//        // time.cust_orderid, task.taskid,
//        // time.cust_orderid, 15, GsonTool.getInstance().toJson(task),
//        // GsonTool.getInstance().toJson(time));
//        //
//        // // AddTask(wtask);
//        //
//        // mTimer.schedule(wtask, new Date(time.req_time_e * 1000));
//        // }
//        //
//        // }
//        //
//        // // 任务超时
//        // else
//
//        if ((time.req_time_e * 1000) < System.currentTimeMillis()) {
//
//            long zeropoint = TimestampTool.getZeroPointTimeStamp((time.req_time_e * 1000));
//
//            if (TimestampTool.getTodayZeroTimeStamp() > zeropoint) {
//
//                String date = TimestampTool.getDayDate(System.currentTimeMillis());
//
//                if (!getSPhelper().isFinishRemindOrder(task.taskid + time.cust_orderid + date, 16)) {
//                    // wtask = new
//                    // TimeWarningTask(combineTaskInfoToContent(task, 16),
//                    // time.cust_orderid + date,
//                    // task.taskid, time.cust_orderid, 16,
//                    // GsonTool.getInstance().toJson(task),
//                    // GsonTool.getInstance().toJson(time));
//
//                    // AddTask(wtask);
//                    AddWarningAfterCheck(task.taskid + time.cust_orderid + date, 16, task.taskid, time.cust_orderid, task, time,
//                            task.corpid);
//
//                }
//
//            } else {
//
//                if (!getSPhelper().isFinishRemindOrder(task.taskid + time.cust_orderid, 15)) {
//                    // wtask = new
//                    // TimeWarningTask(combineTaskInfoToContent(task, 15),
//                    // time.cust_orderid,
//                    // task.taskid, time.cust_orderid, 15,
//                    // GsonTool.getInstance().toJson(task),
//                    // GsonTool.getInstance().toJson(time));
//
//                    // AddTask(wtask);
//
//                    AddWarningAfterCheck(task.taskid + time.cust_orderid, 15, task.taskid, time.cust_orderid, task, time,
//                            task.corpid);
//
//                }
//
//            }
//
//        }
//
//    }
//
//    public void AddWarningAfterCheck(final String declareid, final int type, final String taskId,
//                                     final String cu_orderid, final MtqDeliTask task, final MtqRequestTime time, String corpId) {
//        try {
//
//            MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskId);
//
//            if (detail == null || detail.getOrders() == null || detail.getStore() == null) {
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
//                JudgeSendWarning(detail, declareid, type, taskId, cu_orderid, task, time);
//            }
//
//        } catch (Exception e) {
//        }
//
//    }
//
//    public synchronized void RefreshSingleTaskAndAddNewWarning(String taskStr, String requesttime) {
//
//        MLog.e(TAG, "RefreshSingleTaskAndAddNewWarning");
//
//        MtqDeliTask task = GsonTool.getInstance().fromJson(taskStr, MtqDeliTask.class);
//        MtqRequestTime time = GsonTool.getInstance().fromJson(requesttime, MtqRequestTime.class);
//
//        if (task != null && time != null && task.taskid != null && time.cust_orderid != null) {
//            MLog.e(TAG, "RefreshSingleTaskAndAddNewWarning success");
//            CheckTaskTimeAndCreateTimeTask(task, time);
//
//        }
//
//    }
//
//    /**
//     * 1099#配送任务ID#物流企业#企业ID#消息内容#筛选ID 筛选ID: FILTER_ID_14 = 14; 运单超时报警
//     * FILTER_ID_15 = 15; 运单超时提醒 FILTER_ID_16 = 16; 运单过期报警 FILTER_ID_17 = 17;
//     * 运单过期提醒
//     */
//    public static String combineTaskInfoToContent(MtqDeliTask task, int type, String pointName) {
//
//        // task.taskid
//        // 实际用的还是客户id
//        return "1099#" + MSGTYPE_ORIGINAL + "#" + task.corpname + "#" + task.corpid + "#" + "【" + pointName + "】" + getTypeContent(type)
//                + "#" + type;
//
//    }
//
   public static final String MSGTYPE_ORIGINAL = "original";
//
//    /**
//     * 1099#配送任务ID#物流企业#企业ID#消息内容#筛选ID 筛选ID: FILTER_ID_14 = 14; 运单超时报警
//     * FILTER_ID_15 = 15; 运单超时提醒 FILTER_ID_16 = 16; 运单过期报警 FILTER_ID_17 = 17;
//     * 运单过期提醒
//     */
//    public static String combineTaskInfoToContent(MtqDeliTaskDetail task, int type, String pointName) {
//
//        // task.taskid
//        // 实际用的还是客户id
//        return "1099#" + "original" + "#" + task.getCorpname() + "#" + task.getCorpid() + "#" + "【" + pointName + "】"
//                + getTypeContent(type) + "#" + type;
//
//    }
//
////	private class TimeWarningTask extends TimerTask {// public abstract class
////														// TimerTask implements
////														// Runnable{}
////
////		// private Date time;
////		private String WarningContent;
////
////		// 用来保持消息在sp里key的唯一性 一般用cu_orderid 过期的用cu_orderid+时间
////		private String declareid;
////		private int type;
////		private String taskId;
////		private String cu_orderid;
////		private String taskStr;
////		private String requesttime;
////
////		public TimeWarningTask(String WarningContent, String declareid, String taskid, String cu_orderid, int type,
////				String taskStr, String requesttime) {
////			// TODO Auto-generated constructor stub
////			this.WarningContent = WarningContent;
////			this.declareid = declareid;
////			this.type = type;
////			this.taskId = taskid;
////			this.cu_orderid = cu_orderid;
////			this.taskStr = taskStr;
////			this.requesttime = requesttime;
////		}
////
////		@Override
////		public void run() {
////
////			if (getSPhelper().isFinishRemindOrder(declareid, type))
////				return;
////
////			else {
////				// 首先判断任务是否完成，若完成则不提醒
////
////				boolean isFinish = true;
////
////				MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskId);
////
////				if (detail != null) {
////
////					if (type == 16) {
////
////						Iterator<MtqDeliOrderDetail> iter = detail.orders.iterator();
////						MtqDeliOrderDetail tmp;
////						traversal: while (iter.hasNext()) {
////							// 运货中
////							tmp = iter.next();
////							if (tmp.cust_orderid.equals(cu_orderid)) {
////
////								if (tmp.expired == 0) {
////
////									// 任务未完成
////									isFinish = false;
////									break traversal;
////								}
////							}
////						}
////
////					} else {
////
////						Iterator<MtqDeliStoreDetail> iter = detail.store.iterator();
////						MtqDeliStoreDetail tmp;
////						traversal: while (iter.hasNext()) {
////							// 运货中
////							tmp = iter.next();
////							if (tmp.cust_orderid.equals(cu_orderid) && tmp.taskId.equals(taskId)) {
////
////								if (tmp.storestatus != 2) {
////
////									// 任务未完成
////									isFinish = false;
////									break traversal;
////								}
////							}
////						}
////
////					}
////				} else {
////
////					boolean isExist = TaskOperator.getInstance().isExistTask(taskId);
////
////					if (isExist)
////						isFinish = false;
////					else
////						isFinish = true;
////
////				}
////
////				if (isFinish) {
////					getSPhelper().FinishRemindOrder(declareid, type);
////					return;
////				}
////
////				getSPhelper().FinishRemindOrder(declareid, type);
////				MsgManager.getInstance().createRemindMsg(WarningContent);
////
////				// if (type < 16)
////				// RefreshSingleTaskAndAddNewWarning(taskStr, requesttime);
////
////			}
////
////		}
////	}
//
//    public synchronized GeneralSPHelper getSPhelper() {
//
//        if (sptool == null)
//            sptool = GeneralSPHelper.getInstance(this);
//
//        return sptool;
//    }
//
//    public synchronized SPHelper getIdSPhelper(Context context) {
//
//        if (idsptool == null)
//            idsptool = SPHelper.getInstance(context);
//
//        return idsptool;
//    }
//
//    // public void AddTask(TimeWarningTask task) {
//    //
//    // if (mTaskList != null)
//    // mTaskList.add(task);
//    //
//    // }
//
//    public synchronized Timer getTimer() {
//
//        if (mTimer == null)
//            mTimer = new Timer(true);
//
//        return mTimer;
//
//    }
//
//    public void ProduceBroadcastIntent(TaskWarningInfo bean) {
//
//        getAlarmManger();
//
//        Intent intent = new Intent(ACTION_WARNING);
//        intent.putExtra("taskwarninginfo", GsonTool.getInstance().toJson(bean));
//
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
//
//        am.set(AlarmManager.RTC_WAKEUP, bean.getWarningTime(), pi);
//
//        // .setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),5*1000,pi);
//
//    }
//
//    public AlarmManager getAlarmManger() {
//
//        if (am == null)
//            am = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//        return am;
//
//    }
//
////    /**
////     * onCreate时,申请设备电源锁
////     */
////    private void acquireWakeLock() {
////        if (null == mWakeLock) {
////            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
////            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "myService");
////            if (null != mWakeLock) {
////                mWakeLock.acquire();
////            }
////        }
////    }
////
////    /**
////     * onDestroy时，释放设备电源锁
////     */
////    private void releaseWakeLock() {
////
////        if (null != mWakeLock) {
////            mWakeLock.release();
////            mWakeLock = null;
////        }
////    }
//
//    public static String getTypeContent(int type) {
//
//        String res;
//
//        switch (type) {
//            case 14:
//                res = "距离要求送达时间只剩一个小时，请尽快配送。"; // "运货单 " + cuorderid +
//                break;
//            case 15:
//                res = "已超时，请尽快配送。";
//                break;
//            case 16:
//                res = "一天后即将过期，过期后的运货单不能再配送，请尽快配送。";
//                break;
//            case 17:
//                res = "已过期，有需要请联系调度处理。";
//                break;
//            default:
//                res = "距离要求送达时间只剩一个小时，请尽快配送。";
//                break;
//        }
//
//        return res;
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
//            if (type != 16) {
//
//                if (res.statu == 0) {
//
//                    getSPhelper().FinishRemindOrder(declareid, type);
//                    MsgManager.getInstance().createRemindMsg(
//                            TimeEarlyWarningService.combineTaskInfoToContent(task, type, res.pointName));
//                }
//            } else {
//
//                if (res.statu == 0 || res.statu == 1) {
//
//                    getSPhelper().FinishRemindOrder(declareid, type);
//                    MsgManager.getInstance().createRemindMsg(
//                            TimeEarlyWarningService.combineTaskInfoToContent(task, type, res.pointName));
//                }
//
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
//                            if (TextUtils.isEmpty(tmp.storecode) && !TextUtils.isEmpty(tmp.storename) && tmp.storeaddr.contains(tmp.storename)) {
//                                res.pointName = (tmp.regionname + tmp.storeaddr).replaceAll("\\s*", "");
//                            } else
//                                res.pointName = (TextUtils.isEmpty(tmp.storecode) ? "" : (tmp.storecode + "-")) + tmp.storename;
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
//                                if (TextUtils.isEmpty(tmp.storecode) && !TextUtils.isEmpty(tmp.storename) && tmp.storeaddr.contains(tmp.storename)) {
//                                    res.pointName = (tmp.regionname + tmp.storeaddr).replaceAll("\\s*", "");
//                                } else
//                                    res.pointName = (TextUtils.isEmpty(tmp.storecode) ? "" : (tmp.storecode + "-"))
//                                            + tmp.storename;
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
//
//        }
//        res.statu = isStartorFinish;
//        return res;
//    }

}
