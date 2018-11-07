package com.yunbaba.freighthelper.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.yunbaba.freighthelper.MainActivity;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.manager.NotifyManager;

import java.util.Timer;
import java.util.TimerTask;

public class WaitingUpdateTaskDialog {

    private Context mContext = null;
    private View mLayout = null;
    WindowManager mWindowManager = null;
    private static WaitingUpdateTaskDialog instance = new WaitingUpdateTaskDialog();
    private Timer timer = null;
    private TimerTask task = null;
    private long showTimeStamp = 0;
    public static final String TAG = "WaitingUpdateTaskDialog";
    Handler mViewhandler;

    public synchronized static WaitingUpdateTaskDialog getInstance() {

        return instance;

    }

    public WaitingUpdateTaskDialog() {

    }

    public synchronized void showView(Context context, Handler viewhandler) {
        MLog.d(TAG, "try to show view" + "   Processid " + android.os.Process.myPid() + " Threadid: " + android.os.Process.myTid() + " name " + Thread.currentThread().getName());
        if (mLayout != null) {
            MLog.d(TAG, "fail mlayout not null");
            return;
        }
        if (context == null)
            return;

        if (MainActivity.CURRENT_PAGE != 0) {
            return;
        }
        MLog.d(TAG, "activitycount" + MainApplication.getActivityCount());
        if (MainApplication.getActivityCount() == 0) {

            return;
        }


        MLog.d(TAG, "showviewtrue" + System.currentTimeMillis());
        mContext = context.getApplicationContext();
        mViewhandler = viewhandler;
        try {

            if (mWindowManager == null)
                mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams para = new WindowManager.LayoutParams();
            para.height = WindowManager.LayoutParams.WRAP_CONTENT;
            para.width = WindowManager.LayoutParams.WRAP_CONTENT;
            para.format = PixelFormat.RGBA_8888;

            para.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;

            // para.format = 1;

            // para.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN |
            // WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

            // RelativeLayout.LayoutParams para = new
            // RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
            // RelativeLayout.LayoutParams.WRAP_CONTENT);

            para.type = WindowManager.LayoutParams.TYPE_TOAST;
            mLayout = LayoutInflater.from(mContext).inflate(R.layout.dialog_waitingupdate, null);
            mLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mWindowManager.addView(mLayout, para);


            if (timer != null) {
                if (task != null) {
                    task.cancel();
                    timer.cancel();
                }
            }

            timer = new Timer();
            task = new MyTimerTask();
            timer.schedule(task, 10000);

            showTimeStamp = System.currentTimeMillis();
        } catch (Exception e) {
            MLog.e("checkisshowview_error", e.getMessage() + "");
        }

    }

    public synchronized void clean() {


        removeView();

        mContext = null;
        mLayout = null;

        if (timer != null) {
            if (task != null) {
                task.cancel();
                timer.cancel();
            }
        }

        timer = null;
        task = null;

    }

    public class MyTimerTask extends TimerTask {
        @Override
        public synchronized void run() {
//            runOnUiThread(new Runnable(){
//
//                @Override
//                public void run() {
//                    //更新UI
//                   // imageView.setImageBitmap(bitmap);
//                    removeView();
//                }
//
//            });


            removeView();

        }
    }

    public synchronized void removeView(boolean isUpdatesuccess) {

        //  MLog.e("checkisshowview", "showviewfalse" + System.currentTimeMillis());
        MLog.d(TAG, "try to close view" + "   Processid" + android.os.Process.myPid() + " Threadid: " + android.os.Process.myTid() + " name " + Thread.currentThread().getName());

        if (mLayout != null) {

            MLog.d(TAG, "mlayout not null");


            if (Looper.myLooper() != Looper.getMainLooper()) {
                // Current thread is the UI/Main thread

                if (mViewhandler != null) {
                    MLog.d(TAG, "try to close not mainthread");
                    if (isUpdatesuccess)
                        mViewhandler.sendEmptyMessage(1);
                    else
                        mViewhandler.sendEmptyMessage(2);
                    return;

                }

            }


            // 判断是否attach
            if (mLayout.getWindowToken() == null) {
                MLog.d(TAG, " mLayout.getWindowToken() == null" + System.currentTimeMillis());
                try {
                    mWindowManager.removeViewImmediate(mLayout);

                } catch (Exception e) {

                }
                mLayout = null;

                if (timer != null) {
                    if (task != null) {
                        task.cancel();
                        timer.cancel();
                    }
                }
                return;
            }
            try {
                mWindowManager.removeViewImmediate(mLayout);

                if (isUpdatesuccess) {

                    long currentTime = System.currentTimeMillis();

                    if (currentTime - showTimeStamp < 10000) {

                        if (NotifyManager.getInstance().getContext() != null) {
                            Toast.makeText(NotifyManager.getInstance().getContext(), "更新成功", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            } catch (Exception e) {

            }
            mLayout = null;

        }

        if (timer != null) {
            if (task != null) {
                task.cancel();
                timer.cancel();
            }
        }
    }

    public synchronized void removeView() {

        MLog.d(TAG, "1try to close view" + "   Processid" + android.os.Process.myPid() + " Threadid: " + android.os.Process.myTid() + " name " + Thread.currentThread().getName());

        if (mLayout != null) {

            MLog.d(TAG, "mlayout not null");


            if (Looper.myLooper() != Looper.getMainLooper()) {
                // Current thread is the UI/Main thread
                MLog.d(TAG, "try to close not mainthread");
                if (mViewhandler != null) {
                    mViewhandler.sendEmptyMessage(0);
                    return;
                }
            }


            // 判断是否attach
            if (mLayout.getWindowToken() == null) {

                MLog.d(TAG, " mLayout.getWindowToken() == null" + System.currentTimeMillis());

                try {
                    mWindowManager.removeView(mLayout);

                } catch (Exception e) {

                }
                mLayout = null;

                if (timer != null) {
                    if (task != null) {
                        task.cancel();
                        timer.cancel();
                    }
                }
                return;
            }
            try {
                mWindowManager.removeView(mLayout);
            } catch (Exception e) {
                MLog.d(TAG, "" + e.getMessage());
            }
            mLayout = null;

        }

        if (timer != null) {
            if (task != null) {
                task.cancel();
                timer.cancel();
            }
        }
    }

}
