/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: AppManager.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.manager
 * @Description: APP管理类  
 * @author: zhaoqy
 * @date: 2017年4月12日 下午4:28:10
 * @version: V1.0
 */

package com.yunbaba.freighthelper.manager;

public class AppManager {

//	private static Stack<WeakReference<Activity>> activityStack;
//	private static AppManager instance;
//
//	private AppManager() {
//	}
//
//	/**
//	 * 单一实例
//	 */
//	public static AppManager getInstance() {
//		if (instance == null) {
//			instance = new AppManager();
//		}
//		return instance;
//	}
//
//	/**
//	 * 添加Activity到堆栈
//	 */
//	public void addActivity(Activity activity) {
//		if (activityStack == null) {
//			activityStack = new Stack<WeakReference<Activity>>();
//		}
//		activityStack.add(activity);
//	}
//
//	/**
//	 * 获取当前Activity（堆栈中最后一个压入的）
//	 */
//	public Activity getCurActivity() {
//		WeakReference<Activity> activity = activityStack.lastElement();
//		return activity.get();
//	}
//
//	/**
//	 * 结束当前Activity（堆栈中最后一个压入的）
//	 */
//	public void finishActivity() {
//		WeakReference<Activity> activity = activityStack.lastElement();
//		finishActivity(activity.get());
//	}
//
//	/**
//	 * 结束指定的Activity
//	 */
//	public void finishActivity(WeakReference<Activity> activity) {
//		if (activity != null) {
//			activityStack.remove(activity);
//			activity.get().finish();
//			activity = null;
//		}
//	}
//
//	/**
//	 * 结束指定类名的Activity
//	 */
//	public void finishActivity(Class<?> cls) {
//		for (Activity activity : activityStack) {
//			if (activity.getClass().equals(cls)) {
//				finishActivity(activity);
//			}
//		}
//	}
//
//	/**
//	 * 结束所有Activity
//	 */
//	public void finishAllActivity() {
//
//		if (activityStack != null) {
//
//			int len = activityStack.size();
//			for (int i = 0; i < len; i++) {
//				Activity activity = activityStack.get(i);
//				if (activity != null) {
//					activity.finish();
//				}
//			}
//			activityStack.clear();
//		}
//	}
//
//	/**
//	 * 退出应用程序
//	 */
//	public void exitApp(Context context) {
//		try {
//			finishAllActivity();
//			System.exit(0);
//			android.os.Process.killProcess(android.os.Process.myPid());
//		} catch (Exception e) {
//		}
//	}
}
