package com.yunbaba.freighthelper.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.yunbaba.freighthelper.MainApplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.DisplayMetrics;

/**
 * Tools for handler picture
 * 
 * @author Ryan.Tang
 * 
 */
public final class ImageTools {

	public static final String PATH_HEAD_IMG = MainApplication.getTmpCacheFilePath();

	// MainApplication.getContext().getDir("cachefile",
	// Context.MODE_PRIVATE).getAbsolutePath();

	// MainApplication.getTmpCacheFilePath();

	//
	// public static String compress(Activity context, String srcPath) {
	//
	// Bitmap bitmap = BitmapFactory.decodeFile(srcPath); //
	//
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// int quality =100;
	// try {
	// bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
	//
	// } catch (Exception e) {
	//
	// MLog.e("check", e.toString());
	// return null;
	//
	// }
	//
	// String fileName = System.currentTimeMillis() + ".jpg";
	//
	// File file = new File(PATH_HEAD_IMG, fileName);
	// // if(!file.exists())
	// // file.mkdirs();
	// //MLog.e("check", file.getAbsolutePath());
	// boolean Res = false;
	//
	// try {
	// baos.writeTo(new FileOutputStream(file));
	// Res = true;
	// } catch (Exception e) {
	// e.printStackTrace();
	// Res = false;
	// MLog.e("check", e.toString());
	// } finally {
	// try {
	// baos.flush();
	// baos.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// MLog.e("check", e.toString());
	// }
	//
	// }
	//
	// return Res ? file.getAbsolutePath() : null;
	//
	// }

	public static String compress(Activity context, String srcPath) {
		
		
		  // 取得图片旋转角度  
        int angle = readPictureDegree(srcPath);  
		
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);

		float hh = dm.heightPixels;
		float ww = dm.widthPixels;
		Bitmap bitmap;
		// 如果分辨率不高，则不变大小 否则压缩到1280*对应比例高度
//		if ((hh * ww) <= (1280 * 720)) {
//			bitmap = BitmapFactory.decodeFile(srcPath);
//		} else {

			hh = hh * 2 / 3;
			ww = ww * 2 / 3;

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(srcPath, opts);
			opts.inJustDecodeBounds = false;
			int w = opts.outWidth;
			int h = opts.outHeight;
			int size = 0;
			if (w <= ww && h <= hh) {
				size = 1;
			} else {
				double scale = w >= h ? w / ww : h / hh;
				double log = Math.log(scale) / Math.log(2);
				double logCeil = Math.ceil(log);
				size = (int) Math.pow(2, logCeil);
			}
			opts.inSampleSize = size;

			bitmap = BitmapFactory.decodeFile(srcPath, opts); //
	//	}
		
		
		// 修复图片被旋转的角度  
        bitmap = rotaingImageView(angle,bitmap);  
		

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 70;
		try {
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			System.out.println(baos.toByteArray().length);
			mbreaks: while (baos.toByteArray().length > (100 * 1024)) {
				baos.reset();
				quality -= 10;

				if (quality <= 10) {
					quality = 10;
					bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
					break mbreaks;
				}

				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

				// System.out.println(baos.toByteArray().length);
			}
		} catch (Exception e) {

			MLog.e("check", e.toString());
			return null;

		}

		
	
		
		
		
		String fileName = System.currentTimeMillis() + ".jpg";

		File file = new File(PATH_HEAD_IMG, fileName);
		 if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		 }
		// MLog.e("check", file.getAbsolutePath());
		boolean Res = false;

		try {
			baos.writeTo(new FileOutputStream(file));
			Res = true;
		} catch (Exception e) {
			e.printStackTrace();
			Res = false;
			MLog.e("check", e.toString());
		} finally {
			try {
				baos.flush();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
				MLog.e("check", e.toString());
			}

		}

		return Res ? file.getAbsolutePath() : null;

	}

	/**
	 * Transfer drawable to bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Bitmap to drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}

	/**
	 * Input stream to bitmap
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static Bitmap inputStreamToBitmap(InputStream inputStream) throws Exception {
		return BitmapFactory.decodeStream(inputStream);
	}

	/**
	 * Byte transfer to bitmap
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Bitmap byteToBitmap(byte[] byteArray) {
		if (byteArray.length != 0) {
			return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		} else {
			return null;
		}
	}

	/**
	 * Byte transfer to drawable
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Drawable byteToDrawable(byte[] byteArray) {
		ByteArrayInputStream ins = null;
		if (byteArray != null) {
			ins = new ByteArrayInputStream(byteArray);
		}
		return Drawable.createFromStream(ins, null);
	}

	/**
	 * Bitmap transfer to bytes
	 * 
	 * @param byteArray
	 * @return
	 */
	public static byte[] bitmapToBytes(Bitmap bm) {
		byte[] bytes = null;
		if (bm != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
			bytes = baos.toByteArray();
		}
		return bytes;
	}

	/**
	 * Drawable transfer to bytes
	 * 
	 * @param drawable
	 * @return
	 */
	public static byte[] drawableToBytes(Drawable drawable) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		Bitmap bitmap = bitmapDrawable.getBitmap();
		byte[] bytes = bitmapToBytes(bitmap);
		;
		return bytes;
	}

	/**
	 * Base64 to byte[] //
	 */
	// public static byte[] base64ToBytes(String base64) throws IOException {
	// byte[] bytes = Base64.decode(base64);
	// return bytes;
	// }
	//
	// /**
	// * Byte[] to base64
	// */
	// public static String bytesTobase64(byte[] bytes) {
	// String base64 = Base64.encode(bytes);
	// return base64;
	// }

	/**
	 * Create reflection images
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, h, w, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * Get rounded corner images
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            5 10
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * Resize the bitmap
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	/**
	 * Resize the drawable
	 * 
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable);
		Matrix matrix = new Matrix();
		float sx = ((float) w / width);
		float sy = ((float) h / height);
		matrix.postScale(sx, sy);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
		return new BitmapDrawable(newbmp);
	}

	/**
	 * Get images from SD card by filepathspaths.xml and the name of image
	 * 
	 * @param photoName
	 * @return
	 */
	public static Bitmap getPhotoFromSDCard(String path, String photoName) {
		Bitmap photoBitmap = BitmapFactory.decodeFile(path + "/" + photoName + ".png");
		if (photoBitmap == null) {
			return null;
		} else {
			return photoBitmap;
		}
	}

	public static byte[] getPhotoByteArray(String filepath) {

		Bitmap bitmap = getPhotoFromSDCard(filepath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();

	}

	public static String getPhotoBase64(String filepath) {

		byte[] data = getPhotoByteArray(filepath);
		
		return 	 Base64.encodeToString(data, Base64.DEFAULT);

	}

	/**
	 * Get images from SD card by filepathspaths.xml and the name of image
	 * 
	 * @param photoName
	 * @return
	 */
	public static Bitmap getPhotoFromSDCard(String filepath) {
		Bitmap photoBitmap = BitmapFactory.decodeFile(filepath);
		if (photoBitmap == null) {
			return null;
		} else {
			return photoBitmap;
		}
	}

	/**
	 * Check the SD card
	 * 
	 * @return
	 */
	public static boolean checkSDCardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * Get image from SD card by filepathspaths.xml and the name of image
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean findPhotoFromSDCard(String path, String photoName) {
		boolean flag = false;

		if (checkSDCardAvailable()) {
			File dir = new File(path);
			if (dir.exists()) {
				File folders = new File(path);
				File photoFile[] = folders.listFiles();
				for (int i = 0; i < photoFile.length; i++) {
					String fileName = photoFile[i].getName().split("\\.")[0];
					if (fileName.equals(photoName)) {
						flag = true;
					}
				}
			} else {
				flag = false;
			}
			// File file = new File(filepaths.xmls.xml + "/" + photoName + ".jpg" );
			// if (file.exists()) {
			// flag = true;
			// }else {
			// flag = false;
			// }

		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * Save image to the SD card
	 * 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static void savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName) {
		if (checkSDCardAvailable()) {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File photoFile = new File(path, photoName + ".jpg");
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)) {
						fileOutputStream.flush();
						// fileOutputStream.close();
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
			} finally {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Delete the image from SD card
	 * 
	 * @param context
	 * @param path
	 *            file:///sdcard/temp.jpg
	 */
	public static void deleteAllPhoto(String path) {
		if (checkSDCardAvailable()) {
			File folder = new File(path);
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}
	}

	public static void deletePhotoAtPathAndName(String path, String fileName) {
		if (checkSDCardAvailable()) {
			File folder = new File(path);
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().split("\\.")[0].equals(fileName)) {
					files[i].delete();
				}
			}
		}
	}
	
	 /** 
     * 读取照片旋转角度 
     * 
     * @param path 照片路径 
     * @return 角度 
     */  
    public static int readPictureDegree(String path) {  
        int degree = 0;  
        try {  
            ExifInterface exifInterface = new ExifInterface(path);  
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
            switch (orientation) {  
                case ExifInterface.ORIENTATION_ROTATE_90:  
                    degree = 90;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_180:  
                    degree = 180;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_270:  
                    degree = 270;  
                    break;  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return degree;  
    }  
    
    
    /** 
     * 旋转图片 
     * @param angle 被旋转角度 
     * @param bitmap 图片对象 
     * @return 旋转后的图片 
     */  
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {  
        Bitmap returnBm = null;  
        // 根据旋转角度，生成旋转矩阵  
        Matrix matrix = new Matrix();  
        matrix.postRotate(angle);  
        try {  
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片  
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
        } catch (OutOfMemoryError e) {  
        }  
        if (returnBm == null) {  
            returnBm = bitmap;  
        }  
        if (bitmap != returnBm) {  
            bitmap.recycle();  
        }  
        return returnBm;  
    }  

}
