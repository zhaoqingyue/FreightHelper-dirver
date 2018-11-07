package com.yunbaba.freighthelper.ui.activity.task;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.location.CldLocation;
import com.cld.location.ICldLocationListener;
import com.cld.mapapi.model.LatLng;
import com.cld.mapapi.search.exception.IllegalSearchArgumentException;
import com.cld.mapapi.search.geocode.GeoCodeResult;
import com.cld.mapapi.search.geocode.GeoCoder;
import com.cld.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.cld.mapapi.search.geocode.ReverseGeoCodeOption;
import com.cld.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cld.nv.location.CldCoordUtil;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.api.map.LocationAPI;
import com.yunbaba.api.map.LocationAPI.MTQLocationMode;
import com.yunbaba.api.map.MapViewAPI;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.WaybillManager;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.api.trunk.bean.UpdateTaskPointStatusResult;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.AddressBean;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointStatusRefreshEvent;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointUpdateEvent;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.ui.activity.mapselect.MapSelectPointActivity;
import com.yunbaba.freighthelper.ui.adapter.GoodListAdapter;
import com.yunbaba.freighthelper.ui.adapter.PicGridViewAdapter;
import com.yunbaba.freighthelper.ui.customview.NoScrollGridView;
import com.yunbaba.freighthelper.ui.customview.SimpleIndexSelectDialog;
import com.yunbaba.freighthelper.ui.customview.SimpleIndexSelectDialog.OnSimpleIndexSelectCallBack;
import com.yunbaba.freighthelper.utils.DigitsUtil;
import com.yunbaba.freighthelper.utils.ErrCodeUtil;
import com.yunbaba.freighthelper.utils.FileUtils;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.ImageTools;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MyDebugTool;
import com.yunbaba.freighthelper.utils.PermissionUtil;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.TaskStatus;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliReceiptParm;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
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
import hmi.packages.HPDefine.HPWPoint;


public class UploadPaymentActivity extends BaseButterKnifeActivity implements ICldLocationListener, OnGetGeoCoderResultListener {

    @BindView(R.id.iv_titleleft)
    ImageView ivTitleleft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_titleright)
    ImageView ivTitleright;
    @BindView(R.id.tv_goodinfo)
    TextView tvGoodinfo;
    @BindView(R.id.tv_checkscanrecord)
    TextView tvCheckscanrecord;
    @BindView(R.id.lv_goodinfo)
    NoScrollGridView lvGoodinfo;
    @BindView(R.id.pll_goodinfo)
    PercentLinearLayout pllGoodinfo;
    @BindView(R.id.tv_payway)
    TextView tvPayway;
    @BindView(R.id.pll_payway)
    PercentLinearLayout pllPayway;
    @BindView(R.id.tv_shouldreceive)
    TextView tvShouldreceive;
    @BindView(R.id.et_realreceive)
    EditText etRealreceive;
    @BindView(R.id.pll_feeinfo)
    PercentLinearLayout pllFeeinfo;
    @BindView(R.id.tv_refundamount)
    TextView tvRefundamount;
    @BindView(R.id.tv_refundreason)
    TextView tvRefundreason;
    @BindView(R.id.pll_refundreason)
    PercentLinearLayout pllRefundreason;
    @BindView(R.id.gv_goodreceipt)
    NoScrollGridView gvGoodreceipt;
    @BindView(R.id.gv_goodpic)
    NoScrollGridView gvGoodpic;
    @BindView(R.id.pll_pic)
    PercentLinearLayout pllPic;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.pll_remark)
    PercentLinearLayout pllRemark;
    @BindView(R.id.tv_starttrans)
    TextView tvStarttrans;
    @BindView(R.id.pll_starttransporting_btn)
    PercentLinearLayout pllStarttransportingBtn;

    @BindView(R.id.tv_hint_payinfo)
    TextView tvHintPayinfo;
    @BindView(R.id.tv_hint_returninfo)
    TextView tvHintReturninfo;

    @BindView(R.id.tv_hint_recpic)
    TextView tvHintRecpic;

    @BindView(R.id.pll_recpic)
    PercentRelativeLayout pllRecpic;

    @BindView(R.id.pll_returninfo)
    PercentLinearLayout pllReturnInfo;

    @BindView(R.id.tv_recnumhint)
    TextView tvRecnumhint;

    @BindView(R.id.tv_picnumhint)
    TextView tvPicnumhint;

    @BindView(R.id.tv_wordcount)
    TextView tvWordcount;

    @BindView(R.id.tv_inputhint)
    TextView tvInputHint;

    @BindView(R.id.tv_selectadr)
    TextView tvSelectadr;

    @BindView(R.id.sv_root)
    ScrollView svroot;

    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.pll_adrmark)
    PercentLinearLayout pll_adrmark;
    @BindView(R.id.iv_type)
    ImageView iv_type;
    @BindView(R.id.tv_pointname)
    TextView tv_pointname;
    @BindView(R.id.tv_kcode)
    TextView tv_kcode;

    MtqDeliStoreDetail mStoreDetail;
    MtqDeliOrderDetail mOrderDetail;
    String corpid;
    String taskid;
    GeoCoder mGeoCoder;
    GoodListAdapter goodAdapter;

    private static String[] PayWayStr = new String[]{"现金", "票据", "刷卡", "欠单"};
    private static String[] RefundStr = new String[]{"无", "客户原因", "运输原因", "配货原因", "改期配送"};

    int PayWay = 0; // "现金","票据","刷卡","欠单"
    int RefundReason = 0; // 无,"客户原因","运输原因","配货原因","改期配送"

    // 回单
    public static final int GOOD_RECEIPT = 1;
    // 货物照片
    public static final int GOOD_PIC = 2;

    // 拍照
    public static final int IMAGE_CAPTURE = 2;
    // 从相册选择
    public static final int IMAGE_SELECT = 5;

    // 图片 九宫格适配器
    private PicGridViewAdapter receiptAdapter;
    private PicGridViewAdapter picAdapter;

    // 用于保存图片路径
    private List<String> receiptlistpath = new ArrayList<String>();

    // 用于保存图片路径
    private List<String> piclistpath = new ArrayList<String>();

    public static final int RequestCode_Address = 111;

    private AddressBean currentLocationBean;

    @Override
    public int getLayoutId() {

        return R.layout.activity_upload_payment_info;
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
        tvTitle.setText("完成");

        // ivTitleright.setImageResource(R.drawable.icon_message);
        ivTitleright.setVisibility(View.GONE);

        if (getIntent() == null || getIntent().getStringExtra("storedetail") == null) {

            finish();
        } else {

            try {
                String jsonstr = getIntent().getStringExtra("storedetail");

                mStoreDetail = GsonTool.getInstance().fromJson(jsonstr, MtqDeliStoreDetail.class);

                if (mStoreDetail == null)
                    finish();
            } catch (Exception e) {
                finish();
            }

            if (getIntent().hasExtra("orderdetail")) {

                try {
                    String jsonstr2 = getIntent().getStringExtra("orderdetail");

                    if (jsonstr2 != null && !TextUtils.isEmpty(jsonstr2))
                        mOrderDetail = GsonTool.getInstance().fromJson(jsonstr2, MtqDeliOrderDetail.class);
                } catch (Exception e) {
                    finish();

                }


                if (getIntent().hasExtra("corpid"))
                    corpid = getIntent().getStringExtra("corpid");

                if (getIntent().hasExtra("taskid"))
                    taskid = getIntent().getStringExtra("taskid");

                setData();
            } else {

                finish();

            }

        }

        EventBus.getDefault().register(this);


//        if (!GPSUtils.isOPen(this)) {
//
//            LocationRemindDialog dialog = new LocationRemindDialog(this, "定位服务未开启",
//                    "请在设置中开启 \"定位\" 服务", "设置", "知道了", new LocationRemindDialog.IClickListener() {
//                @Override
//                public void OnCancel() {
//                    GPSUtils.openGPSSettings(UploadPaymentActivity.this);
//                }
//
//                @Override
//                public void OnSure() {
//
//
//                    AddressBean bean = SPHelper2.getInstance(mContext).readMarkStoreAddress(mStoreDetail.taskId + mStoreDetail.waybill);
//
//
//                    if (bean != null) {
//
//                        onLocateSuccess(bean);
//
//                    } else {
//
//                        onLocation();
//
//                    }
//                //    onLocation();
//
//                }
//            });
//
//
//            dialog.show();
//
//        }


    }

    private void setData() {

        //MLog.e("getTaskStatusIsFinish", "res" + getTaskStatusIsFinish());

        if (getTaskStatusIsFinish() == 2) {
            tvStarttrans.setText("完成任务");
        } else {
            tvStarttrans.setText("完成并前往下一站");
        }

        goodAdapter = new GoodListAdapter(this, mOrderDetail);

        lvGoodinfo.setAdapter(goodAdapter);

        goodAdapter.notifyDataSetChanged();

        tvShouldreceive.setText("¥" + DigitsUtil.getPrettyNumber(mStoreDetail.total_amount));

        setUnderLine();

        etRealreceive.setText(DigitsUtil.getPrettyNumber(mStoreDetail.total_amount));

        etRealreceive.setSelection(etRealreceive.getText().length());

        etRealreceive.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                // MLog.e("check", "text change");


                if (!DigitsUtil.isNumber(s.toString())) {
                    // etRealreceive.setText("0");

                    s.clear();

                    // etRealreceive.removeTextChangedListener(this);
                    //
                    // etRealreceive.setText("0.00");
                    // tvRefundamount.setText(DigitsUtil.getPrettyNumber(mStoreDetail.total_amount));
                    // tvRefundreason.setTextColor(getResources().getColor(R.color.black_text));
                    //
                    // etRealreceive.addTextChangedListener(this);
                    //
                    // etRealreceive.setSelection(etRealreceive.getText().toString().length());

                } else {

                    String temp = s.toString();
                    int posDot = temp.indexOf(".");
                    if (posDot > 0) {

                        if (temp.length() - posDot - 1 > 2) {
                            s.delete(posDot + 3, posDot + 4);
                        }
                    }

                    try {

                        float receive = TextUtils.isEmpty(etRealreceive.getText()) ? 0
                                : Float.valueOf(etRealreceive.getText().toString());
                        // mStoreDetail.total_amount - receive;

                        BigDecimal b1 = new BigDecimal(Float.toString(receive));
                        BigDecimal b2 = new BigDecimal(Float.toString(mStoreDetail.total_amount));

                        float refund = b2.subtract(b1).floatValue();

                        // MLog.e("check", mStoreDetail.total_amount + "receive"
                        // + receive + " refund" + refund);

                        if (refund < 0 || receive > mStoreDetail.total_amount) {

                            // s.clear();
                            tvRefundamount.setText("0.00");
                            tvRefundreason.setTextColor(getResources().getColor(R.color.text_hint));

                        } else if (refund == 0) {

                            tvRefundamount.setText("0.00");
                            tvRefundreason.setTextColor(getResources().getColor(R.color.text_hint));
                        } else {

                            // DigitsUtil.getPrettyNumber(refund)

                            tvRefundamount.setText(DigitsUtil.getPrettyNumber(refund));
                            tvRefundreason.setTextColor(getResources().getColor(R.color.black_text));
                        }

                    } catch (Exception e) {
                        // etRealreceive.setText("");
                        s.clear();
                    }

                    // etRealreceive.setSelection(etRealreceive.getText().toString().length());
                }

                if (TextUtils.isEmpty(etRealreceive.getText())) {
                    tvInputHint.setVisibility(View.VISIBLE);
                } else {
                    tvInputHint.setVisibility(View.GONE);
                }

            }
        });

        etRealreceive.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {

                    if (TextUtils.isEmpty(etRealreceive.getText())) {
                        etRealreceive.setText("0.00");
                        // tvRefundamount.setText(DigitsUtil.getPrettyNumber(mStoreDetail.total_amount));
                        // tvRefundreason.setTextColor(getResources().getColor(R.color.black_text));

                    } else {

                        float receive = TextUtils.isEmpty(etRealreceive.getText()) ? 0
                                : Float.valueOf(etRealreceive.getText().toString());
                        // mStoreDetail.total_amount - receive;

                        BigDecimal b1 = new BigDecimal(Float.toString(receive));
                        BigDecimal b2 = new BigDecimal(Float.toString(mStoreDetail.total_amount));

                        float refund = b2.subtract(b1).floatValue();
                        if (refund < 0 || receive > mStoreDetail.total_amount) {
                            Toast.makeText(UploadPaymentActivity.this, "实收金额大于应收金额,请核对", Toast.LENGTH_SHORT).show();

                            etRealreceive.setText(DigitsUtil.getPrettyNumber(mStoreDetail.total_amount));

                        } else {

                            etRealreceive.setText(DigitsUtil.getPrettyNumber(etRealreceive.getText().toString()));
                        }
                    }

                    etRealreceive.setSelection(etRealreceive.getText().toString().length());
                }
            }
        });

        receiptAdapter = new PicGridViewAdapter(this, receiptlistpath, mOrderDetail.can_receipt_nums, 2, corpid,
                taskid);
        picAdapter = new PicGridViewAdapter(this, piclistpath, mOrderDetail.can_photo_nums, 2, corpid, taskid);

        gvGoodpic.setAdapter(picAdapter);
        gvGoodreceipt.setAdapter(receiptAdapter);

        receiptAdapter.notifyDataSetChanged();
        picAdapter.notifyDataSetChanged();

        gvGoodpic.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == piclistpath.size())
                    selectPic(2);
                else {

                    modifyPic(2, position);

                }

            }
        });

        gvGoodreceipt.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == receiptlistpath.size())
                    selectPic(1);
                else {
                    // imageBrower(position, (ArrayList<String>)
                    // receiptlistpath);

                    modifyPic(1, position);
                }
            }
        });

        if (mStoreDetail.total_amount == 0) {

            etRealreceive.setEnabled(false);

        } else {

            etRealreceive.setEnabled(true);

        }

        if (mStoreDetail.is_receipt == 0) {
            tvHintRecpic.setVisibility(View.GONE);
            pllRecpic.setVisibility(View.GONE);
        } else {
            tvHintRecpic.setVisibility(View.VISIBLE);
            pllRecpic.setVisibility(View.VISIBLE);
        }

        if (mStoreDetail.pay_mode == 0) {

            tvHintPayinfo.setVisibility(View.GONE);
            pllFeeinfo.setVisibility(View.GONE);
            tvHintReturninfo.setVisibility(View.GONE);
            pllReturnInfo.setVisibility(View.GONE);
            // pllRemark.setVisibility(View.GONE);
        } else {
            tvHintPayinfo.setVisibility(View.VISIBLE);
            pllFeeinfo.setVisibility(View.VISIBLE);
            tvHintReturninfo.setVisibility(View.VISIBLE);
            pllReturnInfo.setVisibility(View.VISIBLE);

        }

        pllRemark.setVisibility(View.VISIBLE);

        RefreshHint();

        etRemark.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            // private int selectionStart;
            // private int selectionEnd;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
                if (temp == null)
                    temp = "";

            }

            public void afterTextChanged(Editable s) {

                tvWordcount.setText(temp.length() + "/200");

            }
        });

        if (mStoreDetail.isUnknownAddress || TaskUtils.isStorePosUnknown(mStoreDetail)) {

//            pll_adrmark.setVisibility(View.VISIBLE);

            FreightLogicTool.setImgbyOptype(mStoreDetail.optype, iv_type);

            tv_pointname.setText(SetStrSafety(mStoreDetail.storename));

            if (mStoreDetail.isUnknownAddress) {

                if (!TaskUtils.isStorePosUnknown(mStoreDetail)) {
                    tvAddress.setText((mStoreDetail.storeaddr).replaceAll("\\s*", ""));
                } else
                    tvAddress.setText(
                            SetStrSafety((mStoreDetail.regionname + mStoreDetail.storeaddr).replaceAll("\\s*", "")));
            } else
                tvAddress.setText(
                        SetStrSafety((mStoreDetail.regionname + mStoreDetail.storeaddr).replaceAll("\\s*", "")));

            // tvAddress.setText(SetStrSafety((mStoreDetail.regionname +
            // mStoreDetail.storeaddr).replaceAll("\\s*", "")));

            if (TaskUtils.isStorePosUnknown(mStoreDetail))

                tv_kcode.setText("");
            else
                tv_kcode.setText(SetStrSafety(FreightLogicTool
                        .splitKcode(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey))));

            tvSelectadr.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            tvSelectadr.setVisibility(View.GONE);

        } else {

//            pll_adrmark.setVisibility(View.GONE);

        }

        //getCurrentPositionAddr();

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

        SimpleIndexSelectDialog.ShowSelectDialog(UploadPaymentActivity.this, new String[]{"拍照", "图库"}, true,
                new OnSimpleIndexSelectCallBack() {

                    @Override
                    public void OnIndexSelect(int index, String select) {

                        switch (index) {
                            case 0:// 拍照

                                if (PermissionUtil.isNeedPermission(UploadPaymentActivity.this,
                                        Manifest.permission.CAMERA)) {

                                    Toast.makeText(UploadPaymentActivity.this, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
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

    /**
     * 更改，删除
     */
    public void modifyPic(final int type, final int position) {

        if (PermissionUtil.isNeedPermissionForStorage(this)) {

            Toast.makeText(this, "请打开储存空间权限", Toast.LENGTH_SHORT).show();
            return;

        }

        SimpleIndexSelectDialog.ShowSelectDialog(this, new String[]{"更改-拍照", "更改-图库", "查看", "删除"}, true,
                new OnSimpleIndexSelectCallBack() {

                    @Override
                    public void OnIndexSelect(int index, String select) {

                        switch (index) {
                            case 0:// 拍照

                                if (PermissionUtil.isNeedPermission(UploadPaymentActivity.this,
                                        Manifest.permission.CAMERA)) {

                                    Toast.makeText(UploadPaymentActivity.this, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
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

                                        receiptlistpath.remove(position);
                                        receiptAdapter.setList(receiptlistpath);

                                    }
                                } else if (type == 2) {
                                    if (piclistpath != null && position < piclistpath.size()) {

                                        piclistpath.remove(position);
                                        picAdapter.setList(piclistpath);

                                    }

                                }

                                break;
                            default:
                                break;
                        }
                    }
                });

    }

    public void setUnderLine() {

        tvCheckscanrecord.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

    }

    @OnClick({R.id.iv_titleleft, R.id.iv_titleright, R.id.pll_payway, R.id.pll_refundreason, R.id.tv_starttrans,
            R.id.tv_checkscanrecord, R.id.tv_selectadr})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_titleleft:
                if (isUpdateStore)
                    setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
                finish();
                break;
            case R.id.iv_titleright:
                // Intent intent3 = new Intent(this, MsgActivity.class);
                // startActivity(intent3);

                break;
            case R.id.tv_selectadr:

                // Intent intent3 = new Intent(this, MsgActivity.class);
                // startActivity(intent3);

                if (!PermissionUtil.isNeedPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, 108)) {
                    Intent it = new Intent(this, MapSelectPointActivity.class);
                    startActivityForResult(it, RequestCode_Address);
                }

                break;
            case R.id.pll_payway:
                if (mStoreDetail.total_amount != 0) {

                    SimpleIndexSelectDialog.ShowSelectDialog(this, new String[]{"现金", "票据", "刷卡", "欠单"}, true,
                            new OnSimpleIndexSelectCallBack() {

                                @Override
                                public void OnIndexSelect(int index, String select) {

                                    PayWay = index;
                                    tvPayway.setText(select);
                                }
                            });
                }

                break;
            case R.id.pll_refundreason:

                if (mStoreDetail.total_amount != 0 && tvRefundamount.getText() != null
                        && DigitsUtil.isNumber(tvRefundamount.getText().toString())
                        && Float.valueOf(tvRefundamount.getText().toString()) > 0) {

                    SimpleIndexSelectDialog.ShowSelectDialog(this, new String[]{"客户原因", "运输原因", "配货原因", "改期配送"}, true,
                            new OnSimpleIndexSelectCallBack() {

                                @Override
                                public void OnIndexSelect(int index, String select) {

                                    RefundReason = index + 1;
                                    tvRefundreason.setText(select);
                                    tvRefundreason.setTextColor(getResources().getColor(R.color.black_text));
                                }
                            });

                }

                break;
            case R.id.tv_starttrans:

                if (getTaskStatusIsFinish() != 2) {

                    AppStatApi.statOnEventWithLoginName(getApplicationContext(), AppStatApi.BD_STAT_ROUTING_POINT_NEXT_CLICK);
                }

                updateStoreStatus();

                break;
            case R.id.tv_checkscanrecord:

                Intent intent = new Intent(this, WayBillNumberActivity.class);
                // String str = GsonTool.getInstance().toJson(mStoreDetail);
                // intent.putExtra("storedetail", str);
                //
                // String str2 = GsonTool.getInstance().toJson(mOrderDetail);
                // intent.putExtra("orderdetail", str2);
                WaybillManager.getInstance().setmStoreDetail(mStoreDetail);
                WaybillManager.getInstance().setmOrderDetail(mOrderDetail);

                startActivity(intent);
                break;
        }

    }

    /***
     * 上传收款信息请求
     */

    public void UploadPaymentInfo() {

        if (mStoreDetail.pay_mode != 0) {

            if (TextUtils.isEmpty(etRealreceive.getText())) {

                Toast.makeText(this, "请输入实收金额", Toast.LENGTH_SHORT).show();
                // EventBus.getDefault().post(new FreightPointUpdateEvent(0));
                return;
            }

        }

        float receive = 0f;
        float refund = 0f;

        if (mStoreDetail.pay_mode != 0) {

            receive = TextUtils.isEmpty(etRealreceive.getText()) ? 0
                    : Float.valueOf(etRealreceive.getText().toString());
            // refund = mStoreDetail.total_amount - receive;

            BigDecimal b1 = new BigDecimal(Float.toString(receive));
            BigDecimal b2 = new BigDecimal(Float.toString(mStoreDetail.total_amount));

            refund = b2.subtract(b1).floatValue();

        }

        if (refund < 0) {
            Toast.makeText(this, "退款金额不能低于0", Toast.LENGTH_SHORT).show();
            // EventBus.getDefault().post(new FreightPointUpdateEvent(0));
            return;
        }

        WaitingProgressTool.showProgress(this);

        CldDeliReceiptParm param = new CldDeliReceiptParm();

        param.corpid = corpid;
        param.taskid = taskid;
        param.storeid = mStoreDetail.storeid;
        //param.cust_orderid = mStoreDetail.cust_orderid;
        param.pay_method = PayWayStr[PayWay];
        param.return_desc = RefundStr[RefundReason];
        param.real_amount = receive;
        param.return_amount = refund;
        param.waybill = mStoreDetail.waybill;

        if (etRemark.getText() != null && !TextUtils.isEmpty(etRemark.getText()))
            param.pay_remark = etRemark.getText().toString();

        param.uploadPng = new byte[3][];

        for (int i = 0; i < receiptlistpath.size(); i++) {

            try {
                param.uploadPng[i] = FileUtils.toByteArray(receiptlistpath.get(i));
                // ImageTools.getPhotoByteArray(receiptlistpath.get(i));
                //

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        // DebugTool.saveFile2(Base64.encodeToString(param.uploadPng[0],
        // Base64.DEFAULT));

        DeliveryApi.getInstance().UpLoadPayInfo(UploadPaymentActivity.this,param, new OnResponseResult<Integer>() {

            @Override
            public void OnResult(Integer result) {

                if (isFinishing())
                    return;
                if (result != 0) {
                    Toast.makeText(UploadPaymentActivity.this, "操作失败，请检查网络。", Toast.LENGTH_SHORT).show();
                    // EventBus.getDefault().post(new
                    // FreightPointUpdateEvent(0));
                    WaitingProgressTool.closeshowProgress();
                    // finish();
                } else {

                    if (piclistpath.size() == 0) {
                        // Toast.makeText(UploadPaymentActivity.this, "运货完成",
                        // Toast.LENGTH_SHORT).show();
                        WaitingProgressTool.closeshowProgress();

                        UploadStoreAddr();

                    } else {
                        Toast.makeText(UploadPaymentActivity.this, "正在上传货物照片", Toast.LENGTH_SHORT).show();
                        UploadPic(0);
                    }

                }
            }

            @Override
            public void OnError(int ErrCode) {
                if (isFinishing())
                    return;

                if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                    // Toast.makeText(TaskPointDetailActivity.this,
                    // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                } else {

                    if (ErrCodeUtil.isNetErrCode(ErrCode)) {
                        Toast.makeText(UploadPaymentActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UploadPaymentActivity.this, R.string.request_fail, Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(UploadPaymentActivity.this, "操作失败，请检查网络。", Toast.LENGTH_SHORT).show();
                }
                WaitingProgressTool.closeshowProgress();
            }

            @Override
            public void OnGetTag(String Reqtag) {


            }
        });

        // DeliveryApi.getInstance().UpLoadPayInfo(uploadPara, callBack);

    }

    public void UploadPic(final int picIndex) {

        WaitingProgressTool.showProgress(this);

        if (picIndex >= piclistpath.size()) {
            WaitingProgressTool.closeshowProgress();
            Toast.makeText(UploadPaymentActivity.this, "货物图片上传完成", Toast.LENGTH_SHORT).show();

            UploadStoreAddr();

            return;
        }

        String base64_pic = null;
        try {
            base64_pic = Base64.encodeToString(FileUtils.toByteArray(piclistpath.get(picIndex)), Base64.DEFAULT);
            MyDebugTool.saveFile(base64_pic);
        } catch (IOException e) {

            e.printStackTrace();
        }

        DeliveryApi.getInstance().UpLoadDeliPicture(corpid, taskid, mStoreDetail.waybill, mStoreDetail.waybill,
                (long) (System.currentTimeMillis() / 1000), 1, (long) (System.currentTimeMillis() / 1000), 0, 0,
                base64_pic, new OnResponseResult<Integer>() {

                    @Override
                    public void OnResult(Integer result) {

                        // MLog.e("checkuploadpic", result + "");
                        if (isFinishing())
                            return;
                        if (result == 0)
                            UploadPic((picIndex + 1));
                        else {

                            Toast.makeText(UploadPaymentActivity.this,
                                    "货物图片上传失败，已上传" + (picIndex) + "张，未上传" + (piclistpath.size() - picIndex) + "张",
                                    Toast.LENGTH_SHORT).show();
                            WaitingProgressTool.closeshowProgress();
                            setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);

                            isNoLastToTransPoint();
                            if (isUpdateStore)
                                setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
                            finish();

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
                            Toast.makeText(UploadPaymentActivity.this,
                                    "货物图片上传失败，已上传" + (picIndex) + "张，未上传" + (piclistpath.size() - picIndex) + "张",
                                    Toast.LENGTH_SHORT).show();
                        }
                        WaitingProgressTool.closeshowProgress();
                        setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);

                        isNoLastToTransPoint();
                        if (isUpdateStore)
                            setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
                        finish();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


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
             //   mStoreDetail.isUnknownAddress = true;

                if (mStoreDetail.isUnknownAddress) {

                    if (bean != null) {
                        tvAddress.setText(SetStrSafety((bean.address).replaceAll("\\s*", "")));
                    }

                } else
                    tvAddress.setText(
                            SetStrSafety((mStoreDetail.regionname + mStoreDetail.storeaddr).replaceAll("\\s*", "")));

                tv_kcode.setText(SetStrSafety(FreightLogicTool
                        .splitKcode(MapViewAPI.getInstance().getKcode(mStoreDetail.storex, mStoreDetail.storey))));

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

                setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);

            }

            return;
        }

        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {

            String afterCompressPicPath;
            switch (requestCode) {
                case IMAGE_CAPTURE + GOOD_RECEIPT:// 拍照返回 回单

                    afterCompressPicPath = ImageTools.compress(UploadPaymentActivity.this,
                            MainApplication.getTmpCacheFilePath() + "/image.jpg");

                    if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                        receiptlistpath.add(afterCompressPicPath); // afterCompressPicPath
                        RefreshHint();
                        receiptAdapter.setList(receiptlistpath);
                    } else {
                        Toast.makeText(UploadPaymentActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case IMAGE_SELECT + GOOD_RECEIPT:// 选择照片返回 回单
                    // 照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {

                        afterCompressPicPath = null;

                        String fp = FileUtils.getRealFilePath(UploadPaymentActivity.this, originalUri);

                        afterCompressPicPath = ImageTools.compress(UploadPaymentActivity.this, fp);

                        if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {
                            receiptlistpath.add(afterCompressPicPath); //
                            RefreshHint();
                            receiptAdapter.setList(receiptlistpath);
                        } else {
                            Toast.makeText(UploadPaymentActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case IMAGE_CAPTURE + GOOD_PIC:// 拍照返回 货物照片

                    afterCompressPicPath = ImageTools.compress(UploadPaymentActivity.this,
                            MainApplication.getTmpCacheFilePath() + "/image.jpg");

                    if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                        piclistpath.add(afterCompressPicPath);
                        RefreshHint();
                        picAdapter.setList(piclistpath);
                    } else {
                        Toast.makeText(UploadPaymentActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case IMAGE_SELECT + GOOD_PIC:// 选择照片返回 货物照片
                    // 照片的原始资源地址
                    Uri originalUri2 = data.getData();
                    try {

                        afterCompressPicPath = null;

                        afterCompressPicPath = ImageTools.compress(UploadPaymentActivity.this,
                                FileUtils.getRealFilePath(UploadPaymentActivity.this, originalUri2));

                        if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                            piclistpath.add(afterCompressPicPath);
                            RefreshHint();
                            picAdapter.setList(piclistpath);
                        } else {
                            Toast.makeText(UploadPaymentActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    break;
            }

            int position = 0;

            if (requestCode < 1000)
                return;

            if (requestCode % 1000 == (IMAGE_CAPTURE + GOOD_RECEIPT)) {

                position = requestCode / 1000 - 1;

                if (position < 0)
                    return;

                afterCompressPicPath = ImageTools.compress(UploadPaymentActivity.this,
                        MainApplication.getTmpCacheFilePath() + "/image.jpg");

                if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                    receiptlistpath.set(position, afterCompressPicPath);
                    RefreshHint();
                    receiptAdapter.setList(receiptlistpath);
                } else {
                    Toast.makeText(UploadPaymentActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode % 1000 == (IMAGE_SELECT + GOOD_RECEIPT)) {

                position = requestCode / 1000 - 1;

                if (position < 0)
                    return;

                Uri originalUri = data.getData();
                try {

                    afterCompressPicPath = null;

                    String fp = FileUtils.getRealFilePath(UploadPaymentActivity.this, originalUri);

                    afterCompressPicPath = ImageTools.compress(UploadPaymentActivity.this, fp);

                    if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {
                        receiptlistpath.set(position, afterCompressPicPath); //
                        RefreshHint();
                        receiptAdapter.setList(receiptlistpath);
                    } else {
                        Toast.makeText(UploadPaymentActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode % 1000 == (IMAGE_CAPTURE + GOOD_PIC)) {

                position = requestCode / 1000 - 1;

                if (position < 0)
                    return;

                afterCompressPicPath = ImageTools.compress(UploadPaymentActivity.this,
                        MainApplication.getTmpCacheFilePath() + "/image.jpg");

                if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                    piclistpath.set(position, afterCompressPicPath);
                    RefreshHint();
                    picAdapter.setList(piclistpath);
                } else {
                    Toast.makeText(UploadPaymentActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode % 1000 == (IMAGE_SELECT + GOOD_PIC)) {

                position = requestCode / 1000 - 1;

                if (position < 0)
                    return;

                Uri originalUri2 = data.getData();
                try {

                    afterCompressPicPath = null;

                    afterCompressPicPath = ImageTools.compress(UploadPaymentActivity.this,
                            FileUtils.getRealFilePath(UploadPaymentActivity.this, originalUri2));

                    if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                        piclistpath.set(position, afterCompressPicPath);
                        RefreshHint();
                        picAdapter.setList(piclistpath);
                    } else {
                        Toast.makeText(UploadPaymentActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public void isNoLastToTransPoint() {

//        if (getTaskStatusIsFinish() == 2) {
//            Toast.makeText(UploadPaymentActivity.this, "运货完成", Toast.LENGTH_SHORT).show();
//
//            Intent intent = new Intent(UploadPaymentActivity.this, TaskPointDetailActivity.class);
//
//            if (mStoreDetail != null) {
//                // 添加storedetail
//                String str = GsonTool.getInstance().toJson(mStoreDetail);
//                intent.putExtra("storedetail", str);
//            }
//            // 添加taskid
//            intent.putExtra("taskid", taskid);
//            intent.putExtra("corpid", corpid);
//            intent.putExtra("isNeedFresh", true);
//
//            if (mOrderDetail != null) {
//
//                String str2 = GsonTool.getInstance().toJson(mOrderDetail);
//                intent.putExtra("orderdetail", str2);
//
//            }
//
//            startActivity(intent);
//
//            return;
//        }

        // MtqDeliTaskDetail detail =
        // TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);
        //
        // if (detail == null || detail.store == null || detail.store.size() ==
        // 0)
        // return;
        //
        // boolean isNeedToJump = false;
        //
        // over: for (MtqDeliStoreDetail store : detail.store) {
        //
        // if (store.storestatus != 2) {
        // isNeedToJump = true;
        // break over;
        // }
        //
        // }
        //
        // if (isNeedToJump) {

//        Intent intent = new Intent(UploadPaymentActivity.this, SelectTransPointActivity.class);
//        intent.putExtra("corpid", corpid);
//        intent.putExtra("taskid", taskid);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

        // }

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

    @Override
    protected void onResume() {

        super.onResume();
        // updateNews();
    }

    // private void updateNews() {
    // if (MsgManager.getInstance().hasUnReadMsg()) {
    // ivTitleright.setImageResource(R.drawable.msg_icon_news);
    // } else {
    // ivTitleright.setImageResource(R.drawable.msg_icon);
    // }
    // }

    public int getTaskStatusIsFinish() {

        // MLog.e("check taskid", "" + taskid);
        MtqDeliTaskDetail taskdetail = TaskOperator.getInstance().getTaskDetailDataFromDB(mStoreDetail.taskId);

        if (taskdetail == null || taskdetail.store == null) {
            MLog.e("check taskdetail", "taskdetail null");
            return -1;
        }
        MLog.e("check", "" + GsonTool.getInstance().toJson(taskdetail.store));

        boolean isfinish = true;

        over:
        for (MtqDeliStoreDetail store : taskdetail.store) {

            if (!store.waybill.equals(mStoreDetail.waybill) && store.storestatus != TaskStatus.DELIVERRED) {

                isfinish = false;
                break over;
            }

        }
        MLog.e("check isfinish", "" + isfinish);

        if (isfinish)
            return 2;
        else
            return -1;

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                if (etRealreceive != null)
                    etRealreceive.clearFocus();

                if (etRemark != null)
                    etRemark.clearFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                }
                //// if (edit != null)
                //// if (edit.getText().toString().trim().equals("")) {
                ////
                //// replyUserId = "";
                //// replyname = "";
                //// edit.setHint("");
                ////
                //// }
                //
                // }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * @Title: updateStoreStatus
     * @Description: 更新运货点的状态
     * @return: void
     */
    private void updateStoreStatus() {

        if (mStoreDetail.pay_mode != 0) {

            if (TextUtils.isEmpty(etRealreceive.getText())) {

                Toast.makeText(this, "请输入实收金额", Toast.LENGTH_SHORT).show();
                return;
            }

        }

        float receive = 0f;
        float refund = 0f;

        if (mStoreDetail.pay_mode != 0) {

            receive = TextUtils.isEmpty(etRealreceive.getText()) ? 0
                    : Float.valueOf(etRealreceive.getText().toString());
            // refund = mStoreDetail.total_amount - receive;

            BigDecimal b1 = new BigDecimal(Float.toString(receive));
            BigDecimal b2 = new BigDecimal(Float.toString(mStoreDetail.total_amount));

            refund = b2.subtract(b1).floatValue();

        }

        if (refund < 0) {
            Toast.makeText(this, "退款金额不能低于0", Toast.LENGTH_SHORT).show();
            return;
        }

        WaitingProgressTool.showProgress(this);

        String mewaybill = "";
        int status = 2;

        final int taskStatus = getTaskStatus(2);

        // MLog.e("taskpointdetail", "taskStatus" + taskStatus);

        final String fewaybill = mewaybill;

        OnResponseResult<UpdateTaskPointStatusResult> mrespone = new OnResponseResult<UpdateTaskPointStatusResult>() {

            @Override
            public void OnResult(UpdateTaskPointStatusResult result) {

                if (isFinishing())
                    return;
                result.setTaskid(taskid);
                result.setStoreid(mStoreDetail.storeid);
                result.setStatus(2);
                result.setWaybill(mStoreDetail.waybill);
                result.setEwaybill(fewaybill);

                WaitingProgressTool.closeshowProgress();

                MLog.e("checkUpdatePointStatus", "" + result.getErrCode());
                if (result.getErrCode() != 0) {

                    Toast.makeText(UploadPaymentActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT).show();

                    if(TaskUtils.isNeedUpdateInfoCode(result.getErrCode())){
                        //RefreshDetailData();


                        EventBus.getDefault().post(new FreightPointUpdateEvent(0));

                        finish();

                    }


                } else {

                    isUpdateStore = true;

                    TaskOperator.getInstance().UpdateTaskStateByStoreStatusChangeResult(result);

                    mStoreDetail.storestatus = 2;

                    // 任务已完成，从列表中删除
                    if (taskStatus == 2) {
                        TaskOperator.getInstance().RemoveTask(taskid, taskStatus);
                        EventBus.getDefault().post(new FreightPointStatusRefreshEvent());
                    }

                    EventBus.getDefault().post(new FreightPointUpdateEvent());
                    UploadPaymentInfo();

                    // setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
                    // finish();

                }

            }

            @Override
            public void OnError(int ErrCode) {
                if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                    // Toast.makeText(TaskPointDetailActivity.this,
                    // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UploadPaymentActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
                }
                WaitingProgressTool.closeshowProgress();
            }

            @Override
            public void OnGetTag(String Reqtag) {


            }
        };

        if (taskStatus == -1) {

            DeliveryApi.getInstance().UpdateStoreStatus(this,corpid, taskid, mStoreDetail.storeid, status,
                    mStoreDetail.waybill, status == 3 ? "" : fewaybill, -1, mrespone, mStoreDetail.waybill); // status
            // ==
            // 3
            // ?
            // ""
            // :

        } else {

            DeliveryApi.getInstance().UpdateStoreStatus(this,corpid, taskid, mStoreDetail.storeid, status,
                    mStoreDetail.waybill, status == 3 ? "" : fewaybill, taskStatus, mrespone,
                    mStoreDetail.waybill); //
        }
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

    private boolean isUpdateStore = false;

    @Override
    public void onBackPressed() {
        if (isUpdateStore)
            setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        EventBus.getDefault().unregister(this);
        LocationAPI.getInstance().stop();
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

    public CharSequence SetStrSafety(CharSequence str) {

        return str == null ? "" : str;

    }

    public void UploadStoreAddr() {


        OverAndFinish();
        return;

//        AddressBean bean = SPHelper.getInstance(this).readLocalStoreAddress(mStoreDetail.waybill);
//
//        if (bean == null && currentLocationBean != null)
//            bean = currentLocationBean;
//
//
//        boolean isOffLinePointNotUpload = false;
//
//        AddressBean bean2 = SPHelper2.getInstance(mContext).readMarkStoreAddress(mStoreDetail.taskId + mStoreDetail.waybill);
//
//        QueryBuilder<OfflineLocationBean> qb = new QueryBuilder<OfflineLocationBean>(OfflineLocationBean.class)
//                .where("_taskidandwaybill" + " = ?",
//                        new Object[]{(mStoreDetail.taskId + mStoreDetail.waybill)}).limit(0, 1);
//
//        ArrayList<OfflineLocationBean> list = OrmLiteApi.getInstance().getLiteOrm().query(qb);
//
//        if (list != null && list.size() > 0) {
//            isOffLinePointNotUpload = true;
//        }
//
//
//        if (bean2 != null && !isOffLinePointNotUpload) {
//
//            OverAndFinish();
//            return;
//
//        }
//
//
//        boolean isNeedUpdateLocation = false;
//
//        if (bean != null && !TaskUtils.isStorePosUnknown(mStoreDetail)) {
//
//            //LatLng p1LL = CldCoordUtil.gcjToCLDEx(CldCoordUtil.cldToGCJ(nextPoint));
//
//            //LatLng p2LL = CldCoordUtil.gcjToCLDEx(CldCoordUtil.cldToGCJ(point));
//
//
//            HPWPoint nextPoint = new HPWPoint();
//            nextPoint.x = mStoreDetail.storex;
//            nextPoint.y = mStoreDetail.storey;
//            LatLng p1LL = CldCoordUtil.gcjToCLDEx(CldCoordUtil.cldToGCJ(nextPoint));
//
//            LatLng p2LL = new LatLng();
//            p2LL.latitude = bean.latitude;
//            p2LL.longitude = bean.longitude;
//
//            double dis = DistanceUtil.getDistance(p2LL, p1LL);
//
//
//            int range = GeneralSPHelper.getInstance(UploadPaymentActivity.this).getCompanyWantRange(mStoreDetail.corpId);
//
//            if (dis > range)
//                isNeedUpdateLocation = true;
//
//
//        }
//
//
//        if ((TaskUtils.isStorePosUnknown(mStoreDetail) && bean != null) || isNeedUpdateLocation
//
//                ) {
//
//            WaitingProgressTool.showProgress(this);
//
//            CldDeliUploadStoreParm parm = new CldDeliUploadStoreParm();
//
//            // parm.address = tvPosition.getText().toString();
//            parm.corpid = corpid;
//
//            if (!TextUtils.isEmpty(mStoreDetail.linkman))
//                parm.linkman = mStoreDetail.linkman;
//
//            if (!TextUtils.isEmpty(mStoreDetail.linkphone))
//                parm.phone = mStoreDetail.linkphone;
//
//            if (!TextUtils.isEmpty(mStoreDetail.storecode))
//                parm.storecode = mStoreDetail.storecode;
//
//            if (TextUtils.isEmpty(mStoreDetail.storecode))
//                parm.settype = 1;
//            else
//                parm.settype = 3;
//            parm.storeid = mStoreDetail.storeid;
//
//            parm.storename = mStoreDetail.storename;
//            parm.iscenter = 0;
//            parm.storekcode = bean.kcode; // "";//"";//
//            parm.address = bean.uploadAddress;
//
//            parm.extpic = "";
//
//            CldKDeliveryAPI.getInstance().uploadStore(parm, new ICldResultListener() {
//
//                @Override
//                public void onGetResult(int errCode) {
//
//                    WaitingProgressTool.closeshowProgress();
//
//
//                    if (errCode == 0) {
//
//
//                        QueryBuilder<OfflineLocationBean> qb = new QueryBuilder<OfflineLocationBean>(OfflineLocationBean.class)
//                                .whereIn("_taskidandwaybill", new Object[]{(mStoreDetail.taskId + mStoreDetail.waybill)});
//
//                        List<OfflineLocationBean> res = OrmLiteApi.getInstance().getLiteOrm().query(qb);
//
//                        if (res != null && res.size() > 0) {
//
//
//                            OrmLiteApi.getInstance().delete(res.get(0));
//
//                        }
//
//
//                    }
//
//
//                    if (isFinishing())
//                        return;
//
//                    // if (errCode != 0)
//                    // Toast.makeText(StoreUploadActivity.this, "操作失败，请检查网络。",
//                    // Toast.LENGTH_SHORT).show();
//                    // else {
//                    //
//                    // Toast.makeText(StoreUploadActivity.this, "提交成功!",
//                    // Toast.LENGTH_SHORT).show();
//                    // finish();}
//
//                    OverAndFinish();
//
//                }
//
//                @Override
//                public void onGetReqKey(String tag) {
//
//
//                }
//            });
//
//        } else
//            OverAndFinish();

    }

    public void OverAndFinish() {

        setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);


        Toast.makeText(UploadPaymentActivity.this,"已完成",Toast.LENGTH_SHORT).show();

        isNoLastToTransPoint();

        finish();

    }

//	public void getCurrentPositionAddr() {
//
//		if (mStoreDetail.isUnknownAddress && TaskUtils.isStorePosUnknown(mStoreDetail)) {
//
//			onLocation();
//		}
//
//	}

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 223:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startLocation();
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startLocation() {
        //   if (!CldPhoneNet.isNetConnected()) {
//            Toast.makeText(this, "无法获取你的位置信息。", Toast.LENGTH_SHORT).show();
//        } else {
//            // String text = getResources().getString(R.string.area_location);
//            // mCurArea.setText(text);
//            // startLocatAnim();

        WaitingProgressTool.showProgress(this);
        location();
        //      }
    }

    protected void onLocateSuccess(AddressBean bean) {

        if (isFinishing())
            return;

        currentLocationBean = bean;
        tv_kcode.setText(SetStrSafety(FreightLogicTool.splitKcode(currentLocationBean.kcode)));
        LatLng tmp = new LatLng(bean.latitude, bean.longitude);
        if (!TextUtils.isEmpty(currentLocationBean.uploadAddress)) {

            if (mStoreDetail.isUnknownAddress && TaskUtils.isStorePosUnknown(mStoreDetail)) {

                tvAddress.setText(SetStrSafety((currentLocationBean.address).replaceAll("\\s*", "")));


            }

        } else {

            currentLocationBean.address = (mStoreDetail.regionname + mStoreDetail.storeaddr).replaceAll("\\s*", "");
            //    currentLocationBean.uploadAddress = mStoreDetail.storeaddr.replaceAll("\\s*", "");
            currentLocationBean.uploadAddress = "";
            currentLocationBean.pcd = mStoreDetail.regionname.replaceAll("\\s*", "");


            WaitingProgressTool.showProgress(UploadPaymentActivity.this);

            GeoCode(tmp);


        }


    }


    protected void onLocateSuccess(CldLocation location) {

        if (isFinishing())
            return;

        String address = "";
        if (location != null && TaskUtils.isNotFailLocation(location)) {

            LatLng tmp = new LatLng(location.getLatitude(), location.getLongitude());

            currentLocationBean = new AddressBean();

            currentLocationBean.latitude = location.getLatitude();
            currentLocationBean.longitude = location.getLongitude();

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

//                WaitingProgressTool.showProgress(UploadPaymentActivity.this);
//
//                GeoCode(tmp);
                if (mStoreDetail.isUnknownAddress && TaskUtils.isStorePosUnknown(mStoreDetail)) {

                    tvAddress.setText(SetStrSafety((currentLocationBean.address).replaceAll("\\s*", "")));

                    tv_kcode.setText(SetStrSafety(FreightLogicTool.splitKcode(currentLocationBean.kcode)));

                }

            } else {

                currentLocationBean.address = (mStoreDetail.regionname + mStoreDetail.storeaddr).replaceAll("\\s*", "");
                currentLocationBean.uploadAddress = mStoreDetail.storeaddr.replaceAll("\\s*", "");
             //   currentLocationBean.uploadAddress = "";
                currentLocationBean.pcd = mStoreDetail.regionname.replaceAll("\\s*", "");


                WaitingProgressTool.showProgress(UploadPaymentActivity.this);

                GeoCode(tmp);


            }





        } else {
            Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
            // address = mUserInfo.getAddress();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationAPI.getInstance().stop();
        isStop = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStop = false;

//        if (!GPSUtils.isOPen(this) && firstTime) {
//
//            firstTime = false;
//
//        } else {
//            AddressBean bean = SPHelper2.getInstance(mContext).readMarkStoreAddress(mStoreDetail.taskId + mStoreDetail.waybill);
//
//
//            if (bean != null) {
//
//                onLocateSuccess(bean);
//
//            } else {
//                onLocation();
//            }
//
//        }
    }

    private boolean firstTime = true;


    @Override
    public void onReceiveLocation(CldLocation location) {
        WaitingProgressTool.closeshowProgress();
        if (!isFinishing() && !isStop) {
            MLog.e("yyh", "location = " + location.getErrCode() + " " + location.getLatitude() + location.getLongitude() + location.getProvince()
                    + location.getCity() + location.getDistrict() + location.getAddress() + location.getAdCode());

            LocationAPI.getInstance().stop();


            onLocateSuccess(location);

//            if (location.getErrCode() == 0) {
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!isFinishing() && !isStop) {
//                            location();
//                        }
//                    }
//                }, 2000);
//
//
//            } else
//                location();
        }
    }

    private void location() {
        MLog.e("test", " ++++ location =  start location ");

        LocationAPI.getInstance().location(MTQLocationMode.MIXED, 3000, this).setLinster(this);
    }

    protected boolean isStop = false;

    public GeoCoder getGeoCoder() {

        if (mGeoCoder == null)
            mGeoCoder = GeoCoder.newInstance();

        return mGeoCoder;

    }


    public void GeoCode(LatLng latLng) {

        ReverseGeoCodeOption option = new ReverseGeoCodeOption();

        // 设置逆地理坐标的经度值
        option.location.longitude = latLng.longitude;

        // 设置逆地理坐标的纬度值
        option.location.latitude = latLng.latitude;

        getGeoCoder().setOnGetGeoCodeResultListener(this);
        try {
            getGeoCoder().reverseGeoCode(option);// 传入逆地理参数对象
        } catch (IllegalSearchArgumentException e) {


            WaitingProgressTool.closeshowProgress();
            MLog.e("searcherror", e.getMessage() + "");
            e.printStackTrace();
        }

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult mCurGeoResult) {

        if (isFinishing())
            return;

        WaitingProgressTool.closeshowProgress();


        if (mCurGeoResult != null) {

            MLog.e("SearchSDK", mCurGeoResult.address + "\n" + mCurGeoResult.businessCircle);

            if (mCurGeoResult.errorCode != 0) {
                // Toast.makeText(this,
                // result != null && !TextUtils.isEmpty(result.errorMsg) ?
                // result.errorMsg : "逆地理编码失败",
                // Toast.LENGTH_LONG).show();
               // currentLocationBean = null;

                return;

            }
            if (TextUtils.isEmpty(mCurGeoResult.address)) {// 返回地址是否为空判断
                // mTv_Name.setText( result != null
                // && !TextUtils
                // .isEmpty(result.errorMsg) ? result.errorMsg
                // : "逆地理编码失败");

                // SetResult(null);

               // currentLocationBean = null;

            } else {


                // SetResult(result);

                currentLocationBean.address = mCurGeoResult.address;
                currentLocationBean.uploadAddress = mCurGeoResult.address.replaceFirst(mCurGeoResult.addressDetail.province, "")
                        .replaceFirst(mCurGeoResult.addressDetail.city, "")
                        .replaceFirst(mCurGeoResult.addressDetail.district, "");
                currentLocationBean.pcd = (mCurGeoResult.addressDetail.province + mCurGeoResult.addressDetail.city + mCurGeoResult.addressDetail.district)
                        .replaceAll("\\s*", "");
                if (mStoreDetail.isUnknownAddress && TaskUtils.isStorePosUnknown(mStoreDetail)) {

                    tvAddress.setText(SetStrSafety((currentLocationBean.address).replaceAll("\\s*", "")));

                    tv_kcode.setText(SetStrSafety(FreightLogicTool.splitKcode(currentLocationBean.kcode)));

                }


            }

        }
    }



}
