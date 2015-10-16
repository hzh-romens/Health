package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
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
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.android.ui.cells.HeaderCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.SearchHistoryDao;
import com.romens.yjk.health.db.entity.SearchHistoryEntity;
import com.romens.yjk.health.ui.adapter.FlowlayoutAdapter;
import com.romens.yjk.health.ui.components.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/8/17.
 */
public class SearchActivity extends BaseActivity {

    private ListView listView;
    private ListAdapter adapter;

    private ArrayList<Map<String, String>> drugList;
    private ArrayList<String> illnessList;
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
                Toast.makeText(SearchActivity.this, "-->" + position, Toast.LENGTH_SHORT).show();
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

        adapter = new ListAdapter(this);
        adapter.notifyDataSetChanged();
        listView = new ListView(this);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
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
                Toast.makeText(SearchActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
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
        illnessList = new ArrayList<>();
        for (LinkedTreeMap<String, String> item : response) {
            illnessList.add(item.get("DISEASENAME"));
        }
        adapter.notifyDataSetChanged();
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
                Toast.makeText(SearchActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
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
        drugList = new ArrayList<>();
        for (LinkedTreeMap<String, String> item : response) {
            Map<String, String> map = new HashMap<>();
            map.put("name", item.get("MEDICINENAME"));
            map.put("guid", item.get("MERCHANDISEID"));
            drugList.add(map);
        }
        adapter.notifyDataSetChanged();
    }

    class ListAdapter extends BaseFragmentAdapter {
        private Context adapterContext;

        public ListAdapter(Context context) {
            adapterContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return false;
        }

        @Override
        public int getCount() {
            return drugList.size() + illnessList.size() + 3;
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
            if (i == 0) {
                return 0;
            } else if (i < 5) {
                return 1;
            } else if (i == 5) {
                return 2;
            } else if (i == 6) {
                return 3;
            } else if (i > 5) {
                return 4;
            }
            return -1;
        }

        @Override
        public int getViewTypeCount() {
            return 5;
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
                cell.setTextColor(ResourcesConfig.primaryColor);
                cell.setText("相关药品");
            } else if (type == 1) {
                if (view == null) {
                    view = new TextSettingsCell(adapterContext);
                }
                TextSettingsCell cell = (TextSettingsCell) view;
                if (drugList.size() > 1) {
                    final String name = drugList.get(position - 1).get("name");
                    cell.setText(name, false);
                    cell.setBackgroundColor(getResources().getColor(R.color.white));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SearchActivity.this, MedicinalDetailActivity.class);
                            intent.putExtra("guid", drugList.get(position - 1).get("guid"));
                            startActivity(intent);
                        }
                    });
                }

            } else if (type == 2) {
                if (view == null) {
                    view = new ShadowSectionCell(adapterContext);
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new HeaderCell(adapterContext);
                }
                HeaderCell cell = (HeaderCell) view;
                cell.setTextColor(ResourcesConfig.primaryColor);
                cell.setText("相关疾病");
            } else if (type == 4) {
                if (view == null) {
                    view = new TextSettingsCell(adapterContext);
                }
                TextSettingsCell cell = (TextSettingsCell) view;
                String illnessStr = illnessList.get(position - (drugList.size() + 3));
                if (illnessStr != null) {
                    cell.setText(illnessStr, false);
                }
                cell.setBackgroundColor(getResources().getColor(R.color.white));
            }
            return view;
        }
    }
}
