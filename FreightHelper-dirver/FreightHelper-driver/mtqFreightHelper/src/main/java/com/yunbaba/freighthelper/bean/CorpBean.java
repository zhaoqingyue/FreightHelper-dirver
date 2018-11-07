package com.yunbaba.freighthelper.bean;

public class CorpBean {
	
	/** 企业ID */
	private String corpId;
	/** 公司名称 */
	private String corpName;
	
	
	public CorpBean() {
		
	}
	
	public CorpBean(String corpid2, String corpname2) {
		// TODO Auto-generated constructor stub
		this.corpId = corpid2;
		this.corpName = corpname2;
	}
	public String getCorpId() {
		return corpId;
	}
	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}
	public String getCorpName() {
		return corpName;
	}
	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

}
