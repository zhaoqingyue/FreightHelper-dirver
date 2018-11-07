/*
 * @Title CldKMessageAPI.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.ols.api;

import java.util.ArrayList;
import java.util.List;
import com.yunbaba.ols.api.CldOlsInit.ICldOlsInitListener;
import com.yunbaba.ols.bll.CldKMessage;
import com.yunbaba.ols.dal.CldDalKMessage;
import com.yunbaba.ols.sap.bean.CldSapKMParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.SharePoiParm;
import com.yunbaba.ols.sap.bean.CldSapKMParm.ShareRouteParm;
import com.yunbaba.ols.tools.CldSapReturn;



/**
 * 消息相关模块，提供分享消息等功能
 * 
 * @author Zhouls
 * @date 2015-3-5 下午3:26:30
 */
public class CldKMessageAPI {

	/** 消息系统回调监听，初始化时设置一次 */
	private ICldKMessageListener listener;

	private static CldKMessageAPI cldKMessageAPI;

	private CldKMessageAPI() {

	}

	public static CldKMessageAPI getInstance() {
		if (null == cldKMessageAPI) {
			cldKMessageAPI = new CldKMessageAPI();
		}
		return cldKMessageAPI;
	}

	/**
	 * 初始化
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:26:47
	 */
	public void init() {

	}

	/**
	 * 反初始化
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:26:57
	 */
	public void uninit() {

	}

	/**
	 * 初始化密钥
	 * 
	 * @param listener
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:27:08
	 */
	public void initKey(final ICldOlsInitListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				CldKMessage.getInstance().initKey();
				if (null != listener) {
					listener.onInitReslut();
				}
			}
		}).start();
	}

	/**
	 * 设置消息系统回调
	 * 
	 * @param listener
	 *            消息系统回调监听
	 * @return
	 * @return int
	 * @author Zhouls
	 * @date 2015-3-5 下午3:27:18
	 */
	public int setCldKMessageListener(ICldKMessageListener listener) {
		if (null == this.listener) {
			this.listener = listener;
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 发送云分享POI消息
	 * 
	 * @param poi
	 *            分享POI消息
	 * @param targetType
	 *            发给汽车还是别人（0：自己 ，1：别人）
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:27:30
	 */
	public void sendSharePoiMsg(final SharePoiParm poi, final int targetType) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKMessage.getInstance()
						.sendSharePoiMsg(poi, targetType);
				if (null != listener) {
					listener.onSendPoiResult(errRes.errCode);
				}
			}
		}).start();

	}

	/**
	 * 发送云分享路线消息
	 * 
	 * @param route
	 *            分享的路线消息
	 * @param targetType
	 *            发给汽车还是别人（0：自己 ，1：别人）
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:27:54
	 */
	public void sendShareRouteMsg(final ShareRouteParm route,
			final int targetType) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldSapReturn errRes = CldKMessage.getInstance()
						.sendShareRouteMsg(route, targetType);
				if (null != listener) {
					listener.onSendRouteResult(errRes.errCode);
				}
			}
		}).start();
	}

	/**
	 * 接收最新消息历史
	 * 
	 * @param messagetype
	 *            1:活动消息; 2:升级消息; 3:区域消息; 11:POI; 12:路径; 13:路书; 14:路况; 15:一键通
	 *            消息类型，支持多个，以","分隔
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:28:11
	 */
	public void recLastestMsgHistory(final String messagetype) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<CldSapKMParm> list = new ArrayList<CldSapKMParm>();
				CldSapReturn errRes = CldKMessage.getInstance()
						.recLastestMsgHistory(messagetype, list);
				int maxlength = 0;
				if (errRes.errCode == 0) {
					maxlength = CldDalKMessage.getInstance().getMaxlength();
				}
				if (null != listener) {
					listener.onRecLastestMsgHistoryResult(errRes.errCode,
							maxlength, list, messagetype);
				}
			}
		}).start();
	}

	/**
	 * 接收历史新消息（向下刷新）
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
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:28:31
	 */
	public void recNewMsgHistory(final String messagetype, final int offset,
			final int length, final long lastid, final long lasttime) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<CldSapKMParm> list = new ArrayList<CldSapKMParm>();
				CldSapReturn errRes = CldKMessage.getInstance()
						.recNewMsgHistory(messagetype, offset, length, lastid,
								lasttime, list);
				if (null != listener) {
					listener.onRecNewMsgHistoryResult(errRes.errCode, list);
				}
			}
		}).start();
	}

	/**
	 * 接收历史旧消息查询接口（上拉刷新）
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
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:29:21
	 */
	public void recOldMsgHistory(final String messagetype, final int offset,
			final int length, final int lastid, final long lasttime) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<CldSapKMParm> list = new ArrayList<CldSapKMParm>();
				CldSapReturn errRes = CldKMessage.getInstance()
						.recOldMsgHistory(messagetype, offset, length, lastid,
								lasttime, list);
				if (null != listener) {
					listener.onRecOldMsgHistoryResult(errRes.errCode, list);
				}
			}
		}).start();
	}

	/**
	 * 更新消息阅读状态
	 * 
	 * @param list
	 *            Str="msgid,createtime,createtype"
	 *            List消息的消息msgid,createtime,createtype 组成的字符串列表
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:29:38
	 */
	public void upMsgReadStatus(final List<String> list) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				CldSapReturn errRes = CldKMessage.getInstance()
						.upMsgReadStatus(list);
				if (null != listener) {
					listener.onUpMsgReadStatusResult(errRes.errCode, list);
				}
			}
		}).start();
	}

	/**
	 * 下拉彩蛋列表
	 * 
	 * @param isInit
	 *            是否是第一次拉取彩蛋列表，定时器中更新则传false
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:29:52
	 */
	public void dropAreaEggs(final boolean isInit) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				CldKMessage.getInstance().getAreaList(isInit);
			}
		}).start();
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
	 * @date 2015-3-5 下午3:30:06
	 */
	public boolean isInEggsArea(long x, long y) {
		return CldKMessage.getInstance().isInEggsArea(x, y);
	}

	/**
	 * 接收彩蛋消息回调监听
	 * 
	 * @author Zhouls
	 * @date 2015-3-5 下午3:30:26
	 */
	public interface IRecEggMsg {

		/**
		 * 接收彩蛋消息回调
		 * 
		 * @param errCode
		 *            the err code
		 * @param list
		 *            接收到的消息列表
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:30:35
		 */
		public void onRecEggsMsg(int errCode, List<CldSapKMParm> list);
	}

	/**
	 * 接收彩蛋消息
	 * 
	 * @param regionCode
	 *            行政区编码
	 * @param x
	 *            当前坐标x
	 * @param y
	 *            当前坐标y
	 * @param eggListener
	 *            彩蛋消息回调
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-5 下午3:30:50
	 */
	public void recEggsMsg(final int regionCode, final long x, final long y,
			final IRecEggMsg eggListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<CldSapKMParm> list = new ArrayList<CldSapKMParm>();
				CldSapReturn errRes = CldKMessage.getInstance().recShareMsg(
						regionCode, x, y, -1, list);
				if (null != eggListener) {
					eggListener.onRecEggsMsg(errRes.errCode, list);
				}
			}
		}).start();
	}

	/**
	 * 消息系统回调监听
	 * 
	 * @author Zhouls
	 * @date 2015-3-5 下午3:31:24
	 */
	public static interface ICldKMessageListener {

		/**
		 * 发送云分享POI消息回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:33:10
		 */
		public void onSendPoiResult(int errCode);

		/**
		 * 发送云分享Route消息回调
		 * 
		 * @param errCode
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:33:24
		 */
		public void onSendRouteResult(int errCode);

		/**
		 * 接收最新消息历史回调
		 * 
		 * @param errCode
		 * @param maxlength
		 *            the maxlength
		 * @param list
		 *            消息列表
		 * @param Tag
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:33:32
		 */
		public void onRecLastestMsgHistoryResult(int errCode, int maxlength,
				List<CldSapKMParm> list, String Tag);

		/**
		 * 接收历史新消息回调（下拉刷新）
		 * 
		 * @param errCode
		 * @param list
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:34:18
		 */
		public void onRecNewMsgHistoryResult(int errCode,
				List<CldSapKMParm> list);

		/**
		 * 接收历史旧消息回调（上拉刷新）
		 * 
		 * @param errCode
		 * @param list
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:34:37
		 */
		public void onRecOldMsgHistoryResult(int errCode,
				List<CldSapKMParm> list);

		/**
		 * 消息阅读状态回传回调
		 * 
		 * @param errCode
		 * @param list
		 *            传入的拼接串
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-5 下午3:34:47
		 */
		public void onUpMsgReadStatusResult(int errCode, List<String> list);

	}
}
