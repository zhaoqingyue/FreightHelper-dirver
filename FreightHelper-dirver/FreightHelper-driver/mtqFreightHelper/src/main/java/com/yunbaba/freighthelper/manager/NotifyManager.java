package com.yunbaba.freighthelper.manager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.yunbaba.api.trunk.TimeEarlyWarningService;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.msg.Filter;
import com.yunbaba.freighthelper.bean.msg.MsgContent;
import com.yunbaba.freighthelper.bean.msg.MsgInfo;
import com.yunbaba.freighthelper.db.MsgFilterTable;
import com.yunbaba.freighthelper.ui.activity.msg.AlarmMsgActivity;
import com.yunbaba.freighthelper.ui.activity.msg.BusinessMsgActivity;
import com.yunbaba.freighthelper.ui.adapter.AlarmMsgAdapter;
import com.yunbaba.freighthelper.ui.adapter.BusinessMsgAdapter;
import com.yunbaba.freighthelper.utils.MsgParseTool;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.ToastUtils;

public class NotifyManager {

	public static final int NOTIFY_ID = 1000;
	private Context mContext;
	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mBuilder;

	private static NotifyManager mNotifyManager = null;

	public static NotifyManager getInstance() {
		if (mNotifyManager == null) {
			synchronized (NotifyManager.class) {
				if (mNotifyManager == null) {
					mNotifyManager = new NotifyManager();
				}
			}
		}
		return mNotifyManager;
	}

	public NotifyManager() {
	}

	public void init(Context context) {
		mContext = context;
		mBuilder = new NotificationCompat.Builder(mContext);
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public void unInit() {
		mHandler.removeCallbacksAndMessages(null);
	}

	public void showIntentActivityNotify(MsgInfo msginfo) {
		if (msginfo == null)
			return;

		MsgContent msgContent = msginfo.getMsgContent();
		if (msgContent == null)
			return;

		// MLog.e("test", "showIntentActivityNotify ");
		Filter filter = MsgFilterTable.getInstance().query(msgContent.getFilterID(), 2);
		/**
		 * 通知开关设置为0, 则不通知
		 * 
		 * 加入/退出车队、调度消息、 新任务消息 没有通知开关，默认开启
		 */
		if (filter != null && filter.getOpen() != 1)
			return;

		Intent resultIntent = null;
		int business = Integer.parseInt(msginfo.getBusinessCode());
		switch (business) {
		case 1008:
		case 1012:
		case 1016:
		case 1010:
		case 1001:
		case 1099:
		case 1006:
		case 1004:
		case 2001: {
			resultIntent = new Intent(mContext, BusinessMsgActivity.class);
			break;
		}

		case 2002: {
			resultIntent = new Intent(mContext, AlarmMsgActivity.class);
			break;
		}
		default:
			break;
		}

		if (resultIntent == null)
			return;

		String title = "";
		String content = "";
		switch (msgContent.getLayout()) {
		case BusinessMsgAdapter.MSG_BUSINESS_JOIN: {
			title = mContext.getResources().getString(R.string.msg_business_join);

			String contentHint = mContext.getResources().getString(R.string.msg_business_join_corpname);
			String corpname = msgContent.getCorpName();
			String groupname = msgContent.getGroupName();
			content = corpname + String.format(contentHint, groupname);
			break;
		}
		case BusinessMsgAdapter.MSG_BUSINESS_QUIT: {
			title = mContext.getResources().getString(R.string.msg_business_quit);

			String groupnameHint = mContext.getResources().getString(R.string.msg_business_quit_groupname);
			String groupname = String.format(groupnameHint, msgContent.getGroupName());
			String corpnameHint = mContext.getResources().getString(R.string.msg_business_quit_corpname);
			String corpname = String.format(corpnameHint, msgContent.getCorpName());
			content = groupname + corpname;
			break;
		}
		case BusinessMsgAdapter.MSG_BUSINESS_SCHEDULE_KCODE:
		case BusinessMsgAdapter.MSG_BUSINESS_SCHEDULE_GENERAL: {
			title = mContext.getResources().getString(R.string.msg_business_schedule_kcode);

			if (TextUtils.isEmpty(msgContent.getContent()))
				content = "收到一条调度消息";
			else
				content = TextStringUtil.ReplaceHtmlTag(msgContent.getContent());
			break;
		}
		case BusinessMsgAdapter.MSG_BUSINESS_SCHEDULE_SPEECH: {
			title = mContext.getResources().getString(R.string.msg_business_schedule_kcode);

			content = "收到一条语音消息";// msgContent.getContent();
			break;
		}
		case BusinessMsgAdapter.MSG_BUSINESS_TASK_REMIND: {
			title = mContext.getResources().getString(R.string.msg_business_task_remind);

			String taskIdHint = mContext.getResources().getString(R.string.msg_business_task_remind_taskId);
			String taskId = String.format(taskIdHint, msgContent.getTaskId());
			
			
			if (msgContent.getTaskId().equals(TimeEarlyWarningService.MSGTYPE_ORIGINAL)) {
				content = msgContent.getContent();
			} else{
				content = taskId + msgContent.getContent();
			}
			
			break;
		}
		case BusinessMsgAdapter.MSG_BUSINESS_TASK_GENERAL: {
			title = mContext.getResources().getString(R.string.msg_business_task_general);

			String pointHint = mContext.getResources().getString(R.string.msg_business_task_general_point_new);
			//String point = msgContent.getDeliveryPoints();
			content = pointHint;//String.format(pointHint, point);
			break;
		}
		case BusinessMsgAdapter.MSG_BUSINESS_CANCEL_ORDER: {
			title = mContext.getResources().getString(R.string.msg_business_task_general);

			String pointHint = mContext.getResources().getString(R.string.msg_business_order_cancel);
			String point = MsgParseTool.getCuOrderIdFromMsgContent(msgContent.getContent());
			content = String.format(pointHint, point);
			break;
		}
		case BusinessMsgAdapter.MSG_BUSINESS_CANCEL_TASK: {
			title = mContext.getResources().getString(R.string.msg_business_task_general);

			String pointHint = mContext.getResources().getString(R.string.msg_business_task_cancel);
			String point = msgContent.getCorpId();
			content = String.format(pointHint, point);
			break;
		}
		case BusinessMsgAdapter.MSG_ALARM: {
			switch (msgContent.getAlarmType()) {
			case AlarmMsgAdapter.ALARM_CAR_SAFETY: {
				title = mContext.getResources().getString(R.string.switch_car_safety);
				break;
			}
			case AlarmMsgAdapter.ALARM_CAR_ABNORMAL: {
				title = mContext.getResources().getString(R.string.switch_car_abnormal);
				break;
			}
			case AlarmMsgAdapter.ALARM_DEVICE_ABNORMAL: {
				title = mContext.getResources().getString(R.string.switch_device_abnormal);
				break;
			}
			default:
				break;
			}
			content = msgContent.getContent();
			break;
		}
		default:
			break;
		}

		// MLog.e("test", "content: " + content);
		Message msg = new Message();
		msg.what = 0;
		msg.obj = (Object) content;
		mHandler.sendMessage(msg);

		resultIntent.putExtra("extra", true);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setAutoCancel(true).setSmallIcon(R.drawable.ic_launcher).setWhen(System.currentTimeMillis())
				.setContentTitle(title).setContentText(content);
		mBuilder.setContentIntent(pendingIntent);
		mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
	}

	public void cancelAllNotification() {
		if (mNotificationManager != null)
			mNotificationManager.cancelAll();
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0: {
				String content = (String) msg.obj;
				ToastUtils.showMessageLong(mContext, content);
				// Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		}
	};
	
	public Context getContext(){
		return mContext;
	}
}
