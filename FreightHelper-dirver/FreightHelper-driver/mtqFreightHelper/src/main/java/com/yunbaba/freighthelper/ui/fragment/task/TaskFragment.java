package com.yunbaba.freighthelper.ui.fragment.task;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.api.account.AccountAPI;
import com.yunbaba.api.trunk.CheckTaskExpireTool;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.OffLineManager;
import com.yunbaba.api.trunk.OrmLiteApi;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.TaskOperator.OnDialogListener;
import com.yunbaba.api.trunk.UploadStoreAddrManager;
import com.yunbaba.api.trunk.bean.GetTaskListResult;
import com.yunbaba.api.trunk.bean.OnResponseResult;
import com.yunbaba.api.trunk.bean.UpdateTaskStatusResult;
import com.yunbaba.api.trunk.listner.OnObjectListner;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeMajorMainFragment;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.bean.eventbus.AuthEvent;
import com.yunbaba.freighthelper.bean.eventbus.CompanyChangeEvent;
import com.yunbaba.freighthelper.bean.eventbus.FeedBackEvent;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointStatusRefreshEvent;
import com.yunbaba.freighthelper.bean.eventbus.NetworkAvailableEvent;
import com.yunbaba.freighthelper.bean.eventbus.RefreshTaskListFromNetEvent;
import com.yunbaba.freighthelper.bean.eventbus.TaskBusinessMsgEvent;
import com.yunbaba.freighthelper.bean.eventbus.UpdateTaskStatusEvent;
import com.yunbaba.freighthelper.manager.NotifyManager;
import com.yunbaba.freighthelper.ui.activity.me.ScanActivity;
import com.yunbaba.freighthelper.ui.activity.task.FinishTaskListActivity;
import com.yunbaba.freighthelper.ui.activity.task.FreightPointActivity;
import com.yunbaba.freighthelper.ui.activity.task.SearchTaskListActivity;
import com.yunbaba.freighthelper.ui.activity.task.SelectCompanyActivity;
import com.yunbaba.freighthelper.ui.adapter.TaskListFiveAdapter;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.NetWorkUtils;
import com.yunbaba.freighthelper.utils.PermissionUtil;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TimestampTool;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.freighthelper.utils.WaitingUpdateTaskDialog;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.module.delivery.CldDalKDelivery;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IGetFeedBackInfoListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliGroup;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBackInfo;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTaskDetail;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//任务首页面
public class TaskFragment extends BaseButterKnifeMajorMainFragment implements ITaskListView, OnRefreshListener {

    // @BindView(R.id.title_left_img)
    // TextView tv_rightim;

    @BindView(R.id.rv_list)
    ListView rv_list;

    @BindView(R.id.tv_company)
    TextView tv_mcompany;

    @BindView(R.id.rll_company)
    RelativeLayout rll_company;

    @BindView(R.id.iv_select)
    ImageView iv_select;

    // @BindView(R.id.iv_titleright)
    // ImageView iv_titleright;

    @BindView(R.id.iv_scan)
    ImageView iv_scan;

    @BindView(R.id.pb_waiting)
    PercentRelativeLayout pb_waiting;

    @BindView(R.id.prl_warning)
    PercentRelativeLayout prl_warning;


    @BindView(R.id.pll_gotosearchtask)
    PercentLinearLayout pll_gotosearchtask;

    @BindView(R.id.prl_netfail)
    PercentRelativeLayout prl_netfail;

    @BindView(R.id.prl_emtpytask)
    PercentRelativeLayout prl_emptytask;

    @BindView(R.id.prl_netwarning)
    PercentRelativeLayout prl_netwarning;

    @BindView(R.id.tv_warning)
    TextView tv_warning;

    @BindView(R.id.srl_layout)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.tv_check_finishtask2)
    TextView tv_check_finishtask2;

    @BindView(R.id.tv_reload)
    TextView tv_reload;

    @BindView(R.id.tv_reload3)
    TextView tv_reload3;

    private BroadcastReceiver mNetWorkChangeReceiver = null;

    // 头尾view
    TextView tv_check_finishtask, tv_wait_trans, tv_time, tv_progress, tv_totalkm, tv_good, tv_company, tv_stoptask,
            tv_route, tv_transporting, tv_carlicense;

    LinearLayout pll_tasklist_item;

    LayoutInflater mLayoutInflater;

    View mTrasportingView, mFinishView;

    // TaskListAdapter mAdapter;

    // add by zhaoqy 2018-04-25
    TaskListFiveAdapter mFiveAdapter;

    // ArrayList<MtqDeliTask> mTasklist;

    CorpBean mCurCorp;

    MyHandler mHandler;

    boolean isNetConnect = true;

    // MtqDeliTask mCurrentTask;

    private TaskOperator mOperator;

    private final Handler mViewHandler = new ViewHandler(this);


    private ArrayList<MtqDeliTask> waitingList = new ArrayList<MtqDeliTask>();
    // private MtqDeliTask currentTask

    public static TaskFragment newInstance() {

        Bundle args = new Bundle();
        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentViewId() {

        return R.layout.fragment_task;
    }

    public void CheckCorp() {

        boolean isExist = false;

        over:
        for (CldDeliGroup item : CldDalKDelivery.getInstance().getLstOfMyGroups()) {

            if (item.corpId.equals(mCurCorp.getCorpId())) {
                isExist = true;
                break over;
            }

        }

        if (!isExist) {
            mCurCorp = new CorpBean();
            SPHelper.getInstance(_mActivity).WriteCurrentSelectCompanyBean(mCurCorp);
        }

    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {





        mOperator = TaskOperator.getInstance();

        initHeadFootView();

        // List<MsgInfo> mList = new ArrayList<>();
        // adp = new
        // rv_list.setAdapter(adp);
        mCurCorp = SPHelper.getInstance(_mActivity).ReadCurrentSelectCompanyBean();

        if (savedInstanceState == null)
            MLog.e("mainactivity", "oncreate saveinstance null");
        else
            MLog.e("mainactivity", "oncreate saveinstance not null");

        if (mCurCorp != null && !TextUtils.isEmpty(mCurCorp.getCorpId()) && savedInstanceState == null)
            CheckCorp();

        setCurrentCorp();

        mHandler = new MyHandler(this);
//        mAdapter = new TaskListAdapter(this.context, waitingList);
//        rv_list.setAdapter(mAdapter);

        // add by zhaoqy 2018-04-25

        waitingList = new ArrayList<>();

        mFiveAdapter = new TaskListFiveAdapter(this.context, waitingList);
        rv_list.setAdapter(mFiveAdapter);

        registerNetWorkChangeReceiver();


        NetWorkUtils.isNetworkConnected(this.context, new NetWorkUtils.OnNetworkListener() {
            @Override
            public void isAvailable(final boolean isAvailable) {

                _mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //更新UI
                        isNetConnect = isAvailable;
                        RefreshNetState();
                    }

                });

            }
        });

        // RefreshHeadAndTaskList();
        // mAdapter.notifyDataSetChanged();

        // MLog.e("getList", "getlist");
        // DebugFreightTool.AutoGetList(this.context

        EventBus.getDefault().register(this);

        mRefreshLayout.setColorSchemeResources(R.color.app_color2);

        mRefreshLayout.setOnRefreshListener(this);

        GetIsHasFinishTask(false);

        RefreshTaskListFromNet(true, false,false);

//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				WaitingUpdateTaskDialog.getInstance().showView(_mActivity,mViewHandler);
//			}
//		},3000);

    }

    public boolean GetIsHasFinishTask(boolean isNeedCallBack) {

        return DeliveryApi.getInstance().isHasFinishTask(mCurCorp.getCorpId(), _mActivity,
                (!isNeedCallBack) ? null : (new OnResponseResult<Boolean>() {

                    @Override
                    public void OnResult(Boolean result) {

                        SetIsFinishElemVisible(mCurCorp.getCorpId(), result);
                    }

                    @Override
                    public void OnError(int ErrCode) {


                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                }));
    }

    protected void SetIsFinishElemVisible(String corpid, boolean res) {


        if (mCurCorp != null && mCurCorp.getCorpId() != null) {

            if (corpid == null)
                return;

            if (!mCurCorp.getCorpId().equals(corpid))
                return;
        }

        if ((mCurCorp == null || mCurCorp.getCorpId() == null) && corpid != null)
            return;

        if (res) {

            tv_check_finishtask.setVisibility(View.VISIBLE);
            tv_check_finishtask2.setVisibility(View.VISIBLE);

        } else {
            tv_check_finishtask.setVisibility(View.GONE);
            tv_check_finishtask2.setVisibility(View.GONE);
        }

//        mAdapter.notifyDataSetChanged();

        // add by zhaoqy 2018-04-25
//        MtqDeliTask curTask = mOperator.getmCurrentTask();
//        if (curTask != null) {
//            waitingList.add(0, curTask);
//        }
      mFiveAdapter.notifyDataSetChanged();
    }

    int times = 2;

    public void RefreshTaskListFromNet(final boolean isfirsttime, final boolean isShowProgress, final boolean isNeedShowText) {

        // MLog.e("refresh tasklist", "from net");

        if (!isShowProgress) {
            showProgressBar();
            mRefreshLayout.setEnabled(false);
        }



        // if(isfirsttime)


        DeliveryApi.getInstance().getUnfinishTaskInServer(_mActivity,null, new OnResponseResult<GetTaskListResult>() {

            @Override
            public void OnResult(GetTaskListResult result) {

                hideProgressBar();

//                if(_mActivity!=null && _mActivity.isFinishing())
//                    return;

                prl_netfail.setVisibility(View.GONE);
                prl_netwarning.setVisibility(View.GONE);

                List<MtqDeliTask> lstOfTask = result.getLstOfTask();

                if (isAdded()) {
                    HandleGetListResult(lstOfTask);

                    // Toast.makeText(context, "刷新任务列表成功",
                    // Toast.LENGTH_SHORT).show();

                    // MLog.e("checktasklist",
                    // GsonTool.getInstance().toJson(lstOfTask).toString());

                }

                if (!isShowProgress) {
                    mRefreshLayout.setEnabled(true);
                }

                if (mRefreshLayout != null)
                    mRefreshLayout.setRefreshing(false);




                WaitingProgressTool.closeshowProgress();
//                if (_mActivity!=null && !_mActivity.isFinishing()) {
//                    if (result.getErrCode() == 0) {
//                        isNetConnect = true;
//                        RefreshNetState();
//                    }
//                }

                if(isNeedShowText)
                    Toast.makeText(_mActivity,"刷新成功",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void OnError(int ErrCode) {


                WaitingProgressTool.closeshowProgress();

                if(isfirsttime && times >0){

                    times = times - 1;

                    RefreshTaskListFromNet(isfirsttime,  isShowProgress,isNeedShowText);


                    return;

                }


                hideProgressBar();

                if (isAdded()) {
                    if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                        // Toast.makeText(TaskPointDetailActivity.this,
                        // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                    } else {
//                        if (ErrCode == 10001)
//                            Toast.makeText(context, "网络不给力，刷新失败", Toast.LENGTH_SHORT).show();
//                        else
//                            Toast.makeText(context, "刷新失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                }

                if (!isShowProgress) {
                    mRefreshLayout.setEnabled(true);
                }

                if (mRefreshLayout != null)
                    mRefreshLayout.setRefreshing(false);

               // if (isfirsttime)


                List<MtqDeliTask> tasklist =  OrmLiteApi.getInstance().queryAll(MtqDeliTask.class);

                if(tasklist!=null && tasklist.size()>0) {


                    mOperator.getInstance().setTaskListData(_mActivity,tasklist , false);


                }else{


//                    if(!isHasTask()){
//                        prl_netfail.setVisibility(View.VISIBLE);
//                    }


                }

                //  WaitingProgressTool.closeshowProgress();

                RefreshHeadAndTaskList();

            }

            @Override
            public void OnGetTag(String Reqtag) {


            }
        });
    }

    @Override
    public void onRefresh() {
        // TODO handle refresh event
        RefreshTaskListFromNet(false, true,false);
    }

    boolean isFirstTime = true;

    @Override
    public void onResume() {

        super.onResume();
        // updateNews();
        // if(mAdapter != null){
        //
        // RefreshHeadAndTaskList();
        //
        // }

        if (isFirstTime)
            isFirstTime = false;

    }

    @Override
    public void onSupportInvisible() {

        super.onSupportInvisible();
        stopTimer();
    }

    @Override
    public void onSupportVisible() {

        super.onSupportVisible();
        startTimer();
    }

    @Override
    public void onPause() {

        super.onPause();

    }

    // @Override
    // public void onStart() {
    //
    // super.onStart();
    // //mAdapter.tasklist = mOperator.getmTasklist();
    // MLog.e("check", mOperator.getmTasklist().size()+" ");
    //
    // }

    private synchronized void setCurrentCorp() {

        if (mCurCorp == null) {
            tv_mcompany.setText(R.string.all);
            return;
        }

        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(mCurCorp.getCorpId())) {
                    tv_mcompany.setText(R.string.all);
                } else {
                    tv_mcompany.setText(mCurCorp.getCorpName());
                }
            }
        });

    }

    public void initHeadFootView() {

        mLayoutInflater = LayoutInflater.from(_mActivity);
        mTrasportingView = mLayoutInflater.inflate(R.layout.view_homepage_tasklist_head, null);
        mFinishView = mLayoutInflater.inflate(R.layout.view_homepage_tasklist_foot, null);

        // foot
        tv_check_finishtask = ButterKnife.findById(mFinishView, R.id.tv_finishtask);
        tv_check_finishtask.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mFinishView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                JumpToFinishTaskActivity();
            }
        });

        // head
        tv_wait_trans = ButterKnife.findById(mTrasportingView, R.id.tv_waiting_trans);
        tv_time = ButterKnife.findById(mTrasportingView, R.id.tv_time);
        tv_progress = ButterKnife.findById(mTrasportingView, R.id.tv_progress);
        tv_route = ButterKnife.findById(mTrasportingView, R.id.tv_route);
        tv_totalkm = ButterKnife.findById(mTrasportingView, R.id.tv_totalkm);
        tv_good = ButterKnife.findById(mTrasportingView, R.id.tv_good);
        tv_company = ButterKnife.findById(mTrasportingView, R.id.tv_company);
        tv_stoptask = ButterKnife.findById(mTrasportingView, R.id.tv_stoptask);
        tv_transporting = ButterKnife.findById(mTrasportingView, R.id.tv_transporting);
        pll_tasklist_item = ButterKnife.findById(mTrasportingView, R.id.pll_tasklist_item);

        tv_carlicense = ButterKnife.findById(mTrasportingView, R.id.tv_carlicense);

        // modify by zhaoqy 2018-04-25
        // rv_list.addHeaderView(mTrasportingView, null, false);
        rv_list.addFooterView(mFinishView, null, false);

    }

    // 传输中任务显示
    @Override
    public void ShowTransportingItem(MtqDeliTask taskinfo) {

        CharSequence route = FreightLogicTool.getRouteStr(this.context, taskinfo.freight_type, taskinfo.preceipt,
                taskinfo.pdeliver);

        if (taskinfo.isback == 1)
            route = FreightLogicTool.getStoreNameAddBackPic(this.context, route);

        tv_route.setText(route);

        // tv_route.setText(FreightLogicTool.getRouteStr(this.context,
        // taskinfo.freight_type, taskinfo.preceipt,
        // taskinfo.pdeliver));

        tv_time.setText(FreightLogicTool.getTimeStr(this.context,
                TimestampTool.getTimeDescriptionFromTimestamp(this.context, taskinfo.departtime * 1000L)));

        tv_progress.setText(FreightLogicTool.getProgressStr(this.context, taskinfo.finish_count, taskinfo.store_count));

        tv_totalkm.setText(
                FreightLogicTool.getDistanceStr(this.context, ((taskinfo.distance / 1000) + getString(R.string.km)))); //

        tv_good.setText(FreightLogicTool.getGoodInfoStr(this.context,
                (taskinfo.gweight + getString(R.string.ton) + "、" + taskinfo.gvolume + getString(R.string.square))));

        tv_company.setText(taskinfo.corpname);

        // tv_stoptask.setText(taskinfo.gweight+","+taskinfo.gvolume);

        if (!TextUtils.isEmpty(taskinfo.carlicense)) {
            tv_carlicense.setText(taskinfo.carlicense);
        }

        pll_tasklist_item.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mOperator.getmCurrentTask() == null)
                    return;
                Intent intent = new Intent(_mActivity, FreightPointActivity.class);
                intent.putExtra("corpid", mOperator.getmCurrentTask().corpid);
                intent.putExtra("taskid", mOperator.getmCurrentTask().taskid);

                startActivity(intent);
            }
        });

        if (mOperator.getmCurrentTask().isback == 1
                && (mOperator.getmCurrentTask().finish_count == mOperator.getmCurrentTask().store_count - 1) && TaskUtils.isFinalUnFinishPointIsReturnPoint(_mActivity, mOperator.getmCurrentTask().taskid)) // 有回程点
        {
            tv_stoptask.setText("暂停回程");
        } else {


            tv_stoptask.setText("暂停任务");
        }
        tv_stoptask.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                //ShowFeedBackDialog(true, true);
                EventBus.getDefault().post(new UpdateTaskStatusEvent(mOperator.getmCurrentTask().corpid,
                        mOperator.getmCurrentTask().taskid, 3, mOperator.getmCurrentTask().corpid,
                        mOperator.getmCurrentTask().taskid, 0, 0, 0, 0));


            }
        });

    }

    // 等待运输列表显示
    @Override
    public void ShowTaskListItem(List<MtqDeliTask> taskList) {

    }

    // 设置头部Item可见度
    @Override
    public void SetHeadElemVisible(boolean Head, boolean center) {

        if (Head && center) {
            tv_transporting.setVisibility(View.VISIBLE);
            pll_tasklist_item.setVisibility(View.VISIBLE);
            tv_wait_trans.setVisibility(View.VISIBLE);

        } else if (Head && !center) {

            tv_transporting.setVisibility(View.VISIBLE);
            pll_tasklist_item.setVisibility(View.VISIBLE);
            tv_wait_trans.setVisibility(View.GONE);

        } else if (!Head && center) {

            tv_transporting.setVisibility(View.GONE);
            pll_tasklist_item.setVisibility(View.GONE);
            tv_wait_trans.setVisibility(View.VISIBLE);

        } else {

            tv_transporting.setVisibility(View.GONE);
            pll_tasklist_item.setVisibility(View.GONE);
            tv_wait_trans.setVisibility(View.GONE);
        }

        if (GetIsHasFinishTask(true)) {

            tv_check_finishtask.setVisibility(View.VISIBLE);
            tv_check_finishtask2.setVisibility(View.VISIBLE);

        } else {
            tv_check_finishtask.setVisibility(View.GONE);
            tv_check_finishtask2.setVisibility(View.GONE);
        }
    }

    // @OnItemClick(R.id.tv_finishtask)
    public void JumpToFinishTaskActivity() {

        Intent intent = new Intent(_mActivity, FinishTaskListActivity.class);
        _mActivity.startActivity(intent);
        _mActivity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

    }

    @OnClick(R.id.rll_company)
    public void JumpToSelectCompanyActivity() {
        // TransitionsHeleper.startActivity(_mActivity,
        // SelectCompanyActivity.class, iv_select);
        // (_mActivity, SelectCompanyActivity.class, tv_mcompany);
        _mActivity.startActivity(new Intent(_mActivity, SelectCompanyActivity.class));
    }

    @OnClick(R.id. pll_gotosearchtask)
    public void JumpToSearchTaskActivity() {
        // TransitionsHeleper.startActivity(_mActivity,
        // SelectCompanyActivity.class, iv_select);
        // (_mActivity, SelectCompanyActivity.class, tv_mcompany);
       _mActivity.startActivity(new Intent(_mActivity, SearchTaskListActivity.class));


        //_mActivity.startActivity(new Intent(_mActivity, StoreSearchAndMarkActivity.class));
    }



//
//    @OnClick(R.id.title_text)
//    public void JumpToSearchActivity() {
//       // _mActivity.startActivity(new Intent(_mActivity, SearchTaskActivity.class));
//
//        //
//        // Intent intent = new Intent(getActivity(), CarCheckActivity.class);
//        //
//        // MLog.e("carcheck",
//        // SPHelper.getInstance(getActivity()).getRecentCaruid()
//        // + " "
//        // + SPHelper.getInstance(getActivity())
//        // .getRecentCarLicense());
//        // intent.putExtra("caruid", "12345");
//        // intent.putExtra("carlicense", "粤A12345");
//        //
//        // startActivity(intent);
//        //
//
//        int res =  CldSetting.getInt("ols_ka_pwdtype", 0);
//        long  ress = CldKAccountAPI.getInstance().getDuid();
//        MLog.e("initsdk",  "res: "+res+" duid:"+ress);
//
//
//    }

    @OnClick(R.id.tv_check_finishtask2)
    public void JumpToFinishActivity() {
        JumpToFinishTaskActivity();
    }

    @OnClick(R.id.tv_reload)
    public void netReLoad() {

        //isNetConnect = NetWorkUtils.isNetworkConnected(TaskFragment.this.context);

        NetWorkUtils.isNetworkConnected(this.context, new NetWorkUtils.OnNetworkListener() {
            @Override
            public void isAvailable(final boolean isAvailable) {

                _mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //更新UI
                        isNetConnect = isAvailable;
                        if (isNetConnect) {

                            RefreshNetState();
                            RefreshTaskListFromNet(false, false,false);

                        } else {

                            final int visible = prl_emptytask.getVisibility();
                            prl_emptytask.setVisibility(View.GONE);
                            prl_netfail.setVisibility(View.GONE);

                            showProgressBar();

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {

                                    prl_netfail.setVisibility(View.VISIBLE);
                                    prl_emptytask.setVisibility(visible);
                                    hideProgressBar();

                                }
                            }, 200);

                        }
                    }

                });

            }
        });


    }


    @OnClick(R.id.tv_reload3)
    public void netReLoad3() {

        //isNetConnect = NetWorkUtils.isNetworkConnected(TaskFragment.this.context);

        NetWorkUtils.isNetworkConnected(this.context, new NetWorkUtils.OnNetworkListener() {
            @Override
            public void isAvailable(final boolean isAvailable) {

                _mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //更新UI
                        isNetConnect = isAvailable;
                        if (isNetConnect) {


                            boolean res =  AccountAPI.getInstance().isLogined();

                            if (!(res)) {

                                //MLog.e("check", "start RestoreLoginActivity");

                                //startActivityForResult(new Intent(this, RestoreLoginActivity.class), ReStoreLoginResult);
                                Toast.makeText(_mActivity,"未登录，开始登录...",Toast.LENGTH_LONG).show();
                                String loginName = AccountAPI.getInstance().getLoginName();
                                String pwd = AccountAPI.getInstance().getLoginPwd();
                                if (!TextUtils.isEmpty(loginName) && !TextUtils.isEmpty(pwd)) {
                                    /**
                                     * 自动登录
                                     */
                                    AccountAPI.getInstance().startAutoLogin();
                                }
                            }else {
                                prl_emptytask.setVisibility(View.GONE);
                                tv_check_finishtask.setVisibility(View.GONE);
                                RefreshNetState();
                                RefreshTaskListFromNet(false, false,true);
                            }

                        } else {

                            Toast.makeText(_mActivity,"网络断开状态",Toast.LENGTH_LONG).show();

                        }
                    }

                });

            }
        });


    }

    // 处理公司变更消息
    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public synchronized void onCompanyChangeEvent(CompanyChangeEvent event) {

        // mCurCorp = event.getCurrentCorp();

        mCurCorp = SPHelper.getInstance(_mActivity).ReadCurrentSelectCompanyBean();
        setCurrentCorp();
        HandleCompanyChange();

    }


    // 处理公司变更消息
    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
        public synchronized void onNetWorkEvent(NetworkAvailableEvent event) {

        // mCurCorp = event.getCurrentCorp();

        if(_mActivity!=null) {

            if (event != null) {

                if (event.isAvailable) {
                    isNetConnect = true;
                    RefreshNetState();
                }

            }
        }

    }


    // 处理任务状态变更消息
    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public synchronized void onTaskStatusChangeEvent(UpdateTaskStatusEvent event) {

        mOperator.showTaskStatusChangeAskDialog(_mActivity, event, new OnDialogListener() {

            @Override
            public void OnDialogDismiss() {

                // do not thing
            }

            @Override
            public void OnClickRight(UpdateTaskStatusEvent event) {


                if (event.getStatus() == 3) {

                    ShowFeedBackDialog(true, true);

                } else

                    HandleStatusChangeEvent(event);
            }

            @Override
            public void OnClickLeft(UpdateTaskStatusEvent event) {


                // do not thing

            }
        });

    }

    // 处理运货点状态变更消息
    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public synchronized void onFreightPointStatusChangeEvent(UpdateTaskStatusEvent event) {

        RefreshHeadAndTaskList();

    }

    // 处理运货点完成后导致的任务列表变更消息
    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public synchronized void onFreightPointStatusChangeEvent2(FreightPointStatusRefreshEvent event) {

        RefreshHeadAndTaskList();

    }

    // 处理账户登录更改后的任务列表更新消息
    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public synchronized void refreshtasklist(RefreshTaskListFromNetEvent event) {

        RefreshTaskListFromNet(false, false,false);

    }

    // 处理服务器发来的任务变更消息
    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public synchronized void onTaskBusinessMsgEvent(TaskBusinessMsgEvent event) {

        switch (event.getType()) {
            // 新任务，刷新列表
            case 1:
                RefreshTaskListFromNet(false, false,false);
                break;
            case 2:
                RefreshTaskListFromNet(false, false,false);

                if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {
                    // MLog.e("checkisshowview", "showviewtrue");
                    WaitingUpdateTaskDialog.getInstance().showView(_mActivity, mViewHandler);

                }

                if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {
                    CheckTaskExpireTool.CheckTaskExpire(_mActivity, event.getRefreshtaskIdList());
                }

                if (NotifyManager.getInstance().getContext() != null) {
                    Toast.makeText(NotifyManager.getInstance().getContext(), "您的任务列表有更新", Toast.LENGTH_LONG).show();

                }

                break;

            // 作废任务，删除某些任务
            case 3:

                if (event.getTaskIdList() != null && event.getTaskIdList().size() > 0) {

                    final String restr = TaskOperator.getInstance().completelyDeleteTaskByTaskIdList(event.getTaskIdList());

                    // if (!TextUtils.isEmpty(restr)) {
                    //
                    // // ToastUtils.showMessageLong(_mActivity, "运货单" + restr +
                    // // "已撤回");
                    // MLog.e("deletetaskfragment", "remind toast");
                    //
                    // new Handler().postDelayed(new Runnable() {
                    //
                    // @Override
                    // public void run() {
                    //
                    // if (NotifyManager.getInstance().getContext() != null)
                    // Toast.makeText(NotifyManager.getInstance().getContext(),
                    // "运货单" + restr + "已撤回",
                    // Toast.LENGTH_LONG).show();
                    // }
                    // }, 1000);
                    //
                    // }

                }

                RefreshHeadAndTaskList();
                // RefreshTaskListFromNet(false, false);

                WaitingUpdateTaskDialog.getInstance().showView(_mActivity, mViewHandler);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {


                        WaitingUpdateTaskDialog.getInstance().removeView();

                        if (NotifyManager.getInstance().getContext() != null) {
                            Toast.makeText(NotifyManager.getInstance().getContext(), "更新成功", Toast.LENGTH_LONG).show();
                        }

                    }
                }, 2000);

                //

                break;
            case 4:

                if (event.getRefreshtaskIdList() != null && event.getRefreshtaskIdList().size() > 0) {

                    final String restr = TaskOperator.getInstance().getCustOrderIdList(event.getRefreshtaskIdList());

                    // if (!TextUtils.isEmpty(restr)) {
                    //
                    //
                    // if (NotifyManager.getInstance().getContext() != null)
                    // Toast.makeText(NotifyManager.getInstance().getContext(),
                    // "运货单" + restr + "已撤回",
                    // Toast.LENGTH_LONG).show();
                    //
                    // }

                }

                RefreshTaskListFromNet(false, false,false);

                WaitingUpdateTaskDialog.getInstance().showView(_mActivity, mViewHandler);

                break;
            default:
                break;
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void HandleStatusChangeEvent(com.yunbaba.freighthelper.bean.eventbus.HandleStatusChangeEvent event) {


        HandleStatusChangeEvent(event.mevent);


    }


    // 处理消息


    public void HandleStatusChangeEvent(UpdateTaskStatusEvent event) {

        // 如果当前任务从未完成过运货点，直接跳转选择运货点页面，不做请求操作
        // 继续任务也是直接跳转
        if (mOperator.GetTask(event.getTaskid()) != null && event.getStatus() == 1) {

            // 从未设置过运货点
//            Intent intent = new Intent(_mActivity, SelectTransPointActivity.class);
//            intent.putExtra("corpid", event.getCorpid());
//            intent.putExtra("taskid", event.getTaskid());
//            _mActivity.startActivity(intent);
//            _mActivity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

            return;
        }

        showProgressBar();

        final String ecorpid = mOperator.getmCurrentTask() == null ? event.getEcorpid()
                : mOperator.getmCurrentTask().corpid;
        final String etaskid = mOperator.getmCurrentTask() == null ? event.getEtaskid()
                : mOperator.getmCurrentTask().taskid;

        // status; //【0待运货1运货中2已完成3暂停状态4中止状态 】
        //
        // if(event.getStatus() == 3)
        // ecorpid = "";

        DeliveryApi.getInstance().UpdateTaskInfo(_mActivity,event.getCorpid(), event.getTaskid(), event.getStatus(),
                event.getStatus() == 3 ? "" : ecorpid, event.getStatus() == 3 ? "" : etaskid, event.getX(),
                event.getY(), event.getCell(), event.getUid(), new OnResponseResult<UpdateTaskStatusResult>() {

                    @Override
                    public void OnResult(UpdateTaskStatusResult result) {


                        // MLog.e("updatetask", result.getErrCode() + " " +
                        // result.getStatus() + " " + result.getTaskid());
                        hideProgressBar();

                        if (result.getErrCode() != 0) {
                            // 请求错误
                            Toast.makeText(_mActivity, TaskUtils.getErrorMsgFromTaskErrorCode(result.getErrCode()), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // mOperator.HandleResultAfterRequest(result);

                        int status = result.getStatus();
                        switch (status) {
                            case 0:

                                PauseTask(result.getCorpid(), result.getTaskid(), status);
                                break;
                            case 1:

                                ChangeCurrentTask(result.getCorpid(), result.getTaskid(), status);

                                break;

                            case 2:

                                RemoveTask(result.getTaskid(), status);

                                break;
                            case 3:

                                PauseTask(result.getCorpid(), result.getTaskid(), status);
                                break;
                            default:
                                break;
                        }

                    }

                    @Override
                    public void OnError(int ErrCode) {

                        MLog.e("updatetask", ErrCode + " error");
                        if (ErrCode == DeliveryApi.TASK_CANCEL || ErrCode == DeliveryApi.TASKPOINT_CANCEL) {
                            // Toast.makeText(TaskPointDetailActivity.this,
                            // "当前操作任务单已撤回", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(_mActivity, TaskUtils.getErrorMsgFromTaskErrorCode(ErrCode), Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    }

                    @Override
                    public void OnGetTag(String Reqtag) {


                    }
                });
    }

    public void PauseTask(final String corpid, final String taskid, int status) {

        mOperator.PauseTask(corpid, taskid, status);
        RefreshHeadAndTaskList();
    }

    public void RemoveTask(final String taskid, int status) {

        MtqDeliTask task = mOperator.GetTask(taskid);

        if (task != null) {
            Toast.makeText(_mActivity, "已结束任务(完成" + task.finish_count + "/" + task.store_count + ")",
                    Toast.LENGTH_SHORT).show();
        }
        mOperator.RemoveTask(taskid, status);

        if (mOperator.getmCurrentTask() == null)
            RefreshHeadAndTaskList();
        else
            RefreshTaskList();

    }

    public void ChangeCurrentTask(final String corpid, final String taskid, int status) {

        mOperator.ChangeCurrentTask(corpid, taskid, status);

        RefreshHeadAndTaskList();

    }

    // 刷新列表
    public synchronized void RefreshTaskListWithNotSortList(ArrayList<MtqDeliTask> waitingList2,String corpid) {

        waitingList = waitingList2;

//        mAdapter.setList(waitingList);
//        tv_wait_trans.setText(getString(R.string.transport_status_stop) + " (" + waitingList.size() + ")");
//        mAdapter.notifyDataSetChanged();

        // add by zhaoqy 2018-04-25
        MtqDeliTask curTask = mOperator.getmCurrentTask();
        if (curTask != null && (curTask.corpid.equals(corpid)||TextUtils.isEmpty(corpid))) {

	        if ( getCurrentCorpId() != null && !getCurrentCorpId().equals(curTask.corpid) ) {


	        } else {

		        waitingList.add(0, curTask);
		        MLog.e("currenttask","not null"+waitingList.size());

	        }

        }else {

           // MLog.e("currenttask","null"+waitingList.size());
        }

        mFiveAdapter.setList(waitingList);
        mFiveAdapter.notifyDataSetChanged();

        if (!isHasTask()) {
            // 没任务

            prl_emptytask.setVisibility(View.VISIBLE);

        } else {

            prl_emptytask.setVisibility(View.GONE);
        }


        RefreshNetState();

    }

    // 刷新列表
    public void RefreshTaskList() {

        // MLog.e("shuaxinlist",
        // "true"+getCurrentCorpId()+mCurCorp.getCorpName());

        String corpid = getCurrentCorpId();

        waitingList = mOperator.getWaitingTaskListAfterSort(corpid);

//        mAdapter.setList(waitingList);
//        tv_wait_trans.setText(getString(R.string.transport_status_stop) + " (" + waitingList.size() + ")");
//        mAdapter.notifyDataSetChanged();

        // add by zhaoqy 2018-04-25
        MtqDeliTask curTask = mOperator.getmCurrentTask();
        if (curTask != null && (curTask.corpid.equals(corpid)||TextUtils.isEmpty(corpid))) {
            waitingList.add(0, curTask);
        }
        mFiveAdapter.setList(waitingList);
        mFiveAdapter.notifyDataSetChanged();

        if (!isHasTask()) {
            // 没任务

            prl_emptytask.setVisibility(View.VISIBLE);

        } else {

            prl_emptytask.setVisibility(View.GONE);
        }

    }

    public boolean isTaskListNotEmpty(){

        if(mOperator.getmTasklist()!=null && mOperator.getmTasklist().size()>0)
            return  true;
        else
            return  false;
    }

    public boolean isHasTask() {

        MLog.e("checkHastask",(mOperator.getmCurrentTask()==null?0:1)+" "+
                (mOperator.getmTasklist()==null?0:mOperator.getmTasklist().size())
        );

        MLog.e("checkHastask2",(TaskOperator.getInstance().getmCurrentTask()==null?0:1)+" "+
                (TaskOperator.getInstance().getmTasklist()==null?0:TaskOperator.getInstance().getmTasklist().size())
        );

        return (waitingList != null && waitingList.size() > 0)
                || ((mOperator.getmCurrentTask() != null|| isTaskListNotEmpty())
                && (getCurrentCorpId()!=null && mOperator.getmCurrentTask()!=null && mOperator.getmCurrentTask().corpid.equals(getCurrentCorpId())))
                || ((mOperator.getmCurrentTask() != null || isTaskListNotEmpty())&& TextUtils.isEmpty(getCurrentCorpId()))

                || ((mOperator.getmCurrentTask() != null|| isTaskListNotEmpty()));

    }

    // 刷新头部和列表
    public synchronized void RefreshHeadAndTaskList() {

        String corpid = getCurrentCorpId();

        waitingList = mOperator.getWaitingTaskListAfterSort(corpid);

        if (mOperator.getmCurrentTask() != null) {

            String curcropid = getCurrentCorpId();

            if (curcropid == null || mOperator.getmCurrentTask().corpid.equals(curcropid)) {

                ShowTransportingItem(mOperator.getmCurrentTask());

                if (waitingList != null && waitingList.size() > 0)
                    SetHeadElemVisible(true, true);
                else
                    SetHeadElemVisible(true, false);

                prl_warning.setVisibility(View.GONE);

            } else {
                if (waitingList != null && waitingList.size() > 0)
                    SetHeadElemVisible(false, true);
                else
                    SetHeadElemVisible(false, false);

                prl_warning.setVisibility(View.VISIBLE);
                tv_warning.setText("您当前有正在执行的任务,请切换企业到\" " + mOperator.getmCurrentTask().corpname + " \"查看.");

            }

        } else {

            if (waitingList != null && waitingList.size() > 0)
                SetHeadElemVisible(false, true);
            else
                SetHeadElemVisible(false, false);

        }

        RefreshTaskListWithNotSortList(waitingList,corpid);
    }

    // 处理获取完任务列表数据
    private void HandleGetListResult(List<MtqDeliTask> lstOfTask) {


        TaskOperator.getInstance().setTaskListData(_mActivity, lstOfTask, true);

        RefreshHeadAndTaskList();
    }

    // 处理企业变化
    public void HandleCompanyChange() {

        // MLog.e("company change", mCurCorp.getCorpName());
        RefreshHeadAndTaskList();
        RefreshNetState();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<TaskFragment> mActivity;

        public MyHandler(TaskFragment activity) {
            mActivity = new WeakReference<TaskFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;

            }

            if (msg.what == RefreshListToCheckTime && mActivity.get().isRunning) {

//                mActivity.get().mAdapter.notifyDataSetChanged();
                // add zhaoqy
                mActivity.get().mFiveAdapter.notifyDataSetChanged();
            }

            // mActivity.get().todo();
        }
    }

    @OnClick(R.id.iv_scan)
    public void JumpToScanActivity() {


//       List<MtqDeliTask> mTasklist = OrmLiteApi.getInstance().queryAll(MtqDeliTask.class);
//        MLog.e("klogin","2account"+ CldKAccountAPI.getInstance().getKuidLogin());
//        MLog.e("klogin","tasklistsize"+ (mTasklist == null ? "0'":(""+mTasklist.size())));


        if (PermissionUtil.isNeedPermission(_mActivity, Manifest.permission.CAMERA)) {

            Toast.makeText(_mActivity, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
            return;

        }

        Intent intent = new Intent(_mActivity, ScanActivity.class);
        _mActivity.startActivity(intent);


//		WaitingUpdateTaskDialog.getInstance().showView(_mActivity,mViewHandler);
//
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				new Thread(){
//
//					@Override
//					public void run() {
//						super.run();
//
//						EventBus.getDefault().post(new RefreshTaskListFromNetEvent());
//
//					}
//
//
//				}.start();
//
//			}
//		},5000);


        //
        // Intent intent = new Intent(_mActivity, MapSelectPointActivity.class);
        // _mActivity.startActivity(intent);

        // Intent intent = new Intent(_mActivity, ScanLoginActivity.class);
        // _mActivity.startActivity(intent);

        //
        // if (PermissionUtil.isNeedPermission(_mActivity,
        // Manifest.permission.CAMERA)) {
        //
        // Toast.makeText(_mActivity, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
        // return;
        //
        // }
        //
        // Intent intent = new Intent(_mActivity, ScanSearchTaskActivity.class);
        // _mActivity.startActivity(intent);

        // Intent intent = new Intent(_mActivity, MapSelectPointActivity.class);
        // _mActivity.startActivity(intent);
    }

    // @OnItemClick(R.id.iv_titleright)
    // public void JumpToMessageActivity() {
    // // Intent intent = new Intent(_mActivity, MsgActivity.class);
    // // _mActivity.startActivity(intent);
    //
    // Intent intent = new Intent(_mActivity, MapSelectPointActivity.class);
    // _mActivity.startActivity(intent);
    //
    // }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(mNetWorkChangeReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        MLog.e("taskfragment", "onstop");

    }

    protected void showProgressBar() {

        if (mRefreshLayout != null)
            mRefreshLayout.setRefreshing(false);

        if (pb_waiting != null)
            pb_waiting.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        if (pb_waiting != null)
            pb_waiting.setVisibility(View.GONE);
    }

    // @Override
    // public void onHiddenChanged(boolean hidden) {
    // super.onHiddenChanged(hidden);
    // // MLog.e("zhaoqy", "onHiddenChanged 000");
    // if (!hidden) {
    // // MLog.e("zhaoqy", "onHiddenChanged 111");
    // updateNews();
    // }
    // }

    // private void updateNews() {
    // if (MsgManager.getInstance().hasUnReadMsg()) {
    // iv_titleright.setImageResource(R.drawable.msg_icon_news);
    // } else {
    // iv_titleright.setImageResource(R.drawable.msg_icon);
    // }
    // }

    // @Subscribe(threadMode = ThreadMode.MAIN)
    // public void onMessageEvent(DeleteCorpEvent event) {
    //
    // if(!TextUtils.isEmpty(event.cropid)){
    //
    // if(mCurCorp.getCorpId().equals(event.cropid)){
    //
    // dsf
    //
    //
    //
    // }
    //
    // }
    //
    //
    // }

    public String getCurrentCorpId() {
        // if(mCurCorp == null)
        mCurCorp = SPHelper.getInstance(_mActivity).ReadCurrentSelectCompanyBean();

        // mCurCorp =
        // SPHelper.getInstance(_mActivity).ReadCurrentSelectCompanyBean();

        setCurrentCorp();

        return mCurCorp == null ? null : mCurCorp.getCorpId();

    }

    private void RefreshNetState() {


        MLog.e("networkstatus",isNetConnect?"true":"false");

        if (!isNetConnect) {
            // 没网络连接

            // 有任务
            if (isHasTask()) {

                prl_netfail.setVisibility(View.GONE);
                prl_netwarning.setVisibility(View.VISIBLE);

            } else {
                // 没任务

                prl_netfail.setVisibility(View.VISIBLE);
                prl_netwarning.setVisibility(View.GONE);
                // prl_warning.setVisibility(View.GONE);

            }

        } else {

            // 有网络连接

            prl_netfail.setVisibility(View.GONE);
            prl_netwarning.setVisibility(View.GONE);

        }
    }

    private void registerNetWorkChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mNetWorkChangeReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                MLog.e("NetWorkChange", intent.getAction()+"");


                NetWorkUtils.isNetworkConnected(TaskFragment.this._mActivity, new NetWorkUtils.OnNetworkListener() {
                    @Override
                    public void isAvailable(final boolean isAvailable) {

                        _mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                isNetConnect = isAvailable;

//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
                                        RefreshNetState();
//                                    }
//                                },500);



                                if (isNetConnect) {

                                  boolean res =  AccountAPI.getInstance().isLogined();

                                    if (!(res)) {

                                        //MLog.e("check", "start RestoreLoginActivity");

                                        //startActivityForResult(new Intent(this, RestoreLoginActivity.class), ReStoreLoginResult);
                                        String loginName = AccountAPI.getInstance().getLoginName();
                                        String pwd = AccountAPI.getInstance().getLoginPwd();
                                        if (!TextUtils.isEmpty(loginName) && !TextUtils.isEmpty(pwd)) {
                                            /**
                                             * 自动登录
                                             */
                                            AccountAPI.getInstance().startAutoLogin();
                                        }
                                    }else{

                                        UploadStoreAddrManager.getInstance().CheckAndUpload(_mActivity);

                                        OffLineManager.getInstance().TraverseOfflineData(_mActivity);

                                    }

                                }
                                EventBus.getDefault().post(new AuthEvent());
                            }});

                }});

              //  isNetConnect = NetWorkUtils.isNetworkConnected(TaskFragment.this._mActivity);

            }

        };

        if (_mActivity != null)
            _mActivity.registerReceiver(mNetWorkChangeReceiver, intentFilter);
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (_mActivity != null)
            _mActivity.unregisterReceiver(receiver);
    }

    private Timer mTimer;
    private static final int RefreshListToCheckTime = 369;
    private TimerTask checkTimeTask;
    private static final int checkPeriod = 60000;

    public synchronized Timer getTimer() {

        if (mTimer == null)
            mTimer = new Timer();

        return mTimer;

    }

    private boolean isTimerFirstTime = true;

    private synchronized void startTimer() {

        getTimer();

        if (checkTimeTask == null) {
            checkTimeTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(RefreshListToCheckTime);
                }
            };
        }

        if (mTimer != null && checkTimeTask != null) {
            mTimer.schedule(checkTimeTask, isTimerFirstTime ? checkPeriod : 0, checkPeriod);
            isTimerFirstTime = false;

        }

    }

    private synchronized void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        if (checkTimeTask != null) {
            checkTimeTask.cancel();
            checkTimeTask = null;
        }
    }

    public void ShowFeedBackDialog(final boolean isNeedStopTask, final boolean isLast) {

        if (mOperator.getmCurrentTask() == null)
            return;

        mOperator.getTaskDetailDataFromDB(_mActivity,mOperator.getmCurrentTask().taskid, new OnObjectListner<MtqDeliTaskDetail>() {
            @Override
            public void onResult(MtqDeliTaskDetail res) {
                MtqDeliTaskDetail mtaskInfo = res;
                if (mtaskInfo == null || mtaskInfo.store == null || mtaskInfo.orders == null) {


                    DeliveryApi.getInstance().getTaskDetailInServer(mOperator.getmCurrentTask().taskid, mOperator.getmCurrentTask().corpid, new OnResponseResult<MtqDeliTaskDetail>() {

                        @Override
                        public void OnResult(final MtqDeliTaskDetail result) {



                            _mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (result != null) {
                                        TaskOperator.getInstance().saveTaskDetailDataToBD(result);

                                        ShowFeedBackDialog2(result, isNeedStopTask, isLast);
                                    }
                                }
                            });

                        }

                        @Override
                        public void OnGetTag(String Reqtag) {


                        }

                        @Override
                        public void OnError(int ErrCode) {


                        }
                    });


                } else {

                    ShowFeedBackDialog2(mtaskInfo, isNeedStopTask, isLast);

                }
            }
        });


    }

    public void ShowFeedBackDialog2(final MtqDeliTaskDetail mtaskInfo, final boolean isNeedStopTask, final boolean isLast) {


        // boolean isFeedBack = false;


        Iterator<MtqDeliStoreDetail> iter = mtaskInfo.store.iterator();
        MtqDeliStoreDetail tmp = null;
        MtqDeliOrderDetail tmp2 = null;
        over1:
        while (iter.hasNext()) {

            tmp = iter.next();

            if (tmp.storestatus == 1) {

                break over1;

            } else {
                tmp = null;
            }

        }

        if (tmp != null) {

            Iterator<MtqDeliOrderDetail> iter2 = mtaskInfo.orders.iterator();

            over2:
            while (iter2.hasNext()) {
                tmp2 = iter2.next();

                if (tmp2.waybill.equals(tmp.waybill)) {

                    break over2;
                } else {
                    tmp2 = null;
                }

            }

        }

        if (tmp != null && tmp2 != null) {

            final MtqDeliStoreDetail tmp3 = tmp;
            final MtqDeliOrderDetail tmp4 = tmp2;
            showProgressBar();

            CldBllKDelivery.getInstance().getFeedBackReasonInfo(mOperator.getmCurrentTask().corpid,
                    new IGetFeedBackInfoListener() {

                        @Override
                        public void onGetResult(int errCode, List<FeedBackInfo> result) {



                            hideProgressBar();

                            if (errCode == 0 && result != null) {

                                EventBus.getDefault()
                                        .post(new FeedBackEvent((ArrayList<FeedBackInfo>) result, tmp3, tmp4));

                            } else
                                Toast.makeText(_mActivity, "获取反馈信息失败", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onGetReqKey(String arg0) {


                        }
                    });

        } else {

            UpdateTaskStatusEvent event = new UpdateTaskStatusEvent(mOperator.getmCurrentTask().corpid,
                    mOperator.getmCurrentTask().taskid, 3, mOperator.getmCurrentTask().corpid,
                    mOperator.getmCurrentTask().taskid, 0, 0, 0, 0);

            HandleStatusChangeEvent(event);
        }
    }


    private static class ViewHandler extends Handler {
        private final WeakReference<TaskFragment> mActivity;

        public ViewHandler(TaskFragment activity) {
            mActivity = new WeakReference<TaskFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
            if (msg.what == 0)
                mActivity.get().closeUpdateView();
            else if (msg.what == 1) {
                mActivity.get().closeUpdateView(true);
            } else if (msg.what == 2) {
                mActivity.get().closeUpdateView(false);
            }
        }
    }


    public void closeUpdateView() {
        WaitingUpdateTaskDialog.getInstance().removeView();
    }

    public void closeUpdateView(boolean isupdate) {
        WaitingUpdateTaskDialog.getInstance().removeView(isupdate);
    }

}
