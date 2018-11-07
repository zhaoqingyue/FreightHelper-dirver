package com.yunbaba.freighthelper.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.TravelDetailEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

public class WaitingProgressTool {

	private static WaitingDialog dialog;
	private static Timer timer = new Timer();
	static TimerTask task = null;

	public static synchronized void showProgress(Context context) {

		if (context == null)
			return;

		if (dialog != null && dialog.isShowing())
			return;

		
		
		
		try {
			
			Activity ss = (Activity)context;
			
			if (ss.isFinishing()) {
				// show dialog
				
				return;
			}
		} catch (Exception e) {

		}

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_waiting, null);// 得到加载view

		if (dialog != null) {
			if (dialog.getContext() == context) {
				// dialog.setMessage("Please wait while loading...");
				// dialog.setIndeterminate(true);
				// Drawable dw = LoadingLayout(R.anim.)
				// dialog.set
				// dialog.setIndeterminateDrawable(d)
			} else {
				dialog = new WaitingDialog(context, R.style.Translucent_NoTitle, context.getClass().getName());

				dialog.setContentView(v, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT));

			}
		} else {
			dialog = new WaitingDialog(context, R.style.Translucent_NoTitle, context.getClass().getName());
			dialog.setContentView(v, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT));

		}

		// dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION));
		dialog.setCancelable(false);

		dialog.show();
		timer = new Timer();
		task = new MyTimerTask();
		timer.schedule(task, 17000);
	}

	public static synchronized void closeshowProgress() {

		if (dialog != null)
			dialog.cancel();
		// timer.
		if (timer != null) {
			if (task != null) {
				task.cancel();
				timer.cancel();
			}
		}
		MLog.e("Timer", "timer cancel");

	}

	public static class MyTimerTask extends TimerTask {
		@Override
		public synchronized void run() {

			closeshowProgress();
			EventBus.getDefault().post(new TravelDetailEvent(MessageId.MSGID_REQUEST_TIMEOUT, 0));

		}
	}

	public static void closeshowProgress(Context context) {


		if (dialog == null)
			return;

		if (dialog.getActivityName() == null)
			return;

		if (dialog != null && context != null && context.getClass().getName().equals(dialog.getActivityName())) {

		} else
			return;

		// MLog.e("judgeisthecontextsame", context.getClass().getName()+"
		// "+dialog.getActivityName());

		if (dialog != null)
			dialog.cancel();
		// timer.
		if (timer != null) {
			if (task != null) {
				task.cancel();
				timer.cancel();
			}
		}
		MLog.e("Timer", "timer cancel");
	}
}
