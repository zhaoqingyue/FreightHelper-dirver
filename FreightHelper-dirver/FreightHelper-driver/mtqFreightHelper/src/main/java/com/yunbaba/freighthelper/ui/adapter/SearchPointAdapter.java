package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbaba.api.trunk.FreightPointDeal;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import java.util.List;

import static com.yunbaba.freighthelper.utils.FreightLogicTool.setImgbyOptype;

/**
 * Created by zhonghm on 2018/4/23.
 */

public class SearchPointAdapter extends BaseAdapter {

    public List<MtqDeliStoreDetail> mlistdata;
    // HashMap<String, String> mlistdata;
    Context mContext;
    private int currentselect = 0;

    public static final int keycolor = Color.parseColor("#ef2c29");

    private boolean isRecent = true;
    public boolean isRecent() {
        return isRecent;
    }

    public void setRecent(boolean isRecent) {
        this.isRecent = isRecent;
    }

    public void RefreshList(boolean isRecent) {

        this.isRecent = isRecent;
        this.notifyDataSetChanged();
    }

    public SearchPointAdapter(Context mContext, List<MtqDeliStoreDetail> mlistdata2) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        this.mlistdata = mlistdata2;
    }

    @Override
    public int getCount() {

        return mlistdata == null ? 0 : mlistdata.size();
    }

    @Override
    public MtqDeliStoreDetail getItem(int position) {

        // if()
        return mlistdata == null ? null : mlistdata.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    String keyword;

    @Override
    public View getView(int position, View view, ViewGroup parent) {


        SearchPointAdapter.ViewHolder  viewHolder;

        if (view != null) {
            viewHolder = (SearchPointAdapter.ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_search_result, parent, false);
            viewHolder = new SearchPointAdapter.ViewHolder(view);
            view.setTag( viewHolder);
        }


        MtqDeliStoreDetail storeDetail = getItem(position);

        viewHolder.index = storeDetail.storesort;
        viewHolder.storeDetail = storeDetail;
        // holder.address.setText((storeDetail.regionname +
        // storeDetail.storeaddr).replaceAll("\\s*", ""));

        if (storeDetail != null && !TextUtils.isEmpty(storeDetail.storecode)) {
            //  String temp = storeDetail.storesort + "." +storeDetail.storecode + "-" + storeDetail.storename;
            CharSequence format = FreightLogicTool.formatPointNameStr(storeDetail.storesort + ".", storeDetail.storecode, "-" + storeDetail.storename);
            if (format != null) {
                viewHolder.name.setText(format);
            } else {
                viewHolder.name.setText(
                        (viewHolder.index) + "." + (TextUtils.isEmpty(storeDetail.storecode) ? "" : (storeDetail.storecode + "-"))
                                + storeDetail.storename);
            }
        } else {
            // holder.name.setText(storeDetail.storesort + "." + storeDetail.storename);

            viewHolder.name.setText(
                    (viewHolder.index) + "." + (TextUtils.isEmpty(storeDetail.storecode) ? "" : (storeDetail.storecode + "-"))
                            + storeDetail.storename);
        }


        if (storeDetail.isUnknownAddress) {

            if (!TaskUtils.isStorePosUnknown(storeDetail)) {
                viewHolder.address.setText((storeDetail.storeaddr).replaceAll("\\s*", ""));
            } else
                viewHolder.address.setText(FreightLogicTool.getStoreNameNoPosition(
                        (storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", "")));
        } else
            viewHolder.address.setText((storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", ""));

//        holder.name.setText(
//                (position + 1) + "." + (TextUtils.isEmpty(storeDetail.storecode) ? "" : (storeDetail.storecode + "-"))
//                        + storeDetail.storename);


//        holder.name.setText(
//                (holder.index) + "." + (TextUtils.isEmpty(storeDetail.storecode) ? "" : (storeDetail.storecode + "-"))
//                        + storeDetail.storename);

        viewHolder.status
                .setText(FreightPointDeal.getInstace().getStoreStatusText(storeDetail.storestatus, storeDetail.optype));

        viewHolder.time.setVisibility(View.GONE);
        viewHolder.timeStatus.setVisibility(View.GONE);

//        }

       // holder.operate2.setVisibility(View.INVISIBLE);

        setImgbyOptype(storeDetail.optype, viewHolder.image);

        //   setViewbyStoreStatus(storeDetail.storestatus, holder);

        viewHolder.contact.setText(storeDetail.linkman);
        viewHolder.phone.setText(storeDetail.linkphone);


        viewHolder.locate.setVisibility(View.GONE);
        viewHolder.locate2.setVisibility(View.GONE);

        return view;
    }

    public MtqDeliStoreDetail getSelectStoreDetail() {

        if (currentselect < 0 || currentselect > mlistdata.size() - 1)
            return null;

        return mlistdata.get(currentselect);

    }

    public int getCurrentselect() {
        return currentselect;
    }

    public void setCurrentselect(int currentselect) {
        this.currentselect = currentselect;
    }

    static class ViewHolder {
//        @BindView(R.id.tv_letter)
//        TextView tv_letter;

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

        LinearLayout line;

        MtqDeliStoreDetail storeDetail;
        int index;

        public ViewHolder(View view) {

            address = (TextView) view.findViewById(R.id.id_freight_point_address);
            name = (TextView) view.findViewById(R.id.id_freight_point_name);
            status = (TextView) view.findViewById(R.id.id_freight_point_status);
            time = (TextView) view.findViewById(R.id.id_freight_point_time);
            timeStatus = (TextView) view.findViewById(R.id.freight_time_status);
            image = (ImageView) view.findViewById(R.id.freight_point_type_pic);

            line = (LinearLayout) view.findViewById(R.id.freigt_point_line);
            locate = (TextView) view.findViewById(R.id.btn_getposition2);
            locate2  = (LinearLayout) view.findViewById(R.id.btn_getposition);
            phone = (TextView) view.findViewById(R.id.tv_phone);
            contact = (TextView) view.findViewById(R.id.tv_contact);
        }
    }

    public void setList(List<MtqDeliStoreDetail> result) {

        this.mlistdata = result;
    }


}