package com.yunbaba.freighthelper.ui.activity.task;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.nv.util.CldNaviUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.WaybillManager;
import com.yunbaba.api.trunk.bean.OnUIResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.utils.ErrCodeUtil;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.CaptureActivityListener;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Vector;

import butterknife.BindView;
import butterknife.OnClick;

public class WaybillScanActivity extends BaseButterKnifeActivity
		implements SurfaceHolder.Callback, CaptureActivityListener {

	private CaptureActivityHandler handler;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet = "UTF-8";
	private InactivityTimer inactivityTimer;

	private boolean hasSurface;

	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private boolean vibrate;

	/** 震动时长 */
	private static final long VIBRATE_DURATION = 100L;
	/** 提示音音量 */
	private static final float BEEP_VOLUME = 0.10f;

	@BindView(R.id.title_left_img)
	ImageView mImgBack;

	@BindView(R.id.title_right_img)
	ImageView mImgRight;

	@BindView(R.id.title_text)
	TextView mTvTitle;

	/** 预览图像的View */
	@BindView(R.id.rich_scan_preview)
	SurfaceView mSurfaceView;

	@BindView(R.id.scan_viewfinderView)
	ViewfinderView mViewfinderView;

	@BindView(R.id.scan_bottom_input_viewgroup)
	ViewGroup mInputLayout;

	@BindView(R.id.scan_bottom_atuo_viewgroup)
	ViewGroup mScanLayout;

	private MtqDeliStoreDetail mStoreDetail;
	private MtqDeliOrderDetail mOrderDetail;

	private String mJsonStore;
	private String mJsonOrder;

	private boolean mShowFlash = false;

	private IntentFilter mFilter;

	@Override
	public int getLayoutId() {

		return R.layout.activity_waybill_scan;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	protected void onResume() {

		super.onResume();
		startScan();
		registerReceiver(mReceiver, mFilter);
	}

	@Override
	protected void onPause() {

		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@OnClick({ R.id.title_left_img, R.id.scan_bottom_input_viewgroup, R.id.title_right_img })
	void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_left_img:
			// 返回
			finish();
			// Intent intent1 = new Intent();
			// intent1.setClass(this, WaybillInputActivity.class);
			// startActivity(intent1);
			break;
		case R.id.scan_bottom_input_viewgroup:
			Intent intent = new Intent();
			// intent.putExtra("storedetail", mJsonStore);
			// intent.putExtra("orderdetail", mJsonOrder);
			intent.setClass(this, WaybillInputActivity.class);
			startActivity(intent);
			finish();
			break;

		case R.id.title_right_img:
			mShowFlash = !mShowFlash;
			if (mShowFlash) {
				openFlash();
			} else {
				closeFlash();
			}
			updateFlashView(mShowFlash);
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		initResource();
		mFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

		EventBus.getDefault().register(this);
	}

	private void initResource() {
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		// if (getIntent().hasExtra("storedetail")) {
		// try {
		// mJsonStore = getIntent().getStringExtra("storedetail");
		// if (!TextUtils.isEmpty(mJsonStore))
		// mStoreDetail = GsonTool.getInstance().fromJson(mJsonStore,
		// MtqDeliStoreDetail.class);
		// } catch (Exception e) {
		// finish();
		// }
		// }
		// if (getIntent().hasExtra("orderdetail")) {
		// try {
		// mJsonOrder = getIntent().getStringExtra("orderdetail");
		// if (!TextUtils.isEmpty(mJsonOrder))
		// mOrderDetail = GsonTool.getInstance().fromJson(mJsonOrder,
		// MtqDeliOrderDetail.class);
		// } catch (Exception e) {
		// finish();
		// }
		// }

		mStoreDetail = WaybillManager.getInstance().getmStoreDetail();
		mOrderDetail = WaybillManager.getInstance().getmOrderDetail();

		if (mTvTitle != null) {
			
			mTvTitle.setText("扫描货物条码");
			//mTvTitle.setText("运单号:" + mOrderDetail.cust_orderid);
		}

		if (mInputLayout != null) {
			mInputLayout.setSelected(false);
		}

		if (mScanLayout != null) {
			mScanLayout.setSelected(true);
		}

		mImgBack.setVisibility(View.VISIBLE);
		mImgRight.setVisibility(View.VISIBLE);
		mImgRight.setImageResource(R.drawable.ic_scan_flashlight);

		updateFlashView(mShowFlash);
	}

	private void initCamera(final SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (Exception e) { // 打开摄像头失败（没有摄像头，或者用户拒绝权限）
			MLog.w("open camera driver failure");
			Toast.makeText(this, R.string.scan_can_not_open_camera, Toast.LENGTH_SHORT).show();
			finish();
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	/**
	 * 开始扫描
	 * 
	 */
	private void startScan() {
		SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	/**
	 * 停止扫描
	 */
	private void stopScan() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	/**
	 * @Title: restartScan
	 * @Description: 重新开始扫描
	 * @return: void
	 */
	private void restartScan() {
		if (handler == null)
			return;
		Message msg = handler.obtainMessage(CaptureActivityHandler.MSG_RESTART_PREVIEW);
		handler.sendMessageDelayed(msg, 2000);
	}

	/**
	 * @Title: openFlash
	 * @Description: 打开闪关灯
	 * @return: void
	 */
	protected void openFlash() {
		try {
			Camera camera = CameraManager.get().getCamera();
			camera.startPreview();
			Parameters parameter = camera.getParameters();
			parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(parameter);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	protected void closeFlash() {
		try {
			Camera camera = CameraManager.get().getCamera();
			camera.startPreview();
			Parameters parameter = camera.getParameters();
			parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(parameter);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 初始化音频
	 * 
	 * @return void
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	/**
	 * 播放音频和震动
	 */
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		// 没有权限会出异常
		if (vibrate) {
			try {
				Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				vibrator.vibrate(VIBRATE_DURATION);
			} catch (Exception e) {
				// TODO: handle exception
				// Toast.makeText(this, "Error", 100).show();
			}
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
			if (mShowFlash) {
				openFlash();
			} else {
				closeFlash();
			}
			updateFlashView(mShowFlash);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		hasSurface = false;
	}

	@Override
	public Handler getHandler() {

		return handler;
	}

	@Override
	public ViewfinderView getViewfinderView() {

		return mViewfinderView;
	}

	@Override
	public Activity getActivity() {

		return this;
	}

	@Override
	public void handleDecode(Result result, Bitmap barcode) {

		if (!CldNaviUtil.isNetConnected()) {
			// 无网络不处理
			Toast.makeText(WaybillScanActivity.this, "当前网络不可用，请检查网络设置。", Toast.LENGTH_LONG).show();
			return;
		}
		MLog.e("扫描结果",result!=null?result.getText():"");

		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();

		WaitingProgressTool.showProgress(this);
		WaybillManager.getInstance().UpLoadGoodScanRecord(mStoreDetail, mOrderDetail, result.getText(),
				System.currentTimeMillis() / 1000, new OnUIResult() {

					@Override
					public void OnResult() {

						String tmp = "(" + WaybillManager.getInstance().getmScanNum() + "/"
								+ WaybillManager.getInstance().getmTotalNum() + ")";

						String text = "货物上传成功";
						if (WaybillManager.getInstance().getmScanNum() == WaybillManager.getInstance()
								.getmTotalNum()) {
							text = "该货物已全部扫描完毕";
						}
						text += tmp;
						Toast.makeText(WaybillScanActivity.this, text, Toast.LENGTH_LONG).show();
						WaitingProgressTool.closeshowProgress();
						restartScan();
					}

					@Override
					public void OnError(int ErrCode) {

						String tmp = "(" + WaybillManager.getInstance().getmScanNum() + "/"
								+ WaybillManager.getInstance().getmTotalNum() + ")";

						String text = "";

						if (ErrCodeUtil.isNetErrCode(ErrCode)) {
							text = "网络通信出现问题，请稍后再试。";
						} else {
							text = "(条码错误)货物上传失败" + tmp;
						}

						if (ErrCode == 1406) {
							if (WaybillManager.getInstance().getmScanNum() == WaybillManager.getInstance()
									.getmTotalNum()) {
								text = "该货物已全部扫描完毕" + tmp;
							} else {
								text = "该条形码已全部扫描完毕" + tmp;
							}
						}
						if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
							// Toast.makeText(TaskPointDetailActivity.this,
							// "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(WaybillScanActivity.this, text, Toast.LENGTH_LONG).show();
						}
						WaitingProgressTool.closeshowProgress();
						restartScan();
					}
				});

	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
						Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) { // 网络可用时，重新启动扫描
					MLog.d("receiver network is available");
					if (handler != null)
						handler.obtainMessage(CaptureActivityHandler.MSG_RESTART_PREVIEW).sendToTarget();
				} else {
					MLog.d("receiver network is inavailable");
				}
			}

		}
	};

	private void updateFlashView(boolean show) {
		if (show) {
			mImgRight.setImageResource(R.drawable.ic_scan_no_flashlight);
		} else {
			mImgRight.setImageResource(R.drawable.ic_scan_flashlight);
		}
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
	public synchronized void onTaskBusinessMsgEvent(TaskBusinessMsgEvent event) {

		switch (event.getType()) {

		// 任务刷新
		case 2:
			
			break;

		// 作废任务，删除某些任务
		case 3:

			if (mStoreDetail == null)
				return;

			if (isFinishing())
				return;

			if (event.getTaskIdList() != null && event.getTaskIdList().size() > 0) {

				if (TextStringUtil.isContainStr(event.getTaskIdList(), mStoreDetail.taskId)) {

					// Toast.makeText(this, "当前操作任务单已撤回",
					// Toast.LENGTH_LONG).show();
					finish();
				}

			}

			break;
		case 4:
			if (mStoreDetail == null)
				return;

			if (isFinishing())
				return;

			if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {

				if (mOrderDetail != null && !TextUtils.isEmpty(mOrderDetail.waybill)) {

					if (TextStringUtil.isContain(event.getRefreshtaskIdList(),mStoreDetail.taskId, mOrderDetail.waybill)) {
						finish();
					}

				}
			}

			break;

		default:
			break;
		}

	}
}
