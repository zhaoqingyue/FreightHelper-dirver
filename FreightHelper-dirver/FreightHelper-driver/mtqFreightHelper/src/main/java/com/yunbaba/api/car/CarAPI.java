/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: CarAPI.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.api.car
 * @Description: 车况相关接口
 * @author: zhaoqy
 * @date: 2017年4月19日 上午11:57:36
 * @version: V1.0
 */

package com.yunbaba.api.car;

import java.util.List;

import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.bean.eventbus.CompanyChangeEvent;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICarListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICarRouteListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ITaskDetailListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ITaskListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliGroup;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTask;

import org.greenrobot.eventbus.EventBus;

public class CarAPI {

	private static CarAPI mCarAPI = null;

	public static CarAPI getInstance() {
		if (mCarAPI == null) {
			synchronized (CarAPI.class) {
				if (mCarAPI == null) {
					mCarAPI = new CarAPI();
				}
			}
		}
		return mCarAPI;
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
	public void getCarInfo(String taskId, String corpId, ICarListener listener) {
		CldKDeliveryAPI.getInstance().getCarInfo(taskId, corpId, listener);
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
	public void getTasks(String starttime, String endtime, ITaskListener listener) {
		CldKDeliveryAPI.getInstance().getTasks(starttime, endtime, listener);
	}

	/**
	 * 获取单天行程列表
	 * 
	 * @param date
	 *            检索日期（格式：YYYY-MM-DD）
	 * @param taskId
	 *            配送任务ID
	 * @param corpId
	 *            物流公司ID
	 * @param listener
	 * @author zhaoqy
	 */
	public void getCarRoutes(String date, List<MtqTask> tasks, ICarRouteListener listener) {
		CldKDeliveryAPI.getInstance().getCarRoutes(date, tasks, listener);
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
	public void getTaskDetail(String carduid, String serialid, ITaskDetailListener listener) {
		CldKDeliveryAPI.getInstance().getTaskDetail(carduid, serialid, listener);
	}

	/**
	 * 获取我的车队列表
	 * 
	 * @return List<CldDeliGroup>
	 * @author zhaoqy
	 */
	public List<CldDeliGroup> getMyGroups() {
		return CldKDeliveryAPI.getInstance().getMyGroups();
	}

	public void deleteCorp(String cropid) {
		CldKDeliveryAPI.getInstance().deleteCorp(cropid);
		// EventBus.getDefault().post(new DeleteCorpEvent(cropid));

		if (MainApplication.getContext() != null) {

			CorpBean bean = SPHelper.getInstance(MainApplication.getContext()).ReadCurrentSelectCompanyBean();
			if (cropid.equals(bean.getCorpId())) {

				CorpBean cb = new CorpBean();
				SPHelper.getInstance(MainApplication.getContext()).WriteCurrentSelectCompanyBean(new CorpBean());
				EventBus.getDefault().post(new CompanyChangeEvent(cb,true,cropid));
			}
		}

	}

	/**
	 * @Title: getLockcorpid
	 * @Description: 获取锁定的企业ID
	 * @return: int
	 */
	public int getLockcorpid() {
		return CldKDeliveryAPI.getInstance().getLockcorpid();
	}

	public CldDeliGroup getCldDeliGroup(String cropid) {
		return CldKDeliveryAPI.getInstance().getCldDeliGroup(cropid);
	}
}
