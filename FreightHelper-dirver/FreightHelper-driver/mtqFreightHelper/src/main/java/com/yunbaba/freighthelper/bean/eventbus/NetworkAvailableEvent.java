package com.yunbaba.freighthelper.bean.eventbus;

/**
 * Created by zhonghm on 2018/3/28.
 */

public class NetworkAvailableEvent {


   public boolean isAvailable;


    public NetworkAvailableEvent(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
