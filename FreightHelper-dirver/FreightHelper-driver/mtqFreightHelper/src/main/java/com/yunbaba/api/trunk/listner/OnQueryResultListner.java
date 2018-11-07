package com.yunbaba.api.trunk.listner;

import java.util.List;

/**
 * Created by zhonghm on 2018/3/30.
 */
public interface OnQueryResultListner<T> {


    public void onResult(List<T> res);
}
