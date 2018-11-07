package com.yunbaba.ols.api;

import java.util.List;

import android.text.TextUtils;

import com.cld.base.CldBase;
import com.cld.setting.CldSetting;
import com.yunbaba.ols.api.CldOlsInit.ICldOlsInitListener;
import com.yunbaba.ols.bll.CldKAppCenter;
import com.yunbaba.ols.dal.CldDalKAppCenter;
import com.yunbaba.ols.sap.bean.CldSapKAppParm.MtqAppInfo;
import com.yunbaba.ols.sap.bean.CldSapKAppParm.MtqAppInfoNew;
import com.yunbaba.ols.tools.AppInfoUtils;
import com.yunbaba.ols.tools.CldSapReturn;

/**
 * 应用中心相关模块，提供应用中心相关接口
 * 
 * @author zhaoqy
 */
public class CldKAppCenterAPI {

	private static CldKAppCenterAPI cldKAppCenterAPI;

	private CldKAppCenterAPI() {
	}

	public static CldKAppCenterAPI getInstance() {
		if (cldKAppCenterAPI == null) {
			cldKAppCenterAPI = new CldKAppCenterAPI();
		}
		return cldKAppCenterAPI;
	}

	public void init() {

	}

	public void uninit() {

	}

	/**
	 * 初始化密钥
	 * 
	 * @param listener
	 * @return void
	 */
	public void initKey(final ICldOlsInitListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldKAppCenter.getInstance().initKey();
				if (null != listener) {
					listener.onInitReslut();
				}
			}
		}).start();
	}

	/**
	 * 获取应用版本信息
	 */
	public MtqAppInfoNew getMtqAppInfo() {
		return CldDalKAppCenter.getInstance().getMtqAppInfo();
	}

	/**
	 * 是否存在新版本
	 */
	public boolean hasNewVersion() {
		return CldDalKAppCenter.getInstance().getNewVersion();
	}

	/**
	 * 清除版本信息
	 */
	public void clearAppVersion() {
		CldDalKAppCenter.getInstance().uninit();
	}

	/**
	 * 服务获取duid接口
	 */
	public long getDuid() {
		String strDuid = CldSetting.getString("duid");
		if (TextUtils.isEmpty(strDuid)) {
			return 0;
		} else {
			return Long.parseLong(strDuid);
		}
	}

	/**
	 * 服务获取kuid接口
	 */
	public long getKuid() {
		String strKuid = CldSetting.getString("kuid");
		if (TextUtils.isEmpty(strKuid)) {
			return 0;
		} else {
			return Long.parseLong(strKuid);
		}
	}

	/**
	 * 获取宽度
	 */
	public int getWidth() {
		String strWidth = CldSetting.getString("width");
		if (TextUtils.isEmpty(strWidth)) {
			int width = AppInfoUtils.getDensityWidth(CldBase.getAppContext());
			CldSetting.put("width", width + "");
			return width;
		} else {
			return Integer.parseInt(strWidth);
		}
	}

	/**
	 * 获取高度
	 */
	public int getHeight() {
		String strHeight = CldSetting.getString("height");
		if (TextUtils.isEmpty(strHeight)) {
			int height = AppInfoUtils.getDensityHeight(CldBase.getAppContext());
			CldSetting.put("height", height + "");
			return height;
		} else {
			return Integer.parseInt(strHeight);
		}
	}

	/**
	 * 获取dpi
	 */
	public int getDpi() {
		String strDpi = CldSetting.getString("dpi");
		if (TextUtils.isEmpty(strDpi)) {
			int dpi = AppInfoUtils.getDensityDpi(CldBase.getAppContext());
			CldSetting.put("dpi", dpi + "");
			return dpi;
		} else {
			return Integer.parseInt(strDpi);
		}
	}

	/**
	 * 获取系统版本
	 */
	public String getSystemVer() {
		String strSystemVer = CldSetting.getString("systemVer");
		if (TextUtils.isEmpty(strSystemVer)) {
			String systemVer = AppInfoUtils.getSystemVer();
			CldSetting.put("systemVer", systemVer);
			return systemVer;
		} else {
			return strSystemVer;
		}
	}

	/**
	 * 获取包名
	 */
	public String getPackName() {
		return AppInfoUtils.getPackName(CldBase.getAppContext());
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	public int getVerCode() {
		return AppInfoUtils.getVerCode(CldBase.getAppContext());
	}



	public String getmVerName() {
		return "android-"+AppInfoUtils.getVerName(CldBase.getAppContext());
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
	 */
	public void getUpgrade(final int regionId, final int customCode,
			final int planCode, final IUpgradeListener listener) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				int width = getWidth();
				int height = getHeight();
				int dpi = getDpi();
				String systemVer = getSystemVer();
				long duid = getDuid();
				long kuid = getKuid();
				String packname = getPackName();
				int vercode = getVerCode();

				CldKAppCenter.getInstance().getUpgradeForNew(width, height, dpi,
						systemVer, "", duid, kuid, regionId, customCode,
						planCode, packname, vercode, listener);
			}
		}).start();
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
	 */
	public void getAppsUpgrade(final int width, final int height,
			final int dpi, final String systemVer, final int page,
			final int size, final String launcherVer, final long duid,
			final long kuid, final int regionId, final int customCode,
			final int planCode, final List<MtqAppInfo> appParms,
			final IAppsUpgradeListener listener) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				CldKAppCenter.getInstance().getAppsUpgrade(width, height, dpi,
						systemVer, page, size, launcherVer, duid, kuid,
						regionId, customCode, planCode, appParms, listener);
			}
		}).start();
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
	 */
	public void getRecdApp(final int width, final int height, final int page,
			final int size, final String launcherVer,
			final List<MtqAppInfo> appParms, final IAppCenterListener listener) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKAppCenter.getInstance().getRecdApp(
						width, height, page, size, launcherVer, appParms);
				if (listener != null && errRes != null) {
					listener.onResult(errRes.errCode, errRes.jsonReturn);
				}
			}
		}).start();
	}

	/**
	 * 获取应用状态信息(返回已下架app的包名)
	 * 
	 * @param appParms
	 *            已安装app
	 */
	public void getAppStatus(final List<MtqAppInfo> appParms,
			final IAppCenterListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKAppCenter.getInstance().getAppStatus(
						appParms);
				if (listener != null && errRes != null) {
					listener.onResult(errRes.errCode, errRes.jsonReturn);
				}
			}
		}).start();
	}

	/**
	 * 更新应用下载次数
	 * 
	 * @param appParm
	 *            已安装app
	 */
	public void getUpdateAppDowntimes(final MtqAppInfo appParm,
			final IAppCenterListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKAppCenter.getInstance()
						.getUpdateAppDowntimes(appParm);
				if (listener != null && errRes != null) {
					listener.onResult(errRes.errCode, errRes.jsonReturn);
				}
			}
		}).start();
	}

	/**
	 * 获取车型列表
	 * 
	 * @param listener
	 */
	public void getCarList(final IAppCenterListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				CldSapReturn errRes = CldKAppCenter.getInstance().getCarList();
				if (listener != null && errRes != null) {
					listener.onResult(errRes.errCode, errRes.jsonReturn);
				}
			}
		}).start();
	}

	/**
	 * 获取区域名称
	 * 
	 * @param longitude
	 * @param latitude
	 * @param listener
	 */
	public void getRegionDistsName(final double longitude,
			final double latitude, final IGetRigonListener listener) {
		if (listener == null)
			return;

		new Thread(new Runnable() {

			@Override
			public void run() {
				CldKAppCenter.getInstance().getRegionDistsName(longitude,
						latitude, listener);
			}
		}).start();
	}

	/**
	 * 获取区域id
	 * 
	 * @param longitude
	 * @param latitude
	 * @param listener
	 */
	public void getRegionId(final double longitude, final double latitude,
			final IGetRigonListener listener) {
		if (listener == null)
			return;

		new Thread(new Runnable() {

			@Override
			public void run() {
				CldKAppCenter.getInstance().getRegionDistsName(longitude,
						latitude, listener);
			}
		}).start();
	}

	/**
	 * 获取区域id回调监听
	 * 
	 * @author zhaoqy
	 * @date 2017-2-16
	 */
	public static interface IGetRigonListener {
		/**
		 * 结果回调
		 * 
		 * @param regionId
		 * @param provinceName
		 * @param cityName
		 * @param distsName
		 */
		public void onResult(int regionId, String provinceName,
				String cityName, String distsName);
	}

	/**
	 * 应用中心回调监听
	 * 
	 * @author zhaoqy
	 * @date 2017-2-9
	 */
	public static interface IAppCenterListener {
		/**
		 * 结果回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onResult(int errCode, String jsonString);
	}

	/**
	 * 应用中心回调监听
	 * 
	 * @author zhaoqy
	 * @date 2017-4-19
	 */
	public static interface IUpgradeListener {

		/**
		 * 结果回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onResult(int errCode, MtqAppInfoNew result);
	}

	/**
	 * 应用中心回调监听
	 * 
	 * @author zhaoqy
	 * @date 2017-2-9
	 */
	public static interface IAppsUpgradeListener {

		/**
		 * 结果回调
		 * 
		 * @param errCode
		 * @param jsonString
		 */
		public void onListResult(int errCode, List<MtqAppInfo> lstOfResult);
	}

}
