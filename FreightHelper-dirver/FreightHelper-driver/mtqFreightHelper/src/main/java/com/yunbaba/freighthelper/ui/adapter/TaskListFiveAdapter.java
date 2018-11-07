package com.yunbaba.freighthelper.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.ui.activity.task.FreightPointActivity;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TimestampTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliTask;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListFiveAdapter extends BaseAdapter implements Filterable {

	private List<MtqDeliTask> tasklist;
	private Context mContext;



	//增加对搜索的兼容
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



	public TaskListFiveAdapter(Context mContext, List<MtqDeliTask> tasklist) {
		this.mContext = mContext;
		this.tasklist = tasklist;
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
		final MtqDeliTask item = getItem(position);
		ViewHolder holder;
		if (view != null) {
			holder = (ViewHolder) view.getTag();
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_tasklist_five_period,
					parent, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		}

		Drawable taskStatusDrawable;
//		Drawable moreDrawable;
		Drawable departureDrawable;
		Drawable destinationDrawable;
		Drawable arrawDrawable;

		if (item.status == 1) {
			// 运输中
//			holder.ll_top.setBackgroundResource(R.drawable.layer_task_transport_top_bg);
//			taskStatusDrawable = mContext.getResources().getDrawable(
//					R.drawable.task_transport_icon);
////			moreDrawable = mContext.getResources().getDrawable(
////					R.drawable.task_transport_more);
//			departureDrawable = mContext.getResources().getDrawable(
//					R.drawable.task_position);
//			destinationDrawable = mContext.getResources().getDrawable(
//					R.drawable.task_position);
//			arrawDrawable = mContext.getResources().getDrawable(
//					R.drawable.task_transport_arraw);

//			holder.tv_task_status.setTextColor(mContext.getResources().getColor(
//					R.color.tasklist_transport_text_color));
//			holder.tv_task_status.setText(mContext.getString(R.string.task_transport));


			holder.iv_task_status.setImageResource(R.drawable.icon_task_transport);



//			holder.tv_progress.setTextColor(mContext.getResources().getColor(
//					R.color.tasklist_transport_text_color));

//			holder.tv_departure_name.setTextColor(mContext.getResources().getColor(
//					R.color.white));
//			holder.tv_departure.setTextColor(mContext.getResources().getColor(
//					R.color.tasklist_transport_text_color));
//
//			holder.tv_destination_name.setTextColor(mContext.getResources().getColor(
//					R.color.white));
//			holder.tv_destination.setTextColor(mContext.getResources().getColor(
//					R.color.tasklist_transport_text_color));
		} else {
			// 等待运输
//			holder.ll_top.setBackgroundResource(R.drawable.layer_task_wait_top_bg);
//			taskStatusDrawable = mContext.getResources().getDrawable(
//					R.drawable.task_wait_icon);
////			moreDrawable = mContext.getResources().getDrawable(
////					R.drawable.task_wait_more);
//			departureDrawable = mContext.getResources().getDrawable(
//					R.drawable.task_destination);
//			destinationDrawable = mContext.getResources().getDrawable(
//					R.drawable.task_wareroom);
//			arrawDrawable = mContext.getResources().getDrawable(
//					R.drawable.task_wait_arraw);

//			holder.iv_task_status.setTextColor(mContext.getResources().getColor(
//					R.color.tasklist_text_color));
//
//			holder.iv_task_status.setText(mContext.getString(R.string.task_wait));


			holder.iv_task_status.setImageResource(R.drawable.icon_task_waiting);


			if (item.status == 2) {
				// 已完成
//				holder.tv_task_status.setText(mContext.getString(R.string.task_finished));
//				taskStatusDrawable = mContext.getResources().getDrawable(
//						R.drawable.task_finish_icon);

				holder.iv_task_status.setImageResource(R.drawable.icon_task_complete);



			}
//			holder.tv_progress.setTextColor(mContext.getResources().getColor(
//					R.color.app_color));
//
//			holder.tv_departure_name.setTextColor(mContext.getResources().getColor(
//					R.color.black_text));
//			holder.tv_departure.setTextColor(mContext.getResources().getColor(
//					R.color.tasklist_text_color));
//
//			holder.tv_destination_name.setTextColor(mContext.getResources().getColor(
//					R.color.black_text));
//			holder.tv_destination.setTextColor(mContext.getResources().getColor(
//					R.color.tasklist_text_color));
		}

		if (item.freight_type == 1) {
			// 送货: 提货点-->路由城市
			holder.tv_departure.setText(mContext.getString(R.string.departure));
			holder.tv_destination.setText(mContext.getString(R.string.destination));
			//holder.tv_departure_name.setText(item.pdeliver);
			holder.tv_departure_name.setText(TaskUtils.interceptStoreName(item.pdeliver));
			holder.tv_destination_name.setText(TaskUtils.parseDistrictCode(item.district_code));
		} else {
			// 其它都是提货: 路由城市-->集货点
			holder.tv_departure.setText(mContext.getString(R.string.destination));
			holder.tv_destination.setText(mContext.getString(R.string.distribution));
			holder.tv_departure_name.setText(TaskUtils.parseDistrictCode(item.district_code));
			//holder.tv_destination_name.setText(item.preceipt);
			holder.tv_destination_name.setText(TaskUtils.interceptStoreName(item.preceipt));
		}

//		taskStatusDrawable.setBounds(0, 0, taskStatusDrawable.getIntrinsicWidth() * 2/3,
//				taskStatusDrawable.getIntrinsicHeight()* 2/3);

//		holder.iv_task_status.setCompoundDrawables(taskStatusDrawable, null, null, null);

//		moreDrawable.setBounds(0, 0, moreDrawable.getIntrinsicWidth() * 2/3,
//				moreDrawable.getIntrinsicHeight()* 2/3);
//		holder.tv_more.setCompoundDrawables(null, null, moreDrawable, null);

//		holder.tv_more.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(mContext, FreightPointActivity.class);
//				intent.putExtra("corpid", item.corpid);
//				intent.putExtra("taskid", item.taskid);
//				mContext.startActivity(intent);
//			}
//		});

//		departureDrawable.setBounds(0, 0, departureDrawable.getIntrinsicWidth(),
//				departureDrawable.getIntrinsicHeight());
//		holder.tv_departure.setCompoundDrawables(departureDrawable, null, null, null);
//		holder.iv_arraw.setImageDrawable(arrawDrawable);
//
//		destinationDrawable.setBounds(0, 0, destinationDrawable.getIntrinsicWidth(),
//				destinationDrawable.getIntrinsicHeight());
//		holder.tv_destination.setCompoundDrawables(destinationDrawable, null, null, null);

		// 完成进度
		holder.tv_progress.setText(item.finish_count + "/" + item.store_count);

		// 发车时间
		String departtime = TimestampTool.getTimeDescriptionFromTimestamp(mContext, item.departtime * 1000L);
		holder.tv_time.setText(FreightLogicTool.getDepartTimeStr(mContext, departtime));

		// 件数
		String piece_num = item.gamount + mContext.getString(R.string.piece);
		holder.tv_piece_num.setText(FreightLogicTool.getGoodPieceNumStr(mContext, piece_num));

		// 箱数
		String box_num = item.gframecount + mContext.getString(R.string.box);
		holder.tv_box_num.setText(FreightLogicTool.getGoodBoxNumStr(mContext, box_num));

		// 体积
		String volume = TaskUtils.keepADecimal(item.gvolume) + mContext.getString(R.string.square);
		holder.tv_volume.setText(FreightLogicTool.getGoodVolumeStr(mContext, volume));

		// 重量
		String weight  = TaskUtils.keepADecimal(item.gweight) + mContext.getString(R.string.ton);
		holder.tv_weight.setText(FreightLogicTool.getGoodWeightStr(mContext, weight));

		holder.ll_top.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AppStatApi.statOnEventWithLoginName(mContext, AppStatApi.BD_STAT_TASK_ITEM_CLICK);

				Intent intent = new Intent(mContext, FreightPointActivity.class);
				intent.putExtra("corpid", item.corpid);
				intent.putExtra("taskid", item.taskid);
				mContext.startActivity(intent);
			}
		});

		holder.ll_bottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

                AppStatApi.statOnEventWithLoginName(mContext, AppStatApi.BD_STAT_TASK_ITEM_CLICK);

				Intent intent = new Intent(mContext, FreightPointActivity.class);
				intent.putExtra("corpid", item.corpid);
				intent.putExtra("taskid", item.taskid);
				mContext.startActivity(intent);
			}
		});

		holder.tv_taskid.setText(item.taskid);
		return view;
	}

	static class ViewHolder {

		@BindView(R.id.ll_top)
		PercentLinearLayout ll_top;

		@BindView(R.id.ll_bottom)
		PercentLinearLayout ll_bottom;

		@BindView(R.id.iv_task_status)
		ImageView iv_task_status;


		@BindView(R.id.tv_taskid)
		TextView tv_taskid;

		@BindView(R.id.tv_progress)
		TextView tv_progress;

//		@BindView(R.id.tv_more)
//		TextView tv_more;

		@BindView(R.id.tv_departure_name)
		TextView tv_departure_name;

		@BindView(R.id.iv_arraw)
		ImageView iv_arraw;

		@BindView(R.id.tv_destination_name)
		TextView tv_destination_name;

		@BindView(R.id.tv_departure)
		TextView tv_departure;

		@BindView(R.id.tv_destination)
		TextView tv_destination;

		@BindView(R.id.tv_time)
		TextView tv_time;

		@BindView(R.id.tv_piece_num)
		TextView tv_piece_num;

		@BindView(R.id.tv_box_num)
		TextView tv_box_num;

		@BindView(R.id.tv_volume)
		TextView tv_volume;

		@BindView(R.id.tv_weight)
		TextView tv_weight;

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

	public void setList(List<MtqDeliTask> waitingList) {
		this.tasklist = waitingList;
	}
}
