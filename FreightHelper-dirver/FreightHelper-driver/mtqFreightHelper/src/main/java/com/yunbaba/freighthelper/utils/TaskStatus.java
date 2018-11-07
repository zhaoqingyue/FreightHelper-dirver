package com.yunbaba.freighthelper.utils;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: TaskStatus.java
 * @Prject: Freighthelper
 * @Package: com.mtq.freighthelper.utils
 * @Description: 任务的状态
 * @author: zsx
 * @date: 2017-4-24 上午9:52:26
 * @version: V1.0
 */
public class TaskStatus {
	/**  等待送货  **/
	public final static int WAITTING_DELIVER = 0;  
	
	/**	正在配送中 **/
	public final static int DELIVERRING = 1;  
	
	/**	已完成配送*/
	public final static int DELIVERRED = 2;  
	
	/**暂停送货*/	
	public final static int PAUSE_DELIVER = 3;  
}
