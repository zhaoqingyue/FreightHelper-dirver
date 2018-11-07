package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

@Table("MtqHistroyTaskStack")
public class MtqHistroyTaskStack {


	@PrimaryKey(AssignType.AUTO_INCREMENT)
	@Column("_id")
	/** 运货时间【UTC时间】 */
	public long id;

	@Mapping(Relation.OneToOne)
	public MtqHistroyTask2 histroytask;

}
