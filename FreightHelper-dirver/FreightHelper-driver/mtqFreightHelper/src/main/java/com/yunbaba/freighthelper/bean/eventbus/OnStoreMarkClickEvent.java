package com.yunbaba.freighthelper.bean.eventbus;

import com.yunbaba.ols.module.delivery.bean.MtqStore;

/**
 * Created by zhonghm on 2018/6/5.
 */

public class OnStoreMarkClickEvent {

    public MtqStore store;


    public OnStoreMarkClickEvent(MtqStore store) {
        this.store = store;
    }
}
