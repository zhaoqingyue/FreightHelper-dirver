package com.yunbaba.freighthelper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.yunbaba.freighthelper.bean.LastCarCheckResultBean;

public class GeneralSPHelper {

	Context context;
	SharedPreferences sharedPreferences;

	private static GeneralSPHelper instance;

	public static synchronized GeneralSPHelper getInstance(Context context) {

		if (instance == null)
			instance = new GeneralSPHelper(context.getApplicationContext());

		return instance;
	}

	private GeneralSPHelper(Context context) {
		this.context = context;

		sharedPreferences = context.getSharedPreferences("general", Context.MODE_PRIVATE);

	}

	public void FinishRemindOrder(String cuorderid, int type) {

		Editor editor = sharedPreferences.edit();
		editor.putBoolean("remindorder" + cuorderid + type, true);
		editor.commit();

	}

	public boolean isFinishRemindOrder(String cuorderid, int type) {

		return sharedPreferences.getBoolean("remindorder" + cuorderid + type, false);

	}

	public void setTaskNavi(String taskid, boolean isNavi) {

		Editor editor = sharedPreferences.edit();
		int nv = isNavi?1:0;
		
		editor.putInt("tasknavi2" + taskid, nv);
		editor.commit();

	}

	public boolean isTaskNavi(String taskid) {

		return (sharedPreferences.getInt("tasknavi2" + taskid, -1) == 1);

	}
	
	public int getTaskNaviState(String taskid) {

		return sharedPreferences.getInt("tasknavi2" + taskid, -1);

	}
	
	
	public void setIsSpeechRead(String speechid, boolean res) {

		Editor editor = sharedPreferences.edit();
		editor.putBoolean("speechread" + speechid, res);
		editor.commit();

	}

	public boolean IsSpeechRead(String speechid) {

		return sharedPreferences.getBoolean("speechread" + speechid, false);

	}
	
	public LastCarCheckResultBean getLastCarCheckResult(String carduid) {

		
		String str = sharedPreferences.getString("lastcarcheckresult"+carduid,"");
		if(TextUtils.isEmpty(str))
			return null;
		
		
		return GsonTool.getInstance().fromJson(str, LastCarCheckResultBean.class);
	}
	
	public void setLastCarCheckResult(String carduid,LastCarCheckResultBean res) {

		Editor editor = sharedPreferences.edit();

		String json = GsonTool.getInstance().toJson(res);

		editor.putString("lastcarcheckresult"+carduid, json);
		editor.commit();

	}
//
//	public boolean IsSpeechRead(String speechid) {
//
//		return sharedPreferences.getBoolean("speechread" + speechid, false);
//
//	}

	
	

	public void put(String key, String value) {
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getString(String key) {
		return sharedPreferences.getString(key, "");
	}
	
	
	public void setPCDAddress(String kcode,String Address) {

		Editor editor = sharedPreferences.edit();
		editor.putString(kcode, Address);
		editor.commit();
	}

	public String getPCDAddress(String kcode) {

		return sharedPreferences.getString(kcode, null);
	}

	public boolean ReadFirst() {

		return sharedPreferences.getBoolean("firstopen", true);
	}
	
	public void SetFirst() {

		Editor editor = sharedPreferences.edit();
		editor.putBoolean("firstopen", false);
		editor.commit();
	}

	public boolean isMobileLogin() {
		return sharedPreferences.getBoolean("isMobileLogin", true);
	}


	public void setIsMobileLogin(boolean res) {

		Editor editor = sharedPreferences.edit();
		editor.putBoolean("isMobileLogin", res);
		editor.commit();
	}

	public int isMeNewRemind() {

		return sharedPreferences.getInt("isMeNewRemind", -1);
	}

	public void setIsMeNewRemind(int res) {

		Editor editor = sharedPreferences.edit();
		editor.putInt("isMeNewRemind", res);
		editor.commit();
	}

	public boolean isMeNewRemindClick(int version) {

		return sharedPreferences.getBoolean("isMeNewRemindClick"+version, false);
	}

	public void setIsMeNewRemindClick(int version,boolean res) {

		Editor editor = sharedPreferences.edit();
		editor.putBoolean("isMeNewRemindClick"+version, res);
		editor.commit();
	}

	public void writeCompanyWantRange(String corpid, int range) {
		Editor editor = sharedPreferences.edit();
		editor.putInt("companyrange"+corpid, range);
		editor.commit();

	}

	public int getCompanyWantRange(String corpid) {

		int range =  sharedPreferences.getInt("companyrange"+corpid, 0);
		if(range == 0)
			return 0;
		else
			return range;


	}
}
