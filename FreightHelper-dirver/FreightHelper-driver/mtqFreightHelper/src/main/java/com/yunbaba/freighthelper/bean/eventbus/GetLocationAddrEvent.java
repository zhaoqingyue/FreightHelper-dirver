package com.yunbaba.freighthelper.bean.eventbus;

import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

/**
 * Created by zhonghm on 2018/4/17.
 */

public class GetLocationAddrEvent {

    public MtqDeliStoreDetail storeDetail;

    public MtqDeliOrderDetail orderDetail;

    public GetLocationAddrEvent(MtqDeliStoreDetail storeDetail, MtqDeliOrderDetail orderDetail) {
        this.storeDetail = storeDetail;
        this.orderDetail = orderDetail;
    }
}
