package com.yunbaba.freighthelper.bean.eventbus;

import com.yunbaba.freighthelper.bean.CorpBean;

public class CompanyChangeEvent {
	
	private CorpBean currentCorp;
	public boolean isDeleteCropHappen = false;
	public String deletecropid;

	public CompanyChangeEvent(CorpBean currentCorp) {
		// TODO Auto-generated constructor stub
		
		this.currentCorp = currentCorp; 
	}
	
	public CompanyChangeEvent(CorpBean currentCorp,boolean isDeleteCropHappen, String cropid ) {
		// TODO Auto-generated constructor stub
		
		this.currentCorp = currentCorp; 
		this.isDeleteCropHappen = isDeleteCropHappen;
		deletecropid = cropid;
	}
	
	
	public CorpBean getCurrentCorp() {
		return currentCorp;
	}

	public void setCurrentCorp(CorpBean currentCorp) {
		this.currentCorp = currentCorp;
	}
	
}
