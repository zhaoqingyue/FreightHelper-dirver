package com.yunbaba.freighthelper.bean;

public class TaskSpInfo {
	

	public TaskSpInfo(String taskid, String corpid,String carlicense) {
		super();
		this.taskid = taskid;
		this.corpid = corpid;
		this.carlicense = carlicense;
	}
	
	public TaskSpInfo(String taskid, String corpid,String carlicense,String cu_orderid) {
		super();
		this.taskid = taskid;
		this.corpid = corpid;
		this.carlicense = carlicense;
		this.cu_orderid = cu_orderid;
	}

	public String taskid;
	
	public String corpid;
	
	public String carlicense;
	
	public String cu_orderid;
	

}
