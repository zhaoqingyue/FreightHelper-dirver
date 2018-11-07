/*
 * @Title CldKCallNavi.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-21 下午5:19:20
 * @version 1.0
 */
package com.yunbaba.ols.sap.parse;

import java.util.ArrayList;
import java.util.List;

import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;


/**
 * 类注释
 * 
 * @author Zhouls
 * @date 2015-3-21 下午5:19:20
 */
public class CldKCallNaviParse {
	public static class ProtKCallMobile extends ProtBase {
		private List<String> data;

		public ProtKCallMobile() {
			data = new ArrayList<String>();
		}

		public List<String> getData() {
			return data;
		}

		public void setData(List<String> data) {
			this.data = data;
		}
	}
}
