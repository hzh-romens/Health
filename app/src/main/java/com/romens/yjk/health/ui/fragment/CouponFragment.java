package com.romens.yjk.health.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.model.CuoponEntity;
import com.romens.yjk.health.ui.CuoponActivity;
import com.romens.yjk.health.ui.adapter.CouponAdapter;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HZH on 2016/1/15.
 */
public class CouponFragment extends Fragment {
    public static final String ARGUMENT_KEY_SELECT_COUPON_ID = "select_coupon_id";
    public static final String ARGUMENT_KEY_ORDER_AMOUNT = "order_amount";

    private SwipeRefreshLayout refreshLayout;
    private CouponAdapter couponAdapter;
    private String selectedCouponGuid;
    private String requestType;
    private ListView listView;
    private BigDecimal orderAmount = BigDecimal.ZERO;
    private int mPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle argument = getArguments();
        if (argument != null) {
            selectedCouponGuid = argument.getString(ARGUMENT_KEY_SELECT_COUPON_ID);
            double amount = argument.getDouble(ARGUMENT_KEY_ORDER_AMOUNT, 0);
            orderAmount = new BigDecimal(amount);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cuopon, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        couponAdapter = new CouponAdapter(getActivity());
        getData(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                if (mCanclick) {
                                                    CuoponEntity entity = result.get(position);
//                                                    choiceArray = cuoponAdapter.getChoiceArray();
//                                                    cuoponAdapter.setChoiceItem(position, !choiceArray.get(position));
//                                                    if (choiceArray.get(position)) {
//                                                        choiceArray.append(position, false);
//                                                    } else {
//                                                        choiceArray.append(position, true);
//                                                    }
                                                    couponAdapter.switchCheck(entity.getGuid());
                                                    if ("GetCoupon".equals(requestType)) {
                                                        if (orderAmount.compareTo(entity.getLimitAmount()) == -1) {
                                                            Toast.makeText(getActivity(), "未达到优惠卷使用金额", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            CuoponEntity cuoponEntity = result.get(position);
                                                            Intent intent = new Intent();
                                                            String couponguGuid = cuoponEntity.getGuid();
                                                            if (selectedCouponGuid != null && TextUtils.equals(couponguGuid, selectedCouponGuid)) {
                                                                intent.putExtra("position", -1);
                                                                intent.putExtra("orderCouponID", "");
                                                                intent.putExtra("coupon_name", "");
                                                                intent.putExtra("amount", "");
                                                                intent.putExtra("limitAmount", "");
                                                            } else {
                                                                intent.putExtra("position", position);
                                                                intent.putExtra("orderCouponID", couponguGuid);
                                                                intent.putExtra("coupon_name", cuoponEntity.getName());
                                                                intent.putExtra("amount", result.get(position).getAmount());
                                                                intent.putExtra("limitAmount", result.get(position).getLimitamount());
                                                            }
                                                            getActivity().setResult(Activity.RESULT_OK, intent);
                                                            getActivity().finish();
                                                        }
                                                    } else {
                                                        Toast.makeText(getActivity(), "该优惠卷无效", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }

        );
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()

                                           {
                                               @Override
                                               public void onRefresh() {
                                                   getData(true);
                                               }
                                           }

        );
        return view;
    }

    private void getData(boolean isRefresh) {
        if (isRefresh) {
            refreshLayout.setRefreshing(isRefresh);
        }

        if (mPage == CuoponActivity.NOW) {
            requestType = "GetCoupon";
            getCuopon();
        } else {
            requestType = "GetCouponHistory";
            getCuopon();
        }
    }

    private List<CuoponEntity> result;
    private boolean mCanclick;


    public void setPage(int page) {
        this.mPage = page;
    }

    public void setCanClick(boolean canClick) {
        this.mCanclick = canClick;
    }

    public void getCuopon() {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getInstance().getClientUserEntity().getGuid()).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", requestType, args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(getActivity().getClass())
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();

                            result = new ArrayList<CuoponEntity>();
                            for (int i = 0; i < response.size(); i++) {
                                CuoponEntity cuoponEntity = CuoponEntity.toEntity(response.get(i));
                                result.add(cuoponEntity);
                            }
                            //setChoiceList();
                            refreshLayout.setRefreshing(false);
                            couponAdapter.bindData(result);
//                            if (choicePosition >= 0) {
//                                choiceArray.append(choicePosition, true);
//                            }
//                            cuoponAdapter.bindChoiceData(choiceArray);

                            if ("GetCoupon".equals(requestType)) {
                                couponAdapter.setChoice(selectedCouponGuid, requestType);
                            } else {
                                couponAdapter.switchCheck(selectedCouponGuid);
                            }
                            listView.setAdapter(couponAdapter);
                        } else {
                            refreshLayout.setRefreshing(false);
                            Toast.makeText(getActivity(), "数据为空", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).build();
        ConnectManager.getInstance().request(getActivity(), connect);
    }

}
