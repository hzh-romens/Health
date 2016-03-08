package com.romens.yjk.health.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.hyrmtt.ui.activity.CommitOrderActivity;
import com.romens.yjk.health.model.CuoponEntity;
import com.romens.yjk.health.ui.adapter.CuoponAdapter;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by HZH on 2016/1/15.
 */
public class CuoponFragment extends Fragment {
    private ListView listView;
    private SwipeRefreshLayout refreshLayout;
    private CuoponAdapter cuoponAdapter;
    private int mFlag;
    private String requestType;
    private int choicePosition = -1;
    private double sumMoney;
    private SparseBooleanArray choiceArray = new SparseBooleanArray();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cuopon, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        cuoponAdapter = new CuoponAdapter(getActivity());
        getData(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CuoponEntity entity = result.get(position);
                choiceArray = cuoponAdapter.getChoiceArray();
                if (choiceArray.get(position)) {
                    choiceArray.append(position, false);
                } else {
                    choiceArray.append(position, true);
                }
                cuoponAdapter.bindChoiceData(choiceArray);
                //if ("GetCoupon".equals(requestType)) {
                if (sumMoney >= Double.parseDouble(entity.getLimitamount())) {
                    Toast.makeText(getActivity(), "未达到优惠卷使用金额", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), CommitOrderActivity.class);
                    String couponguID = result.get(position).getCouponguid();
                    intent.putExtra("orderCouponID", couponguID);
                    intent.putExtra("position", position);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
                //  } else {
                //    Toast.makeText(getActivity(), "该优惠卷无效", Toast.LENGTH_SHORT).show();
                //}
            }
        });
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(true);
            }
        });
        return view;
    }

    private void getData(boolean isRefresh) {
        if (isRefresh) {
            refreshLayout.setRefreshing(isRefresh);
        }

        if (mFlag == 1) {
            requestType = "GetCoupon";
            getCuopon();
        } else {
            requestType = "GetCouponHistory";
            getCuopon();
        }
    }

    private List<CuoponEntity> result;


    public void setFlag(int flag) {
        this.mFlag = flag;
    }

    public void setChoice(int position, double sumMoney) {
        this.choicePosition = position;
        this.sumMoney = sumMoney;
    }

    public void getCuopon() {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", requestType, args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("GetCoupon", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    if (result != null) {
                        result.clear();
                    }
                    result = CuoponEntity.toEntity(response);
                    setChoiceList();
                    refreshLayout.setRefreshing(false);
                    cuoponAdapter.bindData(result);
                    if (choicePosition >= 0) {
                        choiceArray.append(choicePosition, true);
                    }
                    cuoponAdapter.bindChoiceData(choiceArray);
                    // if ("GetCoupon".equals(requestType)) {
                    cuoponAdapter.setChoice(choicePosition, requestType);
                    //}
                    listView.setAdapter(cuoponAdapter);
                } else {
                    Log.e("GetCoupon", errorMsg.msg);
                }
            }
        });
    }

    private void setChoiceList() {
        for (int i = 0; i < result.size(); i++) {
            choiceArray.append(i, false);
        }
    }

}
