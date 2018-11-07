package com.yunbaba.freighthelper.utils;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

public class ActivityStateUtil {
	
	
	
	public static TopActivityInfo getTopActivityInfo(Context context) {  
	    ActivityManager manager = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE));  
	    TopActivityInfo info = new TopActivityInfo();  
	    if (Build.VERSION.SDK_INT >= 21) {  
	        List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();  
	        ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);  
	        if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {  
	            info.packageName = topAppProcess.processName;  
	            info.topActivityName = "";  
	        }  
	    } else {  
	        //getRunningTasks() is deprecated since API Level 21 (Android 5.0)  
	        List localList = manager.getRunningTasks(1);  
	        ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo)localList.get(0);  
	        info.packageName = localRunningTaskInfo.topActivity.getPackageName();  
	        info.topActivityName = localRunningTaskInfo.topActivity.getClassName();  
	    }  
	    return info;  
	}  
	
	public static class TopActivityInfo {  
	    public String packageName = "";  
	    public String topActivityName = "";  
	}  

}
