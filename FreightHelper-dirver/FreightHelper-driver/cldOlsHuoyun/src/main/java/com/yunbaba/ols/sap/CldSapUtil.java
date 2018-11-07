/*
 * @Title CldSapUtil.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:59
 * @version 1.0
 */
package com.yunbaba.ols.sap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.cld.log.CldLog;
import com.yunbaba.ols.api.CldKConfigAPI;
import com.yunbaba.ols.bll.CldBllUtil;
import com.yunbaba.ols.bll.CldKConfig.ConfigDomainType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * 协议层公共方法类
 * 
 * @author Zhouls
 * @date 2014-9-23 上午11:02:38
 */
public class CldSapUtil {

	/**
	 * 从输入流里读取字符串并解决中文乱码
	 * 
	 * @param is
	 *            the is
	 * @return String
	 * @author Zhouls
	 * @date 2014-10-29 上午9:24:52
	 */
	public static String getStringFromFileIn(InputStream is) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(is, "GBK"));
			String data = "";
			while ((data = br.readLine()) != null) {
				sb.append(data);
			}
		} catch (IOException e1) {
		}
		String result = sb.toString();
		return result;
	}

	/**
	 * 文件转byte[]
	 * 
	 * @param file
	 * @return
	 * @return byte[]
	 * @author Zhouls
	 * @date 2015-3-4 下午5:55:03
	 */
	public static byte[] fileToByte(File file) {
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 获取配置域名头
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-10-24 下午12:00:16
	 */
	public static String getNaviSvrKConfig() {
		String naviSvrKC;
		if (CldBllUtil.getInstance().isTestVersion()) {
			// 测试版本
			naviSvrKC = "http://sttest.careland.com.cn/";
		} else {
			// 正式版本
			naviSvrKC = "http://st.careland.com.cn/";
		}
		return naviSvrKC;
	}

	/**
	 * 获取BD域名头
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-11-17 下午4:43:08
	 */
	public static String getNaviSvrBD() {
		String naviSvrBd = getBdTestDomain();
		if (TextUtils.isEmpty(naviSvrBd)) {
			naviSvrBd = getSvrAddr(ConfigDomainType.NAVI_SVR_BD);
		}
		return naviSvrBd;
	}

	/**
	 * 获取在线导航域名头
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-4-8 下午4:34:48
	 */
	public static String getNaviSvrON() {
		String naviSvrOn = getOnTestDomain();
		if (TextUtils.isEmpty(naviSvrOn)) {
			naviSvrOn = getSvrAddr(ConfigDomainType.NAVI_SVR_ONLINENAVI);
		}
		return naviSvrOn;
	}

	/**
	 * 获取帐户系统域名头
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-15 下午5:44:36
	 */
	public static String getNaviSvrKA() {
		return getSvrAddr(ConfigDomainType.NAVI_SVR_KACCOUNT);
	}

	/**
	 * 获取零散接口域名头
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-25 上午8:31:49
	 */
	public static String getNaviSvrPub() {
		return getSvrAddr(ConfigDomainType.NAVI_SVR_PUB);
	}

	/**
	 * 获取Website域名头
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-25 上午8:31:49
	 */
	public static String getNaviSvrWS() {
		return getSvrAddr(ConfigDomainType.NAVI_SVR_WEBSITE);
	}

	/**
	 * 获取消息系统域名头
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-15 下午5:49:17
	 */
	public static String getNaviSvrMsg() {
		String naviSvrMsg = getMsgTestDomain();
		if (TextUtils.isEmpty(naviSvrMsg)) {
			naviSvrMsg = getSvrAddr(ConfigDomainType.NAVI_SVR_MSG);
		}
		return naviSvrMsg;
	}

	/**
	 * 获取一键通域名头
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-10-29 下午4:54:06
	 */
	public static String getNaviSvrPPT() {
		String naviSvrPpt = getKAKeyCallTestDomain();
		if (TextUtils.isEmpty(naviSvrPpt)) {
			naviSvrPpt = getSvrAddr(ConfigDomainType.NAVI_SVR_PPTCALL);
		}
		return naviSvrPpt;
	}

	/**
	 * 获取位置服务域名头
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-15 下午5:49:17
	 */
	public static String getNaviSvrPos() {
		String naviSvrPos = getPosTestDomain();
		if (TextUtils.isEmpty(naviSvrPos)) {
			naviSvrPos = getSvrAddr(ConfigDomainType.NAVI_SVR_POS);
		}
		return naviSvrPos;
	}

	/**
	 * 获取用户鉴权域名头
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-15 下午5:44:36
	 */
	public static String getNaviSvrAC() {
		return getSvrAddr(ConfigDomainType.NAVI_SVR_AC);
	}

	/**
	 * 获取路况域名头
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-12-26 下午5:07:44
	 */
	public static String getNaviSvrRTI() {
		return getSvrAddr(ConfigDomainType.NAVI_SVR_RTI);
	}

	/**
	 * 获取K云域名头
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-12-26 下午5:07:48
	 */
	public static String getNaviSvrKC() {
		return getSvrAddr(ConfigDomainType.NAVI_SVR_KC);
	}
	
	/**
	 * 获取天气域名头
	 * 
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-7-8 上午8:47:40
	 */
	public static String getNaviSvrWeather() {
		String addr = getSvrAddr(ConfigDomainType.NAVI_SVR_KWEATHER);
		return addr;
	}
	
	/** 获取天气域名头
	 * @return
	 * @return String
	 * @author buxc
	 * @date 2016年3月4日 下午4:04:11
	 */ 
	public static String getNaviSvrSearch() {
		String addr = getSvrAddr(ConfigDomainType.NAVI_SVR_SEARCH);
		return addr;
	}
	
	/** 获取货运域名头
	 * @return
	 * @return String
	 * @author buxc
	 * @date 2016年3月4日 下午4:04:11
	 */ 
	public static String getNaviSvrHY() {
		String addr = getSvrAddr(ConfigDomainType.NAVI_SVR_HY);
//		String addr = "http://test.matiquan.cn:50301/khyapi/hyapi/";
//	String addr = "http://192.168.200.213/khyapi/hyapi/";
		return addr;
	}

	
	/** 获取货运离线下载域名目录
	 * @return String
	 * @author weian
	 * @date 2016年07月14日 上午11:31:11
	 */ 
	public static String getNaviHyDownload() {
		String addr = getSvrAddr(ConfigDomainType.NAVI_SVR_HY_DOWNLOAD);
		return addr;
	}
	
	
	/**
	 * 获取域名头
	 * 
	 * @param svrType
	 *            请求类型
	 * @return String 返回域名头信息
	 * @author huagx
	 * @date 2014-8-19 下午5:56:12
	 */
	public static String getSvrAddr(int svrType) {
		return CldKConfigAPI.getInstance().getSvrDomain(svrType);
	}
	
	/**
	 * @annotation :获取限行上报域名头
	 * @author : yuyh
	 * @date :2016-8-27下午3:30:13
	 * @parama :
	 * @return :
	 **/
	public static String getNaviSvrPo()
	{
		String addr = getSvrAddr(ConfigDomainType.NAVI_SVR_PO);
		return addr;
	}

	/**
	 * 获取本地IP地址
	 * 
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-13 上午10:51:15
	 */
	public static String getLocalIpAddress() {
		String ip = "192.168.1.1";
		try {
			Socket socket = new Socket("www.careland.com.cn", 80);
			ip = socket.getLocalAddress().toString().substring(1);
		} catch (Exception e) {
		}
		return ip;
	}

	/**
	 * 获取本地时间戳
	 * 
	 * @return long
	 * @author Zhouls
	 * @date 2014-9-19 上午10:11:51
	 */
	public static long getLocalTime() {
		long localTime = System.currentTimeMillis() / 1000;
		return localTime;
	}

	/**
	 * 发送短信
	 * 
	 * @param address
	 *            地址
	 * @param content
	 *            内容
	 * @param context
	 *            the context
	 * @return int
	 * @author Zhouls
	 * @date 2014-8-22 下午4:22:03
	 */
	public static int sendSMS(String address, String content, Context context) {
		int errCode = -1;
		/**
		 * 处理返回的发送状态
		 */
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
				sentIntent, 0);
		// register the Broadcast Receivers
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					CldLog.d("ols", "SENT_SMS_ACTION sendOK_sms");
					// 短信发送成功
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					break;
				}
			}
		}, new IntentFilter(SENT_SMS_ACTION));
		/**
		 * 处理返回的接收状态
		 */
		String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
		// create the deilverIntent parameter
		Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
		PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0,
				deliverIntent, 0);
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				// 收信成功
				CldLog.d("ols", "DELIVERED_SMS_ACTION sendOK_sms");
			}
		}, new IntentFilter(DELIVERED_SMS_ACTION));

		SmsManager smsManager = SmsManager.getDefault();
		List<String> divideContents = smsManager.divideMessage(content);
		for (String text : divideContents) {
			smsManager.sendTextMessage(address, null, text, sentPI, deliverPI);
		}
		return errCode;
	}

	/**
	 * 获取设备guid
	 * 
	 * @param timeStamp
	 *            the time stamp
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-22 下午1:59:07
	 */
	public static String getGuid(long timeStamp) {
		String guid;
		String imsi = CldBllUtil.getInstance().getImsi();
		String imei = CldBllUtil.getInstance().getImei();
		if (null == imsi) {
			imsi = "SIMCARDID";
		}
		if (null == imei) {
			imei = "IMEI";
		}
		guid = imsi + imei + timeStamp;
		guid = MD5(guid);
		return guid;
	}

	/**
	 * 随机生成字符串
	 * 
	 * @param pwd_len
	 *            随机密码长度
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-1-19 下午4:32:03
	 */
	public static String genRandomNum(int pwd_len) {
		// 35是因为数组是从0开始的，26个字母+10个 数字
		final int maxNum = 36;
		int i; // 生成的随机数
		int count = 0; // 生成的密码的长度
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < pwd_len) {
			// 生成随机数，取绝对值，防止 生成负数，
			i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		return pwd.toString();
	}

	/**
	 * 非必须字段(String)
	 * 
	 * @param strName
	 *            the str name
	 * @param str
	 *            the str
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-19 上午8:58:41
	 */
	public static String isStrParmRequest(String strName, String str) {
		if (null == str || str.length() <= 0) {
			return "";
		} else {
			return strName + str;
		}
	}

	/**
	 * 非必须字段（int）
	 * 
	 * @param strName
	 *            the str name
	 * @param val
	 *            the val
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-19 上午9:00:35
	 */
	public static String isIntParmRequest(String strName, int val) {
		if (val == -1) {
			return "";
		} else {
			return strName + val;
		}
	}

	/**
	 * 非必须字段（long）
	 * 
	 * @param strName
	 *            the str name
	 * @param val
	 *            the val
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-19 上午9:00:35
	 */
	public static String isLongParmRequest(String strName, long val) {
		if (val == 0) {
			return "";
		} else {
			return strName + val;
		}
	}

	/**
	 * MD5加密
	 * 
	 * @param sourceStr
	 *            the source str
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-13 下午2:39:21
	 */
	public static String MD5(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sourceStr.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}
		return result;
	}

	/**
	 * 解密密钥
	 * 
	 * @param keyCode
	 *            the key code
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-14 下午12:01:57
	 */
	public static String decodeKey(String keyCode) {
		if (TextUtils.isEmpty(keyCode)) {
			return "";
		} else {
			String key = keyCode.substring(keyCode.length() - 6);
			String str = keyCode.substring(0, keyCode.length() - 6);
			CldEDecrpy cldEDecrpy = new CldEDecrpy(str, key);
			keyCode = cldEDecrpy.decrypt();
			return keyCode;
		}
	}

	/**
	 * 解密密钥(test明文)
	 * 
	 * @param keyCode
	 *            the key code
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-14 下午12:01:57
	 */
	public static String ecodeKey(String keyCode) {
		return keyCode;
	}

	/**
	 * 格式化Json字符串
	 * 
	 * @param content
	 *            the content
	 * @return String
	 * @author Zhouls
	 * @date 2014-12-17 上午9:27:28
	 */
	public static String formatJsonString(String content) {
		if (null != content) {
			content = content.substring(content.indexOf("{"),
					content.lastIndexOf("}") + 1);
		}
		return content;
	}

	/**
	 * 密钥加密 解密
	 * 
	 * @author Zhouls
	 * @date 2014-8-13 下午4:05:06
	 */
	public static class CldEDecrpy {
		/** The str. */
		private String str;
		/** The key box. */
		private int[] keyBox;

		/**
		 * Instantiates a new cld e decrpy.
		 * 
		 * @param str
		 *            the str
		 * @param key
		 *            the key
		 */
		public CldEDecrpy(String str, String key) {
			this.str = str;
			keyBox = new int[3];
			keyBox[0] = Integer.parseInt(key.substring(0, 2));
			keyBox[1] = Integer.parseInt(key.substring(2, 4));
			keyBox[2] = Integer.parseInt(key.substring(4, 6));
		}

		/**
		 * 加密
		 * 
		 * @return char
		 * @author Zhouls
		 * @date 2014-8-13 下午5:13:26
		 */
		public String encrypt() {
			String enStr = "";
			int tempKey;
			for (int i = 0; i < str.length(); i++) {
				tempKey = keyBox[i % 3] % 24;
				enStr = enStr
						+ getChEncrpyt(getIndexEncrpyt(str.charAt(i)) + tempKey);
			}
			return enStr;
		}

		/**
		 * 解密
		 * 
		 * @return char
		 * @author Zhouls
		 * @date 2014-8-13 下午5:00:18
		 */
		public String decrypt() {
			String deStr = "";
			int tempKey;
			for (int i = 0; i < str.length(); i++) {
				tempKey = keyBox[i % 3] % 24;
				deStr += getChDecrpyt(getIndexDecrpyt(str.charAt(i)) - tempKey);
			}
			return deStr;
		}

		/**
		 * 获取加密字符
		 * 
		 * @param i
		 *            the i
		 * @return String
		 * @author Zhouls
		 * @date 2014-8-13 下午4:22:18
		 */
		private char getChEncrpyt(int i) {
			if (i >= 0 && i <= 25) {
				return chr(ord('a') + i);
			} else if (i >= 26 && i <= 35) {
				return chr(ord('0') + i - 26);
			} else if (i >= 36 && i <= 61) {
				return chr(ord('A') + i - 36);
			}
			return '0';
		}

		/**
		 * 获取解密字符
		 * 
		 * @param i
		 *            the i
		 * @return String
		 * @author Zhouls
		 * @date 2014-8-13 下午4:21:09
		 */
		private char getChDecrpyt(int i) {
			if (i == 0) {
				return '-';
			} else if (i == 1) {
				return ';';
			} else if (i >= 2 && i <= 11) {
				return chr(i + ord('0') - 2);
			} else if (i >= 12 && i <= 37) {
				return chr(i + ord('A') - 12);
			}
			return '0';
		}

		/**
		 * 获取加密索引
		 * 
		 * @param c
		 *            the c
		 * @return int
		 * @author Zhouls
		 * @date 2014-8-13 下午4:20:45
		 */
		private int getIndexDecrpyt(char c) {
			if (ord(c) >= ord('0') && ord(c) <= ord('9')) {
				return ord(c) + 26 - ord('0');
			} else if (ord(c) >= ord('A') && ord(c) <= ord('Z')) {
				return ord(c) + 36 - ord('A');
			} else {
				return ord(c) - ord('a');
			}
		}

		/**
		 * 获取解密索引
		 * 
		 * @param c
		 *            the c
		 * @return int
		 * @author Zhouls
		 * @date 2014-8-13 下午4:22:13
		 */
		private int getIndexEncrpyt(char c) {
			if (c == '-') {
				return 0;
			}
			if (c == ';') {
				return 1;
			} else if (c >= '0' && c <= '9') {
				return c - '0' + 2;
			} else if (c >= 'A' && c <= 'Z') {
				return c - 'A' + 12;
			}
			return 0;
		}

		/**
		 * int转char
		 * 
		 * @param i
		 *            the i
		 * @return char
		 * @author Zhouls
		 * @date 2014-8-13 下午4:31:41
		 */
		private char chr(int i) {
			char c = (char) i;
			return c;
		}

		/**
		 * char转int
		 * 
		 * @param c
		 *            the c
		 * @return int
		 * @author Zhouls
		 * @date 2014-8-13 下午4:29:52
		 */
		private int ord(char c) {
			int a = c;
			return a;
		}
	}

	/**
	 * Gzip 压缩工具
	 * 
	 * @author Zhouls
	 * @date 2015-3-9 下午5:20:10
	 */
	public static class GZipUtils {
		public static final int BUFFER = 1024;
		public static final String EXT = ".gz";

		/**
		 * 压缩数据
		 * 
		 * @param data
		 * @return
		 * @throws Exception
		 * @return byte[]
		 * @author Zhouls
		 * @date 2015-3-9 下午5:20:25
		 */
		public byte[] compress(byte[] data) throws Exception {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// 压缩
			compress(bais, baos);
			byte[] output = baos.toByteArray();
			baos.flush();
			baos.close();
			bais.close();
			return output;
		}

		/**
		 * 文件压缩
		 * 
		 * @param file
		 * @throws Exception
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-9 下午5:20:42
		 */
		public void compress(File file) throws Exception {
			compress(file, true);
		}

		/**
		 * 文件压缩
		 * 
		 * @param file
		 * @param delete
		 * @throws Exception
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-9 下午5:20:59
		 */
		public void compress(File file, boolean delete) throws Exception {
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(file.getPath() + EXT);
			compress(fis, fos);
			fis.close();
			fos.flush();
			fos.close();
			if (delete) {
				file.delete();
			}
		}

		/**
		 * 数据压缩
		 * 
		 * @param is
		 * @param os
		 * @throws Exception
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-9 下午5:21:09
		 */
		public void compress(InputStream is, OutputStream os) throws Exception {
			GZIPOutputStream gos = new GZIPOutputStream(os);
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = is.read(data, 0, BUFFER)) != -1) {
				gos.write(data, 0, count);
			}
			gos.finish();
			gos.flush();
			gos.close();
		}

		/**
		 * 文件压缩
		 * 
		 * @param path
		 * @throws Exception
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-9 下午5:21:18
		 */
		public void compress(String path) throws Exception {
			compress(path, true);
		}

		/**
		 * 文件压缩
		 * 
		 * @param path
		 * @param delete
		 * @throws Exception
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-9 下午5:21:28
		 */
		public void compress(String path, boolean delete) throws Exception {
			File file = new File(path);
			compress(file, delete);
		}

		/**
		 * 数据解压缩
		 * 
		 * @param data
		 * @return
		 * @throws Exception
		 * @return byte[]
		 * @author Zhouls
		 * @date 2015-3-9 下午5:21:37
		 */
		public byte[] decompress(byte[] data) throws Exception {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// 解压缩
			decompress(bais, baos);
			data = baos.toByteArray();
			baos.flush();
			baos.close();
			bais.close();
			return data;
		}

		/**
		 * 文件解压缩
		 * 
		 * @param file
		 * @throws Exception
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-9 下午5:21:45
		 */
		public void decompress(File file) throws Exception {
			decompress(file, true);
		}

		/**
		 * 文件解压缩
		 * 
		 * @param file
		 * @param delete
		 *            是否删除原始文件
		 * @throws Exception
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-9 下午5:21:52
		 */
		public void decompress(File file, boolean delete) throws Exception {
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(file.getPath().replace(
					EXT, ""));
			decompress(fis, fos);
			fis.close();
			fos.flush();
			fos.close();
			if (delete) {
				file.delete();
			}
		}

		/**
		 * 数据解压缩
		 * 
		 * @param is
		 * @param os
		 * @throws Exception
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-9 下午5:22:05
		 */
		public void decompress(InputStream is, OutputStream os)
				throws Exception {
			GZIPInputStream gis = new GZIPInputStream(is);
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = gis.read(data, 0, BUFFER)) != -1) {
				os.write(data, 0, count);
			}
			gis.close();
		}

		/**
		 * 文件解压缩
		 * 
		 * @param path
		 * @throws Exception
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-9 下午5:22:12
		 */
		public void decompress(String path) throws Exception {
			decompress(path, true);
		}

		/**
		 * 文件解压缩
		 * 
		 * @param path
		 * @param delete
		 * @throws Exception
		 * @return void
		 * @author Zhouls
		 * @date 2015-3-9 下午5:22:20
		 */
		public void decompress(String path, boolean delete) throws Exception {
			File file = new File(path);
			decompress(file, delete);
		}
	}

	/**
	 * url解析键值对
	 * 
	 * @author Zhouls
	 * @date 2015-1-27 下午2:28:05
	 */
	public static class URLAnalysis {
		private Map<String, String> paramMap = new HashMap<String, String>();

		public void analysis(String url) {
			paramMap.clear();
			if (!"".equals(url)) {// 如果URL不是空字符串
				url = url.substring(url.indexOf('?') + 1);
				String paramaters[] = url.split("&");
				for (String param : paramaters) {
					String values[] = param.split("=");
					paramMap.put(values[0], values[1]);
				}
			}
		}

		public String getParam(String name) {
			if (!TextUtils.isEmpty(paramMap.get(name))) {
				return paramMap.get(name);
			} else {
				return "";
			}
		}
	}

	/**
	 * 解码
	 * 
	 * @param urlEncodeStr
	 *            the url encode str
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-26 上午9:26:35
	 */

	public static String getUrlDecodeString(String urlEncodeStr) {
		String decodeStr = "";
		try {
			if (!TextUtils.isEmpty(urlEncodeStr)) {
				decodeStr = URLDecoder.decode(urlEncodeStr, "utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decodeStr;
	}

	/**
	 * 转码
	 * 
	 * @param urlDecodeStr
	 *            the url decode str
	 * @return String
	 * @author Zhouls
	 * @date 2014-8-28 上午10:26:54
	 */
	public static String getUrlEncodeString(String urlDecodeStr) {
		String encodeStr = "";
		try {
			if (!TextUtils.isEmpty(urlDecodeStr)) {
				encodeStr = URLEncoder.encode(urlDecodeStr, "utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodeStr;
	}

	/**
	 * 转成utf-8
	 * 
	 * @param toUtfStr
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-10 下午5:02:53
	 */
	public static String getUtfStr(String toUtfStr) {
		byte[] a;
		try {
			a = toUtfStr.getBytes("unicode");
			return new String(a, "Utf-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 服务初始化初始化配置文件
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2014-9-22 下午5:55:32
	 */
	public static void initMsgTestDomain() {
		m_msgTestDomain = null;
		m_AKeyCallTestDomain = null;
		m_OnTestDomain = null;
		m_PosTestDomain = null;
		m_BdTestDomain = null;
	}

	/** 消息开发使用域名. */
	private static String m_msgTestDomain = null;

	/**
	 * 获取消息的开发域名
	 * 
	 * @return String
	 * @author Wenap
	 * @date 2014-9-17 下午7:51:22
	 */
	protected static String getMsgTestDomain() {
		if (null != m_msgTestDomain) {
			return m_msgTestDomain;
		}
		if (CldBllUtil.getInstance().isTestVersion()) {
			try {
				String strPath = CldBllUtil.getInstance().getAppPath()
						+ "/msgtestaddr.cfg";
				File file = new File(strPath);
				if (file.exists()) {
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					m_msgTestDomain = reader.readLine();
					reader.close();
				}
			} catch (Exception e) {
			}
		}
		return m_msgTestDomain;
	}

	/** 一键通开发使用域名 */
	private static String m_AKeyCallTestDomain = null;

	/**
	 * 获取一键通的开发域名
	 * 
	 * @return String
	 * @author Wenap
	 * @date 2014-9-17 下午7:51:22
	 */
	protected static String getKAKeyCallTestDomain() {
		if (null != m_AKeyCallTestDomain) {
			return m_AKeyCallTestDomain;
		}
		if (CldBllUtil.getInstance().isTestVersion()) {
			try {
				File file = new File(CldBllUtil.getInstance().getAppPath()
						+ "/akeycalltestaddr.cfg");
				if (file.exists()) {
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					m_AKeyCallTestDomain = reader.readLine();
					reader.close();
				}
			} catch (Exception e) {
			}
		}
		return m_AKeyCallTestDomain;
	}

	/** pos开发使用域名. */
	private static String m_PosTestDomain = null;

	/**
	 * 获取pos的开发域名
	 * 
	 * @return String
	 * @author Wenap
	 * @date 2014-9-17 下午7:51:22
	 */
	protected static String getPosTestDomain() {
		if (null != m_PosTestDomain) {
			return m_PosTestDomain;
		}
		if (CldBllUtil.getInstance().isTestVersion()) {
			try {
				File file = new File(CldBllUtil.getInstance().getAppPath()
						+ "/postestaddr.cfg");
				if (file.exists()) {
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					m_PosTestDomain = reader.readLine();
					reader.close();
				}
			} catch (Exception e) {
			}
		}
		return m_PosTestDomain;
	}

	/** ON开发使用域名 */
	private static String m_OnTestDomain = null;

	/**
	 * 获取在线导航的开发域名
	 * 
	 * @return String
	 * @author Wenap
	 * @date 2014-9-17 下午7:51:22
	 */
	protected static String getOnTestDomain() {
		if (null != m_OnTestDomain) {
			return m_OnTestDomain;
		}
		if (CldBllUtil.getInstance().isTestVersion()) {
			try {
				File file = new File(CldBllUtil.getInstance().getAppPath()
						+ "/ontestaddr.cfg");
				if (file.exists()) {
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					m_OnTestDomain = reader.readLine();
					reader.close();
				}
			} catch (Exception e) {
			}
		}
		return m_OnTestDomain;
	}

	/** BD开发使用域名 */
	private static String m_BdTestDomain = null;

	/**
	 * 获取在线导航的开发域名
	 * 
	 * @return String
	 * @author Wenap
	 * @date 2014-9-17 下午7:51:22
	 */
	protected static String getBdTestDomain() {
		if (null != m_BdTestDomain) {
			return m_BdTestDomain;
		}
		if (CldBllUtil.getInstance().isTestVersion()) {
			try {
				File file = new File(CldBllUtil.getInstance().getAppPath()
						+ "/bdtestaddr.cfg");
				if (file.exists()) {
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					m_BdTestDomain = reader.readLine();
					reader.close();
				}
			} catch (Exception e) {
			}
		}
		return m_BdTestDomain;
	}
}
