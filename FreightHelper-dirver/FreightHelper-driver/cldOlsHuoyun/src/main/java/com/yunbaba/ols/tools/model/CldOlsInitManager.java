/*
 * @Title CldOlsInitManager.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-11-30 上午9:42:36
 * @version 1.0
 */
package com.yunbaba.ols.tools.model;

import java.util.ArrayList;

import com.cld.log.CldLog;
import com.cld.net.CldHttpClient;
import com.yunbaba.ols.tools.net.CldNetResponeListener;

/**
 * 初始化模块管理器
 * 
 * @author Zhouls
 * @date 2015-11-30 上午9:42:36
 */
public class CldOlsInitManager {
	private static CldOlsInitManager manager;

	public static CldOlsInitManager getInstance() {
		if (null == manager) {
			manager = new CldOlsInitManager();
		}
		return manager;
	}

	private CldOlsInitManager() {

	}

	/**
	 * 注册模块
	 * 
	 * @param model
	 *            模型模块
	 * @return void
	 * @author Wenap
	 * @date 2015-6-2 下午5:44:35
	 */
	public void regist(CldOlsInitMod model) {
		if (model != null && !mModelNameList.contains(model.getModelName())) {
			mModelList.add(model);
			mModelNameList.add(model.getModelName());
		}
	}

	/**
	 * 反注册模块
	 * 
	 * @param model
	 *            模型模块
	 * @return void
	 * @author Wenap
	 * @date 2015-6-2 下午5:44:35
	 */
	public void unRegist(CldOlsInitMod model) {
		if (model != null) {
			mModelList.remove(model);
			mModelNameList.remove(model.getModelName());
		}
	}

	/**
	 * 初始化
	 * 
	 * @return 错误码（0：成功，其它：失败）
	 * @author Wenap
	 * @date 2015-6-2 下午5:06:39
	 */
	public int init() {
		// 初始化所有注册模块
		for (CldOlsInitMod model : mModelList) {
			Object param = null;
			if (model.mInterface != null) {
				param = model.mInterface.getInitParams();
			}
			long utc = System.currentTimeMillis();
			int ret = model.init(param);
			utc = System.currentTimeMillis() - utc;
			if (0 != ret) {
				CldLog.e("init error! model name: " + model.getModelName()
						+ ", ret: " + ret);
				return ret;
			} else {
				CldLog.d("ols", model.getModelName() + ":" + utc + "ms");
			}
		}
		return 0;
	}

	/**
	 * 初始化在线部分
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2016-1-19 下午5:37:26
	 */
	public void initOnlineData() {
		// 在线部分服务开始前设置网络模块回调监听 输出日志
		CldHttpClient.setReponseListener(new CldNetResponeListener());
		for (CldOlsInitMod model : mModelList) {
			Object param = null;
			if (model.mInterface != null) {
				param = model.mInterface.getInitParams();
			}
			long utc = System.currentTimeMillis();
			model.initOnlineData(param);
			utc = System.currentTimeMillis() - utc;
			CldLog.d("ols_init", model.getModelName() + ":" + utc + "ms");
		}
	}

	private ArrayList<CldOlsInitMod> mModelList = new ArrayList<CldOlsInitMod>();
	private ArrayList<String> mModelNameList = new ArrayList<String>();
}
