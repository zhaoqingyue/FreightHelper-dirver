/*
 * @Title CldSapKPub.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-4 上午10:52:09
 * @version 1.0
 */
package com.yunbaba.ols.sap;

import java.util.HashMap;
import java.util.Map;

import com.yunbaba.ols.bll.CldBllUtil;
import com.yunbaba.ols.sap.parse.CldKBaseParse;
import com.yunbaba.ols.tools.CldSapReturn;

/**
 * 零散接口
 * 
 * @author Zhouls
 * @date 2015-3-4 上午10:52:09
 */
public class CldSapKPub {
	/** 首次密文. */
	public static String keyCode;
	/** 公共输入参数. */
	private static final int APIVER = 1;
	/** The APPID. */
	public static int APPID;
	/** The RSCHARSET. */
	private static final int RSCHARSET = 1;
	/** The RSFORMAT. */
	private static final int RSFORMAT = 1;
	/** The UMSAVER. */
	private static final int UMSAVER = 2;

	/**
	 * 获取零散接口密钥(700)
	 * 
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2014-8-13 下午1:08:04
	 */
	public static CldSapReturn initKeyCode() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apiver", APIVER);
		map.put("rscharset", RSCHARSET);
		map.put("rsformat", RSFORMAT);
		map.put("umsaver", UMSAVER);
		String key;
		if (CldBllUtil.getInstance().isTestVersion()) {
			// 测试版本
			key = "BBA9B3328AD5D4DBEF5C756D3DBF335D";
		} else {
			// 正式版本
			key = "BBA9B3328AD5D4DBEF5C756D3DBF335D";
		}
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrPub() + "php/pub_get_code.php", key);
		return errRes;
	}

}
