package com.yunbaba.api.trunk.bean;

public class UpdateTaskStatusResult {
	
	
	private int errCode;
	private String corpid; 
	private String taskid; 
	private String ecorpid; 
	private String etaskid; 
	
	/**
	 * 【0待运货1运货中2已完成3暂停状态4中止状态 】
	 * */
	private int status;
	
	
	
	public UpdateTaskStatusResult(int errCode,String corpid,String taskid,int status) {
		// TODO Auto-generated constructor stub
		this.setErrCode(errCode);
		this.setCorpid(corpid);
		this.setTaskid(taskid);
		this.setStatus(status);
	}
	
	
	public UpdateTaskStatusResult(int errCode,String corpid,String taskid,String ecorpid,String etaskid,int status) {
		// TODO Auto-generated constructor stub
		this.setErrCode(errCode);
		this.setCorpid(corpid);
		this.setTaskid(taskid);
		this.setStatus(status);
		this.ecorpid = ecorpid;
		this.etaskid = etaskid;
	}
	

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getCorpid() {
		return corpid;
	}

	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	/**
	 * 【0待运货1运货中2已完成3暂停状态4中止状态 】
	 * */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
