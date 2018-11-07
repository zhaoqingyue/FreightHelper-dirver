/*
 * @Title CldKDeliveryTrcApi.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-12-9 上午9:53:08
 * @version 1.0
 */
package com.yunbaba.ols.module.delivery;

import android.text.TextUtils;
import android.util.Log;

import com.cld.db.utils.CldDbUtils;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.api.CldKServiceAPI;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.AuthInfoList;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldAuthTimeOut;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliCorpLimit;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliCorpLimitMapParm;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliCorpLimitRouteParm;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliCorpRoutePlanResult;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliCorpWarning;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliGroup;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliReceiptParm;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliSearchStoreResult;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliTaskDB;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliUploadStoreParm;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldElectfence;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldMonitorAuth;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldReUploadEFParm;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldUploadEFParm;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.Examinationdetail;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBack;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBackInfo;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCar;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCarCheckHistory;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCarRoute;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqExaminationUnit;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTask;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTaskDetail;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.StoreStatusResult;
import com.yunbaba.ols.module.delivery.bean.MtqDeliReceiveData;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
import com.yunbaba.ols.module.delivery.bean.TaskStore;
import com.yunbaba.ols.tools.err.CldOlsErrManager.CldOlsErrCode;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 货运接口模块
 *
 * @author Zhouls
 * @date 2015-12-9 上午9:53:08
 */
public class CldKDeliveryAPI {
    /**
     * 货运初始化回调
     *
     * @author Zhouls
     * @date 2016-5-7 下午5:10:25
     */
    public static interface ICldDeliInitListener {
        /**
         * 登录鉴权结果
         *
         * @param errCode
         * @return void
         * @author Zhouls
         * @date 2016-5-10 下午5:43:21
         */
        public void onLoginAuth(int errCode);

        /**
         * 是否显示相应的入口
         *
         * @param isDisplayTask
         * @param isDisplayStore
         * @return void
         * @author Zhouls
         * @date 2016-5-9 下午6:00:13
         */
        public void onDisplayEnterResult(boolean isDisplayTask, boolean isDisplayStore);

        /**
         * 更新未读货运单条数回调
         *
         * @return void
         * @author Zhouls
         * @date 2016-5-7 下午5:12:02
         */
        public void onUpdateUnreadTaskCount();

        /**
         * 更新未完成运货单条数
         *
         * @return void
         * @author Zhouls
         * @date 2016-5-25 下午5:57:38
         */
        public void onUpdateUnfinishTaskCount();

    }

    /**
     * 货运初始化（在登录或者退出登录调用）
     *
     * @return void
     * @author Zhouls
     * @date 2015-12-10 下午6:36:29
     */
    public void init(final ICldDeliInitListener listener) {
        authListener = listener;
        CldBllKDelivery.getInstance().init(listener);
    }

    /**
     * 初始化授权信息列表,从服务端获取授权列表（异步）
     *
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2015-12-9 上午11:29:48
     */
    public void initMonitorAuthList(final ICldDeliveryMonitorListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().getMonitorAuthList(listener);
            }
        });
    }

    /**
     * 新增授权信息
     *
     * @param mobile   手机号
     * @param mark     标注
     * @param timeOut  有效期
     * @param listener 回调
     * @return void
     * @author Zhouls
     * @date 2015-12-9 上午11:27:18
     */
    public void addMonitorAuth(final String mobile, final String mark, final CldAuthTimeOut timeOut,
                               final ICldResultListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().addMonitorAuth(mobile, mark, timeOut, listener);
            }
        });
    }

    /**
     * 删除授权信息
     *
     * @param id       标识id
     * @param listener 回调
     * @return void
     * @author Zhouls
     * @date 2015-12-9 上午11:28:03
     */
    public void delMonitorAuth(final String id, final ICldResultListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().delMonitorAuth(id, listener);
            }
        });
    }

    /**
     * 获取路径上的限行数据
     *
     * @param parm
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-14 下午4:44:38
     */
    public void getRouteCorpLimitData(final CldDeliCorpLimitRouteParm parm,
                                      final ICldDeliGetCorpLimitListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetCorpLimitResult(errCode, null, -1, null, -1);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().getRouteCorpLimitData(parm, listener);
            }
        });
    }

    /**
     * 获取图面限行数据
     *
     * @param parm
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-14 下午4:44:25
     */
    public void getMapCorpLimitData(final CldDeliCorpLimitMapParm parm, final ICldDeliGetCorpLimitListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetCorpLimitResult(errCode, null, -1, null, -1);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().getMapCorpLimitData(parm, listener);
            }
        });
    }

    /**
     * 获取运货单详情
     *
     * @param corpid   企业ID
     * @param taskid   运货单ID
     * @param page     运货点页码
     * @param pagesize 运货点页容量
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-4-28 下午3:24:53
     */
    public void getDeliTaskDetail(final String corpid, final String taskid, final int page, final int pagesize,
                                  final ICldDeliGetTaskDetailListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetTaskDetailResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().getDeliTaskDetail(corpid, taskid, page, pagesize,
                        new ICldDeliGetTaskDetailListener() {

                            @Override
                            public void onGetTaskDetailResult(int errCode, MtqDeliTaskDetail taskInfo) {

                                CldDalKDelivery.getInstance().saveTaskSatus(corpid, taskid);
                                if (null != listener) {
                                    listener.onGetTaskDetailResult(errCode, taskInfo);
                                }
                            }

                            @Override
                            public void onGetReqKey(String arg0) {

                                listener.onGetReqKey(arg0);
                            }
                        });
            }
        });
    }

    /**
     * 获取当前登录用户运货单列表
     *
     * @param status   运货状态【0待运货1运货中2已完成3暂停状态4中止状态 】，用”|”分隔支持获取多个状态获取。
     * @param corpid   指定企业ID（不指定返回所有加入企业的数据）
     * @param taskid   Integer	配送任务ID（模糊检索）
     * @param page     页码(默认1）
     * @param pagesize 每页条数(默认10)
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-4-28 下午3:27:47
     */
    public void getDeliTaskHistoryList(final String status, final String corpid, final String taskid, final int page, final int pagesize,
                                       final ICldDeliGetTaskHistoryListListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetTaskLstResult(errCode, null, -1, -1, -1);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().getDeliTaskHistoryList(status, corpid, taskid, page, pagesize, listener);
            }
        });
    }


    /**
     * 3.2.10.5获取我的标注记录
     *
     * @param status    运货状态【0待运货1运货中2已完成3暂停状态4中止状态 】，用”|”分隔支持获取多个状态获取。
     * @param corpid    指定企业ID（不指定返回所有加入企业的数据）
     * @param iscenter  Integer	标注类型（3-客户地址，1-配送中心，0-门店；默认为0）
     * @param starttime Integer	标注开始时间（UTC时间戳）
     * @param endtime   Integer	标注结束时间（UTC时间戳）
     * @param status    Integer	审核状态（0为待审核，1为审核通过，2为审核不通过，-1为不限制）
     * @param page      Integer	当前页(从1开始，默认为1)
     * @param pageSize  Integer	每页显示数量(默认20，最大100)
     * @param keyword   String	门店编码或者门店名称（模糊查询）
     * @param listener
     * @return void

     */
    public void getMyMarkStoreList(final String corpid, final String keyword, final int iscenter, final int starttime, final int endtime, final int status, final int page,
                                     final int pageSize, final ICldGetMyMarkStoreListListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode, null, "鉴权失败");
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().getMyMarkStoreList(corpid, keyword, iscenter, starttime, endtime,status,page, pageSize,  listener);
            }
        });
    }


    /**
     * 获取用户的运货单列表
     *
     * @param corpid   指定企业ID（不指定传null,返回所有加入企业的数据）
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-4-28 下午3:28:45
     */
    public void getDeliTaskList(final String corpid, final ICldDeliGetTaskListListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetTaskLstResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().getDeliTaskList(corpid, listener);
            }
        });
    }

    /**
     * 获取未读运货单条数
     *
     * @return int
     * @author Zhouls
     * @date 2016-5-7 下午4:58:16
     */
    public int getUnreadTaskCount() {
        if (!CldKAccountAPI.getInstance().isLogined() || !CldDalKDelivery.getInstance().isLoginAuth()) {
            return 0;
        }
        int webCount = CldDalKDelivery.getInstance().getUnReadTaskCount();
        long kuid = CldKServiceAPI.getInstance().getKuid();
        List<CldDeliTaskDB> lstOfLoc = CldDalKDelivery.getInstance().getLocTaskReadCach(kuid);
        if (null == lstOfLoc || lstOfLoc.size() <= 0) {
            return webCount;
        }
        return CldDalKDelivery.getInstance().getLocUnreadTaskCount(kuid);
    }

    /**
     * 获取本地运货单条数
     *
     * @return int
     * @author Zhouls
     * @date 2016-5-31 下午2:51:47
     */
    public int getLocTaskCount() {
        if (!CldKAccountAPI.getInstance().isLogined() || !CldDalKDelivery.getInstance().isLoginAuth()) {
            return 0;
        }
        long kuid = CldKServiceAPI.getInstance().getKuid();
        List<CldDeliTaskDB> lstOfLoc = CldDalKDelivery.getInstance().getLocTaskReadCach(kuid);
        return lstOfLoc.size();
    }

    /**
     * 获取授权门店企业列表
     *
     * @return List<CldDeliGroup>
     * @author Zhouls
     * @date 2016-5-7 下午4:59:25
     */
    public List<CldDeliGroup> getAuthStoreCorpList() {
        if (!CldKAccountAPI.getInstance().isLogined() || !CldDalKDelivery.getInstance().isLoginAuth()) {
            return new ArrayList<CldDeliGroup>();
        }
        return CldDalKDelivery.getInstance().getLstOfAuthCorps();
    }

    /**
     * 获取授权列表（从货运模块获取授权列表）
     *
     * @return List<CldMonitorAuth>
     * @author Zhouls
     * @date 2015-12-10 下午6:59:13
     */
    public List<CldMonitorAuth> getMonitorAuthList() {
        return CldDalKDelivery.getInstance().getMonitorAuthLst();
    }

    private ICldDeliInitListener authListener;

    /**
     * 同意加入车队
     *
     * @param inviteCode 邀请码（由12位数字组成）
     * @param listener
     * @return void (1301错误码 新加入的企业为独享或者已加入了独享企业，根据lockid判断是哪种情况)
     * @author Zhouls
     * @date 2016-4-28 下午3:20:25
     */
    public void joinGroup(final String inviteCode, final ICldResultListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().joinGroup(inviteCode, listener);
            }
        });
    }

    /**
     * 修改备注
     *
     * @param id       标识id
     * @param mark     标注
     * @param listener 回调
     * @return void
     * @author Zhouls
     * @date 2015-12-9 上午11:28:43
     */
    public void reviseMonitorAuthMark(final String id, final String mark, final ICldResultListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().reviseMonitorAuthMark(id, mark, listener);
            }
        });
    }

    /**
     * 修改授权有效期
     *
     * @param id       标识id
     * @param timeOut  有效期
     * @param listener 回调
     * @return void
     * @author Zhouls
     * @date 2015-12-9 上午11:28:25
     */
    public void reviseMonitorAuthTimeOut(final String id, final CldAuthTimeOut timeOut,
                                         final ICldResultListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().reviseMonitorAuthTimeOut(id, timeOut, listener);
            }
        });
    }

    /**
     * 获取周边门店
     *
     * @param corpid   企业ID
     * @param centerX  当前位置X
     * @param centerY  当前位置Y
     * @param range    搜索范围半径单位(米，最大20KM)
     * @param page     当前页(从1开始，默认为1)
     * @param pageSize 每页显示数量(默认20，最大100)
     * @param listener 回调
     * @return void
     * @author Zhouls
     * @date 2016-4-22 下午6:13:28
     */
    public void searchNearStores(final String corpid, final int centerX, final int centerY, final int range,
                                 final int page, final int pageSize, final ICldDeliSearchStoreListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode, null, "鉴权失败");
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().searchNearStores(corpid, centerX, centerY, range, page, pageSize,
                        listener);
            }
        });
    }

    /**
     * 搜索未标注门店
     *
     * @param corpid   指定企业ID
     * @param page     当前页(从1开始，默认为1)
     * @param pageSize 每页显示数量(默认20，最大100)
     * @param listener 回调
     * @return void
     * @author Zhouls
     * @date 2016-4-22 下午6:14:29
     */
    public void searchNoPosStores(final String corpid, final String regioncity, final int page, final int pageSize,
                                  final int iscenter, final ICldDeliSearchStoreListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode, null, "鉴权失败");
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().searchNoPosStores(corpid, regioncity, page, pageSize, iscenter, listener);
            }
        });
    }

    /**
     * 搜索门店
     *
     * @param corpid   指定企业ID
     * @param keyword  搜索词
     * @param type     (1有位置门店搜索,0无位置待审核)
     * @param page     当前页(从1开始，默认为1)
     * @param pageSize 每页显示数量(默认20，最大100)
     * @param listener 回调
     * @return void
     * @author Zhouls
     * @date 2016-4-22 下午6:12:58
     */
    public void searchStores(final String corpid, final String keyword, final int type, final int page,
                             final int pageSize, final int iscenter, final ICldDeliSearchStoreListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode, null, "鉴权失败");
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().searchStores(corpid, keyword, type, page, pageSize, iscenter, listener);
            }
        });
    }

    /**
     * 搜索门店
     *
     * @param page     当前页(从1开始，默认为1)
     * @param pageSize 每页显示数量(默认20，最大100)
     * @param listener 回调
     */
    public void getCarCheckHistoryCarList(final int page,
                                          final int pageSize, final ICarCheckHistoryListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode, -1, null);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().getCarCheckHistoryCar(page, pageSize, listener);
            }
        });
    }

    /**
     * 拒绝加入车队
     *
     * @param inviteCode 邀请码（由12位数字组成）
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-4-28 下午3:21:22
     */
    public void unJoinGroup(final String inviteCode, final ICldResultListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().unJoinGroup(inviteCode, listener);
            }
        });
    }

    /**
     * 更新运货单状态
     *
     * @param corpid   配送任务所属企业ID
     * @param taskid   配送任务ID
     * @param status   修改运货单状态【0待运货1运货中2已完成3暂停状态4中止状态 】
     * @param ecorpid  需要暂停的运货单所属企业ID
     * @param etaskid  需要暂停的运货单ID
     * @param x        上报位置X
     * @param y        上报位置Y
     * @param cell     道路图幅ID
     * @param uid      道路UID
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-14 下午4:41:55
     */
    public void updateDeliTaskStatus(final String corpid, final String taskid, final int status, final String ecorpid,
                                     final String etaskid, final long x, final long y, final int cell, final int uid,
                                     final ICldDeliTaskStatusListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onUpdateTaskStatus(errCode, null, null, -1);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().updateDeliTaskStatus(corpid, taskid, status, ecorpid, etaskid, x, y, cell,
                        uid, new ICldDeliTaskStatusListener() {

                            @Override
                            public void onUpdateTaskStatus(int errCode, String corpid, String taskid, int status) {

                                CldDalKDelivery.getInstance().saveTaskSatus(corpid, taskid);
                                if (null != listener) {
                                    listener.onUpdateTaskStatus(errCode, ecorpid, etaskid, status);
                                }
                            }

                            @Override
                            public void onGetReqKey(String arg0) {

                                listener.onGetReqKey(arg0);
                            }
                        });
            }
        });
    }

    /**
     * 更新运货点状态
     *
     * @param corpid   企业ID
     * @param taskid   配送任务ID
     * @param storeid  门店ID
     * @param status   修改运货点状态【0未开始1进行中2已完成3暂停状态4中止状态 】
     * @param x        上报位置X
     * @param y        上报位置Y
     * @param cell     道路图幅ID
     * @param uid      道路UID
     * @param waybill  运货点号
     * @param ewaybill 需要暂停的运货点
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-10 上午11:14:57
     */
    public void updateDeliTaskStoreStatus(final String corpid, final String taskid, final String storeid,
                                          final int status, final long x, final long y, final int cell, final int uid, final String waybill,
                                          final String ewaybill, final ICldDeliTaskStoreStatusListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onUpdateTaskStoreStatus(errCode, null, null, null, -1, null, null, null);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().updateDeliTaskStoreStatus(corpid, taskid, storeid, status, x, y, cell,
                        uid, waybill, ewaybill, -1, listener);
            }
        });
    }

    /**
     * 更新运货点状态
     *
     * @param corpid     企业ID
     * @param taskid     配送任务ID
     * @param storeid    门店ID
     * @param status     修改运货点状态【0未开始1进行中2已完成3暂停状态4中止状态 】
     * @param x          上报位置X
     * @param y          上报位置Y
     * @param cell       道路图幅ID
     * @param uid        道路UID
     * @param waybill    运货点号
     * @param ewaybill   需要暂停的运货点
     * @param taskStatus 运货单请求更改的状态
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-10 上午11:14:57
     */
    public void updateDeliTaskStoreStatus(final String corpid, final String taskid, final String storeid,
                                          final int status, final long x, final long y, final int cell, final int uid, final String waybill,
                                          final String ewaybill, final int taskStatus, final ICldDeliTaskStoreStatusListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onUpdateTaskStoreStatus(errCode, null, null, null, -1, null, null, null);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().updateDeliTaskStoreStatus(corpid, taskid, storeid, status, x, y, cell,
                        uid, waybill, ewaybill, taskStatus, listener);
            }
        });
    }

    /**
     * 上传货物照片或者电子回单图片
     */
    public void uploadDeliPicture(final String corpId, final String taskId, final String wayBill,
                                  final String cust_order_id, final long upTime, final int pic_type, final long pic_time, final int pic_x,
                                  final int pic_y, final String base64_pic, final ICldResultListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {


                Log.e("uploadpic_kdeli", "faildeal");

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                Log.e("uploadpic_kdeli", "todeal");


                CldBllKDelivery.getInstance().uploadDeliPicture(corpId, taskId, wayBill, cust_order_id, upTime,
                        pic_type, pic_time, pic_x, pic_y, base64_pic, listener);
            }
        });

    }


    /**
     * 上传照片
     */
    public void uploadPicture(final String corpId, final long pic_time, final int pic_x,
                              final int pic_y, final String base64_pic, final IUploadPicListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {


                Log.e("uploadpic_kdeli", "faildeal");

                if (null != listener) {
                    listener.onGetResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {

                Log.e("uploadpic_kdeli", "todeal");


                CldBllKDelivery.getInstance().uploadPicture(corpId, pic_time, pic_x, pic_y, base64_pic, listener);
            }
        });

    }


    /**
     * 上传货物扫描记录
     */

    public void uploadGoodScanRecord(final String corpId, final String taskId, final String wayBill,
                                     final String cust_order_id, final String bar_code, final long upTime, final int scan_x, final int scan_y,
                                     final ICldResultListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().uploadGoodScanRecord(corpId, taskId, wayBill, cust_order_id, bar_code,
                        upTime, scan_x, scan_y, listener);
            }
        });

    }

    /**
     * 上报收款信息
     *
     * @param param
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-4-22 下午6:10:26
     */
    public void uploadReceipt(final CldDeliReceiptParm param, final ICldResultListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().uploadReceipt(param, listener);
            }
        });
    }

    /**
     * 标注门店
     *
     * @param param
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-4-22 下午6:15:14
     */
    public void uploadStore(final CldDeliUploadStoreParm param, final ICldUploadStoreListListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode,"未鉴权");
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().uploadStore(param, listener);
            }
        });
    }

    /**
     * 获取我的车队列表
     *
     * @return List<CldDeliGroup>
     * @author Zhouls
     * @date 2016-4-29 上午9:27:42
     */
    public List<CldDeliGroup> getMyGroups() {
        if (!CldKAccountAPI.getInstance().isLogined()) {
            return new ArrayList<CldDeliGroup>();
        } else {
            return CldDalKDelivery.getInstance().getLstOfMyGroups();
        }
    }

    public void deleteCorp(String cropid) {
        CldDalKDelivery.getInstance().deleteCorp(cropid);
    }

    public CldDeliGroup getCldDeliGroup(String cropid) {
        return CldDalKDelivery.getInstance().getCldDeliGroup(cropid);
    }

    /**
     * 获取锁定的企业ID
     */
    public int getLockcorpid() {
        if (!CldKAccountAPI.getInstance().isLogined()) {
            return -1;
        } else {
            return CldDalKDelivery.getInstance().getLockcorpid();
        }
    }

    /**
     * 获取企业云端路线(同步接口需外部开启线程)
     *
     * @param isroute      是否使用企业线路参与规划[0-不使用(默认值)、1-使用]
     * @param corpid       企业ID（不使用企业线路时传0，否则传配送任务所属企业ID）
     * @param taskid       配送任务ID（无任务传0）
     * @param routeid      推荐线路ID（使用了推荐线路，重算时传入，否则传0）
     * @param naviid       使用推荐线路导航ID（使用了推荐线路，在同一次导航里面，重算时传入，否则传0）
     * @param islimit      企业的限行数据是否参与规划[0-不用(默认值)、1-使用](当不存在推荐线路的时候有用)
     * @param routePlanStr 6部接口拼接参数
     * @return CldDeliCorpRoutePlanResult
     * @author Zhouls
     * @date 2016-5-11 上午10:09:50
     */
    public CldDeliCorpRoutePlanResult requestCorpRoutePlan(int isroute, String corpid, String taskid, int routeid,
                                                           int naviid, int islimit, String routePlanStr) {
        if (!CldDalKDelivery.getInstance().isLoginAuth())
            return null;
        return CldBllKDelivery.getInstance().requestCorpRoutePlan(isroute, corpid, taskid, routeid, naviid, islimit,
                routePlanStr);
    }

    /**
     * 请求刷新未读货运单条数
     *
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-13 上午11:55:49
     */
    public void requestTaskUnreadCount(ICldResultListener listener) {
        CldBllKDelivery.getInstance().requestUnreadTaskCount(listener);
    }

    /**
     * 加入车队后刷新企业门店权限列表
     *
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-6-1 上午11:41:42
     */
    public void requestAuthStoreList(ICldResultListener listener) {
        CldBllKDelivery.getInstance().requestAuthStoreList(listener);
    }

    public static interface ICarListener {

        /**
         * 获取司机驾驶车辆信息回调
         *
         * @param errCode
         * @param result
         */
        public void onGetResult(int errCode, MtqCar result);

        public void onGetReqKey(String arg0);
    }

    /**
     * 获取司机驾驶车辆信息
     *
     * @param taskId   配送任务ID
     * @param corpId   物流公司ID
     * @param listener
     * @author zhaoqy
     */
    public void getCarInfo(final String taskId, final String corpId, final ICarListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {
                if (listener != null) {
                    listener.onGetResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {
                CldBllKDelivery.getInstance().getCarInfo(taskId, corpId, listener);
            }
        });
    }

    public static interface ITaskListener {

        /**
         * 行程日期列表回调
         *
         * @param errCode
         * @param listOfResult
         */
        public void onGetResult(int errCode, List<MtqTask> listOfResult);

        public void onGetReqKey(String arg0);
    }

    /**
     * 获取行程日期列表
     *
     * @param starttime 检索开始时间（时间戳）
     * @param endtime   检索结束时间
     * @param listener
     * @author zhaoqy
     */
    public void getTasks(final String starttime, final String endtime, final ITaskListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {
                if (listener != null) {
                    listener.onGetResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {
                CldBllKDelivery.getInstance().getTasks(starttime, endtime, listener);
            }
        });
    }

    public static interface ICarRouteListener {

        /**
         * 单天行程列表回调
         *
         * @param errCode
         */
        public void onGetResult(int errCode, List<MtqCarRoute> listOfResult);

        public void onGetReqKey(String arg0);
    }


    public static interface ICarCheckHistoryListener {

        /**
         * 体检历史车辆列表回调
         *
         * @param errCode
         */
        public void onGetResult(int errCode, int total, List<MtqCarCheckHistory> listOfResult);

        public void onGetReqKey(String arg0);
    }

    /**
     * 获取单天行程列表
     *
     * @param date     检索日期（格式：YYYY-MM-DD）
     * @param listener
     * @author zhaoqy
     */
    public void getCarRoutes(final String date, final List<MtqTask> tasks, final ICarRouteListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {
                if (listener != null) {
                    listener.onGetResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {
                CldBllKDelivery.getInstance().getCarRoutes(date, tasks, listener);
            }
        });
    }

    public static interface ITaskDetailListener {

        /**
         * 单天行程列表回调
         *
         * @param errCode
         * @param result
         */
        public void onGetResult(int errCode, MtqTaskDetail result);

        public void onGetReqKey(String arg0);
    }

    /**
     * 获取行程详情
     *
     * @param carduid  车辆设备ID
     * @param serialid 行程记录ID
     * @param listener
     * @author zhaoqy
     */
    public void getTaskDetail(final String carduid, final String serialid, final ITaskDetailListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {
                if (listener != null) {
                    listener.onGetResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {
                CldBllKDelivery.getInstance().getTaskDetail(carduid, serialid, listener);
            }
        });
    }

    public static interface IExaminationHistoryListener {

        /**
         * 车辆体检历史列表回调
         *
         * @param errCode
         * @param result
         */
        public void onGetResult(int errCode, List<MtqExaminationUnit> result);

        public void onGetReqKey(String arg0);
    }

    /**
     * 获取车辆体检历史列表
     *
     * @param carduid:                      车辆设备ID
     * @param pagesize:每页条数（取值范围1-200，默认10）
     * @param pageindex:页码（从1开始计算）
     * @param listener
     */
    public void getExaminationHistory(final String carduid, final int pagesize, final int pageindex,
                                      final IExaminationHistoryListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {
                if (listener != null) {
                    listener.onGetResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {
                CldBllKDelivery.getInstance().getCarExamineHistoryList(carduid, pagesize, pageindex, listener);
            }
        });
    }


    public static interface IExaminationDetailListener {
        /**
         * 车辆体检历史列表回调
         *
         * @param errCode
         * @param result
         */

        public void onGetResult(int errCode, List<Examinationdetail> result);

        public void onGetReqKey(String arg0);
    }


    public static interface IGetFeedBackInfoListener {
        /**
         * 3.2.7.11获取企业反馈配置信息列表
         *
         * @param errCode
         * @param result
         */

        public void onGetResult(int errCode, List<FeedBackInfo> result);

        public void onGetReqKey(String arg0);
    }


//	public static interface ICommitFeedBackListener {
//		/**
//		 * 3.2.7.12提交反馈信息
//		 * 
//		 * @param errCode
//		 * @param result
//		 */
//		dsf
//		public void onGetResult(int errCode, List<Examinationdetail> result);
//
//		public void onGetReqKey(String arg0);
//	}
//	

    public static interface IGetFeedBackListListener {
        /**
         * 3.2.7.13获取我的反馈
         *
         * @param errCode
         * @param result
         */
        public void onGetResult(int errCode, List<FeedBack> result, int count);

        public void onGetReqKey(String arg0);
    }


    /**
     * 2.2.9.7	获取车辆体检明细
     *
     * @param carduid 车辆设备ID
     * @param examid  体检ID（可传0）
     * @param time    体检时间（可传0）
     * @return
     */
    public void getExaminationDetail(final String carduid, final String examid, final long time,
                                     final IExaminationDetailListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {
                if (listener != null) {
                    listener.onGetResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {
                CldBllKDelivery.getInstance().getCarExamineDetail(carduid, examid, time, listener);
            }
        });
    }

    /**
     * 企业限行回调
     *
     * @author Zhouls
     * @date 2016-4-28 下午3:00:11
     */
    public static interface ICldDeliGetCorpLimitListener {
        /**
         * 获取企业限行回调
         *
         * @param errCode
         * @param lstOfLimit   限行列表
         * @param limitCount   限行总数
         * @param lstOfWarning 警示列表
         * @param warningCount 警示总数
         * @return void
         * @author Zhouls
         * @date 2016-4-28 下午3:00:31
         */
        public void onGetCorpLimitResult(int errCode, List<CldDeliCorpLimit> lstOfLimit, int limitCount,
                                         List<CldDeliCorpWarning> lstOfWarning, int warningCount);

        public void onGetReqKey(String arg0);
    }

    /**
     * 获取运货单详情回调
     *
     * @author Zhouls
     * @date 2016-4-27 下午6:49:31
     */
    public static interface ICldDeliGetTaskDetailListener {
        public void onGetTaskDetailResult(int errCode, MtqDeliTaskDetail taskInfo);

        public void onGetReqKey(String arg0);
    }

    /**
     * 获取运货单列表回调
     *
     * @author Zhouls
     * @date 2016-4-27 下午5:18:28
     */
    public static interface ICldDeliGetTaskListListener {
        public void onGetTaskLstResult(int errCode, List<MtqDeliTask> lstOfTask);

        public void onGetReqKey(String arg0);
    }

    /**
     * 获取货运单历史列表回调
     *
     * @author Zhouls
     * @date 2016-4-29 上午9:48:21
     */
    public static interface ICldDeliGetTaskHistoryListListener {
        public void onGetTaskLstResult(int errCode, List<MtqDeliTask> lstOfTask, int page, int pagecount, int total);

        public void onGetReqKey(String arg0);
    }

    /**
     * 搜索门店回调
     *
     * @author Zhouls
     * @date 2016-4-22 下午5:10:15
     */
    public interface ICldDeliSearchStoreListener {
        /**
         * 搜索门店回调
         *
         * @param errCode
         * @param result
         * @return void
         * @author Zhouls
         * @date 2016-4-22 下午5:09:36
         */
        public void onGetResult(int errCode, CldDeliSearchStoreResult result, String errMsg);

        public void onGetReqKey(String arg0);
    }



    public interface ICldGetMyMarkStoreListListener{
        /**
         * 搜索门店回调
         *
         * @param errCode
         * @param result
         * @return void
         * @author Zhouls
         * @date 2016-4-22 下午5:09:36
         */
        public void onGetResult(int errCode, CldSapKDeliveryParam.CldDeliGetMyMarkStoreResult result, String errMsg);

        public void onGetReqKey(String arg0);
    }


    public interface ICldUploadStoreListListener{
        /**
         * 搜索门店回调
         *
         * @param errCode

         * @return void
         * @author Zhouls
         * @date 2016-4-22 下午5:09:36
         */
        public void onGetResult(int errCode, String errMsg);

        public void onGetReqKey(String arg0);
    }


    /**
     * 货运单状态回调
     *
     * @author Zhouls
     * @date 2016-4-27 下午6:49:46
     */
    public static interface ICldDeliTaskStatusListener {
        public void onUpdateTaskStatus(int errCode, String corpid, String taskid, int status);

        public void onGetReqKey(String arg0);
    }

    /**
     * 运货点状态回调
     *
     * @author Zhouls
     * @date 2016-4-27 下午6:50:20
     */
    public static interface ICldDeliTaskStoreStatusListener {
        public void onUpdateTaskStoreStatus(int errCode, String corpid, String taskid, String storeid, int status,
                                            String waybill, String ewaybill, MtqDeliReceiveData data);

        public void onGetReqKey(String arg0);
    }


    /**
     * 上传照片回调
     */
    public static interface IUploadPicListener {

        public void onGetResult(int errCode, String mediaid);

        public void onGetReqKey(String arg0);
    }

    /**
     * 获取授权信息回调
     *
     * @author Zhouls
     * @date 2015-12-9 上午10:29:53
     */
    public static interface ICldDeliveryMonitorListener {
        /**
         * 获取授权列表回调
         *
         * @param errCode     错误码（errCode=0成功,errCode=CldOlsErrCode.
         *                    ACCOUT_NOT_LOGIN未登录或登录态已失效引导用户去登录）
         * @param lstOfResult 首次获取成功回调
         * @return void
         * @author Zhouls
         * @date 2015-12-11 上午11:05:21
         */
        public void onGetResult(int errCode, List<CldMonitorAuth> lstOfResult);

        public void onGetReqKey(String arg0);
    }

    /**
     * @author : yuyh
     * @annotation :获取企业权限信息
     * @date :2016-9-22上午10:36:22
     * @parama :
     * @return :
     **/
    public static interface ICldAuthInfoListener {
        /**
         * 获取授权列表回调
         *
         * @param errCode     错误码（errCode=0成功,errCode=CldOlsErrCode.
         *                    ACCOUT_NOT_LOGIN未登录或登录态已失效引导用户去登录）
         * @param lstOfResult 首次获取成功回调
         * @return void
         * @author Zhouls
         * @date 2015-12-11 上午11:05:21
         */
        public void onGetResult(int errCode, List<AuthInfoList> lstOfResult);

        public void onGetReqKey(String arg0);
    }

    /**
     * 确保鉴权成功
     *
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2015-12-10 下午6:17:00
     */
    public static void dealAfterAuth(final ICldDealAuthListener listener) {
        if (!CldKAccountAPI.getInstance().isLogined()) {
            CldDalKDelivery.getInstance().setLoginAuth(false);
            if (null != listener) {
                listener.failedDeal(CldOlsErrCode.ACCOUT_NOT_LOGIN);
            }
        } else {
            if (CldDalKDelivery.getInstance().isLoginAuth()) {
                if (null != listener) {
                    listener.toDeal();
                }
            } else {
                // CldBllKDelivery.getInstance().init(authListener);
                CldBllKDelivery.getInstance().loginAuth(new ICldResultListener() {

                    @Override
                    public void onGetResult(int errCode) {

                        if (errCode == 0) {
                            if (null != listener) {
                                listener.toDeal();
                            }
                            List<CldDeliGroup> myGroups = CldDalKDelivery.getInstance().getLstOfMyGroups();
                            if (null != myGroups && myGroups.size() > 0) {
                            }
                        } else {
                            if (null != listener) {
                                listener.failedDeal(errCode);
                            }
                        }
                    }

                    @Override
                    public void onGetReqKey(String tag) {


                    }
                });
            }
        }
    }

    /**
     * 送货过程中，在开始送货时需上传其所在区域信息(改变运货单状态实现)，同时在终端行政区位置发生变化时，如从深圳进入东莞时再次上传一下终端的位置信息。
     *
     * @param corpId     运货单所属企业ID
     * @param taskId     运货单ID
     * @param storeId    运货单当前运货点ID
     * @param regionCode 当前位置区域ID
     * @param regionName 当前位置区域名称
     * @param x          当前位置x
     * @param y          当前位置y
     * @param cellId     道路图幅ID
     * @param uid        道路UID
     * @param waybill    配送单号
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-5 下午6:04:33
     */
    public void uploadPostion(String corpId, String taskId, String storeId, int regionCode, String regionName, long x,
                              long y, int cellId, int uid, String waybill, ICldResultListener listener) {
        CldBllKDelivery.getInstance().uploadPostion(corpId, taskId, storeId, regionCode, regionName, x, y, cellId, uid,
                waybill, listener);
    }

    /**
     * 上报线路状态
     *
     * @param corpid   企业ID
     * @param routeid  推荐线路ID
     * @param naviid   使用推荐线路导航ID
     * @param status   0不用推荐线路导航 1使用推荐线路导航 6导航结束(到达目的地)7导航结束(没到目的地) 这期只报 (0 1 6 7)
     * @param x        上报位置X
     * @param y        上报位置y
     * @param cell     道路图幅ID
     * @param uid      道路UID
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-6 上午11:21:41
     */
    public void uploadRoutePlanStatus(final String corpid, final int routeid, final int naviid, final int status,
                                      final long x, final long y, final int cell, final int uid, final ICldResultListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {
            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().uploadRoutePlanStatus(corpid, routeid, naviid, status, x, y, cell, uid,
                        listener);
            }
        });
    }

    /**
     * 从当前加入的车队列表里去Id对应的企业名称
     *
     * @param corpId
     * @return String
     * @author Zhouls
     * @date 2016-5-18 下午5:26:04
     */
    public String getCorpNameById(String corpId) {
        if (TextUtils.isEmpty(corpId)) {
            return "";
        }
        List<CldDeliGroup> myGroups = CldDalKDelivery.getInstance().getLstOfMyGroups();
        if (null != myGroups && myGroups.size() > 0) {
            for (int i = 0; i < myGroups.size(); i++) {
                if (null != myGroups.get(i)) {
                    if (corpId.equals(myGroups.get(i).corpId)) {
                        return myGroups.get(i).corpName;
                    }
                }
            }
        }
        return "";
    }

    /**
     * 踢出企业后操作
     *
     * @param corpId
     * @return void
     * @author Zhouls
     * @date 2016-5-20 下午4:31:46
     */
    public void afterKickOutCorp(String corpId) {
        if (TextUtils.isEmpty(corpId)) {
            return;
        }
        // 移除加入的企业
        List<CldDeliGroup> lstOfMGroups = CldDalKDelivery.getInstance().getLstOfMyGroups();
        if (null != lstOfMGroups && lstOfMGroups.size() > 0) {
            for (int i = 0; i < lstOfMGroups.size(); i++) {
                if (null == lstOfMGroups.get(i)) {
                    continue;
                }
                if (corpId.equals(lstOfMGroups.get(i).corpId)) {
                    // 找到被移除的企业 移除
                    lstOfMGroups.remove(i);
                }
            }
        }
        // 移除授权列表里的企业
        List<CldDeliGroup> lstOfAuthGroups = CldDalKDelivery.getInstance().getLstOfAuthCorps();
        if (null != lstOfAuthGroups && lstOfAuthGroups.size() > 0) {
            for (int i = 0; i < lstOfAuthGroups.size(); i++) {
                if (null == lstOfAuthGroups.get(i)) {
                    continue;
                }
                if (corpId.equals(lstOfAuthGroups.get(i).corpId)) {
                    // 找到被移除的企业 移除
                    lstOfAuthGroups.remove(i);
                }
            }
        }
    }

    /**
     * 收到新运货单消息后保存
     *
     * @param lstOfNewTask
     * @return void
     * @author Zhouls
     * @date 2016-5-26 下午5:39:06
     */
    public void saveNewTaskStatus(List<MtqDeliTask> lstOfNewTask) {
        if (null != lstOfNewTask && lstOfNewTask.size() > 0) {
            List<CldDeliTaskDB> lstOfDb = new ArrayList<CldDeliTaskDB>();
            for (int i = 0; i < lstOfNewTask.size(); i++) {
                MtqDeliTask temptask = lstOfNewTask.get(i);
                if (null == temptask) {
                    continue;
                }
                long id = 0;
                try {
                    long corp = Long.parseLong(temptask.corpid);
                    long task = Long.parseLong(temptask.taskid);
                    id = (corp << 32) + task;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (id == 0) {
                    continue;
                }
                CldDeliTaskDB temp = new CldDeliTaskDB();
                temp.id = id;
                temp.kuid = CldKServiceAPI.getInstance().getKuid();
                temp.status = temptask.readstatus;
                lstOfDb.add(temp);
            }
            if (lstOfDb.size() > 0) {
                CldDbUtils.saveAll(lstOfDb);
            }
        }
    }

    /**
     * 获取未完成运货单条数
     *
     * @param status
     * @return int
     * @author Zhouls
     * @date 2016-5-23 下午6:02:40
     */
    public int getUnFinishTaskCount(int status) {
        return CldBllKDelivery.getInstance().getUnFinishTaskCount(status);
    }

    /**
     * 获取企业电子围栏回调
     *
     * @author Zhouls
     * @date 2016-5-24 下午12:02:44
     */
    public static interface ICldDeliGetElectfenceListener {
        /**
         * 获取企业电子围栏回调
         *
         * @param errCode
         * @param lstOfElectfence
         * @return void
         * @author Zhouls
         * @date 2016-5-24 下午12:03:04
         */
        public void onGetResult(int errCode, List<CldElectfence> lstOfElectfence);

        public void onGetReqKey(String arg0);
    }

    /**
     * 请求电子围栏
     *
     * @param corpid   当前正在运货运货单的企业id
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-24 下午2:31:47
     */
    public void requestElectfence(final String corpid, final ICldDeliGetElectfenceListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {
            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().requestElectfence(corpid, listener);
            }
        });
    }

    /**
     * @return :
     * @annotation : 获取所有企业的围栏
     * @author : yuyh
     * @date :2016-11-28下午2:39:57
     * @parama :
     **/
    public void requestAllElectfence(final ICldDeliGetElectfenceListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().requestAllElectfence(listener);
            }

            @Override
            public void failedDeal(int errcode) {

                listener.onGetResult(errcode, null);
            }
        });
    }

    /**
     * 触发电子围栏报警
     *
     * @param parm
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-24 下午2:34:46
     */
    public void uploadElectfence(final CldUploadEFParm parm, final ICldResultListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {
            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().uploadElectfence(parm, listener);
            }
        });
    }

    /**
     * 补报电子围栏报警
     *
     * @param parm
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-24 下午2:34:46
     */
    public void reUploadElectfence(final CldReUploadEFParm parm, final ICldResultListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {
            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().reUploadElectfence(parm, listener);

            }
        });
    }

    /**
     * @return :
     * @annotation : 获取门店权限列表
     * @author : yuyh
     * @date :2016-9-22上午10:24:51
     * @parama :
     **/
    public void getAuthInfoList(final ICldAuthInfoListener listener) {
        dealAfterAuth(new ICldDealAuthListener() {
            @Override
            public void failedDeal(int errCode) {

                if (null != listener) {
                    listener.onGetResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {

                CldBllKDelivery.getInstance().getAuthInfoList(listener);
            }
        });
    }

    public static interface IBatchReportTaskstoreStatusListener {
        /**
         * 3.2.7.5 批量补报运货点完成状态
         */
        public void onGetResult(int errCode, List<StoreStatusResult> lstOfResult);

        public void onGetReqKey(String arg0);

    }

    /**
     * 3.2.7.5 批量补报运货点完成状态
     *
     * @param corpid 所属企业ID
     * @param taskid 配送任务ID
     * @param list   运货点状态集合
     * @author Zhaoqy
     * @date 2018-4-23 下午3:38:21
     */
    public void batchReportTaskstoreStatus(final String corpid, final String taskid,
                                           final List<TaskStore> list,
                                           final IBatchReportTaskstoreStatusListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {
                if (null != listener) {
                    listener.onGetResult(errCode, null);
                }
            }

            @Override
            public void toDeal() {
                CldBllKDelivery.getInstance().batchReportTaskstoreStatus(corpid, taskid, list, listener);
            }
        });
    }

    public static interface IUploadDeviceLogListener {
        /**
         * 3.2.12.5上传终端日志文件
         */
        public void onGetResult(int errCode);

        public void onGetReqKey(String arg0);

    }

    public void uploadDeviceLog(final String guid, final long time, final String filename,
                                final String base64_data, final IUploadDeviceLogListener listener) {

        dealAfterAuth(new ICldDealAuthListener() {

            @Override
            public void failedDeal(int errCode) {
                if (null != listener) {
                    listener.onGetResult(errCode);
                }
            }

            @Override
            public void toDeal() {
                CldBllKDelivery.getInstance().uploadDeviceLog(guid, time, filename, base64_data,
                        listener);
            }
        });
    }

//	public void uploadDeviceLogTest(String base64_data) {
//		String guid = CldDalKAccount.getInstance().getDuid() + "";
//		long time = CldKAccount.getInstance().getSvrTime();
//		String filename = "test.log";
//
//		uploadDeviceLog(guid, time, filename, base64_data, new IUploadDeviceLogListener() {
//
//
//			@Override
//			public void onGetResult(int errCode) {
//
//			}
//
//			@Override
//			public void onGetReqKey(String arg0) {
//
//			}
//		});
//	}


    /**
     * 退出登录后清楚用户数据
     *
     * @return void
     * @author Zhouls
     * @date 2016-5-20 下午4:37:08
     */
    public void onLoginOut() {
        CldDalKDelivery.getInstance().uninitData();
    }

    public static CldKDeliveryAPI getInstance() {
        if (null == instance) {
            instance = new CldKDeliveryAPI();
        }
        return instance;
    }

    private CldKDeliveryAPI() {

    }

    private static CldKDeliveryAPI instance;

    /**
     * 鉴权处理
     *
     * @author Zhouls
     * @date 2015-12-10 下午6:14:08
     */
    public interface ICldDealAuthListener {
        /**
         * 鉴权失败处理
         *
         * @return void
         * @author Zhouls
         * @date 2015-12-10 下午6:16:17
         */
        public void failedDeal(int errcode);

        /**
         * 已鉴权或者鉴权成功处理
         *
         * @return void
         * @author Zhouls
         * @date 2015-12-10 下午6:16:01
         */
        public void toDeal();
    }
}
