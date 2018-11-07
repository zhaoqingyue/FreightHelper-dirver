package com.yunbaba.freighthelper.ui.activity.me.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.ui.adapter.GroupAdapter;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.ols.module.delivery.CldDalKDelivery;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;

public abstract class StoreListBaseActivity extends BaseButterKnifeActivity {

	ArrayList<CorpBean> companylistdata;

	GroupAdapter mAdapter;

	LayoutInflater mLayoutInflater;

	View HeadView;

	CorpBean curbean;

	TextView tv_currentname;

	// @BindView(R.id.lv_company)
	// ListView lvCompany;


	@Override
	protected void onResume() {
		super.onResume();
		AppStatApi.statOnPageStart(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppStatApi.statOnPagePause(this);
	}

	public void setData(CorpBean selectcorpId) {

		CorpBean currentid = null;
		if(selectcorpId == null)
		   currentid = SPHelper.getInstance(this).ReadStoreSelectCompanyBean();
		else
			currentid = selectcorpId;

		HashMap<String, String> mtmplistdata = new HashMap<>();
		for (CldDeliGroup item : CldDalKDelivery.getInstance().getLstOfMyGroups()) {

			mtmplistdata.put(item.corpId, item.corpName);

		}

		CorpBean bean;
		// CorpBean curbean = null;

		if (currentid != null) {
			curbean = currentid;

		}

		companylistdata.clear();

		Iterator<Map.Entry<String, String>> iter = mtmplistdata.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();

			String id = entry.getKey();

			// String name = entry.getValue();

			// } else {

			bean = new CorpBean();

			bean.setCorpId(id);
			bean.setCorpName(entry.getValue());

			if (curbean == null || curbean.getCorpId() == null)
				curbean = bean;
			else if(!curbean.getCorpId().equals(id)){
				companylistdata.add(bean);
			}

			//
			// bean = new CorpBean();
			//
			// bean.setCorpId(id);
			// bean.setCorpName(entry.getValue());
			//
			// companylistdata.add(bean);
			// }

		}

		if (curbean != null) {

			initHeadFootView(curbean);
			// companylistdata.add(new CorpBean());

		} else {

			initHeadFootView(companylistdata.get(0));

		}
		
		

	}

	public void initHeadFootView(CorpBean curbeans) {

		mLayoutInflater = LayoutInflater.from(this);
		HeadView = mLayoutInflater.inflate(R.layout.view_companylist_head, null);

		tv_currentname = ButterKnife.findById(HeadView, R.id.tv_currentname);
		tv_currentname.setText(curbeans == null ? getString(R.string.all) : curbeans.getCorpName());
		tv_currentname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// finish();
				afterSelectCompany(null);
				
				//RefreshList();
			}
		});
		
		
		
		getCompanyLv().addHeaderView(HeadView, null, false);
		
		

	}
	
	public void RefreshList(){
		
		HashMap<String, String> mtmplistdata = new HashMap<>();
		for (CldDeliGroup item : CldDalKDelivery.getInstance().getLstOfMyGroups()) {

			mtmplistdata.put(item.corpId, item.corpName);

		}

		CorpBean bean;
	

		companylistdata.clear();

		Iterator<Map.Entry<String, String>> iter = mtmplistdata.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();

			String id = entry.getKey();

			// String name = entry.getValue();

			// } else {

			bean = new CorpBean();

			bean.setCorpId(id);
			bean.setCorpName(entry.getValue());

			if (curbean == null)
				curbean = bean;
			else if(!curbean.getCorpId().equals(id)){
				companylistdata.add(bean);
			}
			//
			// bean = new CorpBean();
			//
			// bean.setCorpId(id);
			// bean.setCorpName(entry.getValue());
			//
			// companylistdata.add(bean);
			// }

		}
		
		tv_currentname.setText(curbean == null ? getString(R.string.all) : curbean.getCorpName());
		
		mAdapter.notifyDataSetChanged();
	}

	public void initCompanyListView(CorpBean selectcorpbean) {
		
		
		//getCompanyLv().removeAllViewsInLayout();

		companylistdata = new ArrayList<>();
		
		if(HeadView!=null)
			getCompanyLv().removeHeaderView(HeadView);

		setData(selectcorpbean);

		mAdapter = new GroupAdapter(this, companylistdata);
		
		

		getCompanyLv().setAdapter(mAdapter);

		getCompanyLv().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


				CorpBean bean = (CorpBean) parent.getItemAtPosition(position);
				
			//	RefreshList()
				
				curbean = bean;
				
				SPHelper.getInstance(getApplicationContext()).WriteStoreSelectCompanyBean(bean);
				MLog.e("write store company", bean.getCorpId());
				
				afterSelectCompany(bean);
				
				RefreshList();
				
				
				// if (bean != null) {
				//
				// SPHelper.getInstance(this).WriteCurrentSelectCompanyBean(bean);
				// EventBus.getDefault().post(new CompanyChangeEvent(bean));
				// }

				// finish();

			}
		});
		
		mAdapter.notifyDataSetChanged();

	}

	public abstract ListView getCompanyLv();

	public abstract void afterSelectCompany(CorpBean bean);
	
	
	public String getStoreDesc(int storetype){
		
		String desc ;
		
		switch (storetype) {
		case 0:
			desc = "门店";
			break;
		case 1:
			desc = "配送中心";
			break;
		case 3:
			desc = "客户信息";
			break;
		default:
			desc = "门店";
			break;
		}
		
		return desc;
	}

}
