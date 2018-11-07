/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: TravelTaskTable.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.db
 * @Description: 行程日期Table
 * @author: zhaoqy
 * @date: 2017年4月17日 上午11:13:45
 * @version: V1.0
 */

package com.yunbaba.freighthelper.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.trunk.listner.OnBooleanListner;
import com.yunbaba.freighthelper.bean.CustomDate;
import com.yunbaba.freighthelper.bean.car.TravelTask;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.freighthelper.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class TravelTaskTable {

	public static final String TAG = "TravelTaskTable";
	public static final String TRAVEL_TASK_TABLE = "travel_task_table";
	public static final Uri CONTENT_SORT_URI = Uri.parse("content://"
			+ DbManager.AUTHORITY + "/" + TRAVEL_TASK_TABLE);

	public static final String ID = "_id";
	public static final String TRAVEL_TASKID = "travel_taskid";
	public static final String TRAVEL_CORPID = "travel_corpid";
	public static final String TRAVEL_STARTTIME = "travel_starttime";
	public static final String TRAVEL_FINISHTIME = "travel_finishtime";
	public static final String TRAVEL_DATE = "travel_date";
	public static final String LOGINNAME = "loginname";
	
	private static TravelTaskTable mInstance = null;

	public static TravelTaskTable getInstance() {
		if (mInstance == null) {
			synchronized (TravelTaskTable.class) {
				if (mInstance == null) {
					mInstance = new TravelTaskTable();
				}
			}
		}
		return mInstance;
	}

	public String getCreateSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE  IF NOT EXISTS ");
		sb.append(TRAVEL_TASK_TABLE);
		sb.append("(");
		sb.append(ID);
		sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
		sb.append(LOGINNAME);
		sb.append(" TEXT,");
		sb.append(TRAVEL_TASKID);
		sb.append(" TEXT,");
		sb.append(TRAVEL_CORPID);
		sb.append(" TEXT,");
		sb.append(TRAVEL_STARTTIME);
		sb.append(" TEXT,");
		sb.append(TRAVEL_FINISHTIME);
		sb.append(" TEXT,");
		sb.append(TRAVEL_DATE);
		sb.append(" TEXT");
		sb.append(");");

		return sb.toString();
	}

	public String getUpgradeSql() {
		String string = "DROP TABLE IF EXISTS " + TRAVEL_TASK_TABLE;
		return string;
	}

	public void insert(TravelTask travel) {
		if (DbManager.mDbHelper == null || travel == null)
			return;
		String loginname = AccountAPI.getInstance().getLoginName();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		
		delete();
		ContentValues values = new ContentValues();
		values.put(LOGINNAME, loginname);
		values.put(TRAVEL_TASKID, travel.taskid);
		values.put(TRAVEL_CORPID, travel.corpid);
		values.put(TRAVEL_STARTTIME, travel.starttime);
		values.put(TRAVEL_FINISHTIME, travel.finishtime);
		values.put(TRAVEL_DATE, travel.date);
		db.insert(TRAVEL_TASK_TABLE, null, values);
	}

	public void insert(final List<TravelTask> travelList,final OnBooleanListner lis) {
		if (DbManager.mDbHelper == null) {
			lis.onResult(false);
			return;
		}
		if (travelList == null || travelList.isEmpty()) {
			lis.onResult(false);
			return;
		}
		ThreadPoolTool.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				String loginname = AccountAPI.getInstance().getLoginName();
				MLog.e(TAG, "insert len: " +  travelList.size());
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
				qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

				delete();
				db.beginTransaction();
				int len = travelList.size();
				for (int i = 0; i < len; i++) {
					TravelTask travel = travelList.get(i);
					ContentValues values = new ContentValues();
					values.put(LOGINNAME, loginname);
					values.put(TRAVEL_TASKID, travel.taskid);
					values.put(TRAVEL_CORPID, travel.corpid);
					values.put(TRAVEL_STARTTIME, travel.starttime);
					values.put(TRAVEL_FINISHTIME, travel.finishtime);
					values.put(TRAVEL_DATE, travel.date);
					db.insert(TRAVEL_TASK_TABLE, null, values);

					//MLog.e(TAG, "insert taskid: " +  travel.taskid);
					//MLog.e(TAG, "insert corpid: " +  travel.corpid);
					//MLog.e(TAG, "insert starttime: " +  travel.starttime);
					//MLog.e(TAG, "insert finishtime: " +  travel.finishtime);
					//MLog.e(TAG, "insert date: " +  travel.date);
					//MLog.e(TAG, " ++++++++++++++ insert end +++++++++++++++++++");
				}
				db.setTransactionSuccessful();
				db.endTransaction();

				lis.onResult(true);
			}
		});


	}

	public TravelTask query(String taskId, String corpId) {
		if (DbManager.mDbHelper == null)
			return null;

		if (TextUtils.isEmpty(taskId) || TextUtils.isEmpty(corpId))
			return null;

		String loginname = AccountAPI.getInstance().getLoginName();
		TravelTask travel = null;
		Cursor cursor = null;
		String where = null;
		String orderBy = null;
		where = TRAVEL_TASKID + "=\"" + taskId + "\" " + "AND" + " "
				+ TRAVEL_CORPID + "=\"" + corpId + "\""+" AND " + LOGINNAME
				+ "=\"" + loginname + "\"";
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		cursor = db.query(TRAVEL_TASK_TABLE, null, where, null, null, null,
				orderBy);
		if (cursor != null) {
			cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int len = cursor.getCount();
			for (int i = 0; i < len; i++) {
				cursor.moveToPosition(i);
				travel = new TravelTask();
				travel.taskid = cursor.getString(cursor
						.getColumnIndex(TRAVEL_TASKID));
				travel.corpid = cursor.getString(cursor
						.getColumnIndex(TRAVEL_CORPID));
				travel.starttime = cursor.getString(cursor
						.getColumnIndex(TRAVEL_STARTTIME));
				travel.finishtime = cursor.getString(cursor
						.getColumnIndex(TRAVEL_FINISHTIME));
				travel.date = cursor.getString(cursor
						.getColumnIndex(TRAVEL_DATE));
			}
		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return travel;
	}

	@SuppressLint("UseValueOf") 
	public boolean isTravelTask(CustomDate date) {
		if (DbManager.mDbHelper == null || date == null)
			return false;

		//MLog.i(TAG, "++++++ isTravelTask start +++++ ");
		String loginname = AccountAPI.getInstance().getLoginName();
		String crudate = TimeUtils.dateFromString(date);
		//MLog.e(TAG, "isTravelTask crudate: " +  crudate);
		TravelTask travel = null;
		boolean isTravel = false;
		Cursor cursor = null;
		String where = null;
		String orderBy = null;
		where = TRAVEL_DATE + "=\"" + crudate + "\""+" AND " + LOGINNAME
				+ "=\"" + loginname + "\"";

		/**
		 * add 2017-4-22
		 */
		long cur = System.currentTimeMillis();
		long border_start = cur - TimeUtils.MILLIS_IN_DAY * 31;
		long border_endtime = cur;
		//MLog.e(TAG, "isTravelTask border_start: " +  border_start);
		//MLog.e(TAG, "isTravelTask border_endtime: " +  border_endtime);
		
		long starttime = 0;
		long endtime = 0;
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		cursor = db.query(TRAVEL_TASK_TABLE, null, where, null, null, null,
				orderBy);
		if (cursor != null) {
			cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int len = cursor.getCount();
			//MLog.e(TAG, "isTravelTask len: " +  len);
			for (int i = 0; i < len; i++) {
				cursor.moveToPosition(i);
				travel = new TravelTask();
				travel.taskid = cursor.getString(cursor
						.getColumnIndex(TRAVEL_TASKID));
				travel.corpid = cursor.getString(cursor
						.getColumnIndex(TRAVEL_CORPID));
				travel.starttime = cursor.getString(cursor
						.getColumnIndex(TRAVEL_STARTTIME));
				travel.finishtime = cursor.getString(cursor
						.getColumnIndex(TRAVEL_FINISHTIME));
				travel.date = cursor.getString(cursor
						.getColumnIndex(TRAVEL_DATE));
				/**
				 * 判断是否在一个月内
				 */
				starttime = TimeUtils.getLong(travel.starttime);
				endtime = TimeUtils.getLong(travel.finishtime);
				//MLog.e(TAG, "isTravelTask starttime: " +  starttime);
				//MLog.e(TAG, "isTravelTask endtime: " +  endtime);
				
				if (starttime > border_start && endtime < border_endtime) {
					isTravel = true;
				}
			}
		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		
		//MLog.i(TAG, "++++++ isTravelTask end +++++ ");
		return isTravel;
	}

	public List<TravelTask> query(CustomDate date) {
		if (DbManager.mDbHelper == null || date == null)
			return null;
		String loginname = AccountAPI.getInstance().getLoginName();
		List<TravelTask> travelList = new ArrayList<TravelTask>();
		String crudate = TimeUtils.dateFromString(date);
		TravelTask travel = null;
		Cursor cursor = null;
		String where = null;
		String orderBy = null;
		where = TRAVEL_DATE + "=\"" + crudate + "\"" +" AND " + LOGINNAME
				+ "=\"" + loginname + "\"";
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		cursor = db.query(TRAVEL_TASK_TABLE, null, where, null, null, null,
				orderBy);
		if (cursor != null) {
			cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int len = cursor.getCount();
			for (int i = 0; i < len; i++) {
				cursor.moveToPosition(i);
				travel = new TravelTask();
				travel.taskid = cursor.getString(cursor
						.getColumnIndex(TRAVEL_TASKID));
				travel.corpid = cursor.getString(cursor
						.getColumnIndex(TRAVEL_CORPID));
				travel.starttime = cursor.getString(cursor
						.getColumnIndex(TRAVEL_STARTTIME));
				travel.finishtime = cursor.getString(cursor
						.getColumnIndex(TRAVEL_FINISHTIME));
				travel.date = cursor.getString(cursor
						.getColumnIndex(TRAVEL_DATE));
				travelList.add(travel);
			}
		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return travelList;
	}

	public List<TravelTask> query() {
		if (DbManager.mDbHelper == null)
			return null;
		String loginname = AccountAPI.getInstance().getLoginName();
		List<TravelTask> travelList = new ArrayList<TravelTask>();
		TravelTask travel = null;
		Cursor cursor = null;
		String where = null;
		where = LOGINNAME + "=\"" + loginname + "\"";
		String orderBy = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		cursor = db.query(TRAVEL_TASK_TABLE, null, where, null, null, null,
				orderBy);
		if (cursor != null) {
			cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int len = cursor.getCount();
			for (int i = 0; i < len; i++) {
				cursor.moveToPosition(i);
				travel = new TravelTask();
				travel.taskid = cursor.getString(cursor
						.getColumnIndex(TRAVEL_TASKID));
				travel.corpid = cursor.getString(cursor
						.getColumnIndex(TRAVEL_CORPID));
				travel.starttime = cursor.getString(cursor
						.getColumnIndex(TRAVEL_STARTTIME));
				travel.finishtime = cursor.getString(cursor
						.getColumnIndex(TRAVEL_FINISHTIME));
				travel.date = cursor.getString(cursor
						.getColumnIndex(TRAVEL_DATE));
				travelList.add(travel);
			}
		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return travelList;
	}
	
	public void delete(String taskId, String corpId) {
		if (DbManager.mDbHelper == null)
			return;
		
		if (TextUtils.isEmpty(taskId) || TextUtils.isEmpty(corpId))
			return;


		String loginname = AccountAPI.getInstance().getLoginName();
		String where = null;
		where = TRAVEL_TASKID + "=\"" + taskId + "\" " + "AND" + " "
				+ TRAVEL_CORPID + "=\"" + corpId + "\""+" AND " + LOGINNAME
				+ "=\"" + loginname + "\"";
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(TRAVEL_TASK_TABLE, where, null);
	}

	public void delete() {
		if (DbManager.mDbHelper == null)
			return;
		String loginname = AccountAPI.getInstance().getLoginName();
		String where = null;
		where = LOGINNAME + "=\"" + loginname + "\"";
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(TRAVEL_TASK_TABLE, where, null);
	}

}
