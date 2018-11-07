/*
 * @Title CldKAccountAPI.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 17:37:59
 * @version 1.0
 */
package com.yunbaba.ols.api;

import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.cld.device.CldPhoneNet;
import com.cld.setting.CldSetting;
import com.yunbaba.ols.api.CldOlsBase.IInitListener;
import com.yunbaba.ols.api.CldOlsInit.ICldOlsInitListener;
import com.yunbaba.ols.bll.CldBllUtil;
import com.yunbaba.ols.bll.CldKAccount;
import com.yunbaba.ols.bll.CldKAccount.IAutoLoginListener;
import com.yunbaba.ols.callback.OnAccountResponseCallBack;
import com.yunbaba.ols.dal.CldDalKAccount;
import com.yunbaba.ols.sap.CldSapUtil;
import com.yunbaba.ols.sap.bean.CldSapKAParm.CldUserInfo;
import com.yunbaba.ols.tools.CldErrUtil;
import com.yunbaba.ols.tools.CldSapReturn;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;

/**
 * 帐户相关模块，提供帐户相关功能.
 * 
 * @author Zhouls
 * @date 2014-8-29 上午11:27:37
 */
public class CldKAccountAPI {

	/** 帐户系统回调监听，初始化时设置一次. */
	private ICldKAccountListener listener;
	/** The cld k account api. */
	private static CldKAccountAPI cldKAccountAPI;

	/**
	 * Instantiates a new cld k account api.
	 */
	private CldKAccountAPI() {
	}

	/**
	 * Gets the single instance of CldKAccountAPI.
	 * 
	 * @return single instance of CldKAccountAPI
	 */
	public static CldKAccountAPI getInstance() {
		if (null == cldKAccountAPI) {
			cldKAccountAPI = new CldKAccountAPI();
		}
		return cldKAccountAPI;
	}

	/**
	 * 导航启动初始化变量.
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2014-8-29 下午12:03:02
	 */
	public void init() {

	}

	/**
	 * 反初始化.
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2014-10-24 上午10:00:06
	 */
	public void uninit() {
		CldKAccount.getInstance().uninit();
	}

	/**
	 * 设置回调监听（首次设置有效）
	 * 
	 * @param listener
	 *            回调监听
	 * @return int (0 设置成功，-1已有设置失败)
	 * @author Zhouls
	 * @date 2014-8-29 下午12:09:36
	 */
	public int setCldKAccountListener(ICldKAccountListener listener) {
		if (null == this.listener) {
			this.listener = listener;
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 初始化密钥
	 * 
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2015-2-12 下午6:06:00
	 */
	public void initKey(final ICldOlsInitListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				CldKAccount.getInstance().initKey();
				if (null != listener) {
					listener.onInitReslut();
				}
			}
		}).start();
	}

	/**
	 * 初始化服务器时间
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-1 下午1:13:11
	 */
	public void initSvrTime() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				CldKAccount.getInstance().initSvrTime();
			}
		}).start();
	}

	/**
	 * 初始化duid
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-1 下午1:16:50
	 */
	public void initDuid(final IInitListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				CldKAccount.getInstance().initDuid(listener);
			}
		}).start();
	}

	/**
	 * 设备注册（获取duid）
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2014-11-25 下午3:19:48
	 */
	public void deviceRegister() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldKAccount.getInstance().deviceRegister();
			}
		}).start();
	}

	/**
	 * 判断是否是已注册用户（获取指定用户的kuid）
	 * 
	 * @param loginName
	 *            登录名（电话号码,邮箱,用户名）
	 * @return void
	 * @author Zhouls
	 * @date 2014-8-29 下午12:03:27
	 */
	public void isRegisterUser(final String loginName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				CldSapReturn errRes = CldKAccount.getInstance().isRegisterUser(
						loginName);
				long kuid = 0;
				if (errRes.errCode == 101) {
					kuid = CldDalKAccount.getInstance().getKuidRegUser();
				}
				if (null != listener) {
					listener.onIsRegUser(errRes.errCode, kuid, loginName);
				}
				Looper.loop();
			}
		}).start();
	}

	/**
	 * 上行短信注册.
	 * 
	 * @param password
	 *            用户设置的密码
	 * @return void
	 * @author Zhouls
	 * @date 2014-8-29 下午12:04:17
	 */
	public void registerBySms(final String password) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().registerBySms(
						password);
				long kuid = 0;
				String loginName = "";
				if (errRes.errCode == 0) {
					kuid = CldDalKAccount.getInstance().getKuidRegSms();
					loginName = CldDalKAccount.getInstance()
							.getLoginNameRegSms();
				}
				if (null != listener) {
					listener.onRegBySms(errRes.errCode, kuid, loginName);
				}
			}
		}).start();
	}

	/**
	 * 
	 * 第三方登录类型枚举
	 * 
	 * @author Zhouls
	 * @date 2015-6-23 下午2:59:31
	 */
	public enum CldThirdLoginType {
		QQLOGIN, WEIXLOGIN, SINALOGIN;
		/**
		 * 获取对应的值
		 * 
		 * @param CldThirdLoginType
		 * @return
		 * @return int
		 * @author Zhouls
		 * @date 2015-6-23 下午2:58:13
		 */
		public static int valueOf(CldThirdLoginType cldThirdLoginType) {
			switch (cldThirdLoginType) {
			case QQLOGIN:
				// QQ登录
				return 1;
			case WEIXLOGIN:
				// 微信登录
				return 2;
			case SINALOGIN:
				// 新浪登录
				return 3;
			default:
				return 0;
			}
		}
	}

	/**
	 * 手动登录.
	 * 
	 * @param loginName
	 *            登录名（电话号码,邮箱,用户名）
	 * @param password
	 *            密码
	 * @return void
	 * @author Zhouls
	 * @date 2014-10-23 上午10:35:23
	 * 
	 */
	public void login(final String loginName, final String password) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String loginPwd;
				CldSapReturn errRes;
				if (!TextUtils.isEmpty(loginName)
						&& !TextUtils.isEmpty(password)) {
					if (password.length() == 32) {
						loginPwd = password;
					} else {
						loginPwd = CldSapUtil.MD5(password);
					}
					errRes = CldKAccount.getInstance().login(loginName,
							loginPwd, 1);
				} else {
					errRes = CldErrUtil.errDeal();
				}
				Log.e("yyh", "login result:" + errRes.errCode);
				if (null != listener) {
					listener.onLoginResult(errRes.errCode, false);

				}
			}
		}).start();
	}

	public void login2(final String loginName, final String password) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String loginPwd;
				CldSapReturn errRes;
				if (!TextUtils.isEmpty(loginName)
						&& !TextUtils.isEmpty(password)) {
					//if (password.length() == 32) {
						loginPwd = password;
//					} else {
//						loginPwd = CldSapUtil.MD5(password);
//					}
					errRes = CldKAccount.getInstance().login(loginName,
							loginPwd, 1);
				} else {
					errRes = CldErrUtil.errDeal();
				}
				Log.e("yyh", "login result:" + errRes.errCode);
				if (null != listener) {
					listener.onLoginResult(errRes.errCode, false);

				}
			}
		}).start();
	}



	/**
	 * 手动登录
	 * 
	 * @author ligangfan
	 * @param loginName
	 * @param password
	 * @param callBack
	 */
	public void login(final String loginName, final String password,
			final OnAccountResponseCallBack callBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String loginPwd;
				CldSapReturn errRes;
				if (!TextUtils.isEmpty(loginName)
						&& !TextUtils.isEmpty(password)) {
					if (password.length() == 32) {
						loginPwd = password;
					} else {
						loginPwd = CldSapUtil.MD5(password);
					}
					errRes = CldKAccount.getInstance().login(loginName,
							loginPwd, 1);
				} else {
					errRes = CldErrUtil.errDeal();
				}
				if (callBack != null) {
					callBack.onResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 获取用户信息.
	 * 
	 * @return
	 * @return CldUserInfo
	 * @author Zhouls
	 * @date 2015-3-5 下午2:35:45
	 */
	public CldUserInfo getUserInfoDetail() {
		return CldDalKAccount.getInstance().getCldUserInfo();
	}

	/**
	 * 获取用户信息(阻塞).
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-2-12 上午11:17:47
	 */
	public void getUserInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().getUserInfo();
				if (null != listener) {
					listener.onGetUserInfoResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 注销.
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2014-9-2 上午10:11:12
	 * 
	 */
	public void loginOut() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().loginOut();
				if (null != listener) {
					listener.onLoginOutResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 注销登录
	 * 
	 * @author ligangfan
	 * @date 2017-3-21
	 * @param callBack
	 */
	public void loginOut(final OnAccountResponseCallBack callBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().loginOut();
				if (callBack != null) {
					callBack.onResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 自动登录.
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午2:36:27
	 * 
	 */
	public void startAutoLogin() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldKAccount.getInstance().startAutoLogin(
						new IAutoLoginListener() {
							@Override
							public void onLoginStateChange(int loginState,
									CldSapReturn errRes) {
								if (null != listener) {
									listener.onAutoLoginResult(loginState,
											errRes.errCode);
								}
							}
						});
			}
		}).start();
	}

	/**
	 * 自动登录
	 * 
	 * @author ligangfan
	 * @date 2017-3-21
	 * @param callBack
	 */
	public void startAutoLogin(final OnAccountResponseCallBack callBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldKAccount.getInstance().startAutoLogin(
						new IAutoLoginListener() {
							@Override
							public void onLoginStateChange(int loginState,
									CldSapReturn errRes) {
								if (callBack != null) {
									callBack.onResult(errRes.errCode);
								}
							}
						});
			}
		}).start();
	}

	/**
	 * 获取手机验证码
	 * 
	 * @param mobile
	 *            手机号
	 * @param bussinessCode
	 *            业务类型 101注册 102绑定 103改绑 104解绑 105重置密码 106快捷登录
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午2:54:05
	 */
	public void getVerifyCode(final String mobile, final int bussinessCode,
			final String oldmoble) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().getVerifyCode(
						mobile, bussinessCode, oldmoble);
				if (null != listener) {
					listener.onGetVerifyCode(errRes.errCode, bussinessCode);
				}
			}
		}).start();
	}

	/**
	 * 获取登录的验证码
	 * 
	 * @author ligangfan
	 * @date 2017-3-21
	 * @param mobile
	 * @param callBack
	 */
	public void getVerifyCodeToLogin(final String mobile,
			final OnAccountResponseCallBack callBack) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				CldSapReturn errRes = CldKAccount.getInstance().getVerifyCode(
						mobile, 106, null);
				if (callBack != null) {
					callBack.onResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 获取改绑手机的验证码
	 * 
	 * @param mobile
	 *            手机号
	 * @param oldmobile
	 *            旧手机号
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-10 下午5:40:06
	 * 
	 */
	public void getVerCodeToReviseMobile(final String mobile,
			final String oldmoble) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().getVerifyCode(
						mobile, 103, oldmoble);
				if (null != listener) {
					listener.onGetVerifyCode(errRes.errCode, 103);
				}
			}
		}).start();
	}

	/**
	 * 获取改绑手机的验证码
	 * 
	 * @author ligangfan
	 * @date 2017-3-21
	 * @param mobile
	 * @param oldmoble
	 * @param callBack
	 */
	public void getVerCodeToReviseMobile(final String mobile,
			final String oldmoble, final OnAccountResponseCallBack callBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().getVerifyCode(
						mobile, 103, oldmoble);
				if (callBack != null) {
					callBack.onResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 下行短信注册
	 * 
	 * @param mobile
	 *            手机号
	 * @param password
	 *            密码
	 * @param verifyCode
	 *            手机验证码（bussinessCode=101）
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午2:54:40
	 */
	public void register(final String mobile, final String password,
			final String verifyCode) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().register(
						mobile, password, verifyCode);
				String loginName = "";
				long kuid = 0;
				if (errRes.errCode == 0) {
					loginName = CldDalKAccount.getInstance().getLoginNameReg();
					kuid = CldDalKAccount.getInstance().getKuidReg();
				}
				if (null != listener) {
					listener.onRegister(errRes.errCode, kuid, loginName);
				}
			}
		}).start();
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
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午2:55:18
	 */
	public void retrievePwd(final String mobile, final String newPwd,
			final String verifyCode) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().retrievePwd(
						mobile, newPwd, verifyCode);
				if (null != listener) {
					listener.onRetrievePwd(errRes.errCode);
				}
			}
		}).start();
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
	 * @return void
	 * @author Zhouls
	 * @date 2015-10-9 上午10:10:08
	 */
	public void checkMobileVeriCode(final String mobile,
			final String verifycode, final int bussinesscode) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance()
						.checkMobileVeriCode(mobile, verifycode, bussinesscode);
				if (null != listener) {
					listener.onCheckMobileVeriCode(errRes.errCode,
							bussinesscode);
				}
			}
		}).start();
	}

	/**
	 * 个性账号注册
	 * 
	 * @param username
	 *            个性账号
	 * @param password
	 *            密码
	 * @param autoLoginAfterRegistered
	 *            是否注册成功后自动登陆
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午2:54:40
	 */
	public void registerEx(final String username, final String password,
			final boolean autoLoginAfterRegistered) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().registerEx(
						username, password);
				String loginName = "";
				long kuid = 0;
				if (errRes.errCode == 0) {
					loginName = CldDalKAccount.getInstance().getLoginNameReg();
					kuid = CldDalKAccount.getInstance().getKuidReg();
					if (autoLoginAfterRegistered) {
						CldKAccount.getInstance().startAutoLogin(
								new IAutoLoginListener() {
									@Override
									public void onLoginStateChange(
											int loginState, CldSapReturn errRes) {
										if (null != listener) {
											listener.onAutoLoginResult(
													loginState, errRes.errCode);
										}
									}
								});
					}
				}
				if (null != listener) {
					listener.onRegister(errRes.errCode, kuid, loginName);
				}
			}
		}).start();
	}

	/**
	 * 修改密码
	 * 
	 * @param oldPwd
	 *            旧密码
	 * @param newPwd
	 *            新密码
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午2:55:43
	 * 
	 */
	public void revisePwd(final String oldPwd, final String newPwd) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().revisePwd(
						oldPwd, newPwd);
				if (null != listener) {
					listener.onRevisePwd(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 修改密码
	 * 
	 * @author ligangfan
	 * @date 2017-3-21
	 * @param oldPwd
	 * @param newPwd
	 * @param callBack
	 */
	public void revisePwd(final String oldPwd, final String newPwd,
			final OnAccountResponseCallBack callBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().revisePwd(
						oldPwd, newPwd);
				if (callBack != null) {
					callBack.onResult(errRes.errCode);
				}
			}
		}).start();
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
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午2:56:40
	 */
	public void updateUserInfo(final String username, final String useralias,
			final String email, final String mobile, final String sex,
			final String address, final byte[] photoData) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().updateUserInfo(
						username, useralias, email, mobile, sex, address,
						photoData);
				if (null != listener) {
					listener.onUpdateUserInfo(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 绑定手机号
	 * 
	 * @param mobile
	 *            手机号
	 * @param verifycode
	 *            短信验证码
	 * @return void
	 * @author Zhouls
	 * @date 2015-1-19 下午12:17:31
	 * 
	 * @note 增加一个回调对象参数，用于返回结果
	 * @author ligangfan
	 * @date 2017-3-21
	 */
	public void bindMobile(final String mobile, final String verifycode) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().bindMobile(
						mobile, verifycode);
				if (null != listener) {
					listener.onBindMobile(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 绑定手机号
	 * 
	 * @author ligangfan
	 * @date 2017-3-21
	 * @param mobile
	 * @param verifycode
	 * @param callBack
	 */
	public void bindMobile(final String mobile, final String verifycode,
			final OnAccountResponseCallBack callBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().bindMobile(
						mobile, verifycode);
				if (callBack != null) {
					callBack.onResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 解绑手机号
	 * 
	 * @param mobile
	 *            手机号
	 * @param verifycode
	 *            短信验证码
	 * @return void
	 * @author Zhouls
	 * @date 2015-1-19 下午1:00:52
	 */
	public void unbindMobile(final String mobile, final String verifycode) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().unbindMobile(
						mobile, verifycode);
				if (null != listener) {
					listener.onUnbindMobile(errRes.errCode);
				}
			}
		}).start();
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
	 * @return void
	 * @author Zhouls
	 * @date 2015-1-19 下午12:46:49
	 */
	public void updateMobile(final String mobile, final String oldmobile,
			final String verifycode) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().updateMobile(
						mobile, oldmobile, verifycode);
				if (null != listener) {
					listener.onUpdateMobile(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 快捷登录
	 * 
	 * @param mobile
	 *            手机号
	 * @param verifycode
	 *            106获取的验证码
	 * @return void
	 * @author Zhouls
	 * @date 2015-1-22 上午9:56:54
	 * 
	 */
	public void fastLogin(final String mobile, final String verifycode) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().fastLogin(
						mobile, verifycode);
				if (null != listener) {
					listener.onLoginResult(errRes.errCode, true);
				}
			}
		}).start();
	}

	/**
	 * 快捷登录
	 * 
	 * @author ligangfan
	 * @date 2017-3-21
	 * @param mobile
	 * @param verifycode
	 * @param callBack
	 */
	public void fastLogin(final String mobile, final String verifycode,
			final OnAccountResponseCallBack callBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().fastLogin(
						mobile, verifycode);
				if (callBack != null) {
					callBack.onResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 返回特殊登录（非普通帐户登录）的登录类型
	 * 
	 * @return （是新用户还是老用户登录） 1,新用户注册，2老用户登录
	 * @return int
	 * @author Zhouls
	 * @date 2015-7-3 上午9:19:49
	 */
	public int getSpecialLoginStatus() {
		return CldDalKAccount.getInstance().getCldUserInfo().getStatus();
	}

	/**
	 * 快捷登录后修改密码
	 * 
	 * @param newPwd
	 *            新密码
	 * @return void
	 * @author Zhouls
	 * @date 2015-1-20 上午11:23:29
	 */
	public void revisePwdByFastPwd(final String newPwd) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance()
						.revisePwdByFastPwd(newPwd);
				if (null != listener) {
					listener.onRevisePwdByFastLogin(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 获取二维码的值
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-1-21 下午5:11:34
	 */
	public String getQRcodeValue() {
		int ns = 1;
		if (!CldPhoneNet.isNetConnected()) {
			ns = 0;
		}
		String guid = "cldqr://f=l&p=" + CldDalKAccount.getInstance().getGuid()
				+ "&ns=" + ns + "&apptype="
				+ CldBllUtil.getInstance().getApptype();
		return guid;
	}

	/**
	 * 获取扫描登录的二维码
	 * 
	 * @param QRsideLength
	 *            二维码边长(像素)(如果需要服务端生成的二维码图片（二进制数据）则传此值，否则传0，获取guid 终端生成)
	 * @return void
	 * @author Zhouls
	 * @date 2015-1-19 上午9:34:35
	 */
	public void getQRcode(final int QRsideLength) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().getQRcode(
						QRsideLength);
				if (null != listener) {
					listener.onGetQRcodeResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 二维码登录监听
	 * 
	 * @author Zhouls
	 * @date 2015-1-26 上午11:38:53
	 */
	public interface IQRLoginResultListener {
		/**
		 * 二维码登录回调
		 * 
		 * @param errCode
		 *            （0 成功）
		 * @return void
		 * @author Zhouls
		 * @date 2015-1-26 上午11:38:45
		 */
		public void onLoginByQRcodeResult(int errCode);
	}

	/**
	 * 二维码登录
	 * 
	 * @param guid
	 *            唯一标识符,扫描二维码得到的内容
	 * @return void
	 * @author Zhouls
	 * @date 2015-1-19 上午9:47:47
	 */
	public void loginByQRcode(final String guid,
			final IQRLoginResultListener qrlistener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().loginByQRcode(
						guid);
				if (null != qrlistener) {
					qrlistener.onLoginByQRcodeResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 获取二维码登录状态（errCode=0,登录成功）
	 * 
	 * @param guid
	 *            唯一标识符,获取二维码时所返回
	 * @return void
	 * @author Zhouls
	 * @date 2015-1-19 上午9:58:31
	 */
	public void getLoginStatusByQRcode(final String guid) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance()
						.getLoginStatusByQRcode(guid);
				while (errRes.errCode != 0) {
					try {
						Thread.sleep(15000);
						errRes = CldKAccount.getInstance()
								.getLoginStatusByQRcode(guid);
						if (null != listener) {
							listener.onGetQRLoginStatusResult(errRes.errCode);
						}
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * 第三方登录
	 * 
	 * @param openid
	 *            从第三方获取的唯一openid
	 * @param cldThirdLoginType
	 *            第三方登录类型
	 * @return void
	 * @author Zhouls
	 * @date 2015-10-21 下午5:12:15
	 */
	public void thirdLogin(final String openid,
			final CldThirdLoginType cldThirdLoginType) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKAccount.getInstance().thirdLogin(
						openid, cldThirdLoginType);
				if (null != listener) {
					listener.onThirdLoginResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * session失效
	 * 
	 * @param errCode
	 *            接口返回的错误码
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午2:58:06
	 */
	public void sessionInvalid(int errCode, int bussiness) {
		if (errCode == 500) {
			/**
			 * 500自动登录
			 */
			CldKAccount.getInstance().sessionRelogin();
		} else if (errCode == 501) {
			uninit();
			if (null != listener) {
				listener.onInValidSession(bussiness);
			}
		}
	}

	/**
	 * 帐户系统回调监听.
	 * 
	 * @author Zhouls
	 * @date 2015-3-5 下午2:58:28
	 */
	public static interface ICldKAccountListener {

		/**
		 * 是否已注册回调.
		 * 
		 * @param errCode
		 *            (0,401，411，412，413，414，415，416，417，402 ，910，101)
		 * @param kuid
		 *            errCode=101，回传用户kuid（errCode=0,用户未注册）
		 * @param loginName
		 *            回传判断的登录名
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午2:59:33
		 */
		public void onIsRegUser(int errCode, long kuid, String loginName);

		/**
		 * 更新用户信息回调.
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:00:02
		 */
		public void onUpdateUserInfo(int errCode);

		/**
		 * 获取手机验证码回调.
		 * 
		 * @param errCode
		 *            0成功 ; 201手机号已被认证;202手机未认证;500登录已失效;501已重新登录;
		 *            903XX秒内不能重复发送验证码;906发送次数已达最大限制; 910请求时间已过期;
		 * @param bussinessid
		 *            the bussinessid
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:00:16
		 */
		public void onGetVerifyCode(int errCode, int bussinessid);

		/**
		 * 下行短信注册回调.
		 * 
		 * @param errCode
		 *            101用户名已存在; 201手机号已被认证;503注册失败;
		 *            907验证码已过期;908验证码不正确;909验证码已效验;
		 * @param kuid
		 *            errCode=0,回传注册用户Kuid
		 * @param loginName
		 *            errCode=0,回传登录名
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:00:54
		 */
		public void onRegister(int errCode, long kuid, String loginName);

		/**
		 * 上行短信注册回调.
		 * 
		 * @param errCode
		 *            (402 201，503 ,911,912 ,913 ,914),
		 *            (-1:接收短信延迟,-2:Sim卡异常,-3：网络异常)
		 * @param kuid
		 *            errCode=0,回传注册用户Kuid
		 * @param loginName
		 *            errCode=0,回传登录名
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:01:15
		 */
		public void onRegBySms(int errCode, long kuid, String loginName);

		/**
		 * 通过手机验证重置密码回调.
		 * 
		 * @param errCode
		 *            907验证码已过期;908验证码不正确;909验证码已效验202手机号未认证105修改密码失败
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:01:43
		 */
		public void onRetrievePwd(int errCode);

		/**
		 * 修改密码回调.
		 * 
		 * @param errCode
		 *            500登录失效，501已重新登录，104密码错误，105修改密码失败
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:02:00
		 */
		public void onRevisePwd(int errCode);

		/**
		 * 校验手机验证码是否正确
		 * 
		 * @param errCode
		 * @param bussinessid
		 * @return void
		 * @author Zhouls
		 * @date 2015-8-10 下午2:32:05
		 */
		public void onCheckMobileVeriCode(int errCode, int bussinessid);

		/**
		 * 登录回调.
		 * 
		 * @param errCode
		 *            登录接口错误码
		 * @param isFastLogin
		 *            是否是快捷登录或二维码登录
		 * @return void
		 * @author Zhouls
		 * @date 2015-1-21 下午3:47:53
		 */
		public void onLoginResult(int errCode, boolean isFastLogin);

		/**
		 * 获取用户信息回调
		 * 
		 * @param errCode
		 *            获取用户信息接口错误码
		 * @return void
		 * @author Zhouls
		 * @date 2015-2-12 上午9:57:17
		 */
		public void onGetUserInfoResult(int errCode);

		/**
		 * 注销回调.
		 * 
		 * @param errCode
		 *            (errCode=0 注销成功 已清除本地密码，Kuid )
		 * @return void
		 * @author Zhouls
		 * @date 2014-9-2 上午10:12:55
		 */
		public void onLoginOutResult(int errCode);

		/**
		 * 自动登录回调.
		 * 
		 * @param loginState
		 *            自动登录状态
		 * @param errCode
		 *            自动登录错误码
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:02:38
		 */
		public void onAutoLoginResult(int loginState, int errCode);

		/**
		 * session 失效回调
		 * 
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:02:57
		 */
		public void onInValidSession(int bussiness);

		/**
		 * 绑定手机号回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-1-19 下午12:18:48
		 */
		public void onBindMobile(int errCode);

		/**
		 * 解绑手机号回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-1-19 下午1:02:27
		 */
		public void onUnbindMobile(int errCode);

		/**
		 * 改绑手机号回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-1-19 下午12:46:20
		 */
		public void onUpdateMobile(int errCode);

		/**
		 * 快捷登录修改密码回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-1-20 上午11:25:03
		 */
		public void onRevisePwdByFastLogin(int errCode);

		/**
		 * 获取登录二维码回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-1-19 上午9:34:02
		 */
		public void onGetQRcodeResult(int errCode);

		/**
		 * 获取二维码登录状态回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-1-21 下午5:01:39
		 */
		public void onGetQRLoginStatusResult(int errCode);

		/**
		 * 第三方登录回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-6-10 上午9:55:44
		 */
		public void onThirdLoginResult(int errCode);
	}

	/**
	 * 获取当前登录帐户的Kuid.
	 * 
	 * @return
	 * @return long
	 * @author Zhouls
	 * @date 2015-3-5 下午3:03:40
	 */
	public long getKuidLogin() {
		return CldDalKAccount.getInstance().getKuid();
	}

	/**
	 * 获取当前登录帐户的session
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-5 下午3:03:52
	 */
	public String getSession() {
		return CldDalKAccount.getInstance().getSession();
	}

	/**
	 * 获取当前设备的duid.
	 * 
	 * @return
	 * @return long
	 * @author Zhouls
	 * @date 2015-3-5 下午3:04:13
	 */
	public long getDuid() {
		return CldDalKAccount.getInstance().getDuid();
	}

	/**
	 * 获取保存的密码（MD5值）.
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-5 下午3:04:23
	 */
	public String getLoginPwd() {
		return CldKAccount.getInstance().getLoginPwd();
	}

	/**
	 * 获取当前登录帐号的登录名.
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-5 下午3:04:32
	 */
	public String getLoginName() {
		return CldKAccount.getInstance().getLoginName();
	}

	/**
	 * 获取当前登录帐号的用户名
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-5 下午3:04:43
	 */
	public String getUserName() {
		return CldDalKAccount.getInstance().getCldUserInfo().getUserName();
	}

	/**
	 * 获取登录校验使用的bussiness
	 * 
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-3-5 下午3:04:53
	 */
	public int getBusinessid() {
		return CldBllUtil.getInstance().getBussinessid();
	}

	/**
	 * 获取服务端时间
	 * 
	 * @return
	 * @return long
	 * @author Zhouls
	 * @date 2015-3-5 下午3:05:04
	 */
	public long getSvrTime() {
		return CldKAccount.getInstance().getSvrTime();
	}

	/**
	 * 获取上行注册成功后返回的登录名.
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-5 下午3:05:14
	 */
	public String getLoginNameRegSms() {
		return CldDalKAccount.getInstance().getLoginNameRegSms();
	}

	/**
	 * 获取注册成功后返回的登录名.
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-5 下午3:05:24
	 */
	public String getLoginNameReg() {
		return CldDalKAccount.getInstance().getLoginNameReg();
	}

	/**
	 * 判断当前是否登录（获取用户信息绑定）成功.
	 * 
	 * @return
	 * @return boolean
	 * @author Zhouls
	 * @date 2015-3-5 下午3:05:33
	 */
	public boolean isLogined() {
		return CldKAccount.getInstance().getLoginStatus() == 2 ? true : false;
	}

	/**
	 * 修改密码后保存新密码
	 * 
	 * @param newPwd
	 *            新密码
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:05:44
	 */
	public void setNewPwd(String newPwd) {
		CldKAccount.getInstance().setLoginPwd(newPwd);
	}

	/**
	 * 清除本地保存的kuid.
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:05:57
	 */
	public void cleanKuid() {
		CldSetting.put("kuid", "");
	}

	/**
	 * 清除本地保存的session.
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:06:07
	 */
	public void cleanSession() {
		CldSetting.put("session", "");
	}

	/**
	 * 清除本地保存的密码.
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:06:18
	 */
	public void cleanPassword() {
		CldSetting.put("password", "");
	}

	/**
	 * 清除本地保存的登录名.
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:06:29
	 */
	public void cleanLoginName() {
		CldSetting.put("userName", "");
	}

	/**
	 * 获取绑定的手机号
	 * 
	 * @return 绑定的手机号或者""（未登录，或者未绑定手机号）
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-5 下午3:06:38
	 */
	public String getBindMobile() {
		if (isLogined()) {
			CldUserInfo cldUserInfo = CldDalKAccount.getInstance()
					.getCldUserInfo();
			if (cldUserInfo.getMobileBind() == 1) {
				return cldUserInfo.getMobile();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	/**
	 * 
	 * 登录状态
	 * 
	 * @author Zhouls
	 * @date 2015-4-2 下午4:00:27
	 */
	public static class CldLoginStatus {
		/** 未登录 */
		public static final int LOGIN_NONE = 0;
		/** 正在登录 */
		public static final int LOGIN_DOING = LOGIN_NONE + 1;
		/** 已登录 */
		public static final int LOGIN_DONE = LOGIN_DOING + 1;
	}

	/**
	 * 获取登录状态（CldLoginStatus）
	 * 
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-4-2 下午3:57:20
	 */
	public int getLoginStatus() {
		return CldKAccount.getInstance().getLoginStatus();
	}

	/**
	 * 
	 * 获取用户信息回调
	 * 
	 * @author Zhouls
	 * @date 2016-5-16 下午5:10:49
	 */
	public static interface ICldGetUserInfoListener {
		/**
		 * 函数注释
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2016-5-16 下午4:51:57
		 */
		public void onGetUserInfoResult(int errCode);
	}

	/**
	 * 提交星级认证信息
	 * 
	 * @param driveLicenceData
	 *            驾驶证信息
	 * @param vehicleLicenceData
	 *            行驶证信息
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2016-5-6 下午3:30:37
	 */
	public void uploadStarAuth(byte[] driveLicenceData,
			byte[] vehicleLicenceData, ICldResultListener listener) {
		CldKAccount.getInstance().uploadStarAuth(driveLicenceData,
				vehicleLicenceData, listener);
	}

	/**
	 * 
	 * 验证码业务枚举
	 * 
	 * @author Zhouls
	 * @date 2015-5-6 上午9:52:08
	 */
	public enum CldBussinessCode {
		REGISTER, BIND_MOBILE, BIND_EMAIL, MODIFY_MOBILE, MODIFY_EMAIL, UNBIND_MOBILE, UNBIND_EMAIL, RESET_PWD, FAST_LOGIN;
		/**
		 * 获取对应的业务ID
		 * 
		 * @param bussinessCode
		 * @return
		 * @return int
		 * @author Zhouls
		 * @date 2015-5-6 上午9:52:56
		 */
		public static int valueOf(CldBussinessCode bussinessCode) {
			switch (bussinessCode) {
			case REGISTER:
				/**
				 * 下行短信注册
				 */
				return 101;
			case BIND_MOBILE:
			case BIND_EMAIL:
				/**
				 * 绑定手机，邮箱
				 */
				return 102;
			case MODIFY_MOBILE:
			case MODIFY_EMAIL:
				/**
				 * 修改手机
				 */
				return 103;
			case UNBIND_MOBILE:
			case UNBIND_EMAIL:
				/**
				 * 解绑手机
				 */
				return 104;
			case RESET_PWD:
				/**
				 * 重置密码
				 */
				return 105;
			case FAST_LOGIN:
				/**
				 * 快捷登录
				 */
				return 106;
			default:
				return 0;
			}
		}

		/**
		 * 通过bussinessCode获取对应的枚举
		 * 
		 * @param bussinessCode
		 * @return
		 * @return CldBussinessCode
		 * @author Zhouls
		 * @date 2015-5-21 上午8:55:16
		 */
		public static CldBussinessCode value(int bussinessCode) {
			switch (bussinessCode) {
			case 101:
				/**
				 * 下行短信注册
				 */
				return REGISTER;
			case 102:
				/**
				 * 绑定手机
				 */
				return BIND_MOBILE;
			case 103:
				/**
				 * 修改手机
				 */
				return MODIFY_MOBILE;
			case 104:
				/**
				 * 解绑手机
				 */
				return UNBIND_MOBILE;
			case 105:
				/**
				 * 重置密码
				 */
				return RESET_PWD;
			case 106:
				/**
				 * 快捷登录
				 */
				return FAST_LOGIN;
			default:
				return null;
			}
		}
	}
}
