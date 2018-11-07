package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 消息实体类
 * @author ligangfan
 *
 */
@Table("message")
public class MtqMessage{
	
	public static final String COL_MESSAGE_ID = "_messageid";
	public static final String COL_TITLE = "_title";
	public static final String COL_CONTENT = "_content";
	public static final String COL_CREATE_TYPE = "_create_type";
	public static final String COL_RECEIVE_OBJECT = "_receive_object";
	public static final String COL_MSG_TYPE = "_msg_type";
	public static final String COL_CREATE_USER_ID = "_create_user_id";
	public static final String COL_CREATE_USER_NAME = "_create_user_name";
	public static final String COL_USER_MOBILE = "_user_mobile";
	public static final String COL_CREATE_TIME = "_create_time";
	public static final String COL_DOWNLOAD_TIME = "_download_time";
	public static final String COL_STATUS = "_status";
	public static final String COL_APP_TYPE = "_app_type";
	
	/**note：这个字段必须要添加，否则无法进行数据的插入操作*/
//	@PrimaryKey(AssignType.AUTO_INCREMENT)
//    @Column("_id")
//    protected long id;
	/** 消息Id */
	@PrimaryKey(AssignType.BY_MYSELF)
	@Column(COL_MESSAGE_ID)
	private long messageId;
	/** 标题 */
	@Column(COL_TITLE)
	private String title;
	/**消息内容*/
	@Column(COL_CONTENT)
	private String content;
	/** 构造类型（1：运营；2：终端）. */
	@Column(COL_CREATE_TYPE)
	private int createType;
	/** 1为发送给用户消息；2为发送给设备消息. */
	@Column(COL_RECEIVE_OBJECT)
	private int receiveObject;
	/** 消息类型1:活动消息;2:升级消息;3:区域消息;11:POI;12:路径;13:路书;14:路况;15:一键通.；99：货运消息；101：通知消息 */
	@Column(COL_MSG_TYPE)
	private int msgType;
	/** 构造者ID. */
	@Column(COL_CREATE_USER_ID)
	private long createuserid;
	/** 构造者名称. */
	@Column(COL_CREATE_USER_NAME)
	private String createusername;
	/** 构造者绑定的手机号. */
	@Column(COL_USER_MOBILE)
	private String usermobile;
	/** 构造时间. */
	@Column(COL_CREATE_TIME)
	private String createtime;
	/** 消息接收时间. */
	@Column(COL_DOWNLOAD_TIME)
	private long downloadTime;
	/** 状态（1：构造 ；2：已下载；3：已阅读）. */
	@Column(COL_STATUS)
	private int status;
	/** 应用类型. */
	@Column(COL_APP_TYPE)
	private int apptype;
	
//	public long getId(){
//		return this.id;
//	}
//	
//	public void setId(long id){
//		this.id = id;
//	}
	
	public long getMessageId(){
		return this.messageId;
	}
	
	public void setMessageId(long messageId){
		this.messageId = messageId;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getContent(){
		return this.content;
	}
	
	public void setContent(String content){
		this.content = content;
	}
	
	public int getCreateType(){
		return this.createType;
	}
	
	public void setCreateType(int createType){
		this.createType = createType;
	}
	
	public int getReceiveObject(){
		return this.receiveObject;
	}
	
	public void setReceiveObject(int receiveObject){
		this.receiveObject = receiveObject;
	}
	
	public int getMsgType(){
		return this.msgType;
	}
	
	public void setMsgType(int msgType){
		this.msgType = msgType;
	}
	
	public long getCreateUserId(){
		return this.createuserid;
	}
	
	public void setCreateUserId(long createUserId){
		this.createuserid = createUserId;
	}
	
	public String getCreateUserName(){
		return this.createusername;
	}
	
	public void setCreateUserName(String createUserName){
		this.createusername = createUserName;
	}
	
	public String getUserMobile(){
		return this.usermobile;
	}
	
	public void setUserMobile(String userMobile){
		this.usermobile = userMobile;
	}
	
	public String getCreateTime(){
		return this.createtime;
	}
	
	public void setCreateTime(String createTime){
		this.createtime = createTime;
	}
	
	public long getDownloadTime(){
		return this.downloadTime;
	}
	
	public void setDownloadTime(long downloadTime){
		this.downloadTime = downloadTime;
	}
	
	public int getStatus(){
		return this.status;
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	
	public int getAppType(){
		return apptype;
	}
	
	public void setAppType(int appType){
		this.apptype = appType;
	}
	
	@Override
	public String toString() {

		return "Message{" + "messageId = " + messageId + ", title = " + title + ", content = " + content +
				", createType = " + createType + ", receiveObject = " + receiveObject + 
				", msgType = " + msgType + ", createuserid = " + createuserid + ", " + ", createusername = " + 
				createusername + ", usermobile = " + usermobile + ", createtime = " + createtime +
				", downloadTime = " + downloadTime + ", status = " + status + ", apptype = " + apptype +
				super.toString()
				+ "}";
	}
}




