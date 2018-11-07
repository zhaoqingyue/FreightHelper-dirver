package com.yunbaba.freighthelper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.cld.base.CldBase;
import com.cld.base.CldBaseParam;
import com.cld.log.CldLog;
import com.cld.mapapi.map.CldMap;
import com.cld.navisdk.CldNaviAuthManager;
import com.cld.navisdk.utils.CldNaviSdkUtils;
import com.cld.nv.env.CldNvBaseEnv;
import com.cld.nv.frame.AppProperty;
import com.cld.nv.frame.CldNvBaseManager;
import com.cld.setting.CldSetting;
import com.tendcloud.tenddata.TCAgent;
import com.yunbaba.api.map.NavigateAPI;
import com.yunbaba.api.trunk.OrmLiteApi;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.freighthelper.manager.AccountManager;
import com.yunbaba.freighthelper.manager.AppVersionManager;
import com.yunbaba.freighthelper.manager.CarManager;
import com.yunbaba.freighthelper.manager.MsgManager;
import com.yunbaba.freighthelper.manager.NotifyManager;
import com.yunbaba.freighthelper.manager.UserManager;
import com.yunbaba.freighthelper.utils.CldCrashReport;
import com.yunbaba.freighthelper.utils.Config;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.freighthelper.utils.WaitingUpdateTaskDialog;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.api.CldOlsBase;

import java.io.File;

import hmi.packages.HPGuidanceAPI;

public class MainApplication extends Application {

    private static Context mContext = null;

    private static int activityCount = 0;
    public static boolean isStartAppShowUpdateDialog =  false;


    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static int getActivityCount() {
        return activityCount;
    }


    @Override
    public void onCreate() {

        super.onCreate();
        //  BlockCanary.install(this, new AppBlockCanaryContext()).start();

        activityCount = 0;


        mContext = this;
        // mOrmLiteApi = OrmLiteApi.getInstance();

        /**
         * talkingdata数据统计初始化
         */
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TCAgent.init(this.getApplicationContext(), "98EE287E397B433E84E4AB78C5689057", "0000");
        TCAgent.setReportUncaughtExceptions(true);

        OrmLiteApi.setContext(this);
        //	MLog.e("MainApplication", "OnCreate");
//		OrmLiteApi.init(this);

        //CldSdkCxt.setAppContext(getApplicationContext());
        // 初始化cldbase
        CldBaseParam param = new CldBaseParam();
        param.ctx = getApplicationContext();
        CldBase.init(param);


        int res = CldSetting.getInt("ols_ka_pwdtype", 0);
        long ress = CldKAccountAPI.getInstance().getDuid();
        MLog.e("initsdkorigin", "res: " + res + " duid:" + ress);


        CldLog.setLogCat(false);
        CldLog.setLogFileName(getMTQFileStorePath() + "Log.txt");
        CldLog.setLogEMode(false);
        CldLog.setLogFile(false);


        CldOlsBase.CldOlsParam initParam = new CldOlsBase.CldOlsParam();
        initParam.appver = "M1831-D7160-3H23J22";
        //M1831-D7160-3H23J22

        // M3478-L7032-3723J0Q
        // zsx 发布正式需要把这个改成false
        initParam.isTestVersion = BuildConfig.IS_TESTVERSION;

        initParam.isDefInit = false;

        initParam.apptype = Config.apptype;
        initParam.appid = Config.appid;
        initParam.bussinessid = Config.bussinessid;

        // initParam.apptype = 31;
        // initParam.appid = 25;
        // initParam.bussinessid = 7;
        // initParam.cid = 1060;

        // initParam.apptype = 31;
        // initParam.appid = 25;
        // initParam.bussinessid = 7;

        initParam.cid = 1060;
        initParam.mapver = "37200B13J0Q010A1";


        //   MLog.e("initsdkorigin2", "res: " + CldSetting.getInt("ols_ka_pwdtype", 0) + " duid:" + CldKAccountAPI.getInstance().getDuid());

        CldOlsBase.getInstance().init(initParam, new CldOlsBase.IInitListener() {
            @Override
            public void onUpdateConfig() {

                MLog.e("ols", "ConfigUpdated!");
            }

            @Override
            public void onInitDuid() {

                MLog.e("ols", "duid:" + CldKAccountAPI.getInstance().getDuid());
            }
        });
        AppProperty appProperty = new AppProperty();
        appProperty.appId = initParam.appid;
        appProperty.appType = initParam.apptype;
        appProperty.busssinessId = initParam.bussinessid;
        appProperty.useOpenPlatForm = false;

        //  MLog.e("initsdkorigin2.5", "res: " + CldSetting.getInt("ols_ka_pwdtype", 0) + " duid:" + CldKAccountAPI.getInstance().getDuid());

        CldNvBaseManager.getInstance().setAppProperty(appProperty);

        //  MLog.e("initsdkorigin3", "res: " + CldSetting.getInt("ols_ka_pwdtype", 0) + " duid:" + CldKAccountAPI.getInstance().getDuid());


        AccountManager.getInstance().init();

        //   MLog.e("initsdkorigin4", "res: " + CldSetting.getInt("ols_ka_pwdtype", 0) + " duid:" + CldKAccountAPI.getInstance().getDuid());

        UserManager.getInstance().init();

        //   MLog.e("initsdkorigin5", "res: " + CldSetting.getInt("ols_ka_pwdtype", 0) + " duid:" + CldKAccountAPI.getInstance().getDuid());

        NotifyManager.getInstance().init(mContext);

        //    MLog.e("initsdkorigin6", "res: " + CldSetting.getInt("ols_ka_pwdtype", 0) + " duid:" + CldKAccountAPI.getInstance().getDuid());

        AppVersionManager.getInstance().init();

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                //TaskOperator.getInstance();
                CarManager.getInstance().init(mContext);
                // MsgManager.getInstance().init();


            }
        });


        MLog.e("klogin", "account" + CldKAccountAPI.getInstance().getKuidLogin());

        TaskOperator.getInstance();


        // 临时文件、缓存文件储存本地地址
        File file = new File(Environment.getExternalStorageDirectory() + "/MTQFreightHelperFile");
        if (!file.exists())
            file.mkdirs();
        file = null;

        file = new File(Environment.getExternalStorageDirectory() + "/MTQFreightHelperFile/tempFile");
        if (!file.exists())
            file.mkdirs();

        file = null;


        if (!BuildConfig.LOG_DEBUG) {


            CldCrashReport crashReport = new CldCrashReport();
            crashReport.init(getApplicationContext());

        }
        /**
         * 监听是否切到后台
         * */
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                //	@BindView(v("viclee", activity + "onActivityStarted");
                if (activityCount == 0) {
                    //  MLog.e("mtqfreight lifecycle", ">>>>>>>>>>>>>>>>>>>切到前台  lifecycle");
                }
                activityCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                if (activityCount == 0) {
                    //   MLog.e("mtqfreight lifecycle", ">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");

                    WaitingUpdateTaskDialog.getInstance().removeView();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && GeneralSPHelper.getInstance(this).ReadFirst()) {
            //如果是6.0，而且是第一次打开，等到welcomeactivity询问完权限再初始化
//            if (!) {
//
//            }
        } else {

            initSDK();

        }

//        AppStatApi.statStart(getApplicationContext());
//        AppStatApi.statSetDebugOn(true);

    }

    public static String getTmpCacheFilePath() {

        return Environment.getExternalStorageDirectory() + "/FreightHelperFile/tempFile";


    }

    public static String getMTQFileStorePath() {

        return Environment.getExternalStorageDirectory() + "/FreightHelperFile/";
    }

    public static Context getContext() {
        return mContext;
    }

    public static Context getContextWay() {
        return getContext();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        MsgManager.getInstance().unInit();
        AccountManager.getInstance().unInit();
        NotifyManager.getInstance().unInit();
    }


    private void initSDK() {


        int res = CldSetting.getInt("ols_ka_pwdtype", 0);
        long ress = CldKAccountAPI.getInstance().getDuid();
        MLog.e("initsdk", "res: " + res + " duid:" + ress);

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


				Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
                //      MLog.e("check", str + " " + arg1);
            }

            @Override
            public void initSuccess() {

                //       MLog.e("check", "initsuccess");
            }

            @Override
            public void initStart() {

                //  MLog.e("check", "initstart");
            }

            @Override
            public void initFailed(String arg0) {

                //    MLog.e("check", arg0);
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
