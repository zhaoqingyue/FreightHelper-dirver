/*
 * @Title KAkeyCall.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.apitest.activity;

import java.util.ArrayList;
import java.util.List;
import com.cld.log.CldLog;import com.yunbaba.apitest.listener.MyKClistener;
import com.yunbaba.apitest.ui.DalInput;
import com.yunbaba.apitest.ui.InputDialog;
import com.yunbaba.apitest.ui.MyListAdapter;
import com.yunbaba.apitest.ui.MyListItem;
import com.yunbaba.apitest.ui.InputView.AfterListener;
import com.yunbaba.ols.R;
import com.yunbaba.ols.api.CldKCallNaviAPI;
import com.yunbaba.ols.api.CldKConfigAPI;
import com.yunbaba.ols.bll.CldKConfig.ConfigDomainType;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;


/**
 * The Class KAkeyCall.
 * 
 * @Description 一键通
 * @author Zhouls
 * @date 2014-11-4 下午2:57:56
 */
public class KAkeyCall extends Activity {

	/** The m list view. */
	private ListView mListView;

	/** The m button list. */
	private List<MyListItem> mButtonList;

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
		setContentView(R.layout.kakeycall);
		initData();
		initControls();
		bindData();
	}

	/**
	 * Inits the data.
	 * 
	 * @return void
	 * @Description 初始化数据
	 * @author Zhouls
	 * @date 2014-11-4 下午2:17:51
	 */
	public void initData() {
		mListView = (ListView) findViewById(R.id.list_kakey);
		mButtonList = new ArrayList<MyListItem>();
		CldKCallNaviAPI.getInstance().setCldKCallNaviListener(
				new MyKClistener());
	}

	/**
	 * Inits the controls.
	 * 
	 * @return void
	 * @Description 初始化控件
	 * @author Zhouls
	 * @date 2014-11-4 下午2:18:00
	 */
	public void initControls() {
		MyListItem item;
		item = new MyListItem("获取绑定手机列表", new OnClickListener() {

			@Override
			public void onClick(View v) {
				CldKCallNaviAPI.getInstance().getBindMobile();
			}
		});
		mButtonList.add(item);
		item = new MyListItem("获取绑定手机验证码", new OnClickListener() {

			@Override
			public void onClick(View v) {
				new InputDialog(KAkeyCall.this, new AfterListener() {
					@Override
					public void toDo() {
						Toast.makeText(getApplicationContext(),
								DalInput.getInstance().getStrEd1(),
								Toast.LENGTH_SHORT).show();
						CldKCallNaviAPI.getInstance().getIdentifycode(
								DalInput.getInstance().getStrEd1());
					}

					@Override
					public void stopDo() {


					}
				}).show();

			}
		});
		mButtonList.add(item);
		item = new MyListItem("绑定其他手机(idcode,mobile,nmobile)",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new InputDialog(KAkeyCall.this, new AfterListener() {
							@Override
							public void toDo() {
								Toast.makeText(getApplicationContext(),
										DalInput.getInstance().getStrEd1(),
										Toast.LENGTH_SHORT).show();
								// if
								// (CldDalKCallNavi.getInstance().getMobiles()
								// .size() > 0) {
								// CldKCallNaviAPI.getInstance().bindToMobile(
								// DalInput.getInstance().getStrEd1(),
								// DalInput.getInstance().getStrEd2(),
								// CldDalKCallNavi.getInstance()
								// .getMobiles().get(0));
								// } else {
								CldKCallNaviAPI.getInstance().bindToMobile(
										DalInput.getInstance().getStrEd1(),
										DalInput.getInstance().getStrEd2(),
										null);
								// }

							}

							@Override
							public void stopDo() {


							}
						}).show();
					}
				});
		mButtonList.add(item);
		item = new MyListItem("删除一键通号码", new OnClickListener() {

			@Override
			public void onClick(View v) {
				new InputDialog(KAkeyCall.this, new AfterListener() {
					@Override
					public void toDo() {
						Toast.makeText(getApplicationContext(),
								DalInput.getInstance().getStrEd1(),
								Toast.LENGTH_SHORT).show();
						CldKCallNaviAPI.getInstance().delBindMobile(
								DalInput.getInstance().getStrEd1());
					}

					@Override
					public void stopDo() {


					}
				}).show();

			}
		});
		mButtonList.add(item);
		item = new MyListItem("注册一键通", new OnClickListener() {
			@Override
			public void onClick(View v) {
				CldKCallNaviAPI.getInstance().registerByKuid();

			}
		});
		mButtonList.add(item);
		item = new MyListItem("上报位置", new OnClickListener() {
			@Override
			public void onClick(View v) {
				CldKCallNaviAPI.getInstance().upLocation(
						CldKCallNaviAPI.getInstance().getPptMobile(),
						22.537693, 114.018303);

			}
		});
		mButtonList.add(item);
		item = new MyListItem("拨打电话", new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						String phoneUri = "tel:"
								+ CldKConfigAPI.getInstance().getSvrDomain(
										ConfigDomainType.PHONE_HDSC);
						Uri uri = Uri.parse(phoneUri);
						Intent intent = new Intent(Intent.ACTION_CALL, uri);
						startActivity(intent);
					}
				}).start();
			}
		});
		mButtonList.add(item);
		item = new MyListItem("获取一键通消息", new OnClickListener() {

			@Override
			public void onClick(View v) {
				CldKCallNaviAPI.getInstance().recPptMsg(
						CldKCallNaviAPI.getInstance().getPptMobile(),
						22.537693, 114.018303, false);
			}
		});
		mButtonList.add(item);
		item = new MyListItem("获取一键通手机号列表", new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < CldKCallNaviAPI.getInstance()
						.getPptMobileList().size(); i++) {
					CldLog.d("ols", CldKCallNaviAPI.getInstance()
							.getPptMobileList().get(i));
				}
			}
		});
		mButtonList.add(item);
	}

	/**
	 * Bind data.
	 * 
	 * @return void
	 * @Description 绑定数据
	 * @author Zhouls
	 * @date 2014-11-4 下午2:21:16
	 */
	public void bindData() {
		mListView.setAdapter(new MyListAdapter(getApplicationContext(),
				mButtonList));
	}
}
