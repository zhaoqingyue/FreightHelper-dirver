package com.yunbaba.freighthelper.ui.activity.task;

import android.Manifest;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.api.trunk.WaybillManager;
import com.yunbaba.api.trunk.bean.UploadGoodScanRecordResult;
import com.yunbaba.api.trunk.listner.OnObjectListner;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.utils.PermissionUtil;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqGoodDetail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: WayBillNumberActivity.java
 * @Prject: Freighthelper
 * @Package: com.mtq.freighthelper.ui.activity.task
 * @Description: 运单号
 * @author: zsx
 * @date: 2017-3-30 上午10:34:14
 * @version: V1.0
 */
public class WayBillNumberActivity extends BaseButterKnifeActivity {

	@BindView(R.id.waybill_autoscan)
	Button mBtnAutoScan;
	
	@BindView(R.id.waybill_scan_num)
	TextView mTvScanNum;
	
	@BindView(R.id.waybill_totalnum)
	TextView mTvTotalNum;
	
	@BindView(R.id.waybill_progess)
	ProgressBar mProgressBar;
	
	@BindView(R.id.waybill_checkrecord)
	TextView mTvCheckRecord;
	
	@BindView(R.id.title_left_img)
	ImageView mImgBack;
	
	@BindView(R.id.title_text)
	TextView mTvTitle;
	
	private MtqDeliStoreDetail mStoreDetail;
	private MtqDeliOrderDetail mOrderDetail;
	
	private String mJsonStore;
	private String mJsonOrder;
	
	private int mScanNum,mTotalNum;
	
	private int mRequsetCode = 0;
	
	@Override
	public int getLayoutId() {

		return R.layout.activity_waybill_number;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initData();
		EventBus.getDefault().register(this);
	}
	
	private void initData(){
//		if (getIntent().hasExtra("storedetail")) {
//			try {
//				mJsonStore = getIntent().getStringExtra("storedetail");
//				if (!TextUtils.isEmpty(mJsonStore))
//					mStoreDetail = GsonTool.getInstance().fromJson(mJsonStore, MtqDeliStoreDetail.class);
//			} catch (Exception e) {
//				finish();
//			}
//		}
//		if (getIntent().hasExtra("orderdetail")) {
//			try {
//				mJsonOrder = getIntent().getStringExtra("orderdetail");
//				if (!TextUtils.isEmpty(mJsonOrder))
//					mOrderDetail = GsonTool.getInstance().fromJson(mJsonOrder, MtqDeliOrderDetail.class);
//			} catch (Exception e) {
//				finish();
//			}
//		}
		
		mStoreDetail = WaybillManager.getInstance().getmStoreDetail();
		mOrderDetail = WaybillManager.getInstance().getmOrderDetail();
		
		if (mOrderDetail.goods == null){
			Toast.makeText(this, "goods 为空", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		setScanProgress();
		
		if (mTvCheckRecord != null){
			mTvCheckRecord.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		}
		
		if (mImgBack != null){
			mImgBack.setVisibility(View.VISIBLE);
		}
		
		if (mTvTitle != null){
			mTvTitle.setText("扫描货物条码");
			
			//mTvTitle.setText("运单号:" + mOrderDetail.cust_orderid);
		}
	}

	private void setScanProgress(){
		mScanNum = 0;
		mTotalNum = 0;
		Iterator<MtqGoodDetail> iter = mOrderDetail.goods.iterator();
		while(iter.hasNext()){
			MtqGoodDetail gooddetail = iter.next();
			int singleTotal = Integer.valueOf(gooddetail.amount).intValue();
			int singleScan = Integer.valueOf(gooddetail.scan_cnt).intValue();
			mTotalNum += singleTotal;
			mScanNum += singleScan;
			
			createRecord(gooddetail);
		}
		
		WaybillManager.getInstance().setmScanNum(mScanNum);
		WaybillManager.getInstance().setmTotalNum(mTotalNum);
		
		if (mTvScanNum != null){
			mTvScanNum.setText(""+mScanNum);
		}
		if (mTvTotalNum != null){
			mTvTotalNum.setText("/"+mTotalNum);
		}
		if (mProgressBar != null){
			mProgressBar.setMax(mTotalNum);
			mProgressBar.setProgress(mScanNum);
		}
	}
	
	
	private void createRecord(final MtqGoodDetail gooddetail){
		final String key = mStoreDetail.taskId + mStoreDetail.waybill + gooddetail.bar_code;
		WaybillManager.getInstance().getOneScanRecordByKeyFromDB(key, new OnObjectListner<UploadGoodScanRecordResult>() {
			@Override
			public void onResult(UploadGoodScanRecordResult res) {
				UploadGoodScanRecordResult result = res;
				if (result == null){
					result = new UploadGoodScanRecordResult();
					result.taskAndbarCode = key;
					result.address = mStoreDetail.storeaddr;
					result.amount = gooddetail.amount;
					result.name = gooddetail.name;
					result.bar_code = gooddetail.bar_code;
					result.scan_cnt = gooddetail.scan_cnt;
					result.searchKey = mStoreDetail.taskId + mStoreDetail.waybill;
					result.cust_order_id = mStoreDetail.waybill;
					WaybillManager.getInstance().saveScanRecordToBD(result);
				}
			}
		});

	}
	
	
	
	@Override
	protected void onResume() {

		if (WaybillManager.getInstance().isSuccessUpload()){
			WaybillManager.getInstance().setSuccessUpload(false);
			setScanProgress();
		}
		super.onResume();
		
	}

	@OnClick({R.id.waybill_autoscan,R.id.waybill_inputscan,R.id.waybill_checkrecord,R.id.title_left_img})
	void onclick(View view){
		Intent intent = new Intent();
//		intent.putExtra("storedetail", mJsonStore);
//		intent.putExtra("orderdetail", mJsonOrder);
		
		switch(view.getId()){
		case R.id.waybill_autoscan:
			//扫描签收
			if (PermissionUtil.isNeedPermission(this,Manifest.permission.CAMERA)) {
				Toast.makeText(this, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
				return;
			}
			intent.setClass(this, WaybillScanActivity.class);
			startActivity(intent);
			break;
		case R.id.waybill_inputscan:
			//手动签收
			intent.setClass(this, WaybillInputActivity.class);
			startActivity(intent);
			break;
		case R.id.waybill_checkrecord:
			//查看扫描记录
			intent.putExtra("taskId", mStoreDetail.taskId);
			intent.putExtra("cust_orderid", mStoreDetail.waybill);
			intent.setClass(this, WaybillRecordActivity.class);
			startActivity(intent);
			break;
		case R.id.title_left_img:
			//返回
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
