/*
 * @Title CldSapKDelivery.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-12-9 上午11:34:24
 * @version 1.0
 */
package com.yunbaba.ols.module.delivery;

import android.text.TextUtils;
import android.util.Base64;

import com.cld.gson.Gson;
import com.cld.log.CldLog;
import com.yunbaba.ols.api.CldKAppCenterAPI;
import com.yunbaba.ols.api.CldKServiceAPI;
import com.yunbaba.ols.bll.CldBllUtil;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliElectFenceReUpload;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliElectFenceUpload;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTask;
import com.yunbaba.ols.module.delivery.bean.TaskStore;
import com.yunbaba.ols.module.delivery.tool.CldKDeviceAPI;
import com.yunbaba.ols.module.delivery.tool.CldOlsNetUtils;
import com.yunbaba.ols.module.delivery.tool.CldSapParser;
import com.yunbaba.ols.sap.CldSapUtil;
import com.yunbaba.ols.tools.parse.CldKReturn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 货运协议层
 * 
 * @author Zhouls
 * @date 2015-12-9 上午11:34:24
 */
public class CldSapKDelivery {

	/**
	 * 登录鉴权
	 * 
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2015-12-10 上午10:05:49
	 */
	public static CldKReturn loginAuth() {
		CldLog.e("zhaoqy", "loginAuth");
		Map<String, Object> map = getPubMap();
		//map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = CldOlsNetUtils.getGetParms(map, CldSapUtil.getNaviSvrHY() + "tis/v1/login_auth.php");
		return errRes;
	}

	/**
	 * 新增授权信息
	 * 
	 * @param mobile
	 *            手机号

	 * @param timeOut
	 *            有效期

	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:27:18
	 */
	public static CldKReturn addMonitorAuth(String mobile, String remark, long timeOut) {
		Map<String, Object> map = getPubMap();
		map.put("mobile", mobile);
		map.put("expiry_time", timeOut);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldSapParser.putStringToMap(map, "remark", remark);



		CldKReturn errRes = get(map, outMd5Source, "tis/v1/add_driver_coupon.php");
		return errRes;
	}

	/**
	 * 删除授权信息
	 * 
	 * @param id
	 *            标识id

	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:28:03
	 */
	public static CldKReturn delMonitorAuth(String id) {
		Map<String, Object> map = getPubMap();
		//map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		map.put("coupon", id);
		CldKReturn errRes = get(map, null, "tis/v1/delete_driver_coupon.php");
		return errRes;
	}

	/**
	 * 修改授权有效期
	 * 
	 * @param id
	 *            标识id
	 * @param timeOut
	 *            有效期

	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:28:25
	 */
	public static CldKReturn reviseMonitorAuthTimeOut(String id, long timeOut) {
		Map<String, Object> map = getPubMap();
	//	map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		map.put("coupon", id);
		map.put("expiry_time", timeOut);
		CldKReturn errRes = get(map, null, "tis/v1/expiry_driver_coupon.php");
		return errRes;
	}

	/**
	 * 
	 * 修改备注
	 * 
	 * @param id
	 *            标识id
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:28:43
	 */
	public static CldKReturn reviseMonitorAuthMark(String id, String remark) {
		Map<String, Object> map = getPubMap();
		map.put("coupon", id);
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		map.put("remark", remark);
		CldKReturn errRes = get(map, outMd5Source, "device/remark_driver_coupon.php");
		return errRes;
	}

	/**
	 * 获取授权信息列表
	 * 

	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:29:48
	 */
	public static CldKReturn getMonitorAuthList() {
		Map<String, Object> map = getPubMap();

	//	map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldKReturn errRes = get(map, null, "tis/v1/get_driver_coupon_list.php");
		return errRes;
	}

	/**
	 * 上报收款信息
	 * 
	 * @param corpid
	 *            企业ID（配送任务所属企业ID）
	 * @param taskid
	 *            配送任务ID
	 * @param storeid
	 *            配送任务ID
	 * @param pay_method
	 *            收款方式
	 * @param real_amount
	 *            实收金额
	 * @param return_desc
	 *            退货原因
	 * @param return_amount
	 *            退货金额
	 * @param pay_remark
	 *            收款备注、说明
	 * @param waybill
	 *            配送单号
	 * @param uploadPng
	 *            电子回单
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-22 下午2:41:39
	 */
	public static CldKReturn uploadReceipt(String corpid, String taskid, String storeid, String pay_method,
			float real_amount, String return_desc, float return_amount, String pay_remark, String waybill,
			byte[][] uploadPng, String e_receipts_0_ext) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("taskid", taskid);
		map.put("storeid", storeid);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		// if(e_receipts_0_ext != null && e_receipts_0_ext.length() > 0)
		// map.put("e_receipts_0_ext", e_receipts_0_ext);
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldSapParser.putStringToMap(map, "pay_method", pay_method);
		if (real_amount >= 0) {
			map.put("real_amount", real_amount);
		}
		CldSapParser.putStringToMap(map, "return_desc", return_desc);
		if (return_amount >= 0) {
			map.put("return_amount", return_amount);
		}
		// 图片处理
		if (null != uploadPng && uploadPng.length > 0) {
			List<String> lstOfPng = new ArrayList<String>();
			for (int i = 0; i < uploadPng.length; i++) {
				byte[] temp = uploadPng[i];
				if (null != temp) {
					String pngData = Base64.encodeToString(temp, Base64.DEFAULT);
					if (!TextUtils.isEmpty(pngData)) {
						lstOfPng.add(pngData);
					}
				}
			}

			// DebugTool.saveFile(lstOfPng.get(0));

			if (lstOfPng.size() > 0) {
				switch (lstOfPng.size()) {
				case 1:
					CldSapParser.putStringToMap(map, "e_receipts_0", lstOfPng.get(0));
					break;
				case 2:
					CldSapParser.putStringToMap(map, "e_receipts_0", lstOfPng.get(0));
					CldSapParser.putStringToMap(map, "e_receipts_1", lstOfPng.get(1));
					break;
				case 3:
					CldSapParser.putStringToMap(map, "e_receipts_0", lstOfPng.get(0));
					CldSapParser.putStringToMap(map, "e_receipts_1", lstOfPng.get(1));
					CldSapParser.putStringToMap(map, "e_receipts_2", lstOfPng.get(2));
					break;
				}
			}
		}

		CldSapParser.putStringToMap(map, "pay_remark", pay_remark);

		map.put("waybill", waybill);

		CldKReturn errRes = post(map, outMd5Source, "tis/v1/delivery_set_receipt.php");
		return errRes;
	}

	/**
	 * 搜索周边门店
	 * 
	 * @param corpid
	 * @param x
	 * @param y
	 * @param round
	 * @param page
	 * @param pagesize
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-22 下午4:16:09
	 */
	public static CldKReturn searchNearStores(String corpid, int x, int y, int round, int page, int pagesize) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		map.put("x", x);
		map.put("y", y);
		map.put("round", round);
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		map.put("page", page);
		map.put("pagesize", pagesize);
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_roundstores.php");
		return errRes;
	}

	/**
	 * 搜索门店
	 * 
	 * @param corpid
	 * @param key
	 * @param type
	 * @param page
	 * @param pagesize
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-22 下午5:35:19
	 */
	public static CldKReturn searchStores(String corpid, String key, int type, int page, int pagesize, int iscenter) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("iscenter", iscenter);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		CldSapParser.putStringToMap(map, "key", key);
		// map.put("key", key);
		map.put("type", type);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		map.put("page", page);
		map.put("pagesize", pagesize);
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_searchstores.php");
		return errRes;
	}



	public static CldKReturn getMyMarkStoreRecord(String corpid, String keyword, int iscenter, int starttime, int endtime, int status, int page, int pageSize) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		map.put("iscenter", iscenter);
		map.put("starttime", starttime);
		map.put("endtime", endtime);

		map.put("status", status);


		map.put("page", page);
		map.put("pagesize", pageSize);
		CldSapParser.putStringToMap(map, "keyword", keyword);
		// map.put("key", key);

		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_mystores.php");
		return errRes;
	}


	/**
	 * 搜索区域内无位置门店
	 * 
	 * @param corpid
	 * @param regioncity
	 * @param page
	 * @param pagesize
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-22 下午5:27:04
	 */
	public static CldKReturn searchNoPosStores(String corpid, String regioncity, int page, int pagesize, int iscenter) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("iscenter", iscenter);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldSapParser.putStringToMap(map, "regioncity", regioncity);
		map.put("page", page);
		map.put("pagesize", pagesize);
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_nposstores.php");
		return errRes;
	}

	/**
	 * 标注门店
	 * 
	 * @param corpid
	 * @param settype
	 * @param storeid
	 * @param storename
	 * @param address
	 * @param linkman
	 * @param phone
	 * @param storekcode
	 * @param remark
	 * @param uploadPng
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @param extpic
	 * @date 2016-4-22 下午6:02:15
	 */
	public static CldKReturn uploadStore(String corpid, int settype, String storeid, String storename, String address,
			String linkman, String phone, String storekcode, String remark, byte[] uploadPng, int iscenter,
			String extpic,String storecode,long uptime) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("settype", settype);
		map.put("storeid", storeid);
		CldSapParser.putStringToMap(map, "storecode", storecode);
		map.put("storename", storename);
		map.put("address", address);
		CldSapParser.putStringToMap(map, "linkman", linkman);
		CldSapParser.putStringToMap(map, "phone", phone);
		map.put("storekcode", storekcode);
		CldSapParser.putStringToMap(map, "remark", remark);

		long uptimes =  uptime == 0?CldKDeviceAPI.getSvrTime():uptime;

		if(uptimes <= 1420000000)
			uptimes =  CldKDeviceAPI.getSvrTime();

		map.put("uptime",uptimes);

		CldSapParser.putStringToMap(map, "extpic", extpic);

		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		map.put("iscenter", iscenter);

		// 图片处理
		if (null != uploadPng && uploadPng.length > 0) {
			String pngData = Base64.encodeToString(uploadPng, Base64.DEFAULT);
			if (!TextUtils.isEmpty(pngData)) {
				map.put("pic", pngData);
			}
		}

		CldKReturn errRes = post(map, outMd5Source, "tis/v1/delivery_set_address.php");
		return errRes;
	}

	/**
	 * 加入车队
	 * 
	 * @param invite_code
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-27 下午2:16:39
	 */
	public static CldKReturn joinGroup(String invite_code) {
		Map<String, Object> map = getPubMap();

	//	map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		map.put("invite_code", invite_code);
		CldKReturn errRes = get(map, null, "tis/v1/join_group.php");
		return errRes;
	}

	/**
	 * 拒绝加入车队
	 * 
	 * @param invite_code
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-27 下午2:17:21
	 */
	public static CldKReturn unJoinGroup(String invite_code) {
		Map<String, Object> map = getPubMap();

		map.put("invite_code", invite_code);


		//map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());


		CldKReturn errRes = get(map, null, "tis/v1/unjoin_group.php");
		return errRes;
	}

	/**
	 * 获取未完成运货单列表
	 * 
	 * @param corpid
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-27 下午3:39:43
	 */
	public static CldKReturn getDeliTaskList(String corpid) {
		Map<String, Object> map = getPubMap();
		// 传了ID 取对应ID的 不传取所有企业的
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldSapParser.putStringToMap(map, "corpid", corpid);
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_mission.php");
		return errRes;
	}

	/**
	 * 获取指定状态的运货单历史
	 * 
	 * @param status
	 * @param corpid
	 * @param page
	 * @param pagesize
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-27 下午3:45:10
	 */
	public static CldKReturn getDeliTaskHistoryList(String status, String corpid, String taskid,int page, int pagesize) {
		Map<String, Object> map = getPubMap();
		map.put("status", status);

		CldSapParser.putStringToMap(map, "taskid", taskid);



		map.put("page", page);
		map.put("pagesize", pagesize);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldSapParser.putStringToMap(map, "corpid", corpid);
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_tasklist.php");
		return errRes;
	}

	/**
	 * 获取运货单明细
	 * 
	 * @param corpid
	 * @param taskid
	 * @param page
	 *            运货点页码
	 * @param pagesize
	 *            运货点页容量
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-27 下午4:14:05
	 */
	public static CldKReturn getDeliTaskDetail(String corpid, String taskid, int page, int pagesize) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("taskid", taskid);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldSapParser.putIntToMap(map, "page", page);
		CldSapParser.putIntToMap(map, "pagesize", pagesize);
	//	CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_taskinfo.php");

		CldKReturn errRes = get(map, outMd5Source, "tis/v2/delivery_get_taskinfo.php");

		return errRes;
	}

	/**
	 * 更新货运单状态
	 * 
	 * @param corpid
	 * @param taskid
	 * @param status
	 * @param x
	 * @param y
	 * @param cell
	 * @param uid
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-27 下午4:24:22
	 */
	public static CldKReturn updateDeliTaskStatus(String corpid, String taskid, int status, String ecorpid,
			String etaskid, long x, long y, int cell, int uid) {
		Map<String, Object> map = getPubMap();

		map.put("corpid", corpid);
		map.put("taskid", taskid);
		map.put("status", status);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		map.put("x", x);
		map.put("y", y);
		map.put("cell", cell);
		map.put("uid", uid);
		CldSapParser.putStringToMap(map, "ecorpid", ecorpid);
		CldSapParser.putStringToMap(map, "etaskid", etaskid);

		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_set_taskstatus.php");
		return errRes;
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
	 * @param taskStatus
	 *            运货单请求更改的状态。 -1 为不请求。
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-27 下午4:26:24
	 */
	public static CldKReturn updateDeliTaskStoreStatus(String corpid, String taskid, String storeid, int status, long x,
			long y, int cell, int uid, String waybill, String ewaybill, int taskStatus) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("taskid", taskid);
		map.put("storeid", storeid);
		map.put("status", status);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		map.put("x", x);
		map.put("y", y);
		map.put("cell", cell);
		map.put("uid", uid);
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		map.put("waybill", waybill);
		CldSapParser.putStringToMap(map, "ewaybill", ewaybill);

		if (taskStatus != -1) {
			map.put("taskstatus", taskStatus);
		}

		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_set_taskstorestatus.php");
		return errRes;
	}

	/**
	 * 获取企业限行数据
	 * 
	 * @param corpid

	 * @param uid
	 * @param req
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-28 下午2:26:32
	 */
	public static CldKReturn getCorpLimitData(String corpid, int req, String uid, int minx, int miny, int maxx,
			int maxy, int tht, int twh, int twt, int tad, int tvt) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		CldSapParser.putStringToMap(map, "uid", uid);
		CldSapParser.putIntToMap(map, "minx", minx);
		CldSapParser.putIntToMap(map, "miny", miny);
		CldSapParser.putIntToMap(map, "maxx", maxx);
		CldSapParser.putIntToMap(map, "maxy", maxy);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldSapParser.putIntToMap(map, "req", req);
		CldSapParser.putIntToMap(map, "tht", tht);
		CldSapParser.putIntToMap(map, "twh", twh);
		CldSapParser.putIntToMap(map, "tad", tad);
		CldSapParser.putIntToMap(map, "tvt", tvt);
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_limitdata.php");
		return errRes;
	}

	/**
	 * 获取企业云端路线
	 * 
	 * @param isroute
	 * @param corpid
	 * @param taskid
	 * @param routeid
	 * @param naviid
	 * @param islimit
	 * @param routePlanStr
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-29 下午4:13:57
	 */
	public static CldKReturn getCorpRoutePlan(int isroute, String corpid, String taskid, int routeid, int naviid,
			int islimit, String routePlanStr) {
		Map<String, Object> map = getPubMap();
		map.put("isroute", isroute);
		map.put("corpid", corpid);
		map.put("taskid", taskid);
		map.put("routeid", routeid);
		map.put("naviid", naviid);
		map.put("islimit", islimit);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		map.remove("duid");
		map.remove("userid");
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_roadbook_2.php");
		errRes.url += "&" + routePlanStr;

		// 引擎或框架有BUG，要手动替换kuid
		StringBuilder sb = new StringBuilder(errRes.url);
		int i = sb.indexOf("userid=") + 7;
		while (sb.charAt(i) != '&') {
			sb.deleteCharAt(i);
		}
		String url = sb.toString().replace("userid=", "userid=" + CldKServiceAPI.getInstance().getKuid());
		errRes.url = url;

		CldLog.d("ols", errRes.url);
		return errRes;
	}

	/**
	 * 上报位置
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
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-5-5 下午5:55:17
	 */
	public static CldKReturn uploadPostion(String corpid, String taskid, String storeid, int regioncode,
			String regionname, long x, long y, int cell, int uid, String waybill) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("taskid", taskid);
		map.put("storeid", storeid);
		map.put("regioncode", regioncode);
		map.put("regionname", regionname);
		map.put("x", x);
		map.put("y", y);
		map.put("cell", cell);
		map.put("uid", uid);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		map.put("waybill", waybill);
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_set_region.php");
		return errRes;
	}

	/**
	 * 上报线路状态
	 * 
	 * @param corpid
	 * @param routeid
	 * @param naviid
	 * @param status
	 * @param x
	 * @param y
	 * @param cell
	 * @param uid
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-5-6 上午11:18:47
	 */
	public static CldKReturn uploadRoutePlanStatus(String corpid, int routeid, int naviid, int status, long x, long y,
			int cell, int uid) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("routeid", routeid);
		map.put("naviid", naviid);
		map.put("status", status);
		map.put("x", x);
		map.put("y", y);
		map.put("cell", cell);
		map.put("uid", uid);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_set_roadbookstatus.php");
		return errRes;
	}

	/**
	 * 获取未读运货单条数
	 * 
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-5-7 下午4:34:08
	 */
	public static CldKReturn requestUnreadTaskCount() {
		Map<String, Object> map = getPubMap();
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_taskcount_noread.php");
		return errRes;
	}

	/**
	 * 请求未完成运货单条数
	 * 
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-5-23 下午12:16:49
	 */
	public static CldKReturn requestUnfinishTaskCount() {
		Map<String, Object> map = getPubMap();
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_taskcount_nofinish.php");
		return errRes;
	}

	/**
	 * 获取电子围栏报警规则。前提条件是获取当前正在运货中的企业电子围栏，即已开始运货状态下的企业数据。
	 * 
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-5-24 上午11:20:49
	 */
	public static CldKReturn requestElectfence(String corpid) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_polygon.php");
		return errRes;
	}

	/**
	 * @annotation :获取所有企业的电子围栏
	 * @author : yuyh
	 * @date :2016-11-28下午2:47:32
	 * @parama :
	 * @return :
	 **/
	public static CldKReturn requestAllElectfence() {
		Map<String, Object> map = getPubMap();
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_all_polygon.php");
		return errRes;
	}

	/**
	 * 电子围栏报警
	 * 
	 * @param corpid
	 *            电子围栏所在企业ID

	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-5-24 上午11:28:11
	 */
	public static CldKReturn uploadElectfenceStatus(String corpid, int x, int y, List<CldDeliElectFenceUpload> lst) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		map.put("x", x);
		map.put("y", y);
		if (null != lst && lst.size() > 0) {
			String alarms = "";
			Gson gson = new Gson();
			alarms = gson.toJson(lst);
			map.put("alarms", alarms);
		}
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldKReturn errRes = post(map, outMd5Source, "tis/v1/delivery_set_alarm.php");
		return errRes;
	}

	/**
	 * 补报报警
	 * 
	 * @param lst
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-5-30 上午9:17:58
	 */
	public static CldKReturn reUploadElectfenceStatus(List<CldDeliElectFenceReUpload> lst) {
		Map<String, Object> map = getPubMap();
		if (null != lst && lst.size() > 0) {
			String alarms = "";
			Gson gson = new Gson();
			alarms = gson.toJson(lst);
			map.put("alarms", alarms);
		}
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldKReturn errRes = post(map, outMd5Source, "tis/v1/delivery_set_alarm_his.php");
		return errRes;
	}

	/**
	 * @annotation : 获取企业权限信息
	 * @author : yuyh
	 * @date :2016-9-22上午10:28:11
	 * @parama :
	 * @return :
	 **/
	public static CldKReturn getAuthInfoList() {
		Map<String, Object> map = getPubMap();
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_store_access.php");
		return errRes;
	}

	/**
	 * 获取授权企业门店列表
	 * 
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-5-7 下午4:35:03
	 */
	public static CldKReturn requestAuthStoreList() {
		Map<String, Object> map = getPubMap();
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		map.put("corpid", 0);
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_store_access.php");
		return errRes;
	}

	/**
	 * 
	 * 上传货物图片或者电子回单照片
	 * 
	 * @author ligangfan
	 * @date 2017-3-27
	 * 
	 * @param corpId
	 * @param taskId
	 * @param wayBill
	 * @param cust_order_id

	 * @param pic_type
	 * @param pic_time
	 * @param pic_x
	 * @param pic_y
	 * @param base64_pic
	 * @return
	 */
	public static CldKReturn uploadDeliPicture(String corpId, String taskId, String wayBill, String cust_order_id,
			int pic_type, long pic_time, int pic_x, int pic_y, String base64_pic) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpId);
		map.put("taskid", taskId);
		map.put("waybill", wayBill);
		//map.put("cust_orderid", cust_order_id);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		map.put("pic_type", pic_type);
		map.put("pic_time", pic_time);
		map.put("pic_x", pic_x);
		map.put("pic_y", pic_y);

		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		map.put("goods_pic", base64_pic);

		CldKReturn errRes = post(map, outMd5Source, "tis/v1/delivery_set_goods_pic.php");
		return errRes;
	}

	/**
	 * 
	 * 上传照片
	 * 
	 * @return
	 */

	public static CldKReturn uploadPicture(String corpId, long pic_time, int pic_x, int pic_y, String base64_pic) {

		Map<String, Object> map = getPubMap();
		map.put("corpid", corpId);
		map.put("time", pic_time);
		map.put("x", pic_x);
		map.put("y", pic_y);

		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		map.put("data", base64_pic);

		CldKReturn errRes = post(map, outMd5Source, "tis/v1/upload_attach_pic.php");
		return errRes;
	}

	/**
	 * 货物扫描记录上报
	 * 
	 * @param corpId
	 * @param taskId
	 * @param wayBill
	 * @param cust_order_id
	 * @param bar_code
	 * @param upTime
	 * @param scan_x
	 * @param scan_y
	 * @return
	 */
	public static CldKReturn uploadGoodScanRecord(String corpId, String taskId, String wayBill, String cust_order_id,
			String bar_code, long upTime, int scan_x, int scan_y) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpId);
		map.put("taskid", taskId);
		map.put("waybill", wayBill);
	//	map.put("cust_orderid", cust_order_id);
		map.put("bar_code", bar_code);
		map.put("uptime", CldKDeviceAPI.getSvrTime());
		map.put("scan_x", scan_x);
		map.put("scan_y", scan_y);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = post(map, outMd5Source, "tis/v1/delivery_set_scan_goods.php");
		return errRes;
	}

	/**
	 * 获取司机驾驶车辆信息
	 * 
	 * @param taskId
	 *            配送任务ID
	 * @param corpId
	 *            物流公司ID
	 * @return
	 * @author zhaoqy
	 */
	public static CldKReturn getCarInfo(String taskId, String corpId) {
		Map<String, Object> map = getPubMap();
		map.put("taskid", taskId);
		map.put("corpid", corpId);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/get_tasknavi_last.php");
		return errRes;
	}

	/**
	 * 
	 * 获取行程日期列表
	 * 
	 * @param starttime
	 *            检索开始时间（时间戳）
	 * @param endtime
	 *            检索结束时间
	 * @return
	 * @author zhaoqy
	 */
	public static CldKReturn getTasks(String starttime, String endtime) {
		Map<String, Object> map = getPubMap();
		map.put("starttime", starttime);
		map.put("endtime", endtime);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldKReturn errRes = get(map, outMd5Source, "tis/v1/delivery_get_tasknavi_datelist.php");
		return errRes;
	}

	/**
	 * 获取单天行程列表
	 * 
	 * @param date
	 *            检索日期（格式：YYYY-MM-DD）

	 * @return
	 * @author zhaoqy
	 */
	public static CldKReturn getCarRoutes(String date, List<MtqTask> tasks) {
		Map<String, Object> map = getPubMap();
		map.put("date", date);
		/**
		 * JSON格式 tasks=[{"taskid":170425362641,"corpid":44424246}]
		 */
		String string = "[";
		int len = tasks.size();
		for (int i = 0; i < len; i++) {
			string = string + "{";
			MtqTask item = tasks.get(i);
			string = string + "\"taskid\"" + ":" + item.taskid;
			string = string + ",";
			string = string + "\"corpid\"" + ":" + item.corpid;
			string = string + "}";
			if (i < len - 1) {
				string = string + ",";
			}
		}
		string = string + "]";
		map.put("tasks", string);

		/*
		 * 数组格式
		 * 
		 * List<Map<String, Object>> tasklist = new ArrayList<Map<String,
		 * Object>>(); Map<String, Object> task = null; for (MtqTask item :
		 * tasks) { task = new HashMap<String, Object>(); task.put("taskid",
		 * item.taskid); task.put("corpid", item.corpid); tasklist.add(task); }
		 * map.put("tasks", tasklist);
		 */

		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldKReturn errRes = get(map, outMd5Source, "tis/v1/get_tasknavi_list.php");
		return errRes;
	}

	/**
	 * 获取行程详情
	 * 
	 * @param carduid
	 *            车辆设备ID
	 * @param serialid
	 *            行程记录ID
	 * @return
	 * @author zhaoqy
	 */
	public static CldKReturn getTaskDetail(String carduid, String serialid) {
		Map<String, Object> map = getPubMap();
		map.put("carduid", carduid);
		map.put("serialid", serialid);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldKReturn errRes = get(map, outMd5Source, "tis/v1/get_tasknavi_detail.php");
		return errRes;
	}

	/**
	 * 获取车辆体检历史列表
	 * 

	 * @param pagesize
	 *            每页条数 (取值范围1-200，默认10）
	 * @param pageindex
	 *            页码（从1开始计算）
	 * @return
	 */
	public static CldKReturn getCarExamineList(String carlicense, int pagesize, int pageindex) {
		Map<String, Object> map = getPubMap();
		map.put("carlicense", carlicense);
		map.put("pagesize", pagesize);
		map.put("pageindex", pageindex);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/get_obdexamine_list.php");
		return errRes;
	}

	/**
	 * 2.2.10.6获取体检历史车辆
	 * 
	 * @param pagesize
	 *            每页条数 (取值范围1-200，默认10）
	 * @param pageindex
	 *            页码（从1开始计算）
	 * @return
	 */
	public static CldKReturn getCarCheckHistory(int pagesize, int pageindex) {
		Map<String, Object> map = getPubMap();
		map.put("pagesize", pagesize);
		map.put("pageindex", pageindex);
		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/get_obdexamine_car.php");
		return errRes;
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
	public static CldKReturn getCarExamineDetail(String carduid, String examid, long time) {
		Map<String, Object> map = getPubMap();
		map.put("carduid", carduid);
		map.put("examid", examid);
		map.put("time", time);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = get(map, outMd5Source, "tis/v1/get_obdexamine_detail.php");
		return errRes;
	}

	/**
	 * 3.2.7.11获取企业反馈配置信息列表
	 * 
	 * 
	 * @return
	 */
	public static CldKReturn getFeedBackReasonInfo(String corpid) {
		Map<String, Object> map = getPubMap();

		map.put("corpid", corpid);

		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldKReturn errRes = get(map, outMd5Source, "tis/v1/get_feedback_info.php");
		return errRes;
	}

	/**
	 * 3.2.7.12提交反馈信息
	 * 
	 * 
	 * @return
	 */
	public static CldKReturn uploadFeedBackInfo(String corpid, String taskid, String waybill, String order_id, int type,long x,
			long y, String content, String remark, String mediaid) {
		Map<String, Object> map = getPubMap();

		map.put("corpid", corpid);
		map.put("taskid", taskid);
		map.put("waybill", waybill);
		//map.put("order_id", order_id);
		map.put("type", type);
		map.put("x", x);
		map.put("y", y);
		CldSapParser.putStringToMap(map, "content", content);
		CldSapParser.putStringToMap(map, "remark", remark);
		CldSapParser.putStringToMap(map, "mediaid", mediaid);

		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldKReturn errRes = post(map, outMd5Source, "tis/v1/commit_feedback.php");
		return errRes;
	}
	
	
	/**
	 * 3.2.7.13获取我的反馈
	 * 
	 * 
	 * @return
	 */
	public static CldKReturn getFeedBackList(String corpid, String taskid, String waybill,int page ,int pagesize) {
		Map<String, Object> map = getPubMap();

		map.put("corpid", corpid);
		map.put("taskid", taskid);
		map.put("waybill", waybill);
		//map.put("order_id", orderid);
		map.put("page", page);
		map.put("pagesize", pagesize);
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldKReturn errRes = get(map, outMd5Source, "tis/v1/get_feedback_list.php");
		return errRes;
	}

	/**
	 * 3.2.7.5 批量补报运货点完成状态
	 *
	 * @param corpid 所属企业ID
	 * @param taskid 配送任务ID
	 * @param list 运货点状态集合
	 * @return
	 * @author Zhaoqy
	 * @date 2018-4-23 下午3:38:21
	 */
	public static CldKReturn batchReportTaskstoreStatus(String corpid, String taskid, List<TaskStore> list) {
		Map<String, Object> map = getPubMap();
		map.put("corpid", corpid);
		map.put("taskid", taskid);

		//Log.e("uploadlist",Gson)

		if (list != null && list.size() > 0) {
			String stores = "";
			Gson gson = new Gson();
			stores = gson.toJson(list);
			map.put("stores", stores);
		}
		String outMd5Source = CldSapParser.formatSource(map);

		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());

		CldKReturn errRes = post(map, outMd5Source, "tis/v2/history_set_taskstorestatus.php");
		return errRes;
	}

	public static CldKReturn uploadDeviceLog(String guid, long time, String filename,
											 String base64_data) {
		Map<String, Object> map = getPubMap();
		map.put("guid", guid);
		map.put("time", time);
		map.put("filename", filename);
		map.put("data", base64_data);

		String outMd5Source = CldSapParser.formatSource(map);
		map.put("appver", CldKAppCenterAPI.getInstance().getmVerName());
		CldKReturn errRes = post(map, outMd5Source, "tis/v1/upload_device_log.php");
		return errRes;
	}

	/**
	 * 获取公共参数map
	 * 
	 * @return
	 * @return Map<String,Object>
	 * @author Zhouls
	 * @date 2016-4-27 下午4:21:04
	 */
	public static Map<String, Object> getPubMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", CldKServiceAPI.getInstance().getKuid());
		map.put("session", CldKServiceAPI.getInstance().getSession());
		map.put("business", CldBllUtil.getInstance().getBussinessid());
		map.put("appid", CldBllUtil.getInstance().getAppid());
		map.put("apptype", CldKServiceAPI.getInstance().getApptype());
		map.put("duid",CldKServiceAPI.getInstance().getDuid());//

		return map;
	}

	/**
	 * post
	 * 
	 * @param map
	 * @param outMd5Source
	 *            外部决定参与签名的参数
	 * @param url
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-28 下午5:38:20
	 */
	private static CldKReturn post(Map<String, Object> map, String outMd5Source, String url) {
		return CldOlsNetUtils.getCustPostParms(map, outMd5Source, CldSapUtil.getNaviSvrHY() + url,
				CldDalKDelivery.getInstance().getCldDeliveryKey());
	}

	/**
	 * get
	 * 
	 * @param map
	 * @param outMd5Source
	 *            外部决定参与签名的参数
	 * @param url
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-4-28 下午5:38:26
	 */
	private static CldKReturn get(Map<String, Object> map, String outMd5Source, String url) {

		String key = CldDalKDelivery.getInstance().getCldDeliveryKey();

		return CldOlsNetUtils.getGetParms(map, outMd5Source, CldSapUtil.getNaviSvrHY() + url,
				CldDalKDelivery.getInstance().getCldDeliveryKey(), true);
	}


}
