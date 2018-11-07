package com.yunbaba.api.trunk.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

//上传收款信息参数类

public class CldSetReceiptInfo
{
  private String corpid;
  private String taskid;
  private String storeid;
  private long uptime;
  private String pay_method;
  private double real_amount;
  private double return_amount;
  private String return_desc;
  private byte[] e_receipts_0;
  private byte[] e_receipts_1;
  private byte[] e_receipts_2;
  private String pay_remark;
  private String waybill;
  private String cust_orderid;
  private String e_receipts_0_ext;
  private String[] path;

  public CldSetReceiptInfo(String[] path)
  {
    this.taskid = "";
    this.storeid = "";
    this.uptime = 0L;
    this.pay_method = "";
    this.real_amount = 0.0D;
    this.return_desc = "";
    this.pay_remark = "";
    this.path = path;
    this.waybill = "";
    this.cust_orderid = "";
    this.e_receipts_0_ext = "";

    if (path != null)
      for (int i = 0; i < path.length; i++)
        if (i == 0) {
          this.e_receipts_0 = getBitmapByte(convertToBitmap(path[i]));
        } else if (1 == i) {
          this.e_receipts_1 = getBitmapByte(convertToBitmap(path[i])); } else {
          if (2 != i) break;
          this.e_receipts_2 = getBitmapByte(convertToBitmap(path[i]));
        }
  }

  public String getTaskid()
  {
    return this.taskid;
  }

  public void setTaskid(String taskid)
  {
    this.taskid = taskid;
  }

  public String getStoreid()
  {
    return this.storeid;
  }

  public void setStoreid(String storeid)
  {
    this.storeid = storeid;
  }

  public long getUptime()
  {
    return this.uptime;
  }

  public void setUptime(long uptime)
  {
    this.uptime = uptime;
  }

  public String getPay_method()
  {
    return this.pay_method;
  }

  public String getE_receipts_0_ext() {
    return this.e_receipts_0_ext;
  }

  public void setE_receipts_0_ext(String e_receipts_0_ext) {
    this.e_receipts_0_ext = e_receipts_0_ext;
  }

  public void setPay_method(String pay_method)
  {
    this.pay_method = URLEncoder.encode(pay_method);
  }

  public byte[] getE_receipts_0() {
    return this.e_receipts_0;
  }

  public void setE_receipts_0(byte[] e_receipts_0) {
    this.e_receipts_0 = e_receipts_0;
  }

  public byte[] getE_receipts_1() {
    return this.e_receipts_1;
  }

  public void setE_receipts_1(byte[] e_receipts_1) {
    this.e_receipts_1 = e_receipts_1;
  }

  public byte[] getE_receipts_2() {
    return this.e_receipts_2;
  }

  public void setE_receipts_2(byte[] e_receipts_2) {
    this.e_receipts_2 = e_receipts_2;
  }

  public double getReal_amount() {
    return this.real_amount;
  }

  public void setReal_amount(double real_amount)
  {
    this.real_amount = real_amount;
  }

  public String getReturn_desc()
  {
    return this.return_desc;
  }

  public String getWaybill() {
    return this.waybill;
  }

  public void setWaybill(String waybill) {
    this.waybill = waybill;
  }

  public String getCorpid() {
    return this.corpid;
  }

  public void setCorpid(String corpid) {
    this.corpid = corpid;
  }

  public double getReturn_amount() {
    return this.return_amount;
  }

  public String getCust_orderid() {
    return this.cust_orderid;
  }

  public void setCust_orderid(String cust_orderid) {
    this.cust_orderid = cust_orderid;
  }

  public void setReturn_amount(double return_amount) {
    this.return_amount = return_amount;
  }

  public void setReturn_desc(String return_desc)
  {
    this.return_desc = URLEncoder.encode(return_desc);
  }

  public String getPay_remark() {
    return this.pay_remark;
  }

  public void setPay_remark(String pay_remark)
  {
    this.pay_remark = URLEncoder.encode(pay_remark);
  }

  public String[] getPath() {
    return this.path;
  }

  private byte[] getBitmapByte(Bitmap bitmap)
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    if (bitmap == null) {
      return null;
    }

    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
    try {
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return out.toByteArray();
  }

  private Bitmap convertToBitmap(String path)
  {
    int ScaleW = 800;
    int ScaleH = 480;

    if (path != null) {
      File file = new File(path);
      if (!file.exists())
        return null;
    }
    else {
      return null;
    }

    BitmapFactory.Options opts = new BitmapFactory.Options();

    opts.inJustDecodeBounds = true;
    opts.inPreferredConfig = Bitmap.Config.RGB_565;

    BitmapFactory.decodeFile(path, opts);
    int width = opts.outWidth;
    int height = opts.outHeight;
    float scaleWidth = 1.0F; float scaleHeight = 1.0F;

    if (width < height) {
      ScaleW = 480;
      ScaleH = 800;
    }
    if ((width > ScaleW) || (height > ScaleH))
    {
      scaleWidth = width / ScaleW;
      scaleHeight = height / ScaleH;
    }
    opts.inJustDecodeBounds = false;
    float scale = Math.max(scaleWidth, scaleHeight);
    opts.inSampleSize = (int)scale;
    WeakReference weak = new WeakReference(BitmapFactory.decodeFile(path, opts));

    return Bitmap.createScaledBitmap((Bitmap)weak.get(), ScaleW, ScaleH, true);
  }
}

/* Location:           C:\Users\zhonghm\Desktop\cldtms_v1.0.jar
 * Qualified Name:     com.cld.ols.tms.datebase.CldSetReceiptInfo
 * JD-Core Version:    0.6.0
 */