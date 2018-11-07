/**
 *
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: AboutActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 关于界面
 * @author: zhaoqy
 * @date: 2017年3月27日 上午9:35:37
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.me;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.net.CldFileDownloader;
import com.cld.net.ICldFileDownloadCallBack;
import com.yunbaba.api.appcenter.AppCenterAPI;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.manager.AppVersionManager;
import com.yunbaba.freighthelper.manager.AppVersionManager.IAppVersionListener;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.ui.dialog.ProgressDialog;
import com.yunbaba.freighthelper.ui.dialog.ProgressDialog.ProgressDialogListener;
import com.yunbaba.freighthelper.ui.dialog.UpdateAskDialog;
import com.yunbaba.freighthelper.ui.dialog.UpdateDialog;
import com.yunbaba.freighthelper.utils.AppInfo;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.PermissionUtil;
import com.yunbaba.ols.sap.bean.CldSapKAppParm.MtqAppInfoNew;

import java.io.File;

public class AboutActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = "AboutActivity";
	private ImageView mBack;
	private TextView mTitle;
	private TextView mVersion;
	private TextView mUpdate;
	private MtqAppInfoNew mAppParm;
	UpdateAskDialog updatedialog;
	private NotificationManager manager = null;
	private Notification notification = null;

	private int UPDATE_NOTIFICATION_ID = 140612;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_about;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mVersion = (TextView) findViewById(R.id.about_version);
		mUpdate = (TextView) findViewById(R.id.about_update);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
		findViewById(R.id.about_update_layout).setOnClickListener(this);
		findViewById(R.id.about_agreement_layout).setOnClickListener(this);
		findViewById(R.id.about_web_layout).setOnClickListener(this);
		findViewById(R.id.about_customer_layout).setOnClickListener(this);
		findViewById(R.id.about_downloadlink_layout).setOnClickListener(this);
	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.me_about);
	}

	@Override
	protected void loadData() {

	}

	@Override
	protected void updateUI() {


		// boolean hasNew = AppCenterAPI.getInstance().hasNewVersion();
		// if (hasNew) {
		// /**
		// * 有新版本
		// */
		// mHadler.sendEmptyMessage(1);
		// } else {
		// /**
		// * 已是最新版本
		// */
		// mHadler.sendEmptyMessage(0);
		// }
	}

	@Override
	protected void messageEvent(AccountEvent event) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_left_img: {
				finish();
				break;
			}
			case R.id.about_update_layout: {

				if (!CldPhoneNet.isNetConnected()) {
					Toast.makeText(this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();

					return;
				}

				boolean hasNew = AppCenterAPI.getInstance().hasNewVersion();
				if (hasNew) {
					mAppParm = AppCenterAPI.getInstance().getMtqAppInfo();
					if (mAppParm != null && mAppParm.version > 0 && !TextUtils.isEmpty(mAppParm.filepath)) {
						onCheckResult();
					} else {
						checkAppVersion();
					}
				} else {
					checkAppVersion();
				}
				break;
			}
			case R.id.about_agreement_layout: {
				Intent intent = new Intent(this, AgreementActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.about_web_layout: {
				// http://www.matiquan.cn/
				String url = getResources().getString(R.string.about_web_content);
				Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra("URL", "http://" + url);
				startActivity(intent);
				break;
			}
			case R.id.about_customer_layout: {
				call();
				break;
			}

			case R.id.about_downloadlink_layout:{
				Intent intent = new Intent(this, DownloadQrCodeActivity.class);
				startActivity(intent);

				break;
			}
			default:
				break;
		}
	}

	private void checkAppVersion() {
		String str = getResources().getString(R.string.common_network_version_update);
		ProgressDialog.showProgress(this, str, new ProgressDialogListener() {

			@Override
			public void onCancel() {

			}
		});

		AppVersionManager.getInstance().checkVersion(new IAppVersionListener() {

			@Override
			public void onResult(int errCode, MtqAppInfoNew result) {
				if (ProgressDialog.isShowProgress()) {
					ProgressDialog.cancelProgress();
				}


				MLog.e(TAG, "errCode: " + errCode + ", result: " + result);
				if (errCode == 0) {
					mAppParm = result;
					//mHadler.sendEmptyMessage(2);

					if (mAppParm != null && mAppParm.version > 0 && !TextUtils.isEmpty(mAppParm.filepath)) {
						mHadler.sendEmptyMessage(1);
						mHadler.sendEmptyMessage(2);
					} else
						mHadler.sendEmptyMessage(0);
				} else {
					//mHadler.sendEmptyMessage(0);
					/**
					 * 获取失败
					 */
					MLog.e(TAG, "checkVersion failed. errCode: " + errCode);
				}
			}
		});
	}

	private void onCheckResult() {
		if (mAppParm != null) {
			/**
			 * 有新版本
			 */
			String title = getResources().getString(R.string.dialog_new_version);
			// String vername = mAppParm.version+"";

			String downloadUrl = mAppParm.filepath;

			int lastIndex = downloadUrl.lastIndexOf("_");
			int lastIndex2 = downloadUrl.lastIndexOf(".apk");

			String vername = "";

			try {

				if (lastIndex == -1 || lastIndex2 == -1 || lastIndex + 1 >= lastIndex2
						|| lastIndex + 1 > downloadUrl.length() || lastIndex2 > downloadUrl.length()) {

					int lastIndex3 = downloadUrl.lastIndexOf("/");
					vername = downloadUrl.substring(lastIndex3 + 1, downloadUrl.length());

				} else {
					vername = downloadUrl.substring(lastIndex + 1, lastIndex2);
					vername = vername.toUpperCase();
				}

			} catch (Exception e) {

			}

			MLog.e("check", lastIndex + " " + lastIndex2 + " " + downloadUrl + " " + vername);
			// String contentHint = "最新版本:";
			// getResources().getString(
			// R.string.dialog_lastest_version);
			if (TextUtils.isEmpty(vername))
				vername = mAppParm.version + "";

			String content = "最新版本:" + vername;// String.format(contentHint,
			// vername);
			String cancel = getResources().getString(R.string.dialog_no_need);
			String sure = getResources().getString(R.string.dialog_update);
			updatedialog = new UpdateAskDialog(this, title, content, cancel, sure, mAppParm.upgradeflag, new UpdateAskDialog.IAskDialogListener() {

				@Override
				public void OnCancel() {
					mHadler.sendEmptyMessage(1);
				}

				@Override
				public void OnSure() {
					updateVersion();
				}
			});
			updatedialog.setCancelable(false);
			updatedialog.show();
		} else {
			mHadler.sendEmptyMessage(0);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHadler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0: {
					/**
					 * 已是最新版本
					 */
					if (ProgressDialog.isShowProgress()) {
						ProgressDialog.cancelProgress();
					}

					mUpdate.setCompoundDrawables(null, null, null, null);

					String update = getResources().getString(R.string.about_update_latest);
					mUpdate.setText(update);
					break;
				}
				case 1: {
					/**
					 * 有新版本
					 */
					MLog.e(TAG, "set new drawable");
					Drawable new_version = getResources().getDrawable(R.drawable.about_new_version);

					WindowManager wm = (WindowManager) AboutActivity.this.getSystemService(Context.WINDOW_SERVICE);

					int height = (int) (wm.getDefaultDisplay().getWidth() * 0.06);

					new_version.setBounds(0, 0, height * 2, height);
					mUpdate.setCompoundDrawables(null, null, new_version, null);
					mUpdate.setText("");
					break;
				}
				case 2: {
					onCheckResult();
					break;
				}
				default:
					break;
			}
		}

		;
	};

	UpdateDialog dialog;
	CldFileDownloader mDownloader = null;

	protected void updateVersion() {
		if (!CldPhoneNet.isNetConnected()) {
			Toast.makeText(this, R.string.common_network_abnormal, Toast.LENGTH_SHORT).show();
		} else {

			if (PermissionUtil.isNeedPermissionForStorage(this)) {

				Toast.makeText(this, "请打开储存空间权限", Toast.LENGTH_SHORT).show();
				return;

			}

			String downloadUrl = mAppParm.filepath;
			if (!TextUtils.isEmpty(downloadUrl)) {

			//	mTitle.setText("开始下载");

				if (dialog != null)
					dialog.cancel();

				dialog = new UpdateDialog(this);

				dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {


						if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_UP) {


							if(mAppParm!=null && mAppParm.upgradeflag == 1)
								return false;

							MLog.e("update", "backclick222");

							if (mDownloader != null) {
								MLog.e("update", "backclick cancel");
								mDownloader.stop();
								mDownloader.resetMonitor();
								//mDownloader.setCB(null);
							}


						}
						return false;
					}
				});

				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {

						if(mAppParm!=null && mAppParm.upgradeflag == 1){

							onCheckResult();


						}

					}
				});

				dialog.setCancelable((mAppParm!=null && mAppParm.upgradeflag == 1)?false:true);
				dialog.show();
				startDownloadApk(downloadUrl);
			} else {
				/**
				 * 下载失败
				 */
				updateFail();
			}
		}
	}

	private void updateFail() {
		String string = "下载失败，请检查网络";
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

	private void startDownloadApk(String url) {
		if (mDownloader == null) {
			mDownloader = new CldFileDownloader();
		}


		mDownloader.stop();
		mDownloader.resetMonitor();

		// int lastIndex3 = url.lastIndexOf("/");f
		// String filename = url.substring(lastIndex3 + 1, url.length());

		String target = MainApplication.getMTQFileStorePath() + "/update" + ".apk";
		mDownloader.downloadFile(url, target, false, mDownloadCB);


	}

	@SuppressLint("NewApi")
	private void call() {
		/*
		 * try { intentToCall(); } catch (Exception e) {
		 * 
		 * }
		 */
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// 在6.0 系统中请求某些权限需要检查权限
			if (!hasPermission()) {
				// 动态请求拨打电话权限
				requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 0x11);
			} else {
				intentToCall();
			}
		} else {
			intentToCall();
		}
	}

	@SuppressLint("NewApi")
	private boolean hasPermission() {
		String permission = Manifest.permission.CALL_PHONE;
		if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {

		super.onResume();


		String vername = AppInfo.getVerName(this);
		if (!TextUtils.isEmpty(vername)) {
			mVersion.setText("Android V" + vername);
		}

		AppVersionManager.getInstance().checkVersion(new IAppVersionListener() {

			@Override
			public void onResult(int errCode, MtqAppInfoNew result) {
				if (ProgressDialog.isShowProgress()) {
					ProgressDialog.cancelProgress();
				}

				MLog.e(TAG, "errCode: " + errCode + ", result: " + result == null ? "null" : "not null");
				if (errCode == 0) {
					mAppParm = result;

					if (mAppParm != null && mAppParm.version > 0 && !TextUtils.isEmpty(mAppParm.filepath)) {
						GeneralSPHelper.getInstance(AboutActivity.this).setIsMeNewRemind(mAppParm.version);
						mHadler.sendEmptyMessage(1);
					} else {
						GeneralSPHelper.getInstance(AboutActivity.this).setIsMeNewRemind(-1);
						mHadler.sendEmptyMessage(0);
					}
				} else {
					// mHadler.sendEmptyMessage(0);
					/**
					 * 获取失败
					 */
					MLog.e(TAG, "checkVersion failed. errCode: " + errCode);
				}
			}
		});
	}

	private void intentToCall() {
		String phone = getResources().getString(R.string.about_customer_phone);
		Intent intent = new Intent(Intent.ACTION_CALL);
		Uri data = Uri.parse("tel:" + phone);
		intent.setData(data);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		this.startActivity(intent);
	}

	/**
	 * 动态请求拨打电话权限后，监听用户的点击事件
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
		case 0x11:
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission Granted
				intentToCall();
			} else {
				// Permission Denied
			}
			break;
		default:
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	/**
	 * @Description 取消软件更新通知
	 * @author：Huagx @date：2014-6-12 下午4:15:31
	 * @return void
	 */
	private void cancelUpdateNotification() {
		if (manager != null) {
			if (dialog != null) {
				dialog.dismiss();
			}
			manager.cancel(UPDATE_NOTIFICATION_ID);
			manager = null;
		}
	}

	/**
	 * @Description 安装apk
	 * @author：Wenap @date：2014-12-31 下午8:32:53
	 * @return void
	 */
	
	protected void installApk() {
		
		if(dialog!=null)
			dialog.dismiss();


		Intent intent = new Intent(Intent.ACTION_VIEW);
		String target = MainApplication.getMTQFileStorePath() + "/update.apk";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			Uri contentUri = FileProvider.getUriForFile(this, "com.mtq.freighthelper.fileprovider", new File(target));
			intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

		} else {
			intent.setDataAndType(Uri.fromFile(new File(target)), "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		//if (this.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		//}

		




		/**
		 * 清楚版本信息数据
		 */
		AppCenterAPI.getInstance().clearAppVersion();
		
		
		//finish();
	}

	/**
	 * 下载回调
	 */
	ICldFileDownloadCallBack mDownloadCB = new ICldFileDownloadCallBack() {

		@Override
		public void onConnecting(boolean bReconnect, String errMsg) {
			MLog.e("update","bReconnect: " + bReconnect + ", msg: " + bReconnect);
			
//			if(bReconnect == false){
//				updateHandler.sendEmptyMessage(0);
//			}
			

			if (!CldPhoneNet.isNetConnected()) {
				updateHandler.sendEmptyMessage(5);
			}
			
			
			
		}

		@Override
		public void updateProgress(long down, long total, long rate) {
			int progress = (int) ((down * 1.0 / total) * 100);
			MLog.i("down: " + down + ", total: " + total + ", rate: " + rate + ",progress: " + progress + "%");

			// MLog.e(TAG, progress + "%");
			Message msg = new Message();
			DownProgress bean = new DownProgress(down, total, rate);
			msg.what = 1;
			msg.obj = bean;
			updateHandler.sendMessage(msg);
		}

		@Override
		public void onSuccess(long size, long elapseMs) {
			MLog.i("onSuccess!!!");
			updateHandler.sendEmptyMessage(2);
		}

		@Override
		public void onFailure(String errMsg) {
			MLog.i("onFailure!!! errMsg: " + errMsg);
			updateHandler.sendEmptyMessage(0);
		}

		@Override
		public void onCancel() {
			MLog.i("onCancel!!!");
			updateHandler.sendEmptyMessage(3);
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler updateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 0: // 下载失败
					if (null != dialog) {
						dialog.dismiss();
					}
					if (mDownloader != null) {
						mDownloader.stop();
						mDownloader.resetMonitor();
						//mDownloader.setCB(null);
						
					}
					//Toast.makeText(AboutActivity.this, "下载失败，请检查网络", Toast.LENGTH_SHORT).show();
					updateFail();
					// downloadFailNotifacation();
					break;
				case 1: // 下载中
					DownProgress bean = (DownProgress) msg.obj;
					if (null == mAppParm) {
						MLog.e("upgradeInfo is null!");
						return;
					}
					if (null == bean) {
						MLog.e("DOWN bean is null!");
						return;
					}
					int progress = bean.progress;
					dialog.setProgressBar(progress);
					// updateNotifacation(bean);
					break;
				case 2: // 下载完成
					
					dialog.setProgressBar(100);
					
					postDelayed(new Runnable() {
						
						@Override
						public void run() {

							cancelUpdateNotification();
							installApk();
						}
					}, 500);
					
					
					break;
				case 3:// 取消
					cancelUpdateNotification();
					
					
					break;
					
				case 5:
					if (null != dialog) {
						dialog.dismiss();
					}
					
					if (mDownloader != null) {
						
						mDownloader.setCB(null);
						mDownloader.stop();
						mDownloader.resetMonitor();
						
					}
					updateFail();
					// downloadFailNotifacation();
					break;
					
				}
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 
	 * 下载进度bean
	 * 
	 * @author Zhouls
	 * @date 2016-3-29 下午6:04:46
	 */
	public static class DownProgress {

		public long down;
		public long total;
		public long rate;
		public int progress;

		public DownProgress() {

		}

		public DownProgress(long down, long total, long rate) {
			if (down > 0 && total > 0) {
				this.down = down;
				this.total = total;
				this.rate = rate;
				this.progress = (int) ((down * 1.0 / total) * 100);
			}
		}
	}
	
	
	@Override
	protected void onDestroy() {

		if(dialog != null) {  
	        dialog.dismiss();
	    }  
		
		if (mDownloader != null) {
			mDownloader.stop();
			//mDownloader.setCB(null);
		}
		super.onDestroy();
	}
	
	
	@Override
	public void onBackPressed() {
		
		
		MLog.e("update", "backclick");
		
		if(dialog!=null&&dialog.isShowing()){

			if(mAppParm!=null && mAppParm.upgradeflag == 1){

			}else {
				dialog.dismiss();

				if (mDownloader != null) {
					MLog.e("update", "backclick cancel");
					mDownloader.stop();
					mDownloader.resetMonitor();
					//mDownloader.setCB(null);
				}
			}
			
		}else {


			if(updatedialog!=null && updatedialog.isShowing()){


			}else {
				finish();
			}
		}

	}
	
	
	
	
}
