/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: PromptDialog.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.dialog
 * @Description: 提示对话框
 * @author: zhaoqy
 * @date: 2017年3月28日 上午9:13:33
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;

public class PromptDialog extends Dialog implements OnClickListener {

	private TextView mMessage;
	private Button mSure;
	private Button mCancel;
	private String mMessageStr;
	private String mSureStr;
	private String mCancelStr;
	private IPromptDialogListener mListener;

	public interface IPromptDialogListener {

		public void OnCancel();

		public void OnSure();
	}

	public PromptDialog(Context context) {
		super(context);
	}

	public PromptDialog(Context context, String messageStr) {
		super(context, R.style.dialog_style);
		mMessageStr = messageStr;
	}

	public PromptDialog(Context context, String messageStr, String cancelStr,
			String sureStr, IPromptDialogListener listener) {
		super(context, R.style.dialog_style);
		mMessageStr = messageStr;
		mCancelStr = cancelStr;
		mSureStr = sureStr;
		mListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 点击Dialog以外的区域，Dialog不关闭
		setCanceledOnTouchOutside(false);
		// 设置成系统级别的Dialog，即全局性质的Dialog，在任何界面下都可以弹出来
		// getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		getWindow().setContentView(R.layout.dialog_prompt);

		initViews();
		setListener();
		setViews();
	}

	private void initViews() {
		mMessage = (TextView) findViewById(R.id.dialog_prompt_message);
		mCancel = (Button) findViewById(R.id.dialog_prompt_cancel);
		mSure = (Button) findViewById(R.id.dialog_prompt_sure);
	}

	private void setListener() {
		mSure.setOnClickListener(this);
		mCancel.setOnClickListener(this);
	}

	private void setViews() {
		mMessage.setText(mMessageStr);
		mCancel.setText(mCancelStr);
		mSure.setText(mSureStr);
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		switch (v.getId()) {
		case R.id.dialog_prompt_cancel: {
			dismiss();
			if (mListener != null) {
				mListener.OnCancel();
			}
			break;
		}
		case R.id.dialog_prompt_sure: {
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
