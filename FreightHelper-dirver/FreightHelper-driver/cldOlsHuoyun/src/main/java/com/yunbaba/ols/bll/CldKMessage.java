/*
 * @Title CldKMessage.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.ols.bll;

import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;
import com.cld.device.CldPhoneNet;
import com.cld.log.CldLog;
import com.cld.setting.CldSetting;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.dal.CldDalKAccount;
import com.yunbaba.ols.dal.CldDalKCallNavi;
import com.yunbaba.ols.dal.CldDalKMessage;
import com.yunbaba.ols.sap.CldSapKMessage;
import com.yunbaba.ols.sap.bean.CldSapKMParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareAKeyCallParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.SharePoiParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareRouteParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareAKeyCallParm.CldAreaEgg;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtKeyCode;
import com.yunbaba.ols.sap.parse.CldKMessageParse.ProtCreMsg;
import com.yunbaba.ols.sap.parse.CldKMessageParse.ProtEggs;
import com.yunbaba.ols.sap.parse.CldKMessageParse.ProtMaxLength;
import com.yunbaba.ols.tools.CldErrUtil;
import com.yunbaba.ols.tools.CldSapNetUtil;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;



/**
 * 
 * 消息系统
 * 
 * @author Zhouls
 * @date 2015-4-8 下午4:10:14
 */
public class CldKMessage {

	/** 下拉彩蛋列表间隔2*60*60s(时间戳) */
	private final static long DROP_EGGS_UTC = 2 * 60 * 60;

	private static CldKMessage cldKMessage;

	/**
	 * Instantiates a new cld k message.
	 */
	private CldKMessage() {
	}

	/**
	 * Gets the single instance of CldKMessage.
	 * 
	 * @return single instance of CldKMessage
	 */
	public static CldKMessage getInstance() {
		if (cldKMessage == null)
			cldKMessage = new CldKMessage();
		return cldKMessage;
	}

	/**
	 * 初始化密钥
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 下午4:10:51
	 */
	public void initKey() {
		String cldKMKey = CldDalKMessage.getInstance().getCldKMKey();
		// 只要没有中断登录
		int slpTimer = 5;// 休眠周期
		while (TextUtils.isEmpty(cldKMKey)) {
			int sleepSc = 0;// 休眠秒数
			if (!CldPhoneNet.isNetConnected()) {
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
					// TODO: handle exception
				}
				continue;
			}
			CldSapReturn errRes = CldSapKMessage.initKeyCode();
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			ProtKeyCode protKeyCode = CldSapParser.parseJson(strRtn,
					ProtKeyCode.class, errRes);
			CldLog.d("ols", errRes.errCode + "_initKMKey");
			CldLog.d("ols", errRes.errMsg + "_initKMKey");
			CldErrUtil.handleErr(errRes);
			if (null != protKeyCode && errRes.errCode == 0
					&& !TextUtils.isEmpty(protKeyCode.getCode())) {
				cldKMKey = protKeyCode.getCode();
				CldDalKMessage.getInstance().setCldKMKey(cldKMKey);
			} else {
				try {
					while (sleepSc < slpTimer) {
						Thread.sleep(1000);
						sleepSc++;
					}
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
			if (slpTimer >= 30) {
				/**
				 * 超过30S以后固定30S周期
				 */
				slpTimer = 30;
			} else {
				/**
				 * 不足30S每次加5S
				 */
				slpTimer += 5;
			}
		}
		CldSapKMessage.keyCode = cldKMKey;
	}

	/**
	 * 分享消息
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
	 *            回传参数表：无
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
	 * @param uniqueids
	 *            "kuid,0"
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:11:20
	 */
	private CldSapReturn sendShareMsg(long kuid, long duid, int messagetype,
			int module, CldSapKMParm parm, String title, String endtime,
			int bussinessid, String session, int appid, int apptype,
			String uniqueids) {
		CldSapReturn errRes = CldSapKMessage.createCldMsg(kuid, duid,
				messagetype, module, parm, title, endtime, bussinessid,
				session, appid, apptype);
		String strRtn = CldSapNetUtil
				.sapPostMethod(errRes.url, errRes.jsonPost);
		ProtCreMsg protCreMsg = CldSapParser.parseJson(strRtn,
				ProtCreMsg.class, errRes);
		CldLog.d("ols", errRes.errCode + "_cremsg");
		CldLog.d("ols", errRes.errMsg + "_cremsg");
		CldErrUtil.handleErr(errRes);
		errCodeFix(errRes);
		if (null != protCreMsg) {
			if (errRes.errCode == 0) {
				parm.setMessageId(protCreMsg.getMessageid());
				errRes = CldSapKMessage.sendCldMsg(parm.getMessageId(), duid,
						uniqueids, messagetype, kuid, bussinessid, session,
						appid);
				strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldLog.d("ols", errRes.errCode + "_sendmsg");
				CldLog.d("ols", errRes.errMsg + "_sendmsg");
				CldErrUtil.handleErr(errRes);
				errCodeFix(errRes);
			}
		}
		return errRes;
	}

	/**
	 * 分享一个POI消息
	 * 
	 * @param poi
	 *            分享的点消息
	 * @param targetType
	 *            发给汽车还是别人（0：自己 ，1：别人）
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:11:38
	 */
	public CldSapReturn sendSharePoiMsg(SharePoiParm poi, int targetType) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			// 网络正常
			if (targetType == 0) {
				// 发送给汽车
				CldSapKMParm parm = new CldSapKMParm();
				poi.setPoi(poi.getPoi() + "," + poi.getName());
				parm.setPoiMsg(poi);
				String uniqueids = CldDalKAccount.getInstance().getKuid() + ","
						+ "0";
				errRes = sendShareMsg(CldDalKAccount.getInstance().getKuid(),
						CldDalKAccount.getInstance().getDuid(), 11, CldBllUtil
								.getInstance().getModule(), parm, null, null,
						CldBllUtil.getInstance().getBussinessid(),
						CldDalKAccount.getInstance().getSession(), CldBllUtil
								.getInstance().getAppid(), CldBllUtil
								.getInstance().getApptype(), uniqueids);
			}
			if (targetType == 1) {
				// 发送给他人
				CldSapKMParm parm = new CldSapKMParm();
				poi.setPoi(poi.getPoi() + "," + poi.getName());
				parm.setPoiMsg(poi);
				// 根据用户传来的kuid拼成分享目标字段
				String uniqueids = poi.getTarget() + "," + "0";
				errRes = sendShareMsg(CldDalKAccount.getInstance().getKuid(),
						CldDalKAccount.getInstance().getDuid(), 11, CldBllUtil
								.getInstance().getModule(), parm, null, null,
						CldBllUtil.getInstance().getBussinessid(),
						CldDalKAccount.getInstance().getSession(), CldBllUtil
								.getInstance().getAppid(), CldBllUtil
								.getInstance().getApptype(), uniqueids);
			}
		}
		return errRes;
	}

	/**
	 * 分享一个线路消息
	 * 
	 * @param route
	 *            分享的路线消息
	 * @param targetType
	 *            发给汽车还是别人（0：自己 ，1：别人）
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:12:01
	 */
	public CldSapReturn sendShareRouteMsg(ShareRouteParm route, int targetType) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			// 网络正常
			if (targetType == 0) {
				// 发送给汽车
				CldSapKMParm parm = new CldSapKMParm();
				parm.setRouteMsg(route);
				String uniqueids = CldDalKAccount.getInstance().getKuid() + ","
						+ "0";
				errRes = sendShareMsg(CldDalKAccount.getInstance().getKuid(),
						CldDalKAccount.getInstance().getDuid(), 12, CldBllUtil
								.getInstance().getModule(), parm, null, null,
						CldBllUtil.getInstance().getBussinessid(),
						CldDalKAccount.getInstance().getSession(), CldBllUtil
								.getInstance().getAppid(), CldBllUtil
								.getInstance().getApptype(), uniqueids);
			}
			if (targetType == 1) {
				// 发送给他人
				CldSapKMParm parm = new CldSapKMParm();
				parm.setRouteMsg(route);
				// 根据用户传来的kuid拼成分享目标字段
				String uniqueids = route.getTarget() + "," + "0";
				errRes = sendShareMsg(CldDalKAccount.getInstance().getKuid(),
						CldDalKAccount.getInstance().getDuid(), 12, CldBllUtil
								.getInstance().getModule(), parm, null, null,
						CldBllUtil.getInstance().getBussinessid(),
						CldDalKAccount.getInstance().getSession(), CldBllUtil
								.getInstance().getAppid(), CldBllUtil
								.getInstance().getApptype(), uniqueids);
			}
		}
		return errRes;
	}

	/**
	 * 接收消息
	 * 
	 * @param regionCode
	 *            区域编号
	 * @param x
	 *            当前坐标X
	 * @param y
	 *            当前坐标Y
	 * @param istmc
	 *            是否开启路况（0：开启；1：关闭），默认为0
	 * @param list
	 *            回传消息列表
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2014-12-18 下午12:48:25
	 */
	public CldSapReturn recShareMsg(int regionCode, long x, long y, int istmc,
			List<CldSapKMParm> list) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKMessage.recShareMsg(CldDalKAccount.getInstance()
					.getDuid(), CldBllUtil.getInstance().getApptype(),
					CldBllUtil.getInstance().getProver(), CldDalKAccount
							.getInstance().getKuid(), regionCode, x, y,
					CldBllUtil.getInstance().getBussinessid(), CldDalKAccount
							.getInstance().getSession(), CldBllUtil
							.getInstance().getAppid(), istmc);
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
			CldLog.d("ols", errRes.errCode + "_downloadMsg");
			CldLog.d("ols", errRes.errMsg + "_downloadMsg");
			CldErrUtil.handleErr(errRes);
			errCodeFix(errRes);
			if (errRes.errCode == 0) {
				CldSapKMessage.parseMessage(strRtn, list, CldDalKAccount
						.getInstance().getKuid(), CldBllUtil.getInstance()
						.getApptype());
			}
		}
		return errRes;
	}

	/**
	 * 终端接收最新历史消息(204)
	 * 
	 * @param messagetype
	 *            1:活动消息; 2:升级消息; 3:区域消息; 11:POI; 12:路径; 13:路书; 14:路况; 15:一键通
	 *            消息类型，支持多个，以","分隔
	 * @param list
	 *            回传消息列表
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2014-9-1 上午11:42:47
	 */
	public CldSapReturn recLastestMsgHistory(String messagetype,
			List<CldSapKMParm> list) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKMessage.recLastestMsgHitory(CldDalKAccount
					.getInstance().getDuid(), messagetype, CldDalKAccount
					.getInstance().getKuid(), CldBllUtil.getInstance()
					.getBussinessid(), CldDalKAccount.getInstance()
					.getSession(), CldBllUtil.getInstance().getAppid(),
					CldBllUtil.getInstance().getApptype());
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			ProtMaxLength protMaxLength = CldSapParser.parseJson(strRtn,
					ProtMaxLength.class, errRes);
			CldLog.d("ols", errRes.errCode + "_reclast");
			CldLog.d("ols", errRes.errMsg + "_reclast");
			CldErrUtil.handleErr(errRes);
			errCodeFix(errRes);
			if (null != protMaxLength) {
				if (errRes.errCode == 0) {
					CldSapKMessage.parseMessage(strRtn, list, CldDalKAccount
							.getInstance().getKuid(), CldBllUtil.getInstance()
							.getApptype());
					CldDalKMessage.getInstance().setMaxlength(
							protMaxLength.getMaxlength());
				}
			}
		}
		return errRes;
	}

	/**
	 * 终端接收历史新消息查询接口（向下滑动）
	 * 
	 * @param messagetype
	 *            1:活动消息; 2:升级消息; 3:区域消息; 11:POI; 12:路径; 13:路书; 14:路况; 15:一键通
	 *            消息类型，支持多个，以","分隔
	 * @param offset
	 *            页码，从0开始
	 * @param length
	 *            每次请求记录数
	 * @param lastid
	 *            最后一个的消息id
	 * @param lasttime
	 *            最后一个的消息时间
	 * @param list
	 *            回传消息列表
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:12:44
	 */
	public CldSapReturn recNewMsgHistory(String messagetype, int offset,
			int length, long lastid, long lasttime, List<CldSapKMParm> list) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			// 网络正常
			errRes = CldSapKMessage.recNewMsgHitory(CldDalKAccount
					.getInstance().getDuid(), messagetype, length, lastid,
					lasttime, CldDalKAccount.getInstance().getKuid(),
					CldBllUtil.getInstance().getBussinessid(), CldDalKAccount
							.getInstance().getSession(), CldBllUtil
							.getInstance().getAppid(), CldBllUtil.getInstance()
							.getApptype());
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
			CldLog.d("ols", errRes.errCode + "_recnew");
			CldLog.d("ols", errRes.errMsg + "_recnew");
			CldErrUtil.handleErr(errRes);
			errCodeFix(errRes);
			if (errRes.errCode == 0) {
				CldSapKMessage.parseMessage(strRtn, list, CldDalKAccount
						.getInstance().getKuid(), CldBllUtil.getInstance()
						.getApptype());
			}
		}
		return errRes;
	}

	/**
	 * 终端接收历史旧消息查询接口（向上滑动）
	 * 
	 * @param messagetype
	 *            1:活动消息; 2:升级消息; 3:区域消息; 11:POI; 12:路径; 13:路书; 14:路况; 15:一键通
	 *            消息类型，支持多个，以","分隔
	 * @param offset
	 *            页码，从0开始
	 * @param length
	 *            每次请求记录数
	 * @param lastid
	 *            最后一个的消息id
	 * @param lasttime
	 *            最后一个的消息时间
	 * @param list
	 *            回传消息列表
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:13:03
	 */
	public CldSapReturn recOldMsgHistory(String messagetype, int offset,
			int length, int lastid, long lasttime, List<CldSapKMParm> list) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			// 网络正常
			errRes = CldSapKMessage.recOldMsgHitory(CldDalKAccount
					.getInstance().getDuid(), messagetype, length, lastid,
					lasttime, CldDalKAccount.getInstance().getKuid(),
					CldBllUtil.getInstance().getBussinessid(), CldDalKAccount
							.getInstance().getSession(), CldBllUtil
							.getInstance().getAppid(), CldBllUtil.getInstance()
							.getApptype());
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
			CldLog.d("ols", errRes.errCode + "_recold");
			CldLog.d("ols", errRes.errMsg + "_recold");
			CldErrUtil.handleErr(errRes);
			errCodeFix(errRes);
			if (errRes.errCode == 0) {
				CldSapKMessage.parseMessage(strRtn, list, CldDalKAccount
						.getInstance().getKuid(), CldBllUtil.getInstance()
						.getApptype());
			}
		}
		return errRes;
	}

	/**
	 * 终端阅读消息状态更新接口(207)
	 * 
	 * @param list
	 *            Str="msgid,createtime,createtype"
	 *            List消息的消息msgid,createtime,createtype 组成的字符串列表
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:13:21
	 */
	public CldSapReturn upMsgReadStatus(List<String> list) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			if (list.size() > 0) {
				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 0; i < list.size(); i++) {
					stringBuilder.append(list.get(i));
					stringBuilder.append(";");
				}
				String messageids = stringBuilder.toString();
				errRes = CldSapKMessage.upMsgReadStatus(CldDalKAccount
						.getInstance().getDuid(), CldDalKAccount.getInstance()
						.getKuid(), CldDalKAccount.getInstance().getSession(),
						CldBllUtil.getInstance().getBussinessid(), CldBllUtil
								.getInstance().getAppid(), 2, messageids);
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldLog.d("ols", errRes.errCode + "_updateMsgRead");
				CldLog.d("ols", errRes.errMsg + "_updateMsgRead");
				CldErrUtil.handleErr(errRes);
				errCodeFix(errRes);
			} else {
				errRes.errCode = -2;
			}
		}
		return errRes;
	}

	/**
	 * 上报位置
	 * 
	 * @param mobile
	 *            手机号,如为空，则按kuid绑定的所有手机号都上报位置
	 * @param longitude
	 *            WGS84 经度，单位：度
	 * @param latitude
	 *            WGS84 纬度，单位：度
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:13:39
	 */
	public CldSapReturn upLocation(String mobile, double longitude,
			double latitude) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKMessage.upLocation(CldBllUtil.getInstance()
					.getApptype(), CldBllUtil.getInstance().getProver(),
					CldDalKAccount.getInstance().getKuid(), CldDalKAccount
							.getInstance().getSession(), CldBllUtil
							.getInstance().getAppid(), CldBllUtil.getInstance()
							.getBussinessid(), mobile, longitude, latitude);
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
			CldLog.d("ols", errRes.errCode + "_ppt_uploc");
			CldLog.d("ols", errRes.errMsg + "_ppt_uploc");
			CldErrUtil.handleErr(errRes);
			errCodeFix(errRes);
		}
		return errRes;
	}

	/**
	 * 获取一键通消息
	 * 
	 * @param mobile
	 *            手机号，如果传了手机号，则只会查询指定的手机号,否则轮循所有手机号
	 * @param longitude
	 *            WGS84 经度，单位：度
	 * @param latitude
	 *            WGS84 纬度，单位：度
	 * @param isLoop
	 *            是否轮询多个手机号
	 * @return int
	 * @author Zhouls
	 * @date 2014-11-14 上午9:26:09
	 */
	public CldSapReturn recPptMsg(String mobile, double longitude,
			double latitude, boolean isLoop) {
		CldSapReturn errRes = new CldSapReturn();
		// 先清除旧的消息
		List<ShareAKeyCallParm> list = CldDalKCallNavi.getInstance()
				.getMsgList();
		if (null != list) {
			list.clear();
		} else {
			return errRes;
		}
		for (int i = 0; i < 10; i++) { // 执行10次，间隔5秒，每次执行前判断网络
			if (CldPhoneNet.isNetConnected()) {
				errRes = CldSapKMessage.recPptMsg(CldBllUtil.getInstance()
						.getApptype(), CldBllUtil.getInstance().getProver(),
						CldDalKAccount.getInstance().getKuid(), CldDalKAccount
								.getInstance().getSession(), CldBllUtil
								.getInstance().getAppid(), CldBllUtil
								.getInstance().getBussinessid(), mobile,
						longitude, latitude, isLoop ? 1 : 0);
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				if (errRes.errCode == 0) {
					CldSapKMessage.parsePPtMessage(strRtn);
				}
				CldLog.d("ols", errRes.errCode + "_ppt_rec_ppt");
				CldLog.d("ols", errRes.errMsg + "_ppt_rec_ppt");
				CldErrUtil.handleErr(errRes);
				errCodeFix(errRes);
			}
			if (errRes.errCode == 403 || 0 == list.size()) { // 没有获取到消息，则延时5秒再执行一次
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				continue;
			}
			if (errRes.errCode == 0) {
				break;
			}
		}
		return errRes;
	}

	/**
	 * 获取彩蛋列表
	 * 
	 * @param isInit
	 *            是否是第一次拉取彩蛋列表，定时器中更新则传false
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:14:01
	 */
	public CldSapReturn getAreaList(boolean isInit) {
		CldSapReturn errRes = new CldSapReturn();
		if (isInit) {
			/**
			 * 开启导航第一次拉取彩蛋
			 */
			if (CldPhoneNet.isNetConnected()) {
				errRes = CldSapKMessage.getAreaList(0, null, null);
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				ProtEggs protEggs = CldSapParser.parseJson(strRtn,
						ProtEggs.class, errRes);
				CldLog.d("ols", errRes.errCode + "_ppt_rec_egg");
				CldLog.d("ols", errRes.errMsg + "_ppt_rec_egg");
				CldErrUtil.handleErr(errRes);
				errCodeFix(errRes);
				if (null != protEggs) {
					if (errRes.errCode == 0) {
						/**
						 * 更新成功保存更新时间戳
						 */
						CldSetting.put("ols_egg_utc", CldKAccount.getInstance()
								.getSvrTime());
						List<CldAreaEgg> list = new ArrayList<CldAreaEgg>();
						protEggs.protParse(list);
						CldDalKMessage.getInstance().setListEggs(list);
					}
				}
			}
		} else {
			/**
			 * 定时器30s判断是否更新
			 */
			/** 上次获取彩蛋列表的时间戳 */
			long lastEggUtc = CldSetting.getLong("ols_egg_utc");
			/** 当前时间戳和上次获取彩蛋列表时间戳做差 >2h才更新 */
			long difUtc = CldKAccount.getInstance().getSvrTime() - lastEggUtc
					- DROP_EGGS_UTC;
			if (difUtc >= 0) {
				if (CldPhoneNet.isNetConnected()) {
					errRes = CldSapKMessage.getAreaList(0, null, null);
					String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
					ProtEggs protEggs = CldSapParser.parseJson(strRtn,
							ProtEggs.class, errRes);
					CldLog.d("ols", errRes.errCode + "_ppt_rec_egg");
					CldLog.d("ols", errRes.errMsg + "_ppt_rec_egg");
					CldErrUtil.handleErr(errRes);
					errCodeFix(errRes);
					if (null != protEggs) {
						if (errRes.errCode == 0) {
							/**
							 * 更新成功保存更新时间戳
							 */
							CldSetting.put("ols_egg_utc", CldKAccount
									.getInstance().getSvrTime());
							List<CldAreaEgg> list = new ArrayList<CldAreaEgg>();
							protEggs.protParse(list);
							CldDalKMessage.getInstance().setListEggs(list);
						}
					}
				}
			} else {
				CldLog.d("ols", "the DifUTC < 2H:" + (difUtc + DROP_EGGS_UTC));
			}
		}
		return errRes;
	}

	/**
	 * 判断是否踩到蛋
	 * 
	 * @param x
	 *            当前凯立德坐标X
	 * @param y
	 *            当前凯立德坐标Y
	 * @return
	 * @return boolean
	 * @author Zhouls
	 * @date 2015-4-8 下午4:14:14
	 */
	public boolean isInEggsArea(long x, long y) {
		List<CldAreaEgg> list = CldDalKMessage.getInstance().getListEggs();
		boolean isIn = false;
		if (list.size() > 0) {
			/**
			 * 下拉到彩蛋
			 */
			for (int i = 0; i < list.size(); i++) {
				/**
				 * 遍历所有彩蛋
				 */
				CldAreaEgg egg = list.get(i);
				long nowUtc = CldKAccountAPI.getInstance().getSvrTime();
				/**
				 * 当当前时间在彩蛋时间范围内，并且坐标在坐标框内 才是踩到蛋
				 */
				if (nowUtc >= egg.getStarttime() && nowUtc <= egg.getEndtime()) {
					if (x >= egg.getMinx() && x <= egg.getMaxx()
							&& y >= egg.getMiny() && y <= egg.getMaxy()) {
						/**
						 * 如果踩到蛋，将彩蛋列表中的彩蛋删除
						 */
						CldLog.d("ols", "InEggsArea");
						CldDalKMessage.getInstance().getListEggs().remove(i);
						i--;
						isIn = true;
						/**
						 * 这里不添加break 是为了把这个坐标下满足的彩蛋都过滤掉， 因为一次满足，都会收走所有彩蛋
						 */
					}
				}
			}
		} else {
			/**
			 * 没下拉到彩蛋 也不是在彩蛋区域
			 */
		}
		return isIn;
	}

	/**
	 * 错误码处理
	 * 
	 * @param res
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-8 下午4:14:33
	 */
	public void errCodeFix(CldSapReturn res) {
		switch (res.errCode) {
		case 402: {
			/**
			 * 密钥错误
			 */
			CldDalKMessage.getInstance().setCldKMKey("");
			initKey();
		}
			break;
		case 500: {
			/**
			 * session失效自动登录一次刷新session
			 */
			CldKAccount.getInstance().sessionRelogin();
		}
			break;
		case 501:
			/**
			 * session失效
			 */
			if (res.session.equals(CldDalKAccount.getInstance().getSession())) {
				/**
				 * 当接口使用session和当前帐户里的session相同才挤下线
				 */
				if (!TextUtils.isEmpty(res.session)) {
					CldKAccountAPI.getInstance().sessionInvalid(501, 0);
				}
			}
			break;
		}
	}
}
