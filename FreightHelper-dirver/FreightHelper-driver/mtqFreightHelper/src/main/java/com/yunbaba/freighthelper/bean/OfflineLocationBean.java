package com.yunbaba.freighthelper.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

/**
 * Created by zhonghm on 2018/4/16.
 */
@Table("offlinelocation2")
public class OfflineLocationBean {


    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("_taskidandwaybill")
    public String taskidandwaybill;

    @Mapping(Relation.OneToOne)
    public MtqDeliStoreDetail mStoreDetail;
    @Mapping(Relation.OneToOne)
    public AddressBean addressBean;

    @Column("_updatetime")
    public long updatetime;

    public OfflineLocationBean(String taskidandwaybill,MtqDeliStoreDetail mStoreDetail, AddressBean bean,long updatetime) {
       this.taskidandwaybill = taskidandwaybill;
        this.mStoreDetail = mStoreDetail;
        this.addressBean = bean;
        this.updatetime = updatetime;
    }
}
