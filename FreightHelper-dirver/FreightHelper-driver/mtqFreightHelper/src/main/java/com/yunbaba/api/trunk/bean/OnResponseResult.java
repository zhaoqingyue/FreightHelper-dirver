package com.yunbaba.api.trunk.bean;

//基础回调类
public interface OnResponseResult<T> {
	
	void OnResult(T result);
	
	void OnError(int ErrCode);

	void OnGetTag(String Reqtag);
}
