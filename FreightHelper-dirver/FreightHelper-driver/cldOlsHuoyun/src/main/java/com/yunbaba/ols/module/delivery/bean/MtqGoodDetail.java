package com.yunbaba.ols.module.delivery.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 货物信息详情实体类
 * @author ligangfan
 *
 */
@Table("good")
public class MtqGoodDetail {

	@PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    public long id;
	/**货物名称**/
	@Column("_name")
	public String name;
	/**货物数量**/
	@Column("_amount")
	public int amount;
	/**扫描数量**/
	@Column("_scan_cnt")
	public int scan_cnt;
	
	/**货物条形码**/
	@Column("_bar_code")
	public String bar_code;
	
	/**单位**/
	@Column("_unit")
	public String unit;
	/**包裹？ **/
	@Column("_pack")
	public String pack;
	/**重量**/
	@Column("_weight")
	public String weight;
	/**体积**/
	@Column("_volume")
	public String volume;
	
}
