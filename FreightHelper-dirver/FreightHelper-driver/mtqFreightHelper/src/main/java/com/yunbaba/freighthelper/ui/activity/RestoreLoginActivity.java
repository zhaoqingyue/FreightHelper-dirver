package com.yunbaba.freighthelper.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cld.device.CldPhoneNet;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.freighthelper.bean.UserInfo;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.manager.UserManager;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.sap.bean.CldSapKAParm.CldUserInfo;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RestoreLoginActivity extends Activity {

    //用来恢复登录，避免app被回收后失去登录状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        if (!CldPhoneNet.isNetConnected()) {
            /**
             * 无网络，则直接重启应用
             */
            restartApplication();
        } else {
            if (AccountAPI.getInstance().isLogined()) {

                //TaskOperator.getInstance();

//				String bind = AccountAPI.getInstance().getBindMobile();
//				if (!TextUtils.isEmpty(bind)) {
//					startActivity(MainActivity.class);
//				} else {
//					/**
//					 * 未绑定手机号码, 进入绑定手机号界面
//					 */
//					startActivity(BindMobileActivity.class);
//				}

                finish();

            } else {
                String loginName = AccountAPI.getInstance().getLoginName();
                String pwd = AccountAPI.getInstance().getLoginPwd();
                if (!TextUtils.isEmpty(loginName) && !TextUtils.isEmpty(pwd)) {
                    /**
                     * 自动登录
                     */
                    //TaskOperator.getInstance();

                    AccountAPI.getInstance().startAutoLogin();
                } else {
                    //startActivity(LoginActivity.class);


                    restartApplication();
                }
            }
        }


    }

	@Override
	protected void onResume() {
		super.onResume();
		AppStatApi.statOnPageStart(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppStatApi.statOnPagePause(this);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AccountEvent event) {
        switch (event.msgId) {
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_SUCCESS: {
                //Toast.makeText(this, "自动登录成功", Toast.LENGTH_SHORT).show();


                break;
            }
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_FAILED_NET:
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_FAILED: {
                /**
                 * 自动登录失败，进入登录界面
                 */
                //startActivity(LoginActivity.class);

                restartApplication();
                break;
            }
            case MessageId.MSGID_USERINFO_GETDETAIL_SUCCESS:
            case MessageId.MSGID_USERINFO_GETDETAIL_FAILED: {
                //Toast.makeText(this, "获取用户详情成功", Toast.LENGTH_SHORT).show();
                CldUserInfo info = AccountAPI.getInstance().getUserInfoDetail();
                UserInfo userInfo = new UserInfo();
                userInfo.setSuccess(event.errCode);
                userInfo.setUserName(info.getLoginName());
                userInfo.setUserAlias(info.getUserAlias());
                userInfo.setSex(info.getSex());
                userInfo.setAddress(info.getAddress());
                userInfo.setImgHead(info.getPhotoPath());
                userInfo.setLoginStatus(2);
                userInfo.setLoginType(3);
                final String bind = AccountAPI.getInstance().getBindMobile();
                //String mobile = info.getMobile();
                //MLog.e(TAG, "mobile= " + mobile);
                // 解决问题：解绑后，还是能从服务器获取到手机号码
                if (!bind.equals(info.getMobile())) {
                    userInfo.setMobile(bind);
                } else {
                    userInfo.setMobile(info.getMobile());
                }
                UserManager.getInstance().setUserInfo(userInfo);
                UserManager.getInstance().getTmpUserInfo().assignVaule(userInfo);

                /**
                 * 登录成功后，调货运登录鉴权接口，成功后才可调货运其它接口
                 */
                CldBllKDelivery.getInstance().loginAuth(new ICldResultListener() {

                    @Override
                    public void onGetResult(int errCode) {
                        if (errCode == 0) {
//						if (!TextUtils.isEmpty(bind)) {
//							startActivity(MainActivity.class);
//						} else {
//							/**
//							 * 未绑定手机号码, 进入绑定手机号界面
//							 */
//							Intent intent = new Intent(WelcomeActivity.this,
//									BindMobileActivity.class);
//							intent.putExtra(BindMobileActivity.BIND_EXTRA,
//									BindMobileActivity.BIND_FROM_WELCOMR);
//							startActivity(intent);
//							finish();
//						}

                            //TaskOperator.getInstance();
                            finish();

                        } else {

                            restartApplication();
                        }
                    }

                    @Override
                    public void onGetReqKey(String tag) {


                    }
                });
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    //重启应用
    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
