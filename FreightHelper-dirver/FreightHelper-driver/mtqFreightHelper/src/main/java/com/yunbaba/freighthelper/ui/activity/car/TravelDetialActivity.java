package com.yunbaba.freighthelper.ui.activity.car;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.mapapi.map.MapView;
import com.cld.mapapi.map.MarkerOptions;
import com.cld.mapapi.map.PolyLineOptions;
import com.cld.mapapi.model.LatLng;
import com.cld.nv.map.overlay.impl.MapPolyLine;
import com.yunbaba.api.map.MapViewAPI;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.car.Navi;
import com.yunbaba.freighthelper.bean.car.TravelDetail;
import com.yunbaba.freighthelper.manager.CarManager;
import com.yunbaba.freighthelper.manager.CarManager.IGetTaskDetailListener;
import com.yunbaba.freighthelper.utils.CallUtil;
import com.yunbaba.freighthelper.utils.CarUtils;
import com.yunbaba.freighthelper.utils.CommonTool;
import com.yunbaba.freighthelper.utils.ErrCodeUtil;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.TimeUtils;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqOrder;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqPosData;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTaskDetail;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTrack;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TravelDetialActivity extends BaseButterKnifeActivity {

	@BindView(R.id.title_left_img)
	ImageView titleLeftImg;
	@BindView(R.id.title_text)
	TextView titleText;

	@BindView(R.id.tv_travel_detail_start_time)
	TextView mTvStartTime;

	@BindView(R.id.tv_travel_detail_dest_time)
	TextView mTvDestTime;

	@BindView(R.id.tv_travel_detail_licence)
	TextView mTvLicence; // 车牌号

	@BindView(R.id.tv_travel_detail_check_waybill)
	TextView mTvCheckWaybill; // 查看运单详情

	@BindView(R.id.tv_travel_detail_waybill)
	TextView mTvWaybill; // 运单号

	@BindView(R.id.tv_travel_detail_address)
	TextView mTvAddress; // 地址

	@BindView(R.id.btn_travel_detail_drive_evaluation)
	Button mBtnDriveEvaluation; // 驾驶测评

	@BindView(R.id.tv_travel_detail_hundred_fuel)
	TextView mTvHundredFuel; // 百公里油耗

	@BindView(R.id.tv_travel_detail_total_fuel)
	TextView mTvToatlFuel; // 总油耗

	@BindView(R.id.tv_travel_detail_idle_speed_fuel)
	TextView mTvSpeedFuel; // 怠速油耗

	@BindView(R.id.tv_travel_detail_idle_speed_time)
	TextView mTvSpeedTime; // 怠速时长

	@BindView(R.id.tv_travel_detail_total_time)
	TextView mTvTotalTime; // 总时长

	@BindView(R.id.tv_travel_detail_total_mileage)
	TextView mTvTotalMileage; // 总里程

	@BindView(R.id.tv_travel_detail_max_speed)
	TextView mTvMaxSpeed; // 最高车速

	@BindView(R.id.tv_travel_detail_average_speed)
	TextView mTvAverageSpeed; // 平均车速

	@BindView(R.id.fl_travel_detail_mapview)
	FrameLayout mFrameLayout;

	@BindView(R.id.rl_travel_detail_address)
	RelativeLayout mAddressLayout;

	@BindView(R.id.rl_travel_detail_waybill)
	RelativeLayout mWaybillLayout;

	@BindView(R.id.ll_travel_detail_car_info)
	LinearLayout mLLCarInfo;

	@BindView(R.id.travel_detail_listview)
	ListView mListView;

	@BindView(R.id.travel_detail_order_detail)
	RelativeLayout mOrderDeatil;

	@BindView(R.id.travel_detail_order_detail_bg)
	ImageView imgBg;

	@BindView(R.id.travel_detail_order_detail_cancel)
	ImageView btCancel;

	@BindView(R.id.travel_detail_order_detail_title)
	TextView tvTitle;

	public String mtaskid, mcorpid;

	private MapView mMapView;

	private Navi mNaviRoute;

	/** 轨迹点的坐标 */
	private ArrayList<LatLng> mTrackPointList;
	private ArrayList<ArrayList<LatLng>> mArrayTrackList;
	private MapPolyLine mTrackline;// 线条View

	private String mJonStr;

	private OrderDetailAdapter detailAdapter;

	@Override
	public int getLayoutId() {

		return R.layout.activity_travel_detail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initData();
		initView();
	}

	private void initData() {
		titleLeftImg.setVisibility(View.VISIBLE);
		titleText.setText("行程详情");

		mMapView = MapViewAPI.getInstance().createMapView(this);
		mMapView.getMap().setTrafficEnabled(false);
		mMapView.getMap().setLocationIconEnabled(false);
		mFrameLayout.addView(mMapView);

		mJonStr = getIntent().getStringExtra("route");

		if (TextUtils.isEmpty(mJonStr)) {
			finish();
		}

		mTrackPointList = new ArrayList<>();
		mArrayTrackList = new ArrayList<>();
		mNaviRoute = GsonTool.getInstance().fromJson(mJonStr, Navi.class);

		if(mNaviRoute == null)
			return;
		

		if (mNaviRoute.orders != null & mNaviRoute.orders.size() > 0){
			mtaskid = mNaviRoute.orders.get(0).taskid;
			mcorpid = mNaviRoute.orders.get(0).corpid;
		}
	}

	private void initView() {
		mTvCheckWaybill.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		mTvLicence.setText(mNaviRoute.carlicense);

		mTvStartTime.setText(TimeUtils.stampToFormat(mNaviRoute.starttime, "yyyy/MM/dd HH:mm"));
		mTvDestTime.setText(TimeUtils.stampToFormat(mNaviRoute.endtime, "yyyy/MM/dd HH:mm"));

		List<MtqOrder> orderList = mNaviRoute.orders;

		detailAdapter = new OrderDetailAdapter(this, orderList);
		mListView.setAdapter(detailAdapter);

		mOrderDeatil.setVisibility(View.INVISIBLE);
		imgBg.getBackground().setAlpha(200);
		imgBg.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return true;
			}
		});

		mWaybillLayout.setVisibility(View.VISIBLE);
		mAddressLayout.setVisibility(View.VISIBLE);

		 if (orderList != null && !orderList.isEmpty()) {
			 mWaybillLayout.setVisibility(View.VISIBLE);
			 mAddressLayout.setVisibility(View.VISIBLE);
			 /**
			 * 多个运单号，用逗号隔开
			 */
			 String waybill = "";
			 int len = orderList.size();
			 for (int i = 0; i < len; i++) {
				 waybill += orderList.get(i).cut_orderid;
				 if (i != len - 1) {
					 waybill += ", ";
				 }
			 }
			 
			 mTvWaybill.setText(waybill);
			 mTvAddress.setText(TextStringUtil.ReplaceHtmlTag(orderList.get(0).receive_addr).replaceAll("\\s*", ""));
			 tvTitle.setText("运单详情(" + orderList.size() + ")");
		 } else {
			 mWaybillLayout.setVisibility(View.GONE);
			 mAddressLayout.setVisibility(View.GONE);
		 }

		getLatestNaviDetail(mNaviRoute);

		MapViewAPI.getInstance().showZoomControls(mMapView, false);

	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	protected void onPause() {

		super.onPause();
		if (mMapView != null) {
			mMapView.onPause();
		}
	}

	@Override
	protected void onResume() {

		super.onResume();
		if (mMapView != null) {
			mMapView.onResume();// 当地图控件存在时，调用相应的恢复方法
			mMapView.update();// 同时更新地图控件状态
			if (mTrackPointList.size() > 0) {
//				showTrack(mTrackPointList);
				showTrack(mTrackPointList,mArrayTrackList);
			}
		}
	}

	@OnClick({ R.id.title_left_img, R.id.btn_travel_detail_drive_evaluation, R.id.tv_travel_detail_check_waybill,
			R.id.travel_detail_order_detail_cancel })
	void onClick(View view) {

		if (CommonTool.isFastDoubleClick()) {

			return;
		}


		switch (view.getId()) {
		case R.id.tv_travel_detail_check_waybill:
			if (mOrderDeatil.getVisibility() == View.INVISIBLE) {
				mOrderDeatil.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.travel_detail_order_detail_cancel:
			if (mOrderDeatil.getVisibility() == View.VISIBLE) {
				mOrderDeatil.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.title_left_img:
			finish();
			break;

		case R.id.btn_travel_detail_drive_evaluation:
			Intent intent = new Intent(this, DriveEvaluationActivity.class);
			intent.putExtra("route", mJonStr);
			startActivity(intent);
			break;
		}
	}

	public void getLatestNaviDetail(final Navi navi) {
		final String carlicense = navi.carlicense;
		final String serialid = navi.serialid;
		WaitingProgressTool.showProgress(this);
		CarManager.getInstance().getLatestRouteDetail(navi, new IGetTaskDetailListener() {

			@Override
			public void onGetResult(int errCode, MtqTaskDetail result) {
				WaitingProgressTool.closeshowProgress();
				if (errCode == 0 && result != null) {
					TravelDetail detail = CarUtils.formatTaskDetail(result, carlicense, serialid);
					dealDetailSuccess(detail);
				} else {
					if (ErrCodeUtil.isNetErrCode(errCode)){
						Toast.makeText(TravelDetialActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
					}
					dealDetailFailed();
				}

//				if (!TextUtils.isEmpty(mtaskid) && !TextUtils.isEmpty(mcorpid)) {
//					
//					GetTaskDetailData();
//
//				}
			}
		});
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
		
		
		View view = LayoutInflater.from(this).inflate(R.layout.layout_freightpoint_poi, null);
		TextView txtNum = (TextView) view.findViewById(R.id.freight_point_poi_number);
		ImageView img = (ImageView) view.findViewById(R.id.freight_point_poi_img);
		txtNum.setText("起");
		img.setImageResource(R.drawable.ic_water_start);
		
		mMapView.getMap().addOverlay(new MarkerOptions().
				position(mTrackPointList.get(0)).layout(view));			
		
		view = LayoutInflater.from(this).inflate(R.layout.layout_freightpoint_poi, null);
		txtNum = (TextView) view.findViewById(R.id.freight_point_poi_number);
		img = (ImageView) view.findViewById(R.id.freight_point_poi_img);
		txtNum.setText("终");
		img.setImageResource(R.drawable.ic_water_select);
		mMapView.getMap().addOverlay(new MarkerOptions().
				position(mTrackPointList.get(mTrackPointList.size() - 1)).layout(view));	
	}
	
	private void showTrack(ArrayList<LatLng> trackPointList){
		mMapView.getMap().removeAllOverlay();
		//可能不止这些覆盖物，所以要清除所有的。
//		if (mTrackline != null) {// 存在覆盖物的时候,先清除
//			mMapView.getMap().removeOverlay(mTrackline);
//		}
		// 添加覆盖物
		mTrackline = (MapPolyLine) mMapView.getMap()
				.addOverlay(new PolyLineOptions().color(Color.RED)// 设置线的颜色
						.points(mTrackPointList)// 设置经过的点
						.width(10));// 设置线的宽度
//						.dottedLine(false));

		mMapView.getMap().zoomToSpan(mTrackPointList);// 缩放到合适比例，将传入的点坐标都显示出来
		
		View view = LayoutInflater.from(this).inflate(R.layout.layout_freightpoint_poi, null);
		TextView txtNum = (TextView) view.findViewById(R.id.freight_point_poi_number);
		ImageView img = (ImageView) view.findViewById(R.id.freight_point_poi_img);
		txtNum.setText("起");
		img.setImageResource(R.drawable.ic_water_start);
		
		mMapView.getMap().addOverlay(new MarkerOptions().
				position(mTrackPointList.get(0)).layout(view));			
		
		view = LayoutInflater.from(this).inflate(R.layout.layout_freightpoint_poi, null);
		txtNum = (TextView) view.findViewById(R.id.freight_point_poi_number);
		img = (ImageView) view.findViewById(R.id.freight_point_poi_img);
		txtNum.setText("终");
		img.setImageResource(R.drawable.ic_water_select);
		mMapView.getMap().addOverlay(new MarkerOptions().
				position(mTrackPointList.get(mTrackPointList.size() - 1)).layout(view));	
	}

	private void dealDetailSuccess(final TravelDetail traveldetail) {
		Runnable r = new Runnable() {

			@Override
			public void run() {

				TravelDetail detail = traveldetail;
				showTrack(detail);
				mLLCarInfo.setVisibility(View.VISIBLE);

				if (detail.navi == null) {
					mLLCarInfo.setVisibility(View.INVISIBLE);
				} else {
					// 总油耗
					mTvToatlFuel.setText(detail.navi.fuelcon);

					// 百公里油耗
					mTvHundredFuel.setText(detail.hundred_fuel);

					// 怠速油耗
					mTvSpeedFuel.setText(detail.navi.idlefuelcon);

					// 怠速时长
					mTvSpeedTime.setText(detail.navi.idletime);

					// 总时长
					mTvTotalTime.setText(detail.navi.traveltime);

					// 总里程
					mTvTotalMileage.setText(detail.navi.mileage);

					// 最高车速
					mTvMaxSpeed.setText(detail.navi.topspeed);

					// 平均车速
					mTvAverageSpeed.setText(detail.average_speed);
				}
			}
		};

		if (Looper.getMainLooper() != Looper.myLooper()) {
			mLLCarInfo.post(r);
		} else {
			r.run();
		}
	}

	private void dealDetailFailed() {
		Runnable r = new Runnable() {

			@Override
			public void run() {

				mLLCarInfo.setVisibility(View.INVISIBLE);
			}
		};

		if (Looper.getMainLooper() != Looper.myLooper()) {
			mLLCarInfo.post(r);
		} else {
			r.run();
		}
	}

	private class OrderDetailAdapter extends BaseAdapter {

		private Context context;
		private List<MtqOrder> contentArray;
		MtqOrder tmpData;

		public OrderDetailAdapter(Context c, List<MtqOrder> content) {
			this.context = c;
			this.contentArray = content;
		}

		public void setData(List<MtqOrder> content) {

			contentArray = content;
			notifyDataSetChanged();

		}

		@Override
		public int getCount() {

			return contentArray.size();
		}

		@Override
		public MtqOrder getItem(int position) {

			if (position < contentArray.size()) {

				return contentArray.get(position);
			} else
				return null;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			ViewHolder viewHolder = null;
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(R.layout.item_travel_detail_order_detail, null);
				viewHolder.waybill = (TextView) view.findViewById(R.id.order_detail_item_waybill);
				viewHolder.sendPlace = (TextView) view.findViewById(R.id.order_detail_item_send_place);
				viewHolder.sendMan = (TextView) view.findViewById(R.id.order_detail_item_send_man);
				viewHolder.sendPhone = (TextView) view.findViewById(R.id.order_detail_item_send_man_phone);
				viewHolder.recPlace = (TextView) view.findViewById(R.id.order_detail_item_rec_place);
				viewHolder.recMan = (TextView) view.findViewById(R.id.order_detail_item_rec_man);
				viewHolder.recPhone = (TextView) view.findViewById(R.id.order_detail_item_rec_phone);
				viewHolder.sendPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
				viewHolder.recPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			tmpData = getItem(position);

			if (tmpData != null) {

				viewHolder.waybill.setText(tmpData.cut_orderid);
				viewHolder.sendPlace.setText(TextStringUtil.ReplaceHtmlTag(tmpData.send_addr).replaceAll("\\s*", ""));
				viewHolder.sendMan.setText(tmpData.send_name);
				viewHolder.sendPhone.setText(tmpData.send_phone);
				viewHolder.recPlace.setText(TextStringUtil.ReplaceHtmlTag(tmpData.receive_addr).replaceAll("\\s*", ""));
				viewHolder.recMan.setText(tmpData.receive_name);
				viewHolder.recPhone.setText(tmpData.receive_phone);

				final String revcallPhone = tmpData.receive_phone;

				viewHolder.recPhone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						callPhone = revcallPhone;
						CallUtil.call(TravelDetialActivity.this, revcallPhone);
					}
				});

				final String sendcallPhone = tmpData.send_phone;

				viewHolder.sendPhone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						callPhone = sendcallPhone;
						CallUtil.call(TravelDetialActivity.this, sendcallPhone);
					}
				});

			}

			return view;
		}

		private class ViewHolder {
			public TextView waybill;
			public TextView sendPlace;
			public TextView sendMan;
			public TextView sendPhone;
			public TextView recPlace;
			public TextView recMan;
			public TextView recPhone;
		}

	}

	public void GetTaskDetailData() {

		WaitingProgressTool.showProgress(this);

		DeliveryApi.getInstance().getTaskDetailInServer(mtaskid, mcorpid, new OnResponseResult<MtqDeliTaskDetail>() {

			@Override
			public void OnResult(MtqDeliTaskDetail result) {

				if (isFinishing())
					return;

				WaitingProgressTool.closeshowProgress();

				if (result == null || result.getStore() == null) {
					Toast.makeText(TravelDetialActivity.this, "获取任务信息失败", Toast.LENGTH_SHORT).show();

					return;

				}

				TaskOperator.getInstance().saveTaskDetailDataToBD(result);

				if (detailAdapter != null) {

					List<MtqOrder> orderList = new ArrayList<>();
					MtqOrder rolltmp;

					for (MtqDeliOrderDetail tmp : result.getOrders()) {

						rolltmp = new MtqOrder();
						rolltmp.cut_orderid = tmp.orderno;
						
						//String receAddr= Html.fromHtml(tmp.receive_addr).toString();
						tmp.receive_addr = TextStringUtil.ReplaceHtmlTag(tmp.receive_addr);
						
						//String sendAddr= Html.fromHtml(tmp.send_addr).toString();
						tmp.send_addr =  TextStringUtil.ReplaceHtmlTag(tmp.send_addr);
						
						rolltmp.receive_addr = (tmp.receive_regionname + tmp.receive_addr).replaceAll("\\s*", "");
						rolltmp.receive_name = tmp.receive_name;
						rolltmp.receive_phone = tmp.receive_phone;
						
						
						
						
						rolltmp.send_addr =  (tmp.send_regionname + tmp.send_addr).replaceAll("\\s*", "");
						rolltmp.send_name = tmp.send_name;
						rolltmp.send_phone = tmp.send_phone;

						orderList.add(rolltmp);
						// orderList.add(rolltmp);
						// orderList.add(rolltmp);

					}

					detailAdapter.setData(orderList);

					if (orderList != null && !orderList.isEmpty()) {
						mWaybillLayout.setVisibility(View.VISIBLE);
						mAddressLayout.setVisibility(View.VISIBLE);
						/**
						 * 多个运单号，用逗号隔开
						 */
						String waybill = "";
						int len = orderList.size();

						// if(len == 1 ){
						// waybill += orderList.get(i).cut_orderid;
						// }else if(len == 0){
						// waybill = "";
						// }else{
						//
						// }

						over: for (int i = 0; i < len; i++) {

							if (i == 0) {
								waybill += orderList.get(i).cut_orderid;
							} else {
								waybill += "...";
								break over;
							}
							// if (i != len - 1) {
							// waybill += ", ";
							// }
						}
						mTvWaybill.setText(waybill);
						mTvAddress.setText(TextStringUtil.ReplaceHtmlTag(orderList.get(0).receive_addr).replaceAll("\\s*", ""));
						tvTitle.setText("运单详情(" + orderList.size() + ")");
					} else {
						mWaybillLayout.setVisibility(View.GONE);
						mAddressLayout.setVisibility(View.GONE);
					}

				}

			}

			@Override
			public void OnError(int ErrCode) {

				if (isFinishing())
					return;

				Toast.makeText(TravelDetialActivity.this, "获取任务信息失败", Toast.LENGTH_SHORT).show();
				WaitingProgressTool.closeshowProgress();

			}

			@Override
			public void OnGetTag(String Reqtag) {


			}
		});

	}
	
	public String callPhone = "";

	/**
	 * * 动态请求拨打电话权限后，监听用户的点击事件
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
		case 0x11:
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission Granted
				// String phone = mStore.linkPhone;
				if (!TextUtils.isEmpty(callPhone))
					CallUtil.intentToCall(TravelDetialActivity.this, callPhone);
			} else {
				// Permission Denied
			}
			break;
		default:
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

}
