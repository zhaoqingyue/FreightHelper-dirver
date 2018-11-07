package com.yunbaba.freighthelper.ui.activity.car;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseButterKnifeActivity;
import com.yunbaba.freighthelper.bean.car.Navi;
import com.yunbaba.freighthelper.manager.CarManager;
import com.yunbaba.freighthelper.utils.CommonTool;
import com.yunbaba.freighthelper.utils.ErrCodeUtil;
import com.yunbaba.freighthelper.utils.GsonTool;
import com.yunbaba.freighthelper.utils.TimeUtils;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.ITaskDetailListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTaskDetail;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTrack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DriveEvaluationActivity extends BaseButterKnifeActivity {

	@BindView(R.id.title_left_img)
	ImageView titleLeftImg;
	@BindView(R.id.title_text)
	TextView titleText;

	@BindView(R.id.tv_travel_detail_start_time)
	TextView mTvStartTime;

	@BindView(R.id.tv_travel_detail_dest_time)
	TextView mTvDestTime;

	@BindView(R.id.tv_drive_evaluation_comment)
	TextView mTvEvaluationComment; // 测评总结语

	@BindView(R.id.tv_drive_evaluation_comment_img)
	ColorArcBar mCommentImg; // 总结图片

	@BindView(R.id.tv_drive_hundred_bend_acceleration_value)
	TextView mTvHundredBendAcceleration; // 百公里弯道加速值

	@BindView(R.id.tv_drive_hundred_brakes_value)
	TextView mTvHundredBrakes; // 百公里急刹车值

	@BindView(R.id.tv_drive_hundred_fuel_chargingn_value)
	TextView mTvHundredFuelChargingn; // 百公里急加油值

	@BindView(R.id.tv_drive_hundred_lane_change_value)
	TextView mTvHundredLaneChange; // 百公里频繁变道值

	@BindView(R.id.tv_drive_hundred_overspeed_value)
	TextView mTvHundredOverspeed; // 百公里超速值

	@BindView(R.id.tv_drive_hundred_turn_value)
	TextView mTvHundredTurnd; // 百公里急转弯

	private Navi mNaviRoute;
	private String mJonStr;
	private double sumMile;// 总里程数量
	private float score;// 得分

	@Override
	public int getLayoutId() {

		return R.layout.activity_drive_evaluation;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initData();
		initView();
	}

	private void initData() {
		titleLeftImg.setVisibility(View.VISIBLE);
		titleText.setText("驾驶测评");

		mJonStr = getIntent().getStringExtra("route");
		if (TextUtils.isEmpty(mJonStr)) {
			finish();
		}
		mNaviRoute = GsonTool.getInstance().fromJson(mJonStr, Navi.class);

		if (mNaviRoute == null) {
			finish();
		}

		if (mNaviRoute != null) {
			WaitingProgressTool.showProgress(this);

			final String carlicense = mNaviRoute.carlicense;
			final String carduid = mNaviRoute.carduid;
			final String serialid = mNaviRoute.serialid;
			/**
			 * 获取行程详情
			 */
			getTaskDetail(carlicense, carduid, serialid);
		}
	}

	private void getTaskDetail(final String carlicense, String carduid, final String serialid) {
		CarManager.getInstance().getTaskDetail(carduid, serialid, new ITaskDetailListener() {

			@Override
			public void onGetResult(int errCode, MtqTaskDetail result) {
				if (errCode == 0 && result != null) {
					List<MtqTrack> temp = result.tracks;
					if (temp != null && temp.size() > 0) {
						for (int i = 0; i < temp.size(); i++) {
							sumMile = sumMile + Double.parseDouble(temp.get(i).mileage);
						}
					}

					score = Float.parseFloat(result.navi.comfortscore);

					parseParameters(result.navi.countjson);

				} else {
					/**
					 * 
					 */
					if (ErrCodeUtil.isNetErrCode(errCode)) {
						Toast.makeText(DriveEvaluationActivity.this, "网络通信出现问题，请稍后再试。", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(DriveEvaluationActivity.this, "获取体检失败，请稍后再试。", Toast.LENGTH_SHORT).show();
					}

				}
				WaitingProgressTool.closeshowProgress();

			}

			@Override
			public void onGetReqKey(String arg0) {


			}
		});
	}

	private void parseParameters(String str) {

		if (TextUtils.isEmpty(str))
			return;

		EvaluationData data = new EvaluationData();
		try {
			JSONObject root = new JSONObject(str);

			if (root.has("207")) {
				data.m207 = root.getString("207");
				try {
					mTvHundredBrakes.setText((int) ((Double.parseDouble(data.m207) / (sumMile / 100))) + "");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if (root.has("208")) {
				data.m208 = root.getString("208");
				try {
					mTvHundredFuelChargingn.setText((int) ((Double.parseDouble(data.m208) / (sumMile / 100))) + "");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if (root.has("212")) {
				data.m212 = root.getString("212");
				try {
					mTvHundredLaneChange.setText(((int) (Double.parseDouble(data.m212) / (sumMile / 100))) + "");
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			if (root.has("214")) {
				data.m214 = root.getString("214");
				try {
					mTvHundredTurnd.setText((int) ((Double.parseDouble(data.m214) / (sumMile / 100))) + "");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if (root.has("102")) {
				data.m102 = root.getString("102");
				try {
					mTvHundredOverspeed.setText((int) ((Double.parseDouble(data.m102) / (sumMile / 100))) + "");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if (root.has("210")) {
				data.m210 = root.getString("210");
				try {
					mTvHundredBendAcceleration.setText((int) ((Double.parseDouble(data.m210) / (sumMile / 100))) + "");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}

		mCommentImg.setRate(score / 100f);
		mCommentImg.setContentString((int) score + "");
		mCommentImg.invalidate();
		if (score < 60) {
			mTvEvaluationComment.setText("本次行程驾驶评测很差,请注意行车安全!");
		} else if (score > 80) {
			mTvEvaluationComment.setText("本次行程驾驶评测良好,请继续保持!");
		} else {
			mTvEvaluationComment.setText("本次行程驾驶评测一般,请注意行车安全!");
		}

	}

	private class EvaluationData {
		public String m207;// 急刹车
		public String m208;// 急加油
		public String m212;// 频繁变道
		public String m214;// 急转弯
		public String m102;// 超速
		public String m210;// 弯道加速
	}

	private void initView() {

		if (mNaviRoute == null) {
			finish();
		}

		if(TextUtils.isEmpty(mNaviRoute.starttime)){
			mTvStartTime.setText("--");
		}else
			mTvStartTime.setText(TimeUtils.stampToFormat(mNaviRoute.starttime, "yyyy/MM/dd HH:mm"));


		if(TextUtils.isEmpty(mNaviRoute.endtime)){
			mTvDestTime.setText("--");
		}else
			mTvDestTime.setText(TimeUtils.stampToFormat(mNaviRoute.endtime, "yyyy/MM/dd HH:mm"));

		mCommentImg.setMaxValue(100);
	}

	@OnClick({ R.id.title_left_img })
	void onClick(View view) {

		if (CommonTool.isFastDoubleClick()) {

			return;
		}

		switch (view.getId()) {
		case R.id.title_left_img:
			finish();
			break;
		}
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

}
