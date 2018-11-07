package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.UpdateTaskStatusEvent;
import com.yunbaba.freighthelper.ui.activity.task.FreightPointActivity;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TimestampTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListAdapter extends BaseAdapter implements Filterable {

	ArrayList<MtqDeliTask> tasklist;
	ArrayList<MtqDeliTask> backData;// 用来备份原始数据
	Context mContext;
	// Handler mHandler;

	public TaskListAdapter(Context mContext, ArrayList<MtqDeliTask> tasklist) { // ,
																				// Handler
																				// mHandler
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.tasklist = tasklist;
		// this.mHandler = mHandler;
	}

	@Override
	public int getCount() {


		return tasklist == null ? 0 : tasklist.size();
	}

	@Override
	public MtqDeliTask getItem(int position) {

		MtqDeliTask item = null;

		if (null != tasklist) {
			item = tasklist.get(position);
		}

		return item;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		ViewHolder holder;
		if (view != null) {
			holder = (ViewHolder) view.getTag();
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_tasklist, parent, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		}





		final MtqDeliTask item = getItem(position);




		CharSequence route = FreightLogicTool.getRouteStr(mContext, item.freight_type, item.preceipt,item.pdeliver);

		if (item.isback == 1)
			route = FreightLogicTool.getStoreNameAddBackPic(mContext, route);

		holder.tv_route.setText(route);

		if (item.status == 2) {
			holder.tv_time.setText(TimestampTool.getTimeDescriptionFromTimestamp(mContext, item.departtime * 1000L)
					+ mContext.getString(R.string.issued));
			holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.black_text));

		} else {
			holder.tv_time.setText(FreightLogicTool.getTimeStrForTaskList(mContext,
					TimestampTool.getTimeDescriptionFromTimestamp(mContext, item.departtime * 1000L), item.departtime)); // item.sendtime
		} // *
			// 1000L)));

		holder.tv_progress.setText(FreightLogicTool.getProgressStr(mContext, item.finish_count, item.store_count));

		holder.tv_totalkm.setText(
				FreightLogicTool.getDistanceStr(mContext, ((item.distance / 1000) + mContext.getString(R.string.km)))); //

		holder.tv_good.setText(FreightLogicTool.getGoodInfoStr(mContext, (item.gweight
				+ mContext.getString(R.string.ton) + "、" + item.gvolume + mContext.getString(R.string.square))));

		holder.tv_company.setText(item.corpname);

		if (!TextUtils.isEmpty(item.carlicense)) {
			holder.tv_carlicense.setText(item.carlicense);
		}

		// tv_stoptask.setText(item.gweight+","+item.gvolume);

		if (item.status == 0) {

			if (item.isback == 1 && (item.finish_count == item.store_count - 1) && TaskUtils.isFinalUnFinishPointIsReturnPoint(mContext,item.taskid)) // 有回程点
			{

				holder.tv_right.setText("开始回程");

			} else
				holder.tv_right.setText(R.string.task_start);

		} else if (item.status == 3) {

			if (item.isback == 1 && (item.finish_count == item.store_count - 1)&& TaskUtils.isFinalUnFinishPointIsReturnPoint(mContext,item.taskid)) // 有回程点
			{
				holder.tv_right.setText("继续回程");
			} else
				holder.tv_right.setText(R.string.task_resume);

		} else {

			if (item.finish_count == 0){
				if (item.isback == 1 && (item.finish_count == item.store_count - 1)&& TaskUtils.isFinalUnFinishPointIsReturnPoint(mContext,item.taskid)) // 有回程点
				{
					holder.tv_right.setText("开始回程");
				} else
					holder.tv_right.setText(R.string.task_start);
			}
			else{
				if (item.isback == 1 && (item.finish_count == item.store_count - 1)&& TaskUtils.isFinalUnFinishPointIsReturnPoint(mContext,item.taskid)) // 有回程点
				{
					holder.tv_right.setText("继续回程");
				} else
					holder.tv_right.setText(R.string.task_resume);
				
			}

		}
		holder.pll_tasklist_item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mContext, FreightPointActivity.class);
				intent.putExtra("corpid", item.corpid);
				intent.putExtra("taskid", item.taskid);

				mContext.startActivity(intent);
			}
		});

		/**
		 * 运货状态【0待运货1运货中2已完成3暂停状态 4-中止运货】
		 */
		if (item.status == 2)
			// holder.rll_tasklist_item_modify.setVisibility(View.GONE);
			holder.tv_right.setVisibility(View.GONE);
		else {
			// holder.rll_tasklist_item_modify.setVisibility(View.VISIBLE);
			holder.tv_right.setVisibility(View.VISIBLE);
			holder.tv_right.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {


					if (item.status == 3 || item.status == 0)
						EventBus.getDefault().post(new UpdateTaskStatusEvent(item.corpid, item.taskid, 1, item.corpid,
								item.taskid, 0, 0, 0, 0));

				}
			});

			// holder.tv_left.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			//
			// // Message msg = new Mes
			// // mHandler.sendMessage(msg)
			// EventBus.getDefault().post(new UpdateTaskStatusEvent(item.corpid,
			// item.taskid, 2, item.corpid,
			// item.taskid, 0, 0, 0, 0));
			//
			// }
			// });

		}

		return view;
	}

	static class ViewHolder {

		@BindView(R.id.tv_time)
		TextView tv_time;

		@BindView(R.id.tv_progress)
		TextView tv_progress;

		@BindView(R.id.tv_totalkm)
		TextView tv_totalkm;

		@BindView(R.id.tv_good)
		TextView tv_good;

		@BindView(R.id.tv_company)
		TextView tv_company;

		@BindView(R.id.tv_route)
		TextView tv_route;

		// @BindView(R.id.tv_left)
		// TextView tv_left;

		@BindView(R.id.tv_carlicense)
		TextView tv_carlicense;

		@BindView(R.id.tv_right)
		TextView tv_right;

		// @BindView(R.id.rll_tasklist_item_modify)
		// LinearLayout rll_tasklist_item_modify;

		@BindView(R.id.pll_tasklist_item)
		LinearLayout pll_tasklist_item;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public Filter getFilter() {

		return null;
	}

	public void setList(ArrayList<MtqDeliTask> waitingList) {

		this.tasklist = waitingList;
	}

}
