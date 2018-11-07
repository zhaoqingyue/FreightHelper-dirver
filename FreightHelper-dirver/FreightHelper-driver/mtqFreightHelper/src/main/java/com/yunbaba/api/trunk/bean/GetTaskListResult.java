package com.yunbaba.api.trunk.bean;

import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;

import java.util.List;

//获取任务列表结果实体类

public class GetTaskListResult {
	
	private int errCode;
	private List<MtqDeliTask> lstOfTask;
	private int page;
	private int pagecount;
	private int total;
	public int getErrCode() {
		return errCode;
	}
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	public List<MtqDeliTask> getLstOfTask() {
		return lstOfTask;
	}
	public void setLstOfTask(List<MtqDeliTask> lstOfTask) {
		this.lstOfTask = lstOfTask;
	}


//
//	public List<MtqHistroyTask> geHistLstOfTask() {
//		return hislstOfTask;
//	}
//	public void setHisLstOfTask(List<MtqHistroyTask> lstOfTask) {
//		this.hislstOfTask = lstOfTask;
//	}

	public int getPagecount() {
		return pagecount;
	}
	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	
	
	
	

}
