package com.yunbaba.freighthelper.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TimeTaskUtils {
	
	public String  LastestString = null;
	Timer timer;
	public static final int TIMEOUT = 1000;
	private OnTimerListener mlistener;
	
	public void NewInput(final String str){
		
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
					mlistener.AfterDebounce(str);
				
			}
		}, TIMEOUT);
		
		
	}
	
	
	public void init(){
		if(timer == null){
			 timer = new Timer();
			 
		}
			 
	
	}
	
	public OnTimerListener getListener() {
		return mlistener;
	}


	public void setListener(OnTimerListener listener) {
		this.mlistener = listener;
	}

	public  interface OnTimerListener{

		
		void AfterDebounce(String str);
		
	}
	

	public void clear() {

		if(timer!=null){
			timer.cancel();
			timer.purge();
            timer = null;
		}
	}

}
