package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;


/**
 * 更新货运点返回当前提货收货站信息
 * @author ligangfan
 *
 */
@Table("delireceiveinfo")
public class MtqDeliReceiveData {
	
	@PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    public long id;
	/**当前提货站*/
	@Column("_pdeliver")
	public String pdeliver;
	/**当前收货站*/
	@Column("_preceipt")
	public String preceipt;
	/**订单类型*/
	@Column("_freight_type")
	public int freight_type;
	

}
