package com.yunbaba.freighthelper.utils;

import java.io.File;
import java.io.FileOutputStream;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import android.content.Context;

/**
 * 下载文件工具类
 **/
public class DownloadUtil {
	
	public static final String TAG = DownloadUtil.class.getSimpleName();
	
	
	public RequestQueue mRequsetQueue; // a
	private static DownloadUtil instance;
	
	public DownloadUtil(Context context) {
		
	
		this.mRequsetQueue = Volley.newRequestQueue(context.getApplicationContext());
		this.mRequsetQueue.start();
	}
	
	
	
	
	public static DownloadUtil getInstance(Context context) {
		if (instance == null) {
			synchronized (DownloadUtil.class) {
				if (instance == null) {
					instance = new DownloadUtil(context);
				}
			}
		}

		return instance;
	}
	
	
	
	
	
	public void addRequest(Request request){
		this.mRequsetQueue.add(request);
	}

	public void cancalRequest(String requestId) {
		this.mRequsetQueue.cancelAll(requestId);
	}
	
	
	

	/**
	 * use volley
	 */
	public void DownLoadSpeechFile(String url, final Context context, final String speechId,
			final DownLoadResultListener listener) {
		
		final String fileName = "speechfile" + speechId + ".wav";
		final String path = context.getApplicationContext().getFilesDir().getPath()+"/"+fileName;

//		DownloadManager.Request drequest = new DownloadManager.Request(Uri.parse(url));
//		drequest.setAllowedOverRoaming(true);// 漫游网络是否可以下载
//		drequest.setNotificationVisibility(View.)
//		
//		//指定下载路径和下载文件名
//		drequest.setShowRunningNotification(false);
//		
//		drequest.set(filepathspaths.xml, fileName);
//		//获取下载管理器
//		DownloadManager downloadManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//		//将下载任务加入下载队列，否则不会进行下载
//		downloadManager.enqueue(drequest);
//		drequest.set
		
		InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,new Response.Listener<byte[]>() {

			@Override
			public void onResponse(byte[] arg0) {

				MLog.e("download  speech", "success");
				FileOutputStream outputStream;

				

				// 保存到私有文件夹
				/// data/data/youPackageName/files
				try {
					outputStream = context.getApplicationContext().openFileOutput(fileName,
							Context.MODE_PRIVATE);
					outputStream.write(arg0);
					outputStream.close();
				

					listener.onDownload(path);

					// FileInputStream inStream =
					// context.getApplicationContext().openFileInput(fileName);
					//
					// inStream.getFD();
					// File file = new
					// File("/data/data/cn.itcast.action/files/itcast.txt");

				} catch (Exception e) {
					e.printStackTrace();

					listener.onDownload(null);
				}

			}
			
		},new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {

				MLog.e("download  speech", "fail" + arg0.toString());
				listener.onError(arg0);
			}
		});

		request.setTag("speechfile" + speechId);
		
//		RequestQueue mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
//		MLog.e("dowanload","start");
//		mRequestQueue.add(request);
//		mRequestQueue.add(stringRequest);
		
		
		addRequest(request);
		
		

	}

	public boolean existsFile(Context context, String fileName) {
		String path = context.getApplicationContext().getFilesDir().getPath() + "//";
		File file = new File(path + fileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public static String getSpeechFilePathById(Context context, String speechid) {
		String fileName = "speechfile" + speechid + ".wav";

		String path = context.getApplicationContext().getFilesDir().getPath() + "//" + fileName;

		File file = new File(path + fileName);
		if (file.exists()) {
			return path;
		} else
			return null;

		// return context.getApplicationContext().openFileOutput(fileName,
		// Context.MODE_PRIVATE);
	}

	public static interface DownLoadResultListener {

		void onDownload(String localpath);

		void onError(VolleyError arg0);

	}

}
