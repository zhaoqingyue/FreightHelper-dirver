/*
 * @Title CldDalKAccount.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.ols.dal;

import android.text.TextUtils;

import com.cld.setting.CldSetting;
import com.yunbaba.ols.sap.bean.CldSapKAParm.CldUserInfo;


/**
 * 帐户数据层
 * 
 * @author Zhouls
 * @date 2014-10-28 上午11:46:13
 */
public class CldDalKAccount {

	/** 帐号系统密钥 */
	private String cldKAKey;
	/** duid */
	private long duid;
	/** kuid */
	private long kuid;
	/** session */
	private String session;
	/** 登录名 */
	private String loginName;
	/** 密码 */
	private String loginPwd;
	/** 服务器时间和本地时间差. */
	private long timeDif;
	/** 上行短信注册返回Kuid. */
	private long kuidRegSms;
	/** 上行短信注册返回loginName */
	private String loginNameRegSms;
	/** The cld user info. */
	private CldUserInfo cldUserInfo;
	/** 设备标识码 */
	private String deviceKsn;
	/** 注册返回登录名. */
	private String loginNameReg;
	/** 注册返回Kuid. */
	private long kuidReg;
	/** 判断是否注册成功返回的用户Id. */
	private long kuidRegUser;
	/** 二维码登录唯一标识符，用于轮询获取登录状态 */
	private String guid;
	/** 二维码创建时间，二维码有效时间为30分钟 */
	private String createTime;
	/** 二维码图片二进制数据base64编码之后的结果 png格式 */
	private String imgdata;

	private static CldDalKAccount cldDataKAccount;

	/**
	 * Gets the single instance of CldDalKAccount.
	 * 
	 * @return single instance of CldDalKAccount
	 */
	public static CldDalKAccount getInstance() {
		if (null == cldDataKAccount) {
			cldDataKAccount = new CldDalKAccount();
		}
		return cldDataKAccount;
	}

	/**
	 * Instantiates a new cld dal k account.
	 */
	private CldDalKAccount() {
		timeDif = 0;
		kuidRegSms = 0;
		loginNameRegSms = "";
		cldUserInfo = new CldUserInfo();
		deviceKsn = "";
		loginNameReg = "";
		kuidReg = 0;
		kuidRegUser = 0;
		cldKAKey = "";
	}

	/**
	 * Uninit.
	 */
	public void uninit() {
		CldSetting.put("kuid", "");
		CldSetting.put("session", "");
		kuidRegSms = 0;
		loginNameRegSms = "";
		cldUserInfo = new CldUserInfo();
		deviceKsn = "";
		loginNameReg = "";
		kuidReg = 0;
		kuidRegUser = 0;
	}

	/**
	 * Gets the duid.
	 * 
	 * @return the duid
	 */
	public long getDuid() {
		if (duid != 0) {
			return duid;
		} else {
			if (CldSetting.getString("duid").length() > 0) {
				return Long.parseLong(CldSetting.getString("duid"));
			} else {
				return 0;
			}
		}
	}

	/**
	 * Sets the duid.
	 * 
	 * @param duid
	 *            the new duid
	 */
	public void setDuid(long duid) {
		this.duid = duid;
		CldSetting.put("duid", duid + "");

	}

	/**
	 * Gets the kuid.
	 * 
	 * @return the kuid
	 */
	public long getKuid() {
		if (kuid != 0) {
			return kuid;
		} else {
			if (CldSetting.getString("kuid").length() > 0) {
				return Long.parseLong(CldSetting.getString("kuid"));
			} else {
				return 0;
			}
		}
	}

	/**
	 * Sets the kuid.
	 * 
	 * @param kuid
	 *            the new kuid
	 */
	public void setKuid(long kuid) {
		this.kuid = kuid;
		CldSetting.put("kuid", kuid + "");
	}

	/**
	 * Gets the session.
	 * 
	 * @return the session
	 */
	public String getSession() {
		if (!TextUtils.isEmpty(session)) {
			return session;
		} else {
			return CldSetting.getString("session");
		}
	}

	/**
	 * Sets the session.
	 * 
	 * @param session
	 *            the new session
	 */
	public void setSession(String session) {
		if (!TextUtils.isEmpty(session)) {
			this.session = session;
			CldSetting.put("session", session);
		} else {
			this.session = "";
			CldSetting.put("session", "");
		}
	}
	

	/**
	 * Gets the device ksn.
	 * 
	 * @return the device ksn
	 */
	public String getDeviceKsn() {
		return deviceKsn;
	}

	/**
	 * Sets the device ksn.
	 * 
	 * @param deviceKsn
	 *            the new device ksn
	 */
	public void setDeviceKsn(String deviceKsn) {
		this.deviceKsn = deviceKsn;
	}

	/**
	 * Gets the login name.
	 * 
	 * @return the login name
	 */
	public String getLoginName() {
		if (!TextUtils.isEmpty(loginName)) {
			return loginName;
		} else {
			return CldSetting.getString("userName");
		}
	}

	/**
	 * Sets the login name.
	 * 
	 * @param loginName
	 *            the new login name
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
		CldSetting.put("userName", loginName);
	}

	/**
	 * Gets the login pwd.
	 * 
	 * @return the login pwd
	 */
	public String getLoginPwd() {
		if (!TextUtils.isEmpty(loginPwd)) {
			return loginPwd;
		} else {
			return CldSetting.getString("password");
		}
	}

	/**
	 * Sets the login pwd.
	 * 
	 * @param loginPwd
	 *            the new login pwd
	 */
	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
		CldSetting.put("password", loginPwd);
	}

	/**
	 * Gets the cld user info.
	 * 
	 * @return the cld user info
	 */
	public CldUserInfo getCldUserInfo() {
		return cldUserInfo;
	}

	/**
	 * Sets the cld user info.
	 * 
	 * @param cldUserInfo
	 *            the new cld user info
	 */
	public void setCldUserInfo(CldUserInfo cldUserInfo) {
		this.cldUserInfo = cldUserInfo;
	}

	/**
	 * Gets the login name reg sms.
	 * 
	 * @return the login name reg sms
	 */
	public String getLoginNameRegSms() {
		return loginNameRegSms;
	}

	/**
	 * Sets the login name reg sms.
	 * 
	 * @param loginNameRegSms
	 *            the new login name reg sms
	 */
	public void setLoginNameRegSms(String loginNameRegSms) {
		this.loginNameRegSms = loginNameRegSms;
	}

	/**
	 * Gets the login name reg.
	 * 
	 * @return the login name reg
	 */
	public String getLoginNameReg() {
		return loginNameReg;
	}

	/**
	 * Sets the login name reg.
	 * 
	 * @param loginNameReg
	 *            the new login name reg
	 */
	public void setLoginNameReg(String loginNameReg) {
		this.loginNameReg = loginNameReg;
	}

	/**
	 * Gets the kuid reg.
	 * 
	 * @return the kuid reg
	 */
	public long getKuidReg() {
		return kuidReg;
	}

	/**
	 * Sets the kuid reg.
	 * 
	 * @param kuidReg
	 *            the new kuid reg
	 */
	public void setKuidReg(long kuidReg) {
		this.kuidReg = kuidReg;
	}

	/**
	 * Gets the kuid reg user.
	 * 
	 * @return the kuid reg user
	 */
	public long getKuidRegUser() {
		return kuidRegUser;
	}

	/**
	 * Sets the kuid reg user.
	 * 
	 * @param kuidRegUser
	 *            the new kuid reg user
	 */
	public void setKuidRegUser(long kuidRegUser) {
		this.kuidRegUser = kuidRegUser;
	}

	/**
	 * Gets the time dif.
	 * 
	 * @return the time dif
	 */
	public long getTimeDif() {
		return timeDif;
	}

	/**
	 * Sets the time dif.
	 * 
	 * @param timeDif
	 *            the new time dif
	 */
	public void setTimeDif(long timeDif) {
		this.timeDif = timeDif;
	}

	/**
	 * Gets the kuid reg sms.
	 * 
	 * @return the kuid reg sms
	 */
	public long getKuidRegSms() {
		return kuidRegSms;
	}

	/**
	 * Sets the kuid reg sms.
	 * 
	 * @param kuidRegSms
	 *            the new kuid reg sms
	 */
	public void setKuidRegSms(long kuidRegSms) {
		this.kuidRegSms = kuidRegSms;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getImgdata() {
		return imgdata;
	}

	public void setImgdata(String imgdata) {
		this.imgdata = imgdata;
	}

	/** @return the pwdtype */
	public int getPwdtype() {
		int res = CldSetting.getInt("ols_ka_pwdtype", 0);

		return res;
	}

	/**
	 * @param pwdtype
	 *            the pwdtype to set
	 */
	public void setPwdtype(int pwdtype) {
		CldSetting.put("ols_ka_pwdtype", pwdtype);
	}

	/** @return the cldKAKey */
	public String getCldKAKey() {
		if (!TextUtils.isEmpty(cldKAKey)) {
			return cldKAKey;
		} else {
			return CldSetting.getString("CldKAKey");
		}
	}

	/**
	 * @param cldKAKey
	 *            the cldKAKey to set
	 */
	public void setCldKAKey(String cldKAKey) {
		this.cldKAKey = cldKAKey;
		CldSetting.put("CldKAKey", cldKAKey);
	}
}
