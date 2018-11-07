package com.yunbaba.freighthelper.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yunbaba.freighthelper.MainApplication;

public class BitmapUtils {

	public static final String PATH_HEAD_IMG = "head_img_path";

	
	public static Bitmap getDiskBitmap(String pathString) {
		Bitmap bitmap = null;
		try {
			File file = new File(pathString);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(pathString);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return bitmap;
	}

	/**
	 * 
	 * @Title: saveHeadImgBitmap
	 * @Description: 保存头像
	 * @param context
	 * @param bitmap
	 * @return: void
	 */
	@SuppressLint("SimpleDateFormat")
	public static void saveHeadImgBitmap(Context context, Bitmap bitmap) {
		try {
			String path = MainApplication.getMTQFileStorePath();
			File dirFile = new File(path);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}

			String filePath = "";
			long time = System.currentTimeMillis();
			String date = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date(time));
			filePath = path + "IMG_" + date + ".jpg";
			SPHelper.getInstance(context).put(PATH_HEAD_IMG, filePath);

			FileOutputStream fos = new FileOutputStream(new File(filePath));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: getHeadImgBitmap
	 * @Description: 从本地获取头像
	 * @param context
	 */
	public static Bitmap getHeadImgBitmap(Context context) {
		Bitmap bitmap = null;
		String filePath = SPHelper.getInstance(context)
				.getString(PATH_HEAD_IMG);
		if (!filePath.isEmpty()) {
			try {
				File file = new File(filePath);
				if (file.exists()) {
					bitmap = BitmapFactory.decodeFile(filePath);
				}
			} catch (Exception e) {
			}
		}
		return bitmap;
	}
}
