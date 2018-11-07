/*
 * @Title KMessage.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.apitest.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import com.cld.log.CldLog;
import com.yunbaba.apitest.listener.MyKMlistener;
import com.yunbaba.apitest.ui.DalInput;
import com.yunbaba.apitest.ui.InputDialog;
import com.yunbaba.apitest.ui.MyListAdapter;
import com.yunbaba.apitest.ui.MyListItem;
import com.yunbaba.apitest.ui.InputView.AfterListener;
import com.yunbaba.ols.R;
import com.yunbaba.ols.api.CldKMessageAPI;
import com.yunbaba.ols.api.CldKServiceAPI;
import com.yunbaba.ols.api.CldKMessageAPI.IRecEggMsg;
import com.yunbaba.ols.bll.CldBllUtil;
import com.yunbaba.ols.bll.CldKAccount;
import com.yunbaba.ols.dal.CldDalKAccount;
import com.yunbaba.ols.dal.CldDalKMessage;
import com.yunbaba.ols.sap.bean.CldSapKMParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.SharePoiParm;


/**
 * The Class KMessage.
 * 
 * @Description 消息系统
 * @author Zhouls
 * @date 2014-11-4 下午2:57:14
 */
public class KMessage extends Activity {

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
		setContentView(R.layout.kmessage);
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
		mListView = (ListView) findViewById(R.id.list_km);
		mButtonList = new ArrayList<MyListItem>();
		CldKMessageAPI.getInstance().setCldKMessageListener(new MyKMlistener());
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
		item = new MyListItem("下拉消息", new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						CldKServiceAPI.getInstance().recMessage(
								CldKServiceAPI.getInstance().getDuid(),
								CldKServiceAPI.getInstance().getApptype(),
								"6.4", new ArrayList<CldSapKMParm>(),
								CldKServiceAPI.getInstance().getKuid(), 440300,
								100001, 100023,
								CldBllUtil.getInstance().getBussinessid(),
								CldKServiceAPI.getInstance().getSession(),
								CldBllUtil.getInstance().getAppid());
					}
				}).start();
			}
		});
		mButtonList.add(item);
		item = new MyListItem("分享消息(target,content)", new OnClickListener() {

			@Override
			public void onClick(View v) {
				new InputDialog(KMessage.this, new AfterListener() {

					@Override
					public void toDo() {
						CldKAccount.getInstance().isRegisterUser(
								DalInput.getInstance().getStrEd1());
						SharePoiParm poi = new SharePoiParm(DalInput
								.getInstance().getStrEd2(), "12,15",
								CldDalKAccount.getInstance().getKuidRegUser());
						CldKMessageAPI.getInstance().sendSharePoiMsg(poi, 1);
					}

					@Override
					public void stopDo() {


					}
				}).show();
			}
		});
		mButtonList.add(item);
		item = new MyListItem("接收最新消息历史", new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {

					@Override
					public void run() {

						CldKMessageAPI.getInstance().recLastestMsgHistory(
								"1,2,3,4,11,15");
					}
				}).start();
			}
		});
		mButtonList.add(item);
		item = new MyListItem("下拉消息", new OnClickListener() {

			@Override
			public void onClick(View v) {
				CldKMessageAPI.getInstance().recNewMsgHistory("1,2,3,4,11,15",
						0, 10, 5380, 1415239181);
			}
		});
		mButtonList.add(item);
		item = new MyListItem("上拉消息", new OnClickListener() {

			@Override
			public void onClick(View v) {
				CldKMessageAPI.getInstance().recOldMsgHistory("1,2,3,4,11,15",
						0, 10, 5380, 1415239181);
			}
		});
		mButtonList.add(item);
		item = new MyListItem("更新消息阅读状态", new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<String> list = new ArrayList<String>();
				list.add("538111,1415239141,1");
				CldKMessageAPI.getInstance().upMsgReadStatus(list);
			}
		});
		mButtonList.add(item);
		item = new MyListItem("第一次获取彩蛋列表", new OnClickListener() {
			@Override
			public void onClick(View v) {
				CldKMessageAPI.getInstance().dropAreaEggs(true);
			}
		});
		mButtonList.add(item);
		item = new MyListItem("彩蛋列表定时器", new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {

						CldKMessageAPI.getInstance().dropAreaEggs(false);
					}
				}, 6000, 30000);
			}
		});
		mButtonList.add(item);
		item = new MyListItem("是否踩蛋定时器", new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {

						CldKMessageAPI.getInstance().isInEggsArea(100, 100);
					}
				}, 1000, 1000);
			}
		});
		mButtonList.add(item);
		item = new MyListItem("读彩蛋列表定时器", new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {

						for (int i = 0; i < CldDalKMessage.getInstance()
								.getListEggs().size(); i++) {
							CldLog.d("1", CldDalKMessage.getInstance()
									.getListEggs().size()
									+ "");
							if (i == 5) {
								CldLog.d("2", CldDalKMessage.getInstance()
										.getListEggs().get(i).getMaxx()
										+ ","
										+ CldDalKMessage.getInstance()
												.getListEggs().get(i).getMaxy()
										+ "||" + i);
							}
						}
					}
				}, 1000, 1000);

			}
		});
		mButtonList.add(item);
		item = new MyListItem("接收彩蛋消息", new OnClickListener() {
			@Override
			public void onClick(View v) {
				CldKMessageAPI.getInstance().recEggsMsg(-1, 12, 25,
						new IRecEggMsg() {
							@Override
							public void onRecEggsMsg(int errCode,
									List<CldSapKMParm> list) {
								CldLog.d("ols", list.size() + "");
							}
						});
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
