package com.yunbaba.freighthelper.bean.msg;

import android.os.Parcel;
import android.os.Parcelable;

public class Filter implements Parcelable {

	private int id;         // 筛选ID
	private int type;       // 0-企业消息；1-报警消息; 2-通知开关
	private int select;     // 0-未筛选；1-已筛选
	private int open;       // 0-关闭；1-打开
	private int position;   // 位置
	private String title;   // 标题
	private String content; // 内容
	

	public Filter() {
		id = -1;
		type = -1;
		select = 0;
		open = 0;
		position = -1;
		title = "";
		content = "";
	}

	public Filter(Filter filter) {
		id = filter.id;
		type = filter.type;
		select = filter.select;
		open = filter.open;
		position = filter.position;
		title = filter.title;
		content = filter.content;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeInt(type);
		out.writeInt(select);
		out.writeInt(open);
		out.writeInt(position);
		out.writeString(title);
		out.writeString(content);
	}

	public static final Parcelable.Creator<Filter> CREATOR = new Parcelable.Creator<Filter>() {
		public Filter createFromParcel(Parcel in) {
			return new Filter(in);
		}

		public Filter[] newArray(int size) {
			return new Filter[size];
		}
	};

	private Filter(Parcel in) {
		id = in.readInt();
		type = in.readInt();
		select = in.readInt();
		open = in.readInt();
		position = in.readInt();
		title = in.readString();
		content = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setSelect(int select) {
		this.select = select;
	}

	public int getSelect() {
		return select;
	}
	
	public void setOpen(int open) {
		this.open = open;
	}

	public int getOpen() {
		return open;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public class FilterId {
		/**
		 * 企业消息
		 */
		public final static int FILTER_ID_10 = 10; // 加入车队
		public final static int FILTER_ID_11 = 11; // 退出车队
		public final static int FILTER_ID_12 = 12; // K码调度消息
		public final static int FILTER_ID_13 = 13; // 调度消息
		public final static int FILTER_ID_14 = 14; // 运单超时报警
		public final static int FILTER_ID_15 = 15; // 运单超时提醒
		public final static int FILTER_ID_16 = 16; // 运单过期报警
		public final static int FILTER_ID_17 = 17; // 运单过期提醒
		public final static int FILTER_ID_18 = 18; // 任务
		public final static int FILTER_ID_1311 = 1311; // 语音调度消息
		public final static int FILTER_ID_1802 = 1802; // 运单撤回消息
		public final static int FILTER_ID_1803 = 1803; // 任务撤回消息

		/**
		 * 报警消息
		 */
		public final static int FILTER_ID_19 = 19; // 碰撞提醒
		public final static int FILTER_ID_20 = 20; // 翻车提醒
		public final static int FILTER_ID_21 = 21; // 超速行驶提醒
		public final static int FILTER_ID_22 = 22; // 疲劳驾驶提醒
		public final static int FILTER_ID_23 = 23; // 异常震动提醒
		
		public final static int FILTER_ID_24 = 24; // 车门状态异常提醒
		public final static int FILTER_ID_25 = 25; // 胎压和手刹异常提醒
		public final static int FILTER_ID_26 = 26; // 水温异常提醒
		public final static int FILTER_ID_27 = 27; // 转速异常提醒
		public final static int FILTER_ID_28 = 28; // 电瓶电压异常提醒
		public final static int FILTER_ID_29 = 29; // 车辆故障提醒
		public final static int FILTER_ID_30 = 30; // 漏油提醒
		public final static int FILTER_ID_31 = 31; // 拖吊报警
		
		public final static int FILTER_ID_32 = 32; // 断电提醒
		public final static int FILTER_ID_33 = 33; // 终端异常
		public final static int FILTER_ID_34 = 34; // 设备OBD终端的SIM卡
	}
}
