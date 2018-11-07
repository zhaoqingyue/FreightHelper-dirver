package com.yunbaba.api.trunk.bean;

import com.yunbaba.ols.module.delivery.bean.MtqDeliReceiveData;

public class UpdateTaskPointStatusResult {

	public UpdateTaskPointStatusResult(int errCode, String corpid, String taskid, String storeid, int status,
			String waybill, String ewaybill, MtqDeliReceiveData data) {
		super();
		this.errCode = errCode;
		this.corpid = corpid;
		this.taskid = taskid;
		this.storeid = storeid;
		this.status = status;
		this.setWaybill(waybill);
		this.setEwaybill(ewaybill);
		this.data = data;

	}

	private int errCode;
	private String corpid;
	private String taskid;
	private String storeid;
	private String ewaybill;
	private String waybill;
	private int status;
	private MtqDeliReceiveData data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStoreid() {
		return storeid;
	}

	public void setStoreid(String storeid) {
		this.storeid = storeid;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getCorpid() {
		return corpid;
	}

	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public MtqDeliReceiveData getData() {
		return data;
	}

	public void setData(MtqDeliReceiveData data) {
		this.data = data;
	}

	public String getWaybill() {
		return waybill;
	}

	public void setWaybill(String waybill) {
		this.waybill = waybill;
	}

	public String getEwaybill() {
		return ewaybill;
	}

	public void setEwaybill(String ewaybill) {
		this.ewaybill = ewaybill;
	}

}
