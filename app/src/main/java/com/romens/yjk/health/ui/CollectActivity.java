package com.romens.yjk.health.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.FragmentViewPagerAdapter;
import com.romens.android.ui.widget.SlidingFixTabLayout;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.CollectDataDao;
import com.romens.yjk.health.model.CollectDataEntity;
import com.romens.yjk.health.ui.adapter.CollectAdapter;
import com.romens.yjk.health.ui.cells.CollectDrawerCell;
import com.romens.yjk.health.ui.cells.ImageAndTextCell;
import com.romens.yjk.health.ui.cells.IsSelectCell;
import com.romens.yjk.health.ui.fragment.CollectFragment;
import com.romens.yjk.health.ui.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/10/14.
 * 收藏页面
 */
public class CollectActivity extends BaseActivity {

    private SlidingFixTabLayout tabLayout;
    private ViewPager viewPager;
    private CollectPagerAdapter pagerAdapter;

    private final int DRUG_TYPE = 0;
    private final int DRUG_STROY_TYPE = 1;

    private ListView listView;
    private CollectAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private List<CollectDataEntity> entities;

    //    private LinearLayout container;
    private ImageAndTextCell attachView;
    private IsSelectCell isSelectCell;

    private DrawerLayout contentView;
    private FrameLayout rightLayout;

    private String userGuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userGuid = UserGuidConfig.USER_GUID;
        contentView = new DrawerLayout(this);
        contentView.addView(subContentView(), new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT));

        rightLayout = getDrawerLayout();
        DrawerLayout.LayoutParams drawLayoutParams = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);
        drawLayoutParams.gravity = Gravity.RIGHT;
        rightLayout.setLayoutParams(drawLayoutParams);
        contentView.addView(rightLayout);
        setContentView(contentView);
    }

    private FrameLayout getDrawerLayout() {
        FrameLayout drawerLayout = new FrameLayout(this);
        CollectDrawerCell collectDrawerCell = new CollectDrawerCell(this);
        collectDrawerCell.setOnActionBarClickListener(new CollectDrawerCell.onActionBarClickListener() {
            @Override
            public void onItemClick(int itemIndex) {
                if (itemIndex == -1) {
                    contentView.closeDrawer(rightLayout);
                } else if (itemIndex == 0) {
                    Toast.makeText(CollectActivity.this, "click", Toast.LENGTH_SHORT).show();
                }
            }
        });
        drawerLayout.addView(collectDrawerCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return drawerLayout;
    }

    private LinearLayout subContentView() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
//        setContentView(container);
        actionBarEvent(actionBar);
//        addFragment(container);
        // initData();
        addCellView(container);
        refreshContentView();

        return container;
    }

    private void refreshContentView() {
        if (entities != null && entities.size() > 0) {
            attachView.setVisibility(View.GONE);
            isSelectCell.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            refreshLayout.setVisibility(View.GONE);
            isSelectCell.setVisibility(View.GONE);
            attachView.setVisibility(View.VISIBLE);
        }

    }

    private void addCellView(LinearLayout container) {
        attachView = new ImageAndTextCell(this);
        attachView.setImageAndText(R.drawable.no_collect_img, "您还没有关注过药品");
        LinearLayout.LayoutParams layoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.setMargins(AndroidUtilities.dp(0), AndroidUtilities.dp(100), AndroidUtilities.dp(0), AndroidUtilities.dp(0));
        attachView.setLayoutParams(layoutParams);
        container.addView(attachView);
        haveRefreshCollectView(container);
    }

    private void haveRefreshCollectView(LinearLayout container) {
        refreshLayout = new SwipeRefreshLayout(this);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        LinearLayout.LayoutParams refreshlayoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        refreshlayoutParams.weight = 1;
        refreshLayout.setLayoutParams(refreshlayoutParams);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });
        container.addView(refreshLayout);

        listView = new ListView(this);
        listView.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        refreshLayout.addView(listView);
        adapter = new CollectAdapter(this, entities);
        listView.setAdapter(adapter);

        isSelectCell = new IsSelectCell(this);
        isSelectCell.setGravity(Gravity.BOTTOM);
        isSelectCell.setBackgroundColor(Color.TRANSPARENT);
        container.addView(isSelectCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        isSelectCell.setInfo("全选", false);
        isSelectCell.setRightBtnTxt("删除");
        isSelectCell.needTopDivider(true);
        isSelectCell.setViewClick(new IsSelectCell.onViewClick() {
            @Override
            public void onImageViewClick() {
                boolean isSelect = isSelectCell.changeSelect();
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).setIsSelect(isSelect);
                }
                adapter.setEntities(entities);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void rightBtnClick() {
                entities = adapter.getEntities();
                deleteDb(entities);
                requestDelFavour(userGuid, delItem(entities));
                adapter.setEntities(entities);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private String delItem(List<CollectDataEntity> entities) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).isSelect()) {
//                entities.remove(i);
//                i--;
                try {
                    JSONObject object = new JSONObject();
                    object.put("MERCHANDISEID", entities.get(i).getMerchandiseId());
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("tag", "--delCollect-->" + array.toString());
        return array.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        entities = new ArrayList<>();
        requestCollectData(userGuid);
    }

    private void addFragment(ActionBarLayout.LinearLayoutContainer container) {
        tabLayout = new SlidingFixTabLayout(this);
        tabLayout.setBackgroundResource(R.color.theme_primary);
        container.addView(tabLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        FrameLayout frameLayout = new FrameLayout(this);
        viewPager = new ViewPager(this);
        frameLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        container.addView(frameLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        pagerAdapter = new CollectPagerAdapter(getSupportFragmentManager(), initPagerTitle(), initFragment());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setCustomTabView(R.layout.widget_tab_indicator, android.R.id.text1);
        tabLayout.setTabStripBottomBorderThicknessPadding(AndroidUtilities.dp(2));
        tabLayout.setSelectedIndicatorColors(Color.WHITE);
        tabLayout.setDistributeEvenly(true);
        tabLayout.setViewPager(viewPager);
    }

    private void actionBarEvent(ActionBar actionBar) {
        actionBar.setTitle("我的收藏");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
//        ActionBarMenu actionBarMenu = actionBar.createMenu();
//        actionBarMenu.addItem(0, R.drawable.filter_right_img);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                super.onItemClick(i);
                if (i == -1) {
                    finish();
                } /*else if (i == 0) {
                    Toast.makeText(CollectActivity.this, "filter", Toast.LENGTH_SHORT).show();
                    contentView.openDrawer(GravityCompat.END);
                }*/
            }
        });
    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CollectFragment(this, DRUG_TYPE));
        fragments.add(new CollectFragment(this, DRUG_STROY_TYPE));
        return fragments;
    }

    private List<String> initPagerTitle() {
        List<String> titles = new ArrayList<>();
        titles.add("药品");
        titles.add("药店");
        return titles;
    }

    class CollectPagerAdapter extends FragmentViewPagerAdapter {
        private final List<String> mPageTitle = new ArrayList<>();

        public CollectPagerAdapter(FragmentManager fragmentManager, List<String> pageTitles, List<Fragment> fragments) {
            super(fragmentManager, fragments);
            mPageTitle.clear();
            mPageTitle.addAll(pageTitles);
        }

        @Override
        public String getPageTitle(int position) {
            return mPageTitle.get(position);
        }
    }

    //请求收藏
    private void requestCollectData(String userGuid) {
        int lastTime = DBInterface.instance().getCollectDataLastTime();
        Map<String, Object> args = new HashMap<>();
        args.put("USERGUID", userGuid);
        args.put("LASTTIME", lastTime);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "MyFavourite", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
//                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
//                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(CollectActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    setQueryData(responseProtocol.getResponse());
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    private void setQueryData(String response) {
        if (response == null || response.length() < 0) {
            return;
        }
        entities.clear();
        CollectDataDao dao = DBInterface.instance().openWritableDb().getCollectDataDao();
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                CollectDataEntity entity = new CollectDataEntity();
                entity.setMerchandiseId(item.getString("MERCHANDISEID"));
                entity.setMedicineName(item.getString("MEDICINENAME"));
                entity.setMedicineSpec(item.getString("MEDICINESPEC"));
                entity.setShopId(item.getString("SHOPID"));
                entity.setShopName(item.getString("SHOPNAME"));
                entity.setPicBig(item.getString("PICBIG"));
                entity.setPicSmall(item.getString("PICSMALL"));
                entity.setPrice(item.getString("PRICE"));
                entity.setMemberPrice(item.getString("MEMBERPRICE"));
                entity.setAssessCount(item.getString("ASSESSCOUNT"));
                entity.setSaleCount(item.getString("SALECOUNT"));
                entity.setUpdated(item.getInt("CREATEDATE"));
//                entities.add(entity);
                dao.insert(entity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        entities = dao.loadAll();
        refreshContentView();
        adapter.setEntities(entities);
        adapter.notifyDataSetChanged();
    }

    //从本地删除收藏
    private void deleteDb(List<CollectDataEntity> entityList) {
        CollectDataDao dataDao = DBInterface.instance().openWritableDb().getCollectDataDao();
        for (CollectDataEntity entity : entityList) {
            dataDao.delete(entity);
        }
    }

    //访问删除收藏
    private void requestDelFavour(final String userGuid, String jsonData) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("JSONDATA", jsonData);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "DelFavouriate", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(CollectActivity.this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(CollectActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    Log.e("tag", "--collect--->" + responseProtocol.getResponse());
                    String requestCode = "";
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        requestCode = jsonObject.getString("success");
                        Log.e("tag", "--requestCode--->" + requestCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (requestCode.equals("yes")) {
                        Toast.makeText(CollectActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        requestCollectData(userGuid);
                    } else {
                        Toast.makeText(CollectActivity.this, "删除错误", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg == null) {
                } else {
                    Log.e("reqGetAllUsers", "ERROR");
                    Log.e("tag", "--collect--ERROR-->" + errorMsg.msg);
                }
                needHideProgress();
            }
        });
    }
}
