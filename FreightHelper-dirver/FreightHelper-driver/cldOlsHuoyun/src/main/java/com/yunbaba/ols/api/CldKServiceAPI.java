/*
 * @Title CldKServiceAPI.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.ols.api;

import java.util.List;
import android.text.TextUtils;
import com.cld.log.CldLog;
import com.cld.setting.CldSetting;
import com.yunbaba.ols.bll.CldBllUtil;
import com.yunbaba.ols.dal.CldDalKAccount;
import com.yunbaba.ols.sap.CldSapKMessage;
import com.yunbaba.ols.sap.bean.CldSapKMParm;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtKeyCode;
import com.yunbaba.ols.tools.CldErrUtil;
import com.yunbaba.ols.tools.CldSapNetUtil;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;



/**
 * 消息推送服务模块,提供推送服务支持
 * 
 * @author Zhouls
 * @date 2015-3-5 下午3:38:48
 */
public class CldKServiceAPI {

	private static CldKServiceAPI cldKServiceAPI;

	private CldKServiceAPI() {

	}

	public static CldKServiceAPI getInstance() {
		if (null == cldKServiceAPI) {
			cldKServiceAPI = new CldKServiceAPI();
		}
		return cldKServiceAPI;
	}

	/**
	 * 初始化
	 * 
	 * @param isTestVersion
	 *            是否是测试版本 false 正式 true 测试
	 * @param appPath
	 *            程序路径
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:38:58
	 */
	public void init(boolean isTestVersion, String appPath) {
		CldBllUtil.getInstance().init(isTestVersion, appPath); 
		CldKConfigAPI.getInstance().initDefConfig();
	}

	/**
	 * 反初始化
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:39:17
	 */
	public void uninit() {

	}

	/**
	 * 服务初始化CldKMKey
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:39:28
	 */
	public void initKMkey() {
		String cldKMKey = CldSetting.getString("CldKMKey");
		if (TextUtils.isEmpty(cldKMKey)) {
			CldSapReturn errRes = CldSapKMessage.initKeyCode();
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			ProtKeyCode protKeyCode = CldSapParser.parseJson(strRtn,
					ProtKeyCode.class, errRes);
			CldErrUtil.handleErr(errRes);
			if (null != protKeyCode) {
				if (errRes.errCode == 0) {
					cldKMKey = protKeyCode.getCode();
					CldSetting.put("CldKMKey", cldKMKey);
				}
			}
		}
		CldSapKMessage.keyCode = cldKMKey;
	}

	/**
	 * 服务获取duid接口
	 * 
	 * @return
	 * @return long
	 * @author Zhouls
	 * @date 2015-3-5 下午3:40:05
	 */
	public long getDuid() {
		String strDuid = CldSetting.getString("duid");
		if (TextUtils.isEmpty(strDuid)) {
			return 0;
		} else {
			return Long.parseLong(strDuid);
		}
	}

	/**
	 * 服务获取kuid接口
	 * 
	 * @return
	 * @return long
	 * @author Zhouls
	 * @date 2015-3-5 下午3:40:16
	 */
	public long getKuid() {
		String strKuid = CldSetting.getString("kuid");
		if (TextUtils.isEmpty(strKuid)) {
			return 0;
		} else {
			return Long.parseLong(strKuid);
		}
	}

	/**
	 * 服务获取session
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-5 下午3:40:27
	 */
	public String getSession() {
		return CldSetting.getString("session");
	}

	/**
	 * 服务获取apptype
	 * 
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-3-5 下午3:40:35
	 */
	public int getApptype() {
		String strApptype = CldSetting.getString("apptype");
		if (TextUtils.isEmpty(strApptype)) {
			return 0;
		} else {
			return Integer.parseInt(strApptype);
		}
	}

	/**
	 * 获取业务Id
	 * 
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-3-26 下午3:03:55
	 */
	public int getBussinessid() {
		String strBussinessid = CldSetting.getString("bussinessid");
		if (TextUtils.isEmpty(strBussinessid)) {
			return 0;
		} else {
			return Integer.parseInt(strBussinessid);
		}
	}

	/**
	 * 获取业务id
	 * 
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-3-26 下午3:06:34
	 */
	public int getAppId() {
		String strAppId = CldSetting.getString("appid");
		if (TextUtils.isEmpty(strAppId)) {
			return 0;
		} else {
			return Integer.parseInt(strAppId);
		}
	}

	/**
	 * 接收消息
	 * 
	 * @param duid
	 *            用户设备id
	 * @param apptype
	 *            应用类型（1：CM；2：IOS），多个以";"分隔，如"1;2"
	 * @param prover
	 *            版本号，多个以";"分隔，如"6.0;11.0"
	 * @param list
	 *            回传消息列表
	 * @param kuid
	 *            用户Id（没有则传0）
	 * @param regionCode
	 *            行政区编码
	 * @param x
	 *            当前坐标x
	 * @param y
	 *            当前坐标y
	 * @param bussinessid
	 *            业务编号 CM(1)
	 * @param session
	 *            登录返回的session
	 * @param appid
	 *            CM(9)
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-3-5 下午3:40:45
	 */
	public int recMessage(long duid, int apptype, String prover,
			List<CldSapKMParm> list, long kuid, int regionCode, long x, long y,
			int bussinessid, String session, int appid) {
		initKMkey();
		CldSapReturn errRes = CldSapKMessage.recShareMsg(duid, apptype, prover,
				kuid, regionCode, x, y, bussinessid, session, appid, 1);
		CldLog.d("ols", "errRes.url: " + errRes.url);
		String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
		CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
		CldErrUtil.handleErr(errRes);
		CldLog.d("ols", errRes.errCode + "_downloadMsg");
		CldLog.d("ols", errRes.errMsg + "_downloadMsg");
		errCodeFix(errRes);
		if (errRes.errCode == 0) {
			CldSapKMessage.parseMessage(strRtn, list, CldDalKAccount
					.getInstance().getKuid(), CldBllUtil.getInstance()
					.getApptype());
		}
		return errRes.errCode;
	}
	
	/**
	 * 错误码处理
	 * 
	 * @param res
	 * @return void
	 * @author zhaoqy
	 * @date 2017-4-24
	 */
	public void errCodeFix(CldSapReturn res) {
		switch (res.errCode) {
		case 501: {
			/**
			 * 被挤下线处理
			 */
			if (res.session.equals(CldDalKAccount.getInstance().getSession())) {
				/**
				 * 当接口使用session和当前帐户里的session相同才挤下线
				 */
				if (!TextUtils.isEmpty(res.session)) {
					CldKAccountAPI.getInstance().sessionInvalid(501, 0);
				}
			}
		}
			break;
		}
	}

	/**
	 * 校验帐户系统session是否失效
	 * 
	 * @param errCode
	 * @return
	 * @return boolean
	 * @author Zhouls
	 * @date 2015-3-5 下午3:41:26
	 */
	public boolean isSessionInValid(int errCode) {
		if (500 == errCode || 501 == errCode) {
			return true;
		} else {
			return false;
		}
	}
}
