/*
 * @Title CldSapKAccount.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.ols.sap;

import android.text.TextUtils;
import android.util.Base64;

import com.cld.log.CldLog;
import com.cld.ols.tools.CldAescrpy;
import com.yunbaba.ols.api.CldKServiceAPI;
import com.yunbaba.ols.bll.CldBllUtil;
import com.yunbaba.ols.sap.parse.CldKBaseParse;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * 帐户系统协议接口
 * 
 * @author Zhouls
 * @date 2014-8-15 下午3:02:07
 */
public class CldSapKAccount {

	/** 首次密文. */
	public static String keyCode;
	/** 公共输入参数. */
	private final static int APIVER = 1;
	/** The APPID. */
	public static int APPID;
	/** The RSCHARSET. */
	private final static int RSCHARSET = 1;
	/** The RSFORMAT. */
	private final static int RSFORMAT = 1;
	/** The UMSAVER. */
	private final static int UMSAVER = 2;
	/** The ENCRYPT. */
	private final static int ENCRYPT = 1;
	/** 渠道号 */
	public static int CID;
	/** 程序版本号 */
	public static String PROVER;

	/**
	 * 获取帐户系统密钥(201)
	 * 
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-3-13 下午12:20:53
	 */
	public static CldSapReturn initKeyCode() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		String key;
		if (CldBllUtil.getInstance().isTestVersion()) {
			// 测试版本
			key = "D0E484FCA2BE6038D170DFACC6141DA7";
		} else {
			// 正式版本
			key = "1F42AF9B4AE3DDB194BBF00A14CC2DC7";
		}
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_get_code.php", key);
		return errRes;
	}

	/**
	 * 设备注册(202)
	 * 
	 * @param deviceCode
	 *            设备标示符
	 * @param deviceName
	 *            设备名称
	 * @param osType
	 *            设备[系统]类型(1Android 2IOS 3WP 4WINCE，5未知)
	 * @param apptype
	 *            1 CM 5车机
	 * @param osVer
	 *            设备[系统]版本
	 * @param model
	 *            设备型号
	 * @param macWifi
	 *            Wifi Mac地址
	 * @param macBlue
	 *            Blue Mac地址
	 * @param imei
	 *            Imei
	 * @param simNo
	 *            SIM号码
	 * @param appVer
	 *            应用当前版本
	 * @param deviceSn
	 *            设备特征码
	 * @param naviVer
	 *            导航版本
	 * @param mapVer
	 *            地图版本
	 * @return CldSapReturn (0,402, 301，306)
	 * @author Zhouls
	 * @date 2015-1-5 下午5:41:47
	 */
	public static CldSapReturn deviceRegister(String deviceCode,
			String deviceName, int osType, int apptype, String osVer,
			String model, String macWifi, String macBlue, String imei,
			String simNo, String appVer, String deviceSn, String naviVer,
			String mapVer) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("apptype", apptype);
		map.put("devicecode", deviceCode);
		map.put("devicename", deviceName);
		map.put("ostype", osType);
		map.put("rsformat", RSFORMAT);
		map.put("rscharset", RSCHARSET);
		map.put("umsaver", UMSAVER);
		map.put("cid", CID);
		CldSapParser.putStringToMap(map, "appver", appVer);
		CldSapParser.putStringToMap(map, "devicesn", deviceSn);
		CldSapParser.putStringToMap(map, "imei", imei);
		CldSapParser.putStringToMap(map, "model", model);
		CldSapParser.putStringToMap(map, "macwifi", macWifi);
		CldSapParser.putStringToMap(map, "macblue", macBlue);
		CldSapParser.putStringToMap(map, "mapver", mapVer);
		CldSapParser.putStringToMap(map, "naviver", naviVer);
		CldSapParser.putStringToMap(map, "osver", osVer);
		CldSapParser.putStringToMap(map, "simno", simNo);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_reg_device.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 更新设备信息（203）
	 * 
	 * @param duid
	 *            设备id
	 * @param deviceName
	 *            设备名称
	 * @param osType
	 *            设备[系统]类型(1Android 2IOS 3WP 4WINCE，5未知)
	 * @param apptype
	 *            1 CM 5车机
	 * @param osVer
	 *            设备[系统]版本
	 * @param model
	 *            设备型号
	 * @param macWifi
	 *            Wifi Mac地址
	 * @param macBlue
	 *            Blue Mac地址
	 * @param imei
	 *            Imei
	 * @param simNo
	 *            SIM号码
	 * @param appVer
	 *            应用当前版本
	 * @param deviceSn
	 *            设备特征码
	 * @param naviVer
	 *            导航版本
	 * @param mapVer
	 *            地图版本
	 * @return CldSapReturn (0,402)
	 * @author Zhouls
	 * @date 2014-10-8 上午11:24:42
	 */
	public static CldSapReturn updateDeviceInfo(long duid, String deviceName,
			int osType, int apptype, String osVer, String model,
			String macWifi, String macBlue, String imei, String simNo,
			String appVer, String deviceSn, String naviVer, String mapVer) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("apptype", apptype);
		map.put("duid", duid);
		map.put("devicename", deviceName);
		map.put("ostype", osType);
		map.put("rsformat", RSFORMAT);
		map.put("rscharset", RSCHARSET);
		map.put("umsaver", UMSAVER);
		map.put("cid", CID);
		CldSapParser.putStringToMap(map, "appver", appVer);
		CldSapParser.putStringToMap(map, "devicesn", deviceSn);
		CldSapParser.putStringToMap(map, "imei", imei);
		CldSapParser.putStringToMap(map, "model", model);
		CldSapParser.putStringToMap(map, "macwifi", macWifi);
		CldSapParser.putStringToMap(map, "macblue", macBlue);
		CldSapParser.putStringToMap(map, "mapver", mapVer);
		CldSapParser.putStringToMap(map, "naviver", naviVer);
		CldSapParser.putStringToMap(map, "osver", osVer);
		CldSapParser.putStringToMap(map, "simno", simNo);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_update_device_info.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 是否是已注册用户(204)
	 * 
	 * @param loginName
	 *            电话号码，用户名，邮箱
	 * @param timeStamp
	 *            时间戳
	 * @return errCode (0,101,401,402,910) int
	 * @author Zhouls
	 * @date 2014-8-18 下午3:56:58
	 */
	public static CldSapReturn isRegisterUser(String loginName, long timeStamp) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("loginname", loginName);
		map.put("timestamp", timeStamp);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_is_reg_user.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 通过上行短信注册(205)
	 * 
	 * @param guid
	 *            短信标识符
	 * @param ip
	 *            注册IP
	 * @return errCode(0,101,201,401,402,503,911) int
	 * @author Zhouls
	 * @date 2014-8-22 上午11:31:53
	 */
	public static CldSapReturn registerBySms(String guid, String ip) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("guid", guid);
		map.put("cid", CID);
		map.put("prover", PROVER);
		CldSapParser.putStringToMap(map, "ip", ip);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_reg_by_send_sms.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 登录(206)
	 * 
	 * @param loginname
	 *            用户登录名（登录名、手机号、邮箱）
	 * @param loginpwd
	 *            登录密码
	 * @param bussinessid
	 *            业务编码
	 * @param pwdtype
	 *            密码类型,1普通登录密码（默认）,2快捷登录密码,
	 * @param timestamp
	 *            时间戳
	 * @param ip
	 *            登录IP
	 * @param duid
	 *            设备id
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-1-19 下午4:08:04
	 */
	public static CldSapReturn login(String loginname, String loginpwd,
			int bussinessid, int pwdtype, long timestamp, String ip, long duid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("loginname", loginname);
		map.put("loginpwd", loginpwd);
		map.put("bussinessid", bussinessid);
		map.put("pwdtype", pwdtype);
		map.put("timestamp", timestamp);
		map.put("duid", duid);
		CldSapParser.putStringToMap(map, "ip", ip);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_login_user.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 注销(207)
	 * 
	 * @param kuid
	 *            登录返回的kuid
	 * @param session
	 *            登录返回的session
	 * @param bussinessid
	 *            （1） 业务编码
	 * @return errCode(0,401,402 500，501，504，505) int
	 * @author Zhouls
	 * @date 2014-9-1 下午3:05:00
	 */
	public static CldSapReturn loginOut(long kuid, String session,
			int bussinessid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("kuid", kuid);
		map.put("session", session);
		CldSapParser.putIntToMap(map, "bussinessid", bussinessid);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_logout_user.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 获取用户信息(208)
	 * 
	 * @param kuid
	 *            用户kuid
	 * @param session
	 *            用户session
	 * @param bussinessid
	 *            业务编码
	 * @return errCode(0,108,401,402 ,500，501，504]) int
	 * @author Zhouls
	 * @date 2014-8-20 上午10:33:36
	 */
	public static CldSapReturn getUserInfo(long kuid, String session,
			int bussinessid, int flag) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("flag", flag);
		CldSapParser.putIntToMap(map, "bussinessid", bussinessid);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_get_user_info.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 更新用户信息接口(209)
	 * 
	 * @param kuid
	 *            用户KUID(必传)
	 * @param session
	 *            登录Session（必传）
	 * @param bussinessid
	 *            业务编码
	 * @param username
	 *            用户名称
	 * @param useralias
	 *            用户别名
	 * @param email
	 *            邮箱
	 * @param mobile
	 *            手机
	 * @param sex
	 *            性别
	 * @param cardtype
	 *            证件类型
	 * @param cardcode
	 *            证件号
	 * @param qq
	 *            QQ号
	 * @return int
	 * @author Zhouls
	 * @date 2014-9-2 下午3:08:57
	 */
	@SuppressWarnings("deprecation")
	public static CldSapReturn updateUserInfo(long kuid, String session,
			int bussinessid, String username, String useralias, String email,
			String mobile, String sex, String address, int cardtype,
			String cardcode, String qq, byte[] photoData) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("encrypt", ENCRYPT);
		map.put("kuid", kuid);
		map.put("session", session);

		if ((photoData != null) && (photoData.length > 0)) {
			String strPhoto = Base64.encodeToString(photoData, 0);
			if (!TextUtils.isEmpty(strPhoto)) {
				CldLog.d("ols",
						photoData.length + ",base64:" + strPhoto.length());
			}
			map.put("photo", strPhoto);
			map.put("suffix", "jpg");
		}

		CldSapParser.putStringToMap(map, "username", username);
		CldSapParser.putStringToMap(map, "useralias", useralias);
		CldSapParser.putStringToMap(map, "email", 
			      CldAescrpy.encrypt(CldSapUtil.decodeKey(keyCode), email));
		CldSapParser.putStringToMap(map, "mobile", mobile);
		CldSapParser.putStringToMap(map, "cardcode", 
			      CldAescrpy.encrypt(CldSapUtil.decodeKey(keyCode), cardcode));
		CldSapParser.putStringToMap(map, "qq", qq);
		CldSapParser.putStringToMap(map, "sex", 
		        CldAescrpy.encrypt(CldSapUtil.decodeKey(keyCode), sex));
		CldSapParser.putStringToMap(map, "address", address);
		CldSapParser.putIntToMap(map, "bussinessid", bussinessid);
		CldSapParser.putIntToMap(map, "cardtype", cardtype);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_update_user_info.php",
				CldSapUtil.decodeKey(keyCode));
		errRes.jsonPost = URLEncoder.encode(errRes.jsonPost);
		return errRes;
	}

	/**
	 * 上报星级用户认证
	 * 
	 * @param kuid
	 * @param session
	 * @param bussinessid
	 * @param driving_licence
	 * @param vehicle_licence
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2016-5-6 下午3:20:21
	 */
	@SuppressWarnings("deprecation")
	public static CldSapReturn uploadStarAuth(long kuid, String session,
			int bussinessid, byte[] driving_licence, byte[] vehicle_licence) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("encrypt", ENCRYPT);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("bussinessid", bussinessid);
		String strPhoto = Base64
				.encodeToString(driving_licence, Base64.DEFAULT);
		map.put("driving_licence", strPhoto);
		strPhoto = Base64.encodeToString(vehicle_licence, Base64.DEFAULT);
		map.put("vehicle_licence", strPhoto);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_up_star_auth.php",
				CldSapUtil.decodeKey(keyCode));
		errRes.jsonPost = URLEncoder.encode(errRes.jsonPost);
		return errRes;
	}

	/**
	 * 快捷登录(210)
	 * 
	 * @param mobile
	 *            手机号
	 * @param verifycode
	 *            验证码
	 * @param fastloginpwd
	 *            终端生成快捷登录密码
	 * @param loginname
	 *            用户登录名,建议默认M+mobile,确保唯一
	 * @param duid
	 *            设备唯一标识
	 * @param bussinessid
	 *            业务id
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-1-19 上午8:47:46
	 */
	public static CldSapReturn fastLogin(String mobile, String verifycode,
			String fastloginpwd, String loginname, long duid, int bussinessid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("duid", duid);
		map.put("mobile", mobile);
		map.put("verifycode", verifycode);
		map.put("fastloginpwd", fastloginpwd);
		// 此loginname参数可能会导致快捷登录时返回用户名已存在的问题。
		// map.put("loginname", loginname);
		map.put("bussinessid", bussinessid);
		map.put("cid", CID);
		map.put("prover", PROVER);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_reg_fastlogin.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 获取手机验证码(211)
	 * 
	 * @param mobile
	 *            电话号码
	 * @param bussinessCode
	 *            业务类型 101注册 102绑定 103改绑 104解绑 105重置密码106快捷登录
	 * @param timeStamp
	 *            时间戳
	 * @param kuid
	 *            用户kuid(102，103，104时为必需，非以上不传)
	 * @param session
	 *            登录Session(102，103，104时为必需，非以上不传)
	 * @param bussinessid
	 *            业务编码(102，103，104时为必需，非以上不传)
	 * @param oldmobile
	 *            103 必传
	 * @return int
	 * @author Zhouls
	 * @date 2014-9-29 下午3:44:11
	 */
	public static CldSapReturn getVerifyCode(String mobile, int bussinessCode,
			long timeStamp, long kuid, String session, int bussinessid,
			String oldmobile) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("mobile", mobile);
		map.put("timestamp", timeStamp);
		map.put("bussinesscode", bussinessCode);
		CldSapParser.putIntToMap(map, "bussinessid", bussinessid);
		CldSapParser.putLongToMap(map, "kuid", kuid);
		CldSapParser.putStringToMap(map, "oldmobile", oldmobile);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrKA()
						+ "kaccount_get_mobile_verify_code.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 下行短信注册(212)
	 * 
	 * @param mobile
	 *            电话号码
	 * @param password
	 *            密码
	 * @param verifyCode
	 *            手机验证码
	 * @param loginName
	 *            登录名
	 * @param ip
	 *            注册ip
	 * @return int
	 * @author Zhouls
	 * @date 2014-9-2 下午3:33:44
	 */
	public static CldSapReturn register(String mobile, String password,
			String verifyCode, String loginName, String ip) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("mobile", mobile);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("userpwd", password);
		map.put("verifycode", verifyCode);
		map.put("cid", CID);
		map.put("prover", PROVER);
		CldSapParser.putStringToMap(map, "ip", ip);
		CldSapParser.putStringToMap(map, "loginname", loginName);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_reg_by_phone_number.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 个性注册
	 *
	 * @param ip
	 *            注册ip
	 * @return int
	 * @author Zhouls
	 * @date 2014-9-2 下午3:33:44
	 */
	public static CldSapReturn registerEx(String username, String password,
			String ip) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("prover", PROVER);
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("userpwd", password);
		map.put("cid", CID);
		map.put("apptype", CldKServiceAPI.getInstance().getApptype());
		CldSapParser.putStringToMap(map, "ip", ip);
		CldSapParser.putStringToMap(map, "loginname", username);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_reg_user.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 通过手机号 验证重置密码(213)
	 * 
	 * @param mobile
	 *            电话号码
	 * @param newPwd
	 *            新密码
	 * @param verifyCode
	 *            验证码
	 * @return errCode int
	 * @author Zhouls
	 * @date 2014-8-12 下午6:48:53
	 */
	public static CldSapReturn retrievePwd(String mobile, String newPwd,
			String verifyCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("mobile", mobile);
		map.put("newpwd", newPwd);
		map.put("verifycode", verifyCode);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_reset_pwd_by_mobile.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 修改密码(214)
	 * 
	 * @param oldPwd
	 *            旧密码
	 * @param newPwd
	 *            新密码
	 * @param kuid
	 *            kuid
	 * @param session
	 *            登录Session
	 * @param bussinessid
	 *            业务编码
	 * @param pwdtype
	 *            旧密码类型,1普通登录密码（默认）,2快捷登录密码, 新密码都是普通登录密码
	 * @return int
	 * @author Zhouls
	 * @date 2015-1-19 上午11:01:50
	 */
	public static CldSapReturn revisePwd(String oldPwd, String newPwd,
			long kuid, String session, int bussinessid, int pwdtype) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("oldpwd", oldPwd);
		map.put("newpwd", newPwd);
		map.put("kuid", kuid);
		map.put("pwdtype", pwdtype);
		map.put("session", session);
		map.put("bussinessid", bussinessid);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_update_pwd.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 获取服务端时间戳(215)
	 * 
	 * @return int
	 * @author Zhouls
	 * @date 2014-9-19 上午9:46:48
	 */
	public static CldSapReturn getKAconfig() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_get_config.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 绑定手机号(216)
	 * 
	 * @param kuid
	 *            用户KUID
	 * @param session
	 *            登录Session
	 * @param bussinessid
	 *            业务编码
	 * @param mobile
	 *            手机号
	 * @param verifycode
	 *            短信验证码
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-1-19 上午11:22:57
	 */
	public static CldSapReturn bindMobile(long kuid, String session,
			int bussinessid, String mobile, String verifycode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("bussinessid", bussinessid);
		map.put("mobile", mobile);
		map.put("verifycode", verifycode);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_bind_mobile.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 解绑手机号(217)
	 * 
	 * @param kuid
	 *            用户KUID
	 * @param session
	 *            登录Session
	 * @param bussinessid
	 *            业务编码
	 * @param mobile
	 *            手机号
	 * @param verifycode
	 *            短信验证码
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-1-19 下午12:56:43
	 */
	public static CldSapReturn unbindMobile(long kuid, String session,
			int bussinessid, String mobile, String verifycode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("bussinessid", bussinessid);
		map.put("mobile", mobile);
		map.put("verifycode", verifycode);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_unbind_mobile.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 校验手机验证码是否正确（223）
	 * 
	 * @param mobile
	 *            手机号
	 * @param verifycode
	 *            验证码
	 * @param bussinessCode
	 *            业务类型 101注册 102绑定 103改绑 104解绑 105重置密码106快捷登录
	 * @param timeStamp
	 *            时间戳
	 * @return
	 * @return CldKReturn
	 * @author Zhouls
	 * @date 2015-8-10 下午2:26:40
	 */
	public static CldSapReturn checkMobileVeriCode(String mobile,
			String verifycode, int bussinessCode, long timeStamp) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		//map.put("encrypt", ENCRYPT);
		
		map.put("mobile", mobile);
		map.put("timestamp", timeStamp);
		map.put("bussinesscode", bussinessCode);
		map.put("verifycode", verifycode);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA()
						+ "kaccount_check_mobile_verify_code.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 改绑手机号(218)
	 * 
	 * @param kuid
	 *            用户KUID
	 * @param session
	 *            登录Session
	 * @param bussinessid
	 *            业务编码
	 * @param oldmobile
	 *            旧手机号
	 * @param mobile
	 *            手机号
	 * @param verifycode
	 *            短信验证码
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-1-19 下午12:40:20
	 */
	public static CldSapReturn updateMobile(long kuid, String session,
			int bussinessid, String oldmobile, String mobile, String verifycode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("bussinessid", bussinessid);
		map.put("mobile", mobile);
		map.put("oldmobile", oldmobile);
		map.put("verifycode", verifycode);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_update_bind_mobile.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 获取扫描登录的二维码(219)
	 * 
	 * @param bussinessid
	 *            业务编码
	 * @param fastloginpwd
	 *            快捷登录密码，由使用方生成并保存
	 * @param img
	 *            是否需要返回imgdata数据 0,不需要，imgdata返回空1需要
	 * @param size
	 *            如果img参数为1，此参数为必须 二维码边长=size*(25像素),size>1
	 * @param timestamp
	 *            时间戳
	 * @param duid
	 *            设备标识
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-1-19 上午9:20:50
	 */
	public static CldSapReturn getQRcode(int bussinessid, String fastloginpwd,
			int img, int size, long timestamp, long duid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("bussinessid", bussinessid);
		map.put("timestamp", timestamp);
		map.put("fastloginpwd", fastloginpwd);
		map.put("duid", duid);
		CldSapParser.putIntToMap(map, "img", img);
		CldSapParser.putIntToMap(map, "size", size);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_get_2dcode.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 二维码登录(220)
	 * 
	 * @param kuid
	 * @param session
	 * @param bussinessid
	 *            业务编码
	 * @param guid
	 *            唯一标识符,扫描二维码得到的内容
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-1-19 上午9:42:47
	 */
	public static CldSapReturn loginByQRcode(long kuid, String session,
			int bussinessid, String guid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("kuid", kuid);
		map.put("guid", guid);
		map.put("session", session);
		map.put("bussinessid", bussinessid);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_login_by_2dcode.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 获取二维码登录状态(221)
	 * 
	 * @param guid
	 *            唯一标识符,获取二维码时所返回
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-1-19 上午9:54:36
	 */
	public static CldSapReturn getLoginStatusByQRcode(String guid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("guid", guid);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrKA()
						+ "kaccount_get_login_status_by_2dcode.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 第三方登录接口
	 * 
	 * @param openid
	 *            第三方登录返回的唯一Id
	 * @param type
	 *            第三方类型,1 qq登录，2 微信登录，3新浪微博登录
	 * @param userpwd
	 *            终端随机生成的临时密码
	 * @param bussinessid
	 *            业务id
	 * @param duid
	 *            设备id
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-6-10 上午9:46:42
	 */
	public static CldSapReturn thirdLogin(String openid, int type,
			String userpwd, int bussinessid, long duid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("appid", APPID);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("openid", openid);
		map.put("type", type);
		map.put("userpwd", userpwd);
		map.put("bussinessid", bussinessid);
		map.put("duid", duid);
		map.put("cid", CID);
		map.put("prover", PROVER);
		CldSapReturn errRes = CldKBaseParse.getPostParms(map,
				CldSapUtil.getNaviSvrKA() + "kaccount_third_reg.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

}
