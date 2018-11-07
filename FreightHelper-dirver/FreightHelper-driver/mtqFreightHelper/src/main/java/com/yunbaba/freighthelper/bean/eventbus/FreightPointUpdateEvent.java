package com.yunbaba.freighthelper.bean.eventbus;

public class FreightPointUpdateEvent {
	public int mType = 0;  //0是在线刷新，1是本地刷新
	
	public FreightPointUpdateEvent(){
		
	}
	
	public FreightPointUpdateEvent(int type){
		mType = type;
	}
}
