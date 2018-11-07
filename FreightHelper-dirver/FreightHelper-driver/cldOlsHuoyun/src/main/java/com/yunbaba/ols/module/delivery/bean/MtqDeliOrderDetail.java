package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;

/**
 * 运单详情实体类
 *
 * @author ligangfan
 */
@Table("neworderdetail")
public class MtqDeliOrderDetail {

//	@PrimaryKey(AssignType.AUTO_INCREMENT)
//	@Column("_id")
//	public long id;


    /**
     * 配送单号
     */
    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("_waybill")
    public String waybill;

    /**
     * 客户单号
     **/
    @Column("_orderno")
    public String orderno;


    /**
     * 要求送达时间(起)
     **/
    @Column("_reqtime")
    public long reqtime;
    /**
     * 要求送达时间(止)
     **/
    @Column("_reqtime_e")
    public long reqtime_e;
    /**
     * 发货人
     **/
    @Column("_send_name")
    public String send_name;
    /**
     * 发货人电话
     **/
    @Column("_send_phone")
    public String send_phone;
    /**
     * 发货人地址
     **/
    @Column("_send_addr")
    public String send_addr;
    /**
     * 发货人K码
     **/
    @Column("_send_kcode")
    public String send_kcode;
    /**
     * 收货人
     **/
    @Column("_receive_name")
    public String receive_name;
    /**
     * 收货人电话
     **/
    @Column("_receive_phone")
    public String receive_phone;
    /**
     * 收货人地址
     **/
    @Column("_receive_addr")
    public String receive_addr;
    /**
     * 收货人K码
     **/
    @Column("_receive_kcode")
    public String receive_kcode;
    /**
     * 托运须知
     **/
    @Column("_note")
    public String note;
    /**
     * 已上传照片数
     **/
    @Column("_photo_nums")
    public int photo_nums;
    /**
     * 已上传回单数
     **/
    @Column("_receipt_nums")
    public int receipt_nums;
    /**
     * 可上传照片数
     **/
    @Column("_can_photo_nums")
    public int can_photo_nums;
    /**
     * 可上传回单数
     **/
    @Column("_can_receipt_nums")
    public int can_receipt_nums;

    /**
     * 已上传回单的图片编号（以小写“,”逗号）分隔
     **/
    @Column("_receipt_kcode")
    public String receipt_ids;
    /**
     * 已上传照片的图片编号（以小写“,”逗号）分隔
     */
    @Column("_photo_kcode")
    public String photo_ids;

    @Column("_task_id")
    public String taskId;

    @Column("_corp_id")
    public String corpId;

    /**
     * 区域编码
     */
    @Column("_regioncode")
    public int send_regioncode;

    /**
     * 区域名称
     */
    @Column("_regionname")
    public String send_regionname;

    /**
     * 区域编码
     */
    @Column("_regioncode")
    public int receive_regioncode;

    /**
     * 区域名称
     */
    @Column("_regionname")
    public String receive_regionname;


    @Column("_sendorganize")
    public String send_organize;


    @Column("_receiveorganize")
    public String receive_organize;


    /**
     * 发货门店编码
     **/
    @Column("_sendstorecode")
    public String send_storecode;

    /**
     * 收货门店编码
     **/
    @Column("_receivestorecode")
    public String receive_storecode;


    @Mapping(Relation.OneToMany)
    public ArrayList<MtqGoodDetail> goods;

    /**
     * 过期标识(0有效，非0过期)： 0--有效 1--已过期(终端暂停超过3天） 2--已过期（超过要求时间15天）
     **/
    @Column("_expired")
    public int expired;
    @Column("_piececount")
    public int piececount;
    @Column("_framecount")
    public int framecount;
    @Column("_volume")
    public float volume;
    @Column("_weight")
    public float weight;
    @Column("_framename")
    public String framename;


}
