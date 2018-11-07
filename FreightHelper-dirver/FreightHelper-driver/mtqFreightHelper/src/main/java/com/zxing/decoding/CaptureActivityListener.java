/*
 * @Title ScanActivityListener.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author hejie
 * @date 2015年7月16日 下午3:53:42
 * @version 1.0
 */
package com.zxing.decoding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.zxing.view.ViewfinderView;

/** 
 * 扫描二维码界面需继承的监听器
 * @author hejie
 * @date 2015年7月16日 下午3:53:42
 */
public interface CaptureActivityListener {

	/**
	 * 获取处理二维码消息的Handler
	 * @return
	 * @return Handler
	 * @author hejie
	 * @date 2015年7月16日 下午4:04:18
	 */
	public Handler getHandler();
	
	/**
	 * 获取ViewfinderView对象
	 * @return
	 * @return ViewfinderView
	 * @author hejie
	 * @date 2015年7月16日 下午4:04:43
	 */
	public ViewfinderView getViewfinderView();
	/**
	 * 获取当前页面的Activity
	 * @return
	 * @return Activity
	 * @author hejie
	 * @date 2015年7月16日 下午4:05:03
	 */
	public Activity getActivity();
	/**
	 * 解码操作
	 * @param result
	 * @param barcode
	 * @return void
	 * @author hejie
	 * @date 2015年7月16日 下午4:05:22
	 */
	public void handleDecode(Result result, Bitmap barcode);
	
}
