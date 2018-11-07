/*
 * @Title CldSapNetUtil.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-4-8 上午11:38:53
 * @version 1.0
 */
package com.yunbaba.ols.tools;

import android.text.TextUtils;
import android.util.Log;

import com.cld.log.CldLog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 网络框架
 * 
 * @author Zhouls
 * @date 2015-4-8 上午11:38:53
 */
public class CldSapNetUtil {

	/**
	 * Http Get方法
	 * 
	 * @param strUrl
	 *            （get方法因为是url带有特殊符号，不能统一转码，只能单个转码）
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-4-8 下午2:40:48
	 */
	public static String sapGetMethod(String strUrl) {
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {
			if (!TextUtils.isEmpty(strUrl)) {
				URL url = new URL(strUrl);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(15000);
				conn.setReadTimeout(15000);
				conn.setRequestMethod("GET");
				conn.addRequestProperty("Accept-Encoding", "gzip");
				if (conn.getResponseCode() == 200) {
					inputStream = conn.getInputStream();
					return getJsonFromGZIP(inputStream);
				}
			} else {
				CldLog.e("[ols]", "url is empty!");
				return null;
			}
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Http Post 方法
	 * 
	 * @param strUrl
	 *            the str url
	 * @param strPost
	 *            Post是json可以做统一转码处理（特殊字段不用中文转码）
	 * @return String
	 * @author Zhouls
	 * @date 2014-9-2 上午11:06:45
	 */
	@SuppressWarnings("deprecation")
	public static String sapPostMethod(String strUrl, String strPost) {
		try {
			if (!TextUtils.isEmpty(strUrl) && !TextUtils.isEmpty(strPost)) {
				HttpPost request = new HttpPost(strUrl);
				request.addHeader("Accept-Encoding", "gzip");
				StringEntity strEntity = new StringEntity(strPost, HTTP.UTF_8);
				request.setEntity(strEntity);
				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 4000);
				//HttpResponse httpResponse = new DefaultHttpClient()
				HttpResponse httpResponse = client.execute(request);


				InputStream inputStream = httpResponse.getEntity().getContent();


				return getJsonFromGZIP(inputStream);
			} else {
				CldLog.e("[ols]", "url is empty!");
				return null;
			}
		} catch (Exception e) {

			//Log.e("checkHttpClient","exception"+e.getClass().getName()+"  "+e.getMessage());

			return null;
		}
	}

	/**
	 * Http Post 方法
	 *
	 * @param strUrl
	 *            the str url
	 * @param strPost
	 *            Post是json可以做统一转码处理（特殊字段不用中文转码）
	 * @return String
	 * @author Zhouls
	 * @date 2014-9-2 上午11:06:45
	 */
	@SuppressWarnings("deprecation")
	public static String sapPostMethod2(String strUrl, String strPost) {
		try {
			if (!TextUtils.isEmpty(strUrl) && !TextUtils.isEmpty(strPost)) {
				HttpPost request = new HttpPost(strUrl);
				request.addHeader("Accept-Encoding", "gzip");
				StringEntity strEntity = new StringEntity(strPost, HTTP.UTF_8);
				request.setEntity(strEntity);
				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 4000);
				//HttpResponse httpResponse = new DefaultHttpClient()
				HttpResponse httpResponse = client.execute(request);


				InputStream inputStream = httpResponse.getEntity().getContent();


				return getJsonFromGZIP(inputStream);
			} else {
				CldLog.e("[ols]", "url is empty!");
				return null;
			}
		} catch (Exception e) {

			Log.e("checkHttpClient","exception"+e.getClass().getName()+"  "+e.getMessage());

			return null;
		}
	}

	/**
	 * post二进制数据
	 * 
	 * @param url
	 * @param postData
	 *            二进制数组
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-4-8 下午2:42:35
	 */
	public static String sapPostBinary(String strUrl, byte[] postData) {
		URL u = null;
		HttpURLConnection con = null;
		InputStream inputStream = null;
		try {
			if (!TextUtils.isEmpty(strUrl)) {
				u = new URL(strUrl);
				con = (HttpURLConnection) u.openConnection();
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setUseCaches(false);
				con.setRequestProperty("Content-Encoding", "gzip");
				con.addRequestProperty("Connection", "Keep-Alive");
				con.addRequestProperty("Accept-Encoding", "gzip");
				con.addRequestProperty("Transfer-Encoding", "chunked");
				GZIPOutputStream gzipOut = new GZIPOutputStream(
						con.getOutputStream());
				gzipOut.write(postData);
				gzipOut.finish();
				gzipOut.close();
				if (con.getResponseCode() == 200) {
					inputStream = con.getInputStream();
					return getJsonFromGZIP(inputStream);
				} else {
					return null;
				}
			} else {
				CldLog.e("[ols]", "url is empty!");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	/**
	 * 从GZIP中获取Json数据
	 * 
	 * @param is
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-4-8 下午2:43:13
	 */
	private static String getJsonFromGZIP(InputStream is) {
		String jsonString = null;
		try {
			BufferedInputStream bis = new BufferedInputStream(is);
			bis.mark(2);
			// 取前两个字节
			byte[] header = new byte[2];
			int result = bis.read(header);
			// reset输入流到开始位置
			bis.reset();
			// 判断是否是GZIP格式
			int headerData = getShort(header);
			// Gzip 流 的前两个字节是 0x1f8b
			if (result != -1 && headerData == 0x1f8b) {
				is = new GZIPInputStream(bis);
			} else {
				is = bis;
			}
			InputStreamReader reader = new InputStreamReader(is, "utf-8");
			char[] data = new char[100];
			int readSize;
			StringBuffer sb = new StringBuffer();
			while ((readSize = reader.read(data)) > 0) {
				sb.append(data, 0, readSize);
			}
			jsonString = sb.toString();
			bis.close();
			reader.close();
			CldLog.i("ols", jsonString);
			return jsonString;
		} catch (Exception e) {
			/**
			 * 网络异常，出现jsonSting 为Null情况
			 */
			CldLog.e("[ols]", "net_null");
			return null;
		}
	}

	/**
	 * Gets the short.
	 * 
	 * @param data
	 *            the data
	 * @return the short
	 */
	private static int getShort(byte[] data) {
		return (data[0] << 8) | data[1] & 0xFF;
	}

	/**
	 * 通过jsonStr获取名称
	 * 
	 * @param jsonStr
	 * @param level
	 * @return String
	 */
	public static String getNameByJson(String jsonStr, int level) {
		if (TextUtils.isEmpty(jsonStr))
			return "";

		String distsName = "";
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONArray jsonArray = null;
			if (jsonObject.has("dists")) {
				jsonArray = jsonObject.getJSONArray("dists");
			}

			if (jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonchild = jsonArray.getJSONObject(i);

					int result = jsonchild.getInt("l");
					if (result != level)
						continue;

					if (!jsonchild.has("n"))
						continue;

					distsName = jsonchild.getString("n");
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return distsName;
	}

	/**
	 * 通过jsonStr获取CityId
	 * 
	 * @param jsonStr
	 * @return int
	 */
	public static int getCityIdJson(String jsonStr) {
		if (TextUtils.isEmpty(jsonStr))
			return 0;

		int regionCity = 0;
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONArray jsonArray = null;
			if (jsonObject.has("dists")) {
				jsonArray = jsonObject.getJSONArray("dists");
			}

			if (jsonArray == null)
				return regionCity;

			for (int i = 0; i < jsonArray.length(); ++i) {
				JSONObject jsonchild = jsonArray.getJSONObject(i);
				if (!jsonchild.has("l"))
					continue;

				int result = jsonchild.getInt("l");
				if (result != 2)
					continue;

				if (jsonchild.has("id")) {
					regionCity = jsonchild.getInt("id");
					return regionCity;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return regionCity;
	}
}
