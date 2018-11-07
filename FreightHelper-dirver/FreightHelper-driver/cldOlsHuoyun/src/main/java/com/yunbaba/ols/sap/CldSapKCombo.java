package com.yunbaba.ols.sap;

import java.util.HashMap;
import java.util.Map;

import com.yunbaba.ols.sap.parse.CldKBaseParse;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;

public class CldSapKCombo {

	/** 公共输入参数. */
	private final static int APIVER = 1;
	/** The RSCHARSET. */
	private final static int RSCHARSET = 1;
	/** The RSFORMAT. */
	private final static int RSFORMAT = 1;
	/** The UMSAVER. */
	private final static int UMSAVER = 2;
	/** The ENCRYPT. */
	private final static int ENCRYPT = 0;
	/** 渠道号 */
	public static int CID;
	/** 程序版本号 */
	public static String PROVER;

	/**
	 * 获取可购买的套餐列表
	 * 
	 * @param systemCode
	 *            操作系统编码(运营平台定义)
	 * @param deviceCode
	 *            设备型号编码(运营平台定义)
	 * @param productCode
	 *            产品型号编码(运营平台定义)
	 * @param width
	 *            分辨率宽
	 * @param height
	 *            分辨率高
	 * @param iccid
	 *            Iccid卡号
	 * @param kuid
	 *            用户kuid
	 * @param session
	 *            登录Session
	 * @param appid
	 *            账号系统分配
	 * @param bussinessid
	 *            业务编码
	 * @return CldSapReturn
	 */
	public static CldSapReturn getComboList(int systemCode, int deviceCode,
			int productCode, int width, int height, String iccid, int kuid,
			String session, int appid, int bussinessid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("system_code", systemCode);
		map.put("device_code", deviceCode);
		map.put("product_code", productCode);
		map.put("width", width);
		map.put("height", height);
		map.put("launcher_ver", "");
		map.put("iccid", iccid);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("appid", appid);
		CldSapParser.putIntToMap(map, "bussinessid", bussinessid);

		CldSapReturn errRes = CldKBaseParse.getGetParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_get_combo_list.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	/**
	 * 获取用户已经可购买过的套餐列表
	 * 
	 * @param systemCode
	 *            操作系统编码(运营平台定义)
	 * @param deviceCode
	 *            设备型号编码(运营平台定义)
	 * @param productCode
	 *            产品型号编码(运营平台定义)
	 * @param width
	 *            分辨率宽
	 * @param height
	 *            分辨率高
	 * @param iccid
	 *            Iccid卡号
	 * @param kuid
	 *            用户kuid
	 * @param session
	 *            登录Session
	 * @param appid
	 *            账号系统分配
	 * @param bussinessid
	 *            业务编码
	 * @return CldSapReturn
	 */
	public static CldSapReturn getUserComboList(int systemCode, int deviceCode,
			int productCode, int width, int height, String iccid, int kuid,
			String session, int appid, int bussinessid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("system_code", systemCode);
		map.put("device_code", deviceCode);
		map.put("product_code", productCode);
		map.put("width", width);
		map.put("height", height);
		map.put("launcher_ver", "");
		map.put("iccid", iccid);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("appid", appid);
		CldSapParser.putIntToMap(map, "bussinessid", bussinessid);

		CldSapReturn errRes = CldKBaseParse.getGetParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_get_user_combo_list.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	/**
	 * 获取用户已经可购买过的套餐数量
	 * 
	 * @param iccid
	 *            Iccid卡号
	 * @param kuid
	 *            用户kuid
	 * @param session
	 *            登录Session
	 * @param appid
	 *            账号系统分配
	 * @param bussinessid
	 *            业务编码
	 * @return CldSapReturn
	 */
	public static CldSapReturn getUserComboCount(String iccid, int kuid,
			String session, int appid, int bussinessid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("iccid", iccid);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("appid", appid);
		CldSapParser.putIntToMap(map, "bussinessid", bussinessid);

		CldSapReturn errRes = CldKBaseParse.getGetParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_get_user_combo_count.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	/**
	 * 获取用户已经可购买过的套餐服务，关联的app应用列表
	 * 
	 * @param iccid
	 *            Iccid卡号
	 * @param kuid
	 *            用户kuid
	 * @param session
	 *            登录Session
	 * @param appid
	 *            账号系统分配
	 * @param bussinessid
	 *            业务编码
	 * @return CldSapReturn
	 */
	public static CldSapReturn getServiceApp(String iccid, int kuid,
			String session, int appid, int bussinessid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("iccid", iccid);
		map.put("kuid", kuid);
		map.put("session", session);
		map.put("appid", appid);
		CldSapParser.putIntToMap(map, "bussinessid", bussinessid);

		CldSapReturn errRes = CldKBaseParse.getGetParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_get_service_app.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	/**
	 * 更新套餐购买次数
	 * 
	 * @param combo_code
	 *            套餐编码
	 * @return CldSapReturn
	 */
	public static CldSapReturn getUpdateComboPayTimes(int combo_code) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("combo_code", combo_code);

		CldSapReturn errRes = CldKBaseParse.getGetParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_get_update_combo_pay_times.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	/**
	 * 获取套餐的提醒设置
	 * 
	 * @param combo_code
	 *            套餐编码
	 * @return CldSapReturn
	 */
	public static CldSapReturn getComboAlarmSetting(int combo_code) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("combo_code", combo_code);

		CldSapReturn errRes = CldKBaseParse.getGetParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_get_combo_alarm_setting.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	/**
	 * 购买套餐
	 * 
	 * @param deviceCode
	 *            设备型号编码(运营平台定义)
	 * @param productCode
	 *            产品型号编码(运营平台定义)
	 * @param cust_id
	 *            设备客户ID
	 * @param duid
	 *            设备编号
	 * @param sn
	 *            设备唯一码
	 * @param combo_code
	 *            套餐编码
	 * @param month
	 *            月份数
	 * @param charge
	 *            费用，单位：元
	 * @param iccid
	 *            ICCID卡号
	 * @param kuid
	 *            用户kuid
	 * @param orderno
	 *            订单号
	 * @param flowrate
	 *            流量卡总流量
	 * @return CldSapReturn
	 */
	public static CldSapReturn orderCombo(int deviceCode, int productCode,
			int cust_id, int duid, String sn, int combo_code, int month,
			int charge, String iccid, int kuid, String orderno, int flowrate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("deviceCode", deviceCode);
		map.put("productCode", productCode);
		map.put("cust_id", cust_id);
		map.put("duid", duid);
		map.put("sn", sn);
		map.put("combo_code", combo_code);
		map.put("month", month);
		map.put("charge", charge);
		map.put("iccid", iccid);
		map.put("kuid", kuid);
		map.put("orderno", orderno);
		map.put("flowrate", flowrate);

		CldSapReturn errRes = CldKBaseParse.getPostParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_order_combo.php", CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	/**
	 * 获取可购买的套餐列表
	 * 
	 * @param combo_code
	 *            套餐编码
	 * @return CldSapReturn
	 */
	public static CldSapReturn getAllComboList(int combo_code) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("combo_code", combo_code);

		CldSapReturn errRes = CldKBaseParse.getGetParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_get_all_combo_list.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	/**
	 * 终端手动激活套餐
	 * 
	 * @param combo_code
	 *            套餐编码
	 * @param month
	 *            月份数
	 * @param charge
	 *            费用，单位：元
	 * @param iccid
	 *            ICCID卡号
	 * @param orderno
	 *            订单号
	 * @param flowrate
	 *            流量卡总流量
	 * @return CldSapReturn
	 */
	public static CldSapReturn activateIccidCombo(int combo_code, int month,
			int charge, String iccid, String orderno, int flowrate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("combo_code", combo_code);
		map.put("month", month);
		map.put("charge", charge);
		map.put("iccid", iccid);
		map.put("orderno", orderno);
		map.put("flowrate", flowrate);

		CldSapReturn errRes = CldKBaseParse.getPostParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_activate_iccid_combo.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	/**
	 * 更新ICCID卡对应的设备信息
	 * 
	 * @param iccid
	 *            ICCID卡号
	 * @param deviceCode
	 *            设备型号编码(运营平台定义)
	 * @param productCode
	 *            产品型号编码(运营平台定义)
	 * @param cust_id
	 *            设备客户ID
	 * @param duid
	 *            设备编号
	 * @param sn
	 *            设备唯一码
	 * @param combo_code
	 *            套餐编码
	 * @return CldSapReturn
	 */
	public static CldSapReturn updateIccidDevice(String iccid, int deviceCode,
			int productCode, int cust_id, int duid, String sn, int combo_code) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("iccid", iccid);
		map.put("deviceCode", deviceCode);
		map.put("productCode", productCode);
		map.put("cust_id", cust_id);
		map.put("duid", duid);
		map.put("sn", sn);
		map.put("combo_code", combo_code);

		CldSapReturn errRes = CldKBaseParse.getPostParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_update_iccid_device.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	/**
	 * ICCID卡套餐信息始化
	 * 
	 * @param combo_code
	 *            套餐编码
	 * @param month
	 *            月份数
	 * @param charge
	 *            费用，单位：元
	 * @param iccid
	 *            ICCID卡号
	 * @param flowrate
	 *            流量卡总流量
	 * @return CldSapReturn
	 */
	public static CldSapReturn initIccidCombo(int combo_code, int month,
			int charge, String iccid, int flowrate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("combo_code", combo_code);
		map.put("month", month);
		map.put("charge", charge);
		map.put("iccid", iccid);
		map.put("flowrate", flowrate);

		CldSapReturn errRes = CldKBaseParse.getPostParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_init_iccid_combo.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	/**
	 * ICCID套餐启用
	 * 
	 * @param iccid
	 *            ICCID卡号
	 * @param combo_code
	 *            套餐编码
	 * @param orderdate
	 *            订购日期 （YYYYMMdd）20160501
	 * @param starttime
	 *            开始时间UTC
	 * @param endtime
	 *            结束时间UTC
	 * @return CldSapReturn
	 */
	public static CldSapReturn setIccidComboEnabled(String iccid,
			int combo_code, int orderdate, int starttime, int endtime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("iccid", iccid);
		map.put("combo_code", combo_code);
		map.put("orderdate", orderdate);
		map.put("starttime", starttime);
		map.put("endtime", endtime);

		CldSapReturn errRes = CldKBaseParse.getPostParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_set_iccid_combo_enabled.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}
	
	/**
	 * ICCID套餐过期
	 * 
	 * @param iccid
	 *            ICCID卡号
	 * @param combo_code
	 *            套餐编码
	 * @param orderdate
	 *            订购日期 （YYYYMMdd）20160501
	 * @return CldSapReturn
	 */
	public static CldSapReturn setIccidComboExpired(String iccid,
			int combo_code, int orderdate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("umsaver", UMSAVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("apiver", APIVER);
		map.put("cid", CID);
		map.put("prover", PROVER);
		map.put("encrypt", ENCRYPT);
		map.put("iccid", iccid);
		map.put("combo_code", combo_code);
		map.put("orderdate", orderdate);

		CldSapReturn errRes = CldKBaseParse.getPostParms(map, getComboHeadUrl()
				+ "kgo/api/kgo_set_iccid_combo_expired.php",
				CldSapKAppCenter.getKgoKey());
		return errRes;
	}

	private static String getComboHeadUrl() {
		return "";
	}
}
