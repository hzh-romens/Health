package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
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
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.OrderDetailActivity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ImageAndTextCell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private String userGuid = "3333";
    private ImageAndTextCell attachView;
    private FrameLayout content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userGuid = UserGuidConfig.USER_GUID;
        adapter = new AllOrderViewAdapter(getActivity(), mOrderEntities);
    }

    public void initData() {
        mOrderEntities = new ArrayList<>();
        requestOrderList(userGuid);
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        initData();
        content = new FrameLayout(context);
        swipeRefreshLayout = new SwipeRefreshLayout(context);
        content.addView(swipeRefreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);
        swipeRefreshLayout.setRefreshing(true);

        recyclerView = new RecyclerView(context);
        swipeRefreshLayout.addView(recyclerView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                mOrderEntities = new ArrayList<>();
                requestOrderList(userGuid);
            }
        });
        addCellView(content);
        refershContentView();
        return content;
    }

    public void refershContentView() {
        if (mOrderEntities != null && mOrderEntities.size() > 0) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            attachView.setVisibility(View.GONE);
        } else {
            swipeRefreshLayout.setVisibility(View.GONE);
            attachView.setVisibility(View.VISIBLE);
        }
    }

    private void addCellView(FrameLayout container) {
        attachView = new ImageAndTextCell(getActivity());
        attachView.setImageAndText(R.drawable.no_order_img, "您还没有相关订单");
        LinearLayout.LayoutParams layoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        attachView.setPadding(AndroidUtilities.dp(0), AndroidUtilities.dp(100), AndroidUtilities.dp(0), AndroidUtilities.dp(0));
        attachView.setLayoutParams(layoutParams);
        container.addView(attachView);
    }


    private void requestOrderList(String userGuid) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
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
        Collections.sort(mOrderEntities, comparator);
        refershContentView();
        swipeRefreshLayout.setRefreshing(false);
        adapter.setOrderEntities(mOrderEntities);
        adapter.notifyDataSetChanged();
    }

    private Comparator<AllOrderEntity> comparator = new Comparator<AllOrderEntity>() {
        @Override
        public int compare(AllOrderEntity lhs, AllOrderEntity rhs) {
            return rhs.getCreateDate().compareTo(lhs.getCreateDate());
        }
    };

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        requestOrderList(userGuid);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
    }

    class AllOrderViewAdapter extends RecyclerView.Adapter<ADHolder> {

        private Context mContext;
        List<AllOrderEntity> orderEntities;

        public AllOrderViewAdapter(Context mContext, List<AllOrderEntity> orderEntities) {
            this.mContext = mContext;
            this.orderEntities = orderEntities;
        }

        public void setOrderEntities(List<AllOrderEntity> orderEntities) {
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
                holder.itemView.setBackgroundColor(Color.WHITE);
                final int index = position / 2;
                final ItemViewHolder viewHolder = (ItemViewHolder) holder;
                AllOrderEntity entity = orderEntities.get(index);
                viewHolder.titleView.setText("订单编号：" + entity.getOrderNo());
                viewHolder.evaluateState.setText(entity.getOrderStatuster());
                viewHolder.dateView.setText(entity.getCreateDate());
                viewHolder.moneyView.setText("￥" + entity.getOrderPrice());

                if (entity.getPicSmall() != null) {
                    viewHolder.medicineImg.setImageUrl(entity.getPicSmall(), null, null);
                } else {
                    viewHolder.medicineImg.setImageResource(R.drawable.no_img_upload);
                }

                viewHolder.cardViewLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.itemView.setBackgroundColor(getContext().getResources().getColor(R.color.line_color));
                        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                        intent.putExtra("orderId", orderEntities.get(index).getOrderId());
                        startActivity(intent);
                        notifyDataSetChanged();
                    }
                });
                viewHolder.goodsName.setText(orderEntities.get(index).getGoodsName());
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
        private TextView dateView;
        private TextView moneyView;
        private TextView goodsName;
        private BackupImageView medicineImg;
        private RelativeLayout cardViewLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.order_all_title);
            goodsName = (TextView) itemView.findViewById(R.id.order_all_goods_name);
            evaluateState = (TextView) itemView.findViewById(R.id.order_all_evaluate);
            dateView = (TextView) itemView.findViewById(R.id.order_all_date);
            moneyView = (TextView) itemView.findViewById(R.id.order_all_money);
            cardViewLayout = (RelativeLayout) itemView.findViewById(R.id.order_total_layout);
            medicineImg = (BackupImageView) itemView.findViewById(R.id.order_img);
        }
    }
}
