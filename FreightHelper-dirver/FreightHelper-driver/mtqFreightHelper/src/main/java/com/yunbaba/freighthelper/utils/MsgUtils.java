package com.yunbaba.freighthelper.utils;

import android.text.TextUtils;

import com.cld.mapapi.search.geocode.GeoCodeResult;
import com.cld.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.cld.mapapi.search.geocode.ReverseGeoCodeResult;
import com.yunbaba.api.car.CarAPI;
import com.yunbaba.api.map.MapViewAPI;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.freighthelper.bean.msg.Filter.FilterId;
import com.yunbaba.freighthelper.bean.msg.MsgContent;
import com.yunbaba.freighthelper.bean.msg.MsgInfo;
import com.yunbaba.freighthelper.ui.adapter.AlarmMsgAdapter;
import com.yunbaba.freighthelper.ui.adapter.BusinessMsgAdapter;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.sap.bean.CldSapKMParm;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;

public class MsgUtils {

	public static boolean isHandledMsg(String businessCode) {
		int business = Integer.parseInt(businessCode);
		if (business == 1008 || business == 1010 || business == 1001 || business == 1099 || business == 1006
				|| business == 1004 || business == 2001 || business == 2002  || business == 1012) { 
			
			//|| business == 1016
			
			return true;
		}
		return false;
	}

	public static MsgInfo parm2MsgInfo(CldSapKMParm parm) {
		MsgInfo msgInfo = null;
		if (parm != null) {
			String content = parm.getOperateMsg().getContent();
			String businessCode = content.substring(0, 4);
			if (isHandledMsg(businessCode)) {

				if (businessCode.equalsIgnoreCase("1012")) {

					try {

						String[] sArray = content.split("#");
						if (sArray[1] != null) {
							if (!TaskOperator.getInstance().isExistTask(sArray[1])) {
								return null;
							}

						}
					} catch (Exception e) {
						return null;
					}

				}

				msgInfo = new MsgInfo();
				msgInfo.setMessageId(parm.getMessageId());
				msgInfo.setTitle(parm.getTitle());
				msgInfo.setMsgType(parm.getMsgType());
				msgInfo.setCreateuser(parm.getCreateuser());
				String stamp = TimeUtils.getTimestamp(parm.getCreatetime());
				msgInfo.setCreatetime(stamp);
				msgInfo.setReadMark(parm.getStatus());
				msgInfo.setContent(content);
				msgInfo.setBusinessCode(businessCode);
				msgInfo.setMsgContent(parseMsgContent(msgInfo));
			}
		}
		return msgInfo;
	}

	public static MsgInfo parseMsgInfo(MsgInfo parm) {
		MsgInfo msgInfo = null;
		if (parm != null) {
			String content = parm.getContent();
			String businessCode = content.substring(0, 4);
			if (isHandledMsg(businessCode)) {
				msgInfo = new MsgInfo();
				msgInfo.setMessageId(parm.getMessageId());
				msgInfo.setTitle(parm.getTitle());
				msgInfo.setMsgType(parm.getMsgType());
				msgInfo.setCreateuser(parm.getCreateuser());
				msgInfo.setCreatetime(parm.getCreatetime());
				msgInfo.setReadMark(parm.getReadMark());
				msgInfo.setContent(content);
				msgInfo.setBusinessCode(businessCode);
				msgInfo.setMsgContent(parseMsgContent(msgInfo));
			}
		}
		return msgInfo;
	}

	public static MsgContent parseMsgContent(MsgInfo msgInfo) {

		MsgContent msgContent = new MsgContent();
		if (msgInfo == null) {
			return msgContent;
		}

		try {

			msgInfo.setBusinessCode(msgInfo.getContent().substring(0, 4));
			String businessCode = msgInfo.getBusinessCode();
			msgContent.setMessageId(msgInfo.getMessageId());
			msgContent.setMsgType(msgInfo.getMsgType());
			String time = msgInfo.getCreatetime();
			msgContent.setCreatetime(time);
			if (!TextUtils.isEmpty(businessCode)) {
				int business = Integer.parseInt(businessCode);
				switch (business) {
				case 1008: {
					/**
					 * 加入车队： 1008#邀请码#有效截止UTC时间#企业ID#车队ID#物流企业#车队名称#lockcorpid#
					 */
					String[] sArray = msgInfo.getContent().split("#");
					msgContent.setBusinessCode(1008);
					msgContent.setLayout(BusinessMsgAdapter.MSG_BUSINESS_JOIN);
					msgContent.setFilterID(FilterId.FILTER_ID_10);
					msgContent.setInviteCode(sArray[1]);
					msgContent.setUtcTime(sArray[2]);
					msgContent.setCorpId(sArray[3]);
					msgContent.setGroupId(sArray[4]);
					msgContent.setCorpName(sArray[5]);
					msgContent.setGroupName(sArray[6]);
					msgContent.setLockcorpId(sArray[7]);
					/**
					 * 刷新通讯录-企业列表
					 */
					break;
				}
				case 1010: {
					/**
					 * 退出车队： 1010#踢出UTC时间#企业ID#车队ID#物流企业#车队名称
					 */
					String[] sArray = msgInfo.getContent().split("#");
					msgContent.setBusinessCode(1010);
					msgContent.setLayout(BusinessMsgAdapter.MSG_BUSINESS_QUIT);
					msgContent.setFilterID(FilterId.FILTER_ID_11);
					msgContent.setUtcTime(sArray[1]);
					msgContent.setCorpId(sArray[2]);
					msgContent.setGroupId(sArray[3]);
					msgContent.setCorpName(sArray[4]);
					msgContent.setGroupName(sArray[5]);
					/**
					 * 刷新通讯录-企业列表
					 */
					// MLog.e("MsgUtils", "corpid: " +
					// msgContent.getCorpId());
					// MLog.e("MsgUtils", "corpname: " +
					// msgContent.getCorpName());
					CarAPI.getInstance().deleteCorp(msgContent.getCorpId());
					break;
				}
				case 1001: {
					/**
					 * 普通任务消息： 1001#配送任务ID#运货点数量#物流企业#标题信息#送货车辆#企业ID
					 */
					String[] sArray = msgInfo.getContent().split("#");
					msgContent.setBusinessCode(1001);
					msgContent.setLayout(BusinessMsgAdapter.MSG_BUSINESS_TASK_GENERAL);
					msgContent.setFilterID(FilterId.FILTER_ID_18);
					msgContent.setTaskId(sArray[1]);
					msgContent.setDeliveryPoints(sArray[2]);
					msgContent.setCorpName(sArray[3]);
					msgContent.setContent(sArray[4]);
					msgContent.setDeliveryVehicle(sArray[5]);
					msgContent.setCorpId(sArray[6]);
					break;
				}
				case 1099: {
					/**
					 * 提醒任务消息(自定义)： 1099#配送任务ID#物流企业#企业ID#消息内容#筛选ID
					 */
					String[] sArray = msgInfo.getContent().split("#");
					msgContent.setBusinessCode(1099);
					msgContent.setLayout(BusinessMsgAdapter.MSG_BUSINESS_TASK_REMIND);
					/**
					 * FilterId.FILTER_ID_14 FilterId.FILTER_ID_15
					 * FilterId.FILTER_ID_16 FilterId.FILTER_ID_17
					 */
					msgContent.setTaskId(sArray[1]);
					msgContent.setCorpName(sArray[2]);
					msgContent.setCorpId(sArray[3]);
					msgContent.setContent(sArray[4]);
					int filterid = Integer.parseInt(sArray[5]);
					msgContent.setFilterID(filterid);
					break;
				}
				case 1006: {
					/**
					 * K码调度消息： 1006#凯立德K码#物流企业#消息内容#POI名称#POI地址
					 */
					String[] sArray = msgInfo.getContent().split("#");
					msgContent.setBusinessCode(1006);
					msgContent.setLayout(BusinessMsgAdapter.MSG_BUSINESS_SCHEDULE_KCODE);
					msgContent.setFilterID(FilterId.FILTER_ID_12);
					msgContent.setKCode(sArray[1]);
					msgContent.setCorpName(sArray[2]);
					msgContent.setContent(sArray[3]);
					msgContent.setPoiName(sArray[4]);
					msgContent.setPoiAddress(sArray[5]);
					break;
				}
				case 1004: {
					/**
					 * 普通调度消息： 1004#物流企业#信息内容
					 */
					String[] sArray = msgInfo.getContent().split("#");
					msgContent.setBusinessCode(1004);
					msgContent.setLayout(BusinessMsgAdapter.MSG_BUSINESS_SCHEDULE_GENERAL);
					msgContent.setFilterID(FilterId.FILTER_ID_13);
					msgContent.setCorpName(sArray[1]);
					msgContent.setContent(sArray[2]);
					break;
				}
				case 1016: {
					/**
					 * 运单撤回消息
					 */
					String[] sArray = msgInfo.getContent().split("#");
					msgContent.setBusinessCode(1016);
					msgContent.setLayout(BusinessMsgAdapter.MSG_BUSINESS_CANCEL_ORDER);
					msgContent.setFilterID(FilterId.FILTER_ID_1802);
					msgContent.setCorpName(sArray[2]);
					msgContent.setCorpId(sArray[1]);
					msgContent.setCuOrderid(sArray[4]);
					msgContent.setContent(msgInfo.getContent());
					break;
				}
				case 1012: {
					/**
					 * 任务撤回消息
					 */
					String[] sArray = msgInfo.getContent().split("#");
					msgContent.setBusinessCode(1012);
					msgContent.setLayout(BusinessMsgAdapter.MSG_BUSINESS_CANCEL_TASK);
					msgContent.setFilterID(FilterId.FILTER_ID_1803);
					msgContent.setTaskId(sArray[1]);
					msgContent.setCorpName(sArray[3]);

					msgContent.setContent(msgInfo.getContent());

					if (sArray[1] != null) {
						MtqDeliTask tmptask = TaskOperator.getInstance().getMtqDeliTaskFromDB(sArray[1]);
						msgContent.setCorpId(TimestampTool.DateToString(new Date(tmptask.departtime * 1000)));
					}

					break;
				}
				case 2001: {
					/**
					 * 语音调度消息： 2001#物流企业#信息内容
					 */
					String[] sArray = msgInfo.getContent().split("#");
					msgContent.setBusinessCode(2001);
					msgContent.setLayout(BusinessMsgAdapter.MSG_BUSINESS_SCHEDULE_SPEECH);
					msgContent.setFilterID(FilterId.FILTER_ID_1311);
					msgContent.setCorpName(sArray[2]);
					msgContent.setContent(msgInfo.getContent());
					break;
				}
				case 2002: {
					/**
					 * 报警消息：2002#车牌号码#报警ID#报警时间#x#y#报警名称#报警详情
					 */
					String[] sArray = msgInfo.getContent().split("#", 8);
					msgContent.setBusinessCode(2002);
					msgContent.setLayout(BusinessMsgAdapter.MSG_ALARM);
					msgContent.setBrand(sArray[1]);
					msgContent.setAlarmId(sArray[2]);
					/**
					 * 解析报警id
					 */
					int alarmid = Integer.parseInt(sArray[2]);
					if (isDemandAlarmMsg(alarmid)) {
						switch (alarmid) {
						/**
						 * 车辆安全
						 */
						case 211: {
							msgContent.setFilterID(FilterId.FILTER_ID_19);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_SAFETY);
							break;
						}
						case 215: {
							msgContent.setFilterID(FilterId.FILTER_ID_20);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_SAFETY);
							break;
						}
						case 233: {
							msgContent.setFilterID(FilterId.FILTER_ID_21);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_SAFETY);
							break;
						}
						case 103: {
							msgContent.setFilterID(FilterId.FILTER_ID_22);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_SAFETY);
							break;
						}
						case 216: {
							msgContent.setFilterID(FilterId.FILTER_ID_23);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_SAFETY);
							break;
						}
						/**
						 * 车辆异常
						 */
						case 217: {
							msgContent.setFilterID(FilterId.FILTER_ID_24);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_ABNORMAL);
							break;
						}
						case 218: {
							msgContent.setFilterID(FilterId.FILTER_ID_25);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_ABNORMAL);
							break;
						}
						case 219: {
							msgContent.setFilterID(FilterId.FILTER_ID_26);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_ABNORMAL);
							break;
						}
						case 220: {
							msgContent.setFilterID(FilterId.FILTER_ID_27);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_ABNORMAL);
							break;
						}
						case 221: {
							msgContent.setFilterID(FilterId.FILTER_ID_28);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_ABNORMAL);
							break;
						}
						case 222: {
							msgContent.setFilterID(FilterId.FILTER_ID_29);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_ABNORMAL);
							break;
						}
						case 230: {
							msgContent.setFilterID(FilterId.FILTER_ID_30);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_ABNORMAL);
							break;
						}
						case 224: {
							msgContent.setFilterID(FilterId.FILTER_ID_31);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_CAR_ABNORMAL);
							break;
						}
						/**
						 * 设备异常
						 */
						case 109: {
							msgContent.setFilterID(FilterId.FILTER_ID_32);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_DEVICE_ABNORMAL);
							break;
						}
						case 225: {
							msgContent.setFilterID(FilterId.FILTER_ID_33);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_DEVICE_ABNORMAL);
							break;
						}
						case 402: {
							msgContent.setFilterID(FilterId.FILTER_ID_34);
							msgContent.setAlarmType(AlarmMsgAdapter.ALARM_DEVICE_ABNORMAL);
							break;
						}
						default:
							break;
						}
						msgContent.setAlarmTime(sArray[3]);
						msgContent.setAlarmX(sArray[4]);
						msgContent.setAlarmY(sArray[5]);
						msgContent.setAlarmName(sArray[6]);
						msgContent.setContent(
								parseAlarmMsgContent(sArray[1], sArray[2], sArray[3], sArray[4], sArray[5], sArray[7]));
						// msgContent.setContent(sArray[7]);
					}
					break;
				}
				default:
					break;
				}
			}

		} catch (Exception e) {

		}
		return msgContent;
	}

	private static boolean isDemandAlarmMsg(int alarmid) {
		if (alarmid == 211 || alarmid == 215 || alarmid == 233 || alarmid == 103 || alarmid == 216 || alarmid == 217
				|| alarmid == 218 || alarmid == 219 || alarmid == 220 || alarmid == 221 || alarmid == 222
				|| alarmid == 230 || alarmid == 224 || alarmid == 109 || alarmid == 225 || alarmid == 402) {
			return true;
		}
		return false;
	}

	/**
	 * @Title: parseMsgContent
	 * @Description: 解析消息内容
	 * @param msgContent
	 * @return: HashMap<String,Object>
	 */
	public HashMap<String, Object> parseMsgContent(MsgContent msgContent) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if (msgContent != null) {
			switch (msgContent.getLayout()) {
			case BusinessMsgAdapter.MSG_BUSINESS_JOIN: {
				hashMap.put("businessCode", msgContent.getBusinessCode());
				hashMap.put("createtime", msgContent.getCreatetime());
				hashMap.put("inviteCode", msgContent.getInviteCode());
				hashMap.put("utcTime", msgContent.getUtcTime());
				hashMap.put("corpid", msgContent.getCorpId());
				hashMap.put("groupid", msgContent.getGroupId());
				hashMap.put("corpname", msgContent.getCorpName());
				hashMap.put("groupname", msgContent.getGroupName());
				hashMap.put("lockcorpid", msgContent.getLockcorpId());
				break;
			}
			case BusinessMsgAdapter.MSG_BUSINESS_QUIT: {
				hashMap.put("businessCode", msgContent.getBusinessCode());
				hashMap.put("createtime", msgContent.getCreatetime());
				hashMap.put("utcTime", msgContent.getUtcTime());
				hashMap.put("corpid", msgContent.getCorpId());
				hashMap.put("groupid", msgContent.getGroupId());
				hashMap.put("corpname", msgContent.getCorpName());
				hashMap.put("groupname", msgContent.getGroupName());
				break;
			}
			case BusinessMsgAdapter.MSG_BUSINESS_TASK_GENERAL: {
				hashMap.put("businessCode", msgContent.getBusinessCode());
				hashMap.put("createtime", msgContent.getCreatetime());
				hashMap.put("taskId", msgContent.getTaskId());
				hashMap.put("corpid", msgContent.getCorpId());
				hashMap.put("corpname", msgContent.getCorpName());
				hashMap.put("deliveryPoints", msgContent.getDeliveryPoints());
				hashMap.put("content", msgContent.getContent());
				hashMap.put("deliveryVehicle", msgContent.getDeliveryVehicle());
				break;
			}
			case BusinessMsgAdapter.MSG_BUSINESS_TASK_REMIND: {
				hashMap.put("businessCode", msgContent.getBusinessCode());
				hashMap.put("createtime", msgContent.getCreatetime());
				hashMap.put("taskId", msgContent.getTaskId());
				hashMap.put("corpid", msgContent.getCorpId());
				hashMap.put("corpname", msgContent.getCorpName());
				hashMap.put("content", msgContent.getContent());
				break;
			}
			case BusinessMsgAdapter.MSG_BUSINESS_SCHEDULE_KCODE: {
				hashMap.put("businessCode", msgContent.getBusinessCode());
				hashMap.put("createtime", msgContent.getCreatetime());
				hashMap.put("kcode", msgContent.getKCode());
				hashMap.put("corpname", msgContent.getCorpName());
				hashMap.put("content", msgContent.getContent());
				hashMap.put("poiname", msgContent.getPoiName());
				hashMap.put("poiaddress", msgContent.getPoiAddress());
				break;
			}
			case BusinessMsgAdapter.MSG_BUSINESS_SCHEDULE_GENERAL: {
				hashMap.put("businessCode", msgContent.getBusinessCode());
				hashMap.put("createtime", msgContent.getCreatetime());
				hashMap.put("corpname", msgContent.getCorpName());
				hashMap.put("content", msgContent.getContent());
				break;
			}
			case BusinessMsgAdapter.MSG_ALARM: {
				hashMap.put("businessCode", msgContent.getBusinessCode());
				hashMap.put("createtime", msgContent.getCreatetime());
				hashMap.put("brand", msgContent.getBrand());
				hashMap.put("alarmid", msgContent.getAlarmId());
				hashMap.put("alarmtime", msgContent.getAlarmTime());
				hashMap.put("alarmx", msgContent.getAlarmX());
				hashMap.put("alarmy", msgContent.getAlarmY());
				hashMap.put("alarmname", msgContent.getAlarmName());
				hashMap.put("content", msgContent.getContent());
				hashMap.put("alarmtype", msgContent.getAlarmType());
				break;
			}
			default:
				break;
			}
		}
		return hashMap;
	}

	private static String location = "";

	/**
	 * @Title: parseAlarmMsgContent
	 * @Description: 解析报警消息内容
	 * @param alarmid
	 * @param content
	 * @return: String
	 */
	public static String parseAlarmMsgContent(String barand, String alarmid, String alarmtime, String alarmx,
			String alarmy, String content) {
		String parseContent = "";
		int id = Integer.parseInt(alarmid);
		switch (id) {
		/********************** 车辆安全 **********************/
		case 211: {
			/**
			 * 碰撞 {"collisiondirect":String, //碰撞方位。由0：左、1：右、2：前、3：后组成，多个间以逗号分隔
			 * "speedbefore":Float, //刹车前车速（公里/时） "speedcollision":Float,
			 * //碰撞时车速（公里/时） "turndirect:Integer, //转弯方向，0：向左侧转弯；1：向右侧转弯
			 * "breakdistance":Integer //制动距离（米） }
			 * 
			 * 车辆XXXXXXX在2017-01-01 19:00:01发生碰撞，请注意安全！
			 */
			alarmtime = TimeUtils.stampToYMDHMS(alarmtime);
			parseContent = "车辆" + barand + "在" + alarmtime + "发生碰撞， 请注意安全!";
			break;
		}
		case 215: {
			/**
			 * 翻车 {"turnedoverdirect":String, //翻车方位 由0：左、1：右、2：底朝天组成，多个间以逗号分隔
			 * "speedbefore":Float, //刹车前车速（公里/时） "speedturnedover":Float,
			 * //翻车时车速（公里/时） "turndirect":Integer, //转弯方向，0：向左侧转弯；1：向右侧转弯
			 * "breakdistance":Integer //制动距离（米） }
			 * 
			 * 车辆XXXXXXX在2017-01-01 19:00:01发生翻车，请注意安全！
			 */
			alarmtime = TimeUtils.stampToYMDHMS(alarmtime);
			parseContent = "车辆" + barand + "在" + alarmtime + "发生翻车， 请注意安全!";
			break;
		}
		case 233: {
			/**
			 * OBD超速报警
			 * 
			 * {"status":Integer, //状态 0：报警 1：报警取消 "speed":Float, //报警时速度（公里/时）
			 * "duration":Integer, //行驶时长（秒） "distance":Integer //行驶距离（米） }
			 * 
			 * 车辆XXXXXXX在2017-01-01 19:00:01已超速，速度为120km/h，请注意安全！
			 */
			float speed = 0;
			try {
				JSONObject jsonObj = new JSONObject(content);
				if (jsonObj.has("speed")) {
					speed = (float) jsonObj.getDouble("speed");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			alarmtime = TimeUtils.stampToYMDHMS(alarmtime);
			parseContent = "车辆" + barand + "在" + alarmtime + "已超速，速度为" + speed + "km/h， 请注意安全!";
			break;
		}
		case 103: {
			/**
			 * 疲劳驾驶 {"status":Integer, //状态 0：“持续驾驶时间”超过“疲劳驾驶预设时间”
			 * "starttime":Integer, //持续驾驶开始计时时间（时间戳） "duration":Integer
			 * //报警时已经持续驾驶时间（秒） }
			 * 
			 * 你已持续行驶车辆XXXXXXX XX小时，请注意安全！
			 */
			int duration = 0;
			float hour = 0;
			String time = "0.0";
			try {
				JSONObject jsonObj = new JSONObject(content);
				if (jsonObj.has("duration")) {
					duration = jsonObj.getInt("duration");
					hour = (float) (duration * 1.0 / 3600);
					DecimalFormat df = new DecimalFormat(".0");
					time = df.format(hour);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			parseContent = "您已持续行驶车辆" + barand + " " + time + "小时， 请注意安全!";
			break;
		}
		case 216: {
			/**
			 * 异常震动 {"state":Integer, //状态 0：震动开始； 1：震动停止 "acceleration":Float
			 * //最大加速度（mg） }
			 * 
			 * 车辆XXXXXXX发生异常震动，请确保车辆安全！
			 */
			parseContent = "车辆" + barand + "发生异常震动， 请确保车辆安全!";
			break;
		}

		/********************** 车辆异常 **********************/
		case 217: {
			/**
			 * 车门异常 {"status":String //异常位置
			 * （由0：左前门；1：右前门；2：左后门；3：右后门；4：尾箱等组成，多个间以逗号分隔） }
			 * 
			 * 车辆XXXXXXX行驶中XX门（车厢门）未关，请注意安全！
			 */
			String status = "";
			String alarm = "";
			try {
				JSONObject jsonObj = new JSONObject(content);
				if (jsonObj.has("status")) {
					status = jsonObj.getString("status");
					if (status.length() > 1) {
						String[] sArray = status.split(",");
						int len = sArray.length;
						for (int i = 0; i < len; i++) {
							if (sArray[i].equals("0")) {
								alarm += "左前门";
							} else if (sArray[i].equals("1")) {
								alarm += "右前门";
							} else if (sArray[i].equals("2")) {
								alarm += "左后门";
							} else if (sArray[i].equals("3")) {
								alarm += "右后门";
							} else if (sArray[i].equals("4")) {
								alarm += "尾箱";
							}
							if (i < len - 1 && i > 0) {
								alarm += ",";
							}
						}
					} else if (status.length() == 1) {
						if (status.equals("0")) {
							alarm = "左前门";
						} else if (status.equals("1")) {
							alarm = "右前门";
						} else if (status.equals("2")) {
							alarm = "左后门";
						} else if (status.equals("3")) {
							alarm = "右后门";
						} else if (status.equals("4")) {
							alarm = "尾箱";
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			parseContent = "车辆" + barand + "行驶中" + alarm + "未关， 请确保车辆安全!";
			break;
		}
		case 218: {
			/**
			 * 胎压和手刹异常 {"status":String //异常位置
			 * （由0：左前轮；1：右前轮；2：左后轮；3：右后轮；4：手刹等组成，多个间以逗号分隔） }
			 * 
			 * 车辆XXXXXXX的XX轮异常，（手刹未放），请检查后安全行驶！
			 */
			String status = "";
			String alarm = "";
			try {
				JSONObject jsonObj = new JSONObject(content);
				if (jsonObj.has("status")) {
					status = jsonObj.getString("status");
					if (status.length() > 1) {
						String[] sArray = status.split(",");
						int len = sArray.length;
						for (int i = 0; i < len; i++) {
							if (sArray[i].equals("0")) {
								alarm += "左前轮异常";
							} else if (sArray[i].equals("1")) {
								alarm += "右前轮异常";
							} else if (sArray[i].equals("2")) {
								alarm += "左后轮异常";
							} else if (sArray[i].equals("3")) {
								alarm += "右后轮异常";
							} else if (sArray[i].equals("4")) {
								alarm += "手刹未放";
							}
							if (i < len - 1) {
								alarm += ",";
							}
						}
					} else if (status.length() == 1) {
						if (status.equals("0")) {
							alarm = "左前轮异常";
						} else if (status.equals("1")) {
							alarm = "右前轮异常";
						} else if (status.equals("2")) {
							alarm = "左后轮异常";
						} else if (status.equals("3")) {
							alarm = "右后轮异常";
						} else if (status.equals("4")) {
							alarm = "手刹未放";
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			parseContent = "车辆" + barand + "的" + alarm + "，请检查后安全行驶!";
			break;
		}
		case 219: {
			/**
			 * 水温报警 {"status":Integer, //状态 0：水温过低； 255：水温过高； 1：报警取消
			 * "temperature":Integer //报警时水温（度） }
			 * 
			 * 1、车辆XXXXXXX水温过高，请检查后安全行驶！ 2、车辆XXXXXXX水温过低，请检查后安全行驶！
			 */
			int status = 1;
			String alarm = "";
			try {
				JSONObject jsonObj = new JSONObject(content);
				if (jsonObj.has("status")) {
					status = jsonObj.getInt("status");
					if (status == 0) {
						alarm = "水温过低";
					} else if (status == 255) {
						alarm = "水温过高";
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			parseContent = "车辆" + barand + alarm + "，请检查后安全行驶！";
			break;
		}
		case 220: {
			/**
			 * 转速报警 {"status":Integer, //状态 0：报警 1：报警取消 "speed":Integer
			 * //报警时转速（转） }
			 * 
			 * 车辆XXXXXXX发动机转速异常，请检查后安全行驶！
			 */
			parseContent = "车辆" + barand + "发动机转速异常，请检查后安全行驶！";
			break;
		}
		case 221: {
			/**
			 * 电瓶电压报警 {"status":Integer, //状态 0：电压过低； 255：电压过高; 1:报警取消
			 * "voltage":Float //报警时电压（伏） }
			 * 
			 * 1、车辆XXXXXXX电瓶电压异常，电压过低，请检查后安全行驶！ 2、车辆XXXXXXX电瓶电压异常，电压过高，请检查后安全行驶！
			 */
			int status = 1;
			String alarm = "";
			try {
				JSONObject jsonObj = new JSONObject(content);
				if (jsonObj.has("status")) {
					status = jsonObj.getInt("status");
					if (status == 0) {
						alarm = "电压过低，";
					} else if (status == 255) {
						alarm = "电压过高，";
					} else {
						alarm = "报警取消，";
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			parseContent = "车辆" + barand + "电瓶电压异常，" + alarm + "请检查后安全行驶！";
			break;
		}
		case 222: {
			/**
			 * 车辆故障报警 {"status":Integer, //系统类型 0：发动机；1：变速箱；2：刹车；3：气囊；4：仪表板;
			 * 5：车身控制；6：空调 "errcount":Integer, //故障码个数 "errvalue":String
			 * //故障码，多个故障码间以逗号分隔 }
			 * 
			 * 1、车辆XXXXXXX发动机故障，请检查后安全行驶！ 2、车辆XXXXXXX变速箱故障，请检查后安全行驶！
			 * 3、车辆XXXXXXX刹车故障，请检查后安全行驶！ 4、车辆XXXXXXX气囊故障，请检查后安全行驶！
			 * 5、车辆XXXXXXX仪表板故障，请检查后安全行驶！ 6、车辆XXXXXXX车身控制故障，请检查后安全行驶！
			 * 7、车辆XXXXXXX空调异常故障，请检查后安全行驶！
			 */
			int status = 1;
			String alarm = "";
			try {
				JSONObject jsonObj = new JSONObject(content);
				if (jsonObj.has("status")) {
					status = jsonObj.getInt("status");
					if (status == 0) {
						alarm = "发动机";
					} else if (status == 1) {
						alarm = "变速箱";
					} else if (status == 2) {
						alarm = "刹车";
					} else if (status == 3) {
						alarm = "气囊";
					} else if (status == 4) {
						alarm = "仪表板";
					} else if (status == 5) {
						alarm = "车身控制";
					} else if (status == 6) {
						alarm = "空调异常";
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			parseContent = "车辆" + barand + alarm + "故障，请检查后安全行驶！";
			break;
		}
		case 230: {
			/**
			 * 漏油提醒
			 * 
			 * {"sensorid":Integer, //传感器ID "startoil":Float, //加油起点液位AD值
			 * "endoil":Float //加油终点液位AD值 }
			 * 
			 * 1、车辆XXXXXXX漏油，请检查后安全行驶！
			 */
			parseContent = "车辆" + barand + "漏油，请检查后安全行驶！";
			break;
		}
		case 224: {
			/**
			 * 拖吊报警 {"status":Integer //状态（0：拖吊开始; 1:拖吊结束）}
			 * 
			 * 1、XXXX年XX月XX日 HH:MM:SS，车辆XXXXXXX在 【地点】被拖吊，请注意车辆安全！ 2、XXXX年XX月XX日
			 * HH:MM:SS，车辆XXXXXXX被拖吊到 【地点】，请注意车辆安全！
			 */

			int status = 1;
			String alarm = "地图上的点";
			long x = Long.parseLong(alarmx);
			long y = Long.parseLong(alarmy);
			MapViewAPI.getInstance().getGeoCodeResult(x, y, new OnGetGeoCoderResultListener() {

				@Override
				public void onGetGeoCodeResult(GeoCodeResult arg0) {

				}

				@Override
				public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
					if (arg0 != null) {
						location = arg0.addressDetail.street + arg0.addressDetail.streetNumber;
					} else {
						location = "地图上的点";
					}
				}
			});

			try {
				JSONObject jsonObj = new JSONObject(content);
				if (jsonObj.has("status")) {
					status = jsonObj.getInt("status");
					if (status == 0) {
						alarm = "在【" + location + "】被拖吊，请注意车辆安全！";
					} else if (status == 1) {
						alarm = "被拖吊到 【" + location + "】，请注意车辆安全！";
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			alarmtime = TimeUtils.stampToYMDHMS1(alarmtime);
			parseContent = alarmtime + ", 车辆" + barand + alarm;
			break;
		}
		/********************** 设备异常 **********************/
		case 109: {
			/**
			 * 断电提醒 {"status":Integer//状态（0为断电；1为恢复供电）}
			 * 
			 * 1、车辆XXXXXXX的设备OBD终端目前不能从车辆取电，靠终端电池供电，请检查设备。
			 * 2、车辆XXXXXXX的设备OBD终端已恢复供电。
			 */
			int status = 1;
			String alarm = "";
			try {
				JSONObject jsonObj = new JSONObject(content);
				if (jsonObj.has("status")) {
					status = jsonObj.getInt("status");
					if (status == 0) {
						alarm = "的设备OBD终端目前不能从车辆取电，靠终端电池供电，请检查设备。";
					} else if (status == 1) {
						alarm = "的设备OBD终端已恢复供电。";
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			parseContent = "车辆" + barand + alarm;
			break;
		}
		case 225: {
			/**
			 * 终端异常 {"status":Integer //状态
			 * （0：外置GPS天线数据中断报警；1:外置GPS天线数据恢复；2：终端休眠异常；3：终端休眠异常报警解除） }
			 * 
			 * 1、车辆XXXXXXX的设备OBD终端外置GPS天线数据中断，请检查设备。
			 * 2、车辆XXXXXXX的设备OBD终端外置GPS天线数据已恢复。 3、车辆XXXXXXX的设备OBD终端休眠异常，请检查设备。
			 * 4、车辆XXXXXXX的设备OBD终端休眠异常报警已解除。
			 */
			int status = 1;
			String alarm = "";
			try {
				JSONObject jsonObj = new JSONObject(content);
				if (jsonObj.has("status")) {
					status = jsonObj.getInt("status");
					if (status == 0) {
						alarm = "的设备OBD终端外置GPS天线数据中断，请检查设备。";
					} else if (status == 1) {
						alarm = "的设备OBD终端外置GPS天线数据已恢复。";
					} else if (status == 2) {
						alarm = "的设备OBD终端休眠异常，请检查设备。";
					} else if (status == 3) {
						alarm = "的设备OBD终端休眠异常报警已解除。";
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			parseContent = "车辆" + barand + alarm;
			break;
		}
		case 402: {
			/**
			 * SIM卡状态提醒 {"duid":Integer, //设备DUID "type":Integer,
			 * //硬件类型（1为JTT808，2为OBD007，3为TPND） "name":String, //硬件名称
			 * "mcuid":String, //终端硬件编号 "duedate":String //到期日期（YYYY-MM-DD） }
			 * 
			 * 1、车辆XXXXXXX的设备OBD终端的SIM卡在2012-01-01过期，请及时续费。
			 * 2、车辆XXXXXXX的设备OBD终端的SIM卡在2012-01-01已过期，请及时续费。
			 */
			String duedate = "";
			String alarm = "";
			try {
				JSONObject jsonObj = new JSONObject(content);
				if (jsonObj.has("duedate")) {
					duedate = jsonObj.getString("duedate");
					if (!TimeUtils.isOverdue(duedate)) {
						alarm = "的设备OBD终端的SIM卡在" + duedate + "过期，请及时续费。";
					} else {
						alarm = "的设备OBD终端的SIM卡在" + duedate + "已过期，请及时续费。";
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			parseContent = "车辆" + barand + alarm;
			break;
		}
		default:
			break;
		}
		return parseContent;
	}

	public static MsgInfo parm2MsgInfoForTask(CldSapKMParm parm) {

		MsgInfo msgInfo = null;
		if (parm != null) {
			String content = parm.getOperateMsg().getContent();
			String businessCode = content.substring(0, 4);
			int business = Integer.parseInt(businessCode);
			if (business == 1001 || business == 1011 || business == 1012 || business == 1014 || business == 2001) {
				msgInfo = new MsgInfo();
				msgInfo.setMessageId(parm.getMessageId());
				msgInfo.setTitle(parm.getTitle());
				msgInfo.setMsgType(parm.getMsgType());
				msgInfo.setCreateuser(parm.getCreateuser());
				String stamp = TimeUtils.getTimestamp(parm.getCreatetime());
				msgInfo.setCreatetime(stamp);
				msgInfo.setReadMark(parm.getStatus());
				msgInfo.setContent(content);
				msgInfo.setBusinessCode(businessCode);
				// msgInfo.setMsgContent(parseMsgContent(msgInfo));
			}
		}
		return msgInfo;
	}

	public static MsgInfo parm2MsgInfoForCarInfo(CldSapKMParm parm) {

		MsgInfo msgInfo = null;
		if (parm != null) {
			String content = parm.getOperateMsg().getContent();
			String businessCode = content.substring(0, 4);
			int business = Integer.parseInt(businessCode);
			if (business == 2002) {
				msgInfo = new MsgInfo();
				msgInfo.setMessageId(parm.getMessageId());
				msgInfo.setTitle(parm.getTitle());
				msgInfo.setMsgType(parm.getMsgType());
				msgInfo.setCreateuser(parm.getCreateuser());
				String stamp = TimeUtils.getTimestamp(parm.getCreatetime());
				msgInfo.setCreatetime(stamp);
				msgInfo.setReadMark(parm.getStatus());
				msgInfo.setContent(content);
				msgInfo.setBusinessCode(businessCode);
				msgInfo.setMsgContent(parseMsgContent(msgInfo));
			}
		}
		return msgInfo;
	}
}
