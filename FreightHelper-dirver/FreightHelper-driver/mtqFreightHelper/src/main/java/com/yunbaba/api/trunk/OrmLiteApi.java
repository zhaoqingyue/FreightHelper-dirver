package com.yunbaba.api.trunk;

import android.content.Context;

import com.litesuits.orm.BuildConfig;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.yunbaba.api.trunk.listner.OnQueryResultListner;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.ThreadPoolTool;
import com.yunbaba.ols.api.CldKAccountAPI;

import java.util.List;

/**
 * @author ligangfan
 * @description ORM数据库操作的封装，对外提供接口
 */
public class OrmLiteApi {

    //可以指定数据库文件保存的位置
//	public static final String SD_CARD = Environment.getExternalStorageDirectory().getAbsolutePath();
//	public static final String DB_NAME = SD_CARD + "/lite/orm/cascade.db";
    private String DB_NAME_PATH;//= FreightHelperApplication.getContext().getDir("db", Context.MODE_PRIVATE).getAbsolutePath();
    private static String DB_NAME;// = "mtq_" + CldKAccountAPI.getInstance().getKuid() + "_data.db";
    private LiteOrm mLiteOrm = null;

    private static OrmLiteApi mInstance = null;
    private static Context mcontext;

    public static void setContext(Context context) {
        mcontext = context;
    }


    public static void init(Context context) {

        //DB_NAME_PATH = context.getDir("db", Context.MODE_PRIVATE).getAbsolutePath();
        DB_NAME = context.getDatabasePath("mtq_" + CldKAccountAPI.getInstance().getKuidLogin() + "_data.db").getAbsolutePath();

        //DB_NAME = ;
    }

    //检查是否切换账户了，如果切换了要切换ORMLITE的数据库用户数据对应路径
    //可能会有同步的问题
    public boolean CheckSelfAndFixDBName(Context context) {


        //if(TextUtils.isEmpty(CldKAccountAPI.getInstance().getKuidLogin()))
        //	returnl


        String currentPath = context.getDatabasePath("mtq_" + CldKAccountAPI.getInstance().getKuidLogin() + "_data.db").getAbsolutePath();

        if (mInstance != null && mLiteOrm != null) {

            if (!mLiteOrm.getDataBaseConfig().dbName.equals(currentPath)) {

                MLog.e("check", currentPath + "  " + mLiteOrm.getDataBaseConfig().dbName);

                CloseOrm();

                DB_NAME = context.getDatabasePath("mtq_" + CldKAccountAPI.getInstance().getKuidLogin() + "_data.db").getAbsolutePath();

                initLiteOrm();

//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}

                return true;

            }


        }

        return false;

    }


    private OrmLiteApi() {

        if (DB_NAME == null) {
            if (MainApplication.getContext() != null)
                init(MainApplication.getContext());
            else {
                init(mcontext);
            }
        }


        initLiteOrm();

    }


    public void initLiteOrm() {

//        ThreadPoolTool.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {

        DataBaseConfig dataBaseConfig = new DataBaseConfig(MainApplication.getContext());
//		dataBaseConfig.dbName = DB_NAME_PATH + File.separator + DB_NAME;
        dataBaseConfig.dbName = DB_NAME;
        dataBaseConfig.dbVersion = 3;
        dataBaseConfig.debugged = BuildConfig.DEBUG;  //false;//
        mLiteOrm = LiteOrm.newCascadeInstance(dataBaseConfig);
        mLiteOrm.setDebugged(true);

        MLog.e("dbname",DB_NAME+"");
//
//            }
//        });
    }


    public static synchronized OrmLiteApi getInstance() {
        if (mInstance == null) {
            synchronized (OrmLiteApi.class) {
                if (mInstance == null) {
                    mInstance = new OrmLiteApi();
                }
            }
        }
        return mInstance;
    }

    public void save(final Object object) {


        if (object == null) {
            return;
        }
        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                mLiteOrm.save(object);
            }
        });
    }

    public <T> void saveAll(final List<T> list) {

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (list == null || list.isEmpty()) {
                    return;
                }
                mLiteOrm.save(list);
            }
        });
        //       mLiteOrm.save(list);

    }

    public <T> void saveAll2(final List<T> list) {

        if (list == null || list.isEmpty()) {
            return;
        }


        mLiteOrm.save(list);


    }

    public void delete(final Object object) {
        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (object == null) {
                    return;
                }
                mLiteOrm.delete(object);
            }
        });
    }


    public LiteOrm getLiteOrm() {

        return mLiteOrm;

    }

    public <T> void queryAll(final Class<T> tClass, final OnQueryResultListner<T> listner) {

        if (tClass == null) {
            return;
        }


        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                List<T> res = mLiteOrm.query(tClass);
                if (listner != null)
                    listner.onResult(res);
            }
        });


    }


    public <T> List<T> queryAll(final Class<T> tClass) {

        if (tClass == null) {
            return null;
        }


        List<T> res = mLiteOrm.query(tClass);
        return res;


    }

    /**
     * 使用QueryBuilder进行查找，可以进行扩展
     * 根据具体的字段进行扩展
     *
     * @param tClass
     * @param id
     * @return
     */
    public <T> void queryById(final Class<T> tClass, final long id, final OnQueryResultListner<T> listner) {
        if (tClass == null) {
            return;
        }
        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                QueryBuilder<T> qb = new QueryBuilder<T>(tClass);
                qb.whereEquals("_id", id);
                List<T> res = mLiteOrm.query(qb);
                if (listner != null)
                    listner.onResult(res);

            }
        });
    }


    public <T> void queryByKey(final Class<T> tClass, final String keyName, final Object key, final OnQueryResultListner<T> listner) {
        if (tClass == null) {
            return;
        }
        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                QueryBuilder<T> qb = new QueryBuilder<T>(tClass);
                qb.whereEquals(keyName, key);
                List<T> res = mLiteOrm.query(qb);
                if (listner != null)
                    listner.onResult(res);

            }
        });
    }


    public <T> List<T> queryByKey(final Class<T> tClass, final String keyName, final Object key) {
        if (tClass == null) {
            return null;
        }

        QueryBuilder<T> qb = new QueryBuilder<T>(tClass);
        qb.whereEquals(keyName, key);
        List<T> res = mLiteOrm.query(qb);
        return res;

    }


    public void CloseOrm() {

        mLiteOrm.close();
        mLiteOrm = null;
        DB_NAME = null;
    }

    @SuppressWarnings("unchecked")
    public void deleteAll(final Class class1) {

//        ThreadPoolTool.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {



        mLiteOrm.deleteAll(class1);
//            }
//        });
    }

    public void deleteAll2(final Class class1) {

        ThreadPoolTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {



        mLiteOrm.deleteAll(class1);
            }
        });
    }


//自己拼装SQL语句
//	QueryBuilder<Address> qb = new QueryBuilder<Address>(Address.class)        
//			.columns(new String[]{Address.COL_ADDRESS})    //查询列
//			.appendOrderAscBy(Address.COL_ADDRESS)        //升序
//			.appendOrderDescBy(Address.COL_ID)       //当第一列相同时使用该列降序排序
//			.distinct(true)        //去重
//			.where(Address.COL_ADDRESS + "=?", new String[]{"香港路"}); //where条件
//
//			liteOrm.query(qb);

}



