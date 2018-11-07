package com.yunbaba.api.trunk.bean;

import java.io.Serializable;

//运货单路线参数类

public class CldDelRouteParam
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private long sx;
  private long sy;
  private long tx;
  private long ty;
  private long upTime;
  private int mode;
  private long distance;
  private int station;
  private int limit;
  private int sselect;
  private int tselect;
  private String sstoreid;
  private String tstoreid;
  private int routeid;
  private int naviid;
  CldDelRouteBaseParam base;

  public CldDelRouteParam()
  {
    this.base = new CldDelRouteBaseParam();
  }

  public CldDelRouteBaseParam getBaseParam() {
    return this.base;
  }

  public long getSx()
  {
    return this.sx;
  }

  public void setSx(long sx) {
    this.sx = sx;
  }

  public long getSy() {
    return this.sy;
  }

  public void setSy(long sy) {
    this.sy = sy;
  }

  public long getTx() {
    return this.tx;
  }

  public void setTx(long tx) {
    this.tx = tx;
  }

  public long getTy() {
    return this.ty;
  }

  public void setTy(long ty) {
    this.ty = ty;
  }

  public long getUpTime() {
    return this.upTime;
  }

  public void setUpTime(long upTime) {
    this.upTime = upTime;
  }

  public int getMode() {
    return this.mode;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public long getDistance() {
    return this.distance;
  }

  public void setDistance(long distance) {
    this.distance = distance;
  }

  public int getStation() {
    return this.station;
  }

  public void setStation(int station) {
    this.station = station;
  }

  public int getLimit() {
    return this.limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getSselect() {
    return this.sselect;
  }

  public void setSselect(int sselect) {
    this.sselect = sselect;
  }

  public int getTselect() {
    return this.tselect;
  }

  public void setTselect(int tselect) {
    this.tselect = tselect;
  }
  public String getSstoreid() {
    return this.sstoreid;
  }
  public void setSstoreid(String arg) {
    this.sstoreid = arg;
  }
  public String getTstoreid() {
    return this.tstoreid;
  }
  public void setTstoreid(String arg) {
    this.tstoreid = arg;
  }

  public int getRouteid()
  {
    return this.routeid;
  }

  public void setRouteid(int routeid)
  {
    this.routeid = routeid;
  }

  public int getNaviid()
  {
    return this.naviid;
  }

  public void setNaviid(int naviid)
  {
    this.naviid = naviid;
  }
}

/* Location:           C:\Users\zhonghm\Desktop\cldtms_v1.0.jar
 * Qualified Name:     com.cld.ols.tms.datebase.CldDelRouteParam
 * JD-Core Version:    0.6.0
 */