package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

/**
 * Created by zhonghm on 2018/4/24.
 */
@Table("taskstore22")
public class TaskStore {
    /** 配送单号/挂靠组编号（运货点） */
    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("_waybill")
    public String waybill;
    /** 开始运货信息（start和finish必须至少传一个） */

    @Column("_taskid")
    public String taskid;


    @Column("_corpid")
    public String corpid;

    @Mapping(Relation.OneToOne)
    public TStoreStatusStart start;
    /** 完成运货信息（start和finish必须至少传一个） */

    @Mapping(Relation.OneToOne)
    public TStoreStatusFinish finish;

    @Mapping(Relation.OneToOne)
    public TStoreStatusPause pause;
}
