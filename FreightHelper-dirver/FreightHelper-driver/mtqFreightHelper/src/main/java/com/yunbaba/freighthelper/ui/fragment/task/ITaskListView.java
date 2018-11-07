package com.yunbaba.freighthelper.ui.fragment.task;

import java.util.List;

import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;

public interface ITaskListView {
	
	void ShowTransportingItem(MtqDeliTask transportingtask);
	
	void ShowTaskListItem(List<MtqDeliTask> taskList);
	
	void SetHeadElemVisible(boolean Head,boolean center);
	
	//void StopTask(CldDelUpTask upTask);
	
	

}
