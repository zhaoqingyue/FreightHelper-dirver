/*
 * @Title CldKAuthcheck.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:59
 * @version 1.0
 */
package com.yunbaba.ols.bll;

import com.cld.device.CldPhoneNet;
import com.cld.log.CldLog;
import com.yunbaba.ols.sap.CldSapKAuthcheck;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;
import com.yunbaba.ols.tools.CldErrUtil;
import com.yunbaba.ols.tools.CldSapNetUtil;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;



/**
 * 
 * 鉴权
 * 
 * @author Zhouls
 * @date 2015-4-8 下午4:24:20
 */
public class CldKAuthcheck {

	private static CldKAuthcheck cldKAuthcheck;

	/**
	 * Instantiates a new cld k authcheck.
	 */
	private CldKAuthcheck() {
	}

	/**
	 * Gets the single instance of CldKAuthcheck.
	 * 
	 * @return single instance of CldKAuthcheck
	 */
	public static CldKAuthcheck getInstance() {
		if (cldKAuthcheck == null)
			cldKAuthcheck = new CldKAuthcheck();
		return cldKAuthcheck;
	}

	/**
	 * 鉴权
	 * 
	 * @param key
	 *            访问密钥
	 * @param safeCode
	 *            安全码
	 * @return
	 * @return CldSapReturn
	 * @author Zhouls
	 * @date 2015-4-8 下午4:24:07
	 */
	public CldSapReturn authCheck(String key, String safeCode) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKAuthcheck.authCheck(key, safeCode);
			String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
			CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
			CldErrUtil.handleErr(errRes);
			CldLog.d("ols", errRes.errCode + "_authcheck");
			CldLog.d("ols", errRes.errMsg + "_authcheck");
		}
		return errRes;
	}
}
