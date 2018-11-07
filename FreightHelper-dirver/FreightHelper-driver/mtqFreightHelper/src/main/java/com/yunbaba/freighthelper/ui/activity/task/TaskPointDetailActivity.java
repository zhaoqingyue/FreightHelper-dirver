package com.yunbaba.freighthelper.ui.activity.task;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.location.CldLocation;
import com.cld.location.ICldLocationListener;
import com.cld.mapapi.model.LatLng;
import com.cld.mapapi.util.DistanceUtil;
import com.cld.navisdk.routeplan.CldRoutePlaner;
import com.cld.nv.location.CldCoordUtil;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.api.map.LocationAPI;
import com.yunbaba.api.map.MapViewAPI;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.DeliveryRouteAPI;
import com.yunbaba.api.trunk.FreightPointDeal;
import com.yunbaba.api.trunk.OfflineLocationTool;
import com.yunbaba.api.trunk.OrmLiteApi;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.WaybillManager;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.api.trunk.bean.UpdateTaskPointStatusResult;
import com.yunbaba.api.trunk.bean.UpdateTaskStatusResult;
import com.yunbaba.api.trunk.bean.UploadGoodScanRecordResult;
import com.yunbaba.api.trunk.listner.OnBooleanListner;
import com.yunbaba.api.trunk.listner.OnObjectListner;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.AddressBean;
import com.yunbaba.freighthelper.bean.OfflineLocationBean;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointStatusRefreshEvent;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointUpdateEvent;
import com.yunbaba.freighthelper.bean.eventbus.RefreshAdapterEvent;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.bean.eventbus.UpdateTaskStatusEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.ui.activity.RoutePreviewActivity;
import com.yunbaba.freighthelper.ui.activity.mapselect.MapSelectPointActivity;
import com.yunbaba.freighthelper.ui.activity.task.feedback.FeedBackActivity;
import com.yunbaba.freighthelper.ui.activity.task.feedback.FeedBackDialog;
import com.yunbaba.freighthelper.ui.adapter.GoodListAdapter;
import com.yunbaba.freighthelper.ui.adapter.PicGridViewAdapter;
import com.yunbaba.freighthelper.ui.customview.NoScrollGridView;
import com.yunbaba.freighthelper.ui.customview.SimpleIndexSelectDialog;
import com.yunbaba.freighthelper.ui.customview.TaskAskPopUpDialog;
import com.yunbaba.freighthelper.ui.dialog.LocationRemindDialog;
import com.yunbaba.freighthelper.utils.CallUtil;
import com.yunbaba.freighthelper.utils.CommonTool;
import com.yunbaba.freighthelper.utils.DigitsUtil;
import com.yunbaba.freighthelper.utils.FileUtils;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.GPSUtils;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.ImageTools;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.NetWorkUtils;
import com.yunbaba.freighthelper.utils.PermissionUtil;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.SPHelper2;
import com.yunbaba.freighthelper.utils.TaskStatus;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.TimestampTool;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.module.delivery.CldDalKDelivery;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IGetFeedBackInfoListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBackInfo;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
import com.yunbaba.ols.module.delivery.bean.MtqGoodDetail;
import com.yunbaba.ols.module.delivery.tool.CldKDeviceAPI;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hmi.packages.HPDefine;
import hmi.packages.HPDefine.HPWPoint;

import static com.yunbaba.freighthelper.db.DbManager.mContext;
import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

/**
 * Created by zhonghm on 2017/4/9.
 */

public class TaskPointDetailActivity extends BaseButterKnifeActivity implements ICldLocationListener {

    @BindView(R.id.iv_titleleft)
    ImageView ivTitleleft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_getposition)
    TextView tvGetpostion;
    @BindView(R.id.tv_startnavi)
    TextView tvStartnavi;
    @BindView(R.id.iv_titleright)
    ImageView ivTitleright;
    @BindView(R.id.iv_type)
    ImageView ivType;
    @BindView(R.id.tv_pointname)
    TextView tvPointname;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_kcode)
    TextView tvKcode;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.iv_select)
    ImageView ivSelect;

    @BindView(R.id.iv_simplephone)
    ImageView ivsimplephone;
    @BindView(R.id.pll_navi)
    PercentLinearLayout pllNavi;
    @BindView(R.id.tv_ordernumber)
    TextView tvOrdernumber;
    @BindView(R.id.tv_sendtitle)
    TextView tvSendtitle;
    @BindView(R.id.tv_sendname)
    TextView tvSendname;
    @BindView(R.id.tv_sendphone)
    TextView tvSendphone;
    @BindView(R.id.tv_totitle)
    TextView tvTotitle;
    @BindView(R.id.tv_toname)
    TextView tvToname;
    @BindView(R.id.tv_tophone)
    TextView tvTophone;
    @BindView(R.id.tv_totime)
    TextView tvTotime;
    @BindView(R.id.tv_totime2)
    TextView tvTotime2;
    @BindView(R.id.tv_overtime)
    TextView tvOvertime;
    @BindView(R.id.tv_overtime2)
    TextView tvOvertime2;
    @BindView(R.id.tv_shouldpay)
    TextView tvShouldpay;
    @BindView(R.id.tv_refund)
    TextView tvRefund;
    @BindView(R.id.tv_refund_reason)
    TextView tvRefundReason;
    @BindView(R.id.tv_payway)
    TextView tvPayway;
    @BindView(R.id.tv_realpay)
    TextView tvRealpay;
    @BindView(R.id.pll_finish_fee_info)
    PercentLinearLayout pllFinishFeeInfo;
    @BindView(R.id.pll_simpleinfofoot)
    PercentLinearLayout pllSimpleinfofoot;
    @BindView(R.id.pll_simpleinfofoot2)
    PercentLinearLayout pllSimpleinfofoot2;


    @BindView(R.id.tv_checkscanrecord)
    TextView tvCheckscanrecord;
    @BindView(R.id.lv_goodinfo)
    NoScrollGridView lvGoodinfo;
    @BindView(R.id.gv_goodreceipt)
    NoScrollGridView gvGoodreceipt;
    @BindView(R.id.gv_goodpic)
    NoScrollGridView gvGoodpic;
    @BindView(R.id.pll_pic)
    PercentLinearLayout pllPic;
    @BindView(R.id.pll_remark)
    PercentLinearLayout pllRemark;

    @BindView(R.id.pll_cu_remark)
    PercentLinearLayout cupllRemark;
    @BindView(R.id.tv_starttrans)
    TextView tvStarttrans;
    @BindView(R.id.pll_starttransporting_btn)
    PercentLinearLayout pllStarttransportingBtn;
    @BindView(R.id.tv_stoptrans)
    TextView tvStoptrans;
    @BindView(R.id.tv_finishtrans)
    TextView tvFinishtrans;
    @BindView(R.id.pll_transporting_btn)
    PercentLinearLayout pllTransportingBtn;

    @BindView(R.id.tv_remark)
    TextView tvRemark;

    @BindView(R.id.tv_cu_remark)
    TextView tvCuRemark;

    @BindView(R.id.tv_goodinfo)
    TextView tvGoodinfo;
    @BindView(R.id.tv_feeinfo)
    TextView tvFeeinfo;

    @BindView(R.id.tv_simplename)
    TextView tvsimplename;
    @BindView(R.id.tv_simplename2)
    TextView tvsimplename2;
    @BindView(R.id.tv_simplenamehint)
    TextView tvsimplenamehint;
    @BindView(R.id.tv_simplenamehint2)
    TextView tvsimplenamehint2;
    @BindView(R.id.tv_simplephone)
    TextView tvsimplephone;
    @BindView(R.id.tv_simplephone2)
    TextView tvsimplephone2;
    @BindView(R.id.pll_orderinfo)
    PercentRelativeLayout pllorderinfo;

    @BindView(R.id.pll_goodinfo)
    PercentLinearLayout pllgoodinfo;

    @BindView(R.id.pll_feeinfo)
    PercentLinearLayout pllfeeinfo;

    @BindView(R.id.pll_scaninfo)
    PercentLinearLayout pllScaninfo;

    @BindView(R.id.sv_root)
    ScrollView svroot;

    @BindView(R.id.pll_simpleinfo)
    PercentLinearLayout pllsimpleinfo;
    @BindView(R.id.pll_simpleinfo2)
    PercentLinearLayout pllsimpleinfo2;
    @BindView(R.id.prl_taskpoint)
    PercentRelativeLayout prltaskpoint;

    @BindView(R.id.tv_recnumhint)
    TextView tvRecnumhint;

    @BindView(R.id.tv_picnumhint)
    TextView tvPicnumhint;

    @BindView(R.id.v_link)
    View vLink;

    @BindView(R.id.pb_waiting)
    PercentRelativeLayout pb_waiting;

    @BindView(R.id.pb_waiting2)
    PercentRelativeLayout pb_waiting2;

    String corpid;
    String taskid;

    MtqDeliStoreDetail mStoreDetail;
    MtqDeliOrderDetail mOrderDetail;
    GoodListAdapter goodAdapter;

    @BindView(R.id.tv_titleright)
    TextView tvTitleright;

    @BindView(R.id.tv_titleright2)
    TextView tvTitleright2;

    @BindView(R.id.tv_look_feedback)
    TextView tvLookFeedback;

    @BindView(R.id.tv_kcodehint)
    TextView tvKcodehint;

    @BindView(R.id.prl_look_feedback)
    PercentRelativeLayout prlLookFeedback;

    FeedBackDialog mFeedBackDialog;

    // 回单
    public static final int GOOD_RECEIPT = 1;
    // 货物照片
    public static final int GOOD_PIC = 2;

    // 拍照
    public static final int IMAGE_CAPTURE = 2;
    // 从相册选择
    public static final int IMAGE_SELECT = 5;

    public static final int CODE_FINISHTRANS = 11044;
    public static final int RequestCode_Address = 111;
    // 图片 九宫格适配器
    private PicGridViewAdapter receiptAdapter;
    private PicGridViewAdapter picAdapter;

    private boolean isUpdate = false;

    // 用于保存图片路径
    private List<String> receiptlistpath = new ArrayList<String>();

    // 用于保存图片路径
    private List<String> piclistpath = new ArrayList<String>();

    // 用于保存图片路径
    private List<String> uploadreceiptlistpath = new ArrayList<String>();

    // 用于保存图片路径
    private List<String> uploadpiclistpath = new ArrayList<String>();

    private boolean isUpdateStore = false;

    private boolean isGetLocationFinish = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_taskpoint_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        svroot.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        svroot.setFocusable(true);
        svroot.setFocusableInTouchMode(true);
        svroot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
        tvTitle.setText("详情");
        // ivTitleright.setImageResource(R.drawable.icon_message);
        ivTitleright.setVisibility(View.GONE);

        if (getIntent() == null || getIntent().getStringExtra("storedetail") == null) {

            finish();
        } else {

            try {
                String jsonstr = getIntent().getStringExtra("storedetail");

                mStoreDetail = GsonTool.getInstance().fromJson(jsonstr, MtqDeliStoreDetail.class);


                if (savedInstanceState != null && savedInstanceState.getString("rstoredetail") != null) {
                    mStoreDetail = GsonTool.getInstance().fromJson(savedInstanceState.getString("rstoredetail"), MtqDeliStoreDetail.class);

                }

                if (mStoreDetail == null)
                    finish();
            } catch (Exception e) {
                finish();
            }

            if (getIntent().hasExtra("corpid"))
                corpid = getIntent().getStringExtra("corpid");

            if (getIntent().hasExtra("taskid"))
                taskid = getIntent().getStringExtra("taskid");

            if (getIntent().hasExtra("orderdetail")) {

                try {
                    String jsonstr2 = getIntent().getStringExtra("orderdetail");

                    if (jsonstr2 != null && !TextUtils.isEmpty(jsonstr2))
                        mOrderDetail = GsonTool.getInstance().fromJson(jsonstr2, MtqDeliOrderDetail.class);
                } catch (Exception e) {
                    finish();
                }
                setData();

                if (getIntent().hasExtra("isNeedFresh")) {

                    boolean isNeedFresh = getIntent().getBooleanExtra("isNeedFresh", false);
                    if (isNeedFresh)
                        RefreshDetailData();

                }

            } else {
                // 提货点
                setSimpleData();
                if (getIntent().hasExtra("isNeedFresh")) {

                    boolean isNeedFresh = getIntent().getBooleanExtra("isNeedFresh", false);
                    if (isNeedFresh)
                        RefreshDetailData();

                }
            }

            // if(mStoreDetail.)

        }
        EventBus.getDefault().register(this);


    }

    // isSimple 表示是否是提货点信息，提货点信息很多模块都不显示
    private void setSimpleData() {


        // if (mStoreDetail.optype == 4 || mStoreDetail.optype == 5)
        // pllsampleinfo.setVisibility(View.GONE);
        // else
        // pllsampleinfo.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(mStoreDetail.linkman) && TextUtils.isEmpty(mStoreDetail.linkphone)) {
            pllsimpleinfo.setVisibility(View.GONE);
        } else {
            pllsimpleinfo.setVisibility(View.VISIBLE);
        }
        pllsimpleinfo2.setVisibility(View.GONE);
        pllSimpleinfofoot2.setVisibility(View.GONE);
        pllSimpleinfofoot.setVisibility(View.GONE);
        pllorderinfo.setVisibility(View.GONE);
        pllgoodinfo.setVisibility(View.GONE);
        pllfeeinfo.setVisibility(View.GONE);
        tvGoodinfo.setVisibility(View.GONE);
        tvFeeinfo.setVisibility(View.GONE);

        tvGetpostion.setVisibility(View.GONE);

//        if (mStoreDetail.optype == 4 && mStoreDetail.storestatus == 1) {
//
//            if (TaskUtils.isStorePosUnknown(mStoreDetail))
//                pllNavi.setVisibility(View.GONE);
//            else
//                pllNavi.setVisibility(View.VISIBLE);
//
//
//        } else {
//
//
//        }

        if (TaskUtils.isStorePosUnknown(mStoreDetail))
            pllNavi.setVisibility(View.GONE);
        else
            pllNavi.setVisibility(View.VISIBLE);

        pllFinishFeeInfo.setVisibility(View.GONE);
        pllPic.setVisibility(View.GONE);
        pllRemark.setVisibility(View.GONE);
        // pllCuRemark.setVisibility(View.GONE);
        // pllTransportingBtn.setVisibility(View.GONE);
        // pllStarttransportingBtn.setVisibility(View.GONE);

        MLog.e("storestatus", "" + mStoreDetail.storestatus);

        switch (mStoreDetail.storestatus) {

            case 0:
                if (mStoreDetail.optype == 4)
                    tvStatus.setText("等待回程");
                else
                    tvStatus.setText("待作业");

                if (mStoreDetail.optype == 4) {
                    tvStatus.setText("等待回程");
                    tvStoptrans.setText("暂停回程");
                    tvStarttrans.setText("开始回程");
                } else {
                    tvStatus.setText("待作业");
                    tvStoptrans.setText("暂停");
                    tvStarttrans.setText("开始");
                }

                pllTransportingBtn.setVisibility(View.GONE);
                pllStarttransportingBtn.setVisibility(View.VISIBLE);
                break;

            case 1:
                if (mStoreDetail.optype == 4)
                    tvStatus.setText("回程中");
                else
                    tvStatus.setText("作业中");

                if (mStoreDetail.optype == 4) {
                    tvStoptrans.setText("暂停回程");
                    tvFinishtrans.setText("结束回程");
                } else {
                    tvStoptrans.setText("暂停");
                    tvFinishtrans.setText("完成");
                }

                pllTransportingBtn.setVisibility(View.VISIBLE);
                pllStarttransportingBtn.setVisibility(View.GONE);
                break;
            case 2:
                tvStatus.setText("已完成");
                pllTransportingBtn.setVisibility(View.GONE);
                pllStarttransportingBtn.setVisibility(View.GONE);
                break;

            case 3:
                if (mStoreDetail.optype == 4)
                    tvStatus.setText("暂停回程");
                else
                    tvStatus.setText("暂停");

                if (mStoreDetail.optype == 4) {
                    tvStatus.setText("暂停回程");
                    tvStoptrans.setText("暂停回程");
                    tvStarttrans.setText("继续回程");
                } else {
                    tvStatus.setText("暂停");
                    tvStoptrans.setText("暂停");
                    tvStarttrans.setText("继续");
                }

                pllTransportingBtn.setVisibility(View.GONE);
                pllStarttransportingBtn.setVisibility(View.VISIBLE);
                break;
            default:
                if (isUpdateStore)
                    EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                finish();
                break;

        }

        // ivType.setImageResource(
        // mStoreDetail.optype == 3 ? R.drawable.icon_storetype_take :
        // R.drawable.icon_storetype_send);
        FreightLogicTool.setImgbyOptype(mStoreDetail.optype, ivType);


        // modify by zhaoqy 2018-04-27
//        CharSequence pointName = SetStrSafety((TextUtils.isEmpty(mStoreDetail.storecode) ? "" : (mStoreDetail.storecode + "-"))
//                + mStoreDetail.storename);

        tvPointname.setText(getPointName());

        if (mStoreDetail.isUnknownAddress) {
            AddressBean bean = SPHelper.getInstance(this).readLocalStoreAddress(mStoreDetail.waybill);

            if (bean != null) {
                tvAddress.setText(SetStrSafety((bean.address).replaceAll("\\s*", "")));
            } else
                tvAddress.setText(
                        SetStrSafety((mStoreDetail.regionname + mStoreDetail.storeaddr).replaceAll("\\s*", "")));
        } else
            tvAddress.setText(SetStrSafety((mStoreDetail.regionname + mStoreDetail.storeaddr).replaceAll("\\s*", "")));

        tvKcode.setText(SetStrSafety(FreightLogicTool
                .splitKcode(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey))));
        tvKcode.getPaint().setFlags(0);
        tvKcode.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        // mStoreDetail.stopx, mStoreDetail.stopy)));
        // MLog.e("checkxy", mStoreDetail.storex+" "+ mStoreDetail.storey);


        if (mStoreDetail.optype == 3) {
            tvsimplenamehint.setText("联系人:");
        } else if (mStoreDetail.optype == 1) {
            tvsimplenamehint.setText("联系人:");
        } else {
            tvsimplenamehint.setText("联系人:");
        }


        if (TextUtils.isEmpty(mStoreDetail.linkman)) {
            tvsimplenamehint.setVisibility(View.GONE);
            tvsimplename.setVisibility(View.GONE);
        } else {
            tvsimplename.setText(SetStrSafety(mStoreDetail.linkman));
            tvsimplename.setVisibility(View.VISIBLE);
            tvsimplenamehint.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(mStoreDetail.linkphone)) {


            tvsimplephone.setVisibility(View.GONE);
            ivsimplephone.setVisibility(View.GONE);
        } else {
            tvsimplephone.setText(SetStrSafety(mStoreDetail.linkphone));
            tvsimplephone.setVisibility(View.VISIBLE);
            ivsimplephone.setVisibility(View.VISIBLE);
        }


        setUnderLine();

        // 送货备注
        if (TextUtils.isEmpty(mStoreDetail.storemark))
            cupllRemark.setVisibility(View.GONE);
        else {

            cupllRemark.setVisibility(View.VISIBLE);
            tvCuRemark.setText(mStoreDetail.storemark);
        }

        MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);

        if (detail == null || detail.getStatus() == 2) {

            pllTransportingBtn.setVisibility(View.GONE);
            pllStarttransportingBtn.setVisibility(View.GONE);
        }

        setFeedBackVisible();
        setKcodeState();
    }

    /**
     * 设置详情页面数据
     */
    private void setData() {

        pllsimpleinfo.setVisibility(View.GONE);
        pllSimpleinfofoot.setVisibility(View.GONE);
        pllsimpleinfo2.setVisibility(View.VISIBLE);
        pllSimpleinfofoot2.setVisibility(View.VISIBLE);
        pllorderinfo.setVisibility(View.GONE);

        switch (mStoreDetail.storestatus) {

            case 0:
            case 3:

                if (mStoreDetail.storestatus == 0) {

                    if (mStoreDetail.optype == 4) {
                        tvStatus.setText("等待回程");
                        tvStoptrans.setText("暂停回程");
                        tvStarttrans.setText("开始回程");
                    } else {
                        tvStatus.setText("待作业");
                        tvStoptrans.setText("暂停");
                        tvStarttrans.setText("开始");
                    }
                } else {

                    if (mStoreDetail.optype == 4) {
                        tvStatus.setText("暂停回程");
                        tvStoptrans.setText("暂停回程");
                        tvStarttrans.setText("继续回程");
                    } else {
                        tvStatus.setText("暂停");
                        tvStoptrans.setText("暂停");
                        tvStarttrans.setText("继续");
                    }
                }

                // pllNavi.setVisibility(View.GONE);
                if (TaskUtils.isStorePosUnknown(mStoreDetail)) {

//                    tvStartnavi.setVisibility(View.GONE);
                    tvStartnavi.setVisibility(View.VISIBLE);
                } else {

	                tvStartnavi.setVisibility(View.VISIBLE);
                }
                pllFinishFeeInfo.setVisibility(View.GONE);
                pllPic.setVisibility(View.GONE);
                pllTransportingBtn.setVisibility(View.GONE);
                pllStarttransportingBtn.setVisibility(View.VISIBLE);

                tvCheckscanrecord.setVisibility(View.GONE);

                tvRealpay.setText("¥" + DigitsUtil.getPrettyNumber(mStoreDetail.real_amount));

                tvRefund.setText("¥" + DigitsUtil.getPrettyNumber((mStoreDetail.total_amount - mStoreDetail.real_amount)));

                tvRefundReason.setText(TextUtils.isEmpty(mStoreDetail.return_desc) ? "无" : mStoreDetail.return_desc);

                tvPayway.setText(SetStrSafety(mStoreDetail.pay_method));
                break;

            case 1:
                if (mStoreDetail.optype == 4)
                    tvStatus.setText("回程中");
                else
                    tvStatus.setText("作业中");
                if (TaskUtils.isStorePosUnknown(mStoreDetail))
                    tvStartnavi.setVisibility(View.VISIBLE);
                else
                    tvStartnavi.setVisibility(View.VISIBLE);
                pllFinishFeeInfo.setVisibility(View.GONE);
                pllPic.setVisibility(View.GONE);
                tvCheckscanrecord.setVisibility(View.GONE);

                pllTransportingBtn.setVisibility(View.VISIBLE);

                pllStarttransportingBtn.setVisibility(View.GONE);

                if (mStoreDetail.optype == 4) {
                    tvStoptrans.setText("暂停回程");
                    tvFinishtrans.setText("结束回程");
                } else {
                    tvStoptrans.setText("暂停");
                    tvFinishtrans.setText("完成");
                }
                break;
            case 2:

                tvStatus.setText("已完成");
                // pllNavi.setVisibility(View.GONE);

                if (TaskUtils.isStorePosUnknown(mStoreDetail))
                    tvStartnavi.setVisibility(View.VISIBLE);
                else
                    tvStartnavi.setVisibility(View.VISIBLE);


                tvGetpostion.setVisibility(View.GONE);

                pllFinishFeeInfo.setVisibility(View.VISIBLE);
                pllPic.setVisibility(View.VISIBLE);
                pllTransportingBtn.setVisibility(View.GONE);
                pllStarttransportingBtn.setVisibility(View.GONE);

                pllScaninfo.setVisibility(View.VISIBLE);
                tvCheckscanrecord.setVisibility(View.VISIBLE);

                tvRealpay.setText("¥" + DigitsUtil.getPrettyNumber(mStoreDetail.real_amount));

                tvRefund.setText("¥" + DigitsUtil.getPrettyNumber((mStoreDetail.total_amount - mStoreDetail.real_amount)));

                tvRefundReason.setText(TextUtils.isEmpty(mStoreDetail.return_desc) ? "无" : mStoreDetail.return_desc);

                tvPayway.setText(SetStrSafety(mStoreDetail.pay_method));
                break;

            default:
                finish();
                break;

        }

        // 公共部分

        // ivType.setImageResource(
        // mStoreDetail.optype == 3 ? R.drawable.icon_storetype_take :
        // R.drawable.icon_storetype_send);

        FreightLogicTool.setImgbyOptype(mStoreDetail.optype, ivType);


        if (mStoreDetail.optype == 3) {
            tvsimplenamehint.setText("联系人:");
        } else if (mStoreDetail.optype == 1) {
            tvsimplenamehint.setText("联系人:");
        } else {
            tvsimplenamehint.setText("联系人:");
        }


        tvsimplename2.setText(SetStrSafety(mStoreDetail.linkman));

        tvsimplephone2.setText(SetStrSafety(mStoreDetail.linkphone));

        // modify by zhaoqy 2018-04-27
//        CharSequence pointName = SetStrSafety((TextUtils.isEmpty(mStoreDetail.storecode) ? "" : (mStoreDetail.storecode + "-"))
//                + mStoreDetail.storename);
        tvPointname.setText(getPointName());

        if (mStoreDetail.isUnknownAddress) {

            AddressBean bean = SPHelper.getInstance(this).readLocalStoreAddress(mStoreDetail.waybill);

            if (bean != null) {
                tvAddress.setText(SetStrSafety((bean.address).replaceAll("\\s*", "")));
            } else
                tvAddress.setText(
                        SetStrSafety((mStoreDetail.regionname + mStoreDetail.storeaddr).replaceAll("\\s*", "")));

        } else
            tvAddress.setText(SetStrSafety((mStoreDetail.regionname + mStoreDetail.storeaddr).replaceAll("\\s*", "")));

        if (!isGetLocationFinish) {

            tvKcode.setText(SetStrSafety(FreightLogicTool
                    .splitKcode(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey)))); // mOrderDetail.send_kcode
            tvKcode.getPaint().setFlags(0);
            tvKcode.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);

        }
        // tvKcode.setText(mStoreDetail.optype == 3 ? mOrderDetail.send_kcode :
        // mOrderDetail.receive_kcode);

        // MLog.e("checkxy", mStoreDetail.storex+" "+ mStoreDetail.storey);

        tvCheckscanrecord.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        // 收货送货信息

        tvToname.setText(SetStrSafety(mOrderDetail.receive_name));

        // String receAddr= Html.fromHtml(mOrderDetail.receive_addr).toString();
        mOrderDetail.receive_addr = TextStringUtil.ReplaceHtmlTag(mOrderDetail.receive_addr);

        String toAddr = (mOrderDetail.receive_regionname + mOrderDetail.receive_addr).replaceAll("\\s*", "");

        if (!TextUtils.isEmpty(mOrderDetail.receive_organize)) {

            tvTotitle.setText(
                    (TextUtils.isEmpty(mOrderDetail.receive_storecode) ? "" : (mOrderDetail.receive_storecode + "-"))
                            + SetStrSafety(mOrderDetail.receive_organize));

        } else {

            tvTotitle.setText(
                    (TextUtils.isEmpty(mOrderDetail.receive_storecode) ? "" : (mOrderDetail.receive_storecode + "-"))
                            + SetStrSafety(toAddr));

        }

        // tvTotitle.setText(SetStrSafety(mOrderDetail.receive_addr));

        tvTophone.setText(SetStrSafety(mOrderDetail.receive_phone));

        tvTotime2.setText(TimestampTool.getStoreDetailTimeHint(mOrderDetail.reqtime_e * 1000L) + "前");

        tvSendname.setText(SetStrSafety(mOrderDetail.send_name));
        // "测试测试儿茶素厕所的撒旦d's");

        // SetStrSafety(mOrderDetail.send_name)); // //

        // String senderAddr= Html.fromHtml(mOrderDetail.send_addr).toString();
        mOrderDetail.send_addr = TextStringUtil.ReplaceHtmlTag(mOrderDetail.send_addr);

        String sendAddr = (mOrderDetail.send_regionname + mOrderDetail.send_addr).replaceAll("\\s*", "");

        if (!TextUtils.isEmpty(mOrderDetail.send_organize)) {

            tvSendtitle
                    .setText((TextUtils.isEmpty(mOrderDetail.send_storecode) ? "" : (mOrderDetail.send_storecode + "-"))
                            + SetStrSafety(mOrderDetail.send_organize));

        } else {

            tvSendtitle
                    .setText((TextUtils.isEmpty(mOrderDetail.send_storecode) ? "" : (mOrderDetail.send_storecode + "-"))
                            + SetStrSafety(sendAddr));

        }

        // tvSendtitle.setText(SetStrSafety(mOrderDetail.send_addr));

        tvSendphone.setText(SetStrSafety(mOrderDetail.send_phone));

        MLog.e("check", "" + mOrderDetail.expired);

        if (mOrderDetail.expired > 0) {

            // 已过期
            tvOvertime2.setVisibility(View.VISIBLE);
            tvOvertime2.setText("已过期");
            pllTransportingBtn.setVisibility(View.GONE);
            pllStarttransportingBtn.setVisibility(View.GONE);
        } else {

            // 判断是否超时
            if (isOverTime(mStoreDetail, mOrderDetail)) {
                tvOvertime2.setVisibility(View.VISIBLE);
                tvOvertime2.setText("已超时");
            } else
                tvOvertime2.setVisibility(View.GONE);
        }

        //"¥" +
        tvShouldpay.setText("¥" + DigitsUtil.getPrettyNumber(mStoreDetail.total_amount));

//        ArrayList<GoodInfo> gl = new ArrayList<>();
//        gl.add(new GoodInfo());

        goodAdapter = new GoodListAdapter(this, mOrderDetail);

        lvGoodinfo.setAdapter(goodAdapter);

        goodAdapter.notifyDataSetChanged();

//		tvOrdernumber.setText(
//
//				SetStrSafety(mStoreDetail.cust_orderid));

        MLog.e("remark", mStoreDetail.pay_remark + " / " + mStoreDetail.storemark);

        // 收款备注
        if (TextUtils.isEmpty(mStoreDetail.pay_remark))
            pllRemark.setVisibility(View.GONE);
        else {

            pllRemark.setVisibility(View.VISIBLE);
            tvRemark.setText(mStoreDetail.pay_remark);
        }

        // 送货备注
        if (TextUtils.isEmpty(mStoreDetail.storemark))
            cupllRemark.setVisibility(View.GONE);
        else {

            cupllRemark.setVisibility(View.VISIBLE);
            tvCuRemark.setText(mStoreDetail.storemark);
        }

        setUnderLine();

        receiptlistpath.clear();
        piclistpath.clear();

        receiptlistpath = TextStringUtil.splitPhoneString(mOrderDetail.receipt_ids);
        piclistpath = TextStringUtil.splitPhoneString(mOrderDetail.photo_ids);

        MLog.e("check",
                receiptlistpath.toString() + " " + mOrderDetail.receipt_ids + " " + mOrderDetail.can_receipt_nums);

        receiptAdapter = new PicGridViewAdapter(this, receiptlistpath, mOrderDetail.can_receipt_nums, 1, corpid,
                taskid);
        picAdapter = new PicGridViewAdapter(this, piclistpath, mOrderDetail.can_photo_nums, 1, corpid, taskid);

        // receiptAdapter = new PicGridViewAdapter(this, receiptlistpath, 3, 1,
        // corpid, taskid);// mOrderDetail.photo_nums);
        // picAdapter = new PicGridViewAdapter(this, piclistpath, 7, 1, corpid,
        // taskid);

        gvGoodpic.setAdapter(picAdapter);
        gvGoodreceipt.setAdapter(receiptAdapter);

        receiptAdapter.notifyDataSetChanged();
        picAdapter.notifyDataSetChanged();

        gvGoodpic.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == piclistpath.size())
                    selectPic(2);
                else {

                    if (piclistpath.get(position).contains(".jpg"))
                        modifyPic(2, position);
                    else
                        imageBrower(position, (ArrayList<String>) piclistpath);
                }

            }
        });

        gvGoodreceipt.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == receiptlistpath.size())
                    selectPic(1);
                else {

                    if (receiptlistpath.get(position).contains(".jpg"))
                        modifyPic(1, position);
                    else
                        imageBrower(position, (ArrayList<String>) receiptlistpath);

                }

            }
        });

        RefreshHint();

//		// 动态计算连接图片的高度
//
//		ViewTreeObserver viewTreeObserver = pllorderinfo.getViewTreeObserver();
//
//		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
//			@Override
//			public boolean onPreDraw() {
//				int height = pllorderinfo.getMeasuredHeight();
//				if (height != 0) {
//
//					WindowManager wm = (WindowManager) TaskPointDetailActivity.this
//							.getSystemService(Context.WINDOW_SERVICE);
//					DisplayMetrics outMetrics = new DisplayMetrics();
//					wm.getDefaultDisplay().getMetrics(outMetrics);
//					int mWidthScreen = outMetrics.widthPixels;
//					int mHeightScreen = outMetrics.heightPixels;
//
//					RelativeLayout.LayoutParams Scrollparams = new RelativeLayout.LayoutParams(
//							RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//					Scrollparams.width = (int) (mWidthScreen * 0.025);
//					Scrollparams.height = (int) (height - mWidthScreen * 0.35);
//
//					vLink.setLayoutParams(Scrollparams);
//
//					pllorderinfo.getViewTreeObserver().removeOnPreDrawListener(this);
//				}
//				return false;
//			}
//		});

        MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);

        if (detail == null || detail.getStatus() == 2) {

            pllTransportingBtn.setVisibility(View.GONE);
            pllStarttransportingBtn.setVisibility(View.GONE);
        }

        setFeedBackVisible();
        setKcodeState();

        AddressBean bean = SPHelper2.getInstance(mContext).readMarkStoreAddress(mStoreDetail.taskId + mStoreDetail.waybill);

        if (bean != null) {


            setGetPostionBtnState(true);


            //   tvKcode.setText(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey));


            String kcode = "";
            if (TaskUtils.isStorePosUnknown(mStoreDetail)) {

                kcode = "无位置";
                tvKcode.setTextColor(getResources().getColor(R.color.red));
                tvKcodehint.setTextColor(getResources().getColor(R.color.red));
            } else {
                kcode = SetStrSafety(FreightLogicTool
                        .splitKcode(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey)));
                tvKcode.setTextColor(getResources().getColor(R.color.text_normal_color));
                tvKcodehint.setTextColor(getResources().getColor(R.color.text_normal_color));
            }

//            String kcode = SetStrSafety(FreightLogicTool
//                    .splitKcode(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey)));


//: "+SetStrSafety(FreightLogicTool.splitKcode(bean.kcode))+"

            tvKcode.setText(FreightLogicTool.getKcodeGetPostionFail(kcode, " (已获取位置)"));
            tvKcode.getPaint().setFlags(0);
            tvKcode.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);


//            tvKcode.setText(SetStrSafety(FreightLogicTool.splitKcode(bean.kcode)));
//
//
//            HPWPoint point =  CldCoordUtil.kcodeToCLD(bean.kcode);
//
//
//            mStoreDetail.storex = point.x;
//            mStoreDetail.storey = point.y;
//
//            tvKcodehint.setTextColor(getResources().getColor(R.color.text_normal_color));
//            tvKcode.setTextColor(getResources().getColor(R.color.text_normal_color));
//
//            tvKcode.getPaint().setFlags(0);
//            tvKcode.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);

        } else {

            setGetPostionBtnState(false);
        }

        if (bean == null && TaskUtils.isStorePosUnknown(mStoreDetail) && mStoreDetail.storestatus!=2) {

            tvGetpostion.setVisibility(View.VISIBLE);

        }

        if(mStoreDetail.storestatus==2){
            tvGetpostion.setVisibility(View.GONE);
        }


    }

    public void RefreshHint() {

        if (mOrderDetail == null)
            return;

        if (receiptlistpath.size() > 0)
            tvRecnumhint.setVisibility(View.GONE);
        else {
            tvRecnumhint.setVisibility(View.VISIBLE);
            tvRecnumhint.setText("请拍摄电子回单，最多" + mOrderDetail.can_receipt_nums + "张");

        }

        if (piclistpath.size() > 0)
            tvPicnumhint.setVisibility(View.GONE);
        else {
            tvPicnumhint.setVisibility(View.VISIBLE);
            tvPicnumhint.setText("货物照片，最多" + mOrderDetail.can_photo_nums + "张");

        }

        if ((uploadpiclistpath != null && uploadpiclistpath.size() > 0)
                || (uploadreceiptlistpath != null && uploadreceiptlistpath.size() > 0)) {

            tvTitleright2.setTextColor(this.getResources().getColor(R.color.app_color2));
        } else
            tvTitleright2.setTextColor(this.getResources().getColor(R.color.scan_text_gray));

    }

    public void setUnderLine() {

        tvsimplephone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvsimplephone2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvTophone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvSendphone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        // tvLookFeedback.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

    }

    public void setKcodeState() {

        if (TaskUtils.isStorePosUnknown(mStoreDetail)) {

            tvKcodehint.setTextColor(getResources().getColor(R.color.red));

//            if (mStoreDetail.storestatus == 1) {
//
//                tvKcode.setTextColor(getResources().getColor(R.color.app_color));
//                tvKcode.setText("地图选点");
//
//                tvKcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//            } else {

            tvKcode.setTextColor(getResources().getColor(R.color.red));
            tvKcode.setText("无位置");
            tvKcode.getPaint().setFlags(0);
            tvKcode.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            //    }

        } else {

            if (!isGetLocationFinish) {

                tvKcodehint.setTextColor(getResources().getColor(R.color.text_normal_color));
                tvKcode.setTextColor(getResources().getColor(R.color.text_normal_color));
                tvKcode.getPaint().setFlags(0);
                tvKcode.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            }
        }

    }

    // public final int requestCode = 1001;

    /**
     * 选择图片 1 回单 2 货物
     */
    public void selectPic(final int type) {

        if (PermissionUtil.isNeedPermissionForStorage(this)) {

            Toast.makeText(this, "请打开储存空间权限", Toast.LENGTH_SHORT).show();
            return;

        }

        SimpleIndexSelectDialog.ShowSelectDialog(TaskPointDetailActivity.this, new String[]{"拍照", "图库"}, true,
                new SimpleIndexSelectDialog.OnSimpleIndexSelectCallBack() {

                    @Override
                    public void OnIndexSelect(int index, String select) {

                        switch (index) {
                            case 0:// 拍照

                                if (PermissionUtil.isNeedPermission(TaskPointDetailActivity.this,
                                        Manifest.permission.CAMERA)) {

                                    Toast.makeText(TaskPointDetailActivity.this, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
                                    return;

                                }
                                captureImage(MainApplication.getTmpCacheFilePath(), type, -1);

                                break;
                            case 1:// 从图库选择
                                selectImage(type, -1);

                                break;

                            default:
                                break;
                        }
                    }
                });

    }

    @OnClick({R.id.iv_titleleft, R.id.iv_titleright, R.id.tv_startnavi, R.id.tv_sendphone, R.id.tv_tophone, R.id.tv_getposition,
            R.id.tv_checkscanrecord, R.id.tv_starttrans, R.id.tv_stoptrans, R.id.tv_finishtrans, R.id.tv_simplephone, R.id.tv_simplephone2,
            R.id.prl_taskpoint, R.id.tv_titleright, R.id.prl_look_feedback, R.id.tv_titleright2, R.id.tv_kcode, R.id.pb_waiting})
    public void onClick(View view) {

        if (CommonTool.isFastDoubleClick()) {

            return;
        }


        switch (view.getId()) {
            case R.id.iv_titleleft:
                if (isUpdateStore)
                    EventBus.getDefault().post(new FreightPointUpdateEvent(0));

                // WaitingProgressTool.showProgress(this);
                // WaitingUpdateTaskDialog.getInstance().showView(this);
                // finish();

                if (isModifyInfo()) {

                    ShowAskDialog();

                } else
                    finish();
                break;
            case R.id.tv_titleright:

                AppStatApi.statOnEventWithLoginName(getApplicationContext(), AppStatApi.BD_STAT_FEEDBACK_CLICK);
                // Intent intent3 = new Intent(this, MsgActivity.class);
                // startActivity(intent3);

                ShowFeedBackDialog(false, false);

                break;

            case R.id.tv_kcode:

                // Intent intent3 = new Intent(this, MsgActivity.class);
                // startActivity(intent3);

//                if (tvKcode.getText().toString().equals("地图选点")) {
//
//                    if (!PermissionUtil.isNeedPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, 108)) {
//                        Intent it = new Intent(this, MapSelectPointActivity.class);
//                        startActivityForResult(it, RequestCode_Address);
//                    }
//
//                }

                break;
            case R.id.tv_titleright2:
                if (!CommonTool.isFastDoubleClick()) {
                    if ((uploadpiclistpath != null && uploadpiclistpath.size() > 0)
                            || (uploadreceiptlistpath != null && uploadreceiptlistpath.size() > 0)) {

                        UploadPic();
                    }
                }
                break;
            case R.id.prl_look_feedback:
                if (mOrderDetail != null) {
                    Intent intent3 = new Intent(this, FeedBackActivity.class);

                    intent3.putExtra("taskid", mStoreDetail.taskId);
                    intent3.putExtra("corpid", mStoreDetail.corpId);
                    // if (mOrderDetail != null)
                    intent3.putExtra("orderid", mStoreDetail.waybill);
                    startActivity(intent3);

                }

                break;
            case R.id.tv_startnavi:



                if(mStoreDetail.storex== 0 && mStoreDetail.storey == 0){


                    Toast.makeText(TaskPointDetailActivity.this, "缺少位置信息，使用导航失败", Toast.LENGTH_LONG).show();
                    return;
                }

                AppStatApi.statOnEventWithLoginName(getApplicationContext(), AppStatApi.BD_STAT_NAVI_CLICK);


//                showProgressBar();
//                showProgressBar();
//                showProgressBar();
//                if(true)
//                return;



                if (TaskUtils.isStorePosUnknown(mStoreDetail)) {

//                    TaskOperator.getInstance().ShowNaviDisableDialog(this, new OnCallBack() {
//
//                        @Override
//                        public void onYES() {
//
//
//                        }
//
//                    });
	                Toast.makeText(TaskPointDetailActivity.this, "缺少位置信息，使用导航失败", Toast.LENGTH_LONG).show();

	                return;

                }


	            if (!CldPhoneNet.isNetConnected()) {
	                Toast.makeText(TaskPointDetailActivity.this, "网络处于断开状态，无法在线导航", Toast.LENGTH_LONG).show();
                    return;
                }


                if (TextUtils.isEmpty(CldDalKDelivery.getInstance().getCldDeliveryKey())) {


                    Toast.makeText(TaskPointDetailActivity.this, "帐号处于离线状态，无法在线导航", Toast.LENGTH_LONG).show();
                    return;
                }


                DeliveryRouteAPI.routeplan(mStoreDetail, this, new CldRoutePlaner.RoutePlanListener() {

                    @Override
                    public void onRoutePlanCanceled() {


                    }

                    @Override
                    public void onRoutePlanFaied(int arg0, String arg1) {

                        final String errorHint = arg1;
                        TaskPointDetailActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Toast.makeText(TaskPointDetailActivity.this, errorHint, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onRoutePlanSuccessed() {

                        TaskPointDetailActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Intent intent = new Intent(TaskPointDetailActivity.this, RoutePreviewActivity.class);

                                String str = GsonTool.getInstance().toJson(mStoreDetail);
                                intent.putExtra("storedetail", str);

                                // 添加taskid
                                intent.putExtra("taskid", mStoreDetail.taskId);
                                intent.putExtra("corpid", mStoreDetail.corpId);

                                if (mOrderDetail != null) {
                                    String str2 = GsonTool.getInstance().toJson(mOrderDetail);
                                    intent.putExtra("orderdetail", str2);
                                }

                                str = GsonTool.getInstance().toJson(FreightPointDeal.getInstace().getmMtqDeliTaskDetail());
                                intent.putExtra("taskdetail", str);

                                startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);
                            }
                        });
                    }

                });

                break;
            case R.id.tv_sendphone:
                if (tvSendphone.getText() != null)
                    call(tvSendphone.getText().toString());
                break;
            case R.id.pb_waiting:


                break;
            case R.id.tv_getposition:

                AppStatApi.statOnEventWithLoginName(getApplicationContext(), AppStatApi.BD_STAT_GET_LOCATION_POINT_CLICK);

                GetLocation();


                break;
            case R.id.tv_tophone:
                if (tvTophone.getText() != null)
                    call(tvTophone.getText().toString());
                break;
            case R.id.tv_simplephone:
                if (tvsimplephone.getText() != null)
                    call(tvsimplephone.getText().toString());
                break;
            case R.id.tv_simplephone2:
                if (tvsimplephone2.getText() != null)
                    call(tvsimplephone2.getText().toString());
                break;
            case R.id.tv_checkscanrecord:
                createScanRecord();

                Intent intent = new Intent(this, WaybillRecordActivity.class);
                intent.putExtra("taskId", mStoreDetail.taskId);
                intent.putExtra("cust_orderid", mStoreDetail.waybill);

                startActivity(intent);

                break;
            case R.id.tv_starttrans:
                AppStatApi.statOnEventWithLoginName(getApplicationContext(), AppStatApi.BD_STAT_ROUTING_POINT_START_CLICK);

                // StartTrans();

                // 如果是开始运货要询问是否要导航

                // showNavigationDialog(mStoreDetail);

                IsNeedShowTaskStatusChangeDialog(1);

                break;
            case R.id.tv_stoptrans:
                AppStatApi.statOnEventWithLoginName(getApplicationContext(), AppStatApi.BD_STAT_ROUTING_POINT_PAUSE_CLICK);
                // StopTrans();
                // IsNeedShowTaskStatusChangeDialog(2);
                Operate(2);

                break;
            case R.id.tv_finishtrans:

                // FinishTrans();
                // IsNeedShowTaskStatusChangeDialog(3);
                Operate(3);
                break;

            case R.id.prl_taskpoint:

                // Intent intent2 = new Intent(this, FreightPointActivity.class);
                // intent2.putExtra("corpid", corpid);
                // intent2.putExtra("taskid", taskid);
                // startActivity(intent2);

                break;
        }

    }

    /**
     * @Title: createScanRecord
     * @Description: 生成扫描记录，避免跳过去一片空白
     * @return: void
     */
    private void createScanRecord() {

        Iterator<MtqGoodDetail> iter = mOrderDetail.goods.iterator();
        while (iter.hasNext()) {
            final MtqGoodDetail gooddetail = iter.next();
            final String key = mStoreDetail.taskId + mStoreDetail.waybill + gooddetail.bar_code;
            WaybillManager.getInstance().getOneScanRecordByKeyFromDB(key, new OnObjectListner<UploadGoodScanRecordResult>() {
                @Override
                public void onResult(UploadGoodScanRecordResult res) {
                    UploadGoodScanRecordResult result = res;
                    if (result == null) {
                        result = new UploadGoodScanRecordResult();
                        result.taskAndbarCode = key;
                        result.address = (mStoreDetail.regionname + mStoreDetail.storeaddr).replaceAll("\\s*", "");
                        result.amount = gooddetail.amount;
                        result.name = gooddetail.name;
                        result.bar_code = gooddetail.bar_code;
                        result.scan_cnt = gooddetail.scan_cnt;
                        result.searchKey = mStoreDetail.taskId + mStoreDetail.waybill;
                        result.cust_order_id = mStoreDetail.waybill;
                        WaybillManager.getInstance().saveScanRecordToBD(result);
                    }
                }
            });

        }
    }

    /**
     * 开始运货
     */
    public void StartTrans() {

        boolean isnavi = GeneralSPHelper.getInstance(TaskPointDetailActivity.this).isTaskNavi(taskid);

        if (isnavi) {
            isNeedNavi = true;
            // HandleStatusChangeEvent(event, operatetype);

        }

        updateStoreStatus(1, false);

        // else {
        // new Handler().postDelayed(new Runnable() {
        //
        // @Override
        // public void run() {
        //
        // showNavigationDialog(mStoreDetail);
        // }
        // }, 500);
        // }

    }

    /**
     * 停止运货
     */
    public void StopTrans() {

        MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);
        MLog.e("stoptrans", GsonTool.getInstance().toJson(detail.store));

        boolean isLast = true;

        over:
        for (MtqDeliStoreDetail store : detail.store) {

            if (!store.waybill.equals(mStoreDetail.waybill) && store.storestatus != 2) {

                isLast = false;
                break over;

            }

        }

        showStopAskDialog(isLast);

    }

    public String getFinishCountHintStr() {

        MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);

        int finishcount = 0;
        int storecount = detail.getTotal();

        for (MtqDeliStoreDetail store : detail.store) {

            if (store.storestatus == 2) {

                finishcount += 1;
            }

        }

        return finishcount + "/" + storecount;

    }

    /**
     * 完成运货
     */
    public void FinishTrans() {

        if (mOrderDetail == null) {


            MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);

            boolean isLast = true;

            over:
            for (MtqDeliStoreDetail store : detail.store) {

                if (!store.waybill.equals(mStoreDetail.waybill) && store.storestatus != 2) {

                    isLast = false;
                    break over;

                }

            }
            updateStoreStatus(2, isLast);
        } else {


            //   if (SPHelper2.getInstance(mContext).readMarkStoreAddress(mStoreDetail.taskId + mStoreDetail.waybill) == null) {


            //  GetLocation();
//            }else{
            JumpToUploadPayment();
//            }

            // Toast.makeText(TaskPointDetailActivity.this,
            // R.string.request_success, Toast.LENGTH_SHORT)
            // .show();
        }
        //

    }

    public void JumpToUploadPayment() {

        Intent intent = new Intent(TaskPointDetailActivity.this, UploadPaymentActivity.class);

        // 添加storedetail
        String str = GsonTool.getInstance().toJson(mStoreDetail);
        intent.putExtra("storedetail", str);

        // 添加taskid
        intent.putExtra("taskid", taskid);
        intent.putExtra("corpid", corpid);
        // intent.putExtra("taskStatus", taskStatus);

        String str2 = GsonTool.getInstance().toJson(mOrderDetail);
        intent.putExtra("orderdetail", str2);

        startActivityForResult(intent, CODE_FINISHTRANS);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

    }


    // 操作
    public void Operate(int type) {

        switch (type) {
            case 0:

                break;
            case 1:
                StartTrans();
                break;
            case 2:
                StopTrans();
                break;
            case 3:
                FinishTrans();
                break;

            default:
                break;
        }

    }

    public void IsNeedShowTaskStatusChangeDialog(final int operatetype) {

        if (TaskOperator.getInstance().getmCurrentTask() != null
                && taskid.equals(TaskOperator.getInstance().getmCurrentTask().taskid)
                || TaskOperator.getInstance().getmCurrentTask() == null) {

            ShowChangeAskDialog(2, null);
            // Operate(operatetype);

            return;
        }

        // MLog.e("check", "open update task");

        UpdateTaskStatusEvent event = new UpdateTaskStatusEvent(TaskOperator.getInstance().getmCurrentTask().corpid,
                TaskOperator.getInstance().getmCurrentTask().taskid, 3, "", "", 0, 0, 0, 0);

        TaskOperator.getInstance().showTaskStatusChangeAskDialog(this, event, new TaskOperator.OnDialogListener() {

            @Override
            public void OnDialogDismiss() {

                // do not thing
            }

            @Override
            public void OnClickRight(final UpdateTaskStatusEvent event) {


                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        ShowChangeAskDialog(1, event);
                    }
                }, 500);

            }

            @Override
            public void OnClickLeft(UpdateTaskStatusEvent event) {


                // do not thing

            }
        });

    }

    /**
     * @param
     * @param status
     * @Title: updateStoreStatus
     * @Description: 更新运货点的状态
     * @return: void
     */
    private void updateStoreStatus(final int status, final boolean isLast) {

        WaitingProgressTool.showProgress(this);

        String mewaybill = "";//mStoreDetail.waybill;

        if (status == 1) {

            MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);
            mewaybill = "";
            over:
            for (MtqDeliStoreDetail store : detail.getStore()) {
                if (!store.waybill.equals(mStoreDetail.waybill) && store.storestatus == 1) {
                    mewaybill = store.waybill;
                    break over;
                }


            }

        } else if (status == 2) {

            mewaybill = "";

        } else{
            mewaybill = "";
        }

        int tmptaskstatus = getTaskStatus(status); //

        // MLog.e("taskpointdetail", "taskStatus" + taskStatus);

        if (status == 1) {
            if (TaskOperator.getInstance().getmCurrentTask() != null
                    && TaskOperator.getInstance().getmCurrentTask().taskid.equals(taskid)) {
                tmptaskstatus = -1;

            } else {

                tmptaskstatus = 1;
            }
        }
        final int taskStatus = tmptaskstatus;

        final String fewaybill = mewaybill;

        OnResponseResult<UpdateTaskPointStatusResult> mrespone = new OnResponseResult<UpdateTaskPointStatusResult>() {

            @Override
            public void OnResult(UpdateTaskPointStatusResult result) {


                if (isFinishing())
                    return;


                result.setTaskid(taskid);
                result.setStoreid(mStoreDetail.storeid);
                result.setStatus(status);
                result.setWaybill(mStoreDetail.waybill);
                result.setEwaybill(fewaybill);

                WaitingProgressTool.closeshowProgress();

                MLog.e("checkUpdatePointStatus", "" + result.getErrCode());
                if (result.getErrCode() != 0) {

                    Toast.makeText(TaskPointDetailActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT).show();

                    if(TaskUtils.isNeedUpdateInfoCode(result.getErrCode())){
                        RefreshDetailData();
                        EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                    }


                } else {



                    mStoreDetail.storestatus = status;


                    EventBus.getDefault().post(new FreightPointUpdateEvent(0));


                    if (taskStatus == 1) {
                        TaskOperator.getInstance().ChangeCurrentTask(result.getCorpid(), result.getTaskid(), 1);
                        EventBus.getDefault().post(new FreightPointStatusRefreshEvent());
                    }


                    TaskOperator.getInstance().UpdateTaskStateByStoreStatusChangeResult(result);



                    // 任务已完成，从列表中删除
                    if (taskStatus == 2) {
                        TaskOperator.getInstance().RemoveTask(taskid, taskStatus);
                        EventBus.getDefault().post(new FreightPointStatusRefreshEvent());
                    }

                    if (!isLast && status == 3) {

                        // TaskOperator.getInstance().PauseTask(corpid, taskid,
                        // 3);
//                        Intent intent = new Intent(TaskPointDetailActivity.this, SelectTransPointActivity.class);
//                        intent.putExtra("corpid", result.getCorpid());
//                        intent.putExtra("taskid", result.getTaskid());
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

                        // Toast.makeText(TaskPointDetailActivity.this,
                        // R.string.request_success, Toast.LENGTH_SHORT)
                        // .show();

                        EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                        finish();
                    } else if (isLast && status == 3) {

                        // TaskOperator.getInstance().PauseTask(corpid, taskid,
                        // 3);

                        Toast.makeText(TaskPointDetailActivity.this, "已暂停运货(完成" + getFinishCountHintStr() + ")",
                                Toast.LENGTH_SHORT).show();

                        EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                        finish();

                    } else if (status == 2) {

                        if (taskStatus == 2) {

                            Toast.makeText(TaskPointDetailActivity.this, "运货完成", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TaskPointDetailActivity.this, "已完成", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(TaskPointDetailActivity.this, SelectTransPointActivity.class);
//                            intent.putExtra("corpid", result.getCorpid());
//                            intent.putExtra("taskid", result.getTaskid());
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

                        }

                        EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                        finish();
                    } else if (status == 1) {
                        EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                        Toast.makeText(TaskPointDetailActivity.this, "已开始", Toast.LENGTH_SHORT).show();

                        mStoreDetail.storestatus = 1;
                        if (mOrderDetail != null)
                            setData();
                        else
                            setSimpleData();


                        if (TaskUtils.isStorePosUnknown(mStoreDetail))
                            return;

                        boolean isnavi = GeneralSPHelper.getInstance(TaskPointDetailActivity.this).isTaskNavi(taskid);

                        if (isnavi || isNeedNavi) {

//                            DeliveryRouteAPI.routeplan(mStoreDetail, TaskPointDetailActivity.this,
//                                    new CldRoutePlaner.RoutePlanListener() {
//
//                                        @Override
//                                        public void onRoutePlanSuccessed() {
//
//                                            setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
//                                            TaskPointDetailActivity.this.runOnUiThread(new Runnable() {
//
//                                                @Override
//                                                public void run() {
//                                                    // TODO Auto-generated
//                                                    // method stub
//                                                    Intent intent = new Intent(TaskPointDetailActivity.this,
//                                                            RoutePreviewActivity.class);
//
//                                                    String str = GsonTool.getInstance().toJson(mStoreDetail);
//                                                    intent.putExtra("storedetail", str);
//
//                                                    // 添加taskid
//                                                    intent.putExtra("taskid", mStoreDetail.taskId);
//                                                    intent.putExtra("corpid", mStoreDetail.corpId);
//                                                    intent.putExtra("isFromTaskDetail", true);
//                                                    if (mOrderDetail != null) {
//                                                        String str2 = GsonTool.getInstance().toJson(mOrderDetail);
//                                                        intent.putExtra("orderdetail", str2);
//                                                    }
//
//                                                    str = GsonTool.getInstance().toJson(
//                                                            FreightPointDeal.getInstace().getmMtqDeliTaskDetail());
//                                                    intent.putExtra("taskdetail", str);
//
//                                                    startActivityForResult(intent,
//                                                            FreightConstant.TASK_POINT_REQUSEST_CODE);
//
//                                                    // finish();
//                                                }
//                                            });
//                                        }
//
//                                        @Override
//                                        public void onRoutePlanFaied(int arg0, String arg1) {
//
//                                            final String errcode = arg1;
//                                            TaskPointDetailActivity.this.runOnUiThread(new Runnable() {
//
//                                                @Override
//                                                public void run() {
//                                                    // TODO Auto-generated
//                                                    // method stub
//                                                    Toast.makeText(TaskPointDetailActivity.this, errcode,
//                                                            Toast.LENGTH_LONG).show();
//                                                    setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
//
//                                                }
//                                            });
//
//                                            // finish();
//                                        }
//
//                                        @Override
//                                        public void onRoutePlanCanceled() {
//
//                                            // finish();
//                                        }
//                                    });

                        } else {

                            // setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
                            // showNavigationDialog(mStoreDetail);

                            // finish();

                            // new Handler().postDelayed(new Runnable() {
                            //
                            // @Override
                            // public void run() {
                            //
                            // showNavigationDialog(mStoreDetail);
                            // }
                            // }, 500);

                        }

                    }

                }

            }

            @Override
            public void OnError(int ErrCode) {

                if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                    // Toast.makeText(TaskPointDetailActivity.this,
                    // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(TaskPointDetailActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
                WaitingProgressTool.closeshowProgress();






            }

            @Override
            public void OnGetTag(String Reqtag) {


            }
        };

        if (status == 1) {


            if (TaskOperator.getInstance().getmCurrentTask() != null
                    && TaskOperator.getInstance().getmCurrentTask().taskid.equals(taskid)) {

                DeliveryApi.getInstance().UpdateStoreStatus(this, corpid, taskid, mStoreDetail.storeid, 1,
                        mStoreDetail.waybill, fewaybill, -1, mrespone, mStoreDetail.waybill); // status
                // ==
                // 3
                // ?
                // ""
                // :

            } else {

                DeliveryApi.getInstance().UpdateStoreStatus(this, corpid, taskid, mStoreDetail.storeid, 1,
                        mStoreDetail.waybill, fewaybill, 1, mrespone, mStoreDetail.waybill); //
            }

        } else {

            if (taskStatus == -1) {

                DeliveryApi.getInstance().UpdateStoreStatus(this, corpid, taskid, mStoreDetail.storeid, status,
                        mStoreDetail.waybill, status == 3 ? "" : fewaybill, -1, mrespone, mStoreDetail.waybill); // status
                // ==
                // 3
                // ?
                // ""
                // :

            } else {

                DeliveryApi.getInstance().UpdateStoreStatus(this, corpid, taskid, mStoreDetail.storeid, status,
                        mStoreDetail.waybill, status == 3 ? "" : fewaybill, taskStatus, mrespone,
                        mStoreDetail.waybill); //
            }

        }
    }

    private void showNavigationDialog(final MtqDeliStoreDetail storeDetail, boolean isclick) {

        if (TaskUtils.isStorePosUnknown(mStoreDetail)) {

//            TaskOperator.getInstance().ShowNaviDisableDialog(this, new OnCallBack() {
//
//                @Override
//                public void onYES() {
//
//
//                }
//
//            });

            return;

        }

//        TaskOperator.getInstance().showNavigationDialog(this, null, new TaskOperator.OnDialogListener() {
//
//            @Override
//            public void OnDialogDismiss() {
//
//                MLog.e("check", "dismiss");
//
//            }
//
//            @Override
//            public void OnClickRight(UpdateTaskStatusEvent event) {


        // FreightPointDeal.getInstace().routeplan(storeDetail);
        // MLog.e("check", "right");
        // isNeedNavi = true;
        // updateStoreStatus(1, false);
        // dfd
        DeliveryRouteAPI.routeplan(mStoreDetail, TaskPointDetailActivity.this, new CldRoutePlaner.RoutePlanListener() {

            @Override
            public void onRoutePlanSuccessed() {

                EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                TaskPointDetailActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated
                        // method stub
                        Intent intent = new Intent(TaskPointDetailActivity.this, RoutePreviewActivity.class);

                        String str = GsonTool.getInstance().toJson(mStoreDetail);
                        intent.putExtra("storedetail", str);

                        // 添加taskid
                        intent.putExtra("taskid", mStoreDetail.taskId);
                        intent.putExtra("corpid", mStoreDetail.corpId);
                        intent.putExtra("isFromTaskDetail", true);
                        if (mOrderDetail != null) {
                            String str2 = GsonTool.getInstance().toJson(mOrderDetail);
                            intent.putExtra("orderdetail", str2);
                        }

                        str = GsonTool.getInstance()
                                .toJson(FreightPointDeal.getInstace().getmMtqDeliTaskDetail());
                        intent.putExtra("taskdetail", str);

                        startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);

                        // finish();
                    }
                });
            }

            @Override
            public void onRoutePlanFaied(int arg0, String arg1) {

                final String errcode = arg1;
                TaskPointDetailActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated
                        // method stub
                        Toast.makeText(TaskPointDetailActivity.this, errcode, Toast.LENGTH_LONG).show();
                        EventBus.getDefault().post(new FreightPointUpdateEvent(0));

                    }
                });

                // finish();
            }

            @Override
            public void onRoutePlanCanceled() {

                // finish();
            }
        });

//            }
//
//            @Override
//            public void OnClickLeft(UpdateTaskStatusEvent event) {
//
//                // updateStoreStatus(1, false);
//                // dfd
//            }
//        });
    }

    private boolean isNeedNavi = false;

    public CharSequence SetStrSafety(CharSequence str) {

        return str == null ? "" : str;

    }

    public String SetStrSafety(String str) {

        return str == null ? "" : str;

    }


    public CharSequence getPointName() {
        if (mStoreDetail != null && !TextUtils.isEmpty(mStoreDetail.storecode)) {

            CharSequence format = FreightLogicTool.formatPointNameStr(mStoreDetail.storesort + ".", mStoreDetail.storecode, "-" + mStoreDetail.storename);
            if (format != null) {
                return format;
            }
        }
        return mStoreDetail.storesort + "." + mStoreDetail.storecode + "-" + mStoreDetail.storename;
    }

    // public void UploadPic(final String filepath, final int type) {
    //
    // WaitingProgressTool.showProgress(this);
    // // // String base64_pic = null;
    // // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // // Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
    // // R.drawable.icon_storetype_send);
    // // //ImageTools.getPhotoFromSDCard(filepath);
    // // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    // // byte[] datas = baos.toByteArray();
    // //
    //
    // // ImageTools.getPhotoByteArray(filepath);
    //
    // String base64_pic = null;
    // try {
    // byte[] datas = FileUtils.toByteArray(filepath);
    // // FileUtils.toByteArray(filepath)
    // // Base64.encodeToString(FileUtils.toByteArray(filepath),
    // // Base64.NO_PADDING);
    // // FileUtils.toByteArray(filepath)
    // // base64_pic = ImageTools.getPhotoBase64(filepath); //
    // // Base64.encodeToString(datas,
    // // Base64.NO_WRAP);
    // // new
    // //
    // String(org.apache.commons.codec.binary.Base64.encodeBase64(FileUtils.toByteArray(filepath)));
    // // com.alipay.sdk.encrypt.Base64.a(FileUtils.toByteArray(filepath));
    // // Base64.encode(datas,Base64.NO_WRAP));
    // // base64_pic = "data:image/jpeg;base64,"+base64_pic;
    //
    // // base64_pic = URLEncoder.encode(base64_pic,"UTF-8");
    //
    // base64_pic = Base64.encodeToString(datas, Base64.DEFAULT);
    //
    // DebugTool.saveFile(base64_pic);
    // } catch (Exception e) {
    //
    // e.printStackTrace();
    // Toast.makeText(TaskPointDetailActivity.this, "图片加载失败",
    // Toast.LENGTH_SHORT).show();
    // return;
    // }
    //
    // long uptime = System.currentTimeMillis() / 1000;
    //
    // DeliveryApi.getInstance().UpLoadDeliPicture(corpid, taskid,
    // mStoreDetail.waybill, mStoreDetail.cust_orderid,
    // uptime, type, uptime - 30, 0, 0, base64_pic, new
    // OnResponseResult<Integer>() {
    //
    // @Override
    // public void OnResult(Integer result) {
    //
    // // MLog.e("checkuploadpic", result + "");
    // if (isFinishing())
    // return;
    // if (result == 0) {
    //
    // isUpdateStore = true;
    //
    // Toast.makeText(TaskPointDetailActivity.this, "图片上传成功",
    // Toast.LENGTH_SHORT).show();
    //
    // if (type == 1) {
    // piclistpath.add(filepath);
    // RefreshHint();
    // picAdapter.setList(piclistpath);
    // } else {
    // receiptlistpath.add(filepath); // afterCompressPicPath
    // RefreshHint();
    // receiptAdapter.setList(receiptlistpath);
    //
    // }
    // } else {
    //
    // Toast.makeText(TaskPointDetailActivity.this, "图片上传失败",
    // Toast.LENGTH_SHORT).show();
    //
    // }
    //
    // WaitingProgressTool.closeshowProgress();
    //
    // }
    //
    // @Override
    // public void OnError(int ErrCode) {
    //
    // if (isFinishing())
    // return;
    //
    // // MLog.e("checkuploadpic", ErrCode + "");
    // if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode ==
    // DeliveryApi.TASKPOINT_CANCEL) {
    // // Toast.makeText(TaskPointDetailActivity.this,
    // // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
    // } else
    // Toast.makeText(TaskPointDetailActivity.this, "图片上传失败",
    // Toast.LENGTH_SHORT).show();
    // WaitingProgressTool.closeshowProgress();
    //
    // }
    //
    // @Override
    // public void OnGetTag(String Reqtag) {
    //
    //
    // }
    // });
    //
    // }

    public synchronized void UploadPic() {

        if (uploadpiclistpath != null && uploadpiclistpath.size() == 0 && uploadreceiptlistpath != null
                && uploadreceiptlistpath.size() == 0) {

            WaitingProgressTool.closeshowProgress();
            Toast.makeText(TaskPointDetailActivity.this, "更新完成", Toast.LENGTH_SHORT).show();

            //
            // if (isUpdateStore)
            setFeedBackVisible();

            EventBus.getDefault().post(new FreightPointUpdateEvent(0));
            finish();

            // setFeedBackVisible();
            return;
        }

        if (uploadreceiptlistpath == null || uploadpiclistpath == null) {
            WaitingProgressTool.closeshowProgress();
            return;
        }

        WaitingProgressTool.showProgress(this);

        String pic = null;
        int type = 1;

        if (uploadpiclistpath != null && uploadpiclistpath.size() > 0) {

            pic = uploadpiclistpath.get(0);
            type = 1;

        } else {

            pic = uploadreceiptlistpath.get(0);
            type = 2;
        }

        final int ftype = type;
        final String fpic = pic;
        String base64_pic = null;
        try {
            base64_pic = Base64.encodeToString(FileUtils.toByteArray(fpic), Base64.DEFAULT);
            // MyDebugTool.saveFile(base64_pic);
        } catch (IOException e) {

            e.printStackTrace();
        }

        long uptime = System.currentTimeMillis() / 1000;

        DeliveryApi.getInstance().UpLoadDeliPicture(corpid, taskid, mStoreDetail.waybill, mStoreDetail.waybill,
                uptime, ftype, uptime - 30, 0, 0, base64_pic, new OnResponseResult<Integer>() {

                    @Override
                    public void OnResult(Integer result) {

                        // MLog.e("checkuploadpic", result + "");
                        if (isFinishing())
                            return;
                        if (result == 0) {

                            if (ftype == 1) {

                                Iterator<String> stuIter = uploadpiclistpath.iterator();
                                over:
                                while (stuIter.hasNext()) {
                                    String tmp = stuIter.next();
                                    if (tmp.equals(fpic)) {
                                        stuIter.remove();
                                        break over;
                                    }
                                }
                            } else if (ftype == 2) {
                                Iterator<String> stuIter = uploadreceiptlistpath.iterator();
                                over:
                                while (stuIter.hasNext()) {
                                    String tmp = stuIter.next();
                                    if (tmp.equals(fpic)) {
                                        stuIter.remove();
                                        break over;
                                    }
                                }
                            }
                            EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                            UploadPic();

                        } else {
                            // ，已上传" + (picIndex) + "张，未上传" +
                            // (piclistpath.size() - picIndex) + "张"
                            Toast.makeText(TaskPointDetailActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                            WaitingProgressTool.closeshowProgress();
                            // setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
                            //
                            //
                            // if (isUpdateStore)
                            // setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
                            // finish();

                        }

                    }

                    @Override
                    public void OnError(int ErrCode) {

                        if (isFinishing())
                            return;

                        MLog.e("checkuploadpic", ErrCode + "");
                        if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                            // Toast.makeText(TaskPointDetailActivity.this,
                            // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                        } else {

                            // ，已上传" + (picIndex) + "张，未上传" +
                            // (piclistpath.size() - picIndex) + "张
                            Toast.makeText(TaskPointDetailActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                        }
                        WaitingProgressTool.closeshowProgress();
                        // setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
                        //
                        //
                        // if (isUpdateStore)
                        // setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
                        // finish();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                });

    }

    // 处理任务状态变更
    public void HandleTaskStatusChangeEvent(UpdateTaskStatusEvent event, final int statusOperateType,
                                            final boolean isLast) {

        WaitingProgressTool.showProgress(this);

        final String ecorpid = TaskOperator.getInstance().getmCurrentTask() == null ? ""
                : TaskOperator.getInstance().getmCurrentTask().corpid;
        final String etaskid = TaskOperator.getInstance().getmCurrentTask() == null ? ""
                : TaskOperator.getInstance().getmCurrentTask().taskid;

        DeliveryApi.getInstance().UpdateTaskInfo(TaskPointDetailActivity.this, corpid, taskid, event.getStatus(), ecorpid, etaskid, event.getX(),
                event.getY(), event.getCell(), event.getUid(), new OnResponseResult<UpdateTaskStatusResult>() {

                    @Override
                    public void OnResult(UpdateTaskStatusResult result) {

                        if (isFinishing())
                            return;
                        // MLog.e("updatetask", result.getErrCode() + " " +
                        // result.getStatus() + " " + result.getTaskid());
                        WaitingProgressTool.closeshowProgress();

                        if (result.getErrCode() != 0) {
                            // 请求错误
                            Toast.makeText(TaskPointDetailActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT).show();

                            if(TaskUtils.isNeedUpdateInfoCode(result.getErrCode())){
                                RefreshDetailData();
                                EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                            }

                            return;
                        }

                        TaskOperator.getInstance().HandleResultAfterRequest(result);
                        EventBus.getDefault().post(new FreightPointStatusRefreshEvent());

                        if (statusOperateType == 3) {

                            if (!isLast) {

//                                Intent intent = new Intent(TaskPointDetailActivity.this,
//                                        SelectTransPointActivity.class);
//                                intent.putExtra("corpid", result.getCorpid());
//                                intent.putExtra("taskid", result.getTaskid());
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

                                // Toast.makeText(TaskPointDetailActivity.this,
                                // R.string.request_success,
                                // Toast.LENGTH_SHORT).show();

                                EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                                finish();
                            } else if (isLast) {

                                Toast.makeText(TaskPointDetailActivity.this, "已暂停运货(完成" + getFinishCountHintStr() + ")",
                                        Toast.LENGTH_SHORT).show();

                                EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                                finish();

                            }

                        } else {

                            Operate(statusOperateType);
                        }

                    }

                    @Override
                    public void OnError(int ErrCode) {

                        if (isFinishing())
                            return;

                        // MLog.e("updatetask", ErrCode + " error");
                        if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                            // Toast.makeText(TaskPointDetailActivity.this,
                            // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(TaskPointDetailActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
                        WaitingProgressTool.closeshowProgress();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                });
    }

    public int getTaskStatus(int requestStatus) {

        if (TaskOperator.getInstance().getmCurrentTask() == null)
            return -1;

        if (requestStatus != TaskStatus.DELIVERRED) {

            return -1;
        }

        MtqDeliTaskDetail taskdetail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);
        if (taskdetail == null || taskdetail.store == null)
            return -1;

        boolean update = true;

        // 请求的是完成运货点 而整个单不是完成状态
        if (requestStatus == TaskStatus.DELIVERRED
                && TaskOperator.getInstance().getmCurrentTask().status != TaskStatus.DELIVERRED) {

            Iterator<MtqDeliStoreDetail> iterator = taskdetail.store.iterator();
            over:
            while (iterator.hasNext()) {
                MtqDeliStoreDetail tmp = iterator.next();

                if (!tmp.waybill.equals(mStoreDetail.waybill) && TaskStatus.DELIVERRED != tmp.storestatus) {
                    update = false;
                    break over;

                }
            }

        }

        if (update)
            return 2;
        else
            return -1;

    }

    /**
     * @param phoneNumber
     * @Title: call
     * @Description: TODO
     * @return: void
     */
    public void call(final String phoneNumber) {

        // mPhone = phoneNumber;
        // String cancel = getResources().getString(R.string.dialog_cancel);
        // String sure = getResources().getString(R.string.dialog_call);
        //
        // PhoneCallDialog dialog = new PhoneCallDialog(this, phoneNumber,
        // cancel, sure,
        // new PhoneCalldialog.iPhoneCallDialogListener() {
        //
        // @Override
        // public void OnCancel() {
        //
        // }
        //
        // @Override
        // public void OnSure(String phones) {
        // onSure(phones);
        // }
        // });
        // dialog.show();

        CallUtil.call(this, phoneNumber);

    }

    @TargetApi(23)
    private void onSure(String phone) {
        if (!TextUtils.isEmpty(phone)) {

            int curApiVersion = Build.VERSION.SDK_INT;
            if (curApiVersion >= Build.VERSION_CODES.M) {
                // 在6.0 系统中请求某些权限需要检查权限
                if (!hasPermission()) {
                    // 动态请求拨打电话权限
                    this.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 0x11);
                } else {
                    intentToCall(phone);
                }
            } else {
                intentToCall(phone);
            }
        }
    }

    @TargetApi(23)
    private boolean hasPermission() {
        String permission = Manifest.permission.CALL_PHONE;
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    //TaskAskPopUpDialog mPopUpDialog;

    public synchronized void showStopAskDialog(final boolean isLast) {

        mPopUpDialog = new TaskAskPopUpDialog(this);

        mPopUpDialog.show();

        mPopUpDialog.setDialogType(isLast ? 5 : 6);
        mPopUpDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPopUpDialog.tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                mPopUpDialog.dismiss();

            }
        });
        mPopUpDialog.tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                if (mOrderDetail == null) {
                    UpdateTaskStatusEvent event = new UpdateTaskStatusEvent(corpid, taskid, 3, corpid, taskid, 0, 0, 0,
                            0);
                    HandleTaskStatusChangeEvent(event, 3, isLast);

                } else {

                    ShowFeedBackDialog(true, isLast);
                }

                mPopUpDialog.dismiss();

            }
        });

    }

    /**
     * 拍照
     *
     * @param path 照片存放的路径
     */
    public void captureImage(String path, int type, int position) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory("android.intent.category.DEFAULT");
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        Uri contentUri = FileUtils.getUriForFile(this, new File(path, "image.jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, IMAGE_CAPTURE + type + (position + 1) * 1000);
    }

    /**
     * 从图库中选取图片
     */
    public void selectImage(int type, int position) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, IMAGE_SELECT + type + (position + 1) * 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == MapSelectPointActivity.resultCode && requestCode == RequestCode_Address) {

            if (data != null && data.hasExtra("addressinfo") && data.getParcelableExtra("addressinfo") != null) {

                AddressBean bean = data.getParcelableExtra("addressinfo");

                SPHelper.getInstance(this).writeLocalStoreAddress(mStoreDetail.waybill, bean);

                HPWPoint point = CldCoordUtil.kcodeToCLD(bean.kcode);

                mStoreDetail.storex = point.x;
                mStoreDetail.storey = point.y;
                mStoreDetail.storeaddr = bean.address;
                mStoreDetail.isUnknownAddress = true;

                MtqDeliTaskDetail taskdetail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);

                for (MtqDeliStoreDetail storeDetail : taskdetail.store) {

                    if (storeDetail.waybill.equals(mStoreDetail.waybill)) {

                        storeDetail.storex = point.x;
                        storeDetail.storey = point.y;
                        storeDetail.storeaddr = bean.address;
                        storeDetail.isUnknownAddress = true;

                    }

                }

                TaskOperator.getInstance().saveTaskDetailDataToBD(taskdetail);

                if (mOrderDetail != null)
                    setData();
                else
                    setSimpleData();

                EventBus.getDefault().post(new FreightPointUpdateEvent(0));
            }

            return;
        }

        if (requestCode >= FeedBackDialog.IMAGE_CAPTURE - 1 && requestCode <= FeedBackDialog.IMAGE_CAPTURE + 2 ||

                requestCode >= FeedBackDialog.IMAGE_SELECT - 1 && requestCode <= FeedBackDialog.IMAGE_SELECT + 2)

        {

            if (mFeedBackDialog != null)
                mFeedBackDialog.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == CODE_FINISHTRANS) {

            EventBus.getDefault().post(new FreightPointUpdateEvent(0));
            finish();
            return;

        }

        if (requestCode == FreightConstant.TASK_POINT_REQUSEST_CODE) {

            RefreshDetailData();
            EventBus.getDefault().post(new FreightPointUpdateEvent(0));
            return;
        }

        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {

            String afterCompressPicPath;
            switch (requestCode) {
                case IMAGE_CAPTURE + GOOD_RECEIPT:// 拍照返回 回单

                    afterCompressPicPath = ImageTools.compress(TaskPointDetailActivity.this,
                            MainApplication.getTmpCacheFilePath() + "/image.jpg");

                    if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                        uploadreceiptlistpath.add(afterCompressPicPath);
                        receiptlistpath.add(afterCompressPicPath);
                        RefreshHint();
                        receiptAdapter.setList(receiptlistpath);

                        // UploadPic(afterCompressPicPath, 2);

                    } else {
                        Toast.makeText(TaskPointDetailActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case IMAGE_SELECT + GOOD_RECEIPT:// 选择照片返回 回单
                    // 照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {

                        afterCompressPicPath = null;

                        String fp = FileUtils.getRealFilePath(TaskPointDetailActivity.this, originalUri);

                        afterCompressPicPath = ImageTools.compress(TaskPointDetailActivity.this, fp);

                        if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                            uploadreceiptlistpath.add(afterCompressPicPath);
                            receiptlistpath.add(afterCompressPicPath); //
                            RefreshHint();
                            receiptAdapter.setList(receiptlistpath);

                            // UploadPic(afterCompressPicPath, 2);
                        } else {
                            Toast.makeText(TaskPointDetailActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case IMAGE_CAPTURE + GOOD_PIC:// 拍照返回 货物照片

                    afterCompressPicPath = ImageTools.compress(TaskPointDetailActivity.this,
                            MainApplication.getTmpCacheFilePath() + "/image.jpg");

                    if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                        uploadpiclistpath.add(afterCompressPicPath);
                        piclistpath.add(afterCompressPicPath);
                        RefreshHint();
                        picAdapter.setList(piclistpath);
                        //

                        // UploadPic(afterCompressPicPath, 1);
                    } else {
                        Toast.makeText(TaskPointDetailActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case IMAGE_SELECT + GOOD_PIC:// 选择照片返回 货物照片
                    // 照片的原始资源地址
                    Uri originalUri2 = data.getData();
                    try {

                        afterCompressPicPath = null;

                        afterCompressPicPath = ImageTools.compress(TaskPointDetailActivity.this,
                                FileUtils.getRealFilePath(TaskPointDetailActivity.this, originalUri2));

                        if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {
                            uploadpiclistpath.add(afterCompressPicPath);
                            piclistpath.add(afterCompressPicPath);
                            RefreshHint();
                            picAdapter.setList(piclistpath);
                            // FileUtils.getRealFilePath(TaskPointDetailActivity.this,
                            // originalUri2);
                            // UploadPic(afterCompressPicPath, 1);
                        } else {
                            Toast.makeText(TaskPointDetailActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    break;
            }

            // modify发起的
            int position = 0;

            if (requestCode < 1000)
                return;

            if (requestCode % 1000 == (IMAGE_CAPTURE + GOOD_RECEIPT)) {

                position = requestCode / 1000 - 1;

                if (position < 0 || position >= receiptlistpath.size())
                    return;

                afterCompressPicPath = ImageTools.compress(TaskPointDetailActivity.this,
                        MainApplication.getTmpCacheFilePath() + "/image.jpg");

                if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                    modifyUploadPic(uploadreceiptlistpath, receiptlistpath.get(position), afterCompressPicPath);

                    receiptlistpath.set(position, afterCompressPicPath);

                    RefreshHint();
                    receiptAdapter.setList(receiptlistpath);
                } else {
                    Toast.makeText(TaskPointDetailActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode % 1000 == (IMAGE_SELECT + GOOD_RECEIPT)) {

                position = requestCode / 1000 - 1;

                if (position < 0 || position >= receiptlistpath.size())
                    return;

                Uri originalUri = data.getData();
                try {

                    afterCompressPicPath = null;

                    String fp = FileUtils.getRealFilePath(TaskPointDetailActivity.this, originalUri);

                    afterCompressPicPath = ImageTools.compress(TaskPointDetailActivity.this, fp);

                    if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                        modifyUploadPic(uploadreceiptlistpath, receiptlistpath.get(position), afterCompressPicPath);
                        uploadreceiptlistpath.add(afterCompressPicPath);

                        RefreshHint();
                        receiptAdapter.setList(receiptlistpath);
                    } else {
                        Toast.makeText(TaskPointDetailActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT)
                                .show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode % 1000 == (IMAGE_CAPTURE + GOOD_PIC)) {

                position = requestCode / 1000 - 1;

                if (position < 0 || position >= piclistpath.size())
                    return;

                afterCompressPicPath = ImageTools.compress(TaskPointDetailActivity.this,
                        MainApplication.getTmpCacheFilePath() + "/image.jpg");

                if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {
                    modifyUploadPic(uploadpiclistpath, piclistpath.get(position), afterCompressPicPath);
                    piclistpath.set(position, afterCompressPicPath);
                    RefreshHint();
                    picAdapter.setList(piclistpath);
                } else {
                    Toast.makeText(TaskPointDetailActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode % 1000 == (IMAGE_SELECT + GOOD_PIC)) {

                position = requestCode / 1000 - 1;

                if (position < 0 || position >= piclistpath.size())
                    return;

                Uri originalUri2 = data.getData();
                try {

                    afterCompressPicPath = null;

                    afterCompressPicPath = ImageTools.compress(TaskPointDetailActivity.this,
                            FileUtils.getRealFilePath(TaskPointDetailActivity.this, originalUri2));

                    if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                        modifyUploadPic(uploadpiclistpath, piclistpath.get(position), afterCompressPicPath);

                        piclistpath.set(position, afterCompressPicPath);
                        RefreshHint();
                        picAdapter.setList(piclistpath);
                    } else {
                        Toast.makeText(TaskPointDetailActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT)
                                .show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 打开图片查看器
     *
     * @param position
     * @param urls2
     */
    protected void imageBrower(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(this, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        intent.putExtra("corpid", corpid);
        intent.putExtra("taskid", taskid);
        startActivity(intent);
    }

    private void intentToCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNumber);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (isUpdateStore)
            EventBus.getDefault().post(new FreightPointUpdateEvent(0));

        if (isModifyInfo()) {

            ShowAskDialog();

        } else
            finish();
        // super.onBackPressed();

    }

    private boolean isModifyInfo() {


        if ((uploadpiclistpath != null && uploadpiclistpath.size() > 0)
                || (uploadreceiptlistpath != null && uploadreceiptlistpath.size() > 0)) {
            return true;
        } else
            return false;
    }

    @Override
    protected void onResume() {

        super.onResume();
        // updateNews();
        if (isUpdate)
            RefreshDetailData();
    }

    // private void updateNews() {
    // if (MsgManager.getInstance().hasUnReadMsg()) {
    // ivTitleright.setImageResource(R.drawable.msg_icon_news);
    // } else {
    // ivTitleright.setImageResource(R.drawable.msg_icon);
    // }
    // }

    @Override
    public void onStart() {
        super.onStart();
        // EventBus.getDefault().register(this);

        if (mOrderDetail == null)
            return;


        if (TaskUtils.isStorePosUnknown(mStoreDetail)) {

            return;

        }


        AddressBean bean = SPHelper2.getInstance(mContext).readMarkStoreAddress(mStoreDetail.taskId + mStoreDetail.waybill);

        if (mStoreDetail != null && mStoreDetail.storestatus != 2 && bean == null) {


            if (!GPSUtils.isOPen(this)) {

                LocationRemindDialog dialog = new LocationRemindDialog(this, "定位服务未开启",
                        "请在设置中开启 \"定位\" 服务", "设置", "知道了", new LocationRemindDialog.IClickListener() {
                    @Override
                    public void OnCancel() {
                        GPSUtils.openGPSSettings(TaskPointDetailActivity.this);

                        onLocation();
                    }

                    @Override
                    public void OnSure() {
                        onLocation();
                    }
                });


                dialog.show();

                //   return;

            } else {

                onLocation();
            }
            // }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        //  setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
        EventBus.getDefault().unregister(this);
        WaitingProgressTool.closeshowProgress(this);
        LocationAPI.getInstance().stop();

    }

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

//    protected void showProgressBar() {
//        if (pb_waiting != null)
//            pb_waiting.setVisibility(View.VISIBLE);
//    }
//
//    protected void hideProgressBar() {
//        if (pb_waiting != null)
//            pb_waiting.setVisibility(View.GONE);
//    }

    protected void showProgressBar2() {

//
//        if(pb_waiting!=null && pb_waiting.getVisibility()==View.VISIBLE)
//            pb_waitin


        if (pb_waiting2 != null)
            pb_waiting2.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar2() {
        if (pb_waiting2 != null)
            pb_waiting2.setVisibility(View.GONE);
    }

    public void RefreshDetailData() {

        showProgressBar2();

        DeliveryApi.getInstance().getTaskDetailInServer(taskid, corpid, new OnResponseResult<MtqDeliTaskDetail>() {

            @Override
            public void OnResult(MtqDeliTaskDetail result) {

                if (isFinishing())
                    return;

                if (result == null || result.getStore() == null) {
                    Toast.makeText(TaskPointDetailActivity.this, "刷新最新路由点详情失败", Toast.LENGTH_SHORT).show();
                    hideProgressBar2();
                    return;

                }

                hideProgressBar2();

                TaskOperator.getInstance().saveTaskDetailDataToBD(result);

                over1:
                for (MtqDeliStoreDetail store : result.getStore()) {

                    if (store.waybill.equals(mStoreDetail.waybill)) {

                        mStoreDetail = store;
                        break over1;
                    }

                }

                over2:
                for (MtqDeliOrderDetail order : result.getOrders()) {
                    if (order.waybill.equals(mStoreDetail.waybill)) {

                        mOrderDetail = order;
                        break over2;
                    }

                }

                if (mOrderDetail != null)
                    setData();
                else
                    setSimpleData();

            }

            @Override
            public void OnError(int ErrCode) {

                if (isFinishing())
                    return;

                if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                    // Toast.makeText(TaskPointDetailActivity.this,
                    // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(TaskPointDetailActivity.this, "刷新最新路由点详情失败", Toast.LENGTH_SHORT).show();
                hideProgressBar2();
            }

            @Override
            public void OnGetTag(String Reqtag) {


            }
        });

    }

    // 1 为开始运货弹出暂停任务后弹出调整顺序
    // 2 为开始运货点状态变更前调整顺序
    public void ShowChangeAskDialog(final int type, final UpdateTaskStatusEvent event) {

        MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);

        if (!TaskOperator.getInstance().isChangeSequence(detail, mStoreDetail)) {

            CheckIsNoPosition(type);

            return;
        }

        mPopUpDialog = new TaskAskPopUpDialog(this);

        mPopUpDialog.show();

        mPopUpDialog.setDialogType(7);

        // final MtqDeliStoreDetail detail = mS

        mPopUpDialog.tvTitle.setText("您调整了运货顺序，请确认是否已提货?");

        // 选择优先运货到 " + detail.storename + " ,是否继续?"

        mPopUpDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPopUpDialog.tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                mPopUpDialog.dismiss();
            }
        });
        mPopUpDialog.tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                mPopUpDialog.dismiss();

                // Operate();

                CheckIsNoPosition(type);
            }
        });

    }

    public void CheckIsNoPosition(final int type) {

        if (TaskUtils.isStorePosUnknown(mStoreDetail)) {

            boolean isnavi = GeneralSPHelper.getInstance(TaskPointDetailActivity.this).isTaskNavi(taskid);

            if (isnavi) {

                if (type == 1) {

                    StopTaskForStartTrans();
                    // HandleTaskStatusChangeEvent(event, 1, false);

                } else if (type == 2) {

                    Operate(1);
                }


//                TaskOperator.getInstance().ShowNaviDisableDialog(this, new OnCallBack() {
//
//                    @Override
//                    public void onYES() {
//
//
//                    }
//
//                });

            } else {

                if (type == 1) {

                    StopTaskForStartTrans();
                    // HandleTaskStatusChangeEvent(event, 1, false);

                } else if (type == 2) {

                    Operate(1);
                }

            }
        } else {

            if (type == 1) {

                StopTaskForStartTrans();
                // HandleTaskStatusChangeEvent(event, 1, false);

            } else if (type == 2) {

                Operate(1);
            }

        }
    }

    /**
     * @return
     * @Title: isOverTime
     * @Description: 是否超时
     * @return: boolean
     */
    public boolean isOverTime(MtqDeliStoreDetail storeDetail, MtqDeliOrderDetail orderDetial) {
        boolean ret = false;
        if (orderDetial == null) {
            return false;
        }

        if (storeDetail.finishtime == 0 && (orderDetial.reqtime_e) < System.currentTimeMillis() / 1000) {
            // 没有完成。
            ret = true;
        }

        return ret;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public synchronized void onTaskBusinessMsgEvent(TaskBusinessMsgEvent event) {

        switch (event.getType()) {

            // 任务刷新
            case 2:
                if (taskid == null)
                    return;

                if (isFinishing())
                    return;

                if (isRunning) {
                    if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {

                        if (TextStringUtil.isContain(event.getRefreshtaskIdList(), taskid)) {
                            RefreshDetailData();
                        }
                    }
                } else {
                    isUpdate = true;
                }
                break;

            // 作废任务，删除某些任务
            case 3:

                if (taskid == null)
                    return;

                if (isFinishing())
                    return;

                if (event.getTaskIdList() != null && event.getTaskIdList().size() > 0) {

                    if (TextStringUtil.isContainStr(event.getTaskIdList(), taskid)) {

                        // Toast.makeText(this, "当前操作任务单已撤回",
                        // Toast.LENGTH_LONG).show();
                        finish();
                    }

                }

                break;
            case 4:
                if (taskid == null)
                    return;

                if (isFinishing())
                    return;

                if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {

                    if (mOrderDetail != null && !TextUtils.isEmpty(mOrderDetail.waybill)) {

                        if (TextStringUtil.isContain(event.getRefreshtaskIdList(), taskid, mOrderDetail.waybill)) {
                            finish();
                        }

                    }
                }

                break;

            default:
                break;
        }

    }

    public void StopTaskForStartTrans() {

        if (!(TaskOperator.getInstance().getmCurrentTask() != null
                && !TaskOperator.getInstance().getmCurrentTask().taskid.equals(taskid))) {
            Operate(1);
            return;
        }

        showProgressBar2();

        DeliveryApi.getInstance().UpdateTaskInfo(TaskPointDetailActivity.this, TaskOperator.getInstance().getmCurrentTask().corpid,
                TaskOperator.getInstance().getmCurrentTask().taskid, 3, "", "", 0, 0, 0, 0,
                new OnResponseResult<UpdateTaskStatusResult>() {

                    @Override
                    public void OnResult(UpdateTaskStatusResult result) {

                        if (isFinishing())
                            return;

                        hideProgressBar2();

                        if (result.getErrCode() != 0) {
                            // 请求错误
                            Toast.makeText(TaskPointDetailActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT).show();

                            if(result.getErrCode() == 2000){
                                RefreshDetailData();
                                EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                            }


                            return;
                        }

                        TaskOperator.getInstance().HandleResultAfterRequest(result);

                        // 刷新首页任务列表
                        EventBus.getDefault().post(new FreightPointStatusRefreshEvent());
                        // TaskOperator.getInstance().ChangeCurrentTask(result.getCorpid(),
                        // result.getTaskid(), 1);
                        EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                        Operate(1);

                    }

                    @Override
                    public void OnError(int ErrCode) {

                        if (isFinishing())
                            return;

                        MLog.e("updatetask", ErrCode + " error");
                        if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                            // Toast.makeText(TaskPointDetailActivity.this,
                            // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(TaskPointDetailActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
                        hideProgressBar2();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                });
    }

    public void ShowFeedBackDialog(final boolean isNeedStopTask, final boolean isLast) {

        showProgressBar2();

        CldBllKDelivery.getInstance().getFeedBackReasonInfo(corpid, new IGetFeedBackInfoListener() {

            @Override
            public void onGetResult(int errCode, List<FeedBackInfo> result) {

                if (isFinishing())
                    return;


                hideProgressBar2();

                if (errCode == 0 && result != null) {
                    //
                    // ArrayList<FeedBackInfo > list = new ArrayList<>();
                    // FeedBackInfo info = new FeedBackInfo();
                    // info.name = "客户原因";
                    // FeedBackInfo info2 = new FeedBackInfo();
                    // info2.name = "客户原因2";
                    // FeedBackInfo info3 = new FeedBackInfo();
                    // info3.name = "客户原因3";
                    //
                    // list.add(info);
                    // list.add(info2);
                    // list.add(info3);
                    mFeedBackDialog = new FeedBackDialog(TaskPointDetailActivity.this, (ArrayList<FeedBackInfo>) result,
                            mStoreDetail, mOrderDetail, isNeedStopTask ? 2 : 1);
                    mFeedBackDialog.show();

                    mFeedBackDialog.setOnOkButtonClickListener(new FeedBackDialog.OnOkButtonClickListener() {
                        @Override
                        public void onClick() {
                            AppStatApi.statOnEventWithLoginName(getApplicationContext(), AppStatApi.BD_STAT_FEEDBACK_COMMIT_CLICK);
                        }
                    });

                    if (isNeedStopTask) {

                        mFeedBackDialog.setOnDismissListener(new OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {

                                UpdateTaskStatusEvent event = new UpdateTaskStatusEvent(corpid, taskid, 3, corpid,
                                        taskid, 0, 0, 0, 0);
                                HandleTaskStatusChangeEvent(event, 3, isLast);
                            }
                        });

                    } else {

                        mFeedBackDialog.setOnDismissListener(new OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {


                                if (prlLookFeedback.getVisibility() == View.GONE) {

                                    if (GetIsHasFeedBack(true)) {

                                        prlLookFeedback.setVisibility(View.VISIBLE);
                                    } else {
                                        prlLookFeedback.setVisibility(View.GONE);

                                    }

                                }
                            }
                        });

                    }

                } else {

                    if (isNeedStopTask) {



                        UpdateTaskStatusEvent event = new UpdateTaskStatusEvent(corpid, taskid, 3, corpid,
                                taskid, 0, 0, 0, 0);
                        HandleTaskStatusChangeEvent(event, 3, isLast);

                    }
                    //   Toast.makeText(TaskPointDetailActivity.this, "获取反馈信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGetReqKey(String arg0) {


            }
        });

    }

    public void setFeedBackVisible() {

        if (GetIsHasFeedBack(true)) {

            prlLookFeedback.setVisibility(View.VISIBLE);
        } else {
            prlLookFeedback.setVisibility(View.GONE);

        }

        if (mStoreDetail.storestatus == 0 || mStoreDetail.storestatus == 3 || mStoreDetail.storestatus == 1) {

            if (mOrderDetail == null)
                tvTitleright.setVisibility(View.GONE);
            else
                tvTitleright.setVisibility(View.VISIBLE);

            tvTitleright2.setVisibility(View.GONE);

        } else {

            tvTitleright.setVisibility(View.GONE);

            if (mOrderDetail == null)
                tvTitleright2.setVisibility(View.GONE);
            else
                tvTitleright2.setVisibility(View.VISIBLE);

            if ((uploadpiclistpath != null && uploadpiclistpath.size() > 0)
                    || (uploadreceiptlistpath != null && uploadreceiptlistpath.size() > 0)) {

                tvTitleright2.setTextColor(this.getResources().getColor(R.color.app_color2));
            } else
                tvTitleright2.setTextColor(this.getResources().getColor(R.color.scan_text_gray));

            // tvTitleright.setText("更新");

        }


      //  tvTitleright2.setVisibility(View.VISIBLE);

    }

    public boolean GetIsHasFeedBack(boolean isNeedCallBack) {

        return DeliveryApi.getInstance().isHasFeedBack(corpid, taskid, mStoreDetail == null ? "" : mStoreDetail.waybill,
                this, (!isNeedCallBack) ? null : (new OnResponseResult<Boolean>() {

                    @Override
                    public void OnResult(Boolean result) {


                        if (isFinishing())
                            return;

                        if (result)
                            prlLookFeedback.setVisibility(View.VISIBLE);
                        else
                            prlLookFeedback.setVisibility(View.GONE);
                    }

                    @Override
                    public void OnError(int ErrCode) {


                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                }));
    }

    public void ShowAskDialog() {

        final TaskAskPopUpDialog mPopUpDialogs = new TaskAskPopUpDialog(this);

        mPopUpDialogs.show();

        mPopUpDialogs.setDialogType(9);

        // final MtqDeliStoreDetail detail = mS

        // 选择优先运货到 " + detail.storename + " ,是否继续?"

        mPopUpDialogs.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPopUpDialogs.tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                mPopUpDialogs.dismiss();

                finish();

            }
        });
        mPopUpDialogs.tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                mPopUpDialogs.dismiss();

                if (!CommonTool.isFastDoubleClick()) {
                    if ((uploadpiclistpath != null && uploadpiclistpath.size() > 0)
                            || (uploadreceiptlistpath != null && uploadreceiptlistpath.size() > 0)) {

                        UploadPic();
                    }
                }

            }
        });

    }

    /**
     * 更改，删除
     */
    public void modifyPic(final int type, final int position) {

        if (PermissionUtil.isNeedPermissionForStorage(this)) {

            Toast.makeText(this, "请打开储存空间权限", Toast.LENGTH_SHORT).show();
            return;

        }

        SimpleIndexSelectDialog.ShowSelectDialog(this, new String[]{"更改-拍照", "更改-图库", "查看", "删除"}, true,
                new SimpleIndexSelectDialog.OnSimpleIndexSelectCallBack() {

                    @Override
                    public void OnIndexSelect(int index, String select) {

                        switch (index) {
                            case 0:// 拍照

                                if (PermissionUtil.isNeedPermission(TaskPointDetailActivity.this,
                                        Manifest.permission.CAMERA)) {

                                    Toast.makeText(TaskPointDetailActivity.this, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
                                    return;

                                }

                                captureImage(MainApplication.getTmpCacheFilePath(), type, position);

                                break;
                            case 1:// 从图库选择
                                selectImage(type, position);

                                break;
                            case 2:// 查看

                                if (type == 1)
                                    imageBrower(position, (ArrayList<String>) receiptlistpath);
                                else if (type == 2)
                                    imageBrower(position, (ArrayList<String>) piclistpath);
                                break;
                            case 3:// 删除

                                if (type == 1) {

                                    if (receiptlistpath != null && position < receiptlistpath.size()) {

                                        removeUploadPic(uploadreceiptlistpath, receiptlistpath.get(position));
                                        receiptlistpath.remove(position);
                                        receiptAdapter.setList(receiptlistpath);

                                    }

                                } else if (type == 2) {
                                    if (piclistpath != null && position < piclistpath.size()) {

                                        removeUploadPic(uploadpiclistpath, piclistpath.get(position));
                                        piclistpath.remove(position);
                                        picAdapter.setList(piclistpath);

                                    }

                                }
                                setFeedBackVisible();
                                break;
                            default:
                                break;
                        }
                    }
                });

    }

    public void modifyUploadPic(List<String> tlist, String originStr, String TargetStr) {

        List<String> list = tlist;

        findStrNeedModify:
        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).equals(originStr)) {

                list.set(i, TargetStr);
                break findStrNeedModify;

            }

        }

    }

    public void removeUploadPic(List<String> tlist, String TargetStr) {

        List<String> list = tlist;

        Iterator<String> it = list.iterator();
        findStrNeedDelete:
        while (it.hasNext()) {
            String x = it.next();
            if (x.equals(TargetStr)) {
                it.remove();
                break findStrNeedDelete;
            }
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mStoreDetail != null)
            outState.putString("rstoredetail", GsonTool.getInstance().toJson(mStoreDetail));

        if (TextUtils.isEmpty(taskid)) {
            outState.putString("rtaskid", taskid);

        }

        if (TextUtils.isEmpty(corpid)) {

            outState.putString("rcorpid", corpid);
        }

    }


    public void setGetPostionBtnState(boolean res) {

        if (res) {

          //  tvGetpostion.setBackground(this.getResources().getDrawable(R.drawable.rectangle_background_appcolor_full));
          //  tvGetpostion.setTextColor(this.getResources().getColor(R.color.halfwhite));
            tvGetpostion.setText("已获取位置");
            tvGetpostion.setVisibility(View.GONE);
        } else {

         //   tvGetpostion.setBackground(this.getResources().getDrawable(R.drawable.rectangle_background_appcolor_full));
        //    tvGetpostion.setTextColor(this.getResources().getColor(R.color.white));
            tvGetpostion.setText("获取位置点");
        }
    }


    //@Subscribe(threadMode = ThreadMode.MAIN)
    public void GetLocation() {


        if (SPHelper2.getInstance(mContext).readMarkStoreAddress(mStoreDetail.taskId + mStoreDetail.waybill) != null) {


            //JumpToUploadPayment();

            setGetPostionBtnState(true);
            return;


        }


        if (!GPSUtils.isOPen(this)) {

            LocationRemindDialog dialog = new LocationRemindDialog(this, "定位服务未开启",
                    "请在设置中开启 \"定位\" 服务", "设置", "知道了", new LocationRemindDialog.IClickListener() {
                @Override
                public void OnCancel() {
                    GPSUtils.openGPSSettings(TaskPointDetailActivity.this);
                }

                @Override
                public void OnSure() {
                    //  onLocation();
                }
            });


            dialog.show();

            return;

        }


        if (mStoreDetail != null) {

            MtqDeliStoreDetail storeDetail = mStoreDetail;
            // mStoreDetail = storeDetail;
            //  if (SPHelper2.getInstance(mContext).readMarkStoreAddress(storeDetail.waybill + storeDetail.storeid) == null) {


            // ShowAskDialog(storeDetail);


            showProgressBar2();
            isNeedUpload = true;
            onLocation();

        }
    }

    TaskAskPopUpDialog mPopUpDialog;

    public void ShowLocationAskDialog(final MtqDeliStoreDetail storeDetail, final AddressBean bean, boolean isOverRange) {


        mPopUpDialog = new TaskAskPopUpDialog(this);

        mPopUpDialog.show();

        mPopUpDialog.setDialogType(10);


        if (isOverRange)
            mPopUpDialog.tvTitle.setText("您是否已在此门店 (" + storeDetail.storecode + ") ?");
        else
            mPopUpDialog.tvTitle.setText("当前位置已在门店 (" + storeDetail.storecode + ") 附近,是否上传?");
        // final MtqDeliStoreDetail detail = mS

        // 选择优先运货到 " + detail.storename + " ,是否继续?"

        mPopUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopUpDialog.tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                mPopUpDialog.dismiss();


            }
        });
        mPopUpDialog.tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {



                mPopUpDialog.dismiss();

//                showProgressBar();
//
//                onLocation();


                //   tvKcode.setText(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey));

                String kcode = "";
                if (TaskUtils.isStorePosUnknown(storeDetail)) {

                    kcode = "无位置";
                    tvKcode.setTextColor(getResources().getColor(R.color.red));
                    tvKcodehint.setTextColor(getResources().getColor(R.color.red));



                } else {
                    kcode = SetStrSafety(FreightLogicTool
                            .splitKcode(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey)));
                    tvKcodehint.setTextColor(getResources().getColor(R.color.text_normal_color));

                    tvKcode.setTextColor(getResources().getColor(R.color.text_normal_color));
                }

                // : "+SetStrSafety(FreightLogicTool.splitKcode(bean.kcode))+")")

                tvKcode.setText(FreightLogicTool.getKcodeGetPostionFail(kcode, " (已获取位置)"));
                tvKcode.getPaint().setFlags(0);
                tvKcode.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);


                Toast.makeText(TaskPointDetailActivity.this, "已获取位置", Toast.LENGTH_SHORT).show();

                SPHelper2.getInstance(TaskPointDetailActivity.this).writeMarkStoreAddress(mStoreDetail.taskId + mStoreDetail.waybill, currentLocationBean);


                setGetPostionBtnState(true);

                GetPositionAndCheckUpload(mStoreDetail, currentLocationBean);

                if (TaskUtils.isStorePosUnknown(storeDetail)) {

                    EventBus.getDefault().post(new RefreshAdapterEvent(mStoreDetail.waybill, mStoreDetail.taskId));

                }


            }
        });

    }

    //定位完后，检查是否需要反地理，并检查网络是否上传
    private void GetPositionAndCheckUpload(final MtqDeliStoreDetail storeDetail, final AddressBean bean) {


        NetWorkUtils.isNetworkConnected(this, new NetWorkUtils.OnNetworkListener() {
            @Override
            public void isAvailable(boolean isAvailable) {


                if (isFinishing())
                    return;


                //hideProgressBar();

                if (isAvailable) {
                    OfflineLocationBean beans = new OfflineLocationBean(storeDetail.taskId + storeDetail.waybill, storeDetail, bean, CldKDeviceAPI.getSvrTime());

                    //   OfflineLocationTool.GeoCodeAndUpload(FreightPointActivity.this, bean, storeDetail);

                    OfflineLocationTool.CheckAddrAndUpload(TaskPointDetailActivity.this, beans, new OnBooleanListner() {
                        @Override
                        public void onResult(boolean res) {

                        }
                    }, true);
                    //  JumpToUploadPayment();
//                    OfflineLocationBean beans = new OfflineLocationBean(storeDetail.taskId + storeDetail.waybill, storeDetail, bean);
//                    OrmLiteApi.getInstance().save(beans);


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

                    OfflineLocationBean beans = new OfflineLocationBean(storeDetail.taskId + storeDetail.waybill, storeDetail, bean, CldKDeviceAPI.getSvrTime());
                    OrmLiteApi.getInstance().save(beans);

                    //  JumpToUploadPayment();
                }


            }
        });


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
                startLocation();
            } else {
                startLocation();
            }
        } else {
            startLocation();
        }
    }


    private boolean isNeedUpload = false;


    private void startLocation() {


        //  showProgressBar();

        if (!isGetLocationFinish)
            showProgressBar2();
        location();

    }

    private void location() {
//        MLog.e("test", " ++++ location =  start location ");
//
//        	if (!(AccountAPI.getInstance().isLogined())) {
//
//               // hideProgressBar();
//
//                ThreadPoolTool.getInstance().execute(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                        LocationAPI.getInstance().location(LocationAPI.MTQLocationMode.MIXED, 2000, TaskPointDetailActivity.this).setLinster(TaskPointDetailActivity.this);
//
//
//
//                    }
//                });
//            }else {
//
//                //hideProgressBar();
//
//                ThreadPoolTool.getInstance().execute(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//
//                    }
//                });
//            }

        LocationAPI.getInstance().location(LocationAPI.MTQLocationMode.MIXED, 2000, TaskPointDetailActivity.this).setLinster(TaskPointDetailActivity.this);



        isCancel = false;
        new Handler(TaskPointDetailActivity.this.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isCancel){

                            hideProgressBar2();


                }

            }
        },10000);

    }


    boolean isCancel = true;

    @Override
    public void onReceiveLocation(final CldLocation location) {
        // WaitingProgressTool.closeshowProgress();
        isCancel = true;
        if (isFinishing()) {
         return;
        }

//        TaskPointDetailActivity.this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                hideProgressBar2();
                if (!isFinishing()) {
                    MLog.e("yyh", "location = " + location.getErrCode() + " " + location.getLatitude() + location.getLongitude() + location.getProvince()
                            + location.getCity() + location.getDistrict() + location.getAddress() + location.getAdCode());

                    LocationAPI.getInstance().stop();



                    onLocateSuccess(location);

                }
//            }
//        });

    }


    //定位

    protected void onLocateSuccess(CldLocation location) {


        if (isFinishing())
            return;


        String address = "";
        if (location != null && TaskUtils.isNotFailLocation(location)) {

            isGetLocationFinish = true;

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


            if (isNeedUpload) {

                isNeedUpload = false;

                if (SPHelper2.getInstance(this).readMarkStoreAddress(mStoreDetail.taskId + mStoreDetail.waybill) != null)
                    return;


                if (TaskUtils.isStorePosUnknown(mStoreDetail)) {

//                    tvKcode.setText(SetStrSafety(FreightLogicTool.splitKcode(currentLocationBean.kcode)));
//
//                    HPWPoint point = CldCoordUtil.kcodeToCLD(currentLocationBean.kcode);
//
//
//                    mStoreDetail.storex = point.x;
//                    mStoreDetail.storey = point.y;
//
//                    tvKcodehint.setTextColor(getResources().getColor(R.color.text_normal_color));
//                    tvKcode.setTextColor(getResources().getColor(R.color.text_normal_color));
//
//                    tvKcode.getPaint().setFlags(0);
//                    tvKcode.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
//                    SPHelper2.getInstance(this).writeMarkStoreAddress(mStoreDetail.taskId + mStoreDetail.waybill, currentLocationBean);
//
//                    EventBus.getDefault().post(new RefreshAdapterEvent(mStoreDetail.waybill, mStoreDetail.taskId));
//
//                    Toast.makeText(TaskPointDetailActivity.this, "已获取位置", Toast.LENGTH_SHORT).show();
//
//                    setGetPostionBtnState(true);
//
//                    GetPositionAndCheckUpload(mStoreDetail, currentLocationBean);


                    ShowLocationAskDialog(mStoreDetail, currentLocationBean, true);

                } else {


                    HPDefine.HPWPoint nextPoint = new HPDefine.HPWPoint();
                    nextPoint.x = mStoreDetail.storex;
                    nextPoint.y = mStoreDetail.storey;
                    LatLng p1LL =  new LatLng(nextPoint.y,nextPoint.x);
                            //CldCoordUtil.k(CldCoordUtil.cldToKCode(nextPoint));

                    LatLng p2LL = new LatLng();
                    p2LL.latitude = currentLocationBean.latitude;
                    p2LL.longitude = currentLocationBean.longitude;

                    double dis = DistanceUtil.getDistance(p2LL, p1LL);

                    int range = GeneralSPHelper.getInstance(context).getCompanyWantRange(mStoreDetail.corpId);


                    ShowLocationAskDialog(mStoreDetail, currentLocationBean, dis > range);


                }
            } else {


                if (mStoreDetail.storestatus == 2)
                    return;


                HPDefine.HPWPoint nextPoint = new HPDefine.HPWPoint();
                nextPoint.x = mStoreDetail.storex;
                nextPoint.y = mStoreDetail.storey;
                LatLng p1LL = new LatLng(nextPoint.y,nextPoint.x);
                        //CldCoordUtil.gcjToCLDEx(CldCoordUtil.cldToGCJ(nextPoint));

                LatLng p2LL = new LatLng();
                p2LL.latitude = currentLocationBean.latitude;
                p2LL.longitude = currentLocationBean.longitude;

                double dis = DistanceUtil.getDistance(p2LL, p1LL);

                int range = GeneralSPHelper.getInstance(context).getCompanyWantRange(mStoreDetail.corpId);

                if (dis > range) {

                    tvGetpostion.setVisibility(View.VISIBLE);


                    tvKcode.setTextColor(getResources().getColor(R.color.red));
                    //  tvKcodehint.setTextColor(getResources().getColor(R.color.red));
//                    CharSequence str = SetStrSafety(FreightLogicTool
//                            .splitKcode(currentLocationBean.kcode));
                    CharSequence str = SetStrSafety(FreightLogicTool
                            .splitKcode(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey)));
                    //MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey))

//                    NumberFormat nf = NumberFormat.getNumberInstance();
//                    nf.setMaximumFractionDigits(2);


                    String distance = "";

                    if(dis<1000){


                        distance = " (差距" + (int)dis + "米)";

                    }else {

                        BigDecimal bg = new BigDecimal(dis / 1000.0);
                        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                       distance = " (差距" + getPrettyNumber(f1) + "公里)";
                    }
                    tvKcodehint.setTextColor(getResources().getColor(R.color.red));
                    tvKcode.setText(str + distance);
                    tvKcode.getPaint().setFlags(0);
                    tvKcode.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
                    tvGetpostion.setVisibility(View.VISIBLE);

                } else {

                    String kcode = MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey);

                    tvKcode.setText(SetStrSafety(FreightLogicTool
                            .splitKcode(kcode)));
                    tvKcodehint.setTextColor(getResources().getColor(R.color.text_normal_color));
                    tvKcode.setTextColor(getResources().getColor(R.color.text_normal_color));

                    tvKcode.getPaint().setFlags(0);
                    tvKcode.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);

                    tvGetpostion.setVisibility(View.GONE);

//                    if (mStoreDetail.storestatus == 1) {
//
//                        tvKcode.setTextColor(getResources().getColor(R.color.app_color));
//                        tvKcode.setText("地图选点");
//
//                        tvKcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//                    } else {
//
//                        tvKcode.setTextColor(getResources().getColor(R.color.red));
//                        tvKcode.setText("缺少位置信息");
//                        tvKcode.getPaint().setFlags(0);
//
//                    }

                }

            }

        } else {

            isNeedUpload = false;

            tvGetpostion.setVisibility(View.GONE);

            tvKcodehint.setTextColor(getResources().getColor(R.color.text_normal_color));
            //   tvKcode.setText(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey));

            String kcode = SetStrSafety(FreightLogicTool
                    .splitKcode(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey)));

            tvKcode.setTextColor(getResources().getColor(R.color.text_normal_color));


            if(TaskUtils.isStorePosUnknown(mStoreDetail)){

                kcode = "无位置";
            }


            tvKcode.setText(FreightLogicTool.getKcodeGetPostionFail(kcode, " (获取位置失败)"));
            tvKcode.getPaint().setFlags(0);
            tvKcode.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            Toast.makeText(TaskPointDetailActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
            // address = mUserInfo.getAddress();

            // JumpToUploadPayment();
        }

    }

    public static String getPrettyNumber(double number) {
        return BigDecimal.valueOf(number)
                .stripTrailingZeros().toPlainString();
    }


}