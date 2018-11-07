package com.yunbaba.freighthelper.ui.activity.task;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.location.CldLocation;
import com.cld.location.ICldLocationListener;
import com.cld.mapapi.model.LatLng;
import com.cld.nv.location.CldCoordUtil;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.api.map.LocationAPI;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.FreightPointDeal;
import com.yunbaba.api.trunk.OfflineLocationTool;
import com.yunbaba.api.trunk.OrmLiteApi;
import com.yunbaba.api.trunk.bean.OnUIResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseMainActivity;
import com.yunbaba.freighthelper.base.BaseMainFragment.OnBackToFirstListener;
import com.yunbaba.freighthelper.bean.AddressBean;
import com.yunbaba.freighthelper.bean.OfflineLocationBean;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointEvent;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointUpdateEvent;
import com.yunbaba.freighthelper.bean.eventbus.GetLocationAddrEvent;
import com.yunbaba.freighthelper.bean.eventbus.RefreshAdapterEvent;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.ui.activity.RoutePreviewActivity;
import com.yunbaba.freighthelper.ui.customview.TaskAskPopUpDialog;
import com.yunbaba.freighthelper.ui.dialog.LocationRemindDialog;
import com.yunbaba.freighthelper.ui.fragment.task.FreightPointListFragment;
import com.yunbaba.freighthelper.ui.fragment.task.FreightPointMapFragment;
import com.yunbaba.freighthelper.utils.ErrCodeUtil;
import com.yunbaba.freighthelper.utils.GPSUtils;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.NetWorkUtils;
import com.yunbaba.freighthelper.utils.SPHelper2;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.tool.CldKDeviceAPI;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.yokeyword.fragmentation.SupportFragment;

import static com.yunbaba.freighthelper.ui.activity.task.TaskPointDetailActivity.CODE_FINISHTRANS;

public class FreightPointActivity extends BaseMainActivity implements OnClickListener, OnBackToFirstListener, ICldLocationListener {

    private ImageView mBack, mMessage;
    private TextView mTitle;
    private TextView mMapModeName, mListModeName;
    private ImageView mMapLine, mListLine;
    private PercentRelativeLayout mPbWaiting;
    private PercentRelativeLayout mMapbtnLayout;
    private FrameLayout otherFrameLayout;
    private FrameLayout mapFrameLayout;
    private SupportFragment mFragment[] = new SupportFragment[2];
    private boolean isNeedGetdata; // 是否需要获取数据

    public static final int LISTMODE = 0;
    public static final int MAPMODE = 1;

    private boolean mSelectMap = false;
    private boolean mSelectList = false;
    private String mTaskId;
    MtqDeliStoreDetail mStoreDetail;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected int getLayoutResID() {

        return R.layout.activity_freight_point;
    }

    @Override
    protected void initViews() {

        mBack = (ImageView) findViewById(R.id.title_left_img);
        mMessage = (ImageView) findViewById(R.id.title_right_img);
        mTitle = (TextView) findViewById(R.id.title_text);
        mMapModeName = (TextView) findViewById(R.id.id_map_mode_text);
        mMapbtnLayout = (PercentRelativeLayout) findViewById(R.id.id_map_mode);
        mListModeName = (TextView) findViewById(R.id.id_list_mode_text);
        mMapLine = (ImageView) findViewById(R.id.id_map_mode_line);
        mListLine = (ImageView) findViewById(R.id.id_list_mode_line);
        mPbWaiting = (PercentRelativeLayout) findViewById(R.id.freight_point_pb_waiting);

        mBack.setVisibility(View.VISIBLE);
        mMessage.setVisibility(View.VISIBLE);
        mMessage.setImageResource(R.drawable.icon_search_hint);
        mTitle.setText("路由点列表");

        mSelectMap = false;
        mSelectList = true;
        setSelectStatus();
    }

    @Override
    protected void setListener() {

        mBack.setOnClickListener(this);
        mMessage.setOnClickListener(this);
        findViewById(R.id.id_map_mode).setOnClickListener(this);
        findViewById(R.id.id_list_mode).setOnClickListener(this);
    }

    @Override
    protected void initFragment(Bundle savedInstanceState) {

        // if (savedInstanceState == null){
        // mFragment[LISTMODE] = new FreightPointListFragment();
        // mFragment[MAPMODE] = new FreightPointMapFragment();
        // loadMultipleRootFragment(R.id.id_freightpoit_layout,
        // LISTMODE,mFragment[LISTMODE],mFragment[MAPMODE]);
        // }else{
        // mFragment[LISTMODE] = findFragment(FreightPointListFragment.class);
        // mFragment[MAPMODE] = findFragment(FreightPointMapFragment.class);
        // }
        otherFrameLayout = (FrameLayout) findViewById(R.id.otherFrameLayout);
        mapFrameLayout = (FrameLayout) findViewById(R.id.id_freightpoit_layout);
        getWindow().setFormat(PixelFormat.TRANSLUCENT); // 不要第一次切到地图，会整个屏幕黑一下
        mSelectMap = false;
        mFragmentManager = getSupportFragmentManager();
        // setFragment();
        loadFragment();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        FreightPointDeal.getInstace().uninit();
        EventBus.getDefault().unregister(this);
        WaitingProgressTool.closeshowProgress(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.title_left_img:
                // 返回
                // if (mSelectList){
                // //这里特殊处理
                // showHideFragment(mFragment[MAPMODE], mFragment[LISTMODE]);
                // mFinish = true;
                // }
                finish();
                break;

            case R.id.id_map_mode:
                // 地图模式
                mSelectMap = true;
                mSelectList = false;
                setSelectStatus();
                setFragment();

                AppStatApi.statOnEventWithLoginName(getApplicationContext(), AppStatApi.BD_STAT_MAP_MODE_CLICK);
                // showHideFragment(mFragment[MAPMODE], mFragment[LISTMODE]);
                break;

            case R.id.id_list_mode:
                // 列表模式
                mSelectMap = false;
                mSelectList = true;
                setSelectStatus();
                setFragment();
                // showHideFragment(mFragment[LISTMODE], mFragment[MAPMODE]);
                break;

            case R.id.title_right_img:

                AppStatApi.statOnEventWithLoginName(getApplicationContext(), AppStatApi.BD_STAT_ROUTING_POINT_SEARCH_CLICK);

                if (FreightPointDeal.getInstace().getmMtqDeliTaskDetail() == null) {
                    return;
                }

                Intent intent = new Intent(this, SearchFreightPointActivity.class);
                String str = GsonTool.getInstance().toJson(FreightPointDeal.getInstace().getmMtqDeliTaskDetail());
                intent.putExtra("taskdetail", str);
                startActivity(intent);


                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnMessage(FreightPointEvent event) {
        switch (event.msg.what) {
            case MessageId.MSGID_ROUTE_SUCESS:
                // 算路成功
                if (!this.getClass().getSimpleName().equals(event.ctxSimpleName)) {
                    // 如果不是当前的activity 请求的算路服务 不执行
                    return;
                }
                Toast.makeText(this, "算路成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, RoutePreviewActivity.class);
                startActivity(intent);
                break;

            case MessageId.MSGID_ROUTE_FAIL:
                // 算路失败
                if (!this.getClass().getSimpleName().equals(event.ctxSimpleName)) {
                    // 如果不是当前的activity 请求的算路服务 不执行
                    return;
                }
                Toast.makeText(this, (String) event.msg.obj, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnMessage(GetLocationAddrEvent event) {

//
//        if(SPHelper2.getInstance(mContext).readMarkStoreAddress(mStoreDetail.taskId+mStoreDetail.waybill)!=null) {
//
//
//            JumpToUploadPayment();
//            return;
//
//
//        }


        if (!GPSUtils.isOPen(this)) {

            LocationRemindDialog dialog = new LocationRemindDialog(this, "定位服务未开启",
                    "请在设置中开启 \"定位\" 服务", "设置", "知道了", new LocationRemindDialog.IClickListener() {
                @Override
                public void OnCancel() {
                    GPSUtils.openGPSSettings(FreightPointActivity.this);
                }

                @Override
                public void OnSure() {
                    //  onLocation();
                }
            });


            dialog.show();

            return;

        }


        if (event != null && event.storeDetail != null) {

            MtqDeliStoreDetail storeDetail = event.storeDetail;
            mStoreDetail = storeDetail;
            mOrderDetail = event.orderDetail;
            //  if (SPHelper2.getInstance(mContext).readMarkStoreAddress(storeDetail.waybill + storeDetail.storeid) == null) {
            ShowAskDialog(storeDetail);
            // }

        }
    }

    public MtqDeliOrderDetail mOrderDetail;

    TaskAskPopUpDialog mPopUpDialog;

    public void ShowAskDialog(final MtqDeliStoreDetail storeDetail) {


        showProgressBar();

        onLocation();

//        mPopUpDialog = new TaskAskPopUpDialog(this);
//
//        mPopUpDialog.show();
//
//        mPopUpDialog.setDialogType(10);
//
//        // final MtqDeliStoreDetail detail = mS
//
//        // 选择优先运货到 " + detail.storename + " ,是否继续?"
//
//        mPopUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        mPopUpDialog.tvLeft.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                mPopUpDialog.dismiss();
//
//
//            }
//        });
//        mPopUpDialog.tvRight.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//
//
//                mPopUpDialog.dismiss();
//
//                showProgressBar();
//
//                onLocation();
//                //   finish();
//
//            }
//        });

    }

    //定位完后，检查是否需要反地理，并检查网络是否上传
    private void GetPositionAndCheckUpload(final MtqDeliStoreDetail storeDetail, final AddressBean bean) {


        NetWorkUtils.isNetworkConnected(this, new NetWorkUtils.OnNetworkListener() {
            @Override
            public void isAvailable(boolean isAvailable) {

                //hideProgressBar();

                if (isAvailable) {
                    OfflineLocationBean beans = new OfflineLocationBean(storeDetail.taskId + storeDetail.waybill, storeDetail, bean, CldKDeviceAPI.getSvrTime());

                    //   OfflineLocationTool.GeoCodeAndUpload(FreightPointActivity.this, bean, storeDetail);

                    OfflineLocationTool.CheckAddrAndUpload(FreightPointActivity.this,beans, null, true);

//                    OfflineLocationBean beans = new OfflineLocationBean(storeDetail.taskId + storeDetail.waybill, storeDetail, bean);
//                    OrmLiteApi.getInstance().save(beans);

                    JumpToUploadPayment();
                } else {

                    // OfflineLocationTool.unregisterLocation();
                    //OfflineLocationTool.unregisterLocation(FreightPointActivity.this, this);
//        LatLng lt = new LatLng();
//        lt.longitude = location.getLongitude();
//        lt.latitude = location.getLatitude();
//
//        LatLng res = CldCoordUtil.gcjToCLDEx(lt);
//
//        AddressBean bean = new AddressBean();
//        bean.latitude = res.latitude;
//        bean.longitude = res.longitude;

                    OfflineLocationBean beans = new OfflineLocationBean(storeDetail.taskId + storeDetail.waybill, storeDetail, bean,CldKDeviceAPI.getSvrTime());
                    OrmLiteApi.getInstance().save(beans);

                    JumpToUploadPayment();
                }


            }
        });


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateResult(FreightPointUpdateEvent event) {
        if (event.mType == 0) {

            if (isRunning) {
                isNeedGetdata = true;
                updatedata();
            } else {
                isNeedGetdata = true;
            }

        }
    }

    @Override
    public void onBackToFirstFragment() {

    }

    @Override
    public void onBackPressedSupport() {

        super.onBackPressedSupport();

        // if (mSelectList){
        // showHideFragment(mFragment[MAPMODE], mFragment[LISTMODE]);
        // }
        finish();
    }

    @Override
    protected void onResume() {

        super.onResume();
        updatedata();
        // updateNews();
    }

    private void loadFragment() {
        otherFrameLayout.setVisibility(View.VISIBLE);
        mapFrameLayout.setVisibility(View.INVISIBLE);

        Fragment fragment = null;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction = mFragmentManager.beginTransaction();

        fragment = new FreightPointMapFragment();
        transaction.replace(R.id.id_freightpoit_layout, fragment, "map_fragment");

        fragment = new FreightPointListFragment();
        transaction.replace(R.id.otherFrameLayout, fragment, "list_fragment");

        transaction.commit();
    }

    private void setFragment() {
        Fragment fragment = null;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction = mFragmentManager.beginTransaction();
        if (mSelectMap) {
            mapFrameLayout.setVisibility(View.VISIBLE);
            otherFrameLayout.setVisibility(View.INVISIBLE);

            fragment = mFragmentManager.findFragmentByTag("map_fragment");
            if (fragment == null) {
                fragment = new FreightPointMapFragment();
                transaction.replace(R.id.id_freightpoit_layout, fragment, "map_fragment");
                transaction.commit();
            }

            if (FreightConstant.isSaveFlow) {
                EventBus.getDefault().post(new FreightPointEvent(MessageId.MSGID_MAP_RESUME));
            }
        } else {
            otherFrameLayout.setVisibility(View.VISIBLE);
            mapFrameLayout.setVisibility(View.INVISIBLE);
            fragment = mFragmentManager.findFragmentByTag("list_fragment");
            if (fragment == null) {
                fragment = new FreightPointListFragment();
                transaction.replace(R.id.otherFrameLayout, fragment, "list_fragment");
                transaction.commit();
            }

            if (FreightConstant.isSaveFlow) {
                EventBus.getDefault().post(new FreightPointEvent(MessageId.MSGID_MAP_PAUSE));
            }
        }
    }

    private void setSelectStatus() {
        mMapModeName.setSelected(mSelectMap);
        mListModeName.setSelected(mSelectList);
        mMapLine.setSelected(mSelectMap);
        mListLine.setSelected(mSelectList);
    }

    private void updatedata() {
        if (isNeedGetdata) {
            isNeedGetdata = false;
            String corpid = getIntent().getStringExtra("corpid");
            String taskid = getIntent().getStringExtra("taskid");
            mTaskId = taskid;

            FreightPointDeal.getInstace().init(this);
            FreightPointDeal.getInstace().mHandler = new Handler();


            showProgressBar();
            FreightPointDeal.getInstace().getTaskDetail(this, taskid, corpid, new OnUIResult() {

                @Override
                public void OnResult() {

                    if (isFinishing())
                        return;
                    EventBus.getDefault().post(new FreightPointEvent(MessageId.MSGID_FREIGHTPOINT_ADPTER));
                    FreightPointDeal.getInstace().setAdpate = true;
                    hideProgressBar();
                }

                @Override
                public void OnError(int ErrCode) {

                    if (isFinishing())
                        return;

                    if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                        // Toast.makeText(FreightPointActivity.this,
                        // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                    } else if (ErrCodeUtil.isNetErrCode(ErrCode)) {
                        Toast.makeText(FreightPointActivity.this, "网络通信出现问题，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                    EventBus.getDefault().post(new FreightPointEvent(MessageId.MSGID_FREIGHTPOINT_ADPTER_FAIL));
                    mMapbtnLayout.setEnabled(false);
                    hideProgressBar();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        MLog.e("check act ret", requestCode + " " + resultCode);

        if (requestCode == FreightConstant.TASK_POINT_REQUSEST_CODE
                && resultCode == FreightConstant.TASK_POINT_INFO_NEED_UPDATE) {
            // 从运单详情回来的有可能需要重新刷新。
            isNeedGetdata = true;
            updatedata();
        } else if (requestCode == FreightConstant.TASK_POINT_REQUSEST_CODE
                && resultCode == FreightConstant.TASK_AND_POINT_NEED_UPDATE) {
            // 单和点都需要刷新
            isNeedGetdata = true;
            updatedata();
        }
    }

    public void showPoi(int index) {
        mSelectMap = true;
        mSelectList = false;
        setSelectStatus();
        // showHideFragment(mFragment[MAPMODE], mFragment[LISTMODE]);

        setFragment();

        EventBus.getDefault().post(new FreightPointEvent(MessageId.MSGID_FREIGHTPOINT_SHOWPOI, index));
    }

    @Override
    protected void afterInit() {

        isNeedGetdata = true;
    }

    public void showProgressBar() {
        mPbWaiting.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        mPbWaiting.setVisibility(View.GONE);
    }

// private void updateNews() {
// if (MsgManager.getInstance().hasUnReadMsg()) {
// mMessage.setImageResource(R.drawable.msg_icon_news);
// } else {
// mMessage.setImageResource(R.drawable.msg_icon);
// }
// }
//
// @Subscribe(threadMode = ThreadMode.MAIN)
// public void onMessageEvent(NewMsgEvent event) {
// switch (event.msgId) {
// case MessageId.MSGID_MSG_NEW: {
// updateNews();
// break;
// }
// default:
// break;
// }
// }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x11) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MLog.i("CMCC", "权限被允许");
                FreightPointDeal.getInstace().intentToCall();
            } else {
                MLog.i("CMCC", "权限被拒绝");
            }
        }


        switch (requestCode) {
            case 223:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startLocation();
                } else {
                    // Permission Denied
                }
                break;
            //  default:
            //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public synchronized void onTaskBusinessMsgEvent(TaskBusinessMsgEvent event) {

        switch (event.getType()) {

            // 任务刷新
            case 2:
                if (mTaskId == null)
                    return;

                if (isFinishing())
                    return;

                if (isRunning) {

                    if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {
                        if (TextStringUtil.isContain(event.getRefreshtaskIdList(), mTaskId)) {
                            isNeedGetdata = true;
                            updatedata();
                        }
                    }

                } else {
                    if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {
                        if (TextStringUtil.isContain(event.getRefreshtaskIdList(), mTaskId)) {
                            isNeedGetdata = true;
                        }
                    }
                }

                break;
            // 作废任务，删除某些任务
            case 3:

                MLog.e("checkdeletetask", "删除任务" + mTaskId + "  " + event.getTaskIdList().get(0));

                if (mTaskId == null)
                    return;

                if (isFinishing())
                    return;

                if (event.getTaskIdList() != null && event.getTaskIdList().size() > 0) {

                    MLog.e("checkdeletetask", "删除任务" + mTaskId + "  " + event.getTaskIdList().get(0));

                    if (TextStringUtil.isContainStr(event.getTaskIdList(), mTaskId)) {

                        MLog.e("checkdeletetask", "删除任务");

                        if (isRunning) {
                            // Toast.makeText(this, "当前操作任务单已撤回",
                            // Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }

                }

                break;

            case 4:
                if (mTaskId == null)
                    return;

                if (isFinishing())
                    return;

                if (isRunning) {
                    if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {
                        if (TextStringUtil.isContain(event.getRefreshtaskIdList(), mTaskId)) {
                            isNeedGetdata = true;
                            updatedata();
                        }
                    }
                } else {
                    if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {
                        if (TextStringUtil.isContain(event.getRefreshtaskIdList(), mTaskId)) {
                            isNeedGetdata = true;
                        }
                    }
                }

                break;

            default:
                break;
        }


    }

    private AddressBean currentLocationBean;


    @SuppressLint("NewApi")
    private void onLocation() {
        /*
         * try { startLocation(); } catch (Exception e) {
		 *
		 * }
		 */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 在6.0 系统中请求某些权限需要检查权限
            if (!hasPermission()) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 223);
            } else {
                startLocation();
            }
        } else {
            startLocation();
        }
    }

    @SuppressLint("NewApi")
    private boolean hasPermission() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }


    private void startLocation() {
//        if (!CldPhoneNet.isNetConnected()) {
//            Toast.makeText(this, "无法获取你的位置信息。", Toast.LENGTH_SHORT).show();
//        } else {
        // String text = getResources().getString(R.string.area_location);
        // mCurArea.setText(text);
        // startLocatAnim();

        WaitingProgressTool.showProgress(this);
        location();

//            NetWorkUtils.isNetworkWeakOrNotConnect(this, new NetWorkUtils.OnNetworkListener() {
//                @Override
//                public void isAvailable(boolean isAvailable) {
//                    if(isAvailable){
//
//                        location();
//                    }else{
//                        OfflineLocationTool.registerLocation(FreightPointActivity.this, new LocationListener() {
//                            @Override
//                            public void onLocationChanged(Location location) {
//                                if(location!=null) {
//
//                                    LatLng tmp = new LatLng(location.getLatitude(), location.getLongitude());
//
//                                    tmp = CldCoordUtil.gcjToCLDEx(tmp);
//
//                                    CldLocation loc= new CldLocation(location);
//                                    loc.setLatitude(tmp.latitude);
//                                    loc.setLongitude(tmp.longitude);
//
//                                    onReceiveLocation(loc);
//                                }
//                            }
//
//                            @Override
//                            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                            }
//
//                            @Override
//                            public void onProviderEnabled(String provider) {
//
//                            }
//
//                            @Override
//                            public void onProviderDisabled(String provider) {
//
//                            }
//                        });
//                    }
//                }
//            });

        //  }
    }

    private void location() {
        MLog.e("test", " ++++ location =  start location ");

        LocationAPI.getInstance().location(LocationAPI.MTQLocationMode.MIXED, 3000, this).setLinster(this);
    }

    @Override
    public void onReceiveLocation(CldLocation location) {
        WaitingProgressTool.closeshowProgress();
        hideProgressBar();
        if (!isFinishing()) {
            MLog.e("yyh", "location = " + location.getErrCode() + " " + location.getLatitude() + location.getLongitude() + location.getProvince()
                    + location.getCity() + location.getDistrict() + location.getAddress() + location.getAdCode());

            LocationAPI.getInstance().stop();


            onLocateSuccess(location);

        }
    }


    //定位
    protected void onLocateSuccess(CldLocation location) {
        String address = "";
        if (location != null) {


            LatLng tmp = new LatLng(location.getLatitude(), location.getLongitude());

            currentLocationBean = new AddressBean();

            currentLocationBean.kcode = CldCoordUtil.cldToKCode(tmp);


            if (!TextUtils.isEmpty(location.getAddress()) && !TextUtils.isEmpty(location.getProvince()) && !TextUtils.isEmpty(location.getCity())) {
                address = location.getAddress().replace((location.getProvince().replaceAll("\\s*", "")), "")
                        .replace((location.getCity().replaceAll("\\s*", "")), "")
                        .replace((location.getDistrict().replaceAll("\\s*", "")), "");


                currentLocationBean.address = location.getProvince() + location.getCity() + location.getDistrict()
                        + address;
                currentLocationBean.uploadAddress = address;// location.getDistrict()
                // +
                // location.getAdCode();
                currentLocationBean.pcd = (location.getProvince() + location.getCity() + location.getDistrict())
                        .replaceAll("\\s*", "");


            } else {

                currentLocationBean.address = "";// (mStoreDetail.regionname + mStoreDetail.storeaddr).replaceAll("\\s*", "");
                //    currentLocationBean.uploadAddress = mStoreDetail.storeaddr.replaceAll("\\s*", "");
                currentLocationBean.uploadAddress = "";
                currentLocationBean.pcd = "";//mStoreDetail.regionname.replaceAll("\\s*", "");


            }


            currentLocationBean.latitude = location.getLatitude();
            currentLocationBean.longitude = location.getLongitude();


            SPHelper2.getInstance(this).writeMarkStoreAddress(mStoreDetail.taskId + mStoreDetail.waybill, currentLocationBean);

            EventBus.getDefault().post(new RefreshAdapterEvent(mStoreDetail.waybill, mStoreDetail.taskId));

            GetPositionAndCheckUpload(mStoreDetail, currentLocationBean);

        } else {
            //Toast.makeText(this, R.string.account_locate_failed, Toast.LENGTH_SHORT).show();
            // address = mUserInfo.getAddress();
            JumpToUploadPayment();
        }

    }

    public void JumpToUploadPayment() {

        try {
            Intent intent = new Intent(FreightPointActivity.this, UploadPaymentActivity.class);

            // 添加storedetail
            String str = GsonTool.getInstance().toJson(mStoreDetail);
            intent.putExtra("storedetail", str);

            // 添加taskid
            intent.putExtra("taskid", mStoreDetail.taskId);
            intent.putExtra("corpid", mStoreDetail.corpId);
            // intent.putExtra("taskStatus", taskStatus);

            if (mOrderDetail != null) {
                String str2 = GsonTool.getInstance().toJson(mOrderDetail);
                intent.putExtra("orderdetail", str2);
            }
            startActivityForResult(intent, CODE_FINISHTRANS);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        } catch (Exception e) {

        }

    }

}
