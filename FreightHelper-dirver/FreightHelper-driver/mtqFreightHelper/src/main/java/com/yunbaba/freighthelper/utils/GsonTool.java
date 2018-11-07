package com.yunbaba.freighthelper.utils;

import com.cld.gson.Gson;

public class GsonTool {
	
	private static  final Gson gson =  new Gson();
	
	public static  Gson getInstance(){
		
		return gson;
	}

}
