package com.yunbaba.freighthelper.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunbaba.freighthelper.utils.MLog;

import butterknife.ButterKnife;

/**
 * Created by zhonghm on 2018/5/29.
 */

public   abstract class BaseButterKnifeMajorMainFragment extends MajorMainFragment {

    public abstract int getContentViewId();

    protected Context context;
    protected View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        MLog.e("getList", "initallmember");

        mRootView = inflater.inflate(getContentViewId(), container, false);

        ButterKnife.bind(this, mRootView);// 绑定framgent
        this.context = getActivity();

        initAllMembersView(savedInstanceState);
        return mRootView;
    }

    // @Override
    // public void onCreate(Bundle savedInstanceState) {
    //
    // super.onCreate(savedInstanceState);
    //
    //
    // initAllMembersView(savedInstanceState);
    // }

    protected abstract void initAllMembersView(Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);// 解绑
    }

    public  boolean isRunning = false;

    @Override
    public void onPause() {

        super.onPause();
        isRunning = false;

    }

    @Override
    public void onResume() {

        super.onResume();
        isRunning = true;
    }
}