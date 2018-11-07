package com.yunbaba.freighthelper.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunbaba.api.trunk.TimeEarlyWarningService;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.MajorMainFragment;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.bean.eventbus.NewMsgEvent;
import com.yunbaba.freighthelper.bean.msg.MsgContent;
import com.yunbaba.freighthelper.bean.msg.MsgInfo;
import com.yunbaba.freighthelper.manager.MsgManager;
import com.yunbaba.freighthelper.ui.activity.msg.AlarmMsgActivity;
import com.yunbaba.freighthelper.ui.activity.msg.BusinessMsgActivity;
import com.yunbaba.freighthelper.ui.activity.msg.MsgSwitchActivity;
import com.yunbaba.freighthelper.ui.adapter.BusinessMsgAdapter;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.MsgParseTool;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MsgFragment extends MajorMainFragment implements OnClickListener {

    private ImageView mBack;
    private TextView mTitle;
    private ImageView mSetting;
    private RelativeLayout mBusinessLayout;
    private ImageView mBusinessIcon;
    private TextView mBusinessUpdate;
    private TextView mBusinessContent;
    private View mLine;
    private RelativeLayout mAlarmLayout;
    private ImageView mAlarmIcon;
    private TextView mAlarmUpdate;
    private TextView mAlarmContent;
    private ArrayList<MsgInfo> mBusinessList;
    private ArrayList<MsgInfo> mAlarmList;
    private View mWaitting;
    private View mEmptyView;

    public static MsgFragment newInstance() {

        Bundle args = new Bundle();
        MsgFragment fragment = new MsgFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_msg, container, false);
        initViews(view);
        setListener(view);
        initData();

        updateUI();
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * 返回到消息页面，重新刷新是否有未读消息
         */
        // loadData();
        // updateUI();
//        if (!MsgManager.getInstance().hasUnReadMsg()) {
//            mBusinessIcon.setImageResource(R.drawable.msg_icon_business);
//            mAlarmIcon.setImageResource(R.drawable.msg_icon_alarm);
//        }
        ThreadPoolTool.getInstance().execute(loadMsg);
    }

    protected void initViews(View view) {
        mBack = (ImageView) view.findViewById(R.id.title_left_img);
        mTitle = (TextView) view.findViewById(R.id.title_text);
        mSetting = (ImageView) view.findViewById(R.id.title_right_img);
        mBusinessLayout = (RelativeLayout) view.findViewById(R.id.msg_business_layout);
        mBusinessIcon = (ImageView) view.findViewById(R.id.msg_business_icon);
        mBusinessUpdate = (TextView) view.findViewById(R.id.msg_business_update);
        mBusinessContent = (TextView) view.findViewById(R.id.msg_business_content);
        mLine = view.findViewById(R.id.msg_line);
        mAlarmLayout = (RelativeLayout) view.findViewById(R.id.msg_alarm_layout);
        mAlarmIcon = (ImageView) view.findViewById(R.id.msg_alarm_icon);
        mAlarmUpdate = (TextView) view.findViewById(R.id.msg_alarm_update);
        mAlarmContent = (TextView) view.findViewById(R.id.msg_alarm_content);
        mWaitting = view.findViewById(R.id.msg_waiting);
        mEmptyView = view.findViewById(R.id.msg_empty);
    }

    protected void setListener(View view) {
        mBack.setOnClickListener(this);
        mSetting.setOnClickListener(this);
        mBusinessLayout.setOnClickListener(this);
        mAlarmLayout.setOnClickListener(this);
    }

    protected void initData() {
        mBack.setVisibility(View.GONE);
        mTitle.setText(R.string.msg);
        mSetting.setVisibility(View.VISIBLE);
        mSetting.setImageResource(R.drawable.msg_setting);
    }

    protected void loadData() {
        /**
         * 进入消息页面
         */
        // loadBusiness();
        // loadAlarm();
        showProgressBar();
        new Thread(loadMsg).start();
    }

    /**
     * 开一个线程来查询消息(消息特别多的时候，查询耗时)
     */
    private Runnable loadMsg = new Runnable() {

        @Override
        public void run() {

            synchronized (MsgFragment.this) {

                loadBusiness();
                loadAlarm();
                if (mHandler != null)
                    mHandler.sendEmptyMessage(0);

            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewMsgEvent event) {
        switch (event.msgId) {
            case MessageId.MSGID_MSG_NEW: {

                // MLog.e("check", "new msg");

                /**
                 * 收到新消息，刷新页面
                 */
                // loadData();
                // updateUI();
                ThreadPoolTool.getInstance().execute(loadMsg);
                break;
            }
            default:
                break;
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0: {

                    if (isAdded()) {

//                        ThreadPoolTool.getInstance().execute(new Runnable() {
//                            @Override
//                            public void run() {
                        update();
                        hideProgressBar();
//                            }
//                        });

                    } else {

                        postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                if (mHandler != null)
                                    mHandler.sendEmptyMessage(0);
                            }
                        }, 1000);

                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    protected void showProgressBar() {
        mWaitting.setVisibility(View.VISIBLE);
        mBusinessLayout.setVisibility(View.GONE);
        mAlarmLayout.setVisibility(View.GONE);
    }

    protected void hideProgressBar() {
        mWaitting.setVisibility(View.GONE);
    }

    private void loadBusiness() {
        ArrayList<MsgInfo> temp = MsgManager.getInstance().queryMsg(MsgManager.MSG_BUSINESS, 1, 1);
        //  MLog.e("checkmsgsize","getsize"+temp.size());
        if (mBusinessList != null) {
            mBusinessList.clear();
        } else {
            mBusinessList = new ArrayList<MsgInfo>();
        }
        mBusinessList.addAll(temp);
    }

    private void loadAlarm() {
        ArrayList<MsgInfo> temp = MsgManager.getInstance().queryMsg(MsgManager.MSG_ALARM, 1, 1);
        if (mAlarmList != null) {
            mAlarmList.clear();
        } else {
            mAlarmList = new ArrayList<MsgInfo>();
        }
        mAlarmList.addAll(temp);
    }

    protected void updateUI() {

    }

    private void update() {


        MsgFragment.this._mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateBusiness();
                updateAlarm();
                if (mBusinessList != null && mBusinessList.isEmpty() && mAlarmList != null && mAlarmList.isEmpty()) {
                    mEmptyView.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AccountEvent event) {
        messageEvent(event);

    }

    protected void messageEvent(AccountEvent event) {
        switch (event.msgId) {
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img: {
                // finish();
                break;
            }
            case R.id.title_right_img: {
                Intent intent = new Intent(_mActivity, MsgSwitchActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.msg_business_layout: {
                Intent intent = new Intent(_mActivity, BusinessMsgActivity.class);
                // intent.putParcelableArrayListExtra("business", mBusinessList);
                startActivity(intent);
                break;
            }


            case R.id.msg_alarm_layout: {
                Intent intent = new Intent(_mActivity, AlarmMsgActivity.class);
                /**
                 * 使用putParcelableArrayListExtra 数据特别多的时候，直接崩掉
                 */
                // intent.putParcelableArrayListExtra("alarm", temp);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    private synchronized void updateBusiness() {
        if (mBusinessList != null && !mBusinessList.isEmpty() && mBusinessList.get(0).getMsgContent() != null) {


            ThreadPoolTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                  final   boolean unread = MsgManager.getInstance().hasUnReadMsg(MsgManager.MSG_BUSINESS);
                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (unread) {
                                mBusinessIcon.setImageResource(R.drawable.msg_icon_business_news);
                            } else {
                                mBusinessIcon.setImageResource(R.drawable.msg_icon_business);
                            }

                            MsgContent business = mBusinessList.get(0).getMsgContent();
                            String time = business.getCreatetime();
                            if (!TextUtils.isEmpty(time)) {
                                mBusinessUpdate.setText(business.getCreatetime());
                            }
                            String content = "";
                            int type = business.getLayout();
                            switch (type) {
                                case BusinessMsgAdapter.MSG_BUSINESS_JOIN: {
                                    String contentHint = getResources().getString(R.string.msg_business_join_corpname);
                                    String corpname = business.getCorpName();
                                    String groupname = business.getGroupName();
                                    content = String.format(contentHint, groupname);
                                    mBusinessContent.setText(corpname + content);
                                    break;
                                }
                                case BusinessMsgAdapter.MSG_BUSINESS_QUIT: {
                                    String groupnameHint = getResources().getString(R.string.msg_business_quit_groupname);
                                    String groupname = String.format(groupnameHint, business.getGroupName());
                                    String corpnameHint = getResources().getString(R.string.msg_business_quit_corpname);
                                    String corpname = String.format(corpnameHint, business.getCorpName());
                                    content = groupname + corpname;
                                    mBusinessContent.setText(content);
                                    break;
                                }
                                case BusinessMsgAdapter.MSG_BUSINESS_SCHEDULE_KCODE: {

                                    content = business.getContent();

                                    if (TextUtils.isEmpty(content))
                                        mBusinessContent.setText("调度消息");
                                    else
                                        mBusinessContent.setText(TextStringUtil.ReplaceHtmlTag(content));
                                    break;
                                }
                                case BusinessMsgAdapter.MSG_BUSINESS_SCHEDULE_GENERAL: {
                                    content = business.getContent();
                                    mBusinessContent.setText(TextStringUtil.ReplaceHtmlTag(content));
                                    break;
                                }
                                case BusinessMsgAdapter.MSG_BUSINESS_SCHEDULE_SPEECH: {
                                    // content = business.getContent();
                                    mBusinessContent.setText("语音调度消息");
                                    break;
                                }
                                case BusinessMsgAdapter.MSG_BUSINESS_TASK_REMIND: {
                                    String taskIdHint = getResources().getString(R.string.msg_business_task_remind_taskId);
                                    String taskId = String.format(taskIdHint, business.getTaskId());
                                    content = business.getContent();

                                    if (business.getTaskId().equals(TimeEarlyWarningService.MSGTYPE_ORIGINAL)) {
                                        mBusinessContent.setText(content);
                                    } else {
                                        mBusinessContent.setText(taskId + content);
                                    }

                                    break;
                                }
                                case BusinessMsgAdapter.MSG_BUSINESS_TASK_GENERAL: {
                                    String pointHint = getResources().getString(R.string.msg_business_task_general_point_new);
                                    //String point = business.getDeliveryPoints();
                                    content = pointHint;//String.format(pointHint, point);
                                    mBusinessContent.setText(content);
                                    break;
                                }
                                case BusinessMsgAdapter.MSG_BUSINESS_CANCEL_ORDER: {
                                    String pointHint = getResources().getString(R.string.msg_business_order_cancel);
                                    String point = MsgParseTool.getCuOrderIdFromMsgContent(business.getContent());
                                    content = String.format(pointHint, point);
                                    mBusinessContent.setText(content);
                                    break;
                                }
                                case BusinessMsgAdapter.MSG_BUSINESS_CANCEL_TASK: {
                                    String pointHint = getResources().getString(R.string.msg_business_task_cancel);
                                    String point = business.getCorpId();
                                    content = String.format(pointHint, point);
                                    mBusinessContent.setText(content);
                                    break;
                                }
                                default:
                                    break;
                            }
                            mBusinessLayout.setVisibility(View.VISIBLE);
                            mEmptyView.setVisibility(View.GONE);
                        }
                    });
                }
            });



        } else {
            mBusinessLayout.setVisibility(View.GONE);
//            boolean unread = MsgManager.getInstance().hasUnReadMsg(MsgManager.MSG_BUSINESS);
//            MsgManager.getInstance().queryMsg2(MsgManager.MSG_BUSINESS,1,1);
//            mBusinessLayout.setVisibility(View.VISIBLE);
        }
    }

    private synchronized void updateAlarm() {
        if (mAlarmList != null && !mAlarmList.isEmpty()) {


            ThreadPoolTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    final boolean unread = MsgManager.getInstance().hasUnReadMsg(MsgManager.MSG_ALARM);

                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (unread) {
                                mAlarmIcon.setImageResource(R.drawable.msg_icon_alarm_news);
                            } else {
                                mAlarmIcon.setImageResource(R.drawable.msg_icon_alarm);
                            }

                            final MsgContent alarm = mAlarmList.get(0).getMsgContent();

                            MLog.e("test", "alarmid: " + alarm.getAlarmId());
//            MsgFragment.this._mActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
                            mAlarmUpdate.setText(alarm.getCreatetime());
                            mAlarmContent.setText(alarm.getContent());
                            mAlarmLayout.setVisibility(View.VISIBLE);
                            mLine.setVisibility(View.VISIBLE);
                            mEmptyView.setVisibility(View.GONE);
//                }
//            });

                        }
                    });
                }
            });


        } else {
//            MsgFragment.this._mActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            mAlarmLayout.setVisibility(View.GONE);
            mLine.setVisibility(View.GONE);
//                }
//            });
        }
    }

}
