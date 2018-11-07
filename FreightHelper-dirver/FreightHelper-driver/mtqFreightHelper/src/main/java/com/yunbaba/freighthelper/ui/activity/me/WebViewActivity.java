/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: WebViewActivity.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.activity.me
 * @Description: WebView(官方网站)界面
 * @author: zhaoqy
 * @date: 2017年4月6日 下午6:07:08
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.me;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;

public class WebViewActivity extends BaseActivity implements OnClickListener {

	private ImageView mBack;
	private TextView mTitle;
	private WebView mWebView;
	private ProgressBar mBar;
	private MyTimer mTimer;
	private String mUrl = "";
	private int mProgress = 0;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_webview;
	}

	@Override
	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mWebView = (WebView) findViewById(R.id.webview_webview);
		mBar = (ProgressBar) findViewById(R.id.webview_progress);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			mWebView.getClass().getMethod("onResume")
					.invoke(mWebView, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			mWebView.getClass().getMethod("onPause")
					.invoke(mWebView, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setListener() {
		mBack.setOnClickListener(this);
	}

	@SuppressLint("SetJavaScriptEnabled") 
	@Override
	protected void initData() {
		mBack.setVisibility(View.VISIBLE);
		mTitle.setText(R.string.about_web);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mUrl = bundle.getString("URL");
		}
		if (!TextUtils.isEmpty(mUrl)) {
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.setWebViewClient(new WeiboWebViewClient());
			mWebView.setWebChromeClient(new WebChromeClient());
			mWebView.loadUrl(mUrl);
		}
	}

	@Override
	protected void loadData() {

	}

	@Override
	protected void updateUI() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img: {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				finish();
			}
			break;
		}
		default:
			break;
		}
	}

	@Override
	protected void messageEvent(AccountEvent event) {

	}

	private class WeiboWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (mTimer == null) {
				mTimer = new MyTimer(15000, 50);
			}
			mTimer.start();
			mBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (mTimer == null) {
				mTimer = new MyTimer(15000, 50);
			}
			mTimer.cancel();
			mProgress = 0;
			mBar.setProgress(100);
			mBar.setVisibility(View.GONE);
		}
	}

	/* 定义一个倒计时的内部类 */
	private class MyTimer extends CountDownTimer {
		public MyTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mProgress = 100;
			mBar.setVisibility(View.GONE);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (mProgress == 100) {
				mBar.setVisibility(View.GONE);
			} else {
				mBar.setProgress(mProgress++);
			}
		}
	}
}
