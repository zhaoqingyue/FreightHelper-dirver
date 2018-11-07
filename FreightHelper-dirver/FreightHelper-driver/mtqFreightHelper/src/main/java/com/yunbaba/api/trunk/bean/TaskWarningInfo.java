package com.yunbaba.api.trunk.bean;

public class TaskWarningInfo {

	// public Date time;
	public String WarningContent;

	// 用来保持消息在sp里key的唯一性 一般用cu_orderid 过期的用cu_orderid+时间
	public String declareid;
	public int type;
	public String taskId;
	public String cu_orderid;
	public long warningTime;

	public String getWarningContent() {
		return WarningContent;
	}

	public void setWarningContent(String warningContent) {
		WarningContent = warningContent;
	}

	public String getDeclareid() {
		return declareid;
	}

	public void setDeclareid(String declareid) {
		this.declareid = declareid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getCu_orderid() {
		return cu_orderid;
	}

	public void setCu_orderid(String cu_orderid) {
		this.cu_orderid = cu_orderid;
	}

	public long getWarningTime() {
		return warningTime;
	}

	public void setWarningTime(long warningTime) {
		this.warningTime = warningTime;
	}

}
