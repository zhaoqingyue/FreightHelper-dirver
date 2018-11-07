package com.yunbaba.freighthelper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhoneCallDialog extends Dialog implements OnClickListener {

	private ListView mMessage;
	private Button mCancel;
	private String mMessageStr = "";
	private String mSureStr;
	private View view1, view2;
	private String mCancelStr;
	private IPhoneCallDialogListener mListener;
	private Context mContext;
	private PhoneListAdapter mAdapter;

	public interface IPhoneCallDialogListener {

		public void OnCancel();

		public void OnSure(String phone);
	}

	public PhoneCallDialog(Context context) {
		super(context);
		mContext = context;
	}

	public PhoneCallDialog(Context context, String messageStr) {
		super(context, R.style.dialog_style);
		mMessageStr = messageStr;
		mContext = context;
	}

	public PhoneCallDialog(Context context, String messageStr, String cancelStr, String sureStr,
			IPhoneCallDialogListener listener) {
		super(context, R.style.dialog_style);
		mMessageStr = messageStr;
		mCancelStr = cancelStr;
		mSureStr = sureStr;
		mListener = listener;
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 点击Dialog以外的区域，Dialog不关闭
		setCanceledOnTouchOutside(false);
		// 设置成系统级别的Dialog，即全局性质的Dialog，在任何界面下都可以弹出来
		// getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		getWindow().setContentView(R.layout.dialog_prompt2);

		initViews();
		setListener();
		setViews();
	}

	private void initViews() {
		mMessage = (ListView) findViewById(R.id.dialog_prompt_message);
		mCancel = (Button) findViewById(R.id.dialog_prompt_cancel);
		view1 = (View) findViewById(R.id.view1);
		view2 = (View) findViewById(R.id.view2);
	}

	private void setListener() {

		mCancel.setOnClickListener(this);
	}

	private void setViews() {
		// mMessage.setText(mMessageStr);
		mCancel.setText(mCancelStr);
		// mSure.setText(mSureStr);
		mAdapter = new PhoneListAdapter(mContext, mMessageStr);
		mMessage.setAdapter(mAdapter);

		mAdapter.notifyDataSetChanged();

		WindowManager wm = (WindowManager) mContext

				.getSystemService(Context.WINDOW_SERVICE);

		int height = wm.getDefaultDisplay().getHeight();

		// 获取ListView的高度和当前屏幕的0.6进行比较，如果高，就自适应改变
		int realHeight = getTotalHeightofListView(mMessage);

		if (realHeight > height * 0.5) {
			// 得到ListView的参数值
			ViewGroup.LayoutParams params = mMessage.getLayoutParams();
			// 设置ListView的高度是屏幕的一半
			params.height = (int) (height * 0.5);
			// 设置
			mMessage.setLayoutParams(params);
		}

		if (mAdapter.getCount() <= 1) {
			view1.setVisibility(View.VISIBLE);
			view2.setVisibility(View.VISIBLE);
		} else {
			view1.setVisibility(View.GONE);
			view2.setVisibility(View.GONE);

		}

		// if(realHeight < height * 0.15){
		// ViewGroup.LayoutParams params = mMessage.getLayoutParams();
		// // 设置ListView的高度是屏幕的一半
		// params.height = (int) (height * 0.15);
		// // 设置
		// mMessage.setLayoutParams(params);
		//
		//
		// }

		// mMessage.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		//
		// String str = "";
		// try {
		// str = mAdapter.getItem(position);
		// dismiss();
		// if (mListener != null) {
		// mListener.OnSure(str);
		// }
		// } catch (Exception e) {
		//
		// }
		//
		// }
		// });

	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		switch (v.getId()) {
		case R.id.dialog_prompt_cancel: {
			dismiss();
			if (mListener != null) {
				mListener.OnCancel();
			}
			break;
		}

		default:
			break;
		}
	}

	/**
	 * 获取ListView的高度
	 * 
	 * @param listView
	 *            listview内容列表
	 * @return ListView的高度
	 */
	public int getTotalHeightofListView(ListView list) {
		// ListView的适配器
		ListAdapter mAdapter = list.getAdapter();
		if (mAdapter == null) {
			return 0;
		}
		int totalHeight = 0;
		// 循环适配器中的每一项
		for (int i = 0; i < mAdapter.getCount(); i++) {
			// 得到每项的界面view
			View mView = mAdapter.getView(i, null, list);
			// 得到一个view的大小
			mView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

			// 总共ListView的高度
			totalHeight += mView.getMeasuredHeight();
		}
		return totalHeight;
	}

	public class PhoneListAdapter extends BaseAdapter {

		ArrayList<String> mlistdata;
		// HashMap<String, String> mlistdata;
		Context mContext;

		public PhoneListAdapter(Context mContext, String phonedata) {
			// TODO Auto-generated constructor stub
			this.mContext = mContext;

			this.mlistdata = new ArrayList<String>();

			if (!TextUtils.isEmpty(phonedata)) {

				this.mlistdata = TextStringUtil.splitPhoneString(phonedata);
			}

		}

		@Override
		public int getCount() {

			return mlistdata == null ? 0 : mlistdata.size();
		}

		@Override
		public String getItem(int position) {

			// if()
			return mlistdata == null ? null : mlistdata.get(position);
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {


			ViewHolder holder;

			if (view != null) {
				holder = (ViewHolder) view.getTag();
			} else {
				view = LayoutInflater.from(mContext).inflate(R.layout.item_phone, parent, false);
				holder = new ViewHolder(view);
				view.setTag(holder);
			}

			String tmp = getItem(position);

			if (tmp != null && !TextUtils.isEmpty(tmp)) {

				holder.phonenumber.setText(tmp);
			} else {
				holder.phonenumber.setText("");
			}

			if (position == 0) {
				holder.ll_back.setBackgroundResource(R.drawable.selector_mutiphone_first);
			} else {
				holder.ll_back.setBackgroundResource(R.drawable.selector_mutiphone_centernum);
			}

			holder.ll_back.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					String str = "";
					try {
						str = mAdapter.getItem(position);
						dismiss();
						if (mListener != null) {
							mListener.OnSure(str);
						}
					} catch (Exception e) {

					}
				}
			});

			return view;
		}

		class ViewHolder {

			@BindView(R.id.tv_phone)
			TextView phonenumber;

			@BindView(R.id.ll_back)
			PercentLinearLayout ll_back;

			public ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

		}

	}

}
