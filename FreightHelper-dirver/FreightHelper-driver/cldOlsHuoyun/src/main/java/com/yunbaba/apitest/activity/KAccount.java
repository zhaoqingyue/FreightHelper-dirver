/*
 * @Title KAccount.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.apitest.activity;

import java.util.ArrayList;
import java.util.List;
import com.cld.log.CldLog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;

import com.yunbaba.apitest.listener.MyKAlistener;
import com.yunbaba.apitest.ui.DalInput;
import com.yunbaba.apitest.ui.InputDialog;
import com.yunbaba.apitest.ui.MyListAdapter;
import com.yunbaba.apitest.ui.MyListItem;
import com.yunbaba.apitest.ui.InputView.AfterListener;
import com.yunbaba.ols.R;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.api.CldKConfigAPI;
import com.yunbaba.ols.api.CldKAccountAPI.CldThirdLoginType;
import com.yunbaba.ols.api.CldKAccountAPI.IQRLoginResultListener;
import com.yunbaba.ols.dal.CldDalKAccount;

/**
 * The Class KAccount.
 * 
 * @Description 帐户系统
 * @author Zhouls
 * @date 2014-11-4 下午2:15:30
 */
public class KAccount extends Activity {

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
		setContentView(R.layout.kaccount);
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
		mListView = (ListView) findViewById(R.id.list_ka);
		mButtonList = new ArrayList<MyListItem>();
		CldKAccountAPI.getInstance().setCldKAccountListener(new MyKAlistener());
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
		mButtonList.add(new MyListItem("设备注册", new OnClickListener() {

			@Override
			public void onClick(View v) {
				CldKAccountAPI.getInstance().deviceRegister();
			}
		}));
		mButtonList.add(new MyListItem("是否是已注册用户", new OnClickListener() {

			@Override
			public void onClick(View v) {
				new InputDialog(KAccount.this, new AfterListener() {

					@Override
					public void toDo() {
						CldKAccountAPI.getInstance().isRegisterUser(
								DalInput.getInstance().getStrEd1());
					}

					@Override
					public void stopDo() {


					}
				}).show();

			}
		}));
		mButtonList.add(new MyListItem("上行短信注册", new OnClickListener() {

			@Override
			public void onClick(View v) {
				new InputDialog(KAccount.this, new AfterListener() {

					@Override
					public void toDo() {
						CldKAccountAPI.getInstance().registerBySms(
								DalInput.getInstance().getStrEd1());
					}

					@Override
					public void stopDo() {


					}
				}).show();

			}
		}));
		mButtonList.add(new MyListItem("是否是手机号", new OnClickListener() {

			@Override
			public void onClick(View v) {
				new InputDialog(KAccount.this, new AfterListener() {
					@Override
					public void toDo() {
						boolean isPhone = CldKConfigAPI.getInstance()
								.isPhoneNum(DalInput.getInstance().getStrEd1());
						if (isPhone) {
							CldLog.d("ols", "Is Phone");
						} else {
							CldLog.d("ols", "Not Phone");
						}
					}

					@Override
					public void stopDo() {


					}
				}).show();
			}
		}));
		mButtonList.add(new MyListItem("登录", new OnClickListener() {
			@Override
			public void onClick(View v) {
				new InputDialog(KAccount.this, new AfterListener() {
					@Override
					public void toDo() {
						CldKAccountAPI.getInstance().login(
								DalInput.getInstance().getStrEd1(),
								DalInput.getInstance().getStrEd2());
					}

					@Override
					public void stopDo() {

					}
				}).show();
			}
		}));
		mButtonList.add(new MyListItem("获取用户信息", new OnClickListener() {
			@Override
			public void onClick(View v) {
				CldKAccountAPI.getInstance().getUserInfo();
			}
		}));

		mButtonList.add(new MyListItem("注销", new OnClickListener() {

			@Override
			public void onClick(View v) {
				CldKAccountAPI.getInstance().loginOut(null);
			}
		}));
		mButtonList.add(new MyListItem("自动登录", new OnClickListener() {

			@Override
			public void onClick(View v) {
				CldKAccountAPI.getInstance().startAutoLogin(null);
			}
		}));

		mButtonList.add(new MyListItem("获取手机验证码(mobile,busid)",
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						new InputDialog(KAccount.this, new AfterListener() {
							@Override
							public void toDo() {
								boolean b = CldKConfigAPI.getInstance()
										.isPhoneNum(
												DalInput.getInstance()
														.getStrEd1());
								if (b) {
									CldLog.d("ols", "true");
								} else {
									CldLog.d("ols", "false");
								}
								CldKAccountAPI.getInstance().getVerifyCode(
										DalInput.getInstance().getStrEd1(),
										Integer.parseInt(DalInput.getInstance()
												.getStrEd2()), null);
							}

							@Override
							public void stopDo() {

							}
						}).show();
					}
				}));

		mButtonList.add(new MyListItem("获取绑定的手机号", new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),
						CldKAccountAPI.getInstance().getBindMobile(),
						Toast.LENGTH_SHORT).show();
			}
		}));
		mButtonList.add(new MyListItem("绑定手机号(mobile,identicode)",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new InputDialog(KAccount.this, new AfterListener() {
							@Override
							public void toDo() {
								CldKAccountAPI.getInstance().bindMobile(
										DalInput.getInstance().getStrEd1(),
										DalInput.getInstance().getStrEd2(), null);
							}

							@Override
							public void stopDo() {

							}
						}).show();
					}
				}));
		mButtonList.add(new MyListItem("解绑手机号(mobile,identicode)",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new InputDialog(KAccount.this, new AfterListener() {
							@Override
							public void toDo() {
								CldKAccountAPI.getInstance().unbindMobile(
										DalInput.getInstance().getStrEd1(),
										DalInput.getInstance().getStrEd2());
							}

							@Override
							public void stopDo() {

							}
						}).show();
					}
				}));
		mButtonList.add(new MyListItem("改绑手机号(mobile,oldmobile,identicode)",
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						new InputDialog(KAccount.this, new AfterListener() {

							@Override
							public void toDo() {
								CldKAccountAPI.getInstance().updateMobile(
										DalInput.getInstance().getStrEd1(),
										DalInput.getInstance().getStrEd2(),
										DalInput.getInstance().getStrEd3());
							}

							@Override
							public void stopDo() {

							}
						}).show();
					}
				}));
		mButtonList.add(new MyListItem("注册(mobile,psd,idcode)",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new InputDialog(KAccount.this, new AfterListener() {

							@Override
							public void toDo() {
								CldKAccountAPI.getInstance().register(
										DalInput.getInstance().getStrEd1(),
										DalInput.getInstance().getStrEd2(),
										DalInput.getInstance().getStrEd3());
							}

							@Override
							public void stopDo() {


							}
						}).show();

					}
				}));
		mButtonList.add(new MyListItem("重置密码(mobile,npsd,idcode)",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new InputDialog(KAccount.this, new AfterListener() {

							@Override
							public void toDo() {
								CldKAccountAPI.getInstance().retrievePwd(
										DalInput.getInstance().getStrEd1(),
										DalInput.getInstance().getStrEd2(),
										DalInput.getInstance().getStrEd3());
							}

							@Override
							public void stopDo() {


							}
						}).show();

					}
				}));
		mButtonList.add(new MyListItem("修改密码(opsd,npsd)",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new InputDialog(KAccount.this, new AfterListener() {

							@Override
							public void toDo() {
								CldKAccountAPI.getInstance().revisePwd(
										DalInput.getInstance().getStrEd1(),
										DalInput.getInstance().getStrEd2(), null);
							}

							@Override
							public void stopDo() {


							}
						}).show();

					}
				}));
		mButtonList.add(new MyListItem("更新用户信息(una,ual,sex)",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new InputDialog(KAccount.this, new AfterListener() {

							@Override
							public void toDo() {
								CldKAccountAPI.getInstance().updateUserInfo(
										DalInput.getInstance().getStrEd1(),
										DalInput.getInstance().getStrEd2(),
										null, null,
										DalInput.getInstance().getStrEd3(),
										DalInput.getInstance().getStrEd4(),
										null);
							}

							@Override
							public void stopDo() {


							}
						}).show();
					}
				}));
		mButtonList.add(new MyListItem("获取服务端时间", new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						CldLog.d("svrTime", CldKAccountAPI.getInstance()
								.getSvrTime() + "");
					}
				}).start();
			}
		}));

		mButtonList.add(new MyListItem("快捷登录(mobile,vericode)",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new InputDialog(KAccount.this, new AfterListener() {
							@Override
							public void toDo() {
								CldKAccountAPI.getInstance().fastLogin(
										DalInput.getInstance().getStrEd1(),
										DalInput.getInstance().getStrEd2());
							}

							@Override
							public void stopDo() {


							}
						}).show();
					}
				}));
		mButtonList.add(new MyListItem("快捷登录后修改密码(npsd)",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new InputDialog(KAccount.this, new AfterListener() {

							@Override
							public void toDo() {
								CldKAccountAPI.getInstance()
										.revisePwdByFastPwd(
												DalInput.getInstance()
														.getStrEd1());
							}

							@Override
							public void stopDo() {


							}
						}).show();

					}
				}));
		mButtonList.add(new MyListItem("快捷登录用户类型", new OnClickListener() {
			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(
								getApplicationContext(),
								CldKAccountAPI.getInstance()
										.getSpecialLoginStatus() + "",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}));
		mButtonList.add(new MyListItem("获取二维码", new OnClickListener() {

			@Override
			public void onClick(View v) {
				CldKAccountAPI.getInstance().getQRcode(0);
			}
		}));
		mButtonList.add(new MyListItem("二维码登录", new OnClickListener() {

			@Override
			public void onClick(View v) {
				CldKAccountAPI.getInstance().loginByQRcode(
						CldDalKAccount.getInstance().getGuid(),
						new IQRLoginResultListener() {
							@Override
							public void onLoginByQRcodeResult(int errCode) {

							}
						});
			}
		}));
		mButtonList.add(new MyListItem("获取二维码登录状态", new OnClickListener() {
			@Override
			public void onClick(View v) {
				CldKAccountAPI.getInstance().getLoginStatusByQRcode(
						CldDalKAccount.getInstance().getGuid());
			}
		}));
		mButtonList.add(new MyListItem("获取改绑手机号验证码", new OnClickListener() {
			@Override
			public void onClick(View v) {
				CldKAccountAPI.getInstance().getVerCodeToReviseMobile(
						"13266849942", "13266841424", null);
			}
		}));
		mButtonList.add(new MyListItem("第三方登录", new OnClickListener() {
			@Override
			public void onClick(View v) {
				CldKAccountAPI.getInstance().thirdLogin("zlsaipyxklmjhnasdasd",
						CldThirdLoginType.QQLOGIN);
			}
		}));
		mButtonList.add(new MyListItem("时间", new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("yyh","time = "+CldKAccountAPI.getInstance().getSvrTime());
			}
		}));

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
