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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.freighthelper.R;

public class SetNickDialog extends Dialog implements OnClickListener {

	private Context mContext;
	private TextView mTitle;
	private EditText mContent;
	private Button mSure;
	private Button mCancel;
	private String mTitleStr;
	private String mContentStr;
	private String mSureStr;
	private String mCancelStr;
	private ISetNicDialogListener mListener;

	public interface ISetNicDialogListener {

		public void OnCancel();

		public void OnSure(String nick);
	}

	public SetNickDialog(Context context) {
		super(context);
	}

	public SetNickDialog(Context context, String titleStr) {
		super(context, R.style.dialog_style);
		mTitleStr = titleStr;
	}

	public SetNickDialog(Context context, String titleStr, String contentStr,
			String cancelStr, String sureStr, ISetNicDialogListener listener) {
		super(context, R.style.dialog_style);
		mContext = context;
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
		// 点击Dialog以外的区域，Dialog不关闭
		setCanceledOnTouchOutside(false);
		// 设置成系统级别的Dialog，即全局性质的Dialog，在任何界面下都可以弹出来
		// getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		getWindow().setContentView(R.layout.dialog_set_nick);

		initViews();
		setListener();
		setViews();
	}

	private void initViews() {
		mTitle = (TextView) findViewById(R.id.dialog_set_nick_title);
		mContent = (EditText) findViewById(R.id.dialog_set_nick_content);
		mCancel = (Button) findViewById(R.id.dialog_set_nick_cancel);
		mSure = (Button) findViewById(R.id.dialog_set_nick_sure);
	}

	private void setListener() {
		mSure.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		mContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				/**
				 * 最多输入12个
				 */
				if (s.length() > 12) {
					s = s.subSequence(0, 12);
					mContent.setText(s);
					mContent.setSelection(s.length());
					Toast.makeText(mContext, R.string.maximum_number_reached,
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	private void setViews() {
		mTitle.setText(mTitleStr);
		mCancel.setText(mCancelStr);
		mSure.setText(mSureStr);
		if (TextUtils.isEmpty(mContentStr)) {
			String hint = mContext.getResources().getString(
					R.string.dialog_nick_hint);
			mContent.setHint(hint);
		} else {
			mContent.setText(mContentStr);
			mContent.setSelection(mContentStr.length());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_set_nick_cancel: {
			dismiss();
			if (mListener != null) {
				mListener.OnCancel();
			}
			break;
		}
		case R.id.dialog_set_nick_sure: {
			dismiss();
			if (mListener != null) {
				String nick = mContent.getText().toString();
				mListener.OnSure(nick);
			}
			break;
		}
		default:
			break;
		}
	}
}
