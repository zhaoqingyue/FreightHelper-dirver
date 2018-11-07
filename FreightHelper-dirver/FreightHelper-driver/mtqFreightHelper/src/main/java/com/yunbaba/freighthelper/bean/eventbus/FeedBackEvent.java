package com.yunbaba.freighthelper.bean.eventbus;

import java.util.ArrayList;

import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBackInfo;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

public class FeedBackEvent {

	public ArrayList<FeedBackInfo> reasonlist;

	public MtqDeliStoreDetail storeDetail;
	public MtqDeliOrderDetail orderDetail;

	public FeedBackEvent(ArrayList<FeedBackInfo> reasonlist, MtqDeliStoreDetail storeDetail,
			MtqDeliOrderDetail orderDetail) {
		super();
		this.reasonlist = reasonlist;
		this.storeDetail = storeDetail;
		this.orderDetail = orderDetail;
	}

}
