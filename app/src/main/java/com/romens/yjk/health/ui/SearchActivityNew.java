package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.FlowCell;
import com.romens.android.ui.Components.FlowLayout;
import com.romens.android.ui.Components.FlowLayoutCallback;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.android.ui.cells.HeaderCell;
import com.romens.android.ui.cells.LoadingCell;
import com.romens.android.ui.cells.TextInfoCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.SearchHistoryDao;
import com.romens.yjk.health.db.entity.SearchHistoryEntity;
import com.romens.yjk.health.model.SearchResultEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/8/17.
 */
public class SearchActivityNew extends BaseActivity {
    private View searchHistoryContainer;
    private FlowLayout historyLayout;

    private ListView searchResultList;


    private final List<String> searchHistoryKeywordList = new ArrayList<>();
    private final List<SearchResultEntity> searchDrugResult = new ArrayList<>();
    private final List<SearchResultEntity> searchDiseaseResult = new ArrayList<>();

    private static final int LAYOUT_FLAG_HISTORY = 0;
    private static final int LAYOUT_FLAG_RESULT = 1;

    private int layoutFlag = LAYOUT_FLAG_HISTORY;
    private boolean searchDrugProgress = false;
    private boolean searchDiseaseProgress = false;


    private SearchResultAdapter searchResultAdapter;
    private int rowCount;
    private int searchDrugSession;
    private int searchDrugProgressRow;
    private int searchDrugEmptyRow;
    private int searchDiseaseSession;
    private int searchDiseaseProgressRow;
    private int searchDiseaseEmptyRow;

    private TextView clearSearchKeywordTextView;

    private boolean isFromFamilyDrugGroup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search, R.id.action_bar);

        isFromFamilyDrugGroup = isFromFamilyDrugGroup();

        searchHistoryContainer = findViewById(R.id.search_history_container);
        historyLayout = (FlowLayout) findViewById(R.id.search_history_list);

        searchResultList = (ListView) findViewById(R.id.search_result_list);
        searchResultList.setDivider(null);
        searchResultList.setDividerHeight(0);
        searchResultList.setVerticalScrollBarEnabled(false);
        searchResultList.setSelector(R.drawable.list_selector);
        searchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isFromFamilyDrugGroup) {
                    Intent intent = new Intent(SearchActivityNew.this, FamilyDrugGroupActivity.class);
                    intent.putExtra("searchDrugEntity", searchDrugResult.get(position - (searchDrugSession + 1)));
                    setResult(UserGuidConfig.RESPONSE_SEARCH_TO_DRUGGROUP, intent);
                    finish();
                } else if (isDrugSearchResultRow(position)) {
                    //跳转药品页面
                 //   UIOpenHelper.openMedicinalDetailActivity(SearchActivityNew.this,searchDrugResult.get(position - (searchDrugSession + 1)).guid);
                } else if (isDiseaseSearchResultRow(position)) {
                    //跳转疾病页面
                }
            }
        });

        searchResultAdapter = new SearchResultAdapter(this);
        searchResultList.setAdapter(searchResultAdapter);

        ActionBar actionBar = getMyActionBar();
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        ActionBarMenuItem searchItem = actionBarMenu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true, true);
        searchItem.getSearchField().setHint("输入疾病或者药品");
        searchItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {

            @Override
            public boolean canCollapseSearch() {
                return false;
            }

            @Override
            public void onSearchExpand() {
                updateLayoutFlag(LAYOUT_FLAG_HISTORY);
            }

            @Override
            public void onSearchCollapse() {
                updateLayoutFlag(LAYOUT_FLAG_RESULT);
            }

            @Override
            public void onTextChanged(EditText var1) {
                if (var1.getText().length() <= 0) {
                    updateLayoutFlag(LAYOUT_FLAG_HISTORY);
                }
            }

            @Override
            public void onSearchPressed(EditText var1) {
                String searchText = var1.getText().toString().trim();
                if (!searchText.equals("") && searchText != null) {
                    search(searchText);
                    saveHistoryKeyword(searchText);
                }
            }
        });
        actionBarMenu.addItem(1, R.drawable.ic_camera_alt_white_24dp);
        actionBar.setTitle("搜索");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 1) {
                    startActivity(new Intent("com.romens.yjk.health.QRSCANNER"));
                }
            }
        });
        actionBar.openSearchField("");


        historyLayout.setHorizontalSpacing(AndroidUtilities.dp(8));
        historyLayout.setVerticalSpacing(AndroidUtilities.dp(4));

        historyLayout.setAdapter(new FlowLayoutCallback() {
            @Override
            public int getCount() {
                return searchHistoryKeywordList.size();
            }

            @Override
            public View getView(final int position, ViewGroup container) {
                FlowCell cell = new FlowCell(container.getContext());
                cell.setText(searchHistoryKeywordList.get(position));
                cell.setClickable(true);
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String searchText = searchHistoryKeywordList.get(position);
                        getMyActionBar().openSearchField(searchText);
                        search(searchText);
                    }
                });
                return cell;
            }
        });
        historyLayout.updateLayout();
        updateLayoutFlag(LAYOUT_FLAG_HISTORY);
        refreshSearchHistoryKeywords();
        updateAdapter();

        clearSearchKeywordTextView = (TextView) findViewById(R.id.search_history_clear);
        clearSearchKeywordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchHistoryDao dao = DBInterface.instance().openReadableDb().getSearchHistoryDao();
                dao.deleteAll();
                refreshSearchHistoryKeywords();
            }
        });
    }

    private boolean isFromFamilyDrugGroup() {
        Intent intent = getIntent();
        intent.getStringExtra("fromFramilyDrugGroupTag");
        return intent.getBooleanExtra("fromFramilyDrugGroupTag", false);
    }

    private void search(String queryText) {
        updateLayoutFlag(LAYOUT_FLAG_RESULT);

        searchDrugResult.clear();
        searchDiseaseResult.clear();

        requestSearchDrug(queryText);
        requestSearchDisease(queryText);
    }

    private void refreshSearchHistoryKeywords() {
        searchHistoryKeywordList.clear();
        SearchHistoryDao dao = DBInterface.instance().openReadableDb().getSearchHistoryDao();
        List<SearchHistoryEntity> searchHistoryEntities = dao.queryBuilder().orderAsc(SearchHistoryDao.Properties.Id).list();
        if (searchHistoryEntities != null) {
            for (SearchHistoryEntity entity :
                    searchHistoryEntities) {
                searchHistoryKeywordList.add(entity.getHistoryKeyword());
            }
        }
        if (historyLayout.getVisibility() == View.VISIBLE) {
            historyLayout.updateLayout();
        }
    }

    private void updateLayoutFlag(int flag) {
        layoutFlag = flag;
        if (layoutFlag == LAYOUT_FLAG_RESULT) {
            searchHistoryContainer.setVisibility(View.GONE);
            searchResultList.setVisibility(View.VISIBLE);
        } else {
            searchHistoryContainer.setVisibility(View.VISIBLE);
            searchResultList.setVisibility(View.GONE);
            historyLayout.updateLayout();
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

    private void requestSearchDisease(String searchStr) {
        requestSearchDiseaseProgress(true);
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("KEY", searchStr);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetSearchDisease", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                requestSearchDiseaseProgress(false);
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                requestSearchDiseaseProgress(false);
                if (errorMsg == null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    handleSearchDiseaseResult(responseProtocol.getResponse());
                }
            }
        });
    }

    private void requestSearchDiseaseProgress(boolean progress) {
        searchDiseaseProgress = progress;
        updateAdapter();
    }

    private void handleSearchDiseaseResult(List<LinkedTreeMap<String, String>> response) {
        searchDiseaseResult.clear();
        int count = response == null ? 0 : response.size();
        if (count <= 0) {
            return;
        }
        for (LinkedTreeMap<String, String> item : response) {
            searchDiseaseResult.add(new SearchResultEntity("", item.get("DISEASENAME"), ""));
        }
        updateAdapter();
        searchResultList.smoothScrollToPosition(0);
    }

    //请求相关药品
    private void requestSearchDrug(final String searchStr) {
        requestSearchDrugProgress(true);
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("KEY", searchStr);
        args.put("PAGE", "0");
        args.put("COUNT", "5");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetSearchDrug", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                requestSearchDrugProgress(false);
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                requestSearchDrugProgress(false);
                if (errorMsg == null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    handleSearchDrugResult(responseProtocol.getResponse());
                    saveHistoryKeyword(searchStr);
                }
            }
        });
    }

    private void requestSearchDrugProgress(boolean progress) {
        searchDrugProgress = progress;
        updateAdapter();
    }

    private void handleSearchDrugResult(List<LinkedTreeMap<String, String>> response) {
        searchDrugResult.clear();
        int count = response == null ? 0 : response.size();
        if (count <= 0) {
            return;
        }
        for (LinkedTreeMap<String, String> item : response) {
            searchDrugResult.add(new SearchResultEntity(item.get("MERCHANDISEID"), item.get("MEDICINENAME"), item.get("MEDICINESPEC")));
        }
        updateAdapter();
        searchResultList.smoothScrollToPosition(0);
    }

    private void updateAdapter() {
        rowCount = 0;
        searchDrugSession = rowCount++;
        if (searchDrugProgress) {
            searchDrugProgressRow = rowCount++;
        } else {
            searchDrugProgressRow = -1;
            if (searchDrugResult.size() <= 0) {
                searchDrugEmptyRow = rowCount++;
            } else {
                searchDrugEmptyRow = -1;
                rowCount += searchDrugResult.size();
            }
        }
        searchDiseaseSession = rowCount++;
        if (searchDiseaseProgress) {
            searchDiseaseProgressRow = rowCount++;
        } else {
            searchDiseaseProgressRow = -1;
            if (searchDiseaseResult.size() <= 0) {
                searchDiseaseEmptyRow = rowCount++;
            } else {
                searchDiseaseEmptyRow = -1;
                rowCount += searchDiseaseResult.size();
            }
        }
        searchResultAdapter.notifyDataSetChanged();
    }

    private boolean isDrugSearchResultRow(int position) {
        if (searchDrugEmptyRow == -1 && searchDrugProgressRow == -1) {
            if (position > searchDrugSession && position < searchDiseaseSession) {
                return true;
            }
        }
        return false;
    }

    private boolean isDiseaseSearchResultRow(int position) {
        if (searchDiseaseEmptyRow == -1 && searchDiseaseProgressRow == -1) {
            if (position > searchDiseaseSession) {
                return true;
            }
        }
        return false;
    }

    class SearchResultAdapter extends BaseFragmentAdapter {
        private Context adapterContext;

        public SearchResultAdapter(Context context) {
            adapterContext = context;
        }


        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            if (searchDrugEmptyRow == -1 && searchDrugProgressRow == -1) {
                if (i > searchDrugSession && i < searchDiseaseSession) {
                    return true;
                }
            }
            if (searchDiseaseEmptyRow == -1 && searchDiseaseProgressRow == -1) {
                if (i > searchDiseaseSession) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == searchDrugSession || i == searchDiseaseSession) {
                return 0;
            } else if (i == searchDrugProgressRow || i == searchDiseaseProgressRow) {
                return 2;
            } else if (i == searchDrugEmptyRow || i == searchDiseaseEmptyRow) {
                return 3;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            int type = getItemViewType(position);
            if (type == 0) {
                if (view == null) {
                    view = new HeaderCell(adapterContext);
                }
                HeaderCell cell = (HeaderCell) view;
                cell.setTextColor(ResourcesConfig.textPrimary);
                if (position == searchDrugSession) {
                    cell.setText("相关药品");
                } else if (position == searchDiseaseSession) {
                    cell.setText("相关疾病");
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new TextSettingsCell(adapterContext);
                }
                TextSettingsCell cell = (TextSettingsCell) view;
                if (position > searchDrugSession && position < searchDiseaseSession) {
                    cell.setText(searchDrugResult.get(position - (searchDrugSession + 1)).name, true);
                } else if (position > searchDiseaseSession) {
                    cell.setText(searchDiseaseResult.get(position - (searchDiseaseSession + 1)).name, true);
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new LoadingCell(adapterContext);
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new TextInfoCell(adapterContext);
                }
                TextInfoCell cell = (TextInfoCell) view;
                if (position == searchDrugEmptyRow) {
                    cell.setText("无搜索结果");
                } else if (position == searchDiseaseEmptyRow) {
                    cell.setText("无搜索结果");
                }
            }
            return view;
        }
    }

//    class SearchResultEntity implements Serializable {
//        public final String guid;
//        public final String name;
//        public final String spec;
//
//        public SearchResultEntity(String guid, String name, String spec) {
//            this.guid = guid;
//            this.name = name;
//            this.spec = spec;
//        }
//    }
}
