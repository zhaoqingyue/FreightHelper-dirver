package com.yunbaba.freighthelper.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yunbaba.api.map.MapViewAPI;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.ui.customview.CenterImageSpan;

public class FreightLogicTool {

	public static CharSequence getRouteStr(Context context, int freight_type, String preceipt, String pdeliver) {


		SpannableString route;
		if (freight_type == 1) { // 派送

			route = new SpannableString(
					context.getString(R.string.from) + pdeliver + context.getString(R.string.send_to_several_point));

			if (pdeliver != null) {
				// if (route.length() >= (pdeliver.length() + 2)) {

				route.setSpan(SpanSource.mColorSpan, 2, pdeliver.length() + 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				route.setSpan(SpanSource.mSizeSpan, 2, pdeliver.length() + 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

			}

		} else if (freight_type == 2) { // 提货

			route = new SpannableString(context.getString(R.string.take_good_send_to) + preceipt);

			if (preceipt != null) {
				// if (route.length() >= (preceipt.length() + 6)) {
				route.setSpan(SpanSource.mColorSpan, 6, preceipt.length() + 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

				route.setSpan(SpanSource.mSizeSpan, 6, preceipt.length() + 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

			}

		} else { // 提到送

			route = new SpannableString(
					context.getString(R.string.from2) + pdeliver + context.getString(R.string.to) + preceipt);

			if (pdeliver != null && preceipt != null) {

				route.setSpan(SpanSource.mColorSpan1, 2, pdeliver.length() + 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				route.setSpan(SpanSource.mSizeSpan1, 2, pdeliver.length() + 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

				route.setSpan(SpanSource.mColorSpan, 5 + pdeliver.length(), preceipt.length() + 5 + pdeliver.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);

				route.setSpan(SpanSource.mSizeSpan, 5 + pdeliver.length(), preceipt.length() + 5 + pdeliver.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);

			}

			// route.setSpan(SpanSource.mSizeSpan,2,pdeliver.length(),
			// Spannable.SPAN_INCLUSIVE_INCLUSIVE);

			// route.setSpan(SpanSource.mSizeSpan,5+pdeliver.length(),preceipt.length(),
			// Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		}

		return route;
	}

	public static CharSequence getProgressStr(Context context, int pg, int count) {


		if (pg == count)
			return pg + "/" + count;
		String tmp = String.valueOf(pg);
		SpannableString str = new SpannableString(tmp + "/" + count);
		str.setSpan(SpanSource.mColorSpan2, 0, tmp.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		return str;
	}

	public static CharSequence getTimeStr(Context context, String time) {


		SpannableString timestr = new SpannableString(time + context.getString(R.string.issued));
		if (time != null) {

			timestr.setSpan(SpanSource.mColorSpan, 0, time.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		}

		// timestr.setSpan(SpanSource.mSizeSpan2,0,time.length(),
		// Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		return timestr;
	}

	public static CharSequence getTimeStrForTaskList(Context context, String time, long timeStamp) {


		SpannableString timestr = new SpannableString(time + context.getString(R.string.issued));
		if (time != null) {

			long curtime = System.currentTimeMillis() / 1000L;

			// if (curtime >= timeStamp) {
			// timestr.setSpan(SpanSource.mColorSpan4, 0, time.length(),
			// Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			// } else
			if (curtime >= timeStamp - 1800) {
				timestr.setSpan(SpanSource.mColorSpan3, 0, time.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			} else {

				timestr.setSpan(SpanSource.mColorSpan, 0, time.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			}
		}

		// timestr.setSpan(SpanSource.mSizeSpan2,0,time.length(),
		// Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		return timestr;
	}

	public static CharSequence getDepartTimeStr(Context context, final String st) {
		SpannableString str = new SpannableString(context.getString(R.string.departure_time) + st);
		str.setSpan(SpanSource.mColorSpan, 6, st.length() + 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}

	public static CharSequence getGoodPieceNumStr(Context context, final String st) {
		SpannableString str = new SpannableString(context.getString(R.string.piece_num) + st);
		str.setSpan(SpanSource.mColorSpan, 4, st.length() + 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}

	public static CharSequence getGoodBoxNumStr(Context context, final String st) {
		SpannableString str = new SpannableString(context.getString(R.string.box_num) + st);
		str.setSpan(SpanSource.mColorSpan, 4, st.length() + 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}

	public static CharSequence getGoodVolumeStr(Context context, final String st) {
		SpannableString str = new SpannableString(context.getString(R.string.volume) + st);
		str.setSpan(SpanSource.mColorSpan, 4, st.length() + 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}

	public static CharSequence getGoodWeightStr(Context context, final String st) {
		SpannableString str = new SpannableString(context.getString(R.string.weight) + st);
		str.setSpan(SpanSource.mColorSpan, 4, st.length() + 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}

	public static CharSequence formatPointNameStr(String s1,String st,String s2) {
		SpannableStringBuilder str = new SpannableStringBuilder();
		str.append(s1);
		if(!TextUtils.isEmpty(st)) {
			str.append(st); // 追加后面文案
			str.setSpan(SpanSource.mSizeSpan2, s1.length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置后面的字体颜色
		}
		str.append(s2);


		return str;
	}


//	public static CharSequence formatPointNameStr(String s1,String st,String s2,boolean recommend) {
//		SpannableStringBuilder str = new SpannableStringBuilder();
//		str.append(s1);
//
//		if(!TextUtils.isEmpty(st)) {
//			str.append(st); // 追加后面文案
//			str.setSpan(SpanSource.mSizeSpan2, s1.length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置后面的字体颜色
//		}
//		str.append(s2);
//
//		if(recommend){
//
//			int len = str.length();
//			str.append(st);
//
//
//
//		}
//
//		return str;
//	}


	public static CharSequence getDistanceStr(Context context, String st) {

		SpannableString str = new SpannableString(context.getString(R.string.info_total_km) + st);
		// MLog.e("test", st.length()+" "+str.length());.
		if (st != null)
			str.setSpan(SpanSource.mColorSpan, 5, st.length() + 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		return str;
	}

	public static CharSequence getGoodInfoStr(Context context, final String st) {


		SpannableString str = new SpannableString(context.getString(R.string.info_goods) + st);

		// MLog.e("test", st.length()+" "+str.length());
		str.setSpan(SpanSource.mColorSpan, 4, st.length() + 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return str;
	}

	public static CharSequence getFinishHintRouteStr(Context context, String pdeliver, String preceipt,
			int freight_type, int finish_count, int store_count) {

		SpannableString route;
		if (freight_type == 1) { // 派送

			route = new SpannableString("由" + pdeliver + "送货到各个点" + " (已完成" + finish_count + "/" + store_count + ")"

			);

			if (pdeliver != null) {
				// if (route.length() >= (pdeliver.length() + 2)) {

				route.setSpan(SpanSource.mColorSpan, 1, pdeliver.length() + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				route.setSpan(SpanSource.mColorSpan2, pdeliver.length() + 8, route.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);

			}

		} else if (freight_type == 2) { // 提货

			route = new SpannableString("将货物送至" + preceipt + " (已完成" + finish_count + "/" + store_count + ")");

			if (preceipt != null) {
				// if (route.length() >= (preceipt.length() + 6)) {
				route.setSpan(SpanSource.mColorSpan, 5, preceipt.length() + 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

				route.setSpan(SpanSource.mColorSpan2, preceipt.length() + 6, route.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);

			}

		} else { // 提到送

			route = new SpannableString(
					"从" + pdeliver + "到" + preceipt + " (已完成" + finish_count + "/" + store_count + ")");

			if (pdeliver != null && preceipt != null) {

				route.setSpan(SpanSource.mColorSpan1, 1, pdeliver.length() + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

				route.setSpan(SpanSource.mColorSpan, 2 + pdeliver.length(), preceipt.length() + 2 + pdeliver.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);

				route.setSpan(SpanSource.mColorSpan2, preceipt.length() + 3 + pdeliver.length(), route.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);

			}

		}

		return route;
	}

	public static CharSequence getFinishHintTimeStr(Context context, String corpname, long time) {


		String timeStr = TimestampTool.getDate(time * 1000L);

		return corpname + timeStr + "下发.";

	}

	public static CharSequence getStoreNameRecommend(String string) {


		ForegroundColorSpan mColorSpan = new ForegroundColorSpan(Color.parseColor("#ff7800"));

		SpannableStringBuilder builder = new SpannableStringBuilder(string);

		// builder.setSpan(new
		// ForegroundColorSpan(Color.parseColor(beforeColor)), 0,
		// beforeText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置前面的字体颜色
		// builder.setSpan(new AbsoluteSizeSpan(beforeSize, true), 0,
		// beforeText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE); //设置前面的字体大小
		builder.append("(推荐)"); // 追加后面文案
		builder.setSpan(mColorSpan, string.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置后面的字体颜色
		// builder.setSpan(new AbsoluteSizeSpan(afterSize, true),
		// beforeText.length(), builder.length(),
		// Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//设置后面的字体大小
		// tv.setText(builder);

		return builder;
	}

	public static CharSequence getStoreNameNoPosition(CharSequence string) {


		ForegroundColorSpan mColorSpan = SpanSource.mColorSpan4;

		SpannableStringBuilder builder = new SpannableStringBuilder(string);

		builder.append("(缺少位置信息)"); // 追加后面文案
		builder.setSpan(mColorSpan, string.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置后面的字体颜色

		return builder;
	}

	public static CharSequence getStoreNameGetPosition(CharSequence string) {


		ForegroundColorSpan mColorSpan = SpanSource.mColorSpan4;

		SpannableStringBuilder builder = new SpannableStringBuilder(string);

		builder.append("(已获取位置)"); // 追加后面文案
		builder.setSpan(mColorSpan, string.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置后面的字体颜色

		return builder;
	}

	public static CharSequence getStoreNameAddBackPic(Context context, CharSequence route) {


		SpannableStringBuilder builder = new SpannableStringBuilder();
		
		builder.append(route);
		builder.append("  ");
		
		

		Drawable drawable = context.getResources().getDrawable(R.drawable.ic_task_back);
		int Size = getEmojiSize(context);

		drawable.setBounds(0, 0, Size, Size);// 这里设置图片的大小
		CenterImageSpan imageSpan = new CenterImageSpan(drawable);
		// context,R.drawable.ic_task_back,getEmojiSize(context)

		builder.setSpan(imageSpan, route.length() + 1, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		int length = builder.length();

		builder.append(" "); // 追加后面文案
		builder.setSpan(SpanSource.mSizeSpan1, length, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置后面的字体颜色

		return builder;
	}



	public static CharSequence getStoreNameAddPassByPic(Context context, CharSequence route) {


		SpannableStringBuilder builder = new SpannableStringBuilder();

		builder.append(route);
		builder.append("  ");



		Drawable drawable = context.getResources().getDrawable(R.drawable.ic_task_passpoint);
		int Size = getEmojiSize(context);

		drawable.setBounds(0, 0, Size, Size);// 这里设置图片的大小
		CenterImageSpan imageSpan = new CenterImageSpan(drawable);
		// context,R.drawable.ic_task_back,getEmojiSize(context)

		builder.setSpan(imageSpan, route.length() + 1, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int length = builder.length();

		builder.append(" "); // 追加后面文案
		builder.setSpan(SpanSource.mSizeSpan1, length, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置后面的字体颜色

		return builder;
	}


	public static CharSequence getDialogAddressStr(String storeaddr) {

		SpannableStringBuilder builder = new SpannableStringBuilder("地址: ");

		builder.append(storeaddr); // 追加后面文案
		builder.setSpan(SpanSource.mColorSpan, 4, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置后面的字体颜色

		return builder;
	}


	public static CharSequence getTaskPointHint(int count) {

		SpannableStringBuilder builder = new SpannableStringBuilder("您有");

		builder.append(count+""); // 追加后面文案
		builder.setSpan(SpanSource.mColorSpan4, 2, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置后面的字体颜色

		builder.append("个未完成路由点");

		return builder;
	}

	public static CharSequence getSearchResultCountHint(int count) {

		SpannableStringBuilder builder = new SpannableStringBuilder("搜索到 ");

		builder.append(count+""); // 追加后面文案
		builder.setSpan(SpanSource.mColorSpan4, 4, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置后面的字体颜色

		builder.append(" 个结果");

		return builder;
	}



	
	public static CharSequence getDialogKcodeStr(long storex, long storey) {

		SpannableStringBuilder builder = new SpannableStringBuilder("K码: ");

		builder.append(FreightLogicTool
				.splitKcode(MapViewAPI.getInstance().getKcode(storex, storey))); // 追加后面文案
		builder.setSpan(SpanSource.mColorSpan, 4, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置后面的字体颜色

		return builder;
		
	}

	public static CharSequence getDialogNameAndPhoneStr(String namestr, String phonestr) {

		SpannableStringBuilder builder = new SpannableStringBuilder("联系人: ");

		builder.append(namestr); // 追加后面文案
		builder.setSpan(SpanSource.mColorSpan, 5, 5 + namestr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置后面的字体颜色

		builder.append("  电话: ");

		int currentlen = builder.length();
		builder.append(phonestr); // 追加后面文案
		builder.setSpan(SpanSource.mColorSpan1, currentlen, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置后面的字体颜色
		return builder;
	}

	public static String splitKcode(String kcode) {


		if (kcode == null)
			return null;

		if (kcode.length() == 9) {

			return kcode.substring(0, 3) + " " + kcode.substring(3, 6) + " " + kcode.substring(6, 9);

		} else
			return kcode;

	}

	public static CharSequence getDeliverStatus(Context context, String title, int finish_count, int store_count) {

		SpannableString route;
		String tmp = title + "(" + finish_count + "/" + store_count + ")";
		route = new SpannableString(tmp);

		route.setSpan(SpanSource.mColorSpan, 0, tmp.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		route.setSpan(SpanSource.mColorSpan2, title.length() + 1, title.length() + 2,
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		return route;
	}

	public static CharSequence getMoney(String money) {

		SpannableString route;
		String tmp = "代收货款：";
		String text = tmp + money + "元";
		route = new SpannableString(text);

		route.setSpan(SpanSource.mColorSpan, tmp.length(), text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		return route;
	}

	public static CharSequence getMapPointPosition(String storesort, String size, String s) {



		SpannableStringBuilder builder = new SpannableStringBuilder(storesort+"");

		builder.setSpan(SpanSource.mColorSpan5, 0,  storesort.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		builder.append(size); // 追加后面文案
	//	builder.setSpan(SpanSource.mColorSpan, 5, 5 + namestr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置后面的字体颜色
		int currentlen = builder.length();
		builder.append(s);


		builder.setSpan(SpanSource.mColorSpan, currentlen, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置后面的字体颜色
		return builder;

	}



	public static CharSequence getMapPointPosition2(String storesort,String size, String code,String s2) {



		SpannableStringBuilder builder = new SpannableStringBuilder(storesort+"");

		builder.setSpan(SpanSource.mColorSpan5, 0,  storesort.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		int currentlen3= builder.length();
		builder.append(size); // 追加后面文案

		builder.setSpan(SpanSource.mColorSpan6, currentlen3, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		//	builder.setSpan(SpanSource.mColorSpan, 5, 5 + namestr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置后面的字体颜色
	//	int currentlen = builder.length();
//		builder.append(s1);
//
//
//		builder.setSpan(SpanSource.mColorSpan, currentlen, builder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // 设置后面的字体颜色

		int currentlen2 = builder.length();
		builder.append(code); // 追加后面文案
		builder.setSpan(SpanSource.mSizeSpan2, currentlen2, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置后面的字体颜色
		// 设

		int currentlen = builder.length();
		builder.append(s2);
	//	builder.setSpan(SpanSource.mColorSpan, currentlen, builder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // 设置后面的字体颜色



		return builder;

	}



	public static CharSequence getPS(long time) {

		SpannableString route;
		String tmp = "说明：";

		String text = "要求在" + TimestampTool.getYearMonthDay(time * 1000) + "日送达";
		route = new SpannableString(tmp + text);

		route.setSpan(SpanSource.mColorSpan, tmp.length(), text.length() + tmp.length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		return route;
	}

	/**
	 * @Title: setImgbyOptype
	 * @Description: 根据类型设置图标
	 * @param optype
	 * @param image
	 * @return: void
	 */
	public static void setImgbyOptype(int optype, ImageView image) {
		switch (optype) {
		case 1:
			// 送货
			image.setImageResource(R.drawable.ic_task_deliver);
			break;
		case 3:
			// 提货
			image.setImageResource(R.drawable.ic_task_pickgoods);
			break;
		case 4:
			// 回
			image.setImageResource(R.drawable.ic_task_back);
			break;
		case 5:
			// 必经点
			image.setImageResource(R.drawable.ic_task_passpoint);
			break;
		}
	}

	public static CharSequence getKcodeGetPostionFail(String kcode,String msg) {

		SpannableStringBuilder builder = new SpannableStringBuilder(kcode);

	//	builder.setSpan(SpanSource.mColorSpan5, 0,  storesort.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		int size = builder.length();
		builder.append(msg); // 追加后面文案
		// 设置后面的字体颜色
//		int currentlen = builder.length();
//		builder.append(s);
//
		builder.setSpan(SpanSource.mColorSpan4, size, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		builder.setSpan(SpanSource.mColorSpan, currentlen, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置后面的字体颜色
		return builder;

	}


	private static class SpanSource {

		public static ForegroundColorSpan mColorSpan = new ForegroundColorSpan(Color.parseColor("#222222"));
		public static ForegroundColorSpan mColorSpan1 = new ForegroundColorSpan(Color.parseColor("#222222"));
		public static ForegroundColorSpan mColorSpan2 = new ForegroundColorSpan(Color.parseColor("#0f9e98"));
		public static ForegroundColorSpan mColorSpan5 = new ForegroundColorSpan(Color.parseColor("#ef2c29"));
		public static ForegroundColorSpan mColorSpan6 = new ForegroundColorSpan(Color.parseColor("#7d7d7d"));
	//	public static ForegroundColorSpan mColorSpan7 = new ForegroundColorSpan(Color.parseColor("#666666"));

		public static RelativeSizeSpan mSizeSpan = new RelativeSizeSpan(1.2f);
		public static RelativeSizeSpan mSizeSpan1 = new RelativeSizeSpan(1.2f);
		public static RelativeSizeSpan mSizeSpan2 = new RelativeSizeSpan(1.3f);
		public static ForegroundColorSpan mColorSpan3 = new ForegroundColorSpan(Color.parseColor("#ff7800")); // 警告黄
		public static ForegroundColorSpan mColorSpan4 = new ForegroundColorSpan(Color.parseColor("#FC2828")); // 超时红




		// public static RelativeSizeSpan mSizeSpan2 = new
		// RelativeSizeSpan(1.1f);

	}

	public static int EmojiSize = 0;

	public static int getEmojiSize(Context context) {

		if (EmojiSize <= 0) {
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics outMetrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(outMetrics);

			int mWScreen = outMetrics.widthPixels;
			EmojiSize = (int) (mWScreen * 0.045);

		}

		return EmojiSize;
	}

	public static int getFeedBackReasonSize(Context context) {

		if (EmojiSize <= 0) {
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics outMetrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(outMetrics);

			int mWScreen = outMetrics.widthPixels;
			EmojiSize = (int) (mWScreen * 0.04);

		}

		return EmojiSize;
	}



}
