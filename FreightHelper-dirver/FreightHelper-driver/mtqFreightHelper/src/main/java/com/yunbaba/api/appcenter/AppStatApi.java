package com.yunbaba.api.appcenter;

import android.app.Activity;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.baidu.mobstat.StatService;
import com.yunbaba.api.account.AccountAPI;

public class AppStatApi {

    //  Baidu Statistic Event ID
    public static final String BD_STAT_TASK_ITEM_CLICK = "YDD-RW-01";
    public static final String BD_STAT_ROUTING_POINT_SEARCH_CLICK = "YDD-LY-01";
    public static final String BD_STAT_MAP_MODE_CLICK = "YDD-LY-02";
    public static final String BD_STAT_FEEDBACK_CLICK = "YDD-XQ-01";
    public static final String BD_STAT_FEEDBACK_COMMIT_CLICK = "YDD-XQ-02";
    public static final String BD_STAT_NAVI_CLICK = "YDD-XQ-03";
    public static final String BD_STAT_NAVI_START_CLICK = "YDD-DH-01";
    public static final String BD_STAT_NAVI_CLOSE_CLICK = "YDD-DH-02";
    public static final String BD_STAT_NAVI_SETTING_CLICK = "YDD-DH-03";
    public static final String BD_STAT_NAVI_PREF_AVOID_CONG = "YDD-DH-04";
    public static final String BD_STAT_NAVI_PREF_HIGHWAY_PRIORITY = "YDD-DH-05";
    public static final String BD_STAT_NAVI_PREF_AVOID_HIGHWAY = "YDD-DH-06";
    public static final String BD_STAT_GET_LOCATION_POINT_CLICK = "YDD-XQ-04";
    public static final String BD_STAT_ROUTING_POINT_START_CLICK = "YDD-XQ-05";
    public static final String BD_STAT_ROUTING_POINT_PAUSE_CLICK = "YDD-XQ-06";
    public static final String BD_STAT_ROUTING_POINT_NEXT_CLICK = "YDD-WC-01";

//    USELESS FIELD, USE THE TEXT TO LOGCAT FOR SERACHING.
    private static final String LOGCAT_TAG = "BaiduMobStat";


    public static void statOnEventWithLoginName(Context context, String eventId) {
        statOnEvent(context, eventId, AccountAPI.getInstance().getLoginName());
    }

    public static void statStart(Context context) {
        StatService.start(context);
    }

    public static void statSetDebugOn(boolean on) {
        StatService.setDebugOn(on);
    }

    public static void statOnEvent(Context context, String eventId, String customId) {
        StatService.onEvent(context, eventId, customId);
    }

//    FOR ACTIVITY START STATISTIC
    public static void statOnPageStart(Activity page) {
        StatService.onResume(page);
    }

//    FOR FRAGMENT START STATISTIC
    public static void statOnPageStart(Context context, Fragment page) {
        StatService.onPageStart(context, page.getClass().getSimpleName());
    }

//    FOR ACTIVITY PAUSE STATISTIC
    public static void statOnPagePause(Activity page) {
        StatService.onPause(page);
    }

//    FOR FRAGMENT PAUSE STATISTIC
    public static void statOnPagePause(Context context, Fragment page) {
        StatService.onPageEnd(context, page.getClass().getSimpleName());
    }


}
