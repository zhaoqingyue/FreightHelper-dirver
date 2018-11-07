package com.yunbaba.freighthelper.ui.activity.task;

import android.Manifest;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.WaybillManager;
import com.yunbaba.api.trunk.bean.OnUIResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.utils.ErrCodeUtil;
import com.yunbaba.freighthelper.utils.PermissionUtil;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: WaybillInputActivity.java
 * @Prject: Freighthelper
 * @Package: com.mtq.freighthelper.ui.activity.task
 * @Description: 运单号手动输入界面
 * @author: zsx
 * @date: 2017-3-30 上午11:53:12
 * @version: V1.0
 */
public class WaybillInputActivity extends BaseButterKnifeActivity {

	@BindView(R.id.waybill_checkrecord)
	TextView mTvCheckRecord;

	@BindView(R.id.title_left_img)
	ImageView mImgBack;

	@BindView(R.id.waybill_upload)
	Button mBtnUpload;

	@BindView(R.id.waybill_edit)
	EditText mEdtNumber;

	@BindView(R.id.title_right_img)
	ImageView mImgRight;

	@BindView(R.id.title_text)
	TextView mTvTitle;

	@BindView(R.id.scan_bottom_input_viewgroup)
	ViewGroup mInputLayout;

	@BindView(R.id.scan_bottom_atuo_viewgroup)
	ViewGroup mScanLayout;

	@BindView(R.id.scan_bottom_atuo)
	ImageView mImgScan;

	@BindView(R.id.scan_bottom_input)
	ImageView mImgInput;

	private MtqDeliStoreDetail mStoreDetail;
	private MtqDeliOrderDetail mOrderDetail;

	private String mJsonStore;
	private String mJsonOrder;

	@Override
	public int getLayoutId() {

		return R.layout.activity_waybill_input;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		inidData();
		EventBus.getDefault().register(this);
	}

	private void inidData() {
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

		if (mImgBack != null) {
			mImgBack.setVisibility(View.VISIBLE);
		}

		if (mImgRight != null) {
			mImgRight.setVisibility(View.GONE);
		}

		if (mTvCheckRecord != null) {
			mTvCheckRecord.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		}

		if (mTvTitle != null) {
			mTvTitle.setText("录入货物条码");
			//mTvTitle.setText("运单号:" + mOrderDetail.cust_orderid);
		}

		if (mInputLayout != null) {
			mInputLayout.setSelected(true);
		}

		if (mScanLayout != null) {
			mScanLayout.setSelected(false);
		}
	}

	@OnClick({ R.id.waybill_upload, R.id.title_left_img, R.id.waybill_checkrecord, R.id.scan_bottom_input_viewgroup,
			R.id.scan_bottom_atuo_viewgroup })
	void onClick(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.waybill_upload:
			// 上传
			CharSequence codeBar = mEdtNumber.getText();
			if (TextUtils.isEmpty(codeBar)) {
				Toast.makeText(this, "条行码不能为空", Toast.LENGTH_SHORT).show();
			} else {
				WaitingProgressTool.showProgress(this);
				WaybillManager.getInstance().UpLoadGoodScanRecord(mStoreDetail, mOrderDetail, codeBar.toString(),
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
								Toast.makeText(WaybillInputActivity.this, text, Toast.LENGTH_LONG).show();
								WaitingProgressTool.closeshowProgress();
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

									Toast.makeText(WaybillInputActivity.this, text, Toast.LENGTH_LONG).show();
								}
								WaitingProgressTool.closeshowProgress();
							}
						});
			}

			break;

		case R.id.title_left_img:
			// 返回
			finish();
			break;

		case R.id.waybill_checkrecord:
			// 查看扫描记录
			intent.putExtra("taskId", mStoreDetail.taskId);
			intent.putExtra("cust_orderid", mStoreDetail.waybill);
			intent.setClass(this, WaybillRecordActivity.class);
			startActivity(intent);
			break;

		case R.id.scan_bottom_input_viewgroup:
			// 手动输入：
			// Toast.makeText(this, "手动输入", 100).show();
			break;
		case R.id.scan_bottom_atuo_viewgroup:
			// 扫描：
			if (PermissionUtil.isNeedPermission(this, Manifest.permission.CAMERA)) {
				Toast.makeText(this, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
				return;
			}
			intent.setClass(this, WaybillScanActivity.class);
			// intent.putExtra("storedetail", mJsonStore);
			// intent.putExtra("orderdetail", mJsonOrder);
			startActivity(intent);
			finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		EventBus.getDefault().unregister(this);
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
