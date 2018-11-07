/*
 * @Title CldKMessageParse.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-23 上午9:01:17
 * @version 1.0
 */
package com.yunbaba.ols.sap.parse;

import java.util.List;

import com.yunbaba.ols.sap.bean.CldSapKMParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareAKeyCallParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareOperateParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.SharePoiParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareAKeyCallParm.AKeyCallPoint;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareAKeyCallParm.CldAreaEgg;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;

/**
 * 消息系统解析
 * 
 * @author Zhouls
 * @date 2015-3-23 上午9:01:17
 */
public class CldKMessageParse {
	/**
	 * 
	 * 构建消息返回
	 * 
	 * @author Zhouls
	 * @date 2015-3-25 下午5:11:05
	 */
	public static class ProtCreMsg extends ProtBase {
		/** 构建的消息id */
		private long messageid;

		public long getMessageid() {
			return messageid;
		}

		public void setMessageid(long messageid) {
			this.messageid = messageid;
		}
	}

	/**
	 * 
	 * 接收消息
	 * 
	 * @author Zhouls
	 * @date 2015-3-25 下午5:15:35
	 */
	public static class ProtRecMsg extends ProtBase {
		private List<ProtMsgType> data;

		public static class ProtMsgType {
			/**
			 * 消息类型
			 */
			private int messagetype;

			public int getMessagetype() {
				return messagetype;
			}

			public void setMessagetype(int messagetype) {
				this.messagetype = messagetype;
			}
		}

		public static class ProtMsgParm extends ProtMsgType {
			/**
			 * 通用返回值
			 */
			private long messageid;
			private String title;
			private long createuserid;
			private String usermobile;
			private String createtime;
			private String hyperlink;
			private int poptype;
			private String imageurl;
			private String roadname;
			private long downloadtime;
			private int status;
			private int createtype;
			private int receiveobject;
			private String createuser;
			private int apptype;
			private int module;

			public long getMessageid() {
				return messageid;
			}

			public void setMessageid(long messageid) {
				this.messageid = messageid;
			}

			public String getTitle() {
				return title;
			}

			public void setTitle(String title) {
				this.title = title;
			}

			public long getCreateuserid() {
				return createuserid;
			}

			public void setCreateuserid(long createuserid) {
				this.createuserid = createuserid;
			}

			public String getUsermobile() {
				return usermobile;
			}

			public void setUsermobile(String usermobile) {
				this.usermobile = usermobile;
			}

			public String getCreatetime() {
				return createtime;
			}

			public void setCreatetime(String createtime) {
				this.createtime = createtime;
			}

			public String getHyperlink() {
				return hyperlink;
			}

			public void setHyperlink(String hyperlink) {
				this.hyperlink = hyperlink;
			}

			public int getPoptype() {
				return poptype;
			}

			public void setPoptype(int poptype) {
				this.poptype = poptype;
			}

			public String getImageurl() {
				return imageurl;
			}

			public void setImageurl(String imageurl) {
				this.imageurl = imageurl;
			}

			public String getRoadname() {
				return roadname;
			}

			public void setRoadname(String roadname) {
				this.roadname = roadname;
			}

			public int getStatus() {
				return status;
			}

			public void setStatus(int status) {
				this.status = status;
			}

			public int getCreatetype() {
				return createtype;
			}

			public void setCreatetype(int createtype) {
				this.createtype = createtype;
			}

			public int getReceiveobject() {
				return receiveobject;
			}

			public void setReceiveobject(int receiveobject) {
				this.receiveobject = receiveobject;
			}

			public String getCreateuser() {
				return createuser;
			}

			public void setCreateuser(String createuser) {
				this.createuser = createuser;
			}

			public int getApptype() {
				return apptype;
			}

			public void setApptype(int apptype) {
				this.apptype = apptype;
			}

			public long getDownloadtime() {
				return downloadtime;
			}

			public void setDownloadtime(long downloadtime) {
				this.downloadtime = downloadtime;
			}

			public int getModule() {
				return module;
			}

			public void setModule(int module) {
				this.module = module;
			}
		}

		/**
		 * 
		 * 系统消息
		 * 
		 * @author Zhouls
		 * @date 2015-3-25 下午5:16:24
		 */
		public static class ProtSysMsg extends ProtMsgParm {
			/**
			 * messagetype=1,2,3,4,或未知类型
			 */
			private String content;

			public void protParse(CldSapKMParm parm) {
				parm.setModule(super.module);
				parm.setMsgType(super.getMessagetype());
				parm.setMessageId(super.messageid);
				parm.setTitle(super.title);
				parm.setCreateuserid(super.createuserid);
				parm.setUsermobile(super.usermobile);
				parm.setCreatetime(super.createtime);
				parm.setHyperlink(super.hyperlink);
				parm.setPoptype(super.poptype);
				parm.setImageurl(super.imageurl);
				parm.setRoadname(super.roadname);
				parm.setDownloadTime(super.downloadtime);
				parm.setStatus(super.status);
				parm.setCreateType(super.createtype);
				parm.setReceiveObject(super.receiveobject);
				parm.setCreateuser(super.createuser);
				parm.setApptype(super.apptype);
				parm.setOperateMsg(new ShareOperateParm(content));
			}

			public String getContent() {
				return content;
			}

			public void setContent(String content) {
				this.content = content;
			}
		}

		/**
		 * 
		 * poi消息
		 * 
		 * @author Zhouls
		 * @date 2015-3-25 下午5:16:53
		 */
		public static class ProtPoiMsg extends ProtMsgParm {
			/**
			 * messagetype=11
			 */
			private ProtContent content;

			public void protParse(CldSapKMParm parm) {
				parm.setModule(super.module);
				parm.setMsgType(super.getMessagetype());
				parm.setMessageId(super.messageid);
				parm.setTitle(super.title);
				parm.setCreateuserid(super.createuserid);
				parm.setUsermobile(super.usermobile);
				parm.setCreatetime(super.createtime);
				parm.setHyperlink(super.hyperlink);
				parm.setPoptype(super.poptype);
				parm.setImageurl(super.imageurl);
				parm.setRoadname(super.roadname);
				parm.setDownloadTime(super.downloadtime);
				parm.setStatus(super.status);
				parm.setCreateType(super.createtype);
				parm.setReceiveObject(super.receiveobject);
				parm.setCreateuser(super.createuser);
				parm.setApptype(super.apptype);
				parm.setPoiMsg(new SharePoiParm(content.name, content.x + ","
						+ content.y, 0));
			}

			public static class ProtContent {
				private String x;
				private String y;
				private String name;

				public String getX() {
					return x;
				}

				public void setX(String x) {
					this.x = x;
				}

				public String getY() {
					return y;
				}

				public void setY(String y) {
					this.y = y;
				}

				public String getName() {
					return name;
				}

				public void setName(String name) {
					this.name = name;
				}
			}

			public ProtContent getContent() {
				return content;
			}

			public void setContent(ProtContent content) {
				this.content = content;
			}
		}

		/**
		 * 
		 * 一键通消息
		 * 
		 * @author Zhouls
		 * @date 2015-3-25 下午5:17:40
		 */
		public static class ProtAkeyMsg extends ProtMsgParm {
			/**
			 * messagetype=15
			 */
			private ProtContent content;

			public void protParse(CldSapKMParm parm) {
				parm.setModule(super.module);
				parm.setMsgType(super.getMessagetype());
				parm.setMessageId(super.messageid);
				parm.setTitle(super.title);
				parm.setCreateuserid(super.createuserid);
				parm.setUsermobile(super.usermobile);
				parm.setCreatetime(super.createtime);
				parm.setHyperlink(super.hyperlink);
				parm.setPoptype(super.poptype);
				parm.setImageurl(super.imageurl);
				parm.setRoadname(super.roadname);
				parm.setDownloadTime(super.downloadtime);
				parm.setStatus(super.status);
				parm.setCreateType(super.createtype);
				parm.setReceiveObject(super.receiveobject);
				parm.setCreateuser(super.createuser);
				parm.setApptype(super.apptype);
				ShareAKeyCallParm aKeyParm = new ShareAKeyCallParm();
				content.protParse(aKeyParm);
				parm.setaKeyCallMsg(aKeyParm);
			}

			public static class ProtContent {
				private String Action;
				private ProtPoint StartPoint;
				private ProtPoint EndPoint;
				private ProtPoint RoutePoint;
				private ProtPoint AvoidPoint;
				private String NavigationMode;
				private String Proxy_ID;

				public void protParse(ShareAKeyCallParm aKeyParm) {
					aKeyParm.setAction(Action);
					aKeyParm.setNavigationMode(NavigationMode);
					aKeyParm.setProxyId(Proxy_ID);
					aKeyParm.setStartPoint(new AKeyCallPoint(StartPoint.Name,
							StartPoint.Longitude, StartPoint.Latitude));
					aKeyParm.setEndPoint(new AKeyCallPoint(EndPoint.Name,
							EndPoint.Longitude, EndPoint.Latitude));
					aKeyParm.setRoutePoint(new AKeyCallPoint(RoutePoint.Name,
							RoutePoint.Longitude, RoutePoint.Latitude));
					aKeyParm.setAvoidPoint(new AKeyCallPoint(AvoidPoint.Name,
							AvoidPoint.Longitude, AvoidPoint.Latitude));
				}

				public static class ProtPoint {
					private String Name;
					private String Longitude;
					private String Latitude;

					public String getName() {
						return Name;
					}

					public void setName(String name) {
						Name = name;
					}

					public String getLongitude() {
						return Longitude;
					}

					public void setLongitude(String longitude) {
						Longitude = longitude;
					}

					public String getLatitude() {
						return Latitude;
					}

					public void setLatitude(String latitude) {
						Latitude = latitude;
					}
				}

				public String getAction() {
					return Action;
				}

				public void setAction(String action) {
					Action = action;
				}

				public ProtPoint getStartPoint() {
					return StartPoint;
				}

				public void setStartPoint(ProtPoint startPoint) {
					StartPoint = startPoint;
				}

				public ProtPoint getEndPoint() {
					return EndPoint;
				}

				public void setEndPoint(ProtPoint endPoint) {
					EndPoint = endPoint;
				}

				public ProtPoint getRoutePoint() {
					return RoutePoint;
				}

				public void setRoutePoint(ProtPoint routePoint) {
					RoutePoint = routePoint;
				}

				public ProtPoint getAvoidPoint() {
					return AvoidPoint;
				}

				public void setAvoidPoint(ProtPoint avoidPoint) {
					AvoidPoint = avoidPoint;
				}

				public String getNavigationMode() {
					return NavigationMode;
				}

				public void setNavigationMode(String navigationMode) {
					NavigationMode = navigationMode;
				}

				public String getProxy_ID() {
					return Proxy_ID;
				}

				public void setProxy_ID(String proxy_ID) {
					Proxy_ID = proxy_ID;
				}
			}

			public ProtContent getContent() {
				return content;
			}

			public void setContent(ProtContent content) {
				this.content = content;
			}
		}

		public List<ProtMsgType> getData() {
			return data;
		}

		public void setData(List<ProtMsgType> data) {
			this.data = data;
		}
	}

	public static class ProtMaxLength extends ProtBase {
		private int maxlength;

		public int getMaxlength() {
			return maxlength;
		}

		public void setMaxlength(int maxlength) {
			this.maxlength = maxlength;
		}

	}

	/**
	 * 
	 * 彩蛋消息
	 * 
	 * @author Zhouls
	 * @date 2015-3-25 下午5:18:28
	 */
	public static class ProtEggs {

		private List<ProtData> data;

		public void protParse(List<CldAreaEgg> list) {
			if (null != data) {
				for (int i = 0; i < data.size(); i++) {
					CldAreaEgg egg = new CldAreaEgg();
					egg.setAreaid(data.get(i).getAreaid());
					egg.setEndtime(data.get(i).getEndtime());
					egg.setMaxx(data.get(i).getMaxx());
					egg.setMaxy(data.get(i).getMaxy());
					egg.setMinx(data.get(i).getMinx());
					egg.setMiny(data.get(i).getMiny());
					egg.setRegioncode(data.get(i).getRegioncode());
					egg.setStarttime(data.get(i).getStarttime());
					egg.setType(data.get(i).getType());
					list.add(egg);
				}
			}
		}

		public static class ProtData {
			private long areaid;
			private int type;
			private long starttime;
			private long endtime;
			private long regioncode;
			private long minx;
			private long miny;
			private long maxx;
			private long maxy;

			public long getAreaid() {
				return areaid;
			}

			public void setAreaid(long areaid) {
				this.areaid = areaid;
			}

			public int getType() {
				return type;
			}

			public void setType(int type) {
				this.type = type;
			}

			public long getStarttime() {
				return starttime;
			}

			public void setStarttime(long starttime) {
				this.starttime = starttime;
			}

			public long getEndtime() {
				return endtime;
			}

			public void setEndtime(long endtime) {
				this.endtime = endtime;
			}

			public long getRegioncode() {
				return regioncode;
			}

			public void setRegioncode(long regioncode) {
				this.regioncode = regioncode;
			}

			public long getMinx() {
				return minx;
			}

			public void setMinx(long minx) {
				this.minx = minx;
			}

			public long getMiny() {
				return miny;
			}

			public void setMiny(long miny) {
				this.miny = miny;
			}

			public long getMaxx() {
				return maxx;
			}

			public void setMaxx(long maxx) {
				this.maxx = maxx;
			}

			public long getMaxy() {
				return maxy;
			}

			public void setMaxy(long maxy) {
				this.maxy = maxy;
			}
		}

		public List<ProtData> getData() {
			return data;
		}

		public void setData(List<ProtData> data) {
			this.data = data;
		}
	}
}
