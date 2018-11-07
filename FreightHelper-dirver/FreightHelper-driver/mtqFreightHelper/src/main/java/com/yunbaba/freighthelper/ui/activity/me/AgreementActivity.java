/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: AgreementActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: 服务协议界面
 * @author: zhaoqy
 * @date: 2017年4月10日 下午6:17:08
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.me;

import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;

public class AgreementActivity extends BaseActivity implements OnClickListener {
	
	private ImageView mBack;
	private TextView mTitle;
	private TextView mProtocol;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_agreement;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mProtocol = (TextView) findViewById(R.id.agreement_text);
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);

	}

	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.agreement_protocol);
	}

	@Override
	protected void loadData() {
		String text = getFromAssets("protocol/protocol.txt");
		mProtocol.setText(text);
	}

	@Override
	protected void updateUI() {

	}

	@Override
	protected void messageEvent(AccountEvent event) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img: {
			finish();
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 
	 * @Title: getFromAssets
	 * @Description: 读取本地文件信息(该方法可以换段落)
	 * @param fileName
	 *            : 本地文件名
	 * @return: String
	 */
	public String getFromAssets(String fileName) {
		String text = "";
		try {
			InputStream in = getResources().getAssets().open(fileName);
			int lenght = in.available(); // 获取文件的字节数
			byte[] buffer = new byte[lenght]; // 创建byte数组
			in.read(buffer); // 将文件中的数据读到byte数组中
			text = EncodingUtils.getString(buffer, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}
}
