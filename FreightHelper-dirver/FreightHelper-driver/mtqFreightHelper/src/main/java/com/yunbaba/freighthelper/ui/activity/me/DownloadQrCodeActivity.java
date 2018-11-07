package com.yunbaba.freighthelper.ui.activity.me;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhonghm on 2018/5/28.
 */

public class DownloadQrCodeActivity extends BaseButterKnifeActivity {


    @BindView(R.id.title_left_img)
    ImageView mImgBack;

    @BindView(R.id.title_right_img)
    ImageView mImgRight;

    @BindView(R.id.title_text)
    TextView mTvTitle;


    @Override
    public int getLayoutId() {
        return R.layout.activity_download_qrcode;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTvTitle.setText("二维码");
        // iv_titleright.setImageResource(R.drawable.icon_message);
        mImgRight.setVisibility(View.GONE);
        mImgBack.setVisibility(View.VISIBLE);


    }




    @OnClick({ R.id.title_left_img})
    void onClick(View view) {
        switch (view.getId()) {

            case R.id.title_left_img:
                // 返回
                finish();
                // ToastUtils.showMessageForScan(QRLoginConfirmActivity.this,
                // "TEST",
                // Toast.LENGTH_LONG);
                break;
        }
    }
}
