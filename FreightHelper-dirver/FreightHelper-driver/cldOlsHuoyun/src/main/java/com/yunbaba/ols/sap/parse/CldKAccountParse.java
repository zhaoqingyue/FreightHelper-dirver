/*
 * @Title CldKAccountParse.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-21 下午2:41:09
 * @version 1.0
 */
package com.yunbaba.ols.sap.parse;

import android.annotation.SuppressLint;

import com.cld.gson.JsonArray;
import com.cld.gson.JsonObject;
import com.yunbaba.ols.module.delivery.tool.CldKDeviceAPI;
import com.yunbaba.ols.sap.bean.CldSapKAParm.CldLicenceInfo;
import com.yunbaba.ols.sap.bean.CldSapKAParm.CldUserInfo;
import com.yunbaba.ols.sap.bean.CldSapKAppParm.MtqAppInfo;
import com.yunbaba.ols.sap.bean.CldSapKAppParm.MtqAppInfoNew;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;
import com.yunbaba.ols.tools.CldSapParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 账户系统解析类
 * 
 * @author Zhouls
 * @date 2015-3-21 下午2:41:09
 */
public class CldKAccountParse {

	/**
	 * 设备注册
	 * 
	 * @author Zhouls
	 * @date 2015-3-25 下午4:04:11
	 */
	public static class ProtDeviceInfo extends ProtBase {
		/** 设备名称 */
		private String deviceksn;
		/** 设备id */
		private long duid;

		public ProtDeviceInfo() {
			deviceksn = "";
			duid = 0;
		}

		public String getDeviceksn() {
			return deviceksn;
		}

		public void setDeviceksn(String deviceksn) {
			this.deviceksn = deviceksn;
		}

		public long getDuid() {
			return duid;
		}

		public void setDuid(long duid) {
			this.duid = duid;
		}
	}

	/**
	 * 
	 * Kuid 解析
	 * 
	 * @author Zhouls
	 * @date 2015-3-25 下午4:21:07
	 */
	public static class ProtUserKuid extends ProtBase {
		/** 用户的Kuid */
		private long kuid;

		public ProtUserKuid() {
			kuid = 0;
		}

		public long getKuid() {
			return kuid;
		}

		public void setKuid(long kuid) {
			this.kuid = kuid;
		}
	}

	/**
	 * 
	 * 注册返回
	 * 
	 * @author Zhouls
	 * @date 2015-3-25 下午4:22:24
	 */
	public static class ProtUserRegister extends ProtBase {
		/** 用户的Kuid */
		private long kuid;
		/** 登录名 */
		private String loginname;

		public ProtUserRegister() {
			kuid = 0;
			loginname = "";
		}

		public long getKuid() {
			return kuid;
		}

		public void setKuid(long kuid) {
			this.kuid = kuid;
		}

		public String getLoginname() {
			return loginname;
		}

		public void setLoginname(String loginname) {
			this.loginname = loginname;
		}
	}

	/**
	 * 
	 * 登录返回
	 * 
	 * @author Zhouls
	 * @date 2015-3-25 下午4:23:42
	 */
	public static class ProtLogin extends ProtBase {
		/** 登录返回kuid */
		private long kuid;
		/** 登录返回session */
		private String session;
		/** 登录名 */
		private String loginname;
		/** 用户名 */
		private String username;
		/** 化名 */
		private String useralias;
		/** 邮箱 */
		private String email;
		/** 是否绑定邮箱 */
		private int emailbind;
		/** 手机号 */
		private String mobile;
		/** 是否绑定手机号 */
		private int mobilebind;
		/** 性别 */
		private int sex;
		/** 是否VIP(0否 1是) */
		private int vip;
		/** 用户等级 */
		private int userlevel;
		/** 注册时间 */
		private long regtime;
		/** 最后一次登录时间 */
		private long lastlogintime;
		/** 1,新用户注册，2老用户登录 */
		private int status;

		public void protParse(CldUserInfo userInfo) {
			userInfo.setLoginName(loginname);
			userInfo.setUserName(username);
			userInfo.setUserAlias(useralias);
			userInfo.setEmail(email);
			userInfo.setEmailBind(emailbind);
			userInfo.setMobile(mobile);
			userInfo.setMobileBind(mobilebind);
			userInfo.setSex(sex);
			userInfo.setVip(vip);
			userInfo.setUserLevel(userlevel);
			userInfo.setRegTime(regtime);
			userInfo.setLastLoginTime(lastlogintime);
			userInfo.setStatus(status);
		}

		public long getKuid() {
			return kuid;
		}

		public void setKuid(long kuid) {
			this.kuid = kuid;
		}

		public String getSession() {
			return session;
		}

		public void setSession(String session) {
			this.session = session;
		}

		public String getLoginname() {
			return loginname;
		}

		public void setLoginname(String loginname) {
			this.loginname = loginname;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getUseralias() {
			return useralias;
		}

		public void setUseralias(String useralias) {
			this.useralias = useralias;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public int getEmailbind() {
			return emailbind;
		}

		public void setEmailbind(int emailbind) {
			this.emailbind = emailbind;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public int getMobilebind() {
			return mobilebind;
		}

		public void setMobilebind(int mobilebind) {
			this.mobilebind = mobilebind;
		}

		public int getSex() {
			return sex;
		}

		public void setSex(int sex) {
			this.sex = sex;
		}

		/** @return the vip */
		public int getVip() {
			return vip;
		}

		/**
		 * @param vip
		 *            the vip to set
		 */
		public void setVip(int vip) {
			this.vip = vip;
		}

		/** @return the userlevel */
		public int getUserlevel() {
			return userlevel;
		}

		/**
		 * @param userlevel
		 *            the userlevel to set
		 */
		public void setUserlevel(int userlevel) {
			this.userlevel = userlevel;
		}

		/** @return the regtime */
		public long getRegtime() {
			return regtime;
		}

		/**
		 * @param regtime
		 *            the regtime to set
		 */
		public void setRegtime(long regtime) {
			this.regtime = regtime;
		}

		/** @return the lastlogintime */
		public long getLastlogintime() {
			return lastlogintime;
		}

		/**
		 * @param lastlogintime
		 *            the lastlogintime to set
		 */
		public void setLastlogintime(long lastlogintime) {
			this.lastlogintime = lastlogintime;
		}

		/** @return the status */
		public int getStatus() {
			return status;
		}

		/**
		 * @param status
		 *            the status to set
		 */
		public void setStatus(int status) {
			this.status = status;
		}
	}

	/**
	 * 
	 * 获取用户信息
	 * 
	 * @author Zhouls
	 * @date 2015-3-25 下午4:27:25
	 */
	public static class ProtUserInfo extends ProtBase {
		private ProtData data;

		public ProtLicenceInfo licence_info;

		@SuppressLint("SimpleDateFormat")
		public void protParse(CldUserInfo userInfo) {
			if (null != data) {
				userInfo.setLoginName(data.getLoginname());
				userInfo.setUserName(data.getUsername());
				userInfo.setUserAlias(data.getUseralias());
				userInfo.setEmail(data.getEmail());
				userInfo.setEmailBind(data.getEmailbind());
				userInfo.setMobile(data.getMobile());
				userInfo.setMobileBind(data.getMobilebind());
				userInfo.setSex(data.getSex());
				userInfo.setQq(data.getQq());
				userInfo.setCardType(data.getCardtype());
				userInfo.setCardCode(data.getCardcode());
				userInfo.setUserLevel(data.getUserlevel());
				userInfo.setVip(data.getVip());
				userInfo.setStatus(data.getStatus());
				try {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date;
					date = format.parse(data.getRegtime());
					userInfo.setRegTime(date.getTime() / 1000);
					date = format.parse(data.getLastlogintime());
					userInfo.setLastLoginTime(date.getTime() / 1000);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				userInfo.setRegIp(data.getRegip());
				userInfo.setRegAppid(data.getRegappid());
				userInfo.setUpdateTime(data.getUpdatetime());
				userInfo.setLastLoginIp(data.getLastloginip());
				userInfo.setLastLoginAppid(data.getLastloginappid());
				userInfo.setPhotoPath(data.photo);
				userInfo.setAddress(data.address);
				userInfo.setDriving_licence(data.driving_licence);
				CldLicenceInfo iInfo = new CldLicenceInfo();
				if (null != licence_info) {
					iInfo.licenceName = licence_info.dl_name;
					iInfo.licenceNum = licence_info.dl_num;
					iInfo.vehicleNum = licence_info.vehicle_plate_num;
					iInfo.vehicleType = licence_info.vehicle_type;
					iInfo.status = licence_info.status;
					iInfo.reason = licence_info.reason;
				}
				userInfo.setLicenceInfo(iInfo);
			}
		}

		public static class ProtData {
			private String loginname;
			private String username;
			private String useralias;
			private String email;
			private int emailbind;
			private String mobile;
			private int mobilebind;
			private int sex;
			private String qq;
			private int cardtype;
			private String cardcode;
			private int userlevel;
			private int vip;
			private int status;
			private String regip;
			private String regtime;
			private int regappid;
			private String updatetime;
			private String lastlogintime;
			private String lastloginip;
			private int lastloginappid;
			public String photo;
			public String address;
			public String driving_licence;

			public String getPhoto() {
				return photo;
			}

			public void setPhoto(String photo) {
				this.photo = photo;
			}

			public String getAddress() {
				return address;
			}

			public void setAddress(String address) {
				this.address = address;
			}

			public String getDriving_licence() {
				return driving_licence;
			}

			public void setDriving_licence(String driving_licence) {
				this.driving_licence = driving_licence;
			}

			public String getLoginname() {
				return loginname;
			}

			public void setLoginname(String loginname) {
				this.loginname = loginname;
			}

			public String getUsername() {
				return username;
			}

			public void setUsername(String username) {
				this.username = username;
			}

			public String getUseralias() {
				return useralias;
			}

			public void setUseralias(String useralias) {
				this.useralias = useralias;
			}

			public String getEmail() {
				return email;
			}

			public void setEmail(String email) {
				this.email = email;
			}

			public int getEmailbind() {
				return emailbind;
			}

			public void setEmailbind(int emailbind) {
				this.emailbind = emailbind;
			}

			public String getMobile() {
				return mobile;
			}

			public void setMobile(String mobile) {
				this.mobile = mobile;
			}

			public int getMobilebind() {
				return mobilebind;
			}

			public void setMobilebind(int mobilebind) {
				this.mobilebind = mobilebind;
			}

			public int getSex() {
				return sex;
			}

			public void setSex(int sex) {
				this.sex = sex;
			}

			public String getQq() {
				return qq;
			}

			public void setQq(String qq) {
				this.qq = qq;
			}

			public int getCardtype() {
				return cardtype;
			}

			public void setCardtype(int cardtype) {
				this.cardtype = cardtype;
			}

			public String getCardcode() {
				return cardcode;
			}

			public void setCardcode(String cardcode) {
				this.cardcode = cardcode;
			}

			public int getUserlevel() {
				return userlevel;
			}

			public void setUserlevel(int userlevel) {
				this.userlevel = userlevel;
			}

			public int getVip() {
				return vip;
			}

			public void setVip(int vip) {
				this.vip = vip;
			}

			public int getStatus() {
				return status;
			}

			public void setStatus(int status) {
				this.status = status;
			}

			public String getRegtime() {
				return regtime;
			}

			public void setRegtime(String regtime) {
				this.regtime = regtime;
			}

			public String getUpdatetime() {
				return updatetime;
			}

			public void setUpdatetime(String updatetime) {
				this.updatetime = updatetime;
			}

			public String getLastlogintime() {
				return lastlogintime;
			}

			public void setLastlogintime(String lastlogintime) {
				this.lastlogintime = lastlogintime;
			}

			public String getLastloginip() {
				return lastloginip;
			}

			public void setLastloginip(String lastloginip) {
				this.lastloginip = lastloginip;
			}

			public int getLastloginappid() {
				return lastloginappid;
			}

			public void setLastloginappid(int lastloginappid) {
				this.lastloginappid = lastloginappid;
			}

			public String getRegip() {
				return regip;
			}

			public void setRegip(String regip) {
				this.regip = regip;
			}

			public int getRegappid() {
				return regappid;
			}

			public void setRegappid(int regappid) {
				this.regappid = regappid;
			}
		}

		public ProtData getData() {
			return data;
		}

		public void setData(ProtData data) {
			this.data = data;
		}
	}

	/**
	 * 
	 * 时间戳解析
	 * 
	 * @author Zhouls
	 * @date 2015-4-7 下午2:40:02
	 */
	public static class ProtSvrTime extends ProtBase {
		private ProtData data;

		public static class ProtData {
			/** 服务端时间戳 */
			private long server_time;

			public long getServer_time() {
				return server_time;
			}

			public void setServer_time(long server_time) {
				this.server_time = server_time;
			}
		}

		public ProtData getData() {
			return data;
		}

		public void setData(ProtData data) {
			this.data = data;
		}
	}

	public static class ProtLicenceInfo {
		public String dl_name;
		public String dl_num;
		public String vehicle_plate_num;
		public String vehicle_type;
		public int status;
		public String reason;
	}

	/**
	 * 
	 * 二维码扫描解析
	 * 
	 * @author Zhouls
	 * @date 2015-4-7 下午2:40:22
	 */
	public static class ProtQrCode extends ProtBase {
		private String guid;
		private String create_time;
		private String imgdata;

		public String getGuid() {
			return guid;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		public String getCreate_time() {
			return create_time;
		}

		public void setCreate_time(String create_time) {
			this.create_time = create_time;
		}

		public String getImgdata() {
			return imgdata;
		}

		public void setImgdata(String imgdata) {
			this.imgdata = imgdata;
		}
	}

	// public static MtqAppInfo parseAppInfo(String strData) {
	// MtqAppInfo mtqAppInfo = null;
	// JsonArray array = CldSapParser.fromJsonArray(strData, "data");
	// if (array != null) {
	// for (int i = 0; i < array.size(); i++) {
	// mtqAppInfo = new MtqAppInfo();
	// if (array.get(i) != null) {
	// if (array.get(i).isJsonObject()) {
	// JsonObject json = (JsonObject) array.get(i);
	// ProtAppInfo protAppinfo = CldSapParser.fromJson(
	// json.toString(), ProtAppInfo.class);
	// if (protAppinfo != null) {
	// protAppinfo.protParse(mtqAppInfo);
	// }
	// }
	// }
	// }
	// }
	// return mtqAppInfo;
	// }
	//

	public static MtqAppInfo parseAppInfo(String strData) {
		MtqAppInfo mtqAppInfo = null;
		JsonArray array = CldSapParser.fromJsonArray(strData, "data");
		if (array != null) {
			for (int i = 0; i < array.size(); i++) {
				mtqAppInfo = new MtqAppInfo();
				if (array.get(i) != null) {
					if (array.get(i).isJsonObject()) {
						JsonObject json = (JsonObject) array.get(i);
						ProtAppInfo protAppinfo = CldSapParser.fromJson(json.toString(), ProtAppInfo.class);
						if (protAppinfo != null) {
							protAppinfo.protParse(mtqAppInfo);
						}
					}
				}
			}
		}
		return mtqAppInfo;
	}
	
	
	public static MtqAppInfoNew parseAppInfoForNew(String strData) {
		MtqAppInfoNew mtqAppInfo = null;
		JsonObject tmpobj = CldSapParser.fromJsonObject(strData, "upgrade","upgrade_version");
				//CldSapParser.(strData, "upgrade");
		//JsonArray array = CldSapParser.fromJsonArray(strData, "upgrade");
		
	
		if(tmpobj != null){
			
			mtqAppInfo = CldSapParser.fromJson(tmpobj.toString(), MtqAppInfoNew.class);
			
		}
		
		//Log.e("expiredtime", strData+"  "+mtqAppInfo.expiredtime+" currenttime"+CldKDeviceAPI.getSvrTime()+"  "+mtqAppInfo.filepath);
		if(CldKDeviceAPI.getSvrTime()>mtqAppInfo.expiredtime)
			return null;
		else
			return mtqAppInfo;
//		if (array != null) {
//			for (int i = 0; i < array.size(); i++) {
//				mtqAppInfo = new MtqAppInfo();
//				if (array.get(i) != null) {
//					if (array.get(i).isJsonObject()) {
//						JsonObject json = (JsonObject) array.get(i);
//						ProtAppInfo protAppinfo = CldSapParser.fromJson(json.toString(), ProtAppInfo.class);
//						if (protAppinfo != null) {
//							protAppinfo.protParse(mtqAppInfo);
//						}
//					}
//				}
//			}
//		}
//		return mtqAppInfo;
	}

	public static void parseAppInfos(String strData, List<MtqAppInfo> lstOfResult) {
		JsonArray array = CldSapParser.fromJsonArray(strData, "data");
		if (array != null) {
			for (int i = 0; i < array.size(); i++) {
				MtqAppInfo appinfo = new MtqAppInfo();
				if (array.get(i) != null) {
					if (array.get(i).isJsonObject()) {
						JsonObject json = (JsonObject) array.get(i);
						ProtAppInfo protAppinfo = CldSapParser.fromJson(json.toString(), ProtAppInfo.class);
						if (protAppinfo != null) {
							protAppinfo.protParse(appinfo);
							lstOfResult.add(appinfo);
						}
					}
				}
			}
		}
	}

	/**
	 * App信息解析
	 * 
	 * @author zhaoqy
	 * @date 2017-2-9
	 */
	public static class ProtAppInfo extends ProtBase {

		public String app_icon;
		public String app_name;
		public String ver_name;
		public String app_url;
		public String upgrade_desc;
		public int pack_size;
		public int ver_code;
		public String pack_name;
		public int quiesce;
		public int down_times;
		public int validate;
		public String app_desc;

		public void protParse(MtqAppInfo mtqApp) {
			mtqApp.app_icon = this.app_icon;
			mtqApp.app_name = this.app_name;
			mtqApp.ver_name = this.ver_name;
			mtqApp.app_url = this.app_url;
			mtqApp.upgrade_desc = this.upgrade_desc;
			mtqApp.pack_size = this.pack_size;
			mtqApp.ver_code = this.ver_code;
			mtqApp.pack_name = this.pack_name;
			mtqApp.quiesce = this.quiesce;
			mtqApp.down_times = this.down_times;
			mtqApp.validate = this.validate;
			mtqApp.app_desc = this.app_desc;
		}
	}

	public static class ProtAppStatus extends ProtBase {
		private ArrayList<Packname> packnames;

		public ArrayList<Packname> getPacknames() {
			return packnames;
		}

		public void setPacknames(ArrayList<Packname> packnames) {
			this.packnames = packnames;
		}

		class Packname {
			String packname;

			public String getPackname() {
				return packname;
			}

			public void setPackname(String packname) {
				this.packname = packname;
			}
		}
	}

	public static class ProtAccountKey {
		private String code;
		private int errcode;
		private String errmsg;

		public ProtAccountKey() {
			code = "";
			errcode = -1;
			errmsg = "";
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public int getErrcode() {
			return errcode;
		}

		public void setErrcode(int errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}
	}
}
