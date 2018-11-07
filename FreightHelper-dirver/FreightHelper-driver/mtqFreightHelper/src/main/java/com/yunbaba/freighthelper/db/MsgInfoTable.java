/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: MsgInfoTable.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.db
 * @Description: 消息详情Table
 * @author: zhaoqy
 * @date: 2017年4月10日 下午2:50:37
 * @version: V1.0
 */

package com.yunbaba.freighthelper.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.freighthelper.bean.msg.Filter;
import com.yunbaba.freighthelper.bean.msg.Filter.FilterId;
import com.yunbaba.freighthelper.bean.msg.MsgContent;
import com.yunbaba.freighthelper.bean.msg.MsgInfo;
import com.yunbaba.freighthelper.manager.MsgManager;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.freighthelper.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class MsgInfoTable {

    public static final String TAG = "MsgInfoTable";
    public static final String MSGINFO_TABLE = "msg_info_table";
    public static final Uri CONTENT_SORT_URI = Uri.parse("content://"
            + DbManager.AUTHORITY + "/" + MSGINFO_TABLE);

    public static final String ID = "_id";
    public static final String LOGINNAME = "loginname";
    public static final String MSG_MSGID = "msg_msgid";
    public static final String MSG_TITLE = "msg_title";
    public static final String MSG_MSGTYPE = "msg_msgtype";
    public static final String MSG_CREATEUSER = "msg_createuser";
    public static final String MSG_CREATETIME = "msg_createtime";
    public static final String MSG_CONTENT = "msg_content";
    public static final String MSG_READMARK = "msg_readmark";
    public static final String MSG_BUSINESS = "msg_businesscode";

    private static MsgInfoTable mInstance = null;

    public static MsgInfoTable getInstance() {
        if (mInstance == null) {
            synchronized (MsgInfoTable.class) {
                if (mInstance == null) {
                    mInstance = new MsgInfoTable();
                }
            }
        }
        return mInstance;
    }

    public String getCreateSql() {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE  IF NOT EXISTS ");
        sb.append(MSGINFO_TABLE);
        sb.append("(");
        sb.append(ID);
        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append(LOGINNAME);
        sb.append(" TEXT,");
        sb.append(MSG_MSGID);
        sb.append(" Integer,");
        sb.append(MSG_TITLE);
        sb.append(" TEXT,");
        sb.append(MSG_MSGTYPE);
        sb.append(" Integer,");
        sb.append(MSG_CREATEUSER);
        sb.append(" TEXT,");
        sb.append(MSG_CREATETIME);
        sb.append(" TEXT,");
        sb.append(MSG_CONTENT);
        sb.append(" TEXT,");
        sb.append(MSG_READMARK);
        sb.append(" Integer,");
        sb.append(MSG_BUSINESS);
        sb.append(" TEXT");
        sb.append(");");

        return sb.toString();
    }

    public String getUpgradeSql() {
        String string = "DROP TABLE IF EXISTS " + MSGINFO_TABLE;
        return string;
    }

    public void insert(MsgInfo msginfo) {
        if (DbManager.mDbHelper == null)
            return;

        if (msginfo == null)
            return;

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
        qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

        if (query(msginfo.getMessageId()) != null) {
            delete(msginfo.getMessageId());
        }

        String loginname = AccountAPI.getInstance().getLoginName();
        ContentValues values = new ContentValues();
        values.put(LOGINNAME, loginname);
        values.put(MSG_MSGID, msginfo.getMessageId());
        values.put(MSG_TITLE, msginfo.getTitle());
        values.put(MSG_MSGTYPE, msginfo.getMsgType());
        values.put(MSG_CREATEUSER, msginfo.getCreateuser());
        values.put(MSG_CREATETIME, msginfo.getCreatetime());
        values.put(MSG_CONTENT, msginfo.getContent());
        values.put(MSG_READMARK, msginfo.getReadMark());
        values.put(MSG_BUSINESS, msginfo.getBusinessCode());
        db.insert(MSGINFO_TABLE, null, values);
        MsgContentTable.getInstance().insert(msginfo.getMsgContent());
    }

    public void insert(final ArrayList<MsgInfo> msgList) {
        if (DbManager.mDbHelper == null)
            return;

        if (msgList == null || msgList.isEmpty())
            return;

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
                SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
                qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

                String loginname = AccountAPI.getInstance().getLoginName();
                db.beginTransaction();
                int len = msgList.size();
                for (int i = 0; i < len; i++) {
                    MsgInfo msginfo = msgList.get(i);

                    if (query(msginfo.getMessageId()) != null) {
                        delete(msginfo.getMessageId());
                    }
                    ContentValues values = new ContentValues();
                    values.put(LOGINNAME, loginname);
                    values.put(MSG_MSGID, msginfo.getMessageId());
                    values.put(MSG_TITLE, msginfo.getTitle());
                    values.put(MSG_MSGTYPE, msginfo.getMsgType());
                    values.put(MSG_CREATEUSER, msginfo.getCreateuser());
                    values.put(MSG_CREATETIME, msginfo.getCreatetime());
                    values.put(MSG_CONTENT, msginfo.getContent());
                    values.put(MSG_READMARK, msginfo.getReadMark());
                    values.put(MSG_BUSINESS, msginfo.getBusinessCode());
                    db.insert(MSGINFO_TABLE, null, values);
                    MsgContentTable.getInstance().insert(msginfo.getMsgContent());
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });


    }

    /**
     * 更新本地消息阅读状态
     */
    public void update(final ArrayList<MsgInfo> msgList) {
        if (DbManager.mDbHelper == null)
            return;

        if (msgList == null || msgList.isEmpty())
            return;

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                int len = msgList.size();
                for (int i = 0; i < len; i++) {
                    MsgInfo msg = msgList.get(i);
                    update(msg);
                }
            }
        });

    }

    /**
     * 更新本地消息阅读状态
     */
    public void update(MsgInfo msg) {
        if (DbManager.mDbHelper == null || msg == null)
            return;

        String where = null;
        String loginname = AccountAPI.getInstance().getLoginName();
        where = MSG_MSGID + "=" + msg.getMessageId() + " AND " + LOGINNAME
                + "=\"" + loginname + "\"";


        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
        qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

        ContentValues values = new ContentValues();
        values.put(MSG_READMARK, 3); // 已读
        db.update(MSGINFO_TABLE, values, where, null);
    }

    /**
     * 查询指定msgid
     *
     * @param msgid
     */
    public MsgInfo query(long msgid) {
        if (DbManager.mDbHelper == null)
            return null;

        MsgInfo msginfo = null;
        Cursor cursor = null;
        String where = null;
        String orderBy = null;
        String loginname = AccountAPI.getInstance().getLoginName();
        where = MSG_MSGID + "=" + msgid + " AND " + LOGINNAME + "=\""
                + loginname + "\"";
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
        qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));


        try {


            cursor = db
                    .query(MSGINFO_TABLE, null, where, null, null, null, orderBy);
            if (cursor != null) {
                cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
                        CONTENT_SORT_URI);
                int len = cursor.getCount();
                for (int i = 0; i < len; i++) {
                    cursor.moveToPosition(i);
                    msginfo = new MsgInfo();
                    msginfo.setMessageId(cursor.getLong(cursor
                            .getColumnIndex(MSG_MSGID)));
                    msginfo.setTitle(cursor.getString(cursor
                            .getColumnIndex(MSG_TITLE)));
                    msginfo.setMsgType(cursor.getInt(cursor
                            .getColumnIndex(MSG_MSGTYPE)));
                    msginfo.setCreateuser(cursor.getString(cursor
                            .getColumnIndex(MSG_CREATEUSER)));
                    String stamp = cursor.getString(cursor
                            .getColumnIndex(MSG_CREATETIME));
                    String time = TimeUtils.stampToDate(stamp);
                    msginfo.setCreatetime(time);
                    msginfo.setContent(cursor.getString(cursor
                            .getColumnIndex(MSG_CONTENT)));
                    msginfo.setReadMark(cursor.getInt(cursor
                            .getColumnIndex(MSG_READMARK)));
                    msginfo.setBusinessCode(cursor.getString(cursor
                            .getColumnIndex(MSG_BUSINESS)));
                    msginfo.setMsgContent(MsgContentTable.getInstance()
                            .query(msgid));
                }
            }


        }finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return msginfo;
    }

    public ArrayList<MsgInfo> query() {
        if (DbManager.mDbHelper == null)
            return null;

        ArrayList<MsgInfo> msgList = new ArrayList<MsgInfo>();
        MsgInfo msginfo = null;
        Cursor cursor = null;
        String where = null;
        String orderBy = null;
        orderBy = ID + " DESC";
        String loginname = AccountAPI.getInstance().getLoginName();
        where = LOGINNAME + "=\"" + loginname + "\"";
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
        qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));

        try {

            cursor = db
                    .query(MSGINFO_TABLE, null, where, null, null, null, orderBy);
            if (cursor != null) {
                cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
                        CONTENT_SORT_URI);
                int len = cursor.getCount();
                for (int i = 0; i < len; i++) {
                    cursor.moveToPosition(i);
                    msginfo = new MsgInfo();
                    msginfo.setMessageId(cursor.getLong(cursor
                            .getColumnIndex(MSG_MSGID)));
                    msginfo.setTitle(cursor.getString(cursor
                            .getColumnIndex(MSG_TITLE)));
                    msginfo.setMsgType(cursor.getInt(cursor
                            .getColumnIndex(MSG_MSGTYPE)));
                    msginfo.setCreateuser(cursor.getString(cursor
                            .getColumnIndex(MSG_CREATEUSER)));
                    String stamp = cursor.getString(cursor
                            .getColumnIndex(MSG_CREATETIME));
                    String time = TimeUtils.stampToDate(stamp);
                    msginfo.setCreatetime(time);
                    msginfo.setContent(cursor.getString(cursor
                            .getColumnIndex(MSG_CONTENT)));
                    msginfo.setReadMark(cursor.getInt(cursor
                            .getColumnIndex(MSG_READMARK)));
                    msginfo.setBusinessCode(cursor.getString(cursor
                            .getColumnIndex(MSG_BUSINESS)));
                    msginfo.setMsgContent(MsgContentTable.getInstance().query(
                            msginfo.getMessageId()));
                    msgList.add(msginfo);
                }
            }
        }finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return msgList;
    }


    public ArrayList<MsgInfo> query(int type, int offset, int size) {


        //todotodo

        if (DbManager.mDbHelper == null)
            return null;

        ArrayList<MsgInfo> msgList = new ArrayList<MsgInfo>();
        MsgInfo msginfo = null;
        Cursor cursor = null;
        String where = null;
        String orderBy = null;
        orderBy = ID + " DESC";
        String loginname = AccountAPI.getInstance().getLoginName();
        where = LOGINNAME + "=\"" + loginname + "\"";


        where = where + getLimitStr(type);

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
        qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));


        try {


            cursor = db
                    .query(MSGINFO_TABLE, null, where, null, null, null, orderBy, offset + "," + size);  //"0,10");//
            if (cursor != null) {
                cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
                        CONTENT_SORT_URI);
                int len = cursor.getCount();
                for (int i = 0; i < len; i++) {
                    cursor.moveToPosition(i);
                    String businessCode = cursor.getString(cursor
                            .getColumnIndex(MSG_BUSINESS));
                    int business = Integer.parseInt(businessCode);
                    if (isSpecifyType(type, business)) {
                        msginfo = new MsgInfo();
                        msginfo.setMessageId(cursor.getLong(cursor
                                .getColumnIndex(MSG_MSGID)));
                        msginfo.setTitle(cursor.getString(cursor
                                .getColumnIndex(MSG_TITLE)));
                        msginfo.setMsgType(cursor.getInt(cursor
                                .getColumnIndex(MSG_MSGTYPE)));
                        msginfo.setCreateuser(cursor.getString(cursor
                                .getColumnIndex(MSG_CREATEUSER)));
                        String stamp = cursor.getString(cursor
                                .getColumnIndex(MSG_CREATETIME));
                        String time = TimeUtils.stampToDate(stamp);
                        msginfo.setCreatetime(time);
                        msginfo.setContent(cursor.getString(cursor
                                .getColumnIndex(MSG_CONTENT)));
                        msginfo.setReadMark(cursor.getInt(cursor
                                .getColumnIndex(MSG_READMARK)));
                        msginfo.setBusinessCode(cursor.getString(cursor
                                .getColumnIndex(MSG_BUSINESS)));
                        msginfo.setMsgContent(MsgContentTable.getInstance().query(
                                msginfo.getMessageId()));
                        msgList.add(msginfo);
                    }
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return msgList;
    }


//	public ArrayList<MsgInfo> query(int type) {
//		if (DbManager.mDbHelper == null)
//			return null;
//
//		ArrayList<MsgInfo> msgList = new ArrayList<MsgInfo>();
//		MsgInfo msginfo = null;
//		Cursor cursor = null;
//		String where = null;
//		String orderBy = null;
//		orderBy = ID + " DESC";
//		String loginname = AccountAPI.getInstance().getLoginName();
//		where = LOGINNAME + "=\"" + loginname + "\"";
//		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
//		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
//		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
//		cursor = db
//				.query(MSGINFO_TABLE, null, where, null, null, null, orderBy);
//		if (cursor != null) {
//			cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
//					CONTENT_SORT_URI);
//			int len = cursor.getCount();
//			for (int i = 0; i < len; i++) {
//				cursor.moveToPosition(i);
//				String businessCode = cursor.getString(cursor
//						.getColumnIndex(MSG_BUSINESS));
//				int business = Integer.parseInt(businessCode);
//				if (isSpecifyType(type, business)) {
//					msginfo = new MsgInfo();
//					msginfo.setMessageId(cursor.getLong(cursor
//							.getColumnIndex(MSG_MSGID)));
//					msginfo.setTitle(cursor.getString(cursor
//							.getColumnIndex(MSG_TITLE)));
//					msginfo.setMsgType(cursor.getInt(cursor
//							.getColumnIndex(MSG_MSGTYPE)));
//					msginfo.setCreateuser(cursor.getString(cursor
//							.getColumnIndex(MSG_CREATEUSER)));
//					String stamp = cursor.getString(cursor
//							.getColumnIndex(MSG_CREATETIME));
//					String time = TimeUtils.stampToDate(stamp);
//					msginfo.setCreatetime(time);
//					msginfo.setContent(cursor.getString(cursor
//							.getColumnIndex(MSG_CONTENT)));
//					msginfo.setReadMark(cursor.getInt(cursor
//							.getColumnIndex(MSG_READMARK)));
//					msginfo.setBusinessCode(cursor.getString(cursor
//							.getColumnIndex(MSG_BUSINESS)));
//					msginfo.setMsgContent(MsgContentTable.getInstance().query(
//							msginfo.getMessageId()));
//					msgList.add(msginfo);
//				}
//			}
//		}
//
//		if (cursor != null) {
//			cursor.close();
//			cursor = null;
//		}
//		return msgList;
//	}

    private boolean isSpecifyType(int type, int business) {
        boolean specify = false;
        switch (type) {
            case MsgManager.MSG_BUSINESS:
                if (business == 1008 || business == 1010 || business == 1001
                        || business == 1099 || business == 1006 || business == 1004 || business == 2001 || business == 1016 || business == 1012) {
                    specify = true;
                }
                break;
            case MsgManager.MSG_ALARM:
                if (business == 2002) {
                    specify = true;
                }
                break;
            case MsgManager.MSG_ALL:
                specify = true;
                break;
            default:
                break;
        }
        return specify;
    }


    public ArrayList<MsgInfo> queryUnReadMsg(int size) {


        //todotodo
        if (DbManager.mDbHelper == null)
            return null;

        ArrayList<MsgInfo> msgList = new ArrayList<MsgInfo>();
        MsgInfo msginfo = null;
        Cursor cursor = null;
        String where = null;
        String orderBy = null;
        String loginname = AccountAPI.getInstance().getLoginName();
        //MLog.e(TAG, "login: " + loginname);
        where = LOGINNAME + "=\"" + loginname + "\"" + " AND " + MSG_READMARK
                + "!=" + 3;
        // where = MSG_READMARK + "!=" + 3;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
        qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
        cursor = db
                .query(MSGINFO_TABLE, null, where, null, null, null, orderBy, "0," + size);
        if (cursor != null) {
            cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
                    CONTENT_SORT_URI);
            int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                msginfo = new MsgInfo();
                msginfo.setMessageId(cursor.getLong(cursor
                        .getColumnIndex(MSG_MSGID)));
                msginfo.setTitle(cursor.getString(cursor
                        .getColumnIndex(MSG_TITLE)));
                msginfo.setMsgType(cursor.getInt(cursor
                        .getColumnIndex(MSG_MSGTYPE)));
                msginfo.setCreateuser(cursor.getString(cursor
                        .getColumnIndex(MSG_CREATEUSER)));
                String stamp = cursor.getString(cursor
                        .getColumnIndex(MSG_CREATETIME));
                String time = TimeUtils.stampToDate(stamp);
                msginfo.setCreatetime(time);
                msginfo.setContent(cursor.getString(cursor
                        .getColumnIndex(MSG_CONTENT)));
                msginfo.setReadMark(cursor.getInt(cursor
                        .getColumnIndex(MSG_READMARK)));
                msginfo.setBusinessCode(cursor.getString(cursor
                        .getColumnIndex(MSG_BUSINESS)));
                msginfo.setMsgContent(MsgContentTable.getInstance().query(
                        msginfo.getMessageId()));

//				String loginnane = cursor.getString(cursor
//						.getColumnIndex(MSG_CREATEUSER));

                //MLog.e(TAG, "loginnane: " + loginnane);
                //MLog.e(TAG, "msgid: " + msginfo.getMessageId());
                //MLog.e(TAG, "content: " + msginfo.getContent());
                //MLog.e(TAG, "business: " + msginfo.getBusinessCode());
                //MLog.e(TAG, "create: " + msginfo.getCreatetime());
                msgList.add(msginfo);
            }
        }

        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return msgList;
    }


    public ArrayList<MsgInfo> queryUnReadMsg(int type, int size) {

        //todotodo
        if (DbManager.mDbHelper == null)
            return null;

        ArrayList<MsgInfo> msgList = new ArrayList<MsgInfo>();
        MsgInfo msginfo = null;
        Cursor cursor = null;
        String where = null;
        String orderBy = null;
        String loginname = AccountAPI.getInstance().getLoginName();
        where = LOGINNAME + "=\"" + loginname + "\"" + " AND " + MSG_READMARK
                + "!=" + 3;
        // where = MSG_READMARK + "!=" + 3;

        where = where + getLimitStr(type);

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
        qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
        cursor = db
                .query(MSGINFO_TABLE, null, where, null, null, null, orderBy, "0," + size);
        if (cursor != null) {
            cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
                    CONTENT_SORT_URI);
            int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                String businessCode = cursor.getString(cursor
                        .getColumnIndex(MSG_BUSINESS));
                int business = Integer.parseInt(businessCode);
                if (isSpecifyType(type, business)) {
                    msginfo = new MsgInfo();
                    msginfo.setMessageId(cursor.getLong(cursor
                            .getColumnIndex(MSG_MSGID)));
                    msginfo.setTitle(cursor.getString(cursor
                            .getColumnIndex(MSG_TITLE)));
                    msginfo.setMsgType(cursor.getInt(cursor
                            .getColumnIndex(MSG_MSGTYPE)));
                    msginfo.setCreateuser(cursor.getString(cursor
                            .getColumnIndex(MSG_CREATEUSER)));
                    String stamp = cursor.getString(cursor
                            .getColumnIndex(MSG_CREATETIME));
                    String time = TimeUtils.stampToDate(stamp);
                    msginfo.setCreatetime(time);
                    msginfo.setContent(cursor.getString(cursor
                            .getColumnIndex(MSG_CONTENT)));
                    msginfo.setReadMark(cursor.getInt(cursor
                            .getColumnIndex(MSG_READMARK)));
                    msginfo.setBusinessCode(cursor.getString(cursor
                            .getColumnIndex(MSG_BUSINESS)));
                    msginfo.setMsgContent(MsgContentTable.getInstance().query(
                            msginfo.getMessageId()));
                    msgList.add(msginfo);
                }
            }
        }

        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return msgList;
    }


    public String getLimitStr(int type) {
        String str = "";

        if (type == MsgManager.MSG_BUSINESS) {

            str = " AND " + MSG_BUSINESS + " IN " + "(" +
                    "\"1008\"" + "," +
                    "\"1010\"" + "," +
                    "\"1001\"" + "," +
                    "\"1099\"" + "," +
                    "\"1006\"" + "," +
                    "\"1004\"" + "," +
                    "\"2001\"" + "," +
                    "\"1016\"" + "," +
                    "\"1012\"" + ")";

        } else {

            str = " AND " + MSG_BUSINESS + " IN " + "(" +
                    "\"2002\"" + ")";


        }

        return str;
    }


//	public ArrayList<MsgInfo> queryUnReadMsg(int type) {
//		if (DbManager.mDbHelper == null)
//			return null;
//
//		ArrayList<MsgInfo> msgList = new ArrayList<MsgInfo>();
//		MsgInfo msginfo = null;
//		Cursor cursor = null;
//		String where = null;
//		String orderBy = null;
//		String loginname = AccountAPI.getInstance().getLoginName();
//		where = LOGINNAME + "=\"" + loginname + "\"" + " AND " + MSG_READMARK
//				+ "!=" + 3;
//		// where = MSG_READMARK + "!=" + 3;
//		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
//		SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
//		qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
//		cursor = db
//				.query(MSGINFO_TABLE, null, where, null, null, null, orderBy);
//		if (cursor != null) {
//			cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
//					CONTENT_SORT_URI);
//			int len = cursor.getCount();
//			for (int i = 0; i < len; i++) {
//				cursor.moveToPosition(i);
//				String businessCode = cursor.getString(cursor
//						.getColumnIndex(MSG_BUSINESS));
//				int business = Integer.parseInt(businessCode);
//				if (isSpecifyType(type, business)) {
//					msginfo = new MsgInfo();
//					msginfo.setMessageId(cursor.getLong(cursor
//							.getColumnIndex(MSG_MSGID)));
//					msginfo.setTitle(cursor.getString(cursor
//							.getColumnIndex(MSG_TITLE)));
//					msginfo.setMsgType(cursor.getInt(cursor
//							.getColumnIndex(MSG_MSGTYPE)));
//					msginfo.setCreateuser(cursor.getString(cursor
//							.getColumnIndex(MSG_CREATEUSER)));
//					String stamp = cursor.getString(cursor
//							.getColumnIndex(MSG_CREATETIME));
//					String time = TimeUtils.stampToDate(stamp);
//					msginfo.setCreatetime(time);
//					msginfo.setContent(cursor.getString(cursor
//							.getColumnIndex(MSG_CONTENT)));
//					msginfo.setReadMark(cursor.getInt(cursor
//							.getColumnIndex(MSG_READMARK)));
//					msginfo.setBusinessCode(cursor.getString(cursor
//							.getColumnIndex(MSG_BUSINESS)));
//					msginfo.setMsgContent(MsgContentTable.getInstance().query(
//							msginfo.getMessageId()));
//					msgList.add(msginfo);
//				}
//			}
//		}
//
//		if (cursor != null) {
//			cursor.close();
//			cursor = null;
//		}
//		return msgList;
//	}

    public ArrayList<MsgInfo> filterQuery(int type, List<Filter> filters, int offset, int size) {
        if (DbManager.mDbHelper == null)
            return null;

        ArrayList<MsgInfo> msgList = new ArrayList<MsgInfo>();
        MsgInfo msginfo = null;
        Cursor cursor = null;
        String where = null;
        String orderBy = null;
        orderBy = ID + " DESC";
        String loginname = AccountAPI.getInstance().getLoginName();
        where = LOGINNAME + "=\"" + loginname + "\"";


        where = where + getLimitStr(type);


        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
        qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
        cursor = db
                .query(MSGINFO_TABLE, null, where, null, null, null, orderBy, offset + "," + size);
        if (cursor != null) {
            cursor.setNotificationUri(DbManager.mContext.getContentResolver(),
                    CONTENT_SORT_URI);
            int len = cursor.getCount();
            for (int i = 0; i < len; i++) {
                cursor.moveToPosition(i);
                String businessCode = cursor.getString(cursor
                        .getColumnIndex(MSG_BUSINESS));
                int business = Integer.parseInt(businessCode);
                long msgId = cursor.getLong(cursor.getColumnIndex(MSG_MSGID));
                MsgContent content = MsgContentTable.getInstance().query(msgId);
                boolean specify = isSpecifyFilterType(type, business,
                        content.getFilterID(), filters);
                if (specify) {
                    msginfo = new MsgInfo();
                    msginfo.setMessageId(msgId);
                    msginfo.setTitle(cursor.getString(cursor
                            .getColumnIndex(MSG_TITLE)));
                    msginfo.setMsgType(cursor.getInt(cursor
                            .getColumnIndex(MSG_MSGTYPE)));
                    msginfo.setCreateuser(cursor.getString(cursor
                            .getColumnIndex(MSG_CREATEUSER)));
                    String stamp = cursor.getString(cursor
                            .getColumnIndex(MSG_CREATETIME));
                    String time = TimeUtils.stampToDate(stamp);
                    msginfo.setCreatetime(time);
                    msginfo.setContent(cursor.getString(cursor
                            .getColumnIndex(MSG_CONTENT)));
                    msginfo.setReadMark(cursor.getInt(cursor
                            .getColumnIndex(MSG_READMARK)));
                    msginfo.setBusinessCode(businessCode);
                    msginfo.setMsgContent(content);
                    msgList.add(msginfo);
                }
            }
        }

        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return msgList;
    }

    private boolean isSpecifyFilterType(int type, int business, int filterid,
                                        List<Filter> filters) {
        switch (type) {
            case MsgManager.MSG_BUSINESS:
                if (business == 1008 || business == 1010 || business == 1001
                        || business == 1099 || business == 1006 || business == 1004 || business == 2001 || business == 1016 || business == 1012) {
                    int len = filters.size();
                    if (len > 0) {
                        for (int i = 0; i < len; i++) {
                            int id = filters.get(i).getId();
                            if (id == FilterId.FILTER_ID_10) {
                                if (filterid == FilterId.FILTER_ID_10
                                        || filterid == FilterId.FILTER_ID_11) {
                                    return true;
                                }
                            } else if (id == FilterId.FILTER_ID_12) {
                                if (filterid == FilterId.FILTER_ID_12
                                        || filterid == FilterId.FILTER_ID_13 || filterid == FilterId.FILTER_ID_1311) {
                                    return true;
                                }
                            } else {
                                if (filterid == id) {
                                    return true;
                                }
                            }
                        }
                    } else {
                        return true;
                    }
                }
                break;
            case MsgManager.MSG_ALARM:
                if (business == 2002) {
                    int len = filters.size();
                    if (len > 0) {
                        for (int i = 0; i < len; i++) {
                            if (filterid == filters.get(i).getId()) {
                                return true;
                            }
                        }
                    } else {
                        return true;
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    public void delete(long msgid) {
        if (DbManager.mDbHelper == null)
            return;

        String where = null;
        String loginname = AccountAPI.getInstance().getLoginName();
        where = MSG_MSGID + "=" + msgid + " AND " + LOGINNAME + "=\""
                + loginname + "\"";
        //where = MSG_MSGID + "=" + msgid;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
        qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
        db.delete(MSGINFO_TABLE, where, null);
        MsgContentTable.getInstance().delete(msgid);
    }

    public void delete() {
        if (DbManager.mDbHelper == null)
            return;

        String where = null;
        String loginname = AccountAPI.getInstance().getLoginName();
        where = LOGINNAME + "=\"" + loginname + "\"";
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = DbManager.mDbHelper.getReadableDatabase();
        qb.setTables(CONTENT_SORT_URI.getPathSegments().get(0));
        db.delete(MSGINFO_TABLE, where, null);
        MsgContentTable.getInstance().delete();
    }
}
