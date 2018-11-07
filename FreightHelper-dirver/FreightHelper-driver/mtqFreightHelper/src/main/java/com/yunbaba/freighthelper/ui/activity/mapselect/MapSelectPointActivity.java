package com.yunbaba.freighthelper.ui.activity.mapselect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.location.CldLocation;
import com.cld.location.CldLocationClient;
import com.cld.location.CldLocationOption;
import com.cld.location.ICldLocationListener;
import com.cld.mapapi.map.CldMap.OnMapMovingListener;
import com.cld.mapapi.map.MapView;
import com.cld.mapapi.model.LatLng;
import com.cld.mapapi.model.PoiInfo;
import com.cld.mapapi.search.exception.IllegalSearchArgumentException;
import com.cld.mapapi.search.geocode.GeoCodeResult;
import com.cld.mapapi.search.geocode.GeoCoder;
import com.cld.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.cld.mapapi.search.geocode.ReverseGeoCodeOption;
import com.cld.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cld.mapapi.search.poi.OnPoiSearchResultListener;
import com.cld.mapapi.search.poi.PoiNearSearch;
import com.cld.mapapi.search.poi.PoiNearSearchOption;
import com.cld.mapapi.search.poi.PoiResult;
import com.cld.navisdk.routeplan.CldRoutePlaner;
import com.cld.nv.location.CldCoordUtil;
import com.cld.nv.map.CldMapApi;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.AddressBean;
import com.yunbaba.freighthelper.bean.eventbus.MapSelectPointEvent;
import com.yunbaba.freighthelper.bean.eventbus.StoreMarkSuccessEvent;
import com.yunbaba.freighthelper.utils.DebounceTool;
import com.yunbaba.freighthelper.utils.DebounceTool.OnDebounceListener;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam;
import com.yunbaba.ols.module.delivery.bean.MtqStore;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import hmi.packages.HPDefine.HPWPoint;

public class MapSelectPointActivity extends BaseButterKnifeActivity
		implements OnGetGeoCoderResultListener, OnPoiSearchResultListener {

	@BindView(R.id.tv_title)
	TextView tv_title;

	@BindView(R.id.tv_position)
	TextView tv_position;

	@BindView(R.id.tv_position_detail)
	TextView tv_position_detail;

	@BindView(R.id.tv_confirm)
	TextView tv_confirm;

	@BindView(R.id.prl_loading)
	PercentRelativeLayout prl_loading;
	
	
	@BindView(R.id.ll_jumptosearch)
	PercentRelativeLayout ll_jumptosearch;

	@BindView(R.id.iv_titleleft)
	ImageView iv_titleleft;

	@BindView(R.id.iv_titleright)
	ImageView iv_titleright;

	@BindView(R.id.fl_mapview)
	MapView mMapView;
	// FrameLayout fl_mapview;

	@BindView(R.id.ll_addressdetail)
	PercentLinearLayout ll_addressdetail;

	GeoCoder mGeoCoder;
	PoiNearSearch mPoiSearch;

	LatLng currentlatLng;

	DebounceTool tool;

	// PoiResult poiResult;

	ReverseGeoCodeResult mCurGeoResult;

	PoiInfo mCurPoi;
	MtqStore mStoreDetail;

	@Override
	public int getLayoutId() {

		return R.layout.activity_mapselectpoint;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		EventBus.getDefault().register(this);

		if (getIntent() != null && getIntent().getStringExtra("storedetail") != null) {

			String jsonstr = getIntent().getStringExtra("storedetail");

			mStoreDetail = GsonTool.getInstance().fromJson(jsonstr, MtqStore.class);

		}

		tv_title.setText("地图选点");

		// mMapView = MapViewAPI.getInstance().createMapView(this);

		mMapView.getMap().setTrafficEnabled(false);

		// CldNavigator.getInstance().reset();
		CldRoutePlaner.getInstance().clearRoute();
		// fl_mapview.addView(mMapView);

		HPWPoint start = CldMapApi.getNMapCenter();

		mMapView.getMap().setMapCenter(new LatLng(start.y, start.x));

		// location(LocationMode.MIXED, 0);

		getTool().NewInput("getcenter");

		mMapView.getMap().setOnMapMovingListener(new OnMapMovingListener() {

			@Override
			public void onMapMoving() {


			}

			@Override
			public void onMapMoveStoped() {

				MLog.e("滑动停止", "获取当前位置");
				LoadResult();
				getTool().NewInput("getcenter");

			}
		});

		getTool().setListener(new OnDebounceListener() {

			@Override
			public void AfterDebounce(Object obj) {

				getMapCenter();
			}
		});

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

	public void getMapCenter() {

		if (mMapView != null) {
			// 获取地图中心点坐标

			mCurGeoResult = null;

			currentlatLng = mMapView.getMap().getCenterPosition();
			GeoCode(currentlatLng);

			// 设置结果显示
			// mTv_Result.setText("经度:" + latLng.longitude + " ,纬度:" +
			// latLng.latitude);

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		if (mMapView != null)
			mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		getGeoCoder().setOnGetGeoCodeResultListener(null);
		if (mMapView != null)
			mMapView.destroy();

		EventBus.getDefault().unregister(this);
	}

	public DebounceTool getTool() {

		if (tool == null)
			tool = new DebounceTool();

		return tool;

	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {


	}

	public static final int resultCode = 0;

	@OnClick({ R.id.iv_titleleft, R.id.tv_confirm ,R.id.ll_jumptosearch})
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_titleleft:
			this.finish();

			break;
		case R.id.ll_jumptosearch:
			
			Intent it2 = new Intent(MapSelectPointActivity.this, MapSearchPoiActivity.class);

			if (mStoreDetail != null)
				it2.putExtra("storedetail", GsonTool.getInstance().toJson(mStoreDetail));

			

			startActivity(it2);

			break;
		case R.id.tv_confirm:
			// this.finish();

			if (mCurGeoResult != null) {

				Intent it = new Intent();

				AddressBean bean = new AddressBean();

				bean.address = mCurGeoResult.address;
				MLog.e("selectpoint", mCurGeoResult.addressDetail.province + " " + mCurGeoResult.addressDetail.district
						+ "  " + mCurGeoResult.addressDetail.city + " " + mCurGeoResult.businessCircle);

				bean.uploadAddress = mCurGeoResult.address.replaceFirst(mCurGeoResult.addressDetail.province, "")
						.replaceFirst(mCurGeoResult.addressDetail.city, "")
						.replaceFirst(mCurGeoResult.addressDetail.district, "");

				// bean.address = mCurGeoResult.addressDetail.

				// currentlatLng = mMapView.getMap().getCenterPosition();
				// bean.kcode = MapViewAPI.getInstance().
				bean.kcode = CldCoordUtil.cldToKCode(mCurGeoResult.location);
				
				bean.latitude = mCurGeoResult.location.latitude;
				bean.longitude = mCurGeoResult.location.longitude;
				bean.pcd = mCurGeoResult.addressDetail.province + mCurGeoResult.addressDetail.city
						+ mCurGeoResult.addressDetail.district;
				MLog.e("selectpoint", bean.address + " " + bean.kcode + " " + bean.uploadAddress);

				it.putExtra("addressinfo", bean);
				// it.putExtra("address", mCurGeoResult);

				if (TextUtils.isEmpty(bean.uploadAddress)) {

					Toast.makeText(this, "请选择有详细地址的点", Toast.LENGTH_SHORT).show();

				} else {
					setResult(resultCode, it);

					if(mStoreDetail!=null){


                         UploadStoreAddr(bean);

					}else {
						finish();
					}
				}

			} else {

				if (prl_loading != null && prl_loading.getVisibility() == View.VISIBLE) {

					Toast.makeText(this, "正在获取地址中，请等待", Toast.LENGTH_SHORT).show();

				} else

					Toast.makeText(this, "获取地址失败，请重新获取", Toast.LENGTH_SHORT).show();

			}

			break;
		}
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

		if (isFinishing())
			return;

		ShowResult();

		if (result != null) {
			if (result.errorCode != 0) {
				// Toast.makeText(this,
				// result != null && !TextUtils.isEmpty(result.errorMsg) ?
				// result.errorMsg : "逆地理编码失败",
				// Toast.LENGTH_LONG).show();
				MLog.v("SearchSDK", result.address + "\n" + result.businessCircle);
				SetResult(null);
				return;

			}
			if (TextUtils.isEmpty(result.address)) {// 返回地址是否为空判断
				// mTv_Name.setText( result != null
				// && !TextUtils
				// .isEmpty(result.errorMsg) ? result.errorMsg
				// : "逆地理编码失败");

				SetResult(null);
			} else {

				if (currentlatLng == null || (result.location.latitude == currentlatLng.latitude
						&& result.location.longitude == currentlatLng.longitude)) {

					SetResult(result);

				}

			}
			// mTv_Name.setText(result.address+"\n"+result.businessCircle+"\n"
			// +result.addressDetail.province + "\n"
			// +result.addressDetail.city + "\n"
			// +result.addressDetail.district + "\n"
			// +result.addressDetail.street + "\n"
			// +result.addressDetail.streetNumber + "\n");
		}
	}

	public void SetResult(final ReverseGeoCodeResult result) {

		mCurGeoResult = result;

		MapSelectPointActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				if (result == null) {

					tv_position.setText("获取位置失败");
					tv_position_detail.setText("");

					ll_addressdetail.setVisibility(View.GONE);
					Toast.makeText(MapSelectPointActivity.this, "无法获取位置，请检查网络", Toast.LENGTH_SHORT).show();

				} else {
					LoadResult();
					// result.businessCircle

					// mCurResult.addressDetail.street +
					// mCurResult.addressDetail.streetNumber
					tv_position.setText("");
					tv_position_detail.setText(result.address);
					ll_addressdetail.setVisibility(View.VISIBLE);
					searchNearPoi();
					// tv_position_detail.setText(mCurResult.address);
					// tv_position_detail.setText(
					// result.addressDetail.province + result.addressDetail.city
					// +
					// result.addressDetail.district
					// + result.addressDetail.street +
					// result.addressDetail.streetNumber);
				}

			}
		});

	}

	public void ShowResult() {

		if (prl_loading != null) {

			MapSelectPointActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					prl_loading.setVisibility(View.GONE);
					tv_confirm.setClickable(true);
				}
			});

		}
	}

	public void LoadResult() {
		if (prl_loading != null) {
			MapSelectPointActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					prl_loading.setVisibility(View.VISIBLE);
					tv_confirm.setClickable(false);
				}
			});

		}
	}

	protected void searchNearPoi() {


		PoiNearSearchOption option = new PoiNearSearchOption();
		option.pageNum = 1;// 得到页总数
		option.pageCapacity = 5;// 单页显示数
		// option.keyword = mEt_Keyword.getText().toString();// 搜索关键字

		option.radius = 500;// 默认为100

		option.location.longitude = mCurGeoResult.location.longitude;

		option.location.latitude = mCurGeoResult.location.latitude;

		getPoiSearch().setOnPoiSearchListner(this);
		getPoiSearch().searchNearby(option);// 传入查找参数
	}

	/**
	 * Poi搜索结果
	 */
	@Override
	public void onGetPoiSearchResult(int errorCode, PoiResult result) {

		if (isFinishing())
			return;

		ShowResult();

		// poiResult = result;
		if (errorCode == 0 && result != null && result.totalCount != 0 && result.getPoiInfos() != null
				&& result.getPoiInfos().size() > 0) {// 判断是否有数据

			mCurPoi = result.getPoiInfos().get(0);

			tv_position.setText("在" + mCurPoi.name + "附近");
		} else {

			mCurPoi = null;

			tv_position.setText("附近未找到相关的点");
		}

	}

	public GeoCoder getGeoCoder() {

		if (mGeoCoder == null)
			mGeoCoder = GeoCoder.newInstance();

		return mGeoCoder;

	}

	public PoiNearSearch getPoiSearch() {

		if (mPoiSearch == null)
			mPoiSearch = PoiNearSearch.newInstance();

		return mPoiSearch;

	}

	// 定位终端
	private CldLocationClient locationManager;

	/**
	 * 定位
	 * 
	 * @param locationMode
	 *            定位类型 参考类LocationMode
	 * @param spanMs
	 *            定位频率 单位毫秒
	 * @return void
	 * @author Huagx
	 * @date 2016-1-28 上午9:11:40
	 */
	private void location(int locationMode, int spanMs) {
		if (null == locationManager) {
			locationManager = new CldLocationClient(this);
		}
		// 如果已开启定位，先停掉
		if (locationManager.isStarted()) {
			locationManager.stop();
		}
		// 设置定位选项
		CldLocationOption option = new CldLocationOption();
		option.setLocationMode(locationMode);// 设置定位模式
		option.setNetworkScanSpan(spanMs);// 定位扫描时间
		locationManager.setLocOption(option);
		locationManager.registerLocationListener(new ICldLocationListener() {

			@Override
			public void onReceiveLocation(CldLocation location) {
				if (null != location) {
					double altitude = location.getAltitude();
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					float accuracy = location.getAccuracy();
					float bearing = location.getBearing();
					float speed = location.getSpeed();
					long time = location.getTime();
					String addr = location.getAddress();
					String adCode = location.getAdCode();
					String dist = location.getDistrict();
					String city = location.getCity();
					String cityCode = location.getCityCode();
					String province = location.getProvince();
					String locInfo = "lat:" + latitude + ",lon:" + longitude + "alt:" + altitude + ",acc:" + accuracy
							+ ",bear:" + bearing + ",spd:" + speed + ",time:" + time + ",provice:" + province + "city:"
							+ city + ",code:" + cityCode + ",dist:" + dist + ",addr:" + addr + ",adcode:" + adCode;
					MLog.i("location", locInfo);
					mMapView.getMap().setNMapCenter(new LatLng(location.getLatitude(), location.getLongitude()));

				}
			}
		});
		locationManager.start();
	}

	/**
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		// 关闭定位
		if (locationManager != null) {
			locationManager.stop();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
	public synchronized void onSelectMapPointEvent(MapSelectPointEvent event) {

		Intent it = new Intent();

		it.putExtra("addressinfo", event.getAddresspoint());

		setResult(resultCode, it);
		finish();

	}

	public void UploadStoreAddr(AddressBean bean) {







        if (bean != null && mStoreDetail!=null) {

            WaitingProgressTool.showProgress(this);

            CldSapKDeliveryParam.CldDeliUploadStoreParm parm = new  CldSapKDeliveryParam.CldDeliUploadStoreParm();

            // parm.address = tvPosition.getText().toString();
            parm.corpid = mStoreDetail.corpId;

            if (!TextUtils.isEmpty(mStoreDetail.linkMan))
                parm.linkman = mStoreDetail.linkMan;

            if (!TextUtils.isEmpty(mStoreDetail.linkPhone))
                parm.phone = mStoreDetail.linkPhone;

            if (!TextUtils.isEmpty(mStoreDetail.storeCode))
                parm.storecode = mStoreDetail.storeCode;

            if (TextUtils.isEmpty(mStoreDetail.storeCode))
                parm.settype = 1;
            else
                parm.settype = 3;
            parm.storeid = mStoreDetail.storeId;

            parm.storename = mStoreDetail.storeName;
            parm.iscenter = 0;
            parm.storekcode = bean.kcode; // "";//"";//
            parm.address = bean.uploadAddress;

            parm.extpic = "";

            CldKDeliveryAPI.getInstance().uploadStore(parm, new CldKDeliveryAPI.ICldUploadStoreListListener() {

                @Override
                public void onGetResult(int errCode,String errMsg) {

                    WaitingProgressTool.closeshowProgress();



                    if (errCode == 0) {



						if (!isFinishing())
							Toast.makeText(MapSelectPointActivity.this, "标记成功", Toast.LENGTH_SHORT).show();


						EventBus.getDefault().post(new StoreMarkSuccessEvent());

                    }
					if (isFinishing())
						return;
					finish();





                }

                @Override
                public void onGetReqKey(String tag) {


                }
            });

        } else {


			finish();

		}

	}

}
