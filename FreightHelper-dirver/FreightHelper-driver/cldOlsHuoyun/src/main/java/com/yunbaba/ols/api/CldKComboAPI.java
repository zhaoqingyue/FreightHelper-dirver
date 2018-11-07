package com.yunbaba.ols.api;

import com.yunbaba.ols.bll.CldKCombo;
import com.yunbaba.ols.tools.CldSapReturn;

/**
 * 套餐相关模块，提供套餐相关接口
 * 
 * @author zhaoqy
 */
public class CldKComboAPI {
	private static CldKComboAPI cldKSimCardAPI;
	private ICldKComboAPIListener listener;

	private CldKComboAPI() {

	}

	public static CldKComboAPI getInstance() {
		if (cldKSimCardAPI == null) {
			cldKSimCardAPI = new CldKComboAPI();
		}
		return cldKSimCardAPI;
	}

	/**
	 * 设置回调监听（首次设置有效）
	 * 
	 * @param listener
	 *            回调监听
	 * @return int (0 设置成功，-1 已有设置失败)
	 */
	public int setCldKComboListener(ICldKComboAPIListener listener) {
		if (null == this.listener) {
			this.listener = listener;
			return 0;
		} else {
			return -1;
		}
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
	 */
	public void getComboList(final int systemCode, final int deviceCode,
			final int productCode, final int width, final int height,
			final String iccid, final int kuid, final String session,
			final int appid, final int bussinessid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance().getComboList(
						systemCode, deviceCode, productCode, width, height,
						iccid, kuid, session, appid, bussinessid);

				if (listener != null && errRes != null) {
					listener.onGetComboListResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void getUserComboList(final int systemCode, final int deviceCode,
			final int productCode, final int width, final int height,
			final String iccid, final int kuid, final String session,
			final int appid, final int bussinessid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance().getUserComboList(
						systemCode, deviceCode, productCode, width, height,
						iccid, kuid, session, appid, bussinessid);
				if (listener != null && errRes != null) {
					listener.onGetUserComboListResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void getUserComboCount(final String iccid, final int kuid,
			final String session, final int appid, final int bussinessid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance()
						.getUserComboCount(iccid, kuid, session, appid,
								bussinessid);

				if (listener != null && errRes != null) {
					listener.onGetUserComboCountResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void getServiceApp(final String iccid, final int kuid,
			final String session, final int appid, final int bussinessid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance().getServiceApp(
						iccid, kuid, session, appid, bussinessid);

				if (listener != null && errRes != null) {
					listener.onGetServiceAppResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
	}

	/**
	 * 更新套餐购买次数
	 * 
	 * @param combo_code
	 *            套餐编码
	 */
	public void getUpdateComboPayTimes(final int combo_code) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance()
						.getUpdateComboPayTimes(combo_code);

				if (listener != null && errRes != null) {
					listener.onGetUpdateComboPayTimesResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
	}

	/**
	 * 获取套餐的提醒设置
	 * 
	 * @param combo_code
	 *            套餐编码
	 */
	public void getComboAlarmSetting(final int combo_code) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance()
						.getComboAlarmSetting(combo_code);

				if (listener != null && errRes != null) {
					listener.onGetComboAlarmSettingResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void orderCombo(final int deviceCode, final int productCode,
			final int cust_id, final int duid, final String sn,
			final int combo_code, final int month, final int charge,
			final String iccid, final int kuid, final String orderno,
			final int flowrate) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance().orderCombo(
						deviceCode, productCode, cust_id, duid, sn, combo_code,
						month, charge, iccid, kuid, orderno, flowrate);

				if (listener != null && errRes != null) {
					listener.onOrderComboResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
	}

	/**
	 * 获取可购买的套餐列表
	 * 
	 * @param combo_code
	 *            套餐编码
	 */
	public void getAllComboList(final int combo_code) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance().getAllComboList(
						combo_code);

				if (listener != null && errRes != null) {
					listener.onGetAllComboListResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void activateIccidCombo(final int combo_code, final int month,
			final int charge, final String iccid, final String orderno,
			final int flowrate) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance()
						.activateIccidCombo(combo_code, month, charge, iccid,
								orderno, flowrate);

				if (listener != null && errRes != null) {
					listener.onActivateIccidComboResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void updateIccidDevice(final String iccid, final int deviceCode,
			final int productCode, final int cust_id, final int duid,
			final String sn, final int combo_code) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance()
						.updateIccidDevice(iccid, deviceCode, productCode,
								cust_id, duid, sn, combo_code);

				if (listener != null && errRes != null) {
					listener.onUpdateIccidDeviceResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void initIccidCombo(final int combo_code, final int month,
			final int charge, final String iccid, final int flowrate) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance().initIccidCombo(
						combo_code, month, charge, iccid, flowrate);

				if (listener != null && errRes != null) {
					listener.onInitIccidComboResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void setIccidComboEnabled(final String iccid, final int combo_code,
			final int orderdate, final int starttime, final int endtime) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance()
						.setIccidComboEnabled(iccid, combo_code, orderdate,
								starttime, endtime);

				if (listener != null && errRes != null) {
					listener.onSetIccidComboEnabledResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
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
	 */
	public void setIccidComboExpired(final String iccid,
			final int combo_code, final int orderdate) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKCombo.getInstance()
						.setIccidComboExpired(iccid, combo_code, orderdate);

				if (listener != null && errRes != null) {
					listener.onSetIccidComboExpiredResult(errRes.errCode,
							errRes.jsonReturn);
				}
			}
		}).start();
	}

	/**
	 * 套餐回调监听
	 * 
	 * @author zhaoqy
	 * @date 2017-2-10
	 */
	public static interface ICldKComboAPIListener {
		/**
		 * 获取可购买的套餐列表回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onGetComboListResult(int errCode, String jsonString);

		/**
		 * 获取用户已经可购买过的套餐列表回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onGetUserComboListResult(int errCode, String jsonString);

		/**
		 * 获取用户已经可购买过的套餐数量回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onGetUserComboCountResult(int errCode, String jsonString);

		/**
		 * 获取用户已经可购买过的套餐服务回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onGetServiceAppResult(int errCode, String jsonString);

		/**
		 * 更新套餐购买次数回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onGetUpdateComboPayTimesResult(int errCode,
				String jsonString);

		/**
		 * 获取套餐的提醒设置回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onGetComboAlarmSettingResult(int errCode, String jsonString);

		/**
		 * 购买套餐回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onOrderComboResult(int errCode, String jsonString);

		/**
		 * 获取可购买的套餐列表回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onGetAllComboListResult(int errCode, String jsonString);

		/**
		 * 终端手动激活套餐回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onActivateIccidComboResult(int errCode, String jsonString);

		/**
		 * 更新ICCID卡对应的设备信息回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onUpdateIccidDeviceResult(int errCode, String jsonString);

		/**
		 * ICCID卡套餐信息始化回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onInitIccidComboResult(int errCode, String jsonString);

		/**
		 * ICCID套餐启用回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onSetIccidComboEnabledResult(int errCode, String jsonString);
		
		/**
		 * ICCID套餐过期回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onSetIccidComboExpiredResult(int errCode, String jsonString);
	}
}
