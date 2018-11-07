package com.yunbaba.freighthelper.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunbaba.api.trunk.FreightPointDeal;
import com.yunbaba.api.trunk.TaskOperator;
import com.yunbaba.api.trunk.bean.OnUIResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.eventbus.GetLocationAddrEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.ui.activity.RoutePreviewActivity;
import com.yunbaba.freighthelper.ui.activity.task.FreightPointActivity;
import com.yunbaba.freighthelper.ui.activity.task.TaskPointDetailActivity;
import com.yunbaba.freighthelper.ui.activity.task.UploadPaymentActivity;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.OnCallBack;
import com.yunbaba.freighthelper.utils.SPHelper2;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TimestampTool;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

public class FreightPointAdapter extends BaseAdapter implements OnUIResult {
	private Context mContext;

	private ArrayList<MtqDeliStoreDetail> mStore;
	private HashMap<Integer, MtqDeliOrderDetail> mOrder;

	private int mSelectIndex;
	private boolean isNeedJump = false; // 是否要跳转界面。

	private int mJumpType = 0; // 0 不跳转，1跳到详情 2 跳到收款
    private int tasktype = 1; // 1 送货  2提货
	private Runnable mRunnable = null;

	public FreightPointAdapter(Context cxt) {
		mContext = cxt;
		mStore = FreightPointDeal.getInstace().getmStore();
		mOrder = FreightPointDeal.getInstace().getmOrder();


		if(FreightPointDeal.getInstace().getmMtqDeliTaskDetail()!=null){


			tasktype = FreightPointDeal.getInstace().getmMtqDeliTaskDetail().freight_type;
			//MLog.e("freighttype","notnull"+tasktype);
		}

		FreightPointDeal.getInstace().setmCallBack(this);
	}

	public void setmStore(ArrayList<MtqDeliStoreDetail> mStore) {
		this.mStore = mStore;
	}

	@Override
	public boolean isEnabled(int position) {

		return true;
	}

	@Override
	public int getCount() {

		if (mStore != null) {
			return mStore.size();
		}
		return 0;
	}

	@Override
	public MtqDeliStoreDetail getItem(int position) {

		if (mStore != null) {
			return mStore.get(position);
		}else
			return  null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_freight_point_item, null);
			viewHolder.address = (TextView) convertView.findViewById(R.id.id_freight_point_address);
			viewHolder.name = (TextView) convertView.findViewById(R.id.id_freight_point_name);
			viewHolder.status = (TextView) convertView.findViewById(R.id.id_freight_point_status);
			viewHolder.time = (TextView) convertView.findViewById(R.id.id_freight_point_time);
			viewHolder.timeStatus = (TextView) convertView.findViewById(R.id.freight_time_status);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.freight_point_type_pic);
			viewHolder.operateText = (TextView) convertView.findViewById(R.id.freight_operation_text);
			viewHolder.operateText1 = (TextView) convertView.findViewById(R.id.freight_operation_text1);
			viewHolder.operateText2 = (TextView) convertView.findViewById(R.id.freight_operation_text2);
			viewHolder.operateImg = (ImageView) convertView.findViewById(R.id.freight_point_operation_pic);
			viewHolder.operateImg1 = (ImageView) convertView.findViewById(R.id.freight_point_operation_pic1);
			viewHolder.operateImg2 = (ImageView) convertView.findViewById(R.id.freight_point_operation_pic2);
			viewHolder.operate = (LinearLayout) convertView.findViewById(R.id.freight_operation);
			viewHolder.operate1 = (RelativeLayout) convertView.findViewById(R.id.freight_operation1);
			viewHolder.operate2 = (RelativeLayout) convertView.findViewById(R.id.freight_operation2);
			viewHolder.locate = (TextView)convertView.findViewById(R.id.btn_getposition2);
			viewHolder.locate2  = (LinearLayout) convertView.findViewById(R.id.btn_getposition);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final MtqDeliStoreDetail storeDetail = mStore.get(position);

		// order 可能为空
		MtqDeliOrderDetail orderDetail = mOrder.get(storeDetail.storesort);


		viewHolder.storeDetail = storeDetail;
		viewHolder.index = storeDetail.storesort;
		// if (storeDetail.storex == 0 && storeDetail.storey == 0) {
		// holder.address.setText(FreightLogicTool
		// .getStoreNameNoPosition((storeDetail.regionname +
		// storeDetail.storeaddr).replaceAll("\\s*", "")));
		// } else
		// holder.address.setText((storeDetail.regionname +
		// storeDetail.storeaddr).replaceAll("\\s*", ""));

		if (storeDetail.isUnknownAddress) {

			if (!TaskUtils.isStorePosUnknown(storeDetail)) {
				viewHolder.address.setText((storeDetail.storeaddr).replaceAll("\\s*", ""));
			} else
				viewHolder.address.setText(FreightLogicTool.getStoreNameNoPosition(
						(storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", "")));
		} else
			viewHolder.address.setText((storeDetail.regionname + storeDetail.storeaddr).replaceAll("\\s*", ""));

		viewHolder.name.setText(
				(position + 1) + "." + (TextUtils.isEmpty(storeDetail.storecode) ? "" : (storeDetail.storecode + "-"))
						+ storeDetail.storename);

		viewHolder.status
				.setText(FreightPointDeal.getInstace().getStoreStatusText(storeDetail.storestatus, storeDetail.optype));

		if (orderDetail != null) {
			viewHolder.time.setVisibility(View.VISIBLE);
			viewHolder.time.setText(TimestampTool.getStoreDetailTimeHint(orderDetail.reqtime_e * 1000L) + "前送达");
		} else {
			viewHolder.time.setVisibility(View.INVISIBLE);
		}

		viewHolder.timeStatus.setVisibility(View.INVISIBLE);

		if (orderDetail != null && orderDetail.expired > 0) {
			viewHolder.timeStatus.setVisibility(View.VISIBLE);
			viewHolder.timeStatus.setText("已过期");

		} else if (FreightPointDeal.getInstace().isOverTime(position)) {
			viewHolder.timeStatus.setVisibility(View.VISIBLE);
			viewHolder.timeStatus.setText("已超时");
		}

		viewHolder.operate2.setVisibility(View.INVISIBLE);

		setImgbyOptype(storeDetail.optype, viewHolder.image);

		if (orderDetail != null && orderDetail.expired > 0) {
			setViewbyStoreStatus(2, viewHolder);
		} else
			setViewbyStoreStatus(storeDetail.storestatus, viewHolder);

//storeDetail.storestatus != 2 &&

		viewHolder.locate.setVisibility(View.GONE);
		viewHolder.locate2.setVisibility(View.GONE);


//		if(storeDetail.storestatus !=2 &&((storeDetail.optype != 1 && tasktype ==2  &&storeDetail.optype!=4)||(tasktype == 1 && storeDetail.optype!=4 && storeDetail.optype!= 3))){
//
//			holder.locate.setVisibility(View.VISIBLE);
//			holder.locate2.setVisibility(View.VISIBLE);
//			if(SPHelper2.getInstance(mContext).readMarkStoreAddress(storeDetail.taskId+storeDetail.waybill)==null){
//
//				holder.locate.setText("获取位置点");
//				holder.locate.setBackground(mContext.getResources().getDrawable(R.drawable.round_background_orange));
//				holder.locate2.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//
//						if (!CommonTool.isFastDoubleClick()) {
//							EventBus.getDefault().post(new GetLocationAddrEvent(storeDetail));
//						}
//					}
//				});
//			}else {
//				holder.locate.setText("位置已获取");
//				holder.locate.setBackground(mContext.getResources().getDrawable(R.drawable.round_background_gray));
//				holder.locate2.setOnClickListener(null);
//			}
//
//		}else {
//			holder.locate.setVisibility(View.GONE);
//			holder.locate2.setVisibility(View.GONE);
//		}




		return convertView;
	}

	/**
	 * @Title: setImgbyOptype
	 * @Description: 根据类型设置图标
	 * @param optype
	 * @param image
	 * @return: void
	 */
	private void setImgbyOptype(int optype, ImageView image) {
		switch (optype) {
		case 1:
			// 送货
			image.setImageResource(R.drawable.ic_task_deliver);
			break;
		case 3:
			// 提货
			image.setImageResource(R.drawable.ic_task_pickgoods);
			break;
		case 4:
			// 回
			image.setImageResource(R.drawable.ic_task_back);
			break;
		case 5:
			// 必经点
			image.setImageResource(R.drawable.ic_task_passpoint);
			break;
		}
	}

	private void setViewbyStoreStatus(int storestatus, final ViewHolder viewHolder) {

		switch (storestatus) {
		case 0:
			// 等待运货

			if (viewHolder.storeDetail.optype == 4) {
				viewHolder.operateText.setText("开始回程");
				viewHolder.operateImg.setImageResource(R.drawable.btn_task_back);
			} else {
				viewHolder.operateText.setText("前往");
				viewHolder.operateImg.setImageResource(R.drawable.freight_point_startdeliver);
			}

			viewHolder.operate.setVisibility(View.VISIBLE);

			if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
				viewHolder.operate1.setVisibility(View.GONE);
			else
				viewHolder.operate1.setVisibility(View.VISIBLE);

			viewHolder.operateText1.setText("电话");
			viewHolder.operateImg1.setImageResource(R.drawable.freight_point_phone);

			viewHolder.operate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					FreightPointDeal.getInstace().cleanCallback();
					mSelectIndex = viewHolder.index;

					boolean isTaskNavi = GeneralSPHelper.getInstance(mContext.getApplicationContext())
							.isTaskNavi(viewHolder.storeDetail.taskId);

					if (!isTaskNavi) {
						jumpDetailActivity(viewHolder.index);
						jumpRoutePrewiewActivity(viewHolder.index);
						FreightPointDeal.getInstace().startOrContinueDeliver(viewHolder.storeDetail);
					} else {
						jumpDetailActivity(viewHolder.index);
						jumpRoutePrewiewActivity(viewHolder.index);
						FreightPointDeal.getInstace().startOrContinueDeliverAndNavigate(viewHolder.storeDetail);
					}
				}
			});

			viewHolder.operate1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					// 电话
					FreightPointDeal.getInstace().call(viewHolder.storeDetail.linkphone);
				}
			});

			viewHolder.operate2.setVisibility(View.GONE);

			break;
		case 1:
			// 正在配送中
			if (viewHolder.storeDetail.optype == 4) {
				viewHolder.operateText.setText("结束回程");
				viewHolder.operateImg.setImageResource(R.drawable.btn_task_back);
			} else {
				viewHolder.operateText.setText("完成");
				viewHolder.operateImg.setImageResource(R.drawable.freight_point_finish);
			}
			viewHolder.operate.setVisibility(View.VISIBLE);

			if (TaskUtils.isStorePosUnknown(viewHolder.storeDetail))
				viewHolder.operate1.setVisibility(View.GONE);
			else
				viewHolder.operate1.setVisibility(View.VISIBLE);

			viewHolder.operateText1.setText("导航");
			viewHolder.operateImg1.setImageResource(R.drawable.freight_point_navigtion);

			if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
				viewHolder.operate2.setVisibility(View.GONE);
			else
				viewHolder.operate2.setVisibility(View.VISIBLE);

			// holder.operate2.setVisibility(View.VISIBLE);
			viewHolder.operateText2.setText("电话");
			viewHolder.operateImg2.setImageResource(R.drawable.freight_point_phone);

			viewHolder.operate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// 完成运货
					mSelectIndex = viewHolder.index;

					mRunnable = new Runnable() {

						@Override
						public void run() {

							if (mOrder.get(viewHolder.index) != null) {
								// 在上传收款那边完成任务


								if((mStore.get(mSelectIndex).optype != 1 && tasktype ==2  &&mStore.get(mSelectIndex).optype!=4)||
										(tasktype == 1 && mStore.get(mSelectIndex).optype!=4 && mStore.get(mSelectIndex).optype!= 3)){


									if(SPHelper2.getInstance(mContext).readMarkStoreAddress(mStore.get(mSelectIndex).taskId+mStore.get(mSelectIndex).waybill)==null) {

										EventBus.getDefault().post(new GetLocationAddrEvent(mStore.get(mSelectIndex), mOrder.get(mSelectIndex)));
									}else {
										Intent intent = new Intent(mContext, UploadPaymentActivity.class);

										// 添加storedetail
										String str = GsonTool.getInstance().toJson(mStore.get(mSelectIndex));
										intent.putExtra("storedetail", str);

										// 添加taskid
										intent.putExtra("taskid", mStore.get(mSelectIndex).taskId);
										intent.putExtra("corpid", mStore.get(mSelectIndex).corpId);

										// 添加orderdetail
										String str2 = GsonTool.getInstance().toJson(mOrder.get(mSelectIndex));
										intent.putExtra("orderdetail", str2);
										((Activity) mContext).startActivityForResult(intent,
												FreightConstant.TASK_POINT_REQUSEST_CODE);
									}

								}else {


									Intent intent = new Intent(mContext, UploadPaymentActivity.class);

									// 添加storedetail
									String str = GsonTool.getInstance().toJson(mStore.get(mSelectIndex));
									intent.putExtra("storedetail", str);

									// 添加taskid
									intent.putExtra("taskid", mStore.get(mSelectIndex).taskId);
									intent.putExtra("corpid", mStore.get(mSelectIndex).corpId);

									// 添加orderdetail
									String str2 = GsonTool.getInstance().toJson(mOrder.get(mSelectIndex));
									intent.putExtra("orderdetail", str2);
									((Activity) mContext).startActivityForResult(intent,
											FreightConstant.TASK_POINT_REQUSEST_CODE);
								}
								return;
							}
						}
					};

					if (mOrder.get(viewHolder.index) != null) {
						mRunnable.run();
						mRunnable = null;
						hideProgressBar();
					} else {
						showProgressBar();
						FreightPointDeal.getInstace().finishDeliver(viewHolder.storeDetail);
					}
				}
			});

			viewHolder.operate1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {


					if (TaskUtils.isStorePosUnknown(viewHolder.storeDetail)) {

						TaskOperator.getInstance().ShowNaviDisableDialog(mContext, new OnCallBack() {

							@Override
							public void onYES() {


							}

						});

						return;

					}

					// 导航
					jumpDetailActivity(viewHolder.index);
					jumpRoutePrewiewActivity(viewHolder.index);

					FreightPointDeal.getInstace().routeplan(viewHolder.storeDetail);
				}
			});

			viewHolder.operate2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					// 电话
					FreightPointDeal.getInstace().call(viewHolder.storeDetail.linkphone);
				}
			});

			break;

		case 2:

			// 已完成配送

			if (TaskUtils.isStorePosUnknown(viewHolder.storeDetail)) {

				viewHolder.operateText.setText("电话");
				viewHolder.operateImg.setImageResource(R.drawable.freight_point_phone);
				if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
					viewHolder.operate.setVisibility(View.GONE);
				else
					viewHolder.operate.setVisibility(View.VISIBLE);

				viewHolder.operate.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 电话
						FreightPointDeal.getInstace().call(viewHolder.storeDetail.linkphone);
					}
				});

				viewHolder.operate1.setVisibility(View.GONE);

				viewHolder.operate2.setVisibility(View.GONE);

			} else {

				viewHolder.operateText.setText("查看地图");
				viewHolder.operateImg.setImageResource(R.drawable.freight_point_lookmap);
				viewHolder.operate.setVisibility(View.VISIBLE);

				if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
					viewHolder.operate1.setVisibility(View.GONE);
				else
					viewHolder.operate1.setVisibility(View.VISIBLE);

				viewHolder.operate2.setVisibility(View.GONE);

				viewHolder.operateText1.setText("电话");
				viewHolder.operateImg1.setImageResource(R.drawable.freight_point_phone);

				viewHolder.operate.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 查看地图
						((FreightPointActivity) mContext).showPoi(viewHolder.index);

					}
				});

				viewHolder.operate1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 电话
						FreightPointDeal.getInstace().call(viewHolder.storeDetail.linkphone);
					}
				});

			}
			break;

		case 3:
			// 暂停送货
			if (viewHolder.storeDetail.optype == 4) {
				viewHolder.operateText.setText("继续回程");
				viewHolder.operateImg.setImageResource(R.drawable.btn_task_back);
			} else {
				viewHolder.operateText.setText("继续");
				viewHolder.operateImg.setImageResource(R.drawable.freight_point_continue);
			}
			viewHolder.operate.setVisibility(View.VISIBLE);

			if (TextUtils.isEmpty(viewHolder.storeDetail.linkphone))
				viewHolder.operate1.setVisibility(View.GONE);
			else
				viewHolder.operate1.setVisibility(View.VISIBLE);

			viewHolder.operateText1.setText("电话");
			viewHolder.operateImg1.setImageResource(R.drawable.freight_point_phone);

			viewHolder.operate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					FreightPointDeal.getInstace().cleanCallback();
					mSelectIndex = viewHolder.index;

					boolean isTaskNavi = GeneralSPHelper.getInstance(mContext.getApplicationContext())
							.isTaskNavi(viewHolder.storeDetail.taskId);

					if (!isTaskNavi) {
						jumpDetailActivity(viewHolder.index);
						jumpRoutePrewiewActivity(viewHolder.index);
						FreightPointDeal.getInstace().startOrContinueDeliver(viewHolder.storeDetail);
					} else {
						jumpDetailActivity(viewHolder.index);
						jumpRoutePrewiewActivity(viewHolder.index);
						FreightPointDeal.getInstace().startOrContinueDeliverAndNavigate(viewHolder.storeDetail);
					}
				}
			});

			viewHolder.operate1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					// 电话
					FreightPointDeal.getInstace().call(viewHolder.storeDetail.linkphone);
				}
			});

			viewHolder.operate2.setVisibility(View.GONE);

			break;
		}

	}

	/**
	 * @Title: jumpDetailActivity
	 * @Description: 调整到详情界面
	 * @param position
	 * @return: void
	 */
	private void jumpDetailActivity(final int position) {
		Runnable r = new Runnable() {
			@Override
			public void run() {

				MtqDeliStoreDetail storedetail = mStore.get(position);

				Intent intent = new Intent(mContext, TaskPointDetailActivity.class);

				// 添加storedetail
				String str = GsonTool.getInstance().toJson(storedetail);
				intent.putExtra("storedetail", str);

				// 添加taskid
				intent.putExtra("taskid", storedetail.taskId);
				intent.putExtra("corpid", storedetail.corpId);
				if (mOrder.get(position) != null) {
					String str2 = GsonTool.getInstance().toJson(mOrder.get(position));
					intent.putExtra("orderdetail", str2);
				}

				((Activity) mContext).startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);
			}
		};

		FreightPointDeal.getInstace().setmJumpRunnable(r);
		FreightPointDeal.getInstace().setmRouteFailable(r);
	}

	private void jumpRoutePrewiewActivity(final int position) {
		FreightPointDeal.getInstace().setmRouteSucessable(new Runnable() {
			@Override
			public void run() {

				Intent intent = new Intent(mContext, RoutePreviewActivity.class);

				MtqDeliStoreDetail storedetail = mStore.get(position);

				// 添加storedetail
				String str = GsonTool.getInstance().toJson(storedetail);
				intent.putExtra("storedetail", str);

				// 添加taskid
				intent.putExtra("taskid", storedetail.taskId);
				intent.putExtra("corpid", storedetail.corpId);
				if (mOrder.get(position) != null) {
					String str2 = GsonTool.getInstance().toJson(mOrder.get(position));
					intent.putExtra("orderdetail", str2);
				}

				str = GsonTool.getInstance().toJson(FreightPointDeal.getInstace().getmMtqDeliTaskDetail());
				intent.putExtra("taskdetail", str);

				((Activity) mContext).startActivityForResult(intent, FreightConstant.TASK_POINT_REQUSEST_CODE);
				FreightPointDeal.getInstace().setmRouteSucessable(null);
			}
		});
	}

	private void showProgressBar() {
		// ((FreightPointActivity)mContext).showProgressBar();
		WaitingProgressTool.showProgress(mContext);
	}

	private void hideProgressBar() {
		// ((FreightPointActivity)mContext).hideProgressBar();
		WaitingProgressTool.closeshowProgress();
	}

	final static class ViewHolder {
		ImageView image;
		TextView name;
		TextView address;
		TextView status;
		TextView time;
		TextView timeStatus; // 已完成 超时
		TextView locate;
		LinearLayout locate2;
		LinearLayout operate;
		RelativeLayout operate1;
		RelativeLayout operate2;

		TextView operateText;
		TextView operateText1;
		TextView operateText2;

		ImageView operateImg;
		ImageView operateImg1;
		ImageView operateImg2;

		MtqDeliStoreDetail storeDetail;
		int index;
	}

	@Override
	public void OnResult() {

		notifyDataSetChanged();
		hideProgressBar();

		// if (mRunnable != null) {
		// mRunnable.run();
		// mRunnable = null;
		// }
	}

	@Override
	public void OnError(int ErrCode) {

		hideProgressBar();
	}

}
