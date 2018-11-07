/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: MsgFilterTable.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.db
 * @Description: 消息筛选Table
 * @author: zhaoqy
 * @date: 2017年4月10日 下午2:50:11
 * @version: V1.0
 */

package com.yunbaba.freighthelper.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.yunbaba.freighthelper.bean.msg.Filter;
import com.yunbaba.freighthelper.utils.MLog;

import java.util.ArrayList;
import java.util.List;

public class MsgFilterTable {
	private static final String TAG = "MsgFilterTable";

	public static final String MSGFILTER_TABLE = "msg_filter_table";
	public static final Uri CONTENT_SORT_URI = Uri.parse("content://"
			+ DbManager.AUTHORITY + "/" + MSGFILTER_TABLE);

	public static final String ID = "_id";
	public static final String FILTER_ID = "filter_id";
	public static final String FILTER_TYPE = "filter_type";
	public static final String FILTER_SELECT = "filter_select";
	public static final String FILTER_OPEN = "filter_open";
	public static final String FILTER_POSITION = "filter_position";
	public static final String FILTER_TITLE = "filter_title";
	public static final String FILTER_CONTENT = "filter_content";

	private static MsgFilterTable mInstance = null;

	public static MsgFilterTable getInstance() {
		if (mInstance == null) {
			synchronized (MsgFilterTable.class) {
				if (mInstance == null) {
					mInstance = new MsgFilterTable();
				}
			}
		}
		return mInstance;
	}

	public String getCreateSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE  IF NOT EXISTS ");
		sb.append(MSGFILTER_TABLE);
		sb.append("(");
		sb.append(ID);
		sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
		sb.append(FILTER_ID);
		sb.append(" Integer,");
		sb.append(FILTER_TYPE);
		sb.append(" Integer,");
		sb.append(FILTER_SELECT);
		sb.append(" Integer,");
		sb.append(FILTER_OPEN);
		sb.append(" Integer,");
		sb.append(FILTER_POSITION);
		sb.append(" Integer,");
		sb.append(FILTER_TITLE);
		sb.append(" TEXT,");
		sb.append(FILTER_CONTENT);
		sb.append(" TEXT");
		sb.append(");");

		return sb.toString();
	}

	public String getUpgradeSql() {
		String string = "DROP TABLE IF EXISTS " + MSGFILTER_TABLE;
		return string;
	}

	public void insert(Filter filter) {
		if (DbManager.mDbHelper == null)
			return;

		if (filter == null)
			return;

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

		if (query(filter.getId(), filter.getType()) != null) {
			update(filter, filter.getType());
		} else {
			ContentValues values = new ContentValues();
			values.put(FILTER_ID, filter.getId());
			values.put(FILTER_TYPE, filter.getType());
			values.put(FILTER_SELECT, filter.getSelect());
			values.put(FILTER_OPEN, filter.getOpen());
			values.put(FILTER_POSITION, filter.getPosition());
			values.put(FILTER_TITLE, filter.getTitle());
			values.put(FILTER_CONTENT, filter.getContent());
			db.insert(MSGFILTER_TABLE, null, values);
		}
	}

	public void insert(List<Filter> filterList) {
		if (DbManager.mDbHelper == null)
			return;

		if (filterList == null || filterList.isEmpty())
			return;

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

		db.beginTransaction();
		int len = filterList.size();
		for (int i = 0; i < len; i++) {
			Filter filter = filterList.get(i);
			if (query(filter.getId(), filter.getType()) != null) {
				update(filter, filter.getType());
			} else {
				ContentValues values = new ContentValues();
				values.put(FILTER_ID, filter.getId());
				values.put(FILTER_TYPE, filter.getType());
				values.put(FILTER_SELECT, filter.getSelect());
				values.put(FILTER_OPEN, filter.getOpen());
				values.put(FILTER_POSITION, filter.getPosition());
				values.put(FILTER_TITLE, filter.getTitle());
				values.put(FILTER_CONTENT, filter.getContent());
				db.insert(MSGFILTER_TABLE, null, values);
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void update(List<Filter> filterList, int type) {
		if (DbManager.mDbHelper == null)
			return;

		if (filterList == null || filterList.isEmpty())
			return;
		
		int len = filterList.size();
		MLog.e(TAG, "update len: " + len);
		for (int i = 0; i < len; i++) {
			Filter filter = filterList.get(i);
			update(filter, type);
		}
	}

	public void update(Filter filter, int type) {
		if (DbManager.mDbHelper == null || filter == null)
			return;

		MLog.e(TAG, "update " + filter.getId());
		String where = null;
		where = FILTER_ID + "=" + filter.getId() + " AND " + FILTER_TYPE + "=" + type;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

		ContentValues values = new ContentValues();
		values.put(FILTER_TYPE, filter.getType());
		values.put(FILTER_SELECT, filter.getSelect());
		values.put(FILTER_OPEN, filter.getOpen());
		db.update(MSGFILTER_TABLE, values, where, null);
	}
	
	public Filter query(int id, int type) {
		if (DbManager.mDbHelper == null)
			return null;

		Filter filter = null;
		Cursor cursor = null;
		String where = null;
		String orderBy = null;
		where = FILTER_ID + "=" + id + " AND " + FILTER_TYPE + "=" + type;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		cursor = db.query(MSGFILTER_TABLE, null, where, null, null, null,
				orderBy);
		if (cursor != null) {
			cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int len = cursor.getCount();
			for (int i = 0; i < len; i++) {
				cursor.moveToPosition(i);
				filter = new Filter();
				filter.setId(cursor.getInt(cursor.
						getColumnIndex(FILTER_ID)));
				filter.setType(cursor.getInt(cursor.
						getColumnIndex(FILTER_TYPE)));
				filter.setSelect(cursor.getInt(cursor
						.getColumnIndex(FILTER_SELECT)));
				filter.setOpen(cursor.getInt(
						cursor.getColumnIndex(FILTER_OPEN)));
				filter.setPosition(cursor.getInt(
						cursor.getColumnIndex(FILTER_POSITION)));
				filter.setTitle(cursor.getString(cursor
						.getColumnIndex(FILTER_TITLE)));
				filter.setContent(cursor.getString(cursor
						.getColumnIndex(FILTER_CONTENT)));
			}
		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return filter;
	}

	public List<Filter> queryByType(int type) {
		if (DbManager.mDbHelper == null)
			return null;

		List<Filter> filterList = new ArrayList<Filter>();
		Filter filter = null;
		Cursor cursor = null;
		String where = null;
		where = FILTER_TYPE + "=" + type;
		String orderBy = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		cursor = db.query(MSGFILTER_TABLE, null, where, null, null, null,
				orderBy);
		if (cursor != null) {
			cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
					CONTENT_SORT_URI);
			int len = cursor.getCount();
			for (int i = 0; i < len; i++) {
				cursor.moveToPosition(i);
				filter = new Filter();
				filter.setId(cursor.getInt(cursor.
						getColumnIndex(FILTER_ID)));
				filter.setType(cursor.getInt(cursor.
						getColumnIndex(FILTER_TYPE)));
				filter.setSelect(cursor.getInt(cursor
						.getColumnIndex(FILTER_SELECT)));
				filter.setOpen(cursor.getInt(cursor.
						getColumnIndex(FILTER_OPEN)));
				filter.setPosition(cursor.getInt(
						cursor.getColumnIndex(FILTER_POSITION)));
				filter.setTitle(cursor.getString(cursor
						.getColumnIndex(FILTER_TITLE)));
				filter.setContent(cursor.getString(cursor
						.getColumnIndex(FILTER_CONTENT)));
				filterList.add(filter);
			}
		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return filterList;
	}

	public void delete(long id) {
		if (DbManager.mDbHelper == null)
			return;

		String where = null;
		where = FILTER_ID + "=" + id;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(MSGFILTER_TABLE, where, null);
	}
	
	public void deleteByType(int type) {
		if (DbManager.mDbHelper == null)
			return;

		String where = null;
		where = FILTER_TYPE + "=" + type;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(MSGFILTER_TABLE, where, null);
	}

	public void delete() {
		if (DbManager.mDbHelper == null)
			return;

		String where = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
		db.delete(MSGFILTER_TABLE, where, null);
	}
}
