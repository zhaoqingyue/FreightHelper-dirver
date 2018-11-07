package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.KeywordUtil;
import com.yunbaba.ols.module.delivery.bean.MtqStore;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreListSearchAdapter extends BaseAdapter {

    private List<MtqStore> mList = null;
    private Context mContext;
    public static final int keycolor = Color.parseColor("#ef2c29");

    public StoreListSearchAdapter(Context context, List<MtqStore> list) {
        mContext = context;
        mList = list;
    }

    public void updateListView(List<MtqStore> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final MtqStore contacts = mList.get(position);

        ViewHolder viewHolder;

        if (view != null) {
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_store_search, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        // int section = getSectionForPosition(position);
        // if (position == getPositionForSection(section)) {
        // holder.tv_letter.setVisibility(View.VISIBLE);
        // holder.tv_letter.setText(contacts.letter);
        // } else {
        // holder.tv_letter.setVisibility(View.GONE);
        // holder.v_line.setVisibility(View.VISIBLE);
        // }


        if (TextUtils.isEmpty(contacts.storeName)) {
            viewHolder.tv_first.setText("");
        }
            //holder.tv_first.setText(contacts.linkMan.replaceAll("\\s*", "").trim().subSequence(0, 1));
        else {
            viewHolder.tv_first.setText(contacts.storeName.replaceAll("\\s*", "").trim().subSequence(0, 1));
        }

        viewHolder.tv_store.setText(contacts.storeName);
        // holder.tv_address.setText(contacts.storeAddr);

        if (!TextUtils.isEmpty(contacts.storeAddr) && !TextUtils.isEmpty(contacts.kCode)) {

            if (!TextUtils.isEmpty(keyword)) {

                viewHolder.tv_address.setText(KeywordUtil.matcherSearchTitle(keycolor, contacts.storeAddr, keyword));
            } else {
                viewHolder.tv_address.setText(contacts.storeAddr);
            }

            viewHolder.tv_address.setVisibility(View.VISIBLE);
            viewHolder.tv_mark.setVisibility(View.GONE);
        } else {
            viewHolder.tv_address.setVisibility(View.GONE);
            viewHolder.tv_mark.setVisibility(View.VISIBLE);
        }

        // holder.ll_item.setOnClickListener(new OnClickListener(){
        //
        // @Override
        // public void onClick(View v) {
        //
        // Intent intent = new Intent(mContext, StoreDetailActivity.class);
        // //Bundle bundle = new Bundle();
        // intent.putExtra("storeinfo",
        // GsonTool.getInstance().toJson(contacts));
        // //intent.putExtras(bundle);
        // mContext.startActivity(intent);
        // }});

        return view;
    }

    static class ViewHolder {

//        @BindView(R.id.tv_letter)
//        TextView tv_letter;

        @BindView(R.id.tv_first)
        TextView tv_first;

        @BindView(R.id.tv_address)
        TextView tv_address;

        @BindView(R.id.tv_store)
        TextView tv_store;

        @BindView(R.id.v_line)
        View v_line;

        @BindView(R.id.tv_mark)
        TextView tv_mark;

        @BindView(R.id.ll_item)
        PercentRelativeLayout ll_item;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

    int type = 0; // 0默认 1搜索
    String keyword;

    public void setData(List<MtqStore> mlist2, int i) {

        mList = mlist2;
        type = i;

    }

    public void setData(List<MtqStore> mlist2, int i, String keyword) {

        mList = mlist2;
        type = i;
        this.keyword = keyword;

    }
    // @Override
    // public Object[] getSections() {
    // return null;
    // }
    //
    // @SuppressLint("DefaultLocale")
    // @Override
    // public int getPositionForSection(int section) {
    // for (int i = 0; i < getCount(); i++) {
    // String sortStr = mList.get(i).letter;
    // char firstChar = sortStr.toUpperCase().charAt(0);
    // if (firstChar == section) {
    // return i;
    // }
    // }
    // return -1;
    // }
    //
    // @Override
    // public int getSectionForPosition(int position) {
    // return mList.get(position).letter.charAt(0);
    // }
}
