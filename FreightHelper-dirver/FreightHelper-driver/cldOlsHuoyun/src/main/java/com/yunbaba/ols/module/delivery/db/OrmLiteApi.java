//package com.mtq.ols.module.delivery.db;
//
//import java.io.File;
//import java.util.List;
//
//import com.litesuits.orm.BuildConfig;
//import com.litesuits.orm.LiteOrm;
//import com.litesuits.orm.db.DataBaseConfig;
//import com.litesuits.orm.db.assit.QueryBuilder;
//import com.mtq.apitest.application.CldOlsApplication;
//import com.mtq.ols.api.CldKAccountAPI;
//
//import android.content.Context;
//import android.os.Environment;
//import android.util.Log;
//
///**
// * @description ORM数据库操作的封装，对外提供接口
// * @author ligangfan
// *
// */
//public class OrmLiteApi {
//
//	//可以指定数据库文件保存的位置
////	public static final String SD_CARD = Environment.getExternalStorageDirectory().getAbsolutePath();
////	public static final String DB_NAME = SD_CARD + "/lite/orm/cascade.db";
//	private String DB_NAME_PATH ;//= FreightHelperApplication.getContext().getDir("db", Context.MODE_PRIVATE).getAbsolutePath();
//	private String DB_NAME;// = "mtq_" + CldKAccountAPI.getInstance().getKuid() + "_data.db";
//	private LiteOrm mLiteOrm = null;
//
//	private static OrmLiteApi mInstance = null;
//
//
//
//	public void init(Context context){
//
//		//DB_NAME_PATH = context.getDir("db", Context.MODE_PRIVATE).getAbsolutePath();
//		DB_NAME =context.getDatabasePath("mtq_" + CldKAccountAPI.getInstance().getKuidLogin()+ "_data.db").getAbsolutePath();
//
//		//DB_NAME = ;
//	}
//
//	private OrmLiteApi(){
//
//		if(DB_NAME == null )
//		{
//
//		}
//
//
//		DataBaseConfig dataBaseConfig = new DataBaseConfig(CldOlsApplication.getContext());
////		dataBaseConfig.dbName = DB_NAME_PATH + File.separator + DB_NAME;
//		dataBaseConfig.dbName = DB_NAME;
//		dataBaseConfig.dbVersion = 1;
//		dataBaseConfig.debugged = BuildConfig.DEBUG;
//		mLiteOrm = LiteOrm.newCascadeInstance(dataBaseConfig);
//
//
//
//	}
//
//	public static OrmLiteApi getInstance(){
//		if (mInstance == null) {
//			synchronized (OrmLiteApi.class) {
//				if (mInstance == null) {
//					mInstance = new OrmLiteApi();
//				}
//			}
//		}
//		return mInstance;
//	}
//
//	public long save(Object object){
//		if (object == null) {
//			return -1;
//		}
//		return mLiteOrm.save(object);
//	}
//
//	public <T> void saveAll(List<T> list){
//		if (list == null || list.isEmpty()) {
//			return;
//		}
//		mLiteOrm.save(list);
//	}
//
//	public void delete(Object object){
//		if (object == null) {
//			return;
//		}
//		mLiteOrm.delete(object);
//	}
//
//	public <T> List<T> queryAll(Class<T> tClass){
//		if (tClass == null) {
//			return null;
//		}
//		return mLiteOrm.query(tClass);
//	}
//
//	/**
//	 * 使用QueryBuilder进行查找，可以进行扩展
//	 * 根据具体的字段进行扩展
//	 * @param tClass
//	 * @param id
//	 * @return
//	 */
//	public <T> List<T> queryById(Class<T> tClass, long id){
//		if (tClass == null) {
//			return null;
//		}
//		QueryBuilder<T> qb = new QueryBuilder<T>(tClass);
//		qb.whereEquals("_id", id);
//		return mLiteOrm.query(qb);
//	}
//
//
//	public void CloseOrm(){
//
//		mLiteOrm.close();
//	}
//
////自己拼装SQL语句
////	QueryBuilder<Address> qb = new QueryBuilder<Address>(Address.class)
////			.columns(new String[]{Address.COL_ADDRESS})    //查询列
////			.appendOrderAscBy(Address.COL_ADDRESS)        //升序
////			.appendOrderDescBy(Address.COL_ID)       //当第一列相同时使用该列降序排序
////			.distinct(true)        //去重
////			.where(Address.COL_ADDRESS + "=?", new String[]{"香港路"}); //where条件
////
////			liteOrm.query(qb);
//
//}
//
//
//
