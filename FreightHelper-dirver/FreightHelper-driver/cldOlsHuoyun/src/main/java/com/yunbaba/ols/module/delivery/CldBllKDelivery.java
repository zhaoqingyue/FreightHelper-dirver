/*
 * @Title CldBllKDeliverty.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-12-9 上午11:25:27
 * @version 1.0
 */
package com.yunbaba.ols.module.delivery;

import android.text.TextUtils;
import android.util.Log;

import com.cld.device.CldPhoneNet;
import com.cld.log.CldLog;
import com.cld.net.CldHttpClient;
import com.cld.net.CldResponse.ICldResponse;
import com.cld.net.volley.VolleyError;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICarCheckHistoryListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICarListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICarRouteListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldAuthInfoListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliGetCorpLimitListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliGetElectfenceListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliGetTaskDetailListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliGetTaskHistoryListListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliGetTaskListListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliInitListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliSearchStoreListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliTaskStatusListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliTaskStoreStatusListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldDeliveryMonitorListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IExaminationDetailListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IExaminationHistoryListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IGetFeedBackInfoListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IGetFeedBackListListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ITaskDetailListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ITaskListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IUploadPicListener;
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
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliUploadStoreParm;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldElectfence;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldMonitorAuth;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldReUploadEFParm;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldUploadEFParm;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTask;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTaskDetail;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProCarExamineDetail;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProCarExamineHistoryList;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProFeedBackInfo;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProFeedBackList;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProStoreStatusResultInfo;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtAuthInfoLst;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtCar;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtCarCheckHistory;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtCarRoute;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtGetAuthStoreList;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtGetCorpLimitData;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtGetElectfence;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtGetTaskHistoryList;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtGetTaskInfo;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtGetTaskList;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtGetUnFinishTaskCount;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtGetUnReadTaskCount;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtJoinGroup;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtLoginAuth;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtMonitorAuth;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtMonitorAuthLst;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtMstBussData;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtSearchNearStores;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtSearchNpStores;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtSearchStores;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtTask;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtTaskDetail;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtUpdateTask;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtUpdateTaskStore;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtUploadPic;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
import com.yunbaba.ols.module.delivery.bean.TaskStore;
import com.yunbaba.ols.module.delivery.tool.CldKBaseParse.ProtBase;
import com.yunbaba.ols.module.delivery.tool.CldKDeviceAPI;
import com.yunbaba.ols.module.delivery.tool.CldOlsErrManager.CldOlsErrCode;
import com.yunbaba.ols.module.delivery.tool.CldSapParser;
import com.yunbaba.ols.tools.err.CldOlsErrManager;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;
import com.yunbaba.ols.tools.parse.CldKReturn;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.type;

/**
 * 货运业务逻辑层
 * 
 * @author Zhouls
 * @date 2015-12-9 上午11:25:27
 */
public class CldBllKDelivery {
	private String TAG = "ols_delivery";

	/**
	 * 货运初始化（在登录或者退出登录调用）
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-10 下午6:36:29
	 */
	public void init(final ICldDeliInitListener listener) {
		if (!CldKAccountAPI.getInstance().isLogined()) {
			CldDalKDelivery.getInstance().setLoginAuth(false);
		}
		CldBllKDelivery.getInstance().loginAuth(new ICldResultListener() {
			@Override
			public void onGetResult(int errCode) {

				if (null != listener) {
					listener.onLoginAuth(errCode);
				}
				List<CldDeliGroup> myGroups = CldDalKDelivery.getInstance().getLstOfMyGroups();
				if (errCode == 0 && null != myGroups && myGroups.size() > 0) {
					CldLog.d(TAG, "hy auth success!");
					// 获取有企业权限门店列表
				} else {
					if (errCode != 0) {
						CldLog.d(TAG, "hy auth failed!");
					} else {
						CldLog.d(TAG, "has not join corp!");
					}
				}
			}

			@Override
			public void onGetReqKey(String tag) {


			}
		});
	}

	/**
	 * 登录鉴权
	 * 

	 *            标识id
	 * @param listener
	 *            回调
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:28:03
	 */
	public void loginAuth(final ICldResultListener listener) {
		// 调用后清除之前数据层的数据
		CldDalKDelivery.getInstance().initData();
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}

		if (!CldKAccountAPI.getInstance().isLogined()) {
			// 未登录返回登录错误码
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.ACCOUT_NOT_LOGIN;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.loginAuth();
		CldHttpClient.get(request.url, ProtLoginAuth.class, new ICldResponse<ProtLoginAuth>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtLoginAuth response) {

				CldSapParser.parseObject(response, ProtLoginAuth.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_loginAuth");
				CldLog.d(TAG, errRes.errMsg + "_loginAuth");
				handleErr(request, errRes);
				if (errRes.errCode == 0 && null != response && null != response.data) {
					if (!TextUtils.isEmpty(response.syskey)) {
						CldDalKDelivery.getInstance().setCldDeliveryKey(response.syskey);
						CldDalKDelivery.getInstance().setExpiry_time(response.data.expiry_time);
						CldDalKDelivery.getInstance().setLockcorpid(response.data.lockcorpid);
						CldDalKDelivery.getInstance().setState(response.data.state);
						if (null != response.data.groups && response.data.groups.size() > 0) {
							List<CldDeliGroup> lstOfMyGroups = new ArrayList<CldDeliGroup>();
							response.data.protParse(lstOfMyGroups);
							CldDalKDelivery.getInstance().setLstOfMyGroups(lstOfMyGroups);
						}
						CldDalKDelivery.getInstance().setLoginAuth(true);
						CldKDeliveryAPI.getInstance().getAuthInfoList(new ICldAuthInfoListener() {

							@Override
							public void onGetResult(int errCode, List<AuthInfoList> lstOfResult) {

								CldDalKDelivery.getInstance().setLstOfAuthInfo(lstOfResult);
							}

							@Override
							public void onGetReqKey(String arg0) {


							}
						});
					} else {
						CldLog.e(TAG, "login auth key is empty!");
					}
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 新增授权信息
	 * 
	 * @param mobile
	 *            手机号

	 *            标注
	 * @param timeOut
	 *            有效期
	 * @param listener
	 *            回调
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:27:18
	 */
	public void addMonitorAuth(final String mobile, final String remark, CldAuthTimeOut timeOut,
			final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		// 过期时间
		final long oTime = CldKDeviceAPI.getSvrTime() + CldAuthTimeOut.valueOf(timeOut);
		final CldKReturn request = CldSapKDelivery.addMonitorAuth(mobile, remark, oTime);
		CldHttpClient.get(request.url, ProtMonitorAuth.class, new ICldResponse<ProtMonitorAuth>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtMonitorAuth response) {

				CldSapParser.parseObject(response, ProtMonitorAuth.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_addMonitorAuth");
				CldLog.d(TAG, errRes.errMsg + "_addMonitorAuth");
				handleErr(request, errRes);
				if (errRes.errCode == 0 && null != response) {
					CldMonitorAuth bean = new CldMonitorAuth();
					bean.timeOut = (oTime);
					bean.mobile = (mobile);
					bean.mark = (remark);
					response.protParase(bean);
					CldDalKDelivery.getInstance().addToMonitorLst(bean);
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 删除授权信息
	 * 
	 * @param id
	 *            标识id
	 * @param listener
	 *            回调
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:28:03
	 */
	public void delMonitorAuth(final String id, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.delMonitorAuth(id);
		CldHttpClient.get(request.url, ProtBase.class, new ICldResponse<ProtBase>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase response) {

				CldSapParser.parseObject(response, ProtBase.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_delMonitorAuth");
				CldLog.d(TAG, errRes.errMsg + "_delMonitorAuth");
				handleErr(request, errRes);
				if (errRes.errCode == 0 && null != response) {
					CldDalKDelivery.getInstance().delMonitor(id);
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 修改授权有效期
	 * 
	 * @param id
	 *            标识id
	 * @param timeOut
	 *            有效期
	 * @param listener
	 *            回调
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:28:25
	 */
	public void reviseMonitorAuthTimeOut(final String id, CldAuthTimeOut timeOut, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		// 过期时间
		final long oTime = CldKDeviceAPI.getSvrTime() + CldAuthTimeOut.valueOf(timeOut);
		final CldKReturn request = CldSapKDelivery.reviseMonitorAuthTimeOut(id, oTime);
		CldHttpClient.get(request.url, ProtBase.class, new ICldResponse<ProtBase>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase response) {

				CldSapParser.parseObject(response, ProtBase.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_revMonitorAuth");
				CldLog.d(TAG, errRes.errMsg + "_revMonitorAuth");
				handleErr(request, errRes);
				if (errRes.errCode == 0 && null != response) {
					CldDalKDelivery.getInstance().reviseMonitor(id, null, oTime);
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 
	 * 修改备注
	 * 
	 * @param id
	 *            标识id
	 * @param mark
	 *            标注
	 * @param listener
	 *            回调
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:28:43
	 */
	public void reviseMonitorAuthMark(final String id, final String mark, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.reviseMonitorAuthMark(id, mark);
		CldHttpClient.get(request.url, ProtBase.class, new ICldResponse<ProtBase>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase response) {

				CldSapParser.parseObject(response, ProtBase.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_revMonitorAuth");
				CldLog.d(TAG, errRes.errMsg + "_revMonitorAuth");
				handleErr(request, errRes);
				if (errRes.errCode == 0 && null != response) {
					CldDalKDelivery.getInstance().reviseMonitor(id, mark, -1);
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 获取授权信息列表
	 * 
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:29:48
	 */
	public void getMonitorAuthList(final ICldDeliveryMonitorListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.getMonitorAuthList();
		CldHttpClient.get(request.url, ProtMonitorAuthLst.class, new ICldResponse<ProtMonitorAuthLst>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtMonitorAuthLst response) {

				CldSapParser.parseObject(response, ProtMonitorAuthLst.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getMonitorAuthList");
				CldLog.d(TAG, errRes.errMsg + "_getMonitorAuthList");
				handleErr(request, errRes);
				if (errRes.errCode == 0 && null != response) {
					List<CldMonitorAuth> lstOfBean = new ArrayList<CldMonitorAuth>();
					response.protParase(lstOfBean);
					CldDalKDelivery.getInstance().reloadMonitorLst(lstOfBean);
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode, CldDalKDelivery.getInstance().getMonitorAuthLst());
				}
			}
		});
	}

	/**
	 * 上传收款信息
	 * 
	 * @param param
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-4-22 下午4:21:48
	 */
	public void uploadReceipt(CldDeliReceiptParm param, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		if (null == param) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetResult(CldOlsErrCode.PARAM_INVALID);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.uploadReceipt(param.corpid, param.taskid, param.storeid,
				param.pay_method, param.real_amount, param.return_desc, param.return_amount, param.pay_remark,
				param.waybill, param.uploadPng, param.e_receipts_0_ext);
		CldHttpClient.post(request.url, request.mapPost, ProtBase.class, false, new ICldResponse<ProtBase>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase response) {

				CldSapParser.parseObject(response, ProtBase.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_uploadReceipt");
				CldLog.d(TAG, errRes.errMsg + "_uploadReceipt");
				handleErr(request, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 获取周边门店
	 * 
	 * @param corpid
	 *            企业ID
	 * @param centerX
	 *            当前位置X
	 * @param centerY
	 *            当前位置Y
	 * @param range
	 *            搜索半径
	 * @param page
	 *            页码
	 * @param pageSize
	 *            页容量
	 * @param listener
	 *            回调
	 * @return void
	 * @author Zhouls
	 * @date 2016-4-22 下午4:46:54
	 */
	public void searchNearStores(String corpid, int centerX, int centerY, int range, int page, int pageSize,
			final ICldDeliSearchStoreListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null,"网络通信出现问题，请稍后再试。");
			}
			return;
		}
		if (TextUtils.isEmpty(corpid) || centerX < 0 || centerY < 0 || range < 0) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetResult(CldOlsErrCode.PARAM_INVALID, null,"参数出错");
			}
			return;
		}
		if (page < 0) {
			page = 1;
		}
		if (pageSize < 0) {
			pageSize = 20;
		}
		final CldKReturn request = CldSapKDelivery.searchNearStores(corpid, centerX, centerY, range, page, pageSize);
		CldHttpClient.get(request.url, ProtSearchNearStores.class, new ICldResponse<ProtSearchNearStores>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode, null,"搜索异常");
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtSearchNearStores response) {

				CldSapParser.parseObject(response, ProtSearchNearStores.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_searchNearStores");
				CldLog.d(TAG, errRes.errMsg + "_searchNearStores");
				handleErr(request, errRes);
				CldDeliSearchStoreResult result = null;
				if (null != response) {
					result = response.protParse();
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode, result,response.errmsg);
				}
			}
		});
	}

	/**
	 * 搜索门店
	 * 
	 * @param corpid
	 *            指定企业ID
	 * @param keyword
	 *            搜索词

	 *            (1有位置门店搜索,0无位置待审核)
	 * @param page
	 *            当前页(从1开始，默认为1)
	 * @param pageSize
	 *            每页显示数量(默认20，最大100)
	 * @param listener
	 *            回调
	 * @return void
	 * @author Zhouls
	 * @date 2016-4-22 下午5:49:14
	 */
	public void searchStores(String corpid, String keyword, int type, int page, int pageSize, int iscenter,
			final ICldDeliSearchStoreListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null,"网络通信出现问题，请稍后再试。");
			}
			return;
		}
		if (TextUtils.isEmpty(corpid)) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetResult(CldOlsErrCode.PARAM_INVALID, null,"参数出错");
			}
			return;
		}
		if (page < 0) {
			page = 1;
		}
		if (pageSize < 0) {
			pageSize = 20;
		}
		final CldKReturn request = CldSapKDelivery.searchStores(corpid, keyword, type, page, pageSize, iscenter);
		CldHttpClient.get(request.url, ProtSearchStores.class, new ICldResponse<ProtSearchStores>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode, null,"搜索异常");
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtSearchStores response) {

				CldSapParser.parseObject(response, ProtSearchStores.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_searchStores");
				CldLog.d(TAG, errRes.errMsg + "_searchStores");
				handleErr(request, errRes);
				CldDeliSearchStoreResult result = null;
				if (null != response) {
					result = response.protParse();
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode, result ,response.errmsg);
				}
			}
		});
	}

	public void getMyMarkStoreList(String corpid, String keyword, int iscenter, int starttime, int endtime, int status, int page, int pageSize, final CldKDeliveryAPI.ICldGetMyMarkStoreListListener listener) {


		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null,"网络通信出现问题，请稍后再试。");
			}
			return;
		}
		if (TextUtils.isEmpty(corpid) || type < 0) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetResult(CldOlsErrCode.PARAM_INVALID, null,"参数出错");
			}
			return;
		}
		if (page < 0) {
			page = 1;
		}
		if (pageSize < 0) {
			pageSize = 0;
		}
		final CldKReturn request = CldSapKDelivery.getMyMarkStoreRecord(corpid, keyword, iscenter, starttime, endtime, status, page, pageSize);
		CldHttpClient.get(request.url, CldSapKDeliveryParse.ProtGetMarkStoreStores.class, new ICldResponse<CldSapKDeliveryParse.ProtGetMarkStoreStores>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode, null,"搜索异常");
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(CldSapKDeliveryParse.ProtGetMarkStoreStores response) {

				CldSapParser.parseObject(response, CldSapKDeliveryParse.ProtGetMarkStoreStores.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_searchStores");
				CldLog.d(TAG, errRes.errMsg + "_searchStores");
				handleErr(request, errRes);
				CldSapKDeliveryParam.CldDeliGetMyMarkStoreResult result = null;
				if (null != response) {
					result = response.protParse();
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode, result ,response.errmsg);
				}
			}
		});



	}




	/**
	 * 搜索未标注门店
	 * 
	 * @param corpid
	 *            指定企业ID
	 *            city 区域名称
	 * @param page
	 *            当前页(从1开始，默认为1)
	 * @param pageSize
	 *            每页显示数量(默认20，最大100)
	 * @param listener
	 *            回调
	 * @return void
	 * @author Zhouls
	 * @date 2016-4-22 下午5:52:00
	 */
	public void searchNoPosStores(String corpid, String regioncity, int page, int pageSize, final int iscenter,
			final ICldDeliSearchStoreListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null,"网络通信出现问题，请稍后再试。");
			}
			return;
		}
		if (TextUtils.isEmpty(corpid)) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetResult(CldOlsErrCode.PARAM_INVALID, null,"参数出错");
			}
			return;
		}
		if (page < 0) {
			page = 1;
		}
		if (pageSize < 0) {
			pageSize = 20;
		}
		final CldKReturn request = CldSapKDelivery.searchNoPosStores(corpid, regioncity, page, pageSize, iscenter);
		CldHttpClient.get(request.url, ProtSearchNpStores.class, new ICldResponse<ProtSearchNpStores>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode, null,"搜索异常");
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtSearchNpStores response) {

				CldSapParser.parseObject(response, ProtSearchNpStores.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_searchNoPosStores");
				CldLog.d(TAG, errRes.errMsg + "_searchNoPosStores");
				handleErr(request, errRes);
				CldDeliSearchStoreResult result = null;
				if (null != response) {
					result = response.protParse();
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode, result,response.errmsg);
				}
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
	 * @date 2016-4-22 下午6:08:31
	 */
	public void uploadStore(CldDeliUploadStoreParm param, final CldKDeliveryAPI.ICldUploadStoreListListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode,"网络未连接");
			}
			return;
		}
		if (null == param) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetResult(CldOlsErrCode.PARAM_INVALID,"参数错误");
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.uploadStore(param.corpid, param.settype, param.storeid,
				param.storename, param.address, param.linkman, param.phone, param.storekcode, param.remark,
				param.uploadPng, param.iscenter, param.extpic,param.storecode,param.uptime);
		CldHttpClient.post(request.url, request.mapPost, ProtBase.class, false, new ICldResponse<ProtBase>() {

			@Override
			public void onErrorResponse(VolleyError error) {

				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode,"解析错误");
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase response) {

				CldSapParser.parseObject(response, ProtBase.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_uploadStore");
				CldLog.d(TAG, errRes.errMsg + "_uploadStore");
				handleErr(request, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode,errRes.errMsg);
				}
			}
		});
	}

	/**
	 * 加入车队
	 * 
	 * @param inviteCode
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-4-27 下午3:10:15
	 */
	public void joinGroup(String inviteCode, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		if (TextUtils.isEmpty(inviteCode)) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetResult(CldOlsErrCode.PARAM_INVALID);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.joinGroup(inviteCode);
		CldHttpClient.get(request.url, ProtJoinGroup.class, new ICldResponse<ProtJoinGroup>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtJoinGroup response) {

				CldSapParser.parseObject(response, ProtJoinGroup.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_joinGroup");
				CldLog.d(TAG, errRes.errMsg + "_joinGroup");
				handleErr(request, errRes);
				if (null != response && errRes.errCode == 0) {
					List<CldDeliGroup> lstOfMyGroups = new ArrayList<CldDeliGroup>();
					response.protParse(lstOfMyGroups);
					CldDalKDelivery.getInstance().setLstOfMyGroups(lstOfMyGroups);
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 拒绝加入车队
	 * 
	 * @param inviteCode
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-4-27 下午3:15:57
	 */
	public void unJoinGroup(String inviteCode, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		if (TextUtils.isEmpty(inviteCode)) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetResult(CldOlsErrCode.PARAM_INVALID);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.unJoinGroup(inviteCode);
		CldHttpClient.get(request.url, ProtJoinGroup.class, new ICldResponse<ProtJoinGroup>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtJoinGroup response) {

				CldSapParser.parseObject(response, ProtJoinGroup.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_unJoinGroup");
				CldLog.d(TAG, errRes.errMsg + "_unJoinGroup");
				handleErr(request, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 获取未完成货运单列表
	 * 
	 * @param corpid
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-4-27 下午5:22:34
	 */
	public void getDeliTaskList(String corpid, final ICldDeliGetTaskListListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetTaskLstResult(errRes.errCode, null);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.getDeliTaskList(corpid);
		CldLog.d(TAG, "request.url: " + request.url);
		CldHttpClient.get(request.url, ProtGetTaskList.class, new ICldResponse<ProtGetTaskList>() {
			@Override
			public void onErrorResponse(VolleyError error) {


				Log.e("mymymy", error.getMessage()+"mymymy");

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetTaskLstResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtGetTaskList response) {


				//Log.e("mymymy", new Gson().toJson(response.tasks)+"mymymy");
				// .toString());

				CldSapParser.parseObject(response, ProtGetTaskList.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getDeliTaskList");
				CldLog.d(TAG, errRes.errMsg + "_getDeliTaskList");
				handleErr(request, errRes);
				if (null != response && response.errcode == 0) {
					if (null != response.tasks && response.tasks.size() > 0) {
						// 缓存阅读状态做对比
						// CldDalKDelivery.getInstance()
						// .syncTaskReadStatus(response.tasks);
						if (null != listener) {
							listener.onGetTaskLstResult(errRes.errCode, response.tasks);
						}
					} else {
						if (null != listener) {
							listener.onGetTaskLstResult(errRes.errCode, new ArrayList<MtqDeliTask>());
						}
					}
				} else {
					if (null != listener) {
						listener.onGetTaskLstResult(errRes.errCode, null);
					}
				}
			}
		});
	}

	/**
	 * 获取运货单历史
	 * 
	 * @param status
	 * @param corpid
	 * @param page
	 * @param pagesize
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-4-27 下午5:31:36
	 */
	public void getDeliTaskHistoryList(String status, String corpid, final String taskid,int page, int pagesize,
			final ICldDeliGetTaskHistoryListListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetTaskLstResult(errRes.errCode, null, -1, -1, -1);
			}
			return;
		}
		if (TextUtils.isEmpty(status)) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetTaskLstResult(CldOlsErrCode.PARAM_INVALID, null, -1, -1, -1);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.getDeliTaskHistoryList(status, corpid,taskid,page, pagesize);
		CldHttpClient.get(request.url, ProtGetTaskHistoryList.class, new ICldResponse<ProtGetTaskHistoryList>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				Log.e(TAG, errRes.errCode + "_getDeliTaskHistoryList");
				Log.e(TAG, errRes.errMsg + "_getDeliTaskHistoryList"+" " );

				if (null != listener) {
					listener.onGetTaskLstResult(errRes.errCode, null, -1, -1, -1);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtGetTaskHistoryList response) {

				CldSapParser.parseObject(response, ProtGetTaskHistoryList.class, errRes);

				handleErr(request, errRes);

//				Log.e(TAG, errRes.errCode + "_getDeliTaskHistoryList");
//				Log.e(TAG, errRes.errMsg + "_getDeliTaskHistoryList"+" "+response.tasks.size() );

				if (null != response && response.errcode == 0) {
					if (null != response.tasks && response.tasks.size() > 0) {
						if (null != listener) {
							listener.onGetTaskLstResult(errRes.errCode, response.tasks, response.page,
									response.pagecount, response.total);
						}
					} else {
						if (null != listener) {
							listener.onGetTaskLstResult(errRes.errCode, new ArrayList<MtqDeliTask>(), response.page,
									response.pagecount, response.total);
						}
					}
				} else {
					if (null != listener) {
						listener.onGetTaskLstResult(errRes.errCode, null, -1, -1, -1);
					}
				}
			}
		});
	}

	/**
	 * 获取运货单详情
	 * 
	 * @param corpid
	 * @param taskid
	 * @param page
	 *            运货点页码
	 * @param pagesize
	 *            运货点页容量
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-4-27 下午5:56:55
	 */
	public void getDeliTaskDetail(String corpid, String taskid, int page, int pagesize,
			final ICldDeliGetTaskDetailListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetTaskDetailResult(errRes.errCode, null);
			}
			return;
		}
		if (TextUtils.isEmpty(corpid) || TextUtils.isEmpty(taskid)) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetTaskDetailResult(CldOlsErrCode.PARAM_INVALID, null);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.getDeliTaskDetail(corpid, taskid, page, pagesize);
		CldHttpClient.get(request.url, ProtGetTaskInfo.class, new ICldResponse<ProtGetTaskInfo>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				Log.e(TAG,  "_TaskDetail error"+error.getMessage());
				
				
				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetTaskDetailResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtGetTaskInfo response) {

				
				Log.d(TAG, errRes.errCode + "_TaskDetail0");
				
				CldSapParser.parseObject(response, ProtGetTaskInfo.class, errRes);
				
				Log.d(TAG, errRes.errCode + "_TaskDetail1");
				
				CldLog.d(TAG, errRes.errCode + "_TaskDetail");
				CldLog.d(TAG, errRes.errMsg + "_TaskDetail");
				handleErr(request, errRes);
				if (null != response && response.errcode == 0) {
					if (null != response.taskinfo) {
						if (null != listener) {

							MtqDeliTaskDetail  taskdetails = response.protParse();


							listener.onGetTaskDetailResult(errRes.errCode, taskdetails);
						}
					} else {
						if (null != listener) {
							listener.onGetTaskDetailResult(CldOlsErrCode.PARSE_ERR, null);
						}
					}
				} else {
					if (null != listener) {
						listener.onGetTaskDetailResult(errRes.errCode, null);
					}
				}
			}
		});
	}

	/**
	 * 更新货运单状态
	 * 
	 * @param corpid
	 * @param taskid
	 * @param status
	 * @param ecorpid
	 *            需要暂停的运货单所属企业ID
	 * @param etaskid
	 *            需要暂停的运货单ID
	 * @param x
	 * @param y
	 * @param cell
	 * @param uid
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-14 下午4:39:35
	 */
	public void updateDeliTaskStatus(String corpid, String taskid, int status, String ecorpid, String etaskid, long x,
			long y, int cell, int uid, final ICldDeliTaskStatusListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onUpdateTaskStatus(errRes.errCode, null, null, -1);
			}
			return;
		}
		if (TextUtils.isEmpty(corpid) || TextUtils.isEmpty(taskid)) {
			// 参数不合法回调
			if (null != listener) {
				listener.onUpdateTaskStatus(CldOlsErrCode.PARAM_INVALID, null, null, -1);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.updateDeliTaskStatus(corpid, taskid, status, ecorpid, etaskid, x, y,
				cell, uid);
		CldHttpClient.get(request.url, ProtUpdateTask.class, new ICldResponse<ProtUpdateTask>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onUpdateTaskStatus(errRes.errCode, null, null, -1);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtUpdateTask response) {

				CldSapParser.parseObject(response, ProtUpdateTask.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_updateDeliTaskStatus");
				CldLog.d(TAG, errRes.errMsg + "_updateDeliTaskStatus");
				handleErr(request, errRes);
				if (null != response && response.errcode == 0) {
					if (null != listener) {
						listener.onUpdateTaskStatus(errRes.errCode, response.corpid, response.taskid, response.status);
					}
				} else {
					if (null != listener) {
						listener.onUpdateTaskStatus(errRes.errCode, null, null, -1);
					}
				}
			}
		});
	}

	/**
	 * 更新运货点状态
	 * 
	 * @param corpid
	 * @param taskid
	 * @param storeid
	 * @param status
	 * @param x
	 * @param y
	 * @param cell
	 * @param uid
	 * @param waybill
	 * @param ewaybill
	 *            需要暂停的运货点
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-10 上午11:14:41
	 */
	public void updateDeliTaskStoreStatus(String corpid, String taskid, String storeid, int status, long x, long y,
			int cell, int uid, String waybill, String ewaybill, int taskStatus,
			final ICldDeliTaskStoreStatusListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onUpdateTaskStoreStatus(errRes.errCode, null, null, null, -1, null, null, null);
			}
			return;
		}
		if (TextUtils.isEmpty(corpid) || TextUtils.isEmpty(taskid)) {
			// 参数不合法回调
			if (null != listener) {
				listener.onUpdateTaskStoreStatus(CldOlsErrCode.PARAM_INVALID, null, null, null, -1, null, null, null);
			}
			return;
		}

		final CldKReturn request = CldSapKDelivery.updateDeliTaskStoreStatus(corpid, taskid, storeid, status, x, y,
				cell, uid, waybill, ewaybill, taskStatus);

		CldHttpClient.get(request.url, ProtUpdateTaskStore.class, new ICldResponse<ProtUpdateTaskStore>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onUpdateTaskStoreStatus(errRes.errCode, null, null, null, -1, null, null, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtUpdateTaskStore response) {

				CldSapParser.parseObject(response, ProtUpdateTaskStore.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_updateDeliStoreStatus");
				CldLog.d(TAG, errRes.errMsg + "_updateDeliStroeStatus");
				handleErr(request, errRes);
				if (null != response && response.errcode == 0) {
					if (null != listener) {
						listener.onUpdateTaskStoreStatus(errRes.errCode, response.corpid, response.taskid,
								response.storeid, response.status, response.waybill, response.ewaybill, response.data);
					}
				} else {
					if (null != listener) {
						listener.onUpdateTaskStoreStatus(errRes.errCode, null, null, null, -1, null, null, null);
					}
				}
			}
		});
	}

	/**
	 * 获取图面企业限行数据
	 * 
	 * @param parm
	 *            图面限行参数
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-14 下午4:33:59
	 */
	public void getMapCorpLimitData(CldDeliCorpLimitMapParm parm, final ICldDeliGetCorpLimitListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				listener.onGetCorpLimitResult(CldOlsErrCode.NET_NO_CONNECTED, null, -1, null, -1);
			}
			return;
		}
		if (null == parm) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetCorpLimitResult(CldOlsErrCode.PARAM_INVALID, null, -1, null, -1);
			}
			return;
		}
		// corpid传0 取所有企业限行数据
		final CldKReturn request = CldSapKDelivery.getCorpLimitData("0", parm.req, "", parm.minX, parm.minY, parm.maxX,
				parm.maxY, parm.tht, parm.twh, parm.twt, parm.tad, parm.tvt);
		CldHttpClient.get(request.url, ProtGetCorpLimitData.class, new ICldResponse<ProtGetCorpLimitData>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetCorpLimitResult(errRes.errCode, null, -1, null, -1);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtGetCorpLimitData response) {

				CldSapParser.parseObject(response, ProtGetCorpLimitData.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getCorpLimitData");
				CldLog.d(TAG, errRes.errMsg + "_getCorpLimitData");
				handleErr(request, errRes);
				if (null != response && errRes.errCode == 0) {
					List<CldDeliCorpLimit> lstOfLimit = new ArrayList<CldDeliCorpLimit>();
					List<CldDeliCorpWarning> lstOfWarning = new ArrayList<CldDeliCorpWarning>();
					response.protParse(lstOfLimit, lstOfWarning);
					if (null != listener) {
						listener.onGetCorpLimitResult(errRes.errCode, lstOfLimit, response.truckcount, lstOfWarning,
								response.warningcount);
					}
				} else {
					if (null != listener) {
						listener.onGetCorpLimitResult(errRes.errCode, null, -1, null, -1);
					}
				}
			}
		});
	}

	/**
	 * 获取沿途企业限行数据
	 * 

	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-4-28 下午2:59:59
	 */
	public void getRouteCorpLimitData(CldDeliCorpLimitRouteParm parm, final ICldDeliGetCorpLimitListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				listener.onGetCorpLimitResult(CldOlsErrCode.NET_NO_CONNECTED, null, -1, null, -1);
			}
			return;
		}
		if (null == parm) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetCorpLimitResult(CldOlsErrCode.PARAM_INVALID, null, -1, null, -1);
			}
			return;
		}
		// corpid传0 取所有企业限行数据
		final CldKReturn request = CldSapKDelivery.getCorpLimitData(parm.curTaskCorpId, parm.req, parm.uid, -1, -1, -1,
				-1, parm.tht, parm.twh, parm.twt, parm.tad, parm.tvt);
		CldHttpClient.get(request.url, ProtGetCorpLimitData.class, new ICldResponse<ProtGetCorpLimitData>() {
			@Override
			public void onErrorResponse(VolleyError error) {

				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetCorpLimitResult(errRes.errCode, null, -1, null, -1);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtGetCorpLimitData response) {

				CldSapParser.parseObject(response, ProtGetCorpLimitData.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getCorpLimitData");
				CldLog.d(TAG, errRes.errMsg + "_getCorpLimitData");
				handleErr(request, errRes);
				if (null != response && errRes.errCode == 0) {
					List<CldDeliCorpLimit> lstOfLimit = new ArrayList<CldDeliCorpLimit>();
					List<CldDeliCorpWarning> lstOfWarning = new ArrayList<CldDeliCorpWarning>();
					response.protParse(lstOfLimit, lstOfWarning);
					if (null != listener) {
						listener.onGetCorpLimitResult(errRes.errCode, lstOfLimit, response.truckcount, lstOfWarning,
								response.warningcount);
					}
				} else {
					if (null != listener) {
						listener.onGetCorpLimitResult(errRes.errCode, null, -1, null, -1);
					}
				}
			}
		});
	}

	/**
	 * 获取企业云端线路(同步需外部开线程处理)
	 * 
	 * @param corpid
	 *            企业ID（不使用企业线路时传0，否则传配送任务所属企业ID）
	 * @param taskid
	 *            配送任务ID（无任务传0）
	 * @param routeid
	 *            推荐线路ID（使用了推荐线路，重算时传入，否则传0）
	 * @param naviid
	 *            使用推荐线路导航ID（使用了推荐线路，在同一次导航里面，重算时传入，否则传0）
	 * @param islimit
	 *            企业的限行数据是否参与规划[0-不用(默认值)、1-使用](当不存在推荐线路的时候有用)
	 * @param routePlanStr
	 *            6部接口拼接参数
	 * @return
	 * @return CldDeliCorpRoutePlanResult
	 * @author Zhouls
	 * @date 2016-5-11 上午10:07:57
	 */
	public CldDeliCorpRoutePlanResult requestCorpRoutePlan(int isroute, String corpid, String taskid, int routeid,
			int naviid, int islimit, String routePlanStr) {
		CldDeliCorpRoutePlanResult result = new CldDeliCorpRoutePlanResult();
		if (!CldPhoneNet.isNetConnected()) {
			result.errCode = CldOlsErrCode.NET_NO_CONNECTED;
			return result;
		}
		if (TextUtils.isEmpty(routePlanStr)) {
			// 参数不合法回调
			result.errCode = CldOlsErrCode.PARAM_INVALID;
			return result;
		}
		final CldKReturn request = CldSapKDelivery.getCorpRoutePlan(isroute, corpid, taskid, routeid, naviid, islimit,
				routePlanStr);
		byte[] data = CldHttpClient.getBytes(request.url);
		if (null != data && data.length > 0) {
			CldLog.e("ols", "return data not null!");
			ProtMstBussData bussData = CldSapKDeliveryParse.covertCorpRouteData(data);
			if (null != bussData && null != bussData.routeInfo && null != bussData.rnsData) {
				result.errCode = 0;
				result.routeInfo = bussData.routeInfo.routeinfo;
				result.rpData = bussData.rnsData;
				result.isRoute = bussData.routeInfo.existroute;
				result.corpId = bussData.routeInfo.corpid;
				return result;
			} else {
				result.errCode = CldOlsErrCode.DATA_RETURN_ERR;
				return result;
			}
		} else {
			CldLog.e("ols", "return data null!");
			result.errCode = CldOlsErrCode.DATA_RETURN_ERR;
			return result;
		}
	}

	/**
	 * 送货过程中，在开始送货时需上传其所在区域信息(改变运货单状态实现)，同时在终端行政区位置发生变化时，如从深圳进入东莞时再次上传一下终端的位置信息。
	 * 
	 * @param corpid
	 * @param taskid
	 * @param storeid
	 * @param regioncode
	 * @param regionname
	 * @param x
	 * @param y
	 * @param cell
	 * @param uid
	 * @param waybill
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-5 下午6:03:36
	 */
	public void uploadPostion(String corpid, String taskid, String storeid, int regioncode, String regionname, long x,
			long y, int cell, int uid, String waybill, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		if (TextUtils.isEmpty(corpid) || TextUtils.isEmpty(taskid) || TextUtils.isEmpty(storeid)
				|| TextUtils.isEmpty(regionname) || TextUtils.isEmpty(waybill) || x < 0 || y < 0 || cell < 0
				|| uid < 0) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetResult(CldOlsErrCode.PARAM_INVALID);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.uploadPostion(corpid, taskid, storeid, regioncode, regionname, x, y,
				cell, uid, waybill);
		CldHttpClient.get(request.url, ProtBase.class, new ICldResponse<ProtBase>() {

			@Override
			public void onErrorResponse(VolleyError error) {

				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase response) {

				CldSapParser.parseObject(response, ProtBase.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_uploadPostion");
				CldLog.d(TAG, errRes.errMsg + "_uploadPostion");
				handleErr(request, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 上报路线状态
	 * 
	 * @param corpid
	 * @param routeid
	 * @param naviid
	 * @param status
	 * @param x
	 * @param y
	 * @param cell
	 * @param uid
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-6 上午11:20:35
	 */
	public void uploadRoutePlanStatus(String corpid, int routeid, int naviid, int status, long x, long y, int cell,
			int uid, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		if (TextUtils.isEmpty(corpid) || x < 0 || y < 0 || cell < 0 || uid < 0) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetResult(CldOlsErrCode.PARAM_INVALID);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.uploadRoutePlanStatus(corpid, routeid, naviid, status, x, y, cell,
				uid);
		CldHttpClient.get(request.url, ProtBase.class, new ICldResponse<ProtBase>() {

			@Override
			public void onErrorResponse(VolleyError error) {

				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase response) {

				CldSapParser.parseObject(response, ProtBase.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_uploadRoutePlanStatus");
				CldLog.d(TAG, errRes.errMsg + "_uploadRoutePlanStatus");
				handleErr(request, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 请求未读运货单条数
	 * 
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-7 下午4:41:20
	 */
	public void requestUnreadTaskCount(final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.requestUnreadTaskCount();
		CldHttpClient.get(request.url, ProtGetUnReadTaskCount.class, new ICldResponse<ProtGetUnReadTaskCount>() {

			@Override
			public void onErrorResponse(VolleyError error) {

				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtGetUnReadTaskCount response) {

				CldSapParser.parseObject(response, ProtGetUnReadTaskCount.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getUnreadTaskCount");
				CldLog.d(TAG, errRes.errMsg + "_getUnreadTaskCount");
				handleErr(request, errRes);
				if (null != response) {
					if (response.count >= 0) {
						CldDalKDelivery.getInstance().setUnReadTaskCount(response.count);
					}
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 请求未完成任务数
	 * 
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-23 下午12:19:55
	 */
	public void requestUnfinishTaskCount(final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.requestUnfinishTaskCount();
		CldHttpClient.get(request.url, ProtGetUnFinishTaskCount.class, new ICldResponse<ProtGetUnFinishTaskCount>() {

			@Override
			public void onErrorResponse(VolleyError error) {

				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtGetUnFinishTaskCount response) {

				CldSapParser.parseObject(response, ProtGetUnFinishTaskCount.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_requestUnfinishTaskCount");
				CldLog.d(TAG, errRes.errMsg + "_requestUnfinishTaskCount");
				handleErr(request, errRes);
				if (null != response && response.errcode == 0 && null != response.counts) {
					if (response.counts.size() >= 0) {
						CldDalKDelivery.getInstance().saveUnFinishCountMap(response.counts);
					}
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 请求授权企业列表
	 * 
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-7 下午4:41:36
	 */
	public void requestAuthStoreList(final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.requestAuthStoreList();
		CldHttpClient.get(request.url, ProtGetAuthStoreList.class, new ICldResponse<ProtGetAuthStoreList>() {

			@Override
			public void onErrorResponse(VolleyError error) {

				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtGetAuthStoreList response) {

				CldSapParser.parseObject(response, ProtGetAuthStoreList.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getAuthStoreList");
				CldLog.d(TAG, errRes.errMsg + "_getAuthStoreList");
				handleErr(request, errRes);
				if (null != response) {
					List<CldDeliGroup> lstOfTemp = new ArrayList<CldDeliGroup>();
					response.protParse(lstOfTemp);
					CldDalKDelivery.getInstance().setLstOfAuthCorps(lstOfTemp);
				}
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 获取未完成运货单条数
	 * 
	 * @param status
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2016-5-23 下午6:01:15
	 */
	public int getUnFinishTaskCount(int status) {
		return CldDalKDelivery.getInstance().getUnFinishCountByStatus(status);
	}

	/**
	 * 请求电子围栏
	 * 
	 * @param corpid
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-24 下午12:12:36
	 */
	public void requestElectfence(String corpid, final ICldDeliGetElectfenceListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.requestElectfence(corpid);
		CldHttpClient.get(request.url, ProtGetElectfence.class, new ICldResponse<ProtGetElectfence>() {

			@Override
			public void onErrorResponse(VolleyError error) {

				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtGetElectfence response) {

				CldSapParser.parseObject(response, ProtGetElectfence.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_requestElectfence");
				CldLog.d(TAG, errRes.errMsg + "_requestElectfence");
				handleErr(request, errRes);
				if (null != response && errRes.errCode == 0) {
					List<CldElectfence> lstOfEF = new ArrayList<CldElectfence>();
					response.protParse(lstOfEF);
					if (null != listener) {
						listener.onGetResult(errRes.errCode, lstOfEF);
					}
				} else {
					if (null != listener) {
						listener.onGetResult(CldOlsErrCode.PARSE_ERR, null);
					}
				}
			}
		});
	}

	/**
	 * @annotation :获取所有企业的电子围栏
	 * @author : yuyh
	 * @date :2016-11-28下午2:44:50
	 * @parama :
	 * @return :
	 **/
	public void requestAllElectfence(final ICldDeliGetElectfenceListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.requestAllElectfence();
		CldHttpClient.get(request.url, ProtGetElectfence.class, new ICldResponse<ProtGetElectfence>() {

			@Override
			public void onErrorResponse(VolleyError error) {

				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtGetElectfence response) {

				CldSapParser.parseObject(response, ProtGetElectfence.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_requestElectfence");
				CldLog.d(TAG, errRes.errMsg + "_requestElectfence");
				handleErr(request, errRes);
				if (null != response && errRes.errCode == 0) {
					List<CldElectfence> lstOfEF = new ArrayList<CldElectfence>();
					response.protParse(lstOfEF);
					if (null != listener) {
						listener.onGetResult(errRes.errCode, lstOfEF);
					}
				} else {
					if (null != listener) {
						listener.onGetResult(CldOlsErrCode.PARSE_ERR, null);
					}
				}
			}
		});

	}

	/**
	 * 上报电子围栏
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-24 下午2:24:22
	 */
	public void uploadElectfence(CldUploadEFParm parm, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		if (null == parm || null == parm.lstOfLauchEF || parm.lstOfLauchEF.size() <= 0) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.PARAM_INVALID;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.uploadElectfenceStatus(parm.corpid, parm.x, parm.y,
				parm.lstOfLauchEF);
		CldHttpClient.post(request.url, request.mapPost, ProtBase.class, false, new ICldResponse<ProtBase>() {

			@Override
			public void onErrorResponse(VolleyError error) {

				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase response) {

				CldSapParser.parseObject(response, ProtBase.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_uploadElectfence");
				CldLog.d(TAG, errRes.errMsg + "_uploadElectfence");
				handleErr(request, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 补报电子围栏
	 * 
	 * @param parm
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-30 上午9:28:11
	 */
	public void reUploadElectfence(CldReUploadEFParm parm, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		if (null == parm || null == parm.lstOfLauchEF || parm.lstOfLauchEF.size() <= 0) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.PARAM_INVALID;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.reUploadElectfenceStatus(parm.lstOfLauchEF);
		CldHttpClient.post(request.url, request.mapPost, ProtBase.class, false, new ICldResponse<ProtBase>() {

			@Override
			public void onErrorResponse(VolleyError error) {

				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase response) {

				CldSapParser.parseObject(response, ProtBase.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_uploadElectfence");
				CldLog.d(TAG, errRes.errMsg + "_uploadElectfence");
				handleErr(request, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * @annotation :获取门店权限列表
	 * @author : yuyh
	 * @date :2016-9-22上午10:27:27
	 * @parama :
	 * @return :
	 **/
	public void getAuthInfoList(final ICldAuthInfoListener listener) {

		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.getAuthInfoList();
		CldHttpClient.get(request.url, ProtAuthInfoLst.class, new ICldResponse<ProtAuthInfoLst>() {

			@Override
			public void onErrorResponse(VolleyError error) {

				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtAuthInfoLst response) {

				CldSapParser.parseObject(response, ProtAuthInfoLst.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getAuthInfoList");
				CldLog.d(TAG, errRes.errMsg + "_getAuthInfoList");
				handleErr(request, errRes);
				if (null != response && errRes.errCode == 0) {
					List<AuthInfoList> lstOfEF = new ArrayList<AuthInfoList>();
					response.protParase(lstOfEF);
					if (null != listener) {
						listener.onGetResult(errRes.errCode, lstOfEF);
					}
				} else {
					if (null != listener) {
						listener.onGetResult(CldOlsErrCode.PARSE_ERR, null);
					}
				}
			}
		});

	}

	/**
	 * 上传货物照片或者电子回单图片
	 * 
	 * @author ligangfan
	 * @date 2017-3-27
	 * 
	 * @param corpId
	 *            企业Id
	 * @param taskId
	 *            配送任务Id
	 * @param wayBill
	 *            配送单号
	 * @param cust_order_id
	 *            客户单号
	 * @param upTime
	 *            上传时间【UTC时间】
	 * @param pic_type
	 *            照片类型（1：货物照片【默认】，2：电子回单照片）
	 * @param pic_time
	 *            拍照时间【UTC时间】
	 * @param pic_x
	 *            拍照时凯立德坐标x
	 * @param pic_y
	 *            拍照时凯立德坐标y
	 * @param base64_pic
	 *            货物图片，base64字符串
	 * @param listener
	 *            回调对象
	 */
	public void uploadDeliPicture(String corpId, String taskId, String wayBill, String cust_order_id, long upTime,
			int pic_type, long pic_time, int pic_x, int pic_y, String base64_pic, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
		}

		final CldKReturn request = CldSapKDelivery.uploadDeliPicture(corpId, taskId, wayBill, cust_order_id, pic_type,
				pic_time, pic_x, pic_y, base64_pic);
		CldHttpClient.post(request.url, request.mapPost, ProtBase.class, false, new ICldResponse<ProtBase>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				Log.e("Uploadpic", "request_ErrorResponse" + arg0.getMessage());

				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				Log.e("Uploadpic", "request_GetReqKey" + arg0);
			}

			@Override
			public void onResponse(ProtBase arg0) {


				CldSapParser.parseObject(arg0, ProtBase.class, errRes);

				Log.e("Uploadpic", "request_Responser" + arg0.errcode);
				handleErr(request, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode);
				}
			}

		});

	}

	/**
	 * 上传附件照片文件
	 * 
	 * 
	 * @param corpId
	 *            企业Id
	 * @param pic_x
	 *            拍照时凯立德坐标x
	 * @param pic_y
	 *            拍照时凯立德坐标y
	 * @param base64_pic
	 *            货物图片，base64字符串
	 * @param listener
	 *            回调对象
	 */
	public void uploadPicture(String corpId, long upTime, int pic_x, int pic_y, String base64_pic,
			final IUploadPicListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
		}

		final CldKReturn request = CldSapKDelivery.uploadPicture(corpId, upTime, pic_x, pic_y, base64_pic);
		CldHttpClient.post(request.url, request.mapPost, ProtUploadPic.class, false, new ICldResponse<ProtUploadPic>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				Log.e("Uploadpic", "request_ErrorResponse" + arg0.getMessage());

				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				Log.e("Uploadpic", "request_GetReqKey" + arg0);
			}

			@Override
			public void onResponse(ProtUploadPic arg0) {


				CldSapParser.parseObject(arg0, ProtUploadPic.class, errRes);

				Log.e("Uploadpic", "request_Responser" + arg0.errcode);
				handleErr(request, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode, arg0.mediaid);
				}
			}

		});

	}

	/**
	 * 上传货物扫描记录
	 * 
	 * @author ligangfan
	 * @date 2017-3-27
	 * 
	 * @param corpId
	 *            企业Id
	 * @param taskId
	 *            配送任务id
	 * @param wayBill
	 *            配送单号
	 * @param cust_order_id
	 *            客户单号
	 * @param bar_code
	 *            货物条形码
	 * @param upTime
	 *            扫描时间【UTC时间】
	 * @param scan_x
	 *            扫描位置x
	 * @param scan_y
	 *            扫描位置y
	 * @param listener
	 *            回调对象
	 */
	public void uploadGoodScanRecord(String corpId, String taskId, String wayBill, String cust_order_id,
			String bar_code, long upTime, int scan_x, int scan_y, final ICldResultListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
		}
		final CldKReturn request = CldSapKDelivery.uploadGoodScanRecord(corpId, taskId, wayBill, cust_order_id,
				bar_code, upTime, scan_x, scan_y);
		CldHttpClient.post(request.url, request.mapPost, ProtBase.class, false, new ICldResponse<ProtBase>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {

				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {

				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase arg0) {

				CldSapParser.parseObject(arg0, ProtBase.class, errRes);
				handleErr(request, errRes);
				if (listener != null) {

					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 获取司机驾驶车辆信息
	 * 
	 * @param taskId
	 *            配送任务ID
	 * @param corpId
	 *            物流公司ID
	 * @param listener
	 * @author zhaoqy
	 */
	public void getCarInfo(String taskId, String corpId, final ICarListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
		}

		final CldKReturn request = CldSapKDelivery.getCarInfo(taskId, corpId);
		CldLog.d(TAG, "request.url: " + request.url);
		CldHttpClient.get(request.url, ProtCar.class, new ICldResponse<ProtCar>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {
				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtCar response) {
				CldSapParser.parseObject(response, ProtCar.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getCarInfo");
				CldLog.d(TAG, errRes.errMsg + "_getCarInfo");
				handleErr(request, errRes);
				if (response != null && errRes.errCode == 0) {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, response.car);
					}
				} else {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, null);
					}
				}
			}
		});
	}

	/**
	 * 
	 * 获取行程日期列表
	 * 
	 * @param starttime
	 *            检索开始时间（时间戳）
	 * @param endtime
	 *            检索结束时间
	 * @param listener
	 * @author zhaoqy
	 */
	public void getTasks(String starttime, String endtime, final ITaskListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
			return;
		}

		final CldKReturn request = CldSapKDelivery.getTasks(starttime, endtime);
		CldLog.e("zhaoqy", "request.url: " + request.url);
		CldHttpClient.get(request.url, ProtTask.class, false, new ICldResponse<ProtTask>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {
				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtTask response) {

				CldSapParser.parseObject(response, ProtTask.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getTasks");
				CldLog.d(TAG, errRes.errMsg + "_getTasks");
				handleErr(request, errRes);
				if (response != null && errRes.errCode == 0) {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, response.tasks);
					}
				} else {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, null);
					}
				}
			}
		});
	}

	/**
	 * 获取单天行程列表
	 * 
	 * @param date
	 *            检索日期（格式：YYYY-MM-DD）

	 *            物流公司ID
	 * @param listener
	 * @author zhaoqy
	 */
	public void getCarRoutes(String date, List<MtqTask> tasks, final ICarRouteListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
		}
		final CldKReturn request = CldSapKDelivery.getCarRoutes(date, tasks);
		CldLog.e("zhaoqy", "request.url: " + request.url);
		CldHttpClient.get(request.url, ProtCarRoute.class, false, new ICldResponse<ProtCarRoute>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {
				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtCarRoute response) {
				CldSapParser.parseObject(response, ProtCarRoute.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getCarRoutes");
				CldLog.d(TAG, errRes.errMsg + "_getCarRoutes");
				handleErr(request, errRes);
				if (response != null && errRes.errCode == 0) {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, response.cars);
					}
				} else {
					if (listener != null) {
						listener.onGetResult(CldOlsErrCode.PARSE_ERR, null);
					}
				}
			}
		});
	}

	/**
	 * 获取体检历史车辆列表
	 * 
	 */
	public void getCarCheckHistoryCar(int pageIndex, int pageSize, final ICarCheckHistoryListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, -1, null);
			}
		}
		final CldKReturn request = CldSapKDelivery.getCarCheckHistory(pageSize, pageIndex);
		CldHttpClient.get(request.url, ProtCarCheckHistory.class, false, new ICldResponse<ProtCarCheckHistory>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode, -1, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {
				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtCarCheckHistory response) {
				CldSapParser.parseObject(response, ProtCarCheckHistory.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getCarRoutes");
				CldLog.d(TAG, errRes.errMsg + "_getCarRoutes");
				handleErr(request, errRes);
				if (response != null && errRes.errCode == 0) {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, response.total, response.data);
					}
				} else {
					if (listener != null) {
						listener.onGetResult(CldOlsErrCode.PARSE_ERR, -1, null);
					}
				}
			}
		});
	}

	/**
	 * 获取行程详情
	 * 
	 * @param carduid
	 *            车辆设备ID
	 * @param serialid
	 *            行程记录ID
	 * @param listener
	 * @author zhaoqy
	 */
	public void getTaskDetail(String carduid, String serialid, final ITaskDetailListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
		}
		final CldKReturn request = CldSapKDelivery.getTaskDetail(carduid, serialid);
		CldLog.e("zhaoqy", "request.url: " + request.url);
		CldHttpClient.get(request.url, ProtTaskDetail.class, false, new ICldResponse<ProtTaskDetail>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {
				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtTaskDetail response) {
				CldSapParser.parseObject(response, ProtTaskDetail.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_getTravelDetail");
				CldLog.d(TAG, errRes.errMsg + "_getTravelDetail");
				handleErr(request, errRes);
				if (response != null && errRes.errCode == 0) {
					MtqTaskDetail result = new MtqTaskDetail();
					result.navi = response.navi;
					result.tracks = response.tracks;
					if (listener != null) {
						listener.onGetResult(errRes.errCode, result);
					}
				} else {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, null);
					}
				}
			}
		});
	}

	/**
	 * 获取车辆体检历史列表
	 * 
	 *
	 * @param pagesize:每页条数（取值范围1-200，默认10）
	 * @param pageindex:页码（从1开始计算）
	 * @param listener
	 */
	public void getCarExamineHistoryList(String carlicense, int pagesize, int pageindex,
			final IExaminationHistoryListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
		}
		final CldKReturn request = CldSapKDelivery.getCarExamineList(carlicense, pagesize, pageindex);
		CldHttpClient.get(request.url, ProCarExamineHistoryList.class, false,
				new ICldResponse<ProCarExamineHistoryList>() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						CldOlsErrManager.handlerException(arg0, errRes);
						if (listener != null) {
							listener.onGetResult(errRes.errCode, null);
						}
					}

					@Override
					public void onGetReqKey(String arg0) {
						listener.onGetReqKey(arg0);
					}

					@Override
					public void onResponse(ProCarExamineHistoryList response) {
						CldSapParser.parseObject(response, ProCarExamineHistoryList.class, errRes);
						handleErr(request, errRes);
						if (response != null && errRes.errCode == 0) {
							if (listener != null) {
								listener.onGetResult(errRes.errCode, response.data);
							}
						} else {
							if (listener != null) {
								listener.onGetResult(errRes.errCode, null);
							}
						}
					}
				});
	}

	/**
	 * 2.2.9.7 获取车辆体检明细
	 * 
	 * @param carduid
	 *            车辆设备ID
	 * @param examid
	 *            体检ID（可传0）
	 * @param time
	 *            体检时间（可传0）
	 * @return
	 */
	public void getCarExamineDetail(String carduid, String examid, long time,
			final IExaminationDetailListener listener) {
		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
		}
		final CldKReturn request = CldSapKDelivery.getCarExamineDetail(carduid, examid, time);
		CldHttpClient.get(request.url, ProCarExamineDetail.class, false, new ICldResponse<ProCarExamineDetail>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {
				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProCarExamineDetail response) {
				CldSapParser.parseObject(response, ProCarExamineDetail.class, errRes);
				handleErr(request, errRes);
				if (response != null && errRes.errCode == 0) {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, response.data);
					}
				} else {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, null);
					}
				}
			}
		});
	}

	/**
	 * 3.2.7.11获取企业反馈配置信息列表
	 * 
	 * 
	 * @return
	 */
	public void getFeedBackReasonInfo(String corpid, final IGetFeedBackInfoListener listener) {

		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
		}
		final CldKReturn request = CldSapKDelivery.getFeedBackReasonInfo(corpid);
		CldHttpClient.get(request.url, ProFeedBackInfo.class, false, new ICldResponse<ProFeedBackInfo>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {
				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProFeedBackInfo response) {
				CldSapParser.parseObject(response, ProFeedBackInfo.class, errRes);
				handleErr(request, errRes);
				if (response != null && errRes.errCode == 0) {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, response.data);
					}
				} else {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, null);
					}
				}
			}
		});

	}

	/**
	 * 3.2.7.12提交反馈信息
	 * 
	 * 
	 * @return
	 */
	public void uploadFeedBackInfo(String corpid, String taskid, String waybill, String order_id, int type,long x, long y,
			String content, String remark, String mediaid, final ICldResultListener listener) {

		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
		} 
		final CldKReturn request = CldSapKDelivery.uploadFeedBackInfo(corpid, taskid, waybill, order_id,type, x, y, content,
				remark, mediaid);
		CldHttpClient.post(request.url, request.mapPost, ProtBase.class, false, new ICldResponse<ProtBase>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {
				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase response) {
				CldSapParser.parseObject(response, ProtBase.class, errRes);
				handleErr(request, errRes);
				if (response != null && errRes.errCode == 0) {
					if (listener != null) {
						listener.onGetResult(errRes.errCode);
					}
				} else {
					if (listener != null) {
						listener.onGetResult(errRes.errCode);
					}
				}
			}
		});
	}

	/**
	 * 3.2.7.13获取我的反馈
	 * 
	 * 
	 * @return
	 */
	public void getFeedBackList(String corpid, String taskid,String waybill, int page, int pagesize,
			final IGetFeedBackListListener listener) {

		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (listener != null) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null, 0);
			}
		}
		final CldKReturn request = CldSapKDelivery.getFeedBackList(corpid, taskid, waybill,page, pagesize);
		CldHttpClient.get(request.url, ProFeedBackList.class, false, new ICldResponse<ProFeedBackList>() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				CldOlsErrManager.handlerException(arg0, errRes);
				if (listener != null) {
					listener.onGetResult(errRes.errCode, null, 0);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {
				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProFeedBackList response) {
				CldSapParser.parseObject(response, ProFeedBackList.class, errRes);
				handleErr(request, errRes);
				if (response != null && errRes.errCode == 0) {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, response.data, response.count);
					}
				} else {
					if (listener != null) {
						listener.onGetResult(errRes.errCode, null, 0);
					}
				}
			}
		});
	}

	/**
	 * 3.2.7.5 批量补报运货点完成状态
	 *
	 * @param corpid 所属企业ID
	 * @param taskid 配送任务ID
	 * @param list 运货点状态集合
	 * @author Zhaoqy
	 * @date 2018-4-23 下午3:38:21
	 */
	public void batchReportTaskstoreStatus(String corpid, String taskid, List<TaskStore> list,
										   final CldKDeliveryAPI.IBatchReportTaskstoreStatusListener listener) {

		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode, null);
			}
			return;
		}
		if (null == list || list.isEmpty()) {
			// 参数不合法回调
			if (null != listener) {
				listener.onGetResult(CldOlsErrCode.PARAM_INVALID, null);
			}
			return;
		}
		final CldKReturn request = CldSapKDelivery.batchReportTaskstoreStatus(corpid, taskid, list);
		//CldLog.d(TAG,  "url: " + request.url);
		//CldLog.d(TAG,  "mapPost: " + request.mapPost);
		CldHttpClient.post(request.url, request.mapPost, ProStoreStatusResultInfo.class, false, new ICldResponse<ProStoreStatusResultInfo>() {

			@Override
			public void onErrorResponse(VolleyError error) {
				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode, null);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {
				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProStoreStatusResultInfo response) {
				CldSapParser.parseObject(response, ProtBase.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_batchReportTaskstoreStatus");
				CldLog.d(TAG, errRes.errMsg + "_batchReportTaskstoreStatus");
				handleErr(request, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode, response.result);
				}
			}
		});
	}

	public void uploadDeviceLog(final String guid, final long time, final String filename,
		final String base64_data, final CldKDeliveryAPI.IUploadDeviceLogListener listener) {

		final CldKReturn errRes = new CldKReturn();
		if (!CldPhoneNet.isNetConnected()) {
			if (null != listener) {
				errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
				listener.onGetResult(errRes.errCode);
			}
			return;
		}

		final CldKReturn request = CldSapKDelivery.uploadDeviceLog(guid, time, filename, base64_data);
		CldLog.d(TAG,  "url: " + request.url);
		CldLog.d(TAG,  "mapPost: " + request.mapPost);
		CldHttpClient.post(request.url, request.mapPost, ProtBase.class, false, new ICldResponse<ProtBase>() {

			@Override
			public void onErrorResponse(VolleyError error) {
				/** 通用异常捕获 */
				CldOlsErrManager.handlerException(error, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}

			@Override
			public void onGetReqKey(String arg0) {
				listener.onGetReqKey(arg0);
			}

			@Override
			public void onResponse(ProtBase response) {
				CldSapParser.parseObject(response, ProtBase.class, errRes);
				CldLog.d(TAG, errRes.errCode + "_uploadDeviceLog");
				CldLog.d(TAG, errRes.errMsg + "_uploadDeviceLog");
				handleErr(request, errRes);
				if (null != listener) {
					listener.onGetResult(errRes.errCode);
				}
			}
		});
	}

	/**
	 * 错误码通用处理
	 * 
	 * @param request
	 * @param errRes
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-20 下午2:21:20
	 */
	private void handleErr(CldKReturn request, CldKReturn errRes) {
		CldOlsErrManager.handleErr(request, errRes);
		if (null != request) {
			if (null != errRes) {
				errRes.errCode = fixErrCode(errRes.errCode);
			} else {
				request.errCode = fixErrCode(request.errCode);
			}
		}
	}

	/**
	 * 货运错误码修正
	 * 
	 * @param errCode
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2016-5-20 下午3:31:35
	 */
	private int fixErrCode(int errCode) {
		int res = errCode;
		switch (errCode) {
		case 1001:
			// 必填项缺少
		case 1002:
			// 参数内容格式不合法
		case 1003:
			// 企业ID不存在
		case 1005:
			// Sign签名失败
		case 1006:
			// 登录业务编码不支持
		case 1061:
			// 导航用户不存在
		case 1072:
			// 查车授权不存在
		case 1099:
			// 导航用户无权限操作
		case 1102:
			// 运货单不存在
			// 上述错误码通用为参数错误
			res = CldOlsErrCode.PARAM_INVALID;
			break;
		case 1007:
			// 帐号未登录
			res = CldOlsErrCode.ACCOUT_NOT_LOGIN;
			if (CldKAccountAPI.getInstance().isLogined()) {
				CldLog.e("ols", "session invalid!Deli!");
				CldKAccountAPI.getInstance().sessionInvalid(501, 0);
			}
			break;
		}
		return res;
	}

	private static CldBllKDelivery instance;

	public static CldBllKDelivery getInstance() {
		if (null == instance) {
			instance = new CldBllKDelivery();
		}
		return instance;
	}

	private CldBllKDelivery() {

	}


}
