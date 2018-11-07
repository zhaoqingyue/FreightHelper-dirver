package com.yunbaba.freighthelper.ui.adapter;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.CallUtil;
import com.yunbaba.freighthelper.utils.TimestampTool;
import com.yunbaba.freighthelper.utils.ToastUtils;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam;
import com.yunbaba.ols.module.delivery.bean.MtqStoreMarkRecordInfo;

import java.util.ArrayList;
import java.util.List;

public class MyStoreListAdapter extends BaseAdapter {

    private static final int TAG_ITEM_APPROVE_PENDDING = 0;
    private static final int TAG_ITEM_APPROVE_PROCESSED = 1;

    Activity activity;

    ItemViewHolder holder;

    List<MtqStoreMarkRecordInfo> contentList;

    public MyStoreListAdapter(Activity activity) {
        this.activity = activity;
        contentList = new ArrayList<>();
    }

    public void updateData(List<MtqStoreMarkRecordInfo> dataSet) {
        contentList = dataSet;
        notifyDataSetChanged();
    }

    public void addDataSet(List<MtqStoreMarkRecordInfo> dataSet) {
        contentList.addAll(dataSet);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public Object getItem(int position) {
        return contentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MtqStoreMarkRecordInfo storeInfo = contentList.get(position);
        int itemApproveStatus = storeInfo.approvestatus;

        int tagKey = itemApproveStatus == 0 ? R.id.tag_item_approve_pendding : R.id.tag_item_approve_processed;

        if (convertView == null || convertView.getTag(tagKey) == null ) {

            convertView = LayoutInflater.from(activity).inflate(R.layout.item_my_stores_list, parent, false);

            holder = new ItemViewHolder(convertView);

            convertView.setTag(tagKey, holder);

        } else {

            holder = (ItemViewHolder) convertView.getTag(tagKey);

        }


        final String phoneNum = storeInfo.linkphone;

//        String departtime = TimestampTool.getTimeDescriptionFromTimestamp(mContext, item.departtime * 1000L);
        holder.tvReportTime.setText(TimestampTool.getTimeDescriptionFromTimestamp(activity, storeInfo.settime * 1000L));

        if ( storeInfo.storecode == null || TextUtils.isEmpty(storeInfo.storecode) ) {

            holder.tvStoreInfo.setText(storeInfo.storename);

        } else {
            holder.tvStoreInfo.setText(storeInfo.storecode + "-" + storeInfo.storename);
        }

        holder.tvAddress.setText(storeInfo.storeaddr);
        holder.tvKCode.setText(storeInfo.storekcode == null ? "" : storeInfo.storekcode);
        holder.tvContact.setText(storeInfo.linkman);

        if (phoneNum == null || TextUtils.isEmpty(phoneNum)) {

            holder.ivHintPhoneNum.setVisibility(View.GONE);
            holder.tvPhoneNum.setText("");

        } else {

            holder.ivHintPhoneNum.setVisibility(View.VISIBLE);
            holder.tvPhoneNum.setVisibility(View.VISIBLE);
            holder.tvPhoneNum.setText( Html.fromHtml("<u>"+ phoneNum +"</u>") );

        }


        holder.tvPhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CallUtil.call(activity, phoneNum);
            }
        });

        String strApproveStatus = "";

        switch (itemApproveStatus) {

            case 0:
                holder.clStoreStatusPanel.setVisibility(View.GONE);
                strApproveStatus = "待审核";
                break;
            case 1:
                strApproveStatus = "审核通过";
                holder.btnUploadAgain.setVisibility(View.GONE);
                holder.clStoreStatusPanel.setVisibility(View.VISIBLE);
                break;
            case 2:
                strApproveStatus = "审核不通过";
                holder.btnUploadAgain.setVisibility(View.VISIBLE);
                holder.clStoreStatusPanel.setVisibility(View.VISIBLE);
                break;


        }

        holder.tvApproveStatus.setText(strApproveStatus);

        holder.tvLockedStatus.setText(storeInfo.islock == 0 ? "未锁定" : "已锁定");

        final CldSapKDeliveryParam.CldDeliUploadStoreParm parm = new CldSapKDeliveryParam.CldDeliUploadStoreParm();

        parm.corpid = storeInfo.corpid + "";
        parm.address = storeInfo.storeaddr;
        parm.linkman = storeInfo.linkman;
        parm.phone = storeInfo.linkphone;
        parm.settype = 3;
        parm.storeid = storeInfo.storeid + "";
        parm.storename = storeInfo.storename;
        parm.storecode = storeInfo.storecode + "";
        parm.storekcode = storeInfo.storekcode;
        parm.uptime = System.currentTimeMillis() / 1000;

        holder.btnUploadAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               CldKDeliveryAPI.getInstance().uploadStore(parm, new CldKDeliveryAPI.ICldUploadStoreListListener() {
                   @Override
                   public void onGetResult(int errCode,String errMsg) {

                       ToastUtils.showMessage(activity, "上传成功");

                   }

                   @Override
                   public void onGetReqKey(String tag) {

                   }
               });
            }
        });

        return convertView;
    }

    public class ItemViewHolder {

        TextView tvReportTime;
        TextView tvStoreInfo;
        TextView tvAddress;
        TextView tvKCode;
        TextView tvContact;
        ImageView ivHintPhoneNum;
        TextView tvPhoneNum;
        ConstraintLayout clStoreStatusPanel;
        TextView tvApproveStatus;
        TextView tvLockedStatus;
        Button btnUploadAgain;

        ItemViewHolder(View view) {

            tvReportTime = (TextView) view.findViewById(R.id.tv_ms_report_time);
            tvStoreInfo = (TextView) view.findViewById(R.id.tv_ms_store_info);
            tvAddress = (TextView) view.findViewById(R.id.tv_ms_address);
            tvKCode = (TextView) view.findViewById(R.id.tv_ms_k_code);
            tvContact = (TextView) view.findViewById(R.id.tv_ms_contact);
            ivHintPhoneNum = (ImageView) view.findViewById(R.id.iv_ms_hint_phone_num);
            tvPhoneNum = (TextView) view.findViewById(R.id.tv_ms_phone_num);
            clStoreStatusPanel = (ConstraintLayout) view.findViewById(R.id.cl_store_status_panel);
            tvApproveStatus = (TextView) view.findViewById(R.id.tv_ms_approve_status);
            tvLockedStatus = (TextView) view.findViewById(R.id.tv_ms_locked_status);
            btnUploadAgain = (Button) view.findViewById(R.id.btn_ms_upload_again);
        }
    }
}
