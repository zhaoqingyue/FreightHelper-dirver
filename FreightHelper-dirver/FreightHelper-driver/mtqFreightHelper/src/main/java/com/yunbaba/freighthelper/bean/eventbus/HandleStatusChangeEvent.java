package com.yunbaba.freighthelper.bean.eventbus;

public class HandleStatusChangeEvent {

	public UpdateTaskStatusEvent mevent;

	/**
	 * 【0待运货1运货中2已完成3暂停状态4中止状态 】
	 */
	public HandleStatusChangeEvent(UpdateTaskStatusEvent mevent) {
		// TODO Auto-generated constructor stub

		this.mevent = mevent;
		// this.setPosition(position);

	}

}
