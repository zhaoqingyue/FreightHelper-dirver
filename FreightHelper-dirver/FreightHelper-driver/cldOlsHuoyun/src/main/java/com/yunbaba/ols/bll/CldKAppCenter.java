package com.yunbaba.ols.bll;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.cld.device.CldPhoneNet;
import com.cld.log.CldLog;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.api.CldKAppCenterAPI.IAppsUpgradeListener;
import com.yunbaba.ols.api.CldKAppCenterAPI.IGetRigonListener;
import com.yunbaba.ols.api.CldKAppCenterAPI.IUpgradeListener;
import com.yunbaba.ols.dal.CldDalKAccount;
import com.yunbaba.ols.dal.CldDalKAppCenter;
import com.yunbaba.ols.sap.CldSapKAppCenter;
import com.yunbaba.ols.sap.CldSapUtil;
import com.yunbaba.ols.sap.bean.CldSapKAppParm.MtqAppInfo;
import com.yunbaba.ols.sap.bean.CldSapKAppParm.MtqAppInfoNew;
import com.yunbaba.ols.sap.parse.CldKAccountParse;
import com.yunbaba.ols.sap.parse.CldKAccountParse.ProtAppInfo;
import com.yunbaba.ols.sap.parse.CldKAccountParse.ProtAppStatus;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtKeyCode;
import com.yunbaba.ols.tools.CldErrUtil;
import com.yunbaba.ols.tools.CldSapNetUtil;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;

public class CldKAppCenter {

	private static CldKAppCenter cldKAppCenter;

	private CldKAppCenter() {

	}

	public static CldKAppCenter getInstance() {
		if (cldKAppCenter == null)
			cldKAppCenter = new CldKAppCenter();
		return cldKAppCenter;
	}

	/**
	 * 初始化密钥
	 */
	public void initKey() {
		if (TextUtils.isEmpty(CldSapKAppCenter.getKgoKey())) {
			CldSapReturn errRes = CldSapKAppCenter.initKeyCode();
			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				ProtKeyCode protKeyCode = CldSapParser.parseJson(strRtn,
						ProtKeyCode.class, errRes);
				CldLog.d("ols", errRes.errCode + "");
				CldLog.d("ols", errRes.errMsg);
				CldErrUtil.handleErr(errRes);
				errCodeFix(errRes);
				if (null != protKeyCode && errRes.errCode == 0
						&& !TextUtils.isEmpty(protKeyCode.getCode())) {
					String key = CldSapUtil.decodeKey(protKeyCode.getCode());
					CldSapKAppCenter.setKgoKey(key);
				}
			}
		}
	}

//	/**
//	 * 应用自升级
//	 * 
//	 * @param width
//	 *            分辨率宽
//	 * @param height
//	 *            分辨率高
//	 * @param dpi
//	 *            dpi
//	 * @param systemVer
//	 *            android 系统版本
//	 * @param launcherVer
//	 *            launcher版本号
//	 * @param duid
//	 *            duid
//	 * @param kuid
//	 *            kuid
//	 * @param regionId
//	 *            区域id
//	 * @param customCode
//	 *            客户编号
//	 * @param planCode
//	 *            方案商编号
//	 * @param appParms
//	 *            已安装app
//	 * 
//	 * @return CldSapReturn
//	 */
//	public void getUpgrade(int width, int height, int dpi, String systemVer,
//			String launcherVer, long duid, long kuid, int regionId,
//			int customCode, int planCode, String packname, int vercode,
//			IUpgradeListener listener) {
//		CldSapReturn errRes = new CldSapReturn();
//		if (CldPhoneNet.isNetConnected()) {
//			errRes = CldSapKAppCenter.getUpgrade(width, height, dpi, systemVer,
//					launcherVer, duid, kuid, regionId, customCode, planCode,
//					packname, vercode);
//			if (errRes != null) {
//				String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
//						errRes.jsonPost);
//				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
//				CldErrUtil.handleErr(errRes);
//				CldLog.d("ols", errRes.errCode + "_getAppUpgrade");
//				CldLog.d("ols", errRes.errMsg + "_getAppUpgrade");
//				errCodeFix(errRes);
//				if (errRes.errCode == 0) {
//					MtqAppInfo result = CldKAccountParse.parseAppInfo(strRtn);
//					if (listener != null) {
//						listener.onResult(errRes.errCode, result);
//						if (result != null) {
//							CldDalKAppCenter.getInstance().setMtqAppInfo(result);
//							CldDalKAppCenter.getInstance().setNewVersion(true);
//						}
//					}
//				} else {
//					if (listener != null) {
//						listener.onResult(errRes.errCode, null);
//					}
//				}
//			}
//		} else {
//			errRes.errCode = -2;
//			errRes.errMsg = "网络异常";
//			if (listener != null) {
//				listener.onResult(errRes.errCode, null);
//			}
//		}
//	}

	
	/**
	 * 应用自升级
	 * 
	 * @param width
	 *            分辨率宽
	 * @param height
	 *            分辨率高
	 * @param dpi
	 *            dpi
	 * @param systemVer
	 *            android 系统版本
	 * @param launcherVer
	 *            launcher版本号
	 * @param duid
	 *            duid
	 * @param kuid
	 *            kuid
	 * @param regionId
	 *            区域id
	 * @param customCode
	 *            客户编号
	 * @param planCode
	 *            方案商编号
	 * @param appParms
	 *            已安装app
	 * 
	 * @return CldSapReturn
	 */
	public void getUpgradeForNew(int width, int height, int dpi, String systemVer,
			String launcherVer, long duid, long kuid, int regionId,
			int customCode, int planCode, String packname, int vercode,
			IUpgradeListener listener) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKAppCenter.getUpgradeForNew(width, height, dpi, systemVer,
					launcherVer, duid, kuid, regionId, customCode, planCode,
					packname, vercode);
			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url); //,errRes.jsonPost
				
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
				CldLog.d("ols", errRes.errCode + "_getAppUpgrade");
				CldLog.d("ols", errRes.errMsg + "_getAppUpgrade");
				errCodeFix(errRes);
				if (errRes.errCode == 0) {
					MtqAppInfoNew result = CldKAccountParse.parseAppInfoForNew(strRtn);
		
					if (listener != null) {
						listener.onResult(errRes.errCode, result);
						if (result != null) {
							CldDalKAppCenter.getInstance().setMtqAppInfo(result);
							CldDalKAppCenter.getInstance().setNewVersion(true);
						}
					}
				} else {
					if (listener != null) {
						listener.onResult(errRes.errCode, null);
					}
				}
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
			if (listener != null) {
				listener.onResult(errRes.errCode, null);
			}
		}
	}
	
	/**
	 * 检查已安装的app是否有升级
	 * 
	 * @param width
	 *            分辨率宽
	 * @param height
	 *            分辨率高
	 * @param dpi
	 *            dpi
	 * @param systemVer
	 *            android 系统版本
	 * @param page
	 *            页码
	 * @param size
	 *            每页记录数
	 * @param launcherVer
	 *            launcher版本号
	 * @param duid
	 *            duid
	 * @param kuid
	 *            kuid
	 * @param regionId
	 *            区域id
	 * @param customCode
	 *            客户编号
	 * @param planCode
	 *            方案商编号
	 * @param appParms
	 *            已安装app
	 * 
	 * @return CldSapReturn
	 */
	public void getAppsUpgrade(int width, int height, int dpi,
			String systemVer, int page, int size, String launcherVer,
			long duid, long kuid, int regionId, int customCode, int planCode,
			List<MtqAppInfo> appParms, IAppsUpgradeListener listener) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKAppCenter.getAppsUpgrade(width, height, dpi,
					systemVer, page, size, launcherVer, duid, kuid, regionId,
					customCode, planCode, appParms);
			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
						errRes.jsonPost);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
				CldLog.d("ols", errRes.errCode + "_getAppUpgrade");
				CldLog.d("ols", errRes.errMsg + "_getAppUpgrade");
				errCodeFix(errRes);
				if (errRes.errCode == 0) {
					List<MtqAppInfo> lstOfResult = new ArrayList<MtqAppInfo>();
					CldKAccountParse.parseAppInfos(strRtn, lstOfResult);
					if (listener != null) {
						listener.onListResult(errRes.errCode, lstOfResult);
					}
				}
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
	}

	/**
	 * 获取运营平台推荐的应用列表(并排除终端上已安装的应用)
	 * 
	 * @param systemCode
	 *            操作系统编码(运营平台定义)
	 * @param deviceCode
	 *            设备型号编码(运营平台定义)
	 * @param productCode
	 *            产品型号编码(运营平台定义)
	 * @param width
	 *            分辨率宽
	 * @param height
	 *            分辨率高
	 * @param page
	 *            页码
	 * @param size
	 *            每页记录数
	 * @param launcherVer
	 *            launcher版本号
	 * @param appParms
	 *            已安装app
	 * 
	 * @return CldSapReturn
	 */
	public CldSapReturn getRecdApp(int width, int height, int page, int size,
			String launcherVer, List<MtqAppInfo> appParms) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKAppCenter.getRecdApp(width, height, page, size,
					launcherVer, appParms);
			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
						errRes.jsonPost);
				CldSapParser.parseJson(strRtn, ProtAppInfo.class, errRes);
				CldErrUtil.handleErr(errRes);
				CldLog.d("ols", errRes.errCode + "_getRecdApp");
				CldLog.d("ols", errRes.errMsg + "_getRecdApp");
				errCodeFix(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
		return errRes;
	}

	/**
	 * 获取应用状态信息(返回已下架app的包名)
	 * 
	 * @param appParms
	 *            已安装app
	 * 
	 * @return CldSapReturn
	 */
	public CldSapReturn getAppStatus(List<MtqAppInfo> appParms) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKAppCenter.getAppStatus(appParms);
			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
						errRes.jsonPost);
				CldSapParser.parseJson(strRtn, ProtAppStatus.class, errRes);
				CldErrUtil.handleErr(errRes);
				CldLog.d("ols", errRes.errCode + "_getAppStatus");
				CldLog.d("ols", errRes.errMsg + "_getAppStatus");
				errCodeFix(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
		return errRes;
	}

	/**
	 * 更新应用下载次数
	 * 
	 * @param appParm
	 *            已安装app
	 * 
	 * @return CldSapReturn
	 */
	public CldSapReturn getUpdateAppDowntimes(MtqAppInfo appParm) {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKAppCenter.getUpdateAppDowntimes(appParm);
			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
				CldLog.d("ols", errRes.errCode + "_getUpdateAppDowntimes");
				CldLog.d("ols", errRes.errMsg + "_getUpdateAppDowntimes");
				errCodeFix(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
		return errRes;
	}

	/**
	 * 获取车型列表
	 * 
	 * @return CldSapReturn
	 */
	public CldSapReturn getCarList() {
		CldSapReturn errRes = new CldSapReturn();
		if (CldPhoneNet.isNetConnected()) {
			errRes = CldSapKAppCenter.getCarList();
			if (errRes != null) {
				String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
				CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
				CldErrUtil.handleErr(errRes);
				CldLog.d("ols", errRes.errCode + "_getCarList");
				CldLog.d("ols", errRes.errMsg + "_getCarList");
				errCodeFix(errRes);
			}
		} else {
			errRes.errCode = -2;
			errRes.errMsg = "网络异常";
		}
		return errRes;
	}

	/**
	 * 获取区域名称
	 * 
	 * @param longitude
	 * @param latitude
	 * @param listener
	 */
	public void getRegionDistsName(double longitude, double latitude,
			IGetRigonListener listener) {
		long newLong = (int) (longitude * 3600000);
		long newLat = (int) (latitude * 3600000);
		String params = "&p=" + newLong + "+" + newLat;
		String url = "http://navitest1.careland.com.cn/cgi/pub_getpositioninfo_j.ums?d=1&ct=1"
				+ params;
		CldLog.d("ols", "url location:" + url);
		String json = CldSapNetUtil.sapGetMethod(url);
		CldLog.d("ols", "json location:" + json);
		int regionCityId = CldSapNetUtil.getCityIdJson(json);
		String provinceName = CldSapNetUtil.getNameByJson(json, 1);
		String cityName = CldSapNetUtil.getNameByJson(json, 2);
		String distsName = CldSapNetUtil.getNameByJson(json, 3);

		if (listener != null)
			listener.onResult(regionCityId, provinceName, cityName, distsName);
	}

	/**
	 * 获取区域id
	 * 
	 * @param longitude
	 * @param latitude
	 * @param listener
	 */
	public void getRegionId(double longitude, double latitude,
			final IGetRigonListener listener) {
		long newLong = (int) (longitude * 3600000);
		long newLat = (int) (latitude * 3600000);
		String params = "&p=" + newLong + "+" + newLat;
		String url = "http://navitest1.careland.com.cn/cgi/pub_getpositioninfo_j.ums?d=1&ct=1"
				+ params;
		CldLog.d("ols", "url location:" + url);
		String json = CldSapNetUtil.sapGetMethod(url);
		CldLog.d("ols", "json location:" + json);
		int regionCityId = CldSapNetUtil.getCityIdJson(json);

		if (listener != null)
			listener.onResult(regionCityId, "", "", "");
	}
	
	/**
	 * 错误码处理
	 * 
	 * @param res
	 * @return void
	 * @author zhaoqy
	 * @date 2017-4-24
	 */
	public void errCodeFix(CldSapReturn res) {
		switch (res.errCode) {
		case 501: {
			/**
			 * 被挤下线处理
			 */
			if (res.session.equals(CldDalKAccount.getInstance().getSession())) {
				/**
				 * 当接口使用session和当前帐户里的session相同才挤下线
				 */
				if (!TextUtils.isEmpty(res.session)) {
					CldKAccountAPI.getInstance().sessionInvalid(501, 0);
				}
			}
		}
			break;
		}
	}
}
