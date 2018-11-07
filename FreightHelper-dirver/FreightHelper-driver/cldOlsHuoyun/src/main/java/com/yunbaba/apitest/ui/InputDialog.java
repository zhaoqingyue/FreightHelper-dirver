/*
 * @Title InputDialog.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.apitest.ui;

import com.yunbaba.apitest.ui.InputView.AfterListener;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.View;


/**
 * The Class InputDialog.
 *
 * @Description 参数输入框
 * @author  Zhouls
 * @date  2014-11-14 下午1:12:06
 */
public class InputDialog {
	
	/** The dialog. */
	private AlertDialog dialog;

	/**
	 * Instantiates a new input dialog.
	 *
	 * @param context the context
	 * @param listener the listener
	 */
	public InputDialog(final Context context, AfterListener listener) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("参数");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		final InputView inputView = new InputView(context, listener);
		View view = inputView.view;
		builder.setView(view);
		dialog = builder.create();
	}

	/**
	 * Show.
	 */
	public void show() {
		dialog.show();
	}
}
