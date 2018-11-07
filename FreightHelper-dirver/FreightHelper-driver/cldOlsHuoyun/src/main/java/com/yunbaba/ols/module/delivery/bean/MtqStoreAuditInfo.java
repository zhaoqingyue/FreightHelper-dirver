package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

@Table("storeaudioinfo")
public class MtqStoreAuditInfo {

	@PrimaryKey(AssignType.AUTO_INCREMENT)
	@Column("_id")
	public long id;
	/** 审核状态 */
	@Column("_auditstatus")
	public int auditStatus;
	/** 审核状态说明 */
	@Column("_auditstatustext")
	public String auditStatusText;

}
