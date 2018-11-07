/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: BindMobileActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 绑定手机号界面
 * @author: zhaoqy
 * @date: 2017年3月20日 下午6:43:18
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.me;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.freighthelper.MainActivity;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.UserInfo;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.manager.UserManager;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.ols.tools.err.CldOlsErrManager.CldOlsErrCode;

import java.util.Timer;
import java.util.TimerTask;

public class BindMobileActivity extends BaseActivity implements OnClickListener {

	public static final String TAG = "BindMobileActivity";
	public static final String BIND_EXTRA = "bind_extra";
	public static final int BIND_FROM_WELCOMR = 0;
	public static final int BIND_FROM_LOGIN = 1;
	public static final int BIND_FROM_USERINFO = 2;

	private ImageView mBack;
	private TextView mTitle;
	private ImageView mNewsImg;
	private TextView mAccountText;
	private EditText mMobileEdit;
	private EditText mVericodeEdit;
	private TextView mVericodeText;
	private Button mConfirmBtn;
	private UserInfo mUserInfo;
	private int mExtra = -1;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_bind_mobile;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mNewsImg = (ImageView) findViewById(R.id.title_right_img);
		mAccountText = (TextView) findViewById(R.id.bind_mobile_account);
		mMobileEdit = (EditText) findViewById(R.id.bind_mobile_edit_mobile);
		mVericodeEdit = (EditText) findViewById(R.id.bind_mobile_edit_vericode);
		mVericodeText = (TextView) findViewById(R.id.bind_mobile_btn_vericode);
		mConfirmBtn = (Button) findViewById(R.id.bind_mobile_btn_confirm);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
	//	mNewsImg.setOnClickListener(this);
		mVericodeText.setOnClickListener(this);
		mConfirmBtn.setOnClickListener(this);
		setTextChangedListener();
	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mNewsImg.setVisibility(View.GONE);
		mTitle.setText(R.string.bind_mobile);
		mExtra = getIntent().getIntExtra(BIND_EXTRA, 0);
		mUserInfo = UserManager.getInstance().getUserInfo();
	}

	@Override
	protected void loadData() {

	}

	@Override
	protected void updateUI() {
//		updateNews();
		String username = mUserInfo.getUserName();
		mAccountText.setText(username);
	}

//	private void updateNews() {
//		if (MsgManager.getInstance().hasUnReadMsg()) {
//			mNewsImg.setImageResource(R.drawable.msg_icon_news);
//		} else {
//			mNewsImg.setImageResource(R.drawable.msg_icon);
//		}
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img: {
			if (mExtra == BIND_FROM_LOGIN) {
				/**
				 * 登录一个没有绑定手机号的帐号，跳转到绑定手机界面，不做操作，
				 * 点击返回按钮，视为退出账号，返回到登录界面，并提示"账号登录需绑定手机号"
				 */
				AccountAPI.getInstance().loginOut();
				AccountAPI.getInstance().setLoginPwd("");
				AccountAPI.getInstance().setLoginStatus(0);
				AccountAPI.getInstance().uninit();
				UserManager.getInstance().getUserInfo().reset();
				
				Toast.makeText(this, "账号登录需绑定手机号",
						Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(this, LoginActivity.class);
				intent.putExtra(LoginActivity.LOGIN_EXTRA,
						LoginActivity.LOGIN_FROM_BIND);
				startActivity(intent);
			}
			finish();
			break;
		}
		case R.id.title_right_img: {
//			Intent intent = new Intent(this, MsgActivity.class);
//			startActivity(intent);
			break;
		}
		case R.id.bind_mobile_btn_vericode: {
			getVericode();
			break;
		}
		case R.id.bind_mobile_btn_confirm: {
			confirm();
			break;
		}
		default:
			break;
		}
	}

	private void getVericode() {
		String mobile = mMobileEdit.getText().toString();
		MLog.i(TAG, "mobile= " + mobile);
		if (!TextUtils.isEmpty(mobile)) {
			if (!AccountAPI.getInstance().isPhoneNum(mobile)) {
				Toast.makeText(this, R.string.account_login_mobile_err,
						Toast.LENGTH_SHORT).show();
			} else {
				String bind = AccountAPI.getInstance().getBindMobile();
				if (mobile.equals(bind)) {
					Toast.makeText(this, R.string.userinfo_revise_mobile_same,
							Toast.LENGTH_SHORT).show();
				} else {
					if (!CldPhoneNet.isNetConnected()) {
						Toast.makeText(this, R.string.common_network_abnormal,
								Toast.LENGTH_SHORT).show();
					} else {
						mCodeTime = GET_CODE_TIMEOUT;
						mVericodeText.setEnabled(false);
						startCodeTask();

						if (TextUtils.isEmpty(bind)) {
							/**
							 * 获取验证码: 102-绑定
							 */
							AccountAPI.getInstance().getVerifyCode(mobile, 102,
									null);
						} else {
							/**
							 * 获取验证码: 103-改绑
							 */
							AccountAPI.getInstance().getVerifyCode(mobile, 103,
									bind);
						}
					}
				}
			}
		}
	}

	private void confirm() {
		String mobile = mMobileEdit.getText().toString();
		String veriCode = mVericodeEdit.getText().toString();
		MLog.i(TAG, "mobile= " + mobile + ", veriCode= " + veriCode);
		if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(veriCode)) {
			if (!AccountAPI.getInstance().isPhoneNum(mobile)) {
				Toast.makeText(this, R.string.account_input_mobile_err,
						Toast.LENGTH_SHORT).show();
			} else {
				if (!CldPhoneNet.isNetConnected()) {
					Toast.makeText(this, R.string.common_network_abnormal,
							Toast.LENGTH_SHORT).show();
				} else {
					String bind = AccountAPI.getInstance().getBindMobile();
					if (TextUtils.isEmpty(bind)) {
						/**
						 * 绑定手机号
						 */
						AccountAPI.getInstance().bindMobile(mobile, veriCode);
					} else {
						/**
						 * 改绑手机号
						 */
						AccountAPI.getInstance().updateMobile(mobile, bind,
								veriCode);
					}
				}
			}
		}
	}

	@Override
	protected void messageEvent(AccountEvent event) {
		switch (event.msgId) {
		case MessageId.MSGID_GET_BIND_VERICODE_SUCCESS:
		case MessageId.MSGID_GET_REVISE_BIND_VERICODE_SUCCESS: {
			Toast.makeText(this, R.string.account_get_vericode_success,
					Toast.LENGTH_SHORT).show();
			break;
		}
		case MessageId.MSGID_GET_BIND_VERICODE_FAILED:
		case MessageId.MSGID_GET_REVISE_BIND_VERICODE_FAILED: {
			resetGetCodeTimer();
			switch (event.errCode) {
			case CldOlsErrCode.NET_NO_CONNECTED:
			case CldOlsErrCode.NET_TIMEOUT:
				Toast.makeText(this, R.string.common_network_abnormal,
						Toast.LENGTH_SHORT).show();
				break;
			case 201:
				Toast.makeText(this, R.string.userinfo_mobile_has_binded,
						Toast.LENGTH_SHORT).show();
				break;
			case 903:
				Toast.makeText(this, R.string.account_vericode_repeat_get,
						Toast.LENGTH_SHORT).show();
				break;
			case 906:
				Toast.makeText(this, R.string.account_vericode_timer_more,
						Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(this, R.string.account_get_vericode_failed,
						Toast.LENGTH_SHORT).show();
				break;
			}
			break;
		}
		case MessageId.MSGID_USERINFO_BIND_MOBILE_SUCCESS:
		case MessageId.MSGID_USERINFO_REVISE_MOBILE_SUCCESS: {
			Toast.makeText(this, R.string.userinfo_revise_mobile_success,
					Toast.LENGTH_SHORT).show();
			/**
			 * 设置用户信息中的手机号
			 */
			String mobile = mMobileEdit.getText().toString();
			mUserInfo.setMobile(mobile);
			switch (mExtra) {
			case BIND_FROM_WELCOMR:
			case BIND_FROM_LOGIN: {
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				finish();
				break;
			}
			case BIND_FROM_USERINFO: {
				finish();
				break;
			}
			default:
				break;
			}
			break;
		}
		case MessageId.MSGID_USERINFO_BIND_MOBILE_FAILED:
		case MessageId.MSGID_USERINFO_REVISE_MOBILE_FAILED: {

			switch (event.errCode) {
			case CldOlsErrCode.NET_NO_CONNECTED:
			case CldOlsErrCode.NET_TIMEOUT:
				Toast.makeText(this, R.string.common_network_abnormal,
						Toast.LENGTH_SHORT).show();
				break;
			case 203:
				Toast.makeText(this, R.string.userinfo_mobile_has_binded,
						Toast.LENGTH_SHORT).show();
				break;
			case 907:
				Toast.makeText(this, R.string.account_vericode_has_invalid,
						Toast.LENGTH_SHORT).show();
				break;
			case 908:
				Toast.makeText(this, R.string.account_vericode_err,
						Toast.LENGTH_SHORT).show();
				break;
			case 909:
				Toast.makeText(this, R.string.account_vericode_has_used,
						Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(this, R.string.userinfo_revise_mobile_failed,
						Toast.LENGTH_SHORT).show();
				break;
			}
			break;
		}
		/*case MessageId.MSGID_MSG_NEW: {
			if (MsgManager.getInstance().hasUnReadMsg(MsgManager.MSG_ALL)) {
				mNewsImg.setImageResource(R.drawable.msg_icon_news);
			} else {
				mNewsImg.setImageResource(R.drawable.msg_icon);
			}
			break;
		}*/
		default:
			break;
		}
	}
	
//	@Subscribe(threadMode = ThreadMode.MAIN)
//	public void onMessageEvent(NewMsgEvent event) {
//		switch (event.msgId) {
//		case MessageId.MSGID_MSG_NEW: {
//			updateNews();
//			break;
//		}
//		default:
//			break;
//		}
//	}

	private static final int GET_CODE_TIMEOUT = 60; // 验证码等待超时
	private int mCodeTime = GET_CODE_TIMEOUT; // 验证码即时时间
	private Timer mTimer = new Timer(); // 获取验证码定时器
	private TimerTask mCodeTask = null; // 获取验证码任务

	private void startCodeTask() {
		cancelCodeTask();
		mCodeTask = new TimerTask() {

			@Override
			public void run() {
				if (null != mCodeHandler) {
					mCodeHandler.sendEmptyMessage(0);
				}
			}
		};
		mTimer.schedule(mCodeTask, 0, 1000);
	}

	private void cancelCodeTask() {
		if (mCodeTask != null) {
			mCodeTask.cancel();
			mCodeTask = null;
		}
	}

	private void resetGetCodeTimer() {
		cancelCodeTask();
		mCodeTime = GET_CODE_TIMEOUT;
		if (mVericodeText != null) {
			mVericodeText.setEnabled(true);
			@SuppressWarnings("deprecation")
			int color = getResources().getColor(R.color.vericode_normal_color);
			mVericodeText.setTextColor(color);
			mVericodeText.setText(getResources().getString(
					R.string.login_resend));
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mCodeHandler = new Handler() {
		
		@SuppressLint("DefaultLocale")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				// 验证码倒计时
				if (mCodeTime <= 0) {
					resetGetCodeTimer();
				} else {
					String vericodeHint = getResources().getString(
							R.string.login_sended_vericode);
					String vericode = String.format(vericodeHint, mCodeTime--);
					@SuppressWarnings("deprecation")
					int color = getResources().getColor(R.color.vericode_disable_color);
					mVericodeText.setTextColor(color);
					mVericodeText.setText(vericode);
				}
				break;
			}
			default:
				break;
			}
		}
	};

	private void setTextChangedListener() {
		mMobileEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String mobile = mMobileEdit.getText().toString();
				String vercode = mVericodeEdit.getText().toString();
				if (!TextUtils.isEmpty(mobile) && mobile.length() == 11) {
					mVericodeText.setEnabled(true);
				} else {
					mVericodeText.setEnabled(false);
				}

				if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(vercode)) {
					mConfirmBtn.setEnabled(true);
				} else {
					mConfirmBtn.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mVericodeEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String mobile = mMobileEdit.getText().toString();
				String vercode = mVericodeEdit.getText().toString();
				if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(vercode)) {
					mConfirmBtn.setEnabled(true);
				} else {
					mConfirmBtn.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
	
	@Override
	public void onBackPressed() {

		//super.onBackPressed();
		
		if (mExtra == BIND_FROM_LOGIN) {
			/**
			 * 登录一个没有绑定手机号的帐号，跳转到绑定手机界面，不做操作，
			 * 点击返回按钮，视为退出账号，返回到登录界面，并提示"账号登录需绑定手机号"
			 */
			AccountAPI.getInstance().loginOut();
			AccountAPI.getInstance().setLoginPwd("");
			AccountAPI.getInstance().setLoginStatus(0);
			AccountAPI.getInstance().uninit();
			UserManager.getInstance().getUserInfo().reset();
			
			Toast.makeText(this, "账号登录需绑定手机号",
					Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra(LoginActivity.LOGIN_EXTRA,
					LoginActivity.LOGIN_FROM_BIND);
			startActivity(intent);
		}
		finish();
		
		
	}
}
