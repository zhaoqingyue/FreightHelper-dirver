/*
 * @Title CldKConfigAPI.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.ols.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yunbaba.ols.api.CldOlsBase.IInitListener;
import com.yunbaba.ols.bll.CldKConfig;
import com.yunbaba.ols.bll.CldKConfig.ConfigDomainType;



/**
 * 终端控制相关模块，提供更新配置等功能
 * 
 * @author Zhouls
 * @date 2015-3-5 下午3:23:42
 */
public class CldKConfigAPI {

	private static CldKConfigAPI cldKConfigAPI;

	private CldKConfigAPI() {

	}

	public static CldKConfigAPI getInstance() {
		if (null == cldKConfigAPI) {
			cldKConfigAPI = new CldKConfigAPI();
		}
		return cldKConfigAPI;
	}

	/**
	 * 初始化默认,本地配置
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:24:18
	 */
	public void initDefConfig() {
		CldKConfig.getInstance().initDefConfig();
	}

	/**
	 * 更新配置
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:24:28
	 */
	public void updateCofig(final IInitListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				boolean isUpdate = CldKConfig.getInstance().updateCofig();
				if (null != listener && isUpdate) {
					listener.onUpdateConfig();
				}
			}
		}).start();
	}

	/**
	 * 获取域名头
	 * 
	 * @param type
	 *            CofigDomainType
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-5 下午3:24:43
	 */
	public String getSvrDomain(int type) {
		return CldKConfig.getInstance().getSvrDomain(type);
	}

	/**
	 * 获取服务开关
	 * 
	 * @param type
	 *            ConfigSwitchType
	 * @return 0关，1开
	 * @return int
	 * @author Zhouls
	 * @date 2015-3-5 下午3:24:58
	 */
	public int getSvrSwitch(int type) {
		return CldKConfig.getInstance().getSvrSwitch(type);
	}

	/**
	 * 获取WebUrl
	 * 
	 * @param type
	 *            ConfigWebUrlType
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-5 下午3:25:20
	 */
	public String getWebUrl(int type) {
		return CldKConfig.getInstance().getWebUrl(type);
	}

	/**
	 * 获取服务端频率相关
	 * 
	 * @param type
	 *            ConfigRateType
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-3-5 下午3:25:34
	 */
	public int getSvrRate(int type) {
		return CldKConfig.getInstance().getSvrRate(type);
	}

	/**
	 * 是否是白名单用户
	 * 
	 * @param kuid
	 *            the kuid
	 * @return
	 * @return boolean
	 * @author Zhouls
	 * @date 2015-3-5 下午3:25:52
	 */
	public boolean isInWhiteList(long kuid) {
		return CldKConfig.getInstance().isInWhiteList(kuid);
	}

	/**
	 * 是否是手机号
	 * 
	 * @param phone
	 *            传入手机号
	 * @return
	 * @return boolean
	 * @author Zhouls
	 * @date 2015-3-5 下午3:26:07
	 */
	public boolean isPhoneNum(String phone) {
		Pattern p = Pattern.compile(CldKConfig.getInstance().getSvrDomain(
				ConfigDomainType.REG_EXPRESS));
		Matcher m = p.matcher(phone);
		return m.matches();
	}
}
