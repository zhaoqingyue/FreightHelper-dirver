/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: TravelDetailTable.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.db
 * @Description: 行程详情Table
 * @author: zhaoqy
 * @date: 2017年4月19日 下午6:17:50
 * @version: V1.0
 */

package com.yunbaba.freighthelper.db;

import android.net.Uri;

public class TravelDetailTable {

	public static final String TAG = "TravelDetailTable";
	public static final String TRAVEL_DETAIL_TABLE = "travel_detail_table";
	public static final Uri CONTENT_SORT_URI = Uri.parse("content://"
			+ DbManager.AUTHORITY + "/" + TRAVEL_DETAIL_TABLE);

	public static final String ID = "_id";
	public static final String DETAIL_LICENSE = "detail_carlicense";
	public static final String DETAIL_SERIALID = "detail_serialid";
	public static final String DETAIL_CARDUID = "detail_carduid";
	public static final String DETAIL_STARTTIME = "detail_starttime";
	public static final String DETAIL_ENDTIME = "detail_endtime";
	public static final String DETAIL_FUELCON = "detail_fuelcon";
	public static final String DETAIL_IDLEFUELCON = "detail_idlefuelcon";
	public static final String DETAIL_MILEAGE = "detail_mileage";
	public static final String DETAIL_TOPSPEED = "detail_topspeed";
	public static final String DETAIL_TRAVELTIME = "detail_traveltime";
	public static final String DETAIL_WARMEDTIME = "detail_warmedtime";
	public static final String DETAIL_IDLETIME = "detail_idletime";
	public static final String DETAIL_COMFORTSCORE = "detail_comfortscore";
	public static final String DETAIL_HUNDRED_FUEL = "detail_hundred_fuel";
	public static final String DETAIL_AVERAGE_SPEED = "detail_average_speed";

	private static TravelDetailTable mInstance = null;

	public static TravelDetailTable getInstance() {
		if (mInstance == null) {
			synchronized (TravelDetailTable.class) {
				if (mInstance == null) {
					mInstance = new TravelDetailTable();
				}
			}
		}
		return mInstance;
	}

	public String getCreateSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE  IF NOT EXISTS ");
		sb.append(TRAVEL_DETAIL_TABLE);
		sb.append("(");
		sb.append(ID);
		sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
		sb.append(DETAIL_LICENSE);
		sb.append(" TEXT,");
		sb.append(DETAIL_SERIALID);
		sb.append(" TEXT,");
		sb.append(DETAIL_CARDUID);
		sb.append(" TEXT,");
		sb.append(DETAIL_STARTTIME);
		sb.append(" TEXT,");
		sb.append(DETAIL_ENDTIME);
		sb.append(" TEXT,");
		sb.append(DETAIL_FUELCON);
		sb.append(" TEXT,");
		sb.append(DETAIL_IDLEFUELCON);
		sb.append(" TEXT,");
		sb.append(DETAIL_MILEAGE);
		sb.append(" TEXT,");
		sb.append(DETAIL_TOPSPEED);
		sb.append(" TEXT,");
		sb.append(DETAIL_TRAVELTIME);
		sb.append(" TEXT,");
		sb.append(DETAIL_WARMEDTIME);
		sb.append(" TEXT,");
		sb.append(DETAIL_IDLETIME);
		sb.append(" TEXT,");
		sb.append(DETAIL_COMFORTSCORE);
		sb.append(" TEXT,");
		sb.append(DETAIL_HUNDRED_FUEL);
		sb.append(" TEXT,");
		sb.append(DETAIL_AVERAGE_SPEED);
		sb.append(" TEXT");
		sb.append(");");

		return sb.toString();
	}

	public String getUpgradeSql() {
		String string = "DROP TABLE IF EXISTS " + TRAVEL_DETAIL_TABLE;
		return string;
	}

	/*public void insert(TravelDetail detail) {
		if (DbManager.mDbHelper == null || detail == null)
			return;

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

		ContentValues values = new ContentValues();
		values.put(DETAIL_LICENSE, detail.carlicense);
		values.put(DETAIL_SERIALID, detail.serialid);
		values.put(DETAIL_CARDUID, detail.navi.carduid);
		values.put(DETAIL_STARTTIME, detail.navi.starttime);
		values.put(DETAIL_ENDTIME, detail.navi.endtime);
		values.put(DETAIL_FUELCON, detail.navi.fuelcon);
		values.put(DETAIL_IDLEFUELCON, detail.navi.idlefuelcon);
		values.put(DETAIL_MILEAGE, detail.navi.mileage);
		values.put(DETAIL_TOPSPEED, detail.navi.topspeed);
		values.put(DETAIL_TRAVELTIME, detail.navi.traveltime);
		values.put(DETAIL_WARMEDTIME, detail.navi.warmedtime);
		values.put(DETAIL_IDLETIME, detail.navi.idletime);
		values.put(DETAIL_COMFORTSCORE, detail.navi.comfortscore);
		values.put(DETAIL_HUNDRED_FUEL, detail.hundred_fuel);
		values.put(DETAIL_AVERAGE_SPEED, detail.average_speed);
		db.insert(TRAVEL_DETAIL_TABLE, null, values);
	}*/

	/*public TravelDetail query(String serialid, String carduid) {
		if (DbManager.mDbHelper == null)
			return null;

		if (TextUtils.isEmpty(serialid) || TextUtils.isEmpty(carduid))
			return null;

		TravelDetail detail = null;
		Cursor cursor = null;
		String where = null;
		String orderBy = null;
		where = DETAIL_SERIALID + "=\"" + serialid + "\" " + "AND" + " "
				+ DETAIL_CARDUID + "=\"" + carduid + "\"";
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		cursor = db.query(TRAVEL_DETAIL_TABLE, null, where, null, null, null,
				orderBy);
		if (cursor != null) {
			cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int len = cursor.getCount();
			for (int i = 0; i < len; i++) {
				cursor.moveToPosition(i);
				detail = new TravelDetail();
				detail.carlicense = cursor.getString(cursor
						.getColumnIndex(DETAIL_LICENSE));
				detail.serialid = cursor.getString(cursor
						.getColumnIndex(DETAIL_SERIALID));
				detail.navi.carduid = cursor.getString(cursor
						.getColumnIndex(DETAIL_CARDUID));
				detail.navi.starttime = cursor.getString(cursor
						.getColumnIndex(DETAIL_STARTTIME));
				detail.navi.endtime = cursor.getString(cursor
						.getColumnIndex(DETAIL_ENDTIME));
				detail.navi.fuelcon = cursor.getString(cursor
						.getColumnIndex(DETAIL_FUELCON));
				detail.navi.idlefuelcon = cursor.getString(cursor
						.getColumnIndex(DETAIL_IDLEFUELCON));
				detail.navi.mileage = cursor.getString(cursor
						.getColumnIndex(DETAIL_MILEAGE));
				detail.navi.topspeed = cursor.getString(cursor
						.getColumnIndex(DETAIL_TOPSPEED));
				detail.navi.traveltime = cursor.getString(cursor
						.getColumnIndex(DETAIL_TRAVELTIME));
				detail.navi.warmedtime = cursor.getString(cursor
						.getColumnIndex(DETAIL_WARMEDTIME));
				detail.navi.idletime = cursor.getString(cursor
						.getColumnIndex(DETAIL_IDLETIME));
				detail.navi.comfortscore = cursor.getString(cursor
						.getColumnIndex(DETAIL_COMFORTSCORE));
				detail.hundred_fuel = cursor.getString(cursor
						.getColumnIndex(DETAIL_HUNDRED_FUEL));
				detail.average_speed = cursor.getString(cursor
						.getColumnIndex(DETAIL_AVERAGE_SPEED));
			}
		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return detail;
	}*/

}
