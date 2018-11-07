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

public class FilterAlarmDailog extends Dialog implements OnClickListener {

	private Context mContext;
	private ImageView mBack;
	private TextView mTitle;
	private TextView mFilter[] = new TextView[16];
	private List<Filter> mFilterList = new ArrayList<Filter>();
	private int mLen = 16;
	private int[] mTextID = new int[] { R.id.msg_filter_alarm_item0,
			R.id.msg_filter_alarm_item1, R.id.msg_filter_alarm_item2,
			R.id.msg_filter_alarm_item3, R.id.msg_filter_alarm_item4,
			R.id.msg_filter_alarm_item5, R.id.msg_filter_alarm_item6,
			R.id.msg_filter_alarm_item7, R.id.msg_filter_alarm_item8,
			R.id.msg_filter_alarm_item9, R.id.msg_filter_alarm_item10,
			R.id.msg_filter_alarm_item11, R.id.msg_filter_alarm_item12,
			R.id.msg_filter_alarm_item13, R.id.msg_filter_alarm_item14,
			R.id.msg_filter_alarm_item15 };
	private IFiltertener mListener;

	public interface IFiltertener {

		public void OnFinish(List<Filter> filters);
	}

	public FilterAlarmDailog(Context context, IFiltertener listener) {
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
		// getWindow().setContentView(R.layout.activity_msg_filter_business);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View viewDialog = inflater.inflate(R.layout.activity_msg_filter_alarm,
				null);
		Display display = ((Activity) mContext).getWindowManager()
				.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		// 设置dialog的宽高为屏幕的宽高
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width,
				height);
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
		findViewById(R.id.msg_filter_alarm_clear).setOnClickListener(this);
		findViewById(R.id.msg_filter_alarm_finish).setOnClickListener(this);
		for (int i = 0; i < mLen; i++) {
			mFilter[i].setOnClickListener(this);
		}
	}

	private void initData() {
		mBack.setVisibility(View.VISIBLE);
		mBack.setImageResource(R.drawable.icon_cancel_nor);
		mTitle.setText(R.string.filter_alarm_msg);
	}

	private void loadData() {
		/*
		 * 读取上次的筛选记录
		 * 
		 * mFilterList = MsgFilterTable.getInstance().queryByType(1); if
		 * (mFilterList == null || mFilterList.size() < mLen) {
		 * initAlarmFilter(); updateUI(); } updateUI();
		 */

		/**
		 * 每次进来都是初始状态
		 */
		initAlarmFilter();
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
		case R.id.msg_filter_alarm_item0:
		case R.id.msg_filter_alarm_item1:
		case R.id.msg_filter_alarm_item2:
		case R.id.msg_filter_alarm_item3:
		case R.id.msg_filter_alarm_item4:
		case R.id.msg_filter_alarm_item5:
		case R.id.msg_filter_alarm_item6:
		case R.id.msg_filter_alarm_item7:
		case R.id.msg_filter_alarm_item8:
		case R.id.msg_filter_alarm_item9:
		case R.id.msg_filter_alarm_item10:
		case R.id.msg_filter_alarm_item11:
		case R.id.msg_filter_alarm_item12:
		case R.id.msg_filter_alarm_item13:
		case R.id.msg_filter_alarm_item14:
		case R.id.msg_filter_alarm_item15: {
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
		case R.id.msg_filter_alarm_clear: {
			for (int i = 0; i < mLen; i++) {
				if (mFilter[i].isSelected()) {
					mFilter[i].setSelected(false);
					mFilterList.get(i).setSelect(0);
					/**
					 * 更新状态
					 * MsgFilterTable.getInstance().update(mFilterList.get(i), 1);
					 */
				}
			}
			break;
		}
		case R.id.msg_filter_alarm_finish: {
			ArrayList<Filter> filters = new ArrayList<Filter>();
			for (int i = 0; i < mLen; i++) {
				if (mFilter[i].isSelected()) {
					// MLog.e("test", "filterid: " + mFilterList.get(i).getId());
					filters.add(mFilterList.get(i));
				}
			}

			/**
			 * 更新所有状态
			 * MsgFilterTable.getInstance().update(mFilterList, 1);
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

	private void initAlarmFilter() {
		if (mFilterList != null) {
			mFilterList.clear();
		} else {
			mFilterList = new ArrayList<Filter>();
		}
		/************ 车辆安全 ********/

		/**
		 * 碰撞提醒
		 */
		Filter filter0 = new Filter();
		filter0.setId(FilterId.FILTER_ID_19);
		filter0.setType(1);
		filter0.setSelect(0);
		mFilterList.add(filter0);

		/**
		 * 翻车提醒
		 */
		Filter filter1 = new Filter();
		filter1.setId(FilterId.FILTER_ID_20);
		filter1.setType(1);
		filter1.setSelect(0);
		mFilterList.add(filter1);

		/**
		 * 超速行驶提醒
		 */
		Filter filter2 = new Filter();
		filter2.setId(FilterId.FILTER_ID_21);
		filter2.setType(1);
		filter2.setSelect(0);
		mFilterList.add(filter2);

		/**
		 * 疲劳驾驶提醒
		 */
		Filter filter3 = new Filter();
		filter3.setId(FilterId.FILTER_ID_22);
		filter3.setType(1);
		filter3.setSelect(0);
		mFilterList.add(filter3);

		/**
		 * 异常震动提醒
		 */
		Filter filter4 = new Filter();
		filter4.setId(FilterId.FILTER_ID_23);
		filter4.setType(1);
		filter4.setSelect(0);
		mFilterList.add(filter4);

		/************ 车辆异常 ********/

		/**
		 * 车门状态异常提醒
		 */
		Filter filter5 = new Filter();
		filter5.setId(FilterId.FILTER_ID_24);
		filter5.setType(1);
		filter5.setSelect(0);
		mFilterList.add(filter5);

		/**
		 * 胎压和手刹异常提醒
		 */
		Filter filter6 = new Filter();
		filter6.setId(FilterId.FILTER_ID_25);
		filter6.setType(1);
		filter6.setSelect(0);
		mFilterList.add(filter6);

		/**
		 * 水温异常提醒
		 */
		Filter filter7 = new Filter();
		filter7.setId(FilterId.FILTER_ID_26);
		filter7.setType(1);
		filter7.setSelect(0);
		mFilterList.add(filter7);

		/**
		 * 转速异常提醒
		 */
		Filter filter8 = new Filter();
		filter8.setId(FilterId.FILTER_ID_27);
		filter8.setType(1);
		filter8.setSelect(0);
		mFilterList.add(filter8);

		/**
		 * 电瓶电压异常提醒
		 */
		Filter filter9 = new Filter();
		filter9.setId(FilterId.FILTER_ID_28);
		filter9.setType(1);
		filter9.setSelect(0);
		mFilterList.add(filter9);

		/**
		 * 车辆故障提醒
		 */
		Filter filter10 = new Filter();
		filter10.setId(FilterId.FILTER_ID_29);
		filter10.setType(1);
		filter10.setSelect(0);
		mFilterList.add(filter10);

		/**
		 * 漏油提醒
		 */
		Filter filter11 = new Filter();
		filter11.setId(FilterId.FILTER_ID_30);
		filter11.setType(1);
		filter11.setSelect(0);
		mFilterList.add(filter11);

		/**
		 * 拖吊报警
		 */
		Filter filter12 = new Filter();
		filter12.setId(FilterId.FILTER_ID_31);
		filter12.setType(1);
		filter12.setSelect(0);
		mFilterList.add(filter12);

		/************ 设备异常 ********/

		/**
		 * 断电提醒
		 */
		Filter filter13 = new Filter();
		filter13.setId(FilterId.FILTER_ID_32);
		filter13.setType(1);
		filter13.setSelect(0);
		mFilterList.add(filter13);

		/**
		 * 终端异常
		 */
		Filter filter14 = new Filter();
		filter14.setId(FilterId.FILTER_ID_33);
		filter14.setType(1);
		filter14.setSelect(0);
		mFilterList.add(filter14);

		/**
		 * 设备OBD终端的SIM卡
		 */
		Filter filter15 = new Filter();
		filter15.setId(FilterId.FILTER_ID_34);
		filter15.setType(1);
		filter15.setSelect(0);
		mFilterList.add(filter15);

		/**
		 * 保存到数据库
		 * MsgFilterTable.getInstance().insert(mFilterList);
		 */
	}
}
