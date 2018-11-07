/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: AccountAPI.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.api.account
 * @Description: 账号相关接口
 * @author: zhaoqy
 * @date: 2017年4月19日 上午11:58:02
 * @version: V1.0
 */

package com.yunbaba.api.account;

import android.text.TextUtils;

import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.api.CldKConfigAPI;
import com.yunbaba.ols.bll.CldKAccount;
import com.yunbaba.ols.dal.CldDalKAccount;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.sap.bean.CldSapKAParm.CldUserInfo;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;

public class AccountAPI {

	private static AccountAPI mAccountAPI = null;

	public static AccountAPI getInstance() {
		if (mAccountAPI == null) {
			synchronized (AccountAPI.class) {
				if (mAccountAPI == null) {
					mAccountAPI = new AccountAPI();
				}
			}
		}
		return mAccountAPI;
	}

	/**
	 * 获取当前登录帐户的session
	 */
	public String getSession() {
		return CldKAccountAPI.getInstance().getSession();
	}

	/**
	 * 获取当前设备的duid
	 */
	public long getDuid() {
		return CldKAccountAPI.getInstance().getDuid();
	}

	/**
	 * 获取当前登录帐户的Kuid
	 */
	public long getKuidLogin() {
		return CldKAccountAPI.getInstance().getKuidLogin();
	}

	/**
	 * 判断当前是否登录（获取用户信息绑定）成功
	 */
	public boolean isLogined() {
		return CldKAccountAPI.getInstance().isLogined();
	}

	/**
	 * 获取绑定的手机号
	 * 
	 * @return 绑定的手机号或者 ""（未登录，或者未绑定手机号）
	 */
	public String getBindMobile() {
		return CldKAccountAPI.getInstance().getBindMobile();
	}

	/**
	 * 获取当前登录帐号的登录名
	 */
	public String getLoginName() {
		return CldKAccountAPI.getInstance().getLoginName();
	}

	/**
	 * 获取保存的密码（MD5值）
	 */
	public String getLoginPwd() {
		return CldKAccountAPI.getInstance().getLoginPwd();
	}

	/**
	 * 自动登录
	 */
	public void startAutoLogin() {
		CldKAccountAPI.getInstance().startAutoLogin();
	}

	/**
	 * 获取用户信息(阻塞)
	 */
	public void getUserInfo() {
		CldKAccountAPI.getInstance().getUserInfo();
	}

	/**
	 * 获取用户信息
	 */
	public CldUserInfo getUserInfoDetail() {
		return CldKAccountAPI.getInstance().getUserInfoDetail();
	}

	/**
	 * 登录鉴权
	 */
	public void loginAuth(final ICldResultListener listener) {
		CldBllKDelivery.getInstance().loginAuth(listener);
	}

	/**
	 * 获取手机验证码
	 * 
	 * @param mobile
	 *            手机号
	 * @param bussinessCode
	 *            业务类型: 101-注册; 102-绑定; 103-改绑; 104-解绑; 105-重置密码; 106快捷登录
	 */
	public void getVerifyCode(String mobile, int bussinessCode, String oldmoble) {
		CldKAccountAPI.getInstance().getVerifyCode(mobile, bussinessCode, oldmoble);
	}

	/**
	 * 快捷登录
	 * 
	 * @param mobile
	 *            手机号
	 * @param verifycode
	 *            106获取的验证码
	 */
	public void fastLogin(final String mobile, final String verifycode) {
		CldKAccountAPI.getInstance().fastLogin(mobile, verifycode);
	}

	/**
	 * 手动登录
	 * 
	 * @param loginName
	 *            登录名（电话号码,邮箱,用户名）
	 * @param password
	 *            密码
	 */
	public void login(String loginName, String password) {
		CldKAccountAPI.getInstance().login(loginName, password);
	}

	public void login2(String loginName, String password) {
		CldKAccountAPI.getInstance().login2(loginName, password);
	}

	/**
	 * 注销
	 */
	public void loginOut() {
		CldKAccountAPI.getInstance().loginOut();
	}

	/**
	 * 校验手机验证码是否正确
	 * 
	 * @param mobile
	 *            手机号
	 * @param verifycode
	 *            验证码
	 * @param bussinessCode
	 *            业务ID
	 */
	public void checkMobileVeriCode(String mobile, String verifycode,
			int bussinesscode) {
		CldKAccountAPI.getInstance().checkMobileVeriCode(mobile, verifycode,
				bussinesscode);
	}

	/**
	 * 通过手机验证重置密码
	 * 
	 * @param mobile
	 *            手机号
	 * @param newPwd
	 *            新密码
	 * @param verifyCode
	 *            手机验证码（bussinessCode=105）
	 */
	public void retrievePwd(String mobile, String newPwd, String verifyCode) {
		CldKAccountAPI.getInstance().retrievePwd(mobile, newPwd, verifyCode);
	}

	/**
	 * 修改密码
	 * 
	 * @param oldPwd
	 *            旧密码
	 * @param newPwd
	 *            新密码
	 */
	public void revisePwd(String oldPwd, String newPwd) {
		CldKAccountAPI.getInstance().revisePwd(oldPwd, newPwd);
	}

	/**
	 * 绑定手机号
	 * 
	 * @param mobile
	 *            手机号
	 * @param verifycode
	 *            短信验证码
	 */
	public void bindMobile(String mobile, String verifycode) {
		CldKAccountAPI.getInstance().bindMobile(mobile, verifycode);
	}

	/**
	 * 改绑手机号
	 * 
	 * @param mobile
	 *            手机号
	 * @param oldmobile
	 *            旧手机号
	 * @param verifycode
	 *            短信验证码
	 */
	public void updateMobile(String mobile, String oldmobile, String verifycode) {
		CldKAccountAPI.getInstance()
				.updateMobile(mobile, oldmobile, verifycode);
	}

	/**
	 * 更新用户信息(可部分更新)
	 * 
	 * @param username
	 *            用户名
	 * @param useralias
	 *            化名
	 * @param email
	 *            邮箱(无 传null)
	 * @param mobile
	 *            手机(无 传null)
	 * @param sex
	 *            性别（"0","1" 不改传null）
	 * @param photoData
	 *            头像图片二进制数据
	 */
	public void updateUserInfo(String username, String useralias, String email,
			String mobile, String sex, String address, byte[] photoData) {
		CldKAccountAPI.getInstance().updateUserInfo(username, useralias, email,
				mobile, sex, address, photoData);
	}

	public void setLoginPwd(String loginPwd) {
		CldDalKAccount.getInstance().setLoginPwd(loginPwd);
	}

	public void setLoginStatus(int loginStatus) {
		CldKAccount.getInstance().setLoginStatus(loginStatus);
	}
	
	public int getLoginStatus() {
		return CldKAccount.getInstance().getLoginStatus();
	}

	/**
	 * 反初始化
	 */
	public void uninit() {
		CldKAccountAPI.getInstance().uninit();
	}

	/**
	 * 是否是手机号
	 * 
	 * @param phone
	 *            传入手机号
	 */
	public boolean isPhoneNum(String phone) {
		return CldKConfigAPI.getInstance().isPhoneNum(phone);
		//return isMobile(phone);
	}

	/**
	 * 验证手机格式
	 */
	public static boolean isMobile(String number) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String num = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(number)) {
			return false;
		} else {
			// matches():字符串是否在给定的正则表达式匹配
			return number.matches(num);
		}
	}
}
