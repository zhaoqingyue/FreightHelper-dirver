package com.yunbaba.freighthelper.utils;

import java.io.File;
import java.io.FileOutputStream;

import com.yunbaba.freighthelper.MainApplication;

import android.os.Environment;

public class MyDebugTool {
	public static void saveFile(String str) {
		String filePath = null;
		boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (hasSDCard) {
			filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "MTQFreightHelperFile/debug.txt";
		} else
			filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "MTQFreightHelperFile/debug.txt";
	   
	
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				File dir = new File(file.getParent());
				dir.mkdirs();
				file.createNewFile();
			}
			FileOutputStream outStream = new FileOutputStream(file);
			outStream.write(str.getBytes());
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void saveFile2(String str) {
		String filePath = null;
		boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (hasSDCard) {
			filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "MTQFreightHelperFile/debug2.txt";
		} else
			filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "MTQFreightHelperFile/debug2.txt";
	   
	
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				File dir = new File(file.getParent());
				dir.mkdirs();
				file.createNewFile();
			}
			FileOutputStream outStream = new FileOutputStream(file);
			outStream.write(str.getBytes());
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void writeDataIntoFile(String str) {
		String filePath = null;
		//boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		//if (hasSDCard) {
		//	filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "车辆报警消息.txt";
		//} else
			filePath = MainApplication.getMTQFileStorePath() + File.separator + "车辆报警消息.txt";
	   
	
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				File dir = new File(file.getParent());
				dir.mkdirs();
				file.createNewFile();
			}
			FileOutputStream outStream = new FileOutputStream(file,true);
			outStream.write(str.getBytes());
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
