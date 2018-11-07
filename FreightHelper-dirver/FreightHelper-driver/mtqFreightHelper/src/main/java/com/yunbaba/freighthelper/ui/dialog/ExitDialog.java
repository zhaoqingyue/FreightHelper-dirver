/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: ExitDialog.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.dialog
 * @Description: 退出对话框
 * @author: zhaoqy
 * @date: 2017年4月5日 下午3:05:20
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;

public class ExitDialog extends Dialog implements OnClickListener {

	private TextView mTitle;
	private TextView mContent;
	private Button mSure;
	private Button mCancel;
	private String mTitleStr;
	private String mContentStr;
	private String mSureStr;
	private String mCancelStr;
	private IExitDialogListener mListener;
	private int mtype;
	private Context mContext;

	public interface IExitDialogListener {

		public void OnCancel();

		public void OnSure();
	}

	public ExitDialog(Context context) {
		super(context);
	}

	public ExitDialog(Context context, String titleStr) {
		super(context, R.style.dialog_style);
		mTitleStr = titleStr;
	}

	public ExitDialog(Context context, String titleStr, String contentStr,
			String cancelStr, String sureStr, int type,IExitDialogListener listener) {
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
		getWindow().setContentView(R.layout.dialog_exit);

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

				mSure.setBackground(mContext.getResources().getDrawable(R.drawable.selector_btn_emerald_red_bg));


			} else {
				mSure.setBackground(mContext.getResources().getDrawable(R.drawable.selector_bth_red_bg));

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
