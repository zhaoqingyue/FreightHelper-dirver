package com.yunbaba.freighthelper.ui.activity.car;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.yunbaba.api.trunk.bean.CarCheckResultBean;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.ui.adapter.CarCheckHistoryAdapter;
import com.yunbaba.freighthelper.ui.customview.SpinerPopWindow;
import com.yunbaba.freighthelper.utils.CommonTool;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICarCheckHistoryListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IExaminationDetailListener;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IExaminationHistoryListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.Examinationdetail;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCarCheckHistory;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqExaminationUnit;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarCheckHistoryActivity extends BaseButterKnifeActivity {

	@BindView(R.id.iv_titleleft)
	ImageView ivTitleleft;

	@BindView(R.id.iv_arrow)
	ImageView ivArrow;

	@BindView(R.id.tv_title)
	TextView tvTitle;
	@BindView(R.id.iv_titleright)
	ImageView ivTitleright;
	@BindView(R.id.tv_type)
	TextView tvType;
	@BindView(R.id.ll_carselect)
	PercentLinearLayout llCarselect;

	@BindView(R.id.carinfo_empty)
	PercentLinearLayout llCarinfoEmpty;

	@BindView(R.id.ll_carhistory_empty)
	PercentLinearLayout llCarhistoryEmpty;

	@BindView(R.id.lv_list)
	ListView lvList;
	@BindView(R.id.pb_waiting)
	PercentRelativeLayout pbWaiting;

	@BindView(R.id.prl_netfail)
	PercentRelativeLayout prl_netfail;

	@BindView(R.id.tv_reload)
	TextView tvReload;

	String mcarduid;
	String mcarlicense;

	ArrayList<MtqCarCheckHistory> mCarlist = new ArrayList<>();

	CarCheckHistoryAdapter mAdapter;
	ArrayList<MtqExaminationUnit> mlistdata = new ArrayList<>();

	private SpinerPopWindow mSpinerPopWindow;

	public static final int pageSize = 20;
	int pageIndex = 1;
	boolean loadFinish = false;

	String currentCarlisence;
	MtqCarCheckHistory currentSelectCar;
	public static final int CAR_CHECK = 106;
	private boolean isGetCarListFinish = false;

	@Override
	public int getLayoutId() {
		return R.layout.activity_carcheck_history;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: add setContentView(...) invocation
		ButterKnife.bind(this);

		tvTitle.setText("历史体检");

		if (getIntent().getStringExtra("caruid") == null || getIntent().getStringExtra("carlicense") == null) {

			// finish();

			mcarduid = SPHelper.getInstance(this).getRecentCaruid();
			mcarlicense = SPHelper.getInstance(this).getRecentCarLicense();

		} else {

			mcarduid = getIntent().getStringExtra("caruid");
			mcarlicense = getIntent().getStringExtra("carlicense");

		}

		MLog.e("carcheckhistory", mcarduid + " " + mcarlicense);

		if (!TextUtils.isEmpty(mcarlicense) && !TextUtils.isEmpty(mcarduid)) {

			tvType.setText(mcarlicense);

			MtqCarCheckHistory cur = new MtqCarCheckHistory();

			cur.carlicense = mcarlicense;
			cur.duid = mcarduid;

			mCarlist.clear();
			mCarlist.add(cur);

			//
			// MtqCarCheckHistory cur2 = new MtqCarCheckHistory();
			//
			// cur2.carlicense = "粤A12345";
			// cur2.duid = "6105761";
			//
			//
			// mCarlist.add(cur2);

		}
		mAdapter = new CarCheckHistoryAdapter(this, mlistdata);
		lvList.setAdapter(mAdapter);

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


				MtqExaminationUnit item = mAdapter.getItem(position);

				GetCheckResFromNet(item);

			}
		});

		lvList.setOnScrollListener(new OnScrollListener() {
			// AbsListView view 这个view对象就是listview
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {

						if (!loadFinish) {

							// 加载更多
							GetCheckHistoryFromNet(true);
							// GetListFromNet(true);
						} else {

							// 已经取完了


							// ToastUtils.showMessage(CarCheckHistoryActivity.this,
							// "没有更多记录了", Toast.LENGTH_SHORT);

						}

					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});

		mSpinerPopWindow = new SpinerPopWindow(this, mCarlist, new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


				mSpinerPopWindow.dismiss();
				// tvValue.setText(list.get(position));
				// Toast.makeText(MainActivity.this, "点击了:" +
				// list.get(position),Toast.LENGTH_LONG).show(); F

				try {

					currentCarlisence = mCarlist.get(position).carlicense;
					tvType.setText(currentCarlisence);

					GetCheckHistoryFromNet(false);

					currentSelectCar = mCarlist.get(position);
				} catch (Exception e) {

				}

			}
		});
		mSpinerPopWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {

				ivArrow.setImageResource(R.drawable.icon_down);
			}
		});

		GetCarCheckHistoryCarListFromNet(false);

	}

	public void GetCheckResFromNet(final MtqExaminationUnit item) {

		// String duid = getCarDuidByCarLicense(currentCarlisence);
		//
		// if(duid == null)
		// return;
		if (!CldPhoneNet.isNetConnected()) {
			Toast.makeText(this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();

			return;
		}

		showProgressBar();

		CldKDeliveryAPI.getInstance().getExaminationDetail(item.duid, item.examid, item.time,
				new IExaminationDetailListener() {

					@Override
					public void onGetResult(int errCode, List<Examinationdetail> result) {

						if (isFinishing())
							return;

						hideProgressBar();

						if (errCode != 0)
							Toast.makeText(CarCheckHistoryActivity.this, "当前网络不可用，请检查网络设置。", Toast.LENGTH_SHORT).show();
						else {

							Intent intent = new Intent(CarCheckHistoryActivity.this, CarCheckActivity.class);

							intent.putExtra("caruid", item.duid);

							// String carlicense =
							// getCarLicenseByCarDuid(item.duid);

							// if(TextUtils.isEmpty(carlicense)){
							String carlicense = currentCarlisence;
							// }

							intent.putExtra("carlicense", carlicense);
							CarCheckResultBean bean = new CarCheckResultBean();
							bean.data = result;
							intent.putExtra("checkresbean", GsonTool.getInstance().toJson(bean));

							startActivityForResult(intent, CAR_CHECK);

						}

					}

					@Override
					public void onGetReqKey(String arg0) {


					}

				});

	}

	public void GetCheckHistoryFromNet(final boolean isMore) {

		if (!CldPhoneNet.isNetConnected()) {
			Toast.makeText(this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
			prl_netfail.setVisibility(View.VISIBLE);
			return;
		}

		showProgressBar();

		if (isMore)
			pageIndex += 1;
		else {
			pageIndex = 1;
			loadFinish = false;
		}

		MLog.e("checkList", isMore + " " + currentCarlisence);

		llCarhistoryEmpty.setVisibility(View.GONE);

		// mcarduid
		// "6105756"
		CldKDeliveryAPI.getInstance().getExaminationHistory(currentCarlisence, pageSize, pageIndex,
				new IExaminationHistoryListener() {

					@Override
					public void onGetResult(int errCode, List<MtqExaminationUnit> result) {

						if (isFinishing())
							return;

						hideProgressBar();

						if (errCode == 0) {

							if (!isMore) {

								if (result == null || (result != null && result.size() == 0)) {
									llCarhistoryEmpty.setVisibility(View.VISIBLE);
								} else
									llCarhistoryEmpty.setVisibility(View.GONE);

								mlistdata.clear();

								if (result != null) {
									mlistdata.addAll(result);
								}

							} else {
								if (result != null) {
									mlistdata.addAll(result);
								}
							}

							mAdapter.notifyDataSetChanged();

							if (result == null || result.size() < 20) {

								loadFinish = true;

							}

						} else {

							if (!isMore)
								Toast.makeText(CarCheckHistoryActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT)
										.show();
							else
								Toast.makeText(CarCheckHistoryActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT)
										.show();

						}
					}

					@Override
					public void onGetReqKey(String arg0) {


					}
				});

	}

	int Retrytime = 0;

	public void GetCarCheckHistoryCarListFromNet(boolean isRetry) {

		if (!CldPhoneNet.isNetConnected()) {
			Toast.makeText(this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
			prl_netfail.setVisibility(View.VISIBLE);

			return;
		}

		showProgressBar();

		if (isRetry)
			Retrytime += 1;
		else
			Retrytime = 1;

		// mcarduid
		// "6105756"
		CldKDeliveryAPI.getInstance().getCarCheckHistoryCarList(1, 50, new ICarCheckHistoryListener() {

			@Override
			public void onGetResult(int errCode, int total, List<MtqCarCheckHistory> listOfResult) {


				MLog.e("getcarhistory", errCode + " " + total + " " + GsonTool.getInstance().toJson(listOfResult));

				if (isFinishing())
					return;

				hideProgressBar();

				if (errCode == 0) {

					isGetCarListFinish = true;

					if (total == 0 && TextUtils.isEmpty(mcarduid)) {

						setEmtpyInfoState(true);

					} else {

						setEmtpyInfoState(false);

						boolean isAdd = true;

						for (MtqCarCheckHistory tmp : listOfResult) {

							isAdd = true;

							try {

								over: for (MtqCarCheckHistory tmp2 : mCarlist) {

									if (tmp.carlicense.equals(tmp2.carlicense)) {

										isAdd = false;
										break over;
									}

								}
							} catch (Exception e) {

								isAdd = false;

							}

							if (isAdd) {
								mCarlist.add(tmp);
							}

						}

						setCurrentCar();
						GetCheckHistoryFromNet(false);
						// 8vz88522g

					}
				} else {

					if (Retrytime < 3) {
						GetCarCheckHistoryCarListFromNet(true);
					} else {

						Toast.makeText(CarCheckHistoryActivity.this, "网络连接失败，点击重新加载。", Toast.LENGTH_SHORT).show();

						if (TextUtils.isEmpty(mcarduid)) {

							setEmtpyInfoState(true);

						} else {

							setEmtpyInfoState(false);
							// mCarlist.addAll(listOfResult);
							setCurrentCar();
							GetCheckHistoryFromNet(false);
							// 8vz88522g

						}

					}

				}

			}

			@Override
			public void onGetReqKey(String arg0) {


			}
		});

	}

	public void setCurrentCar() {

		if (!TextUtils.isEmpty(mcarlicense)) {

			currentCarlisence = mcarlicense;
			tvType.setText(mcarlicense);

		} else {

			if (mCarlist != null && mCarlist.size() > 0) {

				if (!TextUtils.isEmpty(mCarlist.get(0).carlicense)) {

					currentCarlisence = mCarlist.get(0).carlicense;
					tvType.setText(currentCarlisence);

				}

			}

		}

	}

	public String getCarDuidByCarLicense(String carlisence) {

		String duid = null;

		over: for (MtqCarCheckHistory tmp : mCarlist) {

			if (tmp.carlicense.equals(carlisence)) {

				duid = tmp.duid;

				break over;

			}

		}

		return duid;

	}

	//
	// public String getCarLicenseByCarDuid(String duid) {
	//
	// String carlisence = null;
	//
	// over: for (MtqCarCheckHistory tmp : mCarlist) {
	//
	// if (tmp.duid.equals(duid)) {
	//
	// carlisence = tmp.carlicense;
	//
	// break over;
	//
	// }
	//
	// }
	//
	// return carlisence;
	//
	// }

	@OnClick({ R.id.iv_titleleft, R.id.ll_carselect, R.id.tv_reload })
	public void onClick(View view) {

		if (CommonTool.isFastDoubleClick()) {

			return;
		}


		switch (view.getId()) {
		case R.id.iv_titleleft:
			finish();
			break;
		case R.id.ll_carselect:

			if (mCarlist != null && mCarlist.size() > 0) {

				ivArrow.setImageResource(R.drawable.icon_up);
				mSpinerPopWindow.setWidth(llCarselect.getWidth());
				mSpinerPopWindow.showAsDropDown(llCarselect);

				String[] carlist = new String[mCarlist.size()];
				MtqCarCheckHistory tmp;
				for (int i = 0; i < mCarlist.size(); i++) {

					if (i < mCarlist.size()) {
						tmp = mCarlist.get(i);
						carlist[i] = tmp.carlicense;
					}

				}

				// SimpleIndexSelectDialog.ShowSelectDialog(this, carlist, true,
				// new OnSimpleIndexSelectCallBack() {
				//
				// @Override
				// public void OnIndexSelect(int index, String select) {
				//
				// currentCarlisence = select;
				// tvType.setText(select);
				//
				// GetCheckHistoryFromNet(false);
				//
				// try {
				//
				// currentSelectCar = mCarlist.get(index);
				// } catch (Exception e) {
				//
				// }
				//
				// }
				// });
			}
			break;

		case R.id.tv_reload:
			
			prl_netfail.setVisibility(View.GONE);
			
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					if (isGetCarListFinish) {
						if (!TextUtils.isEmpty(currentCarlisence)) {

							GetCheckHistoryFromNet(false);

						}
					} else {
						GetCarCheckHistoryCarListFromNet(false);
					}

				}
			}, 300);
			break;
		}
	}

	protected void showProgressBar() {
		if (pbWaiting != null)
			pbWaiting.setVisibility(View.VISIBLE);
	}

	protected void hideProgressBar() {
		if (pbWaiting != null)
			pbWaiting.setVisibility(View.GONE);
	}

	public void setEmtpyInfoState(boolean isVisible) {

		if (llCarinfoEmpty != null)
			llCarinfoEmpty.setVisibility(isVisible ? View.VISIBLE : View.GONE);

	}

	@Override
	protected void onResume() {

		super.onResume();

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {

		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == CAR_CHECK && arg1 == CAR_CHECK) {
			MLog.v("carcheckhistory", "onactivityresult carchek");

			if (!TextUtils.isEmpty(currentCarlisence)) {

				GetCheckHistoryFromNet(false);

			}

		}

	}

}
