package com.yunbaba.ols.sap.bean;


public class CldSapKAppParm {
	
	public static class MtqAppInfo {
		/** 应用图标URL */
		public String app_icon; 
		/** 应用名称 */
		public String app_name; 
		/** 应用版本名称 */
		public String ver_name; 
		/** apk下载地址 */
		public String app_url; 
		/** 更新描述 */
		public String upgrade_desc; 
		/** 安装包大小（单位：字节） */
		public int pack_size; 
		/** 应用版本编码 */
		public int ver_code; 
		/** 应用包名 */
		public String pack_name; 
		/** 是否静默安装 0：否; 1：是  */
		public int quiesce; 
		/** 下载次数 */
		public int down_times; 
		/** 是否需要验证 0：否; 1：是  */
		public int validate; 
		/** 应用描述 */
		public String app_desc; 
	}
	
	public static class MtqAppInfoNew {
		
		public int version;
		public int publishtime;
		public int filesize;
		
		//强制升级标识， 1：是；0：否
		public int upgradeflag;
		
		
		public int expiredtime;
		
		//下载标识，1：下载；2：删除
		public int downloadtype;
		//压缩标识，1：压缩；0：非压缩
		public int zipflag;
		public String filepath;
		public String mark;
		
		
		
//		/** 应用图标URL */
//		public String app_icon; 
//		/** 应用名称 */
//		public String app_name; 
//		/** 应用版本名称 */
//		public String ver_name; 
//		/** apk下载地址 */
//		public String app_url; 
//		/** 更新描述 */
//		public String upgrade_desc; 
//		/** 安装包大小（单位：字节） */
//		public int pack_size; 
//		/** 应用版本编码 */
//		public int ver_code; 
//		/** 应用包名 */
//		public String pack_name; 
//		/** 是否静默安装 0：否; 1：是  */
//		public int quiesce; 
//		/** 下载次数 */
//		public int down_times; 
//		/** 是否需要验证 0：否; 1：是  */
//		public int validate; 
//		/** 应用描述 */
//		public String app_desc; 
	}
}
