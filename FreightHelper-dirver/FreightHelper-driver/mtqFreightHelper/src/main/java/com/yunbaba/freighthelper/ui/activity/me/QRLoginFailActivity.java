package com.yunbaba.freighthelper.ui.activity.me;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.ui.dialog.LoginMsgDialog;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

public class QRLoginFailActivity extends BaseButterKnifeActivity {

	
	@BindView(R.id.title_left_img)
	ImageView mImgBack;

	@BindView(R.id.title_right_img)
	ImageView mImgRight;

	@BindView(R.id.title_text)
	TextView mTvTitle;

	@BindView(R.id.tv_cancel)
	TextView tvCancel;

	Handler handler;

	@Override
	public int getLayoutId() {

		return R.layout.activity_scanlogin_fail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		
		mTvTitle.setText("扫码登录");
		// iv_titleright.setImageResource(R.drawable.icon_message);
		mImgRight.setVisibility(View.GONE);
		mImgBack.setVisibility(View.VISIBLE);
		handler = new MyHandler(this);

	}

	@OnClick({ R.id.title_left_img,R.id.tv_cancel})
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

		String msg2 = QRLoginFailActivity.this.getResources().getString(msg);

		showMsgDialog(msg2);

	}

	public void showToast(String msg) {

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

				LoginMsgDialog dialog = new LoginMsgDialog(QRLoginFailActivity.this, msg,
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
		private final WeakReference<QRLoginFailActivity> mActivity;

		public MyHandler(QRLoginFailActivity activity) {
			mActivity = new WeakReference<QRLoginFailActivity>(activity);
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

