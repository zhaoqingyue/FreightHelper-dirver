/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: SelectPopup.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.customview
 * @Description: 头像选择框
 * @author: zhaoqy
 * @date: 2017年4月5日 上午11:26:23
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.customview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;

public class SelectPopup extends PopupWindow implements OnClickListener {

	private OnPopupListener mListener;
	private View mPopView;

	public SelectPopup(Context context, OnPopupListener listener) {
		super(context);
		mListener = listener;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopView = inflater.inflate(R.layout.dialog_setphoto, null);
		setContentView(mPopView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);

		TextView choose_photo = (TextView) mPopView
				.findViewById(R.id.setphoto_choose_photo);
		TextView take_photo = (TextView) mPopView
				.findViewById(R.id.setphoto_take_photo);
		TextView cancel = (TextView) mPopView.findViewById(R.id.setphoto_cancel);
		choose_photo.setOnClickListener(this);
		take_photo.setOnClickListener(this);
		cancel.setOnClickListener(this);

		// 设置SelectPicPopupWindow弹出窗体可点击
		setFocusable(true);
		// 点击外面的控件也可以使得PopUpWindow dimiss
		setOutsideTouchable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		setAnimationStyle(R.style.SelectPopupAnimation);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		setBackgroundDrawable(dw);// 半透明颜色
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setphoto_choose_photo: {
			mListener.onChoosePhoto();
			break;
		}
		case R.id.setphoto_take_photo: {
			mListener.onTakePhoto();
			break;
		}
		case R.id.setphoto_cancel: {
			mListener.onCancel();
			break;
		}
		default:
			break;
		}
		dismiss();
	}

	public interface OnPopupListener {
		/**
		 * @Description: 拍照
		 */
		public void onTakePhoto();

		/**
		 * @Description: 从相册中获取
		 */
		public void onChoosePhoto();

		/**
		 * @Description: 取消
		 */
		public void onCancel();
	}
}
