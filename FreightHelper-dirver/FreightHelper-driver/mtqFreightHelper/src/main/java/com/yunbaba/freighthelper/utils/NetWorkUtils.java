package com.yunbaba.freighthelper.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 网络工具类 Created by alic on 16-4-8.
 */
public class NetWorkUtils {
    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static void isNetworkConnected(Context context, final OnNetworkListener listener) {
        if (context != null) {
            // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            // 判断NetworkInfo对象是否为空


            if (networkInfo != null) {
                MLog.e("checknetwork", "isconnect" + networkInfo.isConnected() + "  isavailable " + networkInfo.isAvailable());
                if (networkInfo.isAvailable()) {

                    if (listener != null)
                        listener.isAvailable(true);


//
//                    ThreadPoolTool.getInstance().execute(new Runnable() {
//                        @Override
//                        public void run() {

                    //  Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                    //
                    //int ret = 0;
//                            try {
//                                ret = InetAddress.getByName("www.baidu.com").isReachable(5000);
//
//                                if (ret) {
//                                    if (listener != null)
//                                        listener.isAvailable(true);
//                                } else {
//                                    if (listener != null)
//                                        listener.isAvailable(false);
//                                }
//                            } catch (Exception e1) {
//                                e1.printStackTrace();
//                                if (listener != null)
//                                    listener.isAvailable(false);
//                            }
////
                    //                          try {
                    //  Runtime runtime = Runtime.getRuntime();
//                    Process p = null;
//                    try {
//                        p = Runtime.getRuntime().exec("ping -c 3 -w 150 www.baidu.com");
//                        PingWorker worker = new PingWorker(p);
//                        worker.start();
//                        try {
//                            MLog.e("startchecknetwork","start"+System.currentTimeMillis()/1000);
//                            worker.join(3000);
//                            if (worker.exit != null){
//                              //  return worker.exit;
//                                MLog.e("startchecknetwork", "Process:" +System.currentTimeMillis()/1000+" res:"+ worker.exit);
//                                if(worker.exit == 0){
//                                    if (listener != null)
//                                        listener.isAvailable(true);
//                                }else{
//                                    if (listener != null)
//                                        listener.isAvailable(false);
//                                }
//
//
//                            } else{
//                               // throw new TimeoutException();
//                                if (listener != null)
//                                    listener.isAvailable(false);
//                            }
//
//                        }catch (InterruptedException ex) {
//                            worker.interrupt();
//                            //Thread.currentThread().interrupt();
//                           // throw ex;
//                            if (listener != null)
//                                listener.isAvailable(false);
//                        } finally {
//                            p.destroy();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        if (listener != null)
//                            listener.isAvailable(true);
//                    }
                    // int ret = p.waitFor();


//
//                                try {
//
//                                    if (worker.exit != null){
//                                        return worker.exit;
//                                    } else{
//                                        throw new TimeoutException();
//                                    }
//                                }


                    // 正常返回0，连接手机热点或者需要认证的wifi返回1
                    //|| ret == 1


//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                if (listener != null)
//                                    listener.isAvailable(false);
//                            }

//
//                        }
//                    });

//                    if (listener != null)
//                        listener.isAvailable(true);


                } else {


                    if (listener != null)
                        listener.isAvailable(false);


                }


            } else {

                if (listener != null)
                    listener.isAvailable(false);

            }


        }

//		if (listener!=null)
//			listener.isAvailable(false);
    }


    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            // 判断NetworkInfo对象是否为空


            if (networkInfo != null) {
                MLog.e("checknetwork", "isconnect" + networkInfo.isConnected() + "  isavailable " + networkInfo.isAvailable());
                if (networkInfo.isAvailable()) {

                    return true;


                } else {

                    return false;


                }


            } else {

                return false;

            }


        }else
            return true;


    }

    static class PingWorker extends Thread {
        private final Process process;
        private Integer exit;

        public PingWorker(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            try {
                exit = process.waitFor();
            } catch (InterruptedException e) {
                return;
            }
        }
    }


//    /**
//     * 判断是否为无网络或弱网络
//     *
//     * @param context
//     * @return
//     */
//    public static void isNetworkWeakOrNotConnect(Context context, final OnNetworkListener listener) {
//        if (context != null) {
//            // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
//            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            // 获取NetworkInfo对象
//            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
//            // 判断NetworkInfo对象是否为空
//
//
//
//
//            if (networkInfo != null) {
//
//
//                MLog.e("checknetwork", "isconnect"+networkInfo.isConnected()+"  isavailable "+networkInfo.isAvailable());
//
//                if (networkInfo.isAvailable()) {
//
//
//                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//
//                        String _strSubTypeName = networkInfo.getSubtypeName();
//
//
//                        boolean is2G = false;
//
//                        // TD-SCDMA   networkType is 17
//                        int networkType = networkInfo.getSubtype();
//                        switch (networkType) {
//                            case TelephonyManager.NETWORK_TYPE_GPRS:
//                            case TelephonyManager.NETWORK_TYPE_EDGE:
//                            case TelephonyManager.NETWORK_TYPE_CDMA:
//                            case TelephonyManager.NETWORK_TYPE_1xRTT:
//                            case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
//                                is2G = true;
//                                break;
//                            case TelephonyManager.NETWORK_TYPE_UMTS:
//                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
//                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
//                            case TelephonyManager.NETWORK_TYPE_HSDPA:
//                            case TelephonyManager.NETWORK_TYPE_HSUPA:
//                            case TelephonyManager.NETWORK_TYPE_HSPA:
//                            case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
//                            case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
//                            case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
//                                //strNetworkType = "3G";
//                                break;
//                            case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
//                                //strNetworkType = "4G";
//                                break;
//                            default:
//                                // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
//                                if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
//                                    //strNetworkType = "3G";
//                                } else {
//                                    // strNetworkType = _strSubTypeName;
//                                }
//
//                                break;
//                        }
//
//
//                        if (is2G) {
//                            if (listener != null)
//                                listener.isAvailable(false);
//
//                            return;
//
//
//                        }
//
//
//                    }
//
//
//                    ThreadPoolTool.getInstance().execute(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            //  Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
//
//                            Runtime runtime = Runtime.getRuntime();
//                            try {
//                                Process p = runtime.exec("ping -c 3 -w 150 www.baidu.com");
//                                int ret = p.waitFor();
//                                MLog.i("Avalible", "Process:" + ret);
//
//                                // 正常返回0，连接手机热点或者需要认证的wifi返回1
//                                if (ret == 0 || ret == 1) {
//                                    if (listener != null)
//                                        listener.isAvailable(true);
//                                } else {
//                                    if (listener != null)
//                                        listener.isAvailable(false);
//                                }
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                if (listener != null)
//                                    listener.isAvailable(false);
//                            }
//
//
//                        }
//                    });
//
////                    if (listener != null)
////                        listener.isAvailable(true);
//
//
//                } else {
//
//
//                    if (listener != null)
//                        listener.isAvailable(false);
//
//
//                }
//
//
//            } else {
//
//                if (listener != null)
//                    listener.isAvailable(false);
//
//            }
//
//
//        }
//
////		if (listener!=null)
////			listener.isAvailable(false);
//    }


    /**
     * 判断WIFI网络是否可用
     *
     * @param context
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            // 判断NetworkInfo对象是否为空 并且类型是否为MOBILE
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 获取当前网络连接的类型信息 原生
     *
     * @param context
     * @return
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            // 获取手机所有连接管理对象
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                // 返回NetworkInfo的类型
                return networkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 获取当前的网络状态 ：没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2 自定义
     *
     * @param context
     * @return
     */
    public static int getAPNType(Context context) {
        // 结果返回值
        int netType = 0;
        // 获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        // NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            return netType;
        }
        // 否则 NetworkInfo对象不为空 则获取该networkInfo的类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            // WIFI
            netType = 1;
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // 3G 联通的3G为UMTS或HSDPA 电信的3G为EVDO
            if (nSubType == TelephonyManager.NETWORK_TYPE_LTE && !telephonyManager.isNetworkRoaming()) {
                netType = 4;
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0 && !telephonyManager.isNetworkRoaming()) {
                netType = 3;
                // 2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                    || nSubType == TelephonyManager.NETWORK_TYPE_CDMA && !telephonyManager.isNetworkRoaming()) {
                netType = 2;
            } else {
                netType = 2;
            }
        }
        return netType;
    }

    /**
     * 判断GPS是否打开 ACCESS_FINE_LOCATION权限
     *
     * @param context
     * @return
     */
    public static boolean isGPSEnabled(Context context) {
        // 获取手机所有连接LOCATION_SERVICE对象
        LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

//private OnNetworkListener mlistener;

    public interface OnNetworkListener {


        void isAvailable(boolean isAvailable);

    }

}
