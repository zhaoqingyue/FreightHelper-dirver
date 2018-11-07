package com.yunbaba.freighthelper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;

public class LoginMsgDialog extends Dialog implements OnClickListener{

	
	
	private TextView mTitle;
	private TextView mCancel;
	private String mTitleStr;
	public IMsgListener mListener;

	public interface IMsgListener {

		public void OnCancel();

		public void OnSure();
	}

	public LoginMsgDialog(Context context) {
		super(context);
	}

	public LoginMsgDialog(Context context, String titleStr,IMsgListener mListener) {
		super(context, R.style.dialog_style);
		mTitleStr = titleStr;
		this.mListener = mListener;
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCanceledOnTouchOutside(false);
		getWindow().setContentView(R.layout.dialog_loginmsg);

		initViews();
		setListener();
		setViews();
	}

	private void initViews() {
		mTitle = (TextView) findViewById(R.id.tv_title);
		mCancel = (TextView) findViewById(R.id.tv_cancel);
	}

	private void setListener() {
		
		mCancel.setOnClickListener(this);
	}

	private void setViews() {
		mTitle.setText(mTitleStr);
		
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		switch (v.getId()) {
		case R.id.tv_cancel: {
			dismiss();
			if (mListener != null) {
				mListener.OnCancel();
			}
			break;
		}
		
		default:
			break;
		}
	}
	
}
