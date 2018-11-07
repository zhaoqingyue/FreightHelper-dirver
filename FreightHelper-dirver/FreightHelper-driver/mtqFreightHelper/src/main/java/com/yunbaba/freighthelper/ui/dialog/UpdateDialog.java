package com.yunbaba.freighthelper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;

public class UpdateDialog extends Dialog {
	
	private TextView mPercent;
	private ProgressBar mProgress;
	
	public UpdateDialog(Context context) {
		super(context, R.style.dialog_style);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCanceledOnTouchOutside(false);
		getWindow().setContentView(R.layout.dialog_update);

		initViews();
	}

	private void initViews() {
		mPercent = (TextView) findViewById(R.id.update_percent);
		mProgress = (ProgressBar) findViewById(R.id.update_progressbar);
		//mPercent.setText("0%");
	}
	
	public void setProgressBar(int size) {
		
		//MLog.e("updatedialog", "set progress"+size);
		mPercent.setText("(" + size + "%)");
		mProgress.setProgress(size);
		mProgress.setMax(100);
	}
}
