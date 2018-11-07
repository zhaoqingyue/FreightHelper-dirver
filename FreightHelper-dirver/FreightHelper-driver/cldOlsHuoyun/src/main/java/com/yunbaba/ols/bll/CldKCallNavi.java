/*
 * @Title CldKCallNavi.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.ols.bll;

import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;
import com.cld.device.CldPhoneNet;
import com.cld.log.CldLog;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.dal.CldDalKAccount;
import com.yunbaba.ols.dal.CldDalKCallNavi;
import com.yunbaba.ols.sap.CldSapKCallNavi;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtKeyCode;
import com.yunbaba.ols.sap.parse.CldKCallNaviParse.ProtKCallMobile;
import com.yunbaba.ols.tools.CldErrUtil;
import com.yunbaba.ols.tools.CldSapNetUtil;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;


/**
 * 
 * 一键通
 * 
 * @author Zhouls
 * @date 2015-4-8 下午4:35:23
 */
public class CldKCallNavi {

	private static CldKCallNavi cldKCallNavi;

	/**
	 * Instantiates a new cld k call navi.
	 */
	private CldKCallNavi() {

	}

	/**
	 * Gets the single instance of CldKCallNavi.
	 * 
	 * @return single instance of CldKCallNavi
	 */
	public static CldKCallNavi getInstance() {
		if (cldKCallNavi == null)
			cldKCallNavi = new CldKCallNavi();
		return cldKCallNavi;
	}

	/**
	 * Uninit.
	 */
	public void uninit() {
		CldDalKCallNavi.getInstance().uninit();
	}

	/**
	 * 初始化密钥
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 下午4:20:09
	 */
	public void initKey() {
		String cldKCallKey = CldDalKCallNavi.getInstance().getCldKCallKey();
		// 只要没有中断登录
		int slpTimer = 5;// 休眠周期
		while (TextUtils.isEmpty(cldKCallKey)) {
			int sleepSc = 0;// 休眠秒数
			if (!CldPhoneNet.isNetConnected()) {
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
					// TODO: handle exception
				}
				continue;
			}
			CldSapReturn errRes = CldSapKCallNavi.initKeyCode();
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			ProtKeyCode protKeyCode = CldSapParser.parseJson(strRtn,
					ProtKeyCode.class, errRes);
			CldLog.d("ols", errRes.errCode + "_initKCallKey");
			CldLog.d("ols", errRes.errMsg + "_initKCallKey");
			CldErrUtil.handleErr(errRes);
			if (null != protKeyCode && errRes.errCode == 0
					&& !TextUtils.isEmpty(protKeyCode.getCode())) {
				cldKCallKey = protKeyCode.getCode();
				CldDalKCallNavi.getInstance().setCldKCallKey(cldKCallKey);
			} else {
				try {
					while (sleepSc < slpTimer) {
						Thread.sleep(1000);
						sleepSc++;
					}
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
			if (slpTimer >= 30) {
				/**
				 * 超过30S以后固定30S周期
				 */
				slpTimer = 30;
			} else {
				/**
				 * 不足30S每次加5S
				 */
				slpTimer += 5;
			}
		}
		CldSapKCallNavi.keyCode = cldKCallKey;
	}

	/**
	 * 注册一键通帐号
	 * 
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:20:27
	 */
	public CldSapReturn registerByKuid() {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCallNavi.registerByKuid(CldBllUtil.getInstance()
					.getApptype(), CldBllUtil.getInstance().getProver(),
					CldDalKAccount.getInstance().getKuid(), CldDalKAccount
							.getInstance().getSession(), CldBllUtil
							.getInstance().getAppid(), CldBllUtil.getInstance()
							.getBussinessid());
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
			CldLog.d("ols", errRes.errCode + "_pptRegByKuid");
			CldLog.d("ols", errRes.errMsg + "_pptRegByKuid");
			CldErrUtil.handleErr(errRes);
			errCodeFix(errRes);
		}
		return errRes;
	}

	/**
	 * 获取一键通手机验证码
	 * 
	 * @param mobile
	 *            手机号
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:20:36
	 */
	public CldSapReturn getIdentifycode(String mobile) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCallNavi.getIdentifycode(CldBllUtil.getInstance()
					.getApptype(), CldBllUtil.getInstance().getProver(),
					CldDalKAccount.getInstance().getKuid(), CldDalKAccount
							.getInstance().getSession(), CldBllUtil
							.getInstance().getAppid(), CldBllUtil.getInstance()
							.getBussinessid(), mobile);
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
			CldLog.d("ols", errRes.errCode + "_ppt_getIden");
			CldLog.d("ols", errRes.errMsg + "_ppt_getIden");
			CldErrUtil.handleErr(errRes);
			errCodeFix(errRes);
		}
		return errRes;
	}

	/**
	 * 获取绑定手机号码
	 * 
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-12 下午3:24:56
	 */
	public CldSapReturn getBindMobile() {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCallNavi.getBindMobile(CldBllUtil.getInstance()
					.getApptype(), CldBllUtil.getInstance().getProver(),
					CldDalKAccount.getInstance().getKuid(), CldDalKAccount
							.getInstance().getSession(), CldBllUtil
							.getInstance().getAppid(), CldBllUtil.getInstance()
							.getBussinessid());
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			ProtKCallMobile protKCallMobile = CldSapParser.parseJson(strRtn,
					ProtKCallMobile.class, errRes);
			CldLog.d("ols", errRes.errCode + "_ppt_mobiles");
			CldLog.d("ols", errRes.errMsg + "_ppt_mobiles");
			CldErrUtil.handleErr(errRes);
			errCodeFix(errRes);
			if (null != protKCallMobile) {
				if (errRes.errCode == 0) {
					CldDalKCallNavi.getInstance().setMobiles(
							protKCallMobile.getData());
				}
			}
		}
		return errRes;
	}

	/**
	 * 绑定其他手机号
	 * 
	 * @param identifycode
	 *            验证码
	 * @param mobile
	 *            手机号码
	 * @param replacemobile
	 *            要替换的旧号码， 不为空则替换绑定的旧号码；为空则增加新绑定号码
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:20:49
	 */
	public CldSapReturn bindToMobile(String identifycode, String mobile,
			String replacemobile) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCallNavi.bindToMobile(CldBllUtil.getInstance()
					.getApptype(), CldBllUtil.getInstance().getProver(),
					CldDalKAccount.getInstance().getKuid(), CldDalKAccount
							.getInstance().getSession(), CldBllUtil
							.getInstance().getAppid(), CldBllUtil.getInstance()
							.getBussinessid(), identifycode, mobile,
					replacemobile);
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
			CldLog.d("ols", errRes.errCode + "_pptBindMobile");
			CldLog.d("ols", errRes.errMsg + "_pptBindMobile");
			CldErrUtil.handleErr(errRes);
			errCodeFix(errRes);
			if (errRes.errCode == 0) {
				List<String> mobiles = new ArrayList<String>();
				mobiles.add(mobile);
				CldDalKCallNavi.getInstance().setMobiles(mobiles);
			}
		}
		return errRes;
	}

	/**
	 * 删除绑定手机号
	 * 
	 * @param mobile
	 *            手机号码
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:21:07
	 */
	public CldSapReturn delBindMobile(String mobile) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKCallNavi.delBindMobile(CldBllUtil.getInstance()
					.getApptype(), CldBllUtil.getInstance().getProver(),
					CldDalKAccount.getInstance().getKuid(), CldDalKAccount
							.getInstance().getSession(), CldBllUtil
							.getInstance().getAppid(), CldBllUtil.getInstance()
							.getBussinessid(), mobile);
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
			CldLog.d("ols", errRes.errCode + "_pptDelMobile");
			CldLog.d("ols", errRes.errMsg + "_pptDelMobile");
			CldErrUtil.handleErr(errRes);
			errCodeFix(errRes);
		}
		return errRes;
	}

	/**
	 * 错误码处理
	 * 
	 * @param res
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 下午4:21:22
	 */
	public void errCodeFix(CldSapReturn res) {
		switch (res.errCode) {
		case 402: {
			/**
			 * 密钥错误
			 */
			CldDalKCallNavi.getInstance().setCldKCallKey("");
			initKey();
		}
			break;
		case 500: {
			/**
			 * session失效自动登录一次刷新session
			 */
			CldKAccount.getInstance().sessionRelogin();
		}
			break;
		case 501:
			/**
			 * session失效
			 */
			if (res.session.equals(CldDalKAccount.getInstance().getSession())) {
				/**
				 * 当接口使用session和当前帐户里的session相同才挤下线
				 */
				if (!TextUtils.isEmpty(res.session)) {
					CldKAccountAPI.getInstance().sessionInvalid(501, 0);
				}
			}
			break;
		}
	}
}
