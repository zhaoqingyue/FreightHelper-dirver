package com.yunbaba.freighthelper.ui.activity.mapselect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.location.CldLocation;
import com.cld.location.CldLocationClient;
import com.cld.location.CldLocationOption;
import com.cld.location.ICldLocationListener;
import com.cld.mapapi.map.CldMap.OnMapLongClickListener;
import com.cld.mapapi.map.Marker;
import com.cld.mapapi.map.MarkerOptions;
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
import com.cld.nv.map.overlay.Overlay;
import com.cld.nv.map.overlay.listener.IOverlayOnClickListener;
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

import butterknife.BindView;
import butterknife.OnClick;
import hmi.packages.HPDefine.HPWPoint;

public class MapSelectAndSearchActivity extends BaseButterKnifeActivity
		implements OnGetGeoCoderResultListener, OnPoiSearchResultListener {

	@BindView(R.id.iv_titleleft)
	ImageView ivTitleleft;
	// @BindView(R.id.tv_title)
	// TextView tvTitle;
	// @BindView(R.id.iv_titleright)
	// ImageView ivTitleright;
	@BindView(R.id.fl_mapview)
	com.cld.mapapi.map.MapView mMapView;
	@BindView(R.id.v1)
	View v1;
	@BindView(R.id.tv_position)
	TextView tv_position;
	@BindView(R.id.tv_position_detail)
	TextView tv_position_detail;
	@BindView(R.id.ll_addressdetail)
	PercentLinearLayout ll_addressdetail;
	@BindView(R.id.prl_loading)
	PercentRelativeLayout prl_loading;
	@BindView(R.id.prl_bottom)
	PercentRelativeLayout prlBottom;
	@BindView(R.id.tv_confirm)
	TextView tv_confirm;
	@BindView(R.id.ll_root)
	PercentLinearLayout llRoot;
	@BindView(R.id.et_search)
	TextView etSearch;
	GeoCoder mGeoCoder;
	PoiNearSearch mPoiSearch;

	LatLng currentlatLng;

	DebounceTool tool;

	// PoiResult poiResult;

	ReverseGeoCodeResult mCurGeoResult;

	PoiInfo mCurPoi;
	Marker selectMarker;
	Marker touchMarker;

	MtqStore mStoreDetail;
	PoiInfo mChoosePoi;
	private boolean isSelect = true;

	@Override
	public int getLayoutId() {

		return R.layout.activity_mapselectandsearch;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (getIntent() != null && getIntent().getStringExtra("storedetail") != null) {

			String jsonstr = getIntent().getStringExtra("storedetail");

			mStoreDetail = GsonTool.getInstance().fromJson(jsonstr, MtqStore.class);

		}

		if (getIntent() != null && getIntent().getParcelableExtra("poiinfo") != null) {

			// String jsonstr = getIntent().getStringExtra("poiinfo");

			MLog.e("getpoiinfo", "true");

			mChoosePoi = getIntent().getParcelableExtra("poiinfo");

			// GsonTool.getInstance().fromJson(jsonstr, PoiInfo.class);
			MLog.e("getpoiinfo", mChoosePoi.address);
		}

		mMapView.getMap().setTrafficEnabled(false);

		// CldNavigator.getInstance().reset();
		CldRoutePlaner.getInstance().clearRoute();
		// fl_mapview.addView(mMapView);

		// location(LocationMode.MIXED, 0);

		// getTool().NewInput("getcenter");

		// mMapView.getMap().setOnMapMovingListener(new OnMapMovingListener() {
		//
		// @Override
		// public void onMapMoving() {
		//
		//
		// }
		//
		// @Override
		// public void onMapMoveStoped() {
		//
		// MLog.e("滑动停止", "获取当前位置");
		// LoadResult();
		// getTool().NewInput("getcenter");
		//
		// }
		// });

		mMapView.getMap().setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng arg0) {

				LoadResult();

				if (touchMarker != null)
					mMapView.getMap().removeOverlay(touchMarker);

				touchMarker = (Marker) mMapView.getMap()
						.addOverlay(new MarkerOptions().position(arg0).icon(getMapIcon(1)));

				getTool().NewInput(arg0);

				isSelect = false;
			}
		});

		getTool().setListener(new OnDebounceListener() {

			@Override
			public void AfterDebounce(Object obj) {

				// getMapCenter();

				LatLng arg0 = (LatLng) obj;

				GeoCode(arg0);

			}
		});

		// 设置被选的poi点信息
		if (mChoosePoi != null) {

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					setSelectPoiResult(mChoosePoi);
					mMapView.getMap().setMapCenter(mChoosePoi.location);
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							GeoCode(mChoosePoi.location);
						}
						
					}, 100);
					
				}
			}, 500);

			setSelectMarker();

			etSearch.setText(mChoosePoi.name);

			

			// etSearch.setEnabled(false);
			// etSearch.setFocusable(false);

		} else {

			HPWPoint start = CldMapApi.getNMapCenter();

			mMapView.getMap().setMapCenter(new LatLng(start.y, start.x));

		}

	}

	public void setSelectMarker() {
		selectMarker = (Marker) mMapView.getMap()
				.addOverlay(new MarkerOptions().position(mChoosePoi.location).icon(getMapIcon(0)));

		// GeoCode(mChoosePoi.location);

		selectMarker.setOnClickListener(new IOverlayOnClickListener() {

			@Override
			public boolean onClick(Overlay arg0, int arg1) {


				if (touchMarker != null)
					mMapView.getMap().removeOverlay(touchMarker);

				touchMarker = null;

				// getGeoCoder().destroy();
				//
				// mGeoCoder = null;
				//
				// getPoiSearch().destroy();
				//
				// mPoiSearch = null;
				//

				// GeoCode(mChoosePoi.location);

				setSelectPoiResult(mChoosePoi);
				GeoCode(mChoosePoi.location);
				isSelect = true;

				return true;
			}
		});
	}

	Drawable selectMarkerDrawable, touchMarkerDrawable;

	public Drawable getMapIcon(int type) {

		if (type == 0) {
			if (selectMarkerDrawable == null) {

				Drawable tmp = getResources().getDrawable(R.drawable.icon_marker_blue);

				selectMarkerDrawable = zoomBitmap(tmp);
			}
			return selectMarkerDrawable;

		} else {
			if (touchMarkerDrawable == null) {

				Drawable tmp = getResources().getDrawable(R.drawable.icon_marker_green);

				touchMarkerDrawable = zoomBitmap(tmp);

			}
			return touchMarkerDrawable;

		}

	}

	private BitmapDrawable zoomBitmap(Drawable tmp) {

		int width = tmp.getIntrinsicWidth();
		int height = tmp.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(tmp);
		Matrix matrix = new Matrix();

		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

		int swidth = wm.getDefaultDisplay().getWidth();
		int sheight = wm.getDefaultDisplay().getHeight();

		float scaleWidth = ((float) (sheight * 0.11) / width);
		float scaleHeight = ((float) (sheight * 0.18) / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
		return new BitmapDrawable(null, newbmp);
	}

	private Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
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

	// public void getMapCenter() {
	//
	// if (mMapView != null) {
	// // 获取地图中心点坐标
	//
	// mCurGeoResult = null;
	//
	// currentlatLng = mMapView.getMap().getCenterPosition();
	// GeoCode(currentlatLng);
	//
	// // 设置结果显示
	// // mTv_Result.setText("经度:" + latLng.longitude + " ,纬度:" +
	// // latLng.latitude);
	//
	// }
	//
	// }

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

		if (selectMarker != null) {
			setSelectMarker();

		}

		if (touchMarker != null && isSelect == false) {

			touchMarker = (Marker) mMapView.getMap()
					.addOverlay(new MarkerOptions().position(touchMarker.getLatLng()).icon(getMapIcon(1)));

			isSelect = false;

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		getGeoCoder().setOnGetGeoCodeResultListener(null);
		if (mMapView != null) {
			mMapView.getMap().removeAllOverlay();
			mMapView.destroy();
		}
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

	@OnClick({ R.id.iv_titleleft, R.id.tv_confirm, R.id.et_search })
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_titleleft:
			this.finish();

			break;
		case R.id.et_search:
			this.finish();

			break;
		case R.id.tv_confirm:
			// this.finish();

			if (mCurGeoResult != null) {

				//Intent it = new Intent();

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

				bean.pcd = mCurGeoResult.addressDetail.province + mCurGeoResult.addressDetail.city
						+ mCurGeoResult.addressDetail.district;
				
				bean.latitude = mCurGeoResult.location.latitude;
				bean.longitude = mCurGeoResult.location.longitude;
				
				MLog.e("selectpoint", bean.address + " " + bean.kcode + " " + bean.uploadAddress);

				//it.putExtra("addressinfo", bean);
				/// it.putExtra("address", mCurGeoResult);

				if (TextUtils.isEmpty(bean.uploadAddress)) {

					Toast.makeText(this, "请选择有详细地址的点", Toast.LENGTH_SHORT).show();

				} else {


					MapSelectPointEvent event = new MapSelectPointEvent(bean);
					EventBus.getDefault().post(event);


					if(mStoreDetail!=null){


						UploadStoreAddr(bean);

					}else {

						finish();
					}

					//setResult(resultCode, it);
				//	finish();
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

		
		
		MLog.e("reverseGeo", result.errorCode+" "+result.errorMsg+" "+result.address);
		
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

	
	ReverseGeoCodeResult mChoosePoiGeoResult ;
	
	
	public void SetResult(final ReverseGeoCodeResult result) {

		if (isSelect) {
			// result!=null && mCurPoi!=null &&
			// (result.location.latitude!=mCurPoi.location.latitude
			// || result.location.longitude!=mCurPoi.location.longitude)
			
			if(result.location.latitude == mChoosePoi.location.latitude  && 
					result.location.longitude == mChoosePoi.location.longitude
					){
				
				
				mCurGeoResult = result;
				mChoosePoiGeoResult = result;
				tv_position_detail.setText(result.address);
			}
			return;

		}

		mCurGeoResult = result;

		MapSelectAndSearchActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				if (result == null) {

					tv_position.setText("获取位置失败");
					tv_position_detail.setText("");

					ll_addressdetail.setVisibility(View.GONE);
					Toast.makeText(MapSelectAndSearchActivity.this, "无法获取位置，请检查网络", Toast.LENGTH_SHORT).show();

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

			MapSelectAndSearchActivity.this.runOnUiThread(new Runnable() {

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
			MapSelectAndSearchActivity.this.runOnUiThread(new Runnable() {

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

		if (isSelect) {

			return;

		}

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

	public void setSelectPoiResult(PoiInfo mPoiInfo) {

		ShowResult();

		ll_addressdetail.setVisibility(View.VISIBLE);

		tv_position_detail.setText(mPoiInfo.address);
		
		if(mChoosePoiGeoResult != null)
			tv_position_detail.setText(mChoosePoiGeoResult.address);

		tv_position.setText(mPoiInfo.name);

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
							Toast.makeText(MapSelectAndSearchActivity.this, "标记成功", Toast.LENGTH_SHORT).show();


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
