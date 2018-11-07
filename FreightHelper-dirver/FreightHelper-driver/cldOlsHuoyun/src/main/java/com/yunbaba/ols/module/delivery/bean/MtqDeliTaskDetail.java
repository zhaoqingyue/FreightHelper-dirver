package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * 运单详情信息实体类
 * @author ligangfan
 *
 */
@Table("taskdetail")
public class MtqDeliTaskDetail {
	
	public static final String COL_TASK_ID = "_task_id";
	public static final String COL_TASK_DATE = "_task_date";
	public static final String COL_STATUS = "_status";
	public static final String COL_DD_TIME = "_ddtime";
	public static final String COL_SENDER = "_sender";
	public static final String COL_DISTANCE = "_distance";
	public static final String COL_CORP_ID = "_corpid";
	public static final String COL_CORP_NAME = "_corp_name";
	public static final String COL_IS_BACK = "_is_back";
	public static final String COL_PAGE = "_page";
	public static final String COL_PAGE_COUNT = "_page_count";
	public static final String COL_FINISH_COUNT = "_finish_count";
	public static final String COL_TOTAL = "_total";
	public static final String COL_CAR_LICENSE = "_car_license";
	public static final String COL_LOCAL_STATUS = "_local_status";
	public static final String COL_DOWN_UTCTIME = "_down_utc_time";
	
	//@PrimaryKey(AssignType.AUTO_INCREMENT)
   // @Column("_id")
    //private long id;
	/** 配送任务ID */
	@PrimaryKey(AssignType.BY_MYSELF)
	@Column(COL_TASK_ID)
	private String taskid;
	/** 送货日期 */
	@Column(COL_TASK_DATE)
	private String taskdate;
	/** 送货状态 */
	@Column(COL_STATUS)
	private int status;
	/** 运货单最近一次更新时间 **/
	@Column(COL_DD_TIME)
	private  long ddtime;
	/** 送货人员 */
	@Column(COL_SENDER)
	private String sender;
	/** 行程距离（米） */
	@Column(COL_DISTANCE)
	private int distance;
	/** 企业ID */
	@Column(COL_CORP_ID)
	private String corpid;
	/** 物流企业名称 */
	@Column(COL_CORP_NAME)
	private String corpname;
	/** 是否返程（1为是，0为否，‘返程’即送完最后配送点后返回到配送中心） */
	@Column(COL_IS_BACK)
	private int isback;
	/** 当前页码 */
	@Column(COL_PAGE)
	private int page;
	/** 当前页条数 */
	@Column(COL_PAGE_COUNT)
	private int pagecount;
	/** 已完成运货点数量 */
	@Column(COL_FINISH_COUNT)
	private int finishcount;
	/** 运货点总数 */
	@Column(COL_TOTAL)
	private int total;
	
	/** 发送时间 */
	@Column("_send_time")
	public long sendtime;
	
	/** 车牌号 */
	@Column(COL_CAR_LICENSE)
	private String carlicense; 
	/** 客户端维护 **/
	/** 保存在本地的送货状态 */
	@Column(COL_LOCAL_STATUS)
	private int local_status;
	
	@Column(COL_DOWN_UTCTIME)
	private  long downUtcTime;
	/** 运货点列表 */
	@Mapping(Relation.OneToMany)
	public ArrayList<MtqDeliStoreDetail> store;
	/** 运货点明细*/
	@Mapping(Relation.OneToMany)
	public ArrayList<MtqDeliOrderDetail> orders;
	/** 车辆信息*/
	@Mapping(Relation.OneToOne)
	public MtqCarInfo ums_carinfo;
	
	/** 配送路径信息*/
	@Mapping(Relation.OneToOne)
	public MtqDeliReceiveData data;
	/** 订单类型 */
	@Column("_freight_type")
	public int freight_type;
	/** 当前提货站 */
	@Column("_pdeliver")
	public String pdeliver;
	/** 当前收货站 */
	@Column("_preceipt")
	public String preceipt;
	
//	@Mapping(Relation.OneToOne)
//	private UMS_Carinfo ums_carinfo;
//	/** 配送中心 **/
//	@Mapping(Relation.OneToOne)
//	private CldDeliveryTaskCentre taskCentre;

	
//	public long getId() {
//		return id;
//	}
//	public void setId(long id) {
//		this.id = id;
//	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	public String getTaskdate() {
		return taskdate;
	}
	public void setTaskdate(String taskdate) {
		this.taskdate = taskdate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getDdtime() {
		return ddtime;
	}
	public void setDdtime(long ddtime) {
		this.ddtime = ddtime;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public String getCorpid() {
		return corpid;
	}
	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}
	public String getCorpname() {
		return corpname;
	}
	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}
	public int getIsback() {
		return isback;
	}
	public void setIsback(int isback) {
		this.isback = isback;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPagecount() {
		return pagecount;
	}
	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}
	public int getFinishcount() {
		return finishcount;
	}
	public void setFinishcount(int finishcount) {
		this.finishcount = finishcount;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public String getCarlicense() {
		return carlicense;
	}
	public void setCarlicense(String carlicense) {
		this.carlicense = carlicense;
	}
	public int getLocal_status() {
		return local_status;
	}
	public void setLocal_status(int local_status) {
		this.local_status = local_status;
	}
	public long getDownUtcTime() {
		return downUtcTime;
	}
	public void setDownUtcTime(long downUtcTime) {
		this.downUtcTime = downUtcTime;
	}
	public List<MtqDeliStoreDetail> getStore() {
		return store;
	}
	public void setStore(ArrayList<MtqDeliStoreDetail> store) {
		this.store = store;
	}
	public List<MtqDeliOrderDetail> getOrders() {
		return orders;
	}
	public void setOrders(ArrayList<MtqDeliOrderDetail> orders) {
		this.orders = orders;
	}
//	public MtqCarInfo getCarInfo() {
//		return carInfo;
//	}
//	public void setCarInfo(MtqCarInfo carInfo) {
//		this.carInfo = carInfo;
//	}
//	public UMS_Carinfo getUms_carinfo() {
//		return ums_carinfo;
//	}
//	public void setUms_carinfo(UMS_Carinfo ums_carinfo) {
//		this.ums_carinfo = ums_carinfo;
//	}
//	public CldDeliveryTaskCentre getTaskCentre() {
//		return taskCentre;
//	}
//	public void setTaskCentre(CldDeliveryTaskCentre taskCentre) {
//		this.taskCentre = taskCentre;
//	}
}
