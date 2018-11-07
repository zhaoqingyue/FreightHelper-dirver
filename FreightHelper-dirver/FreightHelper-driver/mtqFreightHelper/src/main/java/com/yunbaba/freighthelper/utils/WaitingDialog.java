package com.yunbaba.freighthelper.utils;

import android.app.Dialog;
import android.content.Context;

public class WaitingDialog extends Dialog{
	
	private String ActivityName;
	
	public WaitingDialog(Context context, int themeResId,String ActivityName) {
		
		super(context, themeResId);
		// TODO Auto-generated constructor stub
		this.setActivityName(ActivityName);
	}

	public WaitingDialog(Context context, int themeResId) {
		super(context, themeResId);
		// TODO Auto-generated constructor stub
	}

	public WaitingDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public String getActivityName() {
		return ActivityName;
	}

	public void setActivityName(String activityName) {
		ActivityName = activityName;
	}
	


}
