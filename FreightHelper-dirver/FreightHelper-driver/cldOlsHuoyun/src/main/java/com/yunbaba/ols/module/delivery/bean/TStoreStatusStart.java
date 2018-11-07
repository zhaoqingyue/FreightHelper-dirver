package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by zhonghm on 2018/4/24.
 */
@Table("tsstorestatusstart")
public class TStoreStatusStart {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    /** 运货时间【UTC时间】 */
    public long id;

    @Column("_updatime")
    public long uptime;
    /** 位置X */

    @Column("_x")
    public long x;
    /** 位置Y */
    @Column("_y")
    public long y;
    /** 位置的道路图幅ID */
    @Column("_cell")
    public int cell;
    /** 位置的道路UID */
    @Column("_uid")
    public int uid;

}
