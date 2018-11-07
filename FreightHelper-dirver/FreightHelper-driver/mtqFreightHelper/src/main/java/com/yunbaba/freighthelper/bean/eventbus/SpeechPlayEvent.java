package com.yunbaba.freighthelper.bean.eventbus;

public class SpeechPlayEvent {
	
	public SpeechPlayEvent(String speechid, String corpid) {
		super();
		this.speechid = speechid;
		this.corpid = corpid;
	}

	public String speechid;
	public String corpid;
	

}
