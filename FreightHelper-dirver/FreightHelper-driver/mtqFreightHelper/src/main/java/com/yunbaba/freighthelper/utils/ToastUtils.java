package com.yunbaba.freighthelper.utils;

import com.yunbaba.freighthelper.R;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {
	private static Handler handler = new Handler(Looper.getMainLooper());
	private static Toast toast = null;
	private static Object synObj = new Object();

	/**
	 * Toast发送消息，默认Toast.LENGTH_SHORT
	 * 
	 * @author WikerYong Email:<a href="#">yw_312@foxmail.com</a>
	 * @version 2012-5-22 上午11:13:10
	 * @param act
	 * @param msg
	 */
	public static void showMessage(final Context act, final String msg) {
		showMessage(act, msg, Toast.LENGTH_SHORT);
	}

	/**
	 * Toast发送消息，默认Toast.LENGTH_LONG
	 * 
	 * @author WikerYong Email:<a href="#">yw_312@foxmail.com</a>
	 * @version 2012-5-22 上午11:13:10
	 * @param act
	 * @param msg
	 */
	public static void showMessageLong(final Context act, final String msg) {
		showMessage(act, msg, Toast.LENGTH_LONG);
	}

	// /**
	// * Toast发送消息，默认Toast.LENGTH_SHORT
	// *
	// * @author WikerYong Email:<a href="#">yw_312@foxmail.com</a>
	// * @version 2012-5-22 上午11:13:35
	// * @param act
	// * @param msg
	// */
	// public static void showMessage(final Context act, final int msg) {
	// showMessage(act, msg, Toast.LENGTH_SHORT);
	// }
	//
	// /**
	// * Toast发送消息，默认Toast.LENGTH_LONG
	// *
	// * @author WikerYong Email:<a href="#">yw_312@foxmail.com</a>
	// * @version 2012-5-22 上午11:13:35
	// * @param act
	// * @param msg
	// */
	// public static void showMessageLong(final Context act, final int msg) {
	// //showMessage(act, msg, Toast.LENGTH_LONG);
	// showMessage(act, msg, Toast.LENGTH_LONG);
	// }

	private static long oneTime = 0;
	private static long twoTime = 0;
	private static String oldMsg;

	public static synchronized void showMessage(final Context act, final String msg, final int len) {

		handler.post(new Runnable() {

			@Override
			public void run() {
				synchronized (synObj) {
					if (toast != null) {
						twoTime = System.currentTimeMillis();
						// toast.cancel();
						// toast.setText(msg);
						// toast.setDuration(len);

						if (msg.equals(oldMsg)) {
							if (twoTime - oneTime > len) {
								toast.show();
							}
						} else {
							oldMsg = msg;
							toast.setText(msg);
							toast.show();
						}

					} else {

						oneTime = System.currentTimeMillis();
						toast = Toast.makeText(act.getApplicationContext(), msg, len);
						toast.show();

					}

					oneTime = twoTime;

				}
			}
		});

	}

	public static synchronized void showMessageForScan(final Context act, final String msg, final int len) {

		handler.post(new Runnable() {

			@Override
			public void run() {
				synchronized (synObj) {
					if (toast != null) {
						twoTime = System.currentTimeMillis();
						// toast.cancel();
						// toast.setText(msg);
						// toast.setDuration(len);

						if (msg.equals(oldMsg)) {
							if (twoTime - oneTime > len) {
								toast.show();
							}
						} else {
							oldMsg = msg;
							//toast.setText(msg);
							
							
							LayoutInflater inflate = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

							View v = inflate.inflate(R.layout.layout_scan_toast, null);
							TextView tv = (TextView) v.findViewById(R.id.scantoast_text);
							tv.setText(msg);

							toast.setView(v);
							
							toast.show();
						}

					} else {

						oneTime = System.currentTimeMillis();
						toast = Toast.makeText(act.getApplicationContext(), msg, len);

						LayoutInflater inflate = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

						View v = inflate.inflate(R.layout.layout_scan_toast, null);
						TextView tv = (TextView) v.findViewById(R.id.scantoast_text);
						tv.setText(msg);

						toast.setView(v);

						toast.show();

					}

					oneTime = twoTime;

				}
			}
		});

	}

	/**
	 * 关闭当前Toast
	 * 
	 * @author WikerYong Email:<a href="#">yw_312@foxmail.com</a>
	 * @version 2012-5-22 上午11:14:45
	 */
	public static void cancelCurrentToast() {
		if (toast != null) {
			toast.cancel();
		}
	}
}