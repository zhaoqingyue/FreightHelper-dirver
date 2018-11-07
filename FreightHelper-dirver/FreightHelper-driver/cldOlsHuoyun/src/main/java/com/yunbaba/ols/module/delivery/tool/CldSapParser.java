/*
 * @Title CldSapParser.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-3-17 下午2:29:23
 * @version 1.0
 */
package com.yunbaba.ols.module.delivery.tool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import android.text.TextUtils;

import com.cld.gson.Gson;
import com.cld.gson.JsonArray;
import com.cld.gson.JsonObject;
import com.cld.gson.JsonParser;
import com.yunbaba.ols.module.delivery.tool.CldOlsErrManager.CldOlsErrCode;
import com.yunbaba.ols.module.delivery.tool.CldPubFuction.BubbleSort;
import com.yunbaba.ols.tools.parse.CldKReturn;

/**
 * 协议层解析类
 * 
 * @author Zhouls
 * @date 2015-3-17 下午2:29:23
 */
public class CldSapParser {
	/**
	 * 将json序列化为T对应的对象
	 * 
	 * @param jsonString
	 * @param cls
	 * @return
	 * @return T
	 * @author Zhouls
	 * @date 2015-3-18 下午3:53:54
	 */
	public static <T> T fromJson(String jsonString, Class<T> cls) {
		T t = null;
		try {
			Gson gson = new Gson();
			t = gson.fromJson(jsonString, cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 解析
	 * 
	 * @param jsonString
	 * @param cls
	 * @param errRes
	 * @return
	 * @return T
	 * @author Zhouls
	 * @date 2015-4-3 下午12:00:41
	 */
	public static <T> T parseJson(String jsonString, Class<T> cls,
			CldKReturn errRes) {
		T t = null;
		errRes.jsonReturn = jsonString;
		try {
			Gson gson = new Gson();
			t = gson.fromJson(jsonString, cls);
			if (null != t) {
				if (CldSapParser.hasKey(cls, "errcode")) {
					/**
					 * 如果t 有errcode属性
					 */
					errRes.errCode = (Integer) CldSapParser
							.getFieldValueByName("errcode", t);
					errRes.errMsg = (String) CldSapParser.getFieldValueByName(
							"errmsg", t);
				} else {
					if (CldSapParser.hasKey(cls.getSuperclass(), "errcode")) {
						/**
						 * 如果父类 有errcode属性
						 */
						errRes.errCode = (Integer) CldSapParser
								.getFieldValueByName("errcode", t);
						errRes.errMsg = (String) CldSapParser
								.getFieldValueByName("errmsg", t);
					} else {
						errRes.errCode = 0;
					}
				}
			} else {
				errRes.errCode = CldOlsErrCode.PARSE_ERR;
				errRes.errMsg = "解析异常";
			}
		} catch (Exception e) {
			errRes.errCode = CldOlsErrCode.PARSE_ERR;
			errRes.errMsg = "解析异常";
		}
		return t;
	}

	/**
	 * 解析泛型
	 * 
	 * @param t
	 * @param cls
	 * @param errRes
	 * @return void
	 * @author Zhouls
	 * @date 2015-8-11 上午9:06:39
	 */
	public static <T> void parseObject(T t, Class<T> cls, CldKReturn errRes) {
		if (null != t) {
			if (CldSapParser.hasKey(cls, "errcode")) {
				/**
				 * 如果t 有errcode属性
				 */
				errRes.errCode = (Integer) CldSapParser.getFieldValueByName(
						"errcode", t);
				errRes.errMsg = (String) CldSapParser.getFieldValueByName(
						"errmsg", t);
			} else {
				if (CldSapParser.hasKey(cls.getSuperclass(), "errcode")) {
					/**
					 * 
					 * 如果父类 有errcode属性
					 */
					errRes.errCode = (Integer) CldSapParser
							.getFieldValueByName("errcode", t);
					errRes.errMsg = (String) CldSapParser.getFieldValueByName(
							"errmsg", t);
				} else {
					errRes.errCode = 0;
				}
			}
		} else {
			errRes.errCode = CldOlsErrCode.PARSE_ERR;
			errRes.errMsg = "解析异常";
		}
	}

	/**
	 * 获取Json列表
	 * 
	 * @param jsonString
	 * @param name
	 * @return
	 * @return JsonArray
	 * @author Zhouls
	 * @date 2015-3-23 下午12:06:25
	 */
	public static JsonArray fromJsonArray(String jsonString, String name) {
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
		JsonArray jsonArray = jsonObject.getAsJsonArray(name);
		return jsonArray;
	}

	/**
	 * 将map反序列化为json字符串
	 * 
	 * @param map
	 *            参数表
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-18 下午3:56:16
	 */
	public static <T> String mapToJson(Map<String, T> map) {
		Gson gson = new Gson();
		String jsonStr = gson.toJson(map);
		return jsonStr;
	}

	/**
	 * 将对象反序列化为json字符串
	 * 
	 * @param o
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-20 下午3:06:12
	 */
	public static String objectToJson(Object o) {
		if (null == o) {
			return "";
		}
		return new Gson().toJson(o);
	}

	/**
	 * 参数值不为Null入表
	 * 
	 * @param map
	 * @param key
	 * @param value
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-1 下午5:03:47
	 */
	public static void putStringToMap(Map<String, Object> map, String key,
			String value) {
		if (!TextUtils.isEmpty(value)) {
			map.put(key, value);
		}
	}

	/**
	 * 参数值不为-1的int入表
	 * 
	 * @param map
	 * @param key
	 * @param value
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-2 下午2:30:13
	 */
	public static void putIntToMap(Map<String, Object> map, String key,
			int value) {
		if (-1 != value) {
			map.put(key, value);
		}
	}

	/**
	 * 参数值不为0的long入表
	 * 
	 * @param map
	 * @param key
	 * @param value
	 * @return void
	 * @author Zhouls
	 * @date 2015-4-2 下午2:30:16
	 */
	public static void putLongToMap(Map<String, Object> map, String key,
			long value) {
		if (0 != value) {
			map.put(key, value);
		}
	}

	/**
	 * 获取协议层md5Source串
	 * 
	 * @param o
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-18 上午9:14:17
	 */
	public static String formatSource(Object o) {
		if (null != o) {
			/**
			 * 获取对象中的属性名数组（已按ascii码增序排好顺序）
			 */
			String[] parms = getFiledName(o);
			if (null != parms) {
				String md5Source = "";
				for (int i = 0; i < parms.length; i++) {
					String parm = (String) getFieldValueByName(parms[i], o);
					if (i != 0) {
						if (!TextUtils.isEmpty(parm)) {
							md5Source = md5Source + "&" + parms[i] + "=" + parm;
						}
					} else {
						if (!TextUtils.isEmpty(parm)) {
							md5Source = md5Source + parms[i] + "=" + parm;
						}
					}
				}
				return md5Source;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 获取协议层md5Source串
	 * 
	 * @param map
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-3-18 上午9:54:09
	 */
	@SuppressWarnings("rawtypes")
	public static String formatSource(Map<String, Object> map) {
		if (null != map) {
			int size = map.size();
			String[] parms = new String[size];
			Iterator<?> iter = map.entrySet().iterator();
			int i = 0;
			String md5Source = "";
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				parms[i] = (String) entry.getKey();
				i++;
			}
			BubbleSort.sort(parms);
			for (i = 0; i < parms.length; i++) {
				if (i != 0) {
					if (!TextUtils.isEmpty(parms[i])) {
						md5Source = md5Source + "&" + parms[i] + "="
								+ map.get(parms[i]);
					}
				} else {
					if (!TextUtils.isEmpty(parms[i])) {
						md5Source = md5Source + parms[i] + "="
								+ map.get(parms[i]);
					}
				}
			}
			return md5Source;
		} else {
			return "";
		}
	}

	/**
	 * 获取URL请求参数
	 * 
	 * @param map
	 *            请求参数集合
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2015-6-23 上午9:20:46
	 */
	@SuppressWarnings("rawtypes")
	public static String formatUrlSource(Map<String, Object> map) {
		if (null != map) {
			int size = map.size();
			String[] parms = new String[size];
			Iterator<?> iter = map.entrySet().iterator();
			int i = 0;
			String md5Source = "";
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				parms[i] = (String) entry.getKey();
				i++;
			}
			BubbleSort.sort(parms);
			for (i = 0; i < parms.length; i++) {
				if (i != 0) {
					if (!TextUtils.isEmpty(parms[i])
							&& null != map.get(parms[i])) {
						md5Source = md5Source
								+ "&"
								+ parms[i]
								+ "="
								+ CldPubFuction.getUrlEncodeString(map.get(
										parms[i]).toString());
					}
				} else {
					if (!TextUtils.isEmpty(parms[i])
							&& null != map.get(parms[i])) {
						md5Source = md5Source
								+ parms[i]
								+ "="
								+ CldPubFuction.getUrlEncodeString(map.get(
										parms[i]).toString());
					}
				}
			}
			return md5Source;
		} else {
			return "";
		}
	}

	/**
	 * 是否Url encode字段
	 * @param map
	 * @param isUrlEncoder
	 * @return
	 * @return String
	 * @author Zhouls
	 * @date 2016-5-4 上午9:13:01
	 */
	@SuppressWarnings("rawtypes")
	public static String formatUrlSource(Map<String, Object> map,
			boolean isUrlEncoder) {
		if (null != map) {
			int size = map.size();
			String[] parms = new String[size];
			Iterator<?> iter = map.entrySet().iterator();
			int i = 0;
			String md5Source = "";
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				parms[i] = (String) entry.getKey();
				i++;
			}
			BubbleSort.sort(parms);
			for (i = 0; i < parms.length; i++) {
				if (i != 0) {
					if (!TextUtils.isEmpty(parms[i])
							&& null != map.get(parms[i])) {
						String strParm = isUrlEncoder ? CldPubFuction
								.getUrlEncodeString(map.get(parms[i])
										.toString()) : map.get(parms[i])
								.toString();
						md5Source = md5Source + "&" + parms[i] + "=" + strParm;
					}
				} else {
					if (!TextUtils.isEmpty(parms[i])
							&& null != map.get(parms[i])) {
						String strParm = isUrlEncoder ? CldPubFuction
								.getUrlEncodeString(map.get(parms[i])
										.toString()) : map.get(parms[i])
								.toString();
						md5Source = md5Source + parms[i] + "=" + strParm;
					}
				}
			}
			return md5Source;
		} else {
			return "";
		}
	}

	/**
	 * 根据属性名获取属性值
	 * 
	 * @param fieldName
	 *            属性名
	 * @param o
	 *            对象
	 * @return
	 * @return Object
	 * @author Zhouls
	 * @date 2015-3-18 上午9:15:04
	 */
	public static Object getFieldValueByName(String fieldName, Object o) {
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getMethod(getter, new Class[] {});
			Object value = method.invoke(o, new Object[] {});
			return value;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取属性名数组
	 * 
	 * @param o
	 * @return
	 * @return String[]
	 * @author Zhouls
	 * @date 2015-3-18 上午9:15:30
	 */
	public static String[] getFiledName(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		String[] fieldNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			fieldNames[i] = fields[i].getName();
		}
		return fieldNames;
	}

	/**
	 * 类是否有Name 属性
	 * 
	 * @param o
	 * @param name
	 * @return
	 * @return boolean
	 * @author Zhouls
	 * @date 2015-3-19 上午10:51:51
	 */
	@SuppressWarnings("rawtypes")
	public static boolean hasKey(Class o, String name) {
		Field[] fields = o.getDeclaredFields();
		if (!TextUtils.isEmpty(name)) {
			for (int i = 0; i < fields.length; i++) {
				if (name.equals(fields[i].getName())) {
					return true;
				}
			}
		}
		return false;
	}
}
