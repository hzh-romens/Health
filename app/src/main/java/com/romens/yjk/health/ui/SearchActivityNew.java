package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.HeaderCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.SearchHistoryDao;
import com.romens.yjk.health.db.entity.SearchHistoryEntity;
import com.romens.yjk.health.ui.adapter.FlowlayoutAdapter;
import com.romens.yjk.health.ui.components.FlowLayout;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/8/17.
 */
public class SearchActivityNew extends BaseActivity {

    private ExpandableListView listView;
    private SearchExpandableAdapter expandableAdapter;
    private SwipeRefreshLayout refreshLayout;
//    private ListAdapter adapter;

    private ArrayList<SearchResultEntity> drugList;
    private ArrayList<SearchResultEntity> illnessList;
    private FlowLayout historyLayout;
    private FlowlayoutAdapter historyLayoutAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container, actionBar);

        drugList = new ArrayList<>();
        illnessList = new ArrayList<>();

        addFloatLayout(container);
        actionBarEvent(actionBar, container);
    }

    private void addFloatLayout(final ActionBarLayout.LinearLayoutContainer container) {
        historyLayout = new FlowLayout(this);
        final List<String> keywords = readHistoryKeyword();
        historyLayoutAdapter = new FlowlayoutAdapter(historyLayout, this, keywords);
        historyLayoutAdapter.andTextView();
        historyLayoutAdapter.ItemClickListener(new FlowlayoutAdapter.FlowLayoutItemClick() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(SearchActivityNew.this, "-->" + position, Toast.LENGTH_SHORT).show();
                String searchText = keywords.get(position);
                requestDrugChanged(searchText);
                requestIllnessChanged(searchText);
                showSearchResult(container);
                historyLayout.setVisibility(View.GONE);
            }
        });
        container.addView(historyLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    private void actionBarEvent(ActionBar actionBar, final ActionBarLayout.LinearLayoutContainer container) {
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        ActionBarMenuItem searchItem = actionBarMenu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true, true);
        searchItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {

            @Override
            public boolean canCollapseSearch() {
                historyLayout.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public void onTextChanged(EditText var1) {
                String searchText = var1.getText().toString().trim();
                if (!searchText.equals("") && searchText != null) {
                    requestDrugChanged(searchText);
                    requestIllnessChanged(searchText);
                    showSearchResult(container);
                    historyLayout.setVisibility(View.GONE);
                }
            }
        });
        actionBarMenu.addItem(1, R.drawable.search_zxing_extend_36);
        actionBar.setTitle("搜索");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 1) {
//                    requestDrugChanged(searchStr);
//                    requestIllnessChanged(searchStr);
//                    saveHistoryKeyword(searchStr);
//                    historyLayout.setVisibility(View.GONE);
//                    showSearchResult(container);
                    startActivity(new Intent("com.romens.yjk.health.QRSCANNER"));
                }
            }
        });
        actionBar.openSearchField("");
    }

    private List<String> readHistoryKeyword() {
        List<String> keywords = new ArrayList<>();
        SearchHistoryDao dao = DBInterface.instance().openReadableDb().getSearchHistoryDao();
        List<SearchHistoryEntity> searchHistoryEntities = dao.queryBuilder().orderAsc(SearchHistoryDao.Properties.Id).list();
        for (SearchHistoryEntity entity : searchHistoryEntities) {
            keywords.add(entity.getHistoryKeyword());
        }
        return keywords;
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
    }

    private void showSearchResult(ActionBarLayout.LinearLayoutContainer container) {
        FrameLayout listContainer = new FrameLayout(this);
        container.addView(listContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        refreshLayout = new SwipeRefreshLayout(this);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        refreshLayout.setRefreshing(true);
        listContainer.addView(refreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ExpandableListView(this);
        expandableAdapter = new SearchExpandableAdapter(this, drugList, illnessList);
        listView.setAdapter(expandableAdapter);
        listView.setGroupIndicator(null);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        refreshLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private void requestIllnessChanged(String searchStr) {
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
                Toast.makeText(SearchActivityNew.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    setIllnessListData(responseProtocol.getResponse());
                }
                if (errorMsg == null) {
                } else {
                    Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    private void setIllnessListData(List<LinkedTreeMap<String, String>> response) {
        int count = response == null ? 0 : response.size();
        if (count <= 0) {
            return;
        }
        illnessList.clear();
        for (LinkedTreeMap<String, String> item : response) {
            SearchResultEntity entity = new SearchResultEntity();
            entity.setName(item.get("DISEASENAME"));
            illnessList.add(entity);
        }
        requestOkToSetDataForRefershView();
    }

    //请求相关药品
    private void requestDrugChanged(final String searchStr) {
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
                Toast.makeText(SearchActivityNew.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    setDrugListData(responseProtocol.getResponse());
                    saveHistoryKeyword(searchStr);
                }
                if (errorMsg == null) {
                } else {
                    Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    private void setDrugListData(List<LinkedTreeMap<String, String>> response) {
        int count = response == null ? 0 : response.size();
        if (count <= 0) {
            return;
        }
        drugList.clear();
        for (LinkedTreeMap<String, String> item : response) {
//            Map<String, String> map = new HashMap<>();
//            map.put("name", item.get("NAME"));
//            map.put("guid", item.get("GUID"));
            SearchResultEntity entity = new SearchResultEntity();
            entity.setName(item.get("MEDICINENAME"));
            entity.setGuid(item.get("MERCHANDISEID"));
            drugList.add(entity);
        }
        requestOkToSetDataForRefershView();
    }

    public void requestOkToSetDataForRefershView() {
        refreshLayout.setRefreshing(false);
        expandableAdapter.setAdapterData(drugList, illnessList);
        expandableAdapter.notifyDataSetChanged();

        int count = listView.getCount();
        for (int i = 0; i < count; i++) {
            listView.expandGroup(i);
        }
    }

    class SearchExpandableAdapter extends BaseExpandableListAdapter {

        private List<List<SearchResultEntity>> groupList;
        private Context context;

        public void setAdapterData(List<SearchResultEntity> drugList, List<SearchResultEntity> illnessList) {
            groupList.clear();
            groupList.add(drugList);
            groupList.add(illnessList);
        }

        public SearchExpandableAdapter(Context context, List<SearchResultEntity> drugList, List<SearchResultEntity> illnessList) {
            this.context = context;
            groupList = new ArrayList<>();
            groupList.add(drugList);
            groupList.add(illnessList);
        }

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return groupList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return groupList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new HeaderCell(context);
            }
            HeaderCell cell = (HeaderCell) convertView;
            if (groupPosition == 0) {
                cell.setText("相关药品");
            } else if (groupPosition == 1) {
                cell.setText("相关疾病");
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextSettingsCell(context);
            }
            TextSettingsCell cell = (TextSettingsCell) convertView;
            String nameStr = groupList.get(groupPosition).get(childPosition).getName();
            if (nameStr != null) {
                cell.setText(nameStr, false);
            }
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            cell.setBackgroundColor(getResources().getColor(R.color.white));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    class SearchResultEntity {
        private String name;
        private String guid;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }
    }
}
