package com.yunbaba.freighthelper.ui.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.zhy.android.percent.support.PercentLinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAskPopUpDialog extends Dialog {

    @BindView(R.id.tv_title)
    public TextView tvTitle;
    @BindView(R.id.tv_hint_center)
    public TextView tvHintCenter;
    @BindView(R.id.tv_hint_sp)
    public TextView tvHintSp;
    @BindView(R.id.tv_hint_sp2)
    public TextView tvHintSp2;
    @BindView(R.id.tv_hint_sp3)
    public TextView tvHintSp3;

    @BindView(R.id.tv_hint_sp4)
    public TextView tvHintSp4;
    @BindView(R.id.tv_hint_sp5)
    public TextView tvHintSp5;
    @BindView(R.id.tv_hint_sp6)
    public TextView tvHintSp6;
    @BindView(R.id.pll_sp)
    public PercentLinearLayout pllSp;
    @BindView(R.id.tv_left)
    public TextView tvLeft;
    @BindView(R.id.tv_right)
    public TextView tvRight;

    public TaskAskPopUpDialog(Context context) {
        super(context, R.style.Translucent_NoTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_task_ask);
        setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);

    }

    /**
     * type 0 为默认 最常用简单的模式 1 为暂停任务 2 为结束任务 3 开始新任务，需要暂停当前任务 4开始导航 5 最后一个运货点暂停运货
     * 6非最后一个运货点暂停运货
     */
    public void setDialogType(int type) {

        switch (type) {
            case 1:
                tvTitle.setText(R.string.task_stop_title);
                tvHintCenter.setVisibility(View.VISIBLE);
                tvHintCenter.setText(R.string.task_stop_hint);

                tvLeft.setText(R.string.cancel);
                tvRight.setText(R.string.confirm);
                pllSp.setVisibility(View.GONE);
                break;
            case 2:
                tvTitle.setText(R.string.task_finish_title);
                tvHintCenter.setVisibility(View.VISIBLE);
                tvHintCenter.setText(R.string.task_finish_hint);
                // pllSp.setVisibility(View.GONE);

                tvLeft.setText(R.string.cancel);
                tvRight.setText(R.string.confirm);
                pllSp.setVisibility(View.GONE);

                break;
            case 3:
                tvTitle.setText(R.string.task_start_stop_title);
                tvHintCenter.setVisibility(View.GONE);

                tvLeft.setText(R.string.cancel);
                tvRight.setText(R.string.resume);
                pllSp.setVisibility(View.GONE);
                break;

            case 4:
                tvTitle.setText(R.string.need_navigation);
                tvHintCenter.setVisibility(View.VISIBLE);
                tvHintCenter.setText(R.string.need_navigation_hint);

                tvLeft.setText(R.string.no_use);
                tvRight.setText(R.string.navigation);
                pllSp.setVisibility(View.GONE);
                break;

            case 5:
                tvTitle.setText(R.string.taskpoint_stop_title);
                tvHintCenter.setVisibility(View.VISIBLE);
                tvHintCenter.setText(R.string.taskpoint_stop_hint);

                tvLeft.setText(R.string.close);
                tvRight.setText(R.string.taskpoint_stop);
                pllSp.setVisibility(View.GONE);
                break;

            case 6:
                tvTitle.setText(R.string.taskpoint_stop_title);
                tvHintCenter.setVisibility(View.GONE);

                tvLeft.setText(R.string.cancel);
                tvRight.setText(R.string.confirm);
                pllSp.setVisibility(View.GONE);
                break;
            case 7:
                tvTitle.setText("您调整了送货顺序，是否继续?");
                tvHintCenter.setVisibility(View.GONE);

                tvLeft.setText(R.string.cancel);
                tvRight.setText(R.string.resume);
                pllSp.setVisibility(View.GONE);
                break;

            case 8:
                tvTitle.setText("尚未提交信息，确定放弃吗?");
                tvHintCenter.setVisibility(View.GONE);

                tvLeft.setText(R.string.cancel);
                tvRight.setText(R.string.confirm);
                pllSp.setVisibility(View.GONE);
                break;
            case 9:
                tvTitle.setText("该页面有消息更新，是否进行更新?");
                tvHintCenter.setVisibility(View.GONE);

                tvLeft.setText(R.string.cancel);
                tvRight.setText("更新");
                pllSp.setVisibility(View.GONE);
                break;

            case 10:
                tvTitle.setText("您是否已在此门店?");
                tvHintCenter.setVisibility(View.GONE);

                tvLeft.setText("否");
                tvRight.setText("是");
                pllSp.setVisibility(View.GONE);
                break;
            case 11:
                tvTitle.setText("确认退出导航?");
                tvHintCenter.setVisibility(View.GONE);

                tvLeft.setText("取消");
                tvRight.setText("确认");
                pllSp.setVisibility(View.GONE);
                break;
            default:
                // tvTitle.setText(R.string.task_stop_title);
                // tvHintCenter.setText(R.string.task_stop_hint);
                tvLeft.setText(R.string.cancel);
                tvRight.setText(R.string.confirm);
                pllSp.setVisibility(View.GONE);

                break;
        }

    }

    /**
     * type 0 为默认 最常用简单的模式 1 为暂停任务 2 为结束任务 3 开始新任务，需要暂停当前任务
     */
    public void setSpText(MtqDeliTask task, int type) {

        switch (type) {
            case 2:
                pllSp.setVisibility(View.VISIBLE);
                tvHintSp.setText(R.string.task_finish_sp1);
                tvHintSp2.setText(FreightLogicTool.getFinishHintRouteStr(this.getContext(), task.pdeliver, task.preceipt,
                        task.freight_type, task.finish_count, task.store_count));
                tvHintSp3.setText(FreightLogicTool.getFinishHintTimeStr(this.getContext(), task.corpname, task.sendtime));

                tvHintSp4.setVisibility(View.GONE);
                tvHintSp5.setVisibility(View.GONE);
                tvHintSp6.setVisibility(View.GONE);

                break;
            case 3:
                pllSp.setVisibility(View.VISIBLE);
                tvHintSp.setText(R.string.task_start_stop_sp1);
                tvHintSp2.setText(FreightLogicTool.getFinishHintRouteStr(this.getContext(), task.pdeliver, task.preceipt,
                        task.freight_type, task.finish_count, task.store_count));
                tvHintSp3.setText(FreightLogicTool.getFinishHintTimeStr(this.getContext(), task.corpname, task.sendtime));

                tvHintSp4.setVisibility(View.GONE);
                tvHintSp5.setVisibility(View.GONE);
                tvHintSp6.setVisibility(View.GONE);

                break;
            default:
                break;
        }

    }

    public void setSpText2(MtqDeliStoreDetail task, int index) {

        tvHintSp4.setVisibility(View.VISIBLE);
        tvHintSp5.setVisibility(View.VISIBLE);
        tvHintSp6.setVisibility(View.VISIBLE);

        tvHintSp4.setText(task.storename);

        tvHintSp5.setText(
                FreightLogicTool.getDialogAddressStr((task.regionname + task.storeaddr).replaceAll("\\s*", "")));

        if (TextUtils.isEmpty(task.linkphone) && TextUtils.isEmpty(task.linkman)) {
            tvHintSp6.setVisibility(View.GONE);
        } else {
            tvHintSp6.setVisibility(View.VISIBLE);
            String phone = TextUtils.isEmpty(task.linkphone) ? "无" : task.linkphone;
            String man = TextUtils.isEmpty(task.linkman) ? "无" : task.linkman;

            tvHintSp6.setText(FreightLogicTool.getDialogNameAndPhoneStr(man, phone));
        }
        // pllSp.setVisibility(View.VISIBLE);
        // tvHintSp.setText(R.string.task_finish_sp1);
        // tvHintSp2.setText(FreightLogicTool.getFinishHintRouteStr(this.getContext(),
        // task.pdeliver, task.preceipt,
        // task.freight_type, task.finish_count, task.store_count));
        // tvHintSp3.setText(FreightLogicTool.getFinishHintTimeStr(this.getContext(),
        // task.corpname, task.sendtime));
        //
        //
        // tvHintSp4.setVisibility(View.GONE);
        // tvHintSp5.setVisibility(View.GONE);
        // tvHintSp6.setVisibility(View.GONE);
        //
        // break;
        // case 3:
        // pllSp.setVisibility(View.VISIBLE);
        // tvHintSp.setText(R.string.task_start_stop_sp1);
        // tvHintSp2.setText(FreightLogicTool.getFinishHintRouteStr(this.getContext(),
        // task.pdeliver, task.preceipt,
        // task.freight_type, task.finish_count, task.store_count));
        // tvHintSp3.setText(FreightLogicTool.getFinishHintTimeStr(this.getContext(),
        // task.corpname, task.sendtime));

    }

}
