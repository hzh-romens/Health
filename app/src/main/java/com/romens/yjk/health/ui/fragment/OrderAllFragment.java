package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.OrderDetailActivity;
import com.romens.yjk.health.ui.cells.ADHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/9/22.
 * 我的订单中的全部页面
 */
public class OrderAllFragment extends BaseFragment {


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private AllOrderViewAdapter adapter;
    private List<AllOrderEntity> mOrderEntities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        adapter = new AllOrderViewAdapter(getActivity(), mOrderEntities);
    }

    public void initData() {
        mOrderEntities = new ArrayList<>();
        requestOrderList();
        for (int i = 0; i < 10; i++) {
            AllOrderEntity entit = new AllOrderEntity();
            entit.setOrderStatuster("待评价" + i);
            entit.setOrderStatus("1");
            entit.setGoodsName("");
            entit.setOrderPrice("￥20");
            entit.setOrderId("2000288844448020291");
            mOrderEntities.add(entit);
        }
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout content = new FrameLayout(context);
        swipeRefreshLayout = new SwipeRefreshLayout(context);
        content.addView(swipeRefreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AllOrderAsynTask().execute();
            }
        });

        recyclerView = new RecyclerView(context);
        swipeRefreshLayout.addView(recyclerView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return content;
    }

    private void requestOrderList() {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", "3333");
        args.put("ORDERSTATUS", "1");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "getMyOrders", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(getActivity(), message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(getActivity(), msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    setOrderData(responseProtocol.getResponse());
                }
                if (errorMsg == null) {
                } else {
                    Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    public void setOrderData(List<LinkedTreeMap<String, String>> response) {
        int count = response == null ? 0 : response.size();
        if (count <= 0) {
            return;
        }
        mOrderEntities = new ArrayList<>();
        for (LinkedTreeMap<String, String> item : response) {
            AllOrderEntity entity = AllOrderEntity.mapToEntity(item);
            mOrderEntities.add(entity);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }

    class AllOrderAsynTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    class AllOrderViewAdapter extends RecyclerView.Adapter<ADHolder> {

        private Context mContext;
        List<AllOrderEntity> orderEntities;

        public AllOrderViewAdapter(Context mContext, List<AllOrderEntity> orderEntities) {
            this.mContext = mContext;
            this.orderEntities = orderEntities;
        }

        @Override
        public ADHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_order_all, null);
                return new ItemViewHolder(view);
            }
            return new ADHolder(new ShadowSectionCell(mContext));
        }

        @Override
        public void onBindViewHolder(ADHolder holder, int position) {
            int type = getItemViewType(position);
            if (type == 0) {
                int index = position / 2;
                String url = "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg";
                ItemViewHolder viewHolder = (ItemViewHolder) holder;
                viewHolder.titleView.setText("订单编号：" + orderEntities.get(index).getOrderId());
                viewHolder.evaluateState.setText(orderEntities.get(index).getOrderStatuster());
                viewHolder.firstImageView.setImageUrl(url, "64_64", null);
                viewHolder.secondImageView.setImageUrl(url, "64_64", null);
                viewHolder.threeImageView.setImageUrl(url, "64_64", null);
                viewHolder.fourImageView.setImageUrl(url, "64_64", null);
                viewHolder.dateView.setText("2015-12-15 08:09");
                viewHolder.countView.setText("个包裹");
                viewHolder.moneyView.setText(orderEntities.get(index).getOrderPrice());

                viewHolder.cardViewLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), OrderDetailActivity.class));
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return orderEntities.size() * 2;
        }

        @Override
        public int getItemViewType(int position) {
            return position % 2;
        }
    }

    class ItemViewHolder extends ADHolder {

        private TextView titleView;
        private TextView evaluateState;
        private BackupImageView firstImageView;
        private BackupImageView secondImageView;
        private BackupImageView threeImageView;
        private BackupImageView fourImageView;
        private TextView dateView;
        private TextView countView;
        private TextView moneyView;

        private RelativeLayout cardViewLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.order_all_title);
            evaluateState = (TextView) itemView.findViewById(R.id.order_all_evaluate);
            firstImageView = (BackupImageView) itemView.findViewById(R.id.order_all_fist_img);
            secondImageView = (BackupImageView) itemView.findViewById(R.id.order_all_second_img);
            threeImageView = (BackupImageView) itemView.findViewById(R.id.order_all_three_img);
            fourImageView = (BackupImageView) itemView.findViewById(R.id.order_all_four_img);
            dateView = (TextView) itemView.findViewById(R.id.order_all_date);
            countView = (TextView) itemView.findViewById(R.id.order_all_count);
            moneyView = (TextView) itemView.findViewById(R.id.order_all_money);

            cardViewLayout = (RelativeLayout) itemView.findViewById(R.id.order_total_layout);
        }
    }
}
