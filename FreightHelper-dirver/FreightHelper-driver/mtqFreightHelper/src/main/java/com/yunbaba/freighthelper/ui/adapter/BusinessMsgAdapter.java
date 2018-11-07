/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: BusinessMsgAdapter.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.adapter
 * @Description: 企业消息adapter
 * @author: zhaoqy
 * @date: 2017年3月29日 下午4:00:27
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cld.mapapi.model.LatLng;
import com.cld.mapapi.search.exception.IllegalSearchArgumentException;
import com.cld.mapapi.search.geocode.GeoCodeResult;
import com.cld.mapapi.search.geocode.GeoCoder;
import com.cld.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.cld.mapapi.search.geocode.ReverseGeoCodeOption;
import com.cld.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cld.nv.location.CldCoordUtil;
import com.yunbaba.api.trunk.TimeEarlyWarningService;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.msg.MsgContent;
import com.yunbaba.freighthelper.bean.msg.MsgInfo;
import com.yunbaba.freighthelper.utils.CommonTool;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MsgParseTool;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.List;

import hmi.packages.HPDefine.HPWPoint;

public class BusinessMsgAdapter extends BaseAdapter {

    public static final int MSG_BUSINESS_JOIN = 0;
    public static final int MSG_BUSINESS_QUIT = 1;
    public static final int MSG_BUSINESS_SCHEDULE_KCODE = 2;
    public static final int MSG_BUSINESS_SCHEDULE_GENERAL = 3;
    public static final int MSG_BUSINESS_TASK_REMIND = 4;
    public static final int MSG_BUSINESS_TASK_GENERAL = 5;
    public static final int MSG_ALARM = 6;
    public static final int MSG_BUSINESS_SCHEDULE_SPEECH = 7;
    public static final int MSG_BUSINESS_CANCEL_ORDER = 8;
    public static final int MSG_BUSINESS_CANCEL_TASK = 9;

    private int positionClicked = -1;

    private List<MsgInfo> mList = null;
    private Context mContext;

    ImageView ivHintPlaying;

    public BusinessMsgAdapter(Context context, List<MsgInfo> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 返回有多少个不同的布局
     */
    @Override
    public int getViewTypeCount() {
        return 10;
    }

    /**
     * 返回当前布局的样式type
     */
    @Override
    public int getItemViewType(int position) {
//        if(mList.get(position).getMsgContent() == null)
//            return
        return mList.get(position).getMsgContent().getLayout();
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        final MsgContent item = mList.get(position).getMsgContent();
        String time = item.getCreatetime();
        int type = getItemViewType(position);
        final ViewHolderJoin viewHolderJoin;
        ViewHolderQuit viewHolderQuit = null;
        ViewHolderScheduleKCode viewHolderSchedule = null;
        ViewHolderScheduleGeneral viewHolderScheduleGeneral = null;
        ViewHolderTaskRemind viewHolderTaskRemind = null;
        ViewHolderTaskGeneral viewHolderTaskGeneral = null;
        ViewHolderScheduleSpeech viewHolderScheduleSpeech = null;

        if (view == null) {
            switch (type) {
                case MSG_BUSINESS_JOIN: {
                    viewHolderJoin = new ViewHolderJoin();
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_business_join, null);
                    viewHolderJoin.time = (TextView) view.findViewById(R.id.business_msg_join_item_time);
                    viewHolderJoin.corpname = (TextView) view.findViewById(R.id.business_msg_join_item_corpname);
                    viewHolderJoin.refuse = (TextView) view.findViewById(R.id.business_msg_join_item_refuse);
                    viewHolderJoin.agree = (TextView) view.findViewById(R.id.business_msg_join_item_agree);
                    viewHolderJoin.result = (TextView) view.findViewById(R.id.business_msg_join_item_result);

                    viewHolderJoin.refuse.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                mListener.OnRefuse(item, new IRejectedListener() {

                                    @Override
                                    public void OnRejected(int errCode) {
                                        viewHolderJoin.refuse.setVisibility(View.GONE);
                                        viewHolderJoin.agree.setVisibility(View.GONE);
                                        viewHolderJoin.result.setVisibility(View.VISIBLE);
                                        viewHolderJoin.result.setText("已拒绝");
                                        viewHolderJoin.result.setTextColor(Color.parseColor("#e04343"));
                                    }
                                });
                            }
                        }
                    });

                    viewHolderJoin.agree.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                mListener.OnAgree(item, new IJoinedListener() {

                                    @Override
                                    public void OnJoined(int errCode) {
                                        viewHolderJoin.refuse.setVisibility(View.GONE);
                                        viewHolderJoin.agree.setVisibility(View.GONE);
                                        viewHolderJoin.result.setVisibility(View.VISIBLE);
                                        viewHolderJoin.result.setText("已同意");
                                        viewHolderJoin.result.setTextColor(Color.parseColor("#0f9e98"));
                                    }
                                });
                            }
                        }
                    });

                    if (item.getCorpId().equals("1")) {
                        /**
                         * 已加入
                         */
                        viewHolderJoin.refuse.setVisibility(View.GONE);
                        viewHolderJoin.agree.setVisibility(View.GONE);
                        viewHolderJoin.result.setVisibility(View.VISIBLE);
                        viewHolderJoin.result.setText("已同意");
                        viewHolderJoin.result.setTextColor(Color.parseColor("#0f9e98"));
                    } else if (item.getCorpId().equals("2")) {
                        /**
                         * 已拒绝
                         */
                        viewHolderJoin.refuse.setVisibility(View.GONE);
                        viewHolderJoin.agree.setVisibility(View.GONE);
                        viewHolderJoin.result.setVisibility(View.VISIBLE);
                        viewHolderJoin.result.setText("已拒绝");
                        viewHolderJoin.result.setTextColor(Color.parseColor("#e04343"));
                    } else if (item.getCorpId().equals("-1")) {
                        /**
                         * 已失效
                         */
                        viewHolderJoin.refuse.setVisibility(View.GONE);
                        viewHolderJoin.agree.setVisibility(View.GONE);
                        viewHolderJoin.result.setVisibility(View.VISIBLE);
                        viewHolderJoin.result.setText("邀请已失效");
                        viewHolderJoin.result.setTextColor(Color.parseColor("#ff7800"));

                    } else {
                        viewHolderJoin.refuse.setVisibility(View.VISIBLE);
                        viewHolderJoin.agree.setVisibility(View.VISIBLE);
                        viewHolderJoin.result.setVisibility(View.GONE);
                    }

                    viewHolderJoin.time.setText(time);
                    String contentHint = mContext.getResources().getString(R.string.msg_business_join_corpname);
                    String corpname = item.getCorpName();
                    String groupname = item.getGroupName();
                    String content = String.format(contentHint, groupname);
                    viewHolderJoin.corpname.setText(corpname + content);
                    view.setTag(R.id.business_msg_join_item, viewHolderJoin);
                    break;
                }
                case MSG_BUSINESS_QUIT: {
                    viewHolderQuit = new ViewHolderQuit();
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_business_quit, null);
                    viewHolderQuit.time = (TextView) view.findViewById(R.id.business_msg_quit_item_time);
                    viewHolderQuit.content = (TextView) view.findViewById(R.id.business_msg_quit_item_content);
                    viewHolderQuit.corpname = (TextView) view.findViewById(R.id.business_msg_quit_item_corpname);

                    viewHolderQuit.time.setText(time);
                    String groupnameHint = mContext.getResources().getString(R.string.msg_business_quit_groupname);
                    String groupname = String.format(groupnameHint, item.getGroupName());
                    String corpnameHint = mContext.getResources().getString(R.string.msg_business_quit_corpname);
                    String corpname = String.format(corpnameHint, item.getCorpName());
                    viewHolderQuit.content.setText(groupname + corpname);
                    viewHolderQuit.corpname.setText(item.getCorpName());
                    view.setTag(R.id.business_msg_quit_item, viewHolderQuit);
                    break;
                }
                case MSG_BUSINESS_SCHEDULE_KCODE: {
                    viewHolderSchedule = new ViewHolderScheduleKCode();
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_business_schedule_kcode, null);
                    viewHolderSchedule.time = (TextView) view.findViewById(R.id.business_msg_schedule_kcode_item_time);
                    viewHolderSchedule.content = (TextView) view
                            .findViewById(R.id.business_msg_schedule_kcode_item_content);
                    viewHolderSchedule.corpname = (TextView) view
                            .findViewById(R.id.business_msg_schedule_kcode_item_corpname);
                    viewHolderSchedule.poiname = (TextView) view
                            .findViewById(R.id.business_msg_schedule_kcode_item_poiname);
                    viewHolderSchedule.poiaddress = (TextView) view
                            .findViewById(R.id.business_msg_schedule_kcode_item_poiaddress);
                    viewHolderSchedule.navi = view.findViewById(R.id.business_msg_schedule_kcode_item_navi);

                    viewHolderSchedule.navi.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                mListener.OnNavi(item);
                            }
                        }
                    });

                    viewHolderSchedule.time.setText(time);

                    if (TextUtils.isEmpty(item.getContent()))
                        viewHolderSchedule.content.setVisibility(View.GONE);
                    else {
                        viewHolderSchedule.content.setText(TextStringUtil.ReplaceHtmlTag(item.getContent()));
                        viewHolderSchedule.content.setVisibility(View.VISIBLE);
                    }

                    viewHolderSchedule.corpname.setText(item.getCorpName());

                    // String addr = Html.fromHtml(item.getPoiName()).toString();

                    if (!TextUtils.isEmpty(item.getPoiName())) {
                        viewHolderSchedule.poiname.setText(TextStringUtil.ReplaceHtmlTag(item.getPoiName()));
                    }else
                        viewHolderSchedule.poiname.setText(item.getPoiAddress());
                    // viewHolderSchedule.poiname.setText(item.getPoiName());

                    viewHolderSchedule.poiaddress.setText(item.getPoiAddress());

                    final String kcode = item.getKCode();
                    final TextView addressview = viewHolderSchedule.poiaddress;

                    String address = getPCDAddress(kcode, new OnGetGeoCoderResultListener() {

                        @Override
                        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {

                            if (arg0.errorCode == 0) {
                                GeneralSPHelper.getInstance(mContext).setPCDAddress(kcode, arg0.address);

                                if (addressview != null) {
                                    addressview.setText(TextStringUtil.ReplaceHtmlTag(arg0.address));
                                    addressview.setVisibility(View.VISIBLE);
                                    notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onGetGeoCodeResult(GeoCodeResult arg0) {


                        }
                    });

                    if (!TextUtils.isEmpty(address))
                        viewHolderSchedule.poiaddress.setText(address);
                    else
                        viewHolderSchedule.poiaddress.setText(item.getPoiAddress());

                    view.setTag(R.id.business_msg_schedule_kcode_item, viewHolderSchedule);
                    break;
                }
                case MSG_BUSINESS_SCHEDULE_GENERAL: {
                    viewHolderScheduleGeneral = new ViewHolderScheduleGeneral();
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_business_schedule_general, null);
                    viewHolderScheduleGeneral.time = (TextView) view
                            .findViewById(R.id.business_msg_schedule_general_item_time);
                    viewHolderScheduleGeneral.content = (TextView) view
                            .findViewById(R.id.business_msg_schedule_general_item_content);
                    viewHolderScheduleGeneral.corpname = (TextView) view
                            .findViewById(R.id.business_msg_schedule_general_item_corpname);

                    viewHolderScheduleGeneral.time.setText(time);
                    viewHolderScheduleGeneral.content.setText(TextStringUtil.ReplaceHtmlTag(item.getContent()));
                    viewHolderScheduleGeneral.corpname.setText(item.getCorpName());
                    view.setTag(R.id.business_msg_schedule_general_item, viewHolderScheduleGeneral);
                    break;
                }
                case MSG_BUSINESS_SCHEDULE_SPEECH: {

//                    case #1
                    // MLog.e("adapter", "type" +MSG_BUSINESS_SCHEDULE_SPEECH );
                    viewHolderScheduleSpeech = new ViewHolderScheduleSpeech();
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_business_schedule_speech, null);
                    viewHolderScheduleSpeech.time = (TextView) view
                            .findViewById(R.id.business_msg_schedule_general_item_time);
                    viewHolderScheduleSpeech.tv_speechtime = (TextView) view.findViewById(R.id.tv_speechtime);
                    viewHolderScheduleSpeech.ll_speech = (PercentRelativeLayout) view.findViewById(R.id.ll_speech);
                    viewHolderScheduleSpeech.v_noread = (View) view.findViewById(R.id.v_noread);
                    ivHintPlaying = (ImageView) view.findViewById(R.id.iv_hint_speech_playing);
                    viewHolderScheduleSpeech.corpname = (TextView) view
                            .findViewById(R.id.business_msg_schedule_general_item_corpname);

                    // dfb

                    String[] res = item.getContent().split("#");
                    MLog.e("speech", item.getContent());
                    String speechid = "-1";
                    String corpid = "-1";
                    for (String ress : res) {
                        MLog.e("speech", ress);
                    }
                    if (res.length >= 4) {
                        speechid = res[3];
                        corpid = res[1];
                    }

                    final String speechids = speechid;
                    final String corpids = corpid;



                    if (GeneralSPHelper.getInstance(mContext).IsSpeechRead((corpid + speechid))) {
                        viewHolderScheduleSpeech.v_noread.setVisibility(View.GONE);
                    } else {
                        viewHolderScheduleSpeech.v_noread.setVisibility(View.VISIBLE);
                    }

                    viewHolderScheduleSpeech.ll_speech.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            // EventBus.getDefault().post(new
                            // SpeechPlayEvent(speechids,corpids));

                            if ( position == positionClicked ) {

                                positionClicked = -1;

                                if (mListener != null) {
                                    mListener.OnStopPlaying(v);

                                }

                            } else {

                                positionClicked = position;

                                if (mListener != null) {
                                    mListener.OnStopPlaying(v);
                                    mListener.OnPlaySpeech(v, corpids, speechids);
                                }
                            }


                        }
                    });

                    // 音频时长

                    if (res.length >= 5) {
                        double length = CommonTool.convertToDouble(res[4], -1);

                        if (length > 0) {

                            String st = "" + length;

                            if (length > 1.0)
                                st = "" + (int) Math.round(length);

                            viewHolderScheduleSpeech.tv_speechtime.setText(st + "\"");

                            viewHolderScheduleSpeech.tv_speechtime.setVisibility(View.VISIBLE);
                        } else
                            viewHolderScheduleSpeech.tv_speechtime.setVisibility(View.GONE);
                    } else {
                        viewHolderScheduleSpeech.tv_speechtime.setVisibility(View.GONE);
                    }

                    viewHolderScheduleSpeech.time.setText(time);
                    // viewHolderScheduleGeneral.content.setText(item.getContent());
                    viewHolderScheduleSpeech.corpname.setText(item.getCorpName());
                    view.setTag(R.id.business_msg_schedule_speech_item, viewHolderScheduleSpeech);
                    break;
                }
                case MSG_BUSINESS_TASK_REMIND: {
                    viewHolderTaskRemind = new ViewHolderTaskRemind();
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_business_task_remind, null);
                    viewHolderTaskRemind.time = (TextView) view.findViewById(R.id.business_msg_task_remind_item_time);
                    viewHolderTaskRemind.content = (TextView) view.findViewById(R.id.business_msg_task_remind_item_content);
                    viewHolderTaskRemind.corpname = (TextView) view
                            .findViewById(R.id.business_msg_task_remind_item_corpname);

                    viewHolderTaskRemind.time.setText(time);
                    String taskIdHint = mContext.getResources().getString(R.string.msg_business_task_remind_taskId);
                    String taskId = String.format(taskIdHint, item.getTaskId());
                    String content = item.getContent();

                    if (item.getTaskId().equals(TimeEarlyWarningService.MSGTYPE_ORIGINAL)) {
                        viewHolderTaskRemind.content.setText(content);
                    } else {
                        viewHolderTaskRemind.content.setText(taskId + content);
                    }
                    viewHolderTaskRemind.corpname.setText(item.getCorpName());
                    view.setTag(R.id.business_msg_task_remind_item, viewHolderTaskRemind);
                    break;
                }
                case MSG_BUSINESS_TASK_GENERAL: {
                    viewHolderTaskGeneral = new ViewHolderTaskGeneral();
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_business_task_general, null);
                    viewHolderTaskGeneral.time = (TextView) view.findViewById(R.id.business_msg_task_general_item_time);
                    viewHolderTaskGeneral.point = (TextView) view.findViewById(R.id.business_msg_task_general_item_point);
                    viewHolderTaskGeneral.rllnewtask = (PercentRelativeLayout) view.findViewById(R.id.rll_newtask);
                    viewHolderTaskGeneral.tvcheckdetail = (TextView) view.findViewById(R.id.business_msg_task_general_item_check_detail);
                    viewHolderTaskGeneral.point.setText("");
                    viewHolderTaskGeneral.rllnewtask.setVisibility(View.VISIBLE);
                    viewHolderTaskGeneral.corpname = (TextView) view
                            .findViewById(R.id.business_msg_task_general_item_corpname);

                    viewHolderTaskGeneral.tvTaskId = (TextView) view.findViewById(R.id.business_msg_task_general_item_task_id);
                    viewHolderTaskGeneral.tvVehicle = (TextView) view.findViewById(R.id.business_msg_task_general_item_vehicle);
                    viewHolderTaskGeneral.tvDeliveryPoints = (TextView) view.findViewById(R.id.business_msg_task_general_item_delivery_points);
                    viewHolderTaskGeneral.point.setText("");
                    viewHolderTaskGeneral.time.setText(time);
//                    String pointHint = mContext.getResources().getString(R.string.msg_business_task_general_point_new);
                    // String point = String.format(pointHint,
                    // item.getDeliveryPoints());
//                    viewHolderTaskGeneral.point.setText(pointHint);

                    viewHolderTaskGeneral.tvTaskId.setText(item.getTaskId());
                    viewHolderTaskGeneral.tvVehicle.setText(item.getDeliveryVehicle());
                    viewHolderTaskGeneral.tvDeliveryPoints.setText(item.getDeliveryPoints());
                    viewHolderTaskGeneral.tvcheckdetail.setVisibility(View.VISIBLE);
                    viewHolderTaskGeneral.corpname.setText(item.getCorpName());

                    view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.OnItemClick(item);
                        }
                    });

                    view.setTag(R.id.business_msg_task_general_item, viewHolderTaskGeneral);
                    break;
                }
                case MSG_BUSINESS_CANCEL_ORDER: {
                    viewHolderTaskGeneral = new ViewHolderTaskGeneral();
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_business_task_general, null);
                    viewHolderTaskGeneral.time = (TextView) view.findViewById(R.id.business_msg_task_general_item_time);
                    viewHolderTaskGeneral.point = (TextView) view.findViewById(R.id.business_msg_task_general_item_point);
                    viewHolderTaskGeneral.rllnewtask = (PercentRelativeLayout) view.findViewById(R.id.rll_newtask);
                    viewHolderTaskGeneral.tvcheckdetail = (TextView) view.findViewById(R.id.business_msg_task_general_item_check_detail);
                    viewHolderTaskGeneral.point.setVisibility(View.VISIBLE);
                    viewHolderTaskGeneral.tvcheckdetail.setVisibility(View.GONE);
                    viewHolderTaskGeneral.corpname = (TextView) view
                            .findViewById(R.id.business_msg_task_general_item_corpname);
                    viewHolderTaskGeneral.rllnewtask.setVisibility(View.GONE);
                    viewHolderTaskGeneral.time.setText(time);
                    String pointHint = mContext.getResources().getString(R.string.msg_business_order_cancel);
                    String point = String.format(pointHint, MsgParseTool.getCuOrderIdFromMsgContent(item.getContent()));
                    viewHolderTaskGeneral.point.setText(point);
                    viewHolderTaskGeneral.corpname.setText(item.getCorpName());
                    view.setTag(R.id.business_msg_task_general_item, viewHolderTaskGeneral);
                    break;
                }
                case MSG_BUSINESS_CANCEL_TASK: {
                    viewHolderTaskGeneral = new ViewHolderTaskGeneral();
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_business_task_general, null);
                    viewHolderTaskGeneral.time = (TextView) view.findViewById(R.id.business_msg_task_general_item_time);
                    viewHolderTaskGeneral.point = (TextView) view.findViewById(R.id.business_msg_task_general_item_point);
                    viewHolderTaskGeneral.rllnewtask = (PercentRelativeLayout) view.findViewById(R.id.rll_newtask);
                    viewHolderTaskGeneral.tvcheckdetail = (TextView) view.findViewById(R.id.business_msg_task_general_item_check_detail);
                    viewHolderTaskGeneral.point.setVisibility(View.VISIBLE);
                    viewHolderTaskGeneral.rllnewtask.setVisibility(View.GONE);
                    viewHolderTaskGeneral.corpname = (TextView) view
                            .findViewById(R.id.business_msg_task_general_item_corpname);
                    viewHolderTaskGeneral.tvcheckdetail.setVisibility(View.GONE);
                    viewHolderTaskGeneral.time.setText(time);
                    String pointHint = mContext.getResources().getString(R.string.msg_business_task_cancel);
                    String point = String.format(pointHint, item.getCorpId());
                    //MLog.e("MSG_BUSINESS_CANCEL_TASK", item.getCorpId());
                    viewHolderTaskGeneral.point.setText(point);
                    viewHolderTaskGeneral.corpname.setText(item.getCorpName());
                    view.setTag(R.id.business_msg_task_general_item, viewHolderTaskGeneral);
                    break;
                }
                default:
                    break;
            }
        } else {
            switch (type) {
                case MSG_BUSINESS_JOIN: {
                    viewHolderJoin = (ViewHolderJoin) view.getTag(R.id.business_msg_join_item);
                    viewHolderJoin.time.setText(time);
                    String contentHint = mContext.getResources().getString(R.string.msg_business_join_corpname);
                    String corpname = item.getCorpName();
                    String groupname = item.getGroupName();
                    String content = String.format(contentHint, groupname);
                    viewHolderJoin.corpname.setText(corpname + content);

                    if (item.getCorpId().equals("1")) {
                        /**
                         * 已加入
                         */
                        viewHolderJoin.refuse.setVisibility(View.GONE);
                        viewHolderJoin.agree.setVisibility(View.GONE);
                        viewHolderJoin.result.setVisibility(View.VISIBLE);
                        viewHolderJoin.result.setText("已同意");
                        viewHolderJoin.result.setTextColor(Color.parseColor("#0f9e98"));
                    } else if (item.getCorpId().equals("2")) {
                        /**
                         * 已拒绝
                         */
                        viewHolderJoin.refuse.setVisibility(View.GONE);
                        viewHolderJoin.agree.setVisibility(View.GONE);
                        viewHolderJoin.result.setVisibility(View.VISIBLE);
                        viewHolderJoin.result.setText("已拒绝");
                        viewHolderJoin.result.setTextColor(Color.parseColor("#e04343"));
                    } else if (item.getCorpId().equals("-1")) {
                        /**
                         * 已失效
                         */
                        viewHolderJoin.refuse.setVisibility(View.GONE);
                        viewHolderJoin.agree.setVisibility(View.GONE);
                        viewHolderJoin.result.setVisibility(View.VISIBLE);
                        viewHolderJoin.result.setText("邀请已失效");
                        viewHolderJoin.result.setTextColor(Color.parseColor("#ff7800"));

                    } else {
                        viewHolderJoin.refuse.setVisibility(View.VISIBLE);
                        viewHolderJoin.agree.setVisibility(View.VISIBLE);
                        viewHolderJoin.result.setVisibility(View.GONE);
                    }

                    viewHolderJoin.refuse.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                mListener.OnRefuse(item, new IRejectedListener() {

                                    @Override
                                    public void OnRejected(int errCode) {
                                        viewHolderJoin.refuse.setVisibility(View.GONE);
                                        viewHolderJoin.agree.setVisibility(View.GONE);
                                        viewHolderJoin.result.setVisibility(View.VISIBLE);
                                        viewHolderJoin.result.setText("已拒绝");
                                        viewHolderJoin.result.setTextColor(Color.parseColor("#e04343"));
                                    }
                                });
                            }
                        }
                    });

                    viewHolderJoin.agree.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                mListener.OnAgree(item, new IJoinedListener() {

                                    @Override
                                    public void OnJoined(int errCode) {
                                        viewHolderJoin.refuse.setVisibility(View.GONE);
                                        viewHolderJoin.agree.setVisibility(View.GONE);
                                        viewHolderJoin.result.setVisibility(View.VISIBLE);
                                        viewHolderJoin.result.setText("已同意");
                                        viewHolderJoin.result.setTextColor(Color.parseColor("#0f9e98"));
                                    }
                                });
                            }
                        }
                    });
                    break;
                }
                case MSG_BUSINESS_QUIT: {
                    viewHolderQuit = (ViewHolderQuit) view.getTag(R.id.business_msg_quit_item);
                    viewHolderQuit.time.setText(time);
                    String groupnameHint = mContext.getResources().getString(R.string.msg_business_quit_groupname);
                    String groupname = String.format(groupnameHint, item.getGroupName());
                    String corpnameHint = mContext.getResources().getString(R.string.msg_business_quit_corpname);
                    String corpname = String.format(corpnameHint, item.getCorpName());
                    viewHolderQuit.content.setText(groupname + corpname);
                    viewHolderQuit.corpname.setText(item.getCorpName());
                    break;
                }
                case MSG_BUSINESS_SCHEDULE_KCODE: {
                    viewHolderSchedule = (ViewHolderScheduleKCode) view.getTag(R.id.business_msg_schedule_kcode_item);
                    viewHolderSchedule.time.setText(time);
                    if (TextUtils.isEmpty(item.getContent()))
                        viewHolderSchedule.content.setVisibility(View.GONE);
                    else {
                        viewHolderSchedule.content.setText(TextStringUtil.ReplaceHtmlTag(item.getContent()));
                        viewHolderSchedule.content.setVisibility(View.VISIBLE);
                    }
                    viewHolderSchedule.corpname.setText(item.getCorpName());

                    // String addr = Html.fromHtml(item.getPoiName()).toString();
                    viewHolderSchedule.poiname.setText(TextStringUtil.ReplaceHtmlTag(item.getPoiName()));// Html.fromHtml(addr).toString()
                    // viewHolderSchedule.poiname.setText(item.getPoiName());
                    final String kcode = item.getKCode();
                    final TextView addressview = viewHolderSchedule.poiaddress;

                    String address = getPCDAddress(kcode, new OnGetGeoCoderResultListener() {

                        @Override
                        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {

                            if (arg0.errorCode == 0) {
                                GeneralSPHelper.getInstance(mContext).setPCDAddress(kcode, arg0.address);

                                if (addressview != null) {
                                    addressview.setText(TextStringUtil.ReplaceHtmlTag(arg0.address));
                                    addressview.setVisibility(View.VISIBLE);
                                    notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onGetGeoCodeResult(GeoCodeResult arg0) {


                        }
                    });

                    if (!TextUtils.isEmpty(address))
                        viewHolderSchedule.poiaddress.setText(address);
                    else
                        viewHolderSchedule.poiaddress.setText(item.getPoiAddress());

                    viewHolderSchedule.navi.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                mListener.OnNavi(item);
                            }
                        }
                    });
                    break;
                }
                case MSG_BUSINESS_SCHEDULE_GENERAL: {
                    viewHolderScheduleGeneral = (ViewHolderScheduleGeneral) view
                            .getTag(R.id.business_msg_schedule_general_item);
                    viewHolderScheduleGeneral.time.setText(time);
                    viewHolderScheduleGeneral.content.setText(TextStringUtil.ReplaceHtmlTag(item.getContent()));
                    viewHolderScheduleGeneral.corpname.setText(item.getCorpName());
                    break;
                }
                case MSG_BUSINESS_TASK_REMIND: {
                    viewHolderTaskRemind = (ViewHolderTaskRemind) view.getTag(R.id.business_msg_task_remind_item);
                    viewHolderTaskRemind.time.setText(time);
                    String taskIdHint = mContext.getResources().getString(R.string.msg_business_task_remind_taskId);
                    String taskId = String.format(taskIdHint, item.getTaskId());
                    String content = item.getContent();
                    if (item.getTaskId().equals(TimeEarlyWarningService.MSGTYPE_ORIGINAL)) {
                        viewHolderTaskRemind.content.setText(content);
                    } else {
                        viewHolderTaskRemind.content.setText(taskId + content);
                    }
                    viewHolderTaskRemind.corpname.setText(item.getCorpName());
                    break;
                }
                case MSG_BUSINESS_TASK_GENERAL: {
                    viewHolderTaskGeneral = (ViewHolderTaskGeneral) view.getTag(R.id.business_msg_task_general_item);
                    viewHolderTaskGeneral.time.setText(time);
                    String pointHint = mContext.getResources().getString(R.string.msg_business_task_general_point_new);
                    // String point = String.format(pointHint,
                    // item.getDeliveryPoints());
                    viewHolderTaskGeneral.point.setText("");
                    viewHolderTaskGeneral.corpname.setText(item.getCorpName());


                    viewHolderTaskGeneral.tvcheckdetail.setVisibility(View.VISIBLE);
                    viewHolderTaskGeneral.rllnewtask.setVisibility(View.VISIBLE);
                    viewHolderTaskGeneral.time.setText(time);
//                    String pointHint = mContext.getResources().getString(R.string.msg_business_task_general_point_new);
                    // String point = String.format(pointHint,
                    // item.getDeliveryPoints());
//                    viewHolderTaskGeneral.point.setText(pointHint);

                    viewHolderTaskGeneral.tvTaskId.setText(item.getTaskId());
                    viewHolderTaskGeneral.tvVehicle.setText(item.getDeliveryVehicle());
                    viewHolderTaskGeneral.tvDeliveryPoints.setText(item.getDeliveryPoints());

                    viewHolderTaskGeneral.corpname.setText(item.getCorpName());

                    view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.OnItemClick(item);
                        }
                    });


                    break;
                }
                case MSG_BUSINESS_CANCEL_ORDER: {
                    viewHolderTaskGeneral = (ViewHolderTaskGeneral) view.getTag(R.id.business_msg_task_general_item);
                    viewHolderTaskGeneral.time.setText(time);
                    String pointHint = mContext.getResources().getString(R.string.msg_business_order_cancel);
                    String point = String.format(pointHint, MsgParseTool.getCuOrderIdFromMsgContent(item.getContent()));
                    viewHolderTaskGeneral.point.setText(point);
                    viewHolderTaskGeneral.tvcheckdetail.setVisibility(View.GONE);
                    viewHolderTaskGeneral.rllnewtask.setVisibility(View.GONE);
                    viewHolderTaskGeneral.corpname.setText(item.getCorpName());
                    break;
                }
                case MSG_BUSINESS_CANCEL_TASK: {
                    viewHolderTaskGeneral = (ViewHolderTaskGeneral) view.getTag(R.id.business_msg_task_general_item);
                    viewHolderTaskGeneral.time.setText(time);
                    String pointHint = mContext.getResources().getString(R.string.msg_business_task_cancel);
                    String point = String.format(pointHint, item.getCorpId());
                    //MLog.e("MSG_BUSINESS_CANCEL_TASK", item.getCorpId());
                    viewHolderTaskGeneral.point.setText(point);
                    viewHolderTaskGeneral.tvcheckdetail.setVisibility(View.GONE);
                    viewHolderTaskGeneral.rllnewtask.setVisibility(View.GONE);
                    viewHolderTaskGeneral.corpname.setText(item.getCorpName());
                    break;
                }
                case MSG_BUSINESS_SCHEDULE_SPEECH: {

//                    case #2
                    // MLog.e("adapter", "type" +MSG_BUSINESS_SCHEDULE_SPEECH );

                    viewHolderScheduleSpeech = (ViewHolderScheduleSpeech) view
                            .getTag(R.id.business_msg_schedule_speech_item);

                    String[] res = item.getContent().split("#");

                    String speechid = "-1";
                    String corpid = "-1";
                    if (res.length >= 4) {
                        speechid = res[3];
                        corpid = res[1];
                    }

                    final String speechids = speechid;
                    final String corpids = corpid;
                    if (GeneralSPHelper.getInstance(mContext).IsSpeechRead((corpid + speechid))) {
                        viewHolderScheduleSpeech.v_noread.setVisibility(View.GONE);
                    } else {
                        viewHolderScheduleSpeech.v_noread.setVisibility(View.VISIBLE);
                    }

                    viewHolderScheduleSpeech.ll_speech.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {


                            // EventBus.getDefault().post(new
                            // SpeechPlayEvent(speechids,corpids));
                            if ( position == positionClicked ) {

                                positionClicked = -1;

                                if (mListener != null) {
                                    mListener.OnStopPlaying(v);

                                }

                            } else {

                                positionClicked = position;

                                if (mListener != null) {
                                    mListener.OnStopPlaying(v);
                                    mListener.OnPlaySpeech(v, corpids, speechids);
                                }
                            }


                        }
                    });

                    // 音频时长

                    if (res.length >= 5) {

                        double length = CommonTool.convertToDouble(res[4], -1);

                        // MLog.e("音频时长",""+ res[4]);

                        if (length > 0) {

                            String st = "" + length;

                            if (length > 1.0)
                                st = "" + (int) Math.round(length);

                            viewHolderScheduleSpeech.tv_speechtime.setText(st + "\"");

                            viewHolderScheduleSpeech.tv_speechtime.setVisibility(View.VISIBLE);
                        } else
                            viewHolderScheduleSpeech.tv_speechtime.setVisibility(View.GONE);

                    } else {
                        viewHolderScheduleSpeech.tv_speechtime.setVisibility(View.GONE);
                    }

                    viewHolderScheduleSpeech.time.setText(time);
                    // viewHolderScheduleGeneral.content.setText(item.getContent());
                    viewHolderScheduleSpeech.corpname.setText(item.getCorpName());

                    break;

                }

                default:
                    break;
            }
        }
        return view;
    }


    public ImageView getHintPlayingImageView() {
        return ivHintPlaying;
    }


    /**
     * @Description: 邀请加入车队
     * @author: zhaoqy
     * @date: 2017年3月24日 下午4:25:30
     * @version: V1.0
     */
    final static class ViewHolderJoin {
        TextView time;
        TextView corpname;
        TextView refuse;
        TextView agree;
        TextView result;
    }

    /**
     * @Description: 踢出车队
     * @author: zhaoqy
     * @date: 2017年3月24日 下午4:28:24
     * @version: V1.0
     */
    final static class ViewHolderQuit {
        TextView time;
        TextView content;
        TextView corpname;
    }

    /**
     * @Description: K码调度消息
     * @author: zhaoqy
     * @date: 2017年3月24日 下午4:28:27
     * @version: V1.0
     */
    final static class ViewHolderScheduleKCode {
        TextView time;
        TextView content;
        TextView corpname;
        TextView poiname;
        TextView poiaddress;
        View navi;
    }

    /**
     * @Description: 普通调度消息
     * @author: zhaoqy
     * @date: 2017年3月24日 下午4:28:31
     * @version: V1.0
     */
    final static class ViewHolderScheduleGeneral {
        TextView time;
        TextView content;
        TextView corpname;
    }

    /**
     * @Description: 语音调度消息
     * @author: zhm
     * @date: 2017年3月24日 下午4:28:31
     * @version: V1.0
     */
    final static class ViewHolderScheduleSpeech {
        TextView time;
        TextView tv_speechtime;
        PercentRelativeLayout ll_speech;
        View v_noread;
        TextView corpname;
        ImageView ivHintPlaying;
    }

    /**
     * @Description: 提醒任务消息
     * @author: zhaoqy
     * @date: 2017年3月24日 下午4:28:35
     * @version: V1.0
     */
    final static class ViewHolderTaskRemind {
        TextView time;
        TextView taskid;
        TextView content;
        TextView corpname;
    }

    /**
     * @Description: 普通任务消息
     * @author: zhaoqy
     * @date: 2017年3月24日 下午4:28:38
     * @version: V1.0
     */
    final static class ViewHolderTaskGeneral {
        TextView time;
        TextView point;
        TextView corpname;
        TextView tvcheckdetail;
        PercentRelativeLayout rllnewtask;
        TextView tvTaskId;
        TextView tvVehicle;
        TextView tvDeliveryPoints;
    }

    public interface IRejectedListener {

        /**
         * 已拒绝
         */
        public void OnRejected(int errCode);
    }

    public interface IJoinedListener {

        /**
         * 已加入
         */
        public void OnJoined(int errCode);
    }

    private IBusinessMsgClickListener mListener;

    public void setBusinessMsgClickListener(IBusinessMsgClickListener listener) {
        mListener = listener;
    }

    public interface IBusinessMsgClickListener {

        public void OnItemClick(MsgContent business);

        /**
         * 拒绝加入车队
         */
        public void OnRefuse(MsgContent business, IRejectedListener listener);

        /**
         * 同意加入车队
         */
        public void OnAgree(MsgContent business, IJoinedListener listener);

        /**
         * 导航
         */
        public void OnNavi(MsgContent business);

        /**
         * 播放音频
         *
         */
        public void OnPlaySpeech(View v, String corpid, String speechid);

        public void OnStopPlaying(View v);

    }

    GeoCoder geoCoder;

    public String getPCDAddress(String kcode, OnGetGeoCoderResultListener listener) {

        String localpcdaddress = GeneralSPHelper.getInstance(mContext).getPCDAddress(kcode);

        if (!TextUtils.isEmpty(localpcdaddress))
            return localpcdaddress;

        if (geoCoder == null)
            geoCoder = GeoCoder.newInstance();

        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        HPWPoint point = CldCoordUtil.kcodeToCLD(kcode);
        LatLng latLng = new LatLng();
        latLng.latitude = point.y;
        latLng.longitude = point.x;
        option.location = latLng;
        geoCoder.setOnGetGeoCodeResultListener(listener);

        //
        // new OnGetGeoCoderResultListener() {
        //
        // @Override
        // public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
        //
        //
        // GeneralSPHelper.getInstance(mContext).getPCDAddress(kcode);
        //
        // messageViewHolder.mTvPoiAddress.setText(arg0.address);
        // if (arg0.errorCode == 0) {
        // messageViewHolder.mAddressLayout.setVisibility(View.VISIBLE);
        // }
        // }
        //
        // @Override
        // public void onGetGeoCodeResult(GeoCodeResult arg0) {
        //
        // }
        // });
        try {
            geoCoder.reverseGeoCode(option);
        } catch (IllegalSearchArgumentException e) {

            e.printStackTrace();
        }

        return null;
    }

}
