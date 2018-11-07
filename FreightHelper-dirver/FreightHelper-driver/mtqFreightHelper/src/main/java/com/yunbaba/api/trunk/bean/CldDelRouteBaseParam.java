package com.yunbaba.api.trunk.bean;


import java.io.Serializable;

//运货单路线参数基础类
public class CldDelRouteBaseParam
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String corpid;
  private int dir;
  private String ds;
  private String de;
  private String p;
  private String vd;
  private String fd;
  private String ad;
  private int frd;
  private int tht;
  private int twh;
  private int twt;
  private int tad;
  private int limitspeed;
  private int yawrange;

  public String getCorpid()
  {
    return this.corpid;
  }

  public void setCorpid(String corpid) {
    this.corpid = corpid;
  }

  public int getDir() {
    return this.dir;
  }

  public void setDir(int dir) {
    this.dir = dir;
  }

  public String getP() {
    return this.p;
  }

  public void setP(String p) {
    this.p = p;
  }

  public String getVd() {
    return this.vd;
  }

  public void setVd(String vd) {
    this.vd = vd;
  }

  public String getFd() {
    return this.fd;
  }

  public void setFd(String fd) {
    this.fd = fd;
  }

  public String getAd() {
    return this.ad;
  }

  public void setAd(String ad) {
    this.ad = ad;
  }

  public int getFrd() {
    return this.frd;
  }

  public void setFrd(int frd) {
    this.frd = frd;
  }

  public int getTht() {
    return this.tht;
  }

  public void setTht(int tht) {
    this.tht = tht;
  }

  public int getTwh() {
    return this.twh;
  }

  public void setTwh(int twh) {
    this.twh = twh;
  }

  public int getTwt() {
    return this.twt;
  }

  public void setTwt(int twt) {
    this.twt = twt;
  }

  public int getTad() {
    return this.tad;
  }

  public void setTad(int tad) {
    this.tad = tad;
  }

  public String getDs() {
    return this.ds;
  }
  public void setDs(String arg) {
    this.ds = arg;
  }

  public String getDe() {
    return this.de;
  }
  public void setDe(String arg) {
    this.de = arg;
  }

  public int getLimitspeed() {
    return this.limitspeed;
  }

  public void setLimitspeed(int limitspeed) {
    this.limitspeed = limitspeed;
  }

  public int getYawrange() {
    return this.yawrange;
  }

  public void setYawrange(int yawrange) {
    this.yawrange = yawrange;
  }
}

/* Location:           C:\Users\zhonghm\Desktop\cldtms_v1.0.jar
 * Qualified Name:     com.cld.ols.tms.datebase.CldDelRouteBaseParam
 * JD-Core Version:    0.6.0
 */