package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 车辆信息实体类
 * @author ligangfan
 *
 */
@Table("carinfo")
public class MtqCarInfo {

	@PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    public long id;
	/** 车高级别 */
	@Column("_tht")
	public String tht;
	/** 车宽级别 */
	@Column("_twh")
	public String twh;
	/** 车重级别*/
	@Column("_twt")
	public String twt;
	/** 车型级别 */ 
	@Column("_tvt")
	public String tvt;
	/** 车牌类型*/
	@Column("_tlnt")
	public String tlnt;
	
	
	/** 车牌号*/
	@Column("_carlicense")
	public String carlicense;
	
}
