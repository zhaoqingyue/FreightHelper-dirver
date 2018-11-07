/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: LoginActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 登录界面
 * @author: zhaoqy
 * @date: 2017年3月20日 下午5:18:25
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
import android.text.method.ReplacementTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.setting.CldSetting;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.trunk.DeliveryRouteAPI;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.freighthelper.MainActivity;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.UserInfo;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.manager.UserManager;
import com.yunbaba.freighthelper.ui.activity.GuideActivity;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.utils.CommonTool;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.UserUtils;
import com.yunbaba.freighthelper.utils.UserUtils.InputError;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.freighthelper.utils.WaitingUpdateTaskDialog;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.sap.bean.CldSapKAParm.CldUserInfo;
import com.yunbaba.ols.tools.err.CldOlsErrManager.CldOlsErrCode;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends BaseActivity implements OnClickListener {
    public static final String TAG = "LoginActivity";
    public static final String LOGIN_EXTRA = "login_extra";
    public static final int LOGIN_FROM_BIND = 0;
    public static final int LOGIN_FROM_AUTOLOGINFAIL = 333;
    private LinearLayout mMobileLayou;
    private LinearLayout mAccountLayout;
    private EditText mMobileEdit;
    private EditText mVericodeEdit;
    private TextView mVericodeText;
    private Button mMobileBtn;
    private TextView mAccountText;
    private EditText mAccountEdit;
    private EditText mPwdEdit;
    private Button mAccountBtn;
    private TextView mMobileText;
    private TextView mLostPwdText;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {




        //	CldKDecoupAPI.getInstance().onLoginOut();
        //	CldKDecoupAPI.getInstance().sessionRelogin();
        DeliveryRouteAPI.isNeedSetSession = true;

        WaitingUpdateTaskDialog.getInstance().clean();

        mMobileLayou = (LinearLayout) findViewById(R.id.login_mobile_layout);
        mMobileEdit = (EditText) findViewById(R.id.login_edit_mobile);
        mVericodeEdit = (EditText) findViewById(R.id.login_edit_vericode);
        mVericodeText = (TextView) findViewById(R.id.login_text_vericode);
        mMobileBtn = (Button) findViewById(R.id.login_btn_mobile);
        mAccountText = (TextView) findViewById(R.id.login_text_account);

        mAccountLayout = (LinearLayout) findViewById(R.id.login_account_layout);
        mAccountEdit = (EditText) findViewById(R.id.login_edit_account);
        mPwdEdit = (EditText) findViewById(R.id.login_edit_pwd);
        mAccountBtn = (Button) findViewById(R.id.login_btn_account);
        mMobileText = (TextView) findViewById(R.id.login_text_mobile);
        mLostPwdText = (TextView) findViewById(R.id.login_lost_pwd);
    }

    @Override
    protected void setListener() {
        findViewById(R.id.login_cancel).setOnClickListener(this);
        mVericodeText.setOnClickListener(this);
        mMobileBtn.setOnClickListener(this);
        mAccountText.setOnClickListener(this);
        mAccountBtn.setOnClickListener(this);
        mMobileText.setOnClickListener(this);
        mLostPwdText.setOnClickListener(this);
        setTextChangedListener();
    }

    @Override
    protected void initData() {
        int extra = getIntent().getIntExtra(LOGIN_EXTRA, -1);
        if (extra == LOGIN_FROM_BIND) {
            mMobileLayou.setVisibility(View.GONE);
            mAccountLayout.setVisibility(View.VISIBLE);
        } else if (extra == LOGIN_FROM_AUTOLOGINFAIL) {
            mMobileLayou.setVisibility(View.GONE);
            mAccountLayout.setVisibility(View.VISIBLE);
        }

        String username = CldSetting.getString("username");
        if (!TextUtils.isEmpty(username)) {
            mAccountEdit.setText(username);
            mAccountEdit.setSelection(username.length());
        } else {
            String account = AccountAPI.getInstance().getLoginName();
            if (!TextUtils.isEmpty(account)) {
                mAccountEdit.setText(account);
                mAccountEdit.setSelection(account.length());
            } else {
                String mobile = AccountAPI.getInstance().getBindMobile();
                if (!TextUtils.isEmpty(mobile)) {
                    mAccountEdit.setText(mobile);
                    mAccountEdit.setSelection(mobile.length());
                }
            }
        }

        if (extra == LOGIN_FROM_AUTOLOGINFAIL) {
            String loginName = AccountAPI.getInstance().getLoginName();
            String pwd = AccountAPI.getInstance().getLoginPwd();

            if (!TextUtils.isEmpty(loginName)) {
                mAccountEdit.setText(loginName);
                mAccountEdit.setSelection(loginName.length());
            }

            if (!TextUtils.isEmpty(pwd)) {
                mPwdEdit.setText(pwd);
                mPwdEdit.setSelection(pwd.length());
            }
        }

        if (GeneralSPHelper.getInstance(this).ReadFirst()) {
            startActivity(new Intent(this, GuideActivity.class));
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
            case R.id.login_cancel: {
                finish();
                break;
            }
            case R.id.login_text_vericode: {
                getVericode();
                break;
            }
            case R.id.login_btn_mobile: {

                if (!CommonTool.isFastDoubleClick()) {

                    ShowLoginWaitHint();
                    mobileLogin();
                }
                break;
            }
            case R.id.login_text_account: {
                mMobileLayou.setVisibility(View.GONE);
                mAccountLayout.setVisibility(View.VISIBLE);
                String accout = mAccountEdit.getText().toString();
                mAccountEdit.setSelection(accout.length());
                mAccountEdit.requestFocus();
                break;
            }
            case R.id.login_btn_account: {

                if (!CommonTool.isFastDoubleClick()) {
                    ShowLoginWaitHint();
                    accountLogin();
                }
                break;
            }
            case R.id.login_text_mobile: {
                String mobile = mMobileEdit.getText().toString();
                String vercode = mVericodeEdit.getText().toString();
                if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(vercode)) {
                    mMobileBtn.setEnabled(true);
                } else {
                    mMobileBtn.setEnabled(false);
                }

                mMobileLayou.setVisibility(View.VISIBLE);
                mAccountLayout.setVisibility(View.GONE);
                break;
            }
            case R.id.login_lost_pwd: {
                Intent intent = new Intent(this, SetPwdActivity.class);
                intent.putExtra(SetPwdActivity.SET_PWD_EXTRA, SetPwdActivity.SET_PWD_RETRIEVE_PWD);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    public boolean isWaiting = false;

    public synchronized void ShowLoginWaitHint() {

        if (!isWaiting) {

            isWaiting = true;
            mCodeHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (!isFinishing()) {

                        if (isWaiting) {
                            Toast.makeText(LoginActivity.this, "登录中，请稍后", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            }, 2000);
        }
    }

    private void getVericode() {
        String mobile = mMobileEdit.getText().toString();

        MLog.i(TAG, "mobile: " + mobile);
        if (!AccountAPI.getInstance().isPhoneNum(mobile)) {
            Toast.makeText(this, R.string.account_login_mobile_err, Toast.LENGTH_SHORT).show();
        } else {
            if (!CldPhoneNet.isNetConnected()) {
                Toast.makeText(this, R.string.common_network_abnormal, Toast.LENGTH_SHORT).show();
            } else {
                mCodeTime = GET_CODE_TIMEOUT;
                mVericodeText.setEnabled(false);
                mCodeTimePhone = mobile;
                startCodeTask();
                /**
                 * 获取快速登录验证码: 106-快速登录
                 */
                AccountAPI.getInstance().getVerifyCode(mobile, 106, null);
            }
        }
    }

    private void mobileLogin() {
        String mobile = mMobileEdit.getText().toString();
        String veriCode = mVericodeEdit.getText().toString();
        MLog.i(TAG, "mobile= " + mobile + ", veriCode= " + veriCode);
        if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(veriCode)) {
            if (!AccountAPI.getInstance().isPhoneNum(mobile)) {
                isWaiting = false;
                Toast.makeText(this, R.string.account_login_mobile_err, Toast.LENGTH_SHORT).show();
            } else {
                if (!CldPhoneNet.isNetConnected()) {
                    isWaiting = false;
                    Toast.makeText(this, R.string.common_network_abnormal, Toast.LENGTH_SHORT).show();
                } else {
                    /**
                     * 快速登录
                     */

                    WaitingProgressTool.showProgress(this);
                    AccountAPI.getInstance().fastLogin(mobile, veriCode);
                }
            }
        }
    }

    private void accountLogin() {
        String account = mAccountEdit.getText().toString();
        String pwd = mPwdEdit.getText().toString();
        MLog.i(TAG, "account= " + account + ", pwd= " + pwd);




        int extra2 = getIntent().getIntExtra(LOGIN_EXTRA, -1);
        if (extra2 == LOGIN_FROM_AUTOLOGINFAIL) {
            WaitingProgressTool.showProgress(this);
            AccountAPI.getInstance().login2(account, pwd);

            return;
        }

        InputError errorCode = UserUtils.checkInputIsValid(account, pwd);
        if (errorCode != InputError.eERROR_NONE)
            isWaiting = false;


        switch (errorCode) {
            case eERROR_ACCOUNT_EMPTY: {
                Toast.makeText(this, R.string.account_account_empty, Toast.LENGTH_SHORT).show();
                break;
            }
            case eERROR_PASSWORD_EMPTY: {
                Toast.makeText(this, R.string.account_pasword_empty, Toast.LENGTH_SHORT).show();
                break;
            }
            case eERROR_ACCOUNT_INPUT: {

                Toast.makeText(this, R.string.account_account_error, Toast.LENGTH_SHORT).show();

                break;
            }
            case eERROR_PASSWORD_INPUT: {
                Toast.makeText(this, R.string.account_password_error, Toast.LENGTH_SHORT).show();
                break;
            }
            case eERROR_EMAIL_INPUT: {
                Toast.makeText(this, R.string.account_email_error, Toast.LENGTH_SHORT).show();
                break;
            }
            case eERROR_NONE: {
                if (!CldPhoneNet.isNetConnected()) {
                    isWaiting = false;
                    Toast.makeText(this, R.string.common_network_abnormal, Toast.LENGTH_SHORT).show();
                } else {
                    /**
                     * 账号登录
                     */

                    int extra = getIntent().getIntExtra(LOGIN_EXTRA, -1);
                    if (extra == LOGIN_FROM_AUTOLOGINFAIL) {
                        WaitingProgressTool.showProgress(this);
                        AccountAPI.getInstance().login2(account, pwd);
                    } else {

                        WaitingProgressTool.showProgress(this);
                        AccountAPI.getInstance().login(account, pwd);
                    }

                }
                break;
            }
            default:
                break;
        }
    }

    private int loginType = 0;

    @Override
    protected void messageEvent(final AccountEvent event) {

        switch (event.msgId) {
            case MessageId.MSGID_GET_LOGIN_VERICODE_SUCCESS: {
                Toast.makeText(this, R.string.account_get_vericode_success, Toast.LENGTH_SHORT).show();
                break;
            }
            case MessageId.MSGID_GET_LOGIN_VERICODE_FAILED: {
                resetGetCodeTimer();
                switch (event.errCode) {
                    case CldOlsErrCode.NET_NO_CONNECTED:
                    case CldOlsErrCode.NET_TIMEOUT:
                        Toast.makeText(this, R.string.common_network_abnormal, Toast.LENGTH_SHORT).show();
                        break;
                    case 903:
                        Toast.makeText(this, R.string.account_vericode_repeat_get, Toast.LENGTH_SHORT).show();
                        break;
                    case 906:
                        Toast.makeText(this, R.string.account_vericode_timer_more, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(this, R.string.account_get_vericode_failed, Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            }
            case MessageId.MSGID_LOGIN_MOBILE_LOGIN_SUCCESS: {
                loginType = 1;
                // Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                break;
            }
            case MessageId.MSGID_LOGIN_ACCOUNT_LOGIN_SUCCESS: {
                loginType = 2;
                // Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                break;
            }
            case MessageId.MSGID_LOGIN_MOBILE_LOGIN_FAILED: {

                isWaiting = false;

                WaitingProgressTool.closeshowProgress();

                switch (event.errCode) {
                    case CldOlsErrCode.NET_NO_CONNECTED:
                    case CldOlsErrCode.NET_TIMEOUT:
                        Toast.makeText(this, R.string.common_network_abnormal, Toast.LENGTH_SHORT).show();
                        break;
                    case 907:
                        Toast.makeText(this, R.string.account_vericode_has_invalid, Toast.LENGTH_SHORT).show();
                        break;
                    case 908:
                        Toast.makeText(this, R.string.account_vericode_has_err, Toast.LENGTH_SHORT).show();
                        break;
                    case 909:
                        Toast.makeText(this, R.string.account_vericode_has_used, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(this, R.string.account_login_failed, Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            }
            case MessageId.MSGID_LOGIN_ACCOUNT_LOGIN_FAILED: {

                isWaiting = false;

                WaitingProgressTool.closeshowProgress();

                switch (event.errCode) {
                    case -1:
                    case -2:
                    case -105:
                        Toast.makeText(this, R.string.common_network_abnormal, Toast.LENGTH_SHORT).show();
                        break;
                    case 102:
                        Toast.makeText(this, R.string.account_login_user_err, Toast.LENGTH_SHORT).show();
                        break;
                    case 104:
                        Toast.makeText(this, R.string.account_login_userpwd_err, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(this, R.string.account_login_failed, Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            }
            case MessageId.MSGID_USERINFO_GETDETAIL_SUCCESS:
            case MessageId.MSGID_USERINFO_GETDETAIL_FAILED: {
                cancelCodeTask();

                CldUserInfo info = AccountAPI.getInstance().getUserInfoDetail();
                UserInfo userInfo = new UserInfo();
                userInfo.setSuccess(event.errCode);
                userInfo.setUserName(info.getLoginName());
                CldSetting.put("username", info.getLoginName());
                userInfo.setUserAlias(info.getUserAlias());
                userInfo.setSex(info.getSex());
                userInfo.setAddress(info.getAddress());
                userInfo.setImgHead(info.getPhotoPath());
                userInfo.setLoginStatus(2);
                userInfo.setLoginType(loginType);
                final String bind = AccountAPI.getInstance().getBindMobile();
                // 解决问题：解绑后，还是能从服务器获取到手机号码
                if (!bind.equals(info.getMobile())) {
                    userInfo.setMobile(bind);
                } else {
                    userInfo.setMobile(info.getMobile());
                }
                UserManager.getInstance().setUserInfo(userInfo);
                UserManager.getInstance().getTmpUserInfo().assignVaule(userInfo);
                /**
                 * 登录成功后，调货运登录鉴权接口，成功后才可调货运其它接口
                 */

                CldBllKDelivery.getInstance().loginAuth(new ICldResultListener() {

                    @Override
                    public void onGetResult(int errCode) {
                        if (errCode == 0) {

                            WaitingProgressTool.closeshowProgress();


                            if (mMobileLayou.getVisibility() == View.VISIBLE) {

                                GeneralSPHelper.getInstance(LoginActivity.this).setIsMobileLogin(true);

                            } else {

                                GeneralSPHelper.getInstance(LoginActivity.this).setIsMobileLogin(false);

                            }


                            if (!TextUtils.isEmpty(bind)) {

                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();


                                TaskOperator.getInstance().CheckIsReLoginAndHandle(LoginActivity.this);


                                //DeliveryRouteAPI.setOlsParam();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                                finish();
                            } else {
                                /**
                                 * 未绑定手机号码, 进入绑定手机号界面
                                 */
                                Intent intent = new Intent(LoginActivity.this, BindMobileActivity.class);
                                intent.putExtra(BindMobileActivity.BIND_EXTRA, BindMobileActivity.BIND_FROM_LOGIN);
                                startActivity(intent);
                                finish();
                            }
                        } else {

                            // 鉴权失败
                            isWaiting = false;

                            WaitingProgressTool.closeshowProgress();
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onGetReqKey(String tag) {


                    }
                });
                break;
            }
            default:
                break;
        }
    }

    private static final int GET_CODE_TIMEOUT = 60; // 验证码等待超时
    private int mCodeTime = GET_CODE_TIMEOUT; // 验证码即时时间
    private String mCodeTimePhone = ""; // 发送验证码对应的电话号码
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

            if (mCodeTimePhone.equals(mMobileEdit.getText().toString())) {
                mVericodeText.setText(getResources().getString(R.string.login_resend));

            } else
                mVericodeText.setText(getResources().getString(R.string.login_get_vericode));

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
                        String vericodeHint = getResources().getString(R.string.login_sended_vericode);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mobile = mMobileEdit.getText().toString();
                String vercode = mVericodeEdit.getText().toString();
                if (!TextUtils.isEmpty(mobile) && mobile.length() == 11) {

                    if (mCodeTime > 0 && mCodeTime < 60 || mCodeTime <= 0) {
                        mVericodeText.setEnabled(false);
                    } else
                        mVericodeText.setEnabled(true);
                } else {
                    mVericodeText.setEnabled(false);
                }

                if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(vercode)) {
                    mMobileBtn.setEnabled(true);
                } else {
                    mMobileBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mVericodeEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String mobile = mMobileEdit.getText().toString();
                String vercode = mVericodeEdit.getText().toString();
                if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(vercode)) {
                    mMobileBtn.setEnabled(true);
                } else {
                    mMobileBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public class AllCapTransformationMethod extends ReplacementTransformationMethod {

        private char[] lower = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
                'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        private char[] upper = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
                'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        private boolean allUpper = false;

        public AllCapTransformationMethod(boolean needUpper) {
            this.allUpper = needUpper;
        }

        @Override
        protected char[] getOriginal() {
            if (allUpper) {
                return lower;
            } else {
                return upper;
            }
        }

        @Override
        protected char[] getReplacement() {
            if (allUpper) {
                return upper;
            } else {
                return lower;
            }
        }
    }
}
