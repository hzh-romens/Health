package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.DrugGroupDao;
import com.romens.yjk.health.db.entity.DrugGroupEntity;
import com.romens.yjk.health.ui.adapter.ContentListViewAdapter;
import com.romens.yjk.health.ui.cells.TextViewCell;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2016/1/11.
 */
public class HomeHealthNewFragment extends BaseFragment {

    private ListView leftMenuListView;
    private ListView contentListView;

    private MenuListViewAdapter menuAdapter;
    private ContentListViewAdapter contentAdapter;

    private List<MenuEntity> menuListData;
    private HashMap<String, List<DrugGroupEntity>> childNodes;
    private List<DrugGroupEntity> groupNodes;

    private int oldPosition = 0;

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, null);
        leftMenuListView = (ListView) view.findViewById(R.id.menu_list);
        contentListView = (ListView) view.findViewById(R.id.content_list);

        leftMenuListView.setVerticalScrollBarEnabled(false);
        leftMenuListView.setDivider(null);
        leftMenuListView.setDividerHeight(0);
        contentListView.setVerticalScrollBarEnabled(false);
        contentListView.setDivider(null);
        contentListView.setDividerHeight(0);

        leftMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (childNodes != null) {
                    contentAdapter.setData(childNodes.get(groupNodes.get(position).getId()));
                    changeSelect(position);
                    menuAdapter.notifyDataSetChanged();
                    int index;
                    if (position > oldPosition) {
                        index = Math.abs(position - leftMenuListView.getFirstVisiblePosition());
                    } else {
                        index = Math.abs(position - (leftMenuListView.getFirstVisiblePosition() + 1));
                    }
                    if (position != 0) {
                        leftMenuListView.smoothScrollByOffset(index);
                    }
                    oldPosition = position;
//                    leftMenuListView.smoothScrollToPosition(position);
                }
            }
        });
        return view;
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        menuAdapter = new MenuListViewAdapter(getActivity());
        contentAdapter = new ContentListViewAdapter(getActivity());
        leftMenuListView.setAdapter(menuAdapter);
        contentListView.setAdapter(contentAdapter);
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
        menuListData = new ArrayList<>();
        onDataChanged();
        requestData();
    }

    private void onDataChanged() {
        List<DrugGroupEntity> entities = DBInterface.instance().loadAllDrugGroup();
        groupNodes = new ArrayList<>();
        childNodes = new HashMap<>();
        for (DrugGroupEntity entity :
                entities) {
            if (TextUtils.isEmpty(entity.getPID())) {
                groupNodes.add(entity);
                if (!childNodes.containsKey(entity.getId())) {
                    childNodes.put(entity.getId(), new ArrayList<DrugGroupEntity>());
                }
            } else {
                if (!childNodes.containsKey(entity.getPID())) {
                    childNodes.put(entity.getPID(), new ArrayList<DrugGroupEntity>());
                }
                childNodes.get(entity.getPID()).add(entity);
            }
        }
        initData();
//        adapter.bindData(groupNodes, childNodes);
//        adapter.notifyDataSetChanged();
        leftMenuListView.smoothScrollToPosition(0);
    }

    public void initData() {
        menuListData.clear();
        for (int i = 0; i < groupNodes.size(); i++) {
            menuListData.add(new MenuEntity(groupNodes.get(i).getName(), false));
        }
        menuAdapter.setData(menuListData);

        if (menuListData.size() > 0) {
            menuListData.get(0).isSelet = true;
            contentAdapter.setData(childNodes.get(groupNodes.get(0).getId()));
        }
    }

    private void requestData() {
        Map<String, Object> args = new HashMap<>();
        int lastTime = DBInterface.instance().getDrugGroupDataLastTime();
        // args.put("LASTTIME", lastTime);
        // args.put("GUID","");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetMedicineKind", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .withProtocol(protocol)
                .build();
        FacadeClient.request(getActivity(), message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                bindData(null);
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (errorMsg == null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    bindData(responseProtocol.getResponse());
                }
            }
        });
    }

    private void bindData(List<LinkedTreeMap<String, String>> nodes) {
        List<DrugGroupEntity> needDb = new ArrayList<>();
        DrugGroupEntity entityTemp;
        for (LinkedTreeMap<String, String> item : nodes) {
            entityTemp = new DrugGroupEntity();
            entityTemp.setId(item.get("ID"));
            entityTemp.setName(item.get("NAME"));
            entityTemp.setPID(item.get("PID"));
            entityTemp.setSortIndex(item.get("ORDERINDEX"));
            entityTemp.setCreated((int) Calendar.getInstance().getTimeInMillis());
            entityTemp.setUpdated((int) Calendar.getInstance().getTimeInMillis());
            needDb.add(entityTemp);
        }
        if (needDb.size() > 0) {
            DrugGroupDao userDao = DBInterface.instance().openWritableDb().getDrugGroupDao();
            userDao.insertOrReplaceInTx(needDb);
        }
        onDataChanged();
    }

    class MenuListViewAdapter extends BaseAdapter {

        private Context context;
        private List<MenuEntity> data;

        public MenuListViewAdapter(Context context) {
            this.context = context;
            data = new ArrayList<>();
        }

        public void setData(List<MenuEntity> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextViewCell(context);
            }
            TextViewCell cell = (TextViewCell) convertView;
            cell.setText(data.get(position).content, true);
            if (data.get(position).isSelet) {
                cell.showImageView();
                cell.setBackgroundResource(R.color.title_background_grey);
            } else {
                cell.hiddenImageView();
                cell.setBackgroundResource(R.color.white);
            }
            return cell;
        }
    }

    public void changeSelect(int position) {
        if (menuListData != null) {
            for (MenuEntity entity : menuListData) {
                entity.isSelet = false;
            }
            menuListData.get(position).isSelet = true;
        }
    }

    class MenuEntity {
        public String content;
        public boolean isSelet;

        public MenuEntity(String content, boolean isSelet) {
            this.content = content;
            this.isSelet = isSelet;
        }
    }
}
