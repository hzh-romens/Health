package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import com.romens.yjk.health.ui.components.FlowLayout;
import com.romens.yjk.health.ui.utils.UIUtils;

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
//    private String SearchText;

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

    private void addFloatLayout(ActionBarLayout.LinearLayoutContainer container) {
        historyLayout = new FlowLayout(this);
        historyLayout.setHorizontalSpacing(UIUtils.dip2px(20));
        historyLayout.setVerticalSpacing(UIUtils.dip2px(10));
        historyLayout.setPadding(UIUtils.dip2px(10), UIUtils.dip2px(20), UIUtils.dip2px(10), UIUtils.dip2px(4));
        historyLayout.setMaxLines(2);

        List<String> keywords = readHistoryKeyword();

        for (int i = 0; i < keywords.size(); i++) {
            TextView textView = new TextView(this);
            textView.setMaxLines(1);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(UIUtils.dip2px(4), UIUtils.dip2px(10), UIUtils.dip2px(4), UIUtils.dip2px(10));
            textView.setBackgroundResource(R.drawable.btn_primary);
            textView.setText(keywords.get(i));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SearchActivity.this, "-->" + v.getId(), Toast.LENGTH_SHORT).show();
                }
            });
            historyLayout.addView(textView);
        }

        container.addView(historyLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    private void actionBarEvent(ActionBar actionBar, final ActionBarLayout.LinearLayoutContainer container) {
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        ActionBarMenuItem searchItem = actionBarMenu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true, true);
        searchItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {

            @Override
            public boolean canCollapseSearch() {
                return false;
            }

            @Override
            public void onTextChanged(EditText var1) {
//                Log.e("tag", "search->" + var1.getText());
//                SearchText=var1.getText().toString().trim();
                String searchStr = var1.getText().toString().trim();
                if (!searchStr.equals("") && searchStr != null) {
                    requestDrugChanged(searchStr);
                    requestIllnessChanged(searchStr);
                    showSearchResult(container);
                    historyLayout.setVisibility(View.GONE);
                }
            }
        });

        actionBar.setTitle("搜索");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 0) {
//                    requestDrugChanged(searchStr);
//                    requestIllnessChanged(searchStr);
//                    for (int i = 0; i < 8; i++) {
//                        saveHistoryKeyword("感冒"+i);
//                    }
//                    historyLayout.setVisibility(View.GONE);
//                    showSearchResult(container);
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
        SearchHistoryEntity entity = new SearchHistoryEntity();
        entity.setHistoryKeyword(keyword);
        dao.insert(entity);
    }

    private void showSearchResult(ActionBarLayout.LinearLayoutContainer container) {
        FrameLayout listContainer = new FrameLayout(this);
        container.addView(listContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        adapter = new ListAdapter(this);
        listView = new ListView(this);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position < 5) {
                    Intent intent = new Intent(SearchActivity.this, MedicinalDetailActivity.class);
                    intent.putExtra("guid", drugList.get(position - 1).get("guid"));
                    startActivity(intent);
                }
            }
        });
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

        for (LinkedTreeMap<String, String> item : response) {
            illnessList.add(item.get("DISEASENAME"));
        }
    }

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
                    Log.e("tag", "msg->" + msg);
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

        for (LinkedTreeMap<String, String> item : response) {
            Map<String, String> map = new HashMap<>();
            map.put("name", item.get("NAME"));
            map.put("guid", item.get("GUID"));
            drugList.add(map);
        }
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
                    String name = drugList.get(position - 1).get("name");
                    cell.setText(drugList.get(position - 1).get("name"), false);
                    cell.setBackgroundColor(getResources().getColor(R.color.white));
//                    view.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            requestDrugChanged();
//                            requestIllnessChanged(searchStr);
//                            saveHistoryKeyword("感冒" + i);
//                            historyLayout.setVisibility(View.GONE);
//                            showSearchResult(container);
//                        }
//                    });
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
