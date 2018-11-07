package com.yunbaba.api.trunk.bean;

public interface OnResponseResultContainMsg<T> {

	void OnResult(T result);

	void OnError(int ErrCode, String ErrMsg);

	void OnGetTag(String Reqtag);

}
