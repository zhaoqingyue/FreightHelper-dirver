package com.yunbaba.api.trunk;

import android.text.TextUtils;

import com.cld.nv.map.CldMapApi;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.api.trunk.bean.OnUIResult;
import com.yunbaba.api.trunk.bean.UploadGoodScanRecordResult;
import com.yunbaba.api.trunk.listner.OnObjectListner;
import com.yunbaba.api.trunk.listner.OnQueryResultListner;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqGoodDetail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hmi.packages.HPDefine.HPWPoint;

/**
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: WaybillManager.java
 * @Prject: Freighthelper
 * @Package: com.mtq.api.trunk
 * @Description: 运单扫描管理类
 * @author: zsx
 * @date: 2017-4-19 下午3:24:05
 * @version: V1.0
 */
public class WaybillManager {
    private static String  TAG = WaybillManager.class.getSimpleName();
    private static WaybillManager mWaybillMangager;
    
    //由于数据要共享，所以在这边保存一份，就不在每个activity 传来传去
    private MtqDeliOrderDetail mOrderDetail = null;
    private  MtqDeliStoreDetail mStoreDetail = null;
    
    private boolean successUpload = false;
    
    private int mScanNum = 0;
    private int mTotalNum = 0;
    
    private int mScanArray[];
    private int mToatalArray[];
    
    private ArrayList<ScanDetail> mScanDetailList;
    
    public static WaybillManager getInstance (){
    	synchronized (TAG) {
    		if (mWaybillMangager == null){
    			synchronized (TAG) {
    				mWaybillMangager = new WaybillManager();
    				mWaybillMangager.init();
				}
    		}
		}
    	
    	return mWaybillMangager;
    }
    
    public void init (){
    	mScanDetailList = new ArrayList<ScanDetail>();
    }
    
    public MtqDeliOrderDetail getmOrderDetail() {
		return mOrderDetail;
	}

	public void setmOrderDetail(MtqDeliOrderDetail mOrderDetail) {
		this.mOrderDetail = mOrderDetail;
	}

	public MtqDeliStoreDetail getmStoreDetail() {
		return mStoreDetail;
	}

	public void setmStoreDetail(MtqDeliStoreDetail mStoreDetail) {
		this.mStoreDetail = mStoreDetail;
	}


	public boolean isSuccessUpload() {
		return successUpload;
	}

	public void setSuccessUpload(boolean successUpload) {
		this.successUpload = successUpload;
	}
	

	public int getmScanNum() {
		return mScanNum;
	}

	public void setmScanNum(int mScanNum) {
		this.mScanNum = mScanNum;
	}

	public int getmTotalNum() {
		return mTotalNum;
	}

	public void setmTotalNum(int mTotalNum) {
		this.mTotalNum = mTotalNum;
	}
	
	public ArrayList<ScanDetail> getmScanDetailList() {
		return mScanDetailList;
	}

	public void setmScanDetailList(ArrayList<ScanDetail> mScanDetailList) {
		this.mScanDetailList = mScanDetailList;
	}

	/**
     * @Title: UpLoadGoodScanRecord
     * @Description: 上传条形码
     * @param storeDetail
     * @param orderDetail
     * @param bar_code：输入或者扫描的条形码
     * @param upTime：上传的时间
     * @param scan_x：上传的坐标
     * @param scan_y：上传的坐标Y
     * @param callback：UI回调
     * @return: void
     */
    public void UpLoadGoodScanRecord(final MtqDeliStoreDetail storeDetail,final MtqDeliOrderDetail orderDetail,
    		final String bar_code,final long upTime, int scan_x, int scan_y,final OnUIResult callback){
    	
    		DeliveryApi.getInstance().UpLoadGoodScanRecord(storeDetail.corpId, storeDetail.taskId,
    				storeDetail.waybill, "", bar_code, upTime, scan_x, scan_y,
    				new OnResponseResult<Integer>(){

					@Override
					public void OnResult(Integer result) {

						MLog.v(TAG, "Succes");
						
						if (result != 0){
							if (callback != null){
								callback.OnError(result);
							}
						}else{
							successUpload = true;
							updateOrderDetail(orderDetail, bar_code,false);
							savaScanRecord(storeDetail,orderDetail,bar_code,upTime);
							if (callback != null){
								callback.OnResult();
							}
						}
					}

					@Override
					public void OnError(int ErrCode) {

						MLog.v(TAG, "fail ErrCode =" + ErrCode);
						if (ErrCode == 1406){
							//在这里添加是为了处理多终端扫描同个单的问题。
							successUpload = true;
							updateOrderDetail(orderDetail, bar_code,true);
							savaScanRecord(storeDetail,orderDetail,bar_code,upTime);
						}
						if (callback != null){
							callback.OnError(ErrCode);
						}
					}

					@Override
					public void OnGetTag(String Reqtag) {

						
					}
    		
    	});
    }
    
  
    /**
     * @Title: updateOrderDetail
     * @Description: 更新OrderDetail 的内容
     * @param orderDetail 
     * @param bar_code :条形码
     * @param isOver 是不是超过扫描次数
     * @return: void
     */
    private void updateOrderDetail(MtqDeliOrderDetail orderDetail,String bar_code,boolean isOver){
    	Iterator<MtqGoodDetail> iter = orderDetail.goods.iterator();
    	mScanNum = 0;
    	mTotalNum = 0;
    	while(iter.hasNext()){
    		MtqGoodDetail goodDetail = iter.next();
    		if (!TextUtils.isEmpty(goodDetail.bar_code) && goodDetail.bar_code.equals(bar_code)){
    			if (isOver){
    				//已经超过扫描的个数
    				goodDetail.scan_cnt = goodDetail.amount;
    			}else{
    				goodDetail.scan_cnt++;
    				if (goodDetail.scan_cnt > goodDetail.amount){
    					goodDetail.scan_cnt = goodDetail.amount;
    				}
    			}
    			mScanNum += goodDetail.scan_cnt;
    			mTotalNum += goodDetail.amount;
    		}else{
    			mScanNum += goodDetail.scan_cnt;
    			mTotalNum += goodDetail.amount;
    		}
    	}
    }
    
    
    private void savaScanRecord(final MtqDeliStoreDetail storeDetail, final MtqDeliOrderDetail orderDetail,
    		 final String bar_code,final long upTime){
    	
    	 getOneScanRecordByKeyFromDB(storeDetail.taskId, "", bar_code, new OnObjectListner<UploadGoodScanRecordResult>() {
			 @Override
			 public void onResult(UploadGoodScanRecordResult res) {
				 UploadGoodScanRecordResult record = res;
				 if (record == null){
					 String key = storeDetail.taskId + storeDetail.waybill + bar_code;
					 record = new UploadGoodScanRecordResult();
					 record.taskAndbarCode = key;
				 }

				 record.searchKey = storeDetail.taskId + storeDetail.waybill;
				 record.address = storeDetail.storeaddr;
				 record.uploadDate = upTime;

				 Iterator<MtqGoodDetail> iter = orderDetail.goods.iterator();
				 int scanNum = record.scan_cnt;
				 int totalNmu = record.amount;
				 while(iter.hasNext()){
					 MtqGoodDetail goodDetail = iter.next();
					 if (!TextUtils.isEmpty(goodDetail.bar_code) && goodDetail.bar_code.equals(bar_code)){
						 scanNum = goodDetail.scan_cnt;
						 totalNmu = goodDetail.amount;
						 break;
					 }
				 }
				 record.scan_cnt = scanNum;
				 record.amount = totalNmu;
				 saveScanRecordToBD(record);
			 }
		 });
    	

    }
    
    
    /**
     * @Title: UpLoadGoodScanRecord
     * @Description: 上传条形码
     * @param storeDetail
     * @param orderDetail
     * @param bar_code：输入或者扫描的条形码
     * @param upTime：上传的时间
     * @param callback：UI回调
     * @return: void
     */
    public void UpLoadGoodScanRecord(MtqDeliStoreDetail storeDetail,MtqDeliOrderDetail orderDetail,
    		String bar_code,long upTime,OnUIResult callback){
    	HPWPoint point = CldMapApi.getNMapCenter();
    	UpLoadGoodScanRecord(storeDetail,orderDetail,bar_code,upTime,(int)point.x,(int)point.y,callback);
    }
    
    
	/**
	 * @Title: saveScanRecordToBD
	 * @Description: 保存单个扫描记录到数据库
	 * @param record
	 * @return: void
	 */
	public void saveScanRecordToBD(UploadGoodScanRecordResult record) {
		OrmLiteApi.getInstance().save(record);

	}
	
	
	/**
	 * @Title: getOneScanRecordByKeyFromDB
	 * @Description: 通过key从数据库获取扫描记录
	 * @param taskId
	 * @param barCode
	 * @return
	 * @return: UploadGoodScanRecordResult
	 */
	public void getOneScanRecordByKeyFromDB(String taskId,String cust_order_id ,String barCode,
																  final OnObjectListner<UploadGoodScanRecordResult> listner) {
		String key = taskId + cust_order_id + barCode;
		
		getOneScanRecordByKeyFromDB(key,listner);
	}
	
	/**
	 * @Title: getOneScanRecordByKeyFromDB
	 * @Description: 通过key从数据库获取扫描记录
	 * @param key
	 * @return
	 * @return: UploadGoodScanRecordResult
	 */
	public void getOneScanRecordByKeyFromDB(String key, final OnObjectListner<UploadGoodScanRecordResult> listner) {
		
		 OrmLiteApi.getInstance().queryByKey(UploadGoodScanRecordResult.class,
				 UploadGoodScanRecordResult.COL_KEY, key, new OnQueryResultListner<UploadGoodScanRecordResult>() {
					 @Override
					 public void onResult(List<UploadGoodScanRecordResult> res) {

						 if (res == null || res.size() == 0) {

							 if(listner!=null)
							   listner.onResult(null);
						 }
						 else {
							 if(listner!=null)
								 listner.onResult(res.get(0));

						 }

					 }
				 });
		

	}
	
	/**
	 * @Title: queryAllBySearchKey
	 * @Description: 通过搜索key值查找记录
	 * @param searchkey
	 * @return
	 * @return: List<UploadGoodScanRecordResult>
	 */
	public void queryAllBySearchKey(String searchkey,final OnObjectListner< List<UploadGoodScanRecordResult>> listner){
		
			queryAllByColName(UploadGoodScanRecordResult.COL_SEARCH_KEY, searchkey,listner);
	}
	
	
	/**
	 * @Title: queryAllByColName
	 * @Description: 通过列名和key值来找记录
	 * @param COL_NAME
	 * @param searchkey
	 * @return
	 * @return: List<UploadGoodScanRecordResult>
	 */
	public void queryAllByColName(String COL_NAME, String searchkey,final OnObjectListner< List<UploadGoodScanRecordResult>> listner){
		
		OrmLiteApi.getInstance().queryByKey(UploadGoodScanRecordResult.class,
				COL_NAME, searchkey, new OnQueryResultListner<UploadGoodScanRecordResult>() {
					@Override
					public void onResult(List<UploadGoodScanRecordResult> res) {
						if (res == null || res.size() == 0) {

							if(listner!=null)
								listner.onResult(null);
						}
						else {
							if(listner!=null)
								listner.onResult(res);

						}
					}
				});
		

	}
	
	
	/**
	 * @Title: getAllScanRecordFromDB
	 * @Description: 从数据库获取所有的扫描记录
	 * @return
	 * @return: List<UploadGoodScanRecordResult>
	 */
	public void getAllScanRecordFromDB(final OnObjectListner<List<UploadGoodScanRecordResult>> listner) {
		
		OrmLiteApi.getInstance().queryAll(UploadGoodScanRecordResult.class, new OnQueryResultListner<UploadGoodScanRecordResult>() {
			@Override
			public void onResult(List<UploadGoodScanRecordResult> res) {

				if (res == null || res.size() == 0) {

					if(listner!=null)
						listner.onResult(null);
				}
				else {
					if(listner!=null)
						listner.onResult(res);

				}
			}
		});
		

	}
	
	
	public static class ScanDetail{
		public int scanNum;
		public int totalNum;
		public String codeBar;
	}

}
