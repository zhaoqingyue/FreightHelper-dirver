package com.yunbaba.freighthelper.ui.dialog;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yunbaba.api.car.CarAPI;
import com.yunbaba.freighthelper.R;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliGroup;

public class ConfirmDialog extends Dialog implements OnClickListener {

	private Context mContext;
	private ListView mListView;
	private Button mOK;
	private IConfirmListener mListener;
	private List<CldDeliGroup> mGroupList;

	public interface IConfirmListener {

		public void OnOK();

		void OnCall(String phone);
	}

	public ConfirmDialog(Context context, IConfirmListener listener) {
		super(context, R.style.dialog_style);
		mContext = context;
		mListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCanceledOnTouchOutside(false);
		getWindow().setContentView(R.layout.dialog_confirm);

		initViews();
		setListener();
		setViews();
	}

	private void initViews() {
		mListView = (ListView) findViewById(R.id.dialog_confiem_listview);
		mOK = (Button) findViewById(R.id.dialog_confiem_ok);
	}

	private void setListener() {
		mOK.setOnClickListener(this);
	}

	private void setViews() {
		if (mGroupList != null) {
			mGroupList.clear();
		}
		mGroupList = CarAPI.getInstance().getMyGroups();
		mListView.setAdapter(new GroupAdapter());
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		switch (v.getId()) {
		case R.id.dialog_confiem_ok: {
			dismiss();
			if (mListener != null) {
				mListener.OnOK();
			}
			break;
		}
		default:
			break;
		}
	}

	public class GroupAdapter extends BaseAdapter {

		public GroupAdapter() {
		}

		@Override
		public int getCount() {
			return mGroupList.size();
		}

		@Override
		public Object getItem(int position) {
			return mGroupList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			CldDeliGroup group = mGroupList.get(position);
			ViewHolder viewHolder = null;
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(mContext).inflate(
						R.layout.item_msg_group, null);
				viewHolder.name = (TextView) view
						.findViewById(R.id.group_item_corp);
				viewHolder.mobile = (TextView) view
						.findViewById(R.id.group_item_mobile);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.name.setText(group.corpName);
			viewHolder.mobile.setText(group.mobile);
			
			final String phone =  group.mobile;
		
			viewHolder.mobile.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {


					
					if (mListener != null) {
						mListener.OnCall(phone);
					}
				}
			});
			
			
			return view;
		}

		final class ViewHolder {
			TextView name;
			TextView mobile;
		}
	}
}
