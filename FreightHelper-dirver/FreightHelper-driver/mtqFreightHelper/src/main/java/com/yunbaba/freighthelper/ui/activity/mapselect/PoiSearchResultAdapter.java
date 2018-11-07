package com.yunbaba.freighthelper.ui.activity.mapselect;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cld.mapapi.model.PoiInfo;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.KeywordUtil;
import com.yunbaba.freighthelper.utils.MLog;

import java.util.ArrayList;
import java.util.List;

public class PoiSearchResultAdapter extends BaseAdapter {

	private List<PoiInfo> poiSpecs = new ArrayList<PoiInfo>();
	private LayoutInflater layoutInflater;
	public static final int keycolor = Color.parseColor("#0b928c");

	public PoiSearchResultAdapter(Context context, List<PoiInfo> poiSpecs) {
		if (poiSpecs != null)
			this.poiSpecs.addAll(poiSpecs);

		if (context != null)
			layoutInflater = LayoutInflater.from(context);
	}

	// /**
	// * 重新装载POI数据
	// *
	// * @param poiSpecs
	// * @return void
	// * @author LiangYJ
	// * @date 2015-10-30 上午11:07:49
	// */
	// public void resetPois(List<PoiInfo> poiSpecs) {
	// this.poiSpecs.clear();
	// this.poiSpecs.addAll(poiSpecs);
	// notifyDataSetChanged();
	// }

	/**
	 * 获取POI
	 * 
	 * @param index
	 * @return
	 */
	public PoiInfo getPoiInfo(int index) {
		if (index >= 0 && index < poiSpecs.size()) {
			MLog.v("SearchSDK",
					"lat:" + poiSpecs.get(index).location.latitude + ";lng:" + poiSpecs.get(index).location.longitude);
			return poiSpecs.get(index);
		}

		return null;
	}

	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {

		return poiSpecs.size();
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public PoiInfo getItem(int arg0) {


		if (arg0 < 0)
			return null;

		if (poiSpecs.size() > arg0) {
			return poiSpecs.get(arg0);
		}

		return null;
	}

	/**
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int arg0) {

		return 0;
	}

	/**
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		PoiHolder holder = null;
		if (view == null) {
			if (layoutInflater != null) {
				view = layoutInflater.inflate(R.layout.item_poi, null);
				holder = new PoiHolder();
				holder.txtName = (TextView) view.findViewById(R.id.tv_name);
				holder.txtAddress = (TextView) view.findViewById(R.id.tv_address);
				holder.v_line = (View) view.findViewById(R.id.v_line);
				view.setTag(holder);
			}
		} else {
			holder = (PoiHolder) view.getTag();
		}

		PoiInfo poiSpec = poiSpecs.get(position);
		if (poiSpec != null) {

			if (!TextUtils.isEmpty(keyword)) {

				holder.txtName.setText(KeywordUtil.matcherSearchTitle(keycolor, poiSpec.name, keyword));
				holder.txtAddress.setText(KeywordUtil.matcherSearchTitle(keycolor, poiSpec.address, keyword));

			} else {

				holder.txtName.setText(poiSpec.name);
				holder.txtAddress.setText(poiSpec.address);
			}
		}

		if (position == 0) {

			holder.v_line.setVisibility(View.GONE);

		} else {

			holder.v_line.setVisibility(View.VISIBLE);

		}

		return view;
	}

	private final class PoiHolder {
		public TextView txtName;
		public TextView txtAddress;
		View v_line;
	}

	String keyword;

	/**
	 * 重新装载POI数据
	 * 
	 * @param poiSpecs
	 * @return void
	 * @author LiangYJ
	 * @date 2015-10-30 上午11:07:49
	 */
	public void resetPois(List<PoiInfo> poiSpecs, String keyword) {
		this.poiSpecs.clear();
		this.poiSpecs.addAll(poiSpecs);
		this.keyword = keyword;
		notifyDataSetChanged();
	}

}
