/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: UserInfoActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 个人信息界面
 * @author: zhaoqy
 * @date: 2017年3月20日 下午6:45:08
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.me;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.setting.CldSetting;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.freighthelper.MainActivity;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.UserInfo;
import com.yunbaba.freighthelper.bean.UserInfo.ChangeTaskEnum;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.manager.UserManager;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.ui.customview.SelectPopup;
import com.yunbaba.freighthelper.ui.customview.SelectPopup.OnPopupListener;
import com.yunbaba.freighthelper.ui.dialog.ExitDialog;
import com.yunbaba.freighthelper.ui.dialog.ProgressDialog;
import com.yunbaba.freighthelper.ui.dialog.ProgressDialog.ProgressDialogListener;
import com.yunbaba.freighthelper.ui.dialog.SetNickDialog;
import com.yunbaba.freighthelper.utils.BitmapUtils;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.PermissionUtil;
import com.yunbaba.freighthelper.utils.PhotoHelper;
import com.yunbaba.freighthelper.utils.ResUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInfoActivity extends BaseActivity implements OnClickListener {

    public static final String TAG = "UserInfoActivity";
    public static final int REQUEST_PHOTO_TAKE = 1; // 拍照请求
    public static final int REQUEST_PHOTO_GALLERY = REQUEST_PHOTO_TAKE + 1; // 从相册选取请求
    public static final int REQUEST_PHOTO_CUT = REQUEST_PHOTO_GALLERY + 1; // 图片裁剪请求
    public static final int REQUEST_SET_SEX = REQUEST_PHOTO_CUT + 1; // 修改性别请求
    public static final int REQUEST_SET_AREA = REQUEST_SET_SEX + 1; // 修改地区请求

    private Context mContext;
    private LinearLayout mRoot;
    private ImageView mBack;
    private TextView mTitle;
    private ImageView mNewsImg;
    private ImageView mAvatar;
    private TextView mNickName;
    private TextView mUserName;
    private TextView mArea;
    private TextView mSex;
    private TextView mMobile;
    private SelectPopup mPopup;
    private UserInfo mUserInfo;
    private UserInfo mUserInfoTmp;
    private String NN_path = null;// 如果是中兴n5s，保存拍照的相片路径改变
    private String cutPath = null;// 裁减后照片路径

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_userinfo;
    }

    @Override
    protected void initViews() {
        mRoot = (LinearLayout) findViewById(R.id.userinfo);
        mBack = (ImageView) findViewById(R.id.title_left_img);
        mTitle = (TextView) findViewById(R.id.title_text);
        mNewsImg = (ImageView) findViewById(R.id.title_right_img);
        mAvatar = (ImageView) findViewById(R.id.userinfo_avatar);
        mNickName = (TextView) findViewById(R.id.userinfo_nickname);
        mUserName = (TextView) findViewById(R.id.userinfo_account);
        mArea = (TextView) findViewById(R.id.userinfo_area);
        mSex = (TextView) findViewById(R.id.userinfo_sex);
        mMobile = (TextView) findViewById(R.id.userinfo_mobile);
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);
        mNewsImg.setOnClickListener(this);
        findViewById(R.id.userinfo_avatar_layout).setOnClickListener(this);
        findViewById(R.id.userinfo_nickname_layout).setOnClickListener(this);
        findViewById(R.id.userinfo_area_layout).setOnClickListener(this);
        findViewById(R.id.userinfo_sex_layout).setOnClickListener(this);
        findViewById(R.id.userinfo_mobile_layout).setOnClickListener(this);
        findViewById(R.id.userinfo_modify_pwd_layout).setOnClickListener(this);
        findViewById(R.id.userinfo_logout_layout).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mContext = this;
        mBack.setVisibility(View.VISIBLE);
        mTitle.setText(R.string.userinfo_center);
        mNewsImg.setVisibility(View.GONE);
        mUserInfo = UserManager.getInstance().getUserInfo();
        mUserInfoTmp = UserManager.getInstance().getTmpUserInfo();

        cutPath = PhotoHelper.getPhotoPath(0);
        MLog.d(TAG, "cutPath: " + cutPath);
        NN_path = PhotoHelper.getPath();
        MLog.d(TAG, "NN_path: " + NN_path);

        mPopup = new SelectPopup(this, new OnPopupListener() {

            /**
             * 拍照
             */
            @TargetApi(23)
            @Override
            public void onTakePhoto() {
                /*
				 * try { openCamra(); } catch (Exception e) {
				 * Toast.makeText(mContext, "调用摄像头失败", Toast.LENGTH_SHORT)
				 * .show(); }
				 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 在6.0 系统中请求某些权限需要检查权限
                    if (!hasPermission()) {
                        // 动态请求拍照权限
                        requestPermissions(
                                new String[]{Manifest.permission.CAMERA},
                                222);
                    } else {
                        openCamra();
                    }
                } else {
                    openCamra();
                }
            }

            /**
             * 从相册中获取
             */
            @Override
            public void onChoosePhoto() {
                PhotoHelper.onChoosePhoto(UserInfoActivity.this,
                        REQUEST_PHOTO_GALLERY);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateUI();
    }

    @Override
    protected void updateUI() {
        //updateNews();
        /**
         * 设置昵称
         */
        String alias = mUserInfo.getUserAlias();
        if (!TextUtils.isEmpty(alias)) {
            // mNickName.setText(alias);
            if (alias.length() > 12) {
                alias = alias.substring(0, 12);
            }

            Rect bounds = new Rect();
            TextPaint paint = mNickName.getPaint();
            paint.getTextBounds(alias, 0, alias.length(), bounds);
            final String name = alias;
            mNickName.post(new Runnable() {

                @Override
                public void run() {
                    String ellipsizeStr = (String) TextUtils.ellipsize(name,
                            (TextPaint) mNickName.getPaint(),
                            mNickName.getMeasuredWidth() - 10,
                            TextUtils.TruncateAt.END);
                    mNickName.setText(ellipsizeStr);
                }
            });
        } else {
            String useralias = CldSetting.getString("useralias");
            if (!TextUtils.isEmpty(useralias)) {
                mNickName.setText(useralias);
            } else {
                alias = getResources().getString(R.string.me_set_nickname);
                mNickName.setText(alias);
            }
        }

        /**
         * 设置用户名
         */
        String username = mUserInfo.getUserName();
        if (TextUtils.isEmpty(username)) {
            username = CldSetting.getString("username");
        }
        mUserName.setText(username);

        /**
         * 设置区域
         */
        String address = mUserInfo.getAddress();
        if (TextUtils.isEmpty(address)) {
            address = CldSetting.getString("address");
            if (TextUtils.isEmpty(address)) {
                address = getResources().getString(R.string.area_select);
            }
        }
        mArea.setText(address);

        /**
         * 设置性别
         */
        String sex = ResUtils.getSex(this, mUserInfo.getSex());
        mSex.setText(sex);

        /**
         * 设置手机号
         */
        String mobile = mUserInfo.getMobile();
        if (TextUtils.isEmpty(mobile)) {
            mobile = CldSetting.getString("mobile");
        }
        mMobile.setText(mobile);

        /**
         * 设置头像
         */
        Bitmap headBitmap = PhotoHelper.getPhotoBitmap();
        if (headBitmap != null) {
            mAvatar.setImageBitmap(PhotoHelper.getCircleBitmap(headBitmap));
        } else {
            String avatar = mUserInfo.getImgHead();
            MLog.d(TAG, "avatar: " + avatar);
            if (!TextUtils.isEmpty(avatar)) {
                Picasso.with(this).load(avatar).into(mAvatar);
            } else {
                Bitmap headimg = BitmapUtils.getHeadImgBitmap(MainApplication
                        .getContext());
                if (headimg != null) {
                    mAvatar.setImageBitmap(headimg);
                }
            }
        }
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
//                Intent intent = new Intent(this, MsgActivity.class);
//                startActivity(intent);
                break;
            }
            case R.id.userinfo_avatar_layout: {


                if (!CldPhoneNet.isNetConnected()) {
                    Toast.makeText(mContext, R.string.userinfo_set_failed,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (PermissionUtil.isNeedPermissionForStorage(this)) {

                    Toast.makeText(this, "请打开储存空间权限", Toast.LENGTH_SHORT).show();
                    return;

                }

                mPopup.showAtLocation(mRoot, Gravity.TOP, 0, 0);
                break;
            }
            case R.id.userinfo_nickname_layout: {
                setNickName();
                break;
            }
            case R.id.userinfo_area_layout: {
                Intent intent = new Intent(this, AreaActivity.class);
                startActivityForResult(intent, REQUEST_SET_AREA);
                break;
            }
            case R.id.userinfo_sex_layout: {
                Intent intent = new Intent(this, SetSexActivity.class);
                MLog.e("zhaoqy", "sex= " + mUserInfo.getSex());
                intent.putExtra("sex", mUserInfo.getSex());
                startActivityForResult(intent, REQUEST_SET_SEX);
                break;
            }
            case R.id.userinfo_mobile_layout: {
                Intent intent = new Intent(this, BindMobileActivity.class);
                intent.putExtra(BindMobileActivity.BIND_EXTRA,
                        BindMobileActivity.BIND_FROM_USERINFO);
                startActivity(intent);
                break;
            }
            case R.id.userinfo_modify_pwd_layout: {
                /**
                 * 登录密码为空，则设置新密码；否则修改密码
                 */
			/*
			 * String pwd = AccountAPI.getInstance().getLoginPwd();
			 * MLog.e(TAG, "pwd: " + pwd); if (TextUtils.isEmpty(pwd)) {
			 * Intent intent = new Intent(this, SetPwdActivity.class);
			 * intent.putExtra(SetPwdActivity.SET_PWD_EXTRA,
			 * SetPwdActivity.SET_PWD_SET_NEW_PWD); startActivity(intent); }
			 * else { Intent intent = new Intent(this, ModifyPwdActivity.class);
			 * startActivity(intent); }
			 */
                /**
                 * 用手机+验证码，则设置新密码； 账号+密码登录，则修改密码; 1-手机号+验证码；2-账号+密码; 3-自动登录 modify
                 * 2017-4-28
                 */
                switch (mUserInfo.getLoginType()) {
                    case 1: {
                        Intent intent = new Intent(this, SetPwdActivity.class);
                        intent.putExtra(SetPwdActivity.SET_PWD_EXTRA,
                                SetPwdActivity.SET_PWD_SET_NEW_PWD);
                        startActivity(intent);
                        break;
                    }
                    case 2: {


                        Intent intent = new Intent(this, ModifyPwdActivity.class);
                        startActivity(intent);
                        break;
                    }

                    default: {

                        if (GeneralSPHelper.getInstance(UserInfoActivity.this).isMobileLogin()) {

                            Intent intent = new Intent(this, SetPwdActivity.class);
                            intent.putExtra(SetPwdActivity.SET_PWD_EXTRA,
                                    SetPwdActivity.SET_PWD_SET_NEW_PWD);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(this, ModifyPwdActivity.class);
                            startActivity(intent);
                        }


                        break;
                    }
                }
                break;
            }
            case R.id.userinfo_logout_layout: {
                String title = getResources().getString(R.string.dialog_exit);
                String content = getResources().getString(
                        R.string.dialog_exit_content);
                String cancel = getResources().getString(R.string.dialog_cancel);
                String sure = getResources().getString(R.string.dialog_sure);
                ExitDialog dialog = new ExitDialog(this, title, content, cancel,
                        sure,0, new ExitDialog.IExitDialogListener() {

                    @Override
                    public void OnCancel() {

                    }

                    @Override
                    public void OnSure() {
                        logout();
                    }
                });
                dialog.show();
                break;
            }
            default:
                break;
        }
    }

    private void logout() {
        if (!CldPhoneNet.isNetConnected()) {
            /**
             * 无网络，则直接进入登录界面
             */
            //AccountAPI.getInstance().loginOut();

            //Toast.makeText(this, R.string.common_network_abnormal,
            //		Toast.LENGTH_SHORT).show();

//			CldDalKAccount.getInstance().uninit();
//			CldDalKAccount.getInstance().setLoginPwd("");
//			CldKAccount.getInstance().setLoginStatus(0);
            AccountEvent event = new AccountEvent(MessageId.MSGID_LOGOUT_SUCCESS, 0);

            EventBus.getDefault().post(event);


        } else {
            /**
             * 登出
             */
            AccountAPI.getInstance().loginOut();
        }
    }

    private void setNickName() {
        String title = getResources().getString(R.string.dialog_nick_title);
        String content = mNickName.getText().toString();
        String alias = getResources().getString(R.string.me_set_nickname);
        if (content.equals(alias)) {
            content = "";
        }

        String cancel = getResources().getString(R.string.dialog_cancel);
        String sure = getResources().getString(R.string.dialog_sure);
        SetNickDialog dialog = new SetNickDialog(this, title, content, cancel,
                sure, new SetNickDialog.ISetNicDialogListener() {

            @Override
            public void OnCancel() {

            }

            @Override
            public void OnSure(String nick) {
                if (!TextUtils.isEmpty(nick)) {
                    onSure(nick);
                }
            }
        });
        dialog.show();
    }

    private void onSure(String nick) {
        String nickname = mNickName.getText().toString();
        if (!CldPhoneNet.isNetConnected()) {
            Toast.makeText(mContext, R.string.userinfo_set_failed,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (nick.length() <= 1) {
            Toast.makeText(mContext, R.string.userinfo_useralis_lenth,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String regx = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(nick);
        if (!matcher.matches()) {
            // 不包含特殊字符
            Toast.makeText(mContext, R.string.userinfo_useralis_specchar,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nick.equals(nickname)) {
            String str = getResources().getString(
                    R.string.common_network_data_update);
            int sex = mUserInfo.getSex();
            ProgressDialog.showProgress(mContext, str,
                    new ProgressDialogListener() {

                        @Override
                        public void onCancel() {
                        }
                    });

            mUserInfoTmp.changeUserAlias(nick);
            /**
             * 更新昵称
             */
            AccountAPI.getInstance().updateUserInfo(null, nick, null, null,
                    sex + "", null, null);
        }
    }

    @Override
    protected void messageEvent(AccountEvent event) {

        switch (event.msgId) {
            case MessageId.MSGID_USERINFO_GETDETAIL_SUCCESS: {
                updateUI();
                mUserInfo.setSuccess(0);
                break;
            }
            case MessageId.MSGID_USERINFO_GETDETAIL_FAILED: {
                mUserInfo.setSuccess(1);
                break;
            }
            case MessageId.MSGID_USERINFO_UPDATE_FAILED: {
                if (ProgressDialog.isShowProgress()) {
                    ProgressDialog.cancelProgress();
                }

                int[] status = mUserInfo.getChangeStatus();
                if (status[0] == 1) {
                    Toast.makeText(this, "操作失败，请检查网络。", Toast.LENGTH_SHORT).show();
                    mUserInfo.resetChangeStatus(ChangeTaskEnum.eSEX);
                    Toast.makeText(mContext, R.string.userinfo_set_failed,
                            Toast.LENGTH_SHORT).show();
                } else if (status[1] == 1) {
                    Toast.makeText(this, "操作失败，请检查网络。", Toast.LENGTH_SHORT).show();
                    mUserInfo.resetChangeStatus(ChangeTaskEnum.eUSERALIAS);
                    Toast.makeText(mContext, R.string.userinfo_set_failed,
                            Toast.LENGTH_SHORT).show();
                } else if (status[2] == 1) {
                    Toast.makeText(this, "操作失败，请检查网络。", Toast.LENGTH_SHORT).show();
                    mUserInfo.resetChangeStatus(ChangeTaskEnum.eADDRESS);
                    Toast.makeText(mContext, R.string.userinfo_set_failed,
                            Toast.LENGTH_SHORT).show();
                } else if (status[3] == 1) {
                    Toast.makeText(this, "操作失败，请检查网络。", Toast.LENGTH_SHORT).show();
                    mUserInfo.resetChangeStatus(ChangeTaskEnum.eIMGHEAD);
                    PhotoHelper.saveUserPhotoUrl("ERROR : NO NET !");
                    Toast.makeText(mContext, R.string.userinfo_set_failed,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case MessageId.MSGID_USERINFO_UPDATE_SUCCESS: {
                if (ProgressDialog.isShowProgress()) {
                    ProgressDialog.cancelProgress();
                }

                int[] status = mUserInfoTmp.getChangeStatus();
                if (status[0] == 1) {
                    Toast.makeText(this, "修改性别成功", Toast.LENGTH_SHORT).show();
                    /**
                     * 修改性别
                     */
                    mUserInfo.setSex(mUserInfoTmp.getSex());
                    mUserInfo.resetChangeStatus(ChangeTaskEnum.eSEX);
                    mUserInfoTmp.resetChangeStatus(ChangeTaskEnum.eSEX);

                    String sex = ResUtils.getSex(this, mUserInfo.getSex());
                    mSex.setText(sex);
                } else if (status[1] == 1) {
                    Toast.makeText(this, "修改昵称成功", Toast.LENGTH_SHORT).show();
                    /**
                     * 修改昵称
                     */
                    mUserInfo.setUserAlias(mUserInfoTmp.getUserAlias());
                    mUserInfo.resetChangeStatus(ChangeTaskEnum.eUSERALIAS);
                    mUserInfoTmp.resetChangeStatus(ChangeTaskEnum.eUSERALIAS);

                    mNickName.setText(mUserInfo.getUserAlias());
                } else if (status[2] == 1) {
                    Toast.makeText(this, "修改地区成功", Toast.LENGTH_SHORT).show();
                    /**
                     * 修改地区
                     */
                    mUserInfo.setAddress(mUserInfoTmp.getAddress());
                    mUserInfo.resetChangeStatus(ChangeTaskEnum.eADDRESS);
                    mUserInfoTmp.resetChangeStatus(ChangeTaskEnum.eADDRESS);

                    mArea.setText(mUserInfo.getAddress());
                } else if (status[3] == 1) {
                    /**
                     * 修改头像
                     */
                    Toast.makeText(this, "修改头像成功", Toast.LENGTH_SHORT).show();
                    // CldUserInfo info =
                    // AccountAPI.getInstance().getUserInfoDetail();


                    Bitmap headBitmap = PhotoHelper.getPhotoBitmap(cutPath);
                    MLog.d(TAG, "headBitmap: " + headBitmap);
                    if (headBitmap != null) {
                        mAvatar.setImageBitmap(PhotoHelper.getCircleBitmap(headBitmap));
                    }

                    PhotoHelper.saveBitmapToTrueHead(headBitmap);


                    mUserInfo.setSuccess(event.errCode);
                    // MLog.e("zhaoqy", "imghead: " + info.getPhotoPath());
                    // mUserInfo.setImgHead(info.getPhotoPath());
                    mUserInfo.resetChangeStatus(ChangeTaskEnum.eIMGHEAD);
                    mUserInfoTmp.resetChangeStatus(ChangeTaskEnum.eIMGHEAD);
                }
                break;
            }
            case MessageId.MSGID_LOGOUT_SUCCESS: {
                AccountAPI.getInstance().setLoginPwd("");
                AccountAPI.getInstance().setLoginStatus(0);
                AccountAPI.getInstance().uninit();

                mUserInfo.reset();
                mUserInfoTmp.reset();
                /**
                 * 退出所有界面，进入登录界面
                 */
                //Toast.makeText(mContext, "登出成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);

                intent.putExtra("finishall", true);
                //	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //AppManager.getInstance().finishAllActivity();

                finish();
                //	CommonTool.restartApplication(context);
                break;
            }
            case MessageId.MSGID_LOGOUT_FAILED: {
              //  Toast.makeText(mContext, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();

                AccountEvent event2 = new AccountEvent(MessageId.MSGID_LOGOUT_SUCCESS, 0);

                EventBus.getDefault().post(event2);
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
            case MessageId.MSGID_USERINFO_BIND_MOBILE_SUCCESS:
            case MessageId.MSGID_USERINFO_REVISE_MOBILE_SUCCESS: {

                MLog.i(TAG, "MSGID_USERINFO_REVISE_MOBILE_SUCCESS");
                /**
                 * 绑定手机号成功 更新手机号
                 */
                String mobile = mUserInfo.getMobile();
                MLog.i(TAG, "mobile= " + mobile);
                if (!TextUtils.isEmpty(mobile)) {
                    mMobile.setText(mobile);
                }
                break;
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQUEST_PHOTO_TAKE: // 拍照请求号
                    int level = Build.VERSION.SDK_INT;
                    String model = Build.MODEL;
                    // 针对中兴n5s，特殊处理路径
                    if ("ZTE N5S".equals(model)) {
                        File temp = new File(NN_path + "/photo.jpg");
                        if (!TextUtils.isEmpty(NN_path)) {
                            PhotoHelper.cropImageUri(this, temp, 1,
                                    1, PhotoHelper.PHOTOSIZE,
                                    PhotoHelper.PHOTOSIZE, REQUEST_PHOTO_CUT);
                        }
                        /**
                         * 目前只针对5.0系统以上且小米2手机修改。 原因：小米的5.0
                         * ROM上拍照裁减后保存图片到本地，无法保存（由系统负责，写入数据为0B）
                         * 处理：拍照后直接压缩处理图片显示（去掉裁减图片，注：其他版本手机等都保持不变）
                         */
                    } else if (("MI 2".equals(model)) || "Moto X Pro".equals(model)
                            && level > 20) {
                        // loadUserPhoto();

                        if (!CldPhoneNet.isNetConnected()) {
                            // 如果没网就不在本地保存头像的url，防止下一次登录会显示本地头像而不是从服务器获取
                            PhotoHelper.saveUserPhotoUrl("ERROR : NO NET !");
                            Toast.makeText(mContext, R.string.userinfo_set_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else
                            updatePhoto();
                    } else {
                        PhotoHelper.cropImageUri(this,
                                new File(cutPath), 1, 1,
                                PhotoHelper.PHOTOSIZE, PhotoHelper.PHOTOSIZE,
                                REQUEST_PHOTO_CUT);
                    }
                    break;
                case REQUEST_PHOTO_GALLERY: // 从相册选取请求号
                    MLog.d(TAG, "相册选取返回");
                    // 如果是默认的则显示默认图片
                    //if (PhotoHelper.isSpecModel()) {
                    // 对魅族PRO 5 返回的Uri进行裁剪
                    Uri uri = data.getData();
                    PhotoHelper.startCrop(this, uri,
                            Uri.fromFile(new File(PhotoHelper.getPhotoPath(0))), REQUEST_PHOTO_CUT);
//				} else {
//					if (!CldPhoneNet.isNetConnected()) {
//						// 如果没网就不在本地保存头像的url，防止下一次登录会显示本地头像而不是从服务器获取
//						PhotoHelper.saveUserPhotoUrl("ERROR : NO NET !");
//					}
//					// loadUserPhoto();
//					updatePhoto();
//				}
                    break;
                case REQUEST_PHOTO_CUT: // 图片裁剪请求号

                    MLog.d(TAG, "裁剪返回 ");
                    if (!CldPhoneNet.isNetConnected()) {
                        // 如果没网就不在本地保存头像的url，防止下一次登录会显示本地头像而不是从服务器获取
                        PhotoHelper.saveUserPhotoUrl("ERROR : NO NET !");
                        Toast.makeText(mContext, R.string.userinfo_set_failed,
                                Toast.LENGTH_SHORT).show();
                    } else
                        updatePhoto();
                    break;
                case REQUEST_SET_SEX: {
                    // data为B中回传的Intent
                    Bundle b = data.getExtras();
                    int sex = b.getInt("sex");
                    modifySex(sex);
                    break;
                }
                case REQUEST_SET_AREA: {
                    Bundle b = data.getExtras();
                    String address = b.getString("address");
                    modifyAddress(address);
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void updatePhoto() {


        MLog.d(TAG, "cutPath: " + cutPath);
        byte[] photoData = PhotoHelper.getUserPhotoData(cutPath);
        mUserInfoTmp.changeImgHead(cutPath);
        AccountAPI.getInstance().updateUserInfo(null, null, null, null, null,
                null, photoData);
    }

    private void modifyAddress(String address) {
        if (!CldPhoneNet.isNetConnected()) {
            Toast.makeText(mContext, R.string.userinfo_set_failed,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(address)
                && !address.equals(mUserInfo.getAddress())) {
            String str = getResources().getString(
                    R.string.common_network_data_update);
            ProgressDialog.showProgress(mContext, str,
                    new ProgressDialogListener() {

                        @Override
                        public void onCancel() {
                        }
                    });
            mUserInfoTmp.changeAddress(address);
            /**
             * 更新区域
             */
            AccountAPI.getInstance().updateUserInfo(null, null, null, null,
                    null, address, null);
        }
    }

    private void modifySex(int sex) {
        if (!CldPhoneNet.isNetConnected()) {
            Toast.makeText(mContext, R.string.userinfo_set_failed,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if ((sex == 2 && mUserInfo.getSex() != 2)
                || (sex == 1 && mUserInfo.getSex() != 1)) {
            String str = getResources().getString(
                    R.string.common_network_data_update);
            ProgressDialog.showProgress(mContext, str,
                    new ProgressDialogListener() {

                        @Override
                        public void onCancel() {
                        }
                    });
            mUserInfoTmp.changeSex(sex);
            /**
             * 更新性别
             */
            AccountAPI.getInstance().updateUserInfo(null, null, null, null,
                    sex + "", null, null);
        }
    }

    @SuppressLint("NewApi")
    private boolean hasPermission() {
        String permission = Manifest.permission.CAMERA;
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void openCamra() {
        PhotoHelper.openCamra(this, cutPath, NN_path, REQUEST_PHOTO_TAKE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 222:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    openCamra();
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

}
