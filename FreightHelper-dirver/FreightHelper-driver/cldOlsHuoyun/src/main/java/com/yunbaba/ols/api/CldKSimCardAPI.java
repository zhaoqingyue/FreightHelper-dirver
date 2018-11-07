package com.yunbaba.ols.api;

import com.yunbaba.ols.bll.CldKSimCard;
import com.yunbaba.ols.tools.CldSapReturn;

/**
 * sim卡相关模块，提供sim卡相关接口
 * 
 * @author zhaoqy
 */
public class CldKSimCardAPI {

	private static CldKSimCardAPI cldKSimCardAPI;

	public static CldKSimCardAPI getInstance() {
		if (cldKSimCardAPI == null) {
			cldKSimCardAPI = new CldKSimCardAPI();
		}
		return cldKSimCardAPI;
	}

	private CldKSimCardAPI() {
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
	 */
	public void getSimCardStatus(final String iccid, final String imsi,
			final String sim, final String sn, final String ver,
			final ISimCardAPIListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKSimCard.getInstance()
						.getSimCardStatus(iccid, imsi, sim, sn, ver);

				if (listener != null && errRes != null) {
					listener.onResult(errRes.errCode, errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void checkSimCard(final String iccid, final String imsi,
			final String sim, final String sn, final String ver,
			final long duid, final long kuid, final ISimCardAPIListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKSimCard.getInstance().checkSimCard(
						iccid, imsi, sim, sn, ver, duid, kuid);

				if (listener != null && errRes != null) {
					listener.onResult(errRes.errCode, errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void registerSimCard(final String iccid, final String imsi,
			final String sim, final String sn, final String ver,
			final long duid, final long kuid, final long dcode,
			final long pcode, final long custid,
			final ISimCardAPIListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKSimCard.getInstance()
						.registerSimCard(iccid, imsi, sim, sn, ver, duid, kuid,
								dcode, pcode, custid);

				if (listener != null && errRes != null) {
					listener.onResult(errRes.errCode, errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void checkPayStatus(final String iccid, final String imsi,
			final String sim, final String sn, final String ver,
			final long duid, final long kuid, final long getordertime,
			final ISimCardAPIListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKSimCard.getInstance().checkPayStatus(
						iccid, imsi, sim, sn, ver, duid, kuid, getordertime);

				if (listener != null && errRes != null) {
					listener.onResult(errRes.errCode, errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void checkHeartbeat(final String iccid, final String sim,
			final String sn, final String ver, final long update,
			final ISimCardAPIListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKSimCard.getInstance().checkHeartbeat(
						iccid, sim, sn, ver, update);

				if (listener != null && errRes != null) {
					listener.onResult(errRes.errCode, errRes.jsonReturn);
				}
			}
		}).start();
	}

	/**
	 * sim卡回调监听
	 * 
	 * @author zhaoqy
	 * @date 2017-2-10
	 */
	public static interface ISimCardAPIListener {
		/**
		 * 回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onResult(int errCode, String jsonString);
	}
}
