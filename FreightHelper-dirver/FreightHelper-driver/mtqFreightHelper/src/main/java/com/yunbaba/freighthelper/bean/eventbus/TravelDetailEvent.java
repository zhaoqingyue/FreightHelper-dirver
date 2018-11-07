package com.yunbaba.freighthelper.bean.eventbus;

public class TravelDetailEvent {
	
	public final int msgId;
	public final int errCode;
	
	public TravelDetailEvent(int msgId, int errCode) {  
		this.msgId = msgId;
		this.errCode = errCode;
    } 
	
	public TravelDetailEvent(TravelDetailEvent event) {  
		this.msgId = event.msgId;
		this.errCode = event.errCode;
    } 

}
