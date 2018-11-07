package com.yunbaba.ols.sap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.cld.log.CldLog;
import com.yunbaba.ols.bll.CldBllUtil;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;

public class CldSapKSimCard {

	/** 公共输入参数. */
	private final static int APIVER = 1;

	/**
	 * 获取充值卡状态信息
	 * 
	 * @param iccid
	 *            ICCID号
	 * @param imsi
	 *            IMSI号
	 * @param sim
	 *            sim卡号
	 * @param sn
	 *            序列号-特征码
	 * @param ver
	 *            版本号第一段（设备编号如K3618）
	 * @return CldSapReturn
	 */
	public static CldSapReturn getSimCardStatus(String iccid, String imsi,
			String sim, String sn, String ver) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("iccid", iccid);
		map.put("imsi", imsi);
		map.put("sim", sim);
		map.put("sn", sn);
		map.put("ver", ver);

		CldSapReturn errRes = new CldSapReturn();
		errRes.url = getPostParms(map, getFlowHeadUrl()
				+ "?mod=iov&ac=getcardstatus&", getFlowKey());
		return errRes;
	}

	/**
	 * 查询sim卡激活状态(用于检查充值卡是否非法)
	 * 
	 * @param iccid
	 *            ICCID号
	 * @param imsi
	 *            IMSI号
	 * @param sim
	 *            sim卡号
	 * @param sn
	 *            序列号-特征码
	 * @param ver
	 *            版本号第一段（设备编号如K3618）
	 * @param duid
	 *            设备唯一id
	 * @param kuid
	 *            用户唯一id
	 * @return CldSapReturn
	 */
	public static CldSapReturn checkSimCard(String iccid, String imsi,
			String sim, String sn, String ver, long duid, long kuid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("iccid", iccid);
		map.put("imsi", imsi);
		map.put("sim", sim);
		map.put("sn", sn);
		map.put("ver", ver);
		map.put("duid", duid);
		map.put("kuid", kuid);

		CldSapReturn errRes = new CldSapReturn();
		errRes.url = getPostParms(map, getFlowHeadUrl()
				+ "?mod=iov&ac=checkcard&", getFlowKey());
		return errRes;
	}

	/**
	 * 充值卡首次登记--服务激活
	 * 
	 * @param iccid
	 *            ICCID号
	 * @param imsi
	 *            IMSI号
	 * @param sim
	 *            sim卡号
	 * @param sn
	 *            序列号-特征码
	 * @param ver
	 *            版本号第一段（设备编号如K3618）
	 * @param duid
	 *            设备唯一id
	 * @param kuid
	 *            用户唯一id
	 * @param dcode
	 *            设备型号编码 (运营平台定义)
	 * @param pcode
	 *            产品型号编码 (运营平台定义)
	 * @param custid
	 *            设备客户ID
	 * @return
	 */
	public static CldSapReturn registerSimCard(String iccid, String imsi,
			String sim, String sn, String ver, long duid, long kuid,
			long dcode, long pcode, long custid) {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("apiver", APIVER);
		map.put("iccid", iccid);
		map.put("imsi", imsi);
		map.put("sim", sim);
		map.put("sn", sn);
		map.put("ver", ver);
		map.put("duid", duid);
		map.put("kuid", kuid);
		map.put("dcode", dcode);
		map.put("pcode", pcode);
		map.put("custid", custid);

		CldSapReturn errRes = new CldSapReturn();
		errRes.url = getPostParms(map, getFlowHeadUrl()
				+ "?mod=iov&ac=registercard&", getFlowKey());
		return errRes;
	}

	/**
	 * 查询续费支付状态
	 * 
	 * @param iccid
	 *            ICCID号
	 * @param imsi
	 *            IMSI号
	 * @param sim
	 *            sim卡号
	 * @param sn
	 *            序列号-特征码
	 * @param ver
	 *            版本号第一段（设备编号如K3618）
	 * @param duid
	 *            设备唯一id
	 * @param kuid
	 *            用户唯一id
	 * @param getordertime
	 *            订单生成请求时间戳做唯一标识
	 * @return CldSapReturn
	 */
	public static CldSapReturn checkPayStatus(String iccid, String imsi,
			String sim, String sn, String ver, long duid, long kuid,
			long getordertime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("iccid", iccid);
		map.put("imsi", imsi);
		map.put("sim", sim);
		map.put("sn", sn);
		map.put("ver", ver);
		map.put("duid", duid);
		map.put("kuid", kuid);
		map.put("getordertime", getordertime);

		CldSapReturn errRes = new CldSapReturn();
		;
		errRes.url = getPostParms(map, getFlowHeadUrl()
				+ "?mod=iov&ac=checkcardpay&", getFlowKey());
		return errRes;
	}

	/**
	 * 心跳服务-监控终端状态
	 * 
	 * @param iccid
	 *            ICCID号
	 * @param sim
	 *            sim卡号
	 * @param sn
	 *            序列号-特征码
	 * @param ver
	 *            版本号第一段（设备编号如K3618）
	 * @param update
	 *            终端上传时间戳
	 * @return CldSapReturn
	 */
	public static CldSapReturn checkHeartbeat(String iccid, String sim,
			String sn, String ver, long update) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("iccid", iccid);
		map.put("sim", sim);
		map.put("sn", sn);
		map.put("ver", ver);
		map.put("update", update + "");

		CldSapReturn errRes = new CldSapReturn();;
		errRes.url = getPostParms(map, getFlowHeadUrl()
				+ "?mod=iov&ac=checkheart&", getFlowKey());
		return errRes;
	}

	private static String getFlowHeadUrl() {
		if (CldBllUtil.getInstance().isTestVersion()) {
			return "http://test.careland.com.cn/kldjy/www/";
		} else {
			return "http://navione.careland.com.cn/";
		}
	}

	private static String getFlowKey() {
		return "1a86fb49b070f26d7948d7931ed69233";
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("rawtypes")
	private static String getPostParms(Map<String, Object> map, String urlHead,
			String key) {
		String url = "";
		String urlSource = "";
		String md5Source = "";

		if (null != map) {
			int size = map.size();
			String[] parms = new String[size];
			Iterator<?> iter = map.entrySet().iterator();
			int i = 0;
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				parms[i] = (String) entry.getKey();
				i++;
			}
			CldSapParser.BubbleSort.sort(parms);

			for (i = 0; i < parms.length; i++) {
				if (i != 0) {
					if (!TextUtils.isEmpty(parms[i])) {
						if (map.get(parms[i]) != null
								&& !map.get(parms[i]).toString().isEmpty()) {
							md5Source = md5Source + "&" + parms[i] + "="
									+ map.get(parms[i]);
						}
					}
				} else if (!TextUtils.isEmpty(parms[i])) {
					if (map.get(parms[i]) != null
							&& !map.get(parms[i]).toString().isEmpty()) {
						md5Source = md5Source + parms[i] + "="
								+ map.get(parms[i]);
					}
				}
			}

			for (i = 0; i < parms.length; i++) {
				if (i != 0) {
					if (!TextUtils.isEmpty(parms[i])) {
						if (map.get(parms[i]) != null) {
							urlSource = urlSource
									+ "&"
									+ parms[i]
									+ "="
									+ CldSapUtil.getUrlEncodeString(map.get(
											parms[i]).toString());
						} else {
							urlSource = urlSource + "&" + parms[i] + "=" + "";
						}
					}
				} else {
					if (!TextUtils.isEmpty(parms[i])) {
						if (map.get(parms[i]) != null) {
							urlSource = urlSource
									+ parms[i]
									+ "="
									+ CldSapUtil.getUrlEncodeString(map.get(
											parms[i]).toString());
						} else {
							urlSource = urlSource + "&" + parms[i] + "=" + "";
						}
					}
				}
			}
		}

		if (!TextUtils.isEmpty(key)) {
			md5Source = md5Source + key;
			CldLog.i("ols", "md5Source = " + md5Source);
			String sign = CldSapParser.MD5(md5Source);
			url = urlHead + urlSource + "&sign=" + sign;
		} else {
			url = urlHead + urlSource;
		}

		return url;
	}
}
