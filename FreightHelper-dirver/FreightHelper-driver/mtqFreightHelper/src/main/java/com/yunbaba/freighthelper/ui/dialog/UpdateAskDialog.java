package com.yunbaba.freighthelper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;

/**
 * Created by zhonghm on 2018/5/28.
 */

public class UpdateAskDialog  extends Dialog implements View.OnClickListener {

    private TextView mTitle;
    private TextView mContent;
    private Button mSure;
    private Button mCancel;
    private String mTitleStr;
    private String mContentStr;
    private String mSureStr;
    private String mCancelStr;
    private IAskDialogListener mListener;
    private int mtype;
    private Context mContext;

    public interface IAskDialogListener {

        public void OnCancel();

        public void OnSure();
    }

    public UpdateAskDialog(Context context) {
        super(context);
    }

    public UpdateAskDialog(Context context, String titleStr) {
        super(context, R.style.dialog_style);
        mTitleStr = titleStr;
    }

    public UpdateAskDialog(Context context, String titleStr, String contentStr,
                      String cancelStr, String sureStr, int type,IAskDialogListener listener) {
        super(context, R.style.dialog_style);

        mTitleStr = titleStr;
        mCancelStr = cancelStr;
        mContentStr = contentStr;
        mSureStr = sureStr;
        mListener = listener;
        mtype = type;
        mContext  = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 点击Dialog以外的区域，Dialog不关闭
        setCanceledOnTouchOutside(false);
        // 设置成系统级别的Dialog，即全局性质的Dialog，在任何界面下都可以弹出来
        // getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        getWindow().setContentView(R.layout.dialog_update_ask);

        initViews();
        setListener();
        setViews();
    }

    private void initViews() {
        mTitle = (TextView) findViewById(R.id.dialog_exit_title);
        mContent = (TextView) findViewById(R.id.dialog_exit_content);
        mCancel = (Button) findViewById(R.id.dialog_exit_cancel);
        mSure = (Button) findViewById(R.id.dialog_exit_sure);
    }

    private void setListener() {
        mSure.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    private void setViews() {
        mTitle.setText(mTitleStr);
        if (TextUtils.isEmpty(mContentStr)) {
            mContent.setVisibility(View.GONE);
        } else {
            mContent.setText(mContentStr);
            mContent.setVisibility(View.VISIBLE);
        }
        mCancel.setText(mCancelStr);
        mSure.setText(mSureStr);


        if(mContext!=null) {

            if (mtype == 1) {
                //更新窗口


              //  mCancel.setVisibility(View.VISIBLE);
                //todotodotodotodotodo
               mCancel.setVisibility(View.GONE);

              //  mSure.setBackground(mContext.getResources().getDrawable(R.drawable.selector_btn_emerald_red_bg));


            } else {

                mCancel.setVisibility(View.VISIBLE);

               // mSure.setBackground(mContext.getResources().getDrawable(R.drawable.selector_btn_emerald_green_bg));

            }
        }

    }

    @Override
    public void onClick(View v) {
        this.dismiss();
        switch (v.getId()) {
            case R.id.dialog_exit_cancel: {
                dismiss();
                if (mListener != null) {
                    mListener.OnCancel();
                }
                break;
            }
            case R.id.dialog_exit_sure: {
                dismiss();
                if (mListener != null) {
                    mListener.OnSure();
                }
                break;
            }
            default:
                break;
        }
    }
}
