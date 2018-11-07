package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 门店实体类
 * @author ligangfan
 *
 */

@Table("storeinfo")
public class MtqStore {
	
	
	/** 门店ID */
	@PrimaryKey(AssignType.BY_MYSELF)
	@Column("_storeid")
	public String storeId;
	

	/** 企业ID */
	@Column("_corpid")
	public String corpId;
	
	
	/** 门店编码 */
	@Column("_storecode")
	public String storeCode;
	
	
	/** 门店名称 */
	@Column("_storename")
	public String storeName;
	
	/** 门店地址 */
	@Column("_storeaddr")
	public String storeAddr;
	
	/** 联系人 */
	@Column("_linkman")
	public String linkMan;
	
	/** 联系人电话 */
	@Column("_linkphone")
	public String linkPhone;
	/** 门店说明 */
	@Column("_remark")
	public String remark;
	
//	/** 位置信息 */
//	@Mapping(Relation.OneToOne)
//	public MtqStorePos pos;
	
	
	/** 位置k码 */
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
	
	
	public String getLetter() {
		return letter;
	}
	public void setLetter(String letter) {
		this.letter = letter;
	}
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String letter;
	public String first;
	
//	/** 审核信息 */
//	@Mapping(Relation.OneToOne)
//	public CldDeliStoreAuditInfo auditInfo;
	
	/** 审核状态 */
	@Column("_auditstatus")
	public int auditStatus;
	/** 审核状态说明 */
	@Column("_auditstatustext")
	public String auditStatusText;

	
	
	

}
