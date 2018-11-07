package com.yunbaba.api.trunk;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import com.yunbaba.api.trunk.TaskOperator.OnDialogListener;
import com.yunbaba.freighthelper.bean.eventbus.UpdateTaskStatusEvent;
import com.yunbaba.freighthelper.ui.customview.TaskDialoginNavigation;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: NavigateTaskOperator.java
 * @Prject: Freighthelper
 * @Package: com.mtq.api.trunk
 * @Description: 导航界面运单操作
 * @author: zsx
 * @date: 2017-5-15 下午12:05:15
 * @version: V1.0
 */
public class NavigateTaskOperator {
	private static NavigateTaskOperator mNavigateTaskOperator;
	
	private MtqDeliTaskDetail detail;
	private MtqDeliStoreDetail mStoreDetail;
	private MtqDeliOrderDetail mOrderDetail;
	private int index;
	private int mType;
	
	// 对话框对象
	private TaskDialoginNavigation  mPopUpDialog;
	
	public static NavigateTaskOperator getInstance(){
		synchronized (NavigateTaskOperator.class) {
			if (mNavigateTaskOperator == null){
				synchronized (NavigateTaskOperator.class) {
					mNavigateTaskOperator = new NavigateTaskOperator();
				}
			}
		}
		return mNavigateTaskOperator;
	}

	public MtqDeliTaskDetail getDetail() {
		return detail;
	}

	public void setDetail(MtqDeliTaskDetail detail) {
		this.detail = detail;
	}

	public MtqDeliStoreDetail getmStoreDetail() {
		return mStoreDetail;
	}

	public void setmStoreDetail(MtqDeliStoreDetail mStoreDetail) {
		this.mStoreDetail = mStoreDetail;
	}

	public MtqDeliOrderDetail getmOrderDetail() {
		return mOrderDetail;
	}

	public void setmOrderDetail(MtqDeliOrderDetail mOrderDetail) {
		this.mOrderDetail = mOrderDetail;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	
	public synchronized void showTaskStatusChangeDialog(Context context, final UpdateTaskStatusEvent event,
			int type ,final OnDialogListener listener) {
		
		setPopUpDialog(context);

		mPopUpDialog.show();
		
		mType = type;
		setspText(type);
		
		mPopUpDialog.setDialogType(type,mStoreDetail);
		mPopUpDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mPopUpDialog.tvLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (listener != null){
					listener.OnClickLeft(event);
					listener.OnDialogDismiss();
				}
				mPopUpDialog.dismiss();
			}
		});
		mPopUpDialog.tvRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (listener != null){
					listener.OnClickRight(event);
					listener.OnDialogDismiss();
				}

				mPopUpDialog.dismiss();
			}
		});
		
	}

	
	private void setPopUpDialog(Context context) {

		if (mPopUpDialog != null && context.hashCode() != mPopUpDialog.getContext().hashCode()) {
			mPopUpDialog = null;
		}
		if (mPopUpDialog == null)
			mPopUpDialog = new TaskDialoginNavigation(context);
	}
	
	private void setspText(int type){
		MtqDeliTask deliTask = TaskOperator.getInstance().getmCurrentTask();
		
		mPopUpDialog.setSpText(deliTask,mStoreDetail, mOrderDetail,type);
	}
}
