package com.yunbaba.freighthelper.ui.activity.me;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.nv.util.CldNaviUtil;
import com.cld.ols.tools.URLAnalysis;
import com.google.zxing.Result;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseScanAcivity;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.ToastUtils;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.api.CldKAccountAPI.IQRLoginResultListener;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

public class ScanLoginActivity extends BaseScanAcivity {

	/**
	 * 二维码登录验证返回消息类型，验证超时
	 */
	public static final int MSG_QR_TIMEOUT = 1;
	/**
	 * 二维码登录验证返回消息类型，验证登录成功
	 */
	public static final int MSG_QR_LOGIN_SUCCEED = MSG_QR_TIMEOUT + 1;
	/**
	 * 二维码登录验证返回消息类型，验证登录失败
	 */
	public static final int MSG_QR_LOGIN_FAILURE = MSG_QR_LOGIN_SUCCEED + 1;
	/**
	 * 二维码登录验证返回消息类型，二维码过期或已使用
	 */
	public static final int MSG_QR_CODE_USED = MSG_QR_LOGIN_FAILURE + 1;
	/**
	 * 二维码登录验证返回消息类型，网络异常
	 */
	public static final int MSG_QR_NET_ERROR = MSG_QR_CODE_USED + 1;
	/**
	 * 二维码登录验证返回消息类型，帐号已重新登录
	 */
	public static final int MSG_QR_RELOGINED = MSG_QR_NET_ERROR + 1;
	/**
	 * 二维码登录验证返回消息类型，登录失效
	 */
	public static final int MSG_QR_INVALID = MSG_QR_RELOGINED + 1;

	@BindView(R.id.title_left_img)
	ImageView mImgBack;

	@BindView(R.id.title_right_img)
	ImageView mImgRight;

	@BindView(R.id.title_text)
	TextView mTvTitle;

	Handler handler;

	@Override
	public int getLayoutId() {

		return R.layout.activity_scanlogin;
	}

	@Override
	public void initData() {

		mOpenFlash = false;
		mImgBack.setVisibility(View.VISIBLE);
		mImgRight.setVisibility(View.GONE);
		mTvTitle.setText("扫码登录");

		updateFlashView(mOpenFlash);
		//Toast.makeText(NotifyManager.getInstance().getContext(), "扫出结果", Toast.LENGTH_LONG).show();
		handler = new MyHandler(this);

		// urlAnalysis.analysis(QrData);
	}

	@OnClick({ R.id.title_left_img, R.id.title_right_img })
	void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_left_img:
			// 返回
			finish();
			//ToastUtils.showMessageForScan(ScanLoginActivity.this, "TEST", Toast.LENGTH_LONG);
			break;
		case R.id.title_right_img:
			// 闪光灯
			mOpenFlash = !mOpenFlash;
			operateFlash(mOpenFlash);
			updateFlashView(mOpenFlash);
			break;
		}
	}

	private void operateFlash(boolean open) {
		if (open) {
			openFlash();
		} else {
			closeFlash();
		}
	}

	// 扫描结束后跳转
	@Override
	public void handleDecode(Result result, Bitmap barcode) {

		super.handleDecode(result, barcode);

		//Toast.makeText(NotifyManager.getInstance().getContext(), "扫出结果", Toast.LENGTH_LONG).show();

		if (!CldNaviUtil.isNetConnected()) {
			// 无网络不处理
			
			
			//Toast.makeText(ScanLoginActivity.this, "当前网络不可用，请检查网络设置。", Toast.LENGTH_LONG).show();
			
			Message msg2 = handler.obtainMessage();
			msg2.what = 2;
			msg2.obj = "当前网络不可用，请检查网络设置。";
			handler.handleMessage(msg2);
			
			return;
		}

		String resultString = result.getText();

//		if (resultString == null) {
//			//Toast.makeText(ScanLoginActivity.this, "null", Toast.LENGTH_LONG).show();
//			
//			Message msg2 = handler.obtainMessage();
//			msg2.what = 2;
//			msg2.obj = "null";
//			handler.handleMessage(msg2);
//			
//		} else{
//			Message msg2 = handler.obtainMessage();
//			msg2.what = 2;
//			msg2.obj = resultString;
//			handler.handleMessage(msg2);
//			
//		}
//		
//		Message msg2 = handler.obtainMessage();
//		msg2.what = 2;
//		msg2.obj = "sdfdsfdsf";
//		handler.handleMessage(msg2);
//		
//		
//		
//		msg2 = handler.obtainMessage();
//		msg2.what = 2;
//		msg2.obj = "5436543543543";
//		handler.handleMessage(msg2);

		MLog.e("resultstr", resultString + "");
		//mTvTitle.setText(resultString);

		String guid = null;

		try {

			URLAnalysis urlAnalysis = new URLAnalysis();
			urlAnalysis.analysis(resultString);

			guid = urlAnalysis.getParam("p");
			MLog.e("resultstrguid", guid + "");

		} catch (Exception e) {
			// TODO: handle exception
			// mTvTitle.setText(e.getMessage());
		}

		// String strNs = urlAnalysis.getParam("ns");

		// ns 为1 时 被扫描设备网络正常

		// if(strNs)

		if (guid != null) {
			
			
			WaitingProgressTool.showProgress(this);

			CldKAccountAPI.getInstance().loginByQRcode(guid, new IQRLoginResultListener() {
				@Override
				public void onLoginByQRcodeResult(int errCode) {

					WaitingProgressTool.closeshowProgress();
					
					// Message msg = handler.obtainMessage();
					
					MLog.e("qrlogin", "resultcode"+errCode);
					
					int msg;

					switch (errCode) {
					case 0:// 登录成功
						msg = MSG_QR_LOGIN_SUCCEED;
						break;
					case 801:// 二维码已过期或已使用
						msg = MSG_QR_CODE_USED;
						break;
					case 500:// 登录失效
						msg = MSG_QR_INVALID;
						break;
					case 501:// 用户已重新登录
						msg = MSG_QR_RELOGINED;
						break;
					case -2:// 网络异常
						msg = MSG_QR_NET_ERROR;
						break;
					default: // 其他类型，返回登录失败
						msg = MSG_QR_LOGIN_FAILURE;
						break;
					}

					Message msg1 = handler.obtainMessage();
					msg1.what = 1;
					
					Message msg2 = handler.obtainMessage();
					msg2.what = 2;
					switch (msg) {
					case MSG_QR_LOGIN_SUCCEED:// 登录成功
						//showToast(R.string.scan_result_verify_success);
						
						
						
						msg1.arg1 = R.string.scan_result_verify_success;
						handler.handleMessage(msg1);
						
						
						finish();
						break;
					case MSG_QR_LOGIN_FAILURE:// 登录失败
						//showToast(R.string.scan_result_verify_failure);
						
						msg1.arg1 = R.string.scan_result_verify_failure;
						handler.handleMessage(msg1);
						
						break;
					case MSG_QR_CODE_USED: // 二维码过期或已使用
						//showToast("二维码已过期");
						
						msg1.obj = "二维码已过期";
						handler.handleMessage(msg2);
						
						break;
					case MSG_QR_INVALID: // 登录失效
						//showToast(R.string.scan_result_invalid);
						
						msg1.arg1 = R.string.scan_result_invalid;
						handler.handleMessage(msg1);
						
						break;
					case MSG_QR_RELOGINED: // 账号已登录
						//showToast(R.string.scan_result_relogined);
						
						msg1.arg1 = R.string.scan_result_relogined;
						handler.handleMessage(msg1);
						break;
					case MSG_QR_TIMEOUT: // 登录超时
						//showToast(R.string.scan_result_timeout);
						
						msg1.arg1 = R.string.scan_result_timeout;
						handler.handleMessage(msg1);
						break;
					case MSG_QR_NET_ERROR: // 网络异常
						//showToast(R.string.common_network_abnormal);
						
						msg1.arg1 = R.string.common_network_abnormal;
						handler.handleMessage(msg1);
						break;

					}
					
					
					
					if(msg!=MSG_QR_LOGIN_SUCCEED)
						restartScan();

				}
			});

		} else {

			restartScan();

		}
		
		return;

	}

	private void showToast(int msg) {
		
		MLog.e("showTextqrlogin1", this.getResources().getString(msg));
		//ScanToast.makeText(ScanLoginActivity.this, msg, Toast.LENGTH_LONG).show();
		ToastUtils.showMessageForScan(ScanLoginActivity.this, ScanLoginActivity.this.getResources().getString(msg), Toast.LENGTH_LONG);
	}

	private void showToast(String msg) {
		
		MLog.e("showTextqrlogin2", msg);
		//ScanToast.makeText(ScanLoginActivity.this, msg, Toast.LENGTH_LONG).show();
		//Toast.makeText(ScanLoginActivity.this, msg, Toast.LENGTH_LONG).show();
		
		ToastUtils.showMessageForScan(ScanLoginActivity.this, msg, Toast.LENGTH_LONG);
	}

	private static class MyHandler extends Handler {
		private final WeakReference<ScanLoginActivity> mActivity;

		public MyHandler(ScanLoginActivity activity) {
			mActivity = new WeakReference<ScanLoginActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// System.out.println(msg);
			if (mActivity.get() == null) {
				return;
			}

			if (msg.what == 1) {

				
				
				mActivity.get().showToast(msg.arg1);
				
			} else if (msg.what == 2) {
				
				//MLog.e("showTextqrlogin2", (String) msg.obj);
				mActivity.get().showToast((String) msg.obj);
			}

			// if (msg.what == RequestListMore) {
			// mActivity.get().getList(true, false);
			// } else if (msg.what == RequestList) {
			// mActivity.get().getList(false, false);
			// } else if (msg.what == RequestSearchListMore) {
			// mActivity.get().searchResult((String) msg.obj, true);
			// } else if (msg.what == RequestSearchList) {
			// mActivity.get().searchResult((String) msg.obj, false);
			// } else {
			//
			// }
			// mActivity.get().todo();
		}

	}

	private void updateFlashView(boolean show) {
		if (show) {
			mImgRight.setImageResource(R.drawable.ic_scan_no_flashlight);
		} else {
			mImgRight.setImageResource(R.drawable.ic_scan_flashlight);
		}
	}

}