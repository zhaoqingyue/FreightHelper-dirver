package com.yunbaba.freighthelper.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class SystemBarHintUtil {
	
	public static void setSystemBarHint(Activity activity){
	
//		
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  
//			//透明状态栏  
//	        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
//	        //透明导航栏  
//	        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); 
//	        
//	        
//	        
//			}
		
		 ViewGroup contentFrameLayout = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
	     View parentView = contentFrameLayout.getChildAt(0);
	     if (parentView != null && Build.VERSION.SDK_INT >= 14) {
	         parentView.setFitsSystemWindows(true);
	     }

//		ImmersionBar mImmersionBar = ImmersionBar.with(activity);
//		if (ImmersionBar.isSupportStatusBarDarkFont()) {
//			// 状态栏字体颜色为深色(android6.0以上或者miuiv6以上或者flymeOS4+)
//			mImmersionBar.statusBarDarkFont(true);
//		}
//		mImmersionBar.init();
	}

}
