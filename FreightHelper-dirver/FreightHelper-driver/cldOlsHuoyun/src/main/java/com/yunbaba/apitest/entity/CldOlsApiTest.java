/*
 * @Title CldOlsApiTest.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-9 上午10:53:36
 * @version 1.0
 */
package com.yunbaba.apitest.entity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Zhouls
 * @date 2015-3-9 上午10:53:36
 */
public class CldOlsApiTest {
	private Timer timer;

	public void init() {
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

			}
		}, 5000, 1500);
	}
}
