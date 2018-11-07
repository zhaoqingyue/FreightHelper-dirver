package com.yunbaba.freighthelper.bean.eventbus;

import android.os.Message;

public class FreightPointEvent {
	public Message msg;
	public String ctxSimpleName;  //activity 的simpleName
	public FreightPointEvent(int inMsgId){
		msg = new Message();
		msg.what = inMsgId;
	}
	
	public FreightPointEvent(int inMsgId,String ctxName){
		msg = new Message();
		msg.what = inMsgId;
		ctxSimpleName = ctxName;
	}
	
	public FreightPointEvent(int inMsgId,int param){
		msg = new Message();
		msg.what = inMsgId;
		msg.arg1 = param;
	}
	
	public FreightPointEvent(int inMsgId,int param,String ctxName){
		msg = new Message();
		msg.what = inMsgId;
		msg.arg1 = param;
		ctxSimpleName = ctxName;
	}
	
	public FreightPointEvent(int inMsgId,int param,Object paramObj){
		msg = new Message();
		msg.what = inMsgId;
		msg.arg1 = param;
		msg.obj = paramObj;
	}
	
	public FreightPointEvent(int inMsgId,int param,Object paramObj,String ctxName){
		msg = new Message();
		msg.what = inMsgId;
		msg.arg1 = param;
		msg.obj = paramObj;
		ctxSimpleName = ctxName;
	}
	
	public FreightPointEvent(Message inMsg){
		msg = inMsg;
	}
	
	public FreightPointEvent(Message inMsg,String ctxName){
		msg = inMsg;
		ctxSimpleName = ctxName;
	}
}
