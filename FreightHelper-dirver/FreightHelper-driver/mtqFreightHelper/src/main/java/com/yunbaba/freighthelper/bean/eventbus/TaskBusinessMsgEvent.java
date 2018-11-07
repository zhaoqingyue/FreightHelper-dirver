package com.yunbaba.freighthelper.bean.eventbus;

import java.util.ArrayList;

import com.yunbaba.freighthelper.bean.TaskSpInfo;

public class TaskBusinessMsgEvent {
	
	
	/**
	 * 1 新增
	 * 2 修改
	 * 3 移除
	 * 4 单个运货单撤回
	 * */
	private int type = 0;
	
	private ArrayList<String> taskIdList;
	
	private ArrayList<TaskSpInfo> refreshtaskIdList;
	
	
	/**
	 * 1 新增
	 * 2 修改
	 * 3 移除
	 * 4 单个运货单撤回
	 * */
	public TaskBusinessMsgEvent(int type,ArrayList<String> taskIdList,ArrayList<TaskSpInfo> refreshtaskIdList) {
		super();
		this.setType(type);
		this.setTaskIdList(taskIdList);
		this.setRefreshtaskIdList(refreshtaskIdList);
	}
	
	


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<String> getTaskIdList() {
		return taskIdList;
	}

	public void setTaskIdList(ArrayList<String> taskIdList) {
		this.taskIdList = taskIdList;
	}

	public ArrayList<TaskSpInfo> getRefreshtaskIdList() {
		return refreshtaskIdList;
	}

	public void setRefreshtaskIdList(ArrayList<TaskSpInfo> refreshtaskIdList) {
		this.refreshtaskIdList = refreshtaskIdList;
	}

	
	

}
