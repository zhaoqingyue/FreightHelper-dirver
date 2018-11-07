package com.yunbaba.freighthelper.utils;

import com.yunbaba.ols.tools.err.CldOlsErrManager.CldOlsErrCode;

public class ErrCodeUtil {
	
	public static boolean isNetErrCode(int ErrCode){
		boolean ret = false;
		
		if (ErrCode == CldOlsErrCode.NET_OTHER_ERR ||
				ErrCode == CldOlsErrCode.NET_TIMEOUT ||
				ErrCode == CldOlsErrCode.NET_NO_CONNECTED){
			ret = true;
		}
		
		return ret;
	}
}
