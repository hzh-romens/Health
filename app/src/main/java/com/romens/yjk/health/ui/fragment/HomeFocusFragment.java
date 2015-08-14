package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Components.RecyclerListView;
import com.romens.yjk.health.ui.adapter.FocusAdapter;

/**
 * Created by siery on 15/8/10.
 */
public class HomeFocusFragment extends BaseFragment {
    private RecyclerListView recyclerListView;
    private FocusAdapter focusAdapter;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        focusAdapter = new FocusAdapter(getActivity());
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        recyclerListView = new RecyclerListView(context);
        recyclerListView.setClipToPadding(false);
        recyclerListView.setItemAnimator(null);
        recyclerListView.setLayoutAnimation(null);
        if (Build.VERSION.SDK_INT >= 9) {
            recyclerListView.setOverScrollMode(RecyclerListView.OVER_SCROLL_NEVER);
        }
        content.addView(recyclerListView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        LinearLayoutManager layoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerListView.setLayoutManager(layoutManager);

        return content;
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        recyclerListView.setAdapter(focusAdapter);
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }
}
