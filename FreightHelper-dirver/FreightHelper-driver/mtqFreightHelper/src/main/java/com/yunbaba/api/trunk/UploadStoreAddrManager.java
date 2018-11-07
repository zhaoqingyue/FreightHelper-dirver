package com.yunbaba.api.trunk;

import android.content.Context;
import android.os.Handler;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.trunk.listner.OnBooleanListner;
import com.yunbaba.freighthelper.bean.OfflineLocationBean;

import java.util.ArrayList;

/**
 * Created by zhonghm on 2018/4/17.
 */

public class UploadStoreAddrManager {

    private static UploadStoreAddrManager instance = new UploadStoreAddrManager();

    private boolean isRunning = false;

    public static UploadStoreAddrManager getInstance() {

        return instance;

    }

    public synchronized void CheckAndUpload(final Context context) {


        if (isRunning)
            return;


//        OrmLiteApi.getInstance().queryAll(OfflineLocationBean.class, new OnQueryResultListner<OfflineLocationBean>() {
//            @Override
//            public void onResult(List<OfflineLocationBean> res) {
//                sd
//            }
//        });

//        ThreadPoolTool.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {

        if (!(AccountAPI.getInstance().isLogined())) {
            new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    CheckAndUpload(context);
                }
            },1500);


            return;
        }

        isRunning = true;

        QueryBuilder<OfflineLocationBean> qb = new QueryBuilder<OfflineLocationBean>(OfflineLocationBean.class)
                .limit(0, 1);

        ArrayList<OfflineLocationBean> list = OrmLiteApi.getInstance().getLiteOrm().query(qb);


        if (list != null && list.size() > 0) {

            OfflineLocationBean bean = list.get(0);

            OfflineLocationTool.CheckAddrAndUpload(context,bean,  new OnBooleanListner() {
                @Override
                public void onResult(boolean res) {
                    isRunning = false;

                    new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CheckAndUpload(context);
                        }
                    },1500);



                }
            },false);


        }else{
            isRunning = false;

        }


    }
    //  });





}
