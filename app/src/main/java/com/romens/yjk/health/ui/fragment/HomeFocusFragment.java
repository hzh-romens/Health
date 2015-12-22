package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocation;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.HomeConfig;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.LocationHelper;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.HistoryDao;
import com.romens.yjk.health.db.entity.DiscoveryCollection;
import com.romens.yjk.health.db.entity.HistoryEntity;
import com.romens.yjk.health.helper.MedicareHelper;
import com.romens.yjk.health.model.ADFunctionEntity;
import com.romens.yjk.health.model.ADImageEntity;
import com.romens.yjk.health.model.ADImageListEntity;
import com.romens.yjk.health.model.ADPagerEntity;
import com.romens.yjk.health.model.ADProductEntity;
import com.romens.yjk.health.model.ADProductListEntity;
import com.romens.yjk.health.model.HealthNewsEntity;
import com.romens.yjk.health.ui.adapter.FocusAdapter;
import com.romens.yjk.health.ui.cells.LastLocationCell;
import com.romens.yjk.health.ui.controls.ADBaseControl;
import com.romens.yjk.health.ui.controls.ADFunctionControl;
import com.romens.yjk.health.ui.controls.ADGroupControl;
import com.romens.yjk.health.ui.controls.ADImagesControl;
import com.romens.yjk.health.ui.controls.ADNewsControl;
import com.romens.yjk.health.ui.controls.ADPagerControl;
import com.romens.yjk.health.ui.controls.ADProductsControl;
import com.romens.yjk.health.ui.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by siery on 15/8/10.
 */
public class HomeFocusFragment extends BaseFragment implements AppNotificationCenter.NotificationCenterDelegate {

    private LastLocationCell lastLocationCell;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerListView;
    private FocusAdapter focusAdapter;

    private Thread configHandleThread;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onLastLocationChanged);
        focusAdapter = new FocusAdapter(getActivity());
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setBackgroundColor(ResourcesConfig.greyBackground);

        lastLocationCell = new LastLocationCell(context);
        content.addView(lastLocationCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        refreshLayout = new SwipeRefreshLayout(context);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        content.addView(refreshLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        recyclerListView = new RecyclerView(context);
        recyclerListView.setClipToPadding(false);
        recyclerListView.setItemAnimator(null);
        recyclerListView.setLayoutAnimation(null);
        if (Build.VERSION.SDK_INT >= 9) {
            recyclerListView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        }
        refreshLayout.addView(recyclerListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
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
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HomeConfig.clearCache();
                requestDataChanged();
            }
        });
        recyclerListView.setAdapter(focusAdapter);
    }

    private void changeRefresh(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
        requestDataChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLastLocation();
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onLastLocationChanged);
        super.onDestroy();
    }

    private void requestDataChanged() {
        changeRefresh(true);
        if (HomeConfig.checkCacheValid()) {
            loadConfigFromCache();
            changeRefresh(false);
            return;
        }

        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", UserConfig.getClientUserId());
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetHomeConfig", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(getActivity(), message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                changeRefresh(false);
                loadConfigFromCache();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                changeRefresh(false);
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    handleResponseData(responseProtocol.getResponse(), false);
                } else {
                    loadConfigFromCache();
                }
            }
        });
    }

    private void loadConfigFromCache() {
        String config = HomeConfig.loadConfig();
        handleResponseData(config, true);
    }

    private void onHandleData() {
        SparseArray<ADBaseControl> controls = new SparseArray<>();
//        int keyCursor = 0;
//        List<ADPagerEntity> adPagerEntities = new ArrayList<>();
//        adPagerEntities.add(new ADPagerEntity("", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
//        adPagerEntities.add(new ADPagerEntity("", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
//        adPagerEntities.add(new ADPagerEntity("", "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg"));
//        adPagerEntities.add(new ADPagerEntity("", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
//        adPagerEntities.add(new ADPagerEntity("", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg"));
//        adPagerEntities.add(new ADPagerEntity("", "http://img2.imgtn.bdimg.com/it/u=4271709707,3675764479&fm=21&gp=0.jpg"));
//        controls.append(keyCursor, new ADPagerControl().bindModel(adPagerEntities));
//        keyCursor++;
//        List<ADFunctionEntity> adFunctionEntities = new ArrayList<>();
//        adFunctionEntities.add(new ADFunctionEntity("", "附近药店", R.drawable.attach_location_states));
//        adFunctionEntities.add(new ADFunctionEntity("", "附近药店", R.drawable.attach_location_states));
//        adFunctionEntities.add(new ADFunctionEntity("", "附近药店", R.drawable.attach_location_states));
//        adFunctionEntities.add(new ADFunctionEntity("", "附近药店", R.drawable.attach_location_states));
//        controls.append(keyCursor, new ADFunctionControl().bindModel(adFunctionEntities));
//
//        keyCursor++;
//        controls.append(keyCursor, new ADEmptyControl());
//
//
//        keyCursor++;
//        controls.append(keyCursor, new ADGroupControl().bindModel("促销优惠", true, R.drawable.more_background_deep_orange));
//
//        keyCursor++;
//        controls.append(keyCursor, new ADImageControl().bindModel(new ADImageEntity("", "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg")));
//
//        keyCursor++;
//        controls.append(keyCursor, new ADGroupControl().bindModel("健康资讯", true, R.drawable.more_background_greeen));
//
//        keyCursor++;
//        controls.append(keyCursor, new ADNewsControl().bindModel("到家啦附近的苦辣解放了卡机了放假啊疯了大家都疯了卡节快乐放假啊是", "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg",true));
//        keyCursor++;
//        controls.append(keyCursor, new ADNewsControl().bindModel("就打开了放假卡里就发了卡离开减肥垃圾疯狂垃圾疯狂啦减肥都来就发了卡解放了空间啊浪费", "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg",false));
//
//        keyCursor++;
//        controls.append(keyCursor, new ADGroupControl().bindModel("为您推荐", true, R.drawable.more_background_orange));
//        keyCursor++;
//        controls.append(keyCursor, new ADProductsControl());
//        focusAdapter.bindData(controls);
    }

    private void handleResponseData(final String json, boolean fromCache) {
        if (!fromCache) {
            HomeConfig.saveConfig(json);
        }
        if (configHandleThread != null && configHandleThread.isAlive()) {
            configHandleThread.interrupt();
        }
        configHandleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final LinkedList<ADBaseControl> controls = asyncHandleConfigJson(json);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        focusAdapter.bindData(controls);
                    }
                });
            }
        });
        configHandleThread.start();
    }

    private LinkedList<ADBaseControl> asyncHandleConfigJson(String json) {
        final LinkedList<ADBaseControl> controls = new LinkedList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
            final int dataSize = jsonArray.length();
            List<ADPagerEntity> adPagerEntities = null;
            List<ADFunctionEntity> adFunctionEntities = null;
            List<HealthNewsEntity> adHealthNewsEntities = null;
            List<ADBaseControl> otherControls = new ArrayList<>();
            JSONObject itemJsonTemp;
            for (int i = 0; i < dataSize; i++) {
                itemJsonTemp = jsonArray.getJSONObject(i);
                int type = itemJsonTemp.getInt("TYPE");
                if (type == 0) {
                    adPagerEntities = createADPagerEntitiesFromJsonObject(itemJsonTemp);
                } else if (type == 1) {
                    adFunctionEntities = createADFunctionEntitiesFromJsonObject(itemJsonTemp);
                } else if (type == 2) {
                    ADImagesControl control = createADImagesControlFromJsonObject(itemJsonTemp);
                    if (control != null) {
                        otherControls.add(control);
                    }
                } else if (type == 3) {
                    ADProductsControl control = createADProductListEntityFromJsonObject(itemJsonTemp);
                    if (control != null) {
                        otherControls.add(control);
                    }
                } else if (type == 4) {
                    adHealthNewsEntities = createADHealthNewsEntitiesFromJsonObject(itemJsonTemp);
                }
            }

            if (adFunctionEntities == null) {
                adFunctionEntities = new ArrayList<>();
                ADFunctionEntity newControl = new ADFunctionEntity("", "最新资讯", R.drawable.attach_new_states);
                newControl.setActionValue("com.romens.rhealth.INFORMATION_NEWS");
                ADFunctionEntity nearControl = new ADFunctionEntity("", "附近药店", R.drawable.attach_location_states);
                nearControl.setActionValue("com.romens.yjk.health.NEARBYPHARMACY");
                ADFunctionEntity sortControl = new ADFunctionEntity("", "扫码识药", R.drawable.attach_sort_states);
                sortControl.setActionValue("com.romens.yjk.health.QRSCANNER");
                ADFunctionEntity remindControl = new ADFunctionEntity("", "用药提醒", R.drawable.attach_remind_states);
                remindControl.setActionValue("com.romens.rhealth.MEDICATIONREMINDERS");
                adFunctionEntities.add(nearControl);
                adFunctionEntities.add(newControl);
                adFunctionEntities.add(sortControl);
                adFunctionEntities.add(remindControl);
            }

            if (adPagerEntities != null && adPagerEntities.size() > 0) {
                controls.add(new ADPagerControl().bindModel(adPagerEntities));
            }
            if (adFunctionEntities != null && adFunctionEntities.size() > 0) {
                controls.add(new ADFunctionControl().bindModel(adFunctionEntities));
            }
            if (adHealthNewsEntities != null && adHealthNewsEntities.size() > 0) {
                controls.add(new ADGroupControl().bindModel("健康资讯", true, R.drawable.more_background_greeen));
                final int entitiesSize = adHealthNewsEntities.size();
                for (int i = 0; i < entitiesSize; i++) {
                    controls.add(new ADNewsControl().bindModel(adHealthNewsEntities.get(i), i < entitiesSize - 1));
                }
            }
            Collections.sort(otherControls, new Comparator<ADBaseControl>() {
                @Override
                public int compare(ADBaseControl lhs, ADBaseControl rhs) {
                    if (lhs.sortIndex > rhs.sortIndex) {
                        return 1;
                    } else if (lhs.sortIndex < rhs.sortIndex) {
                        return -1;
                    }
                    return 0;
                }
            });
            controls.addAll(otherControls);

            ADProductsControl historyControl = createHistoryControl();
            if (historyControl != null) {
                controls.add(historyControl);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return controls;
    }

    private ADProductsControl createHistoryControl() {
        HistoryDao historyDao = DBInterface.instance().openReadableDb().getHistoryDao();
        List<HistoryEntity> historyEntities = historyDao.queryBuilder().limit(3).list();

        ADProductListEntity entity = new ADProductListEntity("", "您浏览过的相关商品", "", "");
        int size = historyEntities == null ? 0 : historyEntities.size();
        if (size >= 3) {
            ADProductEntity entityTemp;
            for (int i = 0; i < size; i++) {
                entityTemp = createADProductEntityFromHistoryEntity(historyEntities.get(i));
                if (entityTemp != null) {
                    entity.addProductEntity(i, entityTemp);
                }
            }
            ADProductsControl control = new ADProductsControl();
            control.bindModel(entity);
            return control;
        }
        return null;
    }

    private ADProductEntity createADProductEntityFromHistoryEntity(HistoryEntity historyEntity) {
        ADProductEntity entity = new ADProductEntity(historyEntity.getGuid()
                , historyEntity.getImgUrl(), historyEntity.getMedicinalName(), "", historyEntity.getCurrentPrice());
        entity.setTag(historyEntity.getShopIp());
        return entity;
    }

    private List<ADPagerEntity> createADPagerEntitiesFromJsonObject(JSONObject jsonObject) {
        List<ADPagerEntity> entities = new ArrayList<>();
        ADPagerEntity entityTemp;
        try {
            //JSONArray jsonArray = jsonObject.getJSONArray("VALUE");
            JSONArray jsonArray = new JSONArray(jsonObject.getString("VALUE"));
            for (int i = 0; i < jsonArray.length(); i++) {
                entityTemp = createADPagerEntityFromJsonObject(jsonArray.getJSONObject(i));
                if (entityTemp != null) {
                    entities.add(entityTemp);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entities;
    }

    private ADPagerEntity createADPagerEntityFromJsonObject(JSONObject jsonObject) {
        try {
            ADPagerEntity entity = new ADPagerEntity(jsonObject.getString("ID"), "", jsonObject.getString("ICONURL"));
            entity.setType(jsonObject.getInt("TYPE"));
            entity.setAction(jsonObject.getString("ACTION"));
            return entity;
        } catch (JSONException e) {
            return null;
        }
    }

    private List<ADFunctionEntity> createADFunctionEntitiesFromJsonObject(JSONObject jsonObject) {
        List<ADFunctionEntity> entities = new ArrayList<>();
        ADFunctionEntity entityTemp;
        try {
            //JSONArray jsonArray = jsonObject.getJSONArray("VALUE");
            JSONArray jsonArray = new JSONArray(jsonObject.getString("VALUE"));
            for (int i = 0; i < jsonArray.length(); i++) {
                entityTemp = createADFunctionEntityFromJsonObject(jsonArray.getJSONObject(i));
                if (entityTemp != null) {
                    entities.add(entityTemp);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entities;
    }

    private ADFunctionEntity createADFunctionEntityFromJsonObject(JSONObject jsonObject) {
        ADFunctionEntity entity = null;
        String id;
        String name = null;
        int resId = -1;
        String actionValue = null;
        try {
            id = jsonObject.getString("ID");
            if (TextUtils.equals("ZXYS", id)) {
                name = DiscoveryCollection.PharmicCounseling.name;
                resId = DiscoveryCollection.PharmicCounseling.iconRes;
                actionValue = DiscoveryCollection.PharmicCounseling.value;
            } else if (TextUtils.equals("YPFL", id)) {
                name = DiscoveryCollection.NearbyPharmacy.name;
                resId = DiscoveryCollection.NearbyPharmacy.iconRes;
                actionValue = DiscoveryCollection.NearbyPharmacy.value;
            } else if (TextUtils.equals("YYTX", id)) {
                name = DiscoveryCollection.MedicationReminders.name;
                resId = DiscoveryCollection.MedicationReminders.iconRes;
                actionValue = DiscoveryCollection.MedicationReminders.value;
            } else if (TextUtils.equals("JKZX", id)) {
                name = DiscoveryCollection.InformationNews.name;
                resId = DiscoveryCollection.InformationNews.iconRes;
                actionValue = DiscoveryCollection.InformationNews.value;
            } else if (TextUtils.equals("YBZQ", id)) {
                name = "医保专区";
                resId = R.drawable.attach_ybzq_states;
                actionValue = MedicareHelper.INTENT_ACTION;
            }
            if ((!TextUtils.isEmpty(name)) && resId != -1) {
                entity = new ADFunctionEntity(id, name, resId);
                entity.setActionValue(actionValue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entity;
    }

    private List<HealthNewsEntity> createADHealthNewsEntitiesFromJsonObject(JSONObject jsonObject) {
        List<HealthNewsEntity> entities = new ArrayList<>();
        HealthNewsEntity entityTemp;
        try {
            //JSONArray jsonArray = jsonObject.getJSONArray("VALUE");
            JSONArray jsonArray = new JSONArray(jsonObject.getString("VALUE"));
            for (int i = 0; i < jsonArray.length(); i++) {
                entityTemp = createADHealthNewsEntityFromJsonObject(jsonArray.getJSONObject(i));
                if (entityTemp != null) {
                    entities.add(entityTemp);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entities;
    }

    private HealthNewsEntity createADHealthNewsEntityFromJsonObject(JSONObject jsonObject) {
        try {
            HealthNewsEntity entity = new HealthNewsEntity(jsonObject.getString("ID"), jsonObject.getString("TEXT")
                    , jsonObject.getString("ICONURL"), jsonObject.getString("VALUE"));
            return entity;
        } catch (JSONException e) {
            return null;
        }
    }

    private ADProductsControl createADProductListEntityFromJsonObject(JSONObject jsonObject) {
        try {
            JSONObject valueJsonObject = new JSONObject(jsonObject.getString("VALUE"));
            if (valueJsonObject == null) {
                return null;
            }
            int sortIndex = jsonObject.getInt("SORTINDEX");
            ADProductListEntity entity = new ADProductListEntity(valueJsonObject.getString("ID"), valueJsonObject.getString("NAME")
                    , valueJsonObject.getString("DESC")
                    , valueJsonObject.getString("ADURL"));
            entity.setLayoutStyle(valueJsonObject.getString("STYLE"));
            JSONArray jsonArray = new JSONArray(valueJsonObject.getString("DATA"));
            int size = jsonArray.length();
            ADProductEntity entityTemp;
            for (int i = 0; i < size; i++) {
                entityTemp = createADProductEntityFromJsonObject(jsonArray.getJSONObject(i));
                if (entityTemp != null) {
                    entity.addProductEntity(i, entityTemp);
                }
            }
            ADProductsControl control = new ADProductsControl();
            control.bindModel(entity);
            control.setSortIndex(sortIndex);
            return control;
        } catch (JSONException e) {
            return null;
        }
    }

    private ADProductEntity createADProductEntityFromJsonObject(JSONObject jsonObject) {
        try {
            //jsonObject.getString("OLDPRICE")
            ADProductEntity entity = new ADProductEntity(jsonObject.getString("ID")
                    , jsonObject.getString("ICONURL"), jsonObject.getString("NAME"), "", jsonObject.getString("PRICE"));
            entity.setTag(jsonObject.getString("TAG"));
            return entity;
        } catch (JSONException e) {
            return null;
        }
    }

    private ADImagesControl createADImagesControlFromJsonObject(JSONObject jsonObject) {
        ADImagesControl control = null;
        try {
            ADImageListEntity imageListEntity = new ADImageListEntity();
            JSONObject valueJsonObject = new JSONObject(jsonObject.getString("VALUE"));
            int sortIndex = jsonObject.getInt("SORTINDEX");

            ADImageEntity entityTemp;
            Iterator<String> keys = valueJsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                entityTemp = createADImageEntityFromJsonObject(valueJsonObject.getJSONObject(key));
                if (entityTemp != null) {
                    imageListEntity.addEntity(key, entityTemp);
                }
            }
            if (imageListEntity.size() > 0) {
                control = new ADImagesControl().bindModel(imageListEntity);
                control.setSortIndex(sortIndex);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return control;
    }

    private ADImageEntity createADImageEntityFromJsonObject(JSONObject jsonObject) {
        try {
            ADImageEntity entity = new ADImageEntity(jsonObject.getString("ID"), jsonObject.getString("ICONURL"));
            entity.setType(jsonObject.getInt("TYPE"));
            entity.setAction(jsonObject.getString("ACTION"));
            return entity;
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.onLastLocationChanged) {
            updateLastLocation();
        }
    }

    private void updateLastLocation() {
        AMapLocation location = LocationHelper.getLastLocation(getActivity());
        String address = location == null ? null : location.getAddress();
        if (TextUtils.isEmpty(address)) {
            address = "无法获取当前位置";
        }
        lastLocationCell.setValue(address);
    }
}
