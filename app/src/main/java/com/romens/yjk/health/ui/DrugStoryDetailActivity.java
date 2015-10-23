package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ADImageEntity;
import com.romens.yjk.health.model.ADImageListEntity;
import com.romens.yjk.health.model.DrugStoryDetailEntity;
import com.romens.yjk.health.ui.adapter.DrugStoryDetailAdapter;
import com.romens.yjk.health.ui.cells.DrugStroyBottmImgView;
import com.romens.yjk.health.ui.cells.DrugStroyImageCell;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.romens.yjk.health.ui.adapter.DrugStoryDetailAdapter.*;

/**
 * Created by anlc on 2015/10/17.
 * 药店详情页面
 */
public class DrugStoryDetailActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private DrugStoryDetailAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    private List<Map<String, Object>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intData();
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        actionBarEvent(actionBar);
        setContentView(container, actionBar);

//        DrugStroyBottmImgView bottomImgView = new DrugStroyBottmImgView(this);
//        List<View> testList = new ArrayList<>();
//        testList.add(new DrugStroyImageCell(this));
//        bottomImgView.setData(testList);

//        DrugStroyImageCell cell = new DrugStroyImageCell(this);
//        container.addView(bottomImgView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        refreshLayout = new SwipeRefreshLayout(this);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        container.addView(refreshLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        recyclerView = new RecyclerView(this);
        refreshLayout.addView(recyclerView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        adapter = new DrugStoryDetailAdapter(this, data);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void intData() {
        data = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("titleInfo", "title");
        data.add(map);

        map = new HashMap<>();
        ADImageListEntity entities = new ADImageListEntity();
        entities.addEntity("1234", new ADImageEntity("12", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
        entities.addEntity("13", new ADImageEntity("12", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
        entities.addEntity("24", new ADImageEntity("12", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
        entities.addEntity("13", new ADImageEntity("12", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
        map.put("adImageListEntity", entities);
        data.add(map);

        map = new HashMap<>();
        map.put("firstMenuInfo", "全部商品");
        map.put("secondMenuInfo", "最新上架");
        map.put("threeMenuInfo", "店铺动态");
        data.add(map);

        for (int i = 0; i < 10; i++) {
            map = new HashMap<>();
            map.put("testInfo", "item" + i);
            data.add(map);
        }
    }

    private void actionBarEvent(ActionBar actionBar) {
//        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("药店详情");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
    }
}
