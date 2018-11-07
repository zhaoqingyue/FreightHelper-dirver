package com.yunbaba.freighthelper.ui.activity.me.contact;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.navisdk.CldNaviManager.RoutePlanPreference;
import com.cld.navisdk.routeplan.CldRoutePlaner.RoutePlanListener;
import com.cld.navisdk.routeplan.RoutePlanNode;
import com.cld.navisdk.routeplan.RoutePlanNode.CoordinateType;
import com.cld.navisdk.util.view.CldProgress;
import com.cld.nv.map.CldMapApi;
import com.yunbaba.api.map.NavigateAPI;
import com.yunbaba.api.trunk.DeliveryApi;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeNoTitleHeightActivity;
import com.yunbaba.freighthelper.ui.activity.RoutePreviewActivity;
import com.yunbaba.freighthelper.utils.CallUtil;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.ols.module.delivery.CldDalKDelivery;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliGroup;
import com.yunbaba.ols.module.delivery.bean.MtqStore;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hmi.packages.HPDefine.HPWPoint;

public class StoreDetailActivity extends BaseButterKnifeNoTitleHeightActivity {

	@BindView(R.id.title_left_img)
	ImageView titleLeftImg;
	@BindView(R.id.title_text)
	TextView titleText;
	@BindView(R.id.title_right_img)
	ImageView titleRightImg;
	@BindView(R.id.title_right_text)
	TextView titleRightText;
	@BindView(R.id.tv_namefirst)
	TextView tvNamefirst;
	@BindView(R.id.tv_storename)
	TextView tvStorename;
	@BindView(R.id.tv_corpname)
	TextView tvCorpname;
	@BindView(R.id.tv_address)
	TextView tvAddress;
	@BindView(R.id.tv_kcode)
	TextView tvKcode;
	@BindView(R.id.tv_contact)
	TextView tvContact;
	@BindView(R.id.tv_phone)
	TextView tvPhone;
	@BindView(R.id.tv_remake)
	TextView tvRemake;
	@BindView(R.id.ll_errorcorrection)
	PercentRelativeLayout llErrorcorrection;
	@BindView(R.id.ll_navi)
	PercentRelativeLayout llNavi;
	@BindView(R.id.ll_call)
	PercentRelativeLayout llCall;

	@BindView(R.id.ll_remark)
	PercentLinearLayout ll_remark;

	MtqStore mStore;

	String corpid, corpname;

	int storetype = 0;

	@Override
	public int getLayoutId() {

		return R.layout.activity_storedetail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: add setContentView(...) invocation
		ButterKnife.bind(this);

		if (getIntent() == null || getIntent().getStringExtra("corpid") == null
				|| getIntent().getStringExtra("corpname") == null || getIntent().getIntExtra("storetype", -1) == -1) {

			finish();
		}

		corpid = getIntent().getStringExtra("corpid");
		corpname = getIntent().getStringExtra("corpname");
		storetype = getIntent().getIntExtra("storetype", 0);

		titleText.setText(getStoreDesc(storetype)+"详情");
		titleLeftImg.setVisibility(View.VISIBLE);

		if (getIntent() == null || getIntent().getStringExtra("store") == null) {

			finish();
		} else {

			try {
				String jsonstr = getIntent().getStringExtra("store");

				mStore = GsonTool.getInstance().fromJson(jsonstr, MtqStore.class);

				if (mStore == null)
					finish();
			} catch (Exception e) {
				finish();
			}

			setInfo();

		}
	}

	private void setInfo() {


		//if (storetype != 3) {

			if (!TextUtils.isEmpty(mStore.storeName))
				tvNamefirst.setText(mStore.storeName.replaceAll("\\s*", "").trim().subSequence(0, 1).toString());
			else
				tvNamefirst.setText("");
//		} else {
//
//			if (!TextUtils.isEmpty(mStore.linkMan))
//				tvNamefirst.setText(mStore.linkMan.replaceAll("\\s*", "").trim().subSequence(0, 1).toString());
//			else
//				tvNamefirst.setText("");
//
//		}

		//if (storetype != 3) {
			tvStorename.setText((TextUtils.isEmpty(mStore.storeCode)?"":(mStore.storeCode+"-"))+mStore.storeName.replaceAll("\\s*", ""));
//		} else
//			tvStorename.setText((TextUtils.isEmpty(mStore.storeCode)?"":(mStore.storeCode+"-"))+mStore.linkMan.replaceAll("\\s*", ""));

		over: for (CldDeliGroup item : CldDalKDelivery.getInstance().getLstOfMyGroups()) {

			if (item.corpId.equals(mStore.corpId)) {
				// mtmplistdata.put(item.corpId, item.corpName);
				tvCorpname.setText(item.corpName);
				break over;
			}
		}

		tvAddress.setText((mStore.regionName+mStore.storeAddr).replaceAll("\\s*", ""));

		tvKcode.setText(SetStrSafety(FreightLogicTool.splitKcode(mStore.kCode)));

		tvContact.setText(mStore.linkMan);

		tvPhone.setText(mStore.linkPhone);

		if (TextUtils.isEmpty(mStore.remark)) {

			ll_remark.setVisibility(View.GONE);

		} else {

			ll_remark.setVisibility(View.VISIBLE);
			tvRemake.setText(mStore.remark);

		}
		
		if (storetype == 1 || storetype == 3){
			llErrorcorrection.setVisibility(View.GONE);
		}
	}

	@OnClick({ R.id.title_left_img, R.id.ll_errorcorrection, R.id.ll_navi, R.id.ll_call })
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_left_img:
			finish();
			break;
		case R.id.ll_errorcorrection:

			if (DeliveryApi.getInstance().isHasAuthForStore(this, corpid, 1)) {

				Intent intent = new Intent(StoreDetailActivity.this, StoreUploadActivity.class);

				intent.putExtra("corpid", corpid);
				intent.putExtra("corpname", corpname);
				intent.putExtra("storetype", storetype);
				intent.putExtra("iscorrect", true);

				intent.putExtra("store", GsonTool.getInstance().toJson(mStore));

				startActivity(intent);

				// finish();
			} else {

				Toast.makeText(StoreDetailActivity.this, "您没有编辑门店的权限，请联系企业开通", Toast.LENGTH_LONG).show();

			}
			break;
		case R.id.ll_navi:

			/**
			 * 调用导航
			 */

			if(mStore.x == 0 && mStore.y == 0){


				Toast.makeText(StoreDetailActivity.this, "缺少位置信息，使用导航失败", Toast.LENGTH_LONG).show();
				return;
			}



			HPWPoint start = CldMapApi.getNMapCenter();

			RoutePlanNode startNode = new RoutePlanNode(start.y, start.x, "我的位置", "", CoordinateType.CLD);
			HPWPoint end = new HPWPoint();
			end.x = mStore.x;
			end.y = mStore.y;
			// CldCoordUtil.kcodeToCLD(mStore.kCode);
			RoutePlanNode endNode = new RoutePlanNode(end.y, end.x, mStore.storeName, mStore.storeAddr,
					CoordinateType.CLD);

			NavigateAPI.getInstance().hyRoutePlan(this, startNode, null, endNode,
					RoutePlanPreference.ROUTE_PLAN_MOD_RECOMMEND|RoutePlanPreference.ROUTE_PLAN_MOD_AVOID_TAFFICJAM, new RoutePlanListener() {

						@Override
						public void onRoutePlanSuccessed() {
							StoreDetailActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									CldProgress.cancelProgress();
									Intent intent = new Intent(StoreDetailActivity.this, RoutePreviewActivity.class);
									startActivity(intent);
								}
							});
						}

						@Override
						public void onRoutePlanFaied(int arg0, String arg1) {
							final String errText = arg1;

							StoreDetailActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {

									CldProgress.cancelProgress();

									Toast.makeText(StoreDetailActivity.this, errText, Toast.LENGTH_LONG).show();
								}
							});
						}

						@Override
						public void onRoutePlanCanceled() {

						}
					});

			break;
		case R.id.ll_call:

			if (TextUtils.isEmpty(mStore.linkPhone)) {

				Toast.makeText(this, "号码为空，无法拨号", Toast.LENGTH_SHORT).show();
				return;
			}

			CallUtil.call(this, mStore.linkPhone);
			break;
		}
	}

	/**
	 * * 动态请求拨打电话权限后，监听用户的点击事件
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
		case 0x11:
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission Granted
				String phone = mStore.linkPhone;
				CallUtil.intentToCall(this, phone);
			} else {
				// Permission Denied
			}
			break;
		default:
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	public CharSequence SetStrSafety(CharSequence str) {

		return str == null ? "" : str;

	}

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
