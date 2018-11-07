package com.yunbaba.freighthelper.bean;

import java.util.List;

import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.Examinationdetail;

public class LastCarCheckResultBean {
	
	
	public LastCarCheckResultBean(long time, List<Examinationdetail> result) {
		super();
		this.time = time;
		this.result = result;
	}
	public long time;
	public List<Examinationdetail> result;

}
