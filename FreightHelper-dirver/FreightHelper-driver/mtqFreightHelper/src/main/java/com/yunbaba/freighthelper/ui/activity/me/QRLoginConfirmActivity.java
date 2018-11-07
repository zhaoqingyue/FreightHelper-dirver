package com.yunbaba.freighthelper.ui.activity.me;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.ols.tools.URLAnalysis;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.ui.dialog.LoginMsgDialog;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.ToastUtils;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.api.CldKAccountAPI.IQRLoginResultListener;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

public class QRLoginConfirmActivity extends BaseButterKnifeActivity {

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

	@BindView(R.id.tv_cancel)
	TextView tvCancel;

	@BindView(R.id.btn_confirm)
	Button btnConfirm;

	public String loginurl;
	Handler handler;

	@Override
	public int getLayoutId() {

		return R.layout.activity_qrlogin_confirm;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (getIntent() == null || !getIntent().hasExtra("loginurl")) {

			finish();
		} else {

			if (getIntent().hasExtra("loginurl"))
				loginurl = getIntent().getStringExtra("loginurl");

		}

		mTvTitle.setText("扫码登录");
		// iv_titleright.setImageResource(R.drawable.icon_message);
		mImgRight.setVisibility(View.GONE);
		mImgBack.setVisibility(View.VISIBLE);
		handler = new MyHandler(this);

	}

	@OnClick({ R.id.title_left_img, R.id.btn_confirm ,R.id.tv_cancel})
	void onClick(View view) {
		switch (view.getId()) {
		
		case R.id.tv_cancel:
			// 返回
			finish();
			// ToastUtils.showMessageForScan(QRLoginConfirmActivity.this,
			// "TEST",
			// Toast.LENGTH_LONG);
			break;
		
		case R.id.title_left_img:
			// 返回
			finish();
			// ToastUtils.showMessageForScan(QRLoginConfirmActivity.this,
			// "TEST",
			// Toast.LENGTH_LONG);
			break;
		case R.id.btn_confirm:

			MLog.e("onclick qrcode", loginurl);

			String guid = null;

			try {

				URLAnalysis urlAnalysis = new URLAnalysis();
				urlAnalysis.analysis(loginurl);

				guid = urlAnalysis.getParam("p");
				MLog.e("resultstrguid", guid + "");

			} catch (Exception e) {
				// TODO: handle exception
				// mTvTitle.setText(e.getMessage());
			}

			// String strNs = urlAnalysis.getParam("ns");

			// ns 为1 时 被扫描设备网络正常

			// if(strNs)
			// showMsgDialog("测试");

			if (guid != null) {

				WaitingProgressTool.showProgress(this);

				CldKAccountAPI.getInstance().loginByQRcode(guid, new IQRLoginResultListener() {
					@Override
					public void onLoginByQRcodeResult(int errCode) {

						WaitingProgressTool.closeshowProgress();

						// Message msg = handler.obtainMessage();

						MLog.e("qrlogin", "resultcode" + errCode);

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
							// showToast(R.string.scan_result_verify_success);

							// msg1.arg1 = R.string.scan_result_verify_success;
							// handler.handleMessage(msg1);

							handler.post(new Runnable() {

								@Override
								public void run() {


									ToastUtils.showMessage(QRLoginConfirmActivity.this, QRLoginConfirmActivity.this
											.getResources().getString(R.string.scan_result_verify_success),
											Toast.LENGTH_LONG);

								}
							});

							finish();
							break;
						case MSG_QR_LOGIN_FAILURE:// 登录失败
							// showToast(R.string.scan_result_verify_failure);

							msg1.arg1 = R.string.scan_result_verify_failure;
							handler.handleMessage(msg1);

							break;
						case MSG_QR_CODE_USED: // 二维码过期或已使用
							// showToast("二维码已过期");

							msg2.obj = "二维码过期或已使用";
							handler.handleMessage(msg2);

							break;
						case MSG_QR_INVALID: // 登录失效
							// showToast(R.string.scan_result_invalid);

							msg1.arg1 = R.string.scan_result_invalid;
							handler.handleMessage(msg1);

							break;
						case MSG_QR_RELOGINED: // 账号已登录
							// showToast(R.string.scan_result_relogined);

							msg1.arg1 = R.string.scan_result_relogined;
							handler.handleMessage(msg1);
							break;
						case MSG_QR_TIMEOUT: // 登录超时
							// showToast(R.string.scan_result_timeout);

							msg1.arg1 = R.string.scan_result_timeout;
							handler.handleMessage(msg1);
							break;
						case MSG_QR_NET_ERROR: // 网络异常
							// showToast(R.string.common_network_abnormal);

							msg1.arg1 = R.string.common_network_abnormal;
							handler.handleMessage(msg1);
							break;

						}

					}
				});

			} else {

				Toast.makeText(QRLoginConfirmActivity.this, "获取登录特征码失败", Toast.LENGTH_LONG).show();
			}

			break;
		}
	}

	private void showToast(int msg) {

		// MLog.e("showTextqrlogin1", this.getResources().getString(msg));
		// ScanToast.makeText(QRLoginConfirmActivity.this, msg,
		// Toast.LENGTH_LONG).show();

		// Toast.makeText(QRLoginConfirmActivity.this, msg,
		// Toast.LENGTH_LONG).show();

		// ToastUtils.showMessage(QRLoginConfirmActivity.this,
		// QRLoginConfirmActivity.this.getResources().getString(msg),
		// Toast.LENGTH_LONG);

		String msg2 = QRLoginConfirmActivity.this.getResources().getString(msg);

		showMsgDialog(msg2);

	}

	private void showToast(String msg) {

		// MLog.e("showTextqrlogin2", msg);
		// ScanToast.makeText(QRLoginConfirmActivity.this, msg,
		// Toast.LENGTH_LONG).show();
		// Toast.makeText(QRLoginConfirmActivity.this, msg,
		// Toast.LENGTH_LONG).show();

		// ToastUtils.showMessage(QRLoginConfirmActivity.this, msg,
		// Toast.LENGTH_LONG);

		showMsgDialog(msg);

	}

	public void showMsgDialog(final String msg) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				LoginMsgDialog dialog = new LoginMsgDialog(QRLoginConfirmActivity.this, msg,
						new LoginMsgDialog.IMsgListener() {

							@Override
							public void OnSure() {

								finish();
							}

							@Override
							public void OnCancel() {

								finish();
							}
						});

				dialog.show();
				dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			}
		});

	}

	private static class MyHandler extends Handler {
		private final WeakReference<QRLoginConfirmActivity> mActivity;

		public MyHandler(QRLoginConfirmActivity activity) {
			mActivity = new WeakReference<QRLoginConfirmActivity>(activity);
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

				// MLog.e("showTextqrlogin2", (String) msg.obj);
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

}
