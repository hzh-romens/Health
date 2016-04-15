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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.common.GoodsFlag;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.DrugGroupDao;
import com.romens.yjk.health.db.entity.DrugGroupEntity;
import com.romens.yjk.health.ui.ShopListActivity;
import com.romens.yjk.health.ui.adapter.ContentListViewAdapter;
import com.romens.yjk.health.ui.cells.DrugGroupMenuCell;
import com.romens.yjk.health.ui.components.ToastCell;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2016/1/11.
 */
public class HomeHealthNewFragment extends AppFragment {
    private ListView leftMenuListView;
    private RecyclerView contentListView;

    private MenuListViewAdapter menuAdapter;
    private ContentListViewAdapter contentAdapter;

    private final HashMap<String, List<DrugGroupEntity>> childNodes = new HashMap<>();
    private final List<DrugGroupEntity> groupNodes = new ArrayList<>();

    private int currSelectPosition = -1;
    public static final String ARGUMENTS_KEY_ID = "key_id";
    public static final String ARGUMENTS_KEY_NAME = "key_name";

    private int goodsFlag = GoodsFlag.NORMAL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            goodsFlag = arguments.getInt(GoodsFlag.ARGUMENT_KEY_GOODS_FLAG, GoodsFlag.NORMAL);
        }
    }

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
            arguments.putInt(GoodsFlag.ARGUMENT_KEY_GOODS_FLAG, goodsFlag);
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
        final int size = entities.size();
        for (int i = 0; i < size; i++) {
            DrugGroupEntity entity = entities.get(i);
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
        onDrugGroupSelected(0);
    }

    private void requestData() {
        Map<String, String> args = new HashMap<>();
        args.put("FLAG", GoodsFlag.checkFlagForArg(goodsFlag));
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetMedicineKind", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(HomeHealthNewFragment.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            bindData(responseProtocol.getResponse());
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(getActivity(), connect);
    }

    private void bindData(JsonNode nodes) {
        List<DrugGroupEntity> needDb = new ArrayList<>();
        DrugGroupEntity entityTemp;
        int size = nodes.size();
        JsonNode itemTemp;
        for (int i = 0; i < size; i++) {
            itemTemp = nodes.get(i);
            entityTemp = new DrugGroupEntity();
            entityTemp.setId(itemTemp.get("ID").asText());
            entityTemp.setName(itemTemp.get("NAME").asText());
            entityTemp.setPID(itemTemp.get("PID").asText());
            entityTemp.setSortIndex(itemTemp.get("sortnumber").asInt(0));
            entityTemp.setCreated((int) Calendar.getInstance().getTimeInMillis());
            entityTemp.setUpdated((int) Calendar.getInstance().getTimeInMillis());
            if (itemTemp.has("MLOGO")) {
                entityTemp.setIcon(itemTemp.get("MLOGO").asText());
            } else {
                entityTemp.setIcon("");
            }
            needDb.add(entityTemp);
        }
        DrugGroupDao userDao = DBInterface.instance().openWritableDb().getDrugGroupDao();
        userDao.deleteAll();
        if (needDb.size() > 0) {
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
