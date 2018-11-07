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

public class PopupTravel extends PopupWindow implements OnClickListener {

	private OnPopupListener mListener;
	private View mPopView;

	public PopupTravel(Context context, String title, OnPopupListener listener) {
		super(context);
		mListener = listener;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopView = inflater.inflate(R.layout.dialog_travel, null);
		setContentView(mPopView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);

		TextView text = (TextView) mPopView
				.findViewById(R.id.travel_title);
		text.setOnClickListener(this);
		text.setText(title);

		// 设置SelectPicPopupWindow弹出窗体可点击
		setFocusable(true);
		// 点击外面的控件也可以使得PopUpWindow dimiss
		setOutsideTouchable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		//setAnimationStyle(R.style.SelectPopupAnimation);
		
		// 全透明
		ColorDrawable dw = new ColorDrawable(000000);
		setBackgroundDrawable(dw);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.travel_title: {
			mListener.onClick();
			break;
		}
		default:
			break;
		}
		dismiss();
	}

	public interface OnPopupListener {
		/**
		 * 点击事件
		 */
		public void onClick();
	}
}
