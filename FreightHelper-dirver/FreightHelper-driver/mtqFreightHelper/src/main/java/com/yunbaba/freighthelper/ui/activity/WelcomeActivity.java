/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: WelcomeActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity
 * @Description: 欢迎界面
 * @author: zhaoqy
 * @date: 2017年3月21日 下午2:33:52
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.cld.mapapi.map.CldMap;
import com.cld.navisdk.CldNaviAuthManager;
import com.cld.navisdk.utils.CldNaviSdkUtils;
import com.cld.nv.env.CldNvBaseEnv;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.api.map.NavigateAPI;
import com.yunbaba.apitest.activity.DebugTool;
import com.yunbaba.freighthelper.BuildConfig;
import com.yunbaba.freighthelper.MainActivity;
import com.yunbaba.freighthelper.bean.UserInfo;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.bean.eventbus.NewVersionEvent;
import com.yunbaba.freighthelper.manager.AppVersionManager;
import com.yunbaba.freighthelper.manager.UserManager;
import com.yunbaba.freighthelper.ui.activity.me.LoginActivity;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.NetWorkUtils;
import com.yunbaba.freighthelper.utils.PermissionUtil;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.ols.dal.CldDalKAccount;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.module.delivery.CldDalKDelivery;
import com.yunbaba.ols.sap.bean.CldSapKAParm;
import com.yunbaba.ols.sap.bean.CldSapKAppParm;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import hmi.packages.HPGuidanceAPI;

import static com.yunbaba.freighthelper.ui.activity.me.LoginActivity.LOGIN_EXTRA;

public class WelcomeActivity extends Activity {

    public final String TAG = "WelcomeActivity";

	/*
     * @Override protected int getLayoutResID() { return
	 * R.layout.activity_welcome; }
	 */

    private int time = 0;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        AppStatApi.statStart(getApplicationContext());

        if ( BuildConfig.LOG_DEBUG ) {
            AppStatApi.statSetDebugOn(true);
        } else {
            AppStatApi.statSetDebugOn(false);
        }

        String testDeviceId = StatService.getTestDeviceId(this);
        android.util.Log.d("BaiduMobStat", "Test DeviceId : " + testDeviceId);


    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    boolean isFirstTime = true;

    @Override
    protected void onResume() {
        super.onResume();

        AppStatApi.statOnPageStart(this);
     //   AccountAPI.getInstance().startAutoLogin();
//        HPDefine.HPWPoint nextPoint = new HPDefine.HPWPoint();
//        nextPoint.x =  412230500;
//        nextPoint.y = 83313700;
//        LatLng p1LL = CldCoordUtil.gcjToCLDEx(CldCoordUtil.cldToGCJ(nextPoint));
//
////
////        HPDefine.HPWPoint nextPoint2 = new HPDefine.HPWPoint();
////        nextPoint2.x =  412231000;
////        nextPoint2.y = 83314800;
//
//
////        LatLng p2LL = new LatLng();
////        p2LL.latitude = bean.latitude;
////        p2LL.longitude = bean.longitude;
//
//
//        HPDefine.HPWPoint nextPoint2 = CldCoordUtil.kcodeToCLD("8t7m8fyig");
//
//        LatLng p2LL = CldCoordUtil.gcjToCLDEx(CldCoordUtil.cldToGCJ(nextPoint2));
//        double dis = DistanceUtil.getDistance(p2LL, p1LL);
//
//
//
//        MLog.e("distance","rs"+dis);




        MLog.e("welcomeactivity", " onresume  "+  CldDalKAccount.getInstance().getPwdtype());

        if (isFirstTime) {

            isFirstTime = false;

            if (!PermissionUtil.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || !PermissionUtil.isGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    || !PermissionUtil.isGranted(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    || !PermissionUtil.isGranted(this, Manifest.permission.READ_PHONE_STATE)) {
                //		|| !PermissionUtil.isGranted(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {

                List<String> reqlist = new ArrayList<String>();

                if (!PermissionUtil.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    reqlist.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                }

                if (!PermissionUtil.isGranted(this, Manifest.permission.ACCESS_FINE_LOCATION))
                    reqlist.add(Manifest.permission.ACCESS_FINE_LOCATION);

                if (!PermissionUtil.isGranted(this, Manifest.permission.READ_PHONE_STATE))
                    reqlist.add(Manifest.permission.READ_PHONE_STATE);

                if (!PermissionUtil.isGranted(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    reqlist.add(Manifest.permission.ACCESS_COARSE_LOCATION);


//				if(!PermissionUtil.isGranted(this, Manifest.permission.SYSTEM_ALERT_WINDOW))
//					reqlist.add(Manifest.permission.SYSTEM_ALERT_WINDOW);

                String[] reqarray = new String[reqlist.size()];

                for (int i = 0; i < reqlist.size(); i++) {
                    reqarray[i] = (String) reqlist.get(i);
                }

                ActivityCompat.requestPermissions(this, reqarray, 119);

                Toast.makeText(WelcomeActivity.this, "请授予权限以便更好地使用地图功能", Toast.LENGTH_SHORT).show();
            } else {
                Jump();

            }

        } else
            Jump();

        //检查新版本
        AppVersionManager.getInstance().checkVersion(new AppVersionManager.IAppVersionListener() {

            @Override
            public void onResult(int errCode, CldSapKAppParm.MtqAppInfoNew result) {
//                if (ProgressDialog.isShowProgress()) {
//                    ProgressDialog.cancelProgress();
//                }

                MLog.d(TAG, "errCode: " + errCode + ", result: " + result == null ? "null" : "not null");
                if (errCode == 0) {
                    CldSapKAppParm.MtqAppInfoNew mAppParm = result;

                    if (mAppParm != null && mAppParm.version > 0 && !TextUtils.isEmpty(mAppParm.filepath)) {
                       // mHadler.sendEmptyMessage(1);

                      //  mAppParm.version
                        GeneralSPHelper.getInstance(WelcomeActivity.this).setIsMeNewRemind(mAppParm.version);
                        EventBus.getDefault().post(new NewVersionEvent());

                    } else {
                      //  mHadler.sendEmptyMessage(0);
                        GeneralSPHelper.getInstance(WelcomeActivity.this).setIsMeNewRemind(-1);
                    }
                } else {
                    // mHadler.sendEmptyMessage(0);
                    /**
                     * 获取失败
                     */
                   // DebugTool.saveFile(TAG, "checkVersion failed. errCode: " + errCode);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppStatApi.statOnPagePause(this);
    }

    public void CheckAndInitSdk(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M  && GeneralSPHelper.getInstance(this).ReadFirst()) {

            if(!NavigateAPI.getInstance().isInit())
               initSDK();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 119) {

            CheckAndInitSdk();

            MLog.e("welcomeactivity", " 授予权限返回");

//			if (grantResults[0] == PackageManager.PERMISSION_GRANTED&&permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {

//				MLog.e("授予权限", " " + grantResults[0] + " " + grantResults[1]);

            //授权要重新初始化，不然取不到duid。授权不了
//				NavigateAPI.getInstance().init(this, new NaviInitListener() {
//
//					@Override
//					public void onAuthResult(int status, String arg1) {
//
//
//						String str;
//						if (0 == status) {// 初始化结果状态判断
//							str = "key校验成功!";
//						} else {
//							str = "key校验失败!";
//						}
//
//						Toast.makeText(WelcomeActivity.this, str, Toast.LENGTH_SHORT).show();
//						MLog.e("check", str + " " + arg1);
//
//					}
//
//					@Override
//					public void initSuccess() {
//
//						MLog.e("check", "initsuccess");
//					}
//
//					@Override
//					public void initStart() {
//
//						MLog.e("check", "initstart");
//					}
//
//					@Override
//					public void initFailed(String arg0) {
//
//						MLog.e("check", arg0);
//					}
//				});

//			} else {
//				// Permission Denied
//				// Toast.makeText(WelcomeActivity.this, "请授予权限以便更好使用app功能",
//				// Toast.LENGTH_SHORT).show();
//			}

            // Jump();

            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void Jump() {
        //	initSDK();

       // TaskOperator.getInstance();
        CheckAndInitSdk();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                /**
                 * 要执行的操作
                 */

                onJump();

                //startActivity(LoginActivity.class,true);

            }
        },0);
    }

    boolean firstTime = true;

    @SuppressLint("NewApi")
    private void onJump() {
        NetWorkUtils.isNetworkConnected(this, new NetWorkUtils.OnNetworkListener() {
                    @Override
                    public void isAvailable(final boolean isAvailable) {

                        MLog.e("jumptologin", isAvailable+" net");
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //更新UI
                                if (!isAvailable) {
                                    /**
                                     * 无网络，则直接进入登录界面
                                     */
                                    //Toast.makeText(this, R.string.common_network_abnormal, Toast.LENGTH_SHORT).show();
                                    if (firstTime) {
                                      //  Toast.makeText(WelcomeActivity.this, "当前无网络，请检查网络设置", Toast.LENGTH_LONG).show();
                                        firstTime = false;
                                    }

                                    String loginName = AccountAPI.getInstance().getLoginName();
                                    String pwd = AccountAPI.getInstance().getLoginPwd();
                                    if (!TextUtils.isEmpty(loginName) && !TextUtils.isEmpty(pwd)) {

                                        //AccountAPI.getInstance().startAutoLogin();
                                        DebugTool.saveFile( "无网络已登录直接进入主页");
                                        startActivity(MainActivity.class);
                                    } else {

                                        DebugTool.saveFile("无网络未登录进入登录页面");
                                        startActivity(LoginActivity.class,true);
                                    }


                                } else {
                                    if (AccountAPI.getInstance().isLogined()) {
                                     //   String bind = AccountAPI.getInstance().getBindMobile();
                                     //   if (!TextUtils.isEmpty(bind)) {
                                        DebugTool.saveFile("有网络已登录进入主页面");
                                            startActivity(MainActivity.class);
//                                        } else {
//                                            /**
//                                             * 未绑定手机号码, 进入绑定手机号界面
//                                             */
//                                            startActivity(BindMobileActivity.class);
//                                        }
                                    } else {
                                        String loginName = AccountAPI.getInstance().getLoginName();
                                        String pwd = AccountAPI.getInstance().getLoginPwd();
                                        if (!TextUtils.isEmpty(loginName) && !TextUtils.isEmpty(pwd)) {
                                            /**
                                             * 自动登录
                                             */
                                            DebugTool.saveFile("欢迎页登录鉴权有网络未登录自动登录");
                                            AccountAPI.getInstance().startAutoLogin();
//                                            new Handler().postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    startActivity(MainActivity.class);
//                                                }
//                                            },10000);


                                        } else {
                                            DebugTool.saveFile("欢迎页登录鉴权有网络未登录过进入登录页面");
                                            startActivity(LoginActivity.class,true);


                                        }
                                    }
                                }
                            }

                        });


                    }
                }
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AccountEvent event) {
        switch (event.msgId) {
//            case MessageId.MSGID_LOGIN_AUTO_LOGIN_SUCCESS: {
//                // Toast.makeText(this, "自动登录成功", Toast.LENGTH_SHORT).show();
//                break;
//            }
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_FAILED:{

                if(isFinishing())
                    return;

               startActivity(LoginActivity.class);

              // startActivity(MainActivity.class);
                break;
            }
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_FAILED_NET: {
                /**
                 * 自动登录失败，进入登录界面
                 */
                //startActivity(LoginActivity.class);

//                if (time < 2) {
//
//                    time++;
//                    String loginName = AccountAPI.getInstance().getLoginName();
//                    String pwd = AccountAPI.getInstance().getLoginPwd();
//                    if (!TextUtils.isEmpty(loginName) && !TextUtils.isEmpty(pwd)) {
//                        /**
//                         * 自动登录
//                         */
//                        AccountAPI.getInstance().startAutoLogin();
//                    } else {
//                        startActivity(LoginActivity.class,true);
//                    }
//
//
//                } else {
                DebugTool.saveFile("登录失败直接进入主页");

                    startActivity(MainActivity.class);
        //        }

//			Toast.makeText(this, "登录失败，请检查网络设置", Toast.LENGTH_LONG).show();
//			onJump();
                break;
            }

           case MessageId.MSGID_USERINFO_GETDETAIL_SUCCESS:
            case MessageId.MSGID_USERINFO_GETDETAIL_FAILED: {
                // Toast.makeText(this, "获取用户详情成功", Toast.LENGTH_SHORT).show();
                CldSapKAParm.CldUserInfo info = AccountAPI.getInstance().getUserInfoDetail();
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
                // String mobile = info.getMobile();
                // DebugTool.saveFile(TAG, "mobile= " + mobile);
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
                 break;
            }
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_SUCCESS:{
                MLog.e("loginauth","loginsuccessandstartauth");
               LoginAuth(null);

              // startActivity(MainActivity.class);
                break;
            }
            default:
                break;
        }
    }

   int authTime = 2;

    public void LoginAuth(final String bind ){

        CldBllKDelivery.getInstance().loginAuth(new ICldResultListener() {

            @Override
            public void onGetResult(int errCode) {
                if (errCode == 0) {
                    //if (!TextUtils.isEmpty(bind)) {
                    CldDalKDelivery.getInstance().setLoginAuth(true);


                    if(isFinishing())
                        return;
                    WelcomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DebugTool.saveFile("鉴权成功进入主页面");
                            startActivity(MainActivity.class);
                        }
                    });

//                    } else {
//                        /**
//                         * 未绑定手机号码, 进入绑定手机号界面
//                         */
//                        Intent intent = new Intent(WelcomeActivity.this, BindMobileActivity.class);
//                        intent.putExtra(BindMobileActivity.BIND_EXTRA, BindMobileActivity.BIND_FROM_WELCOMR);
//                        startActivity(intent);
//                        finish();
//                    }
                }else{



                    authTime --;
                    if(authTime <1){


                        if(isFinishing())
                            return;
                        startActivity(LoginActivity.class,true);

                    }else
                     LoginAuth(bind);


                }
            }

            @Override
            public void onGetReqKey(String tag) {


            }
        });


    }



    private void startActivity(Class<?> cls) {

        if(isFinishing())
            return;

        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();
    }


    private void startActivity(Class<LoginActivity> cls, boolean b) {


        if(isFinishing())
            return;

        MLog.e("jumptologin","true");

        Intent intent = new Intent(this, cls);
        intent.putExtra(LOGIN_EXTRA,LoginActivity.LOGIN_FROM_AUTOLOGINFAIL);
        startActivity(intent);
        finish();



    }


    private void initSDK() {
        NavigateAPI.getInstance().init(this, new CldMap.NaviInitListener() {

            @Override
            public void onAuthResult(int status, String arg1) {


                String str;
                if (0 == status) {// 初始化结果状态判断
                    str = "key校验成功!";
                } else {
                    str = "key校验失败!";
                }

                if (status != 0) {
                    authenticate();
                }

                HPGuidanceAPI.HPGDVoiceSettings voiceSet = new HPGuidanceAPI.HPGDVoiceSettings();
                voiceSet.uiNearDestDist = 15;
                voiceSet.blGdOtParking = false;
                HPGuidanceAPI gdApi = CldNvBaseEnv.getHpSysEnv().getGuidanceAPI();
                gdApi.setVoiceSettings(voiceSet);


//				Toast.makeText(WelcomeActivity.this, str, Toast.LENGTH_SHORT).show();
                MLog.e("check", str + " " + arg1);
            }

            @Override
            public void initSuccess() {

                MLog.e("check", "initsuccess");
            }

            @Override
            public void initStart() {

                MLog.e("check", "initstart");
            }

            @Override
            public void initFailed(String arg0) {

                MLog.e("check", arg0);
            }
        });

    }


    static int mEnticateCnt = 0;
    static boolean mIsAuthStatus = false;
    static boolean mIsAuthing = false;  //是否鉴权中


    private void authenticate() {
        mEnticateCnt = 0;
        mIsAuthStatus = false;

        Runnable r = new Runnable() {

            @Override
            public void run() {


                while (mEnticateCnt < 3 && !mIsAuthStatus) {
                    //鉴权不成功且鉴权次数少于3次
                    if (mIsAuthing) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        continue;
                    }

                    if (mEnticateCnt < 3 && !mIsAuthStatus) {
                        mIsAuthing = true;
                        CldNaviAuthManager.getInstance().authenticate(new CldNaviAuthManager.CldAuthManagerListener() {

                            @Override
                            public void onAuthResult(int i, String s) {

                                mIsAuthing = false;
                                mEnticateCnt++;
                                if (i == 0) {
                                    mIsAuthStatus = true;
                                }
//								System.out.println("authenticate mEnticateCnt ="+ mEnticateCnt + " " +s);
                            }
                        }, CldNaviSdkUtils.getAuthValue(getApplicationContext()));
                    } else {
                        break;
                    }
                }
            }
        };

        ThreadPoolTool.getInstance().execute(r);
    }
}
