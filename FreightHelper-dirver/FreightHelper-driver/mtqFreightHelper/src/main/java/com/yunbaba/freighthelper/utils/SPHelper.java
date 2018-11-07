package com.yunbaba.freighthelper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.cld.gson.Gson;
import com.cld.gson.reflect.TypeToken;
import com.cld.mapapi.model.PoiInfo;
import com.yunbaba.freighthelper.bean.AddressBean;
import com.yunbaba.freighthelper.bean.CarSpInfo;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.bean.TaskSpInfo;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.AuthInfoList;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
import com.yunbaba.ols.module.delivery.bean.MtqRequestTime;
import com.yunbaba.ols.module.delivery.bean.MtqStore;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//自定义的SharePreference帮助类
public class SPHelper {
	Context context;
	SharedPreferences sharedPreferences;
	static String currentsppath = "";

	private static SPHelper instance;

	public static synchronized SPHelper getInstance(Context context) {

		if (instance == null)
			instance = new SPHelper(context.getApplicationContext());

		// if(SharedPreferencesCompat)

		if (!currentsppath.equals("spfile" + CldKAccountAPI.getInstance().getKuidLogin())) {

			instance.reCreate();
		}

		return instance;
	}

	private SPHelper(Context context) {
		this.context = context;

		currentsppath = "spfile" + CldKAccountAPI.getInstance().getKuidLogin();

		sharedPreferences = context.getSharedPreferences("spfile" + CldKAccountAPI.getInstance().getKuidLogin(),
				Context.MODE_PRIVATE);

	}

	public synchronized CorpBean ReadCurrentSelectCompanyBean() {

		String content = sharedPreferences.getString("currentcompanyid", "");
		if (content != null && !TextUtils.isEmpty(content)) {
			return GsonTool.getInstance().fromJson(content, CorpBean.class);
		} else
			return new CorpBean();

	}


	
	public synchronized void WriteCurrentSelectCompanyBean(CorpBean bean) {
		Editor editor = sharedPreferences.edit();
		editor.putString("currentcompanyid", GsonTool.getInstance().toJson(bean));
		editor.commit();
	}

	
	
	
	public synchronized CorpBean ReadStoreSelectCompanyBean() {

		String content = sharedPreferences.getString("storeselectcompanyid", "");
		if (content != null && !TextUtils.isEmpty(content)) {
			return GsonTool.getInstance().fromJson(content, CorpBean.class);
		} else
			return new CorpBean();

	}


	
	public synchronized void WriteStoreSelectCompanyBean(CorpBean bean) {

		if(TextUtils.isEmpty(bean.getCorpId()))
			return;

		Editor editor = sharedPreferences.edit();
		editor.putString("storeselectcompanyid", GsonTool.getInstance().toJson(bean));
		editor.commit();
	}
	
	
	public synchronized void saveRecentModifyTask(TaskSpInfo taskInfo) {

		put("recentmodifytaskid", taskInfo.taskid);
		put("recentmodifycorpid", taskInfo.corpid);
		put("recentmodifycarlicense",taskInfo.carlicense);
	}

	/**
	 * 获取最近修改的任务 id和企业id
	 */
	public synchronized TaskSpInfo getRecentModifyTask() {

		String taskid = getString("recentmodifytaskid");
		String corpid = getString("recentmodifycorpid");
		String carlicense = getString("recentmodifycarlicense");
		//carlicense = "adgag";
		if (!TextUtils.isEmpty(taskid) && !TextUtils.isEmpty(corpid)&& !TextUtils.isEmpty(carlicense)) {

			return new TaskSpInfo(taskid, corpid,carlicense);

		} else {
			return null;

		}
	}
	
	/**
	 * 获取最近修改的任务的车id和license
	 */
	public CarSpInfo getRecentCarInfo() {
		String recentCarduid = getString("recentCarduid");
		String recentcarlicence = getString("recentcarlicence");
		
		if (!TextUtils.isEmpty(recentcarlicence) && !TextUtils.isEmpty(recentCarduid)){
			return new CarSpInfo(recentCarduid, recentcarlicence);
		}
		return null;
	}
	
	public String getRecentCaruid() {
		String recentCarduid = getString("recentCarduid");
		return recentCarduid;
	}
	
	
	
	
	
	public synchronized AddressBean readLocalStoreAddress(String waybill) {
		String str = getString("storewaybill"+waybill);
		
		
		if (!TextUtils.isEmpty(str)){
			
			try{
				
				AddressBean bean = GsonTool.getInstance().fromJson(str, AddressBean.class);
				return bean;
			}catch(Exception e){
				
			}
			
			
		}
		return null;
	}
	public synchronized void writeLocalStoreAddress(String waybill,AddressBean bean) {
		put("storewaybill"+waybill, GsonTool.getInstance().toJson(bean));
	}




	
	public String getRecentCarLicense() {
		String recentcarlicence = getString("recentcarlicence");
		return recentcarlicence;
	}
	
	public synchronized void saveRecentCaruid(String recentCarduid,String recentcarlicence) {
		put("recentCarduid",recentCarduid);
		put("recentcarlicence",recentcarlicence);
	}

	public void put(String key, String value) {
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getString(String key) {
		return sharedPreferences.getString(key, "");
	}

	
	
	
	
	
	public void saveRecentCheckOrderList(LimitQueue<MtqRequestTime> list) {

		Editor editor = sharedPreferences.edit();

		String json = GsonTool.getInstance().toJson(list.getQueue());

		editor.putString("RecentCheckOrderList", json);
		editor.commit();

	}

	public LimitQueue<MtqRequestTime> getRecentCheckOrderList() {

		String json = sharedPreferences.getString("RecentCheckOrderList", null);

		LimitQueue<MtqRequestTime> list = new LimitQueue<>(20);

		if (json != null) {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<MtqRequestTime>>() {
			}.getType();
			LinkedList<MtqRequestTime> res = new LinkedList<MtqRequestTime>();
			res = gson.fromJson(json, type);

			list.setQueue(res);

		}

		return list;
	}


	public void saveRecentCheckTaskStoreList(LimitQueue<MtqDeliStoreDetail> list,String taskid) {

		Editor editor = sharedPreferences.edit();

		String json = GsonTool.getInstance().toJson(list.getQueue());

		editor.putString("RecentCheckTaskStoreList"+taskid, json);
		editor.commit();

	}

	public LimitQueue<MtqDeliStoreDetail> getRecentCheckTaskStoreList(String taskid) {

		String json = sharedPreferences.getString("RecentCheckTaskStoreList"+taskid, null);

		LimitQueue<MtqDeliStoreDetail> list = new LimitQueue<>(20);

		if (json != null) {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<MtqDeliStoreDetail>>() {
			}.getType();
			LinkedList<MtqDeliStoreDetail> res = new LinkedList<MtqDeliStoreDetail>();
			res = gson.fromJson(json, type);

			list.setQueue(res);

		}

		return list;
	}



//	public void saveRecentCheckTaskList(LimitQueue<MtqDeliTask> list) {
//
//		Editor editor = sharedPreferences.edit();
//
//		String json = GsonTool.getInstance().toJson(list.getQueue());
//
//		editor.putString("RecentCheckMtqDeliTask", json);
//		editor.commit();
//
//	}
//
//	public LimitQueue<MtqDeliTask> getRecentRecentCheckTaskList() {
//
//		String json = sharedPreferences.getString("RecentCheckMtqDeliTask", null);
//
//		LimitQueue<MtqDeliTask> list = new LimitQueue<>(10);
//
//		if (json != null) {
//			Gson gson = new Gson();
//			Type type = new TypeToken<LinkedList<MtqDeliTask>>() {
//			}.getType();
//			LinkedList<MtqDeliTask> res = new LinkedList<MtqDeliTask>();
//			res = gson.fromJson(json, type);
//
//			list.setQueue(res);
//
//		}
//
//		return list;
//	}


		public void saveRecentCheckMarkStoreList(LimitQueue<MtqStore> list,String corpid) {

		Editor editor = sharedPreferences.edit();

		String json = GsonTool.getInstance().toJson(list.getQueue());

		editor.putString("RecentCheckMtqStore"+corpid, json);
		editor.commit();

	}

	public LimitQueue<MtqStore> getRecentCheckMarkStoreList(String corpid) {

		String json = sharedPreferences.getString("RecentCheckMtqStore"+corpid, null);

		LimitQueue<MtqStore> list = new LimitQueue<>(10);

		if (json != null) {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<MtqStore>>() {
			}.getType();
			LinkedList<MtqStore> res = new LinkedList<MtqStore>();
			res = gson.fromJson(json, type);

			list.setQueue(res);

		}

		return list;
	}


	public void saveRecentSearchPoiInfo(LimitQueue<PoiInfo> list) {

		Editor editor = sharedPreferences.edit();

		String json = GsonTool.getInstance().toJson(list.getQueue());

		editor.putString("RecentSearchPoiInfo", json);
		editor.commit();

	}

	public LimitQueue<PoiInfo> getRecentSearchPoiInfo() {

		String json = sharedPreferences.getString("RecentSearchPoiInfo", null);

		LimitQueue<PoiInfo> list = new LimitQueue<>(20);

		if (json != null) {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<PoiInfo>>() {
			}.getType();
			LinkedList<PoiInfo> res = new LinkedList<PoiInfo>();
			res = gson.fromJson(json, type);

			list.setQueue(res);

		}

		return list;
	}
	
	
	public void saveRecentCheckStoreList(LimitQueue<MtqDeliStoreDetail> list) {

		Editor editor = sharedPreferences.edit();

		String json = GsonTool.getInstance().toJson(list.getQueue());

		editor.putString("RecentCheckStoreList", json);
		editor.commit();

	}

	public LimitQueue<MtqDeliStoreDetail> getRecentCheckStoreList() {

		String json = sharedPreferences.getString("RecentCheckStoreList", null);

		LimitQueue<MtqDeliStoreDetail> list = new LimitQueue<>(20);

		if (json != null) {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<MtqDeliStoreDetail>>() {
			}.getType();
			LinkedList<MtqDeliStoreDetail> res = new LinkedList<MtqDeliStoreDetail>();
			res = gson.fromJson(json, type);

			list.setQueue(res);

		}

		return list;
	}

	public boolean isHasFinishTask(String corpid) {
		return sharedPreferences.getBoolean("ishastaskfinish" + corpid, false);
	}
	
	public boolean isHasFeedBack(String corpid,String taskid,String waybill) {
		return sharedPreferences.getBoolean("ishasfeedback" + corpid + taskid + waybill, false);
	}

	public void setIsHasFinishTask(String corpid, boolean ishastaskfinish) {

		Editor editor = sharedPreferences.edit();
		editor.putBoolean("ishastaskfinish" + corpid, ishastaskfinish);
		editor.commit();
	}






	public void setIsHasFeedBack(String corpid, String taskid,String waybill,boolean ishastaskfinish) {

		Editor editor = sharedPreferences.edit();
		editor.putBoolean("ishasfeedback" + corpid + taskid + waybill, ishastaskfinish);
		editor.commit();
	}



	public int isFinalUnFinishPointIsReturnPoint(String taskid) {
	//	MLog.e("获取状态","任务点是否是返程点");
		return sharedPreferences.getInt("isFinalUnFinishPointIsReturnPoint" + taskid, 0);
	}


	public void setIsFinalUnFinishPointIsReturnPoint(String taskid, int res) {
		//MLog.e("设置状态","最后任务点是否是返程点"+res);
		Editor editor = sharedPreferences.edit();
		editor.putInt("isFinalUnFinishPointIsReturnPoint" + taskid, res);
		editor.commit();
	}


	public synchronized void reCreate() {

		MLog.e("sphelper", "recreate");

		currentsppath = "spfile" + CldKAccountAPI.getInstance().getKuidLogin();

		sharedPreferences = context.getSharedPreferences("spfile" + CldKAccountAPI.getInstance().getKuidLogin(),
				Context.MODE_PRIVATE);

	}

	public void deleteRecentCheckStoreList(MtqDeliTaskDetail detail) {

		String json = sharedPreferences.getString("RecentCheckStoreList", null);

		LimitQueue<MtqDeliStoreDetail> list = new LimitQueue<>(20);

		if (json != null) {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<MtqDeliStoreDetail>>() {
			}.getType();
			LinkedList<MtqDeliStoreDetail> res = new LinkedList<MtqDeliStoreDetail>();
			res = gson.fromJson(json, type);

			list.setQueue(res);

		}

		Iterator<MtqDeliStoreDetail> iter = list.getQueue().iterator();
		MtqDeliStoreDetail tmp;
		while (iter.hasNext()) {
			tmp = iter.next();

			MLog.e("removecheck", tmp.taskId);

			for (MtqDeliStoreDetail store : detail.store) {

				if (tmp.waybill.equals(store.waybill)) {
					// MLog.e("remove", tmp.cust_orderid);
					iter.remove();
					// MLog.e("check", "delete taskid
					// remove"+taskIdList.toString());
				}
			}
		}

		Editor editor = sharedPreferences.edit();

		String json2 = GsonTool.getInstance().toJson(list.getQueue());

		editor.putString("RecentCheckStoreList", json2);
		editor.commit();

	}

	public ArrayList<MtqDeliTask> GetBackUpLastTimeTaskList() {

		String json = sharedPreferences.getString("backuplasttimetasklist", null);

		ArrayList<MtqDeliTask> list =  new ArrayList<MtqDeliTask>();

		if (json != null) {
			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<MtqDeliTask>>() {
			}.getType();

			list = gson.fromJson(json, type);

		}

		return list;
	}

	
	
	
	public List<AuthInfoList>  readStoreAuth() {


		String json = sharedPreferences.getString("storeAuth", null);

		List<AuthInfoList> list =  new ArrayList<AuthInfoList>();

		if (json != null) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<AuthInfoList>>() {
			}.getType();

			list = gson.fromJson(json, type);

		}

		return list;

	}

	public void writeStoreAuth(List<AuthInfoList> list) {

		
		Editor editor = sharedPreferences.edit();

		String json2 = GsonTool.getInstance().toJson(list);

		editor.putString("storeAuth", json2);
		editor.commit();

	}
	

	public void SetBackUpLastTimeTaskList() {

		Editor editor = sharedPreferences.edit();
		editor.putString("backuplasttimetasklist", "");
		editor.commit();

	}

	public void deleteRecentCheckOrderList(ArrayList<String> taskIdList) {

		String json = sharedPreferences.getString("RecentCheckOrderList", null);

		LimitQueue<MtqRequestTime> list = new LimitQueue<>(20);

		if (json != null) {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<MtqRequestTime>>() {
			}.getType();
			LinkedList<MtqRequestTime> res = new LinkedList<MtqRequestTime>();
			res = gson.fromJson(json, type);

			list.setQueue(res);

		}

		Iterator<MtqRequestTime> iter = list.getQueue().iterator();
		MtqRequestTime tmp;
		while (iter.hasNext()) {
			tmp = iter.next();

			//MLog.e("removecheck", tmp.taskId);

			for (String rmtaskid : taskIdList) {

				if (tmp.taskid.equals(rmtaskid)) {
					// MLog.e("remove", tmp.cust_orderid);
					iter.remove();
					// MLog.e("check", "delete taskid
					// remove"+taskIdList.toString());
				}
			}
		}

		Editor editor = sharedPreferences.edit();

		String json2 = GsonTool.getInstance().toJson(list.getQueue());

		editor.putString("RecentCheckOrderList", json2);
		editor.commit();
	}

}
