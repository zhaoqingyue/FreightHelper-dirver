package com.yunbaba.api.trunk;

import com.yunbaba.freighthelper.bean.TaskSpInfo;
import com.yunbaba.freighthelper.bean.eventbus.SpeechDownloadEvent;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.bean.msg.MsgInfo;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MsgUtils;
import com.yunbaba.ols.sap.bean.CldSapKMParm;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理从服务器发来的消息 专门处理任务相关消息 非任务相关消息不处理
 */
public class TaskMsgDispatcher {

	private static TaskMsgDispatcher instance = new TaskMsgDispatcher();

	public static final int TYPE_MSGTYPE = 99;
	public static final int TYPE_AUTHTYPE = 101;
	public static final String CODE_NEWTASK = "1001";
	public static final String CODE_MODIFYTASK = "1011";
	public static final String CODE_DISABLETASK = "1012";
	public static final String CODE_STOREAUTHCHANGE = "1014";
	public static final String CODE_SPEECH = "2001";
	ArrayList<String> deletetaskidlist;
	ArrayList<TaskSpInfo> updatetaskidlist;

	public static TaskMsgDispatcher getInstance() {
		return instance;
	}

	public synchronized void analyzeMsg(List<CldSapKMParm> lists) {

		ArrayList<MsgInfo> flist = new ArrayList<MsgInfo>();
		MsgInfo tmsg = null;
		int len = lists.size();
		for (int i = 0; i < len; i++) {

			tmsg = MsgUtils.parm2MsgInfoForTask(lists.get(i));
			if (tmsg != null) {
				flist.add(tmsg);
			}
		}

		// MLog.e("check", GsonTool.getInstance().toJson(flist));

		boolean isHasNewTask = false;
		deletetaskidlist = new ArrayList<>();
		updatetaskidlist = new ArrayList<>();

		for (MsgInfo msg : flist) {

			// MLog.e("checkrecvmsg", "list res:" +
			// GsonTool.getInstance().toJson(msg));
			// MLog.e("checkrecvmsg", "list res:" + msg.getMsgType());
			// MLog.e("checkrecvmsg", "list res:" + msg.getBusinessCode());

			if (msg.getMsgType() == TYPE_MSGTYPE) {
				// MLog.e("checkrecvmsg", "list res:" + msg.getMsgType());

				if (msg.getBusinessCode().equals(CODE_NEWTASK)) {
					// MLog.e("checkrecvmsg", "list res:" +
					// msg.getBusinessCode());

					isHasNewTask = true;

				} else if (msg.getBusinessCode().equals(CODE_DISABLETASK)) {

					// MLog.e("checkrecvmsgdeletetask", "list res:" +
					//msg.getBusinessCode());

					// EventBus.getDefault().post(new
					// RefreshTaskListFromNetEvent());

					// 任务作废，判断是否过期
					// 无法判断是否已过期，因为这个是针对任务而不是运货单的
					// String date =
					// TimestampTool.getDayDate(System.currentTimeMillis());
					//
					//
					// wtask = new
					// TimeWarningTask(combineTaskInfoToContent(task, time, 16),
					// time.cust_orderid + date,
					// task.taskid, time.cust_orderid, 16);
					//
					// msg.getMsgContent().get
					//
					//
					// String WarningContent = "1099#" + task.taskid + "#" +
					// task.corpname + "#" + task.corpid + "#"
					// + getTypeContent(time.cust_orderid, type) + "#" + type;
					//
					//
					//
					// MsgManager.getInstance().createRemindMsg(WarningContent);

					String[] res = msg.getContent().split("#");

					if (res.length >= 2) {
						String taskid = res[1];

						 MLog.e("checkrecvmsgdeletetask", taskid + "" + res.toString());

						deletetaskidlist.add(taskid);
					}

				} else if (msg.getBusinessCode().equals(CODE_MODIFYTASK)) {

					String[] res = msg.getContent().split("#");
					if (res.length >= 6) {
						String taskid = res[1];
						String corpid = res[5];

						updatetaskidlist.add(new TaskSpInfo(taskid, corpid, ""));
					}

				} else if (msg.getBusinessCode().equals(CODE_SPEECH)) {

					//MLog.e("speechmsg", msg.getContent());

					String[] res = msg.getContent().split("#");

					String speechid = "-1";
					String corpid = "-1";

					if (res.length >= 4) {
						speechid = res[3];
						corpid = res[1];
					}
					EventBus.getDefault().post(new SpeechDownloadEvent(speechid, corpid));

				}

			} else if (msg.getMsgType() == TYPE_AUTHTYPE) {

				if (msg.getBusinessCode().equals(CODE_STOREAUTHCHANGE)) {

					DeliveryApi.getInstance().RefreshStoreAuth();

				}

			}

		}

		if (isHasNewTask)
			EventBus.getDefault().post(new TaskBusinessMsgEvent(1, null,null));

		if (deletetaskidlist != null && deletetaskidlist.size() > 0) {

			EventBus.getDefault().post(new TaskBusinessMsgEvent(3, deletetaskidlist,null));

		}

		if (updatetaskidlist != null && updatetaskidlist.size() > 0) {

			EventBus.getDefault().post(new TaskBusinessMsgEvent(2, null,updatetaskidlist));

		}

		// else {
		// // MLog.e("checkrecvmsg", "list res: no delete" );
		//
		// }

	}

	// private GeneralSPHelper sptool;
	//
	// public synchronized GeneralSPHelper getSPhelper() {
	//
	// if (sptool == null)
	// sptool = GeneralSPHelper.getInstance(this);
	//
	// return sptool;
	// }

}
