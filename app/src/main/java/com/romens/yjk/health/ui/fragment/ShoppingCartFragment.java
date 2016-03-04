package com.romens.yjk.health.ui.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.romens.android.AndroidUtilities;
import com.romens.android.io.json.JacksonMapper;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.ShoppingCartDataEntity;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.activity.CommitOrderBaseActivity;
import com.romens.yjk.health.ui.cells.ShoppingCartEmptyCell;
import com.romens.yjk.health.ui.cells.ShoppingCartGoodsCell;
import com.romens.yjk.health.ui.cells.ShoppingCartStoreCell;
import com.romens.yjk.health.ui.cells.ShoppingCartUnLoginCell;
import com.romens.yjk.health.ui.components.CheckableView;
import com.romens.yjk.health.ui.components.ToastCell;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class ShoppingCartFragment extends BaseFragment implements AppNotificationCenter.NotificationCenterDelegate {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView listView;
    private ProgressBarCircularIndeterminate progressBar;

    private FrameLayout bottomBar;
    private CheckableView allCheckView;
    private TextView amountDescView;
    private LinearLayout commitBtn;

    private ListAdapter listAdapter;

    private boolean unLogin = true;
    private boolean emptyShoppingCart = true;
    private boolean isSyncingServerShoppingCart = false;

    private final Map<String, ShoppingCartDataEntity> shoppingCartData = new HashMap<>();
    private final Object changeSync = new Object();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginOut);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginSuccess);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onShoppingCartChanged);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onCommitShoppingCart);
        unLogin = !UserSession.getInstance().isClientLogin();
        listAdapter = new ListAdapter();
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout content = new FrameLayout(context);
        FrameLayout dataContainer = new FrameLayout(context);

        refreshLayout = new SwipeRefreshLayout(context);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        dataContainer.addView(refreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new RecyclerView(context);
        listView.setLayoutManager(new LinearLayoutManager(context));
        refreshLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        progressBar = new ProgressBarCircularIndeterminate(context);
        progressBar.setBackgroundColor(ResourcesConfig.primaryColor);
        progressBar.setVisibility(View.GONE);
        dataContainer.addView(progressBar, LayoutHelper.createFrame(36, 36, Gravity.CENTER));
        content.addView(dataContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 0, 0, 56));

        bottomBar = new FrameLayout(context);
        bottomBar.setBackgroundColor(0xfff0f0f0);
        content.addView(bottomBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 56, Gravity.BOTTOM));

        allCheckView = new CheckableView(context);
        allCheckView.setChecked(false);
        allCheckView.shouldText(true);
        allCheckView.setText("全选");
        bottomBar.addView(allCheckView, LayoutHelper.createFrame(96, 48, Gravity.LEFT | Gravity.CENTER_VERTICAL));

        amountDescView = new TextView(context);
        amountDescView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        AndroidUtilities.setMaterialTypeface(amountDescView);
        amountDescView.setTextColor(0xff212121);
        amountDescView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        bottomBar.addView(amountDescView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 96, 0, 120, 0));

        commitBtn = new LinearLayout(context);
        commitBtn.setOrientation(LinearLayout.HORIZONTAL);
        commitBtn.setGravity(Gravity.CENTER_VERTICAL);
        commitBtn.setClickable(true);
        commitBtn.setBackgroundResource(R.drawable.btn_primary);
        commitBtn.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        ImageView commitIcon = new ImageView(context);
        commitIcon.setScaleType(ImageView.ScaleType.CENTER);
        commitIcon.setImageResource(R.drawable.ic_done_all_white_24dp);
        commitBtn.addView(commitIcon, LayoutHelper.createLinear(24, 24));

        TextView commitText = new TextView(context);
        commitText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        AndroidUtilities.setMaterialTypeface(commitText);
        commitText.setTextColor(0xffffffff);
        commitText.setGravity(Gravity.CENTER);
        commitText.setText("去结算");
        commitBtn.addView(commitText, LayoutHelper.createLinear(56, LayoutHelper.WRAP_CONTENT));
        bottomBar.addView(commitBtn, LayoutHelper.createFrame(96, 40, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 8, 8, 8));
        return content;
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                syncShoppingCartForServer(true);
            }
        });
        allCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allCheckView.toggle();
                boolean checked = allCheckView.isChecked();
                listAdapter.onAllSelectedChanged(checked);
            }
        });
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryCommitSelectGoods();
            }
        });
        updateAmount();
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
        checkShoppingCartState();
        listView.setAdapter(listAdapter);
        syncShoppingCartForDB();
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginOut);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginSuccess);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onShoppingCartChanged);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onCommitShoppingCart);
        super.onDestroy();
    }

    private void tryCommitSelectGoods() {

        if (isSyncingServerShoppingCart) {
            return;
        }
        ArrayList<String> selectGoods = listAdapter.getCheckedItems();
        if (selectGoods == null || selectGoods.size() <= 0) {
            ToastCell.toast(getActivity(), "请至少选择一个商品!");
            return;
        }
        //清除废数据
        ArrayList<String> needCommitGoods = new ArrayList<>();
        for (String id : selectGoods) {
            if (shoppingCartData.containsKey(id)) {
                needCommitGoods.add(id);
            }
        }
        if (needCommitGoods.size() <= 0) {
            ToastCell.toast(getActivity(), "请至少选择一个商品!");
            return;
        }

        String packName = getActivity().getPackageName();
        ComponentName componentName = new ComponentName(packName, packName + ".ui.activity.CommitOrderActivity");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.putExtra(CommitOrderBaseActivity.ARGUMENTS_KEY_SUPPORT_MEDICARE, true);
        intent.putStringArrayListExtra(CommitOrderBaseActivity.ARGUMENTS_KEY_SELECT_GOODS, needCommitGoods);
        startActivity(intent);
    }

    /**
     * 检测当前购物车状态，未登录和空购物车不显示底部结算标签
     */
    private void checkShoppingCartState() {
        if (unLogin) {
            bottomBar.setVisibility(View.INVISIBLE);
        } else if (emptyShoppingCart) {
            bottomBar.setVisibility(View.INVISIBLE);
        } else {
            bottomBar.setVisibility(View.VISIBLE);
        }
    }

    private void syncShoppingCartForDB() {
        shoppingCartData.clear();
        List<ShoppingCartItem> adapterData = new ArrayList<>();
        if (!unLogin) {
            Map<String, ShoppingCartStoreItem> storeItems = new HashMap<>();
            Map<String, List<ShoppingCartDataEntity>> shoppingCartTemp = new HashMap<>();
            List<ShoppingCartDataEntity> data = DBInterface.instance().getCurrClientShoppingCart();
            //遍历数据库购物车商品数据，再加到本页缓存数据中
            if (data != null) {
                int size = data.size();
                ShoppingCartDataEntity temp;
                for (int i = 0; i < size; i++) {
                    temp = data.get(i);
                    shoppingCartData.put(temp.getGuid(), temp);
                    String shopID = temp.getShopID();
                    if (!shoppingCartTemp.containsKey(shopID)) {
                        storeItems.put(shopID, new ShoppingCartStoreItem(shopID, temp.getShopName()));
                        shoppingCartTemp.put(shopID, new ArrayList<ShoppingCartDataEntity>());
                    }
                    shoppingCartTemp.get(shopID).add(temp);
                }
            }
            //处理购物车商品数据，转化为ListView 行对象
            Iterator<Map.Entry<String, ShoppingCartStoreItem>> iterator = storeItems.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ShoppingCartStoreItem> entry = iterator.next();
                ShoppingCartStoreItem value = entry.getValue();
                List<ShoppingCartDataEntity> items = shoppingCartTemp.get(entry.getKey());
                int storePosition = adapterData.size();
                value.setStart(storePosition);
                value.setCount(items.size());
                adapterData.add(value);
                for (ShoppingCartDataEntity item : items) {
                    adapterData.add(new ShoppingCartGoodsItem(item.getGuid(), entry.getKey(), storePosition));
                }
            }
        }
        listAdapter.bindData(adapterData);
        emptyShoppingCart = adapterData.size() <= 0;
        checkShoppingCartState();
        updateAdapter();
    }

    private void updateAdapter() {
        listAdapter.notifyDataSetChanged();
        updateAmount();
    }

    @Override
    public void onResume() {
        super.onResume();
        syncShoppingCartForServer();
    }

    /**
     * 修改购物车商品数量
     *
     * @param key   商品在购物车中的主键
     * @param count 商品在购物车中的数量
     */
    private void changeShoppingCartCount(final String key, final int count) {
        synchronized (changeSync) {
            if (isSyncingServerShoppingCart) {
                return;
            }
            isSyncingServerShoppingCart = true;
            updateShoppingCartProgress(isSyncingServerShoppingCart);

            long updated = DBInterface.instance().getClientShoppingCartUpdated();
            Map<String, String> args = new HashMap<>();
            args.put("GUID", key);
            args.put("COUNT", String.valueOf(count));
            args.put("LASTTIME", String.valueOf(updated));
            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "ChangeBuyCarCount", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());

            Connect connect = new RMConnect.Builder(ShoppingCartFragment.class)
                    .withProtocol(protocol)
                    .withParser(new JSONNodeParser())
                    .withDelegate(new Connect.AckDelegate() {
                        @Override
                        public void onResult(Message message, Message errorMessage) {
                            isSyncingServerShoppingCart = false;
                            updateShoppingCartProgress(isSyncingServerShoppingCart);
                            if (errorMessage == null) {
                                ResponseProtocol<JsonNode> protocol = (ResponseProtocol) message.protocol;
                                JsonNode response = protocol.getResponse();
                                handleChangeGoodsCountResponse(key, count, response);
                            }
                        }
                    }).build();
            ConnectManager.getInstance().request(getActivity(), connect);
        }
    }


    /**
     * 删除购物车中商品
     *
     * @param needDeleteGoods
     */
    private void deleteShoppingCartGoods(final String... needDeleteGoods) {
        synchronized (changeSync) {
            if (isSyncingServerShoppingCart) {
                return;
            }
            isSyncingServerShoppingCart = true;
            updateShoppingCartProgress(isSyncingServerShoppingCart);

            ArrayNode arrayNode = JacksonMapper.getInstance().createArrayNode();
            ObjectNode itemNode;
            for (String guid : needDeleteGoods) {
                itemNode = JacksonMapper.getInstance().createObjectNode();
                itemNode.put("MERCHANDISEID", guid);
                arrayNode.add(itemNode);
            }
            Map<String, String> args = new HashMap<>();
            args.put("USERGUID", UserConfig.getClientUserEntity().getGuid());
            args.put("JSONDATA", arrayNode.toString());
            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "DelCartItem", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());

            Connect connect = new RMConnect.Builder(ShoppingCartFragment.class)
                    .withProtocol(protocol)
                    .withParser(new JSONNodeParser())
                    .withDelegate(new Connect.AckDelegate() {
                        @Override
                        public void onResult(Message message, Message errorMessage) {
                            isSyncingServerShoppingCart = false;
                            updateShoppingCartProgress(isSyncingServerShoppingCart);
                            if (errorMessage == null) {
                                ResponseProtocol<JsonNode> protocol = (ResponseProtocol) message.protocol;
                                JsonNode response = protocol.getResponse();
                                handleDeleteGoodsResponse(response, needDeleteGoods);
                            } else {
                                ToastCell.toast(getActivity(), "删除失败!");
                            }
                        }
                    }).build();
            ConnectManager.getInstance().request(getActivity(), connect);
        }
    }

    /**
     * 删除购物车商品
     *
     * @param response
     * @param needDeleteGoods
     */
    private void handleDeleteGoodsResponse(JsonNode response, String... needDeleteGoods) {
        if (TextUtils.equals("yes", response.get("success").asText())) {
            DBInterface.instance().deleteShoppingCartGoods(needDeleteGoods);
            //本地数据过期需要同步服务器购物车数据
            syncShoppingCartForDB();
        } else {
            ToastCell.toast(getActivity(), "删除失败!");
        }
    }

    /**
     * 处理修改购物车商品数量回传数据
     *
     * @param key      商品在购物车内的主键
     * @param count    修改后的商品数量
     * @param response 请求服务器修改返回数据
     */
    private void handleChangeGoodsCountResponse(String key, int count, JsonNode response) {
        if (TextUtils.equals("yes", response.get("success").asText())) {
            ShoppingCartDataEntity entity = shoppingCartData.get(key);
            entity.updateCount(count);
            DBInterface.instance().updateShoppingCartCount(entity);
            updateAdapter();
            //检测购物车数据时间戳和服务器购物车数据时间戳对比是否有效
            if (response.has("ISVALID")) {
                boolean isValid = response.get("ISVALID").asBoolean(true);
                if (!isValid) {
                    //本地数据过期需要同步服务器购物车数据
                    syncShoppingCartForServer();
                }
            }
        }
    }

    /**
     * 同步服务器购物车数据（不是来自于下拉刷新）
     */
    private void syncShoppingCartForServer() {
        syncShoppingCartForServer(false);
    }

    /**
     * 同步服务器购物车数据（可能来自于下拉刷新）
     */
    private void syncShoppingCartForServer(final boolean isRefresh) {
        if (unLogin) {
            return;
        }
        if (isSyncingServerShoppingCart) {
            return;
        }
        isSyncingServerShoppingCart = true;
        if (isRefresh) {
            refreshLayout.setRefreshing(isSyncingServerShoppingCart);
        } else {
            updateShoppingCartProgress(isSyncingServerShoppingCart);
        }

        long updated = DBInterface.instance().getClientShoppingCartUpdated();
        Map<String, Object> args = new HashMap<>();
        args.put("LASTTIME", 0);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "SynchronizeShoppingCar", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(ShoppingCartFragment.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        isSyncingServerShoppingCart = false;
                        if (isRefresh) {
                            refreshLayout.setRefreshing(isSyncingServerShoppingCart);
                        } else {
                            updateShoppingCartProgress(isSyncingServerShoppingCart);
                        }
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> protocol = (ResponseProtocol) message.protocol;
                            JsonNode response = protocol.getResponse();
                            handleShoppingCartResponse(response);
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(getActivity(), connect);
    }

    private void updateShoppingCartProgress(boolean progress) {
        progressBar.setVisibility(progress ? View.VISIBLE : View.GONE);
    }

    private void handleShoppingCartResponse(JsonNode response) {
        List<ShoppingCartDataEntity> data = new ArrayList<>();
        if (response != null) {
            int size = response.size();
            for (int i = 0; i < size; i++) {
                data.add(new ShoppingCartDataEntity(response.get(i)));
            }
        }
        if (data.size() > 0) {
            DBInterface.instance().syncClientShoppingCart(data);
            syncShoppingCartForDB();
        }
    }

    private void updateAmount() {
        BigDecimal amount = BigDecimal.ZERO;

        if (listAdapter != null) {
            List<String> checkedItems = listAdapter.getCheckedItems();
            for (String key : checkedItems) {
                if (shoppingCartData.containsKey(key)) {
                    amount = amount.add(shoppingCartData.get(key).getSum());
                }
            }
        }
        SpannableStringBuilder amountText = new SpannableStringBuilder();
        amountText.append("合计:");
        amountText.append(ShoppingHelper.formatPrice(amount));
        amountDescView.setText(amountText);
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.onShoppingCartChanged) {
            syncShoppingCartForDB();
        } else if (i == AppNotificationCenter.loginSuccess || i == AppNotificationCenter.loginOut) {
            unLogin = UserSession.getInstance().isClientLogin();
            checkShoppingCartState();
            syncShoppingCartForServer();
        } else if (i == AppNotificationCenter.onCommitShoppingCart) {
            listAdapter.onAllSelectedChanged(false);
            syncShoppingCartForDB();
            syncShoppingCartForServer();
        }
    }

    private void onItemSelectedChanged() {
        boolean isAllChecked = listAdapter.isAllChecked();
        allCheckView.setChecked(isAllChecked);
        updateAmount();
    }

    protected class ListAdapter extends RecyclerView.Adapter {
        private List<ShoppingCartItem> adapterData;
        private final HashMap<String, Boolean> storeSelected = new HashMap<>();
        private final HashMap<String, Boolean> goodsSelected = new HashMap<>();

        public void bindData(List<ShoppingCartItem> data) {
            adapterData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                ShoppingCartStoreCell cell = new ShoppingCartStoreCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            } else if (viewType == 1) {
                ShoppingCartGoodsCell cell = new ShoppingCartGoodsCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            } else if (viewType == 2) {
                ShoppingCartUnLoginCell cell = new ShoppingCartUnLoginCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            } else if (viewType == 3) {
                ShoppingCartEmptyCell cell = new ShoppingCartEmptyCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder.itemView instanceof ShoppingCartUnLoginCell) {
                ShoppingCartUnLoginCell cell = (ShoppingCartUnLoginCell) holder.itemView;
                cell.setDelegate(new ShoppingCartUnLoginCell.Delegate() {
                    @Override
                    public void onNeedLogin() {
                        UIOpenHelper.openLoginActivity(getActivity());
                    }
                });
            } else if (holder.itemView instanceof ShoppingCartEmptyCell) {
                ShoppingCartEmptyCell cell = (ShoppingCartEmptyCell) holder.itemView;
                cell.setDelegate(new ShoppingCartEmptyCell.Delegate() {
                    @Override
                    public void onNeedSearchGoods() {
                        UIOpenHelper.openMedicineGroupActivity(getActivity());
                    }
                });

            } else if (holder.itemView instanceof ShoppingCartStoreCell) {
                ShoppingCartStoreCell cell = (ShoppingCartStoreCell) holder.itemView;
                ShoppingCartStoreItem item = (ShoppingCartStoreItem) adapterData.get(position);
                boolean checked = storeSelected.containsKey(item.getKey()) && storeSelected.get(item.getKey());
                cell.setValue(checked, item.shopName, true);
                cell.setDelegate(new ShoppingCartStoreCell.Delegate() {
                    @Override
                    public void onCheckableClick(boolean isChecked) {
                        ShoppingCartStoreItem select = (ShoppingCartStoreItem) adapterData.get(position);
                        onStoreSelectedChanged(select, isChecked);
                    }
                });
            } else if (holder.itemView instanceof ShoppingCartGoodsCell) {
                ShoppingCartGoodsCell cell = (ShoppingCartGoodsCell) holder.itemView;
                ShoppingCartGoodsItem item = (ShoppingCartGoodsItem) adapterData.get(position);
                boolean checked = goodsSelected.containsKey(item.getKey()) && goodsSelected.get(item.getKey());

                ShoppingCartDataEntity entity = shoppingCartData.get(item.getKey());
                String iconPath = entity.getIcon();
                String name = entity.getName();
                String desc = String.format("规格:%s", entity.getSpec());
                BigDecimal userPrice = entity.getUserPrice();
                BigDecimal marketPrice = entity.getMarketPrice();
                int count = entity.getBuyCount();
                cell.setValue(checked, iconPath, name, desc, userPrice, marketPrice, count, true);
                cell.setDelegate(new ShoppingCartGoodsCell.Delegate() {
                    @Override
                    public void onCheckableClick(boolean isChecked) {
                        ShoppingCartGoodsItem select = (ShoppingCartGoodsItem) adapterData.get(position);
                        onGoodsSelectedChanged(select, isChecked);
                    }

                    @Override
                    public void onGoodsCountChanged(final int number) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                ShoppingCartGoodsItem select = (ShoppingCartGoodsItem) adapterData.get(position);
                                changeShoppingCartCount(select.getKey(), number);
                            }
                        });
                    }

                    @Override
                    public void onNeedInputGoodsCount() {

                    }

                    @Override
                    public void onItemClick() {

                    }

                    @Override
                    public void onDelete() {
                        ShoppingCartItem select = adapterData.get(position);
                        deleteShoppingCartGoods(select.getKey());
                    }
                });
            }
        }

        public void onAllSelectedChanged(boolean checked) {
            storeSelected.clear();
            goodsSelected.clear();

            if (checked) {
                int size = adapterData.size();
                for (int i = 0; i < size; i++) {
                    ShoppingCartItem item = adapterData.get(i);
                    if (item.getItemViewType() == 0) {
                        storeSelected.put(item.getKey(), true);
                    } else {
                        goodsSelected.put(item.getKey(), true);
                    }
                }
            }
            notifyDataSetChanged();
            onItemSelectedChanged();
        }

        public boolean isAllChecked() {
            int checkCount = 0;
            for (Map.Entry<String, Boolean> entity :
                    goodsSelected.entrySet()) {
                if (entity.getValue() && shoppingCartData.containsKey(entity.getKey())) {
                    checkCount++;
                }
            }
            return (shoppingCartData.size() == checkCount);
        }

        public void onStoreSelectedChanged(ShoppingCartStoreItem item, boolean checked) {
            storeSelected.put(item.getKey(), checked);
            for (int i = item.start; i <= item.count; i++) {
                goodsSelected.put(adapterData.get(i).getKey(), checked);
            }
            notifyDataSetChanged();
            onItemSelectedChanged();
        }

        public void onGoodsSelectedChanged(ShoppingCartGoodsItem item, boolean checked) {
            goodsSelected.put(item.getKey(), checked);
            ShoppingCartStoreItem storeItem = (ShoppingCartStoreItem) adapterData.get(item.storePosition);
            boolean allChecked = true;
            for (int i = storeItem.start; i <= storeItem.count; i++) {
                String key = adapterData.get(i).getKey();
                if (!goodsSelected.containsKey(key)) {
                    allChecked = false;
                    break;
                } else if (!goodsSelected.get(key)) {
                    allChecked = false;
                    break;
                }
            }
            storeSelected.put(item.shopID, allChecked);
            notifyDataSetChanged();
            onItemSelectedChanged();
        }

        public ArrayList<String> getCheckedItems() {
            ArrayList<String> checkedItems = new ArrayList<>();
            Iterator<Map.Entry<String, Boolean>> iterator = goodsSelected.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Boolean> entry = iterator.next();
                if (entry.getValue()) {
                    checkedItems.add(entry.getKey());
                }
            }
            return checkedItems;
        }

        @Override
        public int getItemCount() {
            if (unLogin || emptyShoppingCart) {
                return 1;
            }
            return adapterData == null ? 0 : adapterData.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (unLogin) {
                return 2;
            } else if (emptyShoppingCart) {
                return 3;
            } else {
                return adapterData.get(position).getItemViewType();
            }
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 购物车店铺行UI
     */
    private static class ShoppingCartStoreItem extends ShoppingCartItem {
        public final String shopID;
        public final String shopName;
        private int start = 0;
        private int count = 0;

        public ShoppingCartStoreItem(String id, String name) {
            this.shopID = id;
            this.shopName = name;
        }

        @Override
        public int getItemViewType() {
            return 0;
        }

        @Override
        public String getKey() {
            return shopID;
        }

        public void setStart(int position) {
            start = position + 1;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    /**
     * 购物车商品行UI
     */
    private static class ShoppingCartGoodsItem extends ShoppingCartItem {
        public final String shopID;
        public final String goodsID;
        public final int storePosition;

        public ShoppingCartGoodsItem(String goodsID, String shopID, int storePosition) {
            this.shopID = shopID;
            this.goodsID = goodsID;
            this.storePosition = storePosition;
        }

        @Override
        public int getItemViewType() {
            return 1;
        }

        @Override
        public String getKey() {
            return goodsID;
        }
    }

    /**
     * 购物车行UI基类
     */
    public static abstract class ShoppingCartItem {

        public abstract int getItemViewType();

        public abstract String getKey();
    }
}
