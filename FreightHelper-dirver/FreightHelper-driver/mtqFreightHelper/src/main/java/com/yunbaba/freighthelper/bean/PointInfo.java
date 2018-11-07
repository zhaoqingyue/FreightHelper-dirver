package com.yunbaba.freighthelper.bean;

import com.cld.mapapi.model.LatLng;

/**
 * Created by zhonghm on 2018/3/27.
 */

public class PointInfo {

    LatLng loc;
    int pos;


    public PointInfo(LatLng loc, int pos) {
        this.loc = loc;
        this.pos = pos;
    }

    public LatLng getLoc() {
        return loc;
    }

    public void setLoc(LatLng loc) {
        this.loc = loc;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
