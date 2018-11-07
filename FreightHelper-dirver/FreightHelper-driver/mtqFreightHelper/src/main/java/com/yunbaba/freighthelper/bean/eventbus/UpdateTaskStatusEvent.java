package com.yunbaba.freighthelper.bean.eventbus;

public class UpdateTaskStatusEvent {
	
	private String corpid;
	private String taskid;
	private  int status; //【0待运货1运货中2已完成3暂停状态4中止状态 】
	private  String ecorpid;  
	private  String etaskid;
	private  long x;  
	private  long y;
	private  int cell;
	private  int uid;
	 
	 /**
	  * 【0待运货1运货中2已完成3暂停状态4中止状态 】
	  * */
	 public UpdateTaskStatusEvent(String corpid, final String taskid,
				final int status, final String ecorpid, final String etaskid,
				final long x, final long y, final int cell, final int uid) {
		// TODO Auto-generated constructor stub
		 
		 
		 this.setCorpid(corpid);
		 this.setTaskid(taskid);
		 this.setStatus(status);
		 this.setEcorpid(ecorpid);
		 this.setEtaskid(etaskid);
		 this.setX(x);
		 this.setY(y);
		 this.setCell(cell);
		 this.setUid(uid);
		// this.setPosition(position);
				 
		 
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


	public String getEcorpid() {
		return ecorpid;
	}


	public void setEcorpid(String ecorpid) {
		this.ecorpid = ecorpid;
	}


	public String getEtaskid() {
		return etaskid;
	}


	public void setEtaskid(String etaskid) {
		this.etaskid = etaskid;
	}


	public long getX() {
		return x;
	}


	public void setX(long x) {
		this.x = x;
	}


	public long getY() {
		return y;
	}


	public void setY(long y) {
		this.y = y;
	}


	public int getCell() {
		return cell;
	}


	public void setCell(int cell) {
		this.cell = cell;
	}


	public int getUid() {
		return uid;
	}


	public void setUid(int uid) {
		this.uid = uid;
	}

//
//	public int getPosition() {
//		return position;
//	}
//
//
//	public void setPosition(int position) {
//		this.position = position;
//	}

}
