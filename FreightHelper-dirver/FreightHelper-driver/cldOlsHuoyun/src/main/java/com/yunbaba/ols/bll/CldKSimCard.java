package com.yunbaba.ols.bll;

import com.cld.device.CldPhoneNet;
import com.yunbaba.ols.sap.CldSapKSimCard;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;
import com.yunbaba.ols.tools.CldErrUtil;
import com.yunbaba.ols.tools.CldSapNetUtil;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;

public class CldKSimCard {

	private static CldKSimCard cldKSimCard;

	private CldKSimCard() {

	}

	public static CldKSimCard getInstance() {
		if (cldKSimCard == null) {
			cldKSimCard = new CldKSimCard();
		}
		return cldKSimCard;
	}

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
	public CldSapReturn getSimCardStatus(String iccid, String imsi, String sim,
			String sn, String ver) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKSimCard.getSimCardStatus(iccid, imsi, sim, sn, ver);
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
	public CldSapReturn checkSimCard(String iccid, String imsi, String sim,
			String sn, String ver, long duid, long kuid) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKSimCard.checkSimCard(iccid, imsi, sim, sn, ver,
					duid, kuid);
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
	 * @return CldSapReturn
	 */
	public CldSapReturn registerSimCard(String iccid, String imsi, String sim,
			String sn, String ver, long duid, long kuid, long dcode,
			long pcode, long custid) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKSimCard.registerSimCard(iccid, imsi, sim, sn, ver,
					duid, kuid, dcode, pcode, custid);
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
	public CldSapReturn checkPayStatus(String iccid, String imsi, String sim,
			String sn, String ver, long duid, long kuid, long getordertime) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKSimCard.checkPayStatus(iccid, imsi, sim, sn, ver,
					duid, kuid, getordertime);
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
	public CldSapReturn checkHeartbeat(String iccid, String sim, String sn,
			String ver, long update) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKSimCard.checkHeartbeat(iccid, sim, sn, ver, update);
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
