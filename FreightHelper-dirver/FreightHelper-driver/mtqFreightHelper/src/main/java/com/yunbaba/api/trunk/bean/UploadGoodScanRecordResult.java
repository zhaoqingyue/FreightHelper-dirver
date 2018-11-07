package com.yunbaba.api.trunk.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: UploadGoodScanRecordResult.java
 * @Prject: Freighthelper
 * @Package: com.mtq.api.trunk.bean
 * @Description: 扫描条码结果记录
 * @author: zsx
 * @date: 2017-4-18 下午4:39:02
 * @version: V1.0
 */
public class UploadGoodScanRecordResult {
	
	public static final String COL_KEY = "_key";
	public static final String COL_SEARCH_KEY = "_search_key";
	public static final String COL_BAR_CODE = "_bar_code";
	public static final String COL_NAME = "_name";
	public static final String COL_AMOUNT = "_amount";
	public static final String COL_SCAN_CNT = "_scan_cnt";
	public static final String COL_UPLOAD_DATE = "_upload_date";
	public static final String COL_CUST_ORDER_ID = "_cust_order_id";
	public static final String COL_ADDRESS = "_address";
	
	
	//组成为 taskid + cust_order_id + barcode,这样表示唯一
	@PrimaryKey(AssignType.BY_MYSELF)
	@Column(COL_KEY)
	public String taskAndbarCode;
	
	//组成 taskid + cust_order_id 用于搜索
	@Column(COL_SEARCH_KEY)
	public String searchKey;
	
	/**货物条形码**/
	@Column(COL_BAR_CODE)
	public String bar_code;
	
	/**货物名称**/
	@Column(COL_NAME)
	public String name;
	
	/**货物数量**/
	@Column(COL_AMOUNT)
	public int amount;
	
	/**扫描数量**/
	@Column(COL_SCAN_CNT)
	public int scan_cnt;
	
	@Column(COL_UPLOAD_DATE)
	public long uploadDate;        
	
//	运单号
	@Column(COL_CUST_ORDER_ID)
	public String cust_order_id;
	
	//地址
	@Column(COL_ADDRESS)
	public String address;
	
}
