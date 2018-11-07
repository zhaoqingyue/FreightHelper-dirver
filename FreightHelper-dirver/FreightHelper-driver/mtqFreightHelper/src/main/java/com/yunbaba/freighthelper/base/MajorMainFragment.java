package com.yunbaba.freighthelper.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yunbaba.api.appcenter.AppStatApi;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.ui.fragment.CarFragment;
import com.yunbaba.freighthelper.ui.fragment.MeFragment;
import com.yunbaba.freighthelper.ui.fragment.MsgFragment;
import com.yunbaba.freighthelper.ui.fragment.task.TaskFragment;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by zhonghm on 2018/5/29.
 */

public class MajorMainFragment  extends SupportFragment {
    protected BaseMainFragment.OnBackToFirstListener _mBackToFirstListener;



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if ( Build.VERSION.SDK_INT >= 14) {
            View statusBar = view.findViewById(R.id.statusBarView);
            ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
            layoutParams.height = getStatusBarHeight();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseMainFragment.OnBackToFirstListener) {
            _mBackToFirstListener = (BaseMainFragment.OnBackToFirstListener) context;
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


    public int getStatusBarHeight() {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }



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

            if (this instanceof CarFragment || this instanceof MsgFragment || this instanceof MeFragment
                    || this instanceof TaskFragment) {

                if (doubleBackToExitPressedOnce) {
                    // super.onBackPressed();
                    _mActivity.finish();
                    System.exit(0);
                    return true;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(_mActivity, "再按一次退出" + getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);

            }

            // _mActivity.finish();

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
