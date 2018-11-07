package com.yunbaba.freighthelper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;

/**
 * Created by zhonghm on 2018/3/23.
 */

public class LocationRemindDialog extends Dialog implements View.OnClickListener {

    private TextView mTitle;
    private TextView mContent;
    private TextView mSure;
    private TextView mCancel;
    private String mTitleStr;
    private String mContentStr;
    private String mSureStr;
    private String mCancelStr;
    private LocationRemindDialog.IClickListener mListener;

    public interface IClickListener {

        public void OnCancel();

        public void OnSure();
    }

    public LocationRemindDialog(Context context) {
        super(context);
    }

    public LocationRemindDialog(Context context, String titleStr) {
        super(context, R.style.dialog_style);
        mTitleStr = titleStr;
    }

    public LocationRemindDialog(Context context, String titleStr, String contentStr,
                     String cancelStr, String sureStr,LocationRemindDialog.IClickListener listener) {
        super(context, R.style.dialog_style);
        mTitleStr = titleStr;
        mCancelStr = cancelStr;
        mContentStr = contentStr;
        mSureStr = sureStr;
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        getWindow().setContentView(R.layout.dialog_locationremind);

        initViews();
        setListener();
        setViews();
    }

    private void initViews() {
        mTitle = (TextView) findViewById(R.id.dialog_exit_title);
        mContent = (TextView) findViewById(R.id.dialog_exit_content);
        mCancel = (TextView) findViewById(R.id.dialog_exit_cancel);
        mSure = (TextView) findViewById(R.id.dialog_exit_sure);
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
