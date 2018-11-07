package com.yunbaba.api.trunk.bean;

import java.io.Serializable;


//更新任务单/运单任务类
public class CldDelUpTask
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private int type;
  private String corpId;
  private String taskId;
  private String nodeId;
  private String routeid;
  private CldDelRouteParam routeParam;
  private String msgCtx;
  private String[] picPath;
  private String upMark;
  private String PayMethod;
  private double realAmount;
  private String returnReason;
  private String waybill;
  private double returnMount;
  private int naviid;
  private int uploadState;
  private long time;
  private long x;
  private long y;
  private long cell;
  private long uid;
  private int status;

  public String getKey()
  {
    String key = null;
    switch (this.type) {
    case 0:
      key = this.type + "|" + this.waybill + "|" + this.status;
      break;
    case 1:
      key = this.type + "|" + this.taskId + "|" + this.status;
      break;
    case 2:
      key = this.type + "|" + this.routeid + "|" + this.status;
      break;
    case 3:
      key = this.type + "|" + this.time;
      break;
    case 4:
      key = this.type + "|" + this.msgCtx;
      break;
    case 5:
      key = this.type + "|" + this.waybill;
    }

    return key;
  }

  public CldDelUpTask(int type, String taskId, long time, long x, long y, long cell, long uid, int status)
  {
    this.type = type;
    if (type == 1)
      this.taskId = taskId;
    else if (type == 2) {
      this.routeid = taskId;
    }
    this.time = time;
    this.x = x;
    this.y = y;
    this.cell = cell;
    this.uid = uid;
    this.status = status;
  }

  public CldDelUpTask(int type, String taskId, long time, long x, long y, long cell, long uid, int status, int naviid)
  {
    this.type = type;
    if (type == 1)
      this.taskId = taskId;
    else if (type == 2) {
      this.routeid = taskId;
    }
    this.time = time;
    this.x = x;
    this.y = y;
    this.cell = cell;
    this.uid = uid;
    this.status = status;
    this.naviid = naviid;
  }

  public CldDelUpTask(int type, String taskId, String nodeId, long time, long x, long y, long cell, long uid, int status, String waybill)
  {
    this.type = type;
    this.taskId = taskId;
    this.nodeId = nodeId;
    this.time = time;
    this.x = x;
    this.y = y;
    this.cell = cell;
    this.uid = uid;
    this.status = status;
    this.waybill = waybill;
  }

  public CldDelUpTask(int type, CldDelRouteParam routeParam, long time)
  {
    this.type = type;
    this.routeParam = routeParam;
    this.time = time;
  }

  public CldDelUpTask(int type, String msgCtx, long time)
  {
    this.type = type;
    this.msgCtx = msgCtx;
    this.time = time;
  }

  public CldDelUpTask(int type, String taskId, String nodeId, String payWay, double real, String backReason, String mark, String[] path, long time, String waybill)
  {
    this.type = type;
    this.taskId = taskId;
    this.nodeId = nodeId;
    this.PayMethod = payWay;
    this.realAmount = real;
    this.returnReason = backReason;
    this.upMark = mark;
    this.picPath = path;
    this.time = time;
    this.waybill = waybill;
  }

  public boolean equals(Object o)
  {
    if ((o instanceof CldDelUpTask)) {
      CldDelUpTask task = (CldDelUpTask)o;
      if ((task.type == this.type) && (
        ((this.type == 1) && (task.taskId.equals(this.taskId))) || 
        (this.type == 3) || 
        ((this.type == 0) && (task.taskId.equals(this.taskId)) && (task.nodeId.equals(this.nodeId))) || 
        ((this.type == 2) && (task.routeid.equals(this.routeid))) || 
        ((this.type == 4) && (task.msgCtx != null) && (task.msgCtx.equals(this.msgCtx))) || (
        (this.type == 5) && (task.nodeId.equals(this.nodeId))))) {
        return true;
      }
    }
    return false;
  }

  public double getReturnMount() {
    return this.returnMount;
  }

  public void setReturnMount(double returnMount) {
    this.returnMount = returnMount;
  }

  public String getRouteid()
  {
    return this.routeid;
  }

  public void setRouteid(String routeid)
  {
    this.routeid = routeid;
  }

  public int getStatus()
  {
    return this.status;
  }

  public void setStatus(int status)
  {
    this.status = status;
  }

  public int getUploadState()
  {
    return this.uploadState;
  }

  public void setUploadState(int uploadState)
  {
    this.uploadState = uploadState;
  }

  public int getType()
  {
    return this.type;
  }

  public void setType(int type)
  {
    this.type = type;
  }

  public String getCorpId() {
    return this.corpId;
  }

  public void setCorpId(String corpId) {
    this.corpId = corpId;
  }

  public String getTaskId()
  {
    return this.taskId;
  }

  public void setTaskId(String taskId)
  {
    this.taskId = taskId;
  }

  public String getNodeId()
  {
    return this.nodeId;
  }

  public void setNodeId(String nodeId)
  {
    this.nodeId = nodeId;
  }

  public long getTime()
  {
    return this.time;
  }

  public void setTime(long time)
  {
    this.time = time;
  }

  public long getX()
  {
    return this.x;
  }

  public void setX(long x)
  {
    this.x = x;
  }

  public long getY()
  {
    return this.y;
  }

  public void setY(long y)
  {
    this.y = y;
  }

  public String getMsgCtx()
  {
    return this.msgCtx;
  }

  public void setMsgCtx(String msgCtx)
  {
    this.msgCtx = msgCtx;
  }

  public long getCell()
  {
    return this.cell;
  }

  public void setCell(long cell)
  {
    this.cell = cell;
  }

  public long getUid()
  {
    return this.uid;
  }

  public void setUid(long uid)
  {
    this.uid = uid;
  }

  public CldDelRouteParam getRouteParam()
  {
    return this.routeParam;
  }

  public void setRouteParam(CldDelRouteParam routeParam)
  {
    this.routeParam = routeParam;
  }

  public String[] getPicPath()
  {
    return this.picPath;
  }

  public void setPicPath(String[] picPath)
  {
    this.picPath = picPath;
  }

  public String getUpMark()
  {
    return this.upMark;
  }

  public void setUpMark(String upMark)
  {
    this.upMark = upMark;
  }

  public String getPayMethod()
  {
    return this.PayMethod;
  }

  public void setPayMethod(String payMethod)
  {
    this.PayMethod = payMethod;
  }

  public double getRealAmount()
  {
    return this.realAmount;
  }

  public void setRealAmount(double realAmount)
  {
    this.realAmount = realAmount;
  }

  public String getReturnReason()
  {
    return this.returnReason;
  }

  public void setReturnReason(String returnReason)
  {
    this.returnReason = returnReason;
  }

  public String getWaybill() {
    return this.waybill;
  }

  public void setWaybill(String waybill) {
    this.waybill = waybill;
  }

  public int getNaviid()
  {
    return this.naviid;
  }

  public void setNaviid(int naviid)
  {
    this.naviid = naviid;
  }

  public static class DelUpTaskType
  {
    public static final int tNode = 0;
    public static final int tTask = 1;
    public static final int tUseRoute = 2;
    public static final int tRoute = 3;
    public static final int tUpMsg = 4;
    public static final int tUpMoney = 5;
  }
  
  public static class DelTaskStatus
  {
   public static final int tWait = 0;
    public static final int tDelivering = 1;
    public static final int tFinish = 2;
   public static final int tPause = 3;
   public static final int tShutDown = 4;
  }

}

/* Location:           C:\Users\zhonghm\Desktop\cldtms_v1.0.jar
 * Qualified Name:     com.cld.ols.tms.datebase.CldDelUpTask
 * JD-Core Version:    0.6.0
 */