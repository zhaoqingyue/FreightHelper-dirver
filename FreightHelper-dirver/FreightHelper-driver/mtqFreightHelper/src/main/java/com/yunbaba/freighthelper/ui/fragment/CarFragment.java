package com.yunbaba.freighthelper.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.location.CldLocation;
import com.cld.location.ICldLocationListener;
import com.cld.mapapi.map.MapView;
import com.cld.mapapi.map.MarkerOptions;
import com.cld.mapapi.map.PolyLineOptions;
import com.cld.mapapi.model.LatLng;
import com.cld.mapapi.search.exception.IllegalSearchArgumentException;
import com.cld.mapapi.search.geocode.GeoCodeResult;
import com.cld.mapapi.search.geocode.GeoCoder;
import com.cld.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.cld.mapapi.search.geocode.ReverseGeoCodeOption;
import com.cld.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cld.nv.map.overlay.impl.MapPolyLine;
import com.yunbaba.api.map.LocationAPI;
import com.yunbaba.api.map.LocationAPI.MTQLocationMode;
import com.yunbaba.api.trunk.listner.OnBooleanListner;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.MajorMainFragment;
import com.yunbaba.freighthelper.bean.TaskSpInfo;
import com.yunbaba.freighthelper.bean.car.CarInfo;
import com.yunbaba.freighthelper.bean.car.Navi;
import com.yunbaba.freighthelper.bean.car.TravelDetail;
import com.yunbaba.freighthelper.bean.eventbus.TravelDetailEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.manager.CarManager;
import com.yunbaba.freighthelper.manager.CarManager.IGetCarListener;
import com.yunbaba.freighthelper.manager.CarManager.IGetLatestRouteListener;
import com.yunbaba.freighthelper.manager.CarManager.IGetTaskDetailListener;
import com.yunbaba.freighthelper.ui.activity.car.CarCheckActivity;
import com.yunbaba.freighthelper.ui.activity.car.CarCheckHistoryActivity;
import com.yunbaba.freighthelper.ui.activity.car.CarSelectActivity;
import com.yunbaba.freighthelper.ui.activity.car.HistroyTravelActivity;
import com.yunbaba.freighthelper.ui.customview.DragPercentLinearLayout;
import com.yunbaba.freighthelper.ui.customview.PopupTravel;
import com.yunbaba.freighthelper.ui.customview.PopupTravel.OnPopupListener;
import com.yunbaba.freighthelper.ui.customview.SpringProgressView;
import com.yunbaba.freighthelper.utils.CarUtils;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.TimeUtils;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCarRoute;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCarstate;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqPosData;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTask;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTaskDetail;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTrack;
import com.yunbaba.ols.module.delivery.tool.CldKDeviceAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CarFragment extends MajorMainFragment implements OnClickListener,OnGetGeoCoderResultListener {

	private static final String TAG = "CarFragment";
	// 更新时间
	private TextView mTvCarConditionTime;
	// 车牌
	private TextView mTvCarConditionNumber;

	private LinearLayout mDataLayout;
	private FrameLayout mMapLayout;
	private RelativeLayout mNoDataLayout;
	private TextView mRoot;

	private MapView mMapView = null;

	// 当前位置
	private TextView mCurrentLocation;

	// 剩余油量
	private TextView mTvRemainFuel;

	// 电瓶电压
	private TextView mTvBatteryvoltage;
	// 发动机转速
	private TextView mTvEngineSpeed;
	// 车速
	private TextView mTvCarSpeed;
	// 冷却液温度
	private TextView mTvCoolantTemperature;
	// 瞬间油耗
	private TextView mTvMomentFuel;

	// 车厢温度1
	private SpringProgressView mSpringProgressView1;
	private TextView mTvCarTemp1;
	// 车厢温度2
	private SpringProgressView mSpringProgressView2;
	private TextView mTvCarTemp2;
	// 车厢温度3
	private TextView mTvCarTemp3;
	private SpringProgressView mSpringProgressView3;
	// 车厢温度4
	private TextView mTvCarTemp4;
	private SpringProgressView mSpringProgressView4;

	// 查看历史行程
	private TextView mBtnCheckRoute;
	private TextView mBtnCheckRoute1;

	// 查看历史体检
	private TextView mBtnCheckExamination;
	private TextView mBtnCheckExamination1;

//	private TextView mkmh;
//	private TextView m100;

	private DragPercentLinearLayout mDragLayout;

	// 温度标题，温度
	private RelativeLayout temperLayout;
	private RelativeLayout temperTitle;

	private PopupTravel mPopup;

	private ImageView ivNoCar;
	private TextView tvNoCar;

	// 是否已经布局完成。
	private boolean isLayoutOver;

	// 上次体检的id
	private String mTaskId;
	private String mCorpId;
	// 是否在线更新成功。
	private boolean mUpdateSuccess = false;

	// 获取数据结束
	private boolean mGetDataOver = true;

	/** 间隔时间 100s */
	private final int INTERVAL_TIME = 100000;
	/** 间隔时间 24小时 */
	private final int INTERVAL_MAXTIME = 3600000*24;

	// 间隔时间
	private int mIntervalTime = 0;

	// 是否显示过该界面
	private boolean isShow = false;

	/** 定时器Runnale */
	private Runnable mTimeRunnale = null;
	private Handler mHander = new Handler();

	/** 轨迹点的坐标 */
	private ArrayList<LatLng> mTrackPointList;
	
	private ArrayList<ArrayList<LatLng>> mArrayTrackList;
	
	private MapPolyLine mTrackline;// 线条View

	/** 后台返回的无效值 **/
	private final String INVALID_VALUE = "-512";
	
	/** 是不是在线  **/
	private boolean mIsOnline = true;
	
	private GeoCoder mGeoCoder;
	
	/** 是否暂停刷新地图 **/
	private boolean mIsPauseMap = false;

	public static CarFragment newInstance() {

		Bundle args = new Bundle();
		CarFragment fragment = new CarFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_car1, container, false);
		initViews(view);
		setListener(view);
		initData();
		EventBus.getDefault().register(this);
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			isShow = true;
			updateUI();
			if (!isLayoutOver) {
				mMapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {


						mDataLayout.layout(mDataLayout.getLeft(),
								mDataLayout.getTop() + mMapLayout.getMeasuredHeight() / 2, mDataLayout.getRight(),
								mDataLayout.getBottom() + mMapLayout.getMeasuredHeight() / 2);
						mDragLayout.setmTopLimit(0);
						mDragLayout.setmBottomLimit(mMapLayout.getMeasuredHeight());
						mMapLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						isLayoutOver = true;
					}
				});
			}
			location();
			// 在该界面每一百秒取一次。
			if (mGetDataOver) {
				mIntervalTime = INTERVAL_TIME;
				mHander.removeCallbacks(mTimeRunnale);
				loadData();
			} else {
				// 上次没有获取完。就现在不获取了。
				startTimer(INTERVAL_TIME);
			}
			if (FreightConstant.isSaveFlow){
				if (mMapView != null) {
					mMapView.onResume();// 当地图控件存在时，调用相应的恢复方法
					mMapView.update();
					mIsPauseMap = false;
					if (mTrackPointList.size() > 0) {
						showTrack(mTrackPointList,mArrayTrackList);
					}
				}
			}
		} else {
			// 切到外面不用每一百秒取一次。 后面看需不需要取。
			if (mTimeRunnale != null) {
				startTimer(INTERVAL_MAXTIME);
			}
			mGetDataOver = true;
			if (FreightConstant.isSaveFlow){
				if (mMapView != null) {
					mMapView.onPause();
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mMapView != null) {
			mMapView.onResume();// 当地图控件存在时，调用相应的恢复方法
			mMapView.update();// 同时更新地图控件状态
			mIsPauseMap = false;
			if (mTrackPointList.size() > 0) {
				showTrack(mTrackPointList,mArrayTrackList);
			}
		}
		//MLog.v("checkcarfragmentonresume", "onResume");

		if (!this.isVisible()) {
			if (FreightConstant.isSaveFlow){
				if (mMapView != null) {
					mMapView.onPause();
				}
			}
			//MLog.v("checkcarfragmentonresume", "当前不可见");
			return;
		} else
			//MLog.v("checkcarfragmentonresume", "当前可见");

		if (isShow) {
			startTimer(INTERVAL_TIME);
		} else {
			// 没有显示创建activity时走的
			mIntervalTime = INTERVAL_MAXTIME;
			loadData();
		}

		updateUI();

		
	}

	private void initViews(View view) {
		mCurrentLocation = (TextView) view.findViewById(R.id.current_location);
		mTvCarConditionTime = (TextView) view.findViewById(R.id.tv_car_condition_time);
		mTvCarConditionNumber = (TextView) view.findViewById(R.id.tv_car_condition_car_number);

		mTvRemainFuel = (TextView) view.findViewById(R.id.tv_car_condition_remain_fuel);
		mTvBatteryvoltage = (TextView) view.findViewById(R.id.tv_car_condition_battery_voltage);
		mTvEngineSpeed = (TextView) view.findViewById(R.id.tv_car_condition_engine_speed);
		mTvCarSpeed = (TextView) view.findViewById(R.id.tv_car_condition_car_speed);
		mTvCoolantTemperature = (TextView) view.findViewById(R.id.tv_car_condition_coolant_temperature);
		mTvMomentFuel = (TextView) view.findViewById(R.id.tv_car_condition_moment_fuel);
	//	mkmh = (TextView) view.findViewById(R.id.tv_car_condition_car_speed_km);
	//	m100 = (TextView) view.findViewById(R.id.tv_car_condition_moment_fuel_100);
	//	m100.setVisibility(View.GONE);
		mTvCarTemp1 = (TextView) view.findViewById(R.id.tv_car_condition_temp1);
		mTvCarTemp2 = (TextView) view.findViewById(R.id.tv_car_condition_temp2);
		mTvCarTemp3 = (TextView) view.findViewById(R.id.tv_car_condition_temp3);
		mTvCarTemp4 = (TextView) view.findViewById(R.id.tv_car_condition_temp4);
		mSpringProgressView1 = (SpringProgressView) view.findViewById(R.id.sp_temp1);
		mSpringProgressView2 = (SpringProgressView) view.findViewById(R.id.sp_temp2);
		mSpringProgressView3 = (SpringProgressView) view.findViewById(R.id.sp_temp3);
		mSpringProgressView4 = (SpringProgressView) view.findViewById(R.id.sp_temp4);

		mCurrentLocation.getBackground().setAlpha(130);
		mCurrentLocation.setVisibility(View.INVISIBLE);

		mSpringProgressView1.setMaxCount(30);
		mSpringProgressView1.setCurrentCount(10);
		mSpringProgressView2.setMaxCount(30);
		mSpringProgressView2.setCurrentCount(20);
		mSpringProgressView3.setMaxCount(30);
		mSpringProgressView3.setCurrentCount(30);

		temperLayout = (RelativeLayout) view.findViewById(R.id.temper_layout);
		temperTitle = (RelativeLayout) view.findViewById(R.id.temper_title);
		temperLayout.setVisibility(View.GONE);// 这一期不展示温度
		temperTitle.setVisibility(View.GONE);// 这一期不展示温度

		mBtnCheckRoute = (TextView) view.findViewById(R.id.btn_no_car_check_route);
		mBtnCheckExamination = (TextView) view.findViewById(R.id.btn_no_car_check_examination);
		mBtnCheckRoute1 = (TextView) view.findViewById(R.id.btn_car_check_route);
		mBtnCheckExamination1 = (TextView) view.findViewById(R.id.btn_car_check_examination);

		mDataLayout = (LinearLayout) view.findViewById(R.id.rl_car_condtion_data);
		mNoDataLayout = (RelativeLayout) view.findViewById(R.id.rl_car_condtion_no_data);
		mMapLayout = (FrameLayout) view.findViewById(R.id.fl_car_conditon_mapview);

		mRoot = (TextView) view.findViewById(R.id.tv_car_condition_car_number);

		ivNoCar = (ImageView) view.findViewById(R.id.iv_no_car);

		tvNoCar = (TextView) view.findViewById(R.id.tv_no_car);

		mDragLayout = (DragPercentLinearLayout) view.findViewById(R.id.dll_car_condition_data);
		mDragLayout.setmSupportDragView(mDataLayout);

		if (FreightConstant.isShowMap){
			if (_mActivity != null){
				mMapView = new MapView(_mActivity);
				mMapView.getMap().setTrafficEnabled(false);
				mMapView.getMap().setLocationIconEnabled(false);
				mMapLayout.addView(mMapView);
			}
		}
	}

	private void location() {
		if (mMapView == null){
			return;
		}
		
		LocationAPI.getInstance().location(MTQLocationMode.NETWORK, 3000, getActivity())
				.setLinster(new ICldLocationListener() {
					@Override
					public void onReceiveLocation(CldLocation location) {
						LocationAPI.getInstance().stop();
						onLocation(location);
					}
				});
	}

	private void onLocation(CldLocation location) {
//		MLog.e("yyh", "location = " + location.getProvince() + location.getCity() + location.getDistrict()
//				+ location.getDistrict() + location.getAdCode());
		if (location.getErrCode() != 0){
			return ;
		}
			
		LatLng tmp2 = new LatLng(location.getLatitude(), location.getLongitude());
		if (mTrackPointList.size() == 0 && mMapView!=null && mMapView.getMap()!=null){
			mMapView.getMap().setMapCenter(tmp2);
			mCurrentLocation.setText(location.getAddress());
			mCurrentLocation.setVisibility(View.VISIBLE);
			mMapView.getMap().setLocationIconEnabled(true);
		}
	}

	private void setListener(View view) {
		mRoot.setOnClickListener(this);
		mBtnCheckRoute.setOnClickListener(this);
		mBtnCheckExamination.setOnClickListener(this);
		mBtnCheckRoute1.setOnClickListener(this);
		mBtnCheckExamination1.setOnClickListener(this);
	}

	private void initData() {
		isLayoutOver = false;
		mTrackPointList = new ArrayList<>();
		mArrayTrackList = new ArrayList<>();
		String title = "历史行程";//getResources().getString(R.string.car_condition_route);
		mPopup = new PopupTravel(getActivity(), title, new OnPopupListener() {

			@Override
			public void onClick() {
				Intent intent = new Intent(getActivity(), CarSelectActivity.class);
				startActivity(intent);
			}
		});

		mTimeRunnale = new Runnable() {

			@Override
			public void run() {

				loadData();
			}
		};
	}

	private void getCarInfo() {
		if (CldPhoneNet.isNetConnected()) {
			/**
			 * 重新切换账号，最近一次任务信息没有同步
			 */
			mGetDataOver = false;
			TaskSpInfo task = SPHelper.getInstance(getActivity()).getRecentModifyTask();
			if (task != null) {
				mDragLayout.setVisibility(View.VISIBLE);
			//	mNoDataLayout.setVisibility(View.VISIBLE);
				WaitingProgressTool.showProgress(getActivity());

				final String taskId = task.taskid;
				final String corpId = task.corpid;
				CarManager.getInstance().getCarInfo(taskId, corpId, new IGetCarListener() {

					@Override
					public void onGetResult(int errCode, CarInfo result) {
						mGetDataOver = true;
						WaitingProgressTool.closeshowProgress();
						if (errCode == 0 && result != null) {
							
							if(result.devicetype != 2){
								dealNoDeviceFailed();
								return;
							}
							
							
							if (result.navi == null) {
								dealDetailFailed();
								return;
							}
							mNoDataLayout.setVisibility(View.INVISIBLE);
							Navi navi = new Navi();
							navi.carlicense = result.carlicense;
							navi.carduid = result.carduid;
							SPHelper.getInstance(getActivity()).saveRecentCaruid(navi.carduid, navi.carlicense);
							if (result.navi != null) {
								navi.serialid = result.navi.serialid;
								navi.orders = result.navi.orders;
							}

							updateCarInfo(result);
							getLatestNaviDetail(navi);
							saveCarInfo(result);
						} else {
							if (errCode == 0) {
								mGetDataOver = true;
								EventBus.getDefault().post(new TravelDetailEvent(MessageId.MSGID_GET_TASKNAVI_FAIL, 0));
							} else {
								// 取不到,使用数据库的
								//if(getActivity()!=null)
								//	Toast.makeText(getActivity(), "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
			} else {
				/**
				 * 无车辆信息
				 */
				mGetDataOver = true;
				EventBus.getDefault().post(new TravelDetailEvent(MessageId.MSGID_GET_TASKNAVI_FAIL, 0));
			}
		}else{
			Toast.makeText(getActivity(), "当前网络不可用，请检查网络设置。", Toast.LENGTH_SHORT).show();
		}
	}

	private void loadData() {
		if (mGetDataOver) {
			// 上次获取完了，这次才去获取
			getCarInfo();
		}

		if (mTimeRunnale != null) {
			mHander.removeCallbacks(mTimeRunnale);
			mHander.postDelayed(mTimeRunnale, mIntervalTime);
		}
		/**
		 * 获取行程日期列表
		 */
		/*
		 * CarManager.getInstance().getTasks(new IGetTaskListener() {
		 * 
		 * @Override public void onGetResult(int errCode, List<MtqTask>
		 * listOfResult) { if (errCode == 0 && listOfResult != null &&
		 * !listOfResult.isEmpty()) { int taskLen = listOfResult.size(); MtqTask
		 * task = listOfResult.get(taskLen - 1);
		 *//**
			 * 获取最新日期行程列表
			 */
		/*
		 * getLatestTask(task); } else {
		 *//**
			 * 获取失败
			 */
		/*
		 * dealDetailFailed(); } } });
		 */
	}

	/**
	 * 获取最新日期行程列表
	 */
	public void getLatestTask(MtqTask mtqTask) {
		CarManager.getInstance().getLatestTask(mtqTask, new IGetLatestRouteListener() {

			@Override
			public void onGetResult(int errCode, List<MtqCarRoute> listOfResult) {
				if (errCode == 0 && listOfResult != null && !listOfResult.isEmpty()) {
					List<Navi> naviList = CarUtils.formatNaviRoutes(listOfResult);
					int naviLen = naviList.size();
					if (naviLen > 0) {
						Navi navi = naviList.get(naviLen - 1);
						getLatestNaviDetail(navi);
					} else {
						/**
						 * 无行程轨迹
						 */
						EventBus.getDefault().post(new TravelDetailEvent(MessageId.MSGID_GET_LATEST_ROUTE_FAILED, 0));
					}
				} else {
					/**
					 * 获取失败
					 */
					EventBus.getDefault().post(new TravelDetailEvent(MessageId.MSGID_GET_LATEST_ROUTE_FAILED, 0));
				}
			}
		});
	}

	/**
	 * 获取最近一次行程 里面有轨迹
	 */
	public void getLatestNaviDetail(final Navi navi) {
		final String carlicense = navi.carlicense;
		final String serialid = navi.serialid;
		CarManager.getInstance().getLatestRouteDetail(navi, new IGetTaskDetailListener() {

			@Override
			public void onGetResult(int errCode, MtqTaskDetail result) {
				if (errCode == 0 && result != null) {
					TravelDetail detail = CarUtils.formatTaskDetail(result, carlicense, serialid);
					updateNaviDetail(navi, detail);
				} else {
					/**
					 * 获取失败
					 */
					mGetDataOver = true;
					EventBus.getDefault().post(new TravelDetailEvent(MessageId.MSGID_GET_LATEST_ROUTE_FAILED, 0));
				}
			}
		});
	}

	protected void updateNaviDetail(final Navi navi, final TravelDetail detail) {
		// updateNaviDetail 是在非UI线程调的

		Runnable r = new Runnable() {

			@Override
			public void run() {

				if (navi != null && detail != null && detail.navi != null) {
					updateDetail(navi, detail);
					mDataLayout.setVisibility(View.VISIBLE);
					mNoDataLayout.setVisibility(View.GONE);
					mGetDataOver = true;
				} else {
					dealDetailFailed();
				}
			}
		};

		if (Looper.myLooper() != Looper.getMainLooper()) {
			mDataLayout.post(r);
		} else {
			r.run();
		}
	}

	private void dealDetailFailed() {
		// mDataLayout.setVisibility(View.GONE);
		mNoDataLayout.setVisibility(View.VISIBLE);
		mTvCarConditionTime.setText("车辆");
		mTvCarConditionNumber.setText("无实时车况数据");
		ivNoCar.setImageResource(R.drawable.ic_no_car_data);

		tvNoCar.setText("您目前没有正在行驶的车辆");
		mGetDataOver = true;
		
		if (FreightConstant.isSaveFlow){
			if (mMapView != null) {
				mMapView.onPause();
				mIsPauseMap = true;
			}
		}
	}

	private void dealNoDeviceFailed() {
		// mDataLayout.setVisibility(View.GONE);
		mNoDataLayout.setVisibility(View.VISIBLE);
		mTvCarConditionTime.setText("车辆");
		mTvCarConditionNumber.setText("未连接设备");
		ivNoCar.setImageResource(R.drawable.img_car_not_connect_device);
		tvNoCar.setText("车辆没有连接运力盒子");
		mGetDataOver = true;
		
		if (FreightConstant.isSaveFlow){
			if (mMapView != null) {
				mMapView.onPause();
				mIsPauseMap = true;
			}
		}
	}

	private void updateUI() {
		// updateNews();
	}

	//
	// private void updateNews() {
	// if (MsgManager.getInstance().hasUnReadMsg()) {
	// mNewsImg.setImageResource(R.drawable.msg_icon_news);
	// } else {
	// mNewsImg.setImageResource(R.drawable.msg_icon);
	// }
	// }

	private void saveCarInfo(final CarInfo carInfo) {
		String str = GsonTool.getInstance().toJson(carInfo);
	}

	/**
	 * @Title: updateCarInfo
	 * @Description: 更新车辆信息
	 * @param carInfo
	 * @return: void
	 */
	private void updateCarInfo(final CarInfo carInfo) {
		Runnable r = new Runnable() {

			@Override
			public void run() {

				mTvCarConditionNumber.setText(carInfo.carlicense + " 车况数据");

				if (carInfo.navi != null) {
					mTvCarConditionTime.setVisibility(View.VISIBLE);

					mTvCarConditionTime
							.setText(TimeUtils.stampToFormat(carInfo.navi.endtime, "yyyy/MM/dd HH:mm:ss") + " 更新");

				} else {
					mTvCarConditionTime.setVisibility(View.INVISIBLE);
				}
				
				
				if (carInfo.carstate != null) {
					mIsOnline = IsDeviceOffLine(carInfo.carstate);
					String travelTime = carInfo.carstate.time;
					// mTvCarConditionTime.setText("");

					mTvBatteryvoltage.setTextColor(getResources().getColor(R.color.black));
					mTvCarSpeed.setTextColor(getResources().getColorStateList(R.color.black));
					mTvCoolantTemperature.setTextColor(getResources().getColorStateList(R.color.black));
					mTvEngineSpeed.setTextColor(getResources().getColorStateList(R.color.black));
					mTvRemainFuel.setTextColor(getResources().getColorStateList(R.color.black));
					mTvMomentFuel.setTextColor(getResources().getColorStateList(R.color.black));

					if (!TextUtils.isEmpty(carInfo.carstate.batteryvoltage)
							&& !carInfo.carstate.batteryvoltage.equals(INVALID_VALUE)) {
						mTvBatteryvoltage.setText(carInfo.carstate.batteryvoltage + "V");
					} else {
						mTvBatteryvoltage.setText(getResources().getString(R.string.car_no_data));
					}

					if (!TextUtils.isEmpty(carInfo.carstate.carspeed)
							&& !carInfo.carstate.carspeed.equals(INVALID_VALUE)) {
						mTvCarSpeed.setText(carInfo.carstate.carspeed+"km/h");
//						mkmh.setVisibility(View.VISIBLE);
//						mkmh.setText("km/h");
					} else {
						mTvCarSpeed.setText(getResources().getString(R.string.car_no_data));
					}

					if (!TextUtils.isEmpty(carInfo.carstate.enginecoolcent)
							&& !carInfo.carstate.enginecoolcent.equals(INVALID_VALUE)) {
						mTvCoolantTemperature.setText(carInfo.carstate.enginecoolcent + "℃");
					} else {
						mTvCoolantTemperature.setText(getResources().getString(R.string.car_no_data));
					}

					if (!TextUtils.isEmpty(carInfo.carstate.enginespeed)
							&& !carInfo.carstate.enginespeed.equals(INVALID_VALUE)) {
						mTvEngineSpeed.setText(carInfo.carstate.enginespeed + "rpm");
					} else {
						mTvEngineSpeed.setText(getResources().getString(R.string.car_no_data));
					}

					if (!TextUtils.isEmpty(carInfo.carstate.surplusoil)
							&& !carInfo.carstate.surplusoil.equals(INVALID_VALUE)) {
						mTvRemainFuel.setText(carInfo.carstate.surplusoil + "%");
					} else {
						mTvRemainFuel.setText(getResources().getString(R.string.car_no_data));
					}

					if (!TextUtils.isEmpty(carInfo.carstate.instantfuel)
							&& !carInfo.carstate.instantfuel.equals(INVALID_VALUE)) {
						mTvMomentFuel.setText(carInfo.carstate.instantfuel + "L/100km");
//						.setVisibility(View.VISIBLE);
//						.setText("100km");
					} else {
						mTvMomentFuel.setText(getResources().getString(R.string.car_no_data));
					}

					if (!TextUtils.isEmpty(carInfo.carstate.temperature1)
							&& !carInfo.carstate.temperature1.equals(INVALID_VALUE)) {
						mTvCarTemp1.setText(carInfo.carstate.temperature1 + "℃");
						mSpringProgressView1.setCurrentCount(Float.valueOf(carInfo.carstate.temperature1));
					} else {
						mTvCarTemp1.setText(getResources().getString(R.string.car_no_data));
						mSpringProgressView1.setCurrentCount(0);
					}

					if (!TextUtils.isEmpty(carInfo.carstate.temperature2)
							&& !carInfo.carstate.temperature2.equals(INVALID_VALUE)) {
						mTvCarTemp2.setText(carInfo.carstate.temperature2 + "℃");
						mSpringProgressView2.setCurrentCount(Float.valueOf(carInfo.carstate.temperature2));
					} else {
						mTvCarTemp2.setText(getResources().getString(R.string.car_no_data));
						mSpringProgressView2.setCurrentCount(0);
					}

					if (!TextUtils.isEmpty(carInfo.carstate.temperature3)
							&& !carInfo.carstate.temperature3.equals(INVALID_VALUE)) {
						mTvCarTemp3.setText(carInfo.carstate.temperature3 + "℃");
						mSpringProgressView3.setCurrentCount(Float.valueOf(carInfo.carstate.temperature3));
					} else {
						mTvCarTemp3.setText(getResources().getString(R.string.car_no_data));
						mSpringProgressView3.setCurrentCount(0);
					}

				} else {
					mTvBatteryvoltage.setTextColor(getResources().getColor(R.color.black));
					mTvCarSpeed.setTextColor(getResources().getColorStateList(R.color.black));
					mTvCoolantTemperature.setTextColor(getResources().getColorStateList(R.color.black));
					mTvEngineSpeed.setTextColor(getResources().getColorStateList(R.color.black));
					mTvRemainFuel.setTextColor(getResources().getColorStateList(R.color.black));
					mTvMomentFuel.setTextColor(getResources().getColorStateList(R.color.black));
					mTvBatteryvoltage.setText(getResources().getString(R.string.car_no_data));
					mTvCarSpeed.setText(getResources().getString(R.string.car_no_data));
					mTvCoolantTemperature.setText(getResources().getString(R.string.car_no_data));
					mTvEngineSpeed.setText(getResources().getString(R.string.car_no_data));
					mTvRemainFuel.setText(getResources().getString(R.string.car_no_data));
					mTvMomentFuel.setText(getResources().getString(R.string.car_no_data));
					mTvCarTemp1.setText(getResources().getString(R.string.car_no_data));
					mTvCarTemp2.setText(getResources().getString(R.string.car_no_data));
					mTvCarTemp3.setText(getResources().getString(R.string.car_no_data));

					mSpringProgressView1.setCurrentCount(0);
					mSpringProgressView2.setCurrentCount(0);
					mSpringProgressView3.setCurrentCount(0);
				}
				
				if (FreightConstant.isSaveFlow && mIsPauseMap){
					if (mMapView != null) {
						mMapView.onResume();// 当地图控件存在时，调用相应的恢复方法
						mMapView.update();
						mIsPauseMap = false;
						if (mTrackPointList.size() > 0) {
							showTrack(mTrackPointList,mArrayTrackList);
						}
					}
				}
			}
		};

		if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
			r.run();
		} else {
			mDataLayout.post(r);
		}
	}

	// 显示轨迹
	private void showTrack(TravelDetail detail) {
//		if (detail != null && detail.tracks != null && detail.tracks.size() > 0) {
//			MtqTrack tracks = detail.tracks.get(0);
//			if (tracks.pos_data != null && tracks.pos_data.size() > 0) {
//				mTrackPointList.clear();
//
//				Iterator<MtqPosData> iter = tracks.pos_data.iterator();
//				while (iter.hasNext()) {
//					MtqPosData tmp = iter.next();
//					LatLng point = new LatLng(Double.valueOf(tmp.y), Double.valueOf(tmp.x));
//					mTrackPointList.add(point);
//				}
//
//				showTrack(mTrackPointList);
//			}
//		}
		if (detail != null && detail.tracks != null && detail.tracks.size() > 0) {
			mTrackPointList.clear();
			mArrayTrackList.clear();
			for (int i = 0; i < detail.tracks.size(); i++){				
				MtqTrack tracks = detail.tracks.get(i);
				if (tracks.pos_data != null && tracks.pos_data.size() > 0) {
					ArrayList<LatLng> listTmp = new ArrayList<>();
					Iterator<MtqPosData> iter = tracks.pos_data.iterator();
					while (iter.hasNext()) {
						MtqPosData tmp = iter.next();
						LatLng point = new LatLng(Double.valueOf(tmp.y), Double.valueOf(tmp.x));
						mTrackPointList.add(point);
						listTmp.add(point);
					}
					mArrayTrackList.add(listTmp);
				}
			}
			
			showTrack(mTrackPointList,mArrayTrackList);
		}
	}

	private void showTrack(ArrayList<LatLng> trackPointList){
		if (mTrackline != null) {// 存在覆盖物的时候,先清除
			mMapView.getMap().removeOverlay(mTrackline);
		}
		
		mMapView.getMap().setLocationIconEnabled(false);
		// 添加覆盖物
		mTrackline = (MapPolyLine) mMapView.getMap()
				.addOverlay(new PolyLineOptions().color(Color.RED)// 设置线的颜色
						.points(mTrackPointList)// 设置经过的点
						.width(10));// 设置线的宽度
//						.dottedLine(false));

		mMapView.getMap().zoomToSpan(mTrackPointList);// 缩放到合适比例，将传入的点坐标都显示出来
		
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_freightpoint_poi, null);
		TextView txtNum = (TextView) view.findViewById(R.id.freight_point_poi_number);
		ImageView img = (ImageView) view.findViewById(R.id.freight_point_poi_img);
		txtNum.setText("起");
		img.setImageResource(R.drawable.ic_water_start);
		
		mMapView.getMap().addOverlay(new MarkerOptions().
				position(mTrackPointList.get(0)).layout(view));			
		
		view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_freightpoint_poi, null);
		txtNum = (TextView) view.findViewById(R.id.freight_point_poi_number);
		img = (ImageView) view.findViewById(R.id.freight_point_poi_img);
		txtNum.setVisibility(View.INVISIBLE);
		
		if (mIsOnline){
			img.setImageResource(R.drawable.icon_online);
		}else{
			img.setImageResource(R.drawable.icon_offline);
		}
		
		mMapView.getMap().addOverlay(new MarkerOptions().
				position(mTrackPointList.get(mTrackPointList.size() - 1)).layout(view));	
		
		GeoCode(mTrackPointList.get(mTrackPointList.size() - 1));
	}
	
	private void showTrack(ArrayList<LatLng> trackPointList,ArrayList<ArrayList<LatLng>> arrayTrackList){
		if (trackPointList.size() == 0){
			return;
		}
		mMapView.getMap().removeAllOverlay();
		mMapView.getMap().setLocationIconEnabled(false);
		
		mMapView.getMap().zoomToSpan(mTrackPointList);// 缩放到合适比例，将传入的点坐标都显示出来
		
		for(int i = 0;i < arrayTrackList.size(); i++){
			// 添加覆盖物
			mMapView.getMap().addOverlay(new PolyLineOptions().color(Color.RED)// 设置线的颜色
							.points(arrayTrackList.get(i))// 设置经过的点
							.width(10));// 设置线的宽度
		}
		
		
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_freightpoint_poi, null);
		TextView txtNum = (TextView) view.findViewById(R.id.freight_point_poi_number);
		ImageView img = (ImageView) view.findViewById(R.id.freight_point_poi_img);
		txtNum.setText("起");
		img.setImageResource(R.drawable.ic_water_start);
		
		mMapView.getMap().addOverlay(new MarkerOptions().
				position(mTrackPointList.get(0)).layout(view));			
		
		view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_freightpoint_poi, null);
		txtNum = (TextView) view.findViewById(R.id.freight_point_poi_number);
		img = (ImageView) view.findViewById(R.id.freight_point_poi_img);
		txtNum.setVisibility(View.INVISIBLE);
		
		if (mIsOnline){
			img.setImageResource(R.drawable.icon_online);
		}else{
			img.setImageResource(R.drawable.icon_offline);
		}
		
		mMapView.getMap().addOverlay(new MarkerOptions().
				position(mTrackPointList.get(mTrackPointList.size() - 1)).layout(view));	
		
		GeoCode(mTrackPointList.get(mTrackPointList.size() - 1));
	}
	
	private void updateDetail(Navi navi, TravelDetail detail) {
		showTrack(detail);

		// boolean isSameDay =
		// TimeUtils.isSameDayOfMillis(detail.navi.starttime,
		// detail.navi.endtime);
		// if (isSameDay) {
		// String day = TimeUtils.stampToDay(detail.navi.starttime);
		// String start = TimeUtils.stampToHour(detail.navi.starttime);
		// String end = TimeUtils.stampToHour(detail.navi.endtime);
		// } else {
		// String start = TimeUtils.stampToHour(detail.navi.starttime);
		// String end = TimeUtils.stampToHour(detail.navi.endtime);
		// }
		//
		// String carHint =
		// getResources().getString(R.string.car_condition_car);
		// String car = String.format(carHint, detail.carlicense);
		// if (!TextUtils.isEmpty(car)) {
		// // mPlate.setText(car);
		// } else {
		// MLog.e(TAG, "car: " + car);
		// }
		//
		// if (navi != null) {
		// String waybill = "";
		// List<MtqOrder> orderList = navi.orders;
		// if (orderList != null && !orderList.isEmpty()) {
		// /**
		// * 多个运单号，用逗号隔开
		// */
		// int len = orderList.size();
		// for (int i = 0; i < len; i++) {
		// waybill += orderList.get(i).cut_orderid;
		// if (i != len - 1) {
		// waybill += ", ";
		// }
		// }
		// }
		// String waybillHint = getResources().getString(
		// R.string.car_condition_waybill);
		// String bill = String.format(waybillHint, waybill);
		// if (!TextUtils.isEmpty(bill)) {
		// // mWaybill.setText(bill);
		// } else {
		// MLog.e(TAG, "bill: " + bill);
		// }
		// }

		// mFuel.setText(detail.navi.fuelcon);
		// mHundredFuel.setText(detail.hundred_fuel);
		// mIdleFuel.setText(detail.navi.idlefuelcon);
		// mIdleTime.setText(detail.navi.idletime);
		// mTotalTime.setText(detail.navi.traveltime);
		// mMileage.setText(detail.navi.mileage);
		// mMaxSpeed.setText(detail.navi.topspeed);
		// mAveSpeed.setText(detail.average_speed);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.title_right_img:
//			intent = new Intent(getActivity(), MsgActivity.class);
//			startActivity(intent);
			break;

		case R.id.tv_car_condition_car_number:
			// mPopup.showAsDropDown(mRoot, 0, 0);
			// intent = new Intent(getActivity(),CarSelectActivity.class);
			intent = new Intent(getActivity(), HistroyTravelActivity.class);
			startActivity(intent);
			break;

		case R.id.btn_no_car_check_route:
		case R.id.btn_car_check_route:
			// 历史行程

			WaitingProgressTool.showProgress(this._mActivity);

			CarManager.getInstance().getHistoryCarRoutes(new CarManager.IGetTaskListener() {
				@Override
				public void onGetResult(int errCode, List<MtqTask> listOfResult) {

					if(errCode == 0){



						if (errCode == 0 && listOfResult != null
								&& !listOfResult.isEmpty()) {
							CarUtils.parseTask(listOfResult, new OnBooleanListner() {
								@Override
								public void onResult(boolean res) {
									WaitingProgressTool.closeshowProgress();

									Intent intent = new Intent(getActivity(), HistroyTravelActivity.class);
									startActivity(intent);
								}
							});


						}else {

							WaitingProgressTool.closeshowProgress();
							Intent intent = new Intent(getActivity(), HistroyTravelActivity.class);
							startActivity(intent);
						}


					}else{
						WaitingProgressTool.closeshowProgress();
						Toast.makeText(CarFragment.this._mActivity,"获取历史行程失败",Toast.LENGTH_SHORT).show();

					}



				}
			},null);



			break;

		case R.id.btn_no_car_check_examination:
			// 历史体检
			intent = new Intent(getActivity(), CarCheckHistoryActivity.class);
			startActivity(intent);
			break;

		case R.id.btn_car_check_examination:
			// 车辆体检
			// Toast.makeText(getActivity(), "历史体检", Toast.LENGTH_SHORT).show();
			intent = new Intent(getActivity(), CarCheckActivity.class);

			MLog.e("carcheck", SPHelper.getInstance(getActivity()).getRecentCaruid() + " "
					+ SPHelper.getInstance(getActivity()).getRecentCarLicense());
			intent.putExtra("caruid", SPHelper.getInstance(getActivity()).getRecentCaruid());
			intent.putExtra("carlicense", SPHelper.getInstance(getActivity()).getRecentCarLicense());

			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		startTimer(INTERVAL_MAXTIME);
	}

	@Override
	public void onPause() {

		super.onPause();
		if (mMapView != null) {
			mMapView.onPause();
		}
	}

	// @Subscribe(threadMode = ThreadMode.MAIN)
	// public void onMessageEvent(NewMsgEvent event) {
	// switch (event.msgId) {
	// case MessageId.MSGID_MSG_NEW: {
	// updateNews();
	// break;
	// }
	// default:
	// break;
	// }
	// }

	private void startTimer(int time) {
		mIntervalTime = time;
		mHander.removeCallbacks(mTimeRunnale);
		mHander.postDelayed(mTimeRunnale, time);
	}

	private void stopTimer() {
		mHander.removeCallbacks(mTimeRunnale);
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
		EventBus.getDefault().unregister(this);
		mTimeRunnale = null;
		mHander = null;
		mMapView = null;
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(TravelDetailEvent event) {
		if (event != null) {
			switch (event.msgId) {
			case MessageId.MSGID_GET_LATEST_ROUTE_SUCCESS: {
				break;
			}
			case MessageId.MSGID_GET_LATEST_ROUTE_FAILED: {

				break;
			}

			// 获取行驶车辆动态信息失败
			case MessageId.MSGID_GET_TASKNAVI_FAIL: {
				dealDetailFailed();
				break;
			}
			case MessageId.MSGID_REQUEST_TIMEOUT:
				Toast.makeText(getContext(), "请求超时，请稍后重试", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 设备是否离线
	 */

	public boolean IsDeviceOffLine(MtqCarstate carState) {

		long updatetime = -1;
		try {

			updatetime = Long.valueOf(carState.time);

		} catch (Exception e) {
			// TODO: handle exception
			updatetime = -1;
		}

		// 上次车况数据上传时间距离现在超过10分钟则为离线
		if (updatetime != -1 && updatetime > 0) {
			if (CldKDeviceAPI.getSvrTime() - updatetime >= 600) {

				return true;
			}

		}

		// 如果车辆的发动机状态为熄火，或发动机速度为0为离线
		if (carState.enginestatus.equals("0"))
			return true;
		else if (carState.enginestatus.equals("1"))
			return false;
		else {

			if (carState.enginespeed.equals("0"))
				return true;
			
		}

		return false;

	}
	
	public void GeoCode(LatLng latLng) {

		ReverseGeoCodeOption option = new ReverseGeoCodeOption();

		// 设置逆地理坐标的经度值
		option.location.longitude = latLng.longitude;

		// 设置逆地理坐标的纬度值
		option.location.latitude = latLng.latitude;

		getGeoCoder().setOnGetGeoCodeResultListener(this);
		try {
			getGeoCoder().reverseGeoCode(option);// 传入逆地理参数对象
		} catch (IllegalSearchArgumentException e) {

			e.printStackTrace();
		}

	}
	
	public GeoCoder getGeoCoder() {
		if (mGeoCoder == null){
			mGeoCoder = GeoCoder.newInstance();
		}
		return mGeoCoder;
	}

	
	@Override
	public void onGetGeoCodeResult(GeoCodeResult geocoderesult) {

		
	}

	@Override
	public void onGetReverseGeoCodeResult(
			ReverseGeoCodeResult result) {

		if (result != null) {
			if (result.errorCode != 0) {
				//失败
				mCurrentLocation.post(new Runnable() {
					
					@Override
					public void run() {

						mCurrentLocation.setVisibility(View.INVISIBLE);
					}
				});
				return;
			}
			if (TextUtils.isEmpty(result.address)) {
				// 返回地址是否为空判断
				mCurrentLocation.post(new Runnable() {					
					@Override
					public void run() {

						mCurrentLocation.setVisibility(View.INVISIBLE);
					}
				});
			} else {
				final String address = result.address;
				mCurrentLocation.post(new Runnable() {					
					@Override
					public void run() {

						mCurrentLocation.setVisibility(View.VISIBLE);
						mCurrentLocation.setText(address);
					}
				});
			}
		}else{
			mCurrentLocation.post(new Runnable() {				
				@Override
				public void run() {

					mCurrentLocation.setVisibility(View.INVISIBLE);
				}
			});
		}
	}
	
}
