package com.yunbaba.freighthelper.ui.fragment.task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.yunbaba.api.trunk.FreightPointDeal;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseMainFragment;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointEvent;
import com.yunbaba.freighthelper.bean.eventbus.RefreshAdapterEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.ui.activity.task.TaskPointDetailActivity;
import com.yunbaba.freighthelper.ui.adapter.FreightPointExpandAdapter;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MessageId;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

public class FreightPointListFragment extends BaseMainFragment implements OnItemClickListener, OnItemLongClickListener {
    private View mView;
    private ListView mListView;
    private int mRequestCode = FreightConstant.TASK_POINT_REQUSEST_CODE;
    TextView emptyView;
    FreightPointExpandAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_freightpoint_list, container, false);

        initControl();
        initData();
        EventBus.getDefault().register(this);
        return mView;
    }

	public static FreightPointListFragment newInstance() {
        Bundle args = new Bundle();
        FreightPointListFragment fragment = new FreightPointListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void initControl() {
        mListView = (ListView) mView.findViewById(R.id.id_feightpoint_listview);
        emptyView = (TextView) mView.findViewById(R.id.freight_list_entryview);
        mListView.setEmptyView(emptyView);


        // mListView.setDivider(new ColorDrawable(Color.rgb()));
        // mListView.setDividerHeight(1);   //DensityUtils.dip2px(_mActivity,10)
        if (FreightPointDeal.getInstace().setAdpate) {
            setAdapter();
        }
    }

    private void initData() {
        mListView.setOnItemClickListener(this);

    }

    public void setAdapter() {
        adapter = new FreightPointExpandAdapter(getActivity());
        mListView.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUi(FreightPointEvent event) {
        switch (event.msg.what) {
            case MessageId.MSGID_FREIGHTPOINT_ADPTER:
                setAdapter();
                break;
            case MessageId.MSGID_FREIGHTPOINT_ADPTER_FAIL:
                emptyView.setText("获取不到数据，请稍后再试。");
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsg(RefreshAdapterEvent event) {
        adapter.SortData();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroyView() {

        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position,
                            long id) {


        int type = adapter.getItemViewType(position);

        if (type == 1) {
            //if(storedetail == null) {


            adapter.unFold();
            adapter.notifyDataSetChanged();

            if(position!=0) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!adapter.isFold) {

                            if (position + 1 < adapter.getCount()) {

                                try {
                                    mListView.setSelectionFromTop(position,0);
                                } catch (Exception e) {

                                }

                            }

                        }
                    }
                }, 200);

            }

            return;
            //	}
        } else if (type == 0) {


            MtqDeliStoreDetail storedetail = adapter.getItem(position);
            if (storedetail == null) {

                return;
            }


            //ArrayList<MtqDeliStoreDetail> storelist = FreightPointDeal.getInstace().getmStore();
            HashMap<Integer, MtqDeliOrderDetail> orderlist = FreightPointDeal.getInstace().getmOrder();
            //storelist.get(position);


            Intent intent = new Intent(getActivity(), TaskPointDetailActivity.class);

            //添加storedetail
            String str = GsonTool.getInstance().toJson(storedetail);
            intent.putExtra("storedetail", str);

            //添加taskid
            intent.putExtra("taskid", storedetail.taskId);
            intent.putExtra("corpid", storedetail.corpId);
            if (orderlist.get(storedetail.storesort) != null) {
                String str2 = GsonTool.getInstance().toJson(orderlist.get(storedetail.storesort));
                intent.putExtra("orderdetail", str2);
            }

            getActivity().startActivityForResult(intent, mRequestCode);
        }


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {

        return false;
    }

    @Override
    public boolean onBackPressedSupport() {

        return false;
    }


}
