package com.yunbaba.freighthelper.bean.msg;

import android.os.Parcel;
import android.os.Parcelable;

public class MsgInfo implements Parcelable {
	
	private long messageId;        // 消息Id
	private String title;          // 标题
	private int msgType;           // 消息类型1:活动消息;2:升级消息;
	private String createuser;     // 构造者名称
	private String createtime;     // 构造时间
	private String content;        // 文本内容
	private int readmark;          // 状态（1：构造 ；2：已下载；3：已阅读）
	private String businessCode;   // 业务编码 (自定义)
	private MsgContent msgContent;
	
	public MsgInfo() {
		messageId = -1;
		title = "";
		msgType = -1;
		createuser = "";
		createtime = "";
		content = "";
		readmark = 0;
		businessCode = "";
		msgContent = new MsgContent();
	}
	
	public MsgInfo(MsgInfo msgInfo) {
		messageId = msgInfo.messageId;
		title = msgInfo.title;
		msgType = msgInfo.msgType;
		createuser = msgInfo.createuser;
		createtime = msgInfo.createtime;
		content = msgInfo.content;
		readmark = msgInfo.readmark;
		businessCode = msgInfo.businessCode;
		msgContent = msgInfo.msgContent;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(messageId);
		out.writeString(title);
		out.writeInt(msgType);
		out.writeString(createuser);
		out.writeString(createtime);
		out.writeString(content);
		out.writeInt(readmark);
		out.writeString(businessCode);
		out.writeParcelable(msgContent, 0);
	}
	
	public static final Parcelable.Creator<MsgInfo> CREATOR = new Parcelable.Creator<MsgInfo>() {
		
		public MsgInfo createFromParcel(Parcel in) {
			return new MsgInfo(in);
		}

		public MsgInfo[] newArray(int size) {
			return new MsgInfo[size];
		}
	};

	private MsgInfo(Parcel in) {
		messageId = in.readLong();
		title = in.readString();
		msgType = in.readInt();
		createuser = in.readString();
		createtime = in.readString();
		content = in.readString();
		readmark = in.readInt();
		businessCode = in.readString();
		msgContent = in.readParcelable(MsgContent.class.getClassLoader());
	}
	
	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	
	public String getCreateuser() {
		return createuser;
	}

	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}
	
	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
        this.createtime = createtime;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public int getReadMark() {
		return readmark;
	}

	public void setReadMark(int readmark) {
		this.readmark = readmark;
	}
	
	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}
	
	public MsgContent getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(MsgContent msgContent) {
		this.msgContent = msgContent;
	}
}
