package com.romens.yjk.health.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Components.RecyclerListView;
import com.romens.android.ui.support.widget.LinearLayoutManager;
import com.romens.android.ui.support.widget.RecyclerView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.HealthNewsEntity;
import com.romens.yjk.health.ui.cells.NewsCell;
import com.romens.yjk.health.ui.cells.NewsTopCell;
import com.romens.yjk.health.ui.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/8/13.
 */
public class HealthNewsActivity extends BaseActivity {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerListView recyclerView;
    private ListAdapter adapter;

    private Thread handleJsonThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);

        refreshLayout = new SwipeRefreshLayout(this);
        content.addView(refreshLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        UIHelper.updateSwipeRefreshProgressBarTop(this, refreshLayout);

        recyclerView = new RecyclerListView(this);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(0, 0, 0, AndroidUtilities.dp(8));
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutAnimation(null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);

        refreshLayout.addView(recyclerView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP));

        actionBar.setTitle("健康资讯");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });
        recyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {

                HealthNewsEntity entity = adapter.getItem(i);
                UIOpenHelper.openWebActivityWithHtml(HealthNewsActivity.this, entity.title, entity.getValue());
            }
        });

        adapter = new ListAdapter(this);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });
        requestData();
    }

    private void changeRefreshState(boolean isRefreshing) {
        refreshLayout.setRefreshing(isRefreshing);
    }

    private boolean isRefreshing() {
        return refreshLayout.isRefreshing();
    }


    private void requestData() {
        changeRefreshState(true);
        long lastTime = Calendar.getInstance().getTimeInMillis();
        Map<String, Object> args = new HashMap<>();
        args.put("TIME", lastTime);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetHealthInfoList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(HealthNewsActivity.this, "查询超时,请稍候重试!", Toast.LENGTH_SHORT).show();
                changeRefreshState(false);
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    handleResponseData(responseProtocol.getResponse());
                } else {
                    handleResponseData(null);
                }
            }
        });
    }

    private void bindAdapter(List<HealthNewsEntity> entities) {
        adapter.bindData(entities);
//        adapter.notifyDataSetChanged();
        changeRefreshState(false);
    }

    @Override
    public void onDestroy() {
        if (handleJsonThread != null) {
            handleJsonThread.interrupt();
        }
        super.onDestroy();
    }

    private void handleResponseData(final String data) {
        if (handleJsonThread != null && handleJsonThread.isAlive()) {
            handleJsonThread.interrupt();
        }
        handleJsonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<HealthNewsEntity> entities = asyncHandleJsonData(data);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        bindAdapter(entities);
                    }
                });
            }
        });
        handleJsonThread.start();
    }

    /**
     * 处理返回的JSON数据
     *
     * @param data
     * @return
     */
    private List<HealthNewsEntity> asyncHandleJsonData(String data) {
        //获取资讯数据
        List<HealthNewsEntity> entities = new ArrayList<>();
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if (!jsonObject.has("data")) {
                    return null;
                }
                if (!jsonObject.has("datainfo")) {
                    return null;
                }
                JSONArray dataJsonArray = jsonObject.getJSONArray("data");
                int dataSize = dataJsonArray.length();
                for (int j = 0; j < dataSize; j++) {
                    JSONObject jsonObject1 = dataJsonArray.getJSONObject(j);
                    String newsTime = jsonObject1.getString("time");
                    if (!TextUtils.isEmpty(newsTime)) {
                        JSONObject dataInfoJsonObject = jsonObject.getJSONObject("datainfo");
                        if (dataInfoJsonObject.has(newsTime)) {
                            JSONArray dataInfoJsonArray = dataInfoJsonObject.getJSONArray(newsTime);
                            if (dataInfoJsonArray != null) {
                                JSONObject itemTemp;
                                HealthNewsEntity entityTemp;
                                for (int i = 0; i < dataInfoJsonArray.length(); i++) {
                                    itemTemp = dataInfoJsonArray.getJSONObject(i);
                                    if (itemTemp != null) {
                                        entityTemp = new HealthNewsEntity(
                                                itemTemp.getString("id"),
                                                itemTemp.getString("pic"),
                                                itemTemp.getString("title"),
                                                itemTemp.getString("text"));
                                        entityTemp.setValue(itemTemp.getString("info"));
                                        entities.add(entityTemp);
                                    }
                                }
                            }
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return entities;
    }

    static class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    static class ListAdapter extends RecyclerView.Adapter<Holder> {
        private Context context;
        private final List<HealthNewsEntity> data = new ArrayList<>();

        public ListAdapter(Context context) {
            this.context = context;
        }

        public void bindData(List<HealthNewsEntity> entities) {
            data.clear();
            if (entities != null && entities.size() > 0) {
                data.addAll(entities);
            }
            notifyDataSetChanged();
        }

        public HealthNewsEntity getItem(int position) {
            if (position >= 0 && position < data.size()) {
                return data.get(position);
            }
            return null;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                NewsTopCell cell = new NewsTopCell(context);
                cell.setBackgroundResource(R.drawable.list_selector);
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            } else if (viewType == 1) {
                NewsCell cell = new NewsCell(context);
                cell.setBackgroundResource(R.drawable.list_selector);
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            HealthNewsEntity entity = getItem(position);
            int viewType = getItemViewType(position);
            if (viewType == 0) {
                NewsTopCell cell = (NewsTopCell) holder.itemView;
                cell.setValue(entity.title, entity.content, entity.iconUrl, true);
            } else if (viewType == 1) {
                NewsCell cell = (NewsCell) holder.itemView;
                boolean needDivider = position != (data.size() - 1);
                cell.setValue(entity.title, entity.content, entity.iconUrl, needDivider);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            return 1;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
