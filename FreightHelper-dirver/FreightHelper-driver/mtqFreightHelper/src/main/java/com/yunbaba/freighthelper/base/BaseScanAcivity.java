package com.yunbaba.freighthelper.base;

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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.cld.nv.util.CldNaviUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.MLog;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.CaptureActivityListener;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;


public abstract class BaseScanAcivity extends BaseButterKnifeActivity implements SurfaceHolder.Callback,
			CaptureActivityListener{

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
	
	protected boolean mOpenFlash = false;
	
	private IntentFilter mFilter;
	
	/** 预览图像的View */
//	@BindView(R.id.rich_scan_preview)
	SurfaceView mSurfaceView;
	
//	@BindView(R.id.scan_viewfinderView)
	ViewfinderView mViewfinderView;
	
	@Override
	protected void onDestroy() {

		super.onDestroy();
		
	}

	@Override
	protected void onResume() {

		super.onResume();

		AppStatApi.statOnPageStart(this);
		startScan();
		registerReceiver(mReceiver, mFilter);
	}

	@Override
	protected void onPause() {

		super.onPause();

		AppStatApi.statOnPagePause(this);
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		initResource();
		initData();
	}
	
	private void initResource(){
		mFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		mSurfaceView = (SurfaceView)findViewById(R.id.rich_scan_preview);
		mViewfinderView = (ViewfinderView)findViewById(R.id.scan_viewfinderView);
	}

	
	public void initData(){
		
	}
	
	
	private void initCamera(final SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (Exception e) { // 打开摄像头失败（没有摄像头，或者用户拒绝权限）
			MLog.w("open camera driver failure");
			Toast.makeText(this, R.string.scan_can_not_open_camera, Toast.LENGTH_SHORT)
					.show();
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
	protected void startScan(){
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
	protected void stopScan() {
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
	protected void restartScan() {
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
	protected void openFlash(){
		try {
			Camera  camera = CameraManager.get().getCamera();  
			camera.startPreview();
			Parameters parameter = camera.getParameters();  
			parameter.setFlashMode(Parameters.FLASH_MODE_TORCH); 
			camera.setParameters(parameter);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected void closeFlash(){
		try {
			Camera  camera = CameraManager.get().getCamera(); 
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
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
						file.getLength());
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
	protected void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		//没有权限会出异常
		if (vibrate) {
			try {
				Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				vibrator.vibrate(VIBRATE_DURATION);
			} catch (Exception e) {
				// TODO: handle exception
//				Toast.makeText(this, "Error", 100).show();
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
			if (mOpenFlash){
				openFlash();
			}else{
				closeFlash();
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		
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

//		Toast.makeText(this, "handleDecode", Toast.LENGTH_SHORT).show();
		if (!CldNaviUtil.isNetConnected()) {
			// 无网络不处理
			return;
		}
		
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
	}
	
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) { // 网络可用时，重新启动扫描
					MLog.d("receiver network is available");
					if (handler != null)
						handler.obtainMessage(CaptureActivityHandler.MSG_RESTART_PREVIEW)
								.sendToTarget();
				} else {
					MLog.d("receiver network is inavailable");
				}
			}

		}
	};

	
}
