package com.yunbaba.freighthelper.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.cld.ols.tools.base.CldPubFuction;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.ols.api.CldKAccountAPI;

@SuppressWarnings("deprecation")
public class PhotoHelper {

	public static final String TAG = "PhotoHelper";
	public static int PHOTOSIZE = 150;// 显示的头像宽高

	public static String getCachePath() {
		return MainApplication.getMTQFileStorePath();
	}

	public static String getPath() {
		long kuid = AccountAPI.getInstance().getKuidLogin();
		return getCachePath() + "cache/user/" + kuid;
	}

	/**
	 * 获取用户头像路径
	 * 
	 * @param type
	 *            0- 照片路径; 1-裁减后照片路径
	 * @return String
	 */
	public static String getPhotoPath(int type) {
		// 用户目录
		String photoPath = getPath();
		// 创建路径
		File file = new File(photoPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		if (type == 0) {
			// 拍照后的照片最终路径
			photoPath += "/tmpphoto.jpg";
			return photoPath;
		}
		if (type == 1) {
			// 裁减后的照片最终路径
			photoPath += "/tmpuer_photo.jpg";
			return photoPath;
		}

		if (type == 2) {
			// 裁减后的照片最终路径
			photoPath += "/trueuser_photo.jpg";
			return photoPath;
		}
		return null;
	}

	public static void saveBitmapToTrueHead(Bitmap bm) {
		MLog.v(TAG, "保存头像图片");
		File f = new File(getPhotoPath(2));
//		if (f.exists()) {
//			f.delete();
//		}
		
//		if (!f.exists()) {
//			f.c
//		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		//	MLog.i(TAG, "已经保存");
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static Bitmap getPhotoBitmap() {
		Bitmap bitmap = null;
		try {
			String cutPath = getPhotoPath(2);
			if (!TextUtils.isEmpty(cutPath) && new File(cutPath).exists()) {
				bitmap = cutImg(cutPath, PHOTOSIZE, PHOTOSIZE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MLog.d(TAG, "未获取到保存图片");
		}
		return bitmap;
	}
	
	
	public static Bitmap getPhotoBitmap(String path) {
		Bitmap bitmap = null;
		try {
			String cutPath = path;
			if (!TextUtils.isEmpty(cutPath) && new File(cutPath).exists()) {
				bitmap = cutImg(cutPath, PHOTOSIZE, PHOTOSIZE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MLog.d(TAG, "未获取到保存图片");
		}
		return bitmap;
	}

	public static byte[] getUserPhotoData(String photoPath) {
		if (!TextUtils.isEmpty(photoPath)) {
			File file = new File(photoPath);
			byte[] photoData = CldPubFuction.fileToByte(file);
			return photoData;
		} else {
			MLog.e("ols", "photoPath null or empty");
			return null;
		}
	}

	public static void openCamra(Activity activity, String cutPath, String NN_path, int requestCode) {
		if (!TextUtils.isEmpty(cutPath)) {
			if (("ZTE N5S").equals(getMobileModel())) {
				PhotoHelper.takePhoto(activity, new File(NN_path, "tmphoto.jpg"), requestCode);
			} else {
				PhotoHelper.takePhoto(activity, new File(cutPath), requestCode);
			}

		} else {
			MLog.d(TAG, "未获取到保存图片的路径");
		}
	}

	public static void onChoosePhoto(Activity activity, int requestCode) {
		// if (PhotoHelper.isSpecModel()) {
		/**
		 * 针对魅族PRO 先进行图库图片的选择，在onActivityResult中再调裁剪 不然会出现错误
		 */
		PhotoHelper.startPhotoPick(activity, requestCode);

	}

	/**
	 * @Description 图片裁剪
	 * @param activity
	 * @param
	 *
	 * @param aspectX
	 *            X方向上的比例
	 * @param aspectY
	 *            Y方向上的比例
	 * @param outputX
	 *            裁剪区的宽
	 * @param outputY
	 *            裁剪区的高
	 * @param requestCode
	 *            activity请求号
	 * @return void
	 */
	public static void cropImageUri(Activity activity, File filepath, int aspectX, int aspectY, int outputX, int outputY,
			int requestCode) {

		Uri contentUri = FileUtils.getUriForFile(activity,filepath);



		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(contentUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);

		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
				| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		// intent.putExtra("scale", true);// 黑边
		intent.putExtra("scaleUpIfNeeded", true);// 黑边
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * @Description 针对魅族PRO 5 对特定的uri进行裁剪(头像大小)
	 * @param activity
	 * @param uri
	 * @param cutUri
	 * @param resultcode
	 * @return void
	 */
	public static void startCrop(Activity activity, Uri uri, Uri cutUri, int resultcode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cutUri);
		intent.putExtra("return-data", false); // 返回数据bitmap
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		intent.putExtra("scale", true);// 黑边
		intent.putExtra("scaleUpIfNeeded", true);// 黑边
		activity.startActivityForResult(intent, resultcode);
	}

	/**
	 * @Description 拍照
	 * @param activity
	 * @param file
	 *            保存图片的Uri地址, 必须确保Uri路径存在，否则拍照后无法完成回调
	 * @param requestCode
	 *            activity请求号
	 * @return void
	 */
	public static void takePhoto(Activity activity,File file, int requestCode) {
		try {
			if (isCameraCanUse()) {
				// 调用系统的拍照功能
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (intent.resolveActivity(activity.getPackageManager()) != null) {
					// 必须确保文件夹路径存在，否则拍照后无法完成回调
				//	intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

					Uri contentUri = FileUtils.getUriForFile(activity,file);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
							| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


					activity.startActivityForResult(intent, requestCode);
				}
			} else {
				Toast.makeText(activity, "调用摄像头失败", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 针对魅族PRo5单独打开图库
	 * 
	 * @param activity
	 * @param resultCode
	 * @return void
	 * @author ZhangJunJie
	 * @date 2016年4月19日 下午3:43:08
	 */
	public static void startPhotoPick(Activity activity, int resultCode) {
		Intent intent;
		intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		intent.putExtra("scale", true);// 黑边
		intent.putExtra("scaleUpIfNeeded", true);// 黑边
		activity.startActivityForResult(intent, resultCode);
	}

	/**
	 * @Description 从相册选择图片，并截图
	 * @param activity
	 * @param uri
	 *            保存图片的Uri地址
	 * @param aspectX
	 *            X方向上的比例
	 * @param aspectY
	 *            Y方向上的比例
	 * @param outputX
	 *            裁剪区的宽
	 * @param outputY
	 *            裁剪区的高
	 * @param outputPath
	 *            截图后， 保存路径
	 * @param requestCode
	 *            activity请求号
	 * @return void
	 */
	public static void pickAndCropPhoto(Activity activity, Uri uri, int aspectX, int aspectY, int outputX, int outputY,
			int requestCode) {
		Intent intent;
		intent = new Intent(Intent.ACTION_PICK);
		// intent.setType("image/*");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		intent.putExtra("scale", true);// 黑边
		intent.putExtra("scaleUpIfNeeded", true);// 黑边
		if (intent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivityForResult(intent, requestCode);
		}
	}

	/**
	 * 将头像下载路径存储
	 * 
	 * @param webUrl
	 * @return void
	 */
	public static void saveUserPhotoUrl(String webUrl) {
		String webPath = webUrl;
		if (!TextUtils.isEmpty(webPath)) {
			try {
				String paramsPath = MainApplication.getMTQFileStorePath();
				if (!paramsPath.endsWith("/")) {
					paramsPath += "/";
				}
				// 用户名
				long kuid = CldKAccountAPI.getInstance().getKuidLogin();
				// 用户目录
				String addPath = paramsPath + "cache/user/" + kuid + "/weburl.txt";
				File file = new File(addPath);
				if (!file.exists()) {
					file.createNewFile();
				}
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.write(webPath);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断摄像头是否可用
	 */
	public static boolean isCameraCanUse() {
		boolean canUse = true;
		Camera mCamera = null;
		try {
			mCamera = Camera.open();
			// setParameters 是针对魅族MX5 做的。MX5 通过Camera.open() 拿到的Camera
			// 对象不为null
			Camera.Parameters mParameters = mCamera.getParameters();
			mCamera.setParameters(mParameters);
		} catch (Exception e) {
			canUse = false;
		}
		if (mCamera != null) {
			mCamera.release();
		}
		return canUse;
	}

	public static boolean isSpecModel() {
		String mModel = getMobileModel();
		if ("PRO 5".equals(mModel) || "MI NOTE Pro".equals(mModel) || "MI 5".equals(mModel)
				|| "Redmi Note 3".equals(mModel) || "MI MAX".equals(mModel) || "MI PAD".equals(mModel)
				|| "MX5".equals(mModel) || "MX4".equals(mModel)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取手机型号
	 * 
	 * @return String
	 */
	public static String getMobileModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * 描述：裁剪图片.
	 * 
	 * @param path
	 *            图像文件路径
	 * @param newWidth
	 *            新图片的宽
	 * @param newHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	public static Bitmap cutImg(String path, int newWidth, int newHeight) {
		Bitmap resizeBmp = null;
		if (newWidth <= 0 || newHeight <= 0) {
			throw new IllegalArgumentException("裁剪图片的宽高设置不能小于0");
		}
		File file = new File(path);
		if (!file.exists()) {
			return resizeBmp;
		}
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getPath(), opts);
		// inSampleSize=2表示图片宽高都为原来的二分之一，即图片为原来的四分之一
		// 缩放可以将像素点打薄,裁剪前将图片缩放到目标图2倍大小
		int srcWidth = opts.outWidth; // 获取图片的原始宽度
		int srcHeight = opts.outHeight;// 获取图片原始高度
		int destWidth = 0;
		int destHeight = 0;

		int cutSrcWidth = newWidth * 2;
		int cutSrcHeight = newHeight * 2;

		// 缩放的比例,为了大图的缩小到2倍被裁剪的大小在裁剪
		double ratio = 0.0;
		// 任意一个不够长就不缩放
		if (srcWidth < cutSrcWidth || srcHeight < cutSrcHeight) {
			ratio = 0.0;
			destWidth = srcWidth;
			destHeight = srcHeight;
		} else if (srcWidth > cutSrcWidth) {
			ratio = (double) srcWidth / cutSrcWidth;
			destWidth = cutSrcWidth;
			destHeight = (int) (srcHeight / ratio);
		} else if (srcHeight > cutSrcHeight) {
			ratio = (double) srcHeight / cutSrcHeight;
			destHeight = cutSrcHeight;
			destWidth = (int) (srcWidth / ratio);
		}

		// 默认为ARGB_8888.
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		// 以下两个字段需一起使用：
		// 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
		opts.inPurgeable = true;
		// 位图可以共享一个参考输入数据(inputstream、阵列等)
		opts.inInputShareable = true;
		// 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
		if (ratio > 1) {
			opts.inSampleSize = (int) ratio;
		} else {
			opts.inSampleSize = 1;
		}
		// 设置大小
		opts.outHeight = destHeight;
		opts.outWidth = destWidth;
		// 创建内存
		opts.inJustDecodeBounds = false;
		// 使图片不抖动
		opts.inDither = false;
		Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), opts);
		if (bitmap != null) {
			resizeBmp = cutImg(bitmap, newWidth, newHeight);
		}
		return resizeBmp;
	}

	/**
	 * 描述：裁剪图片.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @param newWidth
	 *            新图片的宽
	 * @param newHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	public static Bitmap cutImg(Bitmap bitmap, int newWidth, int newHeight) {
		if (bitmap == null) {
			return null;
		}

		if (newWidth <= 0 || newHeight <= 0) {
			throw new IllegalArgumentException("裁剪图片的宽高设置不能小于0");
		}

		Bitmap resizeBmp = null;

		try {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();

			if (width <= 0 || height <= 0) {
				return null;
			}
			if (width > newWidth) {
			} else {
				newWidth = width;
			}

			if (height > newHeight) {
			} else {
				newHeight = height;
			}
			resizeBmp = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
		} catch (Exception e) {
			resizeBmp = null;
			e.printStackTrace();
		} finally {
			if (resizeBmp != bitmap) {
				bitmap.recycle();
			}
		}
		return resizeBmp;
	}

	/**
	 * 裁剪圆形图片
	 * 
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap getCircleBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			Paint paint = new Paint();
			Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
			float roundPx = bitmap.getWidth() >> 1;
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.BLACK);
			/**
			 * 绘制Bitmap
			 */
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			canvas.drawBitmap(bitmap, src, rect, paint);
			return output;
		} catch (Exception e) {
			return bitmap;
		}
	}

}
