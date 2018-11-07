package com.yunbaba.freighthelper.ui.activity.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.CorpBean;
import com.yunbaba.freighthelper.bean.eventbus.CompanyChangeEvent;
import com.yunbaba.freighthelper.ui.adapter.GroupAdapter;
import com.yunbaba.freighthelper.utils.SPHelper;
import com.yunbaba.ols.module.delivery.CldDalKDelivery;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectCompanyActivity extends BaseButterKnifeActivity {

	@BindView(R.id.rv_list)
	ListView rv_list;

	@BindView(R.id.tv_title)
	TextView tv_title;

	@BindView(R.id.iv_titleleft)
	ImageView iv_titleleft;

	@BindView(R.id.iv_titleright)
	ImageView iv_titleright;

	// HashMap<String,String> mlistdata;
	ArrayList<CorpBean> mlistdata;

	GroupAdapter mAdapter;

	LayoutInflater mLayoutInflater;

	View HeadView;

	TextView tv_currentname;

	int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// iv_titleleft.setVisibility(View.GONE);
		// TransitionsHeleper.getInstance().setShowMethod(new
		// ColorShowMethod(R.color.white, R.color.white) { //
		// R.color.greeny,R.color.yellow
		// @Override
		// public void loadCopyView(InfoBean bean, final ImageView copyView) {
		// AnimatorSet set = new AnimatorSet();
		// set.playTogether(
		// ObjectAnimator.ofFloat(copyView,"rotation",0,180),
		// ObjectAnimator.ofFloat(copyView, "scaleX", 1, 0),
		// ObjectAnimator.ofFloat(copyView, "scaleY", 1, 0));
		// set.setInterpolator(new AccelerateInterpolator());
		// set.setDuration(duration).start();
		// set.addListener(new AnimatorListener() {
		//
		// @Override
		// public void onAnimationStart(Animator animation) {
		//
		// //copyView.setVisibility(View.GONE);
		//
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animator animation) {
		//
		//
		// }
		//
		// @Override
		// public void onAnimationEnd(Animator animation) {
		//
		// //copyView.setVisibility(View.GONE);
		//
		// }
		//
		// @Override
		// public void onAnimationCancel(Animator animation) {
		//
		//
		// }
		// });
		// }
		//
		// @Override
		// public void loadTargetView(InfoBean bean, ImageView targetView) {
		//
		// }
		// }).show(this, null);

		iv_titleleft.setVisibility(View.VISIBLE);

		tv_title.setText(getString(R.string.switch_company));

		mlistdata = new ArrayList<>();

		setData();

		mAdapter = new GroupAdapter(SelectCompanyActivity.this, mlistdata);

		rv_list.setAdapter(mAdapter);

		rv_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


				CorpBean bean = (CorpBean) parent.getItemAtPosition(position);
				if (bean != null) {

					SPHelper.getInstance(SelectCompanyActivity.this).WriteCurrentSelectCompanyBean(bean);
					EventBus.getDefault().post(new CompanyChangeEvent(bean));
				}

				finish();

			}
		});

	}

	public void setData() {

		CorpBean currentid = SPHelper.getInstance(this).ReadCurrentSelectCompanyBean();

		HashMap<String, String> mtmplistdata = new HashMap<>();
		for (CldDeliGroup item : CldDalKDelivery.getInstance().getLstOfMyGroups()) {

			mtmplistdata.put(item.corpId, item.corpName);

		}

		CorpBean bean;
		CorpBean curbean = null;
		mlistdata.clear();

		Iterator<Map.Entry<String, String>> iter = mtmplistdata.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();

			String id = entry.getKey();

			// String name = entry.getValue();

			if (currentid != null && id.equals(currentid.getCorpId())) {
				curbean = new CorpBean();
				curbean.setCorpId(entry.getKey());
				curbean.setCorpName(entry.getValue());

			} else {
				bean = new CorpBean();

				bean.setCorpId(id);
				bean.setCorpName(entry.getValue());

				mlistdata.add(bean);

				//
				// bean = new CorpBean();
				//
				// bean.setCorpId(id);
				// bean.setCorpName(entry.getValue());
				//
				// mlistdata.add(bean);
			}

		}

		if (curbean != null) {

			initHeadFootView(curbean);
			mlistdata.add(new CorpBean());

		} else {

			initHeadFootView(null);

		}

	}

	public void initHeadFootView(CorpBean curbean) {

		mLayoutInflater = LayoutInflater.from(this);
		HeadView = mLayoutInflater.inflate(R.layout.view_companylist_head, null);

		tv_currentname = ButterKnife.findById(HeadView, R.id.tv_currentname);
		tv_currentname.setText(curbean == null ? getString(R.string.all) : curbean.getCorpName());
		tv_currentname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});

		rv_list.addHeaderView(HeadView, null, false);

	}

	@OnClick(R.id.iv_titleleft)
	public void Finish() {

		this.finish();
	}

	@Override
	public int getLayoutId() {

		return R.layout.activity_selectcompany;
	}

}
