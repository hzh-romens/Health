package com.romens.yjk.health.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.AndroidUtilities;
import com.romens.android.io.json.JacksonMapper;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.FlowCell;
import com.romens.android.ui.Components.FlowLayout;
import com.romens.android.ui.Components.FlowLayoutCallback;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.GreySectionCell;
import com.romens.android.ui.cells.LoadingCell;
import com.romens.android.ui.cells.TextActionCell;
import com.romens.android.ui.cells.TextInfoCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.common.GoodsFlag;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.SearchHistoryDao;
import com.romens.yjk.health.db.entity.SearchHistoryEntity;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.SearchResultEntity;
import com.romens.yjk.health.ui.FamilyDrugGroupActivity;
import com.romens.yjk.health.ui.base.BaseActivity;
import com.romens.yjk.health.ui.cells.DrugCell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by siery on 15/10/26.
 */
public class SearchActivity extends BaseActivity {
    public static final int SEARCH_TYPE_ALL = -1;
    public static final int SEARCH_TYPE_DRUG = 0;
    public static final int SEARCH_TYPE_DISEASE = 1;
    public static final int SEARCH_TYPE_BARCODE = 9;

    public static final String ARGUMENTS_SEARCH_QUERY_TEXT = "search_query_text";
    public static final String ARGUMENTS_SEARCH_TYPE = "search_type";

    private LinearLayout searchHistoryContainer;
    private FlowLayout searchHistoryListView;

    private RecyclerView searchResultList;

    private SearchResultAdapter searchResultAdapter;
    private final List<SearchHistoryEntity> searchHistoryKeywords = new ArrayList<>();
    private final List<SearchResultEntity> searchResultEntities = new ArrayList<>();

    private int searchType = SEARCH_TYPE_ALL;
    private boolean enableSearchHistory = true;


    private boolean isSearchProgress = false;
    private boolean fromFramilyDrugGroupTag = false;

    private int goodsFlag = GoodsFlag.NORMAL;
    private String actionBarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(GoodsFlag.ARGUMENT_KEY_GOODS_FLAG)) {
            goodsFlag = intent.getIntExtra(GoodsFlag.ARGUMENT_KEY_GOODS_FLAG, GoodsFlag.NORMAL);
        }
        fromFramilyDrugGroupTag = intent.getBooleanExtra("fromFramilyDrugGroupTag", false);
        String targetQueryText = intent.getStringExtra(ARGUMENTS_SEARCH_QUERY_TEXT);
        searchType = intent.getIntExtra(ARGUMENTS_SEARCH_TYPE, SEARCH_TYPE_ALL);
        enableSearchHistory = (searchType == SEARCH_TYPE_ALL);

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        FrameLayout searchContainer = new FrameLayout(this);

        if (enableSearchHistory) {
            searchHistoryContainer = new LinearLayout(this);
            searchHistoryContainer.setOrientation(LinearLayout.VERTICAL);
            searchContainer.addView(searchHistoryContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            searchHistoryListView = new FlowLayout(this);
            searchHistoryContainer.addView(searchHistoryListView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 16, 16, 16, 16));
        }

        searchResultList = new RecyclerView(this);
        searchContainer.addView(searchResultList, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        content.addView(searchContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        setContentView(content, actionBar);


        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        searchResultAdapter = new SearchResultAdapter(this);
        searchResultList.setAdapter(searchResultAdapter);

        ActionBarMenu actionBarMenu = actionBar.createMenu();

        if (searchType == SEARCH_TYPE_BARCODE) {
            actionBarTitle="扫描结果";
        } else {
            ActionBarMenuItem searchItem = actionBarMenu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true, true);
            searchItem.getSearchField().setHint("输入药品或者症状,比如:感冒");
            searchItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {

                @Override
                public boolean canCollapseSearch() {
                    return false;
                }

                @Override
                public void onSearchExpand() {
                    switchSearchResultLayout(false);
                }

                @Override
                public void onSearchCollapse() {
                    switchSearchResultLayout(true);
                }

                @Override
                public void onTextChanged(EditText var1) {
                    if (var1.getText().length() <= 0) {
                        switchSearchResultLayout(false);
                    }
                }

                @Override
                public void onSearchPressed(EditText var1) {
                    String searchText = var1.getText().toString().trim();
                    if (!searchText.equals("") && searchText != null) {
                        onSearch(searchText);
                    }
                }
            });
            actionBarMenu.addItem(1, R.drawable.ic_camera_alt_white_24dp);
            actionBarTitle="搜索";
        }
        actionBar.setTitle(actionBarTitle);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 1) {
                    UIOpenHelper.openQRSearchActivity(SearchActivity.this);
                    finish();
                }
            }
        });
        actionBar.openSearchField("");

        if (enableSearchHistory) {
            searchHistoryListView.setHorizontalSpacing(AndroidUtilities.dp(8));
            searchHistoryListView.setVerticalSpacing(AndroidUtilities.dp(4));

            searchHistoryListView.setAdapter(new FlowLayoutCallback() {
                @Override
                public int getCount() {
                    return searchHistoryKeywords.size();
                }

                @Override
                public View getView(final int position, ViewGroup container) {
                    FlowCell cell = new FlowCell(container.getContext());
                    cell.setText(searchHistoryKeywords.get(position).getHistoryKeyword());
                    cell.setClickable(true);
                    cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SearchHistoryEntity entity = searchHistoryKeywords.get(position);
                            String keyword = entity.getHistoryKeyword();
                            getMyActionBar().openSearchField(keyword);
                            onSearch(keyword);
                        }
                    });
                    return cell;
                }
            });
            searchHistoryListView.updateLayout();
        }
        switchSearchResultLayout(!enableSearchHistory);
        refreshSearchHistoryKeywords();
        if (!TextUtils.isEmpty(targetQueryText)) {
            getMyActionBar().openSearchField(targetQueryText);
            onSearch(targetQueryText);
        }
    }

    private void onSearch(String queryText) {
        switchSearchResultLayout(true);
        if (searchType != SEARCH_TYPE_BARCODE) {
            saveHistoryKeyword(queryText);
        }
        requestSearchData(queryText);
    }

    private void switchSearchResultLayout(boolean showResult) {
        if (showResult) {
            if (searchHistoryContainer != null) {
                searchHistoryContainer.setVisibility(View.GONE);
            }
            if (searchResultList.getVisibility() != View.VISIBLE) {
                searchResultList.setVisibility(View.VISIBLE);
            }
        } else {
            if (enableSearchHistory) {
                searchHistoryContainer.setVisibility(View.VISIBLE);
                searchResultList.setVisibility(View.GONE);
                searchHistoryListView.updateLayout();
            }
        }
    }

    private void saveHistoryKeyword(String keyword) {
        SearchHistoryDao dao = DBInterface.instance().openReadableDb().getSearchHistoryDao();
        List<SearchHistoryEntity> entities = dao.queryBuilder().orderAsc(SearchHistoryDao.Properties.Id).list();
        boolean isCanSave = true;
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getHistoryKeyword().equals(keyword)) {
                isCanSave = false;
            }
        }
        if (isCanSave) {
            SearchHistoryEntity entity = new SearchHistoryEntity();
            entity.setHistoryKeyword(keyword);
            if (entities.size() < 10) {
                dao.insert(entity);
            } else {
                dao.delete(entities.get(1));
                dao.insert(entity);
            }
        }
        refreshSearchHistoryKeywords();
    }

    private void refreshSearchHistoryKeywords() {
        if (!enableSearchHistory) {
            return;
        }
        searchHistoryKeywords.clear();
        SearchHistoryDao dao = DBInterface.instance().openReadableDb().getSearchHistoryDao();
        List<SearchHistoryEntity> searchHistoryEntities = dao.queryBuilder().orderAsc(SearchHistoryDao.Properties.Id).list();
        if (searchHistoryEntities != null && searchHistoryEntities.size() > 0) {
            searchHistoryKeywords.addAll(searchHistoryEntities);
        }
        if (searchHistoryListView.getVisibility() == View.VISIBLE) {
            searchHistoryListView.updateLayout();
        }
    }

    private void changeSearchProgress(boolean progress) {
        isSearchProgress = progress;
        updateAdapter();
    }


    private void requestSearchData(String queryText) {
        changeSearchProgress(true);
        Map<String, Object> args = new HashMap<>();
        args.put("FLAG", GoodsFlag.checkFlagForArg(goodsFlag));
        args.put("QUERYTEXT", queryText);
        args.put("SEARCHTYPE", searchType);
        args.put("SEARCHSIZE", 10);

        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetSearchInfo", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(SearchActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        changeSearchProgress(false);
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            handleSearchDataResponse(responseProtocol.getResponse());
                        } else {
                            handleSearchDataResponse(null);
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
//        Message message = new Message.MessageBuilder()
//                .withProtocol(protocol)
//                .build();
//        FacadeClient.request(SearchActivity.this, message, new FacadeClient.FacadeCallback() {
//            @Override
//            public void onTokenTimeout(Message msg) {
//                changeSearchProgress(false);
//                handleSearchDataResponse(null);
//            }
//
//            @Override
//            public void onResult(Message msg, Message errorMsg) {
//
//            }
//        });
    }


    @Override
    public void onDestroy() {
        ConnectManager.getInstance().destroyInitiator(SearchActivity.class);
        super.onDestroy();
    }

    private void handleSearchDataResponse(JsonNode response) {
        searchResultEntities.clear();
        if (response == null) {
            return;
        }
        final int groupSize = response.size();
        if (searchType == SEARCH_TYPE_BARCODE) {
            for (int i = 0; i < groupSize; i++) {
                handleSearchDataResponseSectionForBarCode(response.get(i), searchResultEntities);
            }
        } else {
            for (int i = 0; i < groupSize; i++) {
                handleSearchDataResponseSection(response.get(i), searchResultEntities);
            }
        }
        updateAdapter();
    }

    private void handleSearchDataResponseSectionForBarCode(JsonNode jsonNode, List<SearchResultEntity> searchResult) {
        if (jsonNode == null) {
            return;
        }
        searchResult.add(new SearchResultEntity(jsonNode.get("GUID").asText(), jsonNode.get("NAME").asText(), "0")
                .withViewType(4));
    }

    private void handleSearchDataResponseSection(JsonNode jsonNode, List<SearchResultEntity> searchResult) {
        if (jsonNode == null) {
            return;
        }
        String sectionName = jsonNode.get("TYPENAME").asText();
        String sectionType = jsonNode.get("TYPE").asText();
        String result = jsonNode.get("RESULT").asText();
        JsonNode jsonArray = null;
        try {
            jsonArray = JacksonMapper.getInstance().readTree(result);
        } catch (IOException e) {
            return;
        }
        if (jsonArray != null) {
            int size = jsonArray.size();
            if (size > 0) {
                searchResult.add(new SearchResultEntity("", sectionName, sectionType).withViewType(2));
                JsonNode item;
                for (int i = 0; i < size; i++) {
                    item = jsonArray.get(i);
                    if (TextUtils.equals("0", sectionType)) {
                        searchResult.add(new SearchResultEntity(item.get("MERCHANDISEID").asText(), item.get("MEDICINENAME").asText(), sectionType)
                                .addProperty("MEDICINESPEC", item.get("MEDICINESPEC").asText())
                                .addProperty("GOODSTYPE", item.get("GOODSTYPE").asText())
                                .withViewType(4));
                    } else if (TextUtils.equals("1", sectionType)) {
                        searchResult.add(new SearchResultEntity(item.get("DISEASEID").asText(), item.get("DISEASENAME").asText(), sectionType)
                                .withViewType(3));
                    }
                }
                //searchResult.add(new SearchResultEntity("", String.format("查看更多%s", sectionName), sectionType).withViewType(5));
            }
        }
    }

    private int rowCount;
    private int searchProgressRow;
    private int searchEmptyRow;

    private void updateAdapter() {
        rowCount = 0;
        if (isSearchProgress) {
            searchProgressRow = rowCount++;
            searchEmptyRow = -1;
        } else {
            searchProgressRow = -1;
            if (searchResultEntities.size() > 0) {
                searchEmptyRow = -1;
                rowCount += searchResultEntities.size();
            } else {
                searchEmptyRow = rowCount++;
            }
        }
        searchResultAdapter.notifyDataSetChanged();
    }

    @Override
    protected String getActivityName() {
        return String.format("%s[%s]",actionBarTitle,goodsFlag==GoodsFlag.MEDICARE?"医保":"非医保");
    }

    class SearchResultAdapter extends RecyclerView.Adapter<CellHolder> {
        private Context adapterContext;

        public SearchResultAdapter(Context context) {
            adapterContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public CellHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                LoadingCell view = new LoadingCell(parent.getContext());
                return new CellHolder(view);
            } else if (viewType == 1) {
                TextInfoCell view = new TextInfoCell(parent.getContext());
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                view.setClickable(true);
                view.setBackgroundResource(R.drawable.list_selector);
                return new CellHolder(view);
            } else if (viewType == 2) {
                GreySectionCell view = new GreySectionCell(parent.getContext());
                return new CellHolder(view);
            } else if (viewType == 3) {
                TextSettingsCell view = new TextSettingsCell(parent.getContext());
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                view.setClickable(true);
                view.setBackgroundResource(R.drawable.list_selector);
                return new CellHolder(view);
            } else if (viewType == 4) {
                DrugCell view = new DrugCell(parent.getContext());
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                view.setClickable(true);
                view.setBackgroundResource(R.drawable.list_selector);
                return new CellHolder(view);
            } else if (viewType == 5) {
                TextActionCell view = new TextActionCell(parent.getContext());
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                view.setClickable(true);
                view.setBackgroundResource(R.drawable.list_selector);
                return new CellHolder(view);
            }
            return null;
        }

        public SearchResultEntity getItem(int position) {
            if (searchResultEntities.size() > 0) {
                return searchResultEntities.get(position);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(CellHolder holder, final int position) {
            int itemViewType = getItemViewType(position);
            if (itemViewType == 1) {
                TextInfoCell cell = (TextInfoCell) holder.itemView;
                if (position == searchEmptyRow) {
                    cell.setText("无搜索结果");
                    cell.setOnClickListener(null);
                }
            } else if (itemViewType == 2) {
                GreySectionCell cell = (GreySectionCell) holder.itemView;
                SearchResultEntity entity = getItem(position);
                cell.setText(entity.name);
            } else if (itemViewType == 3) {
                TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                SearchResultEntity entity = getItem(position);
                cell.setText(entity.name, true);
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SearchResultEntity selectedEntity = getItem(position);
                        if (TextUtils.equals(selectedEntity.type, "1")) {

                        }
                    }
                });
            } else if (itemViewType == 4) {
                DrugCell cell = (DrugCell) holder.itemView;
                SearchResultEntity entity = getItem(position);
                boolean isMedicareGoods = TextUtils.equals(String.valueOf(GoodsFlag.MEDICARE), entity.getProperty("GOODSTYPE"));
                CharSequence name = ShoppingHelper.createShoppingCartGoodsName(entity.name, isMedicareGoods);
                String spec = entity.getProperty("MEDICINESPEC");
                String desc = String.format("规格:%s", TextUtils.isEmpty(spec) ? "-" : spec);
                cell.setValue("药", name, desc, true);
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SearchResultEntity selectedEntity = getItem(position);
                        if (TextUtils.equals(selectedEntity.type, "0")) {
                            if (fromFramilyDrugGroupTag) {
                                Intent intent = new Intent(SearchActivity.this, FamilyDrugGroupActivity.class);
                                intent.putExtra("searchDrugEntity", selectedEntity);
                                setResult(UserGuidConfig.RESPONSE_SEARCH_TO_DRUGGROUP, intent);
                                finish();
                            } else {
                                final boolean isMedicareGoods = TextUtils.equals(String.valueOf(GoodsFlag.MEDICARE), selectedEntity.getProperty("GOODSTYPE"));
                                //UIOpenHelper.openDrugDescriptionActivity(SearchActivity.this, selectedEntity.id, selectedEntity.name);
                                UIOpenHelper.openMedicineActivity(SearchActivity.this, selectedEntity.id, isMedicareGoods ? GoodsFlag.MEDICARE : GoodsFlag.NORMAL);
                            }
                        }
                    }
                });
            } else if (itemViewType == 5) {
                TextActionCell cell = (TextActionCell) holder.itemView;
                SearchResultEntity entity = getItem(position);
                cell.setText(entity.name, false);
            }
        }

        @Override
        public int getItemViewType(int i) {
            if (i == searchProgressRow) {
                return 0;
            } else if (i == searchEmptyRow) {
                return 1;
            }
            return searchResultEntities.get(i).viewType;
        }
    }

    static class CellHolder extends RecyclerView.ViewHolder {

        public CellHolder(View itemView) {
            super(itemView);
        }
    }

//    class SearchResultEntity {
//        public final String id;
//        public final String name;
//        public final String type;
//
//        private Map<String, String> properties = new HashMap<>();
//
//        public int viewType = 0;
//
//        public SearchResultEntity(String id, String name, String type) {
//            this.id = id;
//            this.name = name;
//            this.type = type;
//        }
//
//        public SearchResultEntity withViewType(int viewType) {
//            this.viewType = viewType;
//            return this;
//        }
//
//        public SearchResultEntity addProperty(String key, String value) {
//            properties.put(key, value);
//            return this;
//        }
//
//        public String getProperty(String key) {
//            if (properties.containsKey(key)) {
//                return properties.get(key);
//            }
//            return null;
//        }
//
//        public void onAction(Context context) {
//            context.startActivity(new Intent(SearchActivity.this, HealthActivity.class));
//        }
//    }
}
