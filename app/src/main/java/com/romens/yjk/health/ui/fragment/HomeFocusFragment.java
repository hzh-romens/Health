package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Components.RecyclerListView;
import com.romens.android.ui.support.widget.LinearLayoutManager;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.HomeConfig;
import com.romens.yjk.health.model.ADFunctionEntity;
import com.romens.yjk.health.model.ADImageEntity;
import com.romens.yjk.health.model.ADPagerEntity;
import com.romens.yjk.health.model.ADProductListEntity;
import com.romens.yjk.health.model.HealthNewsEntity;
import com.romens.yjk.health.ui.adapter.FocusAdapter;
import com.romens.yjk.health.ui.controls.ADBaseControl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by siery on 15/8/10.
 */
public class HomeFocusFragment extends BaseFragment {
    private RecyclerListView recyclerListView;
    private FocusAdapter focusAdapter;

    private Thread configHandleThread;

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
        requestDataChanged();
    }

    private void requestDataChanged() {
        if (HomeConfig.checkCacheValid()) {
            loadConfigFromCache();
            return;
        }
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", "");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetHomeConfig", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(getActivity(), message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                loadConfigFromCache();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
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
            List<ADPagerEntity> adPagerEntities = new ArrayList<>();
            List<ADFunctionEntity> adFunctionEntities = new ArrayList<>();
            List<HealthNewsEntity> adHealthNewsEntities = new ArrayList<>();
            List<ADImageEntity> adPromotionEntities = new ArrayList<>();
            List<ADProductListEntity> adProductListEntities = new ArrayList<>();
            JSONObject itemJsonTemp;
            for (int i = 0; i < dataSize; i++) {
                itemJsonTemp = jsonArray.getJSONObject(i);
                String type=itemJsonTemp.getString("TYPE");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return controls;
    }
}
