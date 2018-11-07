package com.yunbaba.freighthelper.bean.eventbus;

import java.util.ArrayList;

public class TaskStateUpdateEvent {
	
	

	ArrayList<String> taskIdList;
	
	public TaskStateUpdateEvent(ArrayList<String> taskIdList) {
		super();
		this.taskIdList = taskIdList;
	}
	

}
