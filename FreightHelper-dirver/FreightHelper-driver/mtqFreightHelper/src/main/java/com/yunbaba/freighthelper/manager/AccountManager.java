/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: AccountManager.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.manager
 * @Description: 账号管理
 * @author: zhaoqy
 * @date: 2017年4月25日 上午10:57:26
 * @version: V1.0
 */

package com.yunbaba.freighthelper.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.freighthelper.MainActivity;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.UserInfo;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.api.CldKAccountAPI.ICldKAccountListener;
import com.yunbaba.ols.sap.bean.CldSapKAParm;

import org.greenrobot.eventbus.EventBus;

public class AccountManager {
	private static AccountManager mAccountManager = null;

	public static AccountManager getInstance() {
		if (mAccountManager == null) {
			synchronized (AccountManager.class) {
				if (mAccountManager == null) {
					mAccountManager = new AccountManager();
				}
			}
		}
		return mAccountManager;
	}

	public void init() {
		if (mAccountManager == null) {
			mAccountManager = new AccountManager();
		}
		CldKAccountAPI.getInstance().setCldKAccountListener(mListener);
	}
	
	public void unInit() {
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
	}
	
	public static Handler mHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				Context context = MainApplication.getContext();
				String str = context.getResources().getString(
						R.string.account_invalid);
				Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
				//if (AppInfo.isForeground(context)) {
					Intent intent = new Intent(context, MainActivity.class);
				//	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				//	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
					intent.putExtra("finishall", true);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP); 
					context.startActivity(intent);

//				AppManager.getInstance().finishAllActivity();
//				
//				
//				CommonTool.restartApplication(context);
				break;
			}
			default:
				break;
			}
		}
	};

	public static ICldKAccountListener mListener = new ICldKAccountListener() {

		/**
		 * 是否已注册回调
		 * 
		 * @param errCode
		 *            (0, 401，411，412，413，414，415，416，417，402 ，910，101)
		 * @param kuid
		 *            errCode=101，回传用户kuid（errCode=0,用户未注册）
		 * @param loginName
		 *            回传判断的登录名
		 * @return void
		 */
		@Override
		public void onIsRegUser(int errCode, long kuid, String loginName) {

		}

		/**
		 * 下行短信注册回调
		 * 
		 * @param errCode
		 *            101用户名已存在; 201手机号已被认证;503注册失败;
		 *            907验证码已过期;908验证码不正确;909验证码已效验;
		 * @param kuid
		 *            errCode=0,回传注册用户Kuid
		 * @param loginName
		 *            errCode=0,回传登录名
		 * @return void
		 */
		public void onRegister(int errCode, long kuid, String loginName) {

		}

		/**
		 * 获取登录二维码回调
		 * 
		 * @param errCode
		 * @return void
		 */
		@Override
		public void onGetQRcodeResult(int errCode) {
			if (errCode == 0) {
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_LOGIN_GET_QRCODE_SUCCESS, 0));
			} else {
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_LOGIN_GET_QRCODE_FAILED,
								errCode));
			}
		}

		/**
		 * 获取二维码登录状态回调
		 * 
		 * @param errCode
		 * @return void
		 */
		@Override
		public void onGetQRLoginStatusResult(int errCode) {
			if (errCode == 0) {
				AccountAPI.getInstance().setLoginStatus(2);
				
				// 登陆成功
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_LOGIN_QRCODE_LOGIN_SUCCESS, 0));
			} else if (errCode == 801) {
				// 二维码过期
				CldKAccountAPI.getInstance().getQRcode(0);
			}
		}

		/**
		 * 获取手机验证码回调
		 * 
		 * @param errCode
		 *            0成功 ; 201手机号已被认证;202手机未认证;500登录已失效;501已重新登录;
		 *            903XX秒内不能重复发送验证码;906发送次数已达最大限制; 910请求时间已过期;
		 * @param bussinessid
		 *            bussinessid
		 * @return void
		 */
		@Override
		public void onGetVerifyCode(int errCode, int bussinessid) {
			switch (bussinessid) {
			case 101:
				// 注册
				if (errCode == 0) {
					EventBus.getDefault()
							.post(new AccountEvent(
									MessageId.MSGID_GET_REG_VERICODE_SUCCESS, 0));
				} else {
					EventBus.getDefault().post(
							new AccountEvent(
									MessageId.MSGID_GET_REG_VERICODE_FAILED,
									errCode));
				}
				break;
			case 102:
				// 绑定
				if (errCode == 0) {
					EventBus.getDefault().post(
							new AccountEvent(
									MessageId.MSGID_GET_BIND_VERICODE_SUCCESS,
									0));
				} else {
					EventBus.getDefault().post(
							new AccountEvent(
									MessageId.MSGID_GET_BIND_VERICODE_SUCCESS,
									errCode));
				}
				break;
			case 103:
				// 改绑
				if (errCode == 0) {
					EventBus.getDefault()
							.post(new AccountEvent(
									MessageId.MSGID_GET_REVISE_BIND_VERICODE_SUCCESS,
									0));
				} else {
					EventBus.getDefault()
							.post(new AccountEvent(
									MessageId.MSGID_GET_REVISE_BIND_VERICODE_FAILED,
									errCode));
				}
				break;
			case 104:
				// 解绑
				if (errCode == 0) {
					EventBus.getDefault()
							.post(new AccountEvent(
									MessageId.MSGID_GET_UNBIND_VERICODE_SUCCESS,
									0));
				} else {
					EventBus.getDefault().post(
							new AccountEvent(
									MessageId.MSGID_GET_UNBIND_VERICODE_FAILED,
									errCode));
				}
				break;
			case 105:
				// 重置密码
				if (errCode == 0) {
					EventBus.getDefault()
							.post(new AccountEvent(
									MessageId.MSGID_GET_RETRIEVE_PWD_VERICODE_SUCCESS,
									0));
				} else {
					EventBus.getDefault()
							.post(new AccountEvent(
									MessageId.MSGID_GET_RETRIEVE_PWD_VERICODE_FAILED,
									errCode));
				}
				break;
			case 106:
				// 快捷登录
				if (errCode == 0) {
					EventBus.getDefault().post(
							new AccountEvent(
									MessageId.MSGID_GET_LOGIN_VERICODE_SUCCESS,
									0));
				} else {
					EventBus.getDefault().post(
							new AccountEvent(
									MessageId.MSGID_GET_LOGIN_VERICODE_FAILED,
									errCode));
				}
				break;
			default:
				break;
			}
		}

		@Override
		public void onCheckMobileVeriCode(int errCode, int bussinessid) {
			switch (bussinessid) {
			case 105:
				// 重置密码
				if (errCode == 0) {
					EventBus.getDefault()
							.post(new AccountEvent(
									MessageId.MSGID_CHECK_RETRIEVE_PWD_VERICODE_SUCCESS,
									0));
				} else {
					EventBus.getDefault()
							.post(new AccountEvent(
									MessageId.MSGID_CHECK_RETRIEVE_PWD_VERICODE_FAILED,
									errCode));
				}
			default:
				break;
			}
		}

		/**
		 * 登录回调
		 * 
		 * @param errCode
		 *            登录接口错误码
		 * @param isFastLogin
		 *            是否是快捷登录或二维码登录
		 * @return void
		 */
		public void onLoginResult(int errCode, boolean isFastLogin) {
			if (errCode == 0) {
				AccountAPI.getInstance().setLoginStatus(2);
				
				if (isFastLogin) {
					// 手机号码登录成功
					EventBus.getDefault().post(
							new AccountEvent(
									MessageId.MSGID_LOGIN_MOBILE_LOGIN_SUCCESS,
									0));
				} else {
					// 账号登录成功
					EventBus.getDefault()
							.post(new AccountEvent(
									MessageId.MSGID_LOGIN_ACCOUNT_LOGIN_SUCCESS,
									0));
				}
			} else {
				if (isFastLogin) {
					// 手机号码登录失败
					EventBus.getDefault().post(
							new AccountEvent(
									MessageId.MSGID_LOGIN_MOBILE_LOGIN_FAILED,
									errCode));
				} else {
					// 账号登录失败
					EventBus.getDefault().post(
							new AccountEvent(
									MessageId.MSGID_LOGIN_ACCOUNT_LOGIN_FAILED,
									errCode));
				}
			}
		}

		/**
		 * 第三方登录回调
		 * 
		 * @param errCode
		 * @return void
		 */
		@Override
		public void onThirdLoginResult(int errCode) {
			if (errCode == 0) {
				AccountAPI.getInstance().setLoginStatus(2);
				
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_LOGIN_THIRD_LOGIN_SUCCESS, 0));
			} else {
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_LOGIN_THIRD_LOGIN_FAILED,
								errCode));
			}
		}

		/**
		 * 自动登录回调
		 * 
		 * @param loginState
		 *            自动登录状态
		 * @param errCode
		 *            自动登录错误码
		 * @return void
		 */
		@Override
		public void onAutoLoginResult(int loginState, int errCode) {
			//AccountAPI.getInstance().setLoginStatus(loginState);
			switch (loginState) {
			case 0: // 账号或密码为空
				break;

			case 1: // 登录中
				break;

			case 2: // 登录成功
				AccountAPI.getInstance().setLoginStatus(2);
				
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_LOGIN_AUTO_LOGIN_SUCCESS,
								errCode));
				
				break;

			case 3: // 密码被其他终端修改 登录失败
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.  MSGID_LOGIN_AUTO_LOGIN_FAILED,
								errCode));
				break;

			case 4:
					EventBus.getDefault().post(
							new AccountEvent(
									MessageId.  MSGID_LOGIN_AUTO_LOGIN_FAILED_NET,
									errCode));
					break;
			default:
				break;
			}
		}

		/**
		 * 用户被挤下线
		 * session 失效回调
		 * @return void
		 */
		@Override
		public void onInValidSession(int session) {
			/*
			 * EventBus.getDefault().post( new
			 * AccountEvent(MessageId.MSGID_LOGIN_SESSION_INVAILD, session));
			 */
			int status = UserManager.getInstance().getUserInfo()
					.getLoginStatus();
			if (status == 2) {
				mHandler.sendEmptyMessage(0);
				AccountAPI.getInstance().loginOut();
				AccountAPI.getInstance().setLoginPwd("");
				AccountAPI.getInstance().setLoginStatus(0);
				AccountAPI.getInstance().uninit();
				UserManager.getInstance().getUserInfo().reset();
			}
		}

		/**
		 * 注销回调
		 * 
		 * @param errCode
		 *            (errCode=0 注销成功 已清除本地密码，Kuid )
		 * @return void
		 */
		@Override
		public void onLoginOutResult(int errCode) {
			if (errCode == 0) {
				EventBus.getDefault().post(
						new AccountEvent(MessageId.MSGID_LOGOUT_SUCCESS, 0));
			} else {
				EventBus.getDefault()
						.post(new AccountEvent(MessageId.MSGID_LOGOUT_FAILED,
								errCode));
			}
		}

		/**
		 * 获取用户信息回调
		 * 
		 * @param errCode
		 *            获取用户信息接口错误码
		 * @return void
		 */
		@Override
		public void onGetUserInfoResult(int errCode) {
			if (errCode == 0) {
				CldSapKAParm.CldUserInfo info = AccountAPI.getInstance().getUserInfoDetail();
				UserInfo userInfo = new UserInfo();
                userInfo.setSuccess(0);
                userInfo.setUserName(info.getLoginName());
                userInfo.setUserAlias(info.getUserAlias());
                userInfo.setSex(info.getSex());
                userInfo.setAddress(info.getAddress());
                userInfo.setImgHead(info.getPhotoPath());
                userInfo.setLoginStatus(2);
                userInfo.setLoginType(3);
                final String bind = AccountAPI.getInstance().getBindMobile();
                // String mobile = info.getMobile();
                // MLog.e(TAG, "mobile= " + mobile);
                // 解决问题：解绑后，还是能从服务器获取到手机号码
                if (!bind.equals(info.getMobile())) {
                    userInfo.setMobile(bind);
                } else {
                    userInfo.setMobile(info.getMobile());
                }
                UserManager.getInstance().setUserInfo(userInfo);
                UserManager.getInstance().getTmpUserInfo().assignVaule(userInfo);


				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_USERINFO_GETDETAIL_SUCCESS, 0));
			} else {
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_USERINFO_GETDETAIL_FAILED,
								errCode));
			}
		}

		/**
		 * 更新用户信息回调
		 * 
		 * @param errCode
		 * @return void
		 */
		@Override
		public void onUpdateUserInfo(int errCode) {
			if (errCode == 0) {
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_USERINFO_UPDATE_SUCCESS, 0));
			} else {
				EventBus.getDefault()
						.post(new AccountEvent(
								MessageId.MSGID_USERINFO_UPDATE_FAILED, errCode));
			}
		}

		/**
		 * 绑定手机号回调
		 * 
		 * @param errCode
		 * @return void
		 */
		@Override
		public void onBindMobile(int errCode) {
			if (errCode == 0) {
				EventBus.getDefault()
						.post(new AccountEvent(
								MessageId.MSGID_USERINFO_BIND_MOBILE_SUCCESS, 0));
			} else {
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_USERINFO_BIND_MOBILE_FAILED,
								errCode));
			}
		}

		/**
		 * 解绑手机号回调
		 * 
		 * @param errCode
		 * @return void
		 */
		@Override
		public void onUnbindMobile(int errCode) {
			if (errCode == 0) {
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_USERINFO_UNBIND_MOBILE_SUCCESS,
								0));
			} else {
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_USERINFO_UNBIND_MOBILE_FAILED,
								errCode));
			}
		}

		/**
		 * 改绑手机号回调
		 * 
		 * @param errCode
		 * @return void
		 */
		@Override
		public void onUpdateMobile(int errCode) {
			if (errCode == 0) {
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_USERINFO_REVISE_MOBILE_SUCCESS,
								0));
			} else {
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_USERINFO_REVISE_MOBILE_FAILED,
								errCode));
			}
		}

		/**
		 * 重置密码回调
		 */
		@Override
		public void onRetrievePwd(int errCode) {
			if (errCode == 0) {
				EventBus.getDefault()
						.post(new AccountEvent(
								MessageId.MSGID_PWD_SET_PWD_SUCCESS, 0));
			} else {
				EventBus.getDefault().post(
						new AccountEvent(MessageId.MSGID_PWD_SET_PWD_FAILED,
								errCode));
			}
		}

		/**
		 * 修改密码回调
		 */
		@Override
		public void onRevisePwd(int errCode) {
			if (errCode == 0) {
				EventBus.getDefault().post(
						new AccountEvent(
								MessageId.MSGID_PWD_MODIFY_PWD_SUCCESS, 0));
			} else {
				EventBus.getDefault().post(
						new AccountEvent(MessageId.MSGID_PWD_MODIFY_PWD_FAILED,
								errCode));
			}
		}

		/**
		 * 快捷登录修改密码回调
		 * 
		 * @param errCode
		 * @return void
		 */
		@Override
		public void onRevisePwdByFastLogin(int errCode) {

		}

		@Override
		public void onRegBySms(int arg0, long arg1, String arg2) {

		}
	};
}
