/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: MeFragment.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.fragment
 * @Description: 我的fragment
 * @author: zhaoqy
 * @date: 2017年3月11日 下午3:41:35
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.setting.CldSetting;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.MajorMainFragment;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.bean.UserInfo;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.bean.eventbus.AuthEvent;
import com.yunbaba.freighthelper.bean.eventbus.NewVersionEvent;
import com.yunbaba.freighthelper.bean.eventbus.SelectMyCompanyEvent;
import com.yunbaba.freighthelper.manager.UserManager;
import com.yunbaba.freighthelper.ui.activity.me.AboutActivity;
import com.yunbaba.freighthelper.ui.activity.me.CarInfoActivity;
import com.yunbaba.freighthelper.ui.activity.me.SelectMyCompanyActivity;
import com.yunbaba.freighthelper.ui.activity.me.UserInfoActivity;
import com.yunbaba.freighthelper.ui.activity.me.contact.ContactActivity;
import com.yunbaba.freighthelper.ui.activity.me.contact.MyStoresActivity;
import com.yunbaba.freighthelper.utils.BitmapUtils;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.PhotoHelper;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.freighthelper.utils.ToastUtils;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldDalKDelivery;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam;
import com.yunbaba.ols.tools.AppInfoUtils;
import com.squareup.picasso.Picasso;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MeFragment extends MajorMainFragment implements OnClickListener {

    private static final String TAG = "MeFragment";
    private TextView mTitle;
    //	private ImageView mNewsImg;
    private ImageView mAvatar, iv_updateredpoint;
    private TextView mNickName;
    private TextView mUserName;
    private PercentRelativeLayout prl_company;
    private TextView tv_company;

    private UserInfo mUserInfo;

    public static MeFragment newInstance() {

        Bundle args = new Bundle();
        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        initViews(view);
        setListener(view);
        initData();
        updateUI();

        checkNewVersion();
       // setData();
        EventBus.getDefault().register(this);
        return view;
    }

    private void checkNewVersion() {
        int version = GeneralSPHelper.getInstance(_mActivity).isMeNewRemind();


        if (version > 0) {

            if (version > AppInfoUtils.getVerCode(_mActivity)) {

                iv_updateredpoint.setVisibility(View.VISIBLE);
            } else {
                iv_updateredpoint.setVisibility(View.GONE);


            }

        } else {

            iv_updateredpoint.setVisibility(View.GONE);

        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initViews(View view) {
        mTitle = (TextView) view.findViewById(R.id.title_text);
        //	mNewsImg = (ImageView) view.findViewById(R.id.title_right_img);
        mAvatar = (ImageView) view.findViewById(R.id.me_userinfo_avatar);
        mNickName = (TextView) view.findViewById(R.id.me_userinfo_nickname);
        mUserName = (TextView) view.findViewById(R.id.me_userinfo_account);
        tv_company = (TextView) view.findViewById(R.id.tv_company);
        prl_company =  (PercentRelativeLayout) view.findViewById(R.id.prl_company);
        iv_updateredpoint = (ImageView) view.findViewById(R.id.iv_updateredpoint);
    }

    private void setListener(View view) {
        //	mNewsImg.setOnClickListener(this);
        view.findViewById(R.id.me_userinfo).setOnClickListener(this);
        view.findViewById(R.id.me_carinfo).setOnClickListener(this);
        view.findViewById(R.id.me_about).setOnClickListener(this);
        view.findViewById(R.id.me_contactinfo).setOnClickListener(this);
        view.findViewById(R.id.me_reportinfo).setOnClickListener(this);
        view.findViewById(R.id.prl_company).setOnClickListener(this);
        view.findViewById(R.id.me_markstore).setOnClickListener(this);
    }

    private void initData() {
        mTitle.setText(R.string.main_bottom_me);

        if (!CldPhoneNet.isNetConnected()) {
            mTitle.setText("我的(网络断开)");
        } else if (!AccountAPI.getInstance().isLogined()) {
            mTitle.setText("我的(离线)");

        }

//		else if(TextUtils.isEmpty(CldDalKDelivery.getInstance().getCldDeliveryKey())){
//			mTitle.setText("我的(账户等待鉴权)");
//		}
        //	mNewsImg.setVisibility(View.VISIBLE);
    }

    private void updateUI() {
        //	updateNews();
        mUserInfo = UserManager.getInstance().getUserInfo();
        /**
         * 设置昵称
         */
        String alias = mUserInfo.getUserAlias();
        if (!TextUtils.isEmpty(alias)) {
            //mNickName.setText(alias);
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
                    String ellipsizeStr = (String) TextUtils.ellipsize(
                            name, (TextPaint) mNickName.getPaint(),
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
         * 设置头像
         */
        Bitmap headBitmap = PhotoHelper.getPhotoBitmap();
        if (headBitmap != null) {
            mAvatar.setImageBitmap(PhotoHelper.getCircleBitmap(headBitmap));
        } else {
            String avatar = mUserInfo.getImgHead();
            MLog.d(TAG, "avatar: " + avatar);
            if (!TextUtils.isEmpty(avatar)) {
                Picasso.with(getActivity()).load(avatar).into(mAvatar);
            } else {
                Bitmap headimg = BitmapUtils.getHeadImgBitmap(MainApplication
                        .getContext());
                if (headimg != null) {
                    mAvatar.setImageBitmap(headimg);
                }
            }
        }

        setData();
    }
//
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
            case R.id.title_right_img: {
//			Intent intent = new Intent(getActivity(), MsgActivity.class);
//			startActivity(intent);
                break;
            }
            case R.id.me_userinfo: {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.me_carinfo: {
                Intent intent = new Intent(getActivity(), CarInfoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.me_about: {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);

                //test
                //DeliveryApi.getInstance().batchReportTaskstoreStatus();

//			CldKDeliveryAPI.getInstance().uploadDeviceLogTest("test");
                break;
            }
            case R.id.me_contactinfo: {
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.me_reportinfo: {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.prl_company: {
                Intent intent = new Intent(getActivity(), SelectMyCompanyActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.me_markstore: {


                WaitingProgressTool.showProgress(this._mActivity);

                final String corpId = SPHelper.getInstance(getActivity()).ReadStoreSelectCompanyBean().getCorpId();

                CldKDeliveryAPI.getInstance().getAuthInfoList(new CldKDeliveryAPI.ICldAuthInfoListener() {
                    @Override
                    public void onGetResult(int errCode, List<CldSapKDeliveryParam.AuthInfoList> lstOfResult) {

                        WaitingProgressTool.closeshowProgress();

                        if (errCode != 0) {
                            Toast.makeText(getContext(), "获取门店权限失败", Toast.LENGTH_LONG).show();
                            return;
                        }

                        SPHelper.getInstance(getContext()).writeStoreAuth(lstOfResult);


                        boolean readPerm = DeliveryApi.getInstance().isHasAuthForStore(getContext(), corpId, 1);
                        boolean updatePerm = DeliveryApi.getInstance().isHasAuthForStore(getContext(), corpId, 2);

                        if ( readPerm && updatePerm ) {
                            Intent intent = new Intent(getActivity(), MyStoresActivity.class);
                            startActivity(intent);
                        } else {

                            ToastUtils.showMessage(getContext(), "您未开通此权限");
                        }

                    }

                    @Override
                    public void onGetReqKey(String arg0) {

                    }
                });


                break;

            }


            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //EventBus.getDefault().unregister(this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewVersionEvent event) {

        checkNewVersion();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SelectMyCompanyEvent event) {

       // checkNewVersion();
      setData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AuthEvent event) {

        initData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AccountEvent event) {
        switch (event.msgId) {
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_SUCCESS: {

                initData();


                break;
            }
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_FAILED_NET:
            case MessageId.MSGID_LOGIN_AUTO_LOGIN_FAILED: {
                /**
                 * 自动登录失败，进入登录界面
                 */
                //startActivity(LoginActivity.class);

                //restartApplication();

                initData();

                break;
            }
            case MessageId.MSGID_USERINFO_GETDETAIL_SUCCESS:
            case MessageId.MSGID_USERINFO_GETDETAIL_FAILED: {


                initData();

                setData();

                break;
            }
            default:
                break;
        }
    }


    public void setData() {


      //  DeliveryApi.getInstance().GetMyMarkStoreRecord();




        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                CorpBean currentid = SPHelper.getInstance(_mActivity).ReadStoreSelectCompanyBean();




                HashMap<String, String> mtmplistdata = new HashMap<>();
                for (CldSapKDeliveryParam.CldDeliGroup item : CldDalKDelivery.getInstance().getLstOfMyGroups()) {

                    mtmplistdata.put(item.corpId, item.corpName);

                }

                CorpBean bean;
                CorpBean curbean = null;

                boolean isNotFoundCompany = true;

                Iterator<Map.Entry<String, String>> iter = mtmplistdata.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();

                    String id = entry.getKey();

                    // String name = entry.getValue();

                    if (currentid != null && id.equals(currentid.getCorpId())) {
                        curbean = new CorpBean();
                        curbean.setCorpId(entry.getKey());
                        curbean.setCorpName(entry.getValue());
                        isNotFoundCompany = false;
                    } else {

                        if(curbean == null) {
                            bean = new CorpBean();

                            bean.setCorpId(id);
                            bean.setCorpName(entry.getValue());
                            curbean = bean;

                        }




                    }

                }

                 CorpBean tccurbean = curbean;

                if(currentid == null && curbean!=null){
                    SPHelper.getInstance(_mActivity). WriteStoreSelectCompanyBean(curbean);
                }else if(currentid!=null && isNotFoundCompany && curbean!=null){
                    SPHelper.getInstance(_mActivity). WriteStoreSelectCompanyBean(curbean);
                }else if(currentid!=null && !isNotFoundCompany ){

                    tccurbean = currentid;

                }else if(currentid!=null && curbean == null){

                    tccurbean = currentid;

                }


                final  CorpBean ccurbean = tccurbean;

                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ( ccurbean  != null) {



                            if(tv_company!=null)
                            tv_company.setText(ccurbean.getCorpName());

                            if(prl_company!=null)
                            prl_company.setVisibility(View.VISIBLE);
                          //  initHeadFootView(curbean);
                            //	mlistdata.add(new CorpBean());

                        } else {

                         //   finish();

                            if(prl_company!=null)
                            prl_company.setVisibility(View.GONE);
                            //initHeadFootView(null);

                        }
                    }
                });


            }
        });



    }


}
