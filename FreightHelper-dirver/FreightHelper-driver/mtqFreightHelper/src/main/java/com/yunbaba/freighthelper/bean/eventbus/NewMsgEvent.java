package com.yunbaba.freighthelper.bean.eventbus;

public class NewMsgEvent {
	
	public final int msgId;
	
	public NewMsgEvent(int msgId) {  
		this.msgId = msgId;
    } 
	
	public NewMsgEvent(NewMsgEvent event) {  
		this.msgId = event.msgId;
    } 
}
