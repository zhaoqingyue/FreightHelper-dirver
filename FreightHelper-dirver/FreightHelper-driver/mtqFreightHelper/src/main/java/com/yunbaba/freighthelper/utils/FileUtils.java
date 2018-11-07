package com.yunbaba.freighthelper.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class FileUtils {

	/**
	 * 生成文件夹路径
	 */
	public static String SDPATH = Environment.getExternalStorageDirectory() + "/MTQFreightHelperFile";

	//
	// Environment.getExternalStorageDirectory()
	// + "/TEST_PY/";

	
//	public static void saveFile(byte[] data,String filepaths.xmls.xml,String filename){
//		try {
//
//			
//			File f = new File(filepathspaths.xml, filename);
//			// 如果该文件夹中有同名的文件，就先删除掉原文件
//			if (f.exists()) {
//				f.delete();
//			}
//			FileOutputStream out = new FileOutputStream(f);
//			bm.compress(Bitmap.CompressFormat.JPEG, 60, out);
//			out.flush();
//			out.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
	
	
	
	/**
	 * 将图片压缩保存到文件夹
	 * 
	 * @param bm
	 * @param picName
	 */
	public static void saveBitmap(Bitmap bm, String picName) {
		try {

			// 如果没有文件夹就创建一个程序文件夹
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			File f = new File(SDPATH, picName + ".JPEG");
			// 如果该文件夹中有同名的文件，就先删除掉原文件
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 60, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 质量压缩 并返回Bitmap
	 * 
	 * @param image
	 *            要压缩的图片
	 * @return 压缩后的图片
	 */
	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 70, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 70;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 质量压缩
	 * 
	 * @param bitmap
	 * @param picName
	 */
	public static void compressImageByQuality(final Bitmap bitmap, String picName) {
		// 如果没有文件夹就创建一个程序文件夹
		if (!isFileExist("")) {
			try {
				File tempf = createSDDir("");
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		File f = new File(SDPATH, picName + ".JPEG");
		// 如果该文件夹中有同名的文件，就先删除掉原文件
		if (f.exists()) {
			f.delete();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 70;
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
		// 循环判断如果压缩后图片是否大于200kb,大于继续压缩
		while (baos.toByteArray().length / 1024 > 100) {
			// 重置baos即让下一次的写入覆盖之前的内容
			baos.reset();
			// 图片质量每次减少5
			options -= 10;
			// 如果图片质量小于10，则将图片的质量压缩到最小值
			if (options < 0)
				options = 0;
			// 将压缩后的图片保存到baos中
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
			// 如果图片的质量已降到最低则，不再进行压缩
			if (options == 0)
				break;
		}
		// 将压缩后的图片保存的本地上指定路径中
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File(SDPATH, picName + ".JPEG"));
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static String getRealFilePath(final Context context, final Uri uri) {
		if (null == uri)
			return null;
		final String scheme = uri.getScheme();
		String data = null;
		if (scheme == null)
			data = uri.getPath();
		else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri, new String[] { ImageColumns.DATA }, null, null,
					null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(ImageColumns.DATA);
					if (index > -1) {
						data = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return data;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param dirName
	 *            文件夹名称
	 * @return 文件夹路径
	 * @throws IOException
	 */
	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	/**
	 * 判断改文件是否是一个标准文件
	 * 
	 * @param fileName
	 *            判断的文件路径
	 * @return 判断结果
	 */
	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}

	/**
	 * 删除指定文件
	 * 
	 * @param fileName
	 */
	public static void delFile(String fileName) {
		File file = new File(SDPATH + fileName);
		if (file.isFile()) {
			file.delete();
		}
		file.exists();
	}

	/**
	 * 删除指定文件
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			MLog.i("TAG", "文件不存在！");
		}
	}

	/**
	 * 删除指定文件夹中的所有文件
	 */
	public static void deleteDir() {
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;

		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete();
			else if (file.isDirectory())
				deleteDir();
		}
		dir.delete();
	}

	/**
	 * 判断是否存在该文件
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

	public byte[] getContent(String filePath) throws IOException {
		File file = new File(filePath);
		long fileSize = file.length();
		if (fileSize > Integer.MAX_VALUE) {
			System.out.println("file too big...");
			return null;
		}
		FileInputStream fi = new FileInputStream(file);
		byte[] buffer = new byte[(int) fileSize];
		int offset = 0;
		int numRead = 0;
		while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
			offset += numRead;
		}
		// 确保所有数据均被读取
		if (offset != buffer.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		fi.close();
		return buffer;
	}

	/**
	 * the traditional io way
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(String filename) throws IOException {

		File f = new File(filename);
		if (!f.exists()) {
			throw new FileNotFoundException(filename);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bos.close();
		}
	}

	/**
	 * NIO way
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray2(String filename) throws IOException {

		File f = new File(filename);
		if (!f.exists()) {
			throw new FileNotFoundException(filename);
		}

		FileChannel channel = null;
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(f);
			channel = fs.getChannel();
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
			while ((channel.read(byteBuffer)) > 0) {
				// do nothing
				// System.out.println("reading");
			}
			return byteBuffer.array();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray3(String filename) throws IOException {

		FileChannel fc = null;
		try {
			fc = new RandomAccessFile(filename, "r").getChannel();
			MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
			System.out.println(byteBuffer.isLoaded());
			byte[] result = new byte[(int) fc.size()];
			if (byteBuffer.remaining() > 0) {
				// System.out.println("remain");
				byteBuffer.get(result, 0, byteBuffer.remaining());
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				fc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static Uri getUriForFile(Context context, File file) {
		if (context == null || file == null) {
			throw new NullPointerException();
		}
		Uri uri;
		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.mtq.freighthelper.fileprovider", file);
		} else {
			uri = Uri.fromFile(file);
		}
		return uri;
	}

}
