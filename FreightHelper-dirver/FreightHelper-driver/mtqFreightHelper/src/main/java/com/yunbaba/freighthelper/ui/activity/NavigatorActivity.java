package com.yunbaba.freighthelper.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cld.location.CldLocationManager;
import com.cld.mapapi.frame.CldSdkCxt;
import com.cld.mapapi.map.MapView;
import com.cld.mapapi.map.MarkerOptions;
import com.cld.mapapi.model.LatLng;
import com.cld.mapapi.util.DistanceUtil;
import com.cld.navisdk.guide.BaseNavigorView.OnStopListener;
import com.cld.navisdk.guide.CldNavigatorView;
import com.cld.navisdk.guide.IOnGuideListener;
import com.cld.navisdk.routeplan.CldRoutePlaner;
import com.cld.nv.guide.CldHudInfo;
import com.cld.nv.location.CldGpsEmulator;
import com.cld.nv.map.CldMapApi;
import com.cld.nv.map.overlay.Overlay;
import com.cld.nv.util.CldNaviUtil;
import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.api.map.MapViewAPI;
import com.yunbaba.api.map.NavigateAPI;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.NavigateTaskOperator;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.TaskOperator.OnDialogListener;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.api.trunk.bean.UpdateTaskPointStatusResult;
import com.yunbaba.api.trunk.bean.UpdateTaskStatusResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeNaviActivity;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointStatusRefreshEvent;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointUpdateEvent;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.bean.eventbus.UpdateTaskStatusEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.ui.activity.task.TaskPointDetailActivity;
import com.yunbaba.freighthelper.ui.activity.task.UploadPaymentActivity;
import com.yunbaba.freighthelper.ui.activity.task.feedback.FeedBackDialog;
import com.yunbaba.freighthelper.ui.customview.TaskAskPopUpDialog;
import com.yunbaba.freighthelper.ui.customview.TaskDialoginNavigation;
import com.yunbaba.freighthelper.ui.customview.TaskDialoginNavigation.NavigationDlgType;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.TaskStatus;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IGetFeedBackInfoListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBackInfo;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import hmi.packages.HPDefine.HPWPoint;

public class NavigatorActivity extends BaseButterKnifeNaviActivity {

	@BindView(R.id.navigation_mapView)
	FrameLayout mMapLayout;

	private MapView mMapView;
	private CldNavigatorView mNavigatorView;

	String corpid;
	String taskid;

	MtqDeliStoreDetail mStoreDetail;
	MtqDeliOrderDetail mOrderDetail;
	MtqDeliTaskDetail mTaskDetail;

	String jsonStore;
	String jsonOrder;
	String jsonDeliTaskDetail;
	FeedBackDialog mFeedBackDialog;
	TaskDialoginNavigation dlg;
	boolean isAddMarker = false;
	private Overlay mOverlay;
	static CldGpsEmulator emulator;
    boolean isFromTaskDetail = false;
	int mResultCode = 0;

	private static boolean mIsGpsEmu = false;;

	@Override
	public int getLayoutId() {

		return R.layout.activity_navigation;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initData();
		initView();
		initControl();
		EventBus.getDefault().register(this);








		MLog.e("addmarker","addmarker");

//		List<OverlayOptions> options = new ArrayList<OverlayOptions>();
//
//		View view = LayoutInflater.from(NavigatorActivity.this).inflate(R.layout.layout_freightpoint_poi, null);
//		ImageView img = (ImageView) view.findViewById(R.id.freight_point_poi_img);
//		img.setImageResource(R.drawable.icon_finally_mark);
//
//		LatLng point = new LatLng((double) mStoreDetail.storey, (double)mStoreDetail.storex);
//		options.add(new MarkerOptions().position(point).layout(view));
//
//		MapViewAPI.getInstance().addOverlay(mMapView.getMap(), options);


//		ReflectResource reflectResource;
//		reflectResource = new ReflectResource(this);
//		View v = reflectResource.getResLayoutView(this, "navisdk_navigator",
//				ReflectResource.ResourcesType.LAYOUT);
//
//
//		View layout_rlt_bottom_tool = (RelativeLayout)reflectResource.getResWidgetView(v, "bottom_view", ReflectResource.ResourcesType.ID);
//
//
//		layout_rlt_bottom_tool.setVisibility(View.GONE);


	}


	public void AddMarker(){

		HPWPoint point2 =  new HPWPoint();
		point2.x = mStoreDetail.storex;
		point2.y = mStoreDetail.storey;


//				LatLng lg = CldCoordUtil.cldToGCJ(point2);
//				lg = CldCoordUtil.gcjToCLDEx(lg);

		LatLng lg = new LatLng();
		lg.latitude = point2.y;
		lg.longitude = point2.x;


//		options.add(new MarkerOptions().position(point).layout(view));
		mOverlay = mMapView.getMap().addOverlay(new MarkerOptions()
				.position(lg).icon(
						getResources().getDrawable(R.drawable.icon_finally_mark))
				.rotate(0).alpha(1f));


	}

	private void initControl() {
		mNavigatorView.setOnGuideListener(new IOnGuideListener() {

			@Override
			public void onYaWingPlanSuccess() {


			}

			@Override
			public void onYaWingPlanStart() {

				if(mStoreDetail == null)
					return;



			}

			@Override
			public void onYaWingPlanFail(int i) {


			}

			@Override
			public void onOverSpeed(int i) {


			}

			@Override
			public void onHudUpdate(CldHudInfo cldhudinfo) {


			}

			@Override
			public void onCityChange(String s, String s1) {


			}

			@Override
			public void onArrivePass(Object obj) {


			}

			@Override
			public void onArriveDestNear() {


				if(mStoreDetail == null)
					return;


				isAddMarker = true;

				AddMarker();


			}

			@Override
			public void onArriveDest() {

				//showArrivedDestDlg();

			}
		});

		mNavigatorView.setOnStopListener(new OnStopListener() {

			@Override
			public void onStop() {


			}

			@Override
			public boolean onBeforeStop() {

//				if (TextUtils.isEmpty(taskid)) {
//					return false;
//				}
//				MtqDeliTask deliTask = TaskOperator.getInstance().getmCurrentTask();
//				if (deliTask == null) {
//					// 如果单已经结束了就不弹运货点的框
//					return false;
//				}
//
//				if (mStoreDetail.storestatus == FreightConstant.TASK_PAUSE || mStoreDetail.storestatus == FreightConstant.TASK_FINISH ||
//						mStoreDetail.storestatus == FreightConstant.TASK_READY
//						) {
//					// 如果点已经暂停运货就不弹运货点的框
//					return false;
//				}
//
//				NavigateTaskOperator.getInstance().setmStoreDetail(mStoreDetail);
//				NavigateTaskOperator.getInstance().setmOrderDetail(mOrderDetail);
//				NavigateTaskOperator.getInstance().showTaskStatusChangeDialog(NavigatorActivity.this, null,
//						NavigationDlgType.EXIT_NAVIGATION, new OnDialogListener() {
//
//							@Override
//							public void OnDialogDismiss() {
//
//
//							}
//
//							@Override
//							public void OnClickRight(UpdateTaskStatusEvent event) {
//								// 关闭导航
//								mNavigatorView.stopNavi();
//							}
//
//							@Override
//							public void OnClickLeft(UpdateTaskStatusEvent event) {
//								// 暂停运货
//
//								if (mOrderDetail != null) {
//
//									ShowFeedBackDialog(true, false);
//
//								} else {
//
//									WaitingProgressTool.showProgress(NavigatorActivity.this);
//
//									stopDeliver(mStoreDetail, new Runnable() {
//
//										@Override
//										public void run() {
//
//											if (mResultCode != 0) {
//												setResult(mResultCode);
//											}
//											mNavigatorView.stopNavi();
//										}
//									});
//
//								}
//							}
//						});
//

//				ReflectResource reflectResource;
//				reflectResource = new ReflectResource(NavigatorActivity.this);
//				View v = reflectResource.getResLayoutView(NavigatorActivity.this, "navisdk_navigator",
//						ReflectResource.ResourcesType.LAYOUT);
//
//
//				RelativeLayout layout_rlt_bottom_tool = (RelativeLayout)reflectResource.getResWidgetView(v, "bottom_view",
//						ReflectResource.ResourcesType.ID);
//
//
//				layout_rlt_bottom_tool.setVisibility(View.INVISIBLE);
//
//				TextView tv_whole_remaining = (TextView) reflectResource.getResWidgetView(v,
//						"tv_whole_remaining", ReflectResource.ResourcesType.ID);
//
//				tv_whole_remaining.setVisibility(View.INVISIBLE);

				ShowAskDialog();

				return  true;
			}
		});


	}

	private void initData() {
		jsonStore = getIntent().getStringExtra("storedetail");
		if (!TextUtils.isEmpty(jsonStore)) {
			mStoreDetail = GsonTool.getInstance().fromJson(jsonStore, MtqDeliStoreDetail.class);
		}

		if (getIntent().hasExtra("corpid"))
			corpid = getIntent().getStringExtra("corpid");

		if (getIntent().hasExtra("taskid"))
			taskid = getIntent().getStringExtra("taskid");

		if (getIntent().hasExtra("orderdetail")) {
			jsonOrder = getIntent().getStringExtra("orderdetail");
			mOrderDetail = GsonTool.getInstance().fromJson(jsonOrder, MtqDeliOrderDetail.class);
		}

		if (getIntent().hasExtra("taskdetail")) {
			jsonDeliTaskDetail = getIntent().getStringExtra("taskdetail");
			mTaskDetail = GsonTool.getInstance().fromJson(jsonDeliTaskDetail, MtqDeliTaskDetail.class);
		}


		if(getIntent().hasExtra("isFromTaskDetail")){

			isFromTaskDetail =  getIntent().getBooleanExtra("isFromTaskDetail",false);

		}

	}

	private void initView() {
		// 创建导航视图
		mMapView = MapViewAPI.getInstance().createMapView(this);
		// 初始化导航控件
		mNavigatorView = NavigateAPI.getInstance().init(this, mMapView);
		mNavigatorView.setFocusable(true);
		mNavigatorView.setAutoFinish(false);
		mMapLayout.addView(mNavigatorView);




		dlg = new TaskDialoginNavigation(this);

		// 开始导航
		NavigateAPI.getInstance().startNavi();

		// 让屏幕保持不暗不关闭
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		openGpsEmu(this);
	}

	public void onBackPressed() {

		NavigateAPI.getInstance().onBackPressed();// 调用导航模式相应回退方法
		// showArrivedDestDlg();

	}

	/**
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {

		super.onStop();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (null != mMapView) {
			mMapView.onPause();// 当地图控件存在，调用地图控件暂停方法
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (null != mMapView) {
			mMapView.onResume();// 当地图控件存在，调用地图控件恢复方法
			mMapView.update();
		}
		NavigateAPI.getInstance().onResume();



		if(mOverlay!=null){
			AddMarker();
		}


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);

		CldRoutePlaner.getInstance().clearRoute();
		if (null != mMapView) {
			mMapView.destroy();// 当地图控件存在时，销毁地图控件
		}

		if (mIsGpsEmu) {
			emulator.stop();
			mIsGpsEmu = false;
		}
	}

	/**
	 * @Title: showArrivedDestDlg
	 * @Description: 显示到达对话框
	 * @return: void
	 */
	private void showArrivedDestDlg() {
		if (TextUtils.isEmpty(taskid)) {
			return;
		}
		MtqDeliTask deliTask = TaskOperator.getInstance().getmCurrentTask();
		if (deliTask == null) {
			// 如果单已经结束了就不弹运货点的框
			return;
		}

		if (mStoreDetail.storestatus == FreightConstant.TASK_PAUSE || mStoreDetail.storestatus == FreightConstant.TASK_FINISH ||
				mStoreDetail.storestatus == FreightConstant.TASK_READY
				) {
			// 如果点已经暂停运货就不弹运货点的框
			return ;
		}

		NavigateTaskOperator.getInstance().setmStoreDetail(mStoreDetail);
		NavigateTaskOperator.getInstance().setmOrderDetail(mOrderDetail);
		NavigateTaskOperator.getInstance().showTaskStatusChangeDialog(NavigatorActivity.this, null,
				NavigationDlgType.ARRIVE_DEST, new OnDialogListener() {

					@Override
					public void OnDialogDismiss() {


					}

					@Override
					public void OnClickRight(UpdateTaskStatusEvent event) {

						if (mOrderDetail != null) {
							// 跳到收款界面
							Intent intent = new Intent(NavigatorActivity.this, UploadPaymentActivity.class);
							intent.putExtra("storedetail", jsonStore);

							// 添加taskid
							intent.putExtra("taskid", taskid);
							intent.putExtra("corpid", corpid);
							intent.putExtra("isFromTaskDetail", isFromTaskDetail);
							if (mOrderDetail != null) {
								// 添加orderdetail
								intent.putExtra("orderdetail", jsonOrder);
							}
							startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);
							mNavigatorView.stopNavi();
						} else {
							// 跳转到选择运货点界面
							final MtqDeliTask deliTask = TaskOperator.getInstance().getmCurrentTask();
						//	final int nofinishcnt = deliTask.store_count - deliTask.finish_count;

							Runnable r = null;

							int nofinishcnt =	getTaskStatusIsFinish(mStoreDetail);

							if (nofinishcnt != 2) {
								r = new Runnable() {
									@Override
									public void run() {

										//
										if (mStoreDetail.optype == 4) // 回程
										{

											final MtqDeliStoreDetail nextStore = getNextStorePoint();

											boolean isNeedGotoNext = false;

											if (nextStore != null) {

												HPWPoint point = CldMapApi.getMapCenter();

												HPWPoint nextPoint = new HPWPoint();
												nextPoint.x = nextStore.storex;
												nextPoint.y = nextStore.storey;

												LatLng p1LL = new LatLng(nextPoint.y,nextPoint.x);


													//	CldCoordUtil.gcjToCLDEx(CldCoordUtil. cldToGCJ(nextPoint));

												LatLng p2LL = new LatLng(point.y,point.x);

														//CldCoordUtil.gcjToCLDEx(CldCoordUtil.cldToGCJ(point));

												double dis = DistanceUtil.getDistance(p1LL, p2LL);

												if (dis < 50)
													isNeedGotoNext = true;

											}

											// 距离下一个运货点小于50米
											if (isNeedGotoNext) {

												Runnable rr = new Runnable() {

													@Override
													public void run() {
														// TODO Auto-generated
														// method stub
														mStoreDetail = nextStore;
														mOrderDetail = getOrderDetail(nextStore);

														showArrivedDestDlg();

													}
												};

												startDeliver(nextStore, rr);

											} else { // 距离下一个运货点大于50米

//												Intent intent = new Intent(NavigatorActivity.this,
//														SelectTransPointActivity.class);
//												intent.putExtra("corpid", deliTask.corpid);
//												intent.putExtra("taskid", deliTask.taskid);
//												intent.putExtra("isFromTaskDetail", isFromTaskDetail);
//												startActivity(intent);
												mNavigatorView.stopNavi();

											}

										} else { // 非回程

//											Intent intent = new Intent(NavigatorActivity.this,
//													SelectTransPointActivity.class);
//											intent.putExtra("corpid", deliTask.corpid);
//											intent.putExtra("taskid", deliTask.taskid);
//											intent.putExtra("isFromTaskDetail", isFromTaskDetail);
//											startActivity(intent);
											mNavigatorView.stopNavi();

										}
									}
								};
								finishDeliver(mStoreDetail, -1, r);
							} else {
								r = new Runnable() {
									@Override
									public void run() {

										Toast.makeText(NavigatorActivity.this, "任务完成", Toast.LENGTH_SHORT).show();
										mNavigatorView.stopNavi();
									}
								};
								finishDeliver(mStoreDetail, TaskStatus.DELIVERRED, r);
							}
						}

					}

					@Override
					public void OnClickLeft(UpdateTaskStatusEvent event) {

						WaitingProgressTool.showProgress(NavigatorActivity.this);
						stopDeliver(mStoreDetail, new Runnable() {

							@Override
							public void run() {

								mNavigatorView.stopNavi();
							}
						});
					}
				});
	}

	private void stopDeliver(MtqDeliStoreDetail detail, Runnable callback) {
		// 测速要求不改单的状态
		// updateStoreStatus(detail,TaskStatus.PAUSE_DELIVER,"",-1,callback);
		UpdateTaskStatusEvent event = new UpdateTaskStatusEvent(corpid, taskid, 3, corpid, taskid, 0, 0, 0, 0);
		HandleTaskStatusChangeEvent(event, 3, detail, callback);
		// if (isNeedchangeTaskStatus(detail,mTaskDetail.getStore())){
		// updateStoreStatus(detail,TaskStatus.PAUSE_DELIVER,"",TaskStatus.PAUSE_DELIVER,callback);
		//
		// }else{
		// updateStoreStatus(detail,TaskStatus.PAUSE_DELIVER,"",-1,callback);
		// }
	}

	private void startDeliver(MtqDeliStoreDetail detail, Runnable callback) {
		// 测速要求不改单的状态
		// updateStoreStatus(detail,TaskStatus.PAUSE_DELIVER,"",-1,callback);
		updateStoreStatus(detail, TaskStatus.DELIVERRING, "", -1, callback);

	}

	private void finishDeliver(MtqDeliStoreDetail detail, int taskstatus, Runnable callback) {
		updateStoreStatus(detail, TaskStatus.DELIVERRED, "", taskstatus, callback);
	}

	private boolean isNeedchangeTaskStatus(MtqDeliStoreDetail detail, List<MtqDeliStoreDetail> storelist) {
		boolean change = true;

		Iterator<MtqDeliStoreDetail> iterator = storelist.iterator();

		while (iterator.hasNext()) {
			MtqDeliStoreDetail tmp = iterator.next();
			if (!tmp.waybill.equals(detail.waybill) && TaskStatus.DELIVERRED != tmp.storestatus
					&& TaskStatus.PAUSE_DELIVER != tmp.storestatus) {
				// 如果有一个非当前点的状态不是完成送货或者暂停状态，就不用改变单的状态
				change = false;
			}
		}

		return change;
	}

	private MtqDeliStoreDetail getNextStorePoint() {

		Iterator<MtqDeliStoreDetail> iterator = mTaskDetail.store.iterator();

		MtqDeliStoreDetail res = null;

		findNeedStorePoint: while (iterator.hasNext()) {
			MtqDeliStoreDetail tmp = iterator.next();
			if (!tmp.waybill.equals(mStoreDetail.waybill) && TaskStatus.DELIVERRED != tmp.storestatus) {

				res = tmp;

				break findNeedStorePoint;
			}
		}

		return res;
	}

	private MtqDeliOrderDetail getOrderDetail(MtqDeliStoreDetail target) {

		MtqDeliOrderDetail res = null;

		findOrderPoint: for (MtqDeliOrderDetail order : mTaskDetail.getOrders()) {
			if (order.waybill.equals(target.waybill)) {

				res = order;
				break findOrderPoint;
			}
		}

		return res;
	}

	private void updateStoreStatus(final MtqDeliStoreDetail detail, final int status, final String ewaybill,
			final int taskStaus, final Runnable callback) {

		DeliveryApi.getInstance().UpdateStoreStatus(this,corpid, taskid, detail.storeid, status, detail.waybill, ewaybill,
				taskStaus, new OnResponseResult<UpdateTaskPointStatusResult>() {

					@Override
					public void OnResult(UpdateTaskPointStatusResult result) {

						MLog.e("check", "" + result.getErrCode());
						WaitingProgressTool.closeshowProgress();
						if (isFinishing())
							return;
						if (result.getErrCode() != 0) {
							if (NavigatorActivity.this != null) {
								Toast.makeText(NavigatorActivity.this,  TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							if (result.getTaskid() == null) {
								result.setTaskid(taskid);
							}

							// if (result.getErrCode() == 1900 && taskStaus ==
							// TaskStatus.PAUSE_DELIVER){
							// UpdateTaskStatusResult taskStatusResult = new
							// UpdateTaskStatusResult(0,
							// corpid, taskid, corpid, taskid, taskStaus);
							// TaskOperator.getInstance().HandleResultAfterRequest(taskStatusResult);
							// EventBus.getDefault().post(new
							// FreightPointStatusRefreshEvent());
							// mResultCode =
							// FreightConstant.TASK_POINT_INFO_NEED_UPDATE;
							// }else{
							// mResultCode =
							// FreightConstant.TASK_POINT_INFO_NEED_UPDATE;
							// }

							if (taskStaus == TaskStatus.DELIVERRED) {
								UpdateTaskStatusResult taskStatusResult = new UpdateTaskStatusResult(0, corpid, taskid,
										corpid, taskid, taskStaus);
								TaskOperator.getInstance().HandleResultAfterRequest(taskStatusResult);
								EventBus.getDefault().post(new FreightPointStatusRefreshEvent());

								mTaskDetail.setFinishcount(mTaskDetail.getFinishcount() + 1);
							}
							EventBus.getDefault().post(new FreightPointUpdateEvent());
							mResultCode = FreightConstant.TASK_POINT_INFO_NEED_UPDATE;

							if (NavigatorActivity.this != null) {
								// Toast.makeText(NavigatorActivity.this,
								// R.string.request_success,
								// Toast.LENGTH_SHORT).show();

								// 成功之后更新本地的状态。
								detail.storestatus = status;

								result.setTaskid(taskid);
								result.setStoreid(detail.storeid);
								result.setStatus(status);
								result.setWaybill(detail.waybill);
								result.setEwaybill(ewaybill);
								TaskOperator.getInstance().UpdateTaskStateByStoreStatusChangeResult(result);
							}

							if (callback != null) {
								callback.run();
							}
						}
					}

					@Override
					public void OnError(int ErrCode) {

						WaitingProgressTool.closeshowProgress();
						if (isFinishing())
							return;
						if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
							// Toast.makeText(NavigatorActivity.this,
							// "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
						} else
							Toast.makeText(NavigatorActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
					}

					@Override
					public void OnGetTag(String Reqtag) {


					}
				}, detail.waybill);
	}

	// 处理任务状态变更
	public void HandleTaskStatusChangeEvent(UpdateTaskStatusEvent event, final int statusOperateType,
			final MtqDeliStoreDetail detail, final Runnable callback) {

		final String ecorpid = TaskOperator.getInstance().getmCurrentTask() == null ? ""
				: TaskOperator.getInstance().getmCurrentTask().corpid;
		final String etaskid = TaskOperator.getInstance().getmCurrentTask() == null ? ""
				: TaskOperator.getInstance().getmCurrentTask().taskid;

		DeliveryApi.getInstance().UpdateTaskInfo(NavigatorActivity.this,corpid, taskid, event.getStatus(), ecorpid, etaskid, event.getX(),
				event.getY(), event.getCell(), event.getUid(), new OnResponseResult<UpdateTaskStatusResult>() {

					@Override
					public void OnResult(UpdateTaskStatusResult result) {


						// MLog.e("updatetask", result.getErrCode() + " " +
						// result.getStatus() + " " + result.getTaskid());
						WaitingProgressTool.closeshowProgress();
						if (isFinishing())
							return;

						if (result.getErrCode() != 0) {
							// 请求错误
							Toast.makeText(NavigatorActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT).show();
							return;
						}

						TaskOperator.getInstance().HandleResultAfterRequest(result);
						EventBus.getDefault().post(new FreightPointStatusRefreshEvent());
						EventBus.getDefault().post(new FreightPointUpdateEvent());
						if (statusOperateType == 3) {

							if(!isFromTaskDetail) {

								Intent intent = new Intent(NavigatorActivity.this, TaskPointDetailActivity.class);

								// 添加storedetail
								detail.storestatus = statusOperateType;
								String str = GsonTool.getInstance().toJson(detail);
								intent.putExtra("storedetail", str);

								// 添加taskid
								intent.putExtra("taskid", detail.taskId);
								intent.putExtra("corpid", detail.corpId);
								intent.putExtra("isFromTaskDetail", isFromTaskDetail);
								MtqDeliOrderDetail orderdetail = TaskOperator.getInstance()
										.GetorderDetailFromDB(detail.taskId, detail.waybill);

								if (orderdetail != null) {
									String str2 = GsonTool.getInstance().toJson(orderdetail);
									intent.putExtra("orderdetail", str2);
								}

								startActivity(intent);
								// Toast.makeText(NavigatorActivity.this,
								// R.string.request_success, Toast.LENGTH_SHORT)
								// .show();
							}
							setResult(FreightConstant.TASK_POINT_INFO_NEED_UPDATE);
						}

						if (callback != null) {
							callback.run();
						}
					}

					@Override
					public void OnError(int ErrCode) {

						if (isFinishing())
							return;

						// MLog.e("updatetask", ErrCode + " error");
						if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
							// Toast.makeText(NavigatorActivity.this,
							// "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
						} else
							Toast.makeText(NavigatorActivity.this, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
						WaitingProgressTool.closeshowProgress();
					}

					@Override
					public void OnGetTag(String Reqtag) {


					}
				});
	}

	/**
	 * @Description 启动GPS模拟
	 * @param context
	 * @return boolean 开启是否成功
	 */
	public static boolean openGpsEmu(Context context) {
		if (!CldNaviUtil.isTestVerson()) {
			// 只有测试版才能用
			return false;
		}

		String strGpsLog = CldSdkCxt.getAppPath() + "/" + "GPS_EMU.LOG";
		File fileGpsLog = new File(strGpsLog);
		if (fileGpsLog.exists()) {
			// 关闭定位功能
			CldLocationManager.getInstance().stopLocation();

			// 启动GPS模拟定位
			emulator = new CldGpsEmulator();
			emulator.start();
			mIsGpsEmu = true;
			Toast.makeText(context, "开启GPS模拟定位功能!", Toast.LENGTH_SHORT).show();
		} else {
			mIsGpsEmu = false;
			// Toast.makeText(context, "未找到模拟日志GPS_EMU.LOG文件!",
			// Toast.LENGTH_SHORT)
			// .show();
		}
		return true;
	}

	@Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
	public synchronized void onTaskBusinessMsgEvent(TaskBusinessMsgEvent event) {

		switch (event.getType()) {

		// 任务刷新
		case 2:

			break;

		// 作废任务，删除某些任务
		case 3:

			if (mStoreDetail == null)
				return;

			if (isFinishing())
				return;

			if (event.getTaskIdList() != null && event.getTaskIdList().size() > 0) {

				if (TextStringUtil.isContainStr(event.getTaskIdList(), mStoreDetail.taskId)) {

					// Toast.makeText(this, "当前操作任务单已撤回",
					// Toast.LENGTH_LONG).show();
					if (mNavigatorView != null)
						mNavigatorView.stopNavi();




				}

			}

			break;
		case 4:
			if (mStoreDetail == null)
				return;

			if (isFinishing())
				return;

			if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {

				if (mOrderDetail != null && !TextUtils.isEmpty(mOrderDetail.waybill)) {

					if (TextStringUtil.isContain(event.getRefreshtaskIdList(), mStoreDetail.taskId,
							mOrderDetail.waybill)) {

						if (mNavigatorView != null)
							mNavigatorView.stopNavi();
					}

				}
			}

			break;

		default:
			break;
		}

	}

	public void ShowFeedBackDialog(final boolean isNeedStopTask, final boolean isLast) {

		showProgressBar();

		CldBllKDelivery.getInstance().getFeedBackReasonInfo(corpid, new IGetFeedBackInfoListener() {

			@Override
			public void onGetResult(int errCode, List<FeedBackInfo> result) {

				if (isFinishing())
					return;


				hideProgressBar();

				if (errCode == 0 && result != null) {
					//
					// ArrayList<FeedBackInfo > list = new ArrayList<>();
					// FeedBackInfo info = new FeedBackInfo();
					// info.name = "客户原因";
					// FeedBackInfo info2 = new FeedBackInfo();
					// info2.name = "客户原因2";
					// FeedBackInfo info3 = new FeedBackInfo();
					// info3.name = "客户原因3";
					//
					// list.add(info);
					// list.add(info2);
					// list.add(info3);
					mFeedBackDialog = new FeedBackDialog(NavigatorActivity.this, (ArrayList<FeedBackInfo>) result,
							mStoreDetail, mOrderDetail, isNeedStopTask ? 2 : 1);
					mFeedBackDialog.show();

					if (isNeedStopTask) {

						mFeedBackDialog.setOnDismissListener(new OnDismissListener() {

							@Override
							public void onDismiss(DialogInterface dialog) {

								WaitingProgressTool.showProgress(NavigatorActivity.this);

								stopDeliver(mStoreDetail, new Runnable() {

									@Override
									public void run() {

										if (mResultCode != 0) {
											setResult(mResultCode);
										}
										mNavigatorView.stopNavi();
									}
								});
							}
						});

					} else {

						mFeedBackDialog.setOnDismissListener(null);

					}

				} else
					Toast.makeText(NavigatorActivity.this, "获取反馈信息失败", Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onGetReqKey(String arg0) {


			}
		});

	}

	private void showProgressBar() {

		WaitingProgressTool.showProgress(this);
	}

	private void hideProgressBar() {

		WaitingProgressTool.closeshowProgress();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode >= FeedBackDialog.IMAGE_CAPTURE - 1 && requestCode <= FeedBackDialog.IMAGE_CAPTURE + 2 ||

				requestCode >= FeedBackDialog.IMAGE_SELECT - 1 && requestCode <= FeedBackDialog.IMAGE_SELECT + 2)

		{

			if (mFeedBackDialog != null)
				mFeedBackDialog.onActivityResult(requestCode, resultCode, data);
			return;
		}
	}

	public  int getTaskStatusIsFinish(MtqDeliStoreDetail storedetail) {

		MtqDeliTaskDetail taskdetail = TaskOperator.getInstance().getTaskDetailDataFromDB(storedetail.taskId);
		if (taskdetail == null || taskdetail.store == null)
			return -1;


		boolean isfinish = true;

		over: for (MtqDeliStoreDetail store : taskdetail.getStore()) {

			if (!store.waybill.equals(mStoreDetail.waybill) && store.storestatus != TaskStatus.DELIVERRED) {

				isfinish = false;
				break over;
			}

		}
		MLog.e("check isfinish", "" + isfinish);

		if (isfinish)
			return 2;
		else
			return -1;

	}




	TaskAskPopUpDialog mPopUpDialog;

	public void ShowAskDialog() {


        mPopUpDialog = new TaskAskPopUpDialog(this);

        mPopUpDialog.show();

        mPopUpDialog.setDialogType(11);

        // final MtqDeliStoreDetail detail = mS

        // 选择优先运货到 " + detail.storename + " ,是否继续?"

        mPopUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopUpDialog.tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                mPopUpDialog.dismiss();


            }
        });
        mPopUpDialog.tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

				AppStatApi.statOnEventWithLoginName(getApplicationContext(), AppStatApi.BD_STAT_NAVI_CLOSE_CLICK);

                mPopUpDialog.dismiss();

				mNavigatorView.stopNavi();
                //   finish();

            }
        });

	}











}
