/*
 * @Title CldSapKCallNavi.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.ols.sap;

import java.util.HashMap;
import java.util.Map;

import com.yunbaba.ols.sap.parse.CldKBaseParse;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;


/**
 * 一键通协议层接口
 * 
 * @author Zhouls
 * @date 2014-10-29 下午4:41:07
 */
public class CldSapKCallNavi {

	/** 首次密文 */
	public static String keyCode;
	/** 公共输入参数. */
	private static final int APIVER = 1;
	/** The RSCHARSET. */
	private static final int RSCHARSET = 1;
	/** The RSFORMAT. */
	private static final int RSFORMAT = 1;
	/** The UMSAVER. */
	private static final int UMSAVER = 1;

	/**
	 * 初始化一键通系统密钥(401)
	 * 
	 * @return int
	 * @author Zhouls
	 * @date 2014-9-22 上午9:12:45
	 */
	public static CldSapReturn initKeyCode() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		String key = "ADE8E2743BD4E4EDDF0C060A8FFC524C";
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrPPT() + "kptt_get_code.php", key);
		return errRes;
	}

	/**
	 * 获取一键通验证码（402）
	 * 
	 * @param apptype
	 *            应用类型（1：CM；2：IOS）
	 * @param prover
	 *            版本号
	 * @param kuid
	 *            凯立德帐号id
	 * @param session
	 *            Session
	 * @param appid
	 *            appid
	 * @param bussinessid
	 *            bussinessid
	 * @param mobile
	 *            手机号码
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-12 下午2:58:57
	 */
	public static CldSapReturn getIdentifycode(int apptype, String prover,
			long kuid, String session, int appid, int bussinessid, String mobile) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("apptype", apptype);
		map.put("prover", prover);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("appid", appid);
		map.put("bussinessid", bussinessid);
		map.put("mobile", mobile);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrPPT() + "kptt_get_identifycode.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 获取凯立德帐号绑定的手机号码列表(403)
	 * 
	 * @param apptype
	 *            应用类型（1：CM；2：IOS）
	 * @param prover
	 *            版本号
	 * @param kuid
	 *            凯立德帐号id
	 * @param session
	 *            Session
	 * @param appid
	 *            appid
	 * @param bussinessid
	 *            bussinessid
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-12 下午3:04:59
	 */
	public static CldSapReturn getBindMobile(int apptype, String prover,
			long kuid, String session, int appid, int bussinessid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("apptype", apptype);
		map.put("prover", prover);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("appid", appid);
		map.put("bussinessid", bussinessid);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrPPT() + "kptt_get_bind_mobile.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 绑定其他手机号(404)
	 * 
	 * @param apptype
	 *            应用类型（1：CM；2：IOS）
	 * @param prover
	 *            版本号
	 * @param kuid
	 *            凯立德帐号id
	 * @param session
	 *            Session
	 * @param appid
	 *            appid
	 * @param bussinessid
	 *            bussinessid
	 * @param identifycode
	 *            the identifycode
	 * @param mobile
	 *            手机号码
	 * @param replacemobile
	 *            替换旧号码 不为空则替换绑定的旧号码；为空则增加新绑定号码
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-12 下午3:02:58
	 */
	public static CldSapReturn bindToMobile(int apptype, String prover,
			long kuid, String session, int appid, int bussinessid,
			String identifycode, String mobile, String replacemobile) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("apptype", apptype);
		map.put("prover", prover);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("appid", appid);
		map.put("bussinessid", bussinessid);
		map.put("identifycode", identifycode);
		map.put("mobile", mobile);
		CldSapParser.putStringToMap(map, "replacemobile", replacemobile);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrPPT() + "kptt_bind_mobile.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 删除绑定手机号(405).
	 * 
	 * @param apptype
	 *            应用类型（1：CM；2：IOS）
	 * @param prover
	 *            版本号
	 * @param kuid
	 *            凯立德帐号id
	 * @param session
	 *            Session
	 * @param appid
	 *            appid
	 * @param bussinessid
	 *            bussinessid
	 * @param mobile
	 *            手机号码
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-12 下午3:07:29
	 */
	public static CldSapReturn delBindMobile(int apptype, String prover,
			long kuid, String session, int appid, int bussinessid, String mobile) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("apptype", apptype);
		map.put("prover", prover);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("appid", appid);
		map.put("bussinessid", bussinessid);
		map.put("mobile", mobile);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrPPT() + "kptt_del_bind_mobile.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 注册一键通(406)
	 * 
	 * @param apptype
	 *            应用类型（1：CM；2：IOS）
	 * @param prover
	 *            版本号
	 * @param kuid
	 *            凯立德帐号id
	 * @param session
	 *            Session
	 * @param appid
	 *            appid
	 * @param bussinessid
	 *            bussinessid
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-12 下午2:54:02
	 */
	public static CldSapReturn registerByKuid(int apptype, String prover,
			long kuid, String session, int appid, int bussinessid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("apptype", apptype);
		map.put("prover", prover);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("appid", appid);
		map.put("bussinessid", bussinessid);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrPPT() + "kptt_register_by_kuid.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}
}
