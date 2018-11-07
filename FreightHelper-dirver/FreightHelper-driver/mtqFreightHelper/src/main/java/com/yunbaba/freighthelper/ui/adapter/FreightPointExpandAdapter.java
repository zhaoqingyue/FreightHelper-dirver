package com.yunbaba.freighthelper.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunbaba.api.trunk.FreightPointDeal;
import com.yunbaba.api.trunk.TaskOperator;
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
import com.yunbaba.freighthelper.utils.OnCallBack;
import com.yunbaba.freighthelper.utils.SPHelper2;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zhonghm on 2018/4/23.
 */

public class FreightPointExpandAdapter extends BaseAdapter implements OnUIResult {
    private Context mContext;

    private ArrayList<MtqDeliStoreDetail> mStore = new ArrayList<>();
    private ArrayList<MtqDeliStoreDetail> mFinishStore = new ArrayList<>();
    private HashMap<Integer, MtqDeliOrderDetail> mOrder;

    private int mSelectIndex;
    private boolean isNeedJump = false; // 是否要跳转界面。

    public boolean isFold() {
        return isFold;
    }

    public void setFold(boolean fold) {
        isFold = fold;
    }

    public void unFold() {
        isFold = !isFold;
    }

    public boolean isFold = true;

    private int mJumpType = 0; // 0 不跳转，1跳到详情 2 跳到收款
    private int tasktype = 1; // 1 送货  2提货
    private Runnable mRunnable = null;

    public FreightPointExpandAdapter(Context cxt) {
        mContext = cxt;

        SortData();
        mOrder = FreightPointDeal.getInstace().getmOrder();


        if (FreightPointDeal.getInstace().getmMtqDeliTaskDetail() != null) {


            tasktype = FreightPointDeal.getInstace().getmMtqDeliTaskDetail().freight_type;
            //MLog.e("freighttype","notnull"+tasktype);
        }

        FreightPointDeal.getInstace().setmCallBack(this);

        int storesize = mStore == null ? 0 : mStore.size();
        int storesize2 = mFinishStore == null ? 0 : mFinishStore.size();

        if(storesize2!=0 && storesize == 0)
            isFold = false;
        else
            isFold = true;


    }

    public void setmStore(ArrayList<MtqDeliStoreDetail> mStore) {
        this.mStore = mStore;
    }

    @Override
    public boolean isEnabled(int position) {

        return true;
    }

    @Override
    public int getCount() {

//        if (mStore != null) {
//            return mStore.size();
//        }

        int storesize = mStore == null ? 0 : mStore.size();
        int storesize2 = mFinishStore == null ? 0 : mFinishStore.size();


        if (storesize == 0 && storesize2 == 0)
            return 0;

        if(storesize !=0 && storesize2 !=0) {

            if (isFold)
                return storesize + 2;
            else
                return storesize + storesize2 + 2;

        }else if(storesize ==0 && storesize2 !=0){

            if (isFold)
                return 1;
            else
                return storesize2 + 1;
        }else if(storesize !=0 && storesize2 ==0){


                return storesize + 1;
        }else
            return 0;


    }




//
//    public boolean isAllFinish() {
//
//
//        int storesize = mStore == null ? 0 : mStore.size();
//        int storesize2 = mFinishStore == null ? 0 : mFinishStore.size();
//
//
//        if (storesize == 0 && storesize2 == 0)
//            return 0;
//
//
//
//
//    }



    @Override
    public void notifyDataSetChanged() {


        SortData();

        super.notifyDataSetChanged();
    }

    private int recommandSort = -1;

    public void SortData() {

        recommandSort = -1;

        ArrayList<MtqDeliStoreDetail> allstore = FreightPointDeal.getInstace().getmStore();

        MtqDeliStoreDetail runnningstore = null;

        mStore.clear();
        mFinishStore.clear();

        for (MtqDeliStoreDetail store : allstore) {

            if (store.storestatus == 1) {

                if(recommandSort == -1)
                    recommandSort = store.storesort;

                if(runnningstore == null)
                     runnningstore = store;
                else
                    mStore.add(store);

            } else if (store.storestatus == 2) {

                mFinishStore.add(store);


            } else {

                if(recommandSort == -1)
                    recommandSort = store.storesort;
                mStore.add(store);
            }


        }

        if (runnningstore != null)
            mStore.add(0, runnningstore);


    }

    @Override
    public MtqDeliStoreDetail getItem(int position) {


        int storesize = mStore == null ? 0 : mStore.size();
        int storesize2 = mFinishStore == null ? 0 : mFinishStore.size();

        int type = getItemViewType(position);
        if( type == 1 || type == 2)
            return  null;


        if(storesize!=0&& storesize2!=0) {

            if (position <= storesize) {

                return mStore.get(position - 1);

            } else if (position - 1 > storesize && position - 1 <= storesize + storesize2) {

                return mFinishStore.get(position - storesize - 2);

            }
        }else if(storesize != 0&& storesize2 == 0){


            if (position-1 < storesize) {

                return mStore.get(position-1);

            }
//            else if (position  >=  storesize && position - 1 <= storesize + storesize2) {
//
//                return mFinishStore.get(position - storesize - 1);
//
//            }


        }else if(storesize == 0 && storesize2 !=0){


            if (position-1 < storesize2) {

                return mFinishStore.get(position-1);

            }

        }

        return null;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    /**
     * 返回有多少个不同的布局
     */
    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
//        if(mList.get(position).getMsgContent() == null)
//            return

        int storesize = mStore == null ? 0 : mStore.size();
        int finishstoresize = mFinishStore == null?0:mFinishStore.size();


        if(storesize == 0 && finishstoresize == 0) {

            return 0;

        }else if(storesize != 0 && finishstoresize ==0){


            if (position == 0) {

                return 2;
            } else {

                return 0;
            }

        }else if(storesize == 0 && finishstoresize !=0){

            if (position  == 0) {

                return 1;

            } else {

                return 0;
            }

        }else{

            if (position - 1 == storesize) {

                return 1;

            } else if (position == 0) {

                return 2;
            } else {

                return 0;
            }


        }

        //  return mList.get(position).getMsgContent().getLayout();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        FreightPointExpandAdapter.ViewHolder viewHolder = null;
        FreightPointExpandAdapter.ViewHolderFold viewHolderFold = null;
        FreightPointExpandAdapter.ViewHolderHead viewHolderHead = null;
        int type = getItemViewType(position);


        if (convertView == null) {

            if (type == 0) {


                viewHolder = new FreightPointExpandAdapter.ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_freight_point_item, null);
                viewHolder.address = (TextView) convertView.findViewById(R.id.id_freight_point_address);
                viewHolder.name = (TextView) convertView.findViewById(R.id.id_freight_point_name);
                viewHolder.phone = (TextView) convertView.findViewById(R.id.tv_phone);
                viewHolder.contact = (TextView) convertView.findViewById(R.id.tv_contact);
                viewHolder.status = (TextView) convertView.findViewById(R.id.id_freight_point_status);
                viewHolder.time = (TextView) convertView.findViewById(R.id.id_freight_point_time);
                viewHolder.timeStatus = (TextView) convertView.findViewById(R.id.freight_time_status);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.freight_point_type_pic);
                viewHolder.operateText = (TextView) convertView.findViewById(R.id.freight_operation_text);
                viewHolder.operateText1 = (TextView) convertView.findViewById(R.id.freight_operation_text1);
                viewHolder.operateText2 = (TextView) convertView.findViewById(R.id.freight_operation_text2);
                viewHolder.operateImg = (ImageView) convertView.findViewById(R.id.freight_point_operation_pic);
                viewHolder.operateImg1 = (ImageView) convertView.findViewById(R.id.freight_point_operation_pic1);
                viewHolder.operateImg2 = (ImageView) convertView.findViewById(R.id.freight_point_operation_pic2);
                viewHolder.operate = (LinearLayout) convertView.findViewById(R.id.freight_operation);
                viewHolder.operate1 = (RelativeLayout) convertView.findViewById(R.id.freight_operation1);
                viewHolder.operate2 = (RelativeLayout) convertView.findViewById(R.id.freight_operation2);
                viewHolder.locate = (TextView) convertView.findViewById(R.id.btn_getposition2);
                viewHolder.locate2 = (LinearLayout) convertView.findViewById(R.id.btn_getposition);
                viewHolder.tvrecommend = (TextView) convertView.findViewById(R.id.tv_recommend);
                convertView.setTag(viewHolder);
            } else if (type == 1) {

                viewHolderFold = new FreightPointExpandAdapter.ViewHolderFold();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_freightpoint_expand, null);
                viewHolderFold.tvFinishCount = (TextView) convertView.findViewById(R.id.tv_finishcount);
                viewHolderFold.ivArrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
                convertView.setTag(viewHolderFold);


            } else {

                viewHolderHead = new FreightPointExpandAdapter.ViewHolderHead();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pointlist_head, null);
                viewHolderHead.tvHint = (TextView) convertView.findViewById(R.id.tv_hint);

                convertView.setTag(viewHolderHead);


            }
        } else {


            if (type == 0) {
                viewHolder = (FreightPointExpandAdapter.ViewHolder) convertView.getTag();
            } else if (type == 1) {

                viewHolderFold = (FreightPointExpandAdapter.ViewHolderFold) convertView.getTag();
            } else {

                viewHolderHead = (FreightPointExpandAdapter.ViewHolderHead) convertView.getTag();

            }
        }


        if (type == 0) {
            final MtqDeliStoreDetail storeDetail = getItem(position);

            // order 可能为空
            MtqDeliOrderDetail orderDetail = mOrder.get(storeDetail.storesort);

            viewHolder.index = storeDetail.storesort;
            viewHolder.storeDetail = storeDetail;

            // if (storeDetail.storex == 0 && storeDetail.storey == 0) {
            // holder.address.setText(FreightLogicTool
            // .getStoreNameNoPosition((storeDetail.regionname +
            // storeDetail.storeaddr).replaceAll("\\s*", "")));
            // } else
            // holder.address.setText((storeDetail.regionname +
            // storeDetail.storeaddr).replaceAll("\\s*", ""));

            if (storeDetail.isUnknownAddress) {

                if (!TaskUtils.isStorePosUnknown(storeDetail)) {
                    viewHolder.address.setText((storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", ""));
                } else {

                    if(SPHelper2.getInstance(mContext).readMarkStoreAddress(storeDetail.taskId+storeDetail.waybill)!=null){

                        viewHolder.address.setText(FreightLogicTool.getStoreNameGetPosition(
                                (storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", "")));


                    }else {

                        viewHolder.address.setText(FreightLogicTool.getStoreNameNoPosition(
                                (storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", "")));
                    }
                }
            } else
                viewHolder.address.setText((storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", ""));


            if (storeDetail != null && !TextUtils.isEmpty(storeDetail.storecode)) {
                //  String temp = storeDetail.storesort + "." +storeDetail.storecode + "-" + storeDetail.storename;
                CharSequence format = FreightLogicTool.formatPointNameStr(storeDetail.storesort + ".", storeDetail.storecode, "-" + storeDetail.storename

                );
                viewHolder.name.setText(format);

            } else {

                CharSequence format = FreightLogicTool.formatPointNameStr(storeDetail.storesort + ".", "", "" + storeDetail.storename

                );
                viewHolder.name.setText(format);
            }


            if(storeDetail.storesort == recommandSort){
                viewHolder.tvrecommend.setVisibility(View.VISIBLE);
            }else{
                viewHolder.tvrecommend.setVisibility(View.GONE);
            }




            viewHolder.contact.setText(storeDetail.linkman);
            viewHolder.phone.setText(storeDetail.linkphone);


            viewHolder.status
                    .setText(FreightPointDeal.getInstace().getStoreStatusText(storeDetail.storestatus, storeDetail.optype));

//            if (orderDetail != null) {
//                holder.time.setVisibility(View.VISIBLE);
//                holder.time.setText(TimestampTool.getStoreDetailTimeHint(orderDetail.reqtime_e * 1000L) + "前送达");
//            } else {
//                holder.time.setVisibility(View.INVISIBLE);
//            }
            viewHolder.time.setVisibility(View.GONE);
            viewHolder.timeStatus.setVisibility(View.GONE);
//
//            if (orderDetail != null && orderDetail.expired > 0) {
//                holder.timeStatus.setVisibility(View.VISIBLE);
//                holder.timeStatus.setText("已过期");
//
//            } else if (FreightPointDeal.getInstace().isOverTime(storeDetail,orderDetail)) {
//                holder.timeStatus.setVisibility(View.VISIBLE);
//                holder.timeStatus.setText("已超时");
//            }

            //      holder.operate2.setVisibility(View.INVISIBLE);

            setImgbyOptype(storeDetail.optype, viewHolder.image);

//            if (orderDetail != null && orderDetail.expired > 0) {
//                setViewbyStoreStatus(2, holder);
//            } else
//                setViewbyStoreStatus(storeDetail.storestatus, holder);

//storeDetail.storestatus != 2 &&

            viewHolder.locate.setVisibility(View.GONE);
            viewHolder.locate2.setVisibility(View.GONE);


//		if(storeDetail.storestatus !=2 &&((storeDetail.optype != 1 && tasktype ==2  &&storeDetail.optype!=4)||(tasktype == 1 && storeDetail.optype!=4 && storeDetail.optype!= 3))){
//
//			holder.locate.setVisibility(View.VISIBLE);
//			holder.locate2.setVisibility(View.VISIBLE);
//			if(SPHelper2.getInstance(mContext).readMarkStoreAddress(storeDetail.taskId+storeDetail.waybill)==null){
//
//				holder.locate.setText("获取位置点");
//				holder.locate.setBackground(mContext.getResources().getDrawable(R.drawable.round_background_orange));
//				holder.locate2.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//
//						if (!CommonTool.isFastDoubleClick()) {
//							EventBus.getDefault().post(new GetLocationAddrEvent(storeDetail));
//						}
//					}
//				});
//			}else {
//				holder.locate.setText("位置已获取");
//				holder.locate.setBackground(mContext.getResources().getDrawable(R.drawable.round_background_gray));
//				holder.locate2.setOnClickListener(null);
//			}
//
//		}else {
//			holder.locate.setVisibility(View.GONE);
//			holder.locate2.setVisibility(View.GONE);
//		}

        } else if (type == 1) {

            viewHolderFold.tvFinishCount.setText(" 已完成路由点 ( " + (mFinishStore == null ? 0 : mFinishStore.size()) + " )");
            if (isFold) {
                viewHolderFold.ivArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_arrow_down));

            } else {
                viewHolderFold.ivArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_arrow_up));
            }

        } else {


            viewHolderHead.tvHint.setText(FreightLogicTool.getTaskPointHint((mStore == null ? 0 : mStore.size())));


        }


        return convertView;
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
                // 送货
                image.setImageResource(R.drawable.ic_task_deliver);
                break;
            case 3:
                // 提货
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

    private void setViewbyStoreStatus(int storestatus, final FreightPointExpandAdapter.ViewHolder viewHolder) {

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

                viewHolder.operate.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
                    viewHolder.operate1.setVisibility(View.GONE);
                else
                    viewHolder.operate1.setVisibility(View.VISIBLE);

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
                            jumpDetailActivity(viewHolder.index, viewHolder.storeDetail);
                            jumpRoutePrewiewActivity(viewHolder.index, viewHolder.storeDetail);
                            FreightPointDeal.getInstace().startOrContinueDeliver(viewHolder.storeDetail);
                        } else {
                            jumpDetailActivity(viewHolder.index, viewHolder.storeDetail);
                            jumpRoutePrewiewActivity(viewHolder.index, viewHolder.storeDetail);
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

                viewHolder.operate2.setVisibility(View.GONE);

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
                viewHolder.operate.setVisibility(View.VISIBLE);

                if (TaskUtils.isStorePosUnknown(viewHolder.storeDetail))
                    viewHolder.operate1.setVisibility(View.GONE);
                else
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
                                    // 在上传收款那边完成任务


                                    if ((viewHolder.storeDetail.optype != 1 && tasktype == 2 && viewHolder.storeDetail.optype != 4) ||
                                            (tasktype == 1 && viewHolder.storeDetail.optype != 4 && viewHolder.storeDetail.optype != 3)) {


                                        if (SPHelper2.getInstance(mContext).readMarkStoreAddress(viewHolder.storeDetail.taskId + viewHolder.storeDetail.waybill) == null) {

                                            EventBus.getDefault().post(new GetLocationAddrEvent(viewHolder.storeDetail, mOrder.get(mSelectIndex)));
                                        } else {
                                            Intent intent = new Intent(mContext, UploadPaymentActivity.class);

                                            // 添加storedetail
                                            String str = GsonTool.getInstance().toJson(viewHolder.storeDetail);
                                            intent.putExtra("storedetail", str);

                                            // 添加taskid
                                            intent.putExtra("taskid", viewHolder.storeDetail.taskId);
                                            intent.putExtra("corpid", viewHolder.storeDetail.corpId);

                                            // 添加orderdetail
                                            String str2 = GsonTool.getInstance().toJson(mOrder.get(mSelectIndex));
                                            intent.putExtra("orderdetail", str2);
                                            ((Activity) mContext).startActivityForResult(intent,
                                                    FreightConstant.TASK_POINT_REQUSEST_CODE);
                                        }

                                    } else {


                                        Intent intent = new Intent(mContext, UploadPaymentActivity.class);

                                        // 添加storedetail
                                        String str = GsonTool.getInstance().toJson(viewHolder.storeDetail);
                                        intent.putExtra("storedetail", str);

                                        // 添加taskid
                                        intent.putExtra("taskid", viewHolder.storeDetail.taskId);
                                        intent.putExtra("corpid", viewHolder.storeDetail.corpId);

                                        // 添加orderdetail
                                        String str2 = GsonTool.getInstance().toJson(mOrder.get(mSelectIndex));
                                        intent.putExtra("orderdetail", str2);
                                        ((Activity) mContext).startActivityForResult(intent,
                                                FreightConstant.TASK_POINT_REQUSEST_CODE);
                                    }
                                    return;
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


                        if (TaskUtils.isStorePosUnknown(viewHolder.storeDetail)) {

                            TaskOperator.getInstance().ShowNaviDisableDialog(mContext, new OnCallBack() {

                                @Override
                                public void onYES() {


                                }

                            });

                            return;

                        }

                        // 导航
                        jumpDetailActivity(viewHolder.index, viewHolder.storeDetail);
                        jumpRoutePrewiewActivity(viewHolder.index, viewHolder.storeDetail);

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

                if (TaskUtils.isStorePosUnknown(viewHolder.storeDetail)) {

                    viewHolder.operateText.setText("电话");
                    viewHolder.operateImg.setImageResource(R.drawable.freight_point_phone);
                    if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
                        viewHolder.operate.setVisibility(View.GONE);
                    else
                        viewHolder.operate.setVisibility(View.VISIBLE);

                    viewHolder.operate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 电话
                            FreightPointDeal.getInstace().call(viewHolder.storeDetail.linkphone);
                        }
                    });

                    viewHolder.operate1.setVisibility(View.GONE);

                    viewHolder.operate2.setVisibility(View.GONE);

                } else {

                    viewHolder.operateText.setText("查看地图");
                    viewHolder.operateImg.setImageResource(R.drawable.freight_point_lookmap);
                    viewHolder.operate.setVisibility(View.VISIBLE);

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

                }
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
                viewHolder.operate.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
                    viewHolder.operate1.setVisibility(View.GONE);
                else
                    viewHolder.operate1.setVisibility(View.VISIBLE);

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
                            jumpDetailActivity(viewHolder.index, viewHolder.storeDetail);
                            jumpRoutePrewiewActivity(viewHolder.index, viewHolder.storeDetail);
                            FreightPointDeal.getInstace().startOrContinueDeliver(viewHolder.storeDetail);
                        } else {
                            jumpDetailActivity(viewHolder.index, viewHolder.storeDetail);
                            jumpRoutePrewiewActivity(viewHolder.index, viewHolder.storeDetail);
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

                viewHolder.operate2.setVisibility(View.GONE);

                break;
        }

    }

    /**
     * @param position
     * @Title: jumpDetailActivity
     * @Description: 调整到详情界面
     * @return: void
     */
    private void jumpDetailActivity(final int position, final MtqDeliStoreDetail storeDetail) {
        Runnable r = new Runnable() {
            @Override
            public void run() {

                MtqDeliStoreDetail storedetail = storeDetail;

                Intent intent = new Intent(mContext, TaskPointDetailActivity.class);

                // 添加storedetail
                String str = GsonTool.getInstance().toJson(storedetail);
                intent.putExtra("storedetail", str);

                // 添加taskid
                intent.putExtra("taskid", storedetail.taskId);
                intent.putExtra("corpid", storedetail.corpId);
                if (mOrder.get(storeDetail.storesort) != null) {
                    MLog.e("跳转detail", mOrder.get(storeDetail.storesort).receive_name + " 111");
                    String str2 = GsonTool.getInstance().toJson(mOrder.get(storeDetail.storesort));
                    intent.putExtra("orderdetail", str2);
                }

                ((Activity) mContext).startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);
            }
        };

        FreightPointDeal.getInstace().setmJumpRunnable(r);
        FreightPointDeal.getInstace().setmRouteFailable(r);
    }

    private void jumpRoutePrewiewActivity(final int position, final MtqDeliStoreDetail storeDetail) {
        FreightPointDeal.getInstace().setmRouteSucessable(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(mContext, RoutePreviewActivity.class);

                MtqDeliStoreDetail storedetail = storeDetail;

                // 添加storedetail
                String str = GsonTool.getInstance().toJson(storedetail);
                intent.putExtra("storedetail", str);

                // 添加taskid
                intent.putExtra("taskid", storedetail.taskId);
                intent.putExtra("corpid", storedetail.corpId);
                if (mOrder.get(storeDetail.storesort) != null) {
                    String str2 = GsonTool.getInstance().toJson(mOrder.get(storeDetail.storesort));
                    intent.putExtra("orderdetail", str2);
                }

                str = GsonTool.getInstance().toJson(FreightPointDeal.getInstace().getmMtqDeliTaskDetail());
                intent.putExtra("taskdetail", str);

                ((Activity) mContext).startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);
                FreightPointDeal.getInstace().setmRouteSucessable(null);
            }
        });
    }

    private void showProgressBar() {
        // ((FreightPointActivity)mContext).showProgressBar();
        WaitingProgressTool.showProgress(mContext);
    }

    private void hideProgressBar() {
        // ((FreightPointActivity)mContext).hideProgressBar();
        WaitingProgressTool.closeshowProgress();
    }

    final static class ViewHolder {
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
        TextView tvrecommend;
        MtqDeliStoreDetail storeDetail;
        int index;
    }


    final static class ViewHolderFold {
        ImageView ivArrow;
        TextView tvFinishCount;
    }

    final static class ViewHolderHead {

        TextView tvHint;
    }

    @Override
    public void OnResult() {

        notifyDataSetChanged();
        hideProgressBar();

        // if (mRunnable != null) {
        // mRunnable.run();
        // mRunnable = null;
        // }
    }

    @Override
    public void OnError(int ErrCode) {

        hideProgressBar();
    }

}