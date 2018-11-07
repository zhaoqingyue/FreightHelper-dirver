/*
 * @Title CldKConfig.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.ols.bll;

import android.text.TextUtils;

import com.cld.device.CldPhoneNet;
import com.cld.log.CldLog;
import com.cld.setting.CldSetting;
import com.yunbaba.ols.dal.CldDalKConfig;
import com.yunbaba.ols.sap.CldSapKConfig;
import com.yunbaba.ols.sap.CldSapUtil;
import com.yunbaba.ols.sap.parse.CldKConfigParse.ProtConfig;
import com.yunbaba.ols.tools.CldErrUtil;
import com.yunbaba.ols.tools.CldSapNetUtil;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;



/**
 * 
 * 终端配置
 * 
 * @author Zhouls
 * @date 2015-4-8 下午4:15:11
 */
public class CldKConfig {

	private static CldKConfig cldConfig;

	/**
	 * Instantiates a new cld k config.
	 */
	private CldKConfig() {

	}

	/**
	 * Gets the single instance of CldKConfig.
	 * 
	 * @return single instance of CldKConfig
	 */
	public static CldKConfig getInstance() {
		if (null == cldConfig) {
			cldConfig = new CldKConfig();
		}
		return cldConfig;
	}

	/**
	 * 每次开启导航读取本地JSON配置， 解析到Data层里
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 下午4:16:00
	 */
	public void initDefConfig() {
		readConfigFromDefault();
		readConfigFromLocal();
	}

	/**
	 * 有更新将更新解析到data，并保存到本地JSON配置
	 * 
	 * @return
	 * @return boolean
	 * @author Zhouls
	 * @date 2015-4-8 下午4:16:38
	 */
	public boolean updateCofig() {
		if (0 == readConfigFromWeb().errCode) {
			/**
			 * 服务端返回errCode 0说明有更新，保存配置
			 */
			writeCofigToLocal();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 从本地私有存储区读取配置
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 下午4:16:50
	 */
	public void readConfigFromDefault() {
		int locVersion = CldSetting.getInt("ols_version_type", 0);
		switch (locVersion) {
		case 0:
			if (CldBllUtil.getInstance().getVersionType() == 1) {
				readDebVersion();
			} else {
				readRelVersion();
			}
			break;
		case 1:
			if (CldBllUtil.getInstance().getVersionType() == 2) {
				cleanConfig();
				readRelVersion();
			} else {
				readDebVersion();
			}
			break;
		case 2:
			if (CldBllUtil.getInstance().getVersionType() == 1) {
				cleanConfig();
				readDebVersion();
			} else {
				readRelVersion();
			}
			break;
		default:
			if (CldBllUtil.getInstance().getVersionType() == 1) {
				readDebVersion();
			} else {
				readRelVersion();
			}
			break;
		}
	}

	/**
	 * 读测试版配置
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-20 上午9:28:52
	 */
	public void readDebVersion() {
		String strData;
		InputStream in;
		CldSetting.put("ols_version_type", 1);
		/**
		 * 测试版
		 */
		try {
			/**
			 * classtype=1001001100配置项
			 */
			in = CldBllUtil.getInstance().getContext().getAssets()
					.open("config/deb/1001001100.cfg");
			strData = CldSapUtil.getStringFromFileIn(in);
			CldDalKConfig.getInstance().getDomainConfig().protParse(strData);
			/**
			 * classtype=1001003000配置项
			 */
			in = CldBllUtil.getInstance().getContext().getAssets()
					.open("config/deb/1001003000.cfg");
			strData = CldSapUtil.getStringFromFileIn(in);
			CldDalKConfig.getInstance().getWebUrlConfig().protParse(strData);
			/**
			 * classtype=1003001200配置项
			 */
			in = CldBllUtil.getInstance().getContext().getAssets()
					.open("config/deb/1003001200.cfg");
			strData = CldSapUtil.getStringFromFileIn(in);
			CldDalKConfig.getInstance().getThirdPartConfig().protParse(strData);
			/**
			 * classtype=2002001000配置项
			 */
			in = CldBllUtil.getInstance().getContext().getAssets()
					.open("config/deb/2002001000.cfg");
			strData = CldSapUtil.getStringFromFileIn(in);
			CldDalKConfig.getInstance().getPosReportConfig().protParse(strData);
			/**
			 * classtype=2004001000配置项
			 */
			in = CldBllUtil.getInstance().getContext().getAssets()
					.open("config/deb/2004001000.cfg");
			strData = CldSapUtil.getStringFromFileIn(in);
			CldDalKConfig.getInstance().getKcCloundConfig().protParse(strData);
			CldLog.i("ols", "initDeb");
		} catch (IOException e) {
			CldLog.w("ols", "initExc");
		}
	}

	/**
	 * 读正式版配置
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-20 上午9:29:08
	 */
	public void readRelVersion() {
		CldSetting.put("ols_version_type", 2);
		String strData;
		InputStream in;
		/**
		 * 正式版
		 */
		try {
			/**
			 * classtype=1001001000配置项
			 */
			in = CldBllUtil.getInstance().getContext().getAssets()
					.open("config/rel/1001001100.cfg");
			strData = CldSapUtil.getStringFromFileIn(in);
			CldDalKConfig.getInstance().getDomainConfig().protParse(strData);
			/**
			 * classtype=1001003000配置项
			 */
			in = CldBllUtil.getInstance().getContext().getAssets()
					.open("config/rel/1001003000.cfg");
			strData = CldSapUtil.getStringFromFileIn(in);
			CldDalKConfig.getInstance().getWebUrlConfig().protParse(strData);
			/**
			 * classtype=1003001200配置项
			 */
			in = CldBllUtil.getInstance().getContext().getAssets()
					.open("config/rel/1003001200.cfg");
			strData = CldSapUtil.getStringFromFileIn(in);
			CldDalKConfig.getInstance().getThirdPartConfig().protParse(strData);
			/**
			 * classtype=2002001000配置项
			 */
			in = CldBllUtil.getInstance().getContext().getAssets()
					.open("config/rel/2002001000.cfg");
			strData = CldSapUtil.getStringFromFileIn(in);
			CldDalKConfig.getInstance().getPosReportConfig().protParse(strData);
			/**
			 * classtype=2004001000配置项
			 */
			in = CldBllUtil.getInstance().getContext().getAssets()
					.open("config/rel/2004001000.cfg");
			strData = CldSapUtil.getStringFromFileIn(in);
			CldDalKConfig.getInstance().getKcCloundConfig().protParse(strData);
			CldLog.i("ols", "initRel");
		} catch (IOException e) {
			CldLog.w("ols", "initExc");
		}
	}

	/**
	 * 清空配置
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-20 上午9:29:23
	 */
	public void cleanConfig() {
		CldSetting.remove("1001001000");
		CldSetting.remove("1001001100");
		CldSetting.remove("1001003000");
		CldSetting.remove("1003001200");
		CldSetting.remove("2002001000");
		CldSetting.remove("2004001000");
	//	CldSetting.remove("duid");
	//	CldSetting.remove("MD5duid");
	//	CldSetting.remove("ols_ka_pwdtype");
		CldSetting.remove("CldKAKey");
		CldSetting.remove("CldKMKey");
		CldSetting.remove("CldKCallKey");
		CldSetting.remove("CldKPKey");
		CldSetting.remove("ols_CldKPUBKey");
		CldSetting.remove("ols_AKeyCall_mobiles");
	}

	/**
	 * 从本地私有存储区取数据
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 下午4:17:11
	 */
	public void readConfigFromLocal() {
		String strData;
		strData = CldSetting.getString("1001001100");
		if (TextUtils.isEmpty(strData)) {
			/**
			 * 本地无配置文件 不处理
			 */
			CldLog.d("ols", "loc-empty-1");
		} else {
			CldDalKConfig.getInstance().getDomainConfig().protParse(strData);
		}
		strData = CldSetting.getString("1001003000");
		if (TextUtils.isEmpty(strData)) {
			/**
			 * 本地无配置文件 不处理
			 */
			CldLog.d("ols", "loc-empty-2");
		} else {
			CldDalKConfig.getInstance().getWebUrlConfig().protParse(strData);
		}
		strData = CldSetting.getString("1003001200");
		if (TextUtils.isEmpty(strData)) {
			/**
			 * 本地无配置文件 不处理
			 */
			CldLog.d("ols", "loc-empty-3");
		} else {
			CldDalKConfig.getInstance().getThirdPartConfig().protParse(strData);
		}
		strData = CldSetting.getString("2002001000");
		if (TextUtils.isEmpty(strData)) {
			/**
			 * 本地无配置文件 不处理
			 */
			CldLog.d("ols", "loc-empty-4");
		} else {
			CldDalKConfig.getInstance().getPosReportConfig().protParse(strData);
		}
		strData = CldSetting.getString("2004001000");
		if (TextUtils.isEmpty(strData)) {
			/**
			 * 本地无配置文件 不处理
			 */
			CldLog.d("ols", "loc-empty-5");
		} else {
			CldDalKConfig.getInstance().getKcCloundConfig().protParse(strData);
		}
	}

	/**
	 * 从服务器请求配置（从之前获取的默认或者本地配置取version）
	 * 
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:17:22
	 */
	public CldSapReturn readConfigFromWeb() {
		String classtypes = "";
		CldSapReturn errRes = new CldSapReturn();
		/**
		 * 获取Data层目前配置的version，拼成classtypes，向服务端请求配置
		 */
		if (CldPhoneNet.isNetConnected()) {
			classtypes = "1001001100"
					+ ","
					+ CldDalKConfig.getInstance().getDomainConfig()
							.getVersion()
					+ "|"
					+ "1001003000"
					+ ","
					+ CldDalKConfig.getInstance().getWebUrlConfig()
							.getVersion()
					+ "|"
					+ "1003001200"
					+ ","
					+ CldDalKConfig.getInstance().getThirdPartConfig()
							.getVersion()
					+ "|"
					+ "2002001000"
					+ ","
					+ CldDalKConfig.getInstance().getPosReportConfig()
							.getVersion()
					+ "|"
					+ "2004001000"
					+ ","
					+ CldDalKConfig.getInstance().getKcCloundConfig()
							.getVersion();
			errRes = CldSapKConfig.downloadControlList(classtypes);
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			ProtConfig protConfig = CldSapParser.parseJson(strRtn,
					ProtConfig.class, errRes);
			if (null != protConfig) {
				CldDalKConfig.getInstance().loadConfig(protConfig);
				if (null != protConfig.getItem()) {
					if (protConfig.getItem().size() == 0) {
						errRes.errCode = 110;
						errRes.errMsg = "无配置更新";
					}
				}
			}
			CldLog.d("ols", errRes.errCode + "_dlConfig");
			CldLog.d("ols", errRes.errMsg + "_dlConfig");
			CldErrUtil.handleErr(errRes);
		}
		return errRes;
	}

	/**
	 * 将服务端获取的配置写入私有存储区
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 下午4:17:34
	 */
	public void writeCofigToLocal() {
		Map<String, String> map = CldDalKConfig.getInstance().getJsonMap();
		if (!TextUtils.isEmpty(map.get("1001001100"))) {
			CldSetting.put("1001001100", map.get("1001001100"));
		}
		if (!TextUtils.isEmpty(map.get("1001003000"))) {
			CldSetting.put("1001003000", map.get("1001003000"));
		}
		if (!TextUtils.isEmpty(map.get("1003001200"))) {
			CldSetting.put("1003001200", map.get("1003001200"));
		}
		if (!TextUtils.isEmpty(map.get("2002001000"))) {
			CldSetting.put("2002001000", map.get("2002001000"));
		}
		if (!TextUtils.isEmpty(map.get("2004001000"))) {
			CldSetting.put("2004001000", map.get("2004001000"));
		}
	}

	/**
	 * 获取服务端域名
	 * 
	 * @param type
	 *            CofigDomainType
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-4-8 下午4:17:47
	 */
	public String getSvrDomain(int type) {
		switch (type) {
		case ConfigDomainType.NAVI_SVR_PUB:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_cmpub();
		case ConfigDomainType.NAVI_SVR_KACCOUNT:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_kaccount();
		case ConfigDomainType.NAVI_SVR_MSG:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_msg();
		case ConfigDomainType.NAVI_SVR_BD:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_bd();
		case ConfigDomainType.NAVI_SMS_NUM_CMCC:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSms_num_cmcc();
		case ConfigDomainType.NAVI_SMS_NUM_CTCC:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSms_num_ctcc();
		case ConfigDomainType.NAVI_SMS_NUM_CUCC:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSms_num_cucc();
		case ConfigDomainType.NAVI_SVR_ONLINENAVI:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_onlinenavi();
		case ConfigDomainType.NAVI_SVR_WEBSITE:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_website();
		case ConfigDomainType.NAVI_SVR_PPTCALL:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_ppt();
		case ConfigDomainType.NAVI_SVR_POS:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_pos();
		case ConfigDomainType.PHONE_HDSC:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getPhone_hdsc();
		case ConfigDomainType.REG_EXPRESS:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getReg_express();
		case ConfigDomainType.NAVI_SVR_AC:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_authcheck();
		case ConfigDomainType.NAVI_SVR_KC:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_kc();
		case ConfigDomainType.NAVI_SVR_RTI:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_rti();
		case ConfigDomainType.NAVI_SVR_KWEATHER:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_kweather();
		case ConfigDomainType.NAVI_SVR_SEARCH:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_ksearch();
		case ConfigDomainType.NAVI_SVR_HY:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_khygroup();

		case ConfigDomainType.NAVI_SVR_HY_DOWNLOAD:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_hy_download();
		case ConfigDomainType.NAVI_SVR_PO:
			return CldDalKConfig.getInstance().getDomainConfig()
					.getConfigitem().getSvr_kfeedback();
		default:
			return "";
		}
	}

	/**
	 * 获取WebUrl
	 * 
	 * @param type
	 *            ConfigWebUrlType
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-4-8 下午4:18:01
	 */
	public String getWebUrl(int type) {
		switch (type) {
		case ConfigWebUrlType.CONFIG_URL_KB2KBEAN:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_kb2kbean();
		case ConfigWebUrlType.CONFIG_URL_KBEANDETAIL:
			return "";
		case ConfigWebUrlType.CONFIG_URL_KBEANREMARK:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_kbeanremark();
		case ConfigWebUrlType.CONFIG_URL_KBEANHELP:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_kbeanhelp();
		case ConfigWebUrlType.CONFIG_URL_KBRECHARGE:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_kbrecharge();
		case ConfigWebUrlType.CONFIG_URL_KBHELP:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_kbhelp();
		case ConfigWebUrlType.CONFIG_URL_PAYMONTHLY:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_paymonthly();
		case ConfigWebUrlType.CONFIG_URL_TEAMBUY:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_teambuy();
		case ConfigWebUrlType.CONFIG_URL_COUPON:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_coupon();
		case ConfigWebUrlType.CONFIG_URL_HOTEL:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_hotel();
		case ConfigWebUrlType.CONFIG_URL_APPLIST:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_applist();
		case ConfigWebUrlType.CONFIG_URL_GETACTIVITYCODE:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_getactivitycode();
		case ConfigWebUrlType.CONFIG_URL_EMAILREGISTER:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_emailregister();
		case ConfigWebUrlType.CONFIG_URL_PIONEER:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_pioneer();
		case ConfigWebUrlType.CONFIG_URL_HUD:
			return CldDalKConfig.getInstance().getWebUrlConfig()
					.getConfigitem().getUrl_hud_buy();
		default:
			return "";
		}
	}

	/**
	 * 获取服务端开关
	 * 
	 * @param type
	 *            ConfigSwitchType
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-4-8 下午4:18:17
	 */
	public int getSvrSwitch(int type) {
		switch (type) {
		case ConfigSwitchType.CONFIG_SWITCH_BAIDUSEARCH:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getBaidusearch().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_BAIDULOCATE:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getBaidulocate().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_SHOULEI:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getShoulei().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_YOUMENGTOTAL:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getYoumengtotal().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_YOUMENGSHARE:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getYoumengshare().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_ONEKEYCALL:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getOnekeycall().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_PIONEER:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getPioneer().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_GROUPBUY:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getCoupon().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_COUPON:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getCoupon().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_ORDERHOTEL:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getOrderhotel().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_PROJECTMODE:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getProjectmode().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_HUD:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getHud().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_ZXNJ:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getZxnj().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_DRIVESERVICE:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getDrivingservice().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_CAREVALATE:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getCarevalate().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_AUTOINSURANCE:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getAutoinsurance().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_VIOLATION:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getViolation().getOpen();
		case ConfigSwitchType.CONFIG_SWITCH_GPSLOG:
			return CldDalKConfig.getInstance().getThirdPartConfig()
					.getConfigitem().getGpslog().getOpen();
		default:
			return 0;
		}
	}

	/**
	 * 获取服务端频率相关
	 * 
	 * @param type
	 *            ConfigRateType
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-4-8 下午4:18:36
	 */
	public int getSvrRate(int type) {
		switch (type) {
		case ConfigRateType.CONFIG_RATE_RECORD:
			return CldDalKConfig.getInstance().getPosReportConfig()
					.getConfigitem().getRecord_rate();
		case ConfigRateType.CONFIG_RATE_UPPOS:
			return CldDalKConfig.getInstance().getPosReportConfig()
					.getConfigitem().getUp_rate();
		case ConfigRateType.CONFIG_RATE_DESTSYNC:
			return CldDalKConfig.getInstance().getKcCloundConfig()
					.getConfigitem().getDest_sync_rate();
		case ConfigRateType.CONFIG_RATE_TMC_ROAM:
			return CldDalKConfig.getInstance().getPosReportConfig()
					.getConfigitem().getBroadcast_rate();
		default:
			return 30;
		}
	}

	/**
	 * 判断当前登录的kuid是否在白名单里
	 * 
	 * @param kuid
	 * @return
	 * @return boolean
	 * @author Zhouls
	 * @date 2015-4-8 下午4:18:53
	 */
	public boolean isInWhiteList(long kuid) {
		long[] kuids = CldDalKConfig.getInstance().getThirdPartConfig()
				.getConfigitem().getProjectmode().getKuids();
		if (null != kuids) {
			for (int i = 0; i < kuids.length; i++) {
				if (kuid == kuids[i]) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * 域名配置
	 * 
	 * @author Zhouls
	 * @date 2015-4-8 下午4:19:04
	 */
	public static class ConfigDomainType {

		/** 零散接口名头. */
		public static final int NAVI_SVR_PUB = 0;

		/** 账户系统域名头. */
		public static final int NAVI_SVR_KACCOUNT = NAVI_SVR_PUB + 1;

		/** 消息系统域名头. */
		public static final int NAVI_SVR_MSG = NAVI_SVR_KACCOUNT + 1;

		/** BD域名头. */
		public static final int NAVI_SVR_BD = NAVI_SVR_MSG + 1;

		/** 移动服务端短信号码. */
		public static final int NAVI_SMS_NUM_CMCC = NAVI_SVR_BD + 1;

		/** 电信服务端短信号码. */
		public static final int NAVI_SMS_NUM_CTCC = NAVI_SMS_NUM_CMCC + 1;

		/** 联通服务端短信号码. */
		public static final int NAVI_SMS_NUM_CUCC = NAVI_SMS_NUM_CTCC + 1;

		/** 在线导航域名头. */
		public static final int NAVI_SVR_ONLINENAVI = NAVI_SMS_NUM_CUCC + 1;

		/** 网页域名头. */
		public static final int NAVI_SVR_WEBSITE = NAVI_SVR_ONLINENAVI + 1;

		/** 一键通域名头. */
		public static final int NAVI_SVR_PPTCALL = NAVI_SVR_WEBSITE + 1;

		/** 位置服务域名头. */
		public static final int NAVI_SVR_POS = NAVI_SVR_PPTCALL + 1;

		/** 惠得思创服务号码. */
		public static final int PHONE_HDSC = NAVI_SVR_POS + 1;

		/** 是否是电话号码正则表达式. */
		public static final int REG_EXPRESS = PHONE_HDSC + 1;

		/** 鉴权域名头. */
		public static final int NAVI_SVR_AC = REG_EXPRESS + 1;

		/** K云域名头. */
		public static final int NAVI_SVR_KC = NAVI_SVR_AC + 1;

		/** 路况域名头. */
		public static final int NAVI_SVR_RTI = NAVI_SVR_KC + 1;
		
		/** 天气域名头. */
		public static final int NAVI_SVR_KWEATHER = NAVI_SVR_RTI + 1;

		/** 搜索域名头 */
		public static final int NAVI_SVR_SEARCH = NAVI_SVR_KWEATHER + 1;
		
		/** 货运域名头*/
		public static final int NAVI_SVR_HY = NAVI_SVR_SEARCH + 1;

		/** HY下载目录*/
		public static final int NAVI_SVR_HY_DOWNLOAD = NAVI_SVR_HY + 1;
		
		/** 限行上报域名 **/
		public static final int NAVI_SVR_PO = NAVI_SVR_HY_DOWNLOAD + 1;
	}

	/**
	 * 
	 * 超时配置
	 * 
	 * @author Zhouls
	 * @date 2015-4-8 下午4:19:20
	 */
	public static class ConfigTimeOutType {

		/** 客户端连接服务器超时. */
		public static final int CONFIG_TIMEOUT_CONNECT = 0;

		/** 请求服务器端超时. */
		public static final int CONFIG_TIMEOUT_REQUEST = CONFIG_TIMEOUT_CONNECT + 1;

		/** 接收服务器端超时. */
		public static final int CONFIG_TIMEOUT_RESPONSE = CONFIG_TIMEOUT_REQUEST + 1;
	}

	/**
	 * 
	 * WebUrl配置
	 * 
	 * @author Zhouls
	 * @date 2015-4-8 下午4:19:30
	 */
	public static class ConfigWebUrlType {

		/** K币兑换K豆页面. */
		public static final int CONFIG_URL_KB2KBEAN = 0;

		/** K豆明细查询页面. */
		public static final int CONFIG_URL_KBEANDETAIL = CONFIG_URL_KB2KBEAN + 1;

		/** K豆说明页面. */
		public static final int CONFIG_URL_KBEANREMARK = CONFIG_URL_KBEANDETAIL + 1;

		/** 如何获取K豆页面. */
		public static final int CONFIG_URL_KBEANHELP = CONFIG_URL_KBEANREMARK + 1;

		/** K币充值页面. */
		public static final int CONFIG_URL_KBRECHARGE = CONFIG_URL_KBEANHELP + 1;

		/** 如何获取K币页面. */
		public static final int CONFIG_URL_KBHELP = CONFIG_URL_KBRECHARGE + 1;

		/** 购买包月页面. */
		public static final int CONFIG_URL_PAYMONTHLY = CONFIG_URL_KBHELP + 1;

		/** 团购页面. */
		public static final int CONFIG_URL_TEAMBUY = CONFIG_URL_PAYMONTHLY + 1;

		/** 优惠页面. */
		public static final int CONFIG_URL_COUPON = CONFIG_URL_TEAMBUY + 1;

		/** 酒店预订页面. */
		public static final int CONFIG_URL_HOTEL = CONFIG_URL_COUPON + 1;

		/** 软件推荐页面. */
		public static final int CONFIG_URL_APPLIST = CONFIG_URL_HOTEL + 1;

		/** 获取邀请码. */
		public static final int CONFIG_URL_GETACTIVITYCODE = CONFIG_URL_APPLIST + 1;

		/** 邮箱注册. */
		public static final int CONFIG_URL_EMAILREGISTER = CONFIG_URL_GETACTIVITYCODE + 1;

		/** 进入先锋页面. */
		public static final int CONFIG_URL_PIONEER = CONFIG_URL_EMAILREGISTER + 1;

		/** 进入HUD页面. */
		public static final int CONFIG_URL_HUD = CONFIG_URL_PIONEER + 1;
	}

	/**
	 * 
	 * 开关配置
	 * 
	 * @author Zhouls
	 * @date 2015-4-8 下午4:19:40
	 */
	public static class ConfigSwitchType {

		/** 百度联网搜索接口开关. */
		public static final int CONFIG_SWITCH_BAIDUSEARCH = 0;

		/** 百度联网定位接口开关. */
		public static final int CONFIG_SWITCH_BAIDULOCATE = CONFIG_SWITCH_BAIDUSEARCH + 1;

		/** 手雷下载接口开关. */
		public static final int CONFIG_SWITCH_SHOULEI = CONFIG_SWITCH_BAIDULOCATE + 1;

		/** 友盟统计开关. */
		public static final int CONFIG_SWITCH_YOUMENGTOTAL = CONFIG_SWITCH_SHOULEI + 1;

		/** 友盟分享开关. */
		public static final int CONFIG_SWITCH_YOUMENGSHARE = CONFIG_SWITCH_YOUMENGTOTAL + 1;

		/** 一键通开关. */
		public static final int CONFIG_SWITCH_ONEKEYCALL = CONFIG_SWITCH_YOUMENGSHARE + 1;

		/** 先锋开关. */
		public static final int CONFIG_SWITCH_PIONEER = CONFIG_SWITCH_ONEKEYCALL + 1;

		/** 团购开关. */
		public static final int CONFIG_SWITCH_GROUPBUY = CONFIG_SWITCH_PIONEER + 1;

		/** 优惠开关. */
		public static final int CONFIG_SWITCH_COUPON = CONFIG_SWITCH_GROUPBUY + 1;

		/** 酒店预订开关. */
		public static final int CONFIG_SWITCH_ORDERHOTEL = CONFIG_SWITCH_COUPON + 1;

		/** 工程模式开关. */
		public static final int CONFIG_SWITCH_PROJECTMODE = CONFIG_SWITCH_ORDERHOTEL + 1;

		/** HUD开关. */
		public static final int CONFIG_SWITCH_HUD = CONFIG_SWITCH_PROJECTMODE + 1;

		/** 智行南京开关. */
		public static final int CONFIG_SWITCH_ZXNJ = CONFIG_SWITCH_HUD + 1;

		/** 代驾服务开关. */
		public static final int CONFIG_SWITCH_DRIVESERVICE = CONFIG_SWITCH_ZXNJ + 1;

		/** 车价评估开关. */
		public static final int CONFIG_SWITCH_CAREVALATE = CONFIG_SWITCH_DRIVESERVICE + 1;

		/** 车险计算器开关. */
		public static final int CONFIG_SWITCH_AUTOINSURANCE = CONFIG_SWITCH_CAREVALATE + 1;

		/** 违章查询开关. */
		public static final int CONFIG_SWITCH_VIOLATION = CONFIG_SWITCH_AUTOINSURANCE + 1;
		
		/** GPS log开关. */
		public static final int CONFIG_SWITCH_GPSLOG = CONFIG_SWITCH_VIOLATION + 1;
	}

	/**
	 * 频率类别
	 * 
	 * @author Zhouls
	 * @date 2015-1-30 下午4:30:46
	 */
	public static class ConfigRateType {

		/** 记录位置点频率 */
		public static final int CONFIG_RATE_RECORD = 0;

		/** 上报位置频率 */
		public static final int CONFIG_RATE_UPPOS = CONFIG_RATE_RECORD + 1;

		/** K云频率 */
		public static final int CONFIG_RATE_DESTSYNC = CONFIG_RATE_UPPOS + 1;
		/** 路况漫游播报（扫描）频率 */
		public static final int CONFIG_RATE_TMC_ROAM = CONFIG_RATE_DESTSYNC + 1;
	}
}
