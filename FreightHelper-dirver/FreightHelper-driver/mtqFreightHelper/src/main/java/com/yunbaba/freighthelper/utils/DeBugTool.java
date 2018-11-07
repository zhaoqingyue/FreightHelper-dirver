package com.yunbaba.freighthelper.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zhonghm on 2018/4/17.
 */

public class DeBugTool {

    public static boolean isOpen = false;




	public static void writeDataIntoFile(String str) {
		String filePath = null;
		boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (hasSDCard) {
			filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "运东东debug.txt";
		} else
			filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "运东东debug.txt";


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
