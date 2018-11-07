package com.yunbaba.freighthelper.bean.eventbus;


public class AccountEvent {
	
	public final int msgId;
	public final int errCode;
	
	public AccountEvent(int msgId, int errCode) {  
		this.msgId = msgId;
		this.errCode = errCode;
    } 
	
	public AccountEvent(AccountEvent event) {  
		this.msgId = event.msgId;
		this.errCode = event.errCode;
    } 
}
