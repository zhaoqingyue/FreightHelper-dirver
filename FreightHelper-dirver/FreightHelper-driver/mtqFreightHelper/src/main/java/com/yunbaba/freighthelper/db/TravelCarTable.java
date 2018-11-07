/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: CarInfoTable.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.db
 * @Description: 驾驶车辆信息Table
 * @author: zhaoqy
 * @date: 2017年4月18日 上午11:53:59
 * @version: V1.0
 */

package com.yunbaba.freighthelper.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.freighthelper.bean.car.CarInfo;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;

import java.util.ArrayList;
import java.util.List;

public class TravelCarTable {

	public static final String CARINFO_TABLE = "car_info_table";
	public static final Uri CONTENT_SORT_URI = Uri.parse("content://"
			+ DbManager.AUTHORITY + "/" + CARINFO_TABLE);

	public static final String ID = "_id";
	public static final String LOGINNAME = "loginname";
	public static final String TASKID = "taskId";
	public static final String CORPID = "corpId";
	public static final String CAR_LICENSE = "car_license";
	public static final String CAR_DUID = "car_duid";
	public static final String CAR_MODEL = "car_model";
	public static final String CAR_BRAND = "car_brand";
	public static final String CAR_TYPE = "car_type";
	public static final String CAR_DENAME = "car_dename";
	public static final String CAR_MCUID = "car_mcuid";
	public static final String CAR_ENDTIME = "car_sim_endtime";

	private static TravelCarTable mInstance = null;

	public static TravelCarTable getInstance() {
		if (mInstance == null) {
			synchronized (TravelCarTable.class) {
				if (mInstance == null) {
					mInstance = new TravelCarTable();
				}
			}
		}
		return mInstance;
	}

	public String getCreateSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE  IF NOT EXISTS ");
		sb.append(CARINFO_TABLE);
		sb.append("(");
		sb.append(ID);
		sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
		sb.append(LOGINNAME);
		sb.append(" TEXT,");
		sb.append(TASKID);
		sb.append(" TEXT,");
		sb.append(CORPID);
		sb.append(" TEXT,");
		sb.append(CAR_LICENSE);
		sb.append(" TEXT,");
		sb.append(CAR_DUID);
		sb.append(" TEXT,");
		sb.append(CAR_MODEL);
		sb.append(" TEXT,");
		sb.append(CAR_BRAND);
		sb.append(" TEXT,");
		sb.append(CAR_TYPE);
		sb.append(" TEXT,");
		sb.append(CAR_DENAME);
		sb.append(" TEXT,");
		sb.append(CAR_MCUID);
		sb.append(" TEXT,");
		sb.append(CAR_ENDTIME);
		sb.append(" TEXT");
		sb.append(");");

		return sb.toString();
	}

	public String getUpgradeSql() {
		String string = "DROP TABLE IF EXISTS " + CARINFO_TABLE;
		return string;
	}

	public void insert(CarInfo carinfo) {
		if (DbManager.mDbHelper == null || carinfo == null)
			return;



		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

		if (query(carinfo.taskId, carinfo.corpId) != null) {
			update(carinfo);
		} else {
			String loginname = AccountAPI.getInstance().getLoginName();
			ContentValues values = new ContentValues();
			values.put(LOGINNAME, loginname);
			values.put(TASKID, carinfo.taskId);
			values.put(CORPID, carinfo.corpId);
			values.put(CAR_LICENSE, carinfo.carlicense);
			values.put(CAR_DUID, carinfo.carduid);
			values.put(CAR_MODEL, carinfo.carmodel);
			values.put(CAR_BRAND, carinfo.brand);
			values.put(CAR_TYPE, carinfo.vehicletype);
			values.put(CAR_DENAME, carinfo.devicename);
			values.put(CAR_MCUID, carinfo.mcuid);
			values.put(CAR_ENDTIME, carinfo.sim_endtime);
			db.insert(CARINFO_TABLE, null, values);
		}
	}

	public void insert(final List<CarInfo> carList) {
		if (DbManager.mDbHelper == null)
			return;

		if (carList == null || carList.isEmpty())
			return;

		ThreadPoolTool.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
				qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
				String loginname = AccountAPI.getInstance().getLoginName();
				db.beginTransaction();
				int len = carList.size();
				for (int i = 0; i < len; i++) {
					CarInfo carinfo = carList.get(i);
					if (query(carinfo.taskId, carinfo.corpId) != null) {
						update(carinfo);
					} else {
						ContentValues values = new ContentValues();
						values.put(LOGINNAME, loginname);
						values.put(TASKID, carinfo.taskId);
						values.put(CORPID, carinfo.corpId);
						values.put(CAR_LICENSE, carinfo.carlicense);
						values.put(CAR_DUID, carinfo.carduid);
						values.put(CAR_MODEL, carinfo.carmodel);
						values.put(CAR_BRAND, carinfo.brand);
						values.put(CAR_TYPE, carinfo.vehicletype);
						values.put(CAR_DENAME, carinfo.devicename);
						values.put(CAR_MCUID, carinfo.mcuid);
						values.put(CAR_ENDTIME, carinfo.sim_endtime);
						db.insert(CARINFO_TABLE, null, values);
					}
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			}
		});


	}

	public void update(List<CarInfo> carList) {
		if (DbManager.mDbHelper == null)
			return;

		if (carList == null || carList.isEmpty())
			return;

		int len = carList.size();
		for (int i = 0; i < len; i++) {
			CarInfo carinfo = carList.get(i);
			update(carinfo);
		}
	}

	public void update(CarInfo carinfo) {
		if (DbManager.mDbHelper == null || carinfo == null)
			return;

		String loginname = AccountAPI.getInstance().getLoginName();
		String where = null;
		where = TASKID + "=\"" + carinfo.taskId + "\" " + "AND" + " " + CORPID
				+ "=\"" + carinfo.corpId + "\"" +" AND " + LOGINNAME
				+ "=\"" + loginname + "\"";
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

		ContentValues values = new ContentValues();
		values.put(TASKID, carinfo.taskId);
		values.put(CORPID, carinfo.corpId);
		values.put(CAR_LICENSE, carinfo.carlicense);
		values.put(CAR_DUID, carinfo.carduid);
		values.put(CAR_MODEL, carinfo.carmodel);
		values.put(CAR_BRAND, carinfo.brand);
		values.put(CAR_TYPE, carinfo.vehicletype);
		values.put(CAR_DENAME, carinfo.devicename);
		values.put(CAR_MCUID, carinfo.mcuid);
		values.put(CAR_ENDTIME, carinfo.sim_endtime);
		db.update(CARINFO_TABLE, values, where, null);
	}

	public CarInfo query(String taskId, String corpId) {
		if (DbManager.mDbHelper == null)
			return null;

		if (TextUtils.isEmpty(taskId) || TextUtils.isEmpty(corpId))
			return null;
		String loginname = AccountAPI.getInstance().getLoginName();
		CarInfo carinfo = null;
		Cursor cursor = null;
		String where = null;
		String orderBy = null;
		where = TASKID + "=\"" + taskId + "\" " + "AND" + " " + CORPID + "=\""
				+ corpId + "\""+" AND " + LOGINNAME
				+ "=\"" + loginname + "\"";
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		cursor = db
				.query(CARINFO_TABLE, null, where, null, null, null, orderBy);
		if (cursor != null) {
			cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int len = cursor.getCount();
			for (int i = 0; i < len; i++) {
				cursor.moveToPosition(i);
				carinfo = new CarInfo();
				carinfo.taskId = cursor
						.getString(cursor.getColumnIndex(TASKID));
				carinfo.corpId = cursor
						.getString(cursor.getColumnIndex(CORPID));
				carinfo.carlicense = cursor.getString(cursor
						.getColumnIndex(CAR_LICENSE));
				carinfo.carduid = cursor.getString(cursor
						.getColumnIndex(CAR_DUID));
				carinfo.carmodel = cursor.getString(cursor
						.getColumnIndex(CAR_MODEL));
				carinfo.brand = cursor.getString(cursor
						.getColumnIndex(CAR_BRAND));
				carinfo.vehicletype = cursor.getString(cursor
						.getColumnIndex(CAR_TYPE));
				carinfo.devicename = cursor.getString(cursor
						.getColumnIndex(CAR_DENAME));
				carinfo.mcuid = cursor.getString(cursor
						.getColumnIndex(CAR_MCUID));
				carinfo.sim_endtime = cursor.getString(cursor
						.getColumnIndex(CAR_ENDTIME));
			}
		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return carinfo;
	}

	public List<CarInfo> queryAll() {
		if (DbManager.mDbHelper == null)
			return null;
		String loginname = AccountAPI.getInstance().getLoginName();
		List<CarInfo> carList = new ArrayList<CarInfo>();
		CarInfo carinfo = null;
		Cursor cursor = null;
		String where = null;
		where = LOGINNAME + "=\"" + loginname + "\"";
		String orderBy = null;
		orderBy = ID + " DESC";
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		cursor = db
				.query(CARINFO_TABLE, null, where, null, null, null, orderBy);
		if (cursor != null) {
			cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int len = cursor.getCount();
			for (int i = 0; i < len; i++) {
				cursor.moveToPosition(i);
				carinfo = new CarInfo();
				carinfo.taskId = cursor
						.getString(cursor.getColumnIndex(TASKID));
				carinfo.corpId = cursor
						.getString(cursor.getColumnIndex(CORPID));
				carinfo.carlicense = cursor.getString(cursor
						.getColumnIndex(CAR_LICENSE));
				carinfo.carduid = cursor.getString(cursor
						.getColumnIndex(CAR_DUID));
				carinfo.carmodel = cursor.getString(cursor
						.getColumnIndex(CAR_MODEL));
				carinfo.brand = cursor.getString(cursor
						.getColumnIndex(CAR_BRAND));
				carinfo.vehicletype = cursor.getString(cursor
						.getColumnIndex(CAR_TYPE));
				carinfo.devicename = cursor.getString(cursor
						.getColumnIndex(CAR_DENAME));
				carinfo.mcuid = cursor.getString(cursor
						.getColumnIndex(CAR_MCUID));
				carinfo.sim_endtime = cursor.getString(cursor
						.getColumnIndex(CAR_ENDTIME));
				carList.add(carinfo);
			}
		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return carList;
	}

	public void delete(String carlicense) {
		if (DbManager.mDbHelper == null || TextUtils.isEmpty(carlicense))
			return;
		String loginname = AccountAPI.getInstance().getLoginName();
		String where = null;
		where = CAR_LICENSE + "=\"" + carlicense + " AND " + LOGINNAME + "=\""
				+ loginname +"\"";
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(CARINFO_TABLE, where, null);
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
		db.delete(CARINFO_TABLE, where, null);
	}
}
