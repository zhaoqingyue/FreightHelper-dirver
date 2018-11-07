package com.yunbaba.freighthelper.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunbaba.api.trunk.FreightPointDeal;
import com.yunbaba.api.trunk.bean.OnUIResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.GetLocationAddrEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.ui.activity.RoutePreviewActivity;
import com.yunbaba.freighthelper.ui.activity.task.FreightPointActivity;
import com.yunbaba.freighthelper.ui.activity.task.TaskPointDetailActivity;
import com.yunbaba.freighthelper.ui.activity.task.UploadPaymentActivity;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SPHelper2;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import am.util.viewpager.adapter.RecyclePagerAdapter;

/**
 * Created by zhonghm on 2018/3/21.
 */

public class FreightPointMapBottomAdapter extends RecyclePagerAdapter<FreightPointMapBottomAdapter.MyViewHolder> implements OnUIResult {

    private Context mContext;

    public ArrayList<MtqDeliStoreDetail> getmStore() {
        return mStore;
    }

    public void setmStore(ArrayList<MtqDeliStoreDetail> mStore) {
        this.mStore = mStore;
    }

    //  private LinkedList<View> mViewCache = null;
    private ArrayList<MtqDeliStoreDetail> mStore;
    private HashMap<Integer, MtqDeliOrderDetail> mOrder;
    private int mSelectIndex;
    private int mChildCount = 0;
    private boolean isNeedJump = false; // 是否要跳转界面。
    private Runnable mRunnable = null;
    private int tasktype = 1; // 1 送货  2提货
    private float mScaleX = 0.0f;
    private float mScaleY = 0.0f;

    public FreightPointMapBottomAdapter(Context ctx) {
        mContext = ctx;

        //   mViewCache = new LinkedList<>();
        mStore = Filter((FreightPointDeal.getInstace().getmStore()));
        mOrder = FreightPointDeal.getInstace().getmOrder();
        FreightPointDeal.getInstace().setmMapUICallBack(this);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        mScaleX = (float) (width / 1080.0);
        mScaleY = (float) (height / 1920.0);


        if(FreightPointDeal.getInstace().getmMtqDeliTaskDetail()!=null){


            tasktype = FreightPointDeal.getInstace().getmMtqDeliTaskDetail().freight_type;
            //MLog.e("freighttype","notnull"+tasktype);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_freight_point_bottom, parent,
                false));
        return holder;
    }

    public void RefreshData(){

        mStore = Filter((FreightPointDeal.getInstace().getmStore()));
        notifyDataSetChanged();

    }



    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {


        MLog.e("refreshmappoint","onbindview"+position);
       final MtqDeliStoreDetail storeDetail = mStore.get(position);

        // order 可能为空
        MtqDeliOrderDetail orderDetail = mOrder.get(storeDetail.storesort);

        viewHolder.index = storeDetail.storesort;
        viewHolder.storeDetail = storeDetail;
        // holder.address.setText((storeDetail.regionname +
        // storeDetail.storeaddr).replaceAll("\\s*", ""));

        if (storeDetail.isUnknownAddress) {

            if (!TaskUtils.isStorePosUnknown(storeDetail)) {
                viewHolder.address.setText((storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", ""));
            } else
            if(SPHelper2.getInstance(mContext).readMarkStoreAddress(storeDetail.taskId+storeDetail.waybill)!=null){

                viewHolder.address.setText(FreightLogicTool.getStoreNameGetPosition(
                        (storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", "")));


            }else {

                viewHolder.address.setText(FreightLogicTool.getStoreNameNoPosition(
                        (storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", "")));
            }

        } else
            viewHolder.address.setText((storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", ""));

//        holder.name.setText(
//                (position + 1) + "." + (TextUtils.isEmpty(storeDetail.storecode) ? "" : (storeDetail.storecode + "-"))
//                        + storeDetail.storename);






        viewHolder.name.setText(
                FreightLogicTool.getMapPointPosition2(storeDetail.storesort+"","/"+FreightPointDeal.getInstace().getmStore().size()+".",
                        TextUtils.isEmpty(storeDetail.storecode) ? "" : storeDetail.storecode ,"-"
                                + storeDetail.storename));

        viewHolder.status
                .setText(FreightPointDeal.getInstace().getStoreStatusText(storeDetail.storestatus, storeDetail.optype));

//        if (orderDetail != null) {
//            holder.time.setVisibility(View.VISIBLE);
//            holder.time.setText(TimestampTool.getStoreDetailTimeHint(orderDetail.reqtime_e * 1000L) + "前送达");
//        } else {
//            holder.time.setVisibility(View.INVISIBLE);
//        }
        viewHolder.time.setVisibility(View.GONE);
        viewHolder.timeStatus.setVisibility(View.GONE);

//        if (FreightPointDeal.getInstace().isOverTime(position)) {
//            holder.timeStatus.setVisibility(View.VISIBLE);
//            holder.timeStatus.setText("已超时");
//        }

      //  holder.operate2 .setVisibility(View.INVISIBLE);

        setImgbyOptype(storeDetail.optype, viewHolder.image);

     //   setViewbyStoreStatus(storeDetail.storestatus, holder);

        viewHolder.contact.setText(storeDetail.linkman);
        viewHolder.phone.setText(storeDetail.linkphone);


        viewHolder.locate.setVisibility(View.GONE);
        viewHolder.locate2.setVisibility(View.GONE);

        if(storeDetail.storesort  == recommandSort){
            viewHolder.tvrecommend.setVisibility(View.VISIBLE);
        }else{
            viewHolder.tvrecommend.setVisibility(View.GONE);
        }



//        if(storeDetail.storestatus !=2 &&((storeDetail.optype != 1 && tasktype ==2  &&storeDetail.optype!=4)||(tasktype == 1 && storeDetail.optype!=4 && storeDetail.optype!= 3))){
//
//            holder.locate.setVisibility(View.VISIBLE);
//
//            holder.locate2.setVisibility(View.VISIBLE);
//            if(SPHelper2.getInstance(mContext).readMarkStoreAddress(storeDetail.taskId+storeDetail.waybill)==null){
//
//                holder.locate.setText("获取位置点");
//                holder.locate.setBackground(mContext.getResources().getDrawable(R.drawable.round_background_orange));
//                holder.locate2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!CommonTool.isFastDoubleClick()) {
//                            EventBus.getDefault().post(new GetLocationAddrEvent(storeDetail));
//                        }
//                    }
//                });
//            }else {
//                holder.locate.setText("位置已获取");
//                holder.locate.setBackground(mContext.getResources().getDrawable(R.drawable.round_background_gray));
//                holder.locate2.setOnClickListener(null);
//            }
//
//        }else {
//            holder.locate.setVisibility(View.GONE);
//            holder.locate2.setVisibility(View.GONE);
//        }

    }

    @Override
    public int getItemCount() {
        if (mStore != null) {
            return mStore.size();
        }
        return 0;
    }



    public int getItemPosition(String waybill) {
        if (mStore != null) {

            int position = 0;

            over:for(int i = 0;i<mStore.size();i++){

                if(mStore.get(i).waybill.equals(waybill)){

                    position = i;
                    break over;
                }


            }


            return  position;


        }
        return 0;
    }


    public final static class MyViewHolder extends RecyclePagerAdapter.PagerViewHolder {

        ImageView image;
        TextView name;
        TextView address;
        TextView status;
        TextView time;
        TextView phone;
        TextView contact;
        TextView timeStatus; // 已完成 超时
         TextView locate;
        LinearLayout locate2;
        LinearLayout operate;
        RelativeLayout operate1;
        RelativeLayout operate2;

        TextView operateText;
        TextView operateText1;
        TextView operateText2;

        ImageView operateImg;
        ImageView operateImg1;
        ImageView operateImg2;
        TextView  tvrecommend;
        LinearLayout line;

        MtqDeliStoreDetail storeDetail;
        int index;

        public MyViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.id_freight_point_address);
            name = (TextView) view.findViewById(R.id.id_freight_point_name);
            status = (TextView) view.findViewById(R.id.id_freight_point_status);
            time = (TextView) view.findViewById(R.id.id_freight_point_time);
            timeStatus = (TextView) view.findViewById(R.id.freight_time_status);
            image = (ImageView) view.findViewById(R.id.freight_point_type_pic);
            operateText = (TextView) view.findViewById(R.id.freight_operation_text);
            operateText1 = (TextView) view.findViewById(R.id.freight_operation_text1);
            operateText2 = (TextView) view.findViewById(R.id.freight_operation_text2);
            operateImg = (ImageView) view.findViewById(R.id.freight_point_operation_pic);
            operateImg1 = (ImageView) view.findViewById(R.id.freight_point_operation_pic1);
            operateImg2 = (ImageView) view.findViewById(R.id.freight_point_operation_pic2);
            operate = (LinearLayout) view.findViewById(R.id.freight_operation);
            operate1 = (RelativeLayout) view.findViewById(R.id.freight_operation1);
            operate2 = (RelativeLayout) view.findViewById(R.id.freight_operation2);
            line = (LinearLayout) view.findViewById(R.id.freigt_point_line);
            locate = (TextView) view.findViewById(R.id.btn_getposition2);
            locate2  = (LinearLayout) view.findViewById(R.id.btn_getposition);
            phone = (TextView) view.findViewById(R.id.tv_phone);
            contact = (TextView) view.findViewById(R.id.tv_contact);
            tvrecommend = (TextView) view.findViewById(R.id.tv_recommend);
        }

    }

    /**
     * @param position
     * @Title: jumpDetailActivity
     * @Description: 调整到详情界面
     * @return: void
     */
    private void jumpDetailActivity(final int position) {
        Runnable r = new Runnable() {
            @Override
            public void run() {

                MtqDeliStoreDetail storedetail = mStore.get(position);

                Intent intent = new Intent(mContext, TaskPointDetailActivity.class);

                // 添加storedetail
                String str = GsonTool.getInstance().toJson(storedetail);
                intent.putExtra("storedetail", str);

                // 添加taskid
                intent.putExtra("taskid", storedetail.taskId);
                intent.putExtra("corpid", storedetail.corpId);
                if (mOrder.get(storedetail.storesort) != null) {
                    String str2 = GsonTool.getInstance().toJson(mOrder.get(storedetail.storesort));
                    intent.putExtra("orderdetail", str2);
                }

                ((Activity) mContext).startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);
            }
        };

        FreightPointDeal.getInstace().setmJumpRunnable(r);
        FreightPointDeal.getInstace().setmRouteFailable(r);
    }

    private void jumpRoutePrewiewActivity(final int position) {
        FreightPointDeal.getInstace().setmRouteSucessable(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(mContext, RoutePreviewActivity.class);

                MtqDeliStoreDetail storedetail = mStore.get(position);

                // 添加storedetail
                String str = GsonTool.getInstance().toJson(storedetail);
                intent.putExtra("storedetail", str);

                // 添加taskid
                intent.putExtra("taskid", storedetail.taskId);
                intent.putExtra("corpid", storedetail.corpId);
                if (mOrder.get(storedetail.storesort) != null) {
                    String str2 = GsonTool.getInstance().toJson(mOrder.get(storedetail.storesort));
                    intent.putExtra("orderdetail", str2);
                }

                str = GsonTool.getInstance().toJson(FreightPointDeal.getInstace().getmMtqDeliTaskDetail());
                intent.putExtra("taskdetail", str);

                ((Activity) mContext).startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);
                FreightPointDeal.getInstace().setmRouteSucessable(null);
            }
        });
    }

    @Override
    public void OnResult() {

        notifyDataSetChanged();
        hideProgressBar();
        // if (mRunnable != null){
        // mRunnable.run();
        // mRunnable = null;
        // }
    }

    @Override
    public void OnError(int ErrCode) {

        hideProgressBar();
    }

    private void showProgressBar() {
        WaitingProgressTool.showProgress(mContext);
    }

    private void hideProgressBar() {
        WaitingProgressTool.closeshowProgress();
    }

    private int recommandSort = -1;


    public ArrayList<MtqDeliStoreDetail> Filter(ArrayList<MtqDeliStoreDetail> list) {


        ArrayList<MtqDeliStoreDetail> tmplist = new ArrayList<>();

        recommandSort = -1;


        //Iterator<MtqDeliStoreDetail> iter = list.iterator();
        MtqDeliStoreDetail tmp;
        for(int i = 0; i< list.size() ;i++) {
            // 运货中
            tmp = list.get(i);


            if(tmp.storestatus!=2){

                if(recommandSort == -1)
                  recommandSort = tmp.storesort;
                else{
                    if(tmp.storesort < recommandSort)

                    recommandSort = tmp.storesort;

                }

            }

            if (!TaskUtils.isStorePosUnknown(tmp)) {

               // tmp.storesort = i+1;
                tmplist.add(tmp);
                // break searchforrunningtask;
            }

        }

        return tmplist;

    }

    /**
     * @param optype
     * @param image
     * @Title: setImgbyOptype
     * @Description: 根据类型设置图标
     * @return: void
     */
    private void setImgbyOptype(int optype, ImageView image) {
        switch (optype) {
            case 1:
                // 提货
                image.setImageResource(R.drawable.ic_task_deliver);
                break;
            case 3:
                // 送货
                image.setImageResource(R.drawable.ic_task_pickgoods);
                break;
            case 4:
                // 回
                image.setImageResource(R.drawable.ic_task_back);
                break;
            case 5:
                // 必经点
                image.setImageResource(R.drawable.ic_task_passpoint);
                break;
        }
    }

    private void setViewbyStoreStatus(int storestatus, final MyViewHolder viewHolder) {

        switch (storestatus) {
            case 0:
                // 等待运货
                if (viewHolder.storeDetail.optype == 4) {
                    viewHolder.operateText.setText("开始回程");
                    viewHolder.operateImg.setImageResource(R.drawable.btn_task_back);
                } else {
                    viewHolder.operateText.setText("前往");
                    viewHolder.operateImg.setImageResource(R.drawable.freight_point_startdeliver);
                }

                if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
                    viewHolder.operate1.setVisibility(View.GONE);
                else
                    viewHolder.operate1.setVisibility(View.VISIBLE);

                viewHolder.operate2.setVisibility(View.GONE);

                viewHolder.operateText1.setText("电话");
                viewHolder.operateImg1.setImageResource(R.drawable.freight_point_phone);

                viewHolder.operate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        FreightPointDeal.getInstace().cleanCallback();
                        mSelectIndex = viewHolder.index;

                        boolean isTaskNavi = GeneralSPHelper.getInstance(mContext.getApplicationContext())
                                .isTaskNavi(viewHolder.storeDetail.taskId);

                        if (!isTaskNavi) {
                            jumpDetailActivity(viewHolder.index);
                            jumpRoutePrewiewActivity(viewHolder.index);
                            FreightPointDeal.getInstace().startOrContinueDeliver(viewHolder.storeDetail);
                        } else {
                            jumpDetailActivity(viewHolder.index);
                            jumpRoutePrewiewActivity(viewHolder.index);
                            FreightPointDeal.getInstace().startOrContinueDeliverAndNavigate(viewHolder.storeDetail);
                        }
                    }
                });

                viewHolder.operate1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 电话
                        FreightPointDeal.getInstace().call(viewHolder.storeDetail.linkphone);
                    }
                });

                break;
            case 1:
                // 正在配送中
                if (viewHolder.storeDetail.optype == 4) {
                    viewHolder.operateText.setText("结束回程");
                    viewHolder.operateImg.setImageResource(R.drawable.btn_task_back);
                } else {
                    viewHolder.operateText.setText("完成");
                    viewHolder.operateImg.setImageResource(R.drawable.freight_point_finish);
                }

                viewHolder.operate1.setVisibility(View.VISIBLE);

                viewHolder.operateText1.setText("导航");
                viewHolder.operateImg1.setImageResource(R.drawable.freight_point_navigtion);

                if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
                    viewHolder.operate2.setVisibility(View.GONE);
                else
                    viewHolder.operate2.setVisibility(View.VISIBLE);

                // holder.operate2.setVisibility(View.VISIBLE);
                viewHolder.operateText2.setText("电话");
                viewHolder.operateImg2.setImageResource(R.drawable.freight_point_phone);

                viewHolder.operate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        // 完成运货
                        mSelectIndex = viewHolder.index;

                        mRunnable = new Runnable() {

                            @Override
                            public void run() {

                                if (mOrder.get(viewHolder.index) != null) {
                                    if((mStore.get(mSelectIndex).optype != 1 && tasktype ==2  &&mStore.get(mSelectIndex).optype!=4)||
                                            (tasktype == 1 && mStore.get(mSelectIndex).optype!=4 && mStore.get(mSelectIndex).optype!= 3)){


                                        if(SPHelper2.getInstance(mContext).readMarkStoreAddress(mStore.get(mSelectIndex).taskId+mStore.get(mSelectIndex).waybill)==null) {

                                            EventBus.getDefault().post(new GetLocationAddrEvent(mStore.get(mSelectIndex), mOrder.get(mSelectIndex)));
                                        }else {
                                            Intent intent = new Intent(mContext, UploadPaymentActivity.class);

                                            // 添加storedetail
                                            String str = GsonTool.getInstance().toJson(mStore.get(mSelectIndex));
                                            intent.putExtra("storedetail", str);

                                            // 添加taskid
                                            intent.putExtra("taskid", mStore.get(mSelectIndex).taskId);
                                            intent.putExtra("corpid", mStore.get(mSelectIndex).corpId);

                                            // 添加orderdetail
                                            String str2 = GsonTool.getInstance().toJson(mOrder.get(mSelectIndex));
                                            intent.putExtra("orderdetail", str2);
                                            ((Activity) mContext).startActivityForResult(intent,
                                                    FreightConstant.TASK_POINT_REQUSEST_CODE);
                                        }

                                    }else {


                                        Intent intent = new Intent(mContext, UploadPaymentActivity.class);

                                        // 添加storedetail
                                        String str = GsonTool.getInstance().toJson(mStore.get(mSelectIndex));
                                        intent.putExtra("storedetail", str);

                                        // 添加taskid
                                        intent.putExtra("taskid", mStore.get(mSelectIndex).taskId);
                                        intent.putExtra("corpid", mStore.get(mSelectIndex).corpId);

                                        // 添加orderdetail
                                        String str2 = GsonTool.getInstance().toJson(mOrder.get(mSelectIndex));
                                        intent.putExtra("orderdetail", str2);
                                        ((Activity) mContext).startActivityForResult(intent,
                                                FreightConstant.TASK_POINT_REQUSEST_CODE);
                                    }
                                }
                            }
                        };

                        if (mOrder.get(viewHolder.index) != null) {
                            mRunnable.run();
                            mRunnable = null;
                            hideProgressBar();
                        } else {
                            showProgressBar();
                            FreightPointDeal.getInstace().finishDeliver(viewHolder.storeDetail);
                        }
                    }
                });

                viewHolder.operate1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 导航
                        jumpDetailActivity(viewHolder.index);
                        jumpRoutePrewiewActivity(viewHolder.index);
                        FreightPointDeal.getInstace().routeplan(viewHolder.storeDetail);
                    }
                });

                viewHolder.operate2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 电话
                        FreightPointDeal.getInstace().call(viewHolder.storeDetail.linkphone);
                    }
                });

                break;

            case 2:
                // 已完成配送
                viewHolder.operateText.setText("查看地图");
                viewHolder.operateImg.setImageResource(R.drawable.freight_point_lookmap);

                if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
                    viewHolder.operate1.setVisibility(View.GONE);
                else
                    viewHolder.operate1.setVisibility(View.VISIBLE);

                viewHolder.operate2.setVisibility(View.GONE);

                viewHolder.operateText1.setText("电话");
                viewHolder.operateImg1.setImageResource(R.drawable.freight_point_phone);

                viewHolder.operate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 查看地图
                        ((FreightPointActivity) mContext).showPoi(viewHolder.index);

                    }
                });

                viewHolder.operate1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 电话
                        FreightPointDeal.getInstace().call(viewHolder.storeDetail.linkphone);
                    }
                });
                break;

            case 3:
                // 暂停送货
                if (viewHolder.storeDetail.optype == 4) {
                    viewHolder.operateText.setText("继续回程");
                    viewHolder.operateImg.setImageResource(R.drawable.btn_task_back);
                } else {
                    viewHolder.operateText.setText("继续");
                    viewHolder.operateImg.setImageResource(R.drawable.freight_point_continue);
                }

                if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
                    viewHolder.operate1.setVisibility(View.GONE);
                else
                    viewHolder.operate1.setVisibility(View.VISIBLE);

                viewHolder.operate2.setVisibility(View.GONE);

                viewHolder.operateText1.setText("电话");
                viewHolder.operateImg1.setImageResource(R.drawable.freight_point_phone);

                viewHolder.operate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        FreightPointDeal.getInstace().cleanCallback();
                        mSelectIndex = viewHolder.index;

                        boolean isTaskNavi = GeneralSPHelper.getInstance(mContext.getApplicationContext())
                                .isTaskNavi(viewHolder.storeDetail.taskId);

                        if (!isTaskNavi) {
                            jumpDetailActivity(viewHolder.index);
                            jumpRoutePrewiewActivity(viewHolder.index);
                            FreightPointDeal.getInstace().startOrContinueDeliver(viewHolder.storeDetail);
                        } else {
                            jumpDetailActivity(viewHolder.index);
                            jumpRoutePrewiewActivity(viewHolder.index);
                            FreightPointDeal.getInstace().startOrContinueDeliverAndNavigate(viewHolder.storeDetail);
                        }

                    }
                });

                viewHolder.operate1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 电话
                        FreightPointDeal.getInstace().call(viewHolder.storeDetail.linkphone);
                    }
                });
                break;
        }

    }
}
