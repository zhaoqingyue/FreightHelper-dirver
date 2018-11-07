/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: MsgContentTable.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.db
 * @Description: 消息内容Table
 * @author: zhaoqy
 * @date: 2017年4月10日 下午2:49:44
 * @version: V1.0
 */

package com.yunbaba.freighthelper.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.yunbaba.freighthelper.bean.msg.MsgContent;
import com.yunbaba.freighthelper.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class MsgContentTable {
	
	public static final String TAG = "MsgContentTable";
	
	public static final String MSGCONTENT_TABLE = "msg_content_table";
	public static final Uri CONTENT_SORT_URI = Uri.parse("content://"
			+ DbManager.AUTHORITY + "/" + MSGCONTENT_TABLE);

	public static final String ID = "_id";
	public static final String MSG_MSGID = "msg_msgid";
	public static final String CONTENT_BUSINESS = "businesscode";
	public static final String CONTENT_MSG_TYPE = "msgType";
	public static final String CONTENT_LAYOUT = "layout";
	public static final String CONTENT_CREATETIME = "createtime";
	public static final String CONTENT_CONTENT = "content";
	public static final String CONTENT_FILTERID = "filterid";
	public static final String CONTENT_INVITECODE = "inviteCode";
	public static final String CONTENT_UTCTIME = "utcTime";
	public static final String CONTENT_CORPID = "corpid";
	public static final String CONTENT_CORPNAME = "corpname";
	public static final String CONTENT_GROUPID = "groupid";
	public static final String CONTENT_GROUPNAME = "groupname";
	public static final String CONTENT_LOCKCORPID = "lockcorpid";
	public static final String CONTENT_TASKID = "taskId";
	public static final String CONTENT_POINTS = "deliveryPoints";
	public static final String CONTENT_VEHICLE = "deliveryVehicle";
	public static final String CONTENT_KCODE = "kcode";
	public static final String CONTENT_POINAME = "poiname";
	public static final String CONTENT_POIADDRESS = "poiAddress";
	public static final String CONTENT_BRAND = "brand";
	public static final String CONTENT_ALARMID = "alarmid";
	public static final String CONTENT_ALARMTIME = "alarmtime";
	public static final String CONTENT_ALARMX = "alarmx";
	public static final String CONTENT_ALARMY = "alarmy";
	public static final String CONTENT_ALARMNAME = "alarmname";
	public static final String CONTENT_ALARM_TYPE = "alarmtype";
	

	private static MsgContentTable mInstance = null;

	public  synchronized static MsgContentTable getInstance() {
		if (mInstance == null) {
			synchronized (MsgContentTable.class) {
				if (mInstance == null) {
					mInstance = new MsgContentTable();
				}
			}
		}
		return mInstance;
	}

	public  synchronized String getCreateSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE  IF NOT EXISTS ");
		sb.append(MSGCONTENT_TABLE);
		sb.append("(");
		sb.append(ID);
		sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
		sb.append(MSG_MSGID);
		sb.append(" Integer,");
		sb.append(CONTENT_BUSINESS);
		sb.append(" Integer,");
		sb.append(CONTENT_MSG_TYPE);
		sb.append(" Integer,");
		sb.append(CONTENT_LAYOUT);
		sb.append(" Integer,");
		sb.append(CONTENT_CREATETIME);
		sb.append(" TEXT,");
		sb.append(CONTENT_CONTENT);
		sb.append(" TEXT,");
		sb.append(CONTENT_FILTERID);
		sb.append(" Integer,");
		sb.append(CONTENT_INVITECODE);
		sb.append(" TEXT,");
		sb.append(CONTENT_UTCTIME);
		sb.append(" TEXT,");
		sb.append(CONTENT_CORPID);
		sb.append(" TEXT,");
		sb.append(CONTENT_CORPNAME);
		sb.append(" TEXT,");
		sb.append(CONTENT_GROUPID);
		sb.append(" TEXT,");
		sb.append(CONTENT_GROUPNAME);
		sb.append(" TEXT,");
		sb.append(CONTENT_LOCKCORPID);
		sb.append(" TEXT,");
		sb.append(CONTENT_TASKID);
		sb.append(" TEXT,");
		sb.append(CONTENT_POINTS);
		sb.append(" TEXT,");
		sb.append(CONTENT_VEHICLE);
		sb.append(" TEXT,");
		sb.append(CONTENT_KCODE);
		sb.append(" TEXT,");
		sb.append(CONTENT_POINAME);
		sb.append(" TEXT,");
		sb.append(CONTENT_POIADDRESS);
		sb.append(" TEXT,");
		sb.append(CONTENT_BRAND);
		sb.append(" TEXT,");
		sb.append(CONTENT_ALARMID);
		sb.append(" TEXT,");
		sb.append(CONTENT_ALARMTIME);
		sb.append(" TEXT,");
		sb.append(CONTENT_ALARMX);
		sb.append(" TEXT,");
		sb.append(CONTENT_ALARMY);
		sb.append(" TEXT,");
		sb.append(CONTENT_ALARMNAME);
		sb.append(" TEXT,");
		sb.append(CONTENT_ALARM_TYPE);
		sb.append(" Integer");
		sb.append(");");

		return sb.toString();
	}

	public  synchronized String getUpgradeSql() {
		String string = "DROP TABLE IF EXISTS " + MSGCONTENT_TABLE;
		return string;
	}

	public  synchronized void insert(MsgContent content) {
		if (DbManager.mDbHelper == null)
			return;

		if (content == null)
			return;

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		
		//MLog.e(TAG, "insert msgid: " + content.getMessageId());
		if (query(content.getMessageId()) != null) {
			delete(content.getMessageId());
		}
		ContentValues values = new ContentValues();
		values.put(MSG_MSGID, content.getMessageId());
		values.put(CONTENT_BUSINESS, content.getBusinessCode());
		values.put(CONTENT_MSG_TYPE, content.getMsgType());
		values.put(CONTENT_LAYOUT, content.getLayout());
		values.put(CONTENT_CREATETIME, content.getCreatetime());
		values.put(CONTENT_CONTENT, content.getContent());
		values.put(CONTENT_FILTERID, content.getFilterID());
		values.put(CONTENT_INVITECODE, content.getInviteCode());
		values.put(CONTENT_UTCTIME, content.getUtcTime());
		values.put(CONTENT_CORPID, content.getCorpId());
		values.put(CONTENT_CORPNAME, content.getCorpName());
		values.put(CONTENT_GROUPID, content.getGroupId());
		values.put(CONTENT_GROUPNAME, content.getGroupName());
		values.put(CONTENT_LOCKCORPID, content.getLockcorpId());
		values.put(CONTENT_TASKID, content.getTaskId());
		values.put(CONTENT_POINTS, content.getDeliveryPoints());
		values.put(CONTENT_VEHICLE, content.getDeliveryVehicle());
		values.put(CONTENT_KCODE, content.getKCode());
		values.put(CONTENT_POINAME, content.getPoiName());
		values.put(CONTENT_POIADDRESS, content.getPoiAddress());
		values.put(CONTENT_BRAND, content.getBrand());
		values.put(CONTENT_ALARMID, content.getAlarmId());
		values.put(CONTENT_ALARMTIME, content.getAlarmTime());
		values.put(CONTENT_ALARMX, content.getAlarmX());
		values.put(CONTENT_ALARMY, content.getAlarmY());
		values.put(CONTENT_ALARMNAME, content.getAlarmName());
		values.put(CONTENT_ALARM_TYPE, content.getAlarmType());
		db.insert(MSGCONTENT_TABLE, null, values);
	}
	
	public  synchronized void update(MsgContent content) {
		if (DbManager.mDbHelper == null || content == null)
			return;

		String where = null;
		where = MSG_MSGID + "=" + content.getMessageId();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

		ContentValues values = new ContentValues();
		values.put(CONTENT_CORPID, content.getCorpId()); 
		db.update(MSGCONTENT_TABLE, values, where, null);
	}

	public synchronized MsgContent query(long msgid) {
		if (DbManager.mDbHelper == null)
			return null;

		//MLog.e(TAG, "query msgid: " + msgid);
		MsgContent content = null;
		Cursor cursor = null;
		String where = null;
		String orderBy = null;
		where = MSG_MSGID + "=" + msgid;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

		try {

			cursor = db.query(MSGCONTENT_TABLE, null, where, null, null, null,
					orderBy);
			if (cursor != null) {
				cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
						CONTENT_SORT_URI);
				int len = cursor.getCount();
				for (int i = 0; i < len; i++) {
					cursor.moveToPosition(i);
					content = new MsgContent();
					content.setMessageId(cursor.getLong(cursor
							.getColumnIndex(MSG_MSGID)));
					content.setBusinessCode(cursor.getInt(cursor
							.getColumnIndex(CONTENT_BUSINESS)));
					content.setMsgType(cursor.getInt(cursor
							.getColumnIndex(CONTENT_MSG_TYPE)));
					content.setLayout(cursor.getInt(cursor
							.getColumnIndex(CONTENT_LAYOUT)));
					String stamp = cursor.getString(cursor.getColumnIndex(CONTENT_CREATETIME));
					String time = TimeUtils.stampToDate(stamp);
					content.setCreatetime(time);
					content.setContent(cursor.getString(cursor
							.getColumnIndex(CONTENT_CONTENT)));
					content.setFilterID(cursor.getInt(cursor
							.getColumnIndex(CONTENT_FILTERID)));
					content.setInviteCode(cursor.getString(cursor
							.getColumnIndex(CONTENT_INVITECODE)));
					content.setUtcTime(cursor.getString(cursor
							.getColumnIndex(CONTENT_UTCTIME)));
					content.setCorpId(cursor.getString(cursor
							.getColumnIndex(CONTENT_CORPID)));
					content.setCorpName(cursor.getString(cursor
							.getColumnIndex(CONTENT_CORPNAME)));
					content.setGroupId(cursor.getString(cursor
							.getColumnIndex(CONTENT_GROUPID)));
					content.setGroupName(cursor.getString(cursor
							.getColumnIndex(CONTENT_GROUPNAME)));
					content.setLockcorpId(cursor.getString(cursor
							.getColumnIndex(CONTENT_LOCKCORPID)));
					content.setTaskId(cursor.getString(cursor
							.getColumnIndex(CONTENT_TASKID)));
					content.setDeliveryPoints(cursor.getString(cursor
							.getColumnIndex(CONTENT_POINTS)));
					content.setDeliveryVehicle(cursor.getString(cursor
							.getColumnIndex(CONTENT_VEHICLE)));
					content.setKCode(cursor.getString(cursor
							.getColumnIndex(CONTENT_KCODE)));
					content.setPoiName(cursor.getString(cursor
							.getColumnIndex(CONTENT_POINAME)));
					content.setPoiAddress(cursor.getString(cursor
							.getColumnIndex(CONTENT_POIADDRESS)));
					content.setBrand(cursor.getString(cursor
							.getColumnIndex(CONTENT_BRAND)));
					content.setAlarmId(cursor.getString(cursor
							.getColumnIndex(CONTENT_ALARMID)));
					content.setAlarmTime(cursor.getString(cursor
							.getColumnIndex(CONTENT_ALARMTIME)));
					content.setAlarmX(cursor.getString(cursor
							.getColumnIndex(CONTENT_ALARMX)));
					content.setAlarmY(cursor.getString(cursor
							.getColumnIndex(CONTENT_ALARMY)));
					content.setAlarmName(cursor.getString(cursor
							.getColumnIndex(CONTENT_ALARMNAME)));
					content.setAlarmType(cursor.getInt(cursor
							.getColumnIndex(CONTENT_ALARM_TYPE)));
				}
			}

		}finally {
			if (cursor != null) {
				cursor.close();
				//db.close();
				cursor = null;
			}
		}

		return content;
	}
	
	public  synchronized List<MsgContent> query() {
		if (DbManager.mDbHelper == null)
			return null;

		List<MsgContent> contentList = new ArrayList<MsgContent>();
		MsgContent content = null;
		Cursor cursor = null;
		String where = null;
		String orderBy = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		try {


			cursor = db.query(MSGCONTENT_TABLE, null, where, null, null, null,
					orderBy);
			if (cursor != null) {
				cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
						CONTENT_SORT_URI);
				int len = cursor.getCount();
				for (int i = 0; i < len; i++) {
					cursor.moveToPosition(i);
					content = new MsgContent();
					content.setMessageId(cursor.getLong(cursor
							.getColumnIndex(MSG_MSGID)));
					content.setBusinessCode(cursor.getInt(cursor
							.getColumnIndex(CONTENT_BUSINESS)));
					content.setMsgType(cursor.getInt(cursor
							.getColumnIndex(CONTENT_MSG_TYPE)));
					content.setLayout(cursor.getInt(cursor
							.getColumnIndex(CONTENT_LAYOUT)));
					String stamp = cursor.getString(cursor.getColumnIndex(CONTENT_CREATETIME));
					String time = TimeUtils.stampToDate(stamp);
					content.setCreatetime(time);
					content.setContent(cursor.getString(cursor
							.getColumnIndex(CONTENT_CONTENT)));
					content.setFilterID(cursor.getInt(cursor
							.getColumnIndex(CONTENT_FILTERID)));
					content.setInviteCode(cursor.getString(cursor
							.getColumnIndex(CONTENT_INVITECODE)));
					content.setUtcTime(cursor.getString(cursor
							.getColumnIndex(CONTENT_UTCTIME)));
					content.setCorpId(cursor.getString(cursor
							.getColumnIndex(CONTENT_CORPID)));
					content.setCorpName(cursor.getString(cursor
							.getColumnIndex(CONTENT_CORPNAME)));
					content.setGroupId(cursor.getString(cursor
							.getColumnIndex(CONTENT_GROUPID)));
					content.setGroupName(cursor.getString(cursor
							.getColumnIndex(CONTENT_GROUPNAME)));
					content.setLockcorpId(cursor.getString(cursor
							.getColumnIndex(CONTENT_LOCKCORPID)));
					content.setTaskId(cursor.getString(cursor
							.getColumnIndex(CONTENT_TASKID)));
					content.setDeliveryPoints(cursor.getString(cursor
							.getColumnIndex(CONTENT_POINTS)));
					content.setDeliveryVehicle(cursor.getString(cursor
							.getColumnIndex(CONTENT_VEHICLE)));
					content.setKCode(cursor.getString(cursor
							.getColumnIndex(CONTENT_KCODE)));
					content.setPoiName(cursor.getString(cursor
							.getColumnIndex(CONTENT_POINAME)));
					content.setPoiAddress(cursor.getString(cursor
							.getColumnIndex(CONTENT_POIADDRESS)));
					content.setBrand(cursor.getString(cursor
							.getColumnIndex(CONTENT_BRAND)));
					content.setAlarmId(cursor.getString(cursor
							.getColumnIndex(CONTENT_ALARMID)));
					content.setAlarmTime(cursor.getString(cursor
							.getColumnIndex(CONTENT_ALARMTIME)));
					content.setAlarmX(cursor.getString(cursor
							.getColumnIndex(CONTENT_ALARMX)));
					content.setAlarmY(cursor.getString(cursor
							.getColumnIndex(CONTENT_ALARMY)));
					content.setAlarmName(cursor.getString(cursor
							.getColumnIndex(CONTENT_ALARMNAME)));
					content.setAlarmType(cursor.getInt(cursor
							.getColumnIndex(CONTENT_ALARM_TYPE)));
					contentList.add(content);
				}
			}

		}finally {
			if (cursor != null) {
				cursor.close();
				//db.close();
				cursor = null;
			}
		}

		return contentList;
	}

	public  synchronized void delete(long msgid) {
		if (DbManager.mDbHelper == null)
			return;

		String where = null;
		where = MSG_MSGID + "=" + msgid;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(MSGCONTENT_TABLE, where, null);
	}

	public  synchronized void delete() {
		if (DbManager.mDbHelper == null)
			return;

		String where = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(MSGCONTENT_TABLE, where, null);
	}
}
