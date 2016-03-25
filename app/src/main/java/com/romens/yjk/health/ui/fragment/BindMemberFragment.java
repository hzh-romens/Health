package com.romens.yjk.health.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.MemberType;
import com.romens.yjk.health.ui.adapter.NewMemberAdapter;

/**
 * Created by HZH on 2016/2/1.
 */
public class BindMemberFragment extends BaseFragment {
    private RecyclerView listView;
    private NewMemberAdapter adapter;

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(getActivity());
        listView = new RecyclerView(getActivity());
        listView.setBackgroundColor(getResources().getColor(R.color.new_grey));
        content.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setType();
        adapter = new NewMemberAdapter(getActivity());
        adapter.bindType(types);
        listView.setAdapter(adapter);
        return content;
    }

    private SparseIntArray types = new SparseIntArray();

    private void setType() {
        types.append(0, MemberType.TIP);
        types.append(1, MemberType.EMPTY);
        types.append(2, MemberType.PHONE);
        types.append(3, MemberType.PSW);
        types.append(4, MemberType.ADVICE);
        types.append(5, MemberType.BUTTON);
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }
}
