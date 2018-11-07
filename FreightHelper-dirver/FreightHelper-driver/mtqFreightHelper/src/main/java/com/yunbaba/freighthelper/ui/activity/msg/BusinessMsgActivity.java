/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: BusinessMsgActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.msg
 * @Description: 企业消息界面
 * @author: zhaoqy
 * @date: 2017年3月22日 下午3:14:59
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.msg;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cld.navisdk.CldNaviManager.RoutePlanPreference;
import com.cld.navisdk.routeplan.CldRoutePlaner.RoutePlanListener;
import com.cld.navisdk.routeplan.RoutePlanNode;
import com.cld.navisdk.routeplan.RoutePlanNode.CoordinateType;
import com.cld.navisdk.util.view.CldProgress;
import com.cld.nv.location.CldCoordUtil;
import com.cld.nv.map.CldMapApi;
import com.yunbaba.api.car.CarAPI;
import com.yunbaba.api.map.NavigateAPI;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.bean.eventbus.NewMsgEvent;
import com.yunbaba.freighthelper.bean.msg.Filter;
import com.yunbaba.freighthelper.bean.msg.MsgContent;
import com.yunbaba.freighthelper.bean.msg.MsgInfo;
import com.yunbaba.freighthelper.db.MsgContentTable;
import com.yunbaba.freighthelper.db.MsgInfoTable;
import com.yunbaba.freighthelper.manager.MsgManager;
import com.yunbaba.freighthelper.ui.activity.RoutePreviewActivity;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.ui.activity.task.FreightPointActivity;
import com.yunbaba.freighthelper.ui.adapter.BusinessMsgAdapter;
import com.yunbaba.freighthelper.ui.adapter.BusinessMsgAdapter.IBusinessMsgClickListener;
import com.yunbaba.freighthelper.ui.adapter.BusinessMsgAdapter.IJoinedListener;
import com.yunbaba.freighthelper.ui.adapter.BusinessMsgAdapter.IRejectedListener;
import com.yunbaba.freighthelper.ui.dialog.ConfirmDialog;
import com.yunbaba.freighthelper.ui.dialog.ExitDialog;
import com.yunbaba.freighthelper.ui.dialog.FilterBusinessDailog;
import com.yunbaba.freighthelper.ui.dialog.MsgDialog;
import com.yunbaba.freighthelper.utils.CallUtil;
import com.yunbaba.freighthelper.utils.DownloadUtil;
import com.yunbaba.freighthelper.utils.DownloadUtil.DownLoadResultListener;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.NetWorkUtils;
import com.yunbaba.freighthelper.utils.PlayVoiceTool;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.freighthelper.utils.ToastUtils;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliGroup;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import hmi.packages.HPDefine.HPWPoint;

public class BusinessMsgActivity extends BaseActivity implements OnClickListener, IBusinessMsgClickListener {

    private Context mContext;
    private ImageView mBack;
    private TextView mTitle;
    private TextView mFilter;
    private ListView mListView;

    private ImageView ivPreSpeech;
    private View mEmptyView;
    private BusinessMsgAdapter mAdapter;
    private List<MsgInfo> mMsgList;
    private FilterBusinessDailog mDialog;
    private List<Filter> mFilters = new ArrayList<Filter>();
    private AnimationDrawable animationDrawable;
    public static final int pageSize = 10;
    int pageIndex = 1;
    boolean loadFinish = false;

    PercentRelativeLayout pbWaiting;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_msg_business_msg;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initViews() {
        mBack = (ImageView) findViewById(R.id.title_left_img);
        mTitle = (TextView) findViewById(R.id.title_text);
        mFilter = (TextView) findViewById(R.id.title_right_text);
        mListView = (ListView) findViewById(R.id.business_msg_listview);
        pbWaiting = (PercentRelativeLayout) findViewById(R.id.pb_waiting);
        animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.anim_speech_playback);
        LayoutInflater mLayoutInflater = LayoutInflater.from(this);
        mEmptyView = mLayoutInflater.inflate(R.layout.view_empty_filter_msg, null);
        mEmptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mEmptyView.setVisibility(View.GONE);
        ((ViewGroup) mListView.getParent()).addView(mEmptyView);
        mListView.setEmptyView(mEmptyView);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            // AbsListView view 这个view对象就是listview
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        loadMore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);
        mFilter.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mContext = this;
        mBack.setVisibility(View.VISIBLE);
        mTitle.setText(R.string.msg_business_msg);
        mFilter.setVisibility(View.VISIBLE);
        mFilter.setText(R.string.msg_filter);
        mFilter.setTextColor(getResources().getColor(R.color.app_color2));
    }

    @Override
    protected void loadData() {

        Intent intent = getIntent();
        final boolean extra = intent.getBooleanExtra("extra", false);
        MLog.e("test", "extra: " + extra);

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                mMsgList = MsgManager.getInstance().queryMsg2(MsgManager.MSG_BUSINESS, 1, pageSize);
                pageIndex = 1;
                loadFinish = false;
                updateUI(mMsgList);
                BusinessMsgActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mAdapter = new BusinessMsgAdapter(BusinessMsgActivity.this, mMsgList);
                        mAdapter.setBusinessMsgClickListener(BusinessMsgActivity.this);
                        mListView.setAdapter(mAdapter);
                    }
                });


            }
        });

    }

    @Override
    protected void updateUI() {

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (mMsgList != null && !mMsgList.isEmpty()) {
                    int len = mMsgList.size();
                    for (int i = 0; i < len; i++) {
                        MsgInfo msginfo = mMsgList.get(i);
                        if (msginfo.getReadMark() != 3) {
                            MsgInfoTable.getInstance().update(msginfo);
                        }
                    }
                }
            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ThreadPoolTool.getInstance().execute(update);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewMsgEvent event) {
        switch (event.msgId) {
            case MessageId.MSGID_MSG_NEW: {

                if (mFilters != null && mFilters.size() > 0) {
                    ThreadPoolTool.getInstance().execute(updateandfilter);
                } else
                    ThreadPoolTool.getInstance().execute(update);
                break;
            }
            default:
                break;
        }
    }

    List<MsgInfo> temp;

    private Runnable updateandfilter = new Runnable() {

        @Override
        public void run() {
            if (temp != null) {
                temp.clear();
            }
//			temp = MsgManager.getInstance().queryMsg2(MsgManager.MSG_BUSINESS,pageIndex,pageSize);
//			// mHandler.sendEmptyMessage(3);
//
//			if (temp != null) {
//				temp.clear();
//			}
            temp = MsgManager.getInstance().getFilterMsg(MsgManager.MSG_BUSINESS, mFilters, 1, pageSize);
            //	MLog.e("checkmsgsize","size"+temp.size());
            mHandler.sendEmptyMessage(0);
        }
    };

    private Runnable update = new Runnable() {

        @Override
        public void run() {
            if (temp != null) {
                temp.clear();
            }
            temp = MsgManager.getInstance().queryMsg2(MsgManager.MSG_BUSINESS, 1, pageSize);
            mHandler.sendEmptyMessage(2);
        }
    };

    private Runnable filter = new Runnable() {

        @Override
        public void run() {
            if (temp != null) {
                temp.clear();
            }
            temp = MsgManager.getInstance().getFilterMsg(MsgManager.MSG_BUSINESS, mFilters, 1, pageSize);
            //	MLog.e("checkmsgsize","size"+temp.size());
            mHandler.sendEmptyMessage(0);
        }
    };

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            if (isFinishing())
                return;

            switch (msg.what) {
                case 0: {

                    mMsgList.clear();
                    mMsgList.addAll(temp);
                    pageIndex = 1;
                    loadFinish = false;
                    mListView.requestLayout();
                    mAdapter.notifyDataSetChanged();
                    hideProgressBar();
                    updateUI(mMsgList);
                    break;
                }
                case 2: {
                    mMsgList.clear();
                    mMsgList.addAll(temp);
                    pageIndex = 1;
                    loadFinish = false;
                    mListView.requestLayout();
                    mAdapter.notifyDataSetChanged();
                    updateUI(mMsgList);
                    break;
                }
                default:
                    break;
            }
        }
    };

    protected void showProgressBar() {
        WaitingProgressTool.showProgress(this);
    }

    protected void hideProgressBar() {
        WaitingProgressTool.closeshowProgress();
    }


    public void showProgressBar2() {

        if (pbWaiting != null)
            pbWaiting.setVisibility(View.VISIBLE);

    }

    public void hideProgressBar2() {
        if (pbWaiting != null)
            pbWaiting.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img: {
                finish();
                break;
            }
            case R.id.title_right_text: {
                if (mDialog == null) {
                    mDialog = new FilterBusinessDailog(this, new FilterBusinessDailog.IFiltertener() {

                        @Override
                        public void OnFinish(final List<Filter> filters) {
                            showProgressBar();
                            mFilters.clear();
                            mFilters.addAll(filters);
                            ThreadPoolTool.getInstance().execute(filter);
                        }
                    });
                }
                mDialog.show();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void OnItemClick(MsgContent business) {

        if (!NetWorkUtils.isNetworkConnected(getApplicationContext())) {

            ToastUtils.showMessage(getApplicationContext(), "无网络");

        } else {

            Intent intent = new Intent(mContext, FreightPointActivity.class);
            intent.putExtra("corpid", business.getCorpId());
            intent.putExtra("taskid", business.getTaskId());
            mContext.startActivity(intent);

        }
    }

    @Override
    public void OnRefuse(final MsgContent business, final IRejectedListener listener) {

        String title = getResources().getString(R.string.dialog_confirm_to_refuse_to_join);
        String cancel = getResources().getString(R.string.dialog_cancel);
        String sure = getResources().getString(R.string.dialog_confirm);
        ExitDialog dialog = new ExitDialog(this, title, "", cancel, sure, 0, new ExitDialog.IExitDialogListener() {

            @Override
            public void OnCancel() {
            }

            @Override
            public void OnSure() {
                unjoinGroup(business, listener);
            }
        });
        dialog.show();
    }

    /**
     * @Title: unjoinGroup
     * @Description: 拒绝加入
     * @param business
     * @param listener
     * @return: void
     */
    protected void unjoinGroup(final MsgContent business, final IRejectedListener listener) {
        if (business != null) {


            WaitingProgressTool.showProgress(this);
            String inviteCode = business.getInviteCode();
            CldKDeliveryAPI.getInstance().unJoinGroup(inviteCode, new ICldResultListener() {

                @Override
                public void onGetResult(int errCode) {

                    WaitingProgressTool.closeshowProgress();


                    switch (errCode) {
                        case 0: {
                            Toast.makeText(mContext, "您已拒绝加入企业", Toast.LENGTH_SHORT).show();
                            if (listener != null) {
                                listener.OnRejected(errCode);
                            }
                            /**
                             * 2-已拒绝
                             */
                            business.setCorpId("2");
                            MsgContentTable.getInstance().update(business);
                            break;
                        }
                        case 501: {
                            Toast.makeText(mContext, "导航用户登录已失效", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case 502: {
                            Toast.makeText(mContext, "导航用户已重新登录", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case 1900: {
                            Toast.makeText(mContext, "系统错误", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case 1020: {
                            // 邀请消息已失效
                            Toast.makeText(mContext, "邀请消息已失效", Toast.LENGTH_SHORT).show();

                            /**
                             * -1-已失效
                             */
                            business.setCorpId("-1");
                            MsgContentTable.getInstance().update(business);

                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                        default: {
                            Toast.makeText(mContext, "操作失败，请检查网络。", Toast.LENGTH_SHORT).show();
                            break;

                        }
                    }
                }

                @Override
                public void onGetReqKey(String tag) {


                }
            });
        }
    }

    @Override
    public void OnAgree(MsgContent business, IJoinedListener listener) {
        if (business != null) {
            int lockcorpid = Integer.parseInt(business.getLockcorpId());
            List<CldDeliGroup> groups = CarAPI.getInstance().getMyGroups();
            if (groups != null && !groups.isEmpty()) {

                // MLog.e("onagree", "" + lockcorpid);

                /**
                 * 司机已加入其它企业
                 */
                if (lockcorpid == 0) {
                    /**
                     * 允许加入其它企业: 未限制
                     */

                    int corpid = CarAPI.getInstance().getLockcorpid();

                    // MLog.e("onagree", "lockcorpid" + lockcorpid);

                    if (corpid == 0) {
                        /**
                         * 先前加入的企业允许加入其它企业：未限制
                         */
                        joinGroup(business, listener);
                    } else {
                        /**
                         * 先前加入的企业不允许加入其它企业：限制加入其它车队
                         */
                        String corpname = "";
                        CldDeliGroup group = CarAPI.getInstance().getCldDeliGroup(corpid + "");
                        if (group != null) {
                            corpname = group.corpName;
                        }
                        justJoinOneGroup(business, corpname, listener);
                    }
                } else {
                    /**
                     * 不允许加入其它企业：限制加入其它车队
                     */
                    String corpname = business.getCorpName();
                    justJoinOneGroup(business, corpname, listener);
                }
            } else {
                /**
                 * 司机未加入其它企业
                 */
                if (lockcorpid == 0) {
                    /**
                     * 允许加入其它企业: 未限制
                     */
                    joinGroup(business, listener);
                } else {
                    /**
                     * 不允许加入其它企业：限制加入其它车队
                     */
                    limitJoinOtherGroup(business, listener);
                }
            }
        }
    }

    private void justJoinOneGroup(final MsgContent business, String corpname, final IJoinedListener listener) {

        // MLog.e("check", GsonTool.getInstance().toJson(business));

        // String groupName = business.getGroupName();

        String joincorpname = business.getCorpName();

        String title = "加入" + joincorpname + "时要先退出其他企业车队， 确认要加入" + joincorpname + "?";
        String content = corpname + "只允许您同时加入一个企业车队";
        String cancel = getResources().getString(R.string.dialog_cancel);
        String sure = getResources().getString(R.string.dialog_continue_to_join);
        MsgDialog dialog = new MsgDialog(this, title, content, cancel, sure, new MsgDialog.IMsgListener() {

            @Override
            public void OnCancel() {
            }

            @Override
            public void OnSure() {
                exitOtherGroup(business, listener);
            }
        });
        dialog.show();
    }

    /**
     * @Title: exitOtherGroup
     * @Description: 先退出其他车队
     * @param business
     * @param listener
     * @return: void
     */
    protected void exitOtherGroup(final MsgContent business, final IJoinedListener listener) {

        ConfirmDialog dialog = new ConfirmDialog(this, new ConfirmDialog.IConfirmListener() {

            @Override
            public void OnOK() {
                // joinGroup(business, listener);
            }

            @Override
            public void OnCall(String phone) {
                // joinGroup(business, listener);
                callPhone = phone;
                CallUtil.call(BusinessMsgActivity.this, callPhone);
            }
        });
        dialog.show();

    }

    /**
     * @Title: limitJoinOtherGroup
     * @Description: 限制加入其它车队
     * @param business
     * @param listener
     * @return: void
     */
    private void limitJoinOtherGroup(final MsgContent business, final IJoinedListener listener) {

        // MLog.e("check", GsonTool.getInstance().toJson(business));

        String title = "加入" + business.getCorpName() + "后将不能加入其他企业车队";
        String content = business.getCorpName() + "只允许您同时加入一个企业车队";
        String cancel = getResources().getString(R.string.dialog_cancel);
        String sure = getResources().getString(R.string.dialog_join);
        MsgDialog dialog = new MsgDialog(this, title, content, cancel, sure, new MsgDialog.IMsgListener() {

            @Override
            public void OnCancel() {
            }

            @Override
            public void OnSure() {
                joinGroup(business, listener);
            }
        });
        dialog.show();
    }

    /**
     * @Title: joinGroup
     * @Description: 同意加入
     * @param
     * @return: void
     */
    private void joinGroup(final MsgContent business, final IJoinedListener listener) {


        WaitingProgressTool.showProgress(this);

        String inviteCode = business.getInviteCode();
        CldKDeliveryAPI.getInstance().joinGroup(inviteCode, new ICldResultListener() {

            @Override
            public void onGetResult(int errCode) {


                WaitingProgressTool.closeshowProgress();

                switch (errCode) {
                    case 0: {
                        // 成功
                        Toast.makeText(mContext, "已成功加入车队", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.OnJoined(errCode);
                        }

                        /**
                         * 1-已加入
                         */
                        business.setCorpId("1");
                        MsgContentTable.getInstance().update(business);

                        CldBllKDelivery.getInstance().loginAuth(new ICldResultListener() {

                            @Override
                            public void onGetResult(int errCode) {
                                if (errCode == 0) {
                                }
                            }

                            @Override
                            public void onGetReqKey(String tag) {


                            }
                        });

                        break;
                    }
                    case 1020: {
                        // 邀请消息已失效
                        Toast.makeText(mContext, "邀请消息已失效", Toast.LENGTH_SHORT).show();

                        /**
                         * -1-已失效
                         */
                        business.setCorpId("-1");
                        MsgContentTable.getInstance().update(business);

                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                    case 1021: {
                        // 车队不存在
                        Toast.makeText(mContext, "车队不存在", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 1022: {
                        // 已加入其它车队
                        Toast.makeText(mContext, "已加入其它车队", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 1023: {
                        // 无权限加入车队
                        Toast.makeText(mContext, "无权限加入车队", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 1024: {
                        // 车队邀请成员数已超出每日上限
                        Toast.makeText(mContext, "车队邀请成员数已超出每日上限", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 1025: {
                        // 创建车队数已超出上限
                        Toast.makeText(mContext, "创建车队数已超出上限", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 1302: {
                        // 加入车队数量达到了上限，无法加入新车队
                        Toast.makeText(mContext, "加入车队数量达到了上限, 无法加入新车队", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 1301: {
                        // 当前已加入的企业不允许司机加入其他企业，请先退出再加入!_
                        // MLog.e("joingrouperror", "1301");
                        // Toast.makeText(mContext, "当前已加入的企业不允许司机加入其他企业，请先退出再加入",
                        // Toast.LENGTH_SHORT).show();

                        /**
                         * 先前加入的企业不允许加入其它企业：限制加入其它车队
                         */
                        String corpname = "";

                        if (CarAPI.getInstance().getMyGroups() != null && CarAPI.getInstance().getMyGroups().size() > 0) {
                            CldDeliGroup group = CarAPI.getInstance().getMyGroups().get(0);
                            if (group != null) {
                                corpname = group.corpName;
                                justJoinOneGroup(business, corpname, listener);
                            }
                        }
                        break;
                    }

                    default: {
                        Toast.makeText(mContext, "操作失败，请检查网络。", Toast.LENGTH_SHORT).show();
                        break;

                    }
                }
            }

            @Override
            public void onGetReqKey(String tag) {


            }
        });
    }

    @Override
    public void OnNavi(MsgContent business) {
        if (business != null) {
            String kcode = business.getKCode();
            String poiname = business.getPoiName();
            String poiaddress = business.getPoiAddress();

            if (TextUtils.isEmpty(business.getPoiName())) {
                poiname = poiaddress;
            }


            /**
             * 调用导航
             */

            if (TextUtils.isEmpty(business.getPoiName()) && TextUtils.isEmpty(business.getPoiAddress())) {

                String address = GeneralSPHelper.getInstance(mContext).getPCDAddress(kcode);
                if (TextUtils.isEmpty(address))
                    poiaddress = "待获取";
                else
                    poiaddress = address;
            }


            HPWPoint start = CldMapApi.getNMapCenter();

            RoutePlanNode startNode = new RoutePlanNode(start.y, start.x, "我的位置", "", CoordinateType.CLD);
            HPWPoint end = CldCoordUtil.kcodeToCLD(kcode);
            RoutePlanNode endNode = new RoutePlanNode(end.y, end.x, poiname, poiaddress, CoordinateType.CLD);

            NavigateAPI.getInstance().hyRoutePlan(this, startNode, null, endNode,
                    RoutePlanPreference.ROUTE_PLAN_MOD_RECOMMEND | RoutePlanPreference.ROUTE_PLAN_MOD_AVOID_TAFFICJAM, new RoutePlanListener() {

                        @Override
                        public void onRoutePlanSuccessed() {
                            BusinessMsgActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    CldProgress.cancelProgress();
                                    Intent intent = new Intent(BusinessMsgActivity.this, RoutePreviewActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }

                        @Override
                        public void onRoutePlanFaied(int arg0, String arg1) {
                            final String errText = arg1;

                            BusinessMsgActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    CldProgress.cancelProgress();

                                    Toast.makeText(BusinessMsgActivity.this, errText, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onRoutePlanCanceled() {

                        }
                    });
        }
    }


    @Override
    protected void messageEvent(AccountEvent event) {
        switch (event.msgId) {
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        mFilters.clear();
        PlayVoiceTool.getInstance().Stop();
        EventBus.getDefault().post(new NewMsgEvent(MessageId.MSGID_MSG_UPDATE));
    }

    @Override
    public void OnPlaySpeech(View v, final String corpid, final String speechid) {

        // MLog.e("play speech", corpid + " " + speechid);

        final ImageView iv = (ImageView) v.findViewById(R.id.iv_hint_speech_playing);

        if (!speechid.equals("-1")) {

            String path = DownloadUtil.getSpeechFilePathById(this, speechid);

            // MLog.e("filepaths.xmls.xml", "" + filepathspaths.xml);
            if (path != null) {
                // 已下载
                PlayVoiceTool.getInstance().PlayWithFilePath(path, new PlayVoiceTool.OnPlayStatusListener() {
                    @Override
                    public void onStart() {
                        if (iv != null) {

                            iv.setImageDrawable(animationDrawable);
                            animationDrawable.start();
                            ivPreSpeech = iv;
                        }
                    }

                    @Override
                    public void onStop() {
                        if (ivPreSpeech != null) {
                            animationDrawable.stop();
                            ivPreSpeech.setImageDrawable(getResources().getDrawable(R.drawable.anim_play_frame_3));
                        }


                    }
                });

            } else {

                // 需下载
                // MLog.e("download filepathspaths.xml", DeliveryApi.getSpeechFileUrl(corpid,
                // speechid));

                DownloadUtil.getInstance(this).DownLoadSpeechFile(DeliveryApi.getSpeechFileUrl(corpid, speechid), this,
                        speechid, new DownLoadResultListener() {

                            @Override
                            public void onError(VolleyError arg0) {

                                if (isFinishing())
                                    return;

                                // MLog.e("download fail", "" + arg0.toString());

                                Toast.makeText(BusinessMsgActivity.this, "下载音频失败,请重试", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onDownload(String localpath) {


                                if (isFinishing())
                                    return;

                                // MLog.e("download success", "" + localpath);
                                PlayVoiceTool.getInstance().PlayWithFilePath(localpath, new PlayVoiceTool.OnPlayStatusListener() {
                                    @Override
                                    public void onStart() {
                                        if (iv != null) {
                                            iv.setImageDrawable(animationDrawable);
                                            animationDrawable.start();
                                            ivPreSpeech = iv;
                                        }
                                    }

                                    @Override
                                    public void onStop() {
                                        if (ivPreSpeech != null) {
                                            animationDrawable.stop();
                                            ivPreSpeech.setImageDrawable(getResources().getDrawable(R.drawable.anim_play_frame_3));
                                        }
                                    }
                                });
                                GeneralSPHelper.getInstance(BusinessMsgActivity.this)
                                        .setIsSpeechRead((corpid + speechid), true);

                                mAdapter.notifyDataSetChanged();
                            }
                        });

            }
        }
    }


    @Override
    public void OnStopPlaying(View v) {

//		ImageView iv = (ImageView) v.findViewById(R.id.iv_hint_speech_playing);

        if (ivPreSpeech != null) {
            animationDrawable.stop();
            ivPreSpeech.setImageDrawable(getResources().getDrawable(R.drawable.anim_play_frame_3));
        }

        PlayVoiceTool.getInstance().Stop();

    }

    public String callPhone = "";

    /**
     * * 动态请求拨打电话权限后，监听用户的点击事件
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0x11:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    // String phone = mStore.linkPhone;
                    if (!TextUtils.isEmpty(callPhone))
                        CallUtil.intentToCall(BusinessMsgActivity.this, callPhone);
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    protected void loadMore() {

        // int count = mAdapter.getCount();
        // mAdapter.getCount() % 20 == 0 &&
        if (!loadFinish) {

            // 加载更多


            showProgressBar2();

            pageIndex++;


            ThreadPoolTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    List<MsgInfo> lists;

                    if (mFilters != null && mFilters.size() > 0) {


                        lists = MsgManager.getInstance().getFilterMsg(MsgManager.MSG_BUSINESS,mFilters, pageIndex, pageSize,
                                (mMsgList!=null?mMsgList.size():0));
                    } else {

                        lists = MsgManager.getInstance().queryMsg2(MsgManager.MSG_BUSINESS, pageIndex, pageSize);

                    }

                    final  List<MsgInfo> list = lists;

                    if (list != null) {
                        mMsgList.addAll(list);
                        updateUI(list);
                    }
                    MLog.e("checkmsgsize", "size" + mMsgList.size());


                    BusinessMsgActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            hideProgressBar2();
                            //int count = mAdapter.getCount();
                            if (mAdapter.getCount() % 10 != 0 || list == null || list.size() == 0) {


                                loadFinish = true;

                            } else {


                                loadFinish = false;

                            }

//							mAdapter = new BusinessMsgAdapter(BusinessMsgActivity.this, mMsgList);
//							mAdapter.setBusinessMsgClickListener(BusinessMsgActivity.this);


                            mAdapter.notifyDataSetChanged();

                        }
                    });


                }
            });


        } else {
            Toast.makeText(BusinessMsgActivity.this, "没有更多记录了", Toast.LENGTH_SHORT).show();
            // 已经取完了
//			mHandler.post(new Runnable() {
//
//				@Override
//				public void run() {
//
//
//				}
//			});

        }

    }


    protected void updateUI(List<MsgInfo> mlist) {


//        ThreadPoolTool.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {

        if (mlist == null || mlist.isEmpty())
            return;

        int len = mlist.size();
        for (int i = 0; i < len; i++) {
            MsgInfo msginfo = mlist.get(i);
            if (msginfo.getReadMark() != 3) {
                MsgInfoTable.getInstance().update(msginfo);
            }
        }

        //   }
//        });
    }

}
