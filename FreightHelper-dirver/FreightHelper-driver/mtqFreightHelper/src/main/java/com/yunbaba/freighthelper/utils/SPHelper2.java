package com.yunbaba.freighthelper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yunbaba.freighthelper.bean.AddressBean;
import com.yunbaba.ols.api.CldKAccountAPI;

/**
 * Created by zhonghm on 2018/4/17.
 */

public class SPHelper2 {

    Context context;
    SharedPreferences sharedPreferences;
    static String currentsppath = "";

    private static SPHelper2 instance;

    public static synchronized SPHelper2 getInstance(Context context) {

        if (instance == null)
            instance = new SPHelper2(context.getApplicationContext());

        // if(SharedPreferencesCompat)

        if (!currentsppath.equals("spfile2" + CldKAccountAPI.getInstance().getKuidLogin())) {

            instance.reCreate();
        }

        return instance;
    }

    private SPHelper2(Context context) {
        this.context = context;

        currentsppath = "spfile2" + CldKAccountAPI.getInstance().getKuidLogin();

        sharedPreferences = context.getSharedPreferences("spfile2" + CldKAccountAPI.getInstance().getKuidLogin(),
                Context.MODE_PRIVATE);

    }

    public synchronized void reCreate() {

        MLog.e("sphelper2", "recreate");

        currentsppath = "spfile2" + CldKAccountAPI.getInstance().getKuidLogin();

        sharedPreferences = context.getSharedPreferences("spfile2" + CldKAccountAPI.getInstance().getKuidLogin(),
                Context.MODE_PRIVATE);

    }


    public synchronized void writeMarkStoreAddress(String waybillid,AddressBean bean) {
        put("markaddrstorewaybillandid"+waybillid, GsonTool.getInstance().toJson(bean));
    }


    public synchronized AddressBean readMarkStoreAddress(String waybillid) {
        String str = getString("markaddrstorewaybillandid"+waybillid);


        if (!TextUtils.isEmpty(str)){

            try{

                AddressBean bean = GsonTool.getInstance().fromJson(str, AddressBean.class);
                return bean;
            }catch(Exception e){
                     MLog.e("errorrrr",e.getMessage()+"");
            }


        }
        return null;
    }

    public void put(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }


//    /**
//     * 保存数据到SharedPreferences
//     *
//     * @param key   键
//     * @param value 需要保存的数据
//     * @return 保存结果
//     */
//    public  boolean putData(String key, Object value) {
//        boolean result;
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        String type = value.getClass().getSimpleName();
//        try {
//            switch (type) {
//                case "Boolean":
//                    editor.putBoolean(key, (Boolean) value);
//                    break;
//                case "Long":
//                    editor.putLong(key, (Long) value);
//                    break;
//                case "Float":
//                    editor.putFloat(key, (Float) value);
//                    break;
//                case "String":
//                    editor.putString(key, (String) value);
//                    break;
//                case "Integer":
//                    editor.putInt(key, (Integer) value);
//                    break;
//                default:
//                    Gson gson = new Gson();
//                    String json = gson.toJson(value);
//                    editor.putString(key, json);
//                    break;
//            }
//            result = true;
//        } catch (Exception e) {
//            result = false;
//            e.printStackTrace();
//        }
//        editor.apply();
//        return result;
//    }
//
//    /**
//     * 获取SharedPreferences中保存的数据
//     *
//     * @param key          键
//     * @param defaultValue 获取失败默认值
//     * @return 从SharedPreferences读取的数据
//     */
//    public Object getData(String key, Object defaultValue) {
//        Object result;
//        String type = defaultValue.getClass().getSimpleName();
//        try {
//            switch (type) {
//                case "Boolean":
//                    result = sharedPreferences.getBoolean(key, (Boolean) defaultValue);
//                    break;
//                case "Long":
//                    result = sharedPreferences.getLong(key, (Long) defaultValue);
//                    break;
//                case "Float":
//                    result = sharedPreferences.getFloat(key, (Float) defaultValue);
//                    break;
//                case "String":
//                    result = sharedPreferences.getString(key, (String) defaultValue);
//                    break;
//                case "Integer":
//                    result = sharedPreferences.getInt(key, (Integer) defaultValue);
//                    break;
//                default:
//                    Gson gson = new Gson();
//                    String json = sharedPreferences.getString(key, "");
//                    if (!json.equals("") && json.length() > 0) {
//                        result = gson.fromJson(json, defaultValue.getClass());
//                    } else {
//                        result = defaultValue;
//                    }
//                    break;
//            }
//        } catch (Exception e) {
//            result = null;
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    /**
//     * 用于保存集合
//     *
//     * @param key  key
//     * @param list 集合数据
//     * @return 保存结果
//     */
//    public  <T> boolean putListData(String key, List<T> list) {
//        boolean result;
//        String type = list.get(0).getClass().getSimpleName();
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        JsonArray array = new JsonArray();
//        try {
//            switch (type) {
////                case "Boolean":
////                    for (int i = 0; i < list.size(); i++) {
////                        array.add((Boolean) list.get(i));
////                    }
////                    break;
////                case "Long":
////                    for (int i = 0; i < list.size(); i++) {
////                        array.add((Long) list.get(i));
////                    }
////                    break;
////                case "Float":
////                    for (int i = 0; i < list.size(); i++) {
////                        array.add((Float) list.get(i));
////                    }
////                    break;
////                case "String":
////                    for (int i = 0; i < list.size(); i++) {
////                        array.add((String) list.get(i));
////                    }
////                    break;
////                case "Integer":
////                    for (int i = 0; i < list.size(); i++) {
////                        array.add((Integer) list.get(i));
////                    }
////                    break;
//                default:
//                    Gson gson = new Gson();
//                    for (int i = 0; i < list.size(); i++) {
//                        JsonElement obj = gson.toJsonTree(list.get(i));
//                        array.add(obj);
//                    }
//                    break;
//            }
//            editor.putString(key, array.toString());
//            result = true;
//        } catch (Exception e) {
//            result = false;
//            e.printStackTrace();
//        }
//        editor.apply();
//        return result;
//    }
//
//    /**
//     * 获取保存的List
//     *
//     * @param key key
//     * @return 对应的Lis集合
//     */
//    public  <T> List<T> getListData(String key, Class<T> cls) {
//        List<T> list = new ArrayList<>();
//        String json = sp.getString(key, "");
//        if (!json.equals("") && json.length() > 0) {
//            Gson gson = new Gson();
//            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
//            for (JsonElement elem : array) {
//                list.add(gson.fromJson(elem, cls));
//            }
//        }
//        return list;
//    }
//    /**
//     * 用于保存集合
//     *
//     * @param key key
//     * @param map map数据
//     * @return 保存结果
//     */
//    public  <K, V> boolean putHashMapData(String key, Map<K, V> map) {
//        boolean result;
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        try {
//            Gson gson = new Gson();
//            String json = gson.toJson(map);
//            editor.putString(key, json);
//            result = true;
//        } catch (Exception e) {
//            result = false;
//            e.printStackTrace();
//        }
//        editor.apply();
//        return result;
//    }
//
//    /**
//     * 用于保存集合
//     *
//     * @param key key
//     * @return HashMap
//     */
//    public <V> HashMap<String, V> getHashMapData(String key, Class<V> clsV) {
//        String json = sharedPreferences.getString(key, "");
//        HashMap<String, V> map = new HashMap<>();
//        Gson gson = new Gson();
//        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
//        Set<Map.Entry<String, JsonElement>> entrySet = obj.entrySet();
//        for (Map.Entry<String, JsonElement> entry : entrySet) {
//            String entryKey = entry.getKey();
//            JsonObject value = (JsonObject) entry.getValue();
//            map.put(entryKey, gson.fromJson(value, clsV));
//        }
//        MLog.e("SharedPreferencesUtil", obj.toString());
//        return map;
//    }
}