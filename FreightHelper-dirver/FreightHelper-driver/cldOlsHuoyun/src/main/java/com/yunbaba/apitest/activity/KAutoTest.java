/*
 * @Title KAccount.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.apitest.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.cld.log.CldLog;
import com.yunbaba.apitest.ui.MyListAdapter;
import com.yunbaba.apitest.ui.MyListItem;
import com.yunbaba.ols.R;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.api.CldKConfigAPI;
import com.yunbaba.ols.api.CldKServiceAPI;
import com.yunbaba.ols.bll.CldKConfig.ConfigRateType;
import com.yunbaba.ols.dal.CldDalKAccount;
import com.yunbaba.ols.sap.bean.CldSapKMParm;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


/**
 * The Class KAccount.
 * 
 * @Description 帐户系统
 * @author Zhouls
 * @date 2014-11-4 下午2:15:30
 */
public class KAutoTest extends Activity {

	/** The m list view. */
	private ListView mListView;

	/** The m button list. */
	private List<MyListItem> mButtonList;

	/**
	 * On create.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kautotest);
		initData();
		initControls();
		bindData();
	}

	/**
	 * Inits the data.
	 * 
	 * @return void
	 * @Description 初始化数据
	 * @author Zhouls
	 * @date 2014-11-4 下午2:17:51
	 */
	public void initData() {
		mListView = (ListView) findViewById(R.id.list_kautotest);
		mButtonList = new ArrayList<MyListItem>();
	}

	/**
	 * Inits the controls.
	 * 
	 * @return void
	 * @Description 初始化控件
	 * @author Zhouls
	 * @date 2014-11-4 下午2:18:00
	 */
	public void initControls() {
		mButtonList.add(new MyListItem("autoTest", new OnClickListener() {
			@Override
			public void onClick(View v) {
				CldKAccountAPI.getInstance().login("13266841424", "123456");
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						
					}
				}, 5000, 15000);
			}
		}));
		mButtonList.add(new MyListItem("test", new OnClickListener() {
			@Override
			public void onClick(View v) {
				CldKAccountAPI.getInstance().login("13266841424", "123456");
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {

						new Thread(new Runnable() {

							@Override
							public void run() {

								List<CldSapKMParm> list = new ArrayList<CldSapKMParm>();
								CldKServiceAPI.getInstance().recMessage(
										CldDalKAccount.getInstance().getDuid(),
										1,
										"6.4",
										list,
										CldDalKAccount.getInstance().getKuid(),
										123,
										112,
										11,
										1,
										CldDalKAccount.getInstance()
												.getSession(), 1);
							}
						}).start();
						CldKAccountAPI.getInstance().login("13266841424",
								"123456");
					}
				}, 5000, 15000);
			}
		}));
		mButtonList.add(new MyListItem("test", new OnClickListener() {
			@Override
			public void onClick(View v) {

				// // 定义NotificationManager
				// String ns = Context.NOTIFICATION_SERVICE;
				// NotificationManager mNotificationManager =
				// (NotificationManager) getSystemService(ns);
				// // 定义通知栏展现的内容信息
				// int icon = R.drawable.app_icon;
				final String tickerText = "我的通知栏标题";
				// long when = System.currentTimeMillis();
				// Notification notification = new Notification(icon,
				// tickerText,
				// when);
				// // 定义下拉通知栏时要展现的内容信息
				// Context context = getApplicationContext();
				// CharSequence contentTitle = "我的通知栏标展开标题";
				// CharSequence contentText = "我的通知栏展开详细内容";
				// Intent notificationIntent = new Intent(KAutoTest.this,
				// IndexMenu.class);
				// PendingIntent contentIntent = PendingIntent.getActivity(
				// KAutoTest.this, 0, notificationIntent, 0);
				// notification.setLatestEventInfo(context, contentTitle,
				// contentText, contentIntent);
				// mNotificationManager.notify(1, notification);
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {

						showNotification(getApplicationContext(), tickerText, 1);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						clearNotification(getApplicationContext(), 1);
					}
				}, 1000, 3000);

			}
		}));
		mButtonList.add(new MyListItem("K云频率", new OnClickListener() {
			@Override
			public void onClick(View v) {
				CldLog.e(
						"ols",
						CldKConfigAPI.getInstance().getSvrRate(
								ConfigRateType.CONFIG_RATE_DESTSYNC)
								+ "");
			}
		}));
	}

	/**
	 * @Description 取消通知
	 * @param context
	 * @return void
	 */
	public static void clearNotification(Context context, int notiId) {
		try {
			NotificationManager manager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(Math.abs(notiId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showNotification(Context context, String tipsContent,
			int notiId) {
		try {
			NotificationManager manager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(R.drawable.app_icon,
					tipsContent, System.currentTimeMillis());
			// 指定内容意图
			Intent intent = new Intent();
			PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
					intent, PendingIntent.FLAG_ONE_SHOT);
			// PendingIntent contentIntent = PendingIntent.getActivity(context,
			// 0, intent,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			notification.contentIntent = contentIntent;
			//notification.setLatestEventInfo(context.getApplicationContext(),
			//		tipsContent, tipsContent, contentIntent);
			notification.flags = Notification.FLAG_NO_CLEAR;
			manager.notify(Math.abs(notiId), notification);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Bind data.
	 * 
	 * @return void
	 * @Description 绑定数据
	 * @author Zhouls
	 * @date 2014-11-4 下午2:21:16
	 */
	public void bindData() {
		mListView.setAdapter(new MyListAdapter(getApplicationContext(),
				mButtonList));
	}
}
