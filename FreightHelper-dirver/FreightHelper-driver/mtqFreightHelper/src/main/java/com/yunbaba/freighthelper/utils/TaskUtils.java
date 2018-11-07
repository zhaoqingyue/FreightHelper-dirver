package com.yunbaba.freighthelper.utils;

import android.content.Context;
import android.text.TextUtils;

import com.cld.location.CldLocation;
import com.cld.mapapi.model.LatLng;
import com.cld.nv.location.CldCoordUtil;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cld.navi.region.CldRegionEx;

public class TaskUtils {

    public static boolean isStorePosUnknown(MtqDeliStoreDetail store) {

        return (store.storex == 0 && store.storey == 0);

    }

    public static boolean isFinalUnFinishPointIsReturnPoint(Context context, String taskid) {

        return  false;
    }

    public static String  getErrorMsgFromTaskErrorCode(int errorCode){
        String str =  "";
        switch (errorCode){


            case 1404:
                str = "司机有正在作业任务不能操作其它任务状态";
                break;
            case 1095:
                str = "当前单已经已取消（已删除）";
                break;
            case 1402:
                str = "任务正在优化";
                break;
            case 1096:
                str = "运货单已经取消（已删除）";
                break;
            case 1403:
                str = "车辆正在运货不能开启该车辆的其它运货单";
                break;
            case 1405:
                str = "所有送货点已完成";
                break;
            case 1407:
                str = "送货点没有全部完成";
                break;

            case 10001:
            case 10002:
            case 10003:
            case 10004:
            case 10005:
            case 10006:

            case 20001:
                str = "网络通信出现问题，请稍后再试。";
                break;


//            case 2000:
//                str = "";
//                break;
//
//
//            case     10100:
//
//                break;
            default:
                str = "请求失败:"+errorCode;
                break;

        }

        return str;
    }

    public static String parseDistrictCode(String district_code) {
        if (district_code == null || TextUtils.isEmpty(district_code))
            return "无位置";

        String cityName = "";
        String[] sArray = district_code.split(";");
        //MLog.e("parseDistrictCode", "sArray.length: " + sArray.length);
        for (int i=0; i<sArray.length; i++) {
            //MLog.e("parseDistrictCode", "sArray[" + i + "]: " + sArray[i]);
            String str = sArray[i];
            if (str.length() == 6) {
                StringBuilder sb = new StringBuilder(str);
                // 将后两位替换成"00"，如440304变成440300（将3级行政编码转化成2级行政编码）
                sb.replace(4, 6, "00");
                str = sb.toString();
                sArray[i] = str;
            } else if (str.length() == 5) {
                StringBuilder sb = new StringBuilder(str);
                // 将后四位替换成"0000"，如10102变成10000
                sb.replace(1, 5, "0000");
                str = sb.toString();
                sArray[i] = str;
            }
        }

        // 去重
        ArrayList<String> list = new ArrayList();
        for (int i=0; i<sArray.length; i++) {
            if (!list.contains(sArray[i]))
                list.add(sArray[i]);
        }

        //MLog.e("parseDistrictCode", "list.size: " + list.size());
        for (int i=0; i<list.size(); i++) {
            int cityId =  Integer.parseInt(list.get(i));
            //MLog.e("parseDistrictCode", "cityId: " + cityId);
            StringBuilder sb = new StringBuilder(list.get(i));
            if(CldRegionEx.getInstance().isSpecialDistrict(cityId)) {
                sb.replace(3, 4, "0");
                if (sb.toString().equals("852000")) {
                    cityName += " 香港";
                } else if (sb.toString().equals(853000)) {
                    cityName += " 澳门";
                }
            } else if (CldRegionEx.getInstance().isMunicipality(cityId)) {
                if (sb.toString().equals("10000")) {
                    cityName += " 北京";
                } else if (sb.toString().equals("20000")) {
                    cityName += " 天津";
                } else if (sb.toString().equals("30000")) {
                    cityName += " 上海";
                } else if (sb.toString().equals("40000")) {
                    cityName += " 重庆";
                }
            } else {
                // 将中间两位替换成"00"，如440300变成440000（将2级行政编 码转化成1级行政编码）
                sb.replace(2, 4, "00");
                int provinceId =  Integer.parseInt(sb.toString());
                ArrayList<CldRegionEx.CityLevel> cityList =  CldRegionEx.getInstance().
                        getCityListByProvinceId(provinceId);
                if (cityList != null && !cityList.isEmpty()) {
                    for (int j=0; j<cityList.size(); j++) {
                        if (cityId == cityList.get(j).id) {
                            String name = cityList.get(j).name;
                            //MLog.e("parseDistrictCode", "name: " + name);
                            if (name.endsWith("朝鲜族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 6);
                            } else if (name.endsWith("土家族苗族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 8);
                            } else if (name.endsWith("黎族自治县")) {
                                cityName += " " + name.substring(0, name.length() - 5);
                            } else if (name.endsWith("黎族苗族自治县")) {
                                cityName += " " + name.substring(0, name.length() - 7);
                            } else if (name.endsWith("藏族羌族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 7);
                            } else if (name.endsWith("藏族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 5);
                            } else if (name.endsWith("彝族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 5);
                            } else if (name.endsWith("布依族苗族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 8);
                            } else if (name.endsWith("苗族侗族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 7);
                            } else if (name.endsWith("哈尼族彝族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 8);
                            } else if (name.endsWith("壮族苗族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 7);
                            } else if (name.endsWith("傣族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 5);
                            } else if (name.endsWith("白族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 5);
                            } else if (name.endsWith("傣族景颇族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 8);
                            } else if (name.endsWith("傈僳族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 6);
                            } else if (name.endsWith("回族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 5);
                            } else if (name.endsWith("蒙古族藏族自治州")) {
                                cityName += " " + name.substring(0, name.length() - 8);
                            } else if (name.endsWith("蒙古自治州")) {
                                cityName += " " + name.substring(0, name.length() - 5);
                            } else if (name.endsWith("柯尔克孜自治州")) {
                                cityName += " " + name.substring(0, name.length() - 7);
                            } else if (name.endsWith("哈萨克自治州")) {
                                cityName += " " + name.substring(0, name.length() - 6);
                            } else if (name.endsWith("自治州")) {
                                cityName += " " + name.substring(0, name.length() - 3);
                            } else if (name.endsWith("地区")) {
                                cityName += " " + name.substring(0, name.length() - 2);
                            } else if (name.endsWith("林区")) {
                                cityName += " " + name.substring(0, name.length() - 2);
                            } else if (name.endsWith("市")) {
                                cityName += " " + name.substring(0, name.length() - 1);
                            } else if (name.endsWith("县")) {
                                cityName += " " + name.substring(0, name.length() - 1);
                            } else {
                                cityName += " " + name;
                            }
                        }
                    }
                }
            }
        }
        //MLog.e("parseDistrictCode", "cityName: " + cityName);
        if (TextUtils.isEmpty(cityName))
            cityName = "无位置";

        return cityName.trim();
    }

    public static String keepADecimal(String original) {
        if (!original.contains("."))
            return original;

        String format = "";
        DecimalFormat df = new DecimalFormat("0.0");
        format = df.format(new BigDecimal(original));
        if (format.equals("0.0"))
            format = "0";

        if (format.endsWith(".0"))
            format = format.substring(0, format.length() - 2);

        return format;
    }

    /**
     * 截取门店名称，不显示门店编码
     *
     * add by zhaoqy 2018-04-27
     */
    public static String interceptStoreName(String name) {
        if (TextUtils.isEmpty(name))
            return "";

        String storeName = "";
        if (name.contains("-")) {
            storeName = name.subSequence(name.indexOf("-") + 1,
                    name.length()).toString();
        } else {
            storeName = name;
        }
        return storeName;
    }

    public static  boolean isNetWorkError(int errorCode) {

        boolean isNetWorkError = false;
        switch (errorCode) {
            case 10001:
            case 10002:
            case 10003:
            case 10004:
            case 10005:
            case 10006:
            case 20001:
                isNetWorkError = true;
                break;

        }


        if(errorCode>=10100  && errorCode<=10199){

            isNetWorkError = true;

        }


        return isNetWorkError;


    }

    public static  boolean isNetWorkError2(int errorCode) {

        boolean isNetWorkError = false;
        switch (errorCode) {
            case 10001:
            case 10002:
            case 10003:
            case 10004:
            case 10005:
            case 10006:
            case 20001:
                isNetWorkError = true;
                break;

        }

        return isNetWorkError;


    }


    public static boolean isNotFailLocation(CldLocation location) {


        if(location.getLongitude() ==0 && location.getLatitude() == 0)
            return  false;
        else {

            LatLng tmp = new LatLng(location.getLatitude(), location.getLongitude());

           // currentLocationBean = new AddressBean();

            if(CldCoordUtil.cldToKCode(tmp).equals("100000000")){
                return  false;
            }else
                 return  true;




        }

    }

    public static boolean isNeedUpdateInfoCode(int errCode) {


        boolean isNeedUpdate = false;

        if(errCode == 2000 || errCode == 1408)
            isNeedUpdate = true;

        return isNeedUpdate;


    }

//
//    /**
//     * 任务最后一个未完成运货点是否是回程点
//     */
//    public static boolean isFinalUnFinishPointIsReturnPoint(Context context, String taskid) {
//
//
//        if (context == null || taskid == null)
//            return false;
//        try {
//            int res = SPHelper.getInstance(context).isFinalUnFinishPointIsReturnPoint(taskid);
//
//
//            if (res == 1) {
//                return true;
//            } else if (res == -1) {
//                return false;
//            } else {
//
//
//                MtqDeliTaskDetail taskdetail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);
//
//                if (taskdetail == null || taskdetail.getStore() == null) {
//                    return false;
//                }
//
//                if (!(taskdetail.getIsback() == 1 && (taskdetail.getFinishcount() == taskdetail.getTotal() - 1))) {
//                    return false;
//                }
//
//                boolean isfinalreturn = false;
//
//
//                traversal:
//                for (MtqDeliStoreDetail store : taskdetail.getStore()) {
//
//                    if (store.storestatus != 2 && store.optype == 4) {
//
//
//                        isfinalreturn = true;
//                        break traversal;
//                    }
//
//
//                }
//
//                if (isfinalreturn) {
//                    SPHelper.getInstance(context).setIsFinalUnFinishPointIsReturnPoint(taskid, 1);
//                } else {
//                    SPHelper.getInstance(context).setIsFinalUnFinishPointIsReturnPoint(taskid, -1);
//                }
//
//
//                if (isfinalreturn) {
//                    return true;
//                } else {
//                    return false;
//                }
//
//            }
//
//        } catch (Exception e) {
//            return false;
//        }
//    }

//    public static void checkIsFinalUnFinishPointIsReturnPoint(String taskid, String waybill, String ewaybill, String storeId, int status) {
//
//
//       // MLog.e("设置状态","检查状态"+taskid);
//
//        MtqDeliTaskDetail taskdetail = TaskOperator.getInstance().getTaskDetailDataFromDB(taskid);
//
//        if (taskdetail == null || taskdetail.getStore() == null)
//            return;
//
//
//        try {
//
//            if (TextUtils.isEmpty(storeId)) {
//                traversal:
//                for (MtqDeliStoreDetail store : taskdetail.getStore()) {
//
//                    if (store.storeid.equals(storeId)) {
//
//
//                        store.storestatus = status;
//                        break traversal;
//                    }
//
//
//                }
//
//
//
//
//            }
//
//             if(status == 2) {
//
//                 int finishcount = 0;
//
//                 for (MtqDeliStoreDetail store : taskdetail.getStore()) {
//
//                     if (store.storestatus == 2 ) {
//
//                         finishcount+=1;
//
//                     }
//
//
//                 }
//
//                 taskdetail.setFinishcount(finishcount);
//
//             }
//
//            if (!(taskdetail.getIsback() == 1 && (taskdetail.getFinishcount() == taskdetail.getTotal() - 1))) {
//
//                SPHelper.getInstance(context).setIsFinalUnFinishPointIsReturnPoint(taskid, -1);
//
//                return;
//            }
//
//
//
//            boolean isfinalreturn = false;
//
//
//            traversal:
//            for (MtqDeliStoreDetail store : taskdetail.getStore()) {
//
//                if (store.storestatus != 2 && store.optype == 4) {
//
//
//                    isfinalreturn = true;
//                    break traversal;
//                }
//
//
//            }
//
//            if (isfinalreturn) {
//                SPHelper.getInstance(context).setIsFinalUnFinishPointIsReturnPoint(taskid, 1);
//            } else {
//                SPHelper.getInstance(context).setIsFinalUnFinishPointIsReturnPoint(taskid, -1);
//            }
//
//        } catch (Exception e) {
//
//        }
//    }

}
