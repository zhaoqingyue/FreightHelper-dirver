package com.yunbaba.freighthelper.ui.customview;

import android.support.v4.app.Fragment;

public abstract  class LazyLoadFragment extends Fragment {
	    protected boolean isVisible;
	    /**
	     * 在这里实现Fragment数据的缓加载.
	     * @param isVisibleToUser
	     */
	    @Override
	    public void setUserVisibleHint(boolean isVisibleToUser) {
	        super.setUserVisibleHint(isVisibleToUser);
	        if(getUserVisibleHint()) {
	            isVisible = true;
	            onVisible();
	        } else {
	            isVisible = false;
	            onInvisible();
	        }
	    }

	    protected void onVisible(){
	        lazyLoad();
	    }

	    protected abstract void lazyLoad();

	    protected void onInvisible(){}
	
}
