package com.yunbaba.freighthelper.utils;

import java.util.Timer;
import java.util.TimerTask;


public class DebounceTool {
	
	Object obj;
	Timer timer;
	public static final int TIMEOUT = 1000;
	private OnDebounceListener mlistener;
	
	public void NewInput(final Object obj){
		
		if(timer!=null){
			timer.cancel();
			timer.purge();
            timer = null;
		}
			
		init();

	
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {

				
				if(mlistener!=null)
					mlistener.AfterDebounce(obj);
				
			}
		}, TIMEOUT);
		
		
	}
	
	
	public void init(){
		if(timer == null){
			 timer = new Timer();
			 
		}
			 
	
	}
	
	public OnDebounceListener getListener() {
		return mlistener;
	}


	public void setListener(OnDebounceListener listener) {
		this.mlistener = listener;
	}

	public  interface OnDebounceListener{

		
		void AfterDebounce(Object obj);
		
	}
	

	public void clear() {

		if(timer!=null){
			timer.cancel();
			timer.purge();
            timer = null;
		}
	}

}
