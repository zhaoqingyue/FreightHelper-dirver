package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 运货点的详情实体类
 * @author ligangfan
 *
 */
@Table("storedetail")
public class MtqDeliStoreDetail {

//	@PrimaryKey(AssignType.AUTO_INCREMENT)
//    @Column("_id")
//    public long id;
	/** 配送单号 */
	@PrimaryKey(AssignType.BY_MYSELF)
	@Column("_waybill")
	public String waybill;
	/** 门店ID */
	@Column("_storeid")
	public String storeid;
	/** 位置X(凯立德坐标) */
	@Column("_storex")
	public long storex;
	/** 位置Y(凯立德坐标) */
	@Column("_storey")
	public long storey;
	/** 运货点名称 */
	@Column("_storename")
	public String storename;
	/** 运货点地址 */
	@Column("_storeaddr")
	public String storeaddr;
	/** 收货人 */
	@Column("_linkman")
	public String linkman;
	/** 收货人电话 */
	@Column("_linkphone")
	public String linkphone;
	/** 送货顺序 */
	@Column("_store_sort")
	public int storesort;
	/** 完成时间（只有已完成才有值 */
	@Column("_finish_time")
	public long finishtime;
	/** 送货说明 */
	@Column("_store_mark")
	public String storemark;
	/** 送货状态（0-等待送货，1-正在配送中，2-已完成配送，3-暂停送货 ） */
	@Column("_store_status")
	public int storestatus;
	/** 任务类型（1送货/3提货/4回程/5-必经地） */
	@Column("_optype")
	public int optype;
	/** 停车位置X(凯立德坐标) */
	@Column("_stopx")
	public long stopx;
	/** 停车位置Y(凯立德坐标) */
	@Column("_stopy")
	public long stopy;
	/** 是否需要收款（0-不需要，1-需要） */
	@Column("_pay_mode")
	public int pay_mode;
	/** 收款方式（已选择的收款方式，没有则为空） */
	@Column("_pay_method")
	public String pay_method;
	/** 实收金额（已收到的金额，没有则为0） */
	@Column("_real_amount")
	public float real_amount;
	/** 应收金额（没有则为0） */
	@Column("_total_amount")
	public float total_amount;
	/** 退货原因（已选择的退货原因，没有则为空） */
	@Column("_return_desc")
	public String return_desc;
	/** 最近一次更新时间 */
	@Column("_ddtime")
	public long dttime;
//	/** 客户单号**/
//	@Column("_cust_order_id")
//	public String cust_orderid;
	/** 是否需要回单 **/
	@Column("_is_receipt")
	public int is_receipt;
	/** 运单助手链接 **/	
	@Column("_assist_url")
	public String assist_url;
	
	/**送货日期**/
	@Column("_taskdate")
	public String taskdate;

	/** 客户端维护 **/
	/** 本地送货状态（0-等待送货，1-正在配送中，2-已完成配送，3-暂停送货 ） */
	@Column("_locak_storestatus")
	public int local_storestatus;
//	/** 所属的运货单ID **/
//	@Column("_taskID")
//	public String taskID;
	/** 收款补充说明 **/
	@Column("_pay_remark")
	public String pay_remark;
//	/** 企业ID**/
//	@Column("_corpid")
//	public String corpId;
	/** 发送时间 */
	@Column("_send_time")
	public long sendtime;
	/** 生成回单时间 **/
	@Column("_receipt_time")
	public  long receiptTime;
	
	/** 区域编码 */
	@Column("_regioncode")
	public String regioncode;
	
	/** 区域名称 */
	@Column("_regionname")
	public String regionname;
	
//	/**当前提货站*/
//	@Column("_pdeliver")
//	public String pdeliver;
//	/**当前收货站*/
//	@Column("_preceipt")
//	public String preceipt;
	
	@Column("_task_id")
	public String taskId;
	
	@Column("_corp_id")
	public String corpId;
	
	
	/**订单类型*/
	@Column("_freight_type")
	public int freight_type;
	
	
	
	/** 门店编码**/
	@Column("_storecode")
	public String storecode;
	
	
	
	/** 是否没有位置信息 **/
	@Column("_isunknownaddress")
	public boolean isUnknownAddress;



	/**orders信息**/
//	@Mapping(Relation.OneToOne)
//	public MtqDeliOrderDetail orders;
	
	
//	public String photo_ids;
//	
//	public long reqtime;
//	public long reqtime_e;
//	
//	
//	public String receipt_ids;
//	public String send_name;
//	public String send_phone;
//	public String send_addr;
//	public String send_kcode;
//	
//	
//	public String receive_name;
//	public String receive_phone;
//	public String receive_addr;
//	public String receive_kcode;



}
