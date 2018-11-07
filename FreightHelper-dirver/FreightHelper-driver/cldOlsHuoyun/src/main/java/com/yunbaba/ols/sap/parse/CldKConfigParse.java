/*
 * @Title CldKConfigParse.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-19 下午3:52:05
 * @version 1.0
 */
package com.yunbaba.ols.sap.parse;

import java.util.List;

import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;

/**
 * 类注释
 * 
 * @author Zhouls
 * @date 2015-3-19 下午3:52:05
 */
public class CldKConfigParse {
	public static class ProtConfig extends ProtBase {
		private List<ProtConfigItem> item;

		public List<ProtConfigItem> getItem() {
			return item;
		}

		public void setItem(List<ProtConfigItem> item) {
			this.item = item;
		}

		public static class ProtConfigItem {
			private long classtype;
			private String classname;
			private ProtItem configitem;
			private int version;

			public static class ProtItem {
				/**
				 * classtype=1001001100
				 */
				private String sms_num_cmcc;
				private String sms_num_ctcc;
				private String sms_num_cucc;
				private String phone_hdsc;
				private String reg_express;
				private String svr_bd;
				private String svr_kaccount;
				private String svr_msg;
				private String svr_onlinenavi;
				private String svr_website;
				private String svr_ppt;
				private String svr_pos;
				private String svr_authcheck;
				private String svr_kc;
				private String svr_rti;
				private String svr_tmc;
				private String svr_cmpub;
				private String svr_kweather;
				private String svr_ksearch;
				private String svr_khygroup;
				private String svr_hy_download;
				private String svr_kfeedback;

				/**
				 * classtype=1001003000
				 */
				private String url_kb2kbean;
				private String url_kbeanremark;
				private String url_kbeanhelp;
				private String url_kbrecharge;
				private String url_kbhelp;
				private String url_paymonthly;
				private String url_teambuy;
				private String url_coupon;
				private String url_hotel;
				private String url_applist;
				private String url_getactivitycode;
				private String url_emailregister;
				private String url_hud_buy;
				private String url_pioneer;
				/**
				 * classtype=1003001200
				 */
				private ProtItemOpen baidusearch;
				private ProtItemOpen baidulocate;
				private ProtItemOpen shoulei;
				private ProtItemOpen youmengtotal;
				private ProtItemOpen youmengshare;
				private ProtItemOpen onekeycall;
				private ProtItemOpen pioneer;
				private ProtItemOpen groupbuy;
				private ProtItemOpen coupon;
				private ProtItemOpen orderhotel;
				private ProtItemOpen hud;
				private ProtItemProjectOpen projectmode;
				private ProtItemOpen zxnj;
				private ProtItemOpen drivingservice;
				private ProtItemOpen carevalate;
				private ProtItemOpen autoinsurance;
				private ProtItemOpen voicecontrol;
				private ProtItemOpen violation;
				private ProtItemOpen gpslog;
				
				/**
				 * classtype=2002001000
				 */
				private int record_rate;
				private int up_rate;
				private int broadcast_rate;
				/**
				 * classtype=2004001000
				 */
				private int dest_sync_rate;
				
				
				public ProtItem() {
					sms_num_cmcc = "";
					sms_num_ctcc = "";
					sms_num_cucc = "";
					phone_hdsc = "";
					reg_express = "";
					svr_bd = "";
					svr_kaccount = "";
					svr_msg = "";
					svr_onlinenavi = "";
					svr_website = "";
					svr_ppt = "";
					svr_pos = "";
					svr_authcheck = "";
					svr_kc = "";
					svr_rti = "";
					svr_tmc = "";
					svr_cmpub = "";
					svr_kweather = "";
					svr_ksearch = "";
					svr_khygroup = "";
					svr_hy_download = "";
					svr_kfeedback = "";
					url_kb2kbean = "";
					url_kbeanremark = "";
					url_kbeanhelp = "";
					url_kbrecharge = "";
					url_kbhelp = "";
					url_paymonthly = "";
					url_teambuy = "";
					url_coupon = "";
					url_hotel = "";
					url_applist = "";
					url_getactivitycode = "";
					url_emailregister = "";
					url_hud_buy = "";
					url_pioneer = "";
					baidusearch = new ProtItemOpen();
					baidulocate = new ProtItemOpen();
					shoulei = new ProtItemOpen();
					youmengtotal = new ProtItemOpen();
					youmengshare = new ProtItemOpen();
					onekeycall = new ProtItemOpen();
					pioneer = new ProtItemOpen();
					groupbuy = new ProtItemOpen();
					coupon = new ProtItemOpen();
					orderhotel = new ProtItemOpen();
					hud = new ProtItemOpen();
					projectmode = new ProtItemProjectOpen();
					zxnj = new ProtItemOpen();
					drivingservice = new ProtItemOpen();
					carevalate = new ProtItemOpen();
					autoinsurance = new ProtItemOpen();
					voicecontrol = new ProtItemOpen();
					violation = new ProtItemOpen();
					gpslog = new ProtItemOpen();
					dest_sync_rate = 30;
					record_rate = 10;
					up_rate = 30;
					broadcast_rate = 30;
				}

				public static class ProtItemOpen {
					private int open;

					public ProtItemOpen() {
						open = 1;
					}

					public int getOpen() {
						return open;
					}

					public void setOpen(int open) {
						this.open = open;
					}
				}

				public static class ProtItemProjectOpen extends ProtItemOpen {
					private long[] kuids;

					public long[] getKuids() {
						return kuids;
					}

					public void setKuids(long[] kuids) {
						this.kuids = kuids;
					}
				}

				public String getSms_num_cmcc() {
					return sms_num_cmcc;
				}

				public void setSms_num_cmcc(String sms_num_cmcc) {
					this.sms_num_cmcc = sms_num_cmcc;
				}

				public String getSms_num_ctcc() {
					return sms_num_ctcc;
				}

				public void setSms_num_ctcc(String sms_num_ctcc) {
					this.sms_num_ctcc = sms_num_ctcc;
				}

				public String getSms_num_cucc() {
					return sms_num_cucc;
				}

				public void setSms_num_cucc(String sms_num_cucc) {
					this.sms_num_cucc = sms_num_cucc;
				}

				public String getPhone_hdsc() {
					return phone_hdsc;
				}

				public void setPhone_hdsc(String phone_hdsc) {
					this.phone_hdsc = phone_hdsc;
				}

				public String getReg_express() {
					return reg_express;
				}

				public void setReg_express(String reg_express) {
					this.reg_express = reg_express;
				}

				public String getSvr_bd() {
					return svr_bd;
				}

				public void setSvr_bd(String svr_bd) {
					this.svr_bd = svr_bd;
				}

				public String getSvr_kaccount() {
					return svr_kaccount;
				}

				public void setSvr_kaccount(String svr_kaccount) {
					this.svr_kaccount = svr_kaccount;
				}

				public String getSvr_msg() {
					return svr_msg;
				}

				public void setSvr_msg(String svr_msg) {
					this.svr_msg = svr_msg;
				}

				public String getSvr_onlinenavi() {
					return svr_onlinenavi;
				}

				public void setSvr_onlinenavi(String svr_onlinenavi) {
					this.svr_onlinenavi = svr_onlinenavi;
				}

				public String getSvr_website() {
					return svr_website;
				}

				public void setSvr_website(String svr_website) {
					this.svr_website = svr_website;
				}

				public String getSvr_ppt() {
					return svr_ppt;
				}

				public void setSvr_ppt(String svr_ppt) {
					this.svr_ppt = svr_ppt;
				}

				public String getSvr_pos() {
					return svr_pos;
				}

				public void setSvr_pos(String svr_pos) {
					this.svr_pos = svr_pos;
				}

				public String getSvr_authcheck() {
					return svr_authcheck;
				}

				public void setSvr_authcheck(String svr_authcheck) {
					this.svr_authcheck = svr_authcheck;
				}

				public String getSvr_kc() {
					return svr_kc;
				}

				public void setSvr_kc(String svr_kc) {
					this.svr_kc = svr_kc;
				}

				public String getSvr_rti() {
					return svr_rti;
				}

				public void setSvr_rti(String svr_rti) {
					this.svr_rti = svr_rti;
				}

				public String getSvr_tmc() {
					return svr_tmc;
				}

				public void setSvr_tmc(String svr_tmc) {
					this.svr_tmc = svr_tmc;
				}

				public String getSvr_cmpub() {
					return svr_cmpub;
				}

				public void setSvr_cmpub(String svr_cmpub) {
					this.svr_cmpub = svr_cmpub;
				}

				public String getSvr_kweather() {
					return svr_kweather;
				}

				public void setSvr_kweather(String svr_kweather) {
					this.svr_kweather = svr_kweather;
				}

				public String getSvr_ksearch() {
					return svr_ksearch;
				}

				public void setSvr_ksearch(String svr_ksearch) {
					this.svr_ksearch = svr_ksearch;
				}
				
				public String getSvr_khygroup() {
					return svr_khygroup;
				}

				public void setSvr_khygroup(String svr_ksearch) {
					this.svr_khygroup = svr_ksearch;
				}

				/** @return the svr_hy_download*/
				public String getSvr_hy_download() {
					return svr_hy_download;
				}
				/** @param svr_hy_download the svr_hy_download to set*/
				public void setSvr_hy_download(String svr_hy_download) {
					this.svr_hy_download = svr_hy_download;
				}
				
				public String getSvr_kfeedback() {
					return svr_kfeedback;
				}

				public void setSvr_kfeedback(String svr_kfeedback) {
					this.svr_kfeedback = svr_kfeedback;
				}

				public String getUrl_kb2kbean() {
					return url_kb2kbean;
				}

				public void setUrl_kb2kbean(String url_kb2kbean) {
					this.url_kb2kbean = url_kb2kbean;
				}

				public String getUrl_kbeanremark() {
					return url_kbeanremark;
				}

				public void setUrl_kbeanremark(String url_kbeanremark) {
					this.url_kbeanremark = url_kbeanremark;
				}

				public String getUrl_kbeanhelp() {
					return url_kbeanhelp;
				}

				public void setUrl_kbeanhelp(String url_kbeanhelp) {
					this.url_kbeanhelp = url_kbeanhelp;
				}

				public String getUrl_kbrecharge() {
					return url_kbrecharge;
				}

				public void setUrl_kbrecharge(String url_kbrecharge) {
					this.url_kbrecharge = url_kbrecharge;
				}

				public String getUrl_kbhelp() {
					return url_kbhelp;
				}

				public void setUrl_kbhelp(String url_kbhelp) {
					this.url_kbhelp = url_kbhelp;
				}

				public String getUrl_paymonthly() {
					return url_paymonthly;
				}

				public void setUrl_paymonthly(String url_paymonthly) {
					this.url_paymonthly = url_paymonthly;
				}

				public String getUrl_teambuy() {
					return url_teambuy;
				}

				public void setUrl_teambuy(String url_teambuy) {
					this.url_teambuy = url_teambuy;
				}

				public String getUrl_coupon() {
					return url_coupon;
				}

				public void setUrl_coupon(String url_coupon) {
					this.url_coupon = url_coupon;
				}

				public String getUrl_hotel() {
					return url_hotel;
				}

				public void setUrl_hotel(String url_hotel) {
					this.url_hotel = url_hotel;
				}

				public String getUrl_applist() {
					return url_applist;
				}

				public void setUrl_applist(String url_applist) {
					this.url_applist = url_applist;
				}

				public String getUrl_getactivitycode() {
					return url_getactivitycode;
				}

				public void setUrl_getactivitycode(String url_getactivitycode) {
					this.url_getactivitycode = url_getactivitycode;
				}

				public String getUrl_emailregister() {
					return url_emailregister;
				}

				public void setUrl_emailregister(String url_emailregister) {
					this.url_emailregister = url_emailregister;
				}

				public String getUrl_hud_buy() {
					return url_hud_buy;
				}

				public void setUrl_hud_buy(String url_hud_buy) {
					this.url_hud_buy = url_hud_buy;
				}

				public String getUrl_pioneer() {
					return url_pioneer;
				}

				public void setUrl_pioneer(String url_pioneer) {
					this.url_pioneer = url_pioneer;
				}

				public ProtItemOpen getBaidusearch() {
					return baidusearch;
				}

				public void setBaidusearch(ProtItemOpen baidusearch) {
					this.baidusearch = baidusearch;
				}

				public ProtItemOpen getBaidulocate() {
					return baidulocate;
				}

				public void setBaidulocate(ProtItemOpen baidulocate) {
					this.baidulocate = baidulocate;
				}

				public ProtItemOpen getGpslog(){
					return gpslog;
				}
				
				public void setGpslog(ProtItemOpen gpslog) {
					this.gpslog = gpslog;
				}
				
				public ProtItemOpen getShoulei() {
					return shoulei;
				}

				public void setShoulei(ProtItemOpen shoulei) {
					this.shoulei = shoulei;
				}

				public ProtItemOpen getYoumengtotal() {
					return youmengtotal;
				}

				public void setYoumengtotal(ProtItemOpen youmengtotal) {
					this.youmengtotal = youmengtotal;
				}

				public ProtItemOpen getYoumengshare() {
					return youmengshare;
				}

				public void setYoumengshare(ProtItemOpen youmengshare) {
					this.youmengshare = youmengshare;
				}

				public ProtItemOpen getOnekeycall() {
					return onekeycall;
				}

				public void setOnekeycall(ProtItemOpen onekeycall) {
					this.onekeycall = onekeycall;
				}

				public ProtItemOpen getPioneer() {
					return pioneer;
				}

				public void setPioneer(ProtItemOpen pioneer) {
					this.pioneer = pioneer;
				}

				public ProtItemOpen getGroupbuy() {
					return groupbuy;
				}

				public void setGroupbuy(ProtItemOpen groupbuy) {
					this.groupbuy = groupbuy;
				}

				public ProtItemOpen getCoupon() {
					return coupon;
				}

				public void setCoupon(ProtItemOpen coupon) {
					this.coupon = coupon;
				}

				public ProtItemOpen getOrderhotel() {
					return orderhotel;
				}

				public void setOrderhotel(ProtItemOpen orderhotel) {
					this.orderhotel = orderhotel;
				}

				public ProtItemOpen getHud() {
					return hud;
				}

				public void setHud(ProtItemOpen hud) {
					this.hud = hud;
				}

				public ProtItemProjectOpen getProjectmode() {
					return projectmode;
				}

				public void setProjectmode(ProtItemProjectOpen projectmode) {
					this.projectmode = projectmode;
				}

				public ProtItemOpen getZxnj() {
					return zxnj;
				}

				public void setZxnj(ProtItemOpen zxnj) {
					this.zxnj = zxnj;
				}

				public ProtItemOpen getDrivingservice() {
					return drivingservice;
				}

				public void setDrivingservice(ProtItemOpen drivingservice) {
					this.drivingservice = drivingservice;
				}

				public ProtItemOpen getCarevalate() {
					return carevalate;
				}

				public void setCarevalate(ProtItemOpen carevalate) {
					this.carevalate = carevalate;
				}

				public ProtItemOpen getAutoinsurance() {
					return autoinsurance;
				}

				public void setAutoinsurance(ProtItemOpen autoinsurance) {
					this.autoinsurance = autoinsurance;
				}

				public ProtItemOpen getVoicecontrol() {
					return voicecontrol;
				}

				public void setVoicecontrol(ProtItemOpen voicecontrol) {
					this.voicecontrol = voicecontrol;
				}

				public ProtItemOpen getViolation() {
					return violation;
				}

				public void setViolation(ProtItemOpen violation) {
					this.violation = violation;
				}

				public int getRecord_rate() {
					return record_rate;
				}

				public void setRecord_rate(int record_rate) {
					this.record_rate = record_rate;
				}

				public int getUp_rate() {
					return up_rate;
				}

				public void setUp_rate(int up_rate) {
					this.up_rate = up_rate;
				}
				
				public void setBroadcast_rate(int broadcast_rate) {
					this.broadcast_rate = broadcast_rate;
				}
				
				public int getBroadcast_rate() {
					return broadcast_rate;
				}
				
				public int getDest_sync_rate() {
					return dest_sync_rate;
				}

				public void setDest_sync_rate(int dest_sync_rate) {
					this.dest_sync_rate = dest_sync_rate;
				}
			}

			public String getClassname() {
				return classname;
			}

			public void setClassname(String classname) {
				this.classname = classname;
			}

			public ProtItem getConfigitem() {
				return configitem;
			}

			public void setConfigitem(ProtItem configitem) {
				this.configitem = configitem;
			}

			public int getVersion() {
				return version;
			}

			public void setVersion(int version) {
				this.version = version;
			}

			public long getClasstype() {
				return classtype;
			}

			public void setClasstype(long classtype) {
				this.classtype = classtype;
			}
		}
	}
}
