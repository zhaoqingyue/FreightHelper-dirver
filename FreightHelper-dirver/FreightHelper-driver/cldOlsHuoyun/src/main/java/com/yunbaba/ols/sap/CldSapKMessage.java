/*
 * @Title CldSapKMessage.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.ols.sap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cld.gson.JsonArray;
import com.cld.gson.JsonObject;
import com.yunbaba.ols.dal.CldDalKCallNavi;
import com.yunbaba.ols.sap.bean.CldSapKMParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareAKeyCallParm;
import com.yunbaba.ols.sap.parse.CldKBaseParse;
import com.yunbaba.ols.sap.parse.CldKMessageParse.ProtRecMsg.ProtAkeyMsg;
import com.yunbaba.ols.sap.parse.CldKMessageParse.ProtRecMsg.ProtPoiMsg;
import com.yunbaba.ols.sap.parse.CldKMessageParse.ProtRecMsg.ProtSysMsg;
import com.yunbaba.ols.sap.parse.CldKMessageParse.ProtRecMsg.ProtAkeyMsg.ProtContent;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;


/**
 * 消息系统协议接口
 * 
 * @author Zhouls
 * @date 2014-8-13 上午10:19:48
 */
public class CldSapKMessage {

	/** 首次密文. */
	public static String keyCode;
	/** 协议版本号. */
	private static final int APIVER = 1;
	/** 返回数据编码(1UTF-8 2GBK，目前暂支持1,默认为1). */
	private static final int RSCHARSET = 1;
	/** 返回数据格式(1JSON 2二进制，目前暂支持1,默认为1) */
	private static final int RSFORMAT = 1;
	/** UMSA协议版本(kz:1 kz2:2). */
	private static final int UMSAVER = 2;

	/**
	 * 获取消息系统密钥(301)
	 * 
	 * @return int
	 * @author Zhouls
	 * @date 2014-9-22 上午9:12:45
	 */
	public static CldSapReturn initKeyCode() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		String key = "C9T659YZA5UY6357G140TRVCC5411UY9";
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrMsg() + "kmessage_get_code.php", key);
		return errRes;
	}

	/**
	 * 构造消息(302)
	 * 
	 * @param kuid
	 *            用户Id
	 * @param duid
	 *            用户设备Id
	 * @param messagetype
	 *            消息类型(11:位置POI;12:路线;13:路书;14:路况)
	 * @param module
	 *            消息所属模块(1:K云;2:WEB地图;3:一键通)
	 * @param parm
	 *            回传参数表：errCode=0时，写入messageid
	 * @param title
	 *            消息标题（可为Null）
	 * @param endtime
	 *            有效结束时间(YYYY-MM-dd H:i:s)（可为Null）
	 * @param bussinessid
	 *            业务编号 CM(1)
	 * @param session
	 *            登录返回的session
	 * @param appid
	 *            CM(9)
	 * @param apptype
	 *            the apptype
	 * @return errCode(0,401，402，403，404) int
	 * @author Zhouls
	 * @date 2014-8-21 下午3:28:01
	 */
	public static CldSapReturn createCldMsg(long kuid, long duid,
			int messagetype, int module, CldSapKMParm parm, String title,
			String endtime, int bussinessid, String session, int appid,
			int apptype) {
		Map<String, Object> map = new HashMap<String, Object>();
		CldSapReturn errRes = new CldSapReturn();
		switch (messagetype) {
		case 11:
			/**
			 * 分享一个点
			 */
			map.put("apiver", APIVER);
			map.put("appid", appid);
			map.put("apptype", apptype);
			map.put("bussinessid", bussinessid);
			map.put("duid", duid);
			map.put("kuid", kuid);
			map.put("messagetype", messagetype);
			map.put("module", module);
			map.put("name", parm.getPoiMsg().getName());
			map.put("poi", parm.getPoiMsg().getPoi());
			map.put("rscharset", RSCHARSET);
			map.put("rsformat", RSFORMAT);
			map.put("session", session);
			map.put("umsaver", UMSAVER);
			CldSapParser.putStringToMap(map, "title", title);
			CldSapParser.putStringToMap(map, "endtime", endtime);
			errRes = CldKBaseParse.getPostParms(map, CldSapUtil.getNaviSvrMsg()
					+ "kmessage_create_terminal_message.php",
					CldSapUtil.decodeKey(keyCode));
			return errRes;
		case 12:
			/**
			 * 分享一条路线
			 */
			map.put("apiver", APIVER);
			map.put("appid", appid);
			map.put("apptype", apptype);
			map.put("bussinessid", bussinessid);
			map.put("rscharset", RSCHARSET);
			map.put("rsformat", RSFORMAT);
			map.put("umsaver", UMSAVER);
			map.put("kuid", kuid);
			map.put("duid", duid);
			map.put("messagetype", messagetype);
			map.put("module", module);
			map.put("name", parm.getRouteMsg().getName());
			map.put("start", parm.getRouteMsg().getStart());
			map.put("end", parm.getRouteMsg().getEnd());
			map.put("routepoint", parm.getRouteMsg().getRoutePoint());
			map.put("avoidpoint", parm.getRouteMsg().getAvoidPoint());
			map.put("conditioncode", parm.getRouteMsg().getConditionCode());
			map.put("aviodcondition", parm.getRouteMsg().getAviodCondition());
			map.put("forbidcondition", parm.getRouteMsg().getForbidCondition());
			map.put("mapver", parm.getRouteMsg().getMapVer());
			map.put("session", session);
			CldSapParser.putStringToMap(map, "endtime", endtime);
			CldSapParser.putStringToMap(map, "title", title);
			errRes = CldKBaseParse.getPostParms(map, CldSapUtil.getNaviSvrMsg()
					+ "kmessage_create_terminal_message.php",
					CldSapUtil.decodeKey(keyCode));
			return errRes;
		default:
			return errRes;
		}
	}

	/**
	 * 发送消息(303)
	 * 
	 * @param messageid
	 *            构建消息返回的消息id
	 * @param duid
	 *            用户设备id
	 * @param uniqueids
	 *            用户\设备标识+Apptype，多个以;分隔,如 1,1;2,1
	 * @param messagetype
	 *            消息类型 11:POI; 12:路径; 13:路书; 14:路况; 15:一键通
	 * @param kuid
	 *            the kuid
	 * @param bussinessid
	 *            业务编号 CM(1)
	 * @param session
	 *            登录返回的session
	 * @param appid
	 *            CM(9)
	 * @return int
	 * @author Zhouls
	 * @date 2014-9-1 上午10:15:43
	 */
	public static CldSapReturn sendCldMsg(long messageid, long duid,
			String uniqueids, int messagetype, long kuid, int bussinessid,
			String session, int appid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("appid", appid);
		map.put("bussinessid", bussinessid);
		map.put("duid", duid);
		map.put("kuid", kuid);
		map.put("messageid", messageid);
		map.put("messagetype", messagetype);
		map.put("session", session);
		map.put("uniqueids", uniqueids);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrMsg()
						+ "kmessage_send_terminal_message.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 接收消息（304）
	 * 
	 * @param duid
	 *            用户设备id
	 * @param apptype
	 *            应用类型（1：CM；2：IOS），多个以";"分隔，如"1;2"
	 * @param prover
	 *            版本号，多个以";"分隔，如"6.0;11.0"
	 * @param list
	 *            回传消息列表
	 * @param kuid
	 *            用户Id（没有则传0）
	 * @param regionCode
	 *            行政区编码
	 * @param x
	 *            当前坐标x
	 * @param y
	 *            当前坐标y
	 * @param bussinessid
	 *            业务编号 CM(1)
	 * @param session
	 *            登录返回的session
	 * @param appid
	 *            CM(9)
	 * @param istmc
	 *            是否开启路况（0：开启；1：关闭），默认为0（开启路况才拉彩蛋消息）
	 * @return int
	 * @author Zhouls
	 * @date 2014-12-18 下午12:14:03
	 */
	public static CldSapReturn recShareMsg(long duid, int apptype,
			String prover, long kuid, int regionCode, long x, long y,
			int bussinessid, String session, int appid, int istmc) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("apptype", apptype);
		map.put("duid", duid);
		map.put("prover", prover);
		CldSapParser.putIntToMap(map, "appid", appid);
		CldSapParser.putIntToMap(map, "bussinessid", bussinessid);
		CldSapParser.putIntToMap(map, "istmc", istmc);
		CldSapParser.putIntToMap(map, "regioncode", regionCode);
		CldSapParser.putLongToMap(map, "kuid", kuid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapParser.putLongToMap(map, "x", x);
		CldSapParser.putLongToMap(map, "y", y);
		if(CldSapUtil.getNaviSvrMsg() == null || CldSapUtil.getNaviSvrMsg().length() <= 0)
		{
			
		}
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrMsg() + "kmessage_download_message.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 终端接收最新历史消息(305)
	 * 
	 * @param duid
	 *            设备Id
	 * @param messagetype
	 *            1:活动消息; 2:升级消息; 3:区域消息; 11:POI; 12:路径; 13:路书; 14:路况; 15:一键通
	 *            消息类型，支持多个，以","分隔
	 * @param kuid
	 *            用户Id
	 * @param list
	 *            the list
	 * @param bussinessid
	 *            业务编号 CM(1)
	 * @param session
	 *            登录返回的session
	 * @param appid
	 *            CM(9)
	 * @param apptype
	 *            the apptype
	 * @return errCode(401，402，403，404) int
	 * @author Zhouls
	 * @date 2014-9-1 上午10:22:15
	 */
	public static CldSapReturn recLastestMsgHitory(long duid,
			String messagetype, long kuid, int bussinessid, String session,
			int appid, int apptype) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("bussinessid", bussinessid);
		map.put("duid", duid);
		map.put("messagetype", messagetype);
		map.put("appid", appid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapParser.putLongToMap(map, "kuid", kuid);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrMsg()
						+ "kmessage_receive_latest_termial_history.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 终端接收历史新消息查询接口（向下滑动）(306)
	 * 
	 * @param duid
	 *            设备Id
	 * @param messagetype
	 *            1:活动消息; 2:升级消息; 3:区域消息; 11:POI; 12:路径; 13:路书; 14:路况; 15:一键通
	 *            消息类型，支持多个，以","分隔
	 * @param length
	 *            每次请求记录数
	 * @param lastid
	 *            最后一个的消息id
	 * @param lasttime
	 *            最后一个的消息时间
	 * @param kuid
	 *            用户Id
	 * @param list
	 *            回传消息列表
	 * @param bussinessid
	 *            业务编号 CM(1)
	 * @param session
	 *            登录返回的session
	 * @param appid
	 *            CM(9)
	 * @param apptype
	 *            the apptype
	 * @return 错误编码 401，402，403，404 int
	 * @author Zhouls
	 * @date 2014-9-1 上午10:30:43
	 */
	public static CldSapReturn recNewMsgHitory(long duid, String messagetype,
			int length, long lastid, long lasttime, long kuid, int bussinessid,
			String session, int appid, int apptype) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("bussinessid", bussinessid);
		map.put("duid", duid);
		map.put("messagetype", messagetype);
		map.put("appid", appid);
		map.put("lastid", lastid);
		map.put("lasttime", lasttime);
		map.put("length", length);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapParser.putLongToMap(map, "kuid", kuid);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrMsg()
						+ "kmessage_receive_new_termial_history.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 终端接收历史旧消息查询接口（向上滑动）(307)
	 * 
	 * @param duid
	 *            设备Id
	 * @param messagetype
	 *            1:活动消息; 2:升级消息; 3:区域消息; 11:POI; 12:路径; 13:路书; 14:路况; 15:一键通
	 *            消息类型，支持多个，以","分隔
	 * @param length
	 *            每次请求记录数
	 * @param lastid
	 *            最后一个的消息id
	 * @param lasttime
	 *            最后一个的消息时间
	 * @param kuid
	 *            用户Id
	 * @param list
	 *            回传消息列表
	 * @param bussinessid
	 *            业务编号 CM(1)
	 * @param session
	 *            登录返回的session
	 * @param appid
	 *            CM(9)
	 * @param apptype
	 *            the apptype
	 * @return 错误编码 401，402，403，404 int
	 * @author Zhouls
	 * @date 2014-9-1 上午10:30:43
	 */
	public static CldSapReturn recOldMsgHitory(long duid, String messagetype,
			int length, long lastid, long lasttime, long kuid, int bussinessid,
			String session, int appid, int apptype) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("bussinessid", bussinessid);
		map.put("duid", duid);
		map.put("messagetype", messagetype);
		map.put("appid", appid);
		map.put("lastid", lastid);
		map.put("lasttime", lasttime);
		map.put("length", length);
		CldSapParser.putLongToMap(map, "kuid", kuid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrMsg()
						+ "kmessage_receive_old_termial_history.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 终端更新消息阅读状态接口(308)
	 * 
	 * @param duid
	 *            设备Id
	 * @param kuid
	 *            用户Id
	 * @param session
	 *            Kuid不为空时，appid必传
	 * @param bussinessid
	 *            Kuid不为空时，bussinessid必传
	 * @param appid
	 *            Kuid不为空时，appid必传
	 * @param createtype
	 *            创建类型 1：运营后台；2：终端
	 * @param messageids
	 *            消息Id(msgid,createtime,createtype;msgid,createtime,createtype;)
	 * @return int
	 * @author Zhouls
	 * @date 2014-10-8 下午3:54:33
	 */
	public static CldSapReturn upMsgReadStatus(long duid, long kuid,
			String session, int bussinessid, int appid, int createtype,
			String messageids) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("bussinessid", bussinessid);
		map.put("duid", duid);
		map.put("createtype", createtype);
		map.put("appid", appid);
		map.put("messageids", messageids);
		CldSapParser.putLongToMap(map, "kuid", kuid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrMsg() + "kmessage_up_read_status.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 获取彩蛋列表（309）
	 * 
	 * @param areatype
	 *            区域属性类型：1行政区2范围 0全部
	 * @param starttime
	 *            开始时间
	 * @param endtime
	 *            结束时间
	 * @return int
	 * @author Zhouls
	 * @date 2014-12-17 下午6:37:42
	 */
	public static CldSapReturn getAreaList(int areatype, String starttime,
			String endtime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("areatype", areatype);
		CldSapParser.putStringToMap(map, "endtime", endtime);
		CldSapParser.putStringToMap(map, "starttime", starttime);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrMsg() + "kmessage_terminal_area_list.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 上报位置(407)
	 * 
	 * @param apptype
	 *            应用类型（1：CM；2：IOS）
	 * @param prover
	 *            版本号
	 * @param kuid
	 *            凯立德帐号id
	 * @param session
	 *            Session
	 * @param appid
	 *            appid
	 * @param bussinessid
	 *            bussinessid
	 * @param mobile
	 *            手机号,如为空，则按kuid绑定的所有手机号都上报位置
	 * @param longitude
	 *            WGS84 经度，单位：度
	 * @param latitude
	 *            WGS84 纬度，单位：度
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-14 上午9:15:21
	 */
	public static CldSapReturn upLocation(int apptype, String prover,
			long kuid, String session, int appid, int bussinessid,
			String mobile, double longitude, double latitude) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("bussinessid", bussinessid);
		map.put("apptype", apptype);
		map.put("latitude", latitude);
		map.put("appid", appid);
		map.put("longitude", longitude);
		map.put("prover", prover);
		CldSapParser.putLongToMap(map, "kuid", kuid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapParser.putStringToMap(map, "mobile", mobile);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrMsg() + "kmessage_up_ppt_location.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 获取一键通消息(408)
	 * 
	 * @param apptype
	 *            应用类型（1：CM；2：IOS）
	 * @param prover
	 *            版本号
	 * @param kuid
	 *            凯立德帐号id
	 * @param session
	 *            Session
	 * @param appid
	 *            appid
	 * @param bussinessid
	 *            bussinessid
	 * @param mobile
	 *            手机号码
	 * @param longitude
	 *            WGS84 经度，单位：度
	 * @param latitude
	 *            WGS84 纬度，单位：度
	 * @param isloop
	 *            是否轮循，默认为否
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-14 上午9:20:42
	 */
	public static CldSapReturn recPptMsg(int apptype, String prover, long kuid,
			String session, int appid, int bussinessid, String mobile,
			double longitude, double latitude, int isloop) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		map.put("apptype", apptype);
		map.put("prover", prover);
		map.put("isloop", isloop);
		map.put("latitude", latitude);
		map.put("longitude", longitude);
		CldSapParser.putIntToMap(map, "appid", appid);
		CldSapParser.putIntToMap(map, "bussinessid", bussinessid);
		CldSapParser.putLongToMap(map, "kuid", kuid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapParser.putStringToMap(map, "mobile", mobile);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrMsg() + "kmessage_get_ppt_message.php",
				CldSapUtil.decodeKey(keyCode));
		return errRes;
	}

	/**
	 * 解析消息列表
	 * 
	 * @param strData
	 *            接口返回的消息字符串
	 * @param list
	 *            消息列表
	 * @param kuid
	 *            当前登录的kuid
	 * @param apptype
	 *            应用程序类型
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-23 下午2:34:44
	 */
	public static void parseMessage(String strData, List<CldSapKMParm> list,
			long kuid, int apptype) {
		JsonArray array = CldSapParser.fromJsonArray(strData, "data");
		if (null != array) {
			for (int i = 0; i < array.size(); i++) {
				CldSapKMParm parm = new CldSapKMParm();
				if (null != array.get(i)) {
					if (array.get(i).isJsonObject()) {
						JsonObject json = (JsonObject) array.get(i);
						if (json.has("messagetype")) {
							int msgType = json.get("messagetype").getAsInt();
							switch (msgType) {
							case 1:
							case 2:
							case 3:
							case 4:
								/**
								 * 系统消息
								 */
								ProtSysMsg protSysMsg = CldSapParser.fromJson(
										json.toString(), ProtSysMsg.class);
								if (null != protSysMsg) {
									protSysMsg.protParse(parm);
									list.add(parm);
								}
								break;
							case 11:
								/**
								 * poi消息
								 */
								ProtPoiMsg protPoiMsg = CldSapParser.fromJson(
										json.toString(), ProtPoiMsg.class);
								if (null != protPoiMsg) {
									protPoiMsg.protParse(parm);
									list.add(parm);
								}
								break;
							case 15:
								/**
								 * 一键通消息
								 */
								ProtAkeyMsg protAkeyMsg = CldSapParser
										.fromJson(json.toString(),
												ProtAkeyMsg.class);
								if (null != protAkeyMsg) {
									protAkeyMsg.protParse(parm);
									list.add(parm);
								}
								break;
							default:
								/**
								 * 其他类型的消息
								 */
								ProtSysMsg protOtherMsg = CldSapParser
										.fromJson(json.toString(),
												ProtSysMsg.class);
								if (null != protOtherMsg) {
									protOtherMsg.protParse(parm);
									list.add(parm);
								}
								continue;
							}
						}
					}
				}
				if (parm.getCreateuserid() == kuid) {
					/**
					 * 本帐号设备间消息
					 */
					if (apptype == parm.getApptype()) {
						parm.setCreateuser("发送到其它设备");
					} else {
						switch (parm.getApptype()) {
						case 1:
							parm.setCreateuser("来自我的Android手机");
							break;
						case 2:
							parm.setCreateuser("来自我的iPhone手机");
							break;
						case 5:
							parm.setCreateuser("来自我的汽车");
							break;
						default:
							parm.setCreateuser("来自我的其它设备");
						}
					}
				} else {
					/**
					 * 帐号间消息
					 */
					switch (parm.getMsgType()) {
					case 1:
					case 2:
					case 3:
					case 4:
						parm.setCreateuser("来自系统消息");
						break;
					case 11:
					case 12:
					case 13:
					case 14:
						parm.setCreateuser("来自K友" + parm.getCreateuser());
						break;
					case 15:
						parm.setCreateuser("来自一键通");
						break;
					}
				}
			}
		}
	}

	/**
	 * 解析一键通消息
	 * 
	 * @param strData
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-23 下午2:34:23
	 */
	public static void parsePPtMessage(String strData) {
		List<ShareAKeyCallParm> list = new ArrayList<ShareAKeyCallParm>();
		JsonArray array = CldSapParser.fromJsonArray(strData, "data");
		if (null != array) {
			for (int i = 0; i < array.size(); i++) {
				ShareAKeyCallParm aKeyCallMsg = new ShareAKeyCallParm();
				if (null != array.get(i)) {
					if (array.get(i).isJsonObject()) {
						JsonObject json = (JsonObject) array.get(i);
						ProtContent protAkeyMsg = CldSapParser.fromJson(
								json.toString(), ProtContent.class);
						if (null != protAkeyMsg) {
							protAkeyMsg.protParse(aKeyCallMsg);
							list.add(aKeyCallMsg);
						}
					}
				}
			}
			CldDalKCallNavi.getInstance().setMsgList(list);
		}
	}
}
