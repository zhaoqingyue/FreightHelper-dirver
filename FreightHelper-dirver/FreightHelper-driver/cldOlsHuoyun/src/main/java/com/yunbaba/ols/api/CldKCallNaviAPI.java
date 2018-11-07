/*
 * @Title CldKCallNaviAPI.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.ols.api;

import java.util.List;
import com.yunbaba.ols.api.CldOlsInit.ICldOlsInitListener;
import com.yunbaba.ols.bll.CldKCallNavi;
import com.yunbaba.ols.bll.CldKMessage;
import com.yunbaba.ols.dal.CldDalKCallNavi;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareAKeyCallParm;
import com.yunbaba.ols.tools.CldSapReturn;



/**
 * 一键通相关模块，提供一键通相关功能
 * 
 * @author Zhouls
 * @date 2015-3-5 下午3:18:44
 */
public class CldKCallNaviAPI {
	/** 一键通回调监听，初始化时设置一次 */
	private ICldKCallNaviListener listener;

	private static CldKCallNaviAPI cldKCallNaviAPI;

	private CldKCallNaviAPI() {

	}

	public static CldKCallNaviAPI getInstance() {
		if (null == cldKCallNaviAPI) {
			cldKCallNaviAPI = new CldKCallNaviAPI();
		}
		return cldKCallNaviAPI;
	}

	/**
	 * 初始化密钥
	 * 
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2015-2-12 下午6:05:49
	 */
	public void initKey(final ICldOlsInitListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				CldKCallNavi.getInstance().initKey();
				if (null != listener) {
					listener.onInitReslut();
				}
			}
		}).start();
	}

	/**
	 * 一键通初始化(登陆后调用)
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:19:06
	 */
	public void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldKCallNavi.getInstance().getBindMobile();
			}
		}).start();
	}

	/**
	 * 反初始化
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:19:19
	 */
	public void uninit() {

	}

	/**
	 * 设置一键通回调监听
	 * 
	 * @param listener
	 *            一键通回调
	 * @return (0 设置成功，-1已有设置失败)
	 * @return int
	 * @author Zhouls
	 * @date 2015-3-5 下午3:19:30
	 */
	public int setCldKCallNaviListener(ICldKCallNaviListener listener) {
		if (null == this.listener) {
			this.listener = listener;
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 获取绑定手机号码
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:19:45
	 */
	public void getBindMobile() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKCallNavi.getInstance()
						.getBindMobile();
				if (null != listener) {
					listener.onGetBindMobileResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 获取一键通手机验证码
	 * 
	 * @param mobile
	 *            手机号
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:19:54
	 */
	public void getIdentifycode(final String mobile) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKCallNavi.getInstance()
						.getIdentifycode(mobile);
				if (null != listener) {
					listener.onGetIdentifycodeResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 绑定其他手机号接口
	 * 
	 * @param identifycode
	 *            验证码
	 * @param mobile
	 *            手机号码
	 * @param replacemobile
	 *            需要替换掉的旧号码 ，不为空则替换相应号码；为空则增加mobile
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:20:07
	 */
	public void bindToMobile(final String identifycode, final String mobile,
			final String replacemobile) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKCallNavi.getInstance().bindToMobile(
						identifycode, mobile, replacemobile);
				if (null != listener) {
					listener.onBindToMobileResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 上报位置
	 * 
	 * @param mobile
	 *            手机号,如为空，则按kuid绑定的所有手机号都上报位置
	 * @param longitude
	 *            WGS84 经度，单位：度
	 * @param latitude
	 *            WGS84 纬度，单位：度
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:20:23
	 */
	public void upLocation(final String mobile, final double longitude,
			final double latitude) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKMessage.getInstance().upLocation(
						mobile, longitude, latitude);
				if (null != listener) {
					listener.onUpLocationResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 获取一键通消息
	 * 
	 * @param mobile
	 *            手机号，如果传了手机号，则只会查询指定的手机号,否则轮循所有手机号
	 * @param longitude
	 *            WGS84 经度，单位：度
	 * @param latitude
	 *            WGS84 纬度，单位：度
	 * @param isLoop
	 *            是否轮询多个手机号
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:20:39
	 */
	public void recPptMsg(final String mobile, final double longitude,
			final double latitude, final boolean isLoop) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKMessage.getInstance().recPptMsg(
						mobile, longitude, latitude, isLoop);
				if (null != listener) {
					listener.onRecPptMsgResult(errRes.errCode, CldDalKCallNavi
							.getInstance().getMsgList());
				}
			}
		}).start();
	}

	/**
	 * 注册一键通帐号
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:20:53
	 */
	public void registerByKuid() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKCallNavi.getInstance()
						.registerByKuid();
				if (null != listener) {
					listener.onRegisterResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 删除绑定手机号
	 * 
	 * @param mobile
	 *            手机号码
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:21:03
	 */
	public void delBindMobile(final String mobile) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKCallNavi.getInstance().delBindMobile(
						mobile);
				if (null != listener) {
					listener.onDelMobileResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 获取绑定的手机号
	 * 
	 * @return
	 * @return String 之前调用过获取绑定手机号码则返回之前的号码，否则返回null 再调用获取绑定手机号接口
	 * @author Zhouls
	 * @date 2015-3-5 下午3:21:17
	 */
	public String getPptMobile() {
		if (CldDalKCallNavi.getInstance().getMobiles().size() > 0) {
			return CldDalKCallNavi.getInstance().getMobiles().get(0);
		} else {
			return null;
		}
	}

	/**
	 * 获取绑定的手机号列表
	 * 
	 * @return
	 * @return List<String>
	 * @author Zhouls
	 * @date 2015-3-5 下午3:21:37
	 */
	public List<String> getPptMobileList() {
		return CldDalKCallNavi.getInstance().getMobiles();
	}

	/**
	 * 更新绑定的手机号
	 * 
	 * @param mobiles
	 *            更新的手机号
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:21:49
	 */
	public void updateBindMobile(List<String> mobiles) {
		CldDalKCallNavi.getInstance().setMobiles(mobiles);
	}

	/**
	 * 一键通回调监听
	 * 
	 * @author Zhouls
	 * @date 2015-3-5 下午3:22:04
	 */
	public static interface ICldKCallNaviListener {

		/**
		 * 注册一键通回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:22:16
		 */
		public void onRegisterResult(int errCode);

		/**
		 * 获取绑定手机号码回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:22:29
		 */
		public void onGetBindMobileResult(int errCode);

		/**
		 * 获取一键通手机验证码回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:22:38
		 */
		public void onGetIdentifycodeResult(int errCode);

		/**
		 * 绑定其他手机号回调
		 * 
		 * @param errCode
		 *            0成功 405验证码错误
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:22:47
		 */
		public void onBindToMobileResult(int errCode);

		/**
		 * 删除手机号回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:23:10
		 */
		public void onDelMobileResult(int errCode);

		/**
		 * 上报位置回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:23:19
		 */
		public void onUpLocationResult(int errCode);

		/**
		 * 接收一键通消息回调
		 * 
		 * @param errCode
		 * @param list
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:23:29
		 */
		public void onRecPptMsgResult(int errCode, List<ShareAKeyCallParm> list);
	}
}
