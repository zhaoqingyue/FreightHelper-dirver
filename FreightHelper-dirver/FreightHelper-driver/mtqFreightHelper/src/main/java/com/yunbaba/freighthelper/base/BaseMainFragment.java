package com.yunbaba.freighthelper.base;

import android.content.Context;

import com.yunbaba.api.appcenter.AppStatApi;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * 懒加载 Created by YoKeyword on 16/6/5.
 */
public abstract class BaseMainFragment extends SupportFragment {
	protected OnBackToFirstListener _mBackToFirstListener;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnBackToFirstListener) {
			_mBackToFirstListener = (OnBackToFirstListener) context;
		} else {
			throw new RuntimeException(context.toString() + " must implement OnBackToFirstListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		_mBackToFirstListener = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		AppStatApi.statOnPageStart(getContext(), this);
	}

	@Override
	public void onPause() {
		super.onPause();
		AppStatApi.statOnPagePause(getContext(), this);
	}

	boolean doubleBackToExitPressedOnce = false;

	/**
	 * 处理回退事件
	 *
	 * @return
	 */
	@Override
	public boolean onBackPressedSupport() {
		if (getChildFragmentManager().getBackStackEntryCount() > 1) {
			popChild();
		} else {

//			if (this instanceof CarFragment || this instanceof MsgFragment || this instanceof MeFragment
//					|| this instanceof TaskFragment) {
//
//				if (doubleBackToExitPressedOnce) {
//					// super.onBackPressed();
//					_mActivity.finish();
//					System.exit(0);
//					return true;
//				}
//
//				this.doubleBackToExitPressedOnce = true;
//				Toast.makeText(_mActivity, "再按一次退出运东东", Toast.LENGTH_SHORT).show();
//
//				new Handler().postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						doubleBackToExitPressedOnce = false;
//					}
//				}, 2000);
//
//			}

			_mActivity.finish();

			// if (
			//// this instanceof CarFragment ||
			//// this instanceof ContactFragment ||
			//// this instanceof MeFragment ||
			// this instanceof TaskFragment) { // 如果是 第一个Fragment 则退出app
			// _mActivity.finish();
			//
			// } else { // 如果不是,则回到第一个Fragment
			// _mBackToFirstListener.onBackToFirstFragment();
			// }
		}
		return true;
	}

	public interface OnBackToFirstListener {
		void onBackToFirstFragment();
	}
}
