package com.yunbaba.freighthelper.ui.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.DigitsUtil;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.zhy.android.percent.support.PercentLinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskDialoginNavigation extends Dialog {

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

	@BindView(R.id.tv_hint_sp7)
	public TextView tvHintSp7;
	@BindView(R.id.pll_sp)
	public PercentLinearLayout pllSp;
	@BindView(R.id.tv_left)
	public TextView tvLeft;
	@BindView(R.id.tv_right)
	public TextView tvRight;

	public TaskDialoginNavigation(Context context) {
		super(context, R.style.Translucent_NoTitle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_task_in_navigation);
		setCanceledOnTouchOutside(false);
		ButterKnife.bind(this);
		//setDialogType(NavigationDlgType.EXIT_NAVIGATION);
	}

	/**
	 * 
	 * type 0 为默认 最常用简单的模式 1 为暂停任务 2 为结束任务 3 开始新任务，需要暂停当前任务 4开始导航 5 最后一个运货点暂停运货
	 * 6非最后一个运货点暂停运货
	 * 
	 */
	public void setDialogType(int type, MtqDeliStoreDetail storeDetail) {

		tvHintCenter.setVisibility(View.GONE);

		switch (type) {
		case NavigationDlgType.EXIT_NAVIGATION:

			if (storeDetail.optype == 4) {
				tvLeft.setText("暂停回程");
			} else {
				tvLeft.setText(R.string.taskpoint_stop);
			}
			tvRight.setText(R.string.close_navigate);
			break;

		case NavigationDlgType.ARRIVE_DEST:
			
			if (storeDetail.optype == 4) {
				tvLeft.setText("暂停回程");
				tvRight.setText("结束回程");
			} else {
				tvLeft.setText(R.string.taskpoint_stop);
				tvRight.setText(R.string.taskpoint_arrvied);
			}
			
			break;
		}
	}

	/**
	 * 
	 * type 0 为默认 最常用简单的模式 1 为暂停任务 2 为结束任务 3 开始新任务，需要暂停当前任务
	 * 
	 */
	public void setSpText(MtqDeliTask task, MtqDeliStoreDetail storeDetail, MtqDeliOrderDetail orderDetail, int type) {

		pllSp.setVisibility(View.VISIBLE);

		if (storeDetail.optype == 4) {
			tvTitle.setText("正在回程");
		} else
			tvTitle.setText(FreightLogicTool.getDeliverStatus(this.getContext(),
					this.getContext().getString(R.string.task_in_freight), task.finish_count, task.store_count));

		CharSequence storeName;

		storeName = storeDetail.storesort + "."
				+ (TextUtils.isEmpty(storeDetail.storecode) ? "" : (storeDetail.storecode + "-"))
				+ storeDetail.storename;

		if (storeDetail.optype == 4)
			storeName = FreightLogicTool.getStoreNameAddBackPic(this.getContext(), storeName);

		tvHintSp.setText(storeName);

		tvHintSp2.setText(FreightLogicTool.getDialogAddressStr(storeDetail.storeaddr));

		tvHintSp3.setText(FreightLogicTool.getDialogKcodeStr(storeDetail.storex, storeDetail.storey));

		tvHintSp4.setText(FreightLogicTool.getDialogNameAndPhoneStr(storeDetail.linkman, storeDetail.linkphone));

		tvHintSp5.setText(FreightLogicTool.getMoney(DigitsUtil.getPrettyNumber(storeDetail.real_amount)));

		if (orderDetail != null) {
			tvHintSp6.setText(FreightLogicTool.getPS(orderDetail.reqtime_e));
		}

		int nofinishcnt = task.store_count - task.finish_count;
		tvHintSp7.setText("还有" + nofinishcnt + "个未完成的路由点");

	}

	public void setSpText2(MtqDeliStoreDetail task, int index) {

		tvHintSp4.setVisibility(View.VISIBLE);
		tvHintSp5.setVisibility(View.VISIBLE);
		tvHintSp6.setVisibility(View.VISIBLE);

		tvHintSp4.setText(task.storename);

		tvHintSp5.setText(FreightLogicTool.getDialogAddressStr(task.storeaddr));

		tvHintSp6.setText(FreightLogicTool.getDialogNameAndPhoneStr(task.linkman, task.linkphone));

	}

	public static class NavigationDlgType {
		/** 退出导航 **/
		public static final int EXIT_NAVIGATION = 1;

		/** 到达目的地 **/
		public static final int ARRIVE_DEST = 2;
	}

}
