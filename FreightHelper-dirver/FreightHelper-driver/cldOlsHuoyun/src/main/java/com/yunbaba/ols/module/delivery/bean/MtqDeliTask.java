package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 运货单信息实体类（简单信息,不是运单详情信息）
 * 
 * @author ligangfan
 *
 */
@Table("delitask5")
public class MtqDeliTask {

	// @PrimaryKey(AssignType.AUTO_INCREMENT)
	// @Column("_id")
	// public long id;

	/** 配送任务ID */

	@PrimaryKey(AssignType.BY_MYSELF)
	@Column("_task_id")
	public String taskid;
	/** 送货日期 */
	@Column("_task_date")
	public long taskdate;
	/** 发送时间 */
	@Column("_send_time")
	public long sendtime;
	/** 发车时间 */
	@Column("_departtime")
	public long departtime;
	/** 运货状态【0待运货1运货中2已完成3暂停状态4中止状态 】 */
	@Column("_status")
	public int status;
	/** 所属企业ID */
	@Column("_corp_id")
	public String corpid;
	/** 企业名称 */
	@Column("_corp_name")
	public String corpname;
	/** 运货点总数 */
	@Column("_store_count")
	public int store_count;
	/** 已完成数量 */
	@Column("_finish_count")
	public int finish_count;
	/** 已读未读状态0未读 1 已读 */
	@Column("_read_status")
	public int readstatus;
	/** 最近一次更新时间 */
	@Column("_dttime")
	public long dttime;
	/** 请求时间，包含运单id（用于搜索）、最晚送达时间（用于提醒运单的过期） */
//	@Mapping(Relation.OneToMany)
//	public ArrayList<MtqRequestTime> req_times;


	//	@Mapping(Relation.OneToMany)
//	@Column("_req_times")
//	public String req_times;
	/** 行程距离 */
	@Column("_distance")
	public int distance;
	/** 订单类型 */
	@Column("_freight_type")
	public int freight_type;
	/** 当前提货站 */
	@Column("_pdeliver")
	public String pdeliver;
	/** 当前收货站 */
	@Column("_preceipt")
	public String preceipt;
	/** 货物总数量 */
	@Column("_gamount")
	public String gamount;
	/** 货物总重量 */
	@Column("_gweight")
	public String gweight;
	/** 货物总体积 */
	@Column("_gvolume")
	public String gvolume;



    /**总箱数**/
	@Column("_gframecount")
	public int gframecount;


	/** 行政区路由点列表**/
	@Column("_district_code")
   public String district_code;
	
	/** 车牌号码 */
	@Column("_carlicense")
	public String carlicense;
	
	/**是否有回程（1为是，0为否**/
	@Column("_isback")
	public int isback;

	public boolean equals(Object destination) {
		boolean retVal = false;
		if (destination != null && destination.getClass().equals(this.getClass())) {
			MtqDeliTask bean = (MtqDeliTask) destination;
			if (bean.taskid == null && this.taskid == null) {
				retVal = true;
			} else {
				if (bean.taskid != null && bean.taskid.equals(this.taskid)) {
					retVal = true;
				}
			}
		}
		return retVal;
	}

}
