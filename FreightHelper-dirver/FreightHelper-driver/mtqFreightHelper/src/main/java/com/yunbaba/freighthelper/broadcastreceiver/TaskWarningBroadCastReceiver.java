package com.yunbaba.freighthelper.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TaskWarningBroadCastReceiver extends BroadcastReceiver {

	private String TAG = "TaskWarningBroadCastReceiver";

	@Override
	public void onReceive(Context ct, Intent intent) {


		return;

//		if (!intent.getAction().equals(TimeEarlyWarningService.ACTION_WARNING)) {
//			MLog.e(TAG, intent.getAction());
//			return;
//		}
//
//		if (!intent.hasExtra("taskwarninginfo"))
//			return;
//
//		String infostr = intent.getStringExtra("taskwarninginfo");
//
//		if (infostr == null)
//			return;
//
//		TaskWarningInfo info = GsonTool.getInstance().fromJson(infostr, TaskWarningInfo.class);
//
//		if (info == null || info.getCu_orderid() == null)
//			return;
//
//		MLog.e(TAG, "receive");
//
//		if (getSPhelper(ct).isFinishRemindOrder(info.declareid, info.type))
//			return;
//
//		else {
//			// 首先判断任务是否完成，若完成则不提醒
//
//			boolean isFinish = true;
//
//			MtqDeliTaskDetail detail = TaskOperator.getInstance().getTaskDetailDataFromDB(info.taskId);
//
//			if (detail != null) {
//
//				Iterator<MtqDeliStoreDetail> iter = detail.store.iterator();
//				MtqDeliStoreDetail tmp;
//				traversal:while (iter.hasNext()) {
//					// 运货中
//					tmp = iter.next();
//					if (tmp.cust_orderid.equals(info.cu_orderid) && tmp.taskId.equals(info.taskId)) {
//
//						if (tmp.storestatus == 2) {
//
//							// 任务未完成
//							isFinish = false;
//							break traversal;
//						}
//					}
//				}
//			} else {
//
//				boolean isExist = TaskOperator.getInstance().isExistTask(info.taskId);
//
//				if (isExist)
//					isFinish = false;
//				else
//					isFinish = true;
//
//			}
//
//			if (isFinish) {
//				getSPhelper(ct).FinishRemindOrder(info.declareid, info.type);
//				return;
//			}
//
//			getSPhelper(ct).FinishRemindOrder(info.declareid, info.type);
//			MsgManager.getInstance().createRemindMsg(info.WarningContent);
//		}
//
//	}
//
//	private GeneralSPHelper sptool;
//
//	public synchronized GeneralSPHelper getSPhelper(Context ct) {
//
//		if (sptool == null)
//			sptool = GeneralSPHelper.getInstance(ct);
//
//		return sptool;
//	}
	}
}
