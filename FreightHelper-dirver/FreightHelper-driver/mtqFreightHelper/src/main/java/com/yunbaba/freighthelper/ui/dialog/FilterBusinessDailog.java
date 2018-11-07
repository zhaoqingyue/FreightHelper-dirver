package com.yunbaba.freighthelper.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.msg.Filter;
import com.yunbaba.freighthelper.bean.msg.Filter.FilterId;

import java.util.ArrayList;
import java.util.List;

public class FilterBusinessDailog extends Dialog implements OnClickListener {

	private Context mContext;
	private ImageView mBack;
	private TextView mTitle;
	private int mLen = 7;
	private TextView mFilter[] = new TextView[mLen];
	private List<Filter> mFilterList = new ArrayList<Filter>();
	
	private int[] mTextID = new int[] { R.id.msg_filter_business_item0,
			R.id.msg_filter_business_item1, R.id.msg_filter_business_item2,
			R.id.msg_filter_business_item3, R.id.msg_filter_business_item4,
			R.id.msg_filter_business_item5, R.id.msg_filter_business_item6 };
	private IFiltertener mListener;

	public interface IFiltertener {

		public void OnFinish(List<Filter> filters);
	}

	public FilterBusinessDailog(Context context, IFiltertener listener) {
		super(context, R.style.dialog_style);
		mContext = context;
		mListener = listener;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCanceledOnTouchOutside(false);
		//getWindow().setContentView(R.layout.activity_msg_filter_business);
	
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View viewDialog = inflater.inflate(R.layout.activity_msg_filter_business, null);
		Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		//设置dialog的宽高为屏幕的宽高
		ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(width, height);
		setContentView(viewDialog, layoutParams);
		getWindow().setWindowAnimations(R.style.SelectPopupAnimation); 
		
		initViews();
		setListener();
		initData();
		loadData();
	}

	private void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		for (int i = 0; i < mLen; i++) {
			mFilter[i] = (TextView) findViewById(mTextID[i]);
		}
	}

	private void setListener() {
		mBack.setOnClickListener(this);
		findViewById(R.id.msg_filter_business_clear).setOnClickListener(this);
		findViewById(R.id.msg_filter_business_finish).setOnClickListener(this);
		for (int i = 0; i < mLen; i++) {
			mFilter[i].setOnClickListener(this);
		}
	}
	
	private void initData() {
		mBack.setVisibility(View.VISIBLE);
		mBack.setImageResource(R.drawable.icon_cancel_nor);
		mTitle.setText(R.string.filter_business_msg);
	}
	
	private void loadData() {
		/*
		 * 读取上次的筛选记录
		 * 
		 * mFilterList = MsgFilterTable.getInstance().queryByType(0); if
		 * (mFilterList == null || mFilterList.size() < mLen) {
		 * initBusinessFilter(); } updateUI();
		 */
		
		initBusinessFilter();
		updateUI();
	}
	
	private void updateUI() {
		if (mFilterList != null && !mFilterList.isEmpty()) {
			for (int i = 0; i < mLen; i++) {
				Filter filter = mFilterList.get(i);
				if (filter.getSelect() == 0) {
					mFilter[i].setSelected(false);
				} else if (filter.getSelect() == 1) {
					mFilter[i].setSelected(true);
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img: {
			dismiss();
			break;
		}
		case R.id.msg_filter_business_item0: 
		case R.id.msg_filter_business_item1:
		case R.id.msg_filter_business_item2:
		case R.id.msg_filter_business_item3:
		case R.id.msg_filter_business_item4:
		case R.id.msg_filter_business_item5:
		case R.id.msg_filter_business_item6: {
			for (int i = 0; i < mLen; i++) {
				if (v.getId() == mTextID[i]) {
					// MLog.e("test", "filterid: " + mFilterList.get(i).getId());
					if (mFilter[i].isSelected()) {
						mFilter[i].setSelected(false);
						mFilterList.get(i).setSelect(0);
					} else {
						mFilter[i].setSelected(true);
						mFilterList.get(i).setSelect(1);
					}
				}
			}
			break;
		}
		case R.id.msg_filter_business_clear: {
			for (int i = 0; i < mLen; i++) {
				if (mFilter[i].isSelected()) {
					mFilter[i].setSelected(false);
					mFilterList.get(i).setSelect(0);
					/**
					 * 更新状态
					 * MsgFilterTable.getInstance().update(mFilterList.get(i), 0);
					 */
				}
			}
			break;
		}
		case R.id.msg_filter_business_finish: {
			ArrayList<Filter> filters = new ArrayList<Filter>();
			for (int i = 0; i < mLen; i++) {
				if (mFilter[i].isSelected()) {
					
					// MLog.e("test", "filterid: " + mFilterList.get(i).getId());
					filters.add(mFilterList.get(i));
				}
			}
			
			/**
			 * 更新所有状态
			 * MsgFilterTable.getInstance().update(mFilterList, 0);
			 */
			
			dismiss();
			if (mListener != null) {
				mListener.OnFinish(filters);
			}
			break;
		}
		default:
			break;
		}
	}
	
	private void initBusinessFilter() {
		if (mFilterList != null) {
			mFilterList.clear();
		} else {
			mFilterList = new ArrayList<Filter>();
		}
		
		/************ 企业 ********/
		
		/**
		 * 加入/退出车队
		 * FilterId.FILTER_ID_10 
		 * FilterId.FILTER_ID_11
		 */
		Filter filter0 = new Filter();
		filter0.setId(FilterId.FILTER_ID_10);
		filter0.setType(0);
		filter0.setSelect(0);
		mFilterList.add(filter0);

		/**
		 * 调度消息
		 * FilterId.FILTER_ID_12
		 * FilterId.FILTER_ID_13
		 * FilterId.FILTER_ID_1311
		 */
		Filter filter1 = new Filter();
		filter1.setId(FilterId.FILTER_ID_12);
		filter1.setType(0);
		filter1.setSelect(0);
		mFilterList.add(filter1);
		
		
		/**
		 * 新任务消息
		 * FilterId.FILTER_ID_18
		 */
		Filter filter2 = new Filter();
		filter2.setId(FilterId.FILTER_ID_18);
		filter2.setType(0);
		filter2.setSelect(0);
		mFilterList.add(filter2);
		
		/************ 任务 ********/
		
		/**
		 * 运单超时报警
		 */
		Filter filter3 = new Filter();
		filter3.setId(FilterId.FILTER_ID_14);
		filter3.setType(0);
		filter3.setSelect(0);
		mFilterList.add(filter3);
		
		/**
		 * 运单超时提醒
		 */
		Filter filter4 = new Filter();
		filter4.setId(FilterId.FILTER_ID_15);
		filter4.setType(0);
		filter4.setSelect(0);
		mFilterList.add(filter4);

		/**
		 * 运单过期报警
		 */
		Filter filter5 = new Filter();
		filter5.setId(FilterId.FILTER_ID_16);
		filter5.setType(0);
		filter5.setSelect(0);
		mFilterList.add(filter5);

		/**
		 * 运单过期提醒
		 */
		Filter filter6 = new Filter();
		filter6.setId(FilterId.FILTER_ID_17);
		filter6.setType(0);
		filter6.setSelect(0);
		mFilterList.add(filter6);
		
		/**
		 * 保存到数据库
		 * MsgFilterTable.getInstance().insert(mFilterList);
		 */
	}
}
