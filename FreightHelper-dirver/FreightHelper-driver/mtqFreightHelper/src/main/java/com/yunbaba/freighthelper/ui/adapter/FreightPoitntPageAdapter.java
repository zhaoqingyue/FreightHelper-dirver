package com.yunbaba.freighthelper.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunbaba.api.trunk.FreightPointDeal;
import com.yunbaba.api.trunk.bean.OnUIResult;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.ui.activity.RoutePreviewActivity;
import com.yunbaba.freighthelper.ui.activity.task.FreightPointActivity;
import com.yunbaba.freighthelper.ui.activity.task.TaskPointDetailActivity;
import com.yunbaba.freighthelper.ui.activity.task.UploadPaymentActivity;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.GeneralSPHelper;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.TaskUtils;
import com.yunbaba.freighthelper.utils.TimestampTool;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class FreightPoitntPageAdapter extends PagerAdapter implements OnUIResult {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private LinkedList<View> mViewCache = null;
	private ArrayList<MtqDeliStoreDetail> mStore;
	private HashMap<Integer, MtqDeliOrderDetail> mOrder;
	private int mSelectIndex;
	private int mChildCount = 0;
	private boolean isNeedJump = false; // 是否要跳转界面。
	private Runnable mRunnable = null;

	private float mScaleX = 0.0f;
	private float mScaleY = 0.0f;

	public FreightPoitntPageAdapter(Context ctx) {
		mContext = ctx;
		mLayoutInflater = LayoutInflater.from(ctx);
		mViewCache = new LinkedList<>();
		mStore = Filter((FreightPointDeal.getInstace().getmStore()));
		mOrder = FreightPointDeal.getInstace().getmOrder();
		FreightPointDeal.getInstace().setmMapUICallBack(this);

		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();

		mScaleX = (float) (width / 1080.0);
		mScaleY = (float) (height / 1920.0);
	}

	public ArrayList<MtqDeliStoreDetail> Filter(ArrayList<MtqDeliStoreDetail> list) {

		ArrayList<MtqDeliStoreDetail> tmplist = new ArrayList<>();

		//Iterator<MtqDeliStoreDetail> iter = list.iterator();
		MtqDeliStoreDetail tmp;
		for(int i = 0; i< list.size() ;i++) {
			// 运货中
			tmp = list.get(i);
			if (!TaskUtils.isStorePosUnknown(tmp)) {

				//tmp.storesort = i+1;
				tmplist.add(tmp);
				// break searchforrunningtask;
			}

		}

		return tmplist;

	}

	@Override
	public int getCount() {

		if (mStore != null) {
			return mStore.size();
		}
		return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {

		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {

		container.removeView((View) object);
		mViewCache.add((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		View view;
		ViewHolder viewHolder = null;
		if (mViewCache.size() == 0) {
			viewHolder = new ViewHolder();
			view = mLayoutInflater.inflate(R.layout.layout_freight_point_bottom, null);
			viewHolder.address = (TextView) view.findViewById(R.id.id_freight_point_address);
			viewHolder.name = (TextView) view.findViewById(R.id.id_freight_point_name);
			viewHolder.status = (TextView) view.findViewById(R.id.id_freight_point_status);
			viewHolder.time = (TextView) view.findViewById(R.id.id_freight_point_time);
			viewHolder.timeStatus = (TextView) view.findViewById(R.id.freight_time_status);
			viewHolder.image = (ImageView) view.findViewById(R.id.freight_point_type_pic);
			viewHolder.operateText = (TextView) view.findViewById(R.id.freight_operation_text);
			viewHolder.operateText1 = (TextView) view.findViewById(R.id.freight_operation_text1);
			viewHolder.operateText2 = (TextView) view.findViewById(R.id.freight_operation_text2);
			viewHolder.operateImg = (ImageView) view.findViewById(R.id.freight_point_operation_pic);
			viewHolder.operateImg1 = (ImageView) view.findViewById(R.id.freight_point_operation_pic1);
			viewHolder.operateImg2 = (ImageView) view.findViewById(R.id.freight_point_operation_pic2);
			viewHolder.operate = (LinearLayout) view.findViewById(R.id.freight_operation);
			viewHolder.operate1 = (RelativeLayout) view.findViewById(R.id.freight_operation1);
			viewHolder.operate2 = (RelativeLayout) view.findViewById(R.id.freight_operation2);
			viewHolder.line = (LinearLayout) view.findViewById(R.id.freigt_point_line);

			view.setTag(viewHolder);
		} else {
			view = mViewCache.removeFirst();
			viewHolder = (ViewHolder) view.getTag();
		}

		//createLinedot(holder.line, position);

		MtqDeliStoreDetail storeDetail = mStore.get(position);

		// order 可能为空
		MtqDeliOrderDetail orderDetail = mOrder.get(storeDetail.storesort);

		viewHolder.index = position;
		viewHolder.storeDetail = storeDetail;
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
				FreightLogicTool.getMapPointPosition(storeDetail.storesort+"","/"+FreightPointDeal.getInstace().getmStore().size()+".",
						(TextUtils.isEmpty(storeDetail.storecode) ? "" : (storeDetail.storecode + "-"))
								+ storeDetail.storename));
		viewHolder.status
				.setText(FreightPointDeal.getInstace().getStoreStatusText(storeDetail.storestatus, storeDetail.optype));

		if (orderDetail != null) {
			viewHolder.time.setVisibility(View.VISIBLE);
			viewHolder.time.setText(TimestampTool.getStoreDetailTimeHint(orderDetail.reqtime_e * 1000L) + "前送达");
		} else {
			viewHolder.time.setVisibility(View.INVISIBLE);
		}

		viewHolder.timeStatus.setVisibility(View.INVISIBLE);

		if (FreightPointDeal.getInstace().isOverTime(position)) {
			viewHolder.timeStatus.setVisibility(View.VISIBLE);
			viewHolder.timeStatus.setText("已超时");
		}

		viewHolder.operate2.setVisibility(View.INVISIBLE);

		setImgbyOptype(storeDetail.optype, viewHolder.image);

		setViewbyStoreStatus(storeDetail.storestatus, viewHolder);

		container.addView(view);

		return view;
	}
//
//	private void createLinedot(LinearLayout linelayout, int position) {
//		linelayout.removeAllViews();
//		for (int i = 0; i < mStore.size(); i++) {
//
//			ImageView pointImg = new ImageView(mContext);
//			linelayout.addView(pointImg);
//			LinearLayout.LayoutParams pointImgParams = (LinearLayout.LayoutParams) pointImg.getLayoutParams();
//
//			pointImgParams.width = (int) mScaleX * 32;
//			pointImgParams.height = (int) mScaleX * 32;
//			pointImgParams.rightMargin = (int) mScaleY * 5;
//			pointImgParams.gravity = Gravity.TOP;
//			pointImg.setScaleType(ScaleType.CENTER_INSIDE);
//			pointImg.setLayoutParams(pointImgParams);
//
//			if (i == position) {
//				pointImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.s_point));
//			} else {
//				pointImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.n_point));
//			}
//		}
//	}

	@Override
	public void notifyDataSetChanged() {

		mChildCount = getCount();
		super.notifyDataSetChanged();
	}

	@Override
	public int getItemPosition(Object object) {

		if (mChildCount > 0) {
			mChildCount--;
			return POSITION_NONE;
		}

		return super.getItemPosition(object);
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
			// 提货
			image.setImageResource(R.drawable.ic_task_deliver);
			break;
		case 3:
			// 送货
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
			viewHolder.operateText.setText("查看地图");
			viewHolder.operateImg.setImageResource(R.drawable.freight_point_lookmap);

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
				if (mOrder.get(storedetail.storesort) != null) {
					String str2 = GsonTool.getInstance().toJson(mOrder.get(storedetail.storesort));
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

	final static class ViewHolder {
		ImageView image;
		TextView name;
		TextView address;
		TextView status;
		TextView time;
		TextView timeStatus; // 已完成 超时
		LinearLayout operate;
		RelativeLayout operate1;
		RelativeLayout operate2;

		TextView operateText;
		TextView operateText1;
		TextView operateText2;

		ImageView operateImg;
		ImageView operateImg1;
		ImageView operateImg2;

		LinearLayout line;

		MtqDeliStoreDetail storeDetail;
		int index;
	}

	@Override
	public void OnResult() {

		notifyDataSetChanged();
		hideProgressBar();
		// if (mRunnable != null){
		// mRunnable.run();
		// mRunnable = null;
		// }
	}

	@Override
	public void OnError(int ErrCode) {

		hideProgressBar();
	}

	private void showProgressBar() {
		WaitingProgressTool.showProgress(mContext);
	}

	private void hideProgressBar() {
		WaitingProgressTool.closeshowProgress();
	}

}
