package com.yunbaba.api.trunk;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.cld.nv.map.CldMapApi;
import com.yunbaba.api.trunk.bean.GetFeedBackListResult;
import com.yunbaba.api.trunk.bean.GetTaskListResult;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.api.trunk.bean.OnResponseResultContainMsg;
import com.yunbaba.api.trunk.bean.UpdateTaskPointStatusResult;
import com.yunbaba.api.trunk.bean.UpdateTaskStatusResult;
import com.yunbaba.api.trunk.listner.OnBooleanListner;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.bean.TaskSpInfo;
import com.yunbaba.freighthelper.bean.eventbus.CloseWaitingUpdateTaskDialogEvent;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.ols.api.CldKServiceAPI;
import com.yunbaba.ols.bll.CldBllUtil;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.module.delivery.CldDalKDelivery;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldAuthInfoListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliGetTaskDetailListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliGetTaskHistoryListListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliGetTaskListListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliSearchStoreListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliTaskStatusListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IGetFeedBackListListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.AuthInfoList;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliReceiptParm;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliSearchStoreResult;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBack;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliReceiveData;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
import com.yunbaba.ols.module.delivery.tool.CldKDeviceAPI;
import com.yunbaba.ols.module.delivery.tool.CldOlsErrManager.CldOlsErrCode;
import com.yunbaba.ols.module.delivery.tool.CldSapParser;
import com.yunbaba.ols.sap.CldSapUtil;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import hmi.packages.HPDefine.HPWPoint;

/**
 * 货运服务类，提供货运接口请求 负责货运数据后台处理
 */

public class DeliveryApi implements IDeliveryApi {

    private static DeliveryApi instance = new DeliveryApi();
    private int time_ReqHis = 1;
    private Context mContext;
    public static int TASK_CANCEL = 1095;
    public static int TASKPOINT_CANCEL = 1096;
    public static int SERVER_HANDLE_ERROR = 1900;

    public DeliveryApi() {

        // 获取全局application context
        mContext = MainApplication.getContext();

    }

    public static DeliveryApi getInstance() {
        return instance;
    }

    /**
     * 从服务器获取未完成运货单
     *
     * @param 【0待运货1运货中2已完成3暂停状态4中止状态 】，用”|”分隔支持获取多个状态获取。
     * @param corpid                  指定企业ID（不指定返回所有加入企业的数据）
     * @return void
     */
    @Override
    public void getUnfinishTaskInServer(final Activity activity, String corpid, final OnResponseResult<GetTaskListResult> callBack) {

        CldKDeliveryAPI.getInstance().getDeliTaskList(corpid, new ICldDeliGetTaskListListener() {

            @Override
            public void onGetTaskLstResult(final int errCode, List<MtqDeliTask> lstOfTask) {

                if (errCode != 0) {

                    callBack.OnError(errCode);


//					if(TaskUtils.isNetWorkError(errCode)){
//						//	OffLineManager.getInstance().saveOfflineStoreStatus(point,waybill,status);
//
//
//
//					TaskOperator.getInstance().getAllTaskList(new OnObjectListner<List<MtqDeliTask>>() {
//							@Override
//							public void onResult(final List<MtqDeliTask> res) {
//							//	List<MtqDeliTask> res2 = OrmLiteApi.getInstance().queryAll(MtqDeliTask.class);
//								MLog.e("gettasklist res",(res==null?0:res.size())+"");
//
//                                activity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if(res!=null) {
//
//                                            GetTaskListResult result = new GetTaskListResult();
//                                            result.setErrCode(0);
//                                            result.setLstOfTask(res);
//                                            callBack.OnResult(result);
//                                        }else {
//                                            callBack.OnError(errCode);
//                                        }
//                                    }
//                                });
//
//
//
//
//							}
//						});
//
//					}else {
//						callBack.OnError(errCode);
//					}
                    //callBack.OnError(errCode);
                } else {

                    //callBack.OnError(errCode);




                   final GetTaskListResult result = new GetTaskListResult();
                    result.setErrCode(errCode);
                    result.setLstOfTask(lstOfTask); //      new ArrayList<MtqDeliTask>()
                    callBack.OnResult(result);
//                    new Handler(MainApplication.getContext().getMainLooper()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    },5000);


//					if (lstOfTask != null && lstOfTask.size() > 0) {
//
//						for (MtqDeliTask task : lstOfTask) {
//
//							for (MtqRequestTime time : task.req_times) {
//
//								time.taskidandorderid = task.taskid + time.cust_orderid;
//								time.taskid = task.taskid;
//								time.corpid = task.corpid;
//
//							}
//						}
//					}


                }
            }

            @Override
            public void onGetReqKey(String arg0) {

                if (callBack != null)
                    callBack.OnGetTag(arg0);
            }
        });

    }

    /**
     * 从服务器获取历史运货单
     *
     * @param status   运货状态【0待运货1运货中2已完成3暂停状态4中止状态 】，用”|”分隔支持获取多个状态获取。
     * @param corpid   指定企业ID（不指定返回所有加入企业的数据）
     * @param page     页码(默认1）
     * @param pagesize 每页条数(默认10)
     * @param
     * @return void
     */
    @Override
    public void getHisTaskInServer(String status, String corpid, String taskid, int page, int pagesize,
                                   final OnResponseResult<GetTaskListResult> callBack) {

        CldKDeliveryAPI.getInstance().getDeliTaskHistoryList(status, corpid, taskid, page, pagesize,
                new ICldDeliGetTaskHistoryListListener() {

                    @Override
                    public void onGetTaskLstResult(int errCode, List<MtqDeliTask> lstOfTask, int page, int pagecount,
                                                   int total) {


                        if (errCode != 0)
                            callBack.OnError(errCode);
                        else {

                            GetTaskListResult result = new GetTaskListResult();
                            result.setErrCode(errCode);

//							if (lstOfTask != null && lstOfTask.size() > 0) {
//
//								for (MtqDeliTask task : lstOfTask) {
//
//									for (MtqRequestTime time : task.req_times) {
//
//										time.taskidandorderid = task.taskid + time.cust_orderid;
//										time.taskid = task.taskid;
//										time.corpid = task.corpid;
//
//									}
//								}
//							}

                            result.setLstOfTask(lstOfTask);
                            result.setPage(page);
                            result.setPagecount(pagecount);
                            result.setTotal(total);



                           callBack.OnResult(result);
                        }

                    }

                    @Override
                    public void onGetReqKey(String arg0) {

                        if (callBack != null)
                            callBack.OnGetTag(arg0);
                    }

                });

    }

//
//	/**
//	 * 从服务器获取运货单，关键词taskid搜索
//	 *
//	 * @param status
//	 *            运货状态【0待运货1运货中2已完成3暂停状态4中止状态 】，用”|”分隔支持获取多个状态获取。
//	 * @param corpid
//	 *            指定企业ID（不指定返回所有加入企业的数据）
//	 * @param page
//	 *            页码(默认1）
//	 * @param pagesize
//	 *            每页条数(默认10)
//	 * @param
//	 * @return void
//	 */
//
//	public void getSearchTaskInServer(String status, String corpid,String taskid,int page, int pagesize,
//								   final OnResponseResult<GetTaskListResult> callBack) {
//
//		CldKDeliveryAPI.getInstance().getDeliTaskHistoryList(status, corpid, taskid,page, pagesize,
//				new ICldDeliGetTaskHistoryListListener() {
//
//					@Override
//					public void onGetTaskLstResult(int errCode, List<MtqDeliTask> lstOfTask, int page, int pagecount,
//												   int total) {
//
//
//						if (errCode != 0)
//							callBack.OnError(errCode);
//						else {
//
//							GetTaskListResult result = new GetTaskListResult();
//							result.setErrCode(errCode);
//							result.setLstOfTask(lstOfTask);
//							result.setPage(page);
//							result.setPagecount(pagecount);
//							result.setTotal(total);
//							callBack.OnResult(result);
//						}
//
//					}
//
//					@Override
//					public void onGetReqKey(String arg0) {
//
//						if (callBack != null)
//							callBack.OnGetTag(arg0);
//					}
//
//				});
//
//	}
//


    /**
     * 根据运货单ID从服务器取运货单详情
     *
     * @param taskId
     */

    @Override
    public void getTaskDetailInServer(final String taskId, final String corpId,
                                      final OnResponseResult<MtqDeliTaskDetail> callBack) {

        CldKDeliveryAPI.getInstance().getDeliTaskDetail(corpId, taskId, 1, 200, new ICldDeliGetTaskDetailListener() {

            @Override
            public void onGetTaskDetailResult(int errCode, MtqDeliTaskDetail taskInfo) {


                EventBus.getDefault().post(new CloseWaitingUpdateTaskDialogEvent((errCode == 0 ? true : false)));

                if (errCode != 0) {

                    if (errCode == TASK_CANCEL) {

                        // MLog.e("deletetask", "1095"+taskId);

                        ArrayList<String> deletetaskidlist = new ArrayList<>();
                        deletetaskidlist.add(taskId + "");
                        EventBus.getDefault().post(new TaskBusinessMsgEvent(3, deletetaskidlist, null));
                    } else if (errCode == SERVER_HANDLE_ERROR) {
                        ArrayList<TaskSpInfo> updatetaskidlist = new ArrayList<>();
                        updatetaskidlist.add(new TaskSpInfo(taskId, corpId, "1900"));
                        EventBus.getDefault().post(new TaskBusinessMsgEvent(2, null, updatetaskidlist));
                    }
//					if(TaskUtils.isNetWorkError(errCode)){
//						//	OffLineManager.getInstance().saveOfflineStoreStatus(point,waybill,status);
//						MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskId);
//						if(detail!=null)
//							callBack.OnResult(detail);
//						else
//							callBack.OnError(errCode);
//					}else
                    callBack.OnError(errCode);
                } else {

                    MtqDeliTaskDetail mtaskInfo = taskInfo;
                    if (mtaskInfo != null && mtaskInfo.store != null) {

                        Iterator<MtqDeliStoreDetail> iter = mtaskInfo.store.iterator();
                        MtqDeliStoreDetail tmp = null;

                        //boolean islastunfinishpointisreturnpoint = false;

                        while (iter.hasNext()) {
                            tmp = iter.next();
                            tmp.taskId = taskInfo.getTaskid();
                            tmp.corpId = taskInfo.getCorpid();
                            tmp.sendtime = taskInfo.sendtime;

//							if (tmp.storestatus != 2 && tmp.optype == 4 && (mtaskInfo.getFinishcount() == (mtaskInfo.getTotal() - 1))) {
//
//								islastunfinishpointisreturnpoint = true;
//
//							}


                            if (TaskUtils.isStorePosUnknown(tmp)) {

                                tmp.isUnknownAddress = true;

//								AddressBean bean = SPHelper2.getInstance(mContext).readMarkStoreAddress(tmp.taskId+tmp.waybill);//  LocalStoreAddress(tmp.waybill);
//
//
//								if(bean!=null)
//								{
//
//
//
//									MLog.e("find address bean", bean.kcode);
//									//HPWPoint point = CldCoordUtil.gcjToCLD(new LatLng(bean.latitude,bean.longitude));
//
//									HPWPoint point =  CldCoordUtil.kcodeToCLD(bean.kcode);
//
//
//									tmp.storex = point.x;
//									tmp.storey = point.y;
//
//									//tmp.storeaddr = bean.address;
//								}


                            } else {

                                tmp.isUnknownAddress = false;


                            }


                        }

//
//						if (islastunfinishpointisreturnpoint) {
//							SPHelper.getInstance(mContext).setIsFinalUnFinishPointIsReturnPoint(mtaskInfo.getTaskid(), 1);
//						} else {
//							SPHelper.getInstance(mContext).setIsFinalUnFinishPointIsReturnPoint(mtaskInfo.getTaskid(), -1);
//						}

                        Iterator<MtqDeliOrderDetail> iter2 = mtaskInfo.orders.iterator();
                        MtqDeliOrderDetail tmp2 = null;
                        while (iter2.hasNext()) {
                            tmp2 = iter2.next();
                            tmp2.taskId = taskInfo.getTaskid();
                            tmp2.corpId = taskInfo.getCorpid();

                        }

                    }

//					MtqDeliTaskDetail moldtaskInfo = TaskOperator.getInstance()
//							.getTaskDetailDataFromDB(mtaskInfo.getTaskid());
//
//					/**
//					 * 根据当前和旧的运单详情比较，检查是否有运单被撤回
//					 */
//					if (moldtaskInfo != null && moldtaskInfo.getOrders() != null && mtaskInfo.getOrders() != null) {
//
//						HashMap<String, String> checkmap = new HashMap<>();
//
//						for (MtqDeliOrderDetail neworder : mtaskInfo.getOrders()) {
//
//							if (neworder != null && !TextUtils.isEmpty(neworder.cust_orderid)) {
//
//								checkmap.put(neworder.cust_orderid, "1");
//
//								// ArrayList<TaskSpInfo> updatetaskidlist = new
//								// ArrayList<>();
//								// updatetaskidlist.add(new TaskSpInfo(taskId,
//								// corpId, "", cu_orderid));
//								// EventBus.getDefault().post(new
//								// TaskBusinessMsgEvent(4, null,
//								// updatetaskidlist));
//
//							}
//
//						}

                    // ArrayList<TaskSpInfo> updatetaskidlist = new
                    // ArrayList<>();
                    //
                    // for (MtqDeliOrderDetail oldorder :
                    // moldtaskInfo.getOrders()) {
                    //
                    // if (oldorder != null &&
                    // !TextUtils.isEmpty(oldorder.cust_orderid)) {
                    //
                    // if (!checkmap.containsKey(oldorder.cust_orderid)) {
                    //
                    // updatetaskidlist.add(new
                    // TaskSpInfo(moldtaskInfo.getTaskid(),
                    // moldtaskInfo.getCorpid(), "",
                    // oldorder.cust_orderid));
                    //
                    // }
                    //
                    // }
                    //
                    // }
                    //
                    // if (updatetaskidlist != null &&
                    // updatetaskidlist.size() > 0) {
                    // EventBus.getDefault().post(new
                    // TaskBusinessMsgEvent(4, null, updatetaskidlist));
                    // } else
                    // updatetaskidlist = null;

                    //}

                    callBack.OnResult(mtaskInfo);
                }
            }

            @Override
            public void onGetReqKey(String arg0) {

                if (callBack != null)
                    callBack.OnGetTag(arg0);
            }
        });
    }

    /**
     * 更新/同步任务单状态
     * <p>
     * <p>
     * 配送任务所属企业ID
     *
     * @param taskid  配送任务ID
     * @param status  修改运货单状态【0待运货1运货中2已完成3暂停状态4中止状态 】
     * @param ecorpid 需要暂停的运货单所属企业ID
     * @param etaskid 需要暂停的运货单ID
     * @param x       上报位置X
     * @param y       上报位置Y
     * @param cell    道路图幅ID
     * @param uid     道路UID
     */


    public void UpdateTaskInfo(final Activity context, final String corpid, final String taskid, final int status, final String ecorpid,
                               final String etaskid, final long x, final long y, final int cell, final int uid,
                               final OnResponseResult<UpdateTaskStatusResult> callBack) {


        final HPWPoint point = CldMapApi.getMapCenter();

//		if(point == null)
//			point = new HPWPoint();

        CldKDeliveryAPI.getInstance().updateDeliTaskStatus(corpid, taskid, status, ecorpid, etaskid, point.x, point.y, cell, uid,
                new ICldDeliTaskStatusListener() {
                    @Override
                    public void onUpdateTaskStatus(int errCode, String arg1, String arg2, int arg3) {


                        if (errCode != 0) {

                            if (errCode == TASK_CANCEL) {

                                // MLog.e("deletetask", "1095" + taskid);

                                ArrayList<String> deletetaskidlist = new ArrayList<>();
                                deletetaskidlist.add(taskid + "");

                                EventBus.getDefault().post(new TaskBusinessMsgEvent(3, deletetaskidlist, null));
                            } else if (errCode == SERVER_HANDLE_ERROR) {

                                ArrayList<TaskSpInfo> updatetaskidlist = new ArrayList<>();
                                updatetaskidlist.add(new TaskSpInfo(taskid, corpid, "1900"));
                                EventBus.getDefault().post(new TaskBusinessMsgEvent(2, null, updatetaskidlist));
                            }

                            if (TaskUtils.isNetWorkError(errCode)) {
                                if (status == 3) {

                                    OffLineManager.getInstance().pauseOfflineStoreStatus(point, 3, taskid, corpid, new OnBooleanListner() {
                                        @Override
                                        public void onResult(boolean res) {

                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    callBack.OnResult(new UpdateTaskStatusResult(0, corpid, taskid, ecorpid, etaskid, status));
                                                }
                                            });


                                        }
                                    });

                                } else
                                    callBack.OnResult(new UpdateTaskStatusResult(0, corpid, taskid, ecorpid, etaskid, status));

                            } else
                                callBack.OnError(errCode);
                        } else {
                            //
                            // MLog.d("Delivery", "setTaskStatusCallback " + s
                            // + " ret = " + j + " upStatus = " + i
                            // + " serverStatus = " + k);
                            // 用于传递消息，所赋的值要保证生成的key与上传时的一致
                            // CldDelUpTask setTask = new CldDelUpTask(
                            // DelUpTaskType.tTask, taskid, 0, 0,
                            // 0, 0, 0, upTask.getStatus());
                            // //
                            // 操作成功，或者服务器的任务状态已经是请求状态时，也认为任务状态上传成功，如果返回服务器状态为送货中，也当做成功
                            // if (errCode == 0 || upTask.getStatus() == arg3
                            // || arg3 == DelTaskStatus.tDelivering) {
                            // setTask.setUploadState(2);
                            //
                            // } else {
                            // setTask.setUploadState(0);
                            //
                            // }

                            callBack.OnResult(
                                    new UpdateTaskStatusResult(errCode, corpid, taskid, ecorpid, etaskid, status));

                        }
                    }

                    @Override
                    public void onGetReqKey(String arg0) {

                        if (callBack != null)
                            callBack.OnGetTag(arg0);
                    }
                });
    }

    /**
     * 更新运货点的状态
     *
     * @param
     */


    public void UpdateStoreStatus(Context context, String corpId, String taskId, String storeId, int status, String waybill,
                                  String ewaybill, final OnResponseResult<UpdateTaskPointStatusResult> callBack, String cu_orderid) {

        UpdateStoreStatus(context, corpId, taskId, storeId, status, waybill, ewaybill, -1, callBack, cu_orderid);
    }

    /**
     * @param corpId                                 : 企业ID
     * @param taskId                                 : 配送任务ID
     * @param storeId:                               门店ID
     * @param statuss:修改运货点状态【0未开始1进行中2已完成3暂停状态4中止状态 】
     * @param waybills:运货点号
     * @param ewaybills:需要暂停的运货点
     * @param taskStatus:要更新运货单的状态。                  -1 为不用更新 其他为要更新的状态【0未开始1进行中2已完成3暂停状态4中止状态 】 2017.4.24 后台新增参数
     * @param callBack
     * @Title: UpdateStoreStatus
     * @Description: TODO
     * @return: void
     */
    public void UpdateStoreStatus(Context context, final String corpId, final String taskId, final String storeId, final int statuss, final String waybills,
                                  final String ewaybills, int taskStatus, final OnResponseResult<UpdateTaskPointStatusResult> callBack,
                                  final String waybill2) {

        /**
         * modify by zhaoqy 2018-08-02
         * 使用CldMapApi.getNMapCenter();获取车标位置
         */
        //final HPWPoint point = CldMapApi.getMapCenter();
        final HPWPoint point = CldMapApi.getNMapCenter();

//		if(point == null)
//			point = new HPWPoint();


//		if(!NetWorkUtils.isNetworkConnected(context)){
//
//			OffLineManager.getInstance().saveOfflineStoreStatus(point,waybill,status,taskId,corpId);
//
//			return;
//
//		}


//		if(!NetWorkUtils.isNetworkConnected(context)){
//
//			OffLineManager.getInstance().saveOfflineStoreStatus(point,waybills,statuss,taskId,corpId,ewaybills);
//			callBack.OnResult(new UpdateTaskPointStatusResult(0, corpId, taskId, storeid, statuss,
//					waybills, ewaybills, data));
//
//		}


        CldKDeliveryAPI.getInstance().updateDeliTaskStoreStatus(corpId, taskId, storeId, statuss, point.x, point.y, 0, 0, waybills,
                ewaybills, taskStatus,
                new com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliTaskStoreStatusListener() {
                    @Override
                    public void onUpdateTaskStoreStatus(int errCode, String corpid, String taskid, String storeid,
                                                        int status, String waybill, String ewaybill, MtqDeliReceiveData data) {

                        if (errCode != 0) {

                            if (errCode == TASK_CANCEL) {

                                // MLog.e("deletetask", "1095"+taskId);

                                ArrayList<String> deletetaskidlist = new ArrayList<>();
                                deletetaskidlist.add(taskId + "");
                                EventBus.getDefault().post(new TaskBusinessMsgEvent(3, deletetaskidlist, null));
                            } else if (errCode == TASKPOINT_CANCEL) {

                                if (!TextUtils.isEmpty(waybill2)) {

                                    ArrayList<TaskSpInfo> updatetaskidlist = new ArrayList<>();
                                    updatetaskidlist.add(new TaskSpInfo(taskId, corpId, "", waybill2));
                                    EventBus.getDefault().post(new TaskBusinessMsgEvent(4, null, updatetaskidlist));

                                }
                            } else if (errCode == SERVER_HANDLE_ERROR) {

                                ArrayList<TaskSpInfo> updatetaskidlist = new ArrayList<>();
                                updatetaskidlist.add(new TaskSpInfo(taskId, corpId, "1900"));
                                EventBus.getDefault().post(new TaskBusinessMsgEvent(2, null, updatetaskidlist));
                            }


                            if (TaskUtils.isNetWorkError(errCode)) {
                                OffLineManager.getInstance().saveOfflineStoreStatus(point, waybills, statuss, taskId, corpId, ewaybills);
                                callBack.OnResult(new UpdateTaskPointStatusResult(0, corpId, taskId, storeid, statuss,
                                        waybills, ewaybills, data));
                            } else
                                callBack.OnError(errCode);
                        } else {

                            callBack.OnResult(new UpdateTaskPointStatusResult(errCode, corpid, taskid, storeid, status,
                                    waybill, ewaybill, data));


                            //TaskUtils.checkIsFinalUnFinishPointIsReturnPoint(taskId,waybill,ewaybill,storeId, status);

                        }

                    }

                    @Override
                    public void onGetReqKey(String arg0) {

                        if (callBack != null)
                            callBack.OnGetTag(arg0);
                    }
                });
    }


    @Override
    public void UpLoadPayInfo(Context context, final CldDeliReceiptParm uploadPara, final OnResponseResult<Integer> callBack) {



//		if(!NetWorkUtils.isNetworkConnected(context)){
//
//			callBack.OnResult(0);
//
//		}


        MLog.e("uploadparam", GsonTool.getInstance().toJson(uploadPara));
        CldKDeliveryAPI.getInstance().uploadReceipt(uploadPara, new ICldResultListener() {

            @Override
            public void onGetResult(int errCode) {

                if (errCode != 0) {

                    if (errCode == TASK_CANCEL) {

                        // MLog.e("deletetask", "1095"+taskId);

                        ArrayList<String> deletetaskidlist = new ArrayList<>();
                        deletetaskidlist.add(uploadPara.taskid + "");
                        EventBus.getDefault().post(new TaskBusinessMsgEvent(3, deletetaskidlist, null));
                    } else if (errCode == TASKPOINT_CANCEL) {

                        if (!TextUtils.isEmpty(uploadPara.waybill)) {

                            ArrayList<TaskSpInfo> updatetaskidlist = new ArrayList<>();
                            updatetaskidlist.add(
                                    new TaskSpInfo(uploadPara.taskid, uploadPara.corpid, "", uploadPara.waybill));
                            EventBus.getDefault().post(new TaskBusinessMsgEvent(4, null, updatetaskidlist));

                        }
                    } else if (errCode == SERVER_HANDLE_ERROR) {

                        ArrayList<TaskSpInfo> updatetaskidlist = new ArrayList<>();
                        updatetaskidlist.add(new TaskSpInfo(uploadPara.taskid, uploadPara.corpid, "1900"));
                        EventBus.getDefault().post(new TaskBusinessMsgEvent(2, null, updatetaskidlist));
                    }

                    if (TaskUtils.isNetWorkError(errCode)) {
                        //	OffLineManager.getInstance().saveOfflineStoreStatus(point,waybill,status);
                        callBack.OnResult(0);
                    } else
                        callBack.OnError(errCode);
                } else {
                    callBack.OnResult(errCode);
                }

            }

            @Override
            public void onGetReqKey(String tag) {

                if (callBack != null)
                    callBack.OnGetTag(tag);
            }

        });

    }

    @Override
    public void UpLoadPosition(OnResponseResult<Integer> callBack) {


    }
    //
    // @Override
    // public void UpLoadRoutePlanStatus(UploadRoutePlanParm
    // uploadRoutePlanParm, OnResponseResult<Integer> callBack) {
    //
    //
    // }

    @Override
    public void onYaWingOrRouteFinishUpLoad(int status, OnResponseResult<Integer> callBack) {


    }


    // 判断返回码是否是错误码
    public static boolean isErrorCode(int errCode) {

        if (errCode == CldOlsErrCode.DELI_NOT_SUPPORT_CITY || errCode == CldOlsErrCode.ACCOUT_NOT_LOGIN
                || errCode == CldOlsErrCode.NET_NO_CONNECTED || errCode == CldOlsErrCode.NET_TIMEOUT
                || errCode == CldOlsErrCode.NET_OTHER_ERR || errCode == CldOlsErrCode.PARSE_ERR
                || errCode == CldOlsErrCode.DATA_RETURN_ERR || errCode == CldOlsErrCode.HTTP_REJECT
                || errCode == CldOlsErrCode.METRO_NOT_SUPPORT_CITY || errCode == CldOlsErrCode.PARAM_INVALID)
            return true;
        else
            return false;

    }

    /**
     * 上传货物扫描记录
     *
     * @param corpId        企业Id
     * @param taskId        配送任务id
     * @param wayBill       配送单号
     * @param cust_order_id 客户单号
     * @param bar_code      货物条形码
     * @param upTime        扫描时间【UTC时间】
     * @param scan_x        扫描位置x
     * @param scan_y        扫描位置y
     * @param
     * @author ligangfan
     * @date 2017-3-27
     */
    @Override
    public void UpLoadGoodScanRecord(final String corpId, final String taskId, String wayBill,
                                     final String cust_order_id, String bar_code, long upTime, int scan_x, int scan_y,
                                     final OnResponseResult<Integer> callBack) {

        CldKDeliveryAPI.getInstance().uploadGoodScanRecord(corpId, taskId, wayBill, cust_order_id, bar_code, upTime,
                scan_x, scan_y, new ICldResultListener() {

                    @Override
                    public void onGetResult(int errCode) {

                        if (errCode != 0) {

                            if (errCode == TASK_CANCEL) {

                                // MLog.e("deletetask", "1095"+taskId);

                                ArrayList<String> deletetaskidlist = new ArrayList<>();
                                deletetaskidlist.add(taskId + "");
                                EventBus.getDefault().post(new TaskBusinessMsgEvent(3, deletetaskidlist, null));
                            } else if (errCode == TASKPOINT_CANCEL) {

                                if (!TextUtils.isEmpty(cust_order_id)) {

                                    ArrayList<TaskSpInfo> updatetaskidlist = new ArrayList<>();
                                    updatetaskidlist.add(new TaskSpInfo(taskId, corpId, "", cust_order_id));
                                    EventBus.getDefault().post(new TaskBusinessMsgEvent(4, null, updatetaskidlist));

                                }
                            } else if (errCode == SERVER_HANDLE_ERROR) {

                                ArrayList<TaskSpInfo> updatetaskidlist = new ArrayList<>();
                                updatetaskidlist.add(new TaskSpInfo(taskId, corpId, "1900"));
                                EventBus.getDefault().post(new TaskBusinessMsgEvent(2, null, updatetaskidlist));
                            }

                            callBack.OnError(errCode);
                        } else {
                            callBack.OnResult(errCode);
                        }
                    }

                    @Override
                    public void onGetReqKey(String tag) {

                        if (callBack != null)
                            callBack.OnGetTag(tag);
                    }
                });

    }

    /**
     * 上传货物照片或者电子回单图片
     *
     * @param corpId        企业Id
     * @param taskId        配送任务Id
     * @param wayBill       配送单号
     * @param cust_order_id 客户单号
     * @param upTime        上传时间【UTC时间】
     * @param pic_type      照片类型（1：货物照片【默认】，2：电子回单照片）
     * @param pic_time      拍照时间【UTC时间】
     * @param pic_x         拍照时凯立德坐标x
     * @param pic_y         拍照时凯立德坐标y
     * @param base64_pic    货物图片，base64字符串
     * @param
     * @author ligangfan
     * @date 2017-3-27
     */
    @Override
    public void UpLoadDeliPicture(final String corpId, final String taskId, final String wayBill,
                                  final String cust_order_id, long upTime, int pic_type, long pic_time, int pic_x, int pic_y,
                                  String base64_pic, final OnResponseResult<Integer> callBack) {



        CldKDeliveryAPI.getInstance().uploadDeliPicture(corpId, taskId, wayBill, cust_order_id, upTime, pic_type,
                pic_time, pic_x, pic_y, base64_pic, new ICldResultListener() {

                    @Override
                    public void onGetResult(int errCode) {

                        if (errCode != 0) {

                            if (errCode == TASK_CANCEL) {

                                // MLog.e("deletetask", "1095"+taskId);

                                ArrayList<String> deletetaskidlist = new ArrayList<>();
                                deletetaskidlist.add(taskId + "");
                                EventBus.getDefault().post(new TaskBusinessMsgEvent(3, deletetaskidlist, null));
                            } else if (errCode == TASKPOINT_CANCEL) {

                                if (!TextUtils.isEmpty(cust_order_id)) {

                                    ArrayList<TaskSpInfo> updatetaskidlist = new ArrayList<>();
                                    updatetaskidlist.add(new TaskSpInfo(taskId, corpId, "", cust_order_id));
                                    EventBus.getDefault().post(new TaskBusinessMsgEvent(4, null, updatetaskidlist));

                                }
                            } else if (errCode == SERVER_HANDLE_ERROR) {

                                ArrayList<TaskSpInfo> updatetaskidlist = new ArrayList<>();
                                updatetaskidlist.add(new TaskSpInfo(taskId, corpId, "1900"));
                                EventBus.getDefault().post(new TaskBusinessMsgEvent(2, null, updatetaskidlist));
                            }


                            callBack.OnError(errCode);
                        } else {
                            callBack.OnResult(errCode);
                        }
                    }

                    @Override
                    public void onGetReqKey(String tag) {

                        if (callBack != null)
                            callBack.OnGetTag(tag);
                    }
                });
    }

    /**
     * 是否有已完成的任务
     */
    public synchronized boolean isHasFinishTask(final String corpid, Context context,
                                                final OnResponseResult<Boolean> callBack) {

        final SPHelper sptool = SPHelper.getInstance(context);

        if (sptool.isHasFinishTask(corpid))
            return true;
        else {

            // 本地判断没有已完成任务，从服务器拉取列表查看是否有已完成的

            // CldHttpClient.cancelRequest(tag);

            CldKDeliveryAPI.getInstance().getDeliTaskHistoryList("2", corpid, "", 1, 2,
                    new ICldDeliGetTaskHistoryListListener() {

                        @Override
                        public void onGetTaskLstResult(int errCode, List<MtqDeliTask> lstOfTask, int page,
                                                       int pagecount, int total) {


                            if (errCode != 0) {
                                if (callBack != null)
                                    callBack.OnError(errCode);
                            } else {

                                // GetTaskListResult result = new
                                // GetTaskListResult();
                                // result.setErrCode(errCode);
                                // result.setLstOfTask(lstOfTask);
                                // result.setPage(page);
                                // result.setPagecount(pagecount);
                                // result.setTotal(total);
                                // callBack.OnResult(result);

                                if (lstOfTask != null && lstOfTask.size() > 0) {
                                    sptool.setIsHasFinishTask(corpid, true);
                                    if (callBack != null)
                                        callBack.OnResult(true);
                                } else if (callBack != null) {
                                    sptool.setIsHasFinishTask(corpid, false);
                                    callBack.OnResult(false);

                                }

                            }

                        }

                        @Override
                        public void onGetReqKey(String arg0) {

                            if (callBack != null)
                                callBack.OnGetTag(arg0);
                        }

                    });
            return false;
        }

    }

    /**
     * 搜索门店请求
     */
    public void SearchStore(String corpid, String keyword, int type, int storetype, int page, int pageSize,
                            int iscenter, final OnResponseResultContainMsg<CldDeliSearchStoreResult> callBack) {

        CldKDeliveryAPI.getInstance().searchStores(corpid, keyword, type, page, pageSize, iscenter,
                new ICldDeliSearchStoreListener() {

                    @Override
                    public void onGetResult(int errCode, CldDeliSearchStoreResult result, String errMsg) {

                        if (errCode != 0) {
                            if (callBack != null)
                                callBack.OnError(errCode, errMsg);
                        } else {
                            callBack.OnResult(result);
                        }
                    }

                    @Override
                    public void onGetReqKey(String arg0) {

                        callBack.OnGetTag(arg0);
                    }
                });
    }


    /**
     * 3.2.10.5获取我的标注记录
     *
     * @param status    运货状态【0待运货1运货中2已完成3暂停状态4中止状态 】，用”|”分隔支持获取多个状态获取。
     * @param corpid    指定企业ID（不指定返回所有加入企业的数据）
     * @param iscenter  Integer	标注类型（3-客户地址，1-配送中心，0-门店；默认为0）
     * @param starttime Integer	标注开始时间（UTC时间戳）秒
     * @param endtime   Integer	标注结束时间（UTC时间戳）秒
     * @param status    Integer	审核状态（0为待审核，1为审核通过，2为审核不通过，-1为不限制）
     * @param page      Integer	当前页(从1开始，默认为1)
     * @param pageSize  Integer	每页显示数量(默认20，最大100)
     * @param keyword   String	门店编码或者门店名称（模糊查询）

     * @return void

     */
    public static void GetMyMarkStoreRecord(final String corpid, final String keyword, final int iscenter, final int starttime, final int endtime, final int status, final int page,
                                     final int pageSize, final OnResponseResultContainMsg<CldSapKDeliveryParam.CldDeliGetMyMarkStoreResult> callBack) {

        CldKDeliveryAPI.getInstance().getMyMarkStoreList(corpid, keyword, iscenter, starttime, endtime, status, page, pageSize, new CldKDeliveryAPI.ICldGetMyMarkStoreListListener() {
            @Override
            public void onGetResult(int errCode, CldSapKDeliveryParam.CldDeliGetMyMarkStoreResult result, String errMsg) {

                if (errCode != 0) {
                    if (callBack != null)
                        callBack.OnError(errCode, errMsg);
                } else {
                    callBack.OnResult(result);
                }
            }

            @Override
            public void onGetReqKey(String arg0) {
                callBack.OnGetTag(arg0);
            }


        });


    }


    /**
     * 获取未标注的门店列表
     */
    public void GetNoMarkStoreList(String corpid, int page, int pageSize, int iscenter,
                                   final OnResponseResultContainMsg<CldDeliSearchStoreResult> callBack) {

        CldKDeliveryAPI.getInstance().searchNoPosStores(corpid, null, page, pageSize, iscenter,
                new ICldDeliSearchStoreListener() {

                    @Override
                    public void onGetResult(int errCode, CldDeliSearchStoreResult result, String errMsg) {

                        if (errCode != 0) { // isErrorCode(errCode)
                            if (callBack != null)
                                callBack.OnError(errCode, errMsg);
                        } else {
                            callBack.OnResult(result);
                        }
                    }

                    @Override
                    public void onGetReqKey(String arg0) {

                        callBack.OnGetTag(arg0);
                    }
                });
    }

    // /**
    // * 获取未标注的门店列表
    // */
    // public void GetMarkStoreList(String corpid, int storetype, int page, int
    // pageSize,
    // final OnResponseResult<CldDeliSearchStoreResult> callBack) {
    //
    // CldKDeliveryAPI.getInstance().searchNoPosStores(corpid, null, page,
    // pageSize, new ICldDeliSearchStoreListener() {
    //
    // @Override
    // public void onGetResult(int errCode, CldDeliSearchStoreResult result) {
    //
    // if (errCode != 0) { //isErrorCode(errCode)
    // if (callBack != null)
    // callBack.OnError(errCode);
    // } else {
    // callBack.OnResult(result);
    // }
    // }
    //
    // @Override
    // public void onGetReqKey(String arg0) {
    //
    // callBack.OnGetTag(arg0);
    // }
    // });
    // }


    /**
     * 从服务器获取反馈历史
     */

    public void getFeedBackListInServer(String taskid, String corpid, String waybill, int page, int pagesize,
                                        final OnResponseResult<GetFeedBackListResult> callBack) {


        CldBllKDelivery.getInstance().getFeedBackList(corpid, taskid, waybill, page, pagesize, new IGetFeedBackListListener() {

            @Override
            public void onGetResult(int errCode, List<FeedBack> result, int count) {

                if (errCode != 0)
                    callBack.OnError(errCode);
                else {

                    GetFeedBackListResult res = new GetFeedBackListResult();
                    res.count = count;
                    res.listOfRst = result;

                    callBack.OnResult(res);
                }
            }

            @Override
            public void onGetReqKey(String arg0) {

                if (callBack != null)
                    callBack.OnGetTag(arg0);
            }
        });


    }


    /**
     * 是否有反馈历史
     */
    public synchronized boolean isHasFeedBack(final String corpid, final String taskid, final String waybill, Context context,
                                              final OnResponseResult<Boolean> callBack) {

        final SPHelper sptool = SPHelper.getInstance(context);

        if (sptool.isHasFeedBack(corpid, taskid, waybill))
            return true;
        else {

            // 本地判断没有已完成任务，从服务器拉取列表查看是否有已完成的

            // CldHttpClient.cancelRequest(tag);

            CldBllKDelivery.getInstance().getFeedBackList(corpid, taskid, waybill, 1, 1, new IGetFeedBackListListener() {

                @Override
                public void onGetResult(int errCode, List<FeedBack> lstOfTask, int count) {

                    if (errCode != 0)
                        callBack.OnError(errCode);
                    else {

                        if (lstOfTask != null && lstOfTask.size() > 0) {
                            sptool.setIsHasFeedBack(corpid, taskid, waybill, true);
                            if (callBack != null)
                                callBack.OnResult(true);
                        } else if (callBack != null) {
                            sptool.setIsHasFeedBack(corpid, taskid, waybill, false);
                            callBack.OnResult(false);

                        }
                    }
                }

                @Override
                public void onGetReqKey(String arg0) {

                    if (callBack != null)
                        callBack.OnGetTag(arg0);
                }
            });
            return false;
        }

    }


    public void RefreshStoreAuth() {

        CldKDeliveryAPI.getInstance().getAuthInfoList(new ICldAuthInfoListener() {

            @Override
            public void onGetResult(int errCode, List<AuthInfoList> lstOfResult) {



                if (errCode != 0) {
                    // Toast.makeText(ContactActivity.this, "获取门店权限失败",
                    // Toast.LENGTH_LONG).show();
                    return;
                }

                // lstOfAuth = lstOfResult;
                if (MainApplication.getContext() != null)
                    SPHelper.getInstance(MainApplication.getContext()).writeStoreAuth(lstOfResult);
            }

            @Override
            public void onGetReqKey(String arg0) {


            }
        });

    }

    /**
     * type: 0 新增 1 修改 2 读取
     */
    public boolean isHasAuthForStore(Context context, String corpid, int type) {

        List<AuthInfoList> list = SPHelper.getInstance(context).readStoreAuth();

        boolean isHasAuth = false;

        over:
        for (AuthInfoList tmp : list) {
            if (tmp.corpid.equals(corpid)) {

                switch (type) {
                    case 0:

                        if (tmp.isadd == 1)
                            isHasAuth = true;

                        break;
                    case 1:
                        if (tmp.ismod == 1)
                            isHasAuth = true;
                        break;
                    case 2:
                        if (tmp.isread == 1)
                            isHasAuth = true;
                        break;
                    default:
                        break;
                }

                break over;
            }
        }

        return isHasAuth;

    }

    public static String getSpeechFileUrl(String corpid, String speechid) {

        long time = CldKDeviceAPI.getSvrTime();// System.currentTimeMillis() /
        // 1000;

        Map<String, Object> map = new HashMap<String, Object>();

        long kuid = CldKServiceAPI.getInstance().getKuid();
        String Session = CldKServiceAPI.getInstance().getSession();
        long business = CldBllUtil.getInstance().getBussinessid();
        long appid = CldBllUtil.getInstance().getAppid();
        long apptype = CldKServiceAPI.getInstance().getApptype();
        long duid = CldKServiceAPI.getInstance().getDuid();

        map.put("userid", kuid);
        map.put("session", Session);
        map.put("business", business);
        map.put("appid", appid);
        map.put("apptype", apptype);
        map.put("duid", duid);

        map.put("corpid", corpid);
        map.put("audioid", speechid);
        map.put("audiotype", 1);
        map.put("uptime", time);

        String outMd5Source = CldSapParser.formatSource(map);
        outMd5Source += CldDalKDelivery.getInstance().getCldDeliveryKey();

        String moutMd5Sources = com.yunbaba.ols.module.delivery.tool.CldPubFuction.MD5Util.MD5(outMd5Source);

        map.put("sign", moutMd5Sources);

        // "http://192.168.200.213/khyapi/hyapi"

        return CldSapUtil.getNaviSvrHY() + "tis/v1/delivery_get_radio.php?" + "userid=" + kuid + "&session=" + Session
                + "&business=" + business + "&appid=" + appid + "&apptype=" + apptype + "&duid=" + duid + "&corpid="
                + corpid + "&audioid=" + speechid + "&audiotype=" + 1 + "&uptime=" + time + "&sign=" + moutMd5Sources;

    }

    public static String getThumbpicUrl(String corpid, String taskid, String picid) {

        long time = CldKDeviceAPI.getSvrTime();// System.currentTimeMillis() /
        // 1000;

        Map<String, Object> map = new HashMap<String, Object>();

        long kuid = CldKServiceAPI.getInstance().getKuid();
        String Session = CldKServiceAPI.getInstance().getSession();
        long business = CldBllUtil.getInstance().getBussinessid();
        long appid = CldBllUtil.getInstance().getAppid();
        long apptype = CldKServiceAPI.getInstance().getApptype();
        long duid = CldKServiceAPI.getInstance().getDuid();

        map.put("userid", kuid);
        map.put("session", Session);
        map.put("business", business);
        map.put("appid", appid);
        map.put("apptype", apptype);
        map.put("duid", duid);

        map.put("corpid", corpid);
        map.put("taskid", taskid);
        map.put("picid", picid);
        map.put("uptime", time);

        String outMd5Source = CldSapParser.formatSource(map);
        outMd5Source += CldDalKDelivery.getInstance().getCldDeliveryKey();

        String moutMd5Sources = com.yunbaba.ols.module.delivery.tool.CldPubFuction.MD5Util.MD5(outMd5Source);

        map.put("sign", moutMd5Sources);

        // http://192.168.200.213/khyapi/hyapi/

        return CldSapUtil.getNaviSvrHY() + "/tis/v1/delivery_get_thumb.php?" + "userid=" + kuid + "&session=" + Session
                + "&business=" + business + "&appid=" + appid + "&apptype=" + apptype + "&duid=" + duid + "&corpid="
                + corpid + "&taskid=" + taskid + "&picid=" + picid + "&uptime=" + time + "&sign=" + moutMd5Sources;

    }

    public static String getSourcepicUrl(String corpid, String taskid, String picid) {

        long time = CldKDeviceAPI.getSvrTime();// System.currentTimeMillis() /
        // 1000;

        Map<String, Object> map = new HashMap<String, Object>();

        long kuid = CldKServiceAPI.getInstance().getKuid();
        String Session = CldKServiceAPI.getInstance().getSession();
        long business = CldBllUtil.getInstance().getBussinessid();
        long appid = CldBllUtil.getInstance().getAppid();
        long apptype = CldKServiceAPI.getInstance().getApptype();
        long duid = CldKServiceAPI.getInstance().getDuid();

        map.put("userid", kuid);
        map.put("session", Session);
        map.put("business", business);
        map.put("appid", appid);
        map.put("apptype", apptype);
        map.put("duid", duid);

        map.put("corpid", corpid);
        map.put("taskid", taskid);
        map.put("picid", picid);
        map.put("uptime", time);

        String outMd5Source = CldSapParser.formatSource(map);
        outMd5Source += CldDalKDelivery.getInstance().getCldDeliveryKey();

        MLog.e("key", "" + CldDalKDelivery.getInstance().getCldDeliveryKey());
        String moutMd5Sources = com.yunbaba.ols.module.delivery.tool.CldPubFuction.MD5Util.MD5(outMd5Source);

        map.put("sign", moutMd5Sources);

        // "http://192.168.200.213/khyapi/hyapi"

        return CldSapUtil.getNaviSvrHY() + "tis/v1/delivery_get_pic.php?" + "userid=" + kuid + "&session=" + Session
                + "&business=" + business + "&appid=" + appid + "&apptype=" + apptype + "&duid=" + duid + "&corpid="
                + corpid + "&taskid=" + taskid + "&picid=" + picid + "&uptime=" + time + "&sign=" + moutMd5Sources;

    }


    public static String getThumbpicUrlByFileLink(String fileurl) {

        long time = CldKDeviceAPI.getSvrTime();// System.currentTimeMillis() /
        // 1000;

        Map<String, Object> map = new HashMap<String, Object>();

        long kuid = CldKServiceAPI.getInstance().getKuid();
        String Session = CldKServiceAPI.getInstance().getSession();
        long business = CldBllUtil.getInstance().getBussinessid();
        long appid = CldBllUtil.getInstance().getAppid();
        long apptype = CldKServiceAPI.getInstance().getApptype();
        long duid = CldKServiceAPI.getInstance().getDuid();

        map.put("userid", kuid);
        map.put("session", Session);
        map.put("business", business);
        map.put("appid", appid);
        map.put("apptype", apptype);
        map.put("duid", duid);


        map.put("fileurl", fileurl);
        map.put("uptime", time);

        String outMd5Source = CldSapParser.formatSource(map);
        outMd5Source += CldDalKDelivery.getInstance().getCldDeliveryKey();

        String moutMd5Sources = com.yunbaba.ols.module.delivery.tool.CldPubFuction.MD5Util.MD5(outMd5Source);

        map.put("sign", moutMd5Sources);

        // http://192.168.200.213/khyapi/hyapi/

        return CldSapUtil.getNaviSvrHY() + "tis/v1/get_attach_thumb.php?" + "userid=" + kuid + "&session=" + Session
                + "&business=" + business + "&appid=" + appid + "&apptype=" + apptype + "&duid=" + duid + "&fileurl=" +
                fileurl + "&uptime=" + time + "&sign=" + moutMd5Sources;

    }


//	public void batchReportTaskstoreStatus() {
//
//		String corpid = "44424246";
//		String taskid = "180400002511";
//
//		List<TaskStore> list = new ArrayList<TaskStore>();
//
//		// TaskStore0
//		TaskStore TaskStore0 = new TaskStore();
//
//		TStoreStatusStart start0 = new TStoreStatusStart();
//		start0.uptime = 1524467111;
//		start0.x = 410817466;
//		start0.y = 81362749;
//		start0.cell = 0;
//		start0.uid = 0;
//
//		TStoreStatusStart finish0 = new TStoreStatusStart();
//		finish0.uptime = 1524467118;
//		finish0.x = 410817466;
//		finish0.y = 81362749;
//		finish0.cell = 0;
//		finish0.uid = 0;
//
//		TaskStore0.waybill = "180400040203";
//		TaskStore0.start = start0;
//		TaskStore0.finish = finish0;
//		list.add(TaskStore0);
//
//		// TaskStore1
//		TaskStore TaskStore1 = new TaskStore();
//
//		TStoreStatusStart start1 = new TStoreStatusStart();
//		start1.uptime = 1524467128;
//		start1.x = 410817466;
//		start1.y = 81362749;
//		start1.cell = 0;
//		start1.uid = 0;
//
//		TStoreStatusStart finish1 = new TStoreStatusStart();
//		finish1.uptime = 1524467132;
//		finish1.x = 410817466;
//		finish1.y = 81362749;
//		finish1.cell = 0;
//		finish1.uid = 0;
//
//		TaskStore1.waybill = "180400040202";
//		TaskStore1.start = start1;
//		TaskStore1.finish = finish1;
//		list.add(TaskStore1);
//
//		CldKDeliveryAPI.getInstance().batchReportTaskstoreStatus(corpid, taskid, list,
//				new CldKDeliveryAPI.IBatchReportTaskstoreStatusListener() {
//
//                    @Override
//                    public void onGetResult(int errCode, List<CldSapKDeliveryParam.StoreStatusResult> lstOfResult) {
//
//                    }
//
//                    @Override
//                    public void onGetReqKey(String arg0) {
//
//                    }
//                } );
//	}

}
