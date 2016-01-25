package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.DrugGroupDao;
import com.romens.yjk.health.db.entity.DrugGroupEntity;
import com.romens.yjk.health.ui.ShopListActivity;
import com.romens.yjk.health.ui.adapter.ContentListViewAdapter;
import com.romens.yjk.health.ui.cells.DrugGroupMenuCell;

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
    private RecyclerView contentListView;

    private MenuListViewAdapter menuAdapter;
    private ContentListViewAdapter contentAdapter;

    private final HashMap<String, List<DrugGroupEntity>> childNodes = new HashMap<>();
    private final List<DrugGroupEntity> groupNodes = new ArrayList<>();

    private int currSelectPosition = -1;
    public static final String ARGUMENTS_KEY_ID = "key_id";
    public static final String ARGUMENTS_KEY_NAME = "key_name";

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        LinearLayout content = new LinearLayout(context);
        content.setBackgroundColor(0xfff0f0f0);
        content.setOrientation(LinearLayout.HORIZONTAL);

        leftMenuListView = new ListView(context);
        leftMenuListView.setBackgroundColor(0xffffffff);
        leftMenuListView.setDivider(null);
        leftMenuListView.setDividerHeight(0);
        leftMenuListView.setVerticalScrollBarEnabled(false);
        leftMenuListView.setSelector(R.drawable.list_selector);
        leftMenuListView.setDrawSelectorOnTop(true);
        content.addView(leftMenuListView, LayoutHelper.createLinear(96, LayoutHelper.MATCH_PARENT));

        contentListView = new RecyclerView(context);
        contentListView.setLayoutManager(new GridLayoutManager(context, 3));
        contentListView.addItemDecoration(new ItemDecoration());
        contentListView.setPadding(AndroidUtilities.dp(4), AndroidUtilities.dp(6), AndroidUtilities.dp(4), AndroidUtilities.dp(6));
        contentListView.setVerticalScrollBarEnabled(false);
        content.addView(contentListView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        leftMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (childNodes != null) {
                    onDrugGroupSelected(position);
//                    contentAdapter.setData(childNodes.get(groupNodes.get(position).getId()));
//                    currSelectPosition = position;
//                    menuAdapter.notifyDataSetChanged();
//                    int index;
//                    if (position > oldPosition) {
//                        index = Math.abs(position - leftMenuListView.getFirstVisiblePosition());
//                    } else {
//                        index = Math.abs(position - (leftMenuListView.getFirstVisiblePosition() + 1));
//                    }
//                    if (position != 0) {
//                        leftMenuListView.smoothScrollByOffset(index);
//                    }
//                    oldPosition = position;
////                    leftMenuListView.smoothScrollToPosition(position);
                }
            }
        });
        return content;
    }

    private void onDrugGroupSelected(int position) {
        if (groupNodes.isEmpty()) {
            return;
        }
        boolean selectChanged = !(position == currSelectPosition);
        currSelectPosition = position;
        menuAdapter.notifyDataSetChanged();
        leftMenuListView.smoothScrollToPosition(position);
        if (selectChanged) {
            contentAdapter.setData(childNodes.get(groupNodes.get(position).getId()));
        }
        contentAdapter.notifyDataSetChanged();
        contentListView.smoothScrollToPosition(0);
    }

    private void onDrugChildClick(DrugGroupEntity childNode) {
        if (childNode != null) {
            Intent intent = new Intent(getActivity(), ShopListActivity.class);
            Bundle arguments = new Bundle();
            arguments.putString(ARGUMENTS_KEY_ID, childNode.getId());
            arguments.putString(ARGUMENTS_KEY_NAME, childNode.getName());
            intent.putExtras(arguments);
            startActivity(intent);
        }
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        menuAdapter = new MenuListViewAdapter(getActivity());
        contentAdapter = new ContentListViewAdapter(getActivity(), new ContentListViewAdapter.ContentDelegate() {
            @Override
            public void onCellSelected(DrugGroupEntity entity) {
                onDrugChildClick(entity);
            }
        });
        leftMenuListView.setAdapter(menuAdapter);
        contentListView.setAdapter(contentAdapter);
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
        onDataChanged();
        requestData();
        onDrugGroupSelected(0);
    }

    private void onDataChanged() {
        List<DrugGroupEntity> entities = DBInterface.instance().loadAllDrugGroup();
        groupNodes.clear();
        childNodes.clear();
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
//        adapter.bindData(groupNodes, childNodes);
//        adapter.notifyDataSetChanged();
        onDrugGroupSelected(0);
    }

    private void requestData() {
        Map<String, String> args = new HashMap<>();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetMedicineKind", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(HomeHealthNewFragment.class)
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        if (errorMessage == null) {
                            ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) message.protocol;
                            bindData(responseProtocol.getResponse());
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(getActivity(), connect);
//        Message message = new Message.MessageBuilder()
//                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
//                }))
//                .withProtocol(protocol)
//                .build();
//        FacadeClient.request(getActivity(), message, new FacadeClient.FacadeCallback() {
//            @Override
//            public void onTokenTimeout(Message msg) {
//                bindData(null);
//            }
//
//            @Override
//            public void onResult(Message msg, Message errorMsg) {
//                if (errorMsg == null) {
//                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
//                    bindData(responseProtocol.getResponse());
//                }
//            }
//        });
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
            if (item.containsKey("MLOGO")) {
                entityTemp.setIcon(item.get("MLOGO"));
            } else {
                entityTemp.setIcon("");
            }
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

        public MenuListViewAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return groupNodes.size();
        }

        @Override
        public DrugGroupEntity getItem(int position) {
            return groupNodes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new DrugGroupMenuCell(context);
            }
            DrugGroupMenuCell cell = (DrugGroupMenuCell) convertView;
            DrugGroupEntity groupEntity = getItem(position);
            cell.setValue(groupEntity.getName(), position == currSelectPosition);
            return cell;
        }
    }


    class ItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top += AndroidUtilities.dp(2);
            outRect.bottom += AndroidUtilities.dp(4);
            outRect.left += AndroidUtilities.dp(4);
            outRect.right += AndroidUtilities.dp(2);
        }
    }

}
