package com.yunbaba.ols.bll;

import com.cld.device.CldPhoneNet;
import com.yunbaba.ols.sap.CldSapKCombo;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;
import com.yunbaba.ols.tools.CldErrUtil;
import com.yunbaba.ols.tools.CldSapNetUtil;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;

public class CldKCombo {

	private static CldKCombo cldKCombo;

	private CldKCombo() {

	}

	public static CldKCombo getInstance() {
		if (cldKCombo == null) {
			cldKCombo = new CldKCombo();
		}
		return cldKCombo;
	}

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
	public CldSapReturn getComboList(int systemCode, int deviceCode,
			int productCode, int width, int height, String iccid, int kuid,
			String session, int appid, int bussinessid) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.getComboList(systemCode, deviceCode,
					productCode, width, height, iccid, kuid, session, appid,
					bussinessid);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
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
	public CldSapReturn getUserComboList(int systemCode, int deviceCode,
			int productCode, int width, int height, String iccid, int kuid,
			String session, int appid, int bussinessid) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.getUserComboList(systemCode, deviceCode,
					productCode, width, height, iccid, kuid, session, appid,
					bussinessid);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
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
	public CldSapReturn getUserComboCount(String iccid, int kuid,
			String session, int appid, int bussinessid) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.getUserComboCount(iccid, kuid, session,
					appid, bussinessid);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
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
	public CldSapReturn getServiceApp(String iccid, int kuid,
			String session, int appid, int bussinessid) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.getServiceApp(iccid, kuid, session, appid,
					bussinessid);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
		return errRes;
	}

	/**
	 * 更新套餐购买次数
	 * 
	 * @param combo_code
	 *            套餐编码
	 * @return CldSapReturn
	 */
	public CldSapReturn getUpdateComboPayTimes(int combo_code) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.getUpdateComboPayTimes(combo_code);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
		return errRes;
	}

	/**
	 * 获取套餐的提醒设置
	 * 
	 * @param combo_code
	 *            套餐编码
	 * @return CldSapReturn
	 */
	public CldSapReturn getComboAlarmSetting(int combo_code) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.getComboAlarmSetting(combo_code);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
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
	public CldSapReturn orderCombo(int deviceCode, int productCode,
			int cust_id, int duid, String sn, int combo_code, int month,
			int charge, String iccid, int kuid, String orderno, int flowrate) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.orderCombo(deviceCode, productCode, cust_id,
					duid, sn, combo_code, month, charge, iccid, kuid, orderno,
					flowrate);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
						errRes.jsonPost);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
		return errRes;
	}

	/**
	 * 获取可购买的套餐列表
	 * 
	 * @param combo_code
	 *            套餐编码
	 * @return CldSapReturn
	 */
	public CldSapReturn getAllComboList(int combo_code) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.getAllComboList(combo_code);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
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
	public CldSapReturn activateIccidCombo(int combo_code, int month,
			int charge, String iccid, String orderno, int flowrate) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.activateIccidCombo(combo_code, month, charge,
					iccid, orderno, flowrate);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
						errRes.jsonPost);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
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
	public CldSapReturn updateIccidDevice(String iccid, int deviceCode,
			int productCode, int cust_id, int duid, String sn, int combo_code) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.updateIccidDevice(iccid, deviceCode,
					productCode, cust_id, duid, sn, combo_code);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
						errRes.jsonPost);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
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
	public CldSapReturn initIccidCombo(int combo_code, int month,
			int charge, String iccid, int flowrate) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.initIccidCombo(combo_code, month, charge,
					iccid, flowrate);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
						errRes.jsonPost);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
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
	public CldSapReturn setIccidComboEnabled(String iccid,
			int combo_code, int orderdate, int starttime, int endtime) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.setIccidComboEnabled(iccid, combo_code,
					orderdate, starttime, endtime);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
						errRes.jsonPost);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
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
	public CldSapReturn setIccidComboExpired(String iccid,
			int combo_code, int orderdate) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCombo.setIccidComboExpired(iccid, combo_code,
					orderdate);

			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
						errRes.jsonPost);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
		return errRes;
	}
}
