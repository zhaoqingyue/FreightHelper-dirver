package com.yunbaba.api.trunk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.cld.mapapi.model.LatLng;
import com.cld.mapapi.search.exception.IllegalSearchArgumentException;
import com.cld.mapapi.search.geocode.GeoCodeResult;
import com.cld.mapapi.search.geocode.GeoCoder;
import com.cld.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.cld.mapapi.search.geocode.ReverseGeoCodeOption;
import com.cld.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cld.mapapi.util.DistanceUtil;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.yunbaba.api.trunk.listner.OnBooleanListner;
import com.yunbaba.freighthelper.bean.AddressBean;
import com.yunbaba.freighthelper.bean.OfflineLocationBean;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.tool.CldKDeviceAPI;

import java.util.List;

import hmi.packages.HPDefine;

/**
 * Created by zhonghm on 2018/4/16.
 */

public class OfflineLocationTool {

    static LocationManager locationManager;
    static GeoCoder mGeoCoder;

    /**
     * 如果开启正常，则会直接进入到显示页面，如果开启不正常，则会进行到GPS设置页面
     */
    public static void registerLocation(Context context, LocationListener locationListener) {


        String serviceName = Context.LOCATION_SERVICE;

        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(serviceName);

        // 查找到服务信息
        Criteria criteria = new Criteria();
        // 高精度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        // 低功耗
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        // 获取GPS信息
        String provider = locationManager.getBestProvider(criteria, true);
        // 通过GPS获取位置
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // Location location = locationManager.getLastKnownLocation(provider);


        // updateToNewLocation(location);

        // 设置监听*器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米

        locationManager.requestLocationUpdates(provider, 1 * 1000, 10, locationListener);
    }


    public static void unregisterLocation(Context context, LocationListener locationListener) {

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            try {
                locationManager.removeUpdates(locationListener);
            } catch (Exception e) {

            }


        }


    }


    public static void UploadStoreAddr(Context context,MtqDeliStoreDetail mStoreDetail, AddressBean bean, long uptime,CldKDeliveryAPI.ICldUploadStoreListListener listener) {


        boolean isNeedUpdateLocation = false;

        if (bean != null && !TaskUtils.isStorePosUnknown(mStoreDetail)) {

            //LatLng p1LL = CldCoordUtil.gcjToCLDEx(CldCoordUtil.cldToGCJ(nextPoint));

            //LatLng p2LL = CldCoordUtil.gcjToCLDEx(CldCoordUtil.cldToGCJ(point));


            HPDefine.HPWPoint nextPoint = new HPDefine.HPWPoint();
            nextPoint.x = mStoreDetail.storex;
            nextPoint.y = mStoreDetail.storey;
            LatLng p1LL = new LatLng(nextPoint.y,nextPoint.x);
                  //  CldCoordUtil.gcjToCLDEx(CldCoordUtil.cldToGCJ(nextPoint));

            LatLng p2LL = new LatLng();
            p2LL.latitude = bean.latitude;
            p2LL.longitude = bean.longitude;

            double dis = DistanceUtil.getDistance(p2LL, p1LL);

            int range = GeneralSPHelper.getInstance(context).getCompanyWantRange(mStoreDetail.corpId);

            if (dis > range)
                isNeedUpdateLocation = true;

        }

        //(TaskUtils.isStorePosUnknown(mStoreDetail) && bean != null) || isNeedUpdateLocation
        if (true ) {


            CldSapKDeliveryParam.CldDeliUploadStoreParm parm = new CldSapKDeliveryParam.CldDeliUploadStoreParm();

            // parm.address = tvPosition.getText().toString();
            parm.corpid = mStoreDetail.corpId;

            if (!TextUtils.isEmpty(mStoreDetail.linkman))
                parm.linkman = mStoreDetail.linkman;

            if (!TextUtils.isEmpty(mStoreDetail.linkphone))
                parm.phone = mStoreDetail.linkphone;

            if (!TextUtils.isEmpty(mStoreDetail.storecode))
                parm.storecode = mStoreDetail.storecode;

            if (TextUtils.isEmpty(mStoreDetail.storecode)) {
                if(mStoreDetail.optype == 5)
                    parm.settype = 3;
                else
                    parm.settype = 1;
            } else
                parm.settype = 3;
            parm.storeid = mStoreDetail.storeid;

            parm.storename = mStoreDetail.storename;
            parm.iscenter = 0;
            parm.storekcode = bean.kcode; // "";//"";//

            if( !TextUtils.isEmpty(bean.uploadAddress)) {
                parm.address = bean.uploadAddress;
            }else {
                parm.address = mStoreDetail.storeaddr;
            }
            parm.extpic = "";


            parm.uptime = uptime;

            CldKDeliveryAPI.getInstance().uploadStore(parm, listener);


//                    new CldOlsInterface.ICldResultListener() {
//
//                @Override
//                public void onGetResult(int errCode) {
//
//
//                }
//
//
//
//                @Override
//                public void onGetReqKey(String tag) {
//
//
//                }
//            }


        }else{

            if(listener!=null) {
                listener.onGetResult(0,"请求成功");
            }
//            QueryBuilder<OfflineLocationBean> qb = new QueryBuilder<OfflineLocationBean>(OfflineLocationBean.class)
//                    .whereIn("_taskidandwaybill", new Object[]{(mStoreDetail.taskId + mStoreDetail.waybill)});
//
//            List<OfflineLocationBean> res = OrmLiteApi.getInstance().getLiteOrm().query(qb);
//
//            if (res != null && res.size() > 0) {
//
//
//                OrmLiteApi.getInstance().delete(res.get(0));
//
//            }




        }

    }


    public static synchronized GeoCoder getGeoCoder() {

        if (mGeoCoder == null)
            mGeoCoder = GeoCoder.newInstance();

        return mGeoCoder;

    }


    public static synchronized void deleteGeoCoder() {

        if (mGeoCoder != null)
            mGeoCoder = null;


    }


    public static void GeoCodeAndUpload(final Activity context, final AddressBean currentLocationBean, final MtqDeliStoreDetail storeDetail) {

        WaitingProgressTool.showProgress(context);

        ReverseGeoCodeOption option = new ReverseGeoCodeOption();

        // 设置逆地理坐标的经度值
        option.location.longitude = currentLocationBean.longitude;

        // 设置逆地理坐标的纬度值
        option.location.latitude = currentLocationBean.latitude;

        getGeoCoder().setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult mCurGeoResult) {


                if (context.isFinishing())
                    return;

                WaitingProgressTool.closeshowProgress();


                if (mCurGeoResult != null) {

                    MLog.e("SearchSDK", mCurGeoResult.address + "\n" + mCurGeoResult.businessCircle);

                    if (mCurGeoResult.errorCode != 0) {
                        // Toast.makeText(this,
                        // result != null && !TextUtils.isEmpty(result.errorMsg) ?
                        // result.errorMsg : "逆地理编码失败",
                        // Toast.LENGTH_LONG).show();
                        // currentLocationBean = null;

                        return;

                    }
                    if (TextUtils.isEmpty(mCurGeoResult.address)) {// 返回地址是否为空判断
                        // mTv_Name.setText( result != null
                        // && !TextUtils
                        // .isEmpty(result.errorMsg) ? result.errorMsg
                        // : "逆地理编码失败");

                        // SetResult(null);

                        // currentLocationBean = null;

                    } else {


                        // SetResult(result);

                        currentLocationBean.address = mCurGeoResult.address;
                        currentLocationBean.uploadAddress = mCurGeoResult.address.replaceFirst(mCurGeoResult.addressDetail.province, "")
                                .replaceFirst(mCurGeoResult.addressDetail.city, "")
                                .replaceFirst(mCurGeoResult.addressDetail.district, "");
                        currentLocationBean.pcd = (mCurGeoResult.addressDetail.province + mCurGeoResult.addressDetail.city + mCurGeoResult.addressDetail.district)
                                .replaceAll("\\s*", "");


                        int range = GeneralSPHelper.getInstance(context).getCompanyWantRange(storeDetail.corpId);


                        UploadStoreAddr(context,storeDetail, currentLocationBean, CldKDeviceAPI.getSvrTime(),new CldKDeliveryAPI.ICldUploadStoreListListener() {
                            @Override
                            public void onGetResult(int errCode,String errMsg) {

                                if (errCode == 0 || !TaskUtils.isNetWorkError2(errCode)) {

                                    //SPHelper2.getInstance(context).deleteStoreAddr(storeDetail);
                                    ThreadPoolTool.getInstance().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            QueryBuilder<OfflineLocationBean> qb = new QueryBuilder<OfflineLocationBean>(OfflineLocationBean.class)
                                                    .whereIn("_taskidandwaybill", new Object[]{(storeDetail.taskId + storeDetail.waybill)});

                                            List<OfflineLocationBean> res = OrmLiteApi.getInstance().getLiteOrm().query(qb);

                                            if (res != null && res.size() > 0) {


                                                OrmLiteApi.getInstance().delete(res.get(0));

                                            }


                                            //listner.onResult(res);
                                        }
                                    });


                                } else {

                                    OfflineLocationBean bean = new OfflineLocationBean(storeDetail.taskId + storeDetail.waybill, storeDetail, currentLocationBean, CldKDeviceAPI.getSvrTime());


                                    OrmLiteApi.getInstance().save(bean);

                                }

                            }

                            @Override
                            public void onGetReqKey(String tag) {

                            }
                        });


                    }

                }


            }
        });
        try {
            getGeoCoder().reverseGeoCode(option);// 传入逆地理参数对象
        } catch (IllegalSearchArgumentException e) {



            MLog.e("searcherror", e.getMessage() + "");
            e.printStackTrace();
        }

    }


    public static void CheckAddrAndUpload(final Context context,final OfflineLocationBean bean, final OnBooleanListner lis,final boolean isNeedSave) {

        //WaitingProgressTool.showProgress(context);


        if (TextUtils.isEmpty(bean.addressBean.address)) {


            ReverseGeoCodeOption option = new ReverseGeoCodeOption();

            // 设置逆地理坐标的经度值
            option.location.longitude = bean.addressBean.longitude;

            // 设置逆地理坐标的纬度值
            option.location.latitude = bean.addressBean.latitude;

            getGeoCoder().setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                @Override
                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                }

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult mCurGeoResult) {


                    if (mCurGeoResult != null) {

                        MLog.e("SearchSDK", mCurGeoResult.address + "\n" + mCurGeoResult.businessCircle);

                        if (mCurGeoResult.errorCode != 0) {
                            if(isNeedSave)
                            OrmLiteApi.getInstance().save(bean);

                            if (lis != null)
                                lis.onResult(false);
                            return;

                        }
                        if (TextUtils.isEmpty(mCurGeoResult.address)) {// 返回地址是否为空判断

                            if(isNeedSave)
                            OrmLiteApi.getInstance().save(bean);

                            if (lis != null)
                                lis.onResult(false);
                        } else {


                            // SetResult(result);

                            bean.addressBean.address = mCurGeoResult.address;
                            bean.addressBean.uploadAddress = mCurGeoResult.address.replaceFirst(mCurGeoResult.addressDetail.province, "")
                                    .replaceFirst(mCurGeoResult.addressDetail.city, "")
                                    .replaceFirst(mCurGeoResult.addressDetail.district, "");
                            bean.addressBean.pcd = (mCurGeoResult.addressDetail.province + mCurGeoResult.addressDetail.city + mCurGeoResult.addressDetail.district)
                                    .replaceAll("\\s*", "");


                            GotoUploadAddr(context,bean,isNeedSave, lis);


                        }

                    } else {

                        if(isNeedSave)
                        OrmLiteApi.getInstance().save(bean);

                        if (lis != null)
                            lis.onResult(false);

                    }
                }
            });
            try {
                getGeoCoder().reverseGeoCode(option);// 传入逆地理参数对象
            } catch (IllegalSearchArgumentException e) {


                if(isNeedSave)
                    OrmLiteApi.getInstance().save(bean);

                if (lis != null)
                    lis.onResult(false);

                MLog.e("searcherror", e.getMessage() + "");
                e.printStackTrace();
            }

        } else {


            GotoUploadAddr(context,bean, isNeedSave, lis);


        }


    }


    public synchronized static void GotoUploadAddr(Context context,final OfflineLocationBean bean, final boolean isNeedSave, final OnBooleanListner lis) {


        UploadStoreAddr(context,bean.mStoreDetail, bean.addressBean, bean.updatetime,new CldKDeliveryAPI.ICldUploadStoreListListener() {
            @Override
            public void onGetResult(int errCode,String errMsg) {

                if (errCode == 0 || errCode == 2000 || errCode == 2100 || !TaskUtils.isNetWorkError(errCode)) {

                    //SPHelper2.getInstance(context).deleteStoreAddr(storeDetail);
                    ThreadPoolTool.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            QueryBuilder<OfflineLocationBean> qb = new QueryBuilder<OfflineLocationBean>(OfflineLocationBean.class)
                                    .whereIn("_taskidandwaybill", new Object[]{(bean.mStoreDetail.taskId + bean.mStoreDetail.waybill)});

                            List<OfflineLocationBean> res = OrmLiteApi.getInstance().getLiteOrm().query(qb);

                            if (res != null && res.size() > 0) {


                                OrmLiteApi.getInstance().delete(res.get(0));

                            }

                            if (lis != null)
                                lis.onResult(true);
                            //listner.onResult(res);
                        }
                    });


                } else {
                    if (lis != null)
                        lis.onResult(false);

                    if (isNeedSave) {

                        OrmLiteApi.getInstance().save(bean);
                    }

                    //OrmLiteApi.getInstance().save();

                }

            }

            @Override
            public void onGetReqKey(String tag) {

            }
        });

    }


}
