package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

@Table("storepos")
public class MtqStorePos {
	
	
	/** 位置k码 */
	@PrimaryKey(AssignType.BY_MYSELF)
	@Column("_kcode")
	public String kCode;
	
	/** 门店位置x */
	@Column("_x")
	public int x;
	
	/** 门店位置y */
	@Column("_y")
	public int y;
	
	/** 区域编码 */
	@Column("_regioncode")
	public int regionCode;
	
	/** 区域名称 */
	@Column("_regionname")
	public String regionName;
	
	

}
