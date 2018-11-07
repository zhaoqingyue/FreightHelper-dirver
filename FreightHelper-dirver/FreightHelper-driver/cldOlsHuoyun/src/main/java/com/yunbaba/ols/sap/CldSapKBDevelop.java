/*
 * @Title CldSapKBDevelop.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.ols.sap;

import java.util.HashMap;
import java.util.Map;

import com.yunbaba.ols.sap.parse.CldKBaseParse;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;

/**
 * BD搜索
 * 
 * @author Zhouls
 * @date 2014-11-18 下午3:25:22
 */
public class CldSapKBDevelop {

	/**
	 * 获取周边酒店（800）
	 * 
	 * @param id
	 *            当前检索城市ID
	 * @param m
	 *            检索POI类型：101预定酒店，必须指定
	 * @param x
	 *            检索中心点经度坐标
	 * @param y
	 *            检索中心点纬度坐标
	 * @param r
	 *            搜索半径，最好传入10000以内，要不然范围太大影响效率。单位：米
	 * @param s
	 *            起始序号，从1开始；默认值为1
	 * @param p
	 *            检索个数，取值范围1-200；默认值为200
	 * @param sort
	 *            排序规则：默认排序——SORT_DEFAULT，距离排序——SORT_DISTANCE
	 * @param version
	 *            接口版本，从1开始
	 * @param userid
	 *            用户ID，若无则传null
	 * @param session
	 *            会话ID，若无则传null
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-18 下午3:41:24
	 */
	public static CldSapReturn getHotelList(long id, int m, long x, long y,
			int r, int s, int p, int sort, int version, String userid,
			String session) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("m", m);
		map.put("c", x + "+" + y);
		map.put("r", r);
		map.put("s", s);
		map.put("p", p);
		map.put("sort", sort);
		map.put("version", version);
		CldSapParser.putStringToMap(map, "userid", userid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapReturn errRes = CldKBaseParse.getGetParmsNoEncode(map,
				CldSapUtil.getNaviSvrON() + "pois_search.ums", null);
		return errRes;
	}

	/**
	 * 获取酒店房间预订信息（801）
	 * 
	 * @param uid
	 *            酒店uid，必须指定
	 * @param flag
	 *            uid类型，0-KUID，1-VUID，默认凯立德
	 * @param checkindate
	 *            入住时间 格式yy-mm-dd，如201401011
	 * @param checkoutdate
	 *            离开时间 格式yy-mm-dd，如201401011
	 * @param version
	 *            接口版本，从1开始
	 * @param userid
	 *            用户ID，若无则传null
	 * @param session
	 *            会话ID，若无则传null
	 * @param src
	 *            当version=2时，src参数生效；
	 *            当src=0或者为空时，只下发酒店所对应的第三方接入商srcid,和srcname（接入商列表）
	 *            当src>0,表示通过接入商去获取价格计划
	 *            ，如需要拉取携程(src=5)的价格计划,那么指定src参数为5，如果需要拉取多个接入商的数据可以置src=5,6
	 *            来实现拉取多个接入商
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-18 下午3:49:38
	 */
	public static CldSapReturn getRatePlan(String uid, int flag,
			String checkindate, String checkoutdate, int version,
			String userid, String session, String src) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("flag", flag);
		map.put("checkindate", checkindate);
		map.put("checkoutdate", checkoutdate);
		map.put("version", version);
		CldSapParser.putStringToMap(map, "userid", userid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapParser.putStringToMap(map, "src", src);
		CldSapReturn errRes = CldKBaseParse.getGetParmsNoEncode(map,
				CldSapUtil.getNaviSvrBD() + "get_rate_plan.php", null);
		return errRes;
	}

	/**
	 * 获取poi详情信息（802）
	 * 
	 * @param uid
	 *            Poi Uid 必须指定
	 * @param m
	 *            POI类型：101酒店详情，102餐饮详情，必须指定
	 * @param version
	 *            接口版本，从1开始
	 * @param userid
	 *            用户ID，若无则传null
	 * @param session
	 *            会话ID，若无则传null
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-18 下午3:59:41
	 */
	public static CldSapReturn getPoiDetails(String uid, int m, int version,
			String userid, String session) {
		String strDetailType = "";
		switch (m) {
		case 101:
			strDetailType = "get_hotel_detail.php";
			break;
		case 102:
			strDetailType = "get_cater_detail.php";
			break;
		default:
			strDetailType = "";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("version", version);
		CldSapParser.putStringToMap(map, "userid", userid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapReturn errRes = CldKBaseParse.getGetParmsNoEncode(map,
				CldSapUtil.getNaviSvrBD() + strDetailType, null);
		return errRes;
	}

	/**
	 * 获取某个点的周边信息（803）
	 * 
	 * @param kuid
	 *            凯立德POI UID
	 * @param flag
	 *            uid类型，0-KUID，1-VUID，默认KUID
	 * @param t
	 *            劵型码 1-团购劵 2-优惠劵
	 * @param version
	 *            接口版本，从1开始
	 * @param userid
	 *            用户ID，若无则传null
	 * @param session
	 *            会话ID，若无则传null
	 * @return int
	 * @author Zhouls
	 * @date 2015-1-5 下午5:44:53
	 */
	public static CldSapReturn getPoiGroupCoupon(String kuid, int flag, int t,
			int version, String userid, String session) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("kuid", kuid);
		map.put("flag", flag);
		map.put("t", t);
		map.put("version", version);
		CldSapParser.putStringToMap(map, "userid", userid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapReturn errRes = CldKBaseParse.getGetParmsNoEncode(map,
				CldSapUtil.getNaviSvrBD() + "get_poi_coupons.php", null);
		return errRes;
	}

	/**
	 * 获得某个点团购/优惠数量（804）
	 * 
	 * @param f
	 *            过滤串，多个短语用"-"分隔，UTF8 URL编码，若无则传空字符串
	 * @param city
	 *            城市ID，必须指定
	 * @param code
	 *            分类类型码，若无则传空字符串
	 * @param t
	 *            劵型码 1-团购劵 2-优惠劵
	 * @param x
	 *            中心点经度坐标
	 * @param y
	 *            中心点纬度坐标
	 * @param r
	 *            搜索半径，单位：米
	 * @param sno
	 *            起始序号，从1开始，默认为1
	 * @param num
	 *            请求的劵数量，每次最多取1000个
	 * @param sort
	 *            默认排序——SORT_DEFAULT，距离排序——SORT_DISTANCE（有中心点有效）
	 * @param version
	 *            接口版本，从1开始
	 * @param userid
	 *            用户ID，若无则传null
	 * @param session
	 *            会话ID，若无则传null
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-18 下午4:23:24
	 */
	public static CldSapReturn getGroupCouponCount(String f, String city,
			String code, int t, long x, long y, int r, int sno, int num,
			int sort, int version, String userid, String session) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("f", f);
		map.put("city", city);
		map.put("code", code);
		map.put("t", t);
		map.put("c", x + "," + y);
		map.put("r", r);
		map.put("sno", sno);
		map.put("num", num);
		map.put("sort", sort);
		map.put("version", version);
		CldSapParser.putStringToMap(map, "userid", userid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapReturn errRes = CldKBaseParse.getGetParmsNoEncode(map,
				CldSapUtil.getNaviSvrBD() + "search_coupons.php", null);
		return errRes;
	}

	/**
	 * 获得团购/优惠列表（805）
	 * 
	 * @param f
	 *            过滤串，多个短语用"-"分隔，UTF8 URL编码，若无则传空字符串
	 * @param city
	 *            城市ID（市一级的），必须指定
	 * @param code
	 *            分类类型码，若无则传空字符串
	 * @param t
	 *            劵型码 1-团购劵 2-优惠劵
	 * @param x
	 *            中心点经度坐标
	 * @param y
	 *            中心点纬度坐标
	 * @param r
	 *            搜索半径，最好传入10000以内，要不然范围太大影响效率。单位：米
	 * @param sno
	 *            起始序号，从1开始，默认为1
	 * @param num
	 *            请求的劵数量，每次最多取1000个
	 * @param sort
	 *            默认排序——SORT_DEFAULT，距离排序——SORT_DISTANCE（有中心点有效）
	 * @param version
	 *            接口版本，从1开始
	 * @param userid
	 *            用户ID，若无则传null
	 * @param session
	 *            会话ID，若无则传null
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-18 下午4:45:34
	 */
	public static CldSapReturn getGroupCouponList(String f, long city,
			String code, int t, long x, long y, int r, int sno, int num,
			int sort, int version, String userid, String session) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("f", f);
		map.put("city", city);
		map.put("code", code);
		map.put("t", t);
		map.put("c", x + "," + y);
		map.put("r", r);
		map.put("sno", sno);
		map.put("num", num);
		map.put("sort", sort);
		map.put("version", version);
		CldSapParser.putStringToMap(map, "userid", userid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapReturn errRes = CldKBaseParse.getGetParmsNoEncode(map,
				CldSapUtil.getNaviSvrBD() + "search_coupons.php", null);
		return errRes;
	}

	/**
	 * 获取团购/优惠详情(806)
	 * 
	 * @param uid
	 *            劵uid，必须指定
	 * @param t
	 *            1-团购，2-优惠，必须指定
	 * @param version
	 *            接口版本，从1开始
	 * @param userid
	 *            用户ID，若无则传null
	 * @param session
	 *            会话ID，若无则传null
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-18 下午5:37:51
	 */
	public static CldSapReturn getGroupCouponDetails(String uid, int t,
			int version, String userid, String session) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (t == 2) {
			map.put("cuid", uid);
		} else {
			map.put("guid", uid);
		}
		map.put("version", version);
		CldSapParser.putStringToMap(map, "userid", userid);
		CldSapParser.putStringToMap(map, "session", session);
		CldSapReturn errRes = CldKBaseParse.getGetParmsNoEncode(map,
				CldSapUtil.getNaviSvrBD() + "get_coupons_detail.php", null);
		return errRes;
	}
}
