/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: SetPwdActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 重置密码、设置新密码界面
 * @author: zhaoqy
 * @date: 2017年3月20日 下午5:32:56
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.me;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.UserUtils;
import com.yunbaba.freighthelper.utils.UserUtils.InputError;
import com.yunbaba.ols.tools.err.CldOlsErrManager.CldOlsErrCode;

import java.util.Timer;
import java.util.TimerTask;

public class SetPwdActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "SetPwdActivity";
	public static final String SET_PWD_EXTRA = "set_pwd_extra";
	public static final int SET_PWD_RETRIEVE_PWD = 0; // 重置密码
	public static final int SET_PWD_SET_NEW_PWD = 1; // 设置新密码

	private ImageView mBack;
	private TextView mTitle;
	private LinearLayout mVerifyLayout;
	private LinearLayout mSetPwdLayout;
	private EditText mMobileEdit;
	private EditText mVericodeEdit;
	private TextView mVericodeText;
	private Button mNextBtn;
	private EditText mPwdEdit;
	private EditText mConfirmEdit;
	private int mExtra = 0;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_set_pwd;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);

		mVerifyLayout = (LinearLayout) findViewById(R.id.set_pwd_verify_mobile_layout);
		mMobileEdit = (EditText) findViewById(R.id.set_pwd_edit_mobile);
		mVericodeEdit = (EditText) findViewById(R.id.set_pwd_edit_vericode);
		mVericodeText = (TextView) findViewById(R.id.set_pwd_btn_vericode);
		mNextBtn = (Button) findViewById(R.id.set_pwd_btn_next);
		mSetPwdLayout = (LinearLayout) findViewById(R.id.set_pwd_set_new_pwd_layout);
		mPwdEdit = (EditText) findViewById(R.id.modify_pwd_edit_new_pwd);
		mConfirmEdit = (EditText) findViewById(R.id.set_pwd_edit_confirm_pwd);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
		mVericodeText.setOnClickListener(this);
		mNextBtn.setOnClickListener(this);
		findViewById(R.id.set_pwd_btn_confirm).setOnClickListener(this);
		setTextChangedListener();
	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);

		/**
		 * 如果是从“忘记密码”进入，则显示"重置密码; 否则显示"设置登录密码"
		 */
		mExtra = getIntent().getIntExtra(SET_PWD_EXTRA, 0);
		switch (mExtra) {
		case SET_PWD_RETRIEVE_PWD: {
			mTitle.setText(R.string.set_pwd_retrieve_pwd);
			break;
		}
		case SET_PWD_SET_NEW_PWD: {
			mTitle.setText(R.string.set_pwd_set_new_pwd);
			break;
		}
		default:
			break;
		}
	}

	@Override
	protected void loadData() {

	}

	@Override
	protected void updateUI() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img: {
			finish();
			break;
		}
		case R.id.set_pwd_btn_vericode: {
			getVericode();
			break;
		}
		case R.id.set_pwd_btn_next: {
			next();
			break;
		}
		case R.id.set_pwd_btn_confirm: {
			confirm();
			break;
		}
		default:
			break;
		}
	}

	private void getVericode() {
		String mobile = mMobileEdit.getText().toString();
		MLog.i(TAG, "mobile: " + mobile);
		if (!AccountAPI.getInstance().isPhoneNum(mobile)) {
			Toast.makeText(this, R.string.account_login_mobile_err,
					Toast.LENGTH_SHORT).show();
		} else {
			if (!CldPhoneNet.isNetConnected()) {
				Toast.makeText(this, "操作失败，请检查网络。",
						Toast.LENGTH_SHORT).show();
			} else {
				mCodeTime = GET_CODE_TIMEOUT;
				mVericodeText.setEnabled(false);
				startCodeTask();
				/**
				 * 获取重置密码验证码: 105-重置密码
				 */
				AccountAPI.getInstance().getVerifyCode(mobile, 105, null);
			}
		}
	}

	private void next() {
		String mobile = mMobileEdit.getText().toString();
		String veriCode = mVericodeEdit.getText().toString();
		MLog.i(TAG, "mobile= " + mobile + ", veriCode= " + veriCode);
		if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(veriCode)) {
			if (!AccountAPI.getInstance().isPhoneNum(mobile)) {
				Toast.makeText(this, R.string.account_login_mobile_err,
						Toast.LENGTH_SHORT).show();
			} else {
				if (!CldPhoneNet.isNetConnected()) {
					Toast.makeText(this, "操作失败，请检查网络。",
							Toast.LENGTH_SHORT).show();
				} else {
					/**
					 * 校验手机验证码是否正确 : 105-重置密码
					 */
					AccountAPI.getInstance().checkMobileVeriCode(mobile,
							veriCode, 105);
				}
			}
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void confirm() {
		String newPwd = mPwdEdit.getText().toString();
		String affirmPwd = mConfirmEdit.getText().toString();
		MLog.i(TAG, "newPwd= " + newPwd + ", affirmPwd= " + affirmPwd);

		InputError errorCode = UserUtils.checkRevisePwd("", newPwd, affirmPwd);
		switch (errorCode) {
		case eERROR_OLD_PASSWORD_EMPTY:
			Toast.makeText(this, R.string.userinfo_revise_pwd_old_not_empty,
					Toast.LENGTH_SHORT).show();
			break;
		case eERROR_NEW_PASSWORD_EMPTY:
			Toast.makeText(this, R.string.userinfo_revise_pwd_new_not_empty,
					Toast.LENGTH_SHORT).show();
			break;
		case eERROR_AFFIRM_PASSWORD_EMPTY:
			Toast.makeText(this, R.string.userinfo_revise_pwd_ensure_new,
					Toast.LENGTH_SHORT).show();
			break;
		case eERROR_PASSWORD_INPUT:
			Toast.makeText(this, R.string.account_reset_pwd_new_err,
					Toast.LENGTH_SHORT).show();
			break;
		case eERROR_PASSWORD_CONTAINS_SPECAIL:
			Toast.makeText(this, R.string.account_reset_pwd_new_specail,
					Toast.LENGTH_SHORT).show();
			break;
		case eERROR_PASSWORD_LESSONENUM:
			Toast.makeText(this, R.string.account_reset_pwd_new_lessonenum,
					Toast.LENGTH_SHORT).show();
			break;
		case eERROR_NEW_OLD_SAME:
			Toast.makeText(this, R.string.userinfo_revise_pwd_same,
					Toast.LENGTH_SHORT).show();
			break;
		case eERROR_NEW_AFFIRM_UNSAME:
			Toast.makeText(this, R.string.userinfo_revise_pwd_new_old_unsame,
					Toast.LENGTH_SHORT).show();
			break;
		case eERROR_NONE:
			if (!CldPhoneNet.isNetConnected()) {
				Toast.makeText(this, "操作失败，请检查网络。",
						Toast.LENGTH_SHORT).show();
			} else {
				String mobile = mMobileEdit.getText().toString();
				String vercode = mVericodeEdit.getText().toString();
				/**
				 * 通过手机验证重置密码
				 */
				AccountAPI.getInstance().retrievePwd(mobile, newPwd, vercode);
			}
			break;
		}
	}

	@Override
	protected void messageEvent(AccountEvent event) {
		switch (event.msgId) {
		case MessageId.MSGID_GET_RETRIEVE_PWD_VERICODE_SUCCESS: {
			Toast.makeText(this, R.string.account_get_vericode_success,
					Toast.LENGTH_SHORT).show();
			break;
		}
		case MessageId.MSGID_GET_RETRIEVE_PWD_VERICODE_FAILED: {
			resetGetCodeTimer();

			switch (event.errCode) {
			case CldOlsErrCode.NET_NO_CONNECTED:
			case CldOlsErrCode.NET_TIMEOUT: {
				Toast.makeText(this, "操作失败，请检查网络。",
						Toast.LENGTH_SHORT).show();
				break;
			}
			case 202: {
				Toast.makeText(this,
						R.string.account_retrive_pwd_mobile_unbind,
						Toast.LENGTH_SHORT).show();
				break;
			}
			case 903: {
				Toast.makeText(this, R.string.account_vericode_repeat_get,
						Toast.LENGTH_SHORT).show();
				break;
			}
			case 906: {
				Toast.makeText(this, R.string.account_vericode_timer_more,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				Toast.makeText(this, R.string.account_get_vericode_failed,
						Toast.LENGTH_SHORT).show();
				break;
			}
			break;
		}
		case MessageId.MSGID_CHECK_RETRIEVE_PWD_VERICODE_SUCCESS: {
			cancelCodeTask();
			mVerifyLayout.setVisibility(View.GONE);
			mSetPwdLayout.setVisibility(View.VISIBLE);
			break;
		}
		case MessageId.MSGID_CHECK_RETRIEVE_PWD_VERICODE_FAILED: {
			resetGetCodeTimer();
			Toast.makeText(this, R.string.account_vericode_has_err,
					Toast.LENGTH_SHORT).show();
			break;
		}
		case MessageId.MSGID_PWD_SET_PWD_SUCCESS: {
			finish();
			switch (mExtra) {
			case SET_PWD_RETRIEVE_PWD: {
				Toast.makeText(this, R.string.account_reset_pwd_success,
						Toast.LENGTH_SHORT).show();
				break;
			}
			case SET_PWD_SET_NEW_PWD: {
				Toast.makeText(this, "设置登录密码成功", Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
			break;
		}
		case MessageId.MSGID_PWD_SET_PWD_FAILED: {
			switch (event.errCode) {
			case CldOlsErrCode.NET_NO_CONNECTED:
			case CldOlsErrCode.NET_TIMEOUT:
				Toast.makeText(this, "操作失败，请检查网络。",
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
				Toast.makeText(this, R.string.account_reset_pwd_failed,
						Toast.LENGTH_SHORT).show();
				break;
			}
			break;
		}
		default:
			break;
		}
	}

	private static final int GET_CODE_TIMEOUT = 60; // 验证码等待超时
	private int mCodeTime = GET_CODE_TIMEOUT; // 验证码即时时间
	private Timer mTimer = new Timer(); // 获取验证码定时器
	private TimerTask mCodeTask = null; // 获取验证码任务

	public void startCodeTask() {
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

	public void cancelCodeTask() {
		if (mCodeTask != null) {
			mCodeTask.cancel();
			mCodeTask = null;
		}
	}

	@SuppressWarnings("deprecation")
	public void resetGetCodeTimer() {
		cancelCodeTask();
		mCodeTime = GET_CODE_TIMEOUT;
		if (mVericodeText != null) {
			mVericodeText.setEnabled(true);
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
					mNextBtn.setEnabled(true);
				} else {
					mNextBtn.setEnabled(false);
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
					mNextBtn.setEnabled(true);
				} else {
					mNextBtn.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
}
