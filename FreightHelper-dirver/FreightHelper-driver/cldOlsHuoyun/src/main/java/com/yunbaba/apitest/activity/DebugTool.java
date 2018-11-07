package com.yunbaba.apitest.activity;

public class DebugTool {
	public static void saveFile(String str) {

		return ;

//		String filePath = null;
//		boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//		if (hasSDCard) {
//			filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "MTQFreightHelperFile/debuglog.log";
//		} else
//			filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "MTQFreightHelperFile/debuglog.log";
//
//
//		try {
//			File file = new File(filePath);
//			if (!file.exists()) {
//				File dir = new File(file.getParent());
//				dir.mkdirs();
//				file.createNewFile();
//			}
//			FileOutputStream outStream = new FileOutputStream(file,true);
//			str = str +"\n";
//			outStream.write(str.getBytes());
//			outStream.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
//	
//	
//	public static void writeDataIntoFile(String str) {
//		String filePath = null;
//		boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//		if (hasSDCard) {
//			filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "initdebug.txt";
//		} else
//			filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "initdebug.txt";
//	   
//	
//		try {
//			File file = new File(filePath);
//			if (!file.exists()) {
//				File dir = new File(file.getParent());
//				dir.mkdirs();
//				file.createNewFile();
//			}
//			FileOutputStream outStream = new FileOutputStream(file,true);
//			outStream.write(str.getBytes());
//			outStream.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
