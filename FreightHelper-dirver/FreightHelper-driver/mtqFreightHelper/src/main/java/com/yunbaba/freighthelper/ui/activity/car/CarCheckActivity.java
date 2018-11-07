package com.yunbaba.freighthelper.ui.activity.car;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.net.CldHttpClient;
import com.yunbaba.api.trunk.bean.CarCheckResultBean;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.LastCarCheckResultBean;
import com.yunbaba.freighthelper.ui.adapter.CarCheckResultAdapter;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IExaminationDetailListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.Examinationdetail;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqExaminationUnit;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarCheckActivity extends BaseButterKnifeActivity {

    @BindView(R.id.ll_root)
    PercentRelativeLayout ll_root;
    @BindView(R.id.iv_car_center)
    ImageView ivCarCenter;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_recheck)
    TextView tvRecheck;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.ll_check)
    PercentRelativeLayout llCheck;
    @BindView(R.id.iv_car_top)
    ImageView ivCarTop;
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.iv_resultdesc_error)
    ImageView ivResultdescError;

    @BindView(R.id.iv_scan)
    ImageView ivScan;

    @BindView(R.id.tv_resultdesc_title)
    TextView tvResultdescTitle;
    @BindView(R.id.tv_resultdesc_detail)
    TextView tvResultdescDetail;
    @BindView(R.id.ll_resultdesc)
    PercentLinearLayout llResultdesc;
    @BindView(R.id.lv_result)
    ListView lvResult;
    @BindView(R.id.tv_recheck2)
    TextView tvRecheck2;
    @BindView(R.id.ll_recheck)
    PercentLinearLayout llRecheck;
    @BindView(R.id.ll_checkresult)
    PercentRelativeLayout llCheckresult;
    @BindView(R.id.pb_waiting)
    PercentRelativeLayout pbWaiting;
    @BindView(R.id.iv_titleleft)
    ImageView ivTitleleft;

    @BindView(R.id.iv_titleleft2)
    ImageView ivTitleleft2;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_rightright)
    TextView tvRightright;
    @BindView(R.id.ll_title)
    PercentRelativeLayout llTitle;

    MtqExaminationUnit mcheckresult;
    String mcarduid;
    String mcarlicense;

    CarCheckResultAdapter resAdapter;
    ArrayList<Examinationdetail> mlistdata = new ArrayList<>();
    LastCarCheckResultBean lastcheckbean;
    private Timer mTimer;
    List<Examinationdetail> mresult;
    boolean CheckFinish = false;
    int ErrorTimes = 0;
    public String requestTag;

    Animation checkAnimation;

    @Override
    public int getLayoutId() {

        return R.layout.activity_carcheck;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation

        ButterKnife.bind(this);

        if (getIntent().getStringExtra("caruid") == null || getIntent().getStringExtra("carlicense") == null) {

            finish();
        }

        mcarduid = getIntent().getStringExtra("caruid");
        mcarlicense = getIntent().getStringExtra("carlicense");

        if (getIntent().getStringExtra("checkresbean") != null) {

            CarCheckResultBean bean = GsonTool.getInstance().fromJson(getIntent().getStringExtra("checkresbean"),
                    CarCheckResultBean.class);

            if (bean != null)
                mresult = bean.data;

        }

        MLog.e("carcheck", mcarduid + " " + mcarlicense);

        resAdapter = new CarCheckResultAdapter(this, mlistdata);
        lvResult.setAdapter(resAdapter);

        if (mresult != null) {

            CheckFinishAndShowResult();

        } else {
            startNewCheck();
        }

        lvResult.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (resAdapter != null) {
                    resAdapter.setSpread(true, position);
                    resAdapter.notifyDataSetChanged();
                }

            }
        });

    }

    public void startNewCheck() {

        TransState(false, null);
        startTimeTask();
        NewCheckFromNet();

    }

    public void CheckFinishAndShowResult() {

        stopTimeTask();
        TransState(true, mresult);
    }

    @OnClick({R.id.tv_recheck, R.id.tv_recheck2, R.id.iv_titleleft, R.id.tv_rightright, R.id.iv_titleleft2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_recheck:
            case R.id.tv_recheck2:

                // TransState(checkFinish, res);

                if (!CldPhoneNet.isNetConnected()) {
                    Toast.makeText(this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();

                    return;
                }

                CheckFinish = false;
                startNewCheck();

                break;
            case R.id.iv_titleleft:
            case R.id.iv_titleleft2:
                finish();
                break;
            case R.id.tv_rightright:

                // Intent intent = new Intent(this, CarCheckActivity.class);
                Intent intent = new Intent(this, CarCheckHistoryActivity.class);

                intent.putExtra("caruid", mcarduid);
                intent.putExtra("carlicense", mcarlicense);

                startActivity(intent);

                // intent.putExtra("caruid", caruid)
                // CarManager.getInstance().get

                break;
        }
    }

    public void NewCheckFromNet() {

        // showProgressBar();

        if (!CldPhoneNet.isNetConnected()) {

            mresult = null;

            mhandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // ivScan.setVisibility(View.GONE);TODO Auto-generated
                    // method stub
                    Toast.makeText(CarCheckActivity.this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();

                    CheckFinish = true;

                    stopTimeTask();

                    TransState(true, null);

                    StopAnim();
                }
            }, 500);

            return;

        } else {

            LastCarCheckResultBean resultbean = GeneralSPHelper.getInstance(this).getLastCarCheckResult(mcarduid);

            if (resultbean != null && resultbean.time > 0) {

                long timespan = System.currentTimeMillis() - resultbean.time;

                if (timespan > 0 && timespan < (120 * 1000)) {

                    MLog.e("车辆体检", "距离上次体检在2分钟内，直接读取上次体检结果");

                    mresult = resultbean.result;
                    CheckFinish = true;

                    return;

                }

            }

        }

        CheckFinish = false;

        CldKDeliveryAPI.getInstance().getExaminationDetail(mcarduid, "0", 0, new IExaminationDetailListener() {

            @Override
            public void onGetResult(int errCode, List<Examinationdetail> result) {

                MLog.e("check", GsonTool.getInstance().toJson(result));

                if (isFinishing())
                    return;


                if (errCode == 0) {
                    mresult = result;
                    CheckFinish = true;

                    lastcheckbean = new LastCarCheckResultBean(System.currentTimeMillis(), result);

                    GeneralSPHelper.getInstance(CarCheckActivity.this).setLastCarCheckResult(mcarduid, lastcheckbean);

                    setResult(CarCheckHistoryActivity.CAR_CHECK);

                    // TransState(true, mresult);

                } else {

                    MLog.e("carcheck", "" + errCode);
                    // Toast.makeText(CarCheckHistoryActivity.this,
                    // "体检详情失败",
                    // Toast.LENGTH_SHORT).show();

                    ErrorTimes += 1;

                    if (errCode == 10001) {
                        Toast.makeText(CarCheckActivity.this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                        CheckFinish = true;
                        mresult = null;

                    } else {

                        if (ErrorTimes < 3) {

                            // if (mHandler != null)
                            // mHandler.sendEmptyMessage(RequestFinishTaskList);
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {

                                    NewCheckFromNet();
                                }
                            }, 3000);

                        } else {
                            // hideProgressBar();

                            // Toast.makeText(CarCheckActivity.this,
                            // "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
                            CheckFinish = true;

                            // stopTimeTask();
                            // TransState(true, null);
                            // StopAnim();

                        }

                        mresult = null;
                    }

                }

            }

            @Override
            public void onGetReqKey(String arg0) {

                requestTag = arg0;
            }

        });

    }

    public void TransState(boolean checkFinish, List<Examinationdetail> result) {

        if (checkFinish) {

            if (result == null) {

                ll_root.setBackgroundColor(getResources().getColor(R.color.black));
                llTitle.setBackgroundColor(getResources().getColor(R.color.black));
                tvTitle.setTextColor(getResources().getColor(R.color.white));
                ivTitleleft.setVisibility(View.VISIBLE);
                ivTitleleft2.setVisibility(View.GONE);

//                ll_root.setBackgroundColor(getResources().getColor(R.color.white));
//                llTitle.setBackgroundColor(getResources().getColor(R.color.white));
//                tvTitle.setTextColor(getResources().getColor(R.color.black_title));
//                ivTitleleft.setVisibility(View.GONE);
//                ivTitleleft2.setVisibility(View.VISIBLE);

                llCheck.setVisibility(View.VISIBLE);
                llCheckresult.setVisibility(View.GONE);

                tvProgress.setVisibility(View.INVISIBLE);
                tvState.setText("很抱歉,没有检测到任何数据");
                tvRecheck.setVisibility(View.VISIBLE);

            } else {

                ll_root.setBackgroundColor(getResources().getColor(R.color.white));
                llTitle.setBackgroundColor(getResources().getColor(R.color.white));
                tvTitle.setTextColor(getResources().getColor(R.color.black_title));
                ivTitleleft.setVisibility(View.GONE);
                ivTitleleft2.setVisibility(View.VISIBLE);
                llCheck.setVisibility(View.GONE);
                llCheckresult.setVisibility(View.VISIBLE);

                mlistdata.clear();
                if(result!=null)
                  mlistdata.addAll(result);
                resAdapter.setData(mlistdata);

                resAdapter.notifyDataSetChanged();

                if (result == null || result.size() == 0) {
                    tvResultdescTitle.setText("正常");
                    tvResultdescTitle.setTextColor(getResources().getColor(R.color.app_color));
                    ivResultdescError.setVisibility(View.GONE);
                    tvResultdescDetail.setText("目前车况良好,建议每周检查一次");
                } else {
                    tvResultdescTitle.setText("异常");
                    tvResultdescTitle.setTextColor(getResources().getColor(R.color.red));
                    ivResultdescError.setVisibility(View.VISIBLE);
                    tvResultdescDetail.setText("车辆有异常情况,请根据检查结果进行修复,请注意安全驾驶!");

                }
            }

        } else {

            MLog.e("show", "" + checkFinish);

            ll_root.setBackgroundColor(getResources().getColor(R.color.black));
            llTitle.setBackgroundColor(getResources().getColor(R.color.black));

            llCheck.setVisibility(View.VISIBLE);
            llCheckresult.setVisibility(View.GONE);

            tvRecheck.setVisibility(View.INVISIBLE);
            tvProgress.setText("0%");
            tvProgress.setVisibility(View.VISIBLE);
            tvState.setText("正在检测...");

            // 开始自动检测，动画
        }

        ivScan.clearAnimation();
        ivScan.invalidate();
        ivScan.setVisibility(View.GONE);

    }

    protected void showProgressBar() {
        if (pbWaiting != null)
            pbWaiting.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        if (pbWaiting != null)
            pbWaiting.setVisibility(View.GONE);
    }

    int currentProgress = 0;
    int progressStep = 1;
    Message progressMsg;
    static final int Flag_Progress = 1234;

    Handler mhandler = new Handler() {

        public void handleMessage(Message msg) {

            if (isFinishing())
                return;

            switch (msg.what) {
                case Flag_Progress:

                    currentProgress += progressStep;

                    // if (currentProgress > 100)
                    // currentProgress = 100;

                    if (currentProgress >= 100) {

                        if (CheckFinish) {
                            // tvProgress.setVisibility(View.VISIBLE);
                            tvProgress.setText("100%");

                            stopTimeTask();

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {


                                    TransState(true, mresult);
                                    StopAnim();
                                }
                            }, 300);

                        } else {
                            // tvProgress.setVisibility(View.VISIBLE);

                            if (currentProgress >= 600) {

                                cancelRequest();

                                Toast.makeText(CarCheckActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
                                tvProgress.setText("100%");
                                stopTimeTask();
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {


                                        TransState(true, mresult);
                                        StopAnim();
                                    }
                                }, 300);

                            } else
                                tvProgress.setText("99%");

                        }

                    } else {
                        tvProgress.setText(currentProgress + "%");
                    }

                    break;
            }
            super.handleMessage(msg);
        }

    };

    /**
     * 启动定时器
     */
    private void startTimeTask() {
        stopTimeTask();
        currentProgress = 0;
        progressStep = 1;

        StartAnim();

        getTimer().schedule(new TimerTask() {
            @Override
            public void run() {

                mhandler.sendEmptyMessage(Flag_Progress);

            }
        }, 0, 50);
    }

    public void ShowResult() {

    }

    /**
     * 关闭定时器
     */
    private void stopTimeTask() {

        // ivScan.setVisibility(View.GONE);

        if (mTimer != null) {

            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    public synchronized Timer getTimer() {

        if (mTimer == null)
            mTimer = new Timer();

        return mTimer;

    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    public void cancelRequest() {

        if (!TextUtils.isEmpty(requestTag)) {

            CldHttpClient.cancelRequest(requestTag);

        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (checkAnimation != null)
            checkAnimation.cancel();

    }

    public void StartAnim() {

        initAnimation();

        if (ivScan == null)
            return;

        ivScan.setAnimation(checkAnimation);

        if (checkAnimation != null) {
            // checkAnimation.reset();
            checkAnimation.start();
        }

    }

    public void StopAnim() {

        if (checkAnimation != null) {
            checkAnimation.cancel();
        }

        ivScan.clearAnimation();
        ivScan.invalidate();

        // mhandler.postDelayed(new Runnable() {
        //
        // @Override
        // public void run() {
        // // ivScan.setVisibility(View.GONE);TODO Auto-generated method stub
        // ivScan.setVisibility(View.GONE);
        // }
        // }, 300);

    }

    public void initAnimation() {

        if (checkAnimation == null) {

            WindowManager wm = (WindowManager) CarCheckActivity.this.getSystemService(Context.WINDOW_SERVICE);

            int height = (int) (wm.getDefaultDisplay().getWidth() * 0.6 * 1.5);
            int heightorigin = (int) (wm.getDefaultDisplay().getWidth() * 0.6 * 0.2);

            checkAnimation = new TranslateAnimation(0, 0, -heightorigin, height);
            checkAnimation.setDuration(2000);
            checkAnimation.setRepeatCount(Animation.INFINITE);// 动画的重复次数
            checkAnimation.setFillAfter(false);

            checkAnimation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                    if (ivScan != null)
                        ivScan.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {


                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    if (ivScan != null)
                        ivScan.setVisibility(View.GONE);
                }
            });
        }

    }

}
