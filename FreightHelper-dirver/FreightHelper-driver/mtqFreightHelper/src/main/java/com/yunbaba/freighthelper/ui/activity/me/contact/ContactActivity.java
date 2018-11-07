package com.yunbaba.freighthelper.ui.activity.me.contact;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.api.car.CarAPI;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.api.trunk.bean.OnResponseResultContainMsg;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.bean.eventbus.AccountEvent;
import com.yunbaba.freighthelper.bean.eventbus.CompanyChangeEvent;
import com.yunbaba.freighthelper.ui.activity.base.BaseActivity;
import com.yunbaba.freighthelper.ui.activity.contacts.ContactsActivity;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.ols.module.delivery.CldDalKDelivery;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ICldAuthInfoListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.AuthInfoList;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliGroup;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliSearchStoreResult;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContactActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "ContactFragment";
	private TextView mTitle;
	private ImageView mNewsImg;
	private TextView mCompany;
	private TextView mStore;
	private TextView mDelivery;
	private TextView mCustomer;
	private ImageView mBack;
	private int mLength;
	List<AuthInfoList> lstOfAuth;
	PercentRelativeLayout pbWaiting;

	@Override
	protected int getLayoutResID() {

		return R.layout.fragment_contacts;
	}

	protected void initViews() {
		mBack = (ImageView) findViewById(R.id.title_left_img);
		mTitle = (TextView) findViewById(R.id.title_text);
		mNewsImg = (ImageView) findViewById(R.id.title_right_img);
		mCompany = (TextView) findViewById(R.id.contacts_company_name);
		mStore = (TextView) findViewById(R.id.contacts_store_name);
		mDelivery = (TextView) findViewById(R.id.contacts_delivery_name);
		mCustomer = (TextView) findViewById(R.id.contacts_customer_name);
		pbWaiting = (PercentRelativeLayout) findViewById(R.id.pb_waiting);
	}

	protected void setListener() {
		// mNewsImg.setOnClickListener(this);
		findViewById(R.id.contacts_company).setOnClickListener(this);
		findViewById(R.id.contacts_store).setOnClickListener(this);
		findViewById(R.id.contacts_delivery).setOnClickListener(this);
		findViewById(R.id.contacts_customer).setOnClickListener(this);
		mBack.setOnClickListener(this);
	}

	protected void initData() {
		mTitle.setText(R.string.main_bottom_contacts);
		mBack.setVisibility(View.VISIBLE);

		mNewsImg.setVisibility(View.GONE);
	}

	protected void loadData() {

		showProgressBar();
		// 门店权限
		CldKDeliveryAPI.getInstance().getAuthInfoList(new ICldAuthInfoListener() {

			@Override
			public void onGetResult(int errCode, List<AuthInfoList> lstOfResult) {

				if (isFinishing())
					return;
				hideProgressBar();


				if (errCode != 0) {
					Toast.makeText(ContactActivity.this, "获取门店权限失败", Toast.LENGTH_LONG).show();
					return;
				}

				lstOfAuth = lstOfResult;


				SPHelper.getInstance(ContactActivity.this).writeStoreAuth(lstOfResult);



			}

			@Override
			public void onGetReqKey(String arg0) {


			}
		});

	}
	
	
	public void LoadStoreNum(){
		
		
		check = 0;
		 getStoreSize(0);
		 //getStoreSize(1);
		 //getStoreSize(3);
	}
	

	@Override
	protected void onResume() {

		super.onResume();
		if(mLength > 0){
			
			CorpBean checkbean =  SPHelper.getInstance(this).ReadStoreSelectCompanyBean();
			
			if(checkbean!=null && checkbean.getCorpId()!=null && !checkbean.getCorpId().equals(currentid.getCorpId())){
				
				currentid =  checkbean;
				
				mStore.setText(R.string.contacts_store);
				mDelivery.setText(R.string.contacts_delivery);
				mCustomer.setText(R.string.contacts_customer);
				
				LoadStoreNum();
				
				
			}
			
			
			
		}
		
	}

	public int check = 0;

	public void getStoreSize(final int storetype) {

		showProgressBar();

		DeliveryApi.getInstance().SearchStore(currentid.getCorpId(), null, -1, storetype, 1, 1, storetype,
				new OnResponseResultContainMsg<CldDeliSearchStoreResult>() {

					@Override
					public void OnResult(CldDeliSearchStoreResult result) {
						if (isFinishing())
							return;

						check += 1;

						if (check >= 3)
							hideProgressBar();

						switch (storetype) {
						case 0:

							mStore.setText("门店(" + result.record + ")");
							break;

						case 1:
							mDelivery.setText("配送中心(" + result.record + ")");
							break;

						case 3:
							mCustomer.setText("客户信息(" + result.record + ")");
							break;

						default:
							break;
						}

						if (storetype == 0)
							getStoreSize(1);
						else if (storetype == 1)
							getStoreSize(3);

					}

					@Override
					public void OnGetTag(String Reqtag) {

						MLog.e("checktag", Reqtag);
					}

					@Override
					public void OnError(int ErrCode,String s) {

						if (isFinishing())
							return;
						check += 1;

						if (check >= 3)
							hideProgressBar();

						if (storetype == 0)
							getStoreSize(1);
						else if (storetype == 1)
							getStoreSize(3);

					}
				});

	}

	// 处理公司变更消息
	@Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
		public synchronized void onCompanyChangeEvent(CompanyChangeEvent event) {

			// mCurCorp = event.getCurrentCorp();
			
			if(event.isDeleteCropHappen){
				
				if(currentid.getCorpId().equals(event.deletecropid)){
					
					updateUI();
				}else{
					String str = getResources().getString(R.string.contacts_company_format);
					mLength = CarAPI.getInstance().getMyGroups().size();
					// MLog.e(TAG, "mLength: " + mLength);
					String companyStr = String.format(str, mLength);
					mCompany.setText(companyStr);
				}
				
				
				
			}


	}

	protected void updateUI() {
		// updateNews();
		String str = getResources().getString(R.string.contacts_company_format);
		mLength = CarAPI.getInstance().getMyGroups().size();
		// MLog.e(TAG, "mLength: " + mLength);
		String companyStr = String.format(str, mLength);
		mCompany.setText(companyStr);

		mStore.setText(R.string.contacts_store);
		mDelivery.setText(R.string.contacts_delivery);
		mCustomer.setText(R.string.contacts_customer);

	
		if (mLength > 0) {
			getFirstCorp();				
			LoadStoreNum();
		}
		

	}
	//
	// private void updateNews() {
	// if (MsgManager.getInstance().hasUnReadMsg()) {
	// mNewsImg.setImageResource(R.drawable.msg_icon_news);
	// } else {
	// mNewsImg.setImageResource(R.drawable.msg_icon);
	// }
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_right_img: {
//			Intent intent = new Intent(this, MsgActivity.class);
//			startActivity(intent);
			break;
		}
		case R.id.title_left_img: {
			finish();
			break;
		}

		case R.id.contacts_company: {
			if (mLength > 0) {
				Intent intent = new Intent(this, ContactsActivity.class);
				startActivity(intent);
			} else
				Toast.makeText(this, "没有企业信息", Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.contacts_store: {
			if (mLength > 0) {
				Intent intent = new Intent(this, StoreListActivity.class);
				intent.putExtra("storetype", 0);
				startActivity(intent);
			} else
				Toast.makeText(this, "没有门店信息", Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.contacts_delivery: {
			if (mLength > 0) {
				Intent intent = new Intent(this, StoreListActivity.class);
				intent.putExtra("storetype", 1);
				startActivity(intent);
			} else
				Toast.makeText(this, "没有配送中心信息", Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.contacts_customer: {
			if (mLength > 0) {
				Intent intent = new Intent(this, StoreListActivity.class);
				intent.putExtra("storetype", 3);
				startActivity(intent);
			} else
				Toast.makeText(this, "没有客户信息", Toast.LENGTH_SHORT).show();
			break;
		}
		default:
			break;
		}
	}

	// @Override
	// public void onStart() {
	// super.onStart();
	// EventBus.getDefault().register(this);
	// }
	//
	// @Override
	// public void onStop() {
	// super.onStop();
	// EventBus.getDefault().unregister(this);
	// }

	// @Subscribe(threadMode = ThreadMode.MAIN)
	// public void onMessageEvent(NewMsgEvent event) {
	// switch (event.msgId) {
	// case MessageId.MSGID_MSG_NEW: {
	// updateNews();
	// break;
	// }
	// default:
	// break;
	// }
	// }

	@Override
	protected void messageEvent(AccountEvent event) {
//		switch (event.msgId) {
//		default:
//			break;
//		}
	}

	protected void showProgressBar() {
		if (pbWaiting != null)
			pbWaiting.setVisibility(View.VISIBLE);
	}

	protected void hideProgressBar() {
		if (pbWaiting != null)
			pbWaiting.setVisibility(View.GONE);
	}

	
	
	
	CorpBean currentid;

	public void getFirstCorp() {

		currentid = SPHelper.getInstance(this).ReadStoreSelectCompanyBean();
		
		MLog.e("read store company", ""+currentid.getCorpId());

		if (currentid == null || TextUtils.isEmpty(currentid.getCorpId())) {

			HashMap<String, String> mtmplistdata = new HashMap<>();
			for (CldDeliGroup item : CldDalKDelivery.getInstance().getLstOfMyGroups()) {

				mtmplistdata.put(item.corpId, item.corpName);

			}

			Iterator<Map.Entry<String, String>> iter = mtmplistdata.entrySet().iterator();
			over: while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();

				String id = entry.getKey();

				// String name = entry.getValue();

				// } else {

				currentid = new CorpBean();

				currentid.setCorpId(id);
				currentid.setCorpName(entry.getValue());

				break over;
			}
			
			SPHelper.getInstance(this).WriteStoreSelectCompanyBean(currentid);

		}
		

	}

}
