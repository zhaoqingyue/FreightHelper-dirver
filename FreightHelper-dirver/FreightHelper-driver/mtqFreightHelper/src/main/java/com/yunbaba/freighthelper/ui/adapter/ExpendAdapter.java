package com.yunbaba.freighthelper.ui.adapter;

import java.util.ArrayList;

import com.yunbaba.freighthelper.R;

import cld.navi.region.CldRegionEx;
import cld.navi.region.CldRegionEx.CityLevel;
import cld.navi.region.CldRegionEx.ProvinceLevel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpendAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private ArrayList<ProvinceLevel> mProvinceList;

	public ExpendAdapter(Context context) {
		this.mContext = context;
		mProvinceList = CldRegionEx.getInstance().getProvinceList();
	}
	
	public ArrayList<ProvinceLevel> getGroupList() {
		return mProvinceList;
	}

	/**
	 * 获取指定组中的指定子元素数据
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		ProvinceLevel province = mProvinceList.get(groupPosition);
		int id = province.id;
		if (CldRegionEx.getInstance().isMunicipality(id)) {
			// 直辖市：北京、天津、上海、重庆
			return null;
		} else if (CldRegionEx.getInstance().isSpecialDistrict(id)) {
			// 特别行政区：香港、澳门
			return null;
		} else {
			ArrayList<CityLevel> cityList = CldRegionEx.getInstance()
					.getCityListByProvinceId(id);
			return cityList.get(childPosition);
		}
	}

	/**
	 * 获取指定组中的指定子元素ID
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/**
	 * 获取一个视图对象，显示指定组中的指定子元素数据
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildHolder itemHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_area_child, null);
			itemHolder = new ChildHolder();
			itemHolder.city = (TextView) convertView
					.findViewById(R.id.child_city);
			convertView.setTag(itemHolder);
		} else {
			itemHolder = (ChildHolder) convertView.getTag();
		}

		ProvinceLevel province = mProvinceList.get(groupPosition);
		int id = province.id;
		ArrayList<CityLevel> cityList = CldRegionEx.getInstance()
				.getCityListByProvinceId(id);
		CityLevel city = cityList.get(childPosition);
		itemHolder.city.setText(city.name);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		ProvinceLevel province = mProvinceList.get(groupPosition);
		int id = province.id;
		if (CldRegionEx.getInstance().isMunicipality(id)) {
			// 直辖市：北京、天津、上海、重庆
			return 0;
		} else if (CldRegionEx.getInstance().isSpecialDistrict(id)) {
			// 特别行政区：香港、澳门
			return 0;
		}else {
			ArrayList<CityLevel> cityList = CldRegionEx.getInstance()
					.getCityListByProvinceId(id);
			return cityList.size();
		}
	}

	/**
	 * 获取指定组中的数据
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return mProvinceList.get(groupPosition);
	}

	/**
	 * 获取组的个数
	 */
	@Override
	public int getGroupCount() {
		return mProvinceList.size();
	}

	/**
	 * 获取指定组的ID，这个组ID必须是唯一的
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/**
	 * 获取显示指定组的视图对象
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder groupHolder = null;
		if (convertView == null) {
			groupHolder = new GroupHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_area_group, null);
			groupHolder.province = (TextView) convertView
					.findViewById(R.id.group_province);
			groupHolder.img = (ImageView) convertView
					.findViewById(R.id.group_img);
			convertView.setTag(groupHolder);
		} else {
			groupHolder = (GroupHolder) convertView.getTag();
		}

		ProvinceLevel province = mProvinceList.get(groupPosition);
		int id = province.id;
		if (CldRegionEx.getInstance().isMunicipality(id)) {
			// 直辖市：北京、天津、上海、重庆
			groupHolder.img.setVisibility(View.GONE);
		} else if (CldRegionEx.getInstance().isSpecialDistrict(id)) {
			// 特别行政区：香港、澳门
			groupHolder.img.setVisibility(View.GONE);
		}else {
			groupHolder.img.setVisibility(View.VISIBLE);
			if (isExpanded) {
				groupHolder.img.setImageResource(R.drawable.icon_up);
			} else {
				groupHolder.img.setImageResource(R.drawable.icon_down);
			}
		}
		
		groupHolder.province.setText(province.name + province.name_sufix);
		return convertView;
	}

	/**
	 * 组和子元素是否持有稳定的ID, 也就是底层数据的改变不会影响到它们
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	/**
	 * 是否选中指定位置上的子元素
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	class GroupHolder {
		public TextView province;
		public ImageView img;
	}

	class ChildHolder {
		public TextView city;
	}
}
