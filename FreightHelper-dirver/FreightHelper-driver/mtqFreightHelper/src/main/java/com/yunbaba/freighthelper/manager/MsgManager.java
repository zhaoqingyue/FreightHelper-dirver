/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: MsgManager.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.manager
 * @Description: 消息管理器
 * @author: zhaoqy
 * @date: 2017年3月28日 下午2:25:11
 * @version: V1.0
 */

package com.yunbaba.freighthelper.manager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.cld.device.CldPhoneNet;
import com.cld.utils.CldPackage;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.trunk.TaskMsgDispatcher;
import com.yunbaba.freighthelper.bean.eventbus.NetworkAvailableEvent;
import com.yunbaba.freighthelper.bean.eventbus.NewMsgEvent;
import com.yunbaba.freighthelper.bean.msg.Filter;
import com.yunbaba.freighthelper.bean.msg.MsgInfo;
import com.yunbaba.freighthelper.db.MsgInfoTable;
import com.yunbaba.freighthelper.utils.Config;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.MsgUtils;
import com.yunbaba.ols.api.CldKServiceAPI;
import com.yunbaba.ols.sap.bean.CldSapKMParm;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MsgManager {
	public static final String TAG = "MsgManager";

	public static final int MSG_ALL = 0;
	public static final int MSG_BUSINESS = 1;
	public static final int MSG_ALARM = 2;

	private static MsgManager mMsgManager = null;
	private MsgDownLoader mMsgDownLoader;

	private ArrayList<MsgInfo> mBusinessList;
	private ArrayList<MsgInfo> mAlarmList;

	public static MsgManager getInstance() {
		if (mMsgManager == null) {
			synchronized (MsgManager.class) {
				if (mMsgManager == null) {
					mMsgManager = new MsgManager();
				}
			}
		}
		return mMsgManager;
	}

	public MsgManager() {
		mMsgDownLoader = new MsgDownLoader();
	}

	public void init() {
		mMsgDownLoader.init();
		mMsgDownLoader.startGetMsg();
	}

	public void unInit() {
		mMsgDownLoader.stopGetMsg();
	}

//	private Runnable getBusinessMsg = new Runnable() {
//
//		@Override
//		public void run() {
//			mBusinessList = MsgInfoTable.getInstance().query(MSG_BUSINESS);
//		}
//	};
//
//	private Runnable getMsg = new Runnable() {
//
//		@Override
//		public void run() {
//			mBusinessList = MsgInfoTable.getInstance().query(MSG_BUSINESS);
//			mAlarmList = MsgInfoTable.getInstance().query(MSG_ALARM);
//		}
//	};

	private Handler mHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0: {

				break;
			}
			default:
				break;
			}
		}
	};

	public ArrayList<MsgInfo> getMsg(int type,int page ,int size) {
		switch (type) {
			case MSG_BUSINESS:
				if (mBusinessList == null) {
					ArrayList<MsgInfo> temp = MsgInfoTable.getInstance().query(type,((page-1)*size),size);
					mBusinessList = new ArrayList<MsgInfo>();
					mBusinessList.addAll(temp);
				}
				return mBusinessList;
			case MSG_ALARM:
				if (mAlarmList == null) {
					ArrayList<MsgInfo> temp = MsgInfoTable.getInstance().query(type,((page-1)*size),size);
					mAlarmList = new ArrayList<MsgInfo>();
					mAlarmList.addAll(temp);
				}
				return mAlarmList;
			default:
				break;
		}
		return null;
	}

//	public ArrayList<MsgInfo> getMsg(int type,int page,int size) {
//		switch (type) {
//		case MSG_BUSINESS:
//			if (mBusinessList == null) {
//				ArrayList<MsgInfo> temp = MsgInfoTable.getInstance().query(type,(page-1)*size,size);
//				mBusinessList = new ArrayList<MsgInfo>();
//				mBusinessList.addAll(temp);
//			}
//			return mBusinessList;
//		case MSG_ALARM:
//			if (mAlarmList == null) {
//				ArrayList<MsgInfo> temp = MsgInfoTable.getInstance().query(type,(page-1)*size,size);
//				mAlarmList = new ArrayList<MsgInfo>();
//				mAlarmList.addAll(temp);
//			}
//			return mAlarmList;
//		default:
//			break;
//		}
//		return null;
//	}

//	public ArrayList<MsgInfo> queryMsg(int type) {
//		ArrayList<MsgInfo> temp = MsgInfoTable.getInstance().query(type);
//		switch (type) {
//		case MSG_BUSINESS: {
//			if (mBusinessList != null) {
//				mBusinessList.clear();
//			} else {
//				mBusinessList = new ArrayList<MsgInfo>();
//			}
//			mBusinessList.addAll(temp);
//			break;
//		}
//		case MSG_ALARM: {
//			if (mAlarmList != null) {
//				mAlarmList.clear();
//			} else {
//				mAlarmList = new ArrayList<MsgInfo>();
//			}
//			mAlarmList.addAll(temp);
//			break;
//		}
//		default:
//			break;
//		}
//		return temp;
//	}


	public ArrayList<MsgInfo> queryMsg2(int type,int page,int size) {
		ArrayList<MsgInfo> temp = MsgInfoTable.getInstance().query(type,(page-1)*size,size);
//		switch (type) {
//			case MSG_BUSINESS: {
//				if (mBusinessList != null) {
//					mBusinessList.clear();
//				} else {
//					mBusinessList = new ArrayList<MsgInfo>();
//				}
//				mBusinessList.addAll(temp);
//				break;
//			}
//			case MSG_ALARM: {
//				if (mAlarmList != null) {
//					mAlarmList.clear();
//				} else {
//					mAlarmList = new ArrayList<MsgInfo>();
//				}
//				mAlarmList.addAll(temp);
//				break;
//			}
//			default:
//				break;
//		}
		MLog.e("checkmsgsize","sizesdasdasd"+temp.size());
		return temp;
	}

	public ArrayList<MsgInfo> queryMsg(int type,int page,int size) {
		ArrayList<MsgInfo> temp = MsgInfoTable.getInstance().query(type,(page-1)*size,size);
		switch (type) {
			case MSG_BUSINESS: {
				if (mBusinessList != null) {
					mBusinessList.clear();
				} else {
					mBusinessList = new ArrayList<MsgInfo>();
				}
				mBusinessList.addAll(temp);
				break;
			}
			case MSG_ALARM: {
				if (mAlarmList != null) {
					mAlarmList.clear();
				} else {
					mAlarmList = new ArrayList<MsgInfo>();
				}
				mAlarmList.addAll(temp);
				break;
			}
			default:
				break;
		}
		return temp;
	}

	public boolean hasUnReadMsg() {
		boolean readMark = false;
		List<MsgInfo> msgList = MsgInfoTable.getInstance().queryUnReadMsg(1);
		MLog.e("checkunreadmsg","sizeall"+(msgList!=null?msgList.size():0));

		if(msgList!=null && msgList.size()>0){

			MLog.e("checkunreadmsg","msginfo"+ GsonTool.getInstance().toJson(msgList.get(0)));

		}

		if (msgList != null && !msgList.isEmpty()) {
			readMark = true;
		}
		return readMark;
	}

	/**
	 * 是否有未读消息
	 */
	public boolean hasUnReadMsg(int type) {
		boolean readMark = false;

		List<MsgInfo> msgList = MsgInfoTable.getInstance().queryUnReadMsg(type,1);
		MLog.e("checkunreadmsg","sizetype"+type+" "+(msgList!=null?msgList.size():0));
		if (msgList != null && !msgList.isEmpty()) {
			readMark = true;
		}
		return readMark;
	}

	public List<MsgInfo> getFilterMsg(int type, List<Filter> filters,int page ,int size) {

		int moffset = (page-1)*size;
		return MsgInfoTable.getInstance().filterQuery(type, filters, moffset ,size);
	}

	public List<MsgInfo> getFilterMsg(int type, List<Filter> filters,int page ,int size,int startpos) {

		int moffset = (page-1)*size;
		return MsgInfoTable.getInstance().filterQuery(type, filters, moffset ,size);
	}

	/**
	 * MsgManager.getInstance().createRemindMsg("1099#配送任务ID#物流企业#企业ID#消息内容#14")
	 * ;
	 * 
	 * @Title: createRemindMsg
	 * @Description: 提供给外部设置任务提醒消息
	 * @param content
	 *            : 1099#配送任务ID#物流企业#企业ID#消息内容#筛选ID 筛选ID: FILTER_ID_14 = 14; //
	 *            运单超时报警 FILTER_ID_15 = 15; // 运单超时提醒 FILTER_ID_16 = 16; //
	 *            运单过期报警 FILTER_ID_17 = 17; // 运单过期提醒
	 * @return: void
	 */
	public void createRemindMsg(String content) {
		MsgInfo temp = new MsgInfo();
		long stamp = System.currentTimeMillis();
		long msgid = stamp; // (stamp/1000);
		MLog.e(TAG, "msgid: " + msgid);
		MLog.i(TAG, "content: " + content);
		temp.setMessageId(msgid);
		temp.setMsgType(99);
		temp.setCreatetime(stamp + "");
		temp.setContent(content);
		MsgInfo msginfo = MsgUtils.parseMsgInfo(temp);
		if (msginfo != null) {
			MsgInfoTable.getInstance().insert(msginfo);
			/**
			 * 通知有新消息，更新界面
			 */
			EventBus.getDefault().post(new NewMsgEvent(MessageId.MSGID_MSG_NEW));
			/**
			 * 发送通知
			 */

			NotifyManager.getInstance().showIntentActivityNotify(msginfo);
		}
	}

	public static class MsgDownLoader {

		private static final int GETMSG_INTERVAL = 30 * 1000;
		private long duid;
		private long kuid;
		private int apptype;
		private int bussinessid;
		private int appid;
		private String prover;
		private String session;
		private Timer mTimer;
		private TimerTask mTask;

		public void init() {
			apptype = Config.apptype;
			appid = Config.appid;
			bussinessid = Config.bussinessid;
			prover = CldPackage.getAppVersion();
		}

		/**
		 * 接收消息
		 */
		public void recMsg() {
			if (!CldPhoneNet.isNetConnected())
				return;

			duid = AccountAPI.getInstance().getDuid();
			kuid = AccountAPI.getInstance().getKuidLogin();
			session = AccountAPI.getInstance().getSession();
			List<CldSapKMParm> tempList = new ArrayList<CldSapKMParm>();
			int errCode = CldKServiceAPI.getInstance().recMessage(duid, apptype, prover, tempList, kuid, 0, 0, 0,
					bussinessid, session, appid);
			checkSession(errCode, session);
			if (errCode == 0) {


               EventBus.getDefault().post(new NetworkAvailableEvent(true));


				if (tempList != null && !tempList.isEmpty()) {
					List<CldSapKMParm> list = new ArrayList<CldSapKMParm>();
					int len = tempList.size();
					for (int i = 0; i < len; i++) {
						CldSapKMParm parm = tempList.get(i);
						/**
						 * 货运消息：99
						 */
						if (parm.getMsgType() == 99 || parm.getMsgType() == 101) {
							list.add(parm);
						}

					}
					dealMsg(list);
				}
			}
		}

		private void dealMsg(List<CldSapKMParm> list) {

			if (list != null && !list.isEmpty()) {

				MLog.e("recevmsg", GsonTool.getInstance().toJson(list));

				// /***
				// * 只打印车辆消息
				// *
				// **/
				//
				// String businessCode = "";
				// String content = "";
				//
				// int tlen = list.size();
				// for (int i = 0; i < tlen; i++) {
				//
				// content = list.get(i).getOperateMsg().getContent();
				//
				// businessCode = content.substring(0, 4);
				//
				//
				//
				// int business = Integer.parseInt(businessCode);
				//
				// if (business == 2002) {
				// //MLog.e("车辆报警消息2",content+"");
				// MLog.e("车辆报警消息",content+" "+business);
				// MyDebugTool.writeDataIntoFile(content+"\n");
				//
				// }
				// }

				

				ArrayList<MsgInfo> msgList = new ArrayList<MsgInfo>();
				MsgInfo msg = null;
				int len = list.size();
				for (int i = 0; i < len; i++) {

					msg = MsgUtils.parm2MsgInfo(list.get(i));

					// if(msg.getBusinessCode().equals("2001")){
					//
					// MLog.e("receive msg", "speech"+ msg.getContent());
					// }

					if (msg != null) {
						msgList.add(msg);
					}
				}
				
				/**
				 * 只处理任务相关的消息
				 */
				TaskMsgDispatcher.getInstance().analyzeMsg(list);
				
				/**
				 * 将新接收的消息插入到数据库中
				 */
				MsgInfoTable.getInstance().insert(msgList);
				
				
				
				/**
				 * 通知有新消息，更新界面
				 */
				EventBus.getDefault().post(new NewMsgEvent(MessageId.MSGID_MSG_NEW));
				/**
				 * 发送通知
				 */

				int msgLen = msgList.size();
				if (msgLen > 0) {
					int index = msgLen - 1;
					NotifyManager.getInstance().showIntentActivityNotify(msgList.get(index));
				}

			}
		}

		public void startGetMsg() {
			stopGetMsg();
			if (mTask == null) {
				mTask = new MsgTimerTask();
			}

			if (mTimer == null) {
				mTimer = new Timer();
				mTimer.schedule(mTask, 1000, GETMSG_INTERVAL);
			}
		}

		public void stopGetMsg() {
			if (mTimer != null) {
				mTimer.cancel();
				mTimer = null;
			}

			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
		}

		/**
		 * session是否失效
		 */
		public void checkSession(int errCode, String session) {
			if (CldKServiceAPI.getInstance().isSessionInValid(errCode)) {
			}
		}

		private class MsgTimerTask extends TimerTask {

			@Override
			public void run() {
				recMsg();
			}
		};
	}
}
