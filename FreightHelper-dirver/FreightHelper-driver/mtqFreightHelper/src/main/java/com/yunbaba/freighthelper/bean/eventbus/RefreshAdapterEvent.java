package com.yunbaba.freighthelper.bean.eventbus;

/**
 * Created by zhonghm on 2018/4/17.
 */

public class RefreshAdapterEvent {

    public String waybill;
    public String taskid;


    public RefreshAdapterEvent(String waybill, String taskid) {
        this.waybill = waybill;
        this.taskid = taskid;
    }
}
