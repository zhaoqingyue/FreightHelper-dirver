package com.yunbaba.freighthelper.ui.activity.task;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.nv.util.CldNaviUtil;
import com.google.zxing.Result;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.listner.OnObjectListner;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseScanAcivity;
import com.yunbaba.freighthelper.ui.customview.ScanToast;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: ScanSearchTaskActivity.java
 * @Prject: Freighthelper
 * @Package: com.mtq.freighthelper.ui.activity.task
 * @Description: TODO
 * @author: zsx
 * @date: 2017-4-2 下午5:57:39
 * @version: V1.0
 */
public class ScanSearchTaskActivity extends BaseScanAcivity {
	@BindView(R.id.title_left_img)
	ImageView mImgBack;
	
	@BindView(R.id.title_right_img)
	ImageView mImgRight;
	
	@BindView(R.id.title_text)
	TextView mTvTitle;
	
	
	@Override
	public int getLayoutId() {

		return R.layout.activity_task_search_scan;
	}

	
	@Override
	public void initData() {

		mOpenFlash = false;
		mImgBack.setVisibility(View.VISIBLE);
		mImgRight.setVisibility(View.VISIBLE);
		mTvTitle.setText("扫码查询");
		
		updateFlashView(mOpenFlash);
	}

	
	@OnClick({R.id.title_left_img,R.id.title_right_img})
	void onClick(View view){
		switch(view.getId()){
		case R.id.title_left_img:
			//返回
			finish();
			break;
		case R.id.title_right_img:
			//闪光灯
			mOpenFlash = !mOpenFlash;
			operateFlash(mOpenFlash);
			updateFlashView(mOpenFlash);
			break;
		}
	}
	
	private void operateFlash(boolean open){
		if (open){
			openFlash();
		}else{
			closeFlash();
		}
	}

	//扫描结束后跳转
	@Override
	public void handleDecode(Result result, Bitmap barcode) {

		super.handleDecode(result, barcode);
		
		if (!CldNaviUtil.isNetConnected()) {
			// 无网络不处理
			Toast.makeText(ScanSearchTaskActivity.this, "当前网络不可用，请检查网络设置。", Toast.LENGTH_LONG).show();
			return;
		}
		
		String resultString = result.getText();
		

		
		 SearchTaskDetial(resultString, new OnObjectListner<MtqDeliStoreDetail>() {
			 @Override
			 public void onResult(MtqDeliStoreDetail res) {



				final MtqDeliStoreDetail storedetail = res;
				 runOnUiThread(new Runnable() {
					 @Override
					 public void run() {
						 boolean isFind = false;

						 if (storedetail != null){
							 isFind = true;
						 }

						 if (isFind){
							 Intent intent = new Intent(ScanSearchTaskActivity.this,TaskPointDetailActivity.class);
							 //添加storedetail
							 String str = GsonTool.getInstance().toJson(storedetail);
							 intent.putExtra("storedetail", str);

							 //添加taskid
							 intent.putExtra("taskid", storedetail.taskId);

							 MtqDeliOrderDetail orderdetail = TaskOperator.getInstance().GetorderDetailFromDB(storedetail.taskId,storedetail.waybill);

							 if(orderdetail != null){
								 String str2 = GsonTool.getInstance().toJson(orderdetail);
								 intent.putExtra("orderdetail", str2);
							 }
							 startActivity(intent);
						 }else{
//			Toast.makeText(this, "很抱歉，未查询到该运货单信息", Toast.LENGTH_LONG).show();
//			finish();
							 restartScan();
							 ScanToast.makeText(ScanSearchTaskActivity.this, "很抱歉，未查询到该运货单信息", Toast.LENGTH_LONG).show();
						 }
					 }
				 });
			 }
		 });

		
	}
	
	private void SearchTaskDetial(String key,final OnObjectListner<MtqDeliStoreDetail> listner){
		TaskOperator.getInstance().GetStoreDetailByCustorderidFromDB(key,listner);

	}
	
	private void updateFlashView(boolean show){
		if (show){
			mImgRight.setImageResource(R.drawable.ic_scan_no_flashlight);
		}else{
			mImgRight.setImageResource(R.drawable.ic_scan_flashlight);
		}
	}

}
