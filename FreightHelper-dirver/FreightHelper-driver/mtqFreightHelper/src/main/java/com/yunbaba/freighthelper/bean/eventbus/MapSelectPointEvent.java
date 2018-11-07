package com.yunbaba.freighthelper.bean.eventbus;

import com.yunbaba.freighthelper.bean.AddressBean;

public class MapSelectPointEvent {
	
	
	private AddressBean addresspoint;

	public AddressBean getAddresspoint() {
		return addresspoint;
	}

	public void setAddresspoint(AddressBean addresspoint) {
		this.addresspoint = addresspoint;
	}
	
	

	public MapSelectPointEvent( AddressBean addresspoint) {
		// TODO Auto-generated constructor stub
		
		this.addresspoint = addresspoint;
	}
	
}
