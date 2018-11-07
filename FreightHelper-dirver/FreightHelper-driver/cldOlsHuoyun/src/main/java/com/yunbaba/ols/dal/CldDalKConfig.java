/*
 * @Title CldDalKConfig.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:59
 * @version 1.0
 */
package com.yunbaba.ols.dal;

import java.util.HashMap;
import java.util.Map;
import com.yunbaba.ols.sap.bean.CldSapKConfigParm.CldDomainConfig;
import com.yunbaba.ols.sap.bean.CldSapKConfigParm.CldKCloudConfig;
import com.yunbaba.ols.sap.bean.CldSapKConfigParm.CldPosUpConfig;
import com.yunbaba.ols.sap.bean.CldSapKConfigParm.CldThirdOpenConfig;
import com.yunbaba.ols.sap.bean.CldSapKConfigParm.CldWebUrlConfig;
import com.yunbaba.ols.sap.parse.CldKConfigParse.ProtConfig;
import com.yunbaba.ols.tools.CldSapParser;

/**
 * 配置数据层
 * 
 * @author Zhouls
 * @date 2014-10-28 上午11:46:29
 */
public class CldDalKConfig {

	/** 域名配置 */
	private CldDomainConfig domainConfig;
	/** WebUrl配置. */
	private CldWebUrlConfig webUrlConfig;
	/** // 第三方接口开关配置. */
	private CldThirdOpenConfig thirdPartConfig;
	/** 位置上报配置接口 */
	private CldPosUpConfig posReportConfig;
	/** K云设置 */
	private CldKCloudConfig kcCloundConfig;
	/** 存储配置项缓存map */
	private Map<String, String> jsonMap;

	private static CldDalKConfig cldDalKConfig;

	/**
	 * Gets the single instance of CldDalKConfig.
	 * 
	 * @return single instance of CldDalKConfig
	 */
	public static CldDalKConfig getInstance() {
		if (null == cldDalKConfig) {
			cldDalKConfig = new CldDalKConfig();
		}
		return cldDalKConfig;
	}

	/**
	 * Instantiates a new cld dal k config.
	 */
	private CldDalKConfig() {
		domainConfig = new CldDomainConfig();
		webUrlConfig = new CldWebUrlConfig();
		thirdPartConfig = new CldThirdOpenConfig();
		posReportConfig = new CldPosUpConfig();
		kcCloundConfig = new CldKCloudConfig();
		jsonMap = new HashMap<String, String>();
	}

	/**
	 * 将服务端返回的json 解析类解析到配置项里
	 * 
	 * @param protConfig
	 * @return void
	 * @author Zhouls
	 * @date 2015-3-19 下午5:22:59
	 */
	public void loadConfig(ProtConfig protConfig) {
		if (null != protConfig.getItem()) {
			for (int i = 0; i < protConfig.getItem().size(); i++) {
				if (null != protConfig.getItem().get(i)) {
					long clastype = protConfig.getItem().get(i).getClasstype();
					if (clastype == 1001001100) {
						domainConfig.protParse(protConfig.getItem().get(i));
						jsonMap.put("1001001100",
								CldSapParser.objectToJson(domainConfig));
					} else if (clastype == 1001003000) {
						webUrlConfig.protParse(protConfig.getItem().get(i));
						jsonMap.put("1001003000",
								CldSapParser.objectToJson(webUrlConfig));
					} else if (clastype == 1003001200) {
						thirdPartConfig.protParse(protConfig.getItem().get(i));
						jsonMap.put("1003001200",
								CldSapParser.objectToJson(thirdPartConfig));
					} else if (clastype == 2002001000) {
						posReportConfig.protParse(protConfig.getItem().get(i));
						jsonMap.put("2002001000",
								CldSapParser.objectToJson(posReportConfig));
					} else if (clastype == 2004001000) {
						kcCloundConfig.protParse(protConfig.getItem().get(i));
						jsonMap.put("2004001000",
								CldSapParser.objectToJson(kcCloundConfig));
					}
				}
			}
		}
	}

	public Map<String, String> getJsonMap() {
		return jsonMap;
	}

	public CldDomainConfig getDomainConfig() {
		return domainConfig;
	}

	public void setDomainConfig(CldDomainConfig domainConfig) {
		this.domainConfig = domainConfig;
	}

	public CldWebUrlConfig getWebUrlConfig() {
		return webUrlConfig;
	}

	public void setWebUrlConfig(CldWebUrlConfig webUrlConfig) {
		this.webUrlConfig = webUrlConfig;
	}

	public CldThirdOpenConfig getThirdPartConfig() {
		return thirdPartConfig;
	}

	public void setThirdPartConfig(CldThirdOpenConfig thirdPartConfig) {
		this.thirdPartConfig = thirdPartConfig;
	}

	public CldPosUpConfig getPosReportConfig() {
		return posReportConfig;
	}

	public void setPosReportConfig(CldPosUpConfig posReportConfig) {
		this.posReportConfig = posReportConfig;
	}

	public CldKCloudConfig getKcCloundConfig() {
		return kcCloundConfig;
	}

	public void setKcCloundConfig(CldKCloudConfig kcCloundConfig) {
		this.kcCloundConfig = kcCloundConfig;
	}
}
