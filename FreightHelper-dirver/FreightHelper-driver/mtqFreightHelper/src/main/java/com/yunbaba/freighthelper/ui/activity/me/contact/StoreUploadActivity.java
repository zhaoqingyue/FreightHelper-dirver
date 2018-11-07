package com.yunbaba.freighthelper.ui.activity.me.contact;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.location.CldLocation;
import com.cld.location.ICldLocationListener;
import com.cld.mapapi.model.LatLng;
import com.cld.nv.location.CldCoordUtil;
import com.yunbaba.api.map.LocationAPI;
import com.yunbaba.api.map.LocationAPI.MTQLocationMode;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.AddressBean;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.ui.activity.mapselect.MapSelectPointActivity;
import com.yunbaba.freighthelper.ui.activity.task.ImagePagerActivity;
import com.yunbaba.freighthelper.ui.adapter.PicGridViewAdapter;
import com.yunbaba.freighthelper.ui.customview.NoScrollGridView;
import com.yunbaba.freighthelper.ui.customview.SimpleIndexSelectDialog;
import com.yunbaba.freighthelper.ui.customview.SimpleIndexSelectDialog.OnSimpleIndexSelectCallBack;
import com.yunbaba.freighthelper.ui.customview.TaskAskPopUpDialog;
import com.yunbaba.freighthelper.utils.CommonTool;
import com.yunbaba.freighthelper.utils.FileUtils;
import com.yunbaba.freighthelper.utils.FreightLogicTool;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.ImageTools;
import com.yunbaba.freighthelper.utils.InputCheckUtil;
import com.yunbaba.freighthelper.utils.MLog;
import com.yunbaba.freighthelper.utils.PermissionUtil;
import com.yunbaba.freighthelper.utils.TextStringUtil;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IUploadPicListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.CldDeliUploadStoreParm;
import com.yunbaba.ols.module.delivery.bean.MtqStore;
import com.yunbaba.ols.module.delivery.tool.CldKDeviceAPI;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreUploadActivity extends BaseButterKnifeActivity {

	@BindView(R.id.iv_titleleft)
	ImageView ivTitleleft;
	@BindView(R.id.tv_title)
	TextView tvTitle;
	@BindView(R.id.tv_rightright)
	TextView tvRightright;
	@BindView(R.id.tv_company)
	TextView tvCompany;

	@BindView(R.id.tv_pcd)
	TextView tvPcd;
	@BindView(R.id.tv11)
	TextView tv11;
	@BindView(R.id.tv_storeinputhint)
	TextView tvStoreinputhint;

	@BindView(R.id.tv_storecodeinputhint)
	TextView tvStorecodeinputhint;

	@BindView(R.id.et_store)
	EditText etStore;
	@BindView(R.id.tv_position)
	EditText tvPosition;
	@BindView(R.id.et_storecode)
	EditText etStorecode;
	@BindView(R.id.iv_selectposition)
	ImageView ivSelectposition;
	@BindView(R.id.tv_kcode)
	TextView tvKcode;
	@BindView(R.id.ll_kcode)
	PercentLinearLayout llKcode;
	@BindView(R.id.tv13)
	TextView tv13;
	@BindView(R.id.tv_nameinputhint)
	TextView tvNameinputhint;

	@BindView(R.id.tv_pichint)
	TextView tv_pichint;
	@BindView(R.id.et_name)
	EditText etName;
	@BindView(R.id.tv14)
	TextView tv14;
	@BindView(R.id.tv_phoneinputhint)
	TextView tvPhoneinputhint;
	@BindView(R.id.et_phone)
	EditText etPhone;
	@BindView(R.id.gv_pic)
	NoScrollGridView gvPic;
	@BindView(R.id.tv_picnumhint)
	TextView tvPicnumhint;
	@BindView(R.id.et_remark)
	EditText etRemark;
	@BindView(R.id.tv_wordcount)
	TextView tvWordcount;
	@BindView(R.id.pll_remark)
	PercentLinearLayout pllRemark;
	@BindView(R.id.pb_waiting)
	PercentRelativeLayout pbWaiting;
	TaskAskPopUpDialog mPopUpDialog;
	MtqStore mStore;
	boolean isNew = false;
	String corpid, corpname;
	boolean isCorrect = false;

	int storetype = 0;

	AddressBean addressinfo;

	public static final int RequestCode_Address = 111;

	// 拍照
	public static final int IMAGE_CAPTURE = 2;
	// 从相册选择
	public static final int IMAGE_SELECT = 5;

	boolean isAllowUpload = false;
	private boolean isUpdateStore = false;

	// 图片 九宫格适配器
	private PicGridViewAdapter picAdapter;

	// 用于保存图片路径
	private List<String> piclistpath = new ArrayList<String>();

	@Override
	public int getLayoutId() {

		return R.layout.activity_upload_store;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: add setContentView(...) invocation

		if (getIntent() == null || getIntent().getStringExtra("corpid") == null
				|| getIntent().getStringExtra("corpname") == null || getIntent().getIntExtra("storetype", -1) == -1) {

			finish();
		}

		corpid = getIntent().getStringExtra("corpid");
		corpname = getIntent().getStringExtra("corpname");
		storetype = getIntent().getIntExtra("storetype", 0);

		isCorrect = getIntent().getBooleanExtra("iscorrect", false);

		ButterKnife.bind(this);

		tvRightright.setEnabled(false);

		if (getIntent() == null || getIntent().getStringExtra("store") == null) {

			isNew = true;
			setInfo();
		} else {

			try {
				String jsonstr = getIntent().getStringExtra("store");

				mStore = GsonTool.getInstance().fromJson(jsonstr, MtqStore.class);

				if (mStore == null)
					isNew = true;
			} catch (Exception e) {
				isNew = true;
			}

			isNew = false;
			setInfo();

		}

	}

	private void setInfo() {


		tvCompany.setText(corpname);

		setUnderLineAndListener();

		if (isNew) {

			switch (storetype) {
				case 0:
					tvTitle.setText("门店标注");
					tv11.setText("店名");
					tv_pichint.setText("添加照片");
					break;
				case 1:
					tvTitle.setText("配送中心标注");
					tv11.setText("店名");
					tv_pichint.setText("添加照片");
					break;
				case 3:
					tvTitle.setText("客户资料");
					tv11.setText("客户名");
					tv_pichint.setText("添加LOGO");
					break;
				default:
					break;
			}
		} else {

			if (isCorrect) {

				switch (storetype) {
					case 0:
						tvTitle.setText("门店纠错");
						tv11.setText("店名");
						tv_pichint.setText("添加照片");
						break;
					case 1:
						tvTitle.setText("配送中心纠错");
						tv11.setText("店名");
						tv_pichint.setText("添加照片");
						break;
					case 3:
						tvTitle.setText("客户信息纠错");
						tv11.setText("客户名");
						tv_pichint.setText("添加LOGO");
						break;
					default:
						break;
				}
			} else {

				switch (storetype) {
					case 0:
						tvTitle.setText("门店标注");
						tv11.setText("店名");
						tv_pichint.setText("添加照片");
						break;
					case 1:
						tvTitle.setText("配送中心标注");
						tv11.setText("店名");
						tv_pichint.setText("添加照片");
						break;
					case 3:
						tvTitle.setText("客户资料");
						tv11.setText("客户名");
						tv_pichint.setText("添加LOGO");
						break;
					default:
						break;
				}
			}

			if (!TextUtils.isEmpty(mStore.storeName))
				etStore.setText(mStore.storeName);

			if (!TextUtils.isEmpty(mStore.linkMan))
				etName.setText(mStore.linkMan);

			if (!TextUtils.isEmpty(mStore.linkPhone))
				etPhone.setText(mStore.linkPhone);

			if (!TextUtils.isEmpty(mStore.remark))
				etRemark.setText(mStore.remark);

			if (!TextUtils.isEmpty(mStore.storeCode))
				etStorecode.setText(mStore.storeCode);

		}

		// test

		// addressinfo = new AddressBean();
		// addressinfo.kcode = "85z88xp5g";
		// addressinfo.address = "广东省深圳市宝安区龙华地铁站";
		// tvPosition.setText(addressinfo.address);
		// isAllowUpload();

		ClearFocus();

		if (!isNew && isCorrect) {

			addressinfo = new AddressBean();
			addressinfo.address = mStore.regionName + mStore.storeAddr;
			addressinfo.uploadAddress = mStore.storeAddr;
			addressinfo.pcd = mStore.regionName.replaceAll("\\s*", "");
			addressinfo.kcode = mStore.kCode;

			tvPosition.setText(addressinfo.uploadAddress);
			tvPcd.setText(addressinfo.pcd);

			tvPcd.setVisibility(View.VISIBLE);
			tvKcode.setText(FreightLogicTool.splitKcode(addressinfo.kcode));
			llKcode.setVisibility(View.VISIBLE);

			tvPosition.setEnabled(true);

		} else
			location();

		if (!isNew || isCorrect) {
			etStorecode.setEnabled(false);
			etStorecode.setFocusable(false);
			etStorecode.setKeyListener(null);
			etStorecode.addTextChangedListener(null);

		}

	}

	private void ClearFocus() {

		etName.clearFocus(); // 清除焦点
		etRemark.clearFocus(); // 清除焦点
		etPhone.clearFocus(); // 清除焦点
		etStore.clearFocus(); // 清除焦点
		etStorecode.clearFocus();
		tvPosition.clearFocus();
	}

	/**
	 * 选择图片 1 回单 2 货物
	 */
	public void selectPic(final int type) {

		if (PermissionUtil.isNeedPermissionForStorage(this)) {

			Toast.makeText(this, "请打开储存空间权限", Toast.LENGTH_SHORT).show();
			return;

		}

		SimpleIndexSelectDialog.ShowSelectDialog(StoreUploadActivity.this, new String[]{"拍照", "图库"}, true,
				new OnSimpleIndexSelectCallBack() {

					@Override
					public void OnIndexSelect(int index, String select) {

						switch (index) {
							case 0:// 拍照

								if (PermissionUtil.isNeedPermission(StoreUploadActivity.this, Manifest.permission.CAMERA)) {

									Toast.makeText(StoreUploadActivity.this, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
									return;

								}

								captureImage(MainApplication.getTmpCacheFilePath(), -1);

								break;
							case 1:// 从图库选择
								selectImage(-1);

								break;

							default:
								break;
						}
					}
				});

	}

	public void setUnderLineAndListener() {

		// tvSelectposition.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		etName.addTextChangedListener(new mTextWatcher(1));

		etPhone.addTextChangedListener(new mTextWatcher(2));
		etStore.addTextChangedListener(new mTextWatcher(3));
		etRemark.addTextChangedListener(new mTextWatcher(4));
		tvPosition.addTextChangedListener(new mTextWatcher(5));
		etStorecode.addTextChangedListener(new mTextWatcher(6));

		picAdapter = new PicGridViewAdapter(this, piclistpath, 3, 2, corpid, null);

		gvPic.setAdapter(picAdapter);
		picAdapter.notifyDataSetChanged();

		gvPic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (position == piclistpath.size())
					selectPic(2);
				else {

					modifyPic(2, position);
					// imageBrower(position, (ArrayList<String>) piclistpath);

				}
			}
		});

		etRemark.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			// private int selectionStart;
			// private int selectionEnd;

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s;
				if (temp == null)
					temp = "";

			}

			public void afterTextChanged(Editable s) {

				tvWordcount.setText(temp.length() + "/300");

			}
		});

		CommonTool.setEditTextInhibitInputSpace(tvPosition);

	}

	public class mTextWatcher implements TextWatcher {

		/**
		 * 1联系人 2电话 3门店名 4备注
		 */
		int type;

		public mTextWatcher(int type) {
			// TODO Auto-generated constructor stub
			this.type = type;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {


		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {


		}

		@Override
		public void afterTextChanged(Editable s) {

			if (type < 4 || type == 5 || type == 6) {
				isAllowUpload();
			}

			judgeIsHideHint(type);

			//
			// if (isNew) {
			// // 新增
			//
			//
			//
			// } else {
			// // 纠错
			//
			//
			// }

		}

	}

	public void judgeIsHideHint(int type) {

		switch (type) {
			case 1:
				if (TextUtils.isEmpty(etName.getText())) {
					tvNameinputhint.setVisibility(View.VISIBLE);
				} else {
					tvNameinputhint.setVisibility(View.GONE);
				}
				break;
			case 2:
				if (TextUtils.isEmpty(etPhone.getText())) {
					tvPhoneinputhint.setVisibility(View.VISIBLE);
				} else {
					tvPhoneinputhint.setVisibility(View.GONE);
				}
				break;
			case 3:
				if (TextUtils.isEmpty(etStore.getText())) {
					tvStoreinputhint.setVisibility(View.VISIBLE);
				} else {
					tvStoreinputhint.setVisibility(View.GONE);
				}
				break;
			// case 4:
			// if (TextUtils.isEmpty(etName.getText())) {
			// tvNameinputhint.setVisibility(View.VISIBLE);
			// } else {
			// tvNameinputhint.setVisibility(View.GONE);
			// }
			// break;

			case 6:
				if (TextUtils.isEmpty(etStorecode.getText())) {
					tvStorecodeinputhint.setVisibility(View.VISIBLE);
				} else {
					tvStorecodeinputhint.setVisibility(View.GONE);
				}
				break;

			default:
				break;
		}

	}

	/**
	 * 门店新增类型的可否提交判断
	 */
	public void isAllowUpload() {

		if (!TextUtils.isEmpty(etStore.getText()) && !TextUtils.isEmpty(tvPosition.getText()) && addressinfo != null
				//&& !TextUtils.isEmpty(etName.getText()) && !TextUtils.isEmpty(etPhone.getText())
				&& !TextUtils.isEmpty(tvPosition.getText())) {

			setUploadable(true);

		} else {

			setUploadable(false);

		}

	}

	/**
	 * 是否修改了内容
	 */
	public boolean isModifyInfo() {

		if (isNew) {

			if (!TextUtils.isEmpty(etStore.getText()) || !TextUtils.isEmpty(tvPosition.getText())
					|| !TextUtils.isEmpty(etRemark.getText()) || !TextUtils.isEmpty(etName.getText())
					|| !TextUtils.isEmpty(etPhone.getText()) || piclistpath.size() > 0) {

				return true;

			} else {

				return false;

			}
		} else {

			if (!isEqual(etStore.getText(), mStore.storeName) || !isEqual(etPhone.getText(), mStore.linkPhone)
					|| !isEqual(etName.getText(), mStore.linkMan) || !isEqual(etRemark.getText(), mStore.remark)
					|| !isEqual(tvPosition.getText(), mStore.storeAddr)
					|| !isEqual(etStorecode.getText(), mStore.storeCode) || piclistpath.size() > 0) {
				return true;
			} else
				return false;

		}

	}

	public boolean isEqual(CharSequence a, CharSequence b) {
		if (a == b)
			return true;
		int length;
		if (a != null && b != null && (length = a.length()) == b.length()) {
			if (a instanceof String && b instanceof String) {
				return a.equals(b);
			} else {
				for (int i = 0; i < length; i++) {
					if (a.charAt(i) != b.charAt(i))
						return false;
				}
				return true;
			}
		}
		// else {
		//
		// // 异或
		// if (TextUtils.isEmpty(a) ^ TextUtils.isEmpty(b)) {
		//
		// return false;
		//
		// } else {
		//
		//
		// }
		// }

		if (TextUtils.isEmpty(a) && TextUtils.isEmpty(b))

			return true;
		else
			return false;

	}

	/**
	 * 拍照
	 *
	 * @param path 照片存放的路径
	 */
	public void captureImage(String path, int type) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addCategory("android.intent.category.DEFAULT");
		// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
		//Uri uri = Uri.fromFile(new File(path, "image.jpg"));
		// File file = new File(filepathspaths.xml,"image.jpg");
		// if(!file.exists())
		// file.mkdirs();
		Uri contentUri = FileUtils.getUriForFile(this, new File(path, "image.jpg"));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
				| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		startActivityForResult(intent, IMAGE_CAPTURE + (type + 1) * 1000);
	}

	/**
	 * 从图库中选取图片
	 */
	public void selectImage(int type) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_PICK);
		startActivityForResult(intent, IMAGE_SELECT + (type + 1) * 1000);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// MLog.e("onactivityres", requestCode +" "+requestCode);

		if (resultCode == MapSelectPointActivity.resultCode && requestCode == RequestCode_Address) {

			if (data != null && data.hasExtra("addressinfo") && data.getParcelableExtra("addressinfo") != null) {

				addressinfo = data.getParcelableExtra("addressinfo");

				// MLog.e("selectpointres", addressinfo.address + " " +
				// addressinfo.kcode + " " + addressinfo.uploadAddress+"
				// "+addressinfo.pcd);

				tvPosition.setText(addressinfo.uploadAddress);
				// if (isCorrect) {

				tvKcode.setText(FreightLogicTool.splitKcode(addressinfo.kcode));

				llKcode.setVisibility(View.VISIBLE);
				tvPcd.setText(addressinfo.pcd);

				tvPcd.setVisibility(View.VISIBLE);
				tvPosition.setEnabled(true);
				// }

				// MLog.e("selectpoint", "finish");

				isAllowUpload();
			}

		} else if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {

			String afterCompressPicPath;
			switch (requestCode) {
				case IMAGE_CAPTURE:// 拍照返回

					afterCompressPicPath = ImageTools.compress(StoreUploadActivity.this,
							MainApplication.getTmpCacheFilePath() + "/image.jpg");

					if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

						piclistpath.add(afterCompressPicPath);
						RefreshHint();
						picAdapter.setList(piclistpath);
						// receiptlistpath.add(afterCompressPicPath); //
						// afterCompressPicPath
						// RefreshHint();
						// receiptAdapter.setList(receiptlistpath);
					} else {
						Toast.makeText(StoreUploadActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
					}

					break;
				case IMAGE_SELECT:// 选择照片返回
					// 照片的原始资源地址
					Uri originalUri = data.getData();
					try {

						afterCompressPicPath = null;

						String fp = FileUtils.getRealFilePath(StoreUploadActivity.this, originalUri);

						afterCompressPicPath = ImageTools.compress(StoreUploadActivity.this, fp);

						if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {
							// receiptlistpath.add(afterCompressPicPath); //
							// RefreshHint();
							// receiptAdapter.setList(receiptlistpath);
							piclistpath.add(afterCompressPicPath);
							RefreshHint();
							picAdapter.setList(piclistpath);
						} else {
							Toast.makeText(StoreUploadActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

					break;

				default:
					break;
			}

			int position = 0;

			if (requestCode < 1000)
				return;

			if (requestCode % 1000 == (IMAGE_CAPTURE)) {

				position = requestCode / 1000 - 1;

				if (position < 0)
					return;

				afterCompressPicPath = ImageTools.compress(StoreUploadActivity.this,
						MainApplication.getTmpCacheFilePath() + "/image.jpg");

				if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

					piclistpath.set(position, afterCompressPicPath);
					RefreshHint();
					picAdapter.setList(piclistpath);
					// receiptlistpath.add(afterCompressPicPath); //
					// afterCompressPicPath
					// RefreshHint();
					// receiptAdapter.setList(receiptlistpath);
				} else {
					Toast.makeText(StoreUploadActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
				}

			} else if (requestCode % 1000 == (IMAGE_SELECT)) {

				position = requestCode / 1000 - 1;

				if (position < 0)
					return;

				Uri originalUri = data.getData();
				try {

					afterCompressPicPath = null;

					String fp = FileUtils.getRealFilePath(StoreUploadActivity.this, originalUri);

					afterCompressPicPath = ImageTools.compress(StoreUploadActivity.this, fp);

					if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {
						// receiptlistpath.add(afterCompressPicPath); //
						// RefreshHint();
						// receiptAdapter.setList(receiptlistpath);
						piclistpath.set(position, afterCompressPicPath);
						RefreshHint();
						picAdapter.setList(piclistpath);
					} else {
						Toast.makeText(StoreUploadActivity.this, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	public void UploadInfo() {

		if (!CldPhoneNet.isNetConnected()) {
			Toast.makeText(this, R.string.userinfo_set_failed, Toast.LENGTH_SHORT).show();
			hideProgressBar();
			return;
		}

		if (TextUtils.isEmpty(etStore.getText()) || TextUtils.isEmpty(tvPosition.getText())
				|| addressinfo == null
				&& !TextUtils.isEmpty(tvPosition.getText())) {

			Toast.makeText(this, "门店名、地址是必填项", Toast.LENGTH_SHORT).show();
			hideProgressBar();
			return;
		}

		showProgressBar();

		CldDeliUploadStoreParm parm = new CldDeliUploadStoreParm();

		// parm.address = tvPosition.getText().toString();
		parm.corpid = corpid;

		if (!TextUtils.isEmpty(etRemark.getText()))
			parm.remark = etRemark.getText().toString();

		if (!TextUtils.isEmpty(etName.getText()))
			parm.linkman = etName.getText().toString();

		if (!TextUtils.isEmpty(etPhone.getText()))
			parm.phone = etPhone.getText().toString();

		if (!TextUtils.isEmpty(etStorecode.getText()))
			parm.storecode = etStorecode.getText().toString();

		if (isNew) {
			parm.settype = 1;
			parm.storeid = "0";
		} else {
			parm.settype = 2;
			parm.storeid = mStore.storeId;
		}
		parm.storename = etStore.getText().toString();
		parm.iscenter = storetype;
		parm.storekcode = addressinfo.kcode; // "";//"";//
		parm.address = tvPosition.getText().toString().replaceAll("\\s*", "");

		// || ) ||
		// !TextUtils.isEmpty(etName.getText())
		// || !TextUtils.isEmpty(etPhone.getText())) {

		// if (piclistpath != null && piclistpath.size() > 0) {
		//
		// // parm.uploadPng = new byte[];
		//
		// try {
		// parm.uploadPng = FileUtils.toByteArray(piclistpath.get(0));
		// // ImageTools.getPhotoByteArray(receiptlistpath.get(i));
		// //
		//
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// }
		//
		// }
		parm.extpic = "";

		over:
		for (int i = 0; i < picResponseIdlist.length; i++) {

			String picid = picResponseIdlist[i];

			if (!TextUtils.isEmpty(picid)) {
				if (i != 0) {

					parm.extpic += ";";
				}
				parm.extpic += picid;

			} else {
				break over;
			}

		}

		showProgressBar();

		CldKDeliveryAPI.getInstance().uploadStore(parm, new CldKDeliveryAPI.ICldUploadStoreListListener() {

			@Override
			public void onGetResult(int errCode,String errMsg) {


				if (isFinishing())
					return;
				hideProgressBar();

				if (errCode != 0) {
					Toast.makeText(StoreUploadActivity.this,errMsg == null?"操作失败，请检查网络。":errMsg , Toast.LENGTH_SHORT).show();


					//

				}



				else {

					Toast.makeText(StoreUploadActivity.this, "提交成功!", Toast.LENGTH_SHORT).show();
					finish();

				}

			}

			@Override
			public void onGetReqKey(String tag) {


			}
		});

	}

	/**
	 * 更改，删除
	 */
	public void modifyPic(final int type, final int position) {

		if (PermissionUtil.isNeedPermissionForStorage(this)) {

			Toast.makeText(this, "请打开储存空间权限", Toast.LENGTH_SHORT).show();
			return;

		}

		SimpleIndexSelectDialog.ShowSelectDialog(this, new String[]{"更改-拍照", "更改-图库", "查看", "删除"}, true,
				new OnSimpleIndexSelectCallBack() {

					@Override
					public void OnIndexSelect(int index, String select) {

						switch (index) {
							case 0:// 拍照

								if (PermissionUtil.isNeedPermission(StoreUploadActivity.this, Manifest.permission.CAMERA)) {

									Toast.makeText(StoreUploadActivity.this, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
									return;

								}

								captureImage(MainApplication.getTmpCacheFilePath(), position);

								break;
							case 1:// 从图库选择
								selectImage(position);

								break;
							case 2:// 查看

								imageBrower(position, (ArrayList<String>) piclistpath);
								break;
							case 3:// 删除

								if (piclistpath != null && position < piclistpath.size()) {

									piclistpath.remove(position);
									picAdapter.setList(piclistpath);

								}

								break;
							default:
								break;
						}
					}
				});

	}

	@OnClick({R.id.iv_titleleft, R.id.tv_rightright, R.id.iv_selectposition})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.iv_titleleft:
				if (isModifyInfo()) {

					ShowAskDialog();

				} else
					finish();
				break;
			case R.id.tv_rightright:
				// UploadInfo();

				if (etPhone.getText().length() > 0 && etPhone.getText().length() < 7) {

					hideProgressBar();
					Toast.makeText(this, "请输入有效的电话号码", Toast.LENGTH_SHORT).show();
					return;
				}

				if (!CldPhoneNet.isNetConnected()) {
					Toast.makeText(this, R.string.userinfo_set_failed, Toast.LENGTH_SHORT).show();
					hideProgressBar();
					return;
				}

				if (TextUtils.isEmpty(etStore.getText()) || TextUtils.isEmpty(tvPosition.getText()) || addressinfo == null
						) {
					//&& !TextUtils.isEmpty(etName.getText()) && !TextUtils.isEmpty(etPhone.getText())
					Toast.makeText(this, "门店名、地址是必填项", Toast.LENGTH_SHORT).show();  //联系人和电话、
					hideProgressBar();
					return;
				}

				String upphone = etPhone.getText().toString();

				ArrayList<String> phonelist = TextStringUtil.splitPhoneString(upphone);
				if (!TextUtils.isEmpty(etPhone.getText())) {

					if (phonelist == null || phonelist.size() <= 0) {
						Toast.makeText(this, "请输入有效的电话号码", Toast.LENGTH_SHORT).show();
						hideProgressBar();
						return;
					}

					if (phonelist.size() > 3) {
						Toast.makeText(this, "最多输入3个号码", Toast.LENGTH_SHORT).show();
						hideProgressBar();
						return;
					}

					boolean isFormat = true;

					over:
					for (String tmpphone : phonelist) {

						if (TextUtils.isEmpty(tmpphone)
								|| !(InputCheckUtil.isMobile(tmpphone) || InputCheckUtil.isPhone(tmpphone))) {

							Toast.makeText(this, "请输入有效的电话号码", Toast.LENGTH_SHORT).show();
							hideProgressBar();
							isFormat = false;
							break over;

						}

					}

					if (!isFormat)
						return;

					if (phonelist != null || phonelist.size() > 0) {

						String afterDealPhoneNum = "";

						for (String tmpphone : phonelist) {

							if (!TextUtils.isEmpty(tmpphone)) {

								if (!TextUtils.isEmpty(afterDealPhoneNum))
									afterDealPhoneNum += ",";

								afterDealPhoneNum += tmpphone;
							}

						}

						etPhone.setText(afterDealPhoneNum);
					}
				}
				if (isCorrect) {

					if (!isEqual(etStore.getText(), mStore.storeName) || !isEqual(etPhone.getText(), mStore.linkPhone)
							|| !isEqual(etName.getText(), mStore.linkMan) || !isEqual(etRemark.getText(), mStore.remark)
							|| !isEqual(tvPosition.getText(), mStore.storeAddr)
							|| !isEqual(etStorecode.getText(), mStore.storeCode)) {

					} else {

						if (addressinfo != null && addressinfo.kcode.equals(mStore.kCode)) {

							Toast.makeText(this, "请修改需要纠错的信息后提交", Toast.LENGTH_SHORT).show();
							return;
						}

					}

				}

				if (pbWaiting != null && pbWaiting.getVisibility() == View.GONE)
					UploadPic(picIndexs);
				break;
			case R.id.iv_selectposition:
				if (!FreightConstant.isShowMap) {
					break;
				}
				if (!PermissionUtil.isNeedPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, 108)) {
					Intent it = new Intent(this, MapSelectPointActivity.class);
					startActivityForResult(it, RequestCode_Address);
				}

				break;
		}
	}

	@TargetApi(23)
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == 108) {

			Intent it = new Intent(this, MapSelectPointActivity.class);
			startActivityForResult(it, RequestCode_Address);

			return;
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	protected void showProgressBar() {
		if (pbWaiting != null)
			pbWaiting.setVisibility(View.VISIBLE);
	}

	protected void hideProgressBar() {
		if (pbWaiting != null)
			pbWaiting.setVisibility(View.GONE);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {

				MLog.e("checkedittext", "should hide");
				// if (etStore != null)
				// etStore.clearFocus();
				//
				// if (etName != null)
				// etName.clearFocus();
				//
				// if (etPhone != null)
				// etPhone.clearFocus();
				//
				// if (etRemark != null)
				// etRemark.clearFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}

			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = {0, 0};
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public void setUploadable(boolean allowUpload) {

		if (allowUpload) {

			tvRightright.setTextColor(getResources().getColor(R.color.btn_red));
//            tvRightright.setClickable(true);

			tvRightright.setEnabled(true);
		} else {

			tvRightright.setTextColor(getResources().getColor(R.color.black1));
//            tvRightright.setClickable(false);
			tvRightright.setEnabled(false);
		}


	}

	private void location() {

		showProgressBar();

		LocationAPI.getInstance().location(MTQLocationMode.NETWORK, 3000, this).setLinster(new ICldLocationListener() {
			@Override
			public void onReceiveLocation(CldLocation location) {
				LocationAPI.getInstance().stop();
				onLocation(location);
			}
		});
	}

	private void onLocation(CldLocation location) {

		hideProgressBar();

		MLog.e("yyh", "location = " + location.getLatitude() + location.getLongitude() + location.getProvince()
				+ location.getCity() + location.getDistrict() + location.getAddress() + location.getAdCode());

		LatLng tmp2 = new LatLng(location.getLatitude(), location.getLongitude());
		MLog.e("yyh", "location = " + CldCoordUtil.cldToKCode(tmp2));

		if (location != null && !TextUtils.isEmpty(location.getProvince()) && !TextUtils.isEmpty(location.getCity())) {

			LatLng tmp = new LatLng(location.getLatitude(), location.getLongitude());

			AddressBean bean = new AddressBean();

			bean.kcode = CldCoordUtil.cldToKCode(tmp);

			String address = location.getAddress().replace((location.getProvince().replaceAll("\\s*", "")), "")
					.replace((location.getCity().replaceAll("\\s*", "")), "")
					.replace((location.getDistrict().replaceAll("\\s*", "")), "");

			bean.address = location.getProvince() + location.getCity() + location.getDistrict() + address;
			bean.uploadAddress = address;// location.getDistrict()
			// +
			// location.getAdCode();
			bean.pcd = (location.getProvince() + location.getCity() + location.getDistrict()).replaceAll("\\s*", "");
			addressinfo = bean;

			bean.latitude = location.getLatitude();
			bean.longitude = location.getLongitude();

			tvPosition.setText(bean.uploadAddress);
			// if (isCorrect) {

			tvKcode.setText(FreightLogicTool.splitKcode(addressinfo.kcode));
			llKcode.setVisibility(View.VISIBLE);
			tvPcd.setText(addressinfo.pcd);
			tvPcd.setVisibility(View.VISIBLE);
			tvPosition.setEnabled(true);
			// }

			isAllowUpload();

		}

	}

	@Override
	public void onBackPressed() {
		if (isModifyInfo()) {

			ShowAskDialog();

		} else
			finish();
		// super.onBackPressed();

	}

	public void ShowAskDialog() {

		mPopUpDialog = new TaskAskPopUpDialog(this);

		mPopUpDialog.show();

		mPopUpDialog.setDialogType(8);

		// final MtqDeliStoreDetail detail = mS

		// 选择优先运货到 " + detail.storename + " ,是否继续?"

		mPopUpDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mPopUpDialog.tvLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				mPopUpDialog.dismiss();
			}
		});
		mPopUpDialog.tvRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {


				mPopUpDialog.dismiss();

				finish();

			}
		});

	}

	public String[] picResponseIdlist = new String[3];

	int picIndexs = 0;

	public void UploadPic(final int picIndex) {

		picIndexs = picIndex;

		if (picIndex >= piclistpath.size()) {

			// Toast.makeText(StoreUploadActivity.this, "图片上传完成,开始提交信息",
			// Toast.LENGTH_SHORT).show();
			Toast.makeText(StoreUploadActivity.this, "正在提交", Toast.LENGTH_SHORT).show();
			// showProgressBar();
			hideProgressBar();
			UploadInfo();

			return;
		}

		showProgressBar();

		String base64_pic = null;
		try {
			base64_pic = Base64.encodeToString(FileUtils.toByteArray(piclistpath.get(picIndex)), Base64.DEFAULT);

		} catch (IOException e) {


			e.printStackTrace();
			Toast.makeText(StoreUploadActivity.this, "图片解析失败", Toast.LENGTH_SHORT).show();
		}

		long time = CldKDeviceAPI.getSvrTime();

		CldKDeliveryAPI.getInstance().uploadPicture(corpid, time, 0, 0, base64_pic, new IUploadPicListener() {

			@Override
			public void onGetResult(int errCode, String mediaid) {

				if (isFinishing())
					return;

				if (errCode == 0) {
					picResponseIdlist[picIndex] = mediaid;
					UploadPic((picIndex + 1));
				} else {

					Toast.makeText(StoreUploadActivity.this,

							"操作失败，请检查网络",
							// "图片上传失败，已上传" + (picIndex) + "张，未上传" +
							// (piclistpath.size() - picIndex) + "张",
							Toast.LENGTH_SHORT).show();

					hideProgressBar();

				}

			}

			@Override
			public void onGetReqKey(String arg0) {


			}
		});

	}

	public void RefreshHint() {

		if (piclistpath.size() > 0)
			tvPicnumhint.setVisibility(View.GONE);
		else {
			tvPicnumhint.setVisibility(View.VISIBLE);
			tvPicnumhint.setText("拍张参考照片,我们会优先处理哦~");

		}

	}

	/**
	 * 打开图片查看器
	 *
	 * @param position
	 * @param urls2
	 */
	protected void imageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		intent.putExtra("corpid", corpid);
		intent.putExtra("taskid", "");
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		WaitingProgressTool.closeshowProgress(this);
	}

}
