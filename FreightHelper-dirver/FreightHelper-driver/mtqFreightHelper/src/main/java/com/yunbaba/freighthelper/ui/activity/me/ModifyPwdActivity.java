/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: ModifyPwdActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 修改密码界面
 * @author: zhaoqy
 * @date: 2017年3月20日 下午6:44:36
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.me;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.UserUtils;
import com.yunbaba.freighthelper.utils.UserUtils.InputError;
import com.yunbaba.ols.tools.err.CldOlsErrManager.CldOlsErrCode;

public class ModifyPwdActivity extends BaseActivity implements OnClickListener {
	private ImageView mBack;
	private TextView mTitle;
	private ImageView mNewsImg;
	private EditText mOriginalPwd;
	private EditText mNewPwd;
	private EditText mConfirmPwd;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_modify_pwd;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mNewsImg = (ImageView) findViewById(R.id.title_right_img);
		mOriginalPwd = (EditText) findViewById(R.id.modify_pwd_edit_original_pwd);
		mNewPwd = (EditText) findViewById(R.id.modify_pwd_edit_new_pwd);
		mConfirmPwd = (EditText) findViewById(R.id.modify_pwd_edit_confirm_pwd);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
		//mNewsImg.setOnClickListener(this);
		findViewById(R.id.modify_pwd_btn_confirm).setOnClickListener(this);
	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mNewsImg.setVisibility(View.GONE);
		mTitle.setText(R.string.modify_pwd);
	}

	@Override
	protected void loadData() {

	}

	@Override
	protected void updateUI() {
		//updateNews();
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
			finish();
			break;
		}
		case R.id.title_right_img: {
//			Intent intent = new Intent(this, MsgActivity.class);
//			startActivity(intent);
			break;
		}
		case R.id.modify_pwd_btn_confirm: {
			confirm();
			break;
		}
		default:
			break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void confirm() {
		String oldPwd = mOriginalPwd.getText().toString();
		String newPwd = mNewPwd.getText().toString();
		String confirmPwd = mConfirmPwd.getText().toString();

		InputError errorCode = UserUtils.checkRevisePwd(oldPwd, newPwd,
				confirmPwd);
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
				/**
				 * 修改密码
				 */
				//oldPwd = AccountAPI.getInstance().getLoginPwd();
				AccountAPI.getInstance().revisePwd(oldPwd, newPwd);
			}
			break;
		}
	}

	@Override
	protected void messageEvent(AccountEvent event) {
		switch (event.msgId) {
		case MessageId.MSGID_PWD_MODIFY_PWD_SUCCESS: {
			finish();
			Toast.makeText(this, R.string.userinfo_revise_pwd_success,
					Toast.LENGTH_SHORT).show();
			break;
		}
		case MessageId.MSGID_PWD_MODIFY_PWD_FAILED: {
			switch (event.errCode) {
			case CldOlsErrCode.NET_NO_CONNECTED:
			case CldOlsErrCode.NET_TIMEOUT:
				Toast.makeText(this,"操作失败，请检查网络。",
						Toast.LENGTH_SHORT).show();
				break;
			case 104:
				Toast.makeText(this, R.string.userinfo_revise_pwd_old_err,
						Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(this, R.string.userinfo_revise_pwd_failed,
						Toast.LENGTH_SHORT).show();
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
}
