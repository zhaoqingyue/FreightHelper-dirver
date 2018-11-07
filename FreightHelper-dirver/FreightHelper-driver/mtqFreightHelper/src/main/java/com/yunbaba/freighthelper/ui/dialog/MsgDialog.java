/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: MsgDialog.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.dialog
 * @Description: 消息询问对话框
 * @author: zhaoqy
 * @date: 2017年5月4日 下午3:05:15
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

public class MsgDialog extends Dialog implements OnClickListener {

	private TextView mTitle;
	private TextView mContent;
	private Button mSure;
	private Button mCancel;
	private String mTitleStr;
	private String mContentStr;
	private String mSureStr;
	private String mCancelStr;
	private IMsgListener mListener;

	public interface IMsgListener {

		public void OnCancel();

		public void OnSure();
	}

	public MsgDialog(Context context) {
		super(context);
	}

	public MsgDialog(Context context, String titleStr) {
		super(context, R.style.dialog_style);
		mTitleStr = titleStr;
	}

	public MsgDialog(Context context, String titleStr, String contentStr,
			String cancelStr, String sureStr, IMsgListener listener) {
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
		getWindow().setContentView(R.layout.dialog_msg);

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
