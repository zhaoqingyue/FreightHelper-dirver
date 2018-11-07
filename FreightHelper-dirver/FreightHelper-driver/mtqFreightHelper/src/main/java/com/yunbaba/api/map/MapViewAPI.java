package com.yunbaba.api.map;

import hmi.packages.HPDefine.HPWPoint;

import java.util.List;

import android.content.Context;
import android.graphics.Point;

import com.cld.mapapi.map.CldMap;
import com.cld.mapapi.map.MapView;
import com.cld.mapapi.map.OverlayOptions;
import com.cld.mapapi.model.LatLng;
import com.cld.mapapi.search.exception.IllegalSearchArgumentException;
import com.cld.mapapi.search.geocode.GeoCoder;
import com.cld.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.cld.mapapi.search.geocode.ReverseGeoCodeOption;
import com.cld.mapapi.util.CldMapSdkUtils;
import com.cld.navisdk.CldNaviManager;
import com.cld.nv.location.CldCoordUtil;
import com.cld.nv.map.overlay.Overlay;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: MapViewAPI.java
 * @Prject: Freighthelper
 * @Package: com.mqt.api.map
 * @Description: 地图界面相关API
 * @author: zsx
 * @date: 2017-3-24 下午6:05:47
 * @version: V1.0
 */
public class MapViewAPI {
	private static final String TAG = "MapViewAPI";
	private static MapViewAPI mMapViewAPI;
	
	public static  MapViewAPI getInstance(){
		synchronized (TAG) {
			if (mMapViewAPI == null){
				synchronized (TAG) {
					mMapViewAPI = new MapViewAPI();
				}
			}
		}
		return mMapViewAPI;
	}
	
	/**
	 * @Title: createMapView
	 * @Description: 创建地图布局
	 * @param ctx :传入context
	 * @return
	 * @return: MapView
	 */
	public MapView createMapView(Context ctx){
		//这里不能用application context 不能多个activity都用的是时候就会出错的。
		MapView mapLayout = CldNaviManager.getInstance().createNMapView(ctx);
		
		return mapLayout;
	}
	
	 /**
	 * @Title: getMap
	 * @Description: 获取地图控件
	 * @param mapLayout :地图布局
	 * @return
	 * @return: CldMap
	 */
	public CldMap getMap(MapView mapLayout){
		if (mapLayout != null){
			return mapLayout.getMap();
		}
		return null;
	 }
	
	
	/**
	 * @Title: onPause
	 * @Description: 当Activity暂停时调用地图的暂停
	 * @param mapLayout:地图布局
	 * @return: void
	 */
	public void onPause(MapView mapLayout){
		if (mapLayout != null){
			mapLayout.onPause();
		}
	}
	
	/**
	 * @Title: onDestroy
	 * @Description: 当Activity销毁时调用地图的销毁
	 * @param mapLayout:地图布局
	 * @return: void
	 */
	public void onDestroy(MapView mapLayout){
		if (mapLayout != null){
			mapLayout.destroy();
		}
	}
	
	
	/**
	 * @Title: onResume
	 * @Description: 当Activity唤醒时调用地图唤醒
	 * @param mapLayout:地图布局
	 * @return: void
	 */
	public void onResume(MapView mapLayout){
		if (mapLayout != null){
			mapLayout.onResume();
		}		
	}
	
	/**
	 * @Title: update
	 * @Description: 刷新mMapLayout（包括引擎的mapview，指南针，放大缩小等按钮的状态）
	 * @return: void
	 */
	public void update(MapView mapLayout){
		if (mapLayout != null){
			mapLayout.update();
		}
	}
	
//	/**  该接口2.1.5去掉了
//	 * @Title: update
//	 * @Description: 只刷新引擎的地图.
//	 * @param flag:是否在地图线程刷新
//	 * @return: void
//	 */
//	public void update(boolean flag){
//		if (mMapLayout != null){
//			mMapLayout.update(flag);
//		}
//	}
//	
	
	/**
	 * @Title: setMapCenter
	 * @Description: 设置地图中心坐标（当前位置定位位置）
	 * @param center :sdk对外是经纬度，对内是xy。   latitude（纬度）是y，longitude（经度）是x
	 * @return: void
	 */
	public void setMapCenter(CldMap mapWidget,LatLng center){
		if (mapWidget != null){
			mapWidget.setMapCenter(center);
		}
	}
	
	public void setNMapCenter(CldMap mapWidget,LatLng center){
		if (mapWidget != null){
			mapWidget.setNMapCenter(center);
		}
	}
	
	
	
	/**
	 * @Title: setCompassPosition
	 * @Description: 设置指南針控件的位置
	 * @param p:坐标
	 * @return: void
	 */
	public void setCompassPosition(MapView mapLayout,Point p){
		if (mapLayout != null){
			mapLayout.setCompassPosition(p);
		}
	}
	
	/**
	 * @Title: setMapLogoPosition
	 * @Description: 设置地图上logo位置
	 * @param p
	 * @return: void
	 */
	public void setMapLogoPosition(MapView mapLayout,Point p){
		if (mapLayout != null){
			mapLayout.setMapLogoPosition(p);
		}
	}
	
	/**
	 * @Title: setScaleControlPosition
	 * @Description: 设置比例尺控件的位置
	 * @param p
	 * @return: void
	 */
	public void setScaleControlPosition(MapView mapLayout,Point p){
		if (mapLayout != null){
			mapLayout.setScaleControlPosition(p);
		}
	}
	
	/**
	 * @Title: setZoomControlsPosition
	 * @Description: 设置比例尺控件的位置
	 * @param p
	 * @return: void
	 */
	public void setZoomControlsPosition(MapView mapLayout, Point p){
		if (mapLayout != null){
			mapLayout.setZoomControlsPosition(p);
		}
	}
	
	/**
	 * @Title: showCompassControls
	 * @Description: 设置是否显示指南針控件
	 * @param show
	 * @return: void
	 */
	public void showCompassControls(MapView mapLayout, boolean show){
		if (mapLayout != null){
			mapLayout.showCompassControls(show);
		}
	}
	
	/**
	 * @Title: showScaleControl
	 * @Description: 设置是否显示比例尺控件
	 * @param show
	 * @return: void
	 */
	public void showScaleControl(MapView mapLayout,boolean show){
		if (mapLayout != null){
			mapLayout.showScaleControl(show);
		}
	}
	
	/**
	 * @Title: showScaleControl
	 * @Description: 设置是否显示缩放控件
	 * @param show
	 * @return: void
	 */
	public void showZoomControls(MapView mapLayout, boolean show){
		if (mapLayout != null){
			mapLayout.showZoomControls(show);
		}
	}
	
	/**
	 * @Title: setLocationIconEnabled
	 * @Description: 设置是否显示定位图标
	 * @param mapLayout
	 * @param show
	 * @return: void
	 */
	public void setLocationIconEnabled(MapView mapLayout, boolean show){
		if (mapLayout != null){
			mapLayout.getMap().setLocationIconEnabled(show);
		}
	}
	
	/**
	 * @Title: addOverlay
	 * @Description: 通过列表添加多个覆盖物
	 * @param options
	 * @return: void
	 */
	public List<Overlay> addOverlay(CldMap mapWidget,List<OverlayOptions> options){
		if (mapWidget != null){
			return  mapWidget.addOverlay(options);
		}
		return null;
	}
	
	/**
	 * @Title: removeOverlay
	 * @Description: TODO
	 * @param 移除多个覆盖物
	 * @return: void
	 */
	public void removeOverlay(CldMap mapWidget, List<Overlay> options){
		if (mapWidget != null){
			mapWidget.removeOverlay(options);
		}
	}
	
	/**
	 * @Title: removeAllOverlay
	 * @Description: 清空所有覆盖物
	 * @return: void
	 */
	public void removeAllOverlay(CldMap mapWidget){
		if (mapWidget != null){
			mapWidget.removeAllOverlay();
		}
	}
	
	
	/**
	 * @Title: setLocationIconEnabled
	 * @Description: 设置是否显示定位图标
	 * @param isShow
	 * @return: void
	 */
	public void setLocationIconEnabled(CldMap mapWidget, boolean isShow){
		if (mapWidget != null){
			mapWidget.setLocationIconEnabled(isShow);
		}
	}
	
	/**
	 * @Title: getKcode
	 * @Description: 获取K码
	 * @param x
	 * @param y
	 * @return
	 * @return: String
	 */
	public String getKcode(long x,long y){
		String Kcode = null;
		HPWPoint point = new HPWPoint();
		point.x = x;
		point.y = y;
		Kcode = CldCoordUtil.cldToKCode(point);
		return Kcode;
	}
	
	
	/**
	 * @Title: getGeoCodeResult
	 * @Description: 通过x,y获取逆地理编码
	 * @param x
	 * @param y
	 * @param geoCoderResultListener：
	 * @return
	 * @return: GeoCoder：返回逆地理编码实例。在Activity或者fragment的onDestroy 里要调用 GeoCoder.destroy();方法销毁
	 */
	public GeoCoder getGeoCodeResult(long x,long y, OnGetGeoCoderResultListener geoCoderResultListener){
		GeoCoder geoCoder = GeoCoder.newInstance();
		ReverseGeoCodeOption option = new ReverseGeoCodeOption();
		
		option.location.longitude = x;
		option.location.latitude = y;
		
		geoCoder.setOnGetGeoCodeResultListener(geoCoderResultListener);
		
		try {
			geoCoder.reverseGeoCode(option);//传入逆地理参数对象
		} catch (IllegalSearchArgumentException e) {

			e.printStackTrace();
		}
		
		return geoCoder;
	}
	
	/**
	 * @Title: zoomToSpan
	 * @Description: 缩放到合适比例，将传入的点坐标都显示出来
	 * @param latLngs
	 * @return: void
	 */
	public void	zoomToSpan(List<LatLng> latLngs){
		CldMapSdkUtils.zoomToSpan(latLngs);
	}
	
	
	
}
