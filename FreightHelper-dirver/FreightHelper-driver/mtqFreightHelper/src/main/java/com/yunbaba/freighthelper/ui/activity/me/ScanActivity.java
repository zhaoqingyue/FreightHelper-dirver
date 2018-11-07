package com.yunbaba.freighthelper.ui.activity.me;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.nv.env.CldNvBaseEnv;
import com.cld.nv.env.CldNvBaseEnv.ProjectType;
import com.cld.nv.util.CldNaviUtil;
import com.cld.ols.tools.URLAnalysis;
import com.google.zxing.Result;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.listner.OnObjectListner;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseScanAcivity;
import com.yunbaba.freighthelper.ui.activity.task.TaskPointDetailActivity;
import com.yunbaba.freighthelper.ui.customview.ScanToast;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.zhy.android.percent.support.PercentRelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;

public class ScanActivity extends BaseScanAcivity {
    @BindView(R.id.title_left_img)
    ImageView mImgBack;

    @BindView(R.id.title_right_img)
    ImageView mImgRight;

    @BindView(R.id.title_text)
    TextView mTvTitle;

    @BindView(R.id.prl_qrlogincode)
    PercentRelativeLayout prlQrCode;

    @BindView(R.id.prl_order_code)
    PercentRelativeLayout prlQrderCode;

    @BindView(R.id.iv_qrcodelogin)
    ImageView ivQrCode;

    @BindView(R.id.tv_qrcodelogin)
    TextView tvQrCode;

    @BindView(R.id.iv_ordercode)
    ImageView ivQrderCode;

    @BindView(R.id.tv_ordercode)
    TextView tvQrderCode;

    int scanType = 0;

    @Override
    public int getLayoutId() {

        return R.layout.activity_scan_combine;
    }

    @Override
    public void initData() {

        mOpenFlash = false;
        mImgBack.setVisibility(View.VISIBLE);
        mImgRight.setVisibility(View.VISIBLE);
        setScanTypeUI(0);
        updateFlashView(mOpenFlash);
    }

    @OnClick({R.id.title_left_img, R.id.title_right_img, R.id.prl_qrlogincode, R.id.prl_order_code})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left_img:
                // 返回
                finish();
                break;
            case R.id.prl_order_code:

                scanType = 1;
                setScanTypeUI(scanType);
                break;
            case R.id.prl_qrlogincode:

                scanType = 0;
                setScanTypeUI(scanType);
                break;
            case R.id.title_right_img:
                // 闪光灯
                mOpenFlash = !mOpenFlash;
                operateFlash(mOpenFlash);
                updateFlashView(mOpenFlash);
                break;
        }
    }

    private void operateFlash(boolean open) {
        if (open) {
            openFlash();
        } else {
            closeFlash();
        }
    }

    // 扫描结束后跳转
    @Override
    public void handleDecode(Result result, Bitmap barcode) {

        super.handleDecode(result, barcode);

        if (!CldNaviUtil.isNetConnected()) {
            // 无网络不处理
            Toast.makeText(ScanActivity.this, "当前网络不可用，请检查网络设置。", Toast.LENGTH_LONG).show();
            return;
        }

        String resultString = result.getText();

        if (scanType == 1) {



             SearchTaskDetial(resultString, new OnObjectListner<MtqDeliStoreDetail>() {
                 @Override
                 public void onResult(MtqDeliStoreDetail res) {
                     boolean isFinds = false;
                    final MtqDeliStoreDetail storedetail = res;
                     if (storedetail != null) {
                         isFinds = true;
                     }

                     final boolean isFind = isFinds;
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             if (isFind) {
                                 final Intent intent = new Intent(ScanActivity.this, TaskPointDetailActivity.class);
                                 // 添加storedetail
                                 String str = GsonTool.getInstance().toJson(storedetail);
                                 intent.putExtra("storedetail", str);

                                 // 添加taskid
                                 intent.putExtra("taskid", storedetail.taskId);
                                 intent.putExtra("corpid", storedetail.corpId);

                                 TaskOperator.getInstance().GetorderDetailFromDB(storedetail.taskId,
                                         storedetail.waybill, new OnObjectListner<MtqDeliOrderDetail>() {
                                             @Override
                                             public void onResult(MtqDeliOrderDetail orderdetail) {
                                                 if (orderdetail != null) {
                                                     String str2 = GsonTool.getInstance().toJson(orderdetail);
                                                     intent.putExtra("orderdetail", str2);
                                                 }
                                                 startActivity(intent);
                                             }
                                         });


                             } else {
                                 // Toast.makeText(this, "很抱歉，未查询到该运货单信息",
                                 // Toast.LENGTH_LONG).show();
                                 // finish();
                                 restartScan();
                                 ScanToast.makeText(ScanActivity.this, "很抱歉，未查询到该运货单信息", Toast.LENGTH_LONG).show();
                             }
                         }
                     });


                 }
             });


        } else {

            String guid = null;

            if (getShareType(resultString) == 6) {

                try {

                    URLAnalysis urlAnalysis = new URLAnalysis();
                    urlAnalysis.analysis(resultString);

                    guid = urlAnalysis.getParam("p");
                    MLog.e("resultstrguid", guid + "");

                } catch (Exception e) {
                    // TODO: handle exception
                    // mTvTitle.setText(e.getMessage());
                }

            }

            // String strNs = urlAnalysis.getParam("ns");

            // ns 为1 时 被扫描设备网络正常

            // if(strNs)

            if (guid != null) {

                Intent intent = new Intent(this, QRLoginConfirmActivity.class);

                intent.putExtra("loginurl", resultString);

                startActivity(intent);

                //finish();

            } else {


                Intent intent = new Intent(this, QRLoginFailActivity.class);

                //	intent.putExtra("loginurl", resultString);

                startActivity(intent);

//				restartScan();
//				ScanToast.makeText(this, "非登录二维码", Toast.LENGTH_LONG).show();

            }

        }

    }

    private void SearchTaskDetial(String key,final OnObjectListner<MtqDeliStoreDetail> listner) {
        TaskOperator.getInstance().GetStoreDetailByCustorderidFromDB(key,listner);

    }

    private void updateFlashView(boolean show) {
        if (show) {
            mImgRight.setImageResource(R.drawable.ic_scan_no_flashlight);
        } else {
            mImgRight.setImageResource(R.drawable.ic_scan_flashlight);
        }
    }

    public void setScanTypeUI(int type) {

        if (type == 0) {

            mTvTitle.setText("扫码登录");

            tvQrCode.setTextColor(getResources().getColor(R.color.app_color));

            ivQrCode.setImageResource(R.drawable.ic_qrcode_press);

            tvQrderCode.setTextColor(getResources().getColor(R.color.white));

            ivQrderCode.setImageResource(R.drawable.ic_barcode_normal);

        } else {

            mTvTitle.setText("扫码查询");

            tvQrCode.setTextColor(getResources().getColor(R.color.white));

            ivQrCode.setImageResource(R.drawable.ic_qrcode_normal);

            tvQrderCode.setTextColor(getResources().getColor(R.color.app_color));

            ivQrderCode.setImageResource(R.drawable.ic_barcode_press);

        }

    }

    /**
     * 解析分享信息的类型<br/>
     * 1：第三方位置浏览<br/>
     * 2：凯立德位置分享<br/>
     * 3：版本升级<br/>
     * 4：POI查询<br/>
     * 5：路径规划<br/>
     * 6：二维码登录<br/>
     * 7：特殊处理华为手机短信中目的地文字导航<br/>
     * 8：地铁图快捷方式<br/>
     * 9：凯立德测试版分享<br/>
     * 10：凯立德正式版分享<br/>
     * 11：蓝牙联网<br/>
     * 12：运营活动H5<br/>
     * 0：未知信息<br/>
     *
     * @param data
     * @return int
     * @author hejie
     * @date 2015年7月3日 下午7:09:01
     */
    public int getShareType(String data) {
        String datatemp = data.trim();
        if (datatemp.startsWith("geo:"))// 其他第三方位置浏览
        /**
         * “geo:latitude,longitude,name?z=altitude,type=postype”
         * 美团=>geo:22.535163,114.022764?q=22.535163,114.022764(千寻寿司)
         * 小米=>geo:0,0?q=罗湖区深南东路金丰城大厦B座1301室+华霖电视光学技术(深圳)有限公司 华为
         * =>geo:0,0?q=广东公司深圳分公司
         */
            // geo有位置时，都需要解析，"q="是辅助信息，无位置时，按地理编码(服务支持)处理
            if (datatemp.contains("q=")) {
                return 999;// parseSpecialGeoData(data);
            } else {
                return 1;
            }
        // intent分享增加navione://开头解析(类似http://)
        if (datatemp.contains("f=je")) {// 加入企业短信邀请
            return 22;
        }
        if (datatemp.contains("www.kldlk.com/") && (datatemp.contains("?f=k") || datatemp.contains("?k=")))// 来自凯立德位置分享
            return 2;
        if (datatemp.contains("www.kldlk.com/?f=v") || datatemp.contains("careland.com.cn/zbyz.php?s="))// 凯立德版本升级
            return 3;
        if (datatemp.contains("www.kldlk.com/?f=s"))// 凯立德POI查询
            return 4;
        if (datatemp.contains("www.kldlk.com/?f=web") || datatemp.contains("cldh5")) // 活动运营H5（正式服务，测试服务）
            return 12;
        if (datatemp.contains("www.kldlk.com/") && datatemp.contains("?f=d"))// 驾车、步行路径规划
            return 5;
        if (datatemp.contains("www.kldlk.com/") && datatemp.contains("?f=td"))// 公交路径规划
            return 13;
        if (datatemp.contains("cldqr://f=l") || datatemp.contains("f=cldqr"))// 二维码登录
            return 6;
        if (datatemp.contains("www.subway.com"))// 地体图快捷方式
            return 8;
        if (datatemp.contains("test.careland.com.cn/ksu"))// 来自凯立德测试版分享
            return 9;
        if (datatemp.contains("kditu.cn"))// 来自凯立德正式版分享
            return 10;
        if (datatemp.contains("carelandbt")) {// 蓝牙联网
            return 11;
        }
        if (datatemp.contains("cldqr://f=u")) {// 车机升级
            return 23;
        }
        if (datatemp.contains("navione:") && CldNvBaseEnv.getProjectType() == ProjectType.TYPE_HY) {// 三方调用货车导航
            return 20;
        }
        return 0;// 未知
    }

}
