/*
 * @Title CldSapKAuthcheck.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.ols.sap;

import java.util.HashMap;
import java.util.Map;

import com.yunbaba.ols.sap.parse.CldKBaseParse;
import com.yunbaba.ols.tools.CldSapReturn;


/**
 * KEY鉴权协议接口
 * 
 * @author Zhouls
 * @date 2014-12-9 下午5:50:22
 */
public class CldSapKAuthcheck {

	/**
	 * KEY鉴权(501)
	 * 
	 * @param key
	 *            访问密钥
	 * @param safe_code
	 *            安全码
	 * @return 0为成功，1为参数内容格式不合法，2为安全码与访问密钥不匹配， 3为访问密钥不存在或已删除，4为用户未审核，5为安全码无权限访问，
	 *         6为调用次数超额 ，100为系统错误 int
	 * @author Zhouls
	 * @date 2014-12-9 下午5:57:56
	 */
	public static CldSapReturn authCheck(String key, String safe_code) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", key);
		map.put("safe_code", safe_code);
		CldSapReturn errRes = CldKBaseParse.getGetParms(map,
				CldSapUtil.getNaviSvrAC() + "console/api/authcheck_mobile.php",
				null);
		return errRes;
	}
}
