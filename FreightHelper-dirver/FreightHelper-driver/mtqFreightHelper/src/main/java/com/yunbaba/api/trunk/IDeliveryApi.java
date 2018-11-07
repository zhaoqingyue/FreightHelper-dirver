package com.yunbaba.api.trunk;

import android.app.Activity;
import android.content.Context;

import com.yunbaba.api.trunk.bean.GetTaskListResult;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliReceiptParm;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;

/**
 * 定义封装货运api的接口类
 */
public interface IDeliveryApi {

	/**
	 * 获取未完成运货单列表
	 */
	void getUnfinishTaskInServer(Activity activity,String corpid, OnResponseResult<GetTaskListResult> callBack);

	/**
	 * 从服务器获取历史运货单
	 * 
	 * @param status
	 *            运货状态【0待运货1运货中2已完成3暂停状态4中止状态 】，用”|”分隔支持获取多个状态获取。
	 * @param corpid
	 *            指定企业ID（不指定返回所有加入企业的数据）
	 * @param page
	 *            页码(默认1）
	 * @param pagesize
	 *            每页条数(默认10)

	 * @return void
	 */
	void getHisTaskInServer(String status, String corpid,String taskid, int page, int pagesize,
			OnResponseResult<GetTaskListResult> callBack);

	/**
	 * 根据运货单ID从服务器取运货单详情
	 * 
	 * @param taskId
	 */
	void getTaskDetailInServer(String taskId, String corpId, OnResponseResult<MtqDeliTaskDetail> callBack);

	/**
//	 * 更新/同步运货单状态
//	 */
//	void UpdateTaskInfo(CldDelUpTask upTask, OnResponseResult<CldDelUpTask> callBack);

//	void UpdateTaskInfo(String corpid, String taskid, int status, String ecorpid, String etaskid, long x, long y,
//			int cell, int uid, OnResponseResult<UpdateTaskStatusResult> callBack);

//	/**
//	 * 更新运货点的状态
//	 * 
//	 * @param upTask
//	 */
//	void UpdateStoreStatus(CldDelUpTask upTask, OnResponseResult<CldDelUpTask> callBack);
	
//	void UpdateStoreStatus(Context context, String corpId, String taskId, String nodeId, int status, String waybill, String ewaybill,
//						   OnResponseResult<UpdateTaskPointStatusResult> callBack, String cu_orderid);
//
//	/**
//	 * 函数注释：网络通畅时取缓存中的上传失败队列，上传到服务器
//	 * 
//	 * @return void
//	 * @author chenjq
//	 * @date 2015-3-21 上午11:58:35
//	 */
//	void retryUpTaskOfUnSucc(OnResponseResult<Integer> callBack);

	/**
	 * 上传收款信息
	 * 
	 * localPicPath 需要上传的本地图片地址列表
	 */
	void UpLoadPayInfo(Context context, CldDeliReceiptParm deliReceiptParm, OnResponseResult<Integer> callBack);

	/**
	 * 送货过程中，在开始送货时需上传其所在区域信息(改变运货单状态实现)，同时在终端行政区位置发生变化时，如从深圳进入东莞时再次上传一下终端的位置信息。
	 * 

	 * @return void
	 * @date 2016-5-5 下午6:04:33
	 */
	void UpLoadPosition(OnResponseResult<Integer> callBack);

//	/**
//	 * 上报线路状态
//	 * 
//	 * @param corpid
//	 *            企业ID
//	 * @param routeid
//	 *            推荐线路ID
//	 * @param naviid
//	 *            使用推荐线路导航ID
//	 * @param status
//	 *            0不用推荐线路导航 1使用推荐线路导航 6导航结束(到达目的地)7导航结束(没到目的地) 这期只报 (0 1 6 7)
//	 * @param x
//	 *            上报位置X
//	 * @param y
//	 *            上报位置y
//	 * @param cell
//	 *            道路图幅ID
//	 * @param uid
//	 *            道路UID
//	 * @param listener
//	 * @return void
//	 */
//	void UpLoadRoutePlanStatus(UploadRoutePlanParm uploadRoutePlanParm, OnResponseResult<Integer> callBack);

	/**
	 * 偏航,导航结束时上传位置 status 0不用推荐线路导航 1使用推荐线路导航 6导航结束(到达目的地)7导航结束(没到目的地) 这期只报 (0
	 * 1 6 7)
	 */
	void onYaWingOrRouteFinishUpLoad(int status, OnResponseResult<Integer> callBack);

	/**
	 * 上传货物扫描记录
	 * */
	
	void UpLoadGoodScanRecord(String corpId, String taskId,String wayBill, String cust_order_id, 
			String bar_code, long upTime, int scan_x, int scan_y, OnResponseResult<Integer> callBack);
	
	
	/**
	 * 上传货物图片
	 * */
	
	void UpLoadDeliPicture(String corpId, String taskId, String wayBill, String cust_order_id, 
			long upTime, int pic_type, long pic_time, int pic_x, int pic_y, String base64_pic, OnResponseResult<Integer> callBack);

	// void GetGoodCodeList();

}
