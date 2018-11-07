package com.yunbaba.freighthelper.manager;

import com.yunbaba.freighthelper.bean.UserInfo;


public class UserManager {

	private static UserManager mKCloudUser = null;
	private static UserInfo mUserInfo = null;	
	private static UserInfo mUserInfoTmp = null;	
	
	public static UserManager getInstance() {
		if (mKCloudUser == null) {
			synchronized(UserManager.class) {
				if (mKCloudUser == null) {
					mKCloudUser = new UserManager();
				}
			}
		}
		return mKCloudUser;
	}
	
	public void init() {
		// 个人信息存储
		mUserInfo = new UserInfo();
		mUserInfoTmp = new UserInfo();
	}
	
	/**
	 * 获取用户可修改信息
	 */
	public UserInfo getUserInfo() {
		return mUserInfo;
	}
	
	public void setUserInfo(UserInfo userInfo) {
		mUserInfo = userInfo;
	}
	
	/**
	 * 获取用户临时信息
	 */
	public UserInfo getTmpUserInfo() {
		return mUserInfoTmp;
	}
}
