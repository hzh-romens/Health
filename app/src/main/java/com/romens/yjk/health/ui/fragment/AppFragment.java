package com.romens.yjk.health.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.ui.base.BaseFragment;

/**
 * Created by zhoulisi on 15/4/23.
 */
public abstract class AppFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return onCreateRootView(inflater, container, savedInstanceState);
    }

    protected abstract View onCreateRootView(LayoutInflater inflater, ViewGroup container,
                                             Bundle savedInstanceState);


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onRootViewCreated(view, savedInstanceState);
    }

    protected abstract void onRootViewCreated(View view, Bundle savedInstanceState);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRootActivityCreated(savedInstanceState);
    }

    protected abstract void onRootActivityCreated(Bundle savedInstanceState);

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
