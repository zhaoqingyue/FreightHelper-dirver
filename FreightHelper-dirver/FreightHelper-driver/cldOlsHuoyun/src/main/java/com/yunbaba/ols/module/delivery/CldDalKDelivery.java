/*
 * @Title CldDalKDelivery.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-12-9 上午10:42:32
 * @version 1.0
 */
package com.yunbaba.ols.module.delivery;

import android.text.TextUtils;

import com.cld.db.sqlite.Selector;
import com.cld.db.utils.CldDbUtils;
import com.cld.db.utils.DbException;
import com.cld.log.CldLog;
import com.yunbaba.ols.api.CldKServiceAPI;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.AuthInfoList;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliGroup;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliTaskDB;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldMonitorAuth;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParse.ProtGetUnFinishTaskCount.ProtUnFinishData;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 货运数据层
 * 
 * @author Zhouls
 * @date 2015-12-9 上午10:42:32
 */
public class CldDalKDelivery {

	private String TAG = "ols_delivery";
	private List<CldMonitorAuth> lstOfMonitorAuth;// 用户鉴权列表
	private String key;// 鉴权密钥 密文
	private String expiry_time;// 帐号鉴权截止时间
	private int state;// 状态（0为未购买服务，1为服务正常，2为服务已过期）
	private int lockcorpid; // 锁定的企业ID
	private List<CldDeliGroup> lstOfMyGroups;// 我的车队信息
	private List<CldDeliGroup> lstOfAuthCorps;// 授权门店企业列表
	private List<AuthInfoList> lstOfAuthInfo;// 授权门店企业列表

	private int unReadTaskCount;// 未读运货单条数

	private boolean isLoginAuth = false;

	private static CldDalKDelivery instance;

	public static CldDalKDelivery getInstance() {
		if (null == instance) {
			instance = new CldDalKDelivery();
		}
		return instance;
	}

	private CldDalKDelivery() {
		// 无车队 或者登录鉴权未取到结果使用默认值
		lstOfMonitorAuth = new ArrayList<CldMonitorAuth>();
		lstOfMyGroups = new ArrayList<CldDeliGroup>();
		lstOfAuthCorps = new ArrayList<CldDeliGroup>();
		lstOfAuthInfo = new ArrayList<CldSapKDeliveryParam.AuthInfoList>();
		key = "";
		state = -1;
		expiry_time = "";
		lockcorpid = 0;
		unReadTaskCount = 0;
	}

	/**
	 * 登录鉴权后清楚数据
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2016-4-22 下午2:15:19
	 */
	public void uninitData() {
		lstOfMonitorAuth = new ArrayList<CldMonitorAuth>();
		lstOfMyGroups = new ArrayList<CldDeliGroup>();
		lstOfAuthCorps = new ArrayList<CldDeliGroup>();
		lstOfAuthInfo = new ArrayList<CldSapKDeliveryParam.AuthInfoList>();
		mapOfUnFinishTaskCount = new HashMap<String, Object>();
		key = "";
		state = -1;
		expiry_time = "";
		lockcorpid = 0;
		unReadTaskCount = 0;
	}

	/**
	 * 初始化数据
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-18 下午6:52:12
	 */
	public void initData() {
		lstOfMonitorAuth = new ArrayList<CldMonitorAuth>();
		lstOfMyGroups = new ArrayList<CldDeliGroup>();
		lstOfAuthCorps = new ArrayList<CldDeliGroup>();
		lstOfAuthInfo = new ArrayList<CldSapKDeliveryParam.AuthInfoList>();
		mapOfUnFinishTaskCount = new HashMap<String, Object>();
		key = "";
		state = -1;
		expiry_time = "";
		lockcorpid = 0;
		unReadTaskCount = 0;
	}

	/**
	 * 新增到授权列表
	 * 
	 * @param bean
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:10:18
	 */
	public void addToMonitorLst(CldMonitorAuth bean) {
		if (null != lstOfMonitorAuth && null != bean) {
			if (null == getMonitor(bean.id)) {
				lstOfMonitorAuth.add(0, bean);
			} else {
				CldLog.e(TAG, "add a exsit bean!");
			}
		}
	}

	/**
	 * 获取列表后重新获取
	 * 
	 * @param lstOfBean
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 下午7:27:22
	 */
	public void reloadMonitorLst(List<CldMonitorAuth> lstOfBean) {
		if (null != lstOfBean && null != lstOfMonitorAuth) {
			lstOfMonitorAuth.clear();
			lstOfMonitorAuth.addAll(lstOfBean);
		}
	}

	/**
	 * 获取内存中授权信息列表
	 * 
	 * @return
	 * @return List<CldMonitorAuth>
	 * @author Zhouls
	 * @date 2015-12-9 下午7:29:46
	 */
	public List<CldMonitorAuth> getMonitorAuthLst() {
		List<CldMonitorAuth> lstOfBean = new ArrayList<CldMonitorAuth>();
		lstOfBean.addAll(lstOfMonitorAuth);
		return lstOfBean;
	}

	/**
	 * 修改授权信息
	 * 
	 * @param id
	 * @param mark
	 * @param timeOut
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:12:30
	 */
	public void reviseMonitor(String id, String mark, long timeOut) {
		CldMonitorAuth temp = getMonitor(id);
		if (null != temp) {
			// 有这个id可修改
			if (!TextUtils.isEmpty(mark)) {
				temp.mark = (mark);
			}
			if (timeOut > 0) {
				temp.timeOut = (timeOut);
			}
		}
	}

	/**
	 * 删除授权信息
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-12-9 上午11:14:27
	 */
	public void delMonitor(String id) {
		if (null == lstOfMonitorAuth || lstOfMonitorAuth.size() <= 0
				|| TextUtils.isEmpty(id)) {
			return;
		}
		for (int i = 0; i < lstOfMonitorAuth.size(); i++) {
			if (null == lstOfMonitorAuth.get(i)) {
				continue;
			}
			if (id.equals(lstOfMonitorAuth.get(i).id)) {
				lstOfMonitorAuth.remove(i);
			}
		}
	}

	/**
	 * 从列表中获取指定id的授权信息（有则返回，无则返回null）
	 * 
	 * @param id
	 * @return
	 * @return CldMonitorAuth
	 * @author Zhouls
	 * @date 2015-12-9 上午11:05:41
	 */
	private CldMonitorAuth getMonitor(String id) {
		if (null == lstOfMonitorAuth || lstOfMonitorAuth.size() <= 0
				|| TextUtils.isEmpty(id)) {
			return null;
		}
		for (int i = 0; i < lstOfMonitorAuth.size(); i++) {
			if (null == lstOfMonitorAuth.get(i)) {
				continue;
			}
			if (id.equals(lstOfMonitorAuth.get(i).id)) {
				return lstOfMonitorAuth.get(i);
			}
		}
		return null;
	}

	public boolean isLoginAuth() {
		return isLoginAuth;
	}

	public synchronized void setLoginAuth(boolean isLoginAuth) {
		this.isLoginAuth = isLoginAuth;
	}

	/** @return the cldDeliveryKey */
	public String getCldDeliveryKey() {
		return key;
	}

	/**
	 * @param cldDeliveryKey
	 *            the cldDeliveryKey to set
	 */
	public void setCldDeliveryKey(String cldDeliveryKey) {
		this.key = cldDeliveryKey;
	}

	/** @return the state */
	public int getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}

	/** @return the lockcorpid */
	public int getLockcorpid() {
		return lockcorpid;
	}

	/**
	 * @param lockcorpid
	 *            the lockcorpid to set
	 */
	public void setLockcorpid(int lockcorpid) {
		this.lockcorpid = lockcorpid;
	}

	/**
	 * 删除企业（收到退出车队消息后，更新企业列表）
	 * 
	 * @param cropid
	 */
	public void deleteCorp(String cropid) {
		if (lstOfMyGroups != null && !lstOfMyGroups.isEmpty()) {
			List<CldDeliGroup> temp = new ArrayList<CldDeliGroup>();
			temp.addAll(lstOfMyGroups);

			for (int i = 0; i < temp.size(); i++) {
				if (cropid.equals(temp.get(i).corpId)) {
					lstOfMyGroups.remove(i);
				}
			}
		}
	}

	public CldDeliGroup getCldDeliGroup(String cropid) {
		if (lstOfMyGroups != null && !lstOfMyGroups.isEmpty()) {
			for (int i = 0; i < lstOfMyGroups.size(); i++) {
				if (cropid.equals(lstOfMyGroups.get(i).corpId)) {
					return lstOfMyGroups.get(i);
				}
			}
		}
		return null;
	}

	/** @return the lstOfMyGroups */
	public List<CldDeliGroup> getLstOfMyGroups() {
		return lstOfMyGroups;
	}

	/**
	 * @param lstOfMyGroups
	 *            the lstOfMyGroups to set
	 */
	public void setLstOfMyGroups(List<CldDeliGroup> lstOfMyGroups) {
		this.lstOfMyGroups = lstOfMyGroups;
	}

	public List<AuthInfoList> getLstOfAuthInfo() {
		return lstOfAuthInfo;
	}

	public void setLstOfAuthInfo(List<AuthInfoList> lstOfAuthInfo) {
		this.lstOfAuthInfo = lstOfAuthInfo;
	}

	/** @return the expiry_time */
	public String getExpiry_time() {
		return expiry_time;
	}

	/**
	 * @param expiry_time
	 *            the expiry_time to set
	 */
	public void setExpiry_time(String expiry_time) {
		this.expiry_time = expiry_time;
	}

	/** @return the lstOfAuthCorps */
	public List<CldDeliGroup> getLstOfAuthCorps() {
		return lstOfAuthCorps;
	}

	/**
	 * @param lstOfAuthCorps
	 *            the lstOfAuthCorps to set
	 */
	public void setLstOfAuthCorps(List<CldDeliGroup> lstOfAuthCorps) {
		this.lstOfAuthCorps = lstOfAuthCorps;
	}

	/** @return the unReadTaskCount */
	public int getUnReadTaskCount() {
		return unReadTaskCount;
	}

	/**
	 * @param unReadTaskCount
	 *            the unReadTaskCount to set
	 */
	public void setUnReadTaskCount(int unReadTaskCount) {
		this.unReadTaskCount = unReadTaskCount;
	}

	/**
	 * 取运货单时同步阅读状态
	 * 
	 * @param lstOfDeliTask
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-18 下午6:16:35
	 */
	public void syncTaskReadStatus(List<MtqDeliTask> lstOfDeliTask) {
		if (null == lstOfDeliTask || lstOfDeliTask.size() <= 0) {
			return;
		}
		long kuid = CldKServiceAPI.getInstance().getKuid();
		if (kuid <= 0) {
			return;
		}
		List<CldDeliTaskDB> newStatus = new ArrayList<CldDeliTaskDB>();
		// 遍历比对 同步本地数据
		for (int i = 0; i < lstOfDeliTask.size(); i++) {
			CldDeliTaskDB newDB = new CldDeliTaskDB();
			MtqDeliTask temp = lstOfDeliTask.get(i);
			if (null == temp) {
				continue;
			}
			long id = 0;
			try {
				long corp = Long.parseLong(temp.corpid);
				long task = Long.parseLong(temp.taskid);
				id = (corp << 32) + task;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			if (id == 0) {
				continue;
			}
			List<CldDeliTaskDB> lstOfLocal = getLocTaskReadCach(kuid);
			if (null != lstOfLocal && lstOfLocal.size() > 0) {
				// 每条新数据 都要判断本地是否有已读状态，如果已读，将数据中的状态改掉
				for (int j = 0; j < lstOfLocal.size(); j++) {
					long locId = lstOfLocal.get(j).id;
					int locStatus = lstOfLocal.get(j).status;
					// 逻辑待定
					if (id == locId && temp.readstatus == 0 && locStatus == 1) {
						// 发现本地已读 设置该运货单已读
						temp.readstatus = 1;
					}
				}
			}
			newDB.id = id;
			newDB.kuid = kuid;
			newDB.status = temp.readstatus;
			newStatus.add(newDB);
		}
		CldDbUtils.deleteAll(CldDeliTaskDB.class);
		CldDbUtils.saveAll(newStatus);
	}

	/**
	 * 更新运货单失败的时候将阅读状态存入本地
	 * 
	 * @param corpid
	 * @param taskid
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-18 下午6:22:32
	 */
	public void saveTaskSatus(String corpid, String taskid) {
		// try {
		// long corp = Long.parseLong(corpid);
		// long task = Long.parseLong(taskid);
		// CldDeliTaskDB db = new CldDeliTaskDB();
		// db.id = (corp << 32) + task;
		// db.kuid = CldKServiceAPI.getInstance().getKuid();
		// db.status = 1;
		// CldDbUtils.save(db);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * 获取本地未读运货单条数
	 * 
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2016-5-18 下午6:28:43
	 */
	public int getLocUnreadTaskCount(long kuid) {
		int count = 0;
		try {
			count = (int) CldDbUtils.getDbInstance().count(
					Selector.from(CldDeliTaskDB.class).where("kuid", "=", kuid)
							.and("status", "=", 0));
			if (count > 50) {
				CldLog.e("ols", "too much task hasn't sync!");
			}
		} catch (DbException e) {

			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 获取指定Kuid本地任务单的状态
	 * 
	 * @param kuid
	 * @return
	 * @return List<CldDeliTaskReadStatusDB>
	 * @author Zhouls
	 * @date 2016-5-7 下午5:36:29
	 */
	public List<CldDeliTaskDB> getLocTaskReadCach(long kuid) {
		List<CldDeliTaskDB> lst = null;
		try {
			lst = CldDbUtils.getDbInstance()
					.findAll(
							Selector.from(CldDeliTaskDB.class).where("kuid",
									"=", kuid));
		} catch (DbException e) {

			e.printStackTrace();
		}
		return lst;
	}

	/** 未完成运货单数量 */
	private Map<String, Object> mapOfUnFinishTaskCount;

	/**
	 * 存储未完成运货单数量
	 * 
	 * @param lstOfData
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-23 下午5:58:45
	 */
	public void saveUnFinishCountMap(List<ProtUnFinishData> lstOfData) {
		if (null != mapOfUnFinishTaskCount && null != lstOfData
				&& lstOfData.size() > 0) {
			for (int i = 0; i < lstOfData.size(); i++) {
				ProtUnFinishData temp = lstOfData.get(i);
				if (null == temp) {
					continue;
				}
				mapOfUnFinishTaskCount.put(temp.status + "", temp.count);
			}
		}
	}

	/**
	 * 获取未完成运货单数量
	 * 
	 * @param status
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2016-5-23 下午5:58:13
	 */
	public int getUnFinishCountByStatus(int status) {
		if (null == mapOfUnFinishTaskCount) {
			CldLog.e("ols", "mapOfUnFinishTaskCount is null!");
			return 0;
		}
		if (mapOfUnFinishTaskCount.containsKey(status + "")) {
			try {
				int count = (Integer) mapOfUnFinishTaskCount.get(status + "");
				return count;
			} catch (NumberFormatException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return 0;
	}
}
