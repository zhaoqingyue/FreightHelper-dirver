package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by zhonghm on 2018/6/1.
 * <p>
 * 门店标注记录实体类
 */


@Table("storeinfo")
public class MtqStoreMarkRecordInfo {
    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("_id")
    public int id;//	记录ID

    @Column("_corpid")
    public int corpid;//	Integer	企业ID

    @Column("_storeid")
    public int storeid;

    @Column("_settype")
    public int settype;/*Integer	上报类型

    1-新增：针对门店库中不存在的数据上报。
            2-纠正：针对门店库中存在的数据进行纠错。
            3-标注：针对门店库中无坐标数据的补充。
            */

    @Column("_settime")
    public int settime;// Integer  上报时间（UTC时间戳）

    @Column("_storecod")
    public String storecode;//标注编码

    @Column("_storename")
    public String storename;// 标注名称

    @Column("_x")
    public int x;//Integer

    @Column("_y")
    public int y;// Integer

    @Column("_storeaddr")
    public String storeaddr;

    @Column("_storekcode")
    public String storekcode;

    @Column("_linkman")
    public String linkman;

    @Column("_linkphone")
    public String linkphone;

    @Column("_remark")
    public String remark;

    @Column("_pprovestatus")
    public int approvestatus;//Integer
    //审核状态（0-待审核1-审核通过 2-审核不通过）

    @Column("_approvetime")
    public int approvetime;// Integer
    //审核时间（UTC时间戳）


    @Column("_approvestatustext")
    public String approvestatustext;


    // 审核状态说明
    // pics Json
//    上报图片信息，JSON体，格式如下：
//
//            [
//
//    {
//        "time":拍照时间【UTC时间】,
//        "x":拍照时凯立德坐标X,
//            "y":拍照时凯立德坐标Y,
//            "file":"图片路径"
//    }
//
//    ,
//            ...]


    @Column("_islock")
    public int islock;// Integer

    //  锁定标识(1-锁定，0-未锁)


}
