/*
 * @Title CldSapKConfig.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.ols.sap;

import java.util.HashMap;
import java.util.Map;
import com.yunbaba.ols.bll.CldBllUtil;
import com.yunbaba.ols.sap.parse.CldKBaseParse;
import com.yunbaba.ols.tools.CldSapReturn;


/**
 * 终端配置协议接口
 * 
 * @author Zhouls
 * @date 2014-10-9 下午3:36:31
 */
public class CldSapKConfig {

	/** 公共输入参数. */
	private static final int RSCHARSET = 1;
	/** The RSFORMAT. */
	private static final int RSFORMAT = 1;

	/**
	 * 获取配置列表(100)
	 * 
	 * @param classtypes
	 *            the classtypes
	 * @return errCode (0 有更新，110无更新) int
	 * @author Zhouls
	 * @date 2014-10-9 下午4:01:53
	 */
	public static CldSapReturn downloadControlList(String classtypes) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("classtypes", classtypes);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		String key;
		if (CldBllUtil.getInstance().isTestVersion()) {
			// 测试版本
			key = "D20B600B4C2060EBC21A97AB5557912A";
		} else {
			// 正式版本
			key = "B9720F0D8E5CBCAFC5B6CF409E01C1ED";
		}
		CldSapReturn errRes = CldKBaseParse
				.getGetParms(map, CldSapUtil.getNaviSvrKConfig()
						+ "tc/control_download.php", key);
		return errRes;
	}
}
