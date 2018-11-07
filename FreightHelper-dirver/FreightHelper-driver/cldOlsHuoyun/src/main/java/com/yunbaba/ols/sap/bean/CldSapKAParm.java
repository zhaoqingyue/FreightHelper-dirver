/*
 * @Title CldSapKAParm.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:59
 * @version 1.0
 */
package com.yunbaba.ols.sap.bean;




/**
 * 帐户系统参数表
 * 
 * @author Zhouls
 * @date 2015-3-5 下午3:46:27
 */
public class CldSapKAParm {
	/**
	 * 用户信息
	 * 
	 * @author Zhouls
	 * @date 2015-3-5 下午3:46:59
	 */
		public static class CldUserInfo {
			/** 登录名 */
		private String loginName;
		/** 用户名称 */
		private String userName;
		/** 用户别名 */
		private String userAlias;
		/** 邮箱 */
		private String email;
		/** 邮箱认证(绑定)状态(0未绑定 1绑定 2锁定) */
		private int emailBind;
		/** 手机 */
		private String mobile;
		/** 手机认证(绑定)状态(0未绑定 1绑定 2锁定) */
		private int mobileBind;
		/** 性别 */
		private int sex;
		/** QQ号 */
		private String qq;
		/** 证件类型(1身份证 2军官证 3学生证 4其它) */
		private int cardType;
		/** 证件号 */
		private String cardCode;
		/** 用户等级 */
		private int userLevel;
		/** 是否VIP(0否 1是) */
		private int vip;
		/** 1,新用户注册，2老用户登录 */
		private int status;
		/** 注册时间 */
		private long regTime;
		/** 注册IP */
		private String regIp;
		/** 注册业务ID */
		private int regAppid;
		/** 更新时间 */
		private String updateTime;
		/** 最后一次登录时间 */
		private long lastLoginTime;
		/** 最后一次登录ip */
		private String lastLoginIp;
		/** 最后一次登录appid */
		private int lastLoginAppid;
		/** k豆数量 */
		private int kBeans;
		/** 当前区域　 */
		private String address;
		/** 头像路径 */
		private String photoPath;
		/** 驾驶证url */
		private String driving_licence;
		/** 资料认证信息 */
		private CldLicenceInfo licenceInfo;

		public CldUserInfo() {
			loginName = "";
			userName = "";
			userAlias = "";
			email = "";
			emailBind = -1;
			mobile = "";
			mobileBind = -1;
			sex = 1;
			qq = "";
			cardType = -1;
			cardCode = "";
			userLevel = 0;
			vip = -1;
			status = -1;
			regTime = 0;
			regIp = "";
			regAppid = -1;
			updateTime = "";
			lastLoginTime = 0;
			lastLoginIp = "";
			lastLoginAppid = -1;
			kBeans = 0;
			address = "";
			photoPath = "";
			driving_licence = "";
			licenceInfo = null;
		}
		
		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getPhotoPath() {
			return photoPath;
		}

		public void setPhotoPath(String photoPath) {
			this.photoPath = photoPath;
		}

		public String getDriving_licence() {
			return driving_licence;
		}

		public void setDriving_licence(String driving_licence) {
			this.driving_licence = driving_licence;
		}

		public CldLicenceInfo getLicenceInfo() {
			return licenceInfo;
		}

		public void setLicenceInfo(CldLicenceInfo licenceInfo) {
			this.licenceInfo = licenceInfo;
		}

		/**
		 * Gets the login name.
		 * 
		 * @return the login name
		 */
		public String getLoginName() {
			return loginName;
		}

		/**
		 * Sets the login name.
		 * 
		 * @param loginName
		 *            the new login name
		 */
		public void setLoginName(String loginName) {
			this.loginName = loginName;
		}

		/**
		 * Gets the user name.
		 * 
		 * @return the user name
		 */
		public String getUserName() {
			return userName;
		}

		/**
		 * Sets the user name.
		 * 
		 * @param userName
		 *            the new user name
		 */
		public void setUserName(String userName) {
			this.userName = userName;
		}

		/**
		 * Gets the user alias.
		 * 
		 * @return the user alias
		 */
		public String getUserAlias() {
			return userAlias;
		}

		/**
		 * Sets the user alias.
		 * 
		 * @param userAlias
		 *            the new user alias
		 */
		public void setUserAlias(String userAlias) {
			this.userAlias = userAlias;
		}

		/**
		 * Gets the email.
		 * 
		 * @return the email
		 */
		public String getEmail() {
			return email;
		}

		/**
		 * Sets the email.
		 * 
		 * @param email
		 *            the new email
		 */
		public void setEmail(String email) {
			this.email = email;
		}

		/**
		 * Gets the email bind.
		 * 
		 * @return the email bind
		 */
		public int getEmailBind() {
			return emailBind;
		}

		/**
		 * Sets the email bind.
		 * 
		 * @param emailBind
		 *            the new email bind
		 */
		public void setEmailBind(int emailBind) {
			this.emailBind = emailBind;
		}

		/**
		 * Gets the mobile.
		 * 
		 * @return the mobile
		 */
		public String getMobile() {
			return mobile;
		}

		/**
		 * Sets the mobile.
		 * 
		 * @param mobile
		 *            the new mobile
		 */
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		/**
		 * Gets the mobile bind.
		 * 
		 * @return the mobile bind
		 */
		public int getMobileBind() {
			return mobileBind;
		}

		/**
		 * Sets the mobile bind.
		 * 
		 * @param mobileBind
		 *            the new mobile bind
		 */
		public void setMobileBind(int mobileBind) {
			this.mobileBind = mobileBind;
		}

		/**
		 * Gets the sex.
		 * 
		 * @return the sex
		 */
		public int getSex() {
			return sex;
		}

		/**
		 * Sets the sex.
		 * 
		 * @param sex
		 *            the new sex
		 */
		public void setSex(int sex) {
			this.sex = sex;
		}

		/**
		 * Gets the qq.
		 * 
		 * @return the qq
		 */
		public String getQq() {
			return qq;
		}

		/**
		 * Sets the qq.
		 * 
		 * @param qq
		 *            the new qq
		 */
		public void setQq(String qq) {
			this.qq = qq;
		}

		/**
		 * Gets the card type.
		 * 
		 * @return the card type
		 */
		public int getCardType() {
			return cardType;
		}

		/**
		 * Sets the card type.
		 * 
		 * @param cardType
		 *            the new card type
		 */
		public void setCardType(int cardType) {
			this.cardType = cardType;
		}

		/**
		 * Gets the card code.
		 * 
		 * @return the card code
		 */
		public String getCardCode() {
			return cardCode;
		}

		/**
		 * Sets the card code.
		 * 
		 * @param cardCode
		 *            the new card code
		 */
		public void setCardCode(String cardCode) {
			this.cardCode = cardCode;
		}

		/**
		 * Gets the user level.
		 * 
		 * @return the user level
		 */
		public int getUserLevel() {
			return userLevel;
		}

		/**
		 * Sets the user level.
		 * 
		 * @param userLevel
		 *            the new user level
		 */
		public void setUserLevel(int userLevel) {
			this.userLevel = userLevel;
		}

		/**
		 * Gets the vip.
		 * 
		 * @return the vip
		 */
		public int getVip() {
			return vip;
		}

		/**
		 * Sets the vip.
		 * 
		 * @param vip
		 *            the new vip
		 */
		public void setVip(int vip) {
			this.vip = vip;
		}

		/**
		 * Gets the status.
		 * 
		 * @return the status
		 */
		public int getStatus() {
			return status;
		}

		/**
		 * Sets the status.
		 * 
		 * @param status
		 *            the new status
		 */
		public void setStatus(int status) {
			this.status = status;
		}

		/**
		 * Gets the reg time.
		 * 
		 * @return the reg time
		 */
		public long getRegTime() {
			return regTime;
		}

		/**
		 * Sets the reg time.
		 * 
		 * @param regTime
		 *            the new reg time
		 */
		public void setRegTime(long regTime) {
			this.regTime = regTime;
		}

		/**
		 * Gets the reg ip.
		 * 
		 * @return the reg ip
		 */
		public String getRegIp() {
			return regIp;
		}

		/**
		 * Sets the reg ip.
		 * 
		 * @param regIp
		 *            the new reg ip
		 */
		public void setRegIp(String regIp) {
			this.regIp = regIp;
		}

		/**
		 * Gets the reg appid.
		 * 
		 * @return the reg appid
		 */
		public int getRegAppid() {
			return regAppid;
		}

		/**
		 * Sets the reg appid.
		 * 
		 * @param regAppid
		 *            the new reg appid
		 */
		public void setRegAppid(int regAppid) {
			this.regAppid = regAppid;
		}

		/**
		 * Gets the update time.
		 * 
		 * @return the update time
		 */
		public String getUpdateTime() {
			return updateTime;
		}

		/**
		 * Sets the update time.
		 * 
		 * @param updateTime
		 *            the new update time
		 */
		public void setUpdateTime(String updateTime) {
			this.updateTime = updateTime;
		}

		/**
		 * Gets the last login time.
		 * 
		 * @return the last login time
		 */
		public long getLastLoginTime() {
			return lastLoginTime;
		}

		/**
		 * Sets the last login time.
		 * 
		 * @param lastLoginTime
		 *            the new last login time
		 */
		public void setLastLoginTime(long lastLoginTime) {
			this.lastLoginTime = lastLoginTime;
		}

		/**
		 * Gets the last login ip.
		 * 
		 * @return the last login ip
		 */
		public String getLastLoginIp() {
			return lastLoginIp;
		}

		/**
		 * Sets the last login ip.
		 * 
		 * @param lastLoginIp
		 *            the new last login ip
		 */
		public void setLastLoginIp(String lastLoginIp) {
			this.lastLoginIp = lastLoginIp;
		}

		/**
		 * Gets the last login appid.
		 * 
		 * @return the last login appid
		 */
		public int getLastLoginAppid() {
			return lastLoginAppid;
		}

		/**
		 * Sets the last login appid.
		 * 
		 * @param lastLoginAppid
		 *            the new last login appid
		 */
		public void setLastLoginAppid(int lastLoginAppid) {
			this.lastLoginAppid = lastLoginAppid;
		}

		/**
		 * Gets the k beans.
		 * 
		 * @return the k beans
		 */
		public int getkBeans() {
			return kBeans;
		}

		/**
		 * Sets the k beans.
		 * 
		 * @param kBeans
		 *            the new k beans
		 */
		public void setkBeans(int kBeans) {
			this.kBeans = kBeans;
		}
	}
	
	/**
	 * 
	 * 驾驶证信息
	 * @author Zhouls
	 * @date 2016-5-6 下午3:10:52
	 */
	public static class CldLicenceInfo {
		/** 驾驶证姓名*/
		public String licenceName;
		/** 驾驶证号码*/
		public String licenceNum;
		/** 行驶证车牌号*/
		public String vehicleNum;
		/** 行驶证车辆类型*/
		public String vehicleType;
		/** 司机信息审核状态 0 未提交  1 审核中 2已认证 3认证失败*/
		public int status;
		/** 司机信息审核失败原因*/
		public String reason;

		public CldLicenceInfo() {
			licenceName = "";
			licenceNum = "";
			vehicleNum = "";
			vehicleType = "";
			status = -1;
			reason = "";
		}
	}
}
