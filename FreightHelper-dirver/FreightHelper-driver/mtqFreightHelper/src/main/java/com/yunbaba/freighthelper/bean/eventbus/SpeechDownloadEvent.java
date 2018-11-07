package com.yunbaba.freighthelper.bean.eventbus;

public class SpeechDownloadEvent {
	
	

	public SpeechDownloadEvent(String speechid, String corpid) {
		super();
		this.speechid = speechid;
		this.corpid = corpid;
	}

	public String speechid;
	public String corpid;
}
