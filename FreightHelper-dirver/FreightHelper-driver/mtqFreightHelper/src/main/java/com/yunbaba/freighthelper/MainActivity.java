package com.yunbaba.freighthelper;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.navisdk.CldNaviAuthManager;
import com.cld.navisdk.CldNaviAuthManager.CldAuthManagerListener;
import com.cld.navisdk.utils.CldNaviSdkUtils;
import com.cld.net.CldFileDownloader;
import com.cld.net.ICldFileDownloadCallBack;
import com.cld.ols.module.authcheck.CldKAuthcheckAPI;
import com.cld.ols.module.authcheck.bean.CldAkeyType;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.appcenter.AppCenterAPI;
import com.yunbaba.api.trunk.OffLineManager;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.UploadStoreAddrManager;
import com.yunbaba.freighthelper.base.BaseMainFragment.OnBackToFirstListener;
import com.yunbaba.freighthelper.base.MajorMainActivity;
import com.yunbaba.freighthelper.bean.UserInfo;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.bean.eventbus.CloseWaitingUpdateTaskDialogEvent;
import com.yunbaba.freighthelper.bean.eventbus.FeedBackEvent;
import com.yunbaba.freighthelper.bean.eventbus.HandleStatusChangeEvent;
import com.yunbaba.freighthelper.bean.eventbus.NewMsgEvent;
import com.yunbaba.freighthelper.bean.eventbus.NewVersionEvent;
import com.yunbaba.freighthelper.bean.eventbus.RefreshTaskListFromNetEvent;
import com.yunbaba.freighthelper.bean.eventbus.SpeechDownloadEvent;
import com.yunbaba.freighthelper.bean.eventbus.TabSelectedEvent;
import com.yunbaba.freighthelper.bean.eventbus.UpdateTaskStatusEvent;
import com.yunbaba.freighthelper.manager.CarManager;
import com.yunbaba.freighthelper.manager.MsgManager;
import com.yunbaba.freighthelper.manager.NotifyManager;
import com.yunbaba.freighthelper.manager.UserManager;
import com.yunbaba.freighthelper.ui.activity.GuideActivity;
import com.yunbaba.freighthelper.ui.activity.me.AboutActivity;
import com.yunbaba.freighthelper.ui.activity.me.LoginActivity;
import com.yunbaba.freighthelper.ui.activity.task.feedback.FeedBackDialog;
import com.yunbaba.freighthelper.ui.customview.BottomBar;
import com.yunbaba.freighthelper.ui.customview.BottomBarTab;
import com.yunbaba.freighthelper.ui.dialog.UpdateAskDialog;
import com.yunbaba.freighthelper.ui.dialog.UpdateDialog;
import com.yunbaba.freighthelper.ui.fragment.CarFragment;
import com.yunbaba.freighthelper.ui.fragment.MeFragment;
import com.yunbaba.freighthelper.ui.fragment.MsgFragment;
import com.yunbaba.freighthelper.ui.fragment.task.TaskFragment;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.PermissionUtil;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.freighthelper.utils.WaitingUpdateTaskDialog;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBackInfo;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.sap.bean.CldSapKAParm;
import com.yunbaba.ols.sap.bean.CldSapKAppParm;
import com.yunbaba.ols.tools.AppInfoUtils;
import com.yunbaba.ols.tools.model.CldOlsInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class MainActivity extends MajorMainActivity implements OnClickListener, OnBackToFirstListener {

    private String TAG = "MainActivity";
    private SupportFragment[] mFragments = new SupportFragment[4];

    private BottomBar mBottomBar;

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;
    private NotificationManager manager = null;
    public static int CURRENT_PAGE = 0;
    CldFileDownloader mDownloader = null;
    // 间隔多久取历史行程
    private final int INTERVAL_MAXTIME = 1800000;

    /**
     * 定时器Runnale
     */
    private Runnable mTimeRunnale = null;
    private Handler mHander = new Handler();
    private CldSapKAppParm.MtqAppInfoNew mAppParm;
    UpdateAskDialog updatedialog;
    UpdateDialog dialog;

    @Override
    protected void showProgress() {
        WaitingProgressTool.showProgress(this);

//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				WaitingProgressTool.closeshowProgress();
//			}
//		},5000);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;

    }

    @Override
    protected void initFragment(Bundle savedInstanceState) {


        loadRange();


        if (savedInstanceState == null) {
            mFragments[FIRST] = TaskFragment.newInstance();
            mFragments[SECOND] = CarFragment.newInstance();
            mFragments[THIRD] = MsgFragment.newInstance();// ContactFragment.newInstance();
            mFragments[FOURTH] = MeFragment.newInstance();

            loadMultipleRootFragment(R.id.id_main_layout, FIRST, mFragments[FIRST], mFragments[SECOND],
                    mFragments[THIRD], mFragments[FOURTH]);

        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用,也可以通过getSupportFragmentManager.getFragments()自行进行判断查找(效率更高些),用下面的方法查找更方便些
            mFragments[FIRST] = findFragment(TaskFragment.class);
            mFragments[SECOND] = findFragment(CarFragment.class);
            mFragments[THIRD] = findFragment(MsgFragment.class);// findFragment(ContactFragment.class);
            mFragments[FOURTH] = findFragment(MeFragment.class);
        }

        // 这里判断sdk有没有鉴权成功
        if (!CldNaviAuthManager.getInstance().isAuthStatus()
                || CldKAuthcheckAPI.getInstance().getAkeyType() != CldAkeyType.HY) {
            CldNaviAuthManager.getInstance().authenticate(new CldAuthManagerListener() {

                @Override
                public void onAuthResult(int i, String s) {

                    // Toast.makeText(MainActivity.this, s, 100).show();
                }
            }, CldNaviSdkUtils.getAuthValue(getApplicationContext()));
        }

        /**
         * 进入主界面后，再初始化消息管理器 add by zhaoqy 2017-5-11
         */
        MsgManager.getInstance().init();
    }

    @Override
    protected void initViews() {


        MainActivity.CURRENT_PAGE = 0;
        if (GeneralSPHelper.getInstance(this).ReadFirst()) {

            startActivity(new Intent(this, GuideActivity.class));
        }

        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);

        // mBottomBar.addItem(new BottomBarTab(this,
        // R.drawable.main_bottom_task_normal,getString(R.string.main_bottom_task)))
        // .addItem(new BottomBarTab(this,
        // R.drawable.main_bottom_car_normal,getString(R.string.main_bottom_car)))
        // .addItem(new BottomBarTab(this,
        // R.drawable.main_bottom_contacts_normal,getString(R.string.main_bottom_contacts)))
        // .addItem(new BottomBarTab(this,
        // R.drawable.main_bottom_me_normal,getString(R.string.main_bottom_me)));

        mBottomBar.addItem(new BottomBarTab(this, R.drawable.main_bottom_task, getString(R.string.main_bottom_task)))
                .addItem(new BottomBarTab(this, R.drawable.main_bottom_car, getString(R.string.main_bottom_car)))
                .addItem(new BottomBarTab(this, R.drawable.main_bottom_msg, getString(R.string.main_bottom_msg)))
                .addItem(new BottomBarTab(this, R.drawable.main_bottom_me, getString(R.string.main_bottom_me)));

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {

                // MLog.e("showview", "showview select"+position);

                CURRENT_PAGE = position;

                showHideFragment(mFragments[position], mFragments[prePosition]);

                if (position == 3 && mBottomBar.getItem(3).getUnreadCountVisible()) {

                    int version = GeneralSPHelper.getInstance(MainActivity.this).isMeNewRemind();
                    if (version > 0) {

                        GeneralSPHelper.getInstance(MainActivity.this).setIsMeNewRemindClick(version, true);
                        mBottomBar.getItem(3).setUnreadCount(-1);
                    }
                }

            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                SupportFragment currentFragment = mFragments[position];
                int count = currentFragment.getChildFragmentManager().getBackStackEntryCount();

                // // 如果不在该类别Fragment的主页,则回到主页;
                // if (count > 1) {
                // if (currentFragment instanceof TaskFragment) {
                // //currentFragment.popToChild(FirstHomeFragment.class, false);
                // //currentFragment.popTo(TaskFragment.class, false);
                // //currentFragment.
                //
                //
                // } else if (currentFragment instanceof CarFragment) {
                // // currentFragment.popToChild(ViewPagerFragment.class,
                // false);
                // currentFragment.popTo(TaskFragment.class, false);
                // } else if (currentFragment instanceof ContactFragment) {
                // // currentFragment.popToChild(ShopFragment.class, false);
                // currentFragment.popTo(TaskFragment.class, false);
                // } else if (currentFragment instanceof MeFragment) {
                // // currentFragment.popToChild(MeFragment.class, false);
                // currentFragment.popTo(TaskFragment.class, false);
                // }
                // return;
                // }

                // 这里推荐使用EventBus来实现 -> 解耦
                if (count == 1) {
                    // 在FirstPagerFragment中接收, 因为是嵌套的孙子Fragment 所以用EventBus比较方便
                    // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
                    EventBus.getDefault().post(new TabSelectedEvent(position));
                }
            }

        });

        updateNews();
        setMeNewRemind();

        mTimeRunnale = new Runnable() {
            @Override
            public void run() {

                CarManager.getInstance().setmLoginName("");
                if (mTimeRunnale != null) {
                    mHander.removeCallbacks(mTimeRunnale);
                    mHander.postDelayed(mTimeRunnale, INTERVAL_MAXTIME);
                }
            }
        };

        mHander.postDelayed(mTimeRunnale, INTERVAL_MAXTIME);

        // mBottomBar.getItem(2).setUnreadCount(3);
        CheckVersionAndUpdate();
    }

    @Override
    protected void setListener() {
    }

    @Override
    public void onClick(View view) {

    }



    @Override
    protected void onResume() {

        super.onResume();
        MLog.e("MainActivity", "OnResume");

        if (isFirstTime)
            isFirstTime = false;

        /**
         * 初始化车辆管理器 add by zhaoqy 2017-5-12
         */
        //CarManager.getInstance().init(this);

        // updateNews();


        //getExternalStorageDirectory().toString() + File.separator + "MTQFreightHelperFile/debug3.txt"
//		DebugTool.saveFile("duid:"+CldKAccountAPI.getInstance().getDuid()+"  /"+
//				"kuid:"+CldKAccountAPI.getInstance().getKuidLogin()+"  /"+
//				"session:"+CldKAccountAPI.getInstance().getSession()+"  /"+
//				"pwdtype: "+ CldSetting.getInt("ols_ka_pwdtype", 0)+ " /"+
//				"SYSVERSION" + Build.VERSION.SDK_INT
//
//		);
//
//
//
//		DebugTool.saveFile(CldDalKAccount.getInstance().getDuid()
//				+ CldSapUtil.isStrParmRequest("  WifiMac:", CldBllUtil
//				.getInstance().getWifiMac())
//				+ CldSapUtil.isStrParmRequest("   BlueMac:", CldBllUtil
//				.getInstance().getBlueMac())
//				+ CldSapUtil.isStrParmRequest("   Imei:", CldBllUtil
//				.getInstance().getImei())
//				+ CldSapUtil.isStrParmRequest("    Imsi:", CldBllUtil
//				.getInstance().getImsi()) + CldSapUtil.isStrParmRequest("", CldBllUtil.getInstance()
//				.getAppver()));
//
//
//		String filePath;
//
//		boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//		if (hasSDCard) {
//			filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "MTQFreightHelperFile/debuglog.log";
//		} else
//			filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "MTQFreightHelperFile/debuglog.log";
//
//
//		File file;
//		try {
//
//		   file = new File(filePath);
//			if(getFileSize(file) > 102400L)
//			{
//
//				if (file.isFile())
//					file.delete();
//			}else{
//
//				String base64_pic = null;
//				try {
//					base64_pic = Base64.encodeToString(FileUtils.toByteArray(filePath), Base64.DEFAULT);
//				//	MyDebugTool.saveFile(base64_pic);
//				} catch (IOException e) {
//
//					e.printStackTrace();
//				}
//
//
//				CldKDeliveryAPI.getInstance().uploadDeviceLog(CldKAccountAPI.getInstance().getDuid() + "", System.currentTimeMillis() / 1000, AccountAPI.getInstance().getBindMobile()+"_"+System.currentTimeMillis() / 1000+".log", base64_pic,
//						new CldKDeliveryAPI.IUploadDeviceLogListener() {
//							@Override
//							public void onGetResult(int errCode) {
//
//							}
//
//							@Override
//							public void onGetReqKey(String arg0) {
//
//							}
//						});
//
//
//			}
//
//
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}


    }


    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return super.onCreateFragmentAnimator();
    }

    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

    @Override
    public void onBackToFirstFragment() {
        mBottomBar.setCurrentItem(0);
    }

    @Override
    protected void afterInit() {

        //bindService(new Intent(this, TimeEarlyWarningService.class), conn, Context.BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);

    }

//	private ServiceConnection conn = new ServiceConnection() {
//		/** 获取服务对象时的操作 */
//		public void onServiceConnected(ComponentName name, IBinder service) {
//
//			// countService = ((CountService.ServiceBinder)
//			// service).getService();
//
//		}
//
//		/** 无法获取到服务对象时的操作 */
//		public void onServiceDisconnected(ComponentName name) {
//
//			// countService = null;
//		}
//
//	};

    @Override
    protected void onDestroy() {


        NotifyManager.getInstance().cancelAllNotification();
        TaskOperator.getInstance().ClearData();
        EventBus.getDefault().unregister(this);
//
//		if (conn != null)
//			unbindService(conn);
        MLog.e("mainactivity destory", "true");
        super.onDestroy();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SpeechDownloadEvent event) {

        if (!event.speechid.equals("-1")) {

            // DownloadUtil.DownLoadSpeechFile(DeliveryApi.getSpeechFileUrl(event.corpid,event.speechid),
            // this, event.speechid, new DownLoadResultListener() {
            //
            // @Override
            // public void onError(VolleyError arg0) {
            //
            //
            // }
            //
            // @Override
            // public void onDownload(String localpath) {
            //
            //
            // }
            // });

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseDialogEvent(final CloseWaitingUpdateTaskDialogEvent event) {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                WaitingUpdateTaskDialog.getInstance().removeView(event.isupdatesuccess);
            }
        }, 500);

    }

    private void updateNews() {

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final boolean isHasUnreadMsg = MsgManager.getInstance().hasUnReadMsg();


                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isHasUnreadMsg) {

                            mBottomBar.getItem(2).setUnreadCount(1);

                        } else {

                            mBottomBar.getItem(2).setUnreadCount(-1);
                        }
                    }
                });


            }
        });


    }

    private void setMeNewRemind() {

        if (this.isRunning) {

            int version = GeneralSPHelper.getInstance(this).isMeNewRemind();

            if (version > 0) {

                if (version > AppInfoUtils.getVerCode(this)) {


                    if (!GeneralSPHelper.getInstance(this).isMeNewRemindClick(version)) {

                        mBottomBar.getItem(3).setUnreadCount(1);

                    } else {

                        mBottomBar.getItem(3).setUnreadCount(-1);
                    }

                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewMsgEvent event) {
        switch (event.msgId) {
            case MessageId.MSGID_MSG_NEW: {
                updateNews();
                break;
            }
            case MessageId.MSGID_MSG_UPDATE:
                updateNews();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewVersionEvent event) {

        CheckVersionAndUpdate();
        setMeNewRemind();


    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        if (intent.hasExtra("finishall")) {


            Intent it = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(it);

            finish();
        }

        // stopService(new Intent(this, ResumeDataService.class));

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFeedBackEvent(FeedBackEvent event) {

        ShowFeedBackDialog(true, false, event.storeDetail.corpId, event.storeDetail, event.orderDetail,
                event.reasonlist);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode >= FeedBackDialog.IMAGE_CAPTURE - 1 && requestCode <= FeedBackDialog.IMAGE_CAPTURE + 2 ||

                requestCode >= FeedBackDialog.IMAGE_SELECT - 1 && requestCode <= FeedBackDialog.IMAGE_SELECT + 2)

        {

            if (mFeedBackDialog != null)
                mFeedBackDialog.onActivityResult(requestCode, resultCode, data);
            return;
        }

    }

    FeedBackDialog mFeedBackDialog;

    public void ShowFeedBackDialog(final boolean isNeedStopTask, final boolean isLast, final String corpid,
                                   final MtqDeliStoreDetail mStoreDetail, final MtqDeliOrderDetail mOrderDetail,
                                   final List<FeedBackInfo> result) {

        mFeedBackDialog = new FeedBackDialog(MainActivity.this, (ArrayList<FeedBackInfo>) result, mStoreDetail,
                mOrderDetail, isNeedStopTask ? 2 : 1);
        mFeedBackDialog.show();

        if (isNeedStopTask) {

            mFeedBackDialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {



                    if (TaskOperator.getInstance().getmCurrentTask() != null) {

                        EventBus.getDefault()
                                .post(new HandleStatusChangeEvent(
                                        new UpdateTaskStatusEvent(TaskOperator.getInstance().getmCurrentTask().corpid,
                                                TaskOperator.getInstance().getmCurrentTask().taskid, 3,
                                                TaskOperator.getInstance().getmCurrentTask().corpid,
                                                TaskOperator.getInstance().getmCurrentTask().taskid, 0, 0, 0, 0)));
                    }

                }
            });

        } else {

            mFeedBackDialog.setOnDismissListener(null);

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AccountEvent event) {
        switch (event.msgId) {
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_SUCCESS: {
                //Toast.makeText(this, "自动登录成功", Toast.LENGTH_SHORT).show();
                /**
                 * 登录成功后，调货运登录鉴权接口，成功后才可调货运其它接口
                 */


                CldBllKDelivery.getInstance().loginAuth(new CldOlsInterface.ICldResultListener() {

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
                            EventBus.getDefault().post(new RefreshTaskListFromNetEvent());
                            //	loadRange();

                            //	finish();
                            UploadStoreAddrManager.getInstance().CheckAndUpload(MainActivity.this);
                            OffLineManager.getInstance().TraverseOfflineData(MainActivity.this);

                        } else {
                            EventBus.getDefault().post(new RefreshTaskListFromNetEvent());

                        }
                    }

                    @Override
                    public void onGetReqKey(String tag) {


                    }
                });

                break;
            }
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_FAILED_NET:
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_FAILED: {
                /**
                 * 自动登录失败，进入登录界面
                 */
                //startActivity(LoginActivity.class);

                //restartApplication();
                break;
            }
            case MessageId.MSGID_USERINFO_GETDETAIL_SUCCESS:
            case MessageId.MSGID_USERINFO_GETDETAIL_FAILED: {
                //Toast.makeText(this, "获取用户详情成功", Toast.LENGTH_SHORT).show();
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
                //String mobile = info.getMobile();
                //	MLog.e(TAG, "mobile= " + mobile);
                // 解决问题：解绑后，还是能从服务器获取到手机号码
                if (!bind.equals(info.getMobile())) {
                    userInfo.setMobile(bind);
                } else {
                    userInfo.setMobile(info.getMobile());
                }
                UserManager.getInstance().setUserInfo(userInfo);
                UserManager.getInstance().getTmpUserInfo().assignVaule(userInfo);


                break;
            }
            default:
                break;
        }
    }


    private Timer mTimer;
    private TimerTask checkTimeTask;
    private static final int checkPeriod = 5000;

    boolean isFirstTime = true;

    public synchronized Timer getTimer() {

        if (mTimer == null)
            mTimer = new Timer();

        return mTimer;

    }

    private boolean isTimerFirstTime = true;

    private synchronized void startTimer() {

        stopTimer();


        getTimer();

        if (checkTimeTask == null) {
            checkTimeTask = new TimerTask() {
                @Override
                public void run() {
                    //mHandler.sendEmptyMessage(RefreshListToCheckTime);
                    MLog.e("checkislogin", "islogin " + AccountAPI.getInstance().isLogined());
                    if (!(AccountAPI.getInstance().isLogined())) {

                        //MLog.e("check", "start RestoreLoginActivity");

                        //startActivityForResult(new Intent(this, RestoreLoginActivity.class), ReStoreLoginResult);
                        String loginName = AccountAPI.getInstance().getLoginName();
                        String pwd = AccountAPI.getInstance().getLoginPwd();
                        if (!TextUtils.isEmpty(loginName) && !TextUtils.isEmpty(pwd)) {
                            /**
                             * 自动登录
                             */
                            AccountAPI.getInstance().startAutoLogin();
                        }
                    }

                }
            };
        }

        if (mTimer != null && checkTimeTask != null) {
            mTimer.schedule(checkTimeTask, isTimerFirstTime ? checkPeriod : 0, checkPeriod);
            isTimerFirstTime = false;

        }

    }

    private synchronized void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        if (checkTimeTask != null) {
            checkTimeTask.cancel();
            checkTimeTask = null;
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        startTimer();
        // new Handler().postDelayed(new Runnable() {
        //
        // @Override
        // public void run() {
        //
        // if
        // (PermissionUtil.isNeedPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE))
        // {
        //
        // Toast.makeText(MainActivity.this, "请打开储存空间权限",
        // Toast.LENGTH_SHORT).show();
        // return;
        //
        // }
        // }
        // }, 1500);

    }


    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }


    protected void loadRange() {


        if (!(AccountAPI.getInstance().isLogined())) {

            return;

        }

        // 门店权限
        CldKDeliveryAPI.getInstance().getAuthInfoList(new CldKDeliveryAPI.ICldAuthInfoListener() {

            @Override
            public void onGetResult(int errCode, List<CldSapKDeliveryParam.AuthInfoList> lstOfResult) {




                if (errCode != 0) {
                    return;
                }


                for (CldSapKDeliveryParam.AuthInfoList data : lstOfResult) {

                    if (data.range != 0) {
                        GeneralSPHelper.getInstance(MainActivity.this).writeCompanyWantRange(data.corpid, data.range);
                    }

                }


                SPHelper.getInstance(MainActivity.this).writeStoreAuth(lstOfResult);

            }

            @Override
            public void onGetReqKey(String arg0) {


            }
        });

    }


    ///-----------------------检查更新升级----------------------start-------////

    public synchronized void CheckVersionAndUpdate() {


        if(MainApplication.isStartAppShowUpdateDialog)
            return;



        boolean hasNew = AppCenterAPI.getInstance().hasNewVersion();
        if (hasNew) {
            mAppParm = AppCenterAPI.getInstance().getMtqAppInfo();
            if (mAppParm != null && mAppParm.version > 0 && !TextUtils.isEmpty(mAppParm.filepath)) {

                MainApplication.isStartAppShowUpdateDialog = true;

                onCheckResult();
            } else {
                //checkAppVersion();
            }
        } else {
            //checkAppVersion();
        }


    }


    private void onCheckResult() {
        if (mAppParm != null) {
            /**
             * 有新版本
             */
            String title = getResources().getString(R.string.dialog_new_version);
            // String vername = mAppParm.version+"";

            String downloadUrl = mAppParm.filepath;

            int lastIndex = downloadUrl.lastIndexOf("_");
            int lastIndex2 = downloadUrl.lastIndexOf(".apk");

            String vername = "";

            try {

                if (lastIndex == -1 || lastIndex2 == -1 || lastIndex + 1 >= lastIndex2
                        || lastIndex + 1 > downloadUrl.length() || lastIndex2 > downloadUrl.length()) {

                    int lastIndex3 = downloadUrl.lastIndexOf("/");
                    vername = downloadUrl.substring(lastIndex3 + 1, downloadUrl.length());

                } else {
                    vername = downloadUrl.substring(lastIndex + 1, lastIndex2);
                    vername = vername.toUpperCase();
                }

            } catch (Exception e) {

            }

            MLog.e("check", lastIndex + " " + lastIndex2 + " " + downloadUrl + " " + vername);
            // String contentHint = "最新版本:";
            // getResources().getString(
            // R.string.dialog_lastest_version);
            if (TextUtils.isEmpty(vername))
                vername = mAppParm.version + "";

            String content = "最新版本:" + vername;// String.format(contentHint,
            // vername);
            String cancel = getResources().getString(R.string.dialog_no_need);
            String sure = getResources().getString(R.string.dialog_update);
            updatedialog = new UpdateAskDialog(this, title, content, cancel, sure, mAppParm.upgradeflag, new UpdateAskDialog.IAskDialogListener() {

                @Override
                public void OnCancel() {
                    mHadler.sendEmptyMessage(1);
                }

                @Override
                public void OnSure() {
                    updateVersion();
                }
            });
            updatedialog.setCancelable(false);
            updatedialog.show();
        } else {
            mHadler.sendEmptyMessage(0);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHadler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    /**
                     * 已是最新版本
                     */
//					if (ProgressDialog.isShowProgress()) {
//						ProgressDialog.cancelProgress();
//					}
//
//					mUpdate.setCompoundDrawables(null, null, null, null);
//
//					String update = getResources().getString(R.string.about_update_latest);
//					mUpdate.setText(update);
//					break;
                }
                case 1: {


                    break;
                }
                case 2: {
                    onCheckResult();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    protected void updateVersion() {
        if (!CldPhoneNet.isNetConnected()) {
            Toast.makeText(this, R.string.common_network_abnormal, Toast.LENGTH_SHORT).show();
        } else {

            if (PermissionUtil.isNeedPermissionForStorage(this)) {

                Toast.makeText(this, "请打开储存空间权限", Toast.LENGTH_SHORT).show();
                return;

            }

            String downloadUrl = mAppParm.filepath;
            if (!TextUtils.isEmpty(downloadUrl)) {

                //	mTitle.setText("开始下载");

                if (dialog != null)
                    dialog.cancel();

                dialog = new UpdateDialog(this);

                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {


                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_UP) {


                            if (mAppParm != null && mAppParm.upgradeflag == 1)
                                return false;

                            MLog.e("update", "backclick222");

                            if (mDownloader != null) {
                                MLog.e("update", "backclick cancel");
                                mDownloader.stop();
                                mDownloader.resetMonitor();
                                //mDownloader.setCB(null);
                            }


                        }
                        return false;
                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        if (mAppParm != null && mAppParm.upgradeflag == 1) {

                            onCheckResult();


                        }

                    }
                });

                dialog.setCancelable((mAppParm != null && mAppParm.upgradeflag == 1) ? false : true);
                dialog.show();
                startDownloadApk(downloadUrl);
            } else {
                /**
                 * 下载失败
                 */
                updateFail();
            }
        }
    }

    private void updateFail() {
        String string = "下载失败，请检查网络";
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private void startDownloadApk(String url) {
        if (mDownloader == null) {
            mDownloader = new CldFileDownloader();
        }


        mDownloader.stop();
        mDownloader.resetMonitor();

        // int lastIndex3 = url.lastIndexOf("/");f
        // String filename = url.substring(lastIndex3 + 1, url.length());

        String target = MainApplication.getMTQFileStorePath() + "/update" + ".apk";
        mDownloader.downloadFile(url, target, false, mDownloadCB);


    }

    ICldFileDownloadCallBack mDownloadCB = new ICldFileDownloadCallBack() {

        @Override
        public void onConnecting(boolean bReconnect, String errMsg) {
            MLog.e("update", "bReconnect: " + bReconnect + ", msg: " + bReconnect);

//			if(bReconnect == false){
//				updateHandler.sendEmptyMessage(0);
//			}


            if (!CldPhoneNet.isNetConnected()) {
                updateHandler.sendEmptyMessage(5);
            }


        }

        @Override
        public void updateProgress(long down, long total, long rate) {
            int progress = (int) ((down * 1.0 / total) * 100);
            MLog.i("down: " + down + ", total: " + total + ", rate: " + rate + ",progress: " + progress + "%");

            // MLog.e(TAG, progress + "%");
            Message msg = new Message();
            AboutActivity.DownProgress bean = new AboutActivity.DownProgress(down, total, rate);
            msg.what = 1;
            msg.obj = bean;
            updateHandler.sendMessage(msg);
        }

        @Override
        public void onSuccess(long size, long elapseMs) {
            MLog.i("onSuccess!!!");
            updateHandler.sendEmptyMessage(2);
        }

        @Override
        public void onFailure(String errMsg) {
            MLog.i("onFailure!!! errMsg: " + errMsg);
            updateHandler.sendEmptyMessage(0);
        }

        @Override
        public void onCancel() {
            MLog.i("onCancel!!!");
            updateHandler.sendEmptyMessage(3);
        }
    };


    @SuppressLint("HandlerLeak")
    private Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0: // 下载失败
                        if (null != dialog) {
                            dialog.dismiss();
                        }
                        if (mDownloader != null) {
                            mDownloader.stop();
                            mDownloader.resetMonitor();
                            //mDownloader.setCB(null);

                        }
                        //Toast.makeText(AboutActivity.this, "下载失败，请检查网络", Toast.LENGTH_SHORT).show();
                        updateFail();
                        // downloadFailNotifacation();
                        break;
                    case 1: // 下载中
                        AboutActivity.DownProgress bean = (AboutActivity.DownProgress) msg.obj;
                        if (null == mAppParm) {
                            MLog.e("upgradeInfo is null!");
                            return;
                        }
                        if (null == bean) {
                            MLog.e("DOWN bean is null!");
                            return;
                        }
                        int progress = bean.progress;
                        dialog.setProgressBar(progress);
                        // updateNotifacation(bean);
                        break;
                    case 2: // 下载完成

                        dialog.setProgressBar(100);

                        postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                cancelUpdateNotification();
                                installApk();
                            }
                        }, 500);


                        break;
                    case 3:// 取消
                        cancelUpdateNotification();


                        break;

                    case 5:
                        if (null != dialog) {
                            dialog.dismiss();
                        }

                        if (mDownloader != null) {

                            mDownloader.setCB(null);
                            mDownloader.stop();
                            mDownloader.resetMonitor();

                        }
                        updateFail();
                        // downloadFailNotifacation();
                        break;

                }
            }
            super.handleMessage(msg);
        }
    };


    /**
     * @return void
     * @Description 取消软件更新通知
     * @author：Huagx @date：2014-6-12 下午4:15:31
     */
    private void cancelUpdateNotification() {
        if (manager != null) {
            if (dialog != null) {
                dialog.dismiss();
            }
            manager.cancel(UPDATE_NOTIFICATION_ID);
            manager = null;
        }
    }
    private int UPDATE_NOTIFICATION_ID = 140612;
    /**
     * @return void
     * @Description 安装apk
     * @author：Wenap @date：2014-12-31 下午8:32:53
     */

    protected void installApk() {

        if (dialog != null)
            dialog.dismiss();


        Intent intent = new Intent(Intent.ACTION_VIEW);
        String target = MainApplication.getMTQFileStorePath() + "/update.apk";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, "com.yunbaba.freighthelper.fileprovider", new File(target));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

        } else {
            intent.setDataAndType(Uri.fromFile(new File(target)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        //if (this.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //}


        /**
         * 清楚版本信息数据
         */
        AppCenterAPI.getInstance().clearAppVersion();


        //finish();
    }


    ///-----------------------检查更新升级----------------------end-------////
}
