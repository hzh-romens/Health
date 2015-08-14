package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Components.RecyclerListView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ADFunctionEntity;
import com.romens.yjk.health.model.ADImageEntity;
import com.romens.yjk.health.model.ADPagerEntity;
import com.romens.yjk.health.ui.adapter.FocusAdapter;
import com.romens.yjk.health.ui.controls.ADBaseControl;
import com.romens.yjk.health.ui.controls.ADFunctionControl;
import com.romens.yjk.health.ui.controls.ADImageControl;
import com.romens.yjk.health.ui.controls.ADPagerControl;

import java.util.ArrayList;
import java.util.List;

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
        onHandleData();
    }

    private void onHandleData() {
        SparseArray<ADBaseControl> controls = new SparseArray<>();
        int keyCursor = 0;
        List<ADPagerEntity> adPagerEntities = new ArrayList<>();
        adPagerEntities.add(new ADPagerEntity("", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
        adPagerEntities.add(new ADPagerEntity("", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
        adPagerEntities.add(new ADPagerEntity("", "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg"));
        adPagerEntities.add(new ADPagerEntity("", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
        adPagerEntities.add(new ADPagerEntity("", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
        adPagerEntities.add(new ADPagerEntity("", "http://img2.imgtn.bdimg.com/it/u=4271709707,3675764479&fm=21&gp=0.jpg"));
        controls.append(keyCursor, new ADPagerControl().bindModel(adPagerEntities));
        keyCursor++;
        List<ADFunctionEntity> adFunctionEntities = new ArrayList<>();
        adFunctionEntities.add(new ADFunctionEntity("", "附近药店", R.drawable.attach_location_states));
        adFunctionEntities.add(new ADFunctionEntity("", "附近药店", R.drawable.attach_location_states));
        adFunctionEntities.add(new ADFunctionEntity("", "附近药店", R.drawable.attach_location_states));
        adFunctionEntities.add(new ADFunctionEntity("", "附近药店", R.drawable.attach_location_states));
        controls.append(keyCursor, new ADFunctionControl().bindModel(adFunctionEntities));
        keyCursor++;
        controls.append(keyCursor, new ADImageControl().bindModel(new ADImageEntity("", "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg")));
        keyCursor++;
        controls.append(keyCursor, new ADImageControl().bindModel(new ADImageEntity("", "http://img2.imgtn.bdimg.com/it/u=4271709707,3675764479&fm=21&gp=0.jpg")));
        focusAdapter.bindData(controls);
    }
}