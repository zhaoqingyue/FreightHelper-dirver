package com.yunbaba.api.trunk;

import com.yunbaba.api.trunk.bean.CldDelUpTask;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.api.CldKAccountAPI.CldLoginStatus;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliTaskStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//任务管理类，负责管理本地保存的任务信息和对信息的处理
/**
 * 运货单数据存储
 * */
public class TaskDataManager {
	
	
	/**搜索运货单的历史*/
	private Map<Long , CldDeliTaskStore> searchStoreHis = new ConcurrentHashMap<Long, CldDeliTaskStore>();

	/**上传任务队列    可能多线程处理，使用线程安全结构 */
	private List<CldDelUpTask> delUpTask = new CopyOnWriteArrayList<CldDelUpTask>();
	
	
	 
//	/** 函数注释：重载参数
//	 * @param old
//	 * @return
//	 * @return CldDelParams
//	 * @author chenjq
//	 * @date 2015-4-25 下午1:07:40
//	 */ 
//	public static CldDelParams reloadParam(CldDelParams old){
//		MLog.i("reloadParam", "mPrePath " + mPrePath);
//		MLog.i("reloadParam", "mFileFullPath " + mFileFullPath);
//		if(mPrePath != null && !mPrePath.equals(mFileFullPath)){
//			if(old != null){
//				writerObject(old, mPrePath); //保存当前的参数
//			}
//			//从新路径获取新参数
//			return loadParam();
//		}
//		return old;
//	}
//	
//	/** 函数注释：保存参数到文件
//	 * @param obj
//	 * @return void
//	 * @author chenjq
//	 * @date 2015-4-16 上午9:40:01
//	 */ 
//	public static void saveParam(CldDelParams obj){
//		if(CldKServiceAPI.getInstance().getKuid() == 0)
//			return;
//		if(obj != null){
//			writerObject(obj, getParamPath());			
//		}
//	}
//	
//	/** 函数注释：获取参数对象
//	 * @return
//	 * @return CldDelParams
//	 * @author chenjq
//	 * @date 2015-4-16 上午9:45:55
//	 */ 
//	public static CldDelParams loadParam(String fileType,){
//		CldDelParams param = readerObject(getParamPath());
//		if(param == null){
//			param = new CldDelParams();
//		}
//		return param;
//	}
	
	/** 
	 * @Description 向已创建的文件中写入数据
	 * @author：xiaoquan
	 * @date：2014-6-16 上午10:41:48
	 * @param obj
	 * @return void
	 */ 
	public synchronized static void writerObject(Object obj, String path) {
		try {
			File file = new File(path);
			FileOutputStream fout = new FileOutputStream(file);
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			
			if (obj != null && oout != null) {
				//oout.write(Version.getBytes());
				oout.writeObject(obj);
			}
			
			oout.close();
			fout.close();
		} catch (IOException e) {
			MLog.i("writerObject e.toString()", ""+e.toString());
			e.printStackTrace();
		}
	}
	
	/** 
	 * @Description 文件中读取对象，需要判断返回值是否为空
	 * @author：xiaoquan
	 * @date：2014-6-16 上午10:45:48

	 * @return Object
	 */ 
	public synchronized static Object readerObject(String path) {
		Object obj= null;
		    
		File file = new File(path);
		if (!file.exists()) {
			return null;
		} else {
			try {
				//读取文件开头的字符，判断参数是否正常
				ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file));
				String ver = oin.readLine();//读取文件前面的标记
				if(ver == null ){
					oin.close();
					return null;
				}
				obj = oin.readObject();
				oin.close();
			} catch (Exception e) {			
				MLog.i("readerObject e.toString()", ""+e.toString());
				e.printStackTrace();
			}
		}
		
		return obj;

//		if(obj instanceof T){
//			return (T)obj;
//		} else {
//			return null;
//		}
	}
	
	
	public String  getUserTaskFilePath(){
		
		if(CldKAccountAPI.getInstance().getLoginStatus()  == CldLoginStatus.LOGIN_DONE){
			
			return MainApplication.getMTQFileStorePath()+CldKAccountAPI.getInstance().getKuidLogin()+File.separator+ "delivery.cld";
		}else{
			
			return null;
		}
		
	}
	
	public void clearCache(){
		
		
		
	}
	
	public void  getHistorySearch(){
		
	}
	
	
}
