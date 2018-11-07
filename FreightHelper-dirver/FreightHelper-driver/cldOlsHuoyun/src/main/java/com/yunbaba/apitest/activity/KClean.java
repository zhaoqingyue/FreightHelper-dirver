/*
 * @Title KClean.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.apitest.activity;

import com.yunbaba.apitest.ui.DataCleanManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


/**
 * The Class KClean.
 * 
 * @Description 清理私有区
 * @author Zhouls
 * @date 2014-12-9 上午11:38:32
 */
public class KClean extends Activity {

	/**
	 * On create.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Button cls = new Button(getApplicationContext());
		cls.setText("清空私有存储");
		cls.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				DataCleanManager.cleanSharedPreference(getApplicationContext());
				Toast.makeText(getApplicationContext(), "清理成功",
						Toast.LENGTH_SHORT).show();
			}
		});
		setContentView(cls);
	}
}
