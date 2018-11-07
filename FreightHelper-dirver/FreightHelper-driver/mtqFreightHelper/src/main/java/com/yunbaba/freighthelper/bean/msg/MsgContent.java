package com.yunbaba.freighthelper.bean.msg;

import android.os.Parcel;
import android.os.Parcelable;

public class MsgContent implements Parcelable {
	/**
	 * 公共字段
	 */
	private long messageId;         // 消息Id
	private int businessCode;       // 业务编码
	private int msgType;            // 消息类型
	private int layout;             // 布局类型
	private String createtime;      // 构造时间
	private String content;         // 信息
	private int filterid;           // 筛选ID
	
	/**
	 * 企业消息
	 */
	private String inviteCode;      // 邀请码
	private String utcTime;         // UTC时间
	private String corpid;          // 企业ID(如果是"加入车队"消息，1-已加入；2-已拒绝；否则未加入)
	private String corpname;        // 物流企业
	private String groupid;         // 车队ID
	private String groupname;       // 车队名称
	private String lockcorpid;      // 锁定企业ID

	private String taskId;          // 配送任务ID
	private String deliveryPoints;  // 送货点
	private String deliveryVehicle; // 送货车辆
	private String kcode;           // K码
	private String poiname;         // POI名称
	private String poiAddress;      // POI地址
	private String cuOrderid;   //客户单号
	
	public String getCuOrderid() {
		return cuOrderid;
	}

	public void setCuOrderid(String cuOrderid) {
		this.cuOrderid = cuOrderid;
	}

	/**
	 * 报警消息
	 */
	private String brand;     // 车牌号码
	private String alarmid;   // 报警ID
	private String alarmtime; // 报警时间
	private String alarmx;    // x
	private String alarmy;    // y
	private String alarmname; // 报警名称
	private int alarmtype;    // 报警类型
	
	public MsgContent() {
		messageId = -1;
		businessCode = -1;
		msgType = -1;
		layout = -1;
		createtime = "";
		inviteCode = "";
		utcTime = "";
		corpid = "";
		corpname = "";
		groupid = "";
		groupname = "";
		lockcorpid = "";
		taskId = "";
		deliveryPoints = "";
		content = "";
		deliveryVehicle = "";
		kcode = "";
		poiname = "";
		poiAddress = "";
		brand = "";
		alarmid = "";
		alarmtime = "";
		alarmx = "";
		alarmy = "";
		alarmname = "";
		alarmtype = -1;
		filterid = -1;
		cuOrderid= "";
	}
	
	public MsgContent(MsgContent msgContent) {
		messageId = msgContent.messageId;
		businessCode = msgContent.businessCode;
		msgType = msgContent.msgType;
		layout = msgContent.layout;
		createtime = msgContent.createtime;
		inviteCode = msgContent.inviteCode;
		utcTime = msgContent.utcTime;
		corpid = msgContent.corpid;
		corpname = msgContent.corpname;
		groupid = msgContent.groupid;
		groupname = msgContent.groupname;
		lockcorpid = msgContent.lockcorpid;
		taskId = msgContent.taskId;
		deliveryPoints = msgContent.deliveryPoints;
		content = msgContent.content;
		deliveryVehicle = msgContent.deliveryVehicle;
		kcode = msgContent.kcode;
		poiname = msgContent.poiname;
		poiAddress = msgContent.poiAddress;
		brand = msgContent.brand;
		alarmid = msgContent.alarmid;
		alarmtime = msgContent.alarmtime;
		alarmx = msgContent.alarmx;
		alarmy = msgContent.alarmy;
		alarmname = msgContent.alarmname;
		alarmtype = msgContent.alarmtype;
		cuOrderid = msgContent.cuOrderid;
	}
	
	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	public void setBusinessCode(int businessCode) {
		this.businessCode = businessCode;
	}
	
	public int getBusinessCode() {
		return businessCode;
	}
	
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	
	public int getMsgType() {
		return msgType;
	}
	
	public void setLayout(int layout) {
		this.layout = layout;
	}
	
	public int getLayout() {
		return layout;
	}
	
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	
	public String getCreatetime() {
		return createtime;
	}
	
	public int getFilterID() {
		return filterid;
	}

	public void setFilterID(int filterid) {
		this.filterid = filterid;
	}
	
	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}
	
	public String getInviteCode() {
		return inviteCode;
	}
	
	public void setUtcTime(String utcTime) {
		this.utcTime = utcTime;
	}
	
	public String getUtcTime() {
		return utcTime;
	}
	
	public void setCorpId(String corpid) {
		this.corpid = corpid;
	}
	
	public String getCorpId() {
		return corpid;
	}
	
	public void setCorpName(String corpname) {
		this.corpname = corpname;
	}
	
	public String getCorpName() {
		return corpname;
	}
	
	public void setGroupId(String groupid) {
		this.groupid = groupid;
	}
	
	public String getGroupId() {
		return groupid;
	}
	
	public void setGroupName(String groupname) {
		this.groupname = groupname;
	}
	
	public String getGroupName() {
		return groupname;
	}
	
	public void setLockcorpId(String lockcorpid) {
		this.lockcorpid = lockcorpid;
	}
	
	public String getLockcorpId() {
		return lockcorpid;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getTaskId() {
		return taskId;
	}
	
	public void setDeliveryPoints(String deliveryPoints) {
		this.deliveryPoints = deliveryPoints;
	}
	
	public String getDeliveryPoints() {
		return deliveryPoints;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setDeliveryVehicle(String deliveryVehicle) {
		this.deliveryVehicle = deliveryVehicle;
	}
	
	public String getDeliveryVehicle() {
		return deliveryVehicle;
	}
	
	public void setKCode(String kcode) {
		this.kcode = kcode;
	}
	
	public String getKCode() {
		return kcode;
	}
	
	public void setPoiName(String poiname) {
		this.poiname = poiname;
	}
	
	public String getPoiName() {
		return poiname;
	}
	
	public void setPoiAddress(String poiAddress) {
		this.poiAddress = poiAddress;
	}
	
	public String getPoiAddress() {
		return poiAddress;
	}
	
	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	public String getBrand() {
		return brand;
	}
	
	public void setAlarmId(String alarmid) {
		this.alarmid = alarmid;
	}
	
	public String getAlarmId() {
		return alarmid;
	}
	
	
	public void setAlarmTime(String alarmtime) {
		this.alarmtime = alarmtime;
	}
	
	public String getAlarmTime() {
		return alarmtime;
	}
	
	public void setAlarmX(String alarmx) {
		this.alarmx = alarmx;
	}
	
	public String getAlarmX() {
		return alarmx;
	}
	
	public void setAlarmY(String alarmy) {
		this.alarmy = alarmy;
	}
	
	public String getAlarmY() {
		return alarmy;
	}
	
	public void setAlarmName(String alarmname) {
		this.alarmname = alarmname;
	}
	
	public String getAlarmName() {
		return alarmname;
	}
	
	public void setAlarmType(int alarmtype) {
		this.alarmtype = alarmtype;
	}
	
	public int getAlarmType() {
		return alarmtype;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(messageId);
		out.writeInt(businessCode);
		out.writeInt(msgType);
		out.writeInt(layout);
		out.writeString(createtime);
		out.writeString(inviteCode);
		out.writeString(utcTime);
		out.writeString(corpid);
		out.writeString(corpname);
		out.writeString(groupid);
		out.writeString(groupname);
		out.writeString(lockcorpid);
		out.writeString(taskId);
		out.writeString(deliveryPoints);
		out.writeString(content);
		out.writeString(deliveryVehicle);
		out.writeString(kcode);
		out.writeString(poiname);
		out.writeString(poiAddress);
		out.writeString(brand);
		out.writeString(alarmid);
		out.writeString(alarmtime);
		out.writeString(alarmx);
		out.writeString(alarmy);
		out.writeString(alarmname);
		out.writeInt(alarmtype);
		out.writeInt(filterid);
		out.writeString(cuOrderid);
	}
	
	public static final Parcelable.Creator<MsgContent> CREATOR = new Parcelable.Creator<MsgContent>() {
		
		public MsgContent createFromParcel(Parcel in) {
			return new MsgContent(in);
		}

		public MsgContent[] newArray(int size) {
			return new MsgContent[size];
		}
	};

	private MsgContent(Parcel in) {
		messageId = in.readLong();
		businessCode = in.readInt();
		msgType = in.readInt();
		layout = in.readInt();
		createtime = in.readString();
		inviteCode = in.readString();
		utcTime = in.readString();
		corpid = in.readString();
		corpname = in.readString();
		groupid = in.readString();
		groupname = in.readString();
		lockcorpid = in.readString();
		taskId = in.readString();
		deliveryPoints = in.readString();
		content = in.readString();
		deliveryVehicle = in.readString();
		kcode = in.readString();
		poiname = in.readString();
		poiAddress = in.readString();
		brand = in.readString();
		alarmid = in.readString();
		alarmtime = in.readString();
		alarmx = in.readString();
		alarmy = in.readString();
		alarmname = in.readString();
		alarmtype = in.readInt();
		filterid = in.readInt();
		cuOrderid = in.readString();
	}
}
