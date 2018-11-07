package com.yunbaba.freighthelper.ui.activity.task.feedback;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.cld.gson.reflect.TypeToken;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.ui.activity.task.ImagePagerActivity;
import com.yunbaba.freighthelper.ui.customview.FlowLayout;
import com.yunbaba.freighthelper.ui.customview.TagAdapter;
import com.yunbaba.freighthelper.ui.customview.TagFlowLayout;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.TimestampTool;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBack;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBackPictures;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedBackListAdapter extends BaseAdapter {

	ArrayList<FeedBack> mlistdata;
	// HashMap<String, String> mlistdata;
	Context mContext;

	private String corpid = "";
	private String taskid = "";

	public FeedBackListAdapter(Context mContext, ArrayList<FeedBack> mlistdata2, String corpid, String taskid) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.mlistdata = mlistdata2;
		this.corpid = corpid;
		this.taskid = taskid;
	}

	@Override
	public int getCount() {

		return mlistdata == null ? 0 : mlistdata.size();
	}

	@Override
	public FeedBack getItem(int position) {

		// if()
		return mlistdata == null ? null : mlistdata.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

	//	MLog.e("mystring","getview");
		ViewHolder holder;
		// FeedBackReasonAdapter reasonAdapter;
		// TagAdapter<String> reasonAdapter;
		FeedBackPicAdapter picAdapter;
		OnClick listener = null;

		if (view != null) {
			holder = (ViewHolder) view.getTag();
			// reasonAdapter = (FeedBackReasonAdapter)
			// view.getTag(holder.gv_reason.getId());
			picAdapter = (FeedBackPicAdapter) view.getTag(holder.gv_pic.getId());
			listener = (OnClick) view.getTag(holder.gv_pic.getId()+1);
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_feedback, parent, false);
			holder = new ViewHolder(view);
			listener = new OnClick();// 在这里新建监听对象
			// reasonAdapter = new FeedBackReasonAdapter(mContext, null);
			picAdapter = new FeedBackPicAdapter(mContext, null, corpid, taskid);

			view.setTag(holder);

			// view.setTag(holder.gv_reason.getId(), reasonAdapter);
			view.setTag(holder.gv_pic.getId(), picAdapter);
			view.setTag(holder.gv_pic.getId()+1, listener);

		}

		FeedBack tmp = getItem(position);

//		MLog.e("fb加载",position+"");
		
		
		if (!TextUtils.isEmpty(tmp.remark)) {
			holder.tv_remark.setVisibility(View.VISIBLE);
			holder.tv_remark.setText(tmp.remark);

		} else {
			holder.tv_remark.setVisibility(View.GONE);
		}

		try {
			holder.tv_time.setText(TimestampTool.DateToString(new Date(Long.valueOf(tmp.update_time) * 1000)));

		} catch (Exception e) {

		}

		if (!TextUtils.isEmpty(tmp.remark)) {
			holder.tv_remark.setVisibility(View.VISIBLE);
			holder.tv_remark.setText(tmp.remark);

		} else {
			holder.tv_remark.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(tmp.content)) {

			holder.prl_rs.setVisibility(View.VISIBLE);
			holder.gv_reason.setVisibility(View.VISIBLE);
			ArrayList<String> list = TextStringUtil.splitFeedBackReasonString(tmp.content);
			MLog.e("checklist", "" + GsonTool.getInstance().toJson(list));
			// reasonAdapter.setData(list);

			// reasonAdapter.

			final LayoutInflater inf = LayoutInflater.from(mContext);

			// final FlowLayout fll = holder.gv_reason;

			final float TextSize = holder.tv_time.getTextSize();

			// holder.gv_reason.removeAllViews();

			holder.gv_reason.setAdapter(new TagAdapter<String>(list) {
				@Override
				public View getView(FlowLayout parent, int position, String s) {
					// View view = inf.inflate(R.layout.item_feedback_reason,
					// fll, false);
					//
					// TextView tv = (TextView)
					// view.findViewById(R.id.tv_reason);

					TextView tv = (TextView) inf.inflate(R.layout.item_tv, parent, false);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FreightLogicTool.getFeedBackReasonSize(mContext));
					tv.setText(s);

					return tv;
				}

			});

			// reasonAdapter.notifyDataSetChanged();

		} else {

			holder.prl_rs.setVisibility(View.GONE);

		}

		if (!TextUtils.isEmpty(tmp.pics)) {

			holder.prl_pic.setVisibility(View.VISIBLE);

			Type type = new TypeToken<ArrayList<FeedBackPictures>>() {
			}.getType();
			ArrayList<FeedBackPictures> list = GsonTool.getInstance().fromJson(tmp.pics, type);

			picAdapter.setList(list);

			holder.gv_pic.setAdapter(picAdapter);

			picAdapter.notifyDataSetChanged();
			
			listener.setPosition(mContext, position, list);
			holder.gv_pic.setOnItemClickListener(listener);
			

		} else {

			holder.prl_pic.setVisibility(View.GONE);

		}

		return view;
	}

	public static class OnClick implements OnItemClickListener {
		int position;
		Context context;
		ArrayList<FeedBackPictures> list;

		public void setPosition(Context context, int position, ArrayList<FeedBackPictures> list) {
			this.position = position;
			this.context = context;
			this.list = list;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


			ArrayList<String> piclistpath = new ArrayList<>();
			for (FeedBackPictures pic : list) {

				if (pic != null && !TextUtils.isEmpty(pic.file)) {
					piclistpath.add(pic.file);
				}

			}

			Intent intent = new Intent(context, ImagePagerActivity.class);
			// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, piclistpath);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		
			context.startActivity(intent);

		}
	}

	static class ViewHolder {

		@BindView(R.id.gv_reason)
		TagFlowLayout gv_reason;

		@BindView(R.id.tv_time)
		TextView tv_time;
		@BindView(R.id.tv_remark)
		TextView tv_remark;
		@BindView(R.id.gv_pic)
		GridView gv_pic;

		@BindView(R.id.prl_pic)
		PercentRelativeLayout prl_pic;

		@BindView(R.id.prl_rs)
		PercentRelativeLayout prl_rs;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

	}

}
